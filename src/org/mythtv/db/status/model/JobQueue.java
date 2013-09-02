/**
 * 
 */
package org.mythtv.db.status.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "JobQueue" )
public class JobQueue {

	@Attribute( required = false )
	private int count;
	
	@ElementList( inline = true, required = false )
	private List<Job> jobs;
	
	public JobQueue() { }

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount( int count ) {
		this.count = count;
	}

	/**
	 * @return the jobs
	 */
	public List<Job> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public void setJobs( List<Job> jobs ) {
		this.jobs = jobs;
	}
	
}
