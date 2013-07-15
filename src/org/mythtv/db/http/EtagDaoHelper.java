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
package org.mythtv.db.http;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.util.DateUtils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
//import android.util.Log;


/**
 * @author Daniel Frey
 *
 */
public class EtagDaoHelper extends AbstractDaoHelper {

//	private static final String TAG = EtagDaoHelper.class.getSimpleName();
	
	private static EtagDaoHelper singleton = null;

	/**
	 * Returns the one and only EtagDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static EtagDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( EtagDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new EtagDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	private EtagDaoHelper() {
		super();
	}

	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<EtagInfoDelegate> findAll( final Context context, final LocationProfile locationProfile, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
//		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		List<EtagInfoDelegate> etagInfos = new ArrayList<EtagInfoDelegate>();
		
		selection = appendLocationHostname( context, locationProfile, selection, EtagConstants.TABLE_NAME );

		Cursor cursor = context.getContentResolver().query( EtagConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			EtagInfoDelegate etagInfo = convertCursorToEtagInfoDelegate( cursor );
			etagInfos.add( etagInfo );
		}
		cursor.close();

//		Log.d( TAG, "findAll : exit" );
		return etagInfos;
	}
	
	/**
	 * @return
	 */
	public List<EtagInfoDelegate> finalAll( final Context context, final LocationProfile locationProfile ) {
//		Log.d( TAG, "findAll : enter" );
		
		List<EtagInfoDelegate> etagInfos = findAll( context, locationProfile, null, null, null, null );
		
//		Log.d( TAG, "findAll : exit" );
		return etagInfos;
	}
	
	/**
	 * @param id
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public EtagInfoDelegate findOne( final Context context, final LocationProfile locationProfile, Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
//		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		EtagInfoDelegate etagInfo = EtagInfoDelegate.createEmptyETag();
		
		Uri uri = EtagConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			uri = ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, EtagConstants.TABLE_NAME );
//		Log.i( TAG, "findOne : selection=" + selection );
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			etagInfo = convertCursorToEtagInfoDelegate( cursor );
		}
		cursor.close();
		
//		Log.d( TAG, "findOne : exit" );
		return etagInfo;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public EtagInfoDelegate findOne( final Context context, final LocationProfile locationProfile, final Long id ) {
//		Log.d( TAG, "findOne : enter" );
		
		EtagInfoDelegate etagInfo = findOne( context, locationProfile, id, null, null, null, null );
		
//		Log.d( TAG, "findOne : exit" );
		return etagInfo;
	}

	/**
	 * @param endpoint
	 * @param dataId
	 * @return
	 */
	public EtagInfoDelegate findByEndpointAndDataId( final Context context, final LocationProfile locationProfile, final String endpoint, final String dataId ) {
//		Log.d( TAG, "findByEndpointAndDataId : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		String selection = EtagConstants.FIELD_ENDPOINT + " = ?";
		if( null != dataId && !"".equals( dataId ) ) {
			selection += " AND " + EtagConstants.FIELD_DATA_ID + " = ?";
		}
		
		String[] selectionArgs = new String[] { endpoint };
		if( null != dataId && !"".equals( dataId ) ) {
			selectionArgs = new String[] { endpoint, dataId };
		}
		
		EtagInfoDelegate etagInfo = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
		
//		Log.d( TAG, "findByEndpointAndDataId : exit" );
		return etagInfo;
	}

	/**
	 * @param endpoint
	 * @param date
	 * @return
	 */
	public EtagInfoDelegate findByEndpointAndDate( final Context context, final LocationProfile locationProfile, final String endpoint, final DateTime date ) {
//		Log.d( TAG, "findByEndpointAndDate : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		String selection = EtagConstants.FIELD_ENDPOINT + " = ?";
		if( null != date ) {
			selection += " AND " + EtagConstants.FIELD_DATE + " = ?";
		}
		
		String[] selectionArgs = new String[] { endpoint };
		if( null != date ) {
			selectionArgs = new String[] { endpoint, String.valueOf( date.getMillis() ) };
		}
		
		EtagInfoDelegate etagInfo = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
		
//		Log.d( TAG, "findByEndpointAndDate : exit" );
		return etagInfo;
	}

	/**
	 * @param context
	 * @param locationProfile
	 * @param endpoint
	 * @param dataId
	 * @return
	 */
	public DateTime findDateByEndpointAndDataId( final Context context, final LocationProfile locationProfile, final String endpoint, final String dataId ) {
//		Log.d( TAG, "findDateByEndpointAndDataId : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		String selection = EtagConstants.FIELD_ENDPOINT + " = ?";
		if( null != dataId && !"".equals( dataId ) ) {
			selection += " AND " + EtagConstants.FIELD_DATA_ID + " = ?";
		}
		
		String[] selectionArgs = new String[] { endpoint };
		if( null != dataId && !"".equals( dataId ) ) {
			selectionArgs = new String[] { endpoint, dataId };
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, EtagConstants.TABLE_NAME );

		DateTime etag = null;
		Cursor cursor = context.getContentResolver().query( EtagConstants.CONTENT_URI, new String[] { EtagConstants.FIELD_DATE }, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			etag = new DateTime( cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants.FIELD_DATE ) ) );
		}
		cursor.close();
		
