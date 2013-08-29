/**
 * 
 */
package org.mythtv.client.ui.media.navigationDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmfrey
 *
 */
public enum MediaRowType {
    VERSION_ROW,
    ACTIONS_HEADER_ROW,
    PICTURES_ROW,
    VIDEOS_ROW,
    MUSIC_ROW;

    private static Map<Integer, MediaRowType> valueMap;

    public static MediaRowType getValue( Integer ordinal ) {
        if( null == valueMap ) {
            valueMap = new HashMap<Integer, MediaRowType>();
            
            for( MediaRowType type : values() ) {
                valueMap.put( type.ordinal(), type );
            }
            
        }
        return valueMap.get( ordinal );

    }

}
