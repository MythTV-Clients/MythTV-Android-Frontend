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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.preferences;

import org.mythtv.client.ui.preferences.LocationProfile.LocationType;

/**
 * @author Daniel Frey
 *
 */
public class PlaybackProfile {

	private int id;
	private LocationType type;
	private String name;
	private int width;
	private int height;
	private int videoBitrate;
	private int audioBitrate;
	private int audioSampleRate;
	private boolean selected;

	public PlaybackProfile() { }

	public PlaybackProfile( int id, LocationType type, String name, int width, int height, int videoBitrate, int audioBitrate, int audioSampleRate, boolean selected ) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.width = width;
		this.height = height;
		this.videoBitrate = videoBitrate;
		this.audioBitrate = audioBitrate;
		this.audioSampleRate = audioSampleRate;
		this.selected = selected;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( int id ) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public LocationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType( LocationType type ) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth( int width ) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight( int height ) {
		this.height = height;
	}

	/**
	 * @return the videoBitrate
	 */
	public int getVideoBitrate() {
		return videoBitrate;
	}

	/**
	 * @param videoBitrate the videoBitrate to set
	 */
	public void setVideoBitrate( int videoBitrate ) {
		this.videoBitrate = videoBitrate;
	}

	/**
	 * @return the audioBitrate
	 */
	public int getAudioBitrate() {
		return audioBitrate;
	}

	/**
	 * @param audioBitrate the audioBitrate to set
	 */
	public void setAudioBitrate( int audioBitrate ) {
		this.audioBitrate = audioBitrate;
	}

	/**
	 * @return the audioSampleRate
	 */
	public int getAudioSampleRate() {
		return audioSampleRate;
	}

	/**
	 * @param audioSampleRate the audioSampleRate to set
	 */
	public void setAudioSampleRate( int audioSampleRate ) {
		this.audioSampleRate = audioSampleRate;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected( boolean selected ) {
		this.selected = selected;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "PlaybackProfile [id=" );
		builder.append( id );
		builder.append( ", " );
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
			builder.append( ", " );
		}
		
		if( name != null ) {
			builder.append( "name=" );
			builder.append( name );
			builder.append( ", " );
		}
		
		builder.append( "width=" );
		builder.append( width );
		builder.append( ", height=" );
		builder.append( height );
		builder.append( ", videoBitrate=" );
		builder.append( videoBitrate );
		builder.append( ", audioBitrate=" );
		builder.append( audioBitrate );
		builder.append( ", audioSampleRate=" );
		builder.append( audioSampleRate );
		builder.append( ", selected=" );
		builder.append( selected );
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
