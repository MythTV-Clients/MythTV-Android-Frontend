/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.service.util.NotificationHelper;
import org.mythtv.service.util.NotificationHelper.NotificationType;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.RecRule;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRulesFragment extends MythtvListFragment {

	private static final String TAG = RecordingRulesFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private OnRecordingRuleListener listener = null;
	private RecordingRuleAdapter adapter;

	private MainApplication mainApplication;
	
	/**
	 * OnCheckChangeListener to control rule's active state
	 */
	private OnCheckedChangeListener sRuleCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			RecRule rule = (RecRule)buttonView.getTag();
			rule.setInactive(!isChecked);
			new SetRuleActiveStateTask().execute(isChecked?1:0, rule.getId());
		}
		
	};

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );
		setRetainInstance( true );

		mainApplication = (MainApplication) getActivity().getApplicationContext();
		
	    adapter = new RecordingRuleAdapter( getActivity().getApplicationContext() );
	    
	    setListAdapter( adapter );
	    getListView().setFastScrollEnabled( true );
	    
		Log.v( TAG, "onActivityCreated : exit" );
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

		RecRule rule = (RecRule) l.getItemAtPosition( position );
		
		listener.onRecordingRuleSelected( rule.getId() );
		
		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public void setOnRecordingRuleListener( OnRecordingRuleListener listener ) {
		Log.v( TAG, "setOnRecordingRuleListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnRecordingRuleListener : exit" );
	}

	public interface OnRecordingRuleListener {
		void onRecordingRuleSelected( Integer recordingRuleId );
	}
	
	private class SetRuleActiveStateTask extends AsyncTask<Integer, Void, Void>
	{	
		@Override
		protected Void doInBackground(Integer... params) {
			if(params[0] > 0){
				mainApplication.getMythServicesApi().dvrOperations().enableRecordingSchedule(params[1]);
			}else{
				mainApplication.getMythServicesApi().dvrOperations().disableRecordingSchedule(params[1]);
			}
			return null;
		}	
	}

	// internal helpers
	
	private class RecordingRuleAdapter extends BaseAdapter {

		private final DateTimeFormatter formatter = DateTimeFormat.forPattern( "yyyy-MM-dd" );
		
		private Context mContext;
		private LayoutInflater mInflater;

		private List<RecRule> rules = new ArrayList<RecRule>();

		private ProgramHelper mProgramHelper;
		private NotificationHelper mNotificationHelper;

		public RecordingRuleAdapter( Context context ) {
			
			mContext = context;
			mInflater = LayoutInflater.from( context );
			
			mProgramHelper = ProgramHelper.createInstance( context );
			mNotificationHelper = new NotificationHelper( context );
			
			if( null == rules || rules.isEmpty() ) {
				new DownloadRecordingRulesTask().execute();
			}
		}
		
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			if( null != rules ) {
				return rules.size();
			}
			
			return 0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public RecRule getItem( int position ) {
			if( null != rules ) {
				return rules.get( position );
			}
			
			return null;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId( int position ) {
			if( null != rules ) {
				return position;
			}
			
			return 0;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {

			convertView = mInflater.inflate( R.layout.recording_rules_row, parent, false );
			ViewHolder mHolder = new ViewHolder();
			mHolder.detailRow = (LinearLayout) convertView.findViewById( R.id.recording_rules_detail_row );
			mHolder.category = (View) convertView.findViewById( R.id.recording_rules_category );
			mHolder.title = (TextView) convertView.findViewById( R.id.recording_rules_title );
			mHolder.channel = (TextView) convertView.findViewById( R.id.recording_rules_channel );
			mHolder.type = (TextView) convertView.findViewById( R.id.recording_rules_type );
			mHolder.last = (TextView) convertView.findViewById( R.id.recording_rules_last );
			
			if(android.os.Build.VERSION.SDK_INT >= 14 ){
				mHolder.active = (CompoundButton)convertView.findViewById(R.id.recording_rules_switch_active);
			} else {
				mHolder.active = (CompoundButton)convertView.findViewById(R.id.recording_rules_checkbox_active);
			}
			
			RecRule rule = getItem( position );
			
			String channel = "[Any]";
			Cursor cursor = mContext.getContentResolver().query( ChannelConstants.CONTENT_URI, new String[] { ChannelConstants.FIELD_CHAN_NUM }, ChannelConstants.FIELD_CHAN_ID + " = ?", new String[] { "" + rule.getChanId() }, null );
			if( cursor.moveToFirst() ) {
				 channel = cursor.getString( cursor.getColumnIndexOrThrow( ChannelConstants.FIELD_CHAN_NUM ) );
			}
			cursor.close();

			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( rule.getCategory() ) );
			mHolder.title.setText( rule.getTitle() );
			mHolder.channel.setText( channel );
			mHolder.type.setText( rule.getType() );
			mHolder.last.setText( formatter.print( rule.getLastRecorded() ) );
			mHolder.active.setChecked(!rule.isInactive());
			mHolder.active.setTag(rule);
			mHolder.active.setOnCheckedChangeListener(sRuleCheckChangeListener);
			
			return convertView;
		}
		
		private class ViewHolder {
			
			LinearLayout detailRow;
			View category;
			
			TextView title;
			TextView channel;
			TextView type;
			TextView last;
			CompoundButton active;
			
			ViewHolder() { }

		}

		private class DownloadRecordingRulesTask extends AsyncTask<Void, Void, List<RecRule>> {

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
			@Override
			protected List<RecRule> doInBackground( Void... params ) {
				
				String message = "Retrieving Recording Rules";
				mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );

				ETagInfo etag = ETagInfo.createEmptyETag();
				return mainApplication.getMythServicesApi().dvrOperations().getRecordScheduleList( -1, -1, etag );
			}

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute( List<RecRule> result ) {
				
				mNotificationHelper.completed();
				
				if( null != result && !result.isEmpty() ) {
					rules = result;
					
					notifyDataSetChanged();
				}
			}
			
		}
		
	}

}
