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
package org.mythtv.db.channel.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.mythtv.db.dvr.model.Program;

import android.util.Log;


/**
 * @author Daniel Frey
 *
 */
public class ChannelInfo implements Serializable, Comparable<ChannelInfo> {
	
	private static final String TAG = ChannelInfo.class.getSimpleName();
	
	private static final long serialVersionUID = 5939218201237085127L;

	private long id;
	private int channelId;
	private String channelNumber;
	private String callSign;
	private String iconUrl;
	private String channelName;
	private int multiplexId;
	private int transportId;
	private int serviceId;
	private int networkId;
	private int atscMajorChannel;
	private int atscMinorChannel;
	private String format;
	private String modulation;
	private long frequency;
	private String frequencyId;
	private String frequenceTable;
	private int fineTune;
	private String siStandard;
	private String channelFilters;
	private int sourceId;
	private int inputId;
	private int commercialFree;
	private boolean useEit;
	private boolean visible;
	private String xmltvId;
	private String defaultAuth;
	private List<Program> programs;
	
	public ChannelInfo() { }

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( long id ) {
		this.id = id;
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId( int channelId ) {
		this.channelId = channelId;
	}

	/**
	 * @return the channelNumber
	 */
	public String getChannelNumber() {
		return channelNumber;
	}

	/**
	 * @param channelNumber the channelNumber to set
	 */
	public void setChannelNumber( String channelNumber ) {
		this.channelNumber = channelNumber;
	}

	/**
	 * @return the callSign
	 */
	public String getCallSign() {
		return callSign;
	}

	/**
	 * @param callSign the callSign to set
	 */
	public void setCallSign( String callSign ) {
		this.callSign = callSign;
	}

	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/**
	 * @param iconUrl the iconUrl to set
	 */
	public void setIconUrl( String iconUrl ) {
		this.iconUrl = iconUrl;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName( String channelName ) {
		this.channelName = channelName;
	}

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
	 * @return the serviceId
	 */
	public int getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId( int serviceId ) {
		this.serviceId = serviceId;
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
	 * @return the atscMajorChannel
	 */
	public int getAtscMajorChannel() {
		return atscMajorChannel;
	}

	/**
	 * @param atscMajorChannel the atscMajorChannel to set
	 */
	public void setAtscMajorChannel( int atscMajorChannel ) {
		this.atscMajorChannel = atscMajorChannel;
	}

	/**
	 * @return the atscMinorChannel
	 */
	public int getAtscMinorChannel() {
		return atscMinorChannel;
	}

	/**
	 * @param atscMinorChannel the atscMinorChannel to set
	 */
	public void setAtscMinorChannel( int atscMinorChannel ) {
		this.atscMinorChannel = atscMinorChannel;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat( String format ) {
		this.format = format;
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
	 * @return the frequency
	 */
	public long getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency( long frequency ) {
		this.frequency = frequency;
	}

	/**
	 * @return the frequencyId
	 */
	public String getFrequencyId() {
		return frequencyId;
	}

	/**
	 * @param frequencyId the frequencyId to set
	 */
	public void setFrequencyId( String frequencyId ) {
		this.frequencyId = frequencyId;
	}

	/**
	 * @return the frequenceTable
	 */
	public String getFrequenceTable() {
		return frequenceTable;
	}

	/**
	 * @param frequenceTable the frequenceTable to set
	 */
	public void setFrequenceTable( String frequenceTable ) {
		this.frequenceTable = frequenceTable;
	}

	/**
	 * @return the fineTune
	 */
	public int getFineTune() {
		return fineTune;
	}

	/**
	 * @param fineTune the fineTune to set
	 */
	public void setFineTune( int fineTune ) {
		this.fineTune = fineTune;
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
	 * @return the channelFilters
	 */
	public String getChannelFilters() {
		return channelFilters;
	}

	/**
	 * @param channelFilters the channelFilters to set
	 */
	public void setChannelFilters( String channelFilters ) {
		this.channelFilters = channelFilters;
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
	 * @return the inputId
	 */
	public int getInputId() {
		return inputId;
	}

	/**
	 * @param inputId the inputId to set
	 */
	public void setInputId( int inputId ) {
		this.inputId = inputId;
	}

	/**
	 * @return the commercialFree
	 */
	public int getCommercialFree() {
		return commercialFree;
	}

	/**
	 * @param commercialFree the commercialFree to set
	 */
	public void setCommercialFree( int commercialFree ) {
		this.commercialFree = commercialFree;
	}

	/**
	 * @return the useEit
	 */
	public boolean isUseEit() {
		return useEit;
	}

	/**
	 * @param useEit the useEit to set
	 */
	public void setUseEit( boolean useEit ) {
		this.useEit = useEit;
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
	 * @return the xmltvId
	 */
	public String getXmltvId() {
		return xmltvId;
	}

	/**
	 * @param xmltvId the xmltvId to set
	 */
	public void setXmltvId( String xmltvId ) {
		this.xmltvId = xmltvId;
	}

	/**
	 * @return the defaultAuth
	 */
	public String getDefaultAuth() {
		return defaultAuth;
	}

	/**
	 * @param defaultAuth the defaultAuth to set
	 */
	public void setDefaultAuth( String defaultAuth ) {
		this.defaultAuth = defaultAuth;
	}

	/**
	 * @return the programs
	 */
	public List<Program> getPrograms() {
		return programs;
	}

	/**
	 * @param programs the programs to set
	 */
	public void setPrograms( List<Program> programs ) {
		this.programs = programs;
		
		/* *
		 * Sort the program list in chronological order. The list is assumed
		 * to be sorted when building the guide user interface.
		 * */
		Collections.sort(programs, new Comparator<Program>(){ 

			@Override
			public int compare(Program o1, Program o2) {
				if (o1 == null && o2 == null)
					return 0;
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				if (o1.getStartTime() == null && o2.getStartTime() == null)
					return 0;
				if (o1.getStartTime() == null)
					return -1;
				if (o2.getStartTime() == null)
					return 1;
				return o1.getStartTime().compareTo(o2.getStartTime());
				
			}});
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = 1;
		result = prime * result + ( ( channelNumber == null ) ? 0 : channelNumber.hashCode() );
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) {
			return true;
		}
		
		if( obj == null ) {
			return false;
		}
		
		if( getClass() != obj.getClass() ) {
			return false;
		}
		
		ChannelInfo other = (ChannelInfo) obj;
		return compareTo(other) == 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( ChannelInfo arg ) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
		
	    if( this == arg ) return EQUAL;
	    if( arg == null ) return AFTER;
	    
		try {
			String sThisChannelNumber = channelNumber;
			sThisChannelNumber = sThisChannelNumber.replaceAll("\\D+", ".");

			String sOtherChannelNumber = arg.getChannelNumber();
			sOtherChannelNumber = sOtherChannelNumber.replaceAll("\\D+", ".");

			Double dThisChannelNumber = Double.parseDouble( sThisChannelNumber );
			Double dOtherChannelNumber = Double.parseDouble( sOtherChannelNumber );

			// check for NaN
			if (dThisChannelNumber.isNaN() && dOtherChannelNumber.isNaN())
				return EQUAL;
			if (dThisChannelNumber.isNaN())
				return BEFORE;
			if (dOtherChannelNumber.isNaN())
				return AFTER;
			
			if( dThisChannelNumber.doubleValue() < dOtherChannelNumber.doubleValue() ) return BEFORE;
			if( dThisChannelNumber.doubleValue() > dOtherChannelNumber.doubleValue() ) return AFTER;
		} catch( Exception e ) {
			Log.e( TAG, "compareTo : error", e );
		}

		return EQUAL;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Channel [" );
		
		builder.append( "id=" );
		builder.append( id );
		builder.append( ", " );
		
		builder.append( "channelId=" );
		builder.append( channelId );
		builder.append( ", " );
		
		if( channelNumber != null ) {
			builder.append( "channelNumber=" );
			builder.append( channelNumber );
			builder.append( ", " );
		}
		
		if( callSign != null ) {
			builder.append( "callSign=" );
			builder.append( callSign );
			builder.append( ", " );
		}
		
		if( iconUrl != null ) {
			builder.append( "iconUrl=" );
			builder.append( iconUrl );
			builder.append( ", " );
		}
		
		if( channelName != null ) {
			builder.append( "channelName=" );
			builder.append( channelName );
			builder.append( ", " );
		}
		
		builder.append( "multiplexId=" );
		builder.append( multiplexId );
		builder.append( ", transportId=" );
		builder.append( transportId );
		builder.append( ", serviceId=" );
		builder.append( serviceId );
		builder.append( ", networkId=" );
		builder.append( networkId );
		builder.append( ", atscMajorChannel=" );
		builder.append( atscMajorChannel );
		builder.append( ", atscMinorChannel=" );
		builder.append( atscMinorChannel );
		builder.append( ", " );
		
		if( format != null ) {
			builder.append( "format=" );
			builder.append( format );
			builder.append( ", " );
		}
		
		if( modulation != null ) {
			builder.append( "modulation=" );
			builder.append( modulation );
			builder.append( ", " );
		}
		
		builder.append( "frequency=" );
		builder.append( frequency );
		builder.append( ", " );
		
		if( frequencyId != null ) {
			builder.append( "frequencyId=" );
			builder.append( frequencyId );
			builder.append( ", " );
		}

		if( frequenceTable != null ) {
			builder.append( "frequenceTable=" );
			builder.append( frequenceTable );
			builder.append( ", " );
		}
		
		builder.append( "fineTune=" );
		builder.append( fineTune );
		builder.append( ", " );
		
		if( siStandard != null ) {
			builder.append( "siStandard=" );
			builder.append( siStandard );
			builder.append( ", " );
		}
		
		if( channelFilters != null ) {
			builder.append( "channelFilters=" );
			builder.append( channelFilters );
			builder.append( ", " );
		}
		
		builder.append( "sourceId=" );
		builder.append( sourceId );
		builder.append( ", inputId=" );
		builder.append( inputId );
		builder.append( ", commercialFree=" );
		builder.append( commercialFree );
		builder.append( ", useEit=" );
		builder.append( useEit );
		builder.append( ", visable=" );
		builder.append( visible );
		builder.append( ", " );
		
		if( xmltvId != null ) {
			builder.append( "xmltvId=" );
			builder.append( xmltvId );
			builder.append( ", " );
		}
		
		if( defaultAuth != null ) {
			builder.append( "defaultAuth=" );
			builder.append( defaultAuth );
			builder.append( ", " );
		}
		
		if( programs != null ) {
			builder.append( "programs=" );
			builder.append( programs );
		}
		
		builder.append( "]" );
		
		return builder.toString();
	}
	
}
