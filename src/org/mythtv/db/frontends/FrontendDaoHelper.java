/**
 * 
 */
package org.mythtv.db.frontends;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.frontends.Frontend;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.service.util.DateUtils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author dmfrey
 *
 */
public class FrontendDaoHelper extends AbstractDaoHelper {

	private static final String TAG = FrontendDaoHelper.class.getSimpleName();
	
	private static FrontendDaoHelper singleton = null;

	/**
	 * Returns the one and only FrontendDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static FrontendDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( FrontendDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new FrontendDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private FrontendDaoHelper() {
		super();
	}
	
	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<Frontend> findAll( final Context context, final LocationProfile locationProfile, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "FrontendDaoHelper is not initialized" );
		
		List<Frontend> frontends = new ArrayList<Frontend>();
		
		selection = appendLocationHostname( context, locationProfile, selection, FrontendConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( FrontendConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			Frontend frontend = convertCursorToFrontend( cursor );
			frontends.add( frontend );
		}
		cursor.close();

		Log.d( TAG, "findAll : exit" );
		return frontends;
	}
	
	/**
	 * @return
	 */
	public List<Frontend> findAll( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "findAll : enter" );
		
		String selection = "";
		String[] selectionArgs = null;

		selection = appendLocationHostname( context, locationProfile, selection, FrontendConstants.TABLE_NAME );

		List<Frontend> frontends = findAll( context, locationProfile, null, selection, selectionArgs, FrontendConstants.FIELD_NAME );
		
