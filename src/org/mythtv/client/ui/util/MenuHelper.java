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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * @author Daniel Frey
 * @author Thomas G. Kenny Jr
 * 
 */
public class MenuHelper {

	public static final int ABOUT_ID = Menu.FIRST + 1;
	
	public static final int HELP_ID = Menu.FIRST + 10;
	public static final int FAQ_ID = Menu.FIRST + 11;
	public static final int TROUBLESHOOT_ID = Menu.FIRST + 12;
	public static final int ISSUES_ID = Menu.FIRST + 13;
	public static final int RELEASE_NOTES_ID = Menu.FIRST + 14;
	
	public static final int MYTHMOTE_ID = Menu.FIRST + 20;
	
	public static final int REFRESH_ID = Menu.FIRST + 100;

	public static final int EDIT_ID = Menu.FIRST + 200;
	public static final int SAVE_ID	= Menu.FIRST + 201;
	public static final int RESET_ID = Menu.FIRST + 202;
	public static final int DELETE_ID = Menu.FIRST + 203;
	public static final int WATCH_ID = Menu.FIRST + 204;
	public static final int ADD_ID = Menu.FIRST + 205;
	public static final int WATCH_ON_TV_ID = Menu.FIRST + 206;
	public static final int CLEAR_ID = Menu.FIRST + 207;
	
	public static final int GUIDE_ID = Menu.FIRST + 300;
	
	private static final String TAG = MenuHelper.class.getSimpleName();
	
	private static MenuHelper singleton = null;

