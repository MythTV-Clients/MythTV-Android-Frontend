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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
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
import org.mythtv.client.ui.AbstractMythFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FrontendsDashboardFragment extends AbstractMythFragment implements ServiceListener {

	private final static String TAG = FrontendsDashboardFragment.class.getSimpleName();

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	private static final String MYTHTV_FRONTEND_TYPE = "_mythfrontend._tcp.local.";
	private static final String HOSTNAME = "mythandroid";

	private List<Frontend> frontends = new ArrayList<Frontend>();
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		if( android.os.Build.VERSION.SDK_INT > 9 ) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy( policy );
		}

		View root = inflater.inflate( R.layout.fragment_frontends_dashboard, container, false );

		try {
			startProbe();
			
//			setListAdapter( new FrontendAdapter( getActivity(), frontends ) );
		} catch( IOException e ) {
			Log.e( TAG, "scanHomeLocationProfilePreference : error", e );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
			builder.setTitle( getActivity().getString( R.string.frontends_scan_error_title ) );
			builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) {
					// TODO Auto-generated method stub

				}
			} );
			builder.setMessage( getActivity().getString( R.string.frontends_scan_error_message ) );
			builder.show();
		}
		
		Log.v( TAG, "onCreateView : exit" );
		return root;
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
		
		final String hostname = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getInet4Address().getHostAddress();
		final int port = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getPort();
		Log.v( TAG, "serviceAdded : masterbackend=" + ( "http://" + hostname + ":" + port + "/" ) );

		frontends.add( new Frontend( event.getName(), "http://" + hostname + ":" + port + "/" ) );
		
		Log.v( TAG, "serviceAdded : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	public void serviceRemoved( ServiceEvent event ) {
		Log.v( TAG, "serviceRemoved : enter" );

		Log.v( TAG, "serviceRemoved : event=" + event.toString() );

		Log.v( TAG, "serviceRemoved : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.jmdns.ServiceListener#serviceResolved(javax.jmdns.ServiceEvent)
	 */
	public void serviceResolved( ServiceEvent event ) {
		Log.v( TAG, "serviceResolved : enter" );

		Log.v( TAG, "serviceResolved : event=" + event.toString() );

		Log.v( TAG, "serviceResolved : exit" );
	}


	// internal helpers
	
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

		byte[] byteaddr = new byte[] { (byte) ( intaddr & 0xff ), (byte) ( intaddr >> 8 & 0xff ), (byte) ( intaddr >> 16 & 0xff ), (byte) ( intaddr >> 24 & 0xff ) };
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

	private class FrontendAdapter extends BaseAdapter {

		Context context;
		List<Frontend> frontends;
		
		public FrontendAdapter( Context context, List<Frontend> frontends ) {
			Log.v( TAG, "FrontendAdapter.initialize : enter" );
			
			this.context = context;
			this.frontends = frontends;

			Log.v( TAG, "FrontendAdapter.initialize : exit" );
		}
		
		@Override
		public int getCount() {
			Log.v( TAG, "FrontendAdapter.getCount : enter" );
			Log.d( TAG, "FrontendAdapter.getCount : count=" + frontends.size() );
			Log.v( TAG, "FrontendAdapter.getCount : exit" );
			return frontends.size();
		}

		@Override
		public Object getItem( int position ) {
			Log.v( TAG, "FrontendAdapter.getItem : enter" );
			Log.d( TAG, "FrontendAdapter.getItem : frontend=" + frontends.get( position ) );
			Log.v( TAG, "FrontendAdapter.getItem : exit" );
			return frontends.get( position );
		}

		@Override
		public long getItemId( int position ) {
			Log.v( TAG, "FrontendAdapter.getItemId : enter" );
			Log.d( TAG, "FrontendAdapter.getItemId : position=" + position );
			Log.v( TAG, "FrontendAdapter.getItemId : exit" );
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "FrontendAdapter.getView : enter" );

			Frontend frontend = frontends.get( position );
			
			Log.v( TAG, "FrontendAdapter.getView : exit" );
			return null;
		}

	}

}
