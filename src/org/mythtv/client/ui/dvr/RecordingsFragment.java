/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
package org.mythtv.client.ui.dvr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.service.dvr.BannerDownloadService;
import org.mythtv.service.dvr.ProgramGroupRecordedDownloadService;
import org.mythtv.service.dvr.RecordedDownloadService;
import org.mythtv.service.dvr.cache.BannerLruMemoryCache;
import org.mythtv.service.dvr.cache.RecordedLruMemoryCache;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Programs;
import org.mythtv.services.utils.ArticleCleaner;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsFragment extends MythtvListFragment {

	private static final String TAG = RecordingsFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private OnProgramGroupListener listener = null;
	private ProgramGroupRowAdapter adapter;
	
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();
	private ProgramGroupRecordedDownloadReceiver programGroupRecordedDownloadReceiver = new ProgramGroupRecordedDownloadReceiver();
	private BannerDownloadReceiver bannerDownloadReceiver = new BannerDownloadReceiver();

	private static FileHelper mFileHelper;
	private static ProgramHelper mProgramHelper;

	private RecordedLruMemoryCache cache;
	private BannerLruMemoryCache imageCache;

	private List<Program> programGroups = new ArrayList<Program>();
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );

		mFileHelper = new FileHelper( getActivity() );
		mProgramHelper = ProgramHelper.createInstance( getActivity() );

		cache = new RecordedLruMemoryCache( getActivity() );
		imageCache = new BannerLruMemoryCache( getActivity() );

		setHasOptionsMenu( true );
		setRetainInstance( true );

		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedDownloadService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedDownloadService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		IntentFilter programGroupRecordedDownloadFilter = new IntentFilter( ProgramGroupRecordedDownloadService.ACTION_DOWNLOAD );
		programGroupRecordedDownloadFilter.addAction( ProgramGroupRecordedDownloadService.ACTION_PROGRESS );
		programGroupRecordedDownloadFilter.addAction( ProgramGroupRecordedDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( programGroupRecordedDownloadReceiver, programGroupRecordedDownloadFilter );

		IntentFilter bannerDownloadFilter = new IntentFilter( BannerDownloadService.ACTION_DOWNLOAD );
		bannerDownloadFilter.addAction( BannerDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( bannerDownloadReceiver, bannerDownloadFilter );

		Log.v( TAG, "onStart : enter" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onStart();
	    
		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {

			File existing = new File( programCache, RecordedDownloadService.RECORDED_FILE );
			if( !existing.exists() ) {
				getActivity().startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
			} else {
				loadData();
			}

		}
		
        Log.v( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != recordedDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordedDownloadReceiver );
				recordedDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		if( null != programGroupRecordedDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( programGroupRecordedDownloadReceiver );
				programGroupRecordedDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		if( null != bannerDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( bannerDownloadReceiver );
				bannerDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@TargetApi( 11 )
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh" );
		refresh.setIcon( R.drawable.ic_menu_refresh_default );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			getActivity().startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.util.MythtvListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
		Log.v( TAG, "onListItemClick : position=" + position + ", id=" + id + ", tag=" + v.getTag() );

		TextView programGroup = (TextView) v.findViewById( R.id.program_group_row );
		
		listener.onProgramGroupSelected( programGroup.getText().toString() );
		
		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		Log.v( TAG, "setOnProgramGroupListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramGroupListener : exit" );
	}

	public interface OnProgramGroupListener {
		void onProgramGroupSelected( String programGroup );
	}

	// internal helpers
	
	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
		if( null != adapter ) {
			adapter.clear();
		}
		
		Programs programs = cache.get( RecordedDownloadService.RECORDED_FILE );
		
		Map<String, Program> filtered = new TreeMap<String, Program>();
		for( Program program : programs.getPrograms() ) {
			
			if( null != program.getRecording() && !"LiveTV".equalsIgnoreCase( program.getRecording().getStorageGroup() ) ) {
			
				String cleanedTitle = ArticleCleaner.clean( program.getTitle() );

				if( !filtered.containsKey( cleanedTitle ) ) {
					filtered.put( cleanedTitle, program );
				}
			}
		}
		
		List<Program> sorted = new ArrayList<Program>( filtered.values() );
		Collections.sort( sorted );
		
		programGroups = sorted;

	    adapter = new ProgramGroupRowAdapter( getActivity(), programGroups );
	    setListAdapter( adapter );
		
		Log.v( TAG, "loadData : exit" );
	}
	
	private class ProgramGroupRowAdapter extends ArrayAdapter<Program> {

		private LayoutInflater mInflater;

		private List<Program> programGroups;
		
		public ProgramGroupRowAdapter( Context context, List<Program> programGroups ) {
			super( context, R.id.program_group_row, programGroups );
			Log.v( TAG, "ProgramGroupRowAdapter : enter" );

			mInflater = LayoutInflater.from( context );

			this.programGroups = programGroups;
			
			Log.v( TAG, "ProgramGroupRowAdapter : exit" );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "ProgramGroupRowAdapter.getView : enter" );
			
			View v = convertView;
			ViewHolder mHolder;
			
			if( null == v ) {
				v = mInflater.inflate( R.layout.program_group_row, parent, false );
				
				mHolder = new ViewHolder();				
				mHolder.programGroupDetail = (LinearLayout) v.findViewById( R.id.program_group_detail );
				mHolder.category = (View) v.findViewById( R.id.program_group_category );
				mHolder.programGroup = (TextView) v.findViewById( R.id.program_group_row );
				
				v.setTag( mHolder );
			} else {
				mHolder = (ViewHolder) v.getTag();
			}
						
			Program program = programGroups.get( position );

			mHolder.programGroup.setText( program.getTitle() );
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
			
			BitmapDrawable banner = imageCache.get( program.getInetref() + BannerDownloadService.BANNER_FILE_EXT );
			if( null != banner ) {
				Log.v( TAG, "getView : loading banner from adapter cache" );
					
				mHolder.programGroupDetail.setBackgroundDrawable( banner );
				mHolder.programGroup.setVisibility( View.INVISIBLE );
			} else {
				Log.v( TAG, "getView : banner not found in adapter cache" );

				mHolder.programGroupDetail.setBackgroundDrawable( null );
				mHolder.programGroup.setVisibility( View.VISIBLE );
				
				Intent downloadIntent = new Intent( BannerDownloadService.ACTION_DOWNLOAD );
				downloadIntent.putExtra( BannerDownloadService.BANNER_INETREF, program.getInetref() );
				getActivity().startService( downloadIntent );
			}

			Log.v( TAG, "ProgramGroupRowAdapter.getView : exit" );
			return v;
		}
		
	}

	private static class ViewHolder {
		
		LinearLayout programGroupDetail;
		View category;
		TextView programGroup;
		
		ViewHolder() { }

	}
	
	private class RecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordedDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordedDownloadService.EXTRA_COMPLETE ) );
	        	
	        	Toast.makeText( getActivity(), "Recorded Programs updated!", Toast.LENGTH_SHORT ).show();
	        	
	        	getActivity().startService( new Intent( ProgramGroupRecordedDownloadService.ACTION_DOWNLOAD ) );
	        	
	        	cache.remove( RecordedDownloadService.RECORDED_FILE );
	        	loadData();
	        }

        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class ProgramGroupRecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGroupRecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGroupRecordedDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGroupRecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( ProgramGroupRecordedDownloadService.EXTRA_COMPLETE ) );
	        	
	        }

		}
		
	}

	private class BannerDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( BannerDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "BannerDownloadReceiver.onReceive : complete=" + intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE ) );
	        	
	        	imageCache.remove( intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE_FILENAME ) );
	        	//adapter.notifyDataSetChanged();
	        }

		}
		
	}

}
