/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.frontends;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.mythtv.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author pot8oe
 *
 */
public class FrontendsFragment extends AbstractFrontendFragment implements ServiceListener, OnItemSelectedListener {

	private final static String TAG = FrontendsFragment.class.getSimpleName();


	// private OnFrontendListener listener = null;
	private FrontendAdapter adapter = null;
	// private boolean persistentSelection = false;
	

	private static List<Frontend> frontends = new ArrayList<Frontend>();
	private static Frontend selectedFrontend;
	private static TelephonyManager sTelManager;

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	private static final String MYTHTV_FRONTEND_TYPE = "_mythfrontend._tcp.local.";
	private static final String HOSTNAME = "mythandroid";

	// public FrontendsFragment() {
	// this( false );
	// }
	//
	// public FrontendsFragment( boolean persistentSelection ) {
	// this.persistentSelection = persistentSelection;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate : enter");

		super.onCreate(savedInstanceState);

		// setRetainInstance( true );
		sTelManager = (TelephonyManager)super.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		sTelManager.listen(new PhoneStateListener(){

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				
				if(state == TelephonyManager.CALL_STATE_RINGING){
					
					final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
					final Frontend fe = frontends.getSelectedFrontend();
					
					if( null==fe ) return;
					
					new SendMessageTask().execute(fe.getUrl(), "Incoming Call From: " + incomingNumber);
				}
				
				super.onCallStateChanged(state, incomingNumber);
			}}, PhoneStateListener.LISTEN_CALL_STATE);
		
		adapter = new FrontendAdapter(getActivity(), R.layout.frontend_row, frontends);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Log.v(TAG, "onCreate : exit");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view =  inflater.inflate(R.layout.fragment_frontends, container, false);
		
		Spinner spinner = (Spinner)view.findViewById(R.id.spinner_frontends);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
		return view;
	}

	@Override
	public void onResume() {
		Log.v(TAG, "onResume : enter");

		super.onResume();

		if (frontends.isEmpty()) {
			scanForFrontends();
		}

		Log.v(TAG, "onResume : exit");
	}

	// @Override
	// public void onActivityCreated( Bundle state ) {
	// Log.v( TAG, "onActivityCreated : enter" );
	//
	// super.onActivityCreated( state );
	//
	// restoreState( state );
	//
	// if( persistentSelection ) {
	// enablePersistentSelection();
	// }
	//
	// Log.v( TAG, "onActivityCreated : exit" );
	// }


	// public void setOnFrontendListener( OnFrontendListener listener ) {
	// Log.v( TAG, "setOnFrontendListener : enter" );
	//
	// this.listener = listener;
	//
	// Log.v( TAG, "setOnFrontendListener : exit" );
	// }

	// public interface OnFrontendListener {
	// void onFrontendSelected( List<Frontend> frontend );
	// }
	
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1,
			int arg2, long arg3) {
		Log.d(TAG, "Frontend Spinner Item Selected");
		
		//leave if we don't have frontends, should never happen
		if(frontends.size() <= 0){
			Log.e(TAG, "Frontend selected but no frontends in ArrayList");
			return;
		}
		
		//set selected frontend
		if(arg2 >= 0 && arg2 < frontends.size()){
			selectedFrontend = frontends.get(arg2);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Log.d(TAG, "Frontend Spinner Item Nothing Selected");
	}
	

	// ***************************************
	// JMDNS ServiceListener methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
	 */
	@SuppressWarnings("deprecation")
	public void serviceAdded(ServiceEvent event) {
		Log.v(TAG, "serviceAdded : enter");

		Log.v(TAG,
				"serviceAdded : "
						+ event.getDNS()
								.getServiceInfo(event.getType(),
										event.getName()).toString());

		final String hostname = event.getDNS()
				.getServiceInfo(event.getType(), event.getName())
				.getInet4Address().getHostAddress();
		final int port = event.getDNS()
				.getServiceInfo(event.getType(), event.getName()).getPort();
		Log.v(TAG, "serviceAdded : masterbackend="
				+ ("http://" + hostname + ":" + port + "/"));

		// Dont' do both adds
		final Frontend fe = new Frontend(event.getName(), "http://" + hostname + ":"
				+ port + "/");
		
		
		this.getActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
				//frontends.add(fe);
				adapter.add(fe);
			}});
		
		Log.v(TAG, "serviceAdded : exit");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	public void serviceRemoved(ServiceEvent event) {
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
	public void serviceResolved(ServiceEvent event) {
		// Log.v( TAG, "serviceResolved : enter" );
		//
		// Log.v( TAG, "serviceResolved : event=" + event.toString() );
		//
		// Log.v( TAG, "serviceResolved : exit" );
	}

	// internal helpers

	/**
	 * 
	 */
	public void scanForFrontends() {
		Log.v(TAG, "scanForFrontends : enter");

		new ScanFrontendsTask().execute();

		Log.v(TAG, "scanForFrontends : exit");
	}
	
	/**
	 * 
	 * @return
	 */
	public Frontend getSelectedFrontend(){
		return selectedFrontend;
	}

	private class ScanFrontendsTask extends AsyncTask<Void, Void, Void> {

		private Exception e = null;

		@Override
		protected Void doInBackground(Void... params) {
			Log.v(TAG, "doInBackground : enter");

			try {
				Log.v(TAG, "doInBackground : startProbe");

				startProbe();
			} catch (Exception e) {
				Log.v(TAG, "doInBackground : error");

				this.e = e;
			}

			Log.v(TAG, "doInBackground : exit");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.v(TAG, "onPostExecute : enter");

			if (null != e) {
				Log.e(TAG, "error getting programs", e);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle(getActivity().getString(
						R.string.frontends_scan_error_title));
				builder.setNeutralButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
							}

						});
				builder.setMessage(getActivity().getString(
						R.string.frontends_scan_error_message));
				builder.show();
			}

			Log.v(TAG, "onPostExecute : exit");
		}
	}

	/**
	 * @throws IOException
	 */
	private void startProbe() throws IOException {
		Log.v(TAG, "startProbe : enter");

		if (zeroConf != null) {
			stopProbe();
		}

		// figure out our wifi address, otherwise bail
		WifiManager wifi = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);

		WifiInfo wifiinfo = wifi.getConnectionInfo();
		int intaddr = wifiinfo.getIpAddress();

		byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff),
				(byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
				(byte) (intaddr >> 24 & 0xff) };
		InetAddress addr = InetAddress.getByAddress(byteaddr);
		Log.d(TAG, "startProbe : wifi address=" + addr.toString());

		// start multicast lock
		mLock = wifi.createMulticastLock("mythtv_lock");
		mLock.setReferenceCounted(true);
		mLock.acquire();

		zeroConf = JmDNS.create(addr, HOSTNAME);
		zeroConf.addServiceListener(MYTHTV_FRONTEND_TYPE, this);

		Log.v(TAG, "startProbe : exit");
	}

	/**
	 * @throws IOException
	 */
	private void stopProbe() throws IOException {
		Log.v(TAG, "stopProbe : enter");

		zeroConf.removeServiceListener(MYTHTV_FRONTEND_TYPE, this);
		zeroConf.close();
		zeroConf = null;

		mLock.release();
		mLock = null;

		Log.v(TAG, "stopProbe : exit");
	}

	private class FrontendAdapter extends ArrayAdapter<Frontend> {

		private final String TAG = FrontendAdapter.class.getSimpleName();

		private int layoutResourceId;
		private List<Frontend> frontends = null;

		FrontendAdapter(Context context, int layoutResourceId,
				List<Frontend> frontends) {
			super(context, layoutResourceId, frontends);
			Log.v(TAG, "initialize : enter");

			this.layoutResourceId = layoutResourceId;
			this.frontends = frontends;

			Log.v(TAG, "initialize : exit");
		}

//		 @Override
//		 public int getCount() {
//			 return frontends.size();
//		 }
//		
//		 @Override
//		 public Frontend getItem( int position ) {
//			 return frontends.get( position );
//		 }
//		
//		 @Override
//		 public long getItemId( int position ) {
//			 return position;
//		 }
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getFrontendView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getFrontendView(position, convertView, parent);
		}
		
		
		
		private View getFrontendView(int position, View convertView, ViewGroup parent){
			Log.v(TAG, "getFrontendView : enter");
			
			View row = convertView;
			FrontendHolder holder = null;

			if (row == null) {
				Log.v(TAG, "getFrontendView : row is null");

				LayoutInflater inflater = getActivity().getLayoutInflater();

				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new FrontendHolder();
				holder.name = (TextView) row.findViewById(R.id.frontend_name);
				holder.url = (TextView) row.findViewById(R.id.frontend_url);
				
				row.setTag(holder);
			} else {
				holder = (FrontendHolder) row.getTag();
			}

			Frontend frontend = frontends.get(position);

			holder.name.setText(frontend.getName());
			holder.url.setText(frontend.getUrl());

			Log.v(TAG, "getFrontendView : exit");
			return row;
		}
		
		

		class FrontendHolder {
			TextView name;
			TextView url;
		}

	}

}
