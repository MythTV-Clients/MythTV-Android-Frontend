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
 * This software can be found at <https://github.com/MythTV-Android/MythTV-Service-API/>
 *
 */
package org.mythtv.db.video.model;

import org.joda.time.DateTime;
import org.mythtv.services.api.DateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Daniel Frey
 *
 */
public class VideoMultiplex {

	@JsonProperty( "MplexId" )
	private int multiplexId;
	
	@JsonProperty( "SourceId" )
	private int sourceId;
	
	@JsonProperty( "TransportId" )
	private int transportId;
	
	@JsonProperty( "NetworkId" )
	private int networkId;
	
	@JsonProperty( "Frequency" )
	private int frequency;
	
	@JsonProperty( "Inversion" )
	private String inversion;
	
	@JsonProperty( "SymbolRate" )
	private int symbolRate;
	
	@JsonProperty( "FEC" )
	private String fec;
	
	@JsonProperty( "Polarity" )
	private String polarity;
	
	@JsonProperty( "Modulation" )
	private String modulation;
	
	@JsonProperty( "Bandwidth" )
	private String bandwidth;
	
	@JsonProperty( "LPCodeRate" )
	private String lpCodeRate;
	
	@JsonProperty( "HPCodeRate" )
	private String hpCodeRate;
	
	@JsonProperty( "TransmissionMode" )
	private String transmissionMode;
	
	@JsonProperty( "GuardInterval" )
	private String guardInterval;
	
	@JsonProperty( "Visible" )
	private boolean visible;
	
	@JsonProperty( "Constellation" )
	private String constellation;
	
	@JsonProperty( "Hierarchy" )
	private String hierarchy;
	
	@JsonProperty( "ModulationSystem" )
	private String modulationSystem;
	
	@JsonProperty( "RollOff" )
	private String rollOff;
	
	@JsonProperty( "SIStandard" )
	private String siStandard;
	
	@JsonProperty( "ServiceVersion" )
	private int serviceVersion;
	
