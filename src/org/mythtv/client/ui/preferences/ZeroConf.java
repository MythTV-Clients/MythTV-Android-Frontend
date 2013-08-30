/**
 * 
 */
package org.mythtv.client.ui.preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Frey
 *
 */
public enum ZeroConf {
	VERSION_25( "0.25", "_mythbackend-master._tcp.local." ),
	VERSION_26( "0.26", "_mythbackend-master._tcp.local." ),
	VERSION_27( "0.27", "_mythbackend._tcp.local." );
	
	private String version;
	private String type;
	
	private ZeroConf( String version, String type ) {
		this.version = version;
		this.type = type;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getType() {
		return type;
	}

    private static Map<String, ZeroConf> versionValueMap;

    public static ZeroConf fromVersion( String fromVersion ) {
        
    	if( null == versionValueMap ) {
            versionValueMap = new HashMap<String, ZeroConf>();
            
            for( ZeroConf zc : values() ) {
                versionValueMap.put( zc.version, zc );
            }
            
        }
        
        if( null == fromVersion || "".equals( fromVersion ) ) {
        	throw new IllegalArgumentException( "fromVersion is required" );
        }
        
        if( fromVersion.length() > 4 ) {
        	fromVersion = fromVersion.substring( 0, 3 );
        }
        
        return versionValueMap.get( fromVersion );
    }

    private static Map<String, ZeroConf> typeValueMap;

    public static ZeroConf fromType( String fromType ) {
        
    	if( null == typeValueMap ) {
            typeValueMap = new HashMap<String, ZeroConf>();
            
            for( ZeroConf zc : values() ) {
                versionValueMap.put( zc.type, zc );
            }
            
        }
        
        if( null == fromType || "".equals( fromType ) ) {
        	throw new IllegalArgumentException( "fromType is required" );
        }
        
        return typeValueMap.get( fromType );
    }

}
