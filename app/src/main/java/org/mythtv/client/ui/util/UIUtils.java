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

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;

/**
 * An assortment of UI helpers.
 *
 * @author Daniel Frey
 *
 */
public class UIUtils {

	private static final int BRIGHTNESS_THRESHOLD = 130;

	/**
	 * Calculate whether a color is light or dark, based on a commonly known
	 * brightness formula.
	 * 
	 * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
	 */
	public static boolean isColorDark( int color ) {
		return ( ( 30 * Color.red( color ) + 59 * Color.green( color ) + 11 * Color.blue( color ) ) / 100 ) <= BRIGHTNESS_THRESHOLD;
	}

	public static boolean isHoneycomb() {
		// Can use static final constants like HONEYCOMB, declared in later
		// versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean isTablet( Context context ) {
		return ( context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK ) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isHoneycombTablet( Context context ) {
		return isHoneycomb() && isTablet( context );
	}

	public static long getCurrentTime( final Context context ) {
		// SharedPreferences prefs = context.getSharedPreferences("mock_data",
		// 0);
		// prefs.edit().commit();
		// return prefs.getLong("mock_current_time",
		// System.currentTimeMillis());
		return System.currentTimeMillis();
	}

}
