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
package org.mythtv.client.ui.frontends;

import org.joda.time.DateTime;

/**
 * @author Daniel Frey
 * @author Thomas G. Kenny Jr
 *
 */
public class Frontend {

	private static final String EXCESSIVE_NAME = "Mythfrontend on";
	
	private long id;
	private String name;
	private String url;
	private boolean available;
	private String masterHostname;
	private DateTime lastModifiedDate;
	
	public Frontend() { }

	public Frontend( String name, String url ) {
		super();
		this.name = name;
		this.url = url;
	}

	/**
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId( long id ) {
		this.id = id;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl( String url ) {
		this.url = url;
	}

	/**
	 * @return
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * @param available
	 */
	public void setAvailable( boolean available ) {
		this.available = available;
	}

	/**
	 * @return
	 */
	public String getMasterHostname() {
		return masterHostname;
	}

	/**
	 * @param masterHostname
	 */
	public void setMasterHostname(String masterHostname) {
		this.masterHostname = masterHostname;
	}

	/**
	 * @return
	 */
	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 */
	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Frontend [id=");
		builder.append(id);
		builder.append(", ");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (url != null) {
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		builder.append("available=");
		builder.append(available);
		builder.append(", ");
		if (masterHostname != null) {
			builder.append("masterHostname=");
			builder.append(masterHostname);
			builder.append(", ");
		}
		if (lastModifiedDate != null) {
			builder.append("lastModifiedDate=");
			builder.append(lastModifiedDate);
		}
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Returns the frontend's stripper name
	 * @return
	 */
	public String getNameStripped(){
		if(null == this.name) return this.name;
		return this.name.replace(EXCESSIVE_NAME, "");
	}
	
}
