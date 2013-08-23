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
package org.mythtv.service.util;

import java.util.logging.Level;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythtvServiceHelper {

	private static final String TAG = NetworkHelper.class.getSimpleName();
	
	private static MythtvServiceHelper singleton = null;
	
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();

	/**
	 * Returns the one and only MythtvServiceHelper. init() must be called before 
	 * any 
	 * @return
	 */
	public static MythtvServiceHelper getInstance() {
		if( null == singleton ) {
			
			synchronized( MythtvServiceHelper.class ) {

				if( null == singleton ) {
					singleton = new MythtvServiceHelper();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private MythtvServiceHelper() { }

	public MythServices getMythServicesApi( final Context context ) {
		Log.v( TAG, "getMythServicesApi : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "MythtvServiceHelper is not initialized" );
		
		MythServicesServiceProvider mMythServicesServiceProvider = new MythServicesServiceProvider( mLocationProfileDaoHelper.findConnectedProfile( context ).getUrl(), Level.FINE );
		
		Log.v( TAG, "getMythServicesApi : exit" );
		return mMythServicesServiceProvider.getApi();
	}

	public MythServices getMythServicesApi( LocationProfile profile ) {
		Log.v( TAG, "getMythServicesApi : enter" );
		
		if( null == profile ) 
			throw new RuntimeException( "MythtvServiceHelper is not initialized" );
		
		MythServicesServiceProvider provider = new MythServicesServiceProvider( profile.getUrl() );
		
		Log.v( TAG, "getMythServicesApi : exit" );
		return provider.getApi();
	}

}
