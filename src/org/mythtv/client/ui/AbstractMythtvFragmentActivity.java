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
import org.mythtv.service.util.NetworkHelper;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


	protected MenuHelper mMenuHelper;
	protected NetworkHelper mNetworkHelper;
	
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

		mMenuHelper = MenuHelper.newInstance( this );
		mNetworkHelper = NetworkHelper.newInstance( this );
		
		setupActionBar();
		
		Log.v( TAG, "onCreate : exit" );
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
	
//	@Override
//	public void setContentView(int layoutResID) {
//		
//		//inflate main activity layout
//		View mainLayout = getLayoutInflater().inflate(R.layout.activity_main, null);
//	
//		//get main menu framelayout
//		FrameLayout menu = (FrameLayout) mainLayout.findViewById(R.id.frame_layout_main_menu);
//		
//		//get framgment manager and start a transaction
//		FragmentManager fragMgr = this.getSupportFragmentManager();
//		FragmentTransaction fTran = fragMgr.beginTransaction();
//		
//		fTran.setTransition(FragmentTransaction.TRANSIT_NONE);
//		
//		//Setup main menu fragment
//		Fragment mainMenuFrag = fragMgr.findFragmentById(R.layout.fragment_main_menu);
//		if (null == mainMenuFrag) {
//			mainMenuFrag = Fragment.instantiate(this, MainMenuFragment.class.getName());
//			fTran.add(R.id.frame_layout_main_menu, mainMenuFrag);
//		}else{
//			fTran.replace(R.id.frame_layout_main_menu, mainMenuFrag);
//		}
//		
//		menu.setEnabled(false);
//		
//		//finalize fragment transaction
//		//fTran.commit();
//		//this fixes an exception caused by a bug in support library.
//		fTran.commitAllowingStateLoss();
//		
//		FrameLayout content = (FrameLayout) mainLayout.findViewById(R.id.frame_layout_main_ui);
//	    
//	    // Setting the content of layout your provided to the act_content frame
//		mInitialContentlayoutResID = layoutResID;
//	    getLayoutInflater().inflate(layoutResID, content, true); 
//	    
//	    //set our new main layout
//	    super.setContentView(mainLayout);
//		
//	}
	
	

}
