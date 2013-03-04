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
	
	private static LocationProfileDaoHelper singleton = null;
	
	private Context mContext;
	
	/**
	 * Returns the one and only LocationProfileDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static LocationProfileDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( LocationProfileDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new LocationProfileDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private LocationProfileDaoHelper() { }
	
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
	 * Returns true if LocationProfileDaoHelper has already been initialized
	 * 
	 * @return
	 */
	public boolean isInitialized(){
		return null != this.mContext;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAll() {
		Log.d( TAG, "findAll : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		List<LocationProfile> profiles = findAllByType( LocationType.HOME );

		Log.d( TAG, "findAllHomeLocationProfiles : exit" );
		return profiles;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAllAwayLocationProfiles() {
		Log.d( TAG, "findAllAwayLocationProfiles : enter" );

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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
	public long save( LocationProfile profile ) {
		Log.d( TAG, "save : enter" );

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		long ret = 0;
		
		ContentValues values = convertProfileToContentValues( profile );

		if( profile.getId() == -1 ) {
			Uri inserted = mContext.getContentResolver().insert( LocationProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = ContentUris.parseId( inserted );

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
				ret = profile.getId();
			}
		} else {
			Log.v( TAG, "save : saving new location profile" );

			Uri inserted = mContext.getContentResolver().insert( LocationProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = ContentUris.parseId( inserted );

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
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		boolean saved = false;
		
		LocationProfile profile = findOne( profileId );

		resetSelectedProfiles( profile.getType() );
		
		profile.setSelected( true );
		long id = save( profile );
		if( id != 0 ) {
			saved = true;
		}
		
		Log.d( TAG, "setSelectedLocationProfile : exit" );
		return saved;
	}
	
	/**
	 * @param type
	 * @param url
	 * @return
	 */
	public LocationProfile findByLocationTypeAndUrl( LocationType type, String url ) {
		Log.d( TAG, "findByLocationTypeAndUrl : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		LocationProfile profile = null;
		
		String selection = LocationProfileConstants.FIELD_TYPE + " = ? AND " + LocationProfileConstants.FIELD_URL + " = ?";
		String[] selectionArgs = new String[] { type.name(), url };
		
		Cursor cursor = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, selection, selectionArgs, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findByLocationTypeAndUrl : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findHomeProfileByUrl( String url ) {
		Log.d( TAG, "findHomeProfileByUrl : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		LocationProfile profile = findByLocationTypeAndUrl( LocationType.HOME, url );
		
		Log.d( TAG, "findHomeProfileByUrl : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findAwayProfileByUrl( String url ) {
		Log.d( TAG, "findAwayProfileByUrl : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		LocationProfile profile = findByLocationTypeAndUrl( LocationType.AWAY, url );
		
		Log.d( TAG, "findAwayProfileByUrl : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findSelectedHomeProfile() {
		Log.d( TAG, "findSelectedHomeProfile : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		LocationProfile profile = findSelectedProfile( LocationType.HOME );
		
		Log.d( TAG, "findSelectedHomeProfile : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findSelectedAwayProfile() {
		Log.d( TAG, "findSelectedAwayProfile : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		boolean saved = false;

		resetConnectedProfiles();
		
		LocationProfile profile = findOne( profileId );
		profile.setConnected( true );
		long id = save( profile );
		if( id != 0 ) {
			saved = true;
		}
		
		Log.d( TAG, "setConnectedLocationProfile : exit" );
		return saved;
	}

	/**
	 * @return
	 */
	public LocationProfile findConnectedProfile() {
		Log.d( TAG, "findConnectedProfile : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
		LocationProfile profile = null;
		
		Cursor cursor = mContext.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_CONNECTED + " = ?", new String[] { "1" }, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findConnectedProfile : exit" );
		return profile;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
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
		values.put( LocationProfileConstants.FIELD_VERSION, profile.getVersion() );
		values.put( LocationProfileConstants.FIELD_PROTOCOL_VERSION, profile.getProtocolVersion() );
		values.put( LocationProfileConstants.FIELD_WOL_ADDRESS, profile.getWolAddress() );
		values.put( LocationProfileConstants.FIELD_HOSTNAME, profile.getHostname() );
		
		Log.v( TAG, "convertProfileToContentValues : exit" );
		return values;
	}

	private LocationProfile convertCursorToLocationProfile( Cursor cursor ) {
		Log.v( TAG, "convertCursorToLocationProfile : enter" );
		
		int id = -1, selected = -1, connected = -1;
		String type = "", name = "", url = "", version = "", protocolVersion = "", wolAddress = "", hostname = "";
		
		if( cursor.getColumnIndex( LocationProfileConstants._ID ) > -1 ) {
			id = cursor.getInt( cursor.getColumnIndexOrThrow( LocationProfileConstants._ID ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_TYPE ) > -1 ) {
			type = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_TYPE ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_NAME ) > -1 ) {
			name = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_NAME ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_URL ) > -1 ) {
			url = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_URL ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_SELECTED ) > -1 ) {
			selected = cursor.getInt( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_SELECTED ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_CONNECTED ) > -1 ) {
			connected = cursor.getInt( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_CONNECTED ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_VERSION ) > -1 ) {
			version = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_VERSION ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_PROTOCOL_VERSION ) > -1 ) {
			protocolVersion = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_PROTOCOL_VERSION ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_WOL_ADDRESS ) > -1 ) {
			wolAddress = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_WOL_ADDRESS ) );
		}
		
		if( cursor.getColumnIndex( LocationProfileConstants.FIELD_HOSTNAME ) > -1 ) {
			hostname = cursor.getString( cursor.getColumnIndexOrThrow( LocationProfileConstants.FIELD_HOSTNAME ) );
		}

		LocationProfile profile = new LocationProfile();
		profile.setId( id );
		profile.setType( LocationType.valueOf( type ) );
		profile.setName( name );
		profile.setUrl( url );
		profile.setSelected( 1 == selected ? true : false );
		profile.setConnected( 1 == connected ? true : false );
		profile.setVersion( version );
		profile.setProtocolVersion( protocolVersion );
		profile.setWolAddress( wolAddress );
		profile.setHostname( hostname );
		
		Log.v( TAG, "convertCursorToLocationProfile : exit" );
		return profile;
	}

	private List<LocationProfile> findAllByType( LocationType type ) {
		Log.d( TAG, "findAllByType : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LocationProfileDaoHelper is not initialized" );
		
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
