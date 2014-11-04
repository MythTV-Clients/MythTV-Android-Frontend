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
