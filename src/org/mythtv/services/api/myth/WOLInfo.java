/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.myth;

/**
 * @author Daniel Frey
 *
 */
public class WOLInfo {

	private boolean enabled;
	
	private int reconnect;
	
	private int retry;
	
	private String command;
	
	public WOLInfo() { }

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	/**
	 * @return the reconnect
	 */
	public int getReconnect() {
		return reconnect;
	}

	/**
	 * @param reconnect the reconnect to set
	 */
	public void setReconnect( int reconnect ) {
		this.reconnect = reconnect;
	}

	/**
	 * @return the retry
	 */
	public int getRetry() {
		return retry;
	}

	/**
	 * @param retry the retry to set
	 */
	public void setRetry( int retry ) {
		this.retry = retry;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand( String command ) {
		this.command = command;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "WOLInfo [enabled=" );
		builder.append( enabled );
		builder.append( ", reconnect=" );
		builder.append( reconnect );
		builder.append( ", retry=" );
		builder.append( retry );
		builder.append( ", " );
		
		if( command != null ) {
			builder.append( "command=" );
			builder.append( command );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