//		Log.d( TAG, "findDateByEndpointAndDataId : exit" );
		return etag;
	}

	/**
	 * @param etagInfo
	 * @return
	 */
	public int save( final Context context, final LocationProfile locationProfile, final EtagInfoDelegate etagInfo ) {
//		Log.d( TAG, "save : enter" );

		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		ContentValues values = convertEtagInfoDelegateToContentValues( etagInfo );

		String selection = EtagConstants.FIELD_ENDPOINT + " = ?";
		if( etagInfo.getDataId() > 0 ) {
			selection += " AND " + EtagConstants.FIELD_DATA_ID + " = ?";
		}
		
		String[] selectionArgs = new String[] { etagInfo.getEndpoint() };
		if( etagInfo.getDataId() > 0 ) {
			selectionArgs = new String[] { etagInfo.getEndpoint(), String.valueOf( etagInfo.getDataId() ) };
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, EtagConstants.TABLE_NAME );

		int updated = -1;
		Cursor cursor = context.getContentResolver().query( EtagConstants.CONTENT_URI, new String[] { EtagConstants._ID }, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
//			Log.v( TAG, "save : updating existing etag info" );
			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants._ID ) );

			updated = context.getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
		} else {
			Uri inserted = context.getContentResolver().insert( EtagConstants.CONTENT_URI, values );
			if( null != inserted ) {
				updated = 1;
			}
		}
		cursor.close();
//		Log.v( TAG, "save : updated=" + updated );

//		Log.d( TAG, "save : exit" );
		return updated;
	}

	/**
	 * @return
	 */
	public int deleteAll( Context context ) {
//		Log.d( TAG, "deleteAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( EtagConstants.CONTENT_URI, null, null );
//		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
//		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param id
	 * @return
	 */
	public int delete( final Context context, final LocationProfile locationProfile, final EtagInfoDelegate etag ) {
//		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "EtagDaoHelper is not initialized" );
		
		String selection = EtagConstants.FIELD_ENDPOINT + " = ?";
		if( etag.getDataId() > 0 ) {
			selection += " AND " + EtagConstants.FIELD_DATA_ID + " = ?";
		}
		
		String[] selectionArgs = new String[] { etag.getEndpoint() };
		if( etag.getDataId() > 0 ) {
			selectionArgs = new String[] { etag.getEndpoint(), String.valueOf( etag.getDataId() ) };
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, EtagConstants.TABLE_NAME );

		int deleted = context.getContentResolver().delete( EtagConstants.CONTENT_URI, selection, selectionArgs );
//		Log.v( TAG, "delete : deleted=" + deleted );
		
//		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public EtagInfoDelegate convertCursorToEtagInfoDelegate( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToEtagInfoDelegate : enter" );

		long id = -1;
		int dataId = -1;
		String endpoint = "", value = "", masterHostname = "";
		DateTime date = null, lastModifiedDate = null;
		
		if( cursor.getColumnIndex( EtagConstants._ID ) != -1 ) {
			id = cursor.getLong( cursor.getColumnIndex( EtagConstants._ID ) );
		}

		if( cursor.getColumnIndex( EtagConstants.FIELD_ENDPOINT ) != -1 ) {
			endpoint = cursor.getString( cursor.getColumnIndex( EtagConstants.FIELD_ENDPOINT ) );
		}
		
		if( cursor.getColumnIndex( EtagConstants.FIELD_VALUE ) != -1 ) {
			value = cursor.getString( cursor.getColumnIndex( EtagConstants.FIELD_VALUE ) );
		}
		
		if( cursor.getColumnIndex( EtagConstants.FIELD_DATA_ID ) != -1 ) {
			dataId = cursor.getInt( cursor.getColumnIndex( EtagConstants.FIELD_DATA_ID ) );
		}

		if( cursor.getColumnIndex( EtagConstants.FIELD_DATE ) != -1 ) {
			date = new DateTime( cursor.getLong( cursor.getColumnIndex( EtagConstants.FIELD_DATE ) ) );
		}
		
		if( cursor.getColumnIndex( EtagConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			masterHostname = cursor.getString( cursor.getColumnIndex( EtagConstants.FIELD_MASTER_HOSTNAME ) );
		}
		
		if( cursor.getColumnIndex( EtagConstants.FIELD_LAST_MODIFIED_DATE ) != -1 ) {
			lastModifiedDate = new DateTime( cursor.getLong( cursor.getColumnIndex( EtagConstants.FIELD_LAST_MODIFIED_DATE ) ) );
		}
		
		EtagInfoDelegate etag = new EtagInfoDelegate( value );
		etag.setId( id );
		etag.setEndpoint( endpoint );
		etag.setDataId( dataId );
		etag.setDate( date );
		etag.setMasterHostname( masterHostname );
		etag.setLastModified( lastModifiedDate );
		
//		Log.v( TAG, "convertCursorToEtagInfoDelegate : exit" );
		return etag;
	}

	// internal helpers

	private ContentValues convertEtagInfoDelegateToContentValues( final EtagInfoDelegate etag ) {
//		Log.v( TAG, "convertChannelToContentValues : enter" );
		
		ContentValues values = new ContentValues();
		values.put( EtagConstants.FIELD_ENDPOINT, etag.getEndpoint() );
		values.put( EtagConstants.FIELD_VALUE, etag.getValue() );
		values.put( EtagConstants.FIELD_DATA_ID, etag.getDataId() );
		values.put( EtagConstants.FIELD_DATE, null != etag.getDate() ? etag.getDate().getMillis() : ( DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) ) ).getMillis() );
		values.put( EtagConstants.FIELD_MASTER_HOSTNAME, etag.getMasterHostname() );
		values.put( EtagConstants.FIELD_LAST_MODIFIED_DATE, null != etag.getLastModified() ? etag.getLastModified().getMillis() : ( DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) ) ).getMillis() );
		
//		Log.v( TAG, "convertChannelToContentValues : exit" );
		return values;
	}

}
