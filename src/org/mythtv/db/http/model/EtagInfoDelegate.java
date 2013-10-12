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
package org.mythtv.db.http.model;

import org.joda.time.DateTime;
import org.mythtv.services.api.ETagInfo;

/**
 * @author dmfrey
 *
 */
public class EtagInfoDelegate extends ETagInfo {

	private long id;
	private String endpoint;
	private String value;
	private int dataId;
	private DateTime date;
	private String masterHostname;
	private DateTime lastModified;
	
	private boolean newETag;

	/**
	 * @param eTag
	 */
	public EtagInfoDelegate( String eTag ) {
		super( eTag );
		
		this.value = eTag;
	}

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
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return getETag();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue( String value ) {
		setETag( value );
		
		value = getETag();
	}

	/**
	 * @return the dataId
	 */
	public int getDataId() {
		return dataId;
	}

	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId( int dataId ) {
		this.dataId = dataId;
	}

	/**
	 * @return the date
	 */
	public DateTime getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate( DateTime date ) {
		this.date = date;
	}

	/**
	 * @return the masterHostname
	 */
	public String getMasterHostname() {
		return masterHostname;
	}

	/**
	 * @param masterHostname the masterHostname to set
	 */
	public void setMasterHostname( String masterHostname ) {
		this.masterHostname = masterHostname;
	}

	/**
	 * @return the lastModified
	 */
	public DateTime getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified( DateTime lastModified ) {
		this.lastModified = lastModified;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.ETagInfo#isNewDataEtag()
	 */
	public boolean isNewDataEtag() {
		return newETag;
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.services.api.ETagInfo#isEmptyEtag()
	 */
	public boolean isEmptyEtag(){
		return null == value || "".equals( value );
	}
	
	/**
	 * @return
	 */
	public static EtagInfoDelegate createEmptyETag() {
		return new EtagInfoDelegate( null );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EtagInfoDelegate [id=");
		builder.append(id);
		builder.append(", ");
		if (endpoint != null) {
			builder.append("endpoint=");
			builder.append(endpoint);
			builder.append(", ");
		}
		if (getETag() != null) {
			builder.append("ETAG=");
			builder.append(getETag());
			builder.append(", ");
		}
		if (value != null) {
			builder.append("value=");
			builder.append(value);
			builder.append(", ");
		}
		builder.append("dataId=");
		builder.append(dataId);
		builder.append(", ");
		if (date != null) {
			builder.append("date=");
			builder.append(date);
			builder.append(", ");
		}
		if (masterHostname != null) {
			builder.append("masterHostname=");
			builder.append(masterHostname);
			builder.append(", ");
		}
		if (lastModified != null) {
			builder.append("lastModified=");
			builder.append(lastModified);
		}
		builder.append("]");
		return builder.toString();
	}

}
