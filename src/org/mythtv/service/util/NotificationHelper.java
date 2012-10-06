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

import java.text.DecimalFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author Daniel Frey
 *
 */
public class NotificationHelper {

	public enum NotificationType {
		DOWNLOAD,
		UPLOAD
	}

	private static DecimalFormat formatter = new DecimalFormat( "###" );
	
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;
    
    public NotificationHelper( Context context ) {
        mContext = context;
    }

    @SuppressWarnings( "deprecation" )
	public void createNotification( String title, String tickerText, NotificationType type ) {

    	mNotificationManager = (NotificationManager) mContext.getSystemService( Context.NOTIFICATION_SERVICE );

    	mContentTitle = tickerText;
        CharSequence contentText = "";

        int icon = 0;
        switch( type ) {
        case DOWNLOAD :
        	icon = android.R.drawable.stat_notify_sync;
        	        	
        	contentText = "0% complete";
        	break;
        case UPLOAD :
        	icon = android.R.drawable.stat_sys_download;
        	
        	contentText = "";
        	break;
        }
        long when = System.currentTimeMillis();
        mNotification = new Notification( icon, tickerText, when );

        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity( mContext, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( mContext, mContentTitle, contentText, mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( NOTIFICATION_ID, mNotification );
    }

    @SuppressWarnings( "deprecation" )
	public void progressUpdate( double percentageComplete ) {

    	CharSequence contentText = formatter.format( percentageComplete ) + "% complete";

    	mNotification.setLatestEventInfo( mContext, mContentTitle, contentText, mContentIntent );
    	mNotificationManager.notify( NOTIFICATION_ID, mNotification );
    }

    public void completed()    {
        mNotificationManager.cancel( NOTIFICATION_ID );
    }

}
