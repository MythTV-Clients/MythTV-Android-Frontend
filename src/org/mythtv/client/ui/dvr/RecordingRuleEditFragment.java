/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.RecRule;
import org.mythtv.services.api.dvr.RecRuleWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleEditFragment extends AbstractMythFragment {

	private static final String TAG = RecordingRuleEditFragment.class.getSimpleName();
	
	private ProgramHelper mProgramHelper;
	
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

		mProgramHelper = ProgramHelper.createInstance( getActivity() );
		
		Log.v( TAG, "onActivityCreated : exit" );
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
		Cursor cursor = this.getActivity().getContentResolver().query( ChannelConstants.CONTENT_URI, new String[] { ChannelConstants.FIELD_CHAN_NUM }, ChannelConstants.FIELD_CHAN_ID + " = ?", new String[] { "" + rule.getChanId() }, null );
		if( cursor.moveToFirst() ) {
			 channel = cursor.getString( cursor.getColumnIndexOrThrow( ChannelConstants.FIELD_CHAN_NUM ) );
		}
		cursor.close();
		
		tView = (TextView) getActivity().findViewById( R.id.recording_rule_channel );
		tView.setText( channel );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_active );
		cBox.setChecked( !rule.isInactive() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_comm_flag );
		cBox.setChecked( rule.isAutoCommflag() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_transcode );
		cBox.setChecked( rule.isAutoTranscode() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_meta_lookup );
		cBox.setChecked( rule.isAutoMetaLookup() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job1 );
		cBox.setChecked( rule.isAutoUserJob1() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job2 );
		cBox.setChecked( rule.isAutoUserJob2() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job3 );
		cBox.setChecked( rule.isAutoUserJob3() );
		
		cBox = (CheckBox) getActivity().findViewById( R.id.recording_rule_checkBox_auto_usr_job4 );
		cBox.setChecked( rule.isAutoUserJob4() );
		
		Log.v( TAG, "setupForm : exit" );
	}
	
	private class DownloadRecordingRuleTask extends AsyncTask<Integer, Void, ResponseEntity<RecRuleWrapper>> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResponseEntity<RecRuleWrapper> doInBackground( Integer... params ) {
			
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
	
}
