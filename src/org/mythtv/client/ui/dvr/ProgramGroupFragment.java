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
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.ProgramDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.db.dvr.model.Program;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class ProgramGroupFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = ProgramGroupFragment.class.getSimpleName();
	
	public interface OnEpisodeSelectedListener {
		void onEpisodeSelected( int channelId, DateTime startTime );
	}
	
	private ProgramCursorAdapter mAdapter;
	
	private OnEpisodeSelectedListener mEpisodeListener;
	
	private static ProgramHelper mProgramHelper = ProgramHelper.getInstance();;
	private static ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();;
	private static RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	private LocationProfile mLocationProfile;
	
	private ProgramGroup programGroup;
	
	public ProgramGroupFragment() { }
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );

		if( null == programGroup ) {
			programGroup = mProgramGroupDaoHelper.findByTitle( getActivity(), mLocationProfile, "All" );
		}
		
		String[] projection = { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_CATEGORY, ProgramConstants.FIELD_START_TIME };
		
		String selection = ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_IN_ERROR + " = ?";
		if( null != programGroup && !"All".equals( programGroup.getTitle() ) ) {
			selection = ProgramConstants.FIELD_TITLE + " = ? AND " + selection;
		}
		
		String[] selectionArgs = { mLocationProfile.getHostname(), "0" };
		if( null != programGroup && !"All".equals( programGroup.getTitle() ) ) {
			selectionArgs = new String[] { ( null != programGroup && null != programGroup.getTitle() ? programGroup.getTitle() : "" ), mLocationProfile.getHostname(), "0" };
		}
		
		String sort = ( ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_END_TIME ) + " DESC";
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramConstants.CONTENT_URI_RECORDED, projection, selection, selectionArgs, sort );
	    
	    Log.v( TAG, "onCreateLoader : exit" );
		return cursorLoader;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		mAdapter.swapCursor( cursor );
		
		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		mAdapter.swapCursor( null );
				
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.util.MythtvListFragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.i( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
	    
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		mAdapter = new ProgramCursorAdapter( getActivity() );

		if( null != savedInstanceState ) {
			Long id = savedInstanceState.getLong( "PROGRAM_GROUP_ID" );
			
			programGroup = mProgramGroupDaoHelper.findOne( getActivity(), id );
		} else {
			programGroup = mProgramGroupDaoHelper.findByTitle( getActivity(), mLocationProfile, "All" );
		}
		
	    setListAdapter( mAdapter );

	    getLoaderManager().initLoader( 0, null, this );

	    Log.i( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		if( null == mLocationProfile ) {
			mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		}

		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
    public void onSaveInstanceState( Bundle outState ) {
		Log.i( TAG, "onSaveInstanceState : enter" );
		super.onSaveInstanceState(outState);
        
		outState.putLong( "PROGRAM_GROUP_ID", programGroup.getId() );
		
		Log.i( TAG, "onSaveInstanceState : exit" );
    }
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.util.MythtvListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );
		super.onListItemClick( l, v, position, id );
		
		Log.v ( TAG, "onListItemClick : position=" + position + ", id=" + id );
		
		Program program = mRecordedDaoHelper.findOne( getActivity(), id );
		
		if( null != program && null != mEpisodeListener ) {
			Log.v( TAG, "onListItemClick : selecting episode" );
			Log.v( TAG, "onListItemClick : program=" + program.toString() );
			
			mEpisodeListener.onEpisodeSelected( program.getChannelInfo().getChannelId(), program.getStartTime() );
		}

		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public ProgramGroup getSelectedProgramGroup() {
		return programGroup;
	}
	
	public void loadProgramGroup( ProgramGroup programGroup ) {
		Log.v( TAG, "loadProgramGroup : enter" );
		
		Log.v( TAG, "loadProgramGroup : programGroup=" + programGroup );

		this.programGroup = programGroup;

	    getLoaderManager().restartLoader( 0, null, this );

		Log.v( TAG, "loadProgramGroup : exit" );
	}
	
	/**
	 * @param listener
	 */
	public void setOnEpisodeSelectedListener( OnEpisodeSelectedListener listener ) {
		this.mEpisodeListener = listener;
	}
	
	// internal helpers
	
	private class ProgramCursorAdapter extends CursorAdapter {

		private Context mContext;
		private LayoutInflater mInflater;
		
		private MainApplication mainApplication;
		
		public ProgramCursorAdapter( Context context ) {
			super( context, null, false );
		
			mContext = context;
			mInflater = LayoutInflater.from( context );
			
			mainApplication = (MainApplication) mContext.getApplicationContext();
		}

		/* (non-Javadoc)
		 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			Log.v( TAG, "newView : enter" );

	        View view = mInflater.inflate( R.layout.program_row, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.programGroupDetail = (LinearLayout) view.findViewById( R.id.program_group_detail );
			refHolder.category = (View) view.findViewById( R.id.program_category );
			refHolder.subTitle = (TextView) view.findViewById( R.id.program_sub_title );
			
			view.setTag( refHolder );
			
			Log.v( TAG, "newView : enter" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Log.v( TAG, "bindView : enter" );

			Program program = ProgramDaoHelper.convertCursorToProgram( cursor, ProgramConstants.TABLE_NAME_RECORDED );
//			if( null != program ) {
//				Log.v( TAG, "bindView : program=" + program.toString() );
//			}
			
			String title = "";
			if( null != programGroup && "All".equals( programGroup.getProgramGroup() ) ) {
				
				if( null != program.getSubTitle() && !"".equals( program.getSubTitle() ) ) {
					title = program.getTitle() + " : " + program.getSubTitle();
				} else {
					title = program.getTitle() + " - " + DateUtils.getDateTimeUsingLocaleFormattingPretty( program.getStartTime(), mainApplication.getDateFormat(), mainApplication.getClockType() );
				}

			} else {

				if( null != program.getSubTitle() && !"".equals( program.getSubTitle() ) ) {
					title = program.getSubTitle();
				} else {
					title = DateUtils.getDateTimeUsingLocaleFormattingPretty( program.getStartTime(), mainApplication.getDateFormat(), mainApplication.getClockType() );
				}

			}
			
			final ViewHolder mHolder = (ViewHolder) view.getTag();
	        mHolder.subTitle.setText( title  );
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
			
			Log.v( TAG, "bindView : exit" );
		}

	}

	private static class ViewHolder {
		
		LinearLayout programGroupDetail;
		View category;
		TextView subTitle;
		
		ViewHolder() { }

	}
	
}
