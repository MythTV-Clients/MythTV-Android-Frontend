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
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.Int;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.RecRule;
import org.mythtv.services.api.dvr.RecRuleWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleEditFragment extends AbstractMythFragment implements OnCheckedChangeListener {

	private static final String TAG = RecordingRuleEditFragment.class.getSimpleName();
	
	private ChannelDaoHelper mChannelDaoHelper;
	private ProgramHelper mProgramHelper;
	private boolean mEdited = false;
	private RecRule mRule;
	
	public static RecordingRuleEditFragment newInstance( Bundle args ) {
		RecordingRuleEditFragment fragment = new RecordingRuleEditFragment();
		fragment.setArguments( args );
		
		return fragment;
	}

	public RecordingRuleEditFragment() { }
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );
		
		this.setHasOptionsMenu(true);

		Bundle args = getArguments();
		if( null != args ) {
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

		View v = inflater.inflate( R.layout.recording_rule_edit, container, false );
		
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

	@Override
	@TargetApi( 11 )
	public void onPrepareOptionsMenu(Menu menu) {
		
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		
	    MenuItem save = menu.add( Menu.NONE, RecordingRulesActivity.SAVE_ID, Menu.NONE, "SAVE" );
	    MenuItem reset = menu.add( Menu.NONE, RecordingRulesActivity.RESET_ID, Menu.NONE, "RESET" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	save.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
	    	save.setIcon( android.R.drawable.ic_menu_save );
	    	
	    	reset.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
	    	reset.setIcon( android.R.drawable.ic_menu_revert );
	    }
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
		
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		Intent intent = null;
		
		switch( item.getItemId() ) {
			case android.R.id.home:
				this.getActivity().finish();
				return true;
				
			case RecordingRulesActivity.RESET_ID:
				this.setupForm(this.mRule);
				return true;
				
			case RecordingRulesActivity.SAVE_ID:
				this.saveRecordingRule();
				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}
	
	/**
	 * Called when the rule is edited
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		this.mEdited = true;
	}

	public void loadRecordingRule( Integer recordingRuleId ) {
		Log.v( TAG, "loadRecordingRule : enter" );

		if( null != getActivity() ) {
			new DownloadRecordingRuleTask().execute( recordingRuleId );
		}
		
		Log.v( TAG, "loadRecordingRule : exit" );
	}
	
	// internal helpers

	private void setupForm( RecRule rule ) {
		Log.v( TAG, "setupForm : enter" );
		
		View view;
		CheckBox cBox;
		TextView tView;
		
		this.mRule = rule;
		
		view = getActivity().findViewById( R.id.recording_rule_category_color );
		view.setBackgroundColor( mProgramHelper.getCategoryColor( rule.getCategory() ) );
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_title );
		tView.setText( rule.getTitle() );
		
		if( null != rule.getSubTitle() && rule.getSubTitle() != "" ) {
			tView = (TextView) getActivity().findViewById( R.id.recording_rule_sub_title );
			tView.setText( rule.getSubTitle() );
			tView.setVisibility( View.VISIBLE );
		}
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_category );
		tView.setText( rule.getCategory() );
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_type );
		tView.setText( rule.getType() );
		
		//grabbed channel resolving code from RecordingRulesFragment.java
		// - should we move this to a utility?
		// - slow
		String channel = "[Any]";
		ChannelInfo channelInfo = mChannelDaoHelper.findOne( null, new String[] { ChannelConstants.FIELD_CHAN_NUM }, ChannelConstants.FIELD_CALLSIGN + " = ?", new String[] { rule.getCallSign() }, null );
		if( null != channelInfo ) {
			channel = channelInfo.getChannelNumber();
		}
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_channel );
		tView.setText( channel );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_active );
		cBox.setChecked( !rule.isInactive() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_comm_flag );
		cBox.setChecked( rule.isAutoCommflag() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_transcode );
		cBox.setChecked( rule.isAutoTranscode() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_meta_lookup );
		cBox.setChecked( rule.isAutoMetaLookup() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job1 );
		cBox.setChecked( rule.isAutoUserJob1() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job2 );
		cBox.setChecked( rule.isAutoUserJob2() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job3 );
		cBox.setChecked( rule.isAutoUserJob3() );
		cBox.setOnCheckedChangeListener(this);
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job4 );
		cBox.setChecked( rule.isAutoUserJob4() );
		cBox.setOnCheckedChangeListener(this);
		
		Log.v( TAG, "setupForm : exit" );
	}
	
	
	/**
	 * Reads the rule state from the UI and saves it back to the master backend
	 * @return
	 */
	private void saveRecordingRule(){
		
		Log.v( TAG, "saveRecordingRule : enter" );
		
		//nothing to do
		if(!this.mEdited) 
		{
			Log.v( TAG, "saveRecordingRule : do nothing : exit" );
			return;
		}
		
		RecRule rule = this.mRule;
		CheckBox cBox;
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_active );
		rule.setInactive(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_comm_flag );
		rule.setAutoCommflag(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_transcode );
		rule.setAutoTranscode(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_meta_lookup );
		rule.setAutoMetaLookup(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job1 );
		rule.setAutoUserJob1(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job2 );
		rule.setAutoUserJob2(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job3 );
		rule.setAutoUserJob3(cBox.isChecked());
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job4 );
		rule.setAutoUserJob4(cBox.isChecked());
		
		new SaveRecordingRuleTask().execute(rule);
		
		Log.v( TAG, "saveRecordingRule : exit" );
	}
	
	private void toast(String msg){
		Toast t = Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT);
		t.show();
	}
	
	private class DownloadRecordingRuleTask extends AsyncTask<Integer, Void, ResponseEntity<RecRuleWrapper>> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResponseEntity<RecRuleWrapper> doInBackground( Integer... params ) {
			
			ResponseEntity<String> hostname = getMainApplication().getMythServicesApi().mythOperations().getHostName();
			if( null == hostname || "".equals( hostname ) ) {
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
					setupForm( result.getBody().getRecRule() );
				}
				
			}
			
		}
		
	}
	
	private class SaveRecordingRuleTask extends AsyncTask<RecRule, Void, String>{

		@Override
		protected String doInBackground(RecRule... params) {

			Log.v( TAG, "SaveRecordingRuleTask : doInBackground() : enter" );
			
			try {
				RecRule rule = params[0];
				ETagInfo etag = ETagInfo.createEmptyETag();
				ResponseEntity<Int> response = getMainApplication()
						.getMythServicesApi()
						.dvrOperations()
						.addRecordingSchedule(rule.getChanId(),
								rule.getStartTime(), rule.getParentId(),
								rule.isInactive(), rule.getSeason(),
								rule.getEpisode(), rule.getInetref(),
								rule.getFindId(), rule.getType(),
								rule.getSearchType(), rule.getRecPriority(),
								rule.getPreferredInput(),
								rule.getStartOffset(), rule.getEndOffset(),
								rule.getDupMethod(), rule.getDupIn(),
								rule.getFilter(), rule.getRecProfile(),
								rule.getRecGroup(), rule.getStorageGroup(),
								rule.getPlayGroup(), rule.isAutoExpire(),
								rule.getMaxEpisodes(), rule.isMaxNewest(),
								rule.isAutoCommflag(), rule.isAutoTranscode(),
								rule.isAutoMetaLookup(), rule.isAutoUserJob1(),
								rule.isAutoUserJob2(), rule.isAutoUserJob3(),
								rule.isAutoUserJob4(), rule.getTranscoder());

				// on successful add, delete old recording rule
				if (response.hasBody() && response.getBody().getInteger() == 1) {
					getMainApplication().getMythServicesApi().dvrOperations().removeRecordingSchedule(rule.getId());
				}

			} catch (Exception e) {
				
				Log.e(TAG, e.getMessage());
				
				Log.v( TAG, "SaveRecordingRuleTask : doInBackground() : exit-error" );
				return "Failed To Save Recording Rule";
			}

			Log.v( TAG, "SaveRecordingRuleTask : doInBackground() : exit" );
			return "Recording Rule Saved";
		}
				
		@Override
		protected void onPostExecute(String result) {
			
			toast(result);
			
			super.onPostExecute(result);
		}
	}
	
}
