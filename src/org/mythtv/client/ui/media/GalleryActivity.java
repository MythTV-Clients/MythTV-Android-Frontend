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

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.StringList;
import org.mythtv.services.api.myth.SettingList;
import org.mythtv.services.api.myth.StorageGroupDirectory;
import org.mythtv.services.api.myth.StorageGroupDirectoryList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;

/**
 * @author Espen A. Fossen
 */
public class GalleryActivity extends AbstractMythtvFragmentActivity implements GalleryGridAdapter.OnLoadingImagesListener {

    private static final String TAG = GalleryActivity.class.getSimpleName();
    public static List<GalleryImageItem> images = new ArrayList<GalleryImageItem>();

    public static boolean IMAGE_LIST_DOWNLOADED = false;

    private boolean hasBackendGallerySG = false;
    private LocationProfile mLocationProfile;


    @Override
    public MainApplication getMainApplication() {
        return (MainApplication) super.getApplicationContext();
    }

    private GridView gridView;

    /* (non-Javadoc)
     * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate : enter");
        super.onCreate(savedInstanceState);

        // todo: Temp fix for unpredictable .findConnectedProfile() method
        List<LocationProfile> profiles = mLocationProfileDaoHelper.findAll(GalleryActivity.this);
        mLocationProfile = profiles.get(0);

        setContentView(R.layout.activity_gallery);

        gridView = (GridView) findViewById(R.id.gallery_gridview);

        if(IMAGE_LIST_DOWNLOADED){
            GalleryGridAdapter adapter = new GalleryGridAdapter(GalleryActivity.this, mLocationProfile, this);
            gridView.setAdapter(adapter);
        } else {
            new LoadFileListTask(this).execute();
        }

        Log.v(TAG, "onCreate : exit");
    }

    /* (non-Javadoc)
	 * @see org.mythtv.client.ui.media.GalleryGridAdapter.OnLoadingImagesListener#notifyStart()
	 */
	@Override
	public void notifyStart() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.media.GalleryGridAdapter.OnLoadingImagesListener#notifyEnd()
	 */
	@Override
	public void notifyEnd() {
		// TODO Auto-generated method stub
		
	}

	private class LoadFileListTask extends AsyncTask<Void, Void, Void> {

        GalleryActivity activity;
        boolean backendAndFrontendShareHostname = false;
        boolean galleryDirPresentInSettings = false;
        boolean createStorageGroup = false;
        final String gallerySGName = "Gallery";
        final String gallerySetting = "GalleryDir";
        String galleryDir = "";

        public LoadFileListTask(GalleryActivity galleryActivity) {
            activity = galleryActivity;
        }

        private GalleryActivity getActivity() {
            return activity;
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                ETagInfo eTag = ETagInfo.createEmptyETag();

                // 800p screen / 3 columns = 266,67 for each
                // 720p screen / 3 columns = 240 for each
                String previewWidth = "256";

                // Check if StorageGroup Gallery actually exists, doing an GetFileList will return Default SG if Gallery SG is not present.,
                ResponseEntity<StorageGroupDirectoryList> responseEntity = mMythtvServiceHelper.getMythServicesApi(mLocationProfile).mythOperations().getStorageGroupDirectories(gallerySGName, mLocationProfile.getHostname(), eTag);
                if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                    StorageGroupDirectoryList storageGroups = responseEntity.getBody();
                    for(StorageGroupDirectory sg: storageGroups.getStorageGroupDirectories().getStorageGroupDirectories()){
                        if(sg.getGroupName().equals(gallerySGName)) hasBackendGallerySG = true;
                    }
                }

