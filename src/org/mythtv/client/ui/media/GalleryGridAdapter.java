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
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.StringList;
import org.mythtv.services.api.myth.SettingList;
import org.mythtv.services.api.myth.StorageGroupDirectory;
import org.mythtv.services.api.myth.StorageGroupDirectoryList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * @author Espen A. Fossen
 */
public class GalleryGridAdapter extends BaseAdapter {

	private static final String TAG = GalleryGridAdapter.class.getSimpleName();
	
	private final MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();

	private final Context mContext;
	private final LocationProfile mLocationProfile;
	
	public static List<GalleryImageItem> mImageItems = new ArrayList<GalleryImageItem>();
	
	private ImageLoader imageLoader;
	private String baseUrl;
	private String gallerySGName = "Gallery";
	private String previewWidth = "256";

	private boolean hasBackendGallerySG = false;

	final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory( true )
			.cacheOnDisc( true )
			.build();

	private OnLoadingImagesListener listener;
	
	public interface OnLoadingImagesListener {
		
		void notifyStart();
		
		void notifyEnd();
		
	}
	
	public GalleryGridAdapter( Context context, LocationProfile locationProfile, OnLoadingImagesListener listener ) {
		
		this.mContext = context;
		this.mLocationProfile = locationProfile;
		this.listener = listener;
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init( ImageLoaderConfiguration.createDefault( context ) );
		
		baseUrl = locationProfile.getUrl() + "Content/GetImageFile?StorageGroup=" + gallerySGName + "&FileName=";
		
		if( mImageItems.isEmpty() ) {
			
			refresh();
			
		}
		
	}

	public void refresh() {
		Log.v( TAG, "refresh : enter" );

		new LoadFileListTask().execute();
		
		Log.v( TAG, "refresh : exit" );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return mImageItems.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem( int position ) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId( int position ) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView( final int position, View convertView, ViewGroup parent ) {
		
		final ViewHolder holder;

		if( convertView == null ) {
		
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = mInflater.inflate( R.layout.activity_gallery_griditem, null );

			holder = new ViewHolder();
			// Use with griditem_tree
			// holder.title = (TextView)
			// convertView.findViewById(R.id.grid_item_text);
			holder.image = (ImageView) convertView.findViewById( R.id.grid_item_image );

			holder.image.setImageResource( mImageItems.get( position ).getImageId() );
			// Use with griditem_tree
			// holder.title.setText(imageItems.get(position).getTitle());
			//int h = mContext.getResources().getDisplayMetrics().densityDpi;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );
			convertView.setLayoutParams( new GridView.LayoutParams( params ) );
			convertView.setTag( holder );

		} else {

			holder = (ViewHolder) convertView.getTag();

		}

		holder.image.setOnClickListener( new View.OnClickListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View view ) {
				
				Intent galleryImageIntent = new Intent( mContext, GalleryPagerActivity.class );
				
				Bundle bundle = new Bundle();
				bundle.putInt( "position", position );
				bundle.putString( "baseUrl", baseUrl );
				galleryImageIntent.putExtras( bundle );
				mContext.startActivity( galleryImageIntent );
				
			}
		
		});

		imageLoader.displayImage( baseUrl + mImageItems.get( position ).getUrl() + "&Width=" + previewWidth, holder.image, options, new SimpleImageLoadingListener() {
					
			/* (non-Javadoc)
			 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(java.lang.String, android.view.View, android.graphics.Bitmap)
			 */
			@Override
			public void onLoadingComplete( String imageUri, View view, Bitmap loadedImage ) {
				Animation anim = AnimationUtils.loadAnimation( mContext, android.R.anim.fade_in );
				holder.image.setAnimation( anim );
				anim.start();
			}
		
		});

