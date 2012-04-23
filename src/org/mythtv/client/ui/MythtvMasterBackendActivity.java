/*
 * Copyright (C) 2008 Jeffrey Sharkey, http://jsharkey.org/ This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mythtv.client.ui;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.mythtv.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Show a list of all libraries found on local wifi network. Should have refresh
 * button easly accessibly, and also detect wifi issues.
 */
public class MythtvMasterBackendActivity extends Activity implements ServiceListener {

	private final static String TAG = MythtvMasterBackendActivity.class.getSimpleName();
	
	public final static String MYTHTV_MASTER_BACKEND_TYPE = "_mythtvbackend-master._tcp.local.";
	public final static String HOSTNAME = "mythandroid";
	
	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;
	private BackendService backend;

	public ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected( ComponentName className, IBinder service ) {
			backend = ( (BackendService.BackendBinder) service ).getService();
		}

		public void onServiceDisconnected( ComponentName className ) {
			backend = null;
		}
	};

	// this screen will run a network query of all libraries
	// upon selection it will try authenticating with that library, and launch
	// the pairing activity if failed

	protected void startProbe() throws Exception {
		Log.v( TAG, "startProbe : enter" );
		
		if( zeroConf != null ) {
			this.stopProbe();
		}
		
		adapter.known.clear();
		adapter.notifyDataSetChanged();

		// figure out our wifi address, otherwise bail
		WifiManager wifi = (WifiManager) this.getSystemService( Context.WIFI_SERVICE );

		WifiInfo wifiinfo = wifi.getConnectionInfo();
		int intaddr = wifiinfo.getIpAddress();

		byte[] byteaddr = new byte[] { (byte) ( intaddr & 0xff ), (byte) ( intaddr >> 8 & 0xff ), (byte) ( intaddr >> 16 & 0xff ), (byte) ( intaddr >> 24 & 0xff ) };
		InetAddress addr = InetAddress.getByAddress( byteaddr );
		Log.d( TAG, String.format( "found intaddr=%d, addr=%s", intaddr, addr.toString() ) );
		
		// start multicast lock
		mLock = wifi.createMulticastLock( "Mythtv Master Backend Remote lock" );
		mLock.setReferenceCounted( true );
		mLock.acquire();

		zeroConf = JmDNS.create( addr, addr.getHostName() );
		zeroConf.addServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );

		Log.v( TAG, "startProbe : exit" );
	}

	protected void stopProbe() throws IOException {
		Log.v( TAG, "stopProbe : enter" );

		zeroConf.removeServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );
		zeroConf.close();
		zeroConf = null;

		mLock.release();
		mLock = null;

		Log.v( TAG, "stopProbe : exit" );
	}

	public static JmDNS getZeroConf() {
		Log.v( TAG, "getZeroConf : enter" );
		Log.v( TAG, "getZeroConf : exit" );
		return zeroConf;
	}

	public void serviceAdded( ServiceEvent event ) {
		Log.v( TAG, "serviceAdded : enter" );

		// someone is yelling about their mythtv master backend service
		// go figure out what their ip address is
		final String name = event.getName();

		// trigger delayed gui event
		// needs to be delayed because jmdns hasnt parsed txt info yet
		Log.w( TAG, String.format( "serviceAdded(event=\n%s\n)", event.toString() ) );

		String address = String.format( "%s", name );
		resultsUpdated.sendMessageDelayed( Message.obtain( resultsUpdated, -1, address ), DELAY );

		Log.v( TAG, "serviceAdded : exit" );
	}

	public void serviceRemoved( ServiceEvent event ) {
		Log.v( TAG, "serviceRemoved : enter" );
		
		Log.w( TAG, String.format( "serviceRemoved(event=\n%s\n)", event.toString() ) );
		
		Log.v( TAG, "serviceRemoved : exit" );
	}

	public void serviceResolved( ServiceEvent event ) {
		Log.v( TAG, "serviceResolved : enter" );
		
		Log.w( TAG, String.format( "serviceResolved(event=\n%s\n)", event.toString() ) );
		
		Log.v( TAG, "serviceResolved : exit" );
	}

	public final static int DONE = 3;

	public Handler resultsUpdated = new Handler() {
		@Override
		public void handleMessage( Message msg ) {
			Log.v( TAG, "resultsUpdated.handleMessage : enter" );
			
			if( msg.obj != null ) {
				adapter.notifyFound( (String) msg.obj );
			}
			
			adapter.notifyDataSetChanged();

			Log.v( TAG, "resultsUpdated.handleMessage : exit" );
		}
	};

	public final static int DELAY = 500;

	protected ListView list;
	protected LibraryAdapter adapter;

	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );

		super.onStart();
		this.bindService( new Intent( this, BackendService.class ), connection, Context.BIND_AUTO_CREATE );

		Log.v( TAG, "onStart : exit" );
	}

	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );

		super.onStop();
		this.unbindService( connection );

		Log.v( TAG, "onStop : exit" );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		Log.v( TAG, "onActivityResult : enter" );

		// someone thinks they are ready to pair with us
		if( resultCode == Activity.RESULT_CANCELED ) {
			Log.v( TAG, "onActivityResult : exit, cancelled" );
			
			return;
		}

		String address = data.getStringExtra( BackendService.EXTRA_ADDRESS );
