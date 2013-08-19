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

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.service.util.image.ImageCache;
import org.mythtv.service.util.image.ImageFetcher;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythtvFragmentActivity extends FragmentActivity implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythtvFragmentActivity.class.getSimpleName();

	protected SharedPreferences preferences = null;
	protected FileHelper mFileHelper;
	protected ImageFetcher mImageFetcher;
	protected LiveStreamDaoHelper mLiveStreamDaoHelper;
	protected LocationProfileDaoHelper mLocationProfileDaoHelper;
	protected MenuHelper mMenuHelper;
	protected NetworkHelper mNetworkHelper;
	protected ProgramGroupDaoHelper mProgramGroupDaoHelper;
	protected RecordedDaoHelper mRecordedDaoHelper;
	protected RunningServiceHelper mRunningServiceHelper;
	
	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getMainApplication() {
		return (MainApplication) super.getApplicationContext();
	}

	
	//***************************************
    // FragmentActivity methods
    //***************************************
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setupActionBar();

		preferences = getSharedPreferences( getString( R.string.app_name ), Context.MODE_PRIVATE );
		
		mFileHelper = FileHelper.newInstance( this );
		mLiveStreamDaoHelper = new LiveStreamDaoHelper( this );
		mLocationProfileDaoHelper = new LocationProfileDaoHelper( this );
		mMenuHelper = MenuHelper.newInstance( this );
		mNetworkHelper = NetworkHelper.newInstance( this );
		mProgramGroupDaoHelper = new ProgramGroupDaoHelper( this );
		mRecordedDaoHelper = new RecordedDaoHelper( this );
		mRunningServiceHelper = RunningServiceHelper.newInstance( this );
		
		// Fetch screen height and width, to use as our max size when loading images as this activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
        
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.v( TAG, "onCreate : device hxw - " + height + " x " + width );
        
        int longest = width; //( height < width ? height : width );
        
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams( mFileHelper.getProgramRecordedDataDirectory() );
        cacheParams.setMemCacheSizePercent( this, 0.25f ); // Set memory cache to 25% of mem class

        mImageFetcher = new ImageFetcher( this, longest );
        mImageFetcher.addImageCache( getSupportFragmentManager(), cacheParams );
        mImageFetcher.setImageFadeIn( false );
		
		Log.v( TAG, "onCreate : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
		
        mImageFetcher.setExitTasksEarly( false );

		Log.v( TAG, "onResume : exit" );
	}

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
		Log.v( TAG, "onPause : enter" );
        super.onPause();

        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();

        Log.v( TAG, "onPause : enter" );
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
		Log.v( TAG, "onDestroy : enter" );
		super.onDestroy();
        
		mImageFetcher.closeCache();

        Log.v( TAG, "onDestroy : exit" );
    }

	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

	    mMenuHelper.aboutMenuItem( menu );
	    mMenuHelper.helpSubMenu( menu );
	    
		Log.v( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {			
		case MenuHelper.ABOUT_ID:
			Log.d( TAG, "onOptionsItemSelected : about selected" );

			mMenuHelper.handleAboutMenu();
		    
	        return true;
	    
		case MenuHelper.FAQ_ID:
			
			mMenuHelper.handleFaqMenu();
			
			return true;

		case MenuHelper.TROUBLESHOOT_ID:
			
			mMenuHelper.handleTroubleshootMenu();
			
			return true;
		
		case MenuHelper.ISSUES_ID:

			mMenuHelper.handleIssuesMenu();
			
			return true;
		
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}
	
	
	/**
	 * @return the preferences
	 */
	public SharedPreferences getSharedPreferences() {
		return preferences;
	}
	
	/**
	 * @return the mImageFetcher
	 */
	public ImageFetcher getImageFetcher() {
		return mImageFetcher;
	}

	public LiveStreamDaoHelper getLiveStreamDaoHelper() {
		return mLiveStreamDaoHelper;
	}
	
	/**
	 * @return the mLocationProfileDaoHelper
	 */
	public LocationProfileDaoHelper getLocationProfileDaoHelper() {
		return mLocationProfileDaoHelper;
	}

	/**
	 * @return the mMenuHelper
	 */
	public MenuHelper getMenuHelper() {
		return mMenuHelper;
	}

	/**
	 * @return the mNetworkHelper
	 */
	public NetworkHelper getNetworkHelper() {
		return mNetworkHelper;
	}

	/**
	 * @return the mProgramGroupDaoHelper
	 */
	public ProgramGroupDaoHelper getProgramGroupDaoHelper() {
		return mProgramGroupDaoHelper;
	}

	/**
	 * @return the mRecordedDaoHelper
	 */
	public RecordedDaoHelper getRecordedDaoHelper() {
		return mRecordedDaoHelper;
	}

	/**
	 * @return the mRunningServiceHelper
	 */
	public RunningServiceHelper getRunningServiceHelper() {
		return mRunningServiceHelper;
	}


	// internal helpers
	
	@TargetApi( 11 )
	protected void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}


}
