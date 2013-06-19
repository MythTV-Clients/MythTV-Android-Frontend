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
