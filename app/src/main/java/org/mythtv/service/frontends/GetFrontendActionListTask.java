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
package org.mythtv.service.frontends;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.frontends.model.Action;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetFrontendActionListTask extends AsyncTask<String, Void, List<Action>> {

	private static final String  TAG = GetFrontendActionListTask.class.getSimpleName();

	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetFrontendActionListTaskStarted();
		 
	    void onGetFrontendActionListTaskFinished(List<Action> result );
	    
	}

	public GetFrontendActionListTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
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
		
        listener.onGetFrontendActionListTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<Action> doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );
		
		if( null == mContext ) {
			throw new IllegalArgumentException( "Context is required" );
		}

		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "listener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "Params is required" );
		}
		
		String url = params[ 0 ];
		
		if( !NetworkHelper.getInstance().isFrontendConnected( mContext, mLocationProfile, url ) ) {
			Log.w( TAG, "process : Frontend @ '" + url + "' is unreachable" );
			
			return null;
		}

		List<Action> actions = new ArrayList<Action>();
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				org.mythtv.services.api.v025.MythServicesTemplate mythServicesTemplateV25 = (org.mythtv.services.api.v025.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV25 ) {
					ResponseEntity<org.mythtv.services.api.v025.beans.FrontendActionList> actionsV25 = mythServicesTemplateV25.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
					if( actionsV25.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != actionsV25.getBody().getActionList() && !actionsV25.getBody().getActionList().isEmpty() ) {

							for( String versionAction: actionsV25.getBody().getActionList().keySet() ) {
								Action action = new Action();
								action.setKey( versionAction );
								action.setValue( actionsV25.getBody().getActionList().get( versionAction ) );

								actions.add( action );
							}

						}

					}
				}
				
				break;
				
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV26 ) {
					ResponseEntity<org.mythtv.services.api.v026.beans.FrontendActionList> actionsV26 = mythServicesTemplateV26.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
					if( actionsV26.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != actionsV26.getBody().getActionList() && !actionsV26.getBody().getActionList().isEmpty() ) {

							for(  String versionAction: actionsV26.getBody().getActionList().keySet() ) {
								Action action = new Action();
								action.setKey( versionAction );
								action.setValue( actionsV26.getBody().getActionList().get( versionAction ) );

								actions.add( action );
							}

						}

					}
				}
				
				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV27 ) {
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
				}
				
				break;
			case v028 :

				org.mythtv.services.api.v028.MythServicesTemplate mythServicesTemplateV28 = (org.mythtv.services.api.v028.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV28 ) {
					ResponseEntity<org.mythtv.services.api.v028.beans.FrontendActionList> actionsV28 = mythServicesTemplateV28.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
					if( actionsV28.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != actionsV28.getBody().getActionList() && !actionsV28.getBody().getActionList().isEmpty() ) {

							for( String versionAction: actionsV28.getBody().getActionList().keySet() ) {
								Action action = new Action();
								action.setKey( versionAction );
								action.setValue( actionsV28.getBody().getActionList().get( versionAction ) );

								actions.add( action );
							}

						}

					}
				}
				
				break;
				
			default :
				
				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27Default = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV27Default ) {
					ResponseEntity<org.mythtv.services.api.v027.beans.FrontendActionList> actionsV27Default = mythServicesTemplateV27Default.frontendOperations().getActionList( url, ETagInfo.createEmptyETag() );
					if( actionsV27Default.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != actionsV27Default.getBody().getActionList() && !actionsV27Default.getBody().getActionList().isEmpty() ) {

							for( String versionAction: actionsV27Default.getBody().getActionList().keySet() ) {
								Action action = new Action();
								action.setKey( versionAction );
								action.setValue( actionsV27Default.getBody().getActionList().get( versionAction ) );

								actions.add( action );
							}

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
