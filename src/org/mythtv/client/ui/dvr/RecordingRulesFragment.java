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

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MenuItemRefreshAnimated;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.channel.model.ChannelInfo;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.RecordingRuleConstants;
import org.mythtv.db.dvr.RecordingRuleDaoHelper;
import org.mythtv.db.dvr.model.RecRule;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.service.dvr.RecordingRuleDownloadService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.RunningServiceHelper;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.Toast;

/**
 * @author Daniel Frey
 * @author Thomas G. Kenny Jr
 *
 */
public class RecordingRulesFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = RecordingRulesFragment.class.getSimpleName();

	private RecordingRuleDownloadReceiver recordingRuleDownloadReceiver = new RecordingRuleDownloadReceiver();

	//	private NotificationHelper mNotificationHelper;

	private OnRecordingRuleListener listener = null;
	private RecordingRuleAdapter adapter;
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private RecordingRuleDaoHelper mRecordingRuleDaoHelper = RecordingRuleDaoHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	
	private MainApplication mainApplication;
	
	private LocationProfile mLocationProfile;
	private MenuItemRefreshAnimated mMenuItemRefresh;
	
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mMenuItemRefresh = new MenuItemRefreshAnimated(this.getActivity());
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = null;
		String selection = RecordingRuleConstants.TABLE_NAME + "." + RecordingRuleConstants.FIELD_MASTER_HOSTNAME + " = ?";
		String[] selectionArgs = new String[] { mLocationProfile.getHostname() };
		String sortOrder = RecordingRuleConstants.FIELD_TITLE;
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), RecordingRuleConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		
	    Log.v( TAG, "onCreateLoader : exit" );
		return cursorLoader;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		adapter.swapCursor( cursor );
		
		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		adapter.swapCursor( null );
		
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
//		mNotificationHelper = new NotificationHelper( getActivity() );

		setHasOptionsMenu( true );
