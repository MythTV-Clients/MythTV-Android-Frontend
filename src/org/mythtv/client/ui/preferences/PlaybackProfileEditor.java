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
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.preferences;

import static android.provider.BaseColumns._ID;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.MythtvDatabaseManager;
import org.mythtv.db.preferences.PlaybackProfileConstants;

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
 * 
 */
public class PlaybackProfileEditor extends AbstractMythtvFragmentActivity {

	private static final String TAG = PlaybackProfileEditor.class.getSimpleName();

	private PlaybackProfile profile;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( this.getLayoutInflater().inflate( R.layout.preference_playback_profile_editor, null ) );

		setupSaveButtonEvent( R.id.btnPreferencePlaybackProfileSave );
		setupCancelButtonEvent( R.id.btnPreferencePlaybackProfileCancel );

		int id = getIntent().getIntExtra( _ID, -1 );
		//if( id != -1 ) {
			profile = new PlaybackProfile();
			profile.setId( id );
			profile.setType( LocationType.valueOf( getIntent().getStringExtra( PlaybackProfileConstants.FIELD_TYPE ) ) );
			profile.setName( getIntent().getStringExtra( PlaybackProfileConstants.FIELD_NAME ) );
			profile.setWidth( getIntent().getIntExtra( PlaybackProfileConstants.FIELD_WIDTH, -1 ) );
			profile.setHeight( getIntent().getIntExtra( PlaybackProfileConstants.FIELD_HEIGHT, -1 ) );
			profile.setVideoBitrate( getIntent().getIntExtra( PlaybackProfileConstants.FIELD_BITRATE, -1 ) );
			profile.setAudioBitrate( getIntent().getIntExtra( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, -1 ) );
			profile.setAudioSampleRate( getIntent().getIntExtra( PlaybackProfileConstants.FIELD_SAMPLE_RATE, -1 ) );
			profile.setSelected( 0 != getIntent().getIntExtra( PlaybackProfileConstants.FIELD_SELECTED, 0 ) );

			setUiFromPlaybackProfile();
		//}

		Log.v( TAG, "onCreate : exit" );
	}

	// internal helpers

	private void setUiFromPlaybackProfile() {
		Log.v( TAG, "setUiFromPlaybackProfile : enter" );

		setName( profile.getName() );
		setWidth( "" + profile.getWidth() );
		setHeight( "" + profile.getHeight() );
		setVideoBitrate( "" + profile.getVideoBitrate() );
		setAudioBitrate( "" + profile.getAudioBitrate() );
		setSampleRate( "" + profile.getAudioSampleRate() );

		Log.v( TAG, "setUiFromPlaybackProfile : exit" );
	}

	private final String getName() {
		return getTextBoxText( R.id.preference_playback_profile_edit_text_name );
	}

	private final void setName( String name ) {
		setTextBoxText( R.id.preference_playback_profile_edit_text_name, name );
	}

	private final String getWidth() {
		return getTextBoxText( R.id.preference_playback_profile_edit_text_width );
	}

	private final void setWidth( String width ) {
		setTextBoxText( R.id.preference_playback_profile_edit_text_width, width );
	}

	private final String getHeight() {
		return getTextBoxText( R.id.preference_playback_profile_edit_text_height );
	}

	private final void setHeight( String height ) {
		setTextBoxText( R.id.preference_playback_profile_edit_text_height, height );
	}

	private final String getVideoBitrate() {
		return getTextBoxText( R.id.preference_playback_profile_edit_text_video_bitrate );
	}

	private final void setVideoBitrate( String videoBitrate ) {
		setTextBoxText( R.id.preference_playback_profile_edit_text_video_bitrate, videoBitrate );
	}

	private final String getAudioBitrate() {
		return getTextBoxText( R.id.preference_playback_profile_edit_text_audio_bitrate );
	}

	private final void setAudioBitrate( String audioBitrate ) {
		setTextBoxText( R.id.preference_playback_profile_edit_text_audio_bitrate, audioBitrate );
	}

	private final String getSampleRate() {
		return getTextBoxText( R.id.preference_playback_profile_edit_text_sample_rate );
	}

	private final void setSampleRate( String sampleRate ) {
		setTextBoxText( R.id.preference_playback_profile_edit_text_sample_rate, sampleRate );
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
			Log.v( TAG, "save : creating new Playback Profile" );

			profile = new PlaybackProfile();
		}

		profile.setName( getName() );
		profile.setWidth( Integer.parseInt( getWidth() ) );
		profile.setHeight( Integer.parseInt( getHeight() ) );
		profile.setVideoBitrate( Integer.parseInt( getVideoBitrate() ) );
		profile.setAudioBitrate( Integer.parseInt( getAudioBitrate() ) );
		profile.setAudioSampleRate( Integer.parseInt( getSampleRate() ) );

		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( R.string.preference_edit_error_dialog_title );
		builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

			public void onClick( DialogInterface dialog, int which ) { }
			
		});
		
		if( "".equals( profile.getName().trim() ) ) {
			Log.v( TAG, "save : name contains errors" );

			builder.setMessage( R.string.preference_playback_profile_text_view_name_error_invalid );
			builder.show();
		} else if( profile.getWidth() < 1 ) {
			Log.v( TAG, "save : width contains errors" );

			builder.setMessage( R.string.preference_playback_profile_text_view_width_error_invalid );
			builder.show();
		} else if( profile.getHeight() < 1 ) {
			Log.v( TAG, "save : height contains errors" );

			builder.setMessage( R.string.preference_playback_profile_text_view_height_error_invalid );
			builder.show();
		} else if( profile.getVideoBitrate() < 1 ) {
			Log.v( TAG, "save : video bitrate contains errors" );

			builder.setMessage( R.string.preference_playback_profile_text_view_video_bitrate_error_invalid );
			builder.show();
		} else if( profile.getAudioBitrate() < 1 ) {
			Log.v( TAG, "save : audio bitrate contains errors" );

			builder.setMessage( R.string.preference_playback_profile_text_view_audio_bitrate_error_invalid );
			builder.show();
		} else if( profile.getAudioSampleRate() < 1 ) {
			Log.v( TAG, "save : sample rate contains errors" );

			builder.setMessage( R.string.preference_playback_profile_text_view_sample_rate_error_invalid );
			builder.show();
		} else {
			Log.v( TAG, "save : proceeding to save" );

			MythtvDatabaseManager db = new MythtvDatabaseManager( this );
			if( profile.getId() != -1 ) {
				Log.v( TAG, "save : updating existing playback profile" );

				retVal = db.updatePlaybackProfile( profile );
			}
		}

		Log.v( TAG, "save : exit" );
		return retVal;
	}

}
