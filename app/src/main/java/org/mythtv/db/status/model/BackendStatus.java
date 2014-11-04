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
package org.mythtv.db.status.model;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.mythtv.db.frontends.model.Frontends;

/**
 * @author Daniel Frey
 *
 */
public class BackendStatus implements Serializable {

	private static final long serialVersionUID = -5827686268837625642L;

	private String version;
	private DateTime isoDate;
	private int protocolVersion;
	private String time;
	private String date;
	private Encoders encoders;
	private Scheduled scheduled;
	private Frontends frontends;
	private Backends backends;
	private JobQueue jobQueue;
	private MachineInfo machineInfo;
	private Miscellaneous miscellaneous;
	
	public BackendStatus() { }

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion( String version ) {
		this.version = version;
	}

	/**
	 * @return the isoDate
	 */
	public DateTime getIsoDate() {
		return isoDate;
	}

	/**
	 * @param isoDate the isoDate to set
	 */
	public void setIsoDate( DateTime isoDate ) {
		this.isoDate = isoDate;
	}

	/**
	 * @return the protocolVersion
	 */
	public int getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	public void setProtocolVersion( int protocolVersion ) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime( String time ) {
		this.time = time;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate( String date ) {
		this.date = date;
	}

	/**
	 * @return the encoders
	 */
	public Encoders getEncoders() {
		return encoders;
	}

	/**
	 * @param encoders the encoders to set
	 */
	public void setEncoders( Encoders encoders ) {
		this.encoders = encoders;
	}

	/**
	 * @return the scheduled
	 */
	public Scheduled getScheduled() {
		return scheduled;
	}

	/**
	 * @param scheduled the scheduled to set
	 */
	public void setScheduled( Scheduled scheduled ) {
		this.scheduled = scheduled;
	}

	/**
	 * @return the frontends
	 */
	public Frontends getFrontends() {
		return frontends;
	}

	/**
	 * @param frontends the frontends to set
	 */
	public void setFrontends( Frontends frontends ) {
		this.frontends = frontends;
	}

	/**
	 * @return the backends
	 */
	public Backends getBackends() {
		return backends;
	}

	/**
	 * @param backends the backends to set
	 */
	public void setBackends( Backends backends ) {
		this.backends = backends;
	}

	/**
	 * @return the jobQueue
	 */
	public JobQueue getJobQueue() {
		return jobQueue;
	}

	/**
	 * @param jobQueue the jobQueue to set
	 */
	public void setJobQueue( JobQueue jobQueue ) {
		this.jobQueue = jobQueue;
	}

	/**
	 * @return the machineInfo
	 */
	public MachineInfo getMachineInfo() {
		return machineInfo;
	}

	/**
	 * @param machineInfo the machineInfo to set
	 */
	public void setMachineInfo( MachineInfo machineInfo ) {
		this.machineInfo = machineInfo;
	}

	/**
	 * @return the miscellaneous
	 */
	public Miscellaneous getMiscellaneous() {
		return miscellaneous;
	}

	/**
	 * @param miscellaneous the miscellaneous to set
	 */
	public void setMiscellaneous( Miscellaneous miscellaneous ) {
		this.miscellaneous = miscellaneous;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Status [" );
		if( version != null ) {
			builder.append( "version=" );
			builder.append( version );
			builder.append( ", " );
		}
		if( isoDate != null ) {
			builder.append( "isoDate=" );
			builder.append( isoDate );
			builder.append( ", " );
		}
		builder.append( "protocolVersion=" );
		builder.append( protocolVersion );
		builder.append( ", " );
		if( time != null ) {
			builder.append( "time=" );
			builder.append( time );
			builder.append( ", " );
		}
		if( date != null ) {
			builder.append( "date=" );
			builder.append( date );
			builder.append( ", " );
		}
		if( encoders != null ) {
			builder.append( "encoders=" );
			builder.append( encoders );
			builder.append( ", " );
		}
		if( scheduled != null ) {
			builder.append( "scheduled=" );
			builder.append( scheduled );
			builder.append( ", " );
		}
		if( frontends != null ) {
			builder.append( "frontends=" );
			builder.append( frontends );
			builder.append( ", " );
		}
		if( backends != null ) {
			builder.append( "backends=" );
			builder.append( backends );
			builder.append( ", " );
		}
		if( jobQueue != null ) {
			builder.append( "jobQueue=" );
			builder.append( jobQueue );
			builder.append( ", " );
		}
		if( machineInfo != null ) {
			builder.append( "machineInfo=" );
			builder.append( machineInfo );
			builder.append( ", " );
		}
		if( miscellaneous != null ) {
			builder.append( "miscellaneous=" );
			builder.append( miscellaneous );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
