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

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.NotificationHelper;
import org.mythtv.service.util.NotificationHelper.NotificationType;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.RecRule;
import org.mythtv.services.api.dvr.RecRuleList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
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

	private NotificationHelper mNotificationHelper;

	private OnRecordingRuleListener listener = null;
	private RecordingRuleAdapter adapter;
	private ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	private MenuHelper mMenuHelper;
	
	private MainApplication mainApplication;
	
	/**
	 * OnCheckChangeListener to control rule's active state
	 */
	private OnCheckedChangeListener sRuleCheckChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
			RecRule rule = (RecRule) buttonView.getTag();
			rule.setInactive( !isChecked );
			
			new SetRuleActiveStateTask().execute( isChecked?1:0, rule.getId() );
		}
		
	};

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mNotificationHelper = new NotificationHelper( getActivity() );

		mMenuHelper = MenuHelper.newInstance( getActivity() );
		
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

		mMenuHelper.refreshMenuItem( menu );
		
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

			adapter.refresh();
			
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

		private Context mContext;
		private LayoutInflater mInflater;

		private List<RecRule> rules = new ArrayList<RecRule>();

		private ProgramHelper mProgramHelper;

		public RecordingRuleAdapter( Context context ) {
			
			mContext = context;
			mInflater = LayoutInflater.from( context );
			
			mProgramHelper = ProgramHelper.createInstance( context );
			
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

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
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
			Log.v( TAG, "rule=" + rule.toString() );
			
			String channel = "[Any]";
			if( rule.getChanId() > 0 ) {
				ChannelInfo channelInfo = mChannelDaoHelper.findByChannelId( (long) rule.getChanId() );
				if( null != channelInfo && channelInfo.getChannelId() > -1 ) {
					channel = channelInfo.getChannelNumber();
				}
			}
			
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( rule.getCategory() ) );
			mHolder.title.setText( rule.getTitle() );
			mHolder.channel.setText( channel );
			mHolder.type.setText( rule.getType() );
			mHolder.last.setText(DateUtils.getDateWithLocaleFormatting(rule.getLastRecorded(), mainApplication.getDateFormat()));
			mHolder.active.setChecked(!rule.isInactive());
			mHolder.active.setTag(rule);
			mHolder.active.setOnCheckedChangeListener(sRuleCheckChangeListener);
			
			return convertView;
		}
		
		public void refresh() {
			new DownloadRecordingRulesTask().execute();
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

		private class DownloadRecordingRulesTask extends AsyncTask<Void, Void, ResponseEntity<RecRuleList>> {

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
			@Override
			protected ResponseEntity<RecRuleList> doInBackground( Void... params ) {
				
				String message = "Retrieving Recording Rules";
				mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );
				
				if( !NetworkHelper.getInstance().isMasterBackendConnected() ) {
					return null;
				}

				ETagInfo etag = ETagInfo.createEmptyETag();
				return mainApplication.getMythServicesApi().dvrOperations().getRecordScheduleList( -1, -1, etag );
			}

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute( ResponseEntity<RecRuleList> result ) {
				
				mNotificationHelper.completed();
				
				if( null != result ) {
					
					if( result.getStatusCode().equals( HttpStatus.OK ) ) {
						rules = result.getBody().getRecRules().getRecRules();
					
						notifyDataSetChanged();
					}
					
				}
				
			}
			
		}
		
	}

}