                if(hasBackendGallerySG){
                    getImageList(images, gallerySGName, previewWidth);

                } else {

                    backendAndFrontendShareHostname = isConnectedProfileInHostsList();

                    if(backendAndFrontendShareHostname){
                        ResponseEntity<SettingList> responseEntity2 = mMythtvServiceHelper.getMythServicesApi(mLocationProfile).mythOperations().getSetting(mLocationProfile.getHostname(), gallerySetting, "", eTag);
                        if(responseEntity2.getStatusCode().equals(HttpStatus.OK)){
                            SettingList settingList = responseEntity2.getBody();
                            galleryDir = settingList.getSetting().getSettings().get(gallerySetting);
                            if(galleryDir != null && !"".equalsIgnoreCase(galleryDir)){
                                galleryDirPresentInSettings = true;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "download : error getting file list", e);

            }
              return null;
        }

        private boolean isConnectedProfileInHostsList() {
            ETagInfo eTag = ETagInfo.createEmptyETag();

            ResponseEntity<StringList> responseEntity = mMythtvServiceHelper.getMythServicesApi(mLocationProfile).mythOperations().getHosts(eTag);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                StringList hosts = responseEntity.getBody();
                for (String host : hosts.getStringList()) {
                    if (host.equals(mLocationProfile.getHostname())) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void getImageList(List<GalleryImageItem> images, String gallerySGName, String previewWidth) {
            ETagInfo eTag = ETagInfo.createEmptyETag();

            ResponseEntity<StringList> responseEntity = mMythtvServiceHelper.getMythServicesApi(mLocationProfile).contentOperations().getFileList(gallerySGName, eTag);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                StringList filesOnStorageGroup = responseEntity.getBody();
                // TODO: Add different types of sorting, and filtering
                //String imageUri = mLocationProfileDaoHelper.findConnectedProfile().getUrl() + "Content/GetImageFile?StorageGroup="+gallerySGName+"&Width="+previewWidth+"&FileName=";

                for(String file: filesOnStorageGroup.getStringList()){

                    // First look for image suffixes, then skip files with reoccurring WxH suffixes made by MythTVs getImageFile scalar.
                    if(file.matches(".+(?i)(jpg|png|gif|bmp)$") && !file.matches(".+(?i)(jpg|png|gif|bmp).\\d+x\\d.(?i)(jpg|png|gif|bmp)$")){
                        images.add(new GalleryImageItem(0, "", file));
                        //images.add(new GalleryImageItem(0, "", imageUri+file, true));
                    }
                }
                IMAGE_LIST_DOWNLOADED = true;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(hasBackendGallerySG){
                GalleryGridAdapter adapter = new GalleryGridAdapter(GalleryActivity.this, mLocationProfile, GalleryActivity.this );
                gridView.setAdapter(adapter);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                if(backendAndFrontendShareHostname){

                    EditText input = null;

                    if(galleryDirPresentInSettings){
                        builder.setMessage(getResources().getString(R.string.gallery_sg_exist_create)+mLocationProfile.getHostname()+getResources().getString(R.string.gallery_sg_exist_create2));
                    } else {
                        builder.setMessage(R.string.gallery_sg_create);

                        input = new EditText(getActivity());
                        input.setHint(R.string.gallery_sg_create_hint);
                        builder.setView(input);
                    }
                    final EditText finalInput = input;
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            clickedPosButton(id, finalInput);
                        }
                    });
                    builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            clickedNegButton(id);
                        }
                    });
                } else {
                    builder.setMessage(R.string.gallery_sg_error);
                }

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        private void clickedPosButton(int result, EditText directoryName){

            createStorageGroup = true;
            if(directoryName != null){
                galleryDir = directoryName.getText().toString();
            }
            new CreateSGTask(getActivity()).execute(gallerySGName, galleryDir);

        }
   }

    private class CreateSGTask extends AsyncTask<String, Void, Bool> {

        GalleryActivity activity;

        private CreateSGTask(GalleryActivity galleryActivity) {
            activity = galleryActivity;
        }

        private GalleryActivity getActivity() {
            return activity;
        }

        @Override
        protected Bool doInBackground(String... params) {

            Bool bool = new Bool();
            bool.setBool(false);
            if(params[1] != null && !"".equalsIgnoreCase(params[1])){

                // AddStorageGroupDir
                ResponseEntity<Bool> responseEntity = mMythtvServiceHelper.getMythServicesApi(mLocationProfile).mythOperations().addStorageGroupDir(params[0], params[1], mLocationProfile.getHostname());
                if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                    bool = responseEntity.getBody();
                    return bool;
                }
            }
            return bool;
        }

        @Override
        protected void onPostExecute(Bool bool) {
            super.onPostExecute(bool);

            if (bool.getBool()) {
                // TODO: Check if there is better way to "jump" back to Activity.
                Intent gallery = new Intent(getMainApplication().getApplicationContext(), GalleryActivity.class);
                startActivity(gallery);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.gallery_sg_failed);
                builder.setNeutralButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clickedNegButton(id);
                    }

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }


    }

    private void clickedNegButton(int id) {
        finish();
    }
}