//		String library = data.getStringExtra( BackendService.EXTRA_LIBRARY );
//		String code = data.getStringExtra( BackendService.EXTRA_CODE );
//		Log.d( TAG, String.format( "onActivityResult with address=%s, library=%s, code=%s and resultcode=%d", address, library, code, resultCode ) );

		try {

			// check to see if we can actually authenticate against the library
			backend.setMasterBackend( address );

			// if successful, then throw back to controlactivity
			// LibraryActivity.this.startActivity(new
			// Intent(LibraryActivity.this,
			// ControlActivity.class));
			MythtvMasterBackendActivity.this.setResult( Activity.RESULT_OK );
			MythtvMasterBackendActivity.this.finish();

			Log.v( TAG, "onActivityResult : exit" );
		} catch( Exception e ) {

			Log.e( TAG, "onActivityResult : error", e );

			// we probably had a pairing issue, so start the pairing server and
			// wait for trigger
//			Intent intent = new Intent( MythtvMasterBackendActivity.this, PairingActivity.class );
//			intent.putExtra( BackendService.EXTRA_ADDRESS, address );
//			intent.putExtra( BackendService.EXTRA_LIBRARY, library );
//			MythtvMasterBackendActivity.this.startActivityForResult( intent, 1 );
		}

	}

	// there should be a single backend service that holds current session
	// information
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.gen_list );

		this.adapter = new LibraryAdapter( this );

		this.list = (ListView) this.findViewById( android.R.id.list );
		this.list.addHeaderView( adapter.footerView, null, false );
		this.list.setAdapter( adapter );

		this.list.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				// read ip/port from caption if present
				// pass off to backend to try creating pairing session

//				if( backend == null ) {
//					return;
//				}

				String caption = ( (TextView) view.findViewById( android.R.id.text2 ) ).getText().toString();
				String[] split = caption.split( "-" );
				if( split.length < 2 ) {
					return;
				}

//				String address = split[ 0 ].trim();
//				String library = split[ 1 ].trim();

				// push off fake result to try login
				// this will start the pairing process if needed
				Intent shell = new Intent();
//				shell.putExtra( BackendService.EXTRA_ADDRESS, address );
//				shell.putExtra( BackendService.EXTRA_LIBRARY, library );
				onActivityResult( -1, Activity.RESULT_OK, shell );
			}
		} );

		try {
			this.startProbe();
		} catch( Exception e ) {
			Log.e( TAG, String.format( "onCreate : error", e ) );
		}

		Log.v( TAG, "onCreate : exit" );
	}

