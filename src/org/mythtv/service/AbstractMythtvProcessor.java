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
package org.mythtv.service;

import org.mythtv.client.MainApplication;

import android.content.Context;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythtvProcessor {

	protected static final String TAG = AbstractMythtvProcessor.class.getSimpleName();

	protected MainApplication application;
	protected Context mContext;

	public AbstractMythtvProcessor( Context context ) {
		mContext = context;
		application = (MainApplication) context.getApplicationContext();
	}

	public interface NotifyCallback {

		void notify( String message );
		
	}

	public MainApplication getMainApplication() {
		return application;
	}
	
}
