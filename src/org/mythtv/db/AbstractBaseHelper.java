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
/**
 * 
 */
package org.mythtv.db;

import java.util.ArrayList;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.provider.MythtvProvider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractBaseHelper {

	private static final String TAG = AbstractBaseHelper.class.getSimpleName();
	
	protected static final int BATCH_COUNT_LIMIT = 99;

	protected static EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	protected static LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	protected static String appendLocationHostname( final Context context, final LocationProfile locationProfile, String selection, String table ) {
		
		return ( !TextUtils.isEmpty( table ) ? ( table + "." ) : "" ) + AbstractBaseConstants.FIELD_MASTER_HOSTNAME
				+ " = '"
				+ locationProfile.getHostname()
				+ "'"
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" );
	}

	protected static void processBatch( final Context context, ArrayList<ContentProviderOperation> ops, int processed, int count ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "processBatch : enter" );
		
		if( !ops.isEmpty() ) {
			Log.v( TAG, "processBatch : applying batch" );
			
			ContentProviderResult[] results = context.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
			processed += results.length;
			
			if( results.length > 0 ) {
				ops.clear();

				if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
					for( ContentProviderResult result : results ) {
						Log.v( TAG, "processBatch : batch result=" + result.toString() );
					}
				}
				
			}
			
		}

		count = 0;

		Log.v( TAG, "processBatch : exit" );
	}
	

}
