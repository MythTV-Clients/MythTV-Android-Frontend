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
