/**
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivity;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.NetworkHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @author dmfrey
 * @author Thomas G. Kenny Jr
 *
 */
public class ProfileRow implements Row {
	
	private static final String TAG = ProfileRow.class.getSimpleName();
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private NetworkHelper mNetworkHelper = NetworkHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private LocationProfile mLocationProfile;
	private OnClickListener mManageProfilesOnClick;
	
	//mProfileToggleCheckChangedListener
	private class ProfileToggleCheckChangedListener implements CompoundButton.OnCheckedChangeListener {
		
		/* This is the view returned in getView() */
		public View convertView = null;
		
		
		/* (non-Javadoc)
		 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
		 */
		@Override
		public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
			Log.v( TAG, "onCheckedChanged : enter" );

			Log.v( TAG, "onCheckedChanged : isChecked=" + isChecked );
			if( !isChecked ) { //isChecked - false - home
				Log.v( TAG, "onCheckedChanged : home selected" );

				final LocationProfile profile = mLocationProfileDaoHelper.findSelectedHomeProfile( mContext );
				if( null == profile ) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
					builder.setTitle( R.string.location_alert_error_title );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) { }
						
					});
					builder.setMessage( R.string.location_alert_error_home_message );
					builder.show();

				} else {
					mLocationProfile = profile;
					mLocationProfileDaoHelper.setConnectedLocationProfile( mContext, mLocationProfile.getId() );

					new ConnectBackendTask( convertView ).execute( mLocationProfile );

				}
				
			} else { //ischecked - true - away
				Log.v( TAG, "onCheckedChanged : away selected" );
				
				final LocationProfile profile = mLocationProfileDaoHelper.findSelectedAwayProfile( mContext );
				if( null == profile ) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
					builder.setTitle( R.string.location_alert_error_title );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) { }
						
					});
					builder.setMessage( R.string.location_alert_error_away_message );
					builder.show();

				} else {

					mLocationProfile = profile;
					mLocationProfileDaoHelper.setConnectedLocationProfile( mContext, mLocationProfile.getId() );

					new ConnectBackendTask( convertView ).execute( mLocationProfile );
				}
				
			}

			Log.v( TAG, "onCheckedChanged : exit" );
		}
	};
	
	private ProfileToggleCheckChangedListener mProfileToggleCheckChangedListener = new ProfileToggleCheckChangedListener();
	
	public interface ProfileChangedListener {
        
		void onProfileChanged();
    
	}
	
	private ProfileChangedListener mProfileChangedListener;
	
	public ProfileRow( Context context, ProfileChangedListener listener, OnClickListener manageProfilesOnClick ) {
		this.mContext = context;
		
		this.mProfileChangedListener = listener;
		
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );
		
		this.mManageProfilesOnClick = manageProfilesOnClick;
		
	}
	
	/**
	 * Attempts to connect to the selected backend profile by calling into
	 * the toggle button's oncheckchanged listener with the current state
	 * of the toggle.
	 * @return
	 */
	public boolean backendConnectionUpdate(){
		
		//if the profile toggle check changed listner has been properly init in getView() we can continue
		if(null != mProfileToggleCheckChangedListener){
			
			//create view if necessary
			if(null == mProfileToggleCheckChangedListener.convertView)
				mProfileToggleCheckChangedListener.convertView = getView(null);
			
			//if it's still null bail
			if(null == mProfileToggleCheckChangedListener.convertView) return false;
			
			//get view holder so we can get the toggle
			ViewHolder holder = (ViewHolder)mProfileToggleCheckChangedListener.convertView.getTag();
			
			//fire a checkchanged event with the current toggle state
			mProfileToggleCheckChangedListener.onCheckedChanged(holder.toggle, holder.toggle.isChecked());
			
			return true;
		}else{
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_connected_profile, null );

    		holder = new ViewHolder();
    		holder.hostname = (TextView) convertView.findViewById( R.id.navigation_drawer_connected_profile_hostname );
    		holder.url = (TextView) convertView.findViewById( R.id.navigation_drawer_connected_profile_url );
    		holder.toggle = (ToggleButton) convertView.findViewById( R.id.navigation_drawer_connected_profile_toggle );
    		holder.btnMngProfiles = (ImageButton)convertView.findViewById(R.id.imagebutton_navdrawer_manage_profiles);
    		holder.btnMngProfiles.setOnClickListener(mManageProfilesOnClick);
    		
    		convertView.setTag( holder );
     			
        } else {

    		holder = (ViewHolder) convertView.getTag();
        }
        
        mProfileToggleCheckChangedListener.convertView = convertView;
        holder.toggle.setOnCheckedChangeListener(mProfileToggleCheckChangedListener);
        
        if( null != mLocationProfile ) {
        	holder.hostname.setText( mLocationProfile.getHostname() );
        	holder.url.setText( mLocationProfile.getUrl() );
        	
        	switch( mLocationProfile.getType() ) {
        		case HOME :
        			holder.toggle.setChecked( false );

        			break;
        		
        		case AWAY :
        			holder.toggle.setChecked( true );
        			
        			break;
        	}
        	
        } else {
        	holder.hostname.setText( "Not Connected" );
        }
        
        return convertView;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return TopLevelRowType.PROFILE_ROW.ordinal();
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getTitle()
	 */
	@Override
	public String getTitle() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getFragment()
	 */
	@Override
	public String getFragment() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#isImplemented()
	 */
	@Override
	public boolean isImplemented() {
		return true;
	}

	private static class ViewHolder {
		
		TextView hostname;
		TextView url;
		ToggleButton toggle;
		ImageButton btnMngProfiles;
		
	}
	
	private class ConnectBackendTask extends AsyncTask<LocationProfile, Void, Boolean> {

		private ProgressDialog mProgressDialog;
		
		private LocationProfile profile;
		private View view;
		
		public ConnectBackendTask( View view ) {
			this.view = view;
		}
		
		 /* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog( mContext );
			mProgressDialog.setTitle( "Connecting..." );
			mProgressDialog.setMessage( "Attempting to connection to master backend" );
			mProgressDialog.setCancelable( false );
			mProgressDialog.setIndeterminate( true );
			mProgressDialog.show();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground( LocationProfile... profiles ) {
			Log.v( TAG, "ConnectBackendTask.doInBackground : enter" );
			
			profile = profiles[ 0 ];
			
			return mNetworkHelper.isMasterBackendConnected( mContext, profile );
		}

		@Override
		protected void onPostExecute( Boolean result ) {
			Log.v( TAG, "ConnectBackendTask.onPostExecute : enter" );
			
			ViewHolder holder = (ViewHolder) view.getTag();

			if( result.booleanValue() ) {
				Log.i( TAG, "ConnectBackendTask.onPostExecute : master backend connected" );

				mLocationProfileDaoHelper.setConnectedLocationProfile( mContext, (long) profile.getId() );
				mLocationProfile = profile;
				
				holder.hostname.setText( profile.getHostname() );
				holder.url.setText( profile.getUrl() );
			} else {
				Log.i( TAG, "ConnectBackendTask.onPostExecute : master backend NOT connected" );
				
				mLocationProfileDaoHelper.resetConnectedProfiles( mContext );
			
	        	holder.hostname.setText( "Not Connected" );
	        	holder.url.setText( "" );

				mLocationProfile = null;
			}

			mProgressDialog.dismiss();
			
			mProfileChangedListener.onProfileChanged();
			
			Log.v( TAG, "ConnectBackendTask.onPostExecute : exit" );
		}
		
	}
	
}
