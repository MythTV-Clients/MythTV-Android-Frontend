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
package org.mythtv.service.myth.v25;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v025.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class SettingHelperV25 extends AbstractBaseHelper {

	private static final String TAG = SettingHelperV25.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v025;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static SettingHelperV25 singleton;
	
	/**
	 * Returns the one and only SettingHelperV25. init() must be called before 
	 * any 
	 * @return
	 */
	public static SettingHelperV25 getInstance() {
		if( null == singleton ) {
			
			synchronized( SettingHelperV25.class ) {

				if( null == singleton ) {
					singleton = new SettingHelperV25();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private SettingHelperV25() { }

	public String process( final Context context, final LocationProfile locationProfile, final String settingName, final String settingDefault ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		String setting = null;

		try {

			setting = downloadSetting( locationProfile, settingName, settingDefault );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			setting = null;
		}

		Log.v( TAG, "process : exit" );
		return setting;
	}

	// internal helpers
	
	private String downloadSetting( final LocationProfile locationProfile, final String settingName, String settingDefault ) throws MythServiceApiRuntimeException {
		Log.v( TAG, "downloadSetting : enter" );
	
		String setting = null;

		ResponseEntity<org.mythtv.services.api.v025.beans.SettingList> responseEntity = mMythServicesTemplate.mythOperations().getSetting( locationProfile.getHostname(), settingName, settingDefault,  ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.v025.beans.SettingList settingList = responseEntity.getBody();

			if( null != settingList ) {
				
				if( null != settingList.getSettings() && !settingList.getSettings().isEmpty() ) {
					
					if( settingList.getSettings().containsKey( settingName ) ) {
						setting = settingList.getSettings().get( settingName );
					}
						
				}
				
			}

		}

		Log.v( TAG, "downloadSetting : exit" );
		return setting;
	}
	
}
