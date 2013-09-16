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
	 * @return
	 */
	public List<LocationProfile> findAll( Context context ) {
//		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		List<LocationProfile> profiles = new ArrayList<LocationProfile>();
		
		Cursor cursor = context.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, null, null, null );
		while( cursor.moveToNext() ) {
			LocationProfile profile = convertCursorToLocationProfile( cursor );
			profiles.add( profile );
		}
		cursor.close();
		
//		Log.d( TAG, "findAll : exit" );
		return profiles;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAllHomeLocationProfiles( Context context ) {
//		Log.d( TAG, "findAllHomeLocationProfiles : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		List<LocationProfile> profiles = findAllByType( context, LocationType.HOME );

//		Log.d( TAG, "findAllHomeLocationProfiles : exit" );
		return profiles;
	}
	
	/**
	 * @return
	 */
	public List<LocationProfile> findAllAwayLocationProfiles( Context context ) {
//		Log.d( TAG, "findAllAwayLocationProfiles : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		List<LocationProfile> profiles = findAllByType( context, LocationType.AWAY );

//		Log.d( TAG, "findAllAwayLocationProfiles : exit" );
		return profiles;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public LocationProfile findOne( Context context, Long id ) {
//		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = null;
		
		if( id > 0 ) {
			Cursor cursor = context.getContentResolver().query( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, id ), null, null, null, null );
			if( cursor.moveToFirst() ) {
				profile = convertCursorToLocationProfile( cursor );
			}
			cursor.close();
		}
		
//		Log.d( TAG, "findOne : exit" );
		return profile;
	}
	
	/**
	 * @param profile
	 * @return
	 */
	public long save( Context context, LocationProfile profile ) {
//		Log.d( TAG, "save : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		long ret = 0;
		
		ContentValues values = convertProfileToContentValues( profile );

		if( profile.getId() == -1 ) {
			Uri inserted = context.getContentResolver().insert( LocationProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = ContentUris.parseId( inserted );

				Cursor profiles = context.getContentResolver().query( inserted, new String[] { LocationProfileConstants._ID }, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { profile.getType().name() }, null );
				if( profiles.getCount() == 1 ) {
					ContentValues selected = new ContentValues();
					selected.put( LocationProfileConstants.FIELD_SELECTED, 1 );
					
					context.getContentResolver().update( inserted, selected, null, null );
//					Log.v( TAG, "save : selecting default location profile" );
				}
				profiles.close();
			}

			return ret;
		}
		
		Cursor cursor = context.getContentResolver().query( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, profile.getId() ), null, null, null, null );
		if( cursor.moveToFirst() ) {
//			Log.v( TAG, "save : updating existing location profile" );

			int count = context.getContentResolver().update( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, profile.getId() ), values, null, null );
			if( count > 0 ) {
				ret = profile.getId();
			}
		} else {
//			Log.v( TAG, "save : saving new location profile" );

			Uri inserted = context.getContentResolver().insert( LocationProfileConstants.CONTENT_URI, values );
			if( null != inserted ) {
				ret = ContentUris.parseId( inserted );

				Cursor profiles = context.getContentResolver().query( LocationProfileConstants.CONTENT_URI, new String[] { LocationProfileConstants._ID }, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { profile.getType().name() }, null );
				if( profiles.getCount() == 1 ) {
					ContentValues selected = new ContentValues();
					selected.put( LocationProfileConstants.FIELD_SELECTED, 1 );
					
					context.getContentResolver().update( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, profile.getId() ), selected, null, null );
//					Log.v( TAG, "save : selecting default location profile" );
				}
				profiles.close();
			}
		}
		cursor.close();

//		Log.d( TAG, "save : exit" );
		return ret;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean delete( Context context, Long id ) {
//		Log.d( TAG, "delete : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		boolean ret = false;
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, id ), null, null );
		if( deleted > 0 ) {
			ret = true;
		}
		
