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
package org.mythtv.client.ui.media;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.MythtvApplicationContext;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.gallery.GalleryDownloadService;
import org.mythtv.service.util.RunningServiceHelper;

/**
 * @author Espen A. Fossen
 */
public class GalleryActivity extends Activity implements MythtvApplicationContext {

    private static final String TAG = GalleryActivity.class.getSimpleName();

    private RunningServiceHelper mRunningServiceHelper;

    @Override
    public MainApplication getMainApplication() {
        return (MainApplication) super.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate : enter");
        super.onCreate( savedInstanceState );

        mRunningServiceHelper = RunningServiceHelper.newInstance( this );

        setContentView( R.layout.activity_gallery );


//        if( !mRunningServiceHelper.isServiceRunning( GalleryDownloadService.class ) ) {
//            Intent serviceIntent = new Intent();
//            serviceIntent.setAction(GalleryDownloadService.ACTION_DOWNLOAD);
//            startService(serviceIntent);
//        }

        IntentFilter galleryDownloadFilter = new IntentFilter();
        galleryDownloadFilter.addAction(GalleryDownloadService.ACTION_COMPLETE);

        Log.v(TAG, "onCreate : exit");
    }

}