//	@Override
//	public boolean onCreateOptionsMenu( Menu menu ) {
//		super.onCreateOptionsMenu( menu );
//
//		MenuItem refresh = menu.add( R.string.library_menu_refresh );
//		refresh.setIcon( android.R.drawable.ic_menu_rotate );
//		refresh.setOnMenuItemClickListener( new OnMenuItemClickListener() {
//			public boolean onMenuItemClick( MenuItem item ) {
//
//				try {
//					startProbe();
//				} catch( Exception e ) {
//					Log.d( TAG, String.format( "onCreate Error: %s", e.getMessage() ) );
//				}
//				return true;
//			}
//		} );
//
//		MenuItem manual = menu.add( R.string.library_menu_manual );
//		manual.setIcon( android.R.drawable.ic_menu_manage );
//		manual.setOnMenuItemClickListener( new OnMenuItemClickListener() {
//			public boolean onMenuItemClick( MenuItem item ) {
//
//				LayoutInflater inflater = (LayoutInflater) MythtvMasterBackendActivity.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//				final View view = inflater.inflate( R.layout.dia_text, null );
//				final TextView address = (TextView) view.findViewById( android.R.id.text1 );
//				final TextView code = (TextView) view.findViewById( android.R.id.text2 );
//
//				new AlertDialog.Builder( MythtvMasterBackendActivity.this ).setView( view ).setPositiveButton( R.string.library_manual_pos, new DialogInterface.OnClickListener() {
//							public void onClick( DialogInterface dialog, int which ) {
//								// try connecting to this specific ip address
//								Intent shell = new Intent();
//								shell.putExtra( BackendService.EXTRA_ADDRESS, address.getText().toString() );
//								shell.putExtra( BackendService.EXTRA_LIBRARY, "0" );
//								shell.putExtra( BackendService.EXTRA_CODE, code.getText().toString() );
//								onActivityResult( -1, Activity.RESULT_OK, shell );
//
//							}
//						} ).setNegativeButton( R.string.library_manual_neg, new DialogInterface.OnClickListener() {
//							public void onClick( DialogInterface dialog, int which ) {
//							}
//						} ).create().show();
//
//				return true;
//			}
//		} );
//
//		MenuItem forget = menu.add( R.string.library_menu_forget );
//		forget.setIcon( android.R.drawable.ic_menu_delete );
//		forget.setOnMenuItemClickListener( new OnMenuItemClickListener() {
//			public boolean onMenuItemClick( MenuItem item ) {
//
//				new AlertDialog.Builder( MythtvMasterBackendActivity.this ).setMessage( R.string.library_forget )
//						.setPositiveButton( R.string.library_forget_pos, new DialogInterface.OnClickListener() {
//							public void onClick( DialogInterface dialog, int which ) {
//								backend.pairdb.deleteAll();
//
//							}
//						} ).setNegativeButton( R.string.library_forget_neg, new DialogInterface.OnClickListener() {
//							public void onClick( DialogInterface dialog, int which ) {
//							}
//						} ).create().show();
//
//				return true;
//			}
//		} );
//
//		return true;
//	}

	public class LibraryAdapter extends BaseAdapter {

		protected Context context;
		protected LayoutInflater inflater;

		public View footerView;

		protected List<String> known = new LinkedList<String>();

		public LibraryAdapter( Context context ) {
			this.context = context;
			this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			this.footerView = inflater.inflate( R.layout.item_network, null, false );

		}

		public void notifyFound( String address ) {
			known.add( address );
		}

		public Object getItem( int position ) {
			return known.get( position );
		}

		public boolean hasStableIds() {
			return true;
		}

		public int getCount() {
			return known.size();
		}

		public long getItemId( int position ) {
			return position;
		}

		public View getView( int position, View convertView, ViewGroup parent ) {

			if( convertView == null )
				convertView = inflater.inflate( android.R.layout.simple_list_item_2, parent, false );

			try {

				// fetch the dns txt record to get library info
				final String dnsName = (String) this.getItem( position );
				Log.d( TAG, String.format( "DNS Name: %s", dnsName ) );
				ServiceInfo serviceInfo = getZeroConf().getServiceInfo( MYTHTV_MASTER_BACKEND_TYPE, dnsName );
				if( serviceInfo == null ) {
					throw new IllegalStateException( "ServiceInfo is null" );
				}
				final String title = serviceInfo.getPropertyString( "CtlN" );
				final String addr = serviceInfo.getHostAddress();
				final String library = String.format( "%s - %s", addr, serviceInfo.getPropertyString( "DbId" ) );

				Log.d( TAG, String.format( "ZeroConf Server: %s", serviceInfo.getServer() ) );
				Log.d( TAG, String.format( "ZeroConf Port: %s", serviceInfo.getPort() ) );
				Log.d( TAG, String.format( "ZeroConf Title: %s", title ) );
				Log.d( TAG, String.format( "ZeroConf Library: %s", library ) );

				( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( title );
				( (TextView) convertView.findViewById( android.R.id.text2 ) ).setText( library );

			} catch( Exception e ) {
				Log.d( TAG, String.format( "onCreate Error: %s", e.getMessage() ) );
				( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( "" );
				( (TextView) convertView.findViewById( android.R.id.text2 ) ).setText( "" );
			}

			return convertView;
		}

	}

}
