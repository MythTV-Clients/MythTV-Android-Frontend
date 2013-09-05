/**
 * 
 */
package org.mythtv.db.status.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class JobQueue implements Serializable {

	private static final long serialVersionUID = -1427226129356973913L;

	private int count;
	
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
