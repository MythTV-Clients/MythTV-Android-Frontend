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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * @author John Baab <rhpot1991@ubuntu.com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.preferences;

import org.mythtv.R;
import org.mythtv.client.db.DatabaseHelper;
import org.mythtv.client.db.MythtvDatabaseManager;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class LocationProfileEditor extends AbstractMythtvFragmentActivity {

	private static final String TAG = LocationProfileEditor.class.getSimpleName();

	private LocationProfile profile;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( this.getLayoutInflater().inflate( R.layout.preference_location_profile_editor, null ) );

		setupSaveButtonEvent( R.id.btnPreferenceLocationProfileSave );
		setupCancelButtonEvent( R.id.btnPreferenceLocationProfileCancel );

		int id = getIntent().getIntExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_ID, -1 );
		//if( id != -1 ) {
			profile = new LocationProfile();
			profile.setId( id );
			profile.setType( LocationType.valueOf( getIntent().getStringExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_TYPE ) ) );
			profile.setName( getIntent().getStringExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_NAME ) );
			profile.setUrl( getIntent().getStringExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_URL ) );
			profile.setSelected( 0 != getIntent().getIntExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_SELECTED, 0 ) );

			setUiFromLocationProfile();
		//}

		Log.v( TAG, "onCreate : exit" );
	}

	// internal helpers

	private void setUiFromLocationProfile() {
		Log.v( TAG, "setUiFromLocationProfile : enter" );

		setName( profile.getName() );
		setUrl( profile.getUrl() );

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
		
		if (!url.matches("^http://.*")){
			url = "http://" + url;
		}
		
		if (!url.matches(".*/$")){
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

			finish();
		}
		
		Log.v( TAG, "saveAndExit : exit" );
	}

	private boolean save() {
		Log.v( TAG, "save : enter" );
		
		boolean retVal = false;
		
		if( null == profile ) {
			Log.v( TAG, "save : creating new Location Profile" );

			profile = new LocationProfile();
		}

		profile.setName( getName() );
		profile.setUrl( getUrl() );

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
		} else {
			Log.v( TAG, "save : proceeding to save" );

			MythtvDatabaseManager db = new MythtvDatabaseManager( this );
			if( profile.getId() == -1 ) {
				Log.v( TAG, "save : saving new location profile" );

				switch( profile.getType() ) {
				case HOME:
					profile.setId( (int) db.createHomeLocationProfile( profile ) );
					retVal = true;
					break;
				case AWAY:
					profile.setId( (int) db.createAwayLocationProfile( profile ) );
					retVal = true;
					break;
				}
			} else {
				Log.v( TAG, "save : updating existing location profile" );

				retVal = db.updateLocationProfile( profile );
			}
		}

		Log.v( TAG, "save : exit" );
		return retVal;
	}

}
