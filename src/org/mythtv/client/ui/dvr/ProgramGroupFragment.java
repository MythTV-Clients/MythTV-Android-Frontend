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
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.dvr.Program;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	
	public interface OnEpisodeSelectedListener
	{
		void onEpisodeSelected( int channelId, DateTime startTime );
	}
	
	private ProgramCursorAdapter mAdapter;
	
	private OnEpisodeSelectedListener mEpisodeListener;
	
	private static ProgramHelper mProgramHelper = ProgramHelper.getInstance();;
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	
	private ProgramGroup programGroup;
	
	public ProgramGroupFragment() { }
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		String[] projection = { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_CATEGORY, ProgramConstants.FIELD_START_TIME };
		String selection = ProgramConstants.FIELD_TITLE + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_IN_ERROR + " = ?";
		String[] selectionArgs = { ( null != programGroup && null != programGroup.getTitle() ? programGroup.getTitle() : "" ), locationProfile.getHostname(), "0" };
		 
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramConstants.CONTENT_URI_RECORDED, projection, selection, selectionArgs, ProgramConstants.FIELD_SEASON + " DESC ," + ProgramConstants.FIELD_EPISODE + " DESC" );
	    
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
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.i( TAG, "onCreateView : enter" );

		Log.i( TAG, "onCreateView : exit" );
		return super.onCreateView( inflater, container, savedInstanceState );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.util.MythtvListFragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.i( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
	    
		mAdapter = new ProgramCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.program_row,
	            null, new String[] { ProgramConstants.FIELD_SUB_TITLE }, new int[] { R.id.program_sub_title },
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

	    setListAdapter( mAdapter );

	    getLoaderManager().initLoader( 0, null, this );

	    Log.i( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
    public void onSaveInstanceState( Bundle outState ) {
		Log.i( TAG, "onSaveInstanceState : enter" );
		super.onSaveInstanceState(outState);
        
		Log.i( TAG, "onSaveInstanceState : exit" );
    }
	
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
	
	public void setOnEpisodeSelectedListener(OnEpisodeSelectedListener listener){
		this.mEpisodeListener = listener;
	}
	
	// internal helpers
	
	private static class ProgramCursorAdapter extends SimpleCursorAdapter {

		private LayoutInflater mInflater;
		
		public ProgramCursorAdapter( Context context, int layout, Cursor c, String[] from, int[] to, int flags ) {
			super( context, layout, c, from, to, flags );
		
			mContext = context;
			mInflater = LayoutInflater.from( context );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "getView : enter" );

            MainApplication mainApplication = (MainApplication) mContext.getApplicationContext();
			View v = convertView;
			ViewHolder mHolder;
			
			if( null == v ) {
				v = mInflater.inflate( R.layout.program_row, parent, false );
				
				mHolder = new ViewHolder();				
				mHolder.programGroupDetail = (LinearLayout) v.findViewById( R.id.program_group_detail );
				mHolder.category = (View) v.findViewById( R.id.program_category );
				mHolder.subTitle = (TextView) v.findViewById( R.id.program_sub_title );
				
				v.setTag( mHolder );
			} else {
				mHolder = (ViewHolder) v.getTag();
			}
						
			getCursor().moveToPosition( position );

			Long id = getCursor().getLong( getCursor().getColumnIndexOrThrow( ProgramConstants._ID ) );
	        String title = getCursor().getString( getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE ) );
	        String subTitle = getCursor().getString( getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_SUB_TITLE ) );
	        String category = getCursor().getString( getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_CATEGORY ) );
			Long startTime = getCursor().getLong( getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_START_TIME ) );
	        Log.v( TAG, "getView : id=" + id + ", title=" + title + ", subTitle=" + subTitle );

	        mHolder.subTitle.setText(!"".equals(subTitle) ? subTitle : DateUtils.getDateTimeUsingLocaleFormattingPretty(new DateTime(startTime), mainApplication.getDateFormat(), mainApplication.getClockType()));
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( category ) );

			return v;
		}

	}

	private static class ViewHolder {
		
		LinearLayout programGroupDetail;
		View category;
		TextView subTitle;
		
		ViewHolder() { }

	}
	
}
