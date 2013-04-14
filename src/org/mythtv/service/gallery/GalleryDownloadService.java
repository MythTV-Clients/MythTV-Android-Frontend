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
package org.mythtv.service.gallery;

import android.content.Intent;
import android.util.Log;
import org.apache.commons.io.FileUtils;
import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.StringList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Espen A. Fossen
 */
public class GalleryDownloadService extends MythtvService {

    private static final String TAG = GalleryDownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.galleryDownload.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.galleryDownload.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";

    private File galleryDirectory = null;

    public GalleryDownloadService() {
        super( "GalleryDownloadService" );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent : enter");
        super.onHandleIntent(intent);

        galleryDirectory = mFileHelper.getProgramGroupsDataDirectory();
        if (null == galleryDirectory || !galleryDirectory.exists()) {
            Intent completeIntent = new Intent(ACTION_COMPLETE);
            completeIntent.putExtra(EXTRA_COMPLETE, "Program group location can not be found");
            sendBroadcast(completeIntent);

            Log.d(TAG, "onHandleIntent : exit, galleryDirectory does not exist");
            return;
        }

        if( !mNetworkHelper.isMasterBackendConnected() ) {
      			Intent completeIntent = new Intent( ACTION_COMPLETE );
      			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
      			sendBroadcast( completeIntent );

      			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
      			return;
        }

        if (intent.getAction().equals(ACTION_DOWNLOAD)) {
            Log.i(TAG, "onHandleIntent : DOWNLOAD action selected");

            try {
                download(intent);
            } catch (Exception e) {
                Log.e(TAG, "onHandleIntent : error", e);
            } finally {
                Intent completeIntent = new Intent(ACTION_COMPLETE);
                completeIntent.putExtra(EXTRA_COMPLETE, "Gallery Download Service Finished");
                sendBroadcast(completeIntent);
            }
        }
    }
	// internal helpers


    private void download(Intent intent) throws Exception {
        Log.v(TAG, "download : enter");

        try {
            ETagInfo eTag = ETagInfo.createEmptyETag();
            ResponseEntity<StringList> responseEntity = mMainApplication.getMythServicesApi().contentOperations().getFileList("Gallery", eTag);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                StringList files = responseEntity.getBody();

                List<String> test = new ArrayList<String>();
                test.add(files.getStringList()[0]);
                test.add(files.getStringList()[1]);
                test.add(files.getStringList()[100]);
                test.add(files.getStringList()[200]);

                for (String file : test) {

                    boolean galleryImageExists = false;
                    File checkImage = new File(galleryDirectory, file);
                    if (checkImage.exists()) {
                        galleryImageExists = true;
                    }

                    if (!galleryImageExists) {
                        ResponseEntity<byte[]> responseEntity2 = mMainApplication.getMythServicesApi().contentOperations().getImageFile("Gallery", file, 1024, 0, eTag);
                        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                            byte[] bytes = responseEntity2.getBody();
                            FileUtils.writeByteArrayToFile(checkImage, bytes);

                            Log.v(TAG, "download : downloaded gallery image file '" + checkImage.getName() + "'");
                        }
                    }

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "download : error creating image file", e);

        }

        Log.v( TAG, "download : exit" );
   	}

}
