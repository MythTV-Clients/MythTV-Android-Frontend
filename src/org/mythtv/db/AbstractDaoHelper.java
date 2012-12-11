/**
 * 
 */
package org.mythtv.db;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;

import android.content.Context;
import android.text.TextUtils;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractDaoHelper {

	protected Context mContext;
	
	protected LocationProfileDaoHelper mLocationProfileDaoHelper;
	
	protected LocationProfile mLocationProfile;
	
	public AbstractDaoHelper( Context context ) {
		this.mContext = context;
		
		mLocationProfileDaoHelper = new LocationProfileDaoHelper( mContext );
		
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile();
	}
	
	protected String appendLocationUrl( String selection, String table ) {
		
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile();

		return ( !TextUtils.isEmpty( table ) ? ( table + "." ) : "" ) + AbstractBaseConstants.FIELD_HOSTNAME
				+ " = '"
				+ mLocationProfile.getHostname()
				+ "'"
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" );
	}


}
