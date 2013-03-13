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

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;

import android.content.Context;
import android.text.TextUtils;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractDaoHelper {

	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	protected AbstractDaoHelper() { }

	protected String appendLocationHostname( final Context context, final LocationProfile locationProfile, String selection, String table ) {
		
		return ( !TextUtils.isEmpty( table ) ? ( table + "." ) : "" ) + AbstractBaseConstants.FIELD_MASTER_HOSTNAME
				+ " = '"
				+ locationProfile.getHostname()
				+ "'"
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" );
	}


}
