/**
 * 
 */
package org.mythtv.client.service.delegate;

import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.RunningServiceHelper;

/**
 * @author dmfrey
 *
 */
public abstract class AbstractBaseService {

	protected EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	protected LiveStreamDaoHelper mLiveStreamDaoHelper = LiveStreamDaoHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	protected MenuHelper mMenuHelper = MenuHelper.getInstance();
	protected ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance(); 
	protected RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	protected RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();

}
