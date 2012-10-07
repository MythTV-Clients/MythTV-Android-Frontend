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
package org.mythtv.service.util;

/**
 * @author Daniel Frey
 *
 */
public class UrlUtils {

	public static String encodeUrl( String string ) {
		
		string = string.replace( "%", "%25" );
		string = string.replace( " ", "%20" );
		string = string.replace( "<", "%3C" );
		string = string.replace( ">", "%3E" );
		string = string.replace( "#", "%23" );
		string = string.replace( "{", "%7B" );
		string = string.replace( "}", "%7D" );
		string = string.replace( "|", "%7C" );
		string = string.replace( "\\", "%5C" );
		string = string.replace( "^", "%5E" );
		string = string.replace( "~", "%7E" );
		string = string.replace( "[", "%5B" );
		string = string.replace( "]", "%5D" );
		string = string.replace( "`", "%60" );
		string = string.replace( ";", "%3B" );
		string = string.replace( "/", "%2F" );
		string = string.replace( "?", "%2F" );
		string = string.replace( ":", "%3A" );
		string = string.replace( "@", "%40" );
		string = string.replace( "=", "%3D" );
		string = string.replace( "&", "%26" );
		string = string.replace( "$", "%24" );

		return string;
	}
	
}
