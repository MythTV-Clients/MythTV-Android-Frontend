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
package org.mythtv.service.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Daniel Frey
 * 
 */
public class DateUtils {

	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy-MM-dd" );
	public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
	public static final SimpleDateFormat timeFormatter = new SimpleDateFormat( "hh:mm a" );

	public static Date getEndOfDay( Date day ) {
		return getEndOfDay( day, Calendar.getInstance() );
	}

	public static Date getToday() {
		
		Date day = new Date();
		
		Calendar today = Calendar.getInstance();
		today.setTime( day );
		
		return getEndOfDay( today.getTime() );		
	}
	
	public static Date getYesterday() {
		
		Date day = new Date();
		
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime( day );
		yesterday.add( Calendar.DATE, -1 );
		
		return getEndOfDay( yesterday.getTime() );		
	}
	
	public static Date getNextDay( Date day ) {
		
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.setTime( day );
		tomorrow.add( Calendar.DATE, 1 );
		
		return getEndOfDay( tomorrow.getTime() );
	}
	
	// internal helpers
	
	private static Date getEndOfDay( Date day, Calendar cal ) {
		
		if( null == day ) {
			day = new Date();
		}
		
		cal.setTime( day );
		cal.set( Calendar.HOUR_OF_DAY, cal.getMaximum( Calendar.HOUR_OF_DAY ) );
		cal.set( Calendar.MINUTE, cal.getMaximum( Calendar.MINUTE ) );
		cal.set( Calendar.SECOND, cal.getMaximum( Calendar.SECOND ) );
		cal.set( Calendar.MILLISECOND, cal.getMaximum( Calendar.MILLISECOND ) );
		
		return cal.getTime();
	}
	
}
