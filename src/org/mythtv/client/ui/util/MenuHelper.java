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
package org.mythtv.client.ui.util;

import org.mythtv.R;
import org.mythtv.service.util.NetworkHelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public class MenuHelper {

	public static final int ABOUT_ID = Menu.FIRST + 1;
	
	public static final int HELP_ID = Menu.FIRST + 10;
	public static final int FAQ_ID = Menu.FIRST + 11;
	public static final int TROUBLESHOOT_ID = Menu.FIRST + 12;
	public static final int ISSUES_ID = Menu.FIRST + 13;
	
	public static final int MYTHMOTE_ID = Menu.FIRST + 20;
	
	public static final int REFRESH_ID = Menu.FIRST + 100;

	public static final int EDIT_ID = Menu.FIRST + 200;
	public static final int SAVE_ID	= Menu.FIRST + 201;
	public static final int RESET_ID = Menu.FIRST + 202;
	public static final int DELETE_ID = Menu.FIRST + 203;
	public static final int WATCH_ID = Menu.FIRST + 204;
	public static final int ADD_ID = Menu.FIRST + 205;

	private static final String TAG = MenuHelper.class.getSimpleName();
	
	private Context mContext;
	private NetworkHelper mNetworkHelper;
	private Resources mResources;
	
	public MenuHelper( Context context ) {
		this.mContext = context;
		
		mResources = mContext.getResources();
		mNetworkHelper = new NetworkHelper( mContext );
	}
	
	/**
	 * Build About MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem aboutMenuItem( Menu menu ) {
		
	    MenuItem menuItem = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, mResources.getString( R.string.menu_about ) );
    	menuItem.setIcon( android.R.drawable.ic_menu_info_details );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    }

	    return menuItem;
	}

	/**
	 * Handle About MenuItem
	 */
	public void handleAboutMenu() {
		Log.v( TAG, "handleAboutMenu : enter" );
		
		startWebActivity( "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki" );

		Log.v( TAG, "handleAboutMenu : exit" );
	}
	
	/**
	 * Build the Help SubMenu
	 * 
	 * @param menu
	 * @return
	 */
	public SubMenu helpSubMenu( Menu menu ) {
		Log.v( TAG, "helpSubMenu : enter" );
		
		SubMenu subMenu = menu.addSubMenu( Menu.NONE, HELP_ID, Menu.NONE, mResources.getString( R.string.menu_help ) );
		subMenu.setIcon( android.R.drawable.ic_menu_help );
		subMenu.add( Menu.NONE, FAQ_ID, Menu.NONE, mResources.getString( R.string.menu_help_faq ) );
		subMenu.add( Menu.NONE, TROUBLESHOOT_ID, Menu.NONE, mResources.getString( R.string.menu_help_troubleshoot ) );
		subMenu.add( Menu.NONE, ISSUES_ID, Menu.NONE, mResources.getString( R.string.menu_help_issues ) );
		
		Log.v( TAG, "helpSubMenu : exit" );
		return subMenu;
	}
	
	/**
	 * Build FAQ MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem faqMenuItem( Menu menu ) {
		
	    MenuItem menuItem = menu.add( Menu.NONE, FAQ_ID, Menu.NONE, mResources.getString( R.string.menu_help_faq ) );
    	menuItem.setIcon( android.R.drawable.ic_menu_info_details );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    }

	    return menuItem;
	}

	/**
	 * Handle FAQ MenuItem
	 */
	public void handleFaqMenu() {
		Log.v( TAG, "handleFaqMenu : enter" );

		startWebActivity( "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki/FAQ" );

		Log.v( TAG, "handleFaqMenu : exit" );
	}

	/**
	 * Build Troubleshoot MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem troubleshootMenuItem( Menu menu ) {
		
	    MenuItem menuItem = menu.add( Menu.NONE, TROUBLESHOOT_ID, Menu.NONE, mResources.getString( R.string.menu_help_troubleshoot ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    }

	    return menuItem;
	}

	/**
	 * Handle Troubleshoot MenuItem
	 */
	public void handleTroubleshootMenu() {
		Log.v( TAG, "handleTroubleshootMenu : enter" );

		startWebActivity( "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki/Connection-Troubleshooting-Guidelines" );

		Log.v( TAG, "handleTroubleshootMenu : exit" );
	}

	/**
	 * Build Issues MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem issuesMenuItem( Menu menu ) {
		
	    MenuItem menuItem = menu.add( Menu.NONE, ISSUES_ID, Menu.NONE, mResources.getString( R.string.menu_help_issues ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    }

	    return menuItem;
	}

	/**
	 * Handle Issues MenuItem
	 */
	public void handleIssuesMenu() {
		Log.v( TAG, "handleIssuesMenu : enter" );

		startWebActivity( "https://github.com/MythTV-Clients/MythTV-Android-Frontend/issues" );

		Log.v( TAG, "handleIssuesMenu : exit" );
	}

	/**
	 * Build Mythmote MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem mythmoteMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, MYTHMOTE_ID, Menu.NONE, mResources.getString( R.string.frontends_title ) );
			menuItem.setIcon( R.drawable.ic_menu_mythmote_default );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Refresh MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem refreshMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, mResources.getString( R.string.menu_refresh ) );
			menuItem.setIcon( R.drawable.ic_menu_refresh );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Edit MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem editMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, EDIT_ID, Menu.NONE, mResources.getString( R.string.menu_edit ) );
			menuItem.setIcon( android.R.drawable.ic_menu_edit );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Save MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem saveMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, SAVE_ID, Menu.NONE, mResources.getString( R.string.menu_save ) );
			menuItem.setIcon( android.R.drawable.ic_menu_save );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Reset MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem resetMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, RESET_ID, Menu.NONE, mResources.getString( R.string.menu_reset ) );
			menuItem.setIcon( android.R.drawable.ic_menu_revert );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Delete MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem deleteMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, DELETE_ID, Menu.NONE, mResources.getString( R.string.menu_delete ) );
			menuItem.setIcon( android.R.drawable.ic_menu_delete );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Watch MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem watchMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, WATCH_ID, Menu.NONE, mResources.getString( R.string.menu_watch ) );
			menuItem.setIcon( android.R.drawable.ic_menu_view );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Add MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	public MenuItem addMenuItem( Menu menu ) {
		
		if( mNetworkHelper.isNetworkConnected() ) {
			MenuItem menuItem = menu.add( Menu.NONE, ADD_ID, Menu.NONE, mResources.getString( R.string.menu_add ) );
			menuItem.setIcon( android.R.drawable.ic_menu_add );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
			}

			return menuItem;
		}
		
		return null;
	}

	// internal helpers
	
	private void startWebActivity( String url ) {
		Log.v( TAG, "startActivity : enter" );

		if( mNetworkHelper.isNetworkConnected() ) {
			Intent webIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
			mContext.startActivity( webIntent );
		} else {
			Toast.makeText( mContext, mResources.getString( R.string.menu_help_not_connected ), Toast.LENGTH_SHORT ).show();
		}
		
		Log.v( TAG, "startActivity : exit" );
	}
}
