/**
 * 
 */
package org.mythtv.service.myth.v26;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class SettingHelperV26 extends AbstractBaseHelper {

	private static final String TAG = SettingHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static SettingHelperV26 singleton;
	
	/**
	 * Returns the one and only SettingHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static SettingHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( SettingHelperV26.class ) {

				if( null == singleton ) {
					singleton = new SettingHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private SettingHelperV26() { }

	public String process( final LocationProfile locationProfile, final String settingName, final String settingDefault ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
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
	
	private String downloadSetting( final LocationProfile locationProfile, final String settingName, String settingDefault ) {
		Log.v( TAG, "downloadSetting : enter" );
	
		String setting = null;

		ResponseEntity<org.mythtv.services.api.v026.beans.SettingList> responseEntity = mMythServicesTemplate.mythOperations().getSetting( locationProfile.getHostname(), settingName, settingDefault,  ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.v026.beans.SettingList settingList = responseEntity.getBody();

			if( null != settingList ) {
				
				if( null != settingList.getSetting() ) {
					
					if( null != settingList.getSetting().getSettings() && !settingList.getSetting().getSettings().isEmpty() ) {
						
						if( settingList.getSetting().getSettings().containsKey( settingName ) ) {
							setting = settingList.getSetting().getSettings().get( settingName );
						}
						
					}
					
				}
				
			}

		}

		Log.v( TAG, "downloadSetting : exit" );
		return setting;
	}
	
}
