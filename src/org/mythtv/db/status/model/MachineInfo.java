/**
 * 
 */
package org.mythtv.db.status.model;

import org.mythtv.db.myth.model.Storage;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "MachineInfo" )
public class MachineInfo {

	@Element( name = "Storage" )
	private Storage storage;
	
	@Element( name = "Load" )
	private Load load;
	
	@Element( name = "Guide" )
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
