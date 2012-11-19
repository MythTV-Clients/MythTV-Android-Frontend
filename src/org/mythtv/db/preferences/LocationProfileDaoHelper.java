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

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;

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
public class LocationProfileDaoHelper {

	private static final String TAG = LocationProfileDaoHelper.class.getSimpleName();
	
	private Context mContext;
	
	/**
	 * @param context
	 */
	public LocationProfileDaoHelper( Context context ) {
		this.mContext = context;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAll() {
		Log.d( TAG, "findAll : enter" );
		
		List<LocationProfile> profiles = new ArrayList<LocationProfile>();
		
		Cursor cursor = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, null, null, null );
		while( cursor.moveToNext() ) {
			LocationProfile profile = convertCursorToLocationProfile( cursor );
			profiles.add( profile );
		}
		cursor.close();
		
		Log.d( TAG, "findAll : exit" );
		return profiles;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAllHomeLocationProfiles() {
		Log.d( TAG, "findAllHomeLocationProfiles : enter" );

		List<LocationProfile> profiles = findAllByType( LocationType.HOME );

		Log.d( TAG, "findAllHomeLocationProfiles : exit" );
		return profiles;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAllAwayLocationProfiles() {
		Log.d( TAG, "findAllAwayLocationProfiles : enter" );

		List<LocationProfile> profiles = findAllByType( LocationType.AWAY );

		Log.d( TAG, "findAllAwayLocationProfiles : exit" );
		return profiles;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public LocationProfile findOne( Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		LocationProfile profile = null;
		
		if( id > 0 ) {
			Cursor cursor = mContext.getContentResolver().query( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, id ), null, null, null, null );
			if( cursor.moveToFirst() ) {
				profile = convertCursorToLocationProfile( cursor );
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
	public boolean save( LocationProfile profile ) {
		Log.d( TAG, "save : enter" );

		boolean ret = false;
		
		ContentValues values = convertProfileToContentValues( profile );

		if( profile.getId() == -1 ) {
			Uri inserted = mContext.getContentResolver().insert( LocationProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = true;

				Cursor profiles = mContext.getContentResolver().query( inserted, new String[] { LocationProfileConstants._ID }, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { profile.getType().name() }, null );
				if( profiles.getCount() == 1 ) {
					ContentValues selected = new ContentValues();
					selected.put( LocationProfileConstants.FIELD_SELECTED, 1 );
					
					mContext.getContentResolver().update( inserted, selected, null, null );
					Log.v( TAG, "save : selecting default location profile" );
				}
				profiles.close();
			}

			return ret;
		}
		
		Cursor cursor = mContext.getContentResolver().query( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, profile.getId() ), null, null, null, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing location profile" );

			int count = mContext.getContentResolver().update( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, profile.getId() ), values, null, null );
			if( count > 0 ) {
				ret = true;
			}
		} else {
			Log.v( TAG, "save : saving new location profile" );

			Uri inserted = mContext.getContentResolver().insert( LocationProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = true;

				Cursor profiles = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, new String[] { LocationProfileConstants._ID }, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { profile.getType().name() }, null );
				if( profiles.getCount() == 1 ) {
					ContentValues selected = new ContentValues();
					selected.put( LocationProfileConstants.FIELD_SELECTED, 1 );
					
					mContext.getContentResolver().update( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, profile.getId() ), selected, null, null );
					Log.v( TAG, "save : selecting default location profile" );
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
		
		boolean ret = false;
		
		int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, id ), null, null );
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
	public boolean setSelectedLocationProfile( Long profileId ) {
		Log.d( TAG, "setSelectedLocationProfile : enter" );
		
		LocationProfile profile = findOne( profileId );

		resetSelectedProfiles( profile.getType() );
		
		profile.setSelected( true );
		boolean saved = save( profile );
		
		Log.d( TAG, "setSelectedLocationProfile : exit" );
		return saved;
	}
	
	/**
	 * @return
	 */
	public LocationProfile findSelectedHomeProfile() {
		Log.d( TAG, "findSelectedHomeProfile : enter" );
		
		LocationProfile profile = findSelectedProfile( LocationType.HOME );
		
		Log.d( TAG, "findSelectedHomeProfile : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findSelectedAwayProfile() {
		Log.d( TAG, "findSelectedAwayProfile : enter" );
		
		LocationProfile profile = findSelectedProfile( LocationType.AWAY );
		
		Log.d( TAG, "findSelectedAwayProfile : exit" );
		return profile;
	}

	/**
	 * @param profileId
	 * @return
	 */
	public boolean setConnectedLocationProfile( Long profileId ) {
		Log.d( TAG, "setConnectedLocationProfile : enter" );
		
		resetConnectedProfiles();
		
		LocationProfile profile = findOne( profileId );
		profile.setConnected( true );
		boolean saved = save( profile );
		
		Log.d( TAG, "setConnectedLocationProfile : exit" );
		return saved;
	}

	/**
	 * @return
	 */
	public LocationProfile findConnectedProfile() {
		Log.d( TAG, "findConnectedProfile : enter" );
		
		LocationProfile profile = null;
		
		Cursor cursor = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_CONNECTED + " = ?", new String[] { "1" }, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findConnectedProfile : exit" );
		return profile;
	}

	// internal helpers
	
	private ContentValues convertProfileToContentValues( LocationProfile profile ) {
		Log.v( TAG, "convertProfileToContentValues : enter" );
		
		ContentValues values = new ContentValues();
		values.put( LocationProfileConstants.FIELD_TYPE, profile.getType().name() );
		values.put( LocationProfileConstants.FIELD_NAME, profile.getName() );
		values.put( LocationProfileConstants.FIELD_URL, profile.getUrl() );
		values.put( LocationProfileConstants.FIELD_SELECTED, profile.isSelected() ? 1 : 0 );
		values.put( LocationProfileConstants.FIELD_CONNECTED, profile.isConnected() ? 1 : 0 );
		
		Log.v( TAG, "convertProfileToContentValues : exit" );
		return values;
	}

	private LocationProfile convertCursorToLocationProfile( Cursor cursor ) {
		Log.v( TAG, "convertCursorToLocationProfile : enter" );
		
		int id = cursor.getInt( cursor.getColumnIndexOrThrow( LocationProfileConstants._ID ) );
		String type = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_TYPE ) );
		String name = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_NAME ) );
		String url = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_URL ) );
		int selected = cursor.getInt( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_SELECTED ) );
		int connected = cursor.getInt( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_CONNECTED ) );
		
		LocationProfile profile = new LocationProfile();
		profile.setId( id );
		profile.setType( LocationType.valueOf( type ) );
		profile.setName( name );
		profile.setUrl( url );
		profile.setSelected( 1 == selected ? true : false );
		profile.setConnected( 1 == connected ? true : false );
		
		Log.v( TAG, "convertCursorToLocationProfile : exit" );
		return profile;
	}

	private List<LocationProfile> findAllByType( LocationType type ) {
		Log.d( TAG, "findAllByType : enter" );
		
		List<LocationProfile> profiles = new ArrayList<LocationProfile>();
		
		Cursor cursor = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { type.name() }, null );
		while( cursor.moveToNext() ) {
			LocationProfile profile = convertCursorToLocationProfile( cursor );
			profiles.add( profile );
		}
		cursor.close();
		
		Log.d( TAG, "findAllByType : exit" );
		return profiles;
	}
	
	private LocationProfile findSelectedProfile( LocationType type ) {
		Log.d( TAG, "findSelectedProfile : enter" );
		
		LocationProfile profile = null;
		
		Cursor cursor = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_TYPE + " = ? AND " + LocationProfileConstants.FIELD_SELECTED + " = ?", new String[] { type.name(), "1" }, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findSelectedProfile : exit" );
		return profile;
	}

	private boolean resetSelectedProfiles( LocationType type ) {
		Log.d( TAG, "resetSelectedProfiles : enter" );

		boolean ret = false;
		
		ContentValues values = new ContentValues();
		values.put( LocationProfileConstants.FIELD_SELECTED, 0 );
		int updated = mContext.getContentResolver().update( LocationProfileConstants.CONTENT_URI, values, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { type.name() } );
		if( updated > 0 ) {
			Log.v( TAG, "resetSelectedProfiles : reset all selected location profiles by type" );
		}

		Log.d( TAG, "resetSelectedProfiles : exit" );
		return ret;
	}

	private boolean resetConnectedProfiles() {
		Log.d( TAG, "resetConnectedProfiles : enter" );

		boolean ret = false;
		
		ContentValues values = new ContentValues();
		values.put( LocationProfileConstants.FIELD_CONNECTED, 0 );
		int updated = mContext.getContentResolver().update( LocationProfileConstants.CONTENT_URI, values, null, null );
		if( updated > 0 ) {
			Log.v( TAG, "resetConnectedProfiles : reset all connected location profiles" );
		}

		Log.d( TAG, "resetConnectedProfiles : exit" );
		return ret;
	}

}
