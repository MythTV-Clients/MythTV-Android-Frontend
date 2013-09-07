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
package org.mythtv.client.ui;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.dvr.GuideFragment;
import org.mythtv.client.ui.dvr.RecordingRulesFragment;
import org.mythtv.client.ui.dvr.RecordingsParentFragment;
import org.mythtv.client.ui.dvr.UpcomingPagerFragment;
import org.mythtv.client.ui.frontends.Frontend;
import org.mythtv.client.ui.frontends.MythmoteActivity;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivity;
import org.mythtv.db.channel.ChannelEndpoint;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.frontends.SendMessageTask;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.RunningServiceHelper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 
 * @author Thomas G. Kenny Jr
 * 
 */
public class MainMenuFragment extends AbstractMythFragment implements ServiceListener, OnItemSelectedListener {

	private final static String TAG = MainMenuFragment.class.getSimpleName();

	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();

	private ChannelDownloadReceiver channelDownloadReceiver = new ChannelDownloadReceiver();

	public interface ContentFragmentRequestedListener {
	
		public void OnFragmentRequested( int fragmentId, String fragmentClassName );

		public void OnFragmentRequested( int fragmentId, Fragment fragment );
	
	}

	private static TelephonyManager sTelManager;
	private static List<Frontend> frontends = new ArrayList<Frontend>();
	private static Frontend selectedFrontend;
	private static final String MYTHTV_FRONTEND_TYPE = "_mythfrontend._tcp.local.";
	private static final String HOSTNAME = "mythandroid";
	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;
	private static boolean isAway = false;

	private boolean isConnected = false;
	private FrontendAdapter adapter = null;
	private ContentFragmentRequestedListener mContentFragmentRequestedListener;

	private LocationProfile mLocationProfile;
	
