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
package org.mythtv.client.ui.preferences;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EventListener;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.preferences.PlaybackProfileConstants;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
@TargetApi( 11 )
public class MythtvPreferenceActivityHC extends PreferenceActivity {

	private static final String TAG = MythtvPreferenceActivityHC.class.getSimpleName();
	
	private static LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private static PlaybackProfileDaoHelper mPlaybackProfileDaoHelper = PlaybackProfileDaoHelper.getInstance();
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onBuildHeaders(java.util.List)
	 */
	@Override
	public void onBuildHeaders( List<Header> target ) {
		Log.v( TAG, "onBuildHeaders : enter" );

		loadHeadersFromResource( R.xml.mythtv_preference_headers, target );

		Log.v( TAG, "onBuildHeaders : exit" );
	}

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );
		actionBar.setTitle( R.string.preferences_title );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
			case android.R.id.home:
				finish();
//				// app icon in action bar clicked; go home
//				Intent intent = new Intent( this, LocationActivity.class );
//				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				startActivity( intent );
				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	private abstract static class BasePreferenceFragment extends PreferenceFragment {

		protected static final String TAG = BasePreferenceFragment.class.getSimpleName();

		protected interface LocationProfileChangedEventListener extends EventListener {

			public void defaultLocationProfileChanged();
			
		}

		protected interface PlaybackProfileChangedEventListener extends EventListener {

			public void defaultPlaybackProfileChanged();
			
		}

		/**
		 * @param context
		 * @param profile
		 * @return
		 */
		protected static Preference createLocationProfilePreference( final Context context, LocationProfile profile ) {
			Log.v( TAG, "createLocationProfilePreference : enter" );
			
			Preference preference = new Preference( context );
			preference.setKey( "" + profile.getId() );
			preference.setTitle( profile.getName() );
			preference.setDefaultValue( profile.getUrl() );
			preference.setEnabled( true );
			preference.setSummary( profile.getUrl() );
			preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

				public boolean onPreferenceClick( Preference preference ) {
					
					LocationProfile profile = mLocationProfileDaoHelper.findOne( context, Long.parseLong( preference.getKey() ) );
					
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
		 * @return
		 */
		protected static Preference createPlaybackProfilePreference( final Context context, PlaybackProfile profile ) {
			Log.v( TAG, "createPlaybackProfilePreference : enter" );
			
			Preference preference = new Preference( context );
			preference.setKey( "" + profile.getId() );
			preference.setTitle( profile.getName() );
			preference.setDefaultValue( profile.getWidth() + "x" + profile.getHeight() );
			preference.setEnabled( true );
			preference.setSummary( profile.getWidth() + "x" + profile.getHeight() );
			preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

				public boolean onPreferenceClick( Preference preference ) {
					
					PlaybackProfile profile = mPlaybackProfileDaoHelper.findOne( context, Long.parseLong( preference.getKey() ) );
					
					// show playback profile editor
					showPlaybackProfileEditDialog( context, profile );
						
					return false;
				}

			});
			
			Log.v( TAG, "createPlaybackProfilePreference : exit" );
			return preference;
		}

		/**
		 * @param context
		 * @param profile
		 */
		protected static void showLocationProfileEditDialog( final Context context, LocationProfile profile ) {
			Log.v( TAG, "showLocationProfileEditDialog : enter" );

			Intent intent = new Intent( context, LocationProfileEditor.class);

			// put extra information is needed
			if( null != profile ) {
				intent.putExtra( LocationProfileConstants._ID, profile.getId() );
				intent.putExtra( LocationProfileConstants.FIELD_TYPE, profile.getType().name() );
				intent.putExtra( LocationProfileConstants.FIELD_NAME, profile.getName() );
				intent.putExtra( LocationProfileConstants.FIELD_URL, profile.getUrl() );
				intent.putExtra( LocationProfileConstants.FIELD_SELECTED, ( profile.isSelected() ? 1 : 0 ) );
				intent.putExtra( LocationProfileConstants.FIELD_WOL_ADDRESS, profile.getWolAddress() );
			}

			// start activity
			context.startActivity( intent );
			
			Log.v( TAG, "showLocationProfileEditDialog : exit" );
		}

		/**
		 * @param context
		 * @param profile
		 */
		protected static void showPlaybackProfileEditDialog( final Context context, PlaybackProfile profile ) {
			Log.v( TAG, "showPlaybackProfileEditDialog : enter" );

			Intent intent = new Intent( context, PlaybackProfileEditor.class);

			// put extra information is needed
			if( null != profile ) {
				intent.putExtra( PlaybackProfileConstants._ID, profile.getId() );
				intent.putExtra( PlaybackProfileConstants.FIELD_TYPE, profile.getType().name() );
				intent.putExtra( PlaybackProfileConstants.FIELD_NAME, profile.getName() );
				intent.putExtra( PlaybackProfileConstants.FIELD_WIDTH, profile.getWidth() );
				intent.putExtra( PlaybackProfileConstants.FIELD_HEIGHT, profile.getHeight() );
				intent.putExtra( PlaybackProfileConstants.FIELD_BITRATE, profile.getVideoBitrate() );
				intent.putExtra( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, profile.getAudioBitrate() );
				intent.putExtra( PlaybackProfileConstants.FIELD_SAMPLE_RATE, profile.getAudioSampleRate() );
				intent.putExtra( PlaybackProfileConstants.FIELD_SELECTED, ( profile.isSelected() ? 1 : 0 ) );
			}

			// start activity
			context.startActivity( intent );
			
			Log.v( TAG, "showPlaybackProfileEditDialog : exit" );
		}

		/**
		 * @param context
		 * @param profiles
		 * @param listener
		 */
		protected static void selectLocationProfile( final Context context, final List<LocationProfile> profiles, final LocationType type, final LocationProfileChangedEventListener listener ) {
			Log.v( TAG, "selectLocationProfile : enter" );

			final String[] names = new String[ profiles.size() ];
			final long[] ids = new long[ profiles.size() ];
			
			for( int i = 0; i < profiles.size(); i++) {
				LocationProfile profile = profiles.get( i );

				names[ i ] = profile.getName();
				ids[ i ] = profile.getId();
			}

			// show list of locations as a single selected list
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			
			switch( type ) {
				case HOME :
					builder.setTitle( R.string.preference_home_profiles_select );
					break;
				case AWAY :
					builder.setTitle( R.string.preference_away_profiles_select );
					break;
			} 

			builder.setItems( names, new DialogInterface.OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) {

					// save selected location
					saveSelectedLocationProfile( context, ids[ which ], type );

					listener.defaultLocationProfileChanged();

				}
			});
				
			builder.show();

			Log.v( TAG, "selectLocationProfile : exit" );
		}

		/**
		 * @param context
		 * @param profiles
		 * @param listener
		 */
		protected static void selectPlaybackProfile( final Context context, final List<PlaybackProfile> profiles, final LocationType type, final PlaybackProfileChangedEventListener listener ) {
			Log.v( TAG, "selectLocationProfile : enter" );

			final String[] names = new String[ profiles.size() ];
			final int[] ids = new int[ profiles.size() ];
			
			for( int i = 0; i < profiles.size(); i++) {
				PlaybackProfile profile = profiles.get( i );

				names[ i ] = profile.getName();
				ids[ i ] = profile.getId();
			}

			// show list of locations as a single selected list
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			
			switch( type ) {
				case HOME :
					builder.setTitle( R.string.preference_home_playback_profiles_select );
					break;
				case AWAY :
					builder.setTitle( R.string.preference_away_playback_profiles_select );
					break;
			} 

			builder.setItems( names, new DialogInterface.OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) {

					// save selected location
					saveSelectedPlaybackProfile( context, ids[ which ], type );

					listener.defaultPlaybackProfileChanged();

				}
			});
				
			builder.show();

			Log.v( TAG, "selectPlaybackProfile : exit" );
		}

		/**
		 * @param context
		 * @param id
		 * @param type
		 */
		protected static void saveSelectedLocationProfile( final Context context, final long id, final LocationType type ) {
			Log.v( TAG, "saveSelectedLocationProfile : enter" );

			switch( type ) {
			case HOME :
				Log.v( TAG, "saveSelectedLocationProfile : setting home selected location profile" );

				mLocationProfileDaoHelper.setSelectedLocationProfile( context, (long) id );
				break;
			case AWAY :
				Log.v( TAG, "saveSelectedLocationProfile : setting away selected location profile" );

				mLocationProfileDaoHelper.setSelectedLocationProfile( context, (long) id );
				break;
			} 
			
			Log.v( TAG, "saveSelectedLocationProfile : exit" );
		}

		/**
		 * @param context
		 * @param id
		 * @param type
		 */
		protected static void saveSelectedPlaybackProfile( final Context context, final int id, final LocationType type ) {
			Log.v( TAG, "saveSelectedPlaybackProfile : enter" );

			switch( type ) {
			case HOME :
				Log.v( TAG, "saveSelectedPlaybackProfile : setting home selected playback profile" );

				mPlaybackProfileDaoHelper.setSelectedPlaybackProfile( context, (long) id );
				break;
			case AWAY :
				Log.v( TAG, "saveSelectedPlaybackProfile : setting away selected playback profile" );

				mPlaybackProfileDaoHelper.setSelectedPlaybackProfile( context, (long) id );
				break;
			} 
			
			Log.v( TAG, "saveSelectedPlaybackProfile : exit" );
		}

		protected static void deleteLocationProfile( final Context context, final List<LocationProfile> profiles, final LocationProfileChangedEventListener listener ) {
			Log.v( TAG, "deleteLocationProfile : enter" );

			final String[] names = new String[ profiles.size() ];
			final long[] ids = new long[ profiles.size() ];
			
			for( int i = 0; i < profiles.size(); i++) {
				LocationProfile profile = profiles.get( i );

				names[ i ] = profile.getName();
				ids[ i ] = profile.getId();
			}

			// show list of locations as a single selected list
			AlertDialog.Builder builder = new AlertDialog.Builder( context );
			builder.setTitle( R.string.preferences_profile_delete );
			builder.setItems( names, new DialogInterface.OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) {

					// delete  location
					mLocationProfileDaoHelper.delete( context, (long) ids[ which ] );
					
					listener.defaultLocationProfileChanged();
				}
			});
				
			builder.show();

			Log.v( TAG, "deleteLocationProfile : exit" );
		}

	}
	
	public static class HomeProfilesPreferenceFragment extends BasePreferenceFragment implements ServiceListener {

		private static final String TAG = HomeProfilesPreferenceFragment.class.getSimpleName();
		
		private static final String PREFERENCE_HOME_SELECTED_ID = "preference_home_profiles_default_id";
		private static final String PREFERENCE_HOME_ADD_KEY = "preference_home_profiles_add";
		private static final String PREFERENCE_HOME_SCAN_KEY = "preference_home_profiles_scan";
		private static final String PREFERENCE_HOME_DELETE_KEY = "preference_home_profiles_delete";
		private static final String PREFERENCE_CATEGORY_HOME_SAVED_KEY = "preference_home_profiles_saved";
		
		private static final String MYTHTV_MASTER_BACKEND_TYPE = "_mythbackend-master._tcp.local.";
		private static final String HOSTNAME = "mythandroid";

		private static JmDNS zeroConf = null;
		private static MulticastLock mLock = null;

		private ProgressDialog mProgressDialog;

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate( Bundle savedInstanceState ) {
			Log.v( TAG, "onCreate : enter" );
			
			super.onCreate( savedInstanceState );

			if( android.os.Build.VERSION.SDK_INT > 9 ) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy( policy );
			}

			// Load the preferences from an XML resource
            addPreferencesFromResource( R.xml.mythtv_home_profile_preferences );

            Log.v( TAG, "onCreate : exit" );
		}
		
		/* (non-Javadoc)
		 * @see android.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.v( TAG, "onResume : enter" );

			super.onResume();

			setupPreferences( getActivity() );
			
			Log.v( TAG, "onResume : exit" );
		}
		
		/* (non-Javadoc)
		 * @see android.app.Fragment#onPause()
		 */
		@Override
		public void onPause() {
			Log.v( TAG, "onPause : enter" );
			super.onPause();
			
			if( null != mProgressDialog ) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}

			Log.v( TAG, "onPause : exit" );
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

			if( null != mProgressDialog ) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			
			Log.v( TAG, "serviceAdded : " + event.getDNS().getServiceInfo( event.getType(), event.getName() ).toString() );

			ServiceInfo info = event.getDNS().getServiceInfo( event.getType(), event.getName() );
			final String hostname = info.getInet4Address().getHostAddress();
			final int port = info.getPort();
			Log.v( TAG, "serviceAdded : masterbackend=" + ( "http://" + hostname + ":" + port + "/" ) );

			if( null != hostname && !hostname.isEmpty() ) {
			
				LocationProfile profile = new LocationProfile();
				profile.setId( -1 );
				profile.setType( LocationType.HOME );
				profile.setName( event.getName() );
				profile.setUrl( "http://" + hostname + ":" + port + "/" );
			
				showLocationProfileEditDialog( getActivity(), profile );
			
			} else {

				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
				builder.setTitle( R.string.preference_edit_error_dialog_title );
				builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

					public void onClick( DialogInterface dialog, int which ) { }
					
				});

				builder.setMessage( R.string.preference_home_profiles_scan_error_message );
				builder.show();

			}

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

    		try {
    			mProgressDialog = ProgressDialog.show( getActivity(), "Please wait...", "Scanning network for MythTV Backend.", true, false );
    		} catch( Exception e ) {
    			Log.w( TAG, "startProbe : error", e );
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
			zeroConf.addServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );

			Log.v( TAG, "startProbe : exit" );
		}

		/**
		 * @throws IOException
		 */
		private void stopProbe() throws IOException {
			Log.v( TAG, "stopProbe : enter" );

			if( null != mProgressDialog ) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}

			zeroConf.removeServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );
			zeroConf.close();
			zeroConf = null;

			mLock.release();
			mLock = null;

			Log.v( TAG, "stopProbe : exit" );
		}

		private void setupPreferences( final Context context ) {
			Log.v( TAG, "setupPreferences : enter" );

    		Preference addHomeLocationProfilePreference = findPreference( PREFERENCE_HOME_ADD_KEY );
    		addHomeLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    			public boolean onPreferenceClick( Preference preference ) {
    				
    				LocationProfile profile = new LocationProfile();
    				profile.setId( -1 );
    				profile.setType( LocationType.HOME );
    				
    				showLocationProfileEditDialog( context, profile );
    					
    				return false;
    			}

    		});

    		Preference scanHomeLocationProfilePreference = findPreference( PREFERENCE_HOME_SCAN_KEY );
    		scanHomeLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    			public boolean onPreferenceClick( Preference preference ) {
    				
    				try {
    					startProbe();
    				} catch( IOException e ) {
    					Log.e( TAG, "scanHomeLocationProfilePreference : error", e );
    					
    					AlertDialog.Builder builder = new AlertDialog.Builder( context );
    					builder.setTitle( context.getString( R.string.preference_home_profiles_scan_error_title ) );
    					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

    						public void onClick( DialogInterface dialog, int which ) { }

    					});
    					builder.setMessage( context.getString( R.string.preference_home_profiles_scan_error_message ) );
    					builder.show();
    				}
    					
    				return false;
    			}

    		});

    		PreferenceCategory homeProfilesPreferenceCategory = (PreferenceCategory) findPreference( PREFERENCE_CATEGORY_HOME_SAVED_KEY );
    		homeProfilesPreferenceCategory.removeAll();

    		final List<LocationProfile> homeLocationProfiles = mLocationProfileDaoHelper.findAllHomeLocationProfiles( context );
    		if( null != homeLocationProfiles && !homeLocationProfiles.isEmpty() ) {
    			Log.v( TAG, "setupPreferences : setting Home Location Profiles" );
    			
    	        for( int i = 0; i < homeLocationProfiles.size(); i++ ) {
    	        	LocationProfile profile = homeLocationProfiles.get( i );
    	        	homeProfilesPreferenceCategory.addPreference( createLocationProfilePreference( context, profile ) );
    	        }
    	        
    		}
    		
    		LocationProfile selectedHomeLocationProfile = mLocationProfileDaoHelper.findSelectedHomeProfile( context );

    		Preference deleteHomeLocationProfilePreference = findPreference( PREFERENCE_HOME_DELETE_KEY );
    		deleteHomeLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    			public boolean onPreferenceClick( Preference preference ) {
    				
    				deleteLocationProfile( context, homeLocationProfiles, new LocationProfileChangedEventListener() {

						@Override
						public void defaultLocationProfileChanged() {
							// reset preference list with updated selection
							setupPreferences( context );
						}

					});
    				
    				return false;
    			}

    		});

    		if( null != selectedHomeLocationProfile ) {
    			Log.v( TAG, "setupPreferences : setting selected Home Location Profile" );
    			
    			Preference preference = findPreference( PREFERENCE_HOME_SELECTED_ID );
    			preference.setDefaultValue( selectedHomeLocationProfile.getId() );
    			preference.setSummary( selectedHomeLocationProfile.getName() );
    			preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    				public boolean onPreferenceClick( Preference preference ) {

    					// Displays the list of configured location profiles.
    					// Fires the locationChanged event when the user selects a
    					// location even if the user selects the same location already 
    					// selected.
    					selectLocationProfile( context, homeLocationProfiles, LocationType.HOME, new LocationProfileChangedEventListener() {

    						@Override
    						public void defaultLocationProfileChanged() {
    							// reset preference list with updated selection
    							setupPreferences( context );
    						}

    					});
    					
    					return true;
    				}
    			});

    		}

			Log.v( TAG, "setupPreferences : exit" );
		}

	}
	
	public static class HomePlaybackProfilesPreferenceFragment extends BasePreferenceFragment {
	
		private static final String TAG = HomePlaybackProfilesPreferenceFragment.class.getSimpleName();

		private static final String PREFERENCE_HOME_PLAYBACK_SELECTED_ID = "preference_home_playback_profiles_default_id";
		private static final String PREFERENCE_CATEGORY_HOME_PLAYBACK_SAVED_KEY = "preference_home_playback_profiles_saved";

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate( Bundle savedInstanceState ) {
			Log.v( TAG, "onCreate : enter" );
			
			super.onCreate( savedInstanceState );

            // Load the preferences from an XML resource
            addPreferencesFromResource( R.xml.mythtv_home_playback_profile_preferences );

			Log.v( TAG, "onCreate : exit" );
		}

		/* (non-Javadoc)
		 * @see android.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.v( TAG, "onResume : enter" );

			super.onResume();

			setupPreferences( getActivity() );
			
			Log.v( TAG, "onResume : exit" );
		}

		
		// internal helpers
		
		private void setupPreferences( final Context context ) {
			Log.v( TAG, "setupPreferences : enter" );

    		PreferenceCategory homePlaybackProfilesPreferenceCategory = (PreferenceCategory) findPreference( PREFERENCE_CATEGORY_HOME_PLAYBACK_SAVED_KEY );
    		homePlaybackProfilesPreferenceCategory.removeAll();

    		final List<PlaybackProfile> homePlaybackProfiles = mPlaybackProfileDaoHelper.findAllHomePlaybackProfiles( context );
    		if( null != homePlaybackProfiles && !homePlaybackProfiles.isEmpty() ) {
    			Log.v( TAG, "setupPreferences : setting Home Playback Profiles" );
    			
    	        for( int i = 0; i < homePlaybackProfiles.size(); i++ ) {
    	        	PlaybackProfile profile = homePlaybackProfiles.get( i );
    	        	homePlaybackProfilesPreferenceCategory.addPreference( createPlaybackProfilePreference( context, profile ) );
    	        }
    	        
    		}
    		
    		PlaybackProfile selectedHomePlaybackProfile = mPlaybackProfileDaoHelper.findSelectedHomeProfile( context );

    		if( null != selectedHomePlaybackProfile ) {
    			Log.v( TAG, "setupPreferences : setting selected Home Playback Profile" );
    			
    			Preference preference = findPreference( PREFERENCE_HOME_PLAYBACK_SELECTED_ID );
    			preference.setDefaultValue( selectedHomePlaybackProfile.getId() );
    			preference.setSummary( selectedHomePlaybackProfile.getName() );
    			preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    				public boolean onPreferenceClick( Preference preference ) {

    					// Displays the list of configured location profiles.
    					// Fires the locationChanged event when the user selects a
    					// location even if the user selects the same location already 
    					// selected.
    					selectPlaybackProfile( context, homePlaybackProfiles, LocationType.HOME, new PlaybackProfileChangedEventListener() {

    						@Override
    						public void defaultPlaybackProfileChanged() {
    							// reset preference list with updated selection
    							setupPreferences( context );
    						}

    					} );
    					
    					return true;
    				}
    			});

    		}

    		Log.v( TAG, "setupPreferences : exit" );
		}

	}
	
	public static class AwayProfilesPreferenceFragment extends BasePreferenceFragment {

		private static final String TAG = AwayProfilesPreferenceFragment.class.getSimpleName();
		
		private static final String PREFERENCE_AWAY_SELECTED_ID = "preference_away_profiles_default_id";
		private static final String PREFERENCE_AWAY_ADD_KEY = "preference_away_profiles_add";
		private static final String PREFERENCE_AWAY_DELETE_KEY = "preference_away_profiles_delete";
		private static final String PREFERENCE_CATEGORY_AWAY_SAVED_KEY = "preference_away_profiles_saved";

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate( Bundle savedInstanceState ) {
			Log.v( TAG, "onCreate : enter" );
			
			super.onCreate( savedInstanceState );

            // Load the preferences from an XML resource
            addPreferencesFromResource( R.xml.mythtv_away_profile_preferences );

			Log.v( TAG, "onCreate : exit" );
		}

		/* (non-Javadoc)
		 * @see android.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.v( TAG, "onResume : enter" );

			super.onResume();

			setupPreferences( getActivity() );
			
			Log.v( TAG, "onResume : exit" );
		}

		
		// internal helpers
		
		private void setupPreferences( final Context context ) {
			Log.v( TAG, "setupPreferences : enter" );

    		Preference addAwayLocationProfilePreference = findPreference( PREFERENCE_AWAY_ADD_KEY );
    		addAwayLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    			public boolean onPreferenceClick( Preference preference ) {
    				
    				LocationProfile profile = new LocationProfile();
    				profile.setId( -1 );
    				profile.setType( LocationType.AWAY );
    				
    				showLocationProfileEditDialog( context, profile );
    					
    				return false;
    			}

    		});

    		PreferenceCategory awayProfilesPreferenceCategory = (PreferenceCategory) findPreference( PREFERENCE_CATEGORY_AWAY_SAVED_KEY );
    		awayProfilesPreferenceCategory.removeAll();

    		final List<LocationProfile> awayLocationProfiles = mLocationProfileDaoHelper.findAllAwayLocationProfiles( context );
    		if( null != awayLocationProfiles && !awayLocationProfiles.isEmpty() ) {
    			Log.v( TAG, "setupPreferences : setting Away Location Profiles" );
    			
    	        for( int i = 0; i < awayLocationProfiles.size(); i++ ) {
    	        	LocationProfile profile = awayLocationProfiles.get( i );
    	        	awayProfilesPreferenceCategory.addPreference( createLocationProfilePreference( context, profile ) );
    	        }
    	        
    		}
    		
    		LocationProfile selectedAwayLocationProfile = mLocationProfileDaoHelper.findSelectedAwayProfile( context );

    		Preference deleteAwayLocationProfilePreference = findPreference( PREFERENCE_AWAY_DELETE_KEY );
    		deleteAwayLocationProfilePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    			public boolean onPreferenceClick( Preference preference ) {
    				
    				deleteLocationProfile( context, awayLocationProfiles, new LocationProfileChangedEventListener() {

						@Override
						public void defaultLocationProfileChanged() {
							// reset preference list with updated selection
							setupPreferences( context );
						}

					});
    				
    				return false;
    			}

    		});

    		if( null != selectedAwayLocationProfile ) {
    			Log.v( TAG, "setupPreferences : setting selected Away Location Profile" );
    			
    			Preference preference = findPreference( PREFERENCE_AWAY_SELECTED_ID );
    			preference.setDefaultValue( selectedAwayLocationProfile.getId() );
    			preference.setSummary( selectedAwayLocationProfile.getName() );
    			preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    				public boolean onPreferenceClick( Preference preference ) {

    					// Displays the list of configured location profiles.
    					// Fires the locationChanged event when the user selects a
    					// location even if the user selects the same location already 
    					// selected.
    					selectLocationProfile( context, awayLocationProfiles, LocationType.AWAY, new LocationProfileChangedEventListener() {

    						@Override
    						public void defaultLocationProfileChanged() {
    							// reset preference list with updated selection
    							setupPreferences( context );
    						}

    					} );
    					
    					return true;
    				}
    			});

    		}

			Log.v( TAG, "setupPreferences : exit" );
		}
		
	}
	
	public static class AwayPlaybackProfilesPreferenceFragment extends BasePreferenceFragment {
		
		private static final String TAG = AwayPlaybackProfilesPreferenceFragment.class.getSimpleName();

		private static final String PREFERENCE_AWAY_PLAYBACK_SELECTED_ID = "preference_away_playback_profiles_default_id";
		private static final String PREFERENCE_CATEGORY_AWAY_PLAYBACK_SAVED_KEY = "preference_away_playback_profiles_saved";

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate( Bundle savedInstanceState ) {
			Log.v( TAG, "onCreate : enter" );
			
			super.onCreate( savedInstanceState );

            // Load the preferences from an XML resource
            addPreferencesFromResource( R.xml.mythtv_away_playback_profile_preferences );

			Log.v( TAG, "onCreate : exit" );
		}

		/* (non-Javadoc)
		 * @see android.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.v( TAG, "onResume : enter" );

			super.onResume();

			setupPreferences( getActivity() );
			
			Log.v( TAG, "onResume : exit" );
		}

		
		// internal helpers
		
		private void setupPreferences( final Context context ) {
			Log.v( TAG, "setupPreferences : enter" );

    		PreferenceCategory awayPlaybackProfilesPreferenceCategory = (PreferenceCategory) findPreference( PREFERENCE_CATEGORY_AWAY_PLAYBACK_SAVED_KEY );
    		awayPlaybackProfilesPreferenceCategory.removeAll();

    		final List<PlaybackProfile> awayPlaybackProfiles = mPlaybackProfileDaoHelper.findAllAwayPlaybackProfiles( context );
    		if( null != awayPlaybackProfiles && !awayPlaybackProfiles.isEmpty() ) {
    			Log.v( TAG, "setupPreferences : setting Away Playback Profiles" );
    			
    	        for( int i = 0; i < awayPlaybackProfiles.size(); i++ ) {
    	        	PlaybackProfile profile = awayPlaybackProfiles.get( i );
    	        	awayPlaybackProfilesPreferenceCategory.addPreference( createPlaybackProfilePreference( context, profile ) );
    	        }
    	        
    		}
    		
    		PlaybackProfile selectedAwayPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedAwayProfile( context );

    		if( null != selectedAwayPlaybackProfile ) {
    			Log.v( TAG, "setupPreferences : setting selected Away Playback Profile" );
    			
    			Preference preference = findPreference( PREFERENCE_AWAY_PLAYBACK_SELECTED_ID );
    			preference.setDefaultValue( selectedAwayPlaybackProfile.getId() );
    			preference.setSummary( selectedAwayPlaybackProfile.getName() );
    			preference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

    				public boolean onPreferenceClick( Preference preference ) {

    					// Displays the list of configured location profiles.
    					// Fires the locationChanged event when the user selects a
    					// location even if the user selects the same location already 
    					// selected.
    					selectPlaybackProfile( context, awayPlaybackProfiles, LocationType.AWAY, new PlaybackProfileChangedEventListener() {

    						@Override
    						public void defaultPlaybackProfileChanged() {
    							// reset preference list with updated selection
    							setupPreferences( context );
    						}

    					} );
    					
    					return true;
    				}
    			});

    		}
    		
			Log.v( TAG, "setupPreferences : exit" );
		}

	}
	
	public static class ProgramGuidePreferenceFragment extends PreferenceFragment {
		
		private static final String TAG = ProgramGuidePreferenceFragment.class.getSimpleName();

		private static final String PREFERENCE_PROGRAM_GUIDE_DAYS = "preference_program_guide_days";

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate( Bundle savedInstanceState ) {
			Log.v( TAG, "onCreate : enter" );
			
			super.onCreate( savedInstanceState );

            // Load the preferences from an XML resource
            addPreferencesFromResource( R.xml.mythtv_program_guide_preferences );

			Log.v( TAG, "onCreate : exit" );
		}

		/* (non-Javadoc)
		 * @see android.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.v( TAG, "onResume : enter" );

			super.onResume();

			setupPreferences( getActivity() );
			
			Log.v( TAG, "onResume : exit" );
		}

		
		// internal helpers
		
		private void setupPreferences( final Context context ) {
			Log.v( TAG, "setupPreferences : enter" );

   			Log.v( TAG, "setupPreferences : setting selected Away Playback Profile" );
    		
   	        /** Defining PreferenceChangeListener */
   	        OnPreferenceChangeListener onPreferenceChangeListener = new OnPreferenceChangeListener() {
   	 
   	            @Override
   	            public boolean onPreferenceChange( Preference preference, Object newValue ) {
//   	                OnPreferenceChangeListener listener = ( OnPreferenceChangeListener) context;
//   	                listener.onPreferenceChange( preference, newValue );
   	                return true;
   	            }
   	       };
   	 
   	        /** Getting the ListPreference from the Preference Resource */
  			ListPreference preference = (ListPreference) findPreference( PREFERENCE_PROGRAM_GUIDE_DAYS );
  			if( null == preference.getValue() ) {
  				preference.setValueIndex( 0 );
  			}
   	        
  			/** Setting Preference change listener for the ListPreference */
   	        preference.setOnPreferenceChangeListener( onPreferenceChangeListener );

			Log.v( TAG, "setupPreferences : exit" );
		}

	}
	
}
