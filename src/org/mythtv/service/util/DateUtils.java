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
package org.mythtv.service.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Daniel Frey
 * @author Espen Fossen
 *
 */
public class DateUtils {

	public static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd" );
	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ss" );
	public static final DateTimeFormatter dateTimeFormatterPretty = DateTimeFormat.forPattern( "yyyy-MM-dd hh:mm a" );
	public static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern( "hh:mm a" );
    public static final DateTimeFormatter timeFormatter24 = DateTimeFormat.forPattern( "HH:mm" );
	public static final DateTimeFormatter hourFormatter = DateTimeFormat.forPattern( "hh:'00' a" );
    public static final DateTimeFormatter hourFormatter24 = DateTimeFormat.forPattern( "HH:'00'" );
	public static final DateTimeFormatter fileDateTimeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH-mm-ss" );

	public static DateTime getEndOfDay( DateTime day ) {
		return day.withTime( 23, 59, 59, 999 );		
	}

	public static DateTime getToday() {
		
		DateTime day = convertUtc( new DateTime( System.currentTimeMillis() ) );
		
		return getEndOfDay( day );		
	}
	
	public static DateTime getDaysFromToday( int days ) {
		
		DateTime day = convertUtc( new DateTime( System.currentTimeMillis() ) );
		day = day.plus( Period.days( days ) );
		
		return getEndOfDay( day );		
	}

	public static DateTime getYesterday() {
		
		DateTime day = convertUtc( new DateTime( System.currentTimeMillis() ) );
		
		return getPreviousDay( day );		
	}
	
	public static DateTime getPreviousDay( DateTime day ) {
		
		day = day.minus( Period.days( 1 ) );
		
		return getEndOfDay( day );
	}

	public static DateTime getNextDay( DateTime day ) {
		
		day = day.plus( Period.days( 1 ) );

		return getEndOfDay( day );
	}
	
	public static DateTime getNextDayAfterMythfilldatabase() {
		
		DateTime day = convertUtc( new DateTime( System.currentTimeMillis() ) );
		day = day.plus( Period.days( 1 ) );
		
		return day.withTime( 4, 0, 0, 0 );		
	}
	
	public static DateTime convertUtc( DateTime day ) {
		return day.withZone( DateTimeZone.UTC );
	}

    public static String getDateTimeUsingLocaleFormattingPretty( String dateTime, String dateFormat, String clockType ) {
        DateTime currentDateTime = dateTimeFormatterPretty.parseDateTime( dateTime );
        return getDateTimeUsingLocaleFormattingPretty( currentDateTime, dateFormat, clockType );
    }

    public static String getDateTimeUsingLocaleFormattingPretty( DateTime dateTime, String dateFormat, String clockType ) {
    	dateTime = dateTime.withZone( DateTimeZone.getDefault() );
        if(clockType != null && clockType.equals("24")) return DateTimeFormat.forPattern( dateFormat+" "+"HH:mm" ).print( dateTime );
        else return dateTimeFormatterPretty.print(dateTime);
    }

    public static String getDateTimeUsingLocaleFormattingPrettyDateOnly( DateTime dateTime, String dateFormat ) {
    	dateTime = dateTime.withZone( DateTimeZone.getDefault() );
        return DateTimeFormat.forPattern( dateFormat ).print( dateTime );
    }

    public static String getDateWithLocaleFormatting( String date, String dateFormat ) {
        DateTime currentDate = dateFormatter.parseDateTime( date );
        return getDateWithLocaleFormatting( currentDate, dateFormat );
    }

    public static String getDateWithLocaleFormatting( DateTime date, String dateFormat ) {
        return DateTimeFormat.forPattern( dateFormat ).print( date );
    }

    public static String getTimeWithLocaleFormatting( DateTime date, String clockType ) {
        if( clockType != null && clockType.equals( "24" ) ) return timeFormatter24.print( date );
        else return timeFormatter.print( date );
    }

}
