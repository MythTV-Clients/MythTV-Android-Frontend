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
package org.mythtv.db.preferences;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.service.util.NetworkHelper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class PlaybackProfileDaoHelper {

	private static final String TAG = PlaybackProfileDaoHelper.class.getSimpleName();
	
	private static PlaybackProfileDaoHelper singleton = null;

	private Context mContext;
	
	/**
	 * Returns the one and only PlaybackProfileDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static PlaybackProfileDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( PlaybackProfileDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new PlaybackProfileDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private PlaybackProfileDaoHelper() { }
	
	/**
	 * Must be called once at the beginning of the application. Subsequent 
	 * calls to this will have no effect.
	 * 
	 * @param context
	 */
	public void init( Context context ) {
		
		// ignore any additional calls to init
		if( this.isInitialized() ) 
			return;
		
		this.mContext = context;
	}
	
	/**
	 * Returns true if PlaybackProfileDaoHelper has already been initialized
	 * 
	 * @return
	 */
	public boolean isInitialized(){
		return null != this.mContext;
	}
	
	/**
	 * @return
	 */
	public List<PlaybackProfile> findAll() {
		Log.d( TAG, "findAll : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		List<PlaybackProfile> profiles = new ArrayList<PlaybackProfile>();
		
		Cursor cursor = mContext.getContentResolver().query( PlaybackProfileConstants.CONTENT_URI, null, null, null, null );
		while( cursor.moveToNext() ) {
			PlaybackProfile profile = convertCursorToPlaybackProfile( cursor );
			profiles.add( profile );
		}
		cursor.close();
		
		Log.d( TAG, "findAll : exit" );
		return profiles;
	}

	/**
	 * @return
	 */
	public List<PlaybackProfile> findAllHomePlaybackProfiles() {
		Log.d( TAG, "findAllHomePlaybackProfiles : enter" );

		List<PlaybackProfile> profiles = findAllByType( LocationType.HOME );

		Log.d( TAG, "findAllHomePlaybackProfiles : exit" );
		return profiles;
	}
	
	/**
	 * @return
	 */
	public List<PlaybackProfile> findAllAwayPlaybackProfiles() {
		Log.d( TAG, "findAllAwayPlaybackProfiles : enter" );

		List<PlaybackProfile> profiles = findAllByType( LocationType.AWAY );

		Log.d( TAG, "findAllAwayPlaybackProfiles : exit" );
		return profiles;
	}

	/**
	 * @param id
	 * @return
	 */
	public PlaybackProfile findOne( Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		PlaybackProfile profile = null;
		
		if( id > 0 ) {
			Cursor cursor = mContext.getContentResolver().query( ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, id ), null, null, null, null );
			if( cursor.moveToFirst() ) {
				profile = convertCursorToPlaybackProfile( cursor );
			}
			cursor.close();
		}
		
		Log.d( TAG, "findOne : exit" );
		return profile;
	}
	
	/**
	 * @param profile
	 * @return
	 */
	public boolean save( PlaybackProfile profile ) {
		Log.d( TAG, "save : enter" );

		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		boolean ret = false;
		
		ContentValues values = convertProfileToContentValues( profile );

		if( profile.getId() == -1 ) {
			Uri inserted = mContext.getContentResolver().insert( PlaybackProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = true;

				Cursor profiles = mContext.getContentResolver().query( inserted, new String[] { PlaybackProfileConstants._ID }, PlaybackProfileConstants.FIELD_TYPE + " = ?", new String[] { profile.getType().name() }, null );
				if( profiles.getCount() == 1 ) {
					ContentValues selected = new ContentValues();
					selected.put( PlaybackProfileConstants.FIELD_SELECTED, 1 );
					
					mContext.getContentResolver().update( inserted, selected, null, null );
					Log.v( TAG, "save : selecting default location profile" );
				}
				profiles.close();
			}

			return ret;
		}
		
		Cursor cursor = mContext.getContentResolver().query( ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, profile.getId() ), null, null, null, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing playback profile" );

			int count = mContext.getContentResolver().update( ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, profile.getId() ), values, null, null );
			if( count > 0 ) {
				ret = true;
			}
		} else {
			Log.v( TAG, "save : saving new playback profile" );

			Uri inserted = mContext.getContentResolver().insert( PlaybackProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = true;

				Cursor profiles = mContext.getContentResolver().query( PlaybackProfileConstants.CONTENT_URI, new String[] { PlaybackProfileConstants._ID }, PlaybackProfileConstants.FIELD_TYPE + " = ?", new String[] { profile.getType().name() }, null );
				if( profiles.getCount() == 1 ) {
					ContentValues selected = new ContentValues();
					selected.put( PlaybackProfileConstants.FIELD_SELECTED, 1 );
					
					mContext.getContentResolver().update( ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, profile.getId() ), selected, null, null );
					Log.v( TAG, "save : selecting default playback profile" );
				}
				profiles.close();
			}
		}
		cursor.close();

		Log.d( TAG, "save : exit" );
		return ret;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean delete( Long id ) {
		Log.d( TAG, "delete : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		boolean ret = false;
		
		int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, id ), null, null );
		if( deleted > 0 ) {
			ret = true;
		}
		
		Log.d( TAG, "delete : exit" );
		return ret;
	}

	/**
	 * @param profileId
	 * @return
	 */
	public boolean setSelectedPlaybackProfile( Long profileId ) {
		Log.d( TAG, "setSelectedPlaybackProfile : enter" );
		
		PlaybackProfile profile = findOne( profileId );

		resetSelectedProfiles( profile.getType() );
		
		profile.setSelected( true );
		boolean saved = save( profile );
		
		Log.d( TAG, "setSelectedPlaybackProfile : exit" );
		return saved;
	}
	
	/**
	 * @return
	 */
	public PlaybackProfile findSelectedHomeProfile() {
		Log.d( TAG, "findSelectedHomeProfile : enter" );
		
		PlaybackProfile profile = findSelectedProfile( LocationType.HOME );
		
		Log.d( TAG, "findSelectedHomeProfile : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public PlaybackProfile findSelectedAwayProfile() {
		Log.d( TAG, "findSelectedAwayProfile : enter" );
		
		PlaybackProfile profile = findSelectedProfile( LocationType.AWAY );
		
		Log.d( TAG, "findSelectedAwayProfile : exit" );
		return profile;
	}

	// internal helpers
	
	private ContentValues convertProfileToContentValues( PlaybackProfile profile ) {
		Log.v( TAG, "convertProfileToContentValues : enter" );
		
		ContentValues values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, profile.getType().name() );
		values.put( PlaybackProfileConstants.FIELD_NAME, profile.getName() );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, profile.getWidth() );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, profile.getHeight() );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, profile.getVideoBitrate() );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, profile.getAudioBitrate() );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, profile.getAudioSampleRate() );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, profile.isSelected() ? 1 : 0 );
		
		Log.v( TAG, "convertProfileToContentValues : exit" );
		return values;
	}

	private PlaybackProfile convertCursorToPlaybackProfile( Cursor cursor ) {
		Log.v( TAG, "convertCursorToPlaybackProfile : enter" );
		
		int id = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants._ID ) );
		String type = cursor.getString( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_TYPE ) );
		String name = cursor.getString( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_NAME ) );
		int width = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_WIDTH ) );
		int height = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_HEIGHT ) );
		int bitrate = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_BITRATE ) );
		int audioBitrate = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_AUDIO_BITRATE ) );
		int sampleRate = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_SAMPLE_RATE ) );
		int selected = cursor.getInt( cursor.getColumnIndexOrThrow( PlaybackProfileConstants.FIELD_SELECTED ) );
		
		PlaybackProfile profile = new PlaybackProfile();
		profile.setId( id );
		profile.setType( LocationType.valueOf( type ) );
		profile.setName( name );
		profile.setWidth( width );
		profile.setHeight( height );
		profile.setVideoBitrate( bitrate );
		profile.setAudioBitrate( audioBitrate );
		profile.setAudioSampleRate( sampleRate );
		profile.setSelected( 1 == selected ? true : false );
		
		Log.v( TAG, "convertCursorToPlaybackProfile : exit" );
		return profile;
	}

	private List<PlaybackProfile> findAllByType( LocationType type ) {
		Log.d( TAG, "findAllByType : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		List<PlaybackProfile> profiles = new ArrayList<PlaybackProfile>();
		
		Cursor cursor = mContext.getContentResolver().query( PlaybackProfileConstants.CONTENT_URI, null, PlaybackProfileConstants.FIELD_TYPE + " = ?", new String[] { type.name() }, null );
		while( cursor.moveToNext() ) {
			PlaybackProfile profile = convertCursorToPlaybackProfile( cursor );
			profiles.add( profile );
		}
		cursor.close();
		
		Log.d( TAG, "findAllByType : exit" );
		return profiles;
	}

	private PlaybackProfile findSelectedProfile( LocationType type ) {
		Log.d( TAG, "findSelectedProfile : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		PlaybackProfile profile = null;
		
		Cursor cursor = mContext.getContentResolver().query( PlaybackProfileConstants.CONTENT_URI, null, PlaybackProfileConstants.FIELD_TYPE + " = ? AND " + PlaybackProfileConstants.FIELD_SELECTED + " = ?", new String[] { type.name(), "1" }, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToPlaybackProfile( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findSelectedProfile : exit" );
		return profile;
	}

	private boolean resetSelectedProfiles( LocationType type ) {
		Log.d( TAG, "resetSelectedProfiles : enter" );

		if( !this.isInitialized() ) 
			throw new RuntimeException( "PlaybackProfileDaoHelper is not initialized" );
		
		boolean ret = false;
		
		ContentValues values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		int updated = mContext.getContentResolver().update( PlaybackProfileConstants.CONTENT_URI, values, PlaybackProfileConstants.FIELD_TYPE + " = ?", new String[] { type.name() } );
		if( updated > 0 ) {
			Log.v( TAG, "resetSelectedProfiles : reset all selected playback profiles by type" );
		}

		Log.d( TAG, "resetSelectedProfiles : exit" );
		return ret;
	}

}
