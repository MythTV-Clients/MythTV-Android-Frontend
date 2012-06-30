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
package org.mythtv.client.ui.preferences;

import static android.provider.BaseColumns._ID;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EventListener;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.MythtvDatabaseManager;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.PlaybackProfileConstants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythtvPreferenceActivity extends PreferenceActivity implements ServiceListener {

	private static final String TAG = MythtvPreferenceActivity.class.getSimpleName();

	private static final String PREFERENCE_HOME_SELECTED_ID = "preference_home_profiles_default_id";
	private static final String PREFERENCE_HOME_ADD_KEY = "preference_home_profiles_add";
	private static final String PREFERENCE_HOME_SCAN_KEY = "preference_home_profiles_scan";
	private static final String PREFERENCE_HOME_DELETE_KEY = "preference_home_profiles_delete";
	private static final String PREFERENCE_CATEGORY_HOME_SAVED_KEY = "preference_home_profiles_saved";
	private static final String PREFERENCE_HOME_PLAYBACK_SELECTED_ID = "preference_home_playback_profiles_default_id";
	private static final String PREFERENCE_CATEGORY_HOME_PLAYBACK_SAVED_KEY = "preference_home_playback_profiles_saved";
	private static final String PREFERENCE_AWAY_SELECTED_ID = "preference_away_profiles_default_id";
	private static final String PREFERENCE_AWAY_ADD_KEY = "preference_away_profiles_add";
	private static final String PREFERENCE_AWAY_DELETE_KEY = "preference_away_profiles_delete";
	private static final String PREFERENCE_CATEGORY_AWAY_SAVED_KEY = "preference_away_profiles_saved";
	private static final String PREFERENCE_AWAY_PLAYBACK_SELECTED_ID = "preference_away_playback_profiles_default_id";
	private static final String PREFERENCE_CATEGORY_AWAY_PLAYBACK_SAVED_KEY = "preference_away_playback_profiles_saved";
	
	private static final String MYTHTV_MASTER_BACKEND_TYPE = "_mythbackend-master._tcp.local.";
	private static final String HOSTNAME = "mythandroid";

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings( "deprecation" )
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		// Load the preferences from an XML resource
        addPreferencesFromResource( R.xml.mythtv_home_profile_preferences );
        addPreferencesFromResource( R.xml.mythtv_home_playback_profile_preferences );
        addPreferencesFromResource( R.xml.mythtv_away_profile_preferences );
        addPreferencesFromResource( R.xml.mythtv_away_playback_profile_preferences );

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

	// internal helpers
	
	@SuppressWarnings( "deprecation" )
	private void setupPreferences( final Context context ) {
		Log.v( TAG, "setupPreferences : enter" );

		MythtvDatabaseManager db = new MythtvDatabaseManager( context );

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

		final List<LocationProfile> homeLocationProfiles = db.fetchHomeLocationProfiles();
		if( null != homeLocationProfiles && !homeLocationProfiles.isEmpty() ) {
			Log.v( TAG, "setupPreferences : setting Home Location Profiles" );
			
	        for( int i = 0; i < homeLocationProfiles.size(); i++ ) {
	        	LocationProfile profile = homeLocationProfiles.get( i );
	        	homeProfilesPreferenceCategory.addPreference( createLocationProfilePreference( context, profile ) );
	        }
	        
		}
		
		LocationProfile selectedHomeLocationProfile = db.fetchSelectedHomeLocationProfile();

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

		PreferenceCategory homePlaybackProfilesPreferenceCategory = (PreferenceCategory) findPreference( PREFERENCE_CATEGORY_HOME_PLAYBACK_SAVED_KEY );
		homePlaybackProfilesPreferenceCategory.removeAll();

		final List<PlaybackProfile> homePlaybackProfiles = db.fetchHomePlaybackProfiles();
		if( null != homePlaybackProfiles && !homePlaybackProfiles.isEmpty() ) {
			Log.v( TAG, "setupPreferences : setting Home Playback Profiles" );
			
	        for( int i = 0; i < homePlaybackProfiles.size(); i++ ) {
	        	PlaybackProfile profile = homePlaybackProfiles.get( i );
	        	homePlaybackProfilesPreferenceCategory.addPreference( createPlaybackProfilePreference( context, profile ) );
	        }
	        
		}
		
		PlaybackProfile selectedHomePlaybackProfile = db.fetchSelectedHomePlaybackProfile();

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

		final List<LocationProfile> awayLocationProfiles = db.fetchAwayLocationProfiles();
		if( null != awayLocationProfiles && !awayLocationProfiles.isEmpty() ) {
			Log.v( TAG, "setupPreferences : setting Away Location Profiles" );
			
	        for( int i = 0; i < awayLocationProfiles.size(); i++ ) {
	        	LocationProfile profile = awayLocationProfiles.get( i );
	        	awayProfilesPreferenceCategory.addPreference( createLocationProfilePreference( context, profile ) );
	        }
	        
		}
		
		LocationProfile selectedAwayLocationProfile = db.fetchSelectedAwayLocationProfile();

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

		PreferenceCategory awayPlaybackProfilesPreferenceCategory = (PreferenceCategory) findPreference( PREFERENCE_CATEGORY_AWAY_PLAYBACK_SAVED_KEY );
		awayPlaybackProfilesPreferenceCategory.removeAll();

		final List<PlaybackProfile> awayPlaybackProfiles = db.fetchAwayPlaybackProfiles();
		if( null != awayPlaybackProfiles && !awayPlaybackProfiles.isEmpty() ) {
			Log.v( TAG, "setupPreferences : setting Away Playback Profiles" );
			
	        for( int i = 0; i < awayPlaybackProfiles.size(); i++ ) {
	        	PlaybackProfile profile = awayPlaybackProfiles.get( i );
	        	awayPlaybackProfilesPreferenceCategory.addPreference( createPlaybackProfilePreference( context, profile ) );
	        }
	        
		}
		
		PlaybackProfile selectedAwayPlaybackProfile = db.fetchSelectedAwayPlaybackProfile();

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
	
//	private abstract static class BasePreference {

//		protected static final String TAG = BasePreference.class.getSimpleName();

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
					
					MythtvDatabaseManager db = new MythtvDatabaseManager( context );
					PlaybackProfile profile = db.fetchPlaybackProfile( Integer.parseInt( preference.getKey() ) );
					
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
				intent.putExtra( _ID, profile.getId() );
				intent.putExtra( LocationProfileConstants.FIELD_TYPE, profile.getType().name() );
				intent.putExtra( LocationProfileConstants.FIELD_NAME, profile.getName() );
				intent.putExtra( LocationProfileConstants.FIELD_URL, profile.getUrl() );
				intent.putExtra( LocationProfileConstants.FIELD_SELECTED, ( profile.isSelected() ? 1 : 0 ) );
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
				intent.putExtra( _ID, profile.getId() );
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
			final int[] ids = new int[ profiles.size() ];
			
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
		protected static void saveSelectedLocationProfile( final Context context, final int id, final LocationType type ) {
			Log.v( TAG, "saveSelectedLocationProfile : enter" );

			MythtvDatabaseManager db = new MythtvDatabaseManager( context );
			switch( type ) {
			case HOME :
				Log.v( TAG, "saveSelectedLocationProfile : setting home selected location profile" );

				db.setSelectedHomeLocationProfile( id );
				break;
			case AWAY :
				Log.v( TAG, "saveSelectedLocationProfile : setting away selected location profile" );

				db.setSelectedAwayLocationProfile( id );
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

			MythtvDatabaseManager db = new MythtvDatabaseManager( context );
			switch( type ) {
			case HOME :
				Log.v( TAG, "saveSelectedPlaybackProfile : setting home selected playback profile" );

				db.setSelectedHomePlaybackProfile( id );
				break;
			case AWAY :
				Log.v( TAG, "saveSelectedPlaybackProfile : setting away selected playback profile" );

				db.setSelectedAwayPlaybackProfile( id );
				break;
			} 
			
			Log.v( TAG, "saveSelectedPlaybackProfile : exit" );
		}

		protected static void deleteLocationProfile( final Context context, final List<LocationProfile> profiles, final LocationProfileChangedEventListener listener ) {
			Log.v( TAG, "deleteLocationProfile : enter" );

			final String[] names = new String[ profiles.size() ];
			final int[] ids = new int[ profiles.size() ];
			
			for( int i = 0; i < profiles.size(); i++) {
				LocationProfile profile = profiles.get( i );

				names[ i ] = profile.getName();
				ids[ i ] = profile.getId();
			}

			// show list of locations as a single selected list
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle( R.string.preferences_profile_delete );
			builder.setItems( names, new DialogInterface.OnClickListener() {

				public void onClick( DialogInterface dialog, int which ) {

					// delete  location
					MythtvDatabaseManager db = new MythtvDatabaseManager( context );
					db.deleteLocationProfile( ids[ which ] );
					
					listener.defaultLocationProfileChanged();
				}
			});
				
			builder.show();

			Log.v( TAG, "deleteLocationProfile : exit" );
		}

//	}
	
}
