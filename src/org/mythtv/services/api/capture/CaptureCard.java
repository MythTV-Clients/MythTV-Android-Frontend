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
package org.mythtv.services.api.capture;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 * 
 */
public class CaptureCard {

	@JsonProperty( "CardId" )
	private int cardId;
	
	@JsonProperty( "VideoDevice" )
	private String videoDevice;
	
	@JsonProperty( "AudioDevice" )
	private String audioDevice;
	
	@JsonProperty( "VBIDevice" )
	private String vbiDevice;
	
	@JsonProperty( "CardType" )
	private String cardType;
	
	@JsonProperty( "AudioRateLimit" )
	private int audioRateLimit;
	
	@JsonProperty( "HostName" )
	private String hostName;
	
	@JsonProperty( "DVBSWFilter" )
	private int dvbSwFilter;
	
	@JsonProperty( "DVBSatType" )
	private int dvbSatType;
	
	@JsonProperty( "DVBWaitForSeqStart" )
	private boolean dvbWaitForSeqStart;
	
	@JsonProperty( "SkipBTAudio" )
	private boolean skipBtAudio;
	
	@JsonProperty( "DVBOnDemand" )
	private boolean dvbOnDemand;
	
	@JsonProperty( "DVBDiSEqCType" )
	private int dvbDiSEqCType;
	
	@JsonProperty( "FirewireSpeed" )
	private int firewireSpeed;
	
	@JsonProperty( "FirewireModel" )
	private String firewireModel;
	
	@JsonProperty( "FirewireConnection" )
	private int firewireConnection;
	
	@JsonProperty( "SignalTimeout" )
	private int signalTimeout;
	
	@JsonProperty( "ChannelTimeout" )
	private int channelTimeout;
	
	@JsonProperty( "DVBTuningDelay" )
	private int dvbTuningDelay;
	
	@JsonProperty( "Contrast" )
	private int contrast;
	
	@JsonProperty( "Brightness" )
	private int brightness;
	
	@JsonProperty( "Colour" )
	private int colour;
	
	@JsonProperty( "Hue" )
	private int hue;
	
	@JsonProperty( "DiSEqCId" )
	private int diSEqCId;
	
	@JsonProperty( "DVBEITScan" )
	private boolean dvbEitScan;

	/**
	 * 
	 */
	public CaptureCard() {
	}

	/**
	 * @param cardId
	 * @param videoDevice
	 * @param audioDevice
	 * @param vbiDevice
	 * @param cardType
	 * @param audioRateLimit
	 * @param hostName
	 * @param dvbSwFilter
	 * @param dvbSatType
	 * @param dvbWaitForSeqStart
	 * @param skipBtAudio
	 * @param dvbOnDemand
	 * @param dvbDiSEqCType
	 * @param firewireSpeed
	 * @param firewireModel
	 * @param firewireConnection
	 * @param signalTimeout
	 * @param channelTimeout
	 * @param dvbTuningDelay
	 * @param contrast
	 * @param brightness
	 * @param colour
	 * @param hue
	 * @param diSEqCId
	 * @param dvbEitScan
	 */
	public CaptureCard( int cardId, String videoDevice, String audioDevice, String vbiDevice, String cardType,
			Integer audioRateLimit, String hostName, Integer dvbSwFilter, Integer dvbSatType,
			Boolean dvbWaitForSeqStart, Boolean skipBtAudio, Boolean dvbOnDemand, Integer dvbDiSEqCType,
			Integer firewireSpeed, String firewireModel, Integer firewireConnection, Integer signalTimeout,
			Integer channelTimeout, Integer dvbTuningDelay, Integer contrast, Integer brightness, Integer colour,
			Integer hue, Integer diSEqCId, Boolean dvbEitScan ) {
		this.cardId = cardId;
		this.videoDevice = videoDevice;
		this.audioDevice = audioDevice;
		this.vbiDevice = vbiDevice;
		this.cardType = cardType;
		this.audioRateLimit = audioRateLimit;
		this.hostName = hostName;
		this.dvbSwFilter = dvbSwFilter;
		this.dvbSatType = dvbSatType;
		this.dvbWaitForSeqStart = dvbWaitForSeqStart;
		this.skipBtAudio = skipBtAudio;
		this.dvbOnDemand = dvbOnDemand;
		this.dvbDiSEqCType = dvbDiSEqCType;
		this.firewireSpeed = firewireSpeed;
		this.firewireModel = firewireModel;
		this.firewireConnection = firewireConnection;
		this.signalTimeout = signalTimeout;
		this.channelTimeout = channelTimeout;
		this.dvbTuningDelay = dvbTuningDelay;
		this.contrast = contrast;
		this.brightness = brightness;
		this.colour = colour;
		this.hue = hue;
		this.diSEqCId = diSEqCId;
		this.dvbEitScan = dvbEitScan;
	}

	/**
	 * @return the cardId
	 */
	public int getCardId() {
		return cardId;
	}

