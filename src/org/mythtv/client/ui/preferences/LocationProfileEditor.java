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
package org.mythtv.client.ui.preferences;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.myth.GetHostnameTask;
import org.mythtv.service.preferences.PreferencesRecordedDownloadService;
import org.mythtv.service.util.RunningServiceHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class LocationProfileEditor extends AbstractMythtvFragmentActivity implements GetHostnameTask.TaskFinishedListener {

	private static final String TAG = LocationProfileEditor.class.getSimpleName();

    private PreferencesRecordedDownloadReceiver receiver = new PreferencesRecordedDownloadReceiver();

    private ProgressDialog mProgressDialog;
    
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	
	private boolean isNew = false;
	private LocationProfile profile;

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter preferencesRecordedDownloadFilter = new IntentFilter( PreferencesRecordedDownloadService.ACTION_DOWNLOAD );
		preferencesRecordedDownloadFilter.addAction( PreferencesRecordedDownloadService.ACTION_COMPLETE );
        registerReceiver( receiver, preferencesRecordedDownloadFilter );

		Log.v( TAG, "onStart : enter" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( this.getLayoutInflater().inflate( R.layout.preference_location_profile_editor, null ) );

		setupSaveButtonEvent( R.id.btnPreferenceLocationProfileSave );
		setupCancelButtonEvent( R.id.btnPreferenceLocationProfileCancel );

		long id = getIntent().getLongExtra( LocationProfileConstants._ID, -1 );

		profile = mLocationProfileDaoHelper.findOne( this, id );
		if( null == profile ) {
			isNew = true;
			profile = new LocationProfile();
			
			profile.setId( id );
			profile.setType( LocationType.valueOf( getIntent().getStringExtra( LocationProfileConstants.FIELD_TYPE ) ) );
			profile.setName( getIntent().getStringExtra( LocationProfileConstants.FIELD_NAME ) );
			profile.setUrl( getIntent().getStringExtra( LocationProfileConstants.FIELD_URL ) );
			profile.setSelected( 0 != getIntent().getIntExtra( LocationProfileConstants.FIELD_SELECTED, 0 ) );
			profile.setWolAddress( getIntent().getStringExtra( LocationProfileConstants.FIELD_WOL_ADDRESS ) );
		}
		
		setUiFromLocationProfile();

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != receiver ) {
			try {
				unregisterReceiver( receiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		if( mProgressDialog != null ) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
	    }

		Log.v( TAG, "onStop : exit" );
	}

	// internal helpers

	private void setUiFromLocationProfile() {
		Log.v( TAG, "setUiFromLocationProfile : enter" );

		setName( profile.getName() );
		setUrl( profile.getUrl() );
		setWolAddress( profile.getWolAddress() );

		Log.v( TAG, "setUiFromLocationProfile : exit" );
	}

	private final String getName() {
		return getTextBoxText( R.id.preference_location_profile_edit_text_name );
	}

	private final void setName( String name ) {
		setTextBoxText( R.id.preference_location_profile_edit_text_name, name );
	}

	private final String getUrl() {
		String url = getTextBoxText( R.id.preference_location_profile_edit_text_url ).trim();
		
		if (!url.startsWith("http://")){
			url = "http://" + url;
		}
		
		if (!url.endsWith("/")){
			url += "/";
		}
		
		if (!url.matches(".*:\\d+.*")){
			url = url.replaceAll("/$", ":6544/");
		}
		
		return url;
	}

	private final void setUrl( String url ) {
		setTextBoxText( R.id.preference_location_profile_edit_text_url, url );
	}

	private final String getWolAddress() {
		return getTextBoxText( R.id.preference_location_profile_edit_text_wol );
	}

	private final void setWolAddress( String wolAddress ) {
		setTextBoxText( R.id.preference_location_profile_edit_text_wol, wolAddress );
	}

	private final String getTextBoxText( int textBoxViewId ) {
		final EditText text = (EditText) findViewById( textBoxViewId );
		return text.getText().toString();
	}

	private final void setTextBoxText( int textBoxViewId, String text ) {
		final EditText textBox = (EditText) findViewById( textBoxViewId );
		textBox.setText( text );
	}

	private final void setupSaveButtonEvent( int buttonViewId ) {
		Log.v( TAG, "setupSaveButtonEvent : enter" );
		
		final Button button = (Button) this.findViewById( buttonViewId );
		button.setOnClickListener( new OnClickListener() {
			
			public void onClick( View v ) {
				Log.v( TAG, "setupSaveButtonEvent.onClick : enter" );
				
				saveAndExit();

				Log.v( TAG, "setupSaveButtonEvent.onClick : exit" );
			}
		
		});

		Log.v( TAG, "setupSaveButtonEvent : exit" );
	}

	private final void setupCancelButtonEvent( int buttonViewId ) {
		Log.v( TAG, "setupCancelButtonEvent : enter" );

		final Button button = (Button) this.findViewById( buttonViewId );
		button.setOnClickListener( new OnClickListener() {
		
			public void onClick( View v ) {
				Log.v( TAG, "setupCancelButtonEvent.onClick : enter" );

				finish();

				Log.v( TAG, "setupCancelButtonEvent.onClick : exit" );
			}
			
		});

		Log.v( TAG, "setupCancelButtonEvent : enter" );
	}

	private void saveAndExit() {
		Log.v( TAG, "saveAndExit : enter" );

		if( save() ) {
			Log.v( TAG, "saveAndExit : save completed successfully" );

			Long id = mLocationProfileDaoHelper.save( LocationProfileEditor.this, profile );
			profile = mLocationProfileDaoHelper.findOne( LocationProfileEditor.this, id );
			Log.v( TAG, "saveAndExit : profile=" + profile.toString() );
			
			if( !mRunningServiceHelper.isServiceRunning( this, "org.mythtv.service.preferences.PreferencesRecordedDownloadService" ) ) {
				
				mProgressDialog = ProgressDialog.show( LocationProfileEditor.this, getResources().getString( R.string.please_wait ), getResources().getString( R.string.preferences_profile_loading ), true, false );
				
				Intent intent = new Intent( PreferencesRecordedDownloadService.ACTION_DOWNLOAD );
				intent.putExtra( LocationProfileConstants._ID, profile.getId() );
				startService( intent );
			}

		}
		
		Log.v( TAG, "saveAndExit : exit" );
	}

	private boolean save() {
		Log.v( TAG, "save : enter" );
		
		boolean retVal = false;
		
		if( null == profile ) {
			Log.v( TAG, "save : creating new Location Profile" );

			isNew = true;
			profile = new LocationProfile();
		}
		Log.v( TAG, "save : isNew=" + isNew );
		
		profile.setName( getName() );
		profile.setUrl( getUrl() );
		profile.setWolAddress( getWolAddress() );
		
		LocationProfile existing = null;
		if( isNew ) {
			existing = mLocationProfileDaoHelper.findByLocationTypeAndUrl( this, profile.getType(), profile.getUrl() );
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( R.string.preference_edit_error_dialog_title );
		builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

			public void onClick( DialogInterface dialog, int which ) { }
			
		});
		
		if( "".equals( profile.getName().trim() ) ) {
			Log.v( TAG, "save : name contains errors" );

			builder.setMessage( R.string.preference_location_profile_text_view_name_error_invalid );
			builder.show();
		} else if( "".equals( profile.getUrl().trim() ) ) {
			Log.v( TAG, "save : url contains errors" );

			builder.setMessage( R.string.preference_location_profile_text_view_url_error_invalid );
			builder.show();
		} else if( null != existing ) {
			Log.v( TAG, "save : url exists" );
			
			builder.setMessage( R.string.preference_location_profile_text_view_url_error_already_exists );
			builder.show();
		} else {
			Log.v( TAG, "save : proceeding to save" );

			retVal = true;
		}

		Log.v( TAG, "save : exit" );
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetHostnameTask.TaskFinishedListener#onGetHostnameTaskStarted()
	 */
	@Override
	public void onGetHostnameTaskStarted() {
       	Log.d( TAG, "onGetHostnameTaskStarted : enter" );
		
       	Log.d( TAG, "onGetHostnameTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetHostnameTask.TaskFinishedListener#onGetHostnameTaskFinished(java.lang.String)
	 */
	@Override
	public void onGetHostnameTaskFinished( String result ) {
       	Log.d( TAG, "onGetHostnameTaskFinished : enter" );
		
		if( null != result ) {
			Log.v( TAG, "onGetHostnameTaskFinished : saving hostname '" + result + "'" );
				
			profile.setHostname( result );
				
			mLocationProfileDaoHelper.save( LocationProfileEditor.this, profile );
			Log.v( TAG, "profile=" + profile.toString() );
				
		    finish();
		        
		    return;
			
		}
		
	    if( mProgressDialog != null ) {
	    	mProgressDialog.dismiss();
	    	mProgressDialog = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder( LocationProfileEditor.this );
		builder.setTitle( R.string.preference_edit_error_dialog_title );
		builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

			public void onClick( DialogInterface dialog, int which ) { }
			
		});
		
		builder.setMessage( R.string.preference_location_profile_not_connected );
		builder.show();
	
       	Log.d( TAG, "onGetHostnameTaskFinished : exit" );
	}

	private class PreferencesRecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "PreferencesRecordedDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( PreferencesRecordedDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "PreferencesRecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( PreferencesRecordedDownloadService.EXTRA_COMPLETE ) );
	        }

	        profile = mLocationProfileDaoHelper.findOne( LocationProfileEditor.this, profile.getId() );
	        
	        new GetHostnameTask( profile, LocationProfileEditor.this ).execute();
	        
        	Log.i( TAG, "PreferencesRecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

}