	@JsonProperty( "UpdateTimeStamp" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime updateTimestamp;
	
	@JsonProperty( "DefaultAuthority" )
	private String defaultAuthority;
	
	public VideoMultiplex() { }

	/**
	 * @return the multiplexId
	 */
	public int getMultiplexId() {
		return multiplexId;
	}

	/**
	 * @param multiplexId the multiplexId to set
	 */
	public void setMultiplexId( int multiplexId ) {
		this.multiplexId = multiplexId;
	}

	/**
	 * @return the sourceId
	 */
	public int getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId( int sourceId ) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the transportId
	 */
	public int getTransportId() {
		return transportId;
	}

	/**
	 * @param transportId the transportId to set
	 */
	public void setTransportId( int transportId ) {
		this.transportId = transportId;
	}

	/**
	 * @return the networkId
	 */
	public int getNetworkId() {
		return networkId;
	}

	/**
	 * @param networkId the networkId to set
	 */
	public void setNetworkId( int networkId ) {
		this.networkId = networkId;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency( int frequency ) {
		this.frequency = frequency;
	}

	/**
	 * @return the inversion
	 */
	public String getInversion() {
		return inversion;
	}

	/**
	 * @param inversion the inversion to set
	 */
	public void setInversion( String inversion ) {
		this.inversion = inversion;
	}

	/**
	 * @return the symbolRate
	 */
	public int getSymbolRate() {
		return symbolRate;
	}

	/**
	 * @param symbolRate the symbolRate to set
	 */
	public void setSymbolRate( int symbolRate ) {
		this.symbolRate = symbolRate;
	}

	/**
	 * @return the fec
	 */
	public String getFec() {
		return fec;
	}

	/**
	 * @param fec the fec to set
	 */
	public void setFec( String fec ) {
		this.fec = fec;
	}

	/**
	 * @return the polarity
	 */
	public String getPolarity() {
		return polarity;
	}

	/**
	 * @param polarity the polarity to set
	 */
	public void setPolarity( String polarity ) {
		this.polarity = polarity;
	}

	/**
	 * @return the modulation
	 */
	public String getModulation() {
		return modulation;
	}

	/**
	 * @param modulation the modulation to set
	 */
	public void setModulation( String modulation ) {
		this.modulation = modulation;
	}

	/**
	 * @return the bandwidth
	 */
	public String getBandwidth() {
		return bandwidth;
	}

	/**
	 * @param bandwidth the bandwidth to set
	 */
	public void setBandwidth( String bandwidth ) {
		this.bandwidth = bandwidth;
	}

	/**
	 * @return the lpCodeRate
	 */
	public String getLpCodeRate() {
		return lpCodeRate;
	}

	/**
	 * @param lpCodeRate the lpCodeRate to set
	 */
	public void setLpCodeRate( String lpCodeRate ) {
		this.lpCodeRate = lpCodeRate;
	}

	/**
	 * @return the hpCodeRate
	 */
	public String getHpCodeRate() {
		return hpCodeRate;
	}

	/**
	 * @param hpCodeRate the hpCodeRate to set
	 */
	public void setHpCodeRate( String hpCodeRate ) {
		this.hpCodeRate = hpCodeRate;
	}

	/**
	 * @return the transmissionMode
	 */
	public String getTransmissionMode() {
		return transmissionMode;
	}

	/**
	 * @param transmissionMode the transmissionMode to set
	 */
	public void setTransmissionMode( String transmissionMode ) {
		this.transmissionMode = transmissionMode;
	}

	/**
	 * @return the guardInterval
	 */
	public String getGuardInterval() {
		return guardInterval;
	}

	/**
	 * @param guardInterval the guardInterval to set
	 */
	public void setGuardInterval( String guardInterval ) {
		this.guardInterval = guardInterval;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible( boolean visible ) {
		this.visible = visible;
	}

	/**
	 * @return the constellation
	 */
	public String getConstellation() {
		return constellation;
	}

	/**
	 * @param constellation the constellation to set
	 */
	public void setConstellation( String constellation ) {
		this.constellation = constellation;
	}

	/**
	 * @return the hierarchy
	 */
	public String getHierarchy() {
		return hierarchy;
	}

	/**
	 * @param hierarchy the hierarchy to set
	 */
	public void setHierarchy( String hierarchy ) {
		this.hierarchy = hierarchy;
	}

	/**
	 * @return the modulationSystem
	 */
	public String getModulationSystem() {
		return modulationSystem;
	}

	/**
	 * @param modulationSystem the modulationSystem to set
	 */
	public void setModulationSystem( String modulationSystem ) {
		this.modulationSystem = modulationSystem;
	}

	/**
	 * @return the rollOff
	 */
	public String getRollOff() {
		return rollOff;
	}

	/**
	 * @param rollOff the rollOff to set
	 */
	public void setRollOff( String rollOff ) {
		this.rollOff = rollOff;
	}

	/**
	 * @return the siStandard
	 */
	public String getSiStandard() {
		return siStandard;
	}

	/**
	 * @param siStandard the siStandard to set
	 */
	public void setSiStandard( String siStandard ) {
		this.siStandard = siStandard;
	}

	/**
	 * @return the serviceVersion
	 */
	public int getServiceVersion() {
		return serviceVersion;
	}

	/**
	 * @param serviceVersion the serviceVersion to set
	 */
	public void setServiceVersion( int serviceVersion ) {
		this.serviceVersion = serviceVersion;
	}

	/**
	 * @return the updateTimestamp
	 */
	public DateTime getUpdateTimestamp() {
		return updateTimestamp;
	}

	/**
	 * @param updateTimestamp the updateTimestamp to set
	 */
	public void setUpdateTimestamp( DateTime updateTimestamp ) {
		this.updateTimestamp = updateTimestamp;
	}

	/**
	 * @return the defaultAuthority
	 */
	public String getDefaultAuthority() {
		return defaultAuthority;
	}

	/**
	 * @param defaultAuthority the defaultAuthority to set
	 */
	public void setDefaultAuthority( String defaultAuthority ) {
		this.defaultAuthority = defaultAuthority;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoMultiplex [multiplexId=" );
		builder.append( multiplexId );
		builder.append( ", sourceId=" );
		builder.append( sourceId );
		builder.append( ", transportId=" );
		builder.append( transportId );
		builder.append( ", networkId=" );
		builder.append( networkId );
		builder.append( ", frequency=" );
		builder.append( frequency );
		builder.append( ", " );
		
		if( inversion != null ) {
			builder.append( "inversion=" );
			builder.append( inversion );
			builder.append( ", " );
		}
		
		builder.append( "symbolRate=" );
		builder.append( symbolRate );
		builder.append( ", " );
		
		if( fec != null ) {
			builder.append( "fec=" );
			builder.append( fec );
			builder.append( ", " );
		}
		
		if( polarity != null ) {
			builder.append( "polarity=" );
			builder.append( polarity );
			builder.append( ", " );
		}
		
		if( modulation != null ) {
			builder.append( "modulation=" );
			builder.append( modulation );
			builder.append( ", " );
		}
		
		if( bandwidth != null ) {
			builder.append( "bandwidth=" );
			builder.append( bandwidth );
			builder.append( ", " );
		}
		
		if( lpCodeRate != null ) {
			builder.append( "lpCodeRate=" );
			builder.append( lpCodeRate );
			builder.append( ", " );
		}
		
		if( hpCodeRate != null ) {
			builder.append( "hpCodeRate=" );
			builder.append( hpCodeRate );
			builder.append( ", " );
		}
		
		if( transmissionMode != null ) {
			builder.append( "transmissionMode=" );
			builder.append( transmissionMode );
			builder.append( ", " );
		}
		
		if( guardInterval != null ) {
			builder.append( "guardInterval=" );
			builder.append( guardInterval );
			builder.append( ", " );
		}
		
		builder.append( "visible=" );
		builder.append( visible );
		builder.append( ", " );
		
		if( constellation != null ) {
			builder.append( "constellation=" );
			builder.append( constellation );
			builder.append( ", " );
		}
		
		if( hierarchy != null ) {
			builder.append( "hierarchy=" );
			builder.append( hierarchy );
			builder.append( ", " );
		}
		
		if( modulationSystem != null ) {
			builder.append( "modulationSystem=" );
			builder.append( modulationSystem );
			builder.append( ", " );
		}
		
		if( rollOff != null ) {
			builder.append( "rollOff=" );
			builder.append( rollOff );
			builder.append( ", " );
		}
		
		if( siStandard != null ) {
			builder.append( "siStandard=" );
			builder.append( siStandard );
			builder.append( ", " );
		}
		
		builder.append( "serviceVersion=" );
		builder.append( serviceVersion );
		builder.append( ", " );
		
		if( updateTimestamp != null ) {
			builder.append( "updateTimestamp=" );
			builder.append( updateTimestamp );
			builder.append( ", " );
		}
		
		if( defaultAuthority != null ) {
			builder.append( "defaultAuthority=" );
			builder.append( defaultAuthority );
		}
		
		builder.append( "]" );
		
		return builder.toString();
	}
	
}
