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
import org.mythtv.client.ui.MainMenuFragment.ContentFragmentRequestedListener;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;


/**
 * 
 * @author Thomas G. Kenny Jr
 *
 */
public class MainActivity extends AbstractMythtvFragmentActivity implements ContentFragmentRequestedListener{

	public static final String TAG = MainActivity.class.getName();
	private static int sContentId;
	
	private boolean mMenuVisible = false;
	

	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		this.setContentView(R.layout.activity_main);
		
		//get main menu framelayout
		FrameLayout menu = (FrameLayout) findViewById(R.id.frame_layout_main_menu);
		
		//get framgment manager and start a transaction
		FragmentManager fragMgr = this.getSupportFragmentManager();
		FragmentTransaction fTran = fragMgr.beginTransaction();
		
		//Setup main menu fragment
		MainMenuFragment mainMenuFrag = (MainMenuFragment)fragMgr.findFragmentById(R.layout.fragment_main_menu);
		if (null == mainMenuFrag) {
			mainMenuFrag = (MainMenuFragment)Fragment.instantiate(this, MainMenuFragment.class.getName());
			mainMenuFrag.setContentFragmentRequestedListener(this);
			fTran.add(R.id.frame_layout_main_menu, mainMenuFrag);
		}else{
			fTran.replace(R.id.frame_layout_main_menu, mainMenuFrag);
		}
		
		/* only set the backend status fragment if this is our first entry into application
		 * or it is the current screen. This elinates the double fragment overlap that occures
		 * during orientation change.
		 */
		Log.d(TAG, "FragmentID: " + sContentId);
		if (sContentId == 0 || sContentId == R.layout.fragment_backend_status) {
			// setup backend status as first fragment
			Fragment backendStatusFrag = fragMgr.findFragmentById(R.layout.fragment_backend_status);
			if (null == backendStatusFrag) {
				backendStatusFrag = Fragment.instantiate(this,
						BackendStatusFragment.class.getName());
				fTran.add(R.id.frame_layout_main_ui, backendStatusFrag);
			} else {
				fTran.replace(R.id.frame_layout_main_ui, backendStatusFrag);
			}
			
			sContentId = R.layout.fragment_backend_status;
		}
		
		//finalize fragment transaction
		//fTran.commit();
		//this fixes an exception caused by a bug in support library.
		fTran.commitAllowingStateLoss();
	    
		setupActionBar();
		
