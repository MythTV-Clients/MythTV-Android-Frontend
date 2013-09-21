/**
 * 
 */
package org.mythtv.service.content;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.content.model.LiveStreamInfo;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.service.content.v26.LiveStreamHelperV26;
import org.mythtv.service.content.v27.LiveStreamHelperV27;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetLiveStreamTask extends AsyncTask<Integer, Void, Boolean> {

	private static final String TAG = GetLiveStreamTask.class.getSimpleName();
	
	private final LiveStreamDaoHelper mLiveStreamDaoHelper = LiveStreamDaoHelper.getInstance();

	private final Context mContext;
	private final Program mProgram;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	private long mLiveStreamInfoId;
	
	public interface TaskFinishedListener {

		void onGetLiveStreamTaskStarted();
		 
	    void onGetLiveStreamTaskFinished( LiveStreamInfo result );
	    
	}

	public GetLiveStreamTask( Context context, Program program, LocationProfile locationProfile, TaskFinishedListener listener ) {
		this.mContext = context;
		this.mProgram = program;
		this.mLocationProfile = locationProfile;
		this.listener = listener;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
    protected void onPreExecute() {
		Log.d( TAG, "onPreExecute : enter" );
		
        listener.onGetLiveStreamTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground( Integer... params ) {
		Log.d( TAG, "doInBackground : enter" );

		if( null == mContext ) {
			throw new IllegalArgumentException( "Context is required" );
		}

		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == mProgram ) {
			throw new IllegalArgumentException( "Program is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "Param is required" );
		}

		mLiveStreamInfoId = params[ 0 ];
		Log.d( TAG, "doInBackground : mLiveStreamInfoId=" + mLiveStreamInfoId );
		
		boolean updated = false;

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :

				updated = LiveStreamHelperV26.getInstance().update( mContext, mLocationProfile, mLiveStreamInfoId, mProgram.getChannelInfo().getChannelId(), mProgram.getStartTime() );

				break;

		case v027 :

				updated = LiveStreamHelperV27.getInstance().update( mContext, mLocationProfile, mLiveStreamInfoId, mProgram.getChannelInfo().getChannelId(), mProgram.getStartTime() );

				break;

		default :

				updated = LiveStreamHelperV26.getInstance().update( mContext, mLocationProfile, mLiveStreamInfoId, mProgram.getChannelInfo().getChannelId(), mProgram.getStartTime() );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return updated;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( Boolean result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		if( result.booleanValue() ) {
			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByLiveStreamId( mContext, mLocationProfile, mLiveStreamInfoId );
		
			listener.onGetLiveStreamTaskFinished( liveStreamInfo );
		}
		
		Log.d( TAG, "onPostExecute : exit" );
	}

}
