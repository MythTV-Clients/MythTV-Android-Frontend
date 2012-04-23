/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mythtv.client.ui.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;

/**
 * An assortment of UI helpers.
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
