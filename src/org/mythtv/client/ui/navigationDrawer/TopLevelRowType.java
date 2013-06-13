/**
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmfrey
 *
 */
public enum TopLevelRowType {
    VERSION_ROW,
    PROFILE_ROW,
    FRONTENDS_HEADER_ROW,
    FRONTENDS_ROW,
    ACTIONS_HEADER_ROW,
    ACTION_ROW;

    private static Map<Integer, TopLevelRowType> valueMap;

    public static TopLevelRowType getValue( Integer ordinal ) {
        if( null == valueMap ) {
            valueMap = new HashMap<Integer, TopLevelRowType>();
            
            for( TopLevelRowType type : values() ) {
                valueMap.put( type.ordinal(), type );
            }
            
        }
        return valueMap.get( ordinal );

    }

}