//		setRetainInstance( true );

		mainApplication = (MainApplication) getActivity().getApplicationContext();
		
		adapter = new RecordingRuleAdapter( getActivity().getApplicationContext() );
	    
		setListAdapter( adapter );
    	getListView().setFastScrollEnabled( true );

		getLoaderManager().initLoader( 0, null, this );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter recordingRuleDownloadFilter = new IntentFilter( RecordingRuleDownloadService.ACTION_DOWNLOAD );
		recordingRuleDownloadFilter.addAction( RecordingRuleDownloadService.ACTION_PROGRESS );
		recordingRuleDownloadFilter.addAction( RecordingRuleDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordingRuleDownloadReceiver, recordingRuleDownloadFilter );

		Log.v( TAG, "onStart : enter" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onStart();
	    
		DateTime etag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), mLocationProfile, DvrEndpoint.GET_RECORD_SCHEDULE_LIST.name(), "" );
		if( null != etag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) );
			if( now.getMillis() - etag.getMillis() > 3600000 ) {
				if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordingRuleDownloadService" ) ) {
					getActivity().startService( new Intent( RecordingRuleDownloadService.ACTION_DOWNLOAD ) );
				}
			}
			
		} else {
			if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordingRuleDownloadService" ) ) {
				getActivity().startService( new Intent( RecordingRuleDownloadService.ACTION_DOWNLOAD ) );
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
		if( null != recordingRuleDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordingRuleDownloadReceiver );
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

		mMenuHelper.refreshMenuItem( getActivity(), menu, this.mMenuItemRefresh );
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case MenuHelper.REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

//			adapter.refresh();
			if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordingRuleDownloadService" ) ) {
				getActivity().startService( new Intent( RecordingRuleDownloadService.ACTION_DOWNLOAD ) );
				this.mMenuItemRefresh.startRefreshAnimation();
			}
			
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

		Log.v( TAG, "onListItemClick : position=" + position + ", id=" + id );

		//get selected rule
		RecRule rule = mRecordingRuleDaoHelper.findOne( getActivity(), mLocationProfile, id );
		
		//only continue if we got a valid rule
		if( null != rule ) {
		    
       		//call rule selected listener if defined
       		boolean handled = false;
       		if(null != listener) handled = listener.onRecordingRuleSelected( rule );
        		
       		//handle rule selection ourself if not handled by listener
       		if(!handled) this.recordingRuleSelected( rule );
		}
		
		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public void setOnRecordingRuleListener( OnRecordingRuleListener listener ) {
		Log.v( TAG, "setOnRecordingRuleListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnRecordingRuleListener : exit" );
	}

	public void recordingRuleSelected( RecRule recordingRule ) {
		Log.d( TAG, "onRecordingRuleSelected : enter" );
		
		if( null != this.getActivity().findViewById( R.id.fragment_dvr_recording_rule ) ) {
			Log.v( TAG, "onRecordingRuleSelected : adding recording rule to pane" );

			FragmentManager manager = getActivity().getSupportFragmentManager();

			RecordingRuleFragment recordingRuleFragment = (RecordingRuleFragment) manager.findFragmentById( R.id.fragment_dvr_recording_rule );
			FragmentTransaction transaction = manager.beginTransaction();

			if( null == recordingRuleFragment ) {
				Log.v( TAG, "onRecordingRuleSelected : creating new recordingRuleFragment" );
				
				Bundle args = new Bundle();
				args.putLong( "RECORDING_RULE_ID", recordingRule.getId() );
				recordingRuleFragment = RecordingRuleFragment.newInstance( args );
				
				transaction
					.add( R.id.fragment_dvr_recording_rule, recordingRuleFragment )
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
					.addToBackStack( null )
					.commit();
			}
			
			Log.v( TAG, "onRecordingRuleSelected : setting recording rule to display" );
			recordingRuleFragment.loadRecordingRule( (long) recordingRule.getId() );
		} else {
			Log.v( TAG, "onRecordingRuleSelected : starting recording rule activity" );

			Intent i = new Intent( this.getActivity(), RecordingRuleActivity.class );
			i.putExtra( RecordingRuleActivity.EXTRA_RECORDING_RULE_KEY, (long) recordingRule.getId() );
			startActivity( i );
		}

		Log.d( TAG, "onRecordingRuleSelected : exit" );
	}
	
	public interface OnRecordingRuleListener {
	    	/**
	    	 * Called when a recording rule is slected
	    	 * @param recordingRuleId
	    	 * @return true if selection has been handled.
	    	 */
		boolean onRecordingRuleSelected( RecRule recordingRule );
	}
	
	// internal helpers
	
	private class RecordingRuleAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		private ProgramHelper mProgramHelper = ProgramHelper.getInstance();

		public RecordingRuleAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
			
		}
		
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			//Log.v( TAG, "newView : enter" );

	        View view = mInflater.inflate( R.layout.recording_rules_row, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.category = (View) view.findViewById( R.id.recording_rules_category );
			refHolder.title = (TextView) view.findViewById( R.id.recording_rules_title );
			refHolder.channel = (TextView) view.findViewById( R.id.recording_rules_channel );
			refHolder.type = (TextView) view.findViewById( R.id.recording_rules_type );
			refHolder.last = (TextView) view.findViewById( R.id.recording_rules_last );
			refHolder.active = (TextView) view.findViewById(R.id.recording_rules_switch_active);

			view.setTag( refHolder );
			
			//Log.v( TAG, "newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {

			final RecRule recRule = mRecordingRuleDaoHelper.convertCursorToRecRule( cursor );
			final ChannelInfo channelInfo = ChannelDaoHelper.convertCursorToChannelInfo( cursor );
			
//			Log.i( TAG, "recRule=" + recRule.toString() );
			
			final ViewHolder mHolder = (ViewHolder) view.getTag();

			String channel = "[Any]";
			if( recRule.getChanId() > 0 ) {
				//ChannelInfo channelInfo = mChannelDaoHelper.findByChannelId( getActivity(), mLocationProfile, (long) recRule.getChanId() );
				if( null != channelInfo && channelInfo.getChannelId() > -1 ) {
					channel = channelInfo.getChannelNumber();
				}
			}
			
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( recRule.getCategory() ) );
			mHolder.title.setText( recRule.getTitle() );
			mHolder.channel.setText( channel );
			mHolder.type.setText( recRule.getType() );
			mHolder.last.setText( DateUtils.getDateWithLocaleFormatting( recRule.getLastRecorded(), mainApplication.getDateFormat() ) );
			mHolder.active.setText( !recRule.isInactive() ? "Active" : "Inactive" );
			
		}
		
	}

	private static class ViewHolder {
		
		View category;
		
		TextView title;
		TextView channel;
		TextView type;
		TextView last;
		TextView active;
		
		ViewHolder() { }

	}

	private class RecordingRuleDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordingRuleDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordingRuleDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordingRuleDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordingRuleDownloadService.EXTRA_COMPLETE ) );
	        	
	        	mMenuItemRefresh.stopRefreshAnimation();
	        	
	        	if( intent.getExtras().containsKey( RecordingRuleDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
//	        		Toast.makeText( getActivity(), "Recording Rules are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( RecordingRuleDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
//	        		Toast.makeText( getActivity(), "Recording Rules update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
//	        		Toast.makeText( getActivity(), "Recording Rules updated!", Toast.LENGTH_SHORT ).show();

	        		adapter.notifyDataSetChanged();
	        	}
	        	
	        }

        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : exit" );
		}
		
	}

}