		Log.v( TAG, "onCreate : exit" );
	}
	
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
		case android.R.id.home:
			return toggleMainMenuVisibility();
		}
		
		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean toggleMainMenuVisibility() {
		
		//get content containers
		final FrameLayout content = (FrameLayout)this.findViewById(R.id.frame_layout_main_ui);
		
		
		//check that we received them
		if(null != content){

			//show/hide
			if(content.getX() > 0){
				//HIDE menu
				return this.hideMainMenu();
			}else{
				//SHOW Menu
				return this.showMainMenu();
			}
		}
		
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean hideMainMenu(){
		//get menu and content containers
		final FrameLayout menu = (FrameLayout) this.findViewById(R.id.frame_layout_main_menu);
		final FrameLayout content = (FrameLayout)this.findViewById(R.id.frame_layout_main_ui);
		
		//check that we received them
		if(null != content && null != menu && content.getX() > 0){
			
			//animator that moves content panel
			AnimatorUpdateListener hideMenuListener = new AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Float w = (Float)animation.getAnimatedValue();
					content.setX(w);
					
					if(animation.getDuration() - animation.getCurrentPlayTime() <= 0)
						menu.setX(-menu.getWidth());
				}
			};
			
			//animator that scales content panel 
			AnimatorUpdateListener scaleContentListener = new AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Float w = (Float)animation.getAnimatedValue();
					content.setScaleX(w);
					content.setScaleY(w);
				}
			};
			
			
			ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.05f);
			scaleAnimator.setDuration(200);
			scaleAnimator.setRepeatCount(1);
			scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
			scaleAnimator.addUpdateListener(scaleContentListener);
			
			//HIDE Menu
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				ValueAnimator transAnimator = ValueAnimator.ofFloat(content.getX(), 0);
				transAnimator.setDuration(300);
				transAnimator.setStartDelay(100);
				transAnimator.addUpdateListener(hideMenuListener);

				scaleAnimator.start();
				transAnimator.start();
			}else{
				
				//move content panel
				FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) content.getLayoutParams();
				params.leftMargin = 0;
				content.setLayoutParams(params);
				
				//move menu panel
				params = (android.widget.FrameLayout.LayoutParams) menu.getLayoutParams();
				params.leftMargin = -menu.getWidth();
				menu.setLayoutParams(params);
			}

			mMenuVisible = false;
			return true;
		}
		
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean showMainMenu(){
		//get menu and content containers
		final FrameLayout menu = (FrameLayout) this.findViewById(R.id.frame_layout_main_menu);
		final FrameLayout content = (FrameLayout)this.findViewById(R.id.frame_layout_main_ui);
		
		
		//check that we received them
		if(null != content && null != menu && content.getX() <= 0){
			
			//animator that moves content panel
			AnimatorUpdateListener showMenuListener = new AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Float w = (Float)animation.getAnimatedValue();
					content.setX(w);
				}
			};
			
			//animator that scales content panel 
			AnimatorUpdateListener scaleContentListener = new AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Float w = (Float)animation.getAnimatedValue();
					content.setScaleX(w);
					content.setScaleY(w);
				}
			};
			
			
			ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.05f);
			scaleAnimator.setDuration(200);
			scaleAnimator.setRepeatCount(1);
			scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
			scaleAnimator.addUpdateListener(scaleContentListener);
			
			
			//SHOW Menu
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				
				//make sure menu is in position before we start
				menu.setX(0);
				
				ValueAnimator transAnimator = ValueAnimator.ofFloat(0, menu.getWidth());
				transAnimator.setDuration(300);
				transAnimator.setStartDelay(100);
				transAnimator.addUpdateListener(showMenuListener);

				scaleAnimator.start();
				transAnimator.start();
			}else{
				
				//move content panel
				FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) content.getLayoutParams();
				params.leftMargin = menu.getWidth();
				content.setLayoutParams(params);
				
				//move menu
				params = (android.widget.FrameLayout.LayoutParams) menu.getLayoutParams();
				params.leftMargin = 0;
				menu.setLayoutParams(params);
			}
			
			mMenuVisible = true;
			return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	public boolean isMainMenuVisible(){
		return this.mMenuVisible;
	}
	

	@Override
	public void OnFragmentRequested(int fragmentId, String fragmentClassName) {
	
		//find fragment and instantiate if necessary
		Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentId);
                if (null == fragment)
                    fragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(fragmentId));
		if (null == fragment)
		    fragment = Fragment.instantiate(this, fragmentClassName);
		
		//call into overload
		OnFragmentRequested(fragmentId, fragment);
	}


	@Override
	public void OnFragmentRequested(int fragmentId, Fragment fragment) {
		
		if(null == fragment){
			Log.e(TAG, "OnFragmentRequested(fragment) argument null.");
		}
		
		//set content fragment id
		sContentId = fragmentId;
		Log.d(TAG, "OnFragmentRequested() FragmentID: " + sContentId);
		
		//get framgment manager and start a transaction
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction fTran = fragMgr.beginTransaction();
		
		//replace content fragment with this one
		fTran.replace(R.id.frame_layout_main_ui, fragment, Integer.toString(fragmentId) );
		fTran.addToBackStack(null);
		
		//finalize fragment transaction
		//fTran.commit();
		//this fixes an exception caused by a bug in support library.
		fTran.commitAllowingStateLoss();
		
		//hide menu
		this.hideMainMenu();
	}
	
	
}
