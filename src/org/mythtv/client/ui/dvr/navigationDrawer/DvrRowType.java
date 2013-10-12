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
package org.mythtv.client.ui.dvr.navigationDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmfrey
 *
 */
public enum DvrRowType {
    VERSION_ROW,
    ACTIONS_HEADER_ROW,
    RECORDINGS_ROW,
    RECORDINGS_LAST_UPDATE_ROW,
    UPCOMING_ROW,
    UPCOMING_LAST_UPDATE_ROW,
    GUIDE_ROW,
    GUIDE_LAST_UPDATE_ROW,
    RECORDING_RULES_ROW,
    RECORDING_RULES_LAST_UPDATE_ROW;

    private static Map<Integer, DvrRowType> valueMap;

    public static DvrRowType getValue( Integer ordinal ) {
        if( null == valueMap ) {
            valueMap = new HashMap<Integer, DvrRowType>();
            
            for( DvrRowType type : values() ) {
                valueMap.put( type.ordinal(), type );
            }
            
        }
        return valueMap.get( ordinal );

    }

}