		Log.d( TAG, "findAll : exit" );
		return frontends;
	}
	
	/**
	 * @return
	 */
	public List<Frontend> findAllAvailable( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "findAllAvailable : enter" );
		
		String selection = FrontendConstants.FIELD_AVAILABLE + " = ?";
		String[] selectionArgs = new String[] { "1" };

		selection = appendLocationHostname( context, locationProfile, selection, FrontendConstants.TABLE_NAME );

		List<Frontend> frontends = findAll( context, locationProfile, null, selection, selectionArgs, FrontendConstants.FIELD_NAME );
		
		Log.d( TAG, "findAllAvailable : exit" );
		return frontends;
	}
	
	/**
	 * @param id
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public Frontend findOne( final Context context, final LocationProfile locationProfile, Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "FrontendDaoHelper is not initialized" );
		
		Frontend frontend = null;
		
		Uri uri = FrontendConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			Log.d( TAG, "findOne : appending id=" + id );
			uri = ContentUris.withAppendedId( FrontendConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, FrontendConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			frontend = convertCursorToFrontend( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findOne : exit" );
		return frontend;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public Frontend findOne( final Context context, final LocationProfile locationProfile, final Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		Frontend frontend = findOne( context, locationProfile, id, null, null, null, null );
		if( null != frontend ) {
			Log.v( TAG, "findOne : frontend=" + frontend.toString() );
		}
		
		
		Log.d( TAG, "findOne : exit" );
		return frontend;
	}

	/**
	 * @param name
	 * @return
	 */
	public Frontend findByName( final Context context, final LocationProfile locationProfile, final String name ) {
		Log.d( TAG, "findByName : enter" );
		
		Log.d( TAG, "findByName : name=" + name );

		String selection = FrontendConstants.FIELD_NAME + " = ?";
		String[] selectionArgs = new String[] { name };
		
		Frontend frontend = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
		if( null != frontend ) {
			Log.v( TAG, "findByName : frontend=" + frontend.toString() );
		}
				
		Log.d( TAG, "findByName : exit" );
		return frontend;
	}

	/**
	 * @param url
	 * @return
	 */
	public Frontend findByUrl( final Context context, final LocationProfile locationProfile, final String url ) {
		Log.d( TAG, "findByUrl : enter" );
		
		Log.d( TAG, "findByUrl : url=" + url );

		String selection = FrontendConstants.FIELD_URL + " = ?";
		String[] selectionArgs = new String[] { url };
		
		Frontend frontend = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
		if( null != frontend ) {
			Log.v( TAG, "findByUrl : frontend=" + frontend.toString() );
		}
				
		Log.d( TAG, "findByUrl : exit" );
		return frontend;
	}

	/**
	 * @param frontend
	 * @return
	 */
	public int save( final Context context, final LocationProfile locationProfile, Frontend frontend ) {
		Log.d( TAG, "save : enter" );

		if( null == context ) 
			throw new RuntimeException( "FrontendDaoHelper is not initialized" );
		
		ContentValues values = convertFrontendToContentValues( locationProfile, DateUtils.convertUtc( new DateTime() ), frontend );

		String[] projection = new String[] { FrontendConstants._ID };
		String selection = FrontendConstants.FIELD_NAME + " = ? AND " + FrontendConstants.FIELD_URL + " = ?";
		String[] selectionArgs = new String[] { frontend.getName(), frontend.getUrl() };
		
		selection = appendLocationHostname( context, locationProfile, selection, FrontendConstants.TABLE_NAME );
		
		int updated = -1;
		Cursor cursor = context.getContentResolver().query( FrontendConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing frontend" );
			long id = cursor.getLong( cursor.getColumnIndexOrThrow( FrontendConstants._ID ) );
			
			updated = context.getContentResolver().update( ContentUris.withAppendedId( FrontendConstants.CONTENT_URI, id ), values, null, null );
		} else {
			Log.v( TAG, "save : inserting new frontend" );
			
			Uri inserted = context.getContentResolver().insert( FrontendConstants.CONTENT_URI, values );
			if( null != inserted ) {
				updated = 1;
			}
			
		}
		cursor.close();
		Log.v( TAG, "save : updated=" + updated );

		Log.d( TAG, "save : exit" );
		return updated;
	}

	public void resetAllAvailable( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "resetAllAvailable : enter" );
		
		List<Frontend> frontends = findAll( context, locationProfile );
		if( null != frontends && !frontends.isEmpty() ) {
			for( Frontend fe : frontends ) {
				fe.setAvailable( false );
				save( context, locationProfile, fe );
			}
		}
		
		Log.d( TAG, "resetAllAvailable : exit" );
	}
	
	/**
	 * @return
	 */
	public int deleteAll( final Context context ) {
		Log.d( TAG, "deleteAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "FrontendDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( FrontendConstants.CONTENT_URI, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param id
	 * @return
	 */
	public int delete( final Context context, final Long id ) {
		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "FrontendDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( FrontendConstants.CONTENT_URI, id ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param context
	 * @param frontend
	 * @return
	 */
	public int delete( final Context context, final LocationProfile locationProfile, Frontend frontend ) {
		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "FrontendDaoHelper is not initialized" );
		
		String selection = FrontendConstants.FIELD_NAME + " = ? AND " + FrontendConstants.FIELD_URL + " = ?";
		String[] selectionArgs = new String[] { frontend.getName(), frontend.getUrl() };
		
		selection = appendLocationHostname( context, locationProfile, selection, FrontendConstants.TABLE_NAME );
		
		int deleted = context.getContentResolver().delete( FrontendConstants.CONTENT_URI, selection, selectionArgs );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public Frontend convertCursorToFrontend( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToChannelInfo : enter" );

		long id = -1;
		int available = -1;
		String name = "", url = "", masterHostname = "";
		DateTime lastModifiedDate = null;
		
		if( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants._ID ) != -1 ) {
			id = cursor.getLong( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants._ID ) );
		}
		
		if( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_NAME ) != -1 ) {
			name = cursor.getString( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_NAME ) );
		}
		
		if( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_URL ) != -1 ) {
			url = cursor.getString( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_URL ) );
		}
		
		if( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_AVAILABLE ) != -1 ) {
			available = cursor.getInt( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_AVAILABLE ) );
		}
		
		if( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			masterHostname = cursor.getString( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_MASTER_HOSTNAME ) );
		}
		
		if( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_LAST_MODIFIED_DATE ) != -1 ) {
			lastModifiedDate = new DateTime( cursor.getLong( cursor.getColumnIndex( FrontendConstants.TABLE_NAME + "_" + FrontendConstants.FIELD_LAST_MODIFIED_DATE ) ) );
		}
		
		Frontend frontend = new Frontend();
		frontend.setId( id );
		frontend.setName( name );
		frontend.setUrl( url );
		frontend.setAvailable( available == 1 ? true : false );
		frontend.setMasterHostname( masterHostname );
		frontend.setLastModifiedDate( lastModifiedDate );
		
//		Log.v( TAG, "convertCursorToFrontend : exit" );
		return frontend;
	}

	// internal helpers

	private ContentValues[] convertFrontendsToContentValuesArray( final LocationProfile locationProfile, final DateTime lastModified, final List<Frontend> frontends ) {
//		Log.v( TAG, "convertFrontendsToContentValuesArray : enter" );
		
		if( null != frontends && !frontends.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( Frontend frontend : frontends ) {

				contentValues = convertFrontendToContentValues( locationProfile, lastModified, frontend );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
//				Log.v( TAG, "convertFrontendsToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
//		Log.v( TAG, "convertFrontendsToContentValuesArray : exit, no frontends to convert" );
		return null;
	}

	private ContentValues convertFrontendToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final Frontend frontend ) {
//		Log.v( TAG, "convertChannelToContentValues : enter" );
		
		ContentValues values = new ContentValues();
		values.put( FrontendConstants.FIELD_NAME, frontend.getName() );
		values.put( FrontendConstants.FIELD_URL, frontend.getUrl() );
		values.put( FrontendConstants.FIELD_AVAILABLE, frontend.isAvailable() ? 1 : 0 );
		values.put( FrontendConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( FrontendConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
//		Log.v( TAG, "convertChannelToContentValues : exit" );
		return values;
	}

}