	/**
	 * Returns the one and only MenuHelper. init() must be called before 
	 * any 
	 * @return
	 */
	public static MenuHelper getInstance() {
		if( null == singleton ) {
			
			synchronized( MenuHelper.class ) {

				if( null == singleton ) {
					singleton = new MenuHelper();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private MenuHelper() { }
	
	/**
	 * Build About MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem prefMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, EDIT_ID, Menu.NONE, resources.getString( R.string.menu_prefs ) );
    	menuItem.setIcon( android.R.drawable.ic_menu_preferences );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

	    return menuItem;
	}

	/**
	 * Build About MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem aboutMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, resources.getString( R.string.menu_about ) );
    	menuItem.setIcon( android.R.drawable.ic_menu_info_details );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );

	    return menuItem;
	}

	/**
	 * Handle About MenuItem
	 */
	public void handleAboutMenu( Context context ) {
		Log.v( TAG, "handleAboutMenu : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		startWebActivity( context, "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki" );

		Log.v( TAG, "handleAboutMenu : exit" );
	}
	
	/**
	 * Build the Help SubMenu
	 * 
	 * @param menu
	 * @return
	 */
	public SubMenu helpSubMenu( Context context, Menu menu ) {
		Log.v( TAG, "helpSubMenu : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		SubMenu subMenu = menu.addSubMenu( Menu.NONE, HELP_ID, Menu.NONE, resources.getString( R.string.menu_help ) );
		subMenu.setIcon( android.R.drawable.ic_menu_help );
		subMenu.add( Menu.NONE, FAQ_ID, Menu.NONE, resources.getString( R.string.menu_help_faq ) );
		subMenu.add( Menu.NONE, TROUBLESHOOT_ID, Menu.NONE, resources.getString( R.string.menu_help_troubleshoot ) );
		subMenu.add( Menu.NONE, ISSUES_ID, Menu.NONE, resources.getString( R.string.menu_help_issues ) );
		
		Log.v( TAG, "helpSubMenu : exit" );
		return subMenu;
	}
	
	/**
	 * Build FAQ MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem faqMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, FAQ_ID, Menu.NONE, resources.getString( R.string.menu_help_faq ) );
    	menuItem.setIcon( android.R.drawable.ic_menu_info_details );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );

	    return menuItem;
	}

	/**
	 * Handle FAQ MenuItem
	 */
	public void handleFaqMenu( Context context ) {
		Log.v( TAG, "handleFaqMenu : enter" );

		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		startWebActivity( context, "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki/FAQ" );

		Log.v( TAG, "handleFaqMenu : exit" );
	}

	/**
	 * Build Troubleshoot MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem troubleshootMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, TROUBLESHOOT_ID, Menu.NONE, resources.getString( R.string.menu_help_troubleshoot ) );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );

	    return menuItem;
	}

	/**
	 * Handle Troubleshoot MenuItem
	 */
	public void handleTroubleshootMenu( Context context ) {
		Log.v( TAG, "handleTroubleshootMenu : enter" );

		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		startWebActivity( context, "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki/Connection-Troubleshooting-Guidelines" );

		Log.v( TAG, "handleTroubleshootMenu : exit" );
	}

	/**
	 * Build Issues MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem issuesMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, ISSUES_ID, Menu.NONE, resources.getString( R.string.menu_help_issues ) );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );

	    return menuItem;
	}

	/**
	 * Handle Issues MenuItem
	 */
	public void handleIssuesMenu( Context context ) {
		Log.v( TAG, "handleIssuesMenu : enter" );

		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		startWebActivity( context, "https://github.com/MythTV-Clients/MythTV-Android-Frontend/issues" );

		Log.v( TAG, "handleIssuesMenu : exit" );
	}

	/**
	 * Build Release Notes MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem releaseNotesMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, RELEASE_NOTES_ID, Menu.NONE, resources.getString( R.string.menu_help_release_notes ) );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );

	    return menuItem;
	}

	/**
	 * Handle Release Notes MenuItem
	 */
	public void handleReleaseNotesMenu( Context context ) {
		Log.v( TAG, "handleReleaseNotesMenu : enter" );

		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		startWebActivity( context, "https://github.com/MythTV-Clients/MythTV-Android-Frontend/wiki/Release-Notes" );

		Log.v( TAG, "handleReleaseNotesMenu : exit" );
	}

	/**
	 * Build Mythmote MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem mythmoteMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, MYTHMOTE_ID, Menu.NONE, resources.getString( R.string.frontends_title ) );
			menuItem.setIcon( R.drawable.ic_menu_mythmote_default );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Refresh MenuItem
	 * 
	 * @param menu
	 * @param actionView Optional actionview. MenuItemRefreshAnimated objects are a good choice
	 * @return
	 */
	public MenuItem refreshMenuItem( Context context, final Menu menu, View actionView ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, resources.getString( R.string.menu_refresh ) );
			if(null == actionView){
				menuItem.setIcon( R.drawable.ic_menu_refresh );
			}else{
				menuItem.setActionView(actionView);
				actionView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						menu.performIdentifierAction(REFRESH_ID, 0);
					}});
			}
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
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
	public MenuItem editMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, EDIT_ID, Menu.NONE, resources.getString( R.string.menu_edit ) );
			menuItem.setIcon( android.R.drawable.ic_menu_edit );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

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
	public MenuItem saveMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, SAVE_ID, Menu.NONE, resources.getString( R.string.menu_save ) );
			menuItem.setIcon( android.R.drawable.ic_menu_save );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

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
	public MenuItem resetMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, RESET_ID, Menu.NONE, resources.getString( R.string.menu_reset ) );
			menuItem.setIcon( android.R.drawable.ic_menu_revert );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

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
	public MenuItem deleteMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, DELETE_ID, Menu.NONE, resources.getString( R.string.menu_delete ) );
			menuItem.setIcon( android.R.drawable.ic_menu_delete );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

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
	public MenuItem watchMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, WATCH_ID, Menu.NONE, resources.getString( R.string.menu_watch ) );
			menuItem.setIcon( android.R.drawable.ic_menu_view );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

			return menuItem;
		}
		
		return null;
	}
	
	/**
	 * Build Watch On Frontend MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem watchOnFrontendMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, WATCH_ON_TV_ID, Menu.NONE, resources.getString( R.string.menu_watch ) );
			menuItem.setIcon( android.R.drawable.ic_menu_send );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

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
	public MenuItem addMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, ADD_ID, Menu.NONE, resources.getString( R.string.menu_add ) );
			menuItem.setIcon( android.R.drawable.ic_menu_add );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

			return menuItem;
		}
		
		return null;
	}

	public MenuItem clearMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			MenuItem menuItem = menu.add( Menu.NONE, CLEAR_ID, Menu.NONE, resources.getString( R.string.menu_clear ) );
			menuItem.setIcon( android.R.drawable.ic_menu_close_clear_cancel );
			menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

			return menuItem;
		}
		
		return null;
	}

	/**
	 * Build Guide Day MenuItem
	 * 
	 * @param menu
	 * @return
	 */
	public MenuItem guideDayMenuItem( Context context, Menu menu ) {
		
		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

	    MenuItem menuItem = menu.add( Menu.NONE, GUIDE_ID, Menu.NONE, resources.getString( R.string.menu_guide_day ) );
		menuItem.setIcon( android.R.drawable.ic_menu_day );
    	menuItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

	    return menuItem;
	}

	// internal helpers
	
	private void startWebActivity( Context context, String url ) {
		Log.v( TAG, "startActivity : enter" );

		if( null == context ) 
			throw new RuntimeException( "MenuHelper is not initialized" );
		
		Resources resources = context.getResources();

		if( NetworkHelper.getInstance().isNetworkConnected( context ) ) {
			Intent webIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
			context.startActivity( webIntent );
		} else {
			Toast.makeText( context, resources.getString( R.string.menu_help_not_connected ), Toast.LENGTH_SHORT ).show();
		}
		
		Log.v( TAG, "startActivity : exit" );
	}
}