	/**
	 * @param cardId
	 *            the cardId to set
	 */
	public void setCardId( int cardId ) {
		this.cardId = cardId;
	}

	/**
	 * @return the videoDevice
	 */
	public String getVideoDevice() {
		return videoDevice;
	}

	/**
	 * @param videoDevice
	 *            the videoDevice to set
	 */
	public void setVideoDevice( String videoDevice ) {
		this.videoDevice = videoDevice;
	}

	/**
	 * @return the audioDevice
	 */
	public String getAudioDevice() {
		return audioDevice;
	}

	/**
	 * @param audioDevice
	 *            the audioDevice to set
	 */
	public void setAudioDevice( String audioDevice ) {
		this.audioDevice = audioDevice;
	}

	/**
	 * @return the vbiDevice
	 */
	public String getVbiDevice() {
		return vbiDevice;
	}

	/**
	 * @param vbiDevice
	 *            the vbiDevice to set
	 */
	public void setVbiDevice( String vbiDevice ) {
		this.vbiDevice = vbiDevice;
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType( String cardType ) {
		this.cardType = cardType;
	}

	/**
	 * @return the audioRateLimit
	 */
	public Integer getAudioRateLimit() {
		return audioRateLimit;
	}

	/**
	 * @param audioRateLimit
	 *            the audioRateLimit to set
	 */
	public void setAudioRateLimit( Integer audioRateLimit ) {
		this.audioRateLimit = audioRateLimit;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public void setHostName( String hostName ) {
		this.hostName = hostName;
	}

	/**
	 * @return the dvbSwFilter
	 */
	public Integer getDvbSwFilter() {
		return dvbSwFilter;
	}

	/**
	 * @param dvbSwFilter
	 *            the dvbSwFilter to set
	 */
	public void setDvbSwFilter( Integer dvbSwFilter ) {
		this.dvbSwFilter = dvbSwFilter;
	}

	/**
	 * @return the dvbSatType
	 */
	public Integer getDvbSatType() {
		return dvbSatType;
	}

	/**
	 * @param dvbSatType
	 *            the dvbSatType to set
	 */
	public void setDvbSatType( Integer dvbSatType ) {
		this.dvbSatType = dvbSatType;
	}

	/**
	 * @return the dvbWaitForSeqStart
	 */
	public Boolean getDvbWaitForSeqStart() {
		return dvbWaitForSeqStart;
	}

	/**
	 * @param dvbWaitForSeqStart
	 *            the dvbWaitForSeqStart to set
	 */
	public void setDvbWaitForSeqStart( Boolean dvbWaitForSeqStart ) {
		this.dvbWaitForSeqStart = dvbWaitForSeqStart;
	}

	/**
	 * @return the skipBtAudio
	 */
	public Boolean getSkipBtAudio() {
		return skipBtAudio;
	}

	/**
	 * @param skipBtAudio
	 *            the skipBtAudio to set
	 */
	public void setSkipBtAudio( Boolean skipBtAudio ) {
		this.skipBtAudio = skipBtAudio;
	}

	/**
	 * @return the dvbOnDemand
	 */
	public Boolean getDvbOnDemand() {
		return dvbOnDemand;
	}

	/**
	 * @param dvbOnDemand
	 *            the dvbOnDemand to set
	 */
	public void setDvbOnDemand( Boolean dvbOnDemand ) {
		this.dvbOnDemand = dvbOnDemand;
	}

	/**
	 * @return the dvbDiSEqCType
	 */
	public Integer getDvbDiSEqCType() {
		return dvbDiSEqCType;
	}

	/**
	 * @param dvbDiSEqCType
	 *            the dvbDiSEqCType to set
	 */
	public void setDvbDiSEqCType( Integer dvbDiSEqCType ) {
		this.dvbDiSEqCType = dvbDiSEqCType;
	}

	/**
	 * @return the firewireSpeed
	 */
	public Integer getFirewireSpeed() {
		return firewireSpeed;
	}

	/**
	 * @param firewireSpeed
	 *            the firewireSpeed to set
	 */
	public void setFirewireSpeed( Integer firewireSpeed ) {
		this.firewireSpeed = firewireSpeed;
	}

	/**
	 * @return the firewireModel
	 */
	public String getFirewireModel() {
		return firewireModel;
	}

	/**
	 * @param firewireModel
	 *            the firewireModel to set
	 */
	public void setFirewireModel( String firewireModel ) {
		this.firewireModel = firewireModel;
	}

	/**
	 * @return the firewireConnection
	 */
	public Integer getFirewireConnection() {
		return firewireConnection;
	}

	/**
	 * @param firewireConnection
	 *            the firewireConnection to set
	 */
	public void setFirewireConnection( Integer firewireConnection ) {
		this.firewireConnection = firewireConnection;
	}

	/**
	 * @return the signalTimeout
	 */
	public Integer getSignalTimeout() {
		return signalTimeout;
	}

	/**
	 * @param signalTimeout
	 *            the signalTimeout to set
	 */
	public void setSignalTimeout( Integer signalTimeout ) {
		this.signalTimeout = signalTimeout;
	}

	/**
	 * @return the channelTimeout
	 */
	public Integer getChannelTimeout() {
		return channelTimeout;
	}

	/**
	 * @param channelTimeout
	 *            the channelTimeout to set
	 */
	public void setChannelTimeout( Integer channelTimeout ) {
		this.channelTimeout = channelTimeout;
	}

	/**
	 * @return the dvbTuningDelay
	 */
	public Integer getDvbTuningDelay() {
		return dvbTuningDelay;
	}

	/**
	 * @param dvbTuningDelay
	 *            the dvbTuningDelay to set
	 */
	public void setDvbTuningDelay( Integer dvbTuningDelay ) {
		this.dvbTuningDelay = dvbTuningDelay;
	}

	/**
	 * @return the contrast
	 */
	public Integer getContrast() {
		return contrast;
	}

	/**
	 * @param contrast
	 *            the contrast to set
	 */
	public void setContrast( Integer contrast ) {
		this.contrast = contrast;
	}

	/**
	 * @return the brightness
	 */
	public Integer getBrightness() {
		return brightness;
	}

	/**
	 * @param brightness
	 *            the brightness to set
	 */
	public void setBrightness( Integer brightness ) {
		this.brightness = brightness;
	}

	/**
	 * @return the colour
	 */
	public Integer getColour() {
		return colour;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public void setColour( Integer colour ) {
		this.colour = colour;
	}

	/**
	 * @return the hue
	 */
	public Integer getHue() {
		return hue;
	}

	/**
	 * @param hue
	 *            the hue to set
	 */
	public void setHue( Integer hue ) {
		this.hue = hue;
	}

	/**
	 * @return the diSEqCId
	 */
	public Integer getDiSEqCId() {
		return diSEqCId;
	}

	/**
	 * @param diSEqCId
	 *            the diSEqCId to set
	 */
	public void setDiSEqCId( Integer diSEqCId ) {
		this.diSEqCId = diSEqCId;
	}

	/**
	 * @return the dvbEitScan
	 */
	public Boolean getDvbEitScan() {
		return dvbEitScan;
	}

	/**
	 * @param dvbEitScan
	 *            the dvbEitScan to set
	 */
	public void setDvbEitScan( Boolean dvbEitScan ) {
		this.dvbEitScan = dvbEitScan;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append( "CaptureCard [" );

		builder.append( "cardId=" ).append( cardId ).append( ", " );

		if( videoDevice != null ) {
			builder.append( "videoDevice=" );
			builder.append( videoDevice );
			builder.append( ", " );
		}

		if( audioDevice != null ) {
			builder.append( "audioDevice=" );
			builder.append( audioDevice );
			builder.append( ", " );
		}

		if( vbiDevice != null ) {
			builder.append( "vbiDevice=" );
			builder.append( vbiDevice );
			builder.append( ", " );
		}

		if( cardType != null ) {
			builder.append( "cardType=" );
			builder.append( cardType );
			builder.append( ", " );
		}

		builder.append( "audioRateLimit=" ).append( audioRateLimit ).append( ", " );

		if( hostName != null ) {
			builder.append( "hostName=" );
			builder.append( hostName );
			builder.append( ", " );
		}

		builder.append( "dvbSwFilter=" ).append( dvbSwFilter ).append( ", " );
		builder.append( "dvbSatType=" ).append( dvbSatType ).append( ", " );
		builder.append( "dvbWaitForSeqStart=" ).append( dvbWaitForSeqStart ).append( ", " );
		builder.append( "skipBtAudio=" ).append( skipBtAudio ).append( ", " );
		builder.append( "dvbOnDemand=" ).append( dvbOnDemand ).append( ", " );
		builder.append( "dvbDiSEqCType=" ).append( dvbDiSEqCType ).append( ", " );
		builder.append( "firewireSpeed=" ).append( firewireSpeed ).append( ", " );
		builder.append( "firewireModel=" ).append( firewireModel ).append( ", " );
		builder.append( "firewireConnection=" ).append( firewireConnection ).append( ", " );
		builder.append( "signalTimeout=" ).append( signalTimeout ).append( ", " );
		builder.append( "channelTimeout=" ).append( channelTimeout ).append( ", " );
		builder.append( "dvbTuningDelay=" ).append( dvbTuningDelay ).append( ", " );
		builder.append( "contrast=" ).append( contrast ).append( ", " );
		builder.append( "brightness=" ).append( brightness ).append( ", " );
		builder.append( "colour=" ).append( colour ).append( ", " );
		builder.append( "hue=" ).append( hue ).append( ", " );
		builder.append( "diSEqCId=" ).append( diSEqCId ).append( ", " );
		builder.append( "dvbEitScan=" ).append( dvbEitScan );

		builder.append( "]" );

		return builder.toString();
	}

}
