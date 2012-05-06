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

package org.mythtv.client.ui.preferences;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.db.DatabaseHelper;
import org.mythtv.client.db.MythtvDatabaseManager;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythtvPreferencesActivity extends PreferenceActivity implements ServiceListener {

	private static final String TAG = AbstractMythtvPreferencesActivity.class.getSimpleName();
	
	public static final String MYTHTV_SHARED_PREFERENCES_ID = "mythtv.preferences";
	public static final String MYTHTV_DEFAULT_HOME_PROFILE_ID = "default-home-profile";
	public static final String MYTHTV_DEFAULT_HOME_PLAYBACK_PROFILE_ID = "default-home-playback-profile";
	public static final String MYTHTV_DEFAULT_AWAY_PROFILE_ID = "default-away-profile";
	public static final String MYTHTV_DEFAULT_AWAY_PLAYBACK_PROFILE_ID = "default-away-playback-profile";
	
	private static final String MYTHTV_MASTER_BACKEND_TYPE = "_mythbackend-master._tcp.local.";
	private static final String HOSTNAME = "mythandroid";

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	private MainApplication mainApplication;
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings( "deprecation" )
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );

		mainApplication = (MainApplication) getApplicationContext();
		
		if( android.os.Build.VERSION.SDK_INT > 9 ) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy( policy );
		}
		
		getPreferenceManager().setSharedPreferencesName( MYTHTV_SHARED_PREFERENCES_ID );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();

		setupPreferences( this );
		
		Log.v( TAG, "onResume : exit" );
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

		LocationProfile profile = new LocationProfile();
		profile.setId( -1 );
		profile.setType( LocationType.HOME );
		profile.setName( event.getName() );
		profile.setUrl( "http://" + hostname + ":" + port + "/" );
		
		showLocationProfileEditDialog( this, profile );

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
	
	@SuppressWarnings( "deprecation" )
	private void setupPreferences( final PreferenceActivity context ) {
		Log.v( TAG, "setupPreferences : enter" );
		
		Preference addHomeLocationProfilePreference = new Preference( context );
		addHomeLocationProfilePreference.setTitle( context.getString( R.string.preference_home_profiles_add ) );
		addHomeLocationProfilePreference.setSummary( context.getString( R.string.preference_home_profiles_add_summary ) );
		addHomeLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				
				LocationProfile profile = new LocationProfile();
				profile.setId( -1 );
				profile.setType( LocationType.HOME );
				
				showLocationProfileEditDialog( context, profile );
					
				return false;
			}

		});

		Preference scanHomeLocationProfilePreference = new Preference( context );
		scanHomeLocationProfilePreference.setTitle( context.getString( R.string.preference_home_profiles_scan ) );
		scanHomeLocationProfilePreference.setSummary( context.getString( R.string.preference_home_profiles_scan_summary ) );
		scanHomeLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				
				try {
					startProbe();
				} catch( IOException e ) {
					Log.e( TAG, "scanHomeLocationProfilePreference : error", e );
					
					AlertDialog.Builder builder = new AlertDialog.Builder( context );
					builder.setTitle( context.getString( R.string.preference_home_profiles_scan_error_title ) );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) {
							// TODO Auto-generated method stub

						}
					} );
					builder.setMessage( context.getString( R.string.preference_home_profiles_scan_error_message ) );
					builder.show();
				}
					
				return false;
			}

		});

		Preference addAwayLocationProfilePreference = new Preference( context );
		addAwayLocationProfilePreference.setTitle( context.getString( R.string.preference_away_profiles_add ) );
		addAwayLocationProfilePreference.setSummary( context.getString( R.string.preference_away_profiles_add_summary ) );
		addAwayLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				
				LocationProfile profile = new LocationProfile();
				profile.setId( -1 );
				profile.setType( LocationType.AWAY );
				
				showLocationProfileEditDialog( context, profile );
					
				return false;
			}

		});

		MythtvDatabaseManager db = new MythtvDatabaseManager( context );
		
		PreferenceCategory homeProfilesPreferenceCategory = (PreferenceCategory) findPreference( "preference_home_profiles" );
		homeProfilesPreferenceCategory.removeAll();
		homeProfilesPreferenceCategory.addPreference( scanHomeLocationProfilePreference );
		homeProfilesPreferenceCategory.addPreference( addHomeLocationProfilePreference );

		List<LocationProfile> homeLocationProfiles = db.fetchHomeLocationProfiles();
		if( null != homeLocationProfiles && !homeLocationProfiles.isEmpty() ) {
			Log.v( TAG, "setupPreferences : setting Home Location Profiles" );
			
	        for( int i = 0; i < homeLocationProfiles.size(); i++ ) {
	        	LocationProfile profile = homeLocationProfiles.get( i );
	        	homeProfilesPreferenceCategory.addPreference( createLocationProfilePreference( context, profile ) );
	        }
	        
		}
		
		PreferenceCategory awayProfilesPreferenceCategory = (PreferenceCategory) findPreference( "preference_away_profiles" );
		awayProfilesPreferenceCategory.removeAll();
		awayProfilesPreferenceCategory.addPreference( addAwayLocationProfilePreference );

		List<LocationProfile> awayLocationProfiles = db.fetchAwayLocationProfiles();
		if( null != awayLocationProfiles && !awayLocationProfiles.isEmpty() ) {
			Log.v( TAG, "setupPreferences : setting Away Location Profiles" );
			
	        for( int i = 0; i < awayLocationProfiles.size(); i++ ) {
	        	LocationProfile profile = awayLocationProfiles.get( i );
	        	awayProfilesPreferenceCategory.addPreference( createLocationProfilePreference( context, profile ) );
	        }
	        
		}
		
		LocationProfile selectedHomeLocationProfile = db.fetchSelectedHomeLocationProfile();
		if( null != selectedHomeLocationProfile ) {
			Log.v( TAG, "setupPreferences : setting selected Home Location Profile" );
			
			Preference preference = findPreference( "preference_home_profiles_default_id" );
			preference.setDefaultValue( selectedHomeLocationProfile.getId() );
			preference.setSummary( selectedHomeLocationProfile.getName() );
		}
		
		LocationProfile selectedAwayLocationProfile = db.fetchSelectedHomeLocationProfile();
		if( null != selectedAwayLocationProfile ) {
			Log.v( TAG, "setupPreferences : setting selected Away Location Profile" );

			Preference preference = findPreference( "preference_away_profiles_default_id" );
			preference.setDefaultValue( selectedAwayLocationProfile.getId() );
			preference.setSummary( selectedAwayLocationProfile.getName() );
		}
		
		Log.v( TAG, "setupPreferences : exit" );
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
		WifiManager wifi = (WifiManager) this.getSystemService( Context.WIFI_SERVICE );

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
		zeroConf.addServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );

		Log.v( TAG, "startProbe : exit" );
	}

	/**
	 * @throws IOException
	 */
	private void stopProbe() throws IOException {
		Log.v( TAG, "stopProbe : enter" );

		zeroConf.removeServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );
		zeroConf.close();
		zeroConf = null;

		mLock.release();
		mLock = null;

		Log.v( TAG, "stopProbe : exit" );
	}

	/**
	 * @param context
	 * @param profile
	 * @return
	 */
	private static Preference createLocationProfilePreference( final Activity context, LocationProfile profile ) {
		Log.v( TAG, "createLocationProfilePreference : enter" );
		
		Preference preference = new Preference( context );
		preference.setKey( "" + profile.getId() );
		preference.setTitle( profile.getName() );
		preference.setDefaultValue( profile.getUrl() );
		preference.setEnabled(true);
		preference.setSummary( profile.getUrl() );
		preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				
				MythtvDatabaseManager db = new MythtvDatabaseManager( context );
				LocationProfile profile = db.fetchLocationProfile( Integer.parseInt( preference.getKey() ) );
				
				// show location editor
				showLocationProfileEditDialog( context, profile );
					
				return false;
			}

		});

		Log.v( TAG, "createLocationProfilePreference : exit" );
		return preference;
	}

	/**
	 * @param context
	 * @param profile
	 */
	private static void showLocationProfileEditDialog( Context context, LocationProfile profile ) {
		Log.v( TAG, "showLocationProfileEditDialog : enter" );

		Intent intent = new Intent( context, LocationProfileEditor.class);

		// put extra information is needed
		if( null != profile ) {
			intent.putExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_ID, profile.getId() );
			intent.putExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_TYPE, profile.getType().name() );
			intent.putExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_NAME, profile.getName() );
			intent.putExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_URL, profile.getUrl() );
			intent.putExtra( DatabaseHelper.TABLE_LOCATION_PROFILE_SELECTED, ( profile.isSelected() ? 1 : 0 ) );
		}

		// start activity
		context.startActivity( intent );
		
		Log.v( TAG, "showLocationProfileEditDialog : exit" );
	}

}
