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
package org.mythtv.client.ui.frontends;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythmoteActivity extends AbstractFrontendsActivity {

	private static final String TAG = MythmoteActivity.class.getSimpleName();
		
	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;
	private List<Fragment> fragmentArrayList;
	private List<String> headerArrayList;
	

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_mythmote );
		
		//setup the viewpager if layout contains one
		setupViewPager();
		
		//get power manager so we can keep the screen on
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	@TargetApi( 11 )
	protected void setupActionBar() {
		super.setupActionBar();
		Log.v( TAG, "MythmoteActivity.setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setTitle(R.string.frontends_title);
		}
		
		Log.v( TAG, "MythmoteActivity.setupActionBar : exit" );
	}

	@Override
	protected void onResume() {
		
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Mythmote wakelock");
		wakeLock.acquire();
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		
		wakeLock.release();
		
		super.onPause();
	}

	
	/**
	 * Setups up the viewpager and MythmotePagerAdapter if
	 * the current layout contains the mythmote_pager view pager.
	 */
	private void setupViewPager() {

		// get viewpager from layout
		ViewPager pager = (ViewPager) findViewById(R.id.mythmote_pager);

		// if there is a viewpager set it up
		if (null != pager) {
			
			//get fragment manager
			FragmentManager fm = this.getSupportFragmentManager();
			
			//create fragment and header arrays
			fragmentArrayList = new ArrayList<Fragment>();
			headerArrayList = new ArrayList<String>();
			
			//mythmote navigation page fragment
			Fragment nav = Fragment.instantiate(this, NavigationFragment.class.getName());
			fragmentArrayList.add(nav);
			headerArrayList.add(this.getString(R.string.mythmote_page_navigation));

			//mythmote numbers page fragment
			Fragment num = Fragment.instantiate(this, NumbersFragment.class.getName());
			fragmentArrayList.add(num);
			headerArrayList.add(this.getString(R.string.mythmote_page_numbers));
			
			//mythmote action list fragment
			Fragment actions = Fragment.instantiate(this, MythmoteActionListFragment.class.getName());
			fragmentArrayList.add(actions);
			headerArrayList.add(this.getString(R.string.mythmote_page_actionlist));
			
			//set pager adapter and initial item
			pager.setAdapter(new MythmotePagerAdapter(this.getSupportFragmentManager()));
			pager.setCurrentItem(0);

		}
	}
	
	
	
	class MythmotePagerAdapter extends FragmentStatePagerAdapter {
		
        public MythmotePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }
        
        @Override
		public CharSequence getPageTitle(int position) {
			return headerArrayList.get(position);
		}

    }

}
