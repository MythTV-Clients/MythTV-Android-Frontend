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
/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.RecRule;
import org.mythtv.services.api.dvr.RecRuleWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleFragment extends AbstractMythFragment {

	private static final String TAG = RecordingRuleFragment.class.getSimpleName();
	
	private ChannelDaoHelper mChannelDaoHelper;
	private MenuHelper mMenuHelper;
	private ProgramHelper mProgramHelper;
	private Integer mRecordingRuleId;
	
	public static RecordingRuleFragment newInstance( Bundle args ) {
		RecordingRuleFragment fragment = new RecordingRuleFragment();
		fragment.setArguments( args );
		
		return fragment;
	}
	
	public RecordingRuleFragment() { }
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );
		
		//we have an option menu
		this.setHasOptionsMenu(true);

		Bundle args = getArguments();
		if( null != args ){
			int recordingRuleId = args.getInt( "RECORDING_RULE_ID" );
			loadRecordingRule( recordingRuleId );
		}
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View v = inflater.inflate( R.layout.recording_rule, container, false );
		
		Log.v( TAG, "onCreateView : exit" );
		return v;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mChannelDaoHelper = new ChannelDaoHelper( getActivity() );
		mProgramHelper = ProgramHelper.createInstance( getActivity() );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	public void loadRecordingRule( Integer recordingRuleId ) {
		Log.v( TAG, "loadRecordingRule : enter" );
		
		mRecordingRuleId = recordingRuleId;

		new DownloadRecordingRuleTask().execute( mRecordingRuleId );
		
		Log.v( TAG, "loadRecordingRule : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

		mMenuHelper = ( (AbstractMythtvFragmentActivity) getActivity() ).getMenuHelper();

		mMenuHelper.editMenuItem( menu );
		mMenuHelper.deleteMenuItem( menu );

		Log.v( TAG, "onCreateOptionsMenu : exit" );
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		Intent intent = null;
		
		switch( item.getItemId() ) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				intent = new Intent( this.getActivity(), RecordingRulesActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );

				return true;
			
			case MenuHelper.EDIT_ID:

				Toast.makeText( getActivity(), "Edit Recording Rule - Coming Soon!", Toast.LENGTH_SHORT ).show();
				
//				intent = new Intent( this.getActivity(), RecordingRuleEditActivity.class );
//				intent.putExtra( RecordingRuleEditActivity.EXTRA_RECORDING_RULE_EDIT_KEY, mRecordingRuleId );
//				startActivity( intent );
				
				return true;

			case MenuHelper.DELETE_ID:

				Toast.makeText( getActivity(), "Delete Recording Rule - Coming Soon!", Toast.LENGTH_SHORT ).show();
				
				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}
	
	// internal helpers

	private void setup( RecRule rule ) {
		Log.v( TAG, "setup : enter" );
		
		View view;
		TextView tView;
		
		view = getActivity().findViewById( R.id.recording_rule_category_color) ;
		view.setBackgroundColor( mProgramHelper.getCategoryColor( rule.getCategory() ) );
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_title );
		tView.setText( rule.getTitle() );

		tView = (TextView) getActivity().findViewById( R.id.recording_rule_sub_title );
		if( null != rule.getSubTitle() && rule.getSubTitle() != "" ) {
			tView.setText( rule.getSubTitle() );
		}
		else {
			tView.setText( "-" );
		}
		tView.setVisibility( View.VISIBLE );
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_category );
		tView.setText(rule.getCategory());
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_type );
		tView.setText(rule.getType());
		
		//grabbed channel resolving code from RecordingRulesFragment.java
		// - should we move this to a utility?
		// - slow
		String channel = "[Any]";
		if( rule.getChanId() > 0 ) {
			ChannelInfo channelInfo = mChannelDaoHelper.findByChannelId( (long) rule.getChanId() );
			if( null != channelInfo && channelInfo.getChannelId() > -1 ) {
				channel = channelInfo.getChannelNumber();
			}
		}
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_channel );
		tView.setText( channel );
		
		Log.v( TAG, "setup : exit" );
	}
	
	private class DownloadRecordingRuleTask extends AsyncTask<Integer, Void, ResponseEntity<RecRuleWrapper>> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResponseEntity<RecRuleWrapper> doInBackground( Integer... params ) {
			
			if( !NetworkHelper.getInstance().isMasterBackendConnected() ) {
				return null;
			}
			
			Integer id = params[ 0 ];
			
			ETagInfo etag = ETagInfo.createEmptyETag();
			return getMainApplication().getMythServicesApi().dvrOperations().getRecordSchedule( id, etag );
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( ResponseEntity<RecRuleWrapper> result ) {
			
			if( null != result ) {
				
				if( result.getStatusCode().equals( HttpStatus.OK ) ) {
					setup( result.getBody().getRecRule() );
				}
				
			}
			
		}
		
	}
	
}
