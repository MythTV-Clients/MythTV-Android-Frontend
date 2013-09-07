/**
 * 
 */
package org.mythtv.service.frontends;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.frontends.model.Action;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetFrontendActionListTask extends AsyncTask<String, Void, List<Action>> {

	private static final String  TAG = GetFrontendActionListTask.class.getSimpleName();

	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetFrontendActionListTaskStarted();
		 
	    void onGetFrontendActionListTaskFinished(List<Action> result );
	    
	}

	public GetFrontendActionListTask( LocationProfile locationProfile, TaskFinishedListener listener ) {
		this.mLocationProfile = locationProfile;
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
    protected void onPreExecute() {
		Log.d( TAG, "onPreExecute : enter" );
		
        listener.onGetFrontendActionListTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<Action> doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "listener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "Params is required" );
		}
		
		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		String url = params[ 0 ];
		
		List<Action> actions = new ArrayList<Action>();
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				ResponseEntity<org.mythtv.services.api.v026.beans.FrontendActionList> actionsV26 = mythServicesTemplateV26.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
				if( actionsV26.getStatusCode().equals( HttpStatus.OK ) ) {
					
					if( null != actionsV26.getBody().getActions() && !actionsV26.getBody().getActions().isEmpty() ) {
						
						for( org.mythtv.services.api.v026.beans.Action versionAction: actionsV26.getBody().getActions() ) {
							Action action = new Action();
							action.setKey( versionAction.getKey() );
							action.setValue( versionAction.getValue() );
							
							actions.add( action );
						}
						
					}
					
				}
				
				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				ResponseEntity<org.mythtv.services.api.v027.beans.FrontendActionList> actionsV27 = mythServicesTemplateV27.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
				if( actionsV27.getStatusCode().equals( HttpStatus.OK ) ) {
					
					if( null != actionsV27.getBody().getActionList() && !actionsV27.getBody().getActionList().isEmpty() ) {
						
						for( String versionAction: actionsV27.getBody().getActionList().keySet() ) {
							Action action = new Action();
							action.setKey( versionAction );
							action.setValue( actionsV27.getBody().getActionList().get( versionAction ) );
							
							actions.add( action );
						}
						
					}
					
				}
				
				break;
				
			default :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26Default = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				ResponseEntity<org.mythtv.services.api.v026.beans.FrontendActionList> actionsV26Default = mythServicesTemplateV26Default.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
				if( actionsV26Default.getStatusCode().equals( HttpStatus.OK ) ) {
					
					if( null != actionsV26Default.getBody().getActions() && !actionsV26Default.getBody().getActions().isEmpty() ) {
						
						for( org.mythtv.services.api.v026.beans.Action versionAction: actionsV26Default.getBody().getActions() ) {
							Action action = new Action();
							action.setKey( versionAction.getKey() );
							action.setValue( versionAction.getValue() );
							
							actions.add( action );
						}
						
					}
					
				}

				break;
		}
		
		Log.d( TAG, "doInBackground : exit" );
		return actions;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( List<Action> result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		listener.onGetFrontendActionListTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}
		
}
