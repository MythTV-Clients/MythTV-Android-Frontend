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

package org.mythtv.client.db;

import static org.mythtv.client.db.DatabaseHelper.TABLE_LOCATION_PROFILE;
import static org.mythtv.client.db.DatabaseHelper.TABLE_LOCATION_PROFILE_ID;
import static org.mythtv.client.db.DatabaseHelper.TABLE_LOCATION_PROFILE_NAME;
import static org.mythtv.client.db.DatabaseHelper.TABLE_LOCATION_PROFILE_SELECTED;
import static org.mythtv.client.db.DatabaseHelper.TABLE_LOCATION_PROFILE_TYPE;
import static org.mythtv.client.db.DatabaseHelper.TABLE_LOCATION_PROFILE_URL;

import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_ID;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_NAME;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_SELECTED;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_TYPE;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_WIDTH;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_HEIGHT;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_BITRATE;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE;
import static org.mythtv.client.db.DatabaseHelper.TABLE_PLAYBACK_PROFILE_SAMPLE_RATE;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class MythtvDatabaseManager {

	private static final String TAG = MythtvDatabaseManager.class.getSimpleName();

	private final Context context;

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	/**
	 * @param context
	 */
	public MythtvDatabaseManager( final Context context ) {
		Log.v( TAG, "initialize : enter" );

		this.context = context;
		this.helper = new DatabaseHelper( context );

		Log.v( TAG, "initialize : exit" );
	}

	/**
	 * 
	 */
	private void open() {
		Log.v( TAG, "open : enter" );

		if( null != db ) {
			close();
		}

		db = helper.getWritableDatabase();

		Log.v( TAG, "open : exit" );
	}

	/**
	 * 
	 */
	private void close() {
		Log.v( TAG, "close : enter" );

		helper.close();
		db = null;

		Log.v( TAG, "open : exit" );

	}

	/**
	 * @return
	 */
	public List<LocationProfile> fetchHomeLocationProfiles() {
		Log.v( TAG, "fetchHomeLocationProfiles : enter" );
		Log.v( TAG, "fetchHomeLocationProfiles : exit" );
		return fetchLocationProfilesByType( LocationType.HOME );
	}

	/**
	 * @return
	 */
	public List<LocationProfile> fetchAwayLocationProfiles() {
		Log.v( TAG, "fetchAwayLocationProfiles : enter" );
		Log.v( TAG, "fetchAwayLocationProfiles : exit" );
		return fetchLocationProfilesByType( LocationType.AWAY );
	}

	public LocationProfile fetchLocationProfile( long id ) {
		Log.v( TAG, "fetchLocationProfile : enter" );
		
		open();
		
		LocationProfile profile = null;
		
		try {
			Cursor cursor = db.query( 
						TABLE_LOCATION_PROFILE, 
						new String[] { TABLE_LOCATION_PROFILE_ID, TABLE_LOCATION_PROFILE_TYPE, TABLE_LOCATION_PROFILE_NAME, TABLE_LOCATION_PROFILE_URL, TABLE_LOCATION_PROFILE_SELECTED }, 
						TABLE_LOCATION_PROFILE_ID + "=" + id, 
						null, null, null, null 
					  );
			
			if( cursor.getCount() == 1 && cursor.moveToFirst() ) {
				Log.v( TAG, "fetchLocationProfile : location profile found" );

				profile = convertCursorToLocationProfile( cursor );
			}
			
			cursor.close();
			
		} catch( SQLException e ) {
			Log.e( TAG, "fetchLocationProfile : error", e );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( context );
			builder.setTitle( "DataBase Error" );
			builder.setMessage( "An error occurred locating profile" );
			builder.setNeutralButton( R.string.close, new OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) { }
			
			});
		} finally {
			close();
		}
		
		Log.v( TAG, "fetchLocationProfile : exit" );
		return profile;
	}

	/**
	 * @param profile
	 * @return
	 */
	public long createHomeLocationProfile( LocationProfile profile ) {
		Log.v( TAG, "createHomeLocationProfile : enter" );

		profile.setType( LocationType.HOME );

		long id = createLocationProfile( profile );
		
		if( fetchHomeLocationProfiles().size() == 1 ) {
			Log.v( TAG, "createHomeLocationProfile : making default" );

			setSelectedHomeLocationProfile( id );
		}
		
		Log.v( TAG, "createHomeLocationProfile : exit" );
		return id;
	}

	/**
	 * @param profile
	 * @return
	 */
	public long createAwayLocationProfile( LocationProfile profile ) {
		Log.v( TAG, "createAwayLocationProfile : enter" );

		profile.setType( LocationType.AWAY );

		long id = createLocationProfile( profile );

		if( fetchAwayLocationProfiles().size() == 1 ) {
			Log.v( TAG, "createAwayLocationProfile : making default" );

			setSelectedAwayLocationProfile( id );
		}
				
		Log.v( TAG, "createAwayLocationProfile : enter" );
		return id;
	}

	/**
	 * @param profile
	 * @return
	 */
	public boolean updateLocationProfile( LocationProfile profile ) {
		Log.v( TAG, "updateLocationProfile : enter" );

		open();

		ContentValues args = new ContentValues();
		args.put( TABLE_LOCATION_PROFILE_TYPE, profile.getType().name() );
		args.put( TABLE_LOCATION_PROFILE_NAME, profile.getName() );
		args.put( TABLE_LOCATION_PROFILE_URL, profile.getUrl() );
		args.put( TABLE_LOCATION_PROFILE_SELECTED, profile.isSelected() ? 1 : 0 );

		int rows = db.update( TABLE_LOCATION_PROFILE, args, TABLE_LOCATION_PROFILE_ID + "=" + profile.getId(), null );
	
		close();

		Log.v( TAG, "updateLocationProfile : exit" );
		return rows > 0;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean deleteLocationProfile( long id ) {
		Log.v( TAG, "deleteLocationProfile : enter" );
		
		open();
		
		boolean result = db.delete( TABLE_LOCATION_PROFILE, TABLE_LOCATION_PROFILE_ID + "=" + id, null ) > 0;
		
		close();
		
		Log.v( TAG, "deleteLocationProfile : exit" );
		return result;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean setSelectedHomeLocationProfile( long id ) {
		Log.v( TAG, "setSelectedHomeLocationProfile : enter" );
		Log.v( TAG, "setSelectedHomeLocationProfile : exit" );
		return setSelectedLocationProfile( id, LocationType.HOME );
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean setSelectedAwayLocationProfile( long id ) {
		Log.v( TAG, "setSelectedAwayLocationProfile : enter" );
		Log.v( TAG, "setSelectedAwayLocationProfile : exit" );
		return setSelectedLocationProfile( id, LocationType.AWAY );
	}

	/**
	 * @return
	 */
	public LocationProfile fetchSelectedHomeLocationProfile() {
		Log.v( TAG, "fetchSelectedHomeLocationProfile : enter" );
		Log.v( TAG, "fetchSelectedHomeLocationProfile : exit" );
		return fetchSelectedLocationProfileByType( LocationType.HOME );
	}

	/**
	 * @return
	 */
	public LocationProfile fetchSelectedAwayLocationProfile() {
		Log.v( TAG, "fetchSelectedAwayLocationProfile : enter" );
		Log.v( TAG, "fetchSelectedAwayLocationProfile : exit" );
		return fetchSelectedLocationProfileByType( LocationType.AWAY );
	}

	/**
	 * @return
	 */
	public List<PlaybackProfile> fetchHomePlaybackProfiles() {
		Log.v( TAG, "fetchHomePlaybackProfiles : enter" );
		Log.v( TAG, "fetchHomePlaybackProfiles : exit" );
		return fetchPlaybackProfilesByType( LocationType.HOME );
	}

	/**
	 * @return
	 */
	public List<PlaybackProfile> fetchAwayPlaybackProfiles() {
		Log.v( TAG, "fetchAwayPlaybackProfiles : enter" );
		Log.v( TAG, "fetchAwayPlaybackProfiles : exit" );
		return fetchPlaybackProfilesByType( LocationType.AWAY );
	}

	public PlaybackProfile fetchPlaybackProfile( long id ) {
		Log.v( TAG, "fetchPlaybackProfile : enter" );
		
		open();
		
		PlaybackProfile profile = null;
		
		try {
			Cursor cursor = db.query( 
						TABLE_PLAYBACK_PROFILE, 
						new String[] { TABLE_PLAYBACK_PROFILE_ID, TABLE_PLAYBACK_PROFILE_TYPE, TABLE_PLAYBACK_PROFILE_NAME, TABLE_PLAYBACK_PROFILE_WIDTH, TABLE_PLAYBACK_PROFILE_HEIGHT, TABLE_PLAYBACK_PROFILE_BITRATE, TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, TABLE_PLAYBACK_PROFILE_SELECTED }, 
						TABLE_PLAYBACK_PROFILE_ID + "=" + id, 
						null, null, null, null 
					  );
			
			if( cursor.getCount() == 1 && cursor.moveToFirst() ) {
				Log.v( TAG, "fetchPlaybackProfile : playback profile found" );

				profile = convertCursorToPlaybackProfile( cursor );
			}
			
			cursor.close();
			
		} catch( SQLException e ) {
			Log.e( TAG, "fetchPlaybackProfile : error", e );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( context );
			builder.setTitle( "DataBase Error" );
			builder.setMessage( "An error occurred locating profile" );
			builder.setNeutralButton( R.string.close, new OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) { }
			
			});
		}
		
		close();
		
		Log.v( TAG, "fetchPlaybackProfile : exit" );
		return profile;
	}

	/**
	 * @param profile
	 * @return
	 */
	public boolean updatePlaybackProfile( PlaybackProfile profile ) {
		Log.v( TAG, "updatePlaybackProfile : enter" );

		open();

		ContentValues args = new ContentValues();
		args.put( TABLE_PLAYBACK_PROFILE_TYPE, profile.getType().name() );
		args.put( TABLE_PLAYBACK_PROFILE_NAME, profile.getName() );
		args.put( TABLE_PLAYBACK_PROFILE_WIDTH, profile.getWidth() );
		args.put( TABLE_PLAYBACK_PROFILE_HEIGHT, profile.getHeight() );
		args.put( TABLE_PLAYBACK_PROFILE_BITRATE, profile.getVideoBitrate() );
		args.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, profile.getAudioBitrate() );
		args.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, profile.getAudioSampleRate() );
		args.put( TABLE_PLAYBACK_PROFILE_SELECTED, profile.isSelected() ? 1 : 0 );

		int rows = db.update( TABLE_PLAYBACK_PROFILE, args, TABLE_PLAYBACK_PROFILE_ID + "=" + profile.getId(), null );
	
		close();

		Log.v( TAG, "updatePlaybackProfile : exit" );
		return rows > 0;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean setSelectedHomePlaybackProfile( long id ) {
		Log.v( TAG, "setSelectedHomePlaybackProfile : enter" );
		Log.v( TAG, "setSelectedHomePlaybackProfile : exit" );
		return setSelectedPlaybackProfile( id, LocationType.HOME );
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean setSelectedAwayPlaybackProfile( long id ) {
		Log.v( TAG, "setSelectedAwayPlaybackProfile : enter" );
		Log.v( TAG, "setSelectedAwayPlaybackProfile : exit" );
		return setSelectedPlaybackProfile( id, LocationType.AWAY );
	}

	/**
	 * @return
	 */
	public PlaybackProfile fetchSelectedHomePlaybackProfile() {
		Log.v( TAG, "fetchSelectedHomePlaybackProfile : enter" );
		Log.v( TAG, "fetchSelectedHomePlaybackProfile : exit" );
		return fetchSelectedPlaybackProfileByType( LocationType.HOME );
	}

	/**
	 * @return
	 */
	public PlaybackProfile fetchSelectedAwayPlaybackProfile() {
		Log.v( TAG, "fetchSelectedAwayPlaybackProfile : enter" );
		Log.v( TAG, "fetchSelectedAwayPlaybackProfile : exit" );
		return fetchSelectedPlaybackProfileByType( LocationType.AWAY );
	}

	// internal helpers

	/**
	 * @param cursor
	 * @return
	 */
	private LocationProfile convertCursorToLocationProfile( Cursor cursor ) {
		Log.v( TAG, "convertCursorToLocationProfile : enter" );
		
		LocationProfile profile = new LocationProfile();
		profile.setId( cursor.getInt( 0 ) );
		profile.setType( LocationType.valueOf( cursor.getString( 1 ) ) );
		profile.setName( cursor.getString( 2 ) );
		profile.setUrl( cursor.getString( 3 ) );
		profile.setSelected( cursor.getInt( 4 ) != 0 );
		
		Log.v( TAG, "convertCursorToLocationProfile : exit" );
		return profile;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	private PlaybackProfile convertCursorToPlaybackProfile( Cursor cursor ) {
		Log.v( TAG, "convertCursorToPlaybackProfile : enter" );
		
		PlaybackProfile profile = new PlaybackProfile();
		profile.setId( cursor.getInt( 0 ) );
		profile.setType( LocationType.valueOf( cursor.getString( 1 ) ) );
		profile.setName( cursor.getString( 2 ) );
		profile.setWidth( cursor.getInt( 3 ) );
		profile.setHeight( cursor.getInt( 4 ) );
		profile.setVideoBitrate( cursor.getInt( 5 ) );
		profile.setAudioBitrate( cursor.getInt( 6 ) );
		profile.setAudioSampleRate( cursor.getInt( 7 ) );
		profile.setSelected( cursor.getInt( 8 ) != 0 );
		
		Log.v( TAG, "convertCursorToPlaybackProfile : exit" );
		return profile;
	}

	/**
	 * @param type
	 * @return
	 */
	private List<LocationProfile> fetchLocationProfilesByType( LocationType type ) {
		Log.v( TAG, "fetchLocationProfilesByType : enter" );
		
		open();
		
		List<LocationProfile> profiles = new ArrayList<LocationProfile>();

		Cursor cursor = db.query( TABLE_LOCATION_PROFILE, new String[] { TABLE_LOCATION_PROFILE_ID, TABLE_LOCATION_PROFILE_TYPE, TABLE_LOCATION_PROFILE_NAME, TABLE_LOCATION_PROFILE_URL, TABLE_LOCATION_PROFILE_SELECTED }, "type=?",	new String[] { type.name() }, null, null, null );
		
		int count = cursor.getCount();
		if( count > 0 && cursor.moveToFirst() ) {
			Log.v( TAG, "fetchLocationProfilesByType : location profiles found" );
		
			for( int i = 0; i < count; i++ ) {
				Log.v( TAG, "fetchLocationProfilesByType : location profile cursor iteration" );
				
				profiles.add( convertCursorToLocationProfile( cursor ) );
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		close();
		
		Log.v( TAG, "fetchLocationProfilesByType : exit" );
		return profiles;
	}

	/**
	 * @param type
	 * @return
	 */
	private List<PlaybackProfile> fetchPlaybackProfilesByType( LocationType type ) {
		Log.v( TAG, "fetchPlaybackProfilesByType : enter" );
		
		open();
		
		List<PlaybackProfile> profiles = new ArrayList<PlaybackProfile>();

		Cursor cursor = db.query( TABLE_PLAYBACK_PROFILE, new String[] { TABLE_PLAYBACK_PROFILE_ID, TABLE_PLAYBACK_PROFILE_TYPE, TABLE_PLAYBACK_PROFILE_NAME, TABLE_PLAYBACK_PROFILE_WIDTH, TABLE_PLAYBACK_PROFILE_HEIGHT, TABLE_PLAYBACK_PROFILE_BITRATE, TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, TABLE_PLAYBACK_PROFILE_SELECTED }, "type=?",	new String[] { type.name() }, null, null, null );
		
		int count = cursor.getCount();
		if( count > 0 && cursor.moveToFirst() ) {
			Log.v( TAG, "fetchPlaybackProfilesByType : playback profiles found" );
		
			for( int i = 0; i < count; i++ ) {
				Log.v( TAG, "fetchPlaybackProfilesByType : playback profile cursor iteration" );
				
				profiles.add( convertCursorToPlaybackProfile( cursor ) );
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		close();
		
		Log.v( TAG, "fetchPlaybackProfilesByType : exit" );
		return profiles;
	}

	/**
	 * @param profile
	 * @return
	 */
	private long createLocationProfile( LocationProfile profile ) {
		Log.v( TAG, "createLocationProfile : enter" );

		Log.v( TAG, "createLocationProfile : profile=" + profile.toString() );
		
		open();
		
		ContentValues initialValues = new ContentValues();
		initialValues.put( TABLE_LOCATION_PROFILE_TYPE, profile.getType().name() );
		initialValues.put( TABLE_LOCATION_PROFILE_NAME, profile.getName() );
		initialValues.put( TABLE_LOCATION_PROFILE_URL, profile.getUrl() );
		initialValues.put( TABLE_LOCATION_PROFILE_SELECTED, 0 );

		long id = db.insert( TABLE_LOCATION_PROFILE, null, initialValues );
		
		close();
		
		Log.v( TAG, "createLocationProfile : enter" );
		return id;
	}

	private boolean setSelectedLocationProfile( long id, LocationType type ) {
		Log.v( TAG, "setSelectedLocationProfile : enter" );

		open();
		
		ContentValues args = new ContentValues();
		args.put( TABLE_LOCATION_PROFILE_SELECTED, 0 );

		db.update( TABLE_LOCATION_PROFILE, args, TABLE_LOCATION_PROFILE_TYPE + "=?", new String[] { type.name() } );

		args = new ContentValues();
		args.put( TABLE_LOCATION_PROFILE_SELECTED, 1 );

		int rows = db.update( TABLE_LOCATION_PROFILE, args, TABLE_LOCATION_PROFILE_ID + "=?", new String[] { "" + id } );
	
		close();

		Log.v( TAG, "setSelectedLocationProfile : exit" );
		return rows > 0;
	}

	private boolean setSelectedPlaybackProfile( long id, LocationType type ) {
		Log.v( TAG, "setSelectedPlaybackProfile : enter" );

		open();
		
		ContentValues args = new ContentValues();
		args.put( TABLE_PLAYBACK_PROFILE_SELECTED, 0 );

		db.update( TABLE_PLAYBACK_PROFILE, args, TABLE_PLAYBACK_PROFILE_TYPE + "=?", new String[] { type.name() } );

		args = new ContentValues();
		args.put( TABLE_PLAYBACK_PROFILE_SELECTED, 1 );

		int rows = db.update( TABLE_PLAYBACK_PROFILE, args, TABLE_PLAYBACK_PROFILE_ID + "=?", new String[] { "" + id } );
	
		close();

		Log.v( TAG, "setSelectedPlaybackProfile : exit" );
		return rows > 0;
	}

	private LocationProfile fetchSelectedLocationProfileByType( LocationType type ) {
		Log.v( TAG, "fetchSelectedLocationProfileByType : enter" );
		
		open();
		
		LocationProfile profile = null;

		try {
			Cursor cursor = db.query( 
						TABLE_LOCATION_PROFILE, 
						new String[] { TABLE_LOCATION_PROFILE_ID, TABLE_LOCATION_PROFILE_TYPE, TABLE_LOCATION_PROFILE_NAME, TABLE_LOCATION_PROFILE_URL, TABLE_LOCATION_PROFILE_SELECTED }, 
						TABLE_LOCATION_PROFILE_TYPE + "=? and " + TABLE_LOCATION_PROFILE_SELECTED + "=?", 
						new String[] { type.name(), "1" }, 
						null, null, null 
					  );
			
			if( cursor.getCount() == 1 && cursor.moveToFirst() ) {
				Log.v( TAG, "fetchSelectedLocationProfileByType : location profile found" );

				profile = convertCursorToLocationProfile( cursor );
			}
			
			cursor.close();
			
		} catch( SQLException e ) {
			Log.e( TAG, "fetchSelectedLocationProfileByType : error", e );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( context );
			builder.setTitle( "DataBase Error" );
			builder.setMessage( "An error occurred locating profile" );
			builder.setNeutralButton( R.string.close, new OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) { }
			
			});
		} finally {
			close();
		}
		
		Log.v( TAG, "fetchSelectedLocationProfileByType : exit" );
		return profile;
	}

	private PlaybackProfile fetchSelectedPlaybackProfileByType( LocationType type ) {
		Log.v( TAG, "fetchSelectedPlaybackProfileByType : enter" );
		
		open();
		
		PlaybackProfile profile = null;

		try {
			Cursor cursor = db.query( 
						TABLE_PLAYBACK_PROFILE, 
						new String[] { TABLE_PLAYBACK_PROFILE_ID, TABLE_PLAYBACK_PROFILE_TYPE, TABLE_PLAYBACK_PROFILE_NAME, TABLE_PLAYBACK_PROFILE_WIDTH, TABLE_PLAYBACK_PROFILE_HEIGHT, TABLE_PLAYBACK_PROFILE_BITRATE, TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, TABLE_PLAYBACK_PROFILE_SELECTED }, 
						TABLE_PLAYBACK_PROFILE_TYPE + "=? and " + TABLE_PLAYBACK_PROFILE_SELECTED + "=?", 
						new String[] { type.name(), "1" }, 
						null, null, null 
					  );
			
			if( cursor.getCount() == 1 && cursor.moveToFirst() ) {
				Log.v( TAG, "fetchSelectedPlaybackProfileByType : playback profile found" );

				profile = convertCursorToPlaybackProfile( cursor );
			}
			
			cursor.close();
			
		} catch( SQLException e ) {
			Log.e( TAG, "fetchSelectedPlaybackProfileByType : error", e );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( context );
			builder.setTitle( "DataBase Error" );
			builder.setMessage( "An error occurred locating profile" );
			builder.setNeutralButton( R.string.close, new OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) { }
			
			});
		} finally {
			close();
		}
		
		Log.v( TAG, "fetchSelectedPlaybackProfileByType : exit" );
		return profile;
	}

}
