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
package org.mythtv.db.status.model;

import java.io.Serializable;

import org.mythtv.db.myth.model.Storage;

/**
 * @author Daniel Frey
 *
 */
public class MachineInfo implements Serializable {

	private static final long serialVersionUID = -9032423198741844863L;

	private Storage storage;
	private Load load;
	private Guide guide;

	public MachineInfo() { }

	/**
	 * @return the storage
	 */
	public Storage getStorage() {
		return storage;
	}

	/**
	 * @param storage the storage to set
	 */
	public void setStorage( Storage storage ) {
		this.storage = storage;
	}

	/**
	 * @return the load
	 */
	public Load getLoad() {
		return load;
	}

	/**
	 * @param load the load to set
	 */
	public void setLoad( Load load ) {
		this.load = load;
	}

	/**
	 * @return the guide
	 */
	public Guide getGuide() {
		return guide;
	}

	/**
	 * @param guide the guide to set
	 */
	public void setGuide( Guide guide ) {
		this.guide = guide;
	}
	
}
