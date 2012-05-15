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
import org.mythtv.client.ui.util.PersistentListFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FrontendsFragment extends PersistentListFragment implements ServiceListener {

	private final static String TAG = FrontendsFragment.class.getSimpleName();

	private OnFrontendListener listener = null;
	private FrontendAdapter adapter = null;
	private boolean persistentSelection = false;

	private List<Frontend> frontends = new ArrayList<Frontend>();

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	private static final String MYTHTV_FRONTEND_TYPE = "_mythfrontend._tcp.local.";
	private static final String HOSTNAME = "mythandroid";

	public FrontendsFragment() {
		this( false );
	}

	public FrontendsFragment( boolean persistentSelection ) {
		this.persistentSelection = persistentSelection;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );

		setRetainInstance( true );

		adapter = new FrontendAdapter( frontends );
		setListAdapter( adapter );
		
		Log.v( TAG, "onCreate : enter" );
	}

	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();
	    
		if( frontends.isEmpty() ) {
			scanForFrontends();
		}

		Log.v( TAG, "onResume : exit" );
	}

	@Override
	public void onActivityCreated( Bundle state ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( state );

		restoreState( state );

		if( persistentSelection ) {
			enablePersistentSelection();
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}

	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
	    
		if( null != listener ) {
			listener.onFrontendSelected( frontends );
		}

		Log.v( TAG, "onListItemClick : exit" );
	}

	public void setOnFrontendListener( OnFrontendListener listener ) {
		Log.v( TAG, "setOnFrontendListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnFrontendListener : exit" );
	}

	public interface OnFrontendListener {
		void onFrontendSelected( List<Frontend> frontend );
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
		adapter.notifyDataSetChanged();
		
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

			if( null != e ) {
				Log.e( TAG, "error getting programs", e );
				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
				builder.setTitle( getActivity().getString( R.string.frontends_scan_error_title ) );
				builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

					public void onClick( DialogInterface dialog, int which ) { }
					
				});
				builder.setMessage( getActivity().getString( R.string.frontends_scan_error_message ) );
				builder.show();
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
		
		private final String TAG = FrontendAdapter.class.getSimpleName();
		
		private List<Frontend> frontends = null;

		FrontendAdapter( List<Frontend> frontends ) {
			super();
			Log.v( TAG, "initialize : enter" );

			this.frontends = frontends;

			Log.v( TAG, "initialize : exit" );
		}

		@Override
		public int getCount() {
			return frontends.size();
		}

		@Override
		public Frontend getItem( int position ) {
			return frontends.get( position );
		}

		@Override
		public long getItemId( int position ) {
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "getView : enter" );

			View row = convertView;

			if( row == null ) {
				Log.v( TAG, "getView : row is null" );

				LayoutInflater inflater = getActivity().getLayoutInflater();

				row = inflater.inflate( R.layout.frontend_row, parent, false );
			}

			Frontend frontend = getItem( position );

			TextView name = (TextView) row.findViewById( R.id.frontend_name );
			TextView url = (TextView) row.findViewById( R.id.frontend_url );

			name.setText( frontend.getName() );
			url.setText( frontend.getUrl() );
			
			Log.v( TAG, "getView : exit" );
			return row;
		}

	}

}
