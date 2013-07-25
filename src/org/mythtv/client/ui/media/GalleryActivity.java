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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.MythtvApplicationContext;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.StringList;
import org.mythtv.services.api.myth.SettingList;
import org.mythtv.services.api.myth.StorageGroupDirectory;
import org.mythtv.services.api.myth.StorageGroupDirectoryList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Espen A. Fossen
 */
public class GalleryActivity extends Activity implements MythtvApplicationContext {

    private static final String TAG = GalleryActivity.class.getSimpleName();

    private LocationProfileDaoHelper mLocationProfileDaoHelper;

    @Override
    public MainApplication getMainApplication() {
        return (MainApplication) super.getApplicationContext();
    }

    private GridView gridView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate : enter");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);
        mLocationProfileDaoHelper = new LocationProfileDaoHelper( this );

        gridView = (GridView) findViewById(R.id.gallery_gridview);
        new LoadFileListTask().execute();
        Log.v(TAG, "onCreate : exit");
    }

    private class LoadFileListTask extends AsyncTask<Void, Void, List<GalleryImageItem>> {

        @Override
        protected List<GalleryImageItem> doInBackground(Void... params) {

            List<GalleryImageItem> images = new ArrayList<GalleryImageItem>();

            try {
                ETagInfo eTag = ETagInfo.createEmptyETag();

                boolean hasBackendGallerySG = false;
                boolean isBackendAndFrontendShareHostname = false;
                boolean isGalleryDirPresentInSettings = false;
                String gallerySGName = "Gallery";
                String galleryDir = "";
                String gallerySetting = "GalleryDir";

                // 800p screen / 3 columns = 266,67 for each
                // 720p screen / 3 columns = 240 for each
                String previewWidth = "256";
                LocationProfile profile = mLocationProfileDaoHelper.findConnectedProfile();

                // Check if StorageGroup Gallery actually exists, doing an GetFileList will return Default SG if Gallery SG is not present.,
                ResponseEntity<StorageGroupDirectoryList> responseEntity = getMainApplication().getMythServicesApi().mythOperations().getStorageGroupDirectories(gallerySGName, profile.getHostname(), eTag);
                if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                    StorageGroupDirectoryList storageGroups = responseEntity.getBody();
                    for(StorageGroupDirectory sg: storageGroups.getStorageGroupDirectories().getStorageGroupDirectories()){
                        if(sg.getGroupName().equals(gallerySGName)) hasBackendGallerySG = true;
                    }
                }

                if(hasBackendGallerySG){
                    getImages(images, gallerySGName, previewWidth);

                } else {

                    // Get all hosts registered on Backend
                    isBackendAndFrontendShareHostname = getHosts(profile);

                    if(isBackendAndFrontendShareHostname){
                        ResponseEntity<SettingList> responseEntity2 = getMainApplication().getMythServicesApi().mythOperations().getSetting(profile.getHostname(), gallerySetting, "", eTag);
                        if(responseEntity2.getStatusCode().equals(HttpStatus.OK)){
                            SettingList settingList = responseEntity2.getBody();
                            galleryDir = settingList.getSetting().getSettings().get(gallerySetting);
                            if(galleryDir != null || !"".equalsIgnoreCase(galleryDir)){
                                isGalleryDirPresentInSettings = true;
                            }
                        }
                    }

                    if(isBackendAndFrontendShareHostname && isGalleryDirPresentInSettings){

                        // AddStorageGroupDir
                        ResponseEntity<Bool> responseEntity3 = getMainApplication().getMythServicesApi().mythOperations().addStorageGroupDir(gallerySGName, galleryDir, profile.getHostname());
                        if (responseEntity3.getStatusCode().equals(HttpStatus.OK)) {
                            Bool bool = responseEntity3.getBody();
                            if(bool.getBool()){
                                // success
                                String test = "";
                            }
                        }
                    }

                    getImages(images,gallerySGName, previewWidth);


                    // Just for testing, RemoveStorageGroupDir
                    ResponseEntity<Bool> responseEntity4 = getMainApplication().getMythServicesApi().mythOperations().removeStorageGroupDirectory(gallerySGName, galleryDir, profile.getHostname());
                    if (responseEntity4.getStatusCode().equals(HttpStatus.OK)) {
                        Bool bool = responseEntity4.getBody();
                        if(bool.getBool()){
                            // success
                            String test = "";
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "download : error getting file list", e);

            }

            return images;
       }

        private boolean getHosts(LocationProfile profile) {
            ETagInfo eTag = ETagInfo.createEmptyETag();

            ResponseEntity<StringList> responseEntity = getMainApplication().getMythServicesApi().mythOperations().getHosts(eTag);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                StringList hosts = responseEntity.getBody();
                for(String host : hosts.getStringList()){
                    if(host.equals(profile.getHostname())){
                        return true;
                    }
                }
            }
            return false;
        }

        private void getImages(List<GalleryImageItem> images, String gallerySGName, String previewWidth) {
            ETagInfo eTag = ETagInfo.createEmptyETag();

            ResponseEntity<StringList> responseEntity = getMainApplication().getMythServicesApi().contentOperations().getFileList(gallerySGName, eTag);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                StringList filesOnStorageGroup = responseEntity.getBody();
                // TODO: Add different types of sorting, and filtering
                String imageUri = mLocationProfileDaoHelper.findConnectedProfile().getUrl() + "Content/GetImageFile?StorageGroup="+gallerySGName+"&Width="+previewWidth+"&FileName=";

                for(String file: filesOnStorageGroup.getStringList()){

                    // First look for image files, then skip files with reoccuring WxH suffixes made by MythTV's getImageFile scaler.
                    if(file.matches(".+(?i)(jpg|png|gif|bmp)$") && !file.matches(".+(?i)(jpg|png|gif|bmp).\\d+x\\d.(?i)(jpg|png|gif|bmp)$")){
                        images.add(new GalleryImageItem(0, "", imageUri+file, true));
                    }
                }
            }
        }

        @Override
       protected void onPostExecute(List<GalleryImageItem> imageItems) {
           GalleryGridAdapter adapter = new GalleryGridAdapter(GalleryActivity.this, imageItems);
           gridView.setAdapter(adapter);

       }
   }


}