		return convertView;
	}

	static class ViewHolder {
		TextView title;
		ImageView image;
	}

	// internal helpers
	
	private void notifyParentStart() {

		listener.notifyStart();

	}
	
	private void notifyParentEnd() {

		listener.notifyEnd();

	}
	
	private class LoadFileListTask extends AsyncTask<Void, Void, Void> {

		final String gallerySGName = "Gallery";
		final String gallerySetting = "GalleryDir";

		boolean backendAndFrontendShareHostname = false;
		boolean galleryDirPresentInSettings = false;
		String galleryDir = "";

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground( Void... params ) {
			Log.v( TAG, "LoadFileListTask.doInBackground : enter" );

			try {
				ETagInfo eTag = ETagInfo.createEmptyETag();

				// 800p screen / 3 columns = 266,67 for each
				// 720p screen / 3 columns = 240 for each
				String previewWidth = "256";

				// Check if StorageGroup Gallery actually exists, doing an
				// GetFileList will return Default SG if Gallery SG is not
				// present.,
				ResponseEntity<StorageGroupDirectoryList> responseEntity = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).mythOperations().getStorageGroupDirectories( gallerySGName, mLocationProfile.getHostname(), eTag );
				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					
					StorageGroupDirectoryList storageGroups = responseEntity.getBody();
					for( StorageGroupDirectory sg : storageGroups.getStorageGroupDirectories().getStorageGroupDirectories() ) {
						if( sg.getGroupName().equals( gallerySGName ) )
							hasBackendGallerySG = true;
					}
					
				}

				if( hasBackendGallerySG ) {
					
					getImageList( previewWidth );

				} else {

					backendAndFrontendShareHostname = isConnectedProfileInHostsList();

					if( backendAndFrontendShareHostname ) {
						ResponseEntity<SettingList> responseEntity2 = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).mythOperations().getSetting( mLocationProfile.getHostname(), gallerySetting, "", eTag );
						if( responseEntity2.getStatusCode().equals( HttpStatus.OK ) ) {

							SettingList settingList = responseEntity2.getBody();
							galleryDir = settingList.getSetting().getSettings().get( gallerySetting );
							if( galleryDir != null && !"".equalsIgnoreCase( galleryDir ) ) {
								galleryDirPresentInSettings = true;
							}
							
						}
						
					}
					
				}

			} catch( Exception e ) {
				Log.e( TAG, "LoadFileListTask.doInBackground : error getting file list", e );
			}
			
			Log.v( TAG, "LoadFileListTask.doInBackground : exit" );
			return null;
		}

		private boolean isConnectedProfileInHostsList() {
			Log.v( TAG, "LoadFileListTask.isConnectedProfileInHostsList : enter" );
		
			ETagInfo eTag = ETagInfo.createEmptyETag();

			ResponseEntity<StringList> responseEntity = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).mythOperations().getHosts( eTag );
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			
				StringList hosts = responseEntity.getBody();
				for( String host : hosts.getStringList() ) {
				
					if( host.equals( mLocationProfile.getHostname() ) ) {
						Log.v( TAG, "LoadFileListTask.isConnectedProfileInHostsList : exit, true" );

						return true;
					}
			
				}
			
			}
			
			Log.v( TAG, "LoadFileListTask.isConnectedProfileInHostsList : exit, false" );
			return false;
		}

		private void getImageList( String previewWidth ) {
			Log.v( TAG, "LoadFileListTask.getImageList : enter" );

			ETagInfo eTag = ETagInfo.createEmptyETag();

			ResponseEntity<StringList> responseEntity = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).contentOperations().getFileList( gallerySGName, eTag );
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				
				mImageItems = new ArrayList<GalleryImageItem>();
				
				StringList filesOnStorageGroup = responseEntity.getBody();
				// TODO: Add different types of sorting, and filtering
				// String imageUri =
				// mLocationProfileDaoHelper.findConnectedProfile().getUrl() +
				// "Content/GetImageFile?StorageGroup="+gallerySGName+"&Width="+previewWidth+"&FileName=";
				
				for( String file : filesOnStorageGroup.getStringList() ) {

					// First look for image suffixes, then skip files with
					// reoccurring WxH suffixes made by MythTVs getImageFile
					// scalar.
					if( file.matches( ".+(?i)(jpg|png|gif|bmp)$" ) && !file.matches( ".+(?i)(jpg|png|gif|bmp).\\d+x\\d.(?i)(jpg|png|gif|bmp)$" ) ) {
						GalleryImageItem imageItem = new GalleryImageItem( 0, "", file );
						mImageItems.add( imageItem );
						// images.add(new GalleryImageItem(0, "", imageUri+file,
						// true));
					}
				
				}
			
			}
			
			Log.v( TAG, "LoadFileListTask.getImageList : exit" );
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( Void aVoid ) {
			Log.v( TAG, "LoadFileListTask.onPostExecute : enter" );

			notifyParentEnd();
			
			if( hasBackendGallerySG ) {
			
				notifyDataSetChanged();
				
			} else {
				
				AlertDialog.Builder builder = new AlertDialog.Builder( mContext );

				if( backendAndFrontendShareHostname ) {

					EditText input = null;

					if( galleryDirPresentInSettings ) {
						builder.setMessage( mContext.getResources().getString( R.string.gallery_sg_exist_create )
								+ mLocationProfile.getHostname()
								+ mContext.getResources().getString( R.string.gallery_sg_exist_create2 ) );
					} else {
						builder.setMessage( R.string.gallery_sg_create );

						input = new EditText( mContext );
						input.setHint( R.string.gallery_sg_create_hint );
						builder.setView( input );
					}
					
					final EditText finalInput = input;
					builder.setPositiveButton( R.string.btn_ok, new DialogInterface.OnClickListener() {
				
						/* (non-Javadoc)
						 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
						 */
						public void onClick( DialogInterface dialog, int id ) {
							clickedPosButton( id, finalInput );
						}
					
					});
					
					builder.setNegativeButton( R.string.btn_cancel, new DialogInterface.OnClickListener() {
					
						/* (non-Javadoc)
						 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
						 */
						public void onClick( DialogInterface dialog, int id ) {
						}
						
					});
				
				} else {
				
					builder.setMessage( R.string.gallery_sg_error );
				
				}

				AlertDialog dialog = builder.create();
				dialog.show();
			}

			Log.v( TAG, "LoadFileListTask.onPostExecute : exit" );
		}

		private void clickedPosButton( int result, EditText directoryName ) {

			if( directoryName != null ) {
				galleryDir = directoryName.getText().toString();
			}
			new CreateSGTask().execute( gallerySGName, galleryDir );

		}
		
	}

	private class CreateSGTask extends AsyncTask<String, Void, Bool> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bool doInBackground( String... params ) {

			Bool bool = new Bool();
			bool.setBool( false );
			if( params[ 1 ] != null && !"".equalsIgnoreCase( params[ 1 ] ) ) {

				// AddStorageGroupDir
				ResponseEntity<Bool> responseEntity = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).mythOperations().addStorageGroupDir( params[ 0 ], params[ 1 ], mLocationProfile.getHostname() );
				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					bool = responseEntity.getBody();
					return bool;
				}
			}
			
			return bool;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( Bool bool ) {
			super.onPostExecute( bool );

			if( bool.getBool() ) {

				new LoadFileListTask().execute();
				
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
				builder.setMessage( R.string.gallery_sg_failed );
				builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

					/* (non-Javadoc)
					 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
					 */
					public void onClick( DialogInterface dialog, int id ) {
					}

				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		
		}

	}

}