	private OnCheckedChangeListener homeAwayCheckedChanged = new OnCheckedChangeListener() {

		/* (non-Javadoc)
		 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
		 */
		@Override
		public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {

			// set if profile is home/away
			isAway = isChecked;

			if( !isChecked ) { // isChecked - false - home

				if( null == mLocationProfileDaoHelper.findSelectedHomeProfile( getActivity() ) ) {

					AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
					builder.setTitle( R.string.location_alert_error_title );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) {
						}

					} );
					builder.setMessage( R.string.location_alert_error_home_message );
					builder.show();

				} else {
					// LocationProfile profile =
					// mLocationProfileDaoHelper.findSelectedHomeProfile(
					// getActivity() );
					// mLocationProfileDaoHelper.setConnectedLocationProfile(
					// getActivity(), (long) profile.getId() );

					// here i think we need to re-start ourself and do not need
					// to fire this intent
					// This intent was used in LocationDashboardFragment to
					// start HomeActivity
					getActivity().startService( new Intent( MythtvService.ACTION_CONNECT ) );
				}

			} else { // ischecked - true - away

				if( null == mLocationProfileDaoHelper.findSelectedAwayProfile( getActivity() ) ) {

					AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
					builder.setTitle( R.string.location_alert_error_title );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) {
						}

					} );
					builder.setMessage( R.string.location_alert_error_away_message );
					builder.show();

				} else {
					// LocationProfile profile =
					// mLocationProfileDaoHelper.findSelectedAwayProfile(
					// getActivity() );
					// mLocationProfileDaoHelper.setConnectedLocationProfile(
					// getActivity(), (long) profile.getId() );

					// here i think we need to re-start ourself and do not need
					// to fire this intent
					// This intent was used in LocationDashboardFragment to
					// start AwayActivity
					getActivity().startService( new Intent( MythtvService.ACTION_CONNECT ) );
				}
			}

			// show/hide frontend selection
			LinearLayout linearLayoutFrontends = (LinearLayout) getActivity().findViewById(
					R.id.linear_layout_frontend_spinner );
			if( null != linearLayoutFrontends ) {
				linearLayoutFrontends.setVisibility( isChecked ? View.GONE : View.VISIBLE );
			}
		}

	};

	private OnClickListener preferenceButtonOnClick = new OnClickListener() {
		@Override
		public void onClick( View v ) {
			if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
				startActivity( new Intent( getActivity(), MythtvPreferenceActivity.class ) );
			} else {
				startActivity( new Intent( getActivity(), MythtvPreferenceActivity.class ) );
			}
		}
	};

	private OnClickListener mythmoteButtonOnClick = new OnClickListener() {
		@Override
		public void onClick( View v ) {
			if( NetworkHelper.getInstance().isNetworkConnected( getActivity() )
					&& !getActivity().getClass().equals( MythmoteActivity.class ) ) {
				startActivity( new Intent( getActivity(), MythmoteActivity.class ) );
			}
		}
	};

	private OnClickListener activityButtonOnClick = new OnClickListener() {
		@Override
		public void onClick( View v ) {

			// get button text
			Button btn = (Button) v;
			String btnTxt = btn.getText().toString();

			// check if we're connected to a backend
			if( !isConnected ) {
				Toast.makeText( getActivity(), "No connection to backend or no backend profile is selected.",
						Toast.LENGTH_SHORT ).show();
				return;
			}

			// find button action based on the display string
			if( getString( R.string.btn_guide ).equals( btnTxt ) ) {
				requestContentFragment( R.id.fragment_dvr_guide, GuideFragment.class.getName() );
			} else if( getString( R.string.btn_music ).equals( btnTxt ) ) {
				Toast.makeText( getActivity(), "Music - Coming Soon!", Toast.LENGTH_SHORT ).show();
			} else if( getString( R.string.btn_pictures ).equals( btnTxt ) ) {
				Toast.makeText( getActivity(), "Pictures - Coming Soon!", Toast.LENGTH_SHORT ).show();
			} else if( getString( R.string.btn_recording_rules ).equals( btnTxt ) ) {
				requestContentFragment( R.id.fragment_dvr_recording_rules, RecordingRulesFragment.class.getName() );
			} else if( getString( R.string.btn_recordings ).equals( btnTxt ) ) {
				requestContentFragment( R.id.fragment_dvr_recordings_parent, RecordingsParentFragment.class.getName() );
			} else if( getString( R.string.btn_upcoming ).equals( btnTxt ) ) {
				requestContentFragment( R.id.fragment_dvr_upcoming, UpcomingPagerFragment.class.getName() );
			} else if( getString( R.string.btn_videos ).equals( btnTxt ) ) {
				Toast.makeText( getActivity(), "Video - Coming Soon!", Toast.LENGTH_SHORT ).show();
			}
		}
	};

	/**
	 * Returns the list of known frontends
	 * 
	 * @return
	 */
	public static List<Frontend> GetFrontends() {
		return frontends;
	}

	/**
	 * 
	 * @return
	 */
	public static Frontend getSelectedFrontend() {
		return selectedFrontend;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isAway() {
		return isAway;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		adapter = new FrontendAdapter( getActivity(), R.layout.frontend_row, MainMenuFragment.GetFrontends() );

		sTelManager = (TelephonyManager) getActivity().getSystemService( Context.TELEPHONY_SERVICE );
		sTelManager.listen( new PhoneStateListener() {

			/* (non-Javadoc)
			 * @see android.telephony.PhoneStateListener#onCallStateChanged(int, java.lang.String)
			 */
			@Override
			public void onCallStateChanged( int state, String incomingNumber ) {

				if( state == TelephonyManager.CALL_STATE_RINGING ) {

					final Frontend fe = selectedFrontend;

					if( null == fe )
						return;

					SendMessageTask sendMessageTask = new SendMessageTask( mLocationProfile );
					sendMessageTask.execute( fe.getUrl(), "Incoming Call From: " + incomingNumber );
				}

				super.onCallStateChanged( state, incomingNumber );
			}
		}, PhoneStateListener.LISTEN_CALL_STATE );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter channelDownloadFilter = new IntentFilter();
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_COMPLETE );
		getActivity().registerReceiver( channelDownloadReceiver, channelDownloadFilter );

		Log.v( TAG, "onStart : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != channelDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( channelDownloadReceiver );
				// channelDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		View mainMenuView = inflater.inflate( R.layout.fragment_main_menu, container, false );

		ToggleButton toggleIsAway = (ToggleButton) mainMenuView.findViewById( R.id.toggleButtonIsAway );
		toggleIsAway.setOnCheckedChangeListener( this.homeAwayCheckedChanged );

		// set version string - we only do this to prepend the 'V'
		TextView tView = (TextView) mainMenuView.findViewById( R.id.textview_mainmenu_version );
		tView.setText( "V" + this.getString( R.string.app_version ) );

		// set frontends spinner
		Spinner spinner = (Spinner) mainMenuView.findViewById( R.id.spinner_frontends );
		spinner.setAdapter( adapter );
		spinner.setOnItemSelectedListener( this );

		// set preference button click listener
		ImageButton prefButton = (ImageButton) mainMenuView.findViewById( R.id.imagebutton_main_menu_preferences );
		prefButton.setOnClickListener( this.preferenceButtonOnClick );

		// set mythmote button click listener
		ImageButton mmButton = (ImageButton) mainMenuView.findViewById( R.id.imagebutton_main_menu_mythmote );
		mmButton.setOnClickListener( this.mythmoteButtonOnClick );

		// set activities button click listeners
		Button aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_guide );
		aBtn.setOnClickListener( this.activityButtonOnClick );
		aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_music );
		aBtn.setOnClickListener( this.activityButtonOnClick );
		aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_pictures );
		aBtn.setOnClickListener( this.activityButtonOnClick );
		aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_recording_rules );
		aBtn.setOnClickListener( this.activityButtonOnClick );
		aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_recordings );
		aBtn.setOnClickListener( this.activityButtonOnClick );
		aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_upcoming );
		aBtn.setOnClickListener( this.activityButtonOnClick );
		aBtn = (Button) mainMenuView.findViewById( R.id.button_main_menu_videos );
		aBtn.setOnClickListener( this.activityButtonOnClick );

		return mainMenuView;
	}

	@Override
	public void onResume() {
		super.onResume();

		// get connected location profile
		LocationProfile profile = this.mLocationProfileDaoHelper.findConnectedProfile( this.getActivity() );

		// check if we have a connected profile
		if( null == profile ) {

			// auto connected to the first profile found
			List<LocationProfile> profiles = mLocationProfileDaoHelper.findAll( getActivity() );
			if( profiles.size() > 0 ) {
				profile = profiles.get( 0 );

				// mLocationProfileDaoHelper.setConnectedLocationProfile(
				// getActivity(), profile.getId() );
				isConnected = true;
			} else {
				isConnected = false;
			}
		} else {
			isConnected = true;
		}

		// get away/home toggle
		ToggleButton toggleIsAway = (ToggleButton) this.getActivity().findViewById( R.id.toggleButtonIsAway );
		toggleIsAway.setOnCheckedChangeListener( this.homeAwayCheckedChanged );

		// set away/home toggle based on the connected location profile
		if( null != profile && null != toggleIsAway ) {
			toggleIsAway.setChecked( profile.getType() == LocationProfile.LocationType.AWAY );
		}

		// if frontend list is empty start a scan.
		if( frontends.isEmpty() ) {
			scanForFrontends();
		}

		if( isConnected ) {

			DateTime etag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), profile,
					ChannelEndpoint.GET_CHANNEL_INFO_LIST.name(), "" );
			if( null != etag ) {

				DateTime now = DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) );
				if( now.getMillis() - etag.getMillis() > 86400000 ) {
					if( !mRunningServiceHelper.isServiceRunning( getActivity(),
							"org.mythtv.service.channel.ChannelDownloadService" ) ) {
						getActivity().startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
					}
				}

			} else {
				if( !mRunningServiceHelper.isServiceRunning( getActivity(),
						"org.mythtv.service.channel.ChannelDownloadService" ) ) {
					getActivity().startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
				}
			}

		}
	}

	@Override
	public void onItemSelected( AdapterView<?> arg0, View arg1, int arg2, long arg3 ) {
		Log.d( TAG, "Frontend Spinner Item Selected" );

		// leave if we don't have frontends, should never happen
		if( MainMenuFragment.GetFrontends().size() <= 0 ) {
			Log.e( TAG, "Frontend selected but no frontends in ArrayList" );
			return;
		}

		// set selected frontend
		if( arg2 >= 0 && arg2 < MainMenuFragment.GetFrontends().size() ) {
			selectedFrontend = MainMenuFragment.GetFrontends().get( arg2 );
		}
	}

	@Override
	public void onNothingSelected( AdapterView<?> arg0 ) {
		Log.d( TAG, "Frontend Spinner Item Nothing Selected" );
	}

	/**
	 * 
	 */
	public void scanForFrontends() {
		Log.v( TAG, "scanForFrontends : enter" );

		new ScanFrontendsTask().execute();

		Log.v( TAG, "scanForFrontends : exit" );
	}

	private class ScanFrontendsTask extends AsyncTask<Void, Void, Void> {

		private Exception e = null;

		@Override
		protected Void doInBackground( Void... params ) {
			Log.v( TAG, "doInBackground : enter" );

			try {
				Log.v( TAG, "doInBackground : startProbe" );

				startProbe();
			} catch( Exception e ) {
				Log.v( TAG, "doInBackground : error" );

				this.e = e;
			}

			Log.v( TAG, "doInBackground : exit" );
			return null;
		}

		@Override
		protected void onPostExecute( Void result ) {
			Log.v( TAG, "onPostExecute : enter" );

			if( null != e && null != getActivity() ) {
				Log.e( TAG, "error getting programs", e );

				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
				if( null != builder ) {
					builder.setTitle( getString( R.string.frontends_scan_error_title ) );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) {

						}

					} );
					builder.setMessage( getString( R.string.frontends_scan_error_message ) );
					builder.show();
				}
			}

			Log.v( TAG, "onPostExecute : exit" );
		}
	}

	/**
	 * @throws IOException
	 */
	private void startProbe() throws IOException {
		Log.v( TAG, "startProbe : enter" );

		if( zeroConf != null ) {
			stopProbe();
		}

		// figure out our wifi address, otherwise bail
		WifiManager wifi = (WifiManager) getActivity().getSystemService( Context.WIFI_SERVICE );

		WifiInfo wifiinfo = wifi.getConnectionInfo();
		int intaddr = wifiinfo.getIpAddress();

		byte[] byteaddr = new byte[] { (byte) ( intaddr & 0xff ), (byte) ( intaddr >> 8 & 0xff ),
				(byte) ( intaddr >> 16 & 0xff ), (byte) ( intaddr >> 24 & 0xff ) };
		InetAddress addr = InetAddress.getByAddress( byteaddr );
		Log.d( TAG, "startProbe : wifi address=" + addr.toString() );

		// start multicast lock
		mLock = wifi.createMulticastLock( "mythtv_lock" );
		mLock.setReferenceCounted( true );
		mLock.acquire();

		zeroConf = JmDNS.create( addr, HOSTNAME );
		zeroConf.addServiceListener( MYTHTV_FRONTEND_TYPE, this );

		Log.v( TAG, "startProbe : exit" );
	}

	/**
	 * @throws IOException
	 */
	private void stopProbe() throws IOException {
		Log.v( TAG, "stopProbe : enter" );

		zeroConf.removeServiceListener( MYTHTV_FRONTEND_TYPE, this );
		zeroConf.close();
		zeroConf = null;

		mLock.release();
		mLock = null;

		Log.v( TAG, "stopProbe : exit" );
	}

	/**
	 * Call this to tell the containing activity it's time to close the menu
	 * 
	 * @param fragmentId
	 * @param fragmentClassName
	 */
	private void requestContentFragment( int fragmentId, String fragmentClassName ) {
		if( null != mContentFragmentRequestedListener )
			mContentFragmentRequestedListener.OnFragmentRequested( fragmentId, fragmentClassName );
	}

	/**
	 * 
	 * @param fragment
	 */
	private void requestContentFragment( int fragmentId, Fragment fragment ) {
		if( null != mContentFragmentRequestedListener )
			mContentFragmentRequestedListener.OnFragmentRequested( fragmentId, fragment );
	}

	/**
	 * 
	 * @param listener
	 */
	public void setContentFragmentRequestedListener( ContentFragmentRequestedListener listener ) {
		mContentFragmentRequestedListener = listener;
	}

	// ***************************************
	// JMDNS ServiceListener methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
	 */
	@SuppressWarnings( "deprecation" )
	public void serviceAdded( ServiceEvent event ) {
		Log.v( TAG, "serviceAdded : enter" );

		Log.v( TAG, "serviceAdded : " + event.getDNS().getServiceInfo( event.getType(), event.getName() ).toString() );

		final String hostname = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getInet4Address()
				.getHostAddress();
		final int port = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getPort();
		Log.v( TAG, "serviceAdded : masterbackend=" + ( "http://" + hostname + ":" + port + "/" ) );

		// Dont' do both adds
		final Frontend fe = new Frontend( event.getName(), "http://" + hostname + ":" + port + "/" );

		this.getActivity().runOnUiThread( new Runnable() {

			@Override
			public void run() {
				// frontends.add(fe);
				adapter.add( fe );
			}
		} );

		Log.v( TAG, "serviceAdded : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	public void serviceRemoved( ServiceEvent event ) {
		// Log.v( TAG, "serviceRemoved : enter" );
		//
		// Log.v( TAG, "serviceRemoved : event=" + event.toString() );
		//
		// Log.v( TAG, "serviceRemoved : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.jmdns.ServiceListener#serviceResolved(javax.jmdns.ServiceEvent)
	 */
	public void serviceResolved( ServiceEvent event ) {
		// Log.v( TAG, "serviceResolved : enter" );
		//
		// Log.v( TAG, "serviceResolved : event=" + event.toString() );
		//
		// Log.v( TAG, "serviceResolved : exit" );
	}

	private class FrontendAdapter extends ArrayAdapter<Frontend> {

		private final String TAG = FrontendAdapter.class.getSimpleName();

		private int layoutResourceId;
		private List<Frontend> frontends = null;

		FrontendAdapter( Context context, int layoutResourceId, List<Frontend> frontends ) {
			super( context, layoutResourceId, frontends );
			Log.v( TAG, "initialize : enter" );

			this.layoutResourceId = layoutResourceId;
			this.frontends = frontends;

			Log.v( TAG, "initialize : exit" );
		}

		// @Override
		// public int getCount() {
		// return frontends.size();
		// }
		//
		// @Override
		// public Frontend getItem( int position ) {
		// return frontends.get( position );
		// }
		//
		// @Override
		// public long getItemId( int position ) {
		// return position;
		// }

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			return getFrontendView( position, convertView, parent );
		}

		@Override
		public View getDropDownView( int position, View convertView, ViewGroup parent ) {
			return getFrontendView( position, convertView, parent );
		}

		private View getFrontendView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "getFrontendView : enter" );

			View row = convertView;
			FrontendHolder holder = null;

			if( row == null ) {
				Log.v( TAG, "getFrontendView : row is null" );

				LayoutInflater inflater = getActivity().getLayoutInflater();

				row = inflater.inflate( layoutResourceId, parent, false );

				holder = new FrontendHolder();
				holder.name = (TextView) row.findViewById( R.id.frontend_name );
				holder.url = (TextView) row.findViewById( R.id.frontend_url );

				row.setTag( holder );
			} else {
				holder = (FrontendHolder) row.getTag();
			}

			Frontend frontend = frontends.get( position );

			holder.name.setText( frontend.getName() );
			holder.url.setText( frontend.getUrl() );

			Log.v( TAG, "getFrontendView : exit" );
			return row;
		}

		class FrontendHolder {
			TextView name;
			TextView url;
		}

	}

	private class ChannelDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {

			if( intent.getAction().equals( ChannelDownloadService.ACTION_PROGRESS ) ) {
				Log.i( TAG,
						"ProgramGuideDownloadReceiver.onReceive : progress="
								+ intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
			}

			if( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
				Log.i( TAG,
						"ProgramGuideDownloadReceiver.onReceive : "
								+ intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );

				Toast.makeText( getActivity(), "Channels Updated!", Toast.LENGTH_SHORT ).show();

				// if( !mRunningServiceHelper.isServiceRunning(
				// "org.mythtv.service.guide.ProgramGuideDownloadService" ) ) {
				// startService( new Intent(
				// ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
				// }

			}

		}

	}

}