//		Log.d( TAG, "delete : exit" );
		return ret;
	}

	/**
	 * @param profileId
	 * @return
	 */
	public boolean setSelectedLocationProfile( Context context, Long profileId ) {
//		Log.d( TAG, "setSelectedLocationProfile : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		boolean saved = false;
		
		LocationProfile profile = findOne( context, profileId );

		resetSelectedProfiles( context, profile.getType() );
		
		profile.setSelected( true );
		long id = save( context, profile );
		if( id != 0 ) {
			saved = true;
		}
		
//		Log.d( TAG, "setSelectedLocationProfile : exit" );
		return saved;
	}
	
	/**
	 * @param type
	 * @param url
	 * @return
	 */
	public LocationProfile findByLocationTypeAndUrl( Context context, LocationType type, String url ) {
//		Log.d( TAG, "findByLocationTypeAndUrl : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = null;
		
		String selection = LocationProfileConstants.FIELD_TYPE + " = ? AND " + LocationProfileConstants.FIELD_URL + " = ?";
		String[] selectionArgs = new String[] { type.name(), url };
		
		Cursor cursor = context.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, selection, selectionArgs, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
//		Log.d( TAG, "findByLocationTypeAndUrl : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findHomeProfileByUrl( Context context, String url ) {
//		Log.d( TAG, "findHomeProfileByUrl : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = findByLocationTypeAndUrl( context, LocationType.HOME, url );
		
//		Log.d( TAG, "findHomeProfileByUrl : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findAwayProfileByUrl( Context context, String url ) {
//		Log.d( TAG, "findAwayProfileByUrl : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = findByLocationTypeAndUrl( context, LocationType.AWAY, url );
		
//		Log.d( TAG, "findAwayProfileByUrl : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findSelectedHomeProfile( Context context ) {
//		Log.d( TAG, "findSelectedHomeProfile : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = findSelectedProfile( context, LocationType.HOME );
		
//		Log.d( TAG, "findSelectedHomeProfile : exit" );
		return profile;
	}

	/**
	 * @return
	 */
	public LocationProfile findSelectedAwayProfile( Context context ) {
//		Log.d( TAG, "findSelectedAwayProfile : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = findSelectedProfile( context, LocationType.AWAY );
		
//		Log.d( TAG, "findSelectedAwayProfile : exit" );
		return profile;
	}

	/**
	 * @param profileId
	 * @return
	 */
	public boolean setConnectedLocationProfile( Context context, Long profileId ) {
//		Log.d( TAG, "setConnectedLocationProfile : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		boolean saved = false;

		resetConnectedProfiles( context );
		
		LocationProfile profile = findOne( context, profileId );
		profile.setConnected( true );
		long id = save( context, profile );
		if( id != 0 ) {
			saved = true;
		}
		
//		Log.d( TAG, "setConnectedLocationProfile : exit" );
		return saved;
	}

	/**
	 * @return
	 */
	public LocationProfile findConnectedProfile( Context context ) {
//		Log.d( TAG, "findConnectedProfile : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = null;
		
		Cursor cursor = context.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_CONNECTED + " = ?", new String[] { "1" }, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
//		Log.d( TAG, "findConnectedProfile : exit" );
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
//		Log.v( TAG, "convertProfileToContentValues : enter" );
		
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
		
//		Log.v( TAG, "convertProfileToContentValues : exit" );
		return values;
	}

	private LocationProfile convertCursorToLocationProfile( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToLocationProfile : enter" );
		
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
		
//		Log.v( TAG, "convertCursorToLocationProfile : exit" );
		return profile;
	}

	private List<LocationProfile> findAllByType( Context context, LocationType type ) {
//		Log.d( TAG, "findAllByType : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		List<LocationProfile> profiles = new ArrayList<LocationProfile>();
		
		Cursor cursor = context.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { type.name() }, null );
		while( cursor.moveToNext() ) {
			LocationProfile profile = convertCursorToLocationProfile( cursor );
			profiles.add( profile );
		}
		cursor.close();
		
//		Log.d( TAG, "findAllByType : exit" );
		return profiles;
	}
	
	private LocationProfile findSelectedProfile( Context context, LocationType type ) {
//		Log.d( TAG, "findSelectedProfile : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		LocationProfile profile = null;
		
		Cursor cursor = context.getContentResolver().query( LocationProfileConstants.CONTENT_URI, null, LocationProfileConstants.FIELD_TYPE + " = ? AND " + LocationProfileConstants.FIELD_SELECTED + " = ?", new String[] { type.name(), "1" }, null );
		if( cursor.moveToNext() ) {
			profile = convertCursorToLocationProfile( cursor );
		}
		cursor.close();
		
//		Log.d( TAG, "findSelectedProfile : exit" );
		return profile;
	}

	private boolean resetSelectedProfiles( Context context, LocationType type ) {
//		Log.d( TAG, "resetSelectedProfiles : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		boolean ret = false;
		
		ContentValues values = new ContentValues();
		values.put( LocationProfileConstants.FIELD_SELECTED, 0 );
//		int updated = context.getContentResolver().update( LocationProfileConstants.CONTENT_URI, values, LocationProfileConstants.FIELD_TYPE + " = ?", new String[] { type.name() } );
//		if( updated > 0 ) {
//			Log.v( TAG, "resetSelectedProfiles : reset all selected location profiles by type" );
//		}

//		Log.d( TAG, "resetSelectedProfiles : exit" );
		return ret;
	}

	public boolean resetConnectedProfiles( Context context ) {
//		Log.d( TAG, "resetConnectedProfiles : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		boolean ret = false;
		
		ContentValues values = new ContentValues();
		values.put( LocationProfileConstants.FIELD_CONNECTED, 0 );
		int updated = context.getContentResolver().update( LocationProfileConstants.CONTENT_URI, values, null, null );
		if( updated > 0 ) {
			Log.v( TAG, "resetConnectedProfiles : reset all connected location profiles" );
		}

//		Log.d( TAG, "resetConnectedProfiles : exit" );
		return ret;
	}

}
