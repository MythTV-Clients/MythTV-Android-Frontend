/**
 * 
 */
package org.mythtv.service.myth;

import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.myth.model.StorageGroupDirectory;
import org.mythtv.service.myth.v26.StorageGroupHelperV26;
import org.mythtv.service.myth.v27.StorageGroupHelperV27;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetStorageGroupsTask extends AsyncTask<String, Void, List<StorageGroupDirectory>> {

	private static final String TAG = GetStorageGroupsTask.class.getSimpleName();
	
	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetStorageGroupsTaskStarted();
		 
	    void onGetStorageGroupsTaskFinished( List<StorageGroupDirectory> result );
	    
	}

	public GetStorageGroupsTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
		this.mContext = context;
		this.mLocationProfile = locationProfile;
		this.listener = listener;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
    protected void onPreExecute() {
		Log.d( TAG, "onPreExecute : enter" );
		
        listener.onGetStorageGroupsTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<StorageGroupDirectory> doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );

		if( null == mContext ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "String params are required" );
		}

		List<StorageGroupDirectory> storageGroupDirectoryList = null;
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		String groupName = params[ 0 ];
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				storageGroupDirectoryList = StorageGroupHelperV26.getInstance().process( mContext, mLocationProfile, groupName );
				
				break;
			case v027 :

				storageGroupDirectoryList = StorageGroupHelperV27.getInstance().process( mContext, mLocationProfile, groupName );
				
				break;
				
			default :
				
				storageGroupDirectoryList = StorageGroupHelperV26.getInstance().process( mContext, mLocationProfile, groupName );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return storageGroupDirectoryList;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( List<StorageGroupDirectory> result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		listener.onGetStorageGroupsTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}

}
