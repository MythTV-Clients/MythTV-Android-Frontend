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
import org.mythtv.db.myth.model.StorageGroupDirectory;
import org.mythtv.service.content.GetFileListTask;
import org.mythtv.service.myth.CreateStorageGroupTask;
import org.mythtv.service.myth.GetHostsTask;
import org.mythtv.service.myth.GetSettingTask;
import org.mythtv.service.myth.GetStorageGroupsTask;
import org.mythtv.services.api.ApiVersion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
public class GalleryGridAdapter extends BaseAdapter implements
		GetFileListTask.TaskFinishedListener,
		CreateStorageGroupTask.TaskFinishedListener,
		GetStorageGroupsTask.TaskFinishedListener,
		GetHostsTask.TaskFinishedListener,
		GetSettingTask.TaskFinishedListener {

	private static final String TAG = GalleryGridAdapter.class.getSimpleName();
	
	private final Context mContext;
	private final LocationProfile mLocationProfile;
	
	public static List<GalleryImageItem> mImageItems = new ArrayList<GalleryImageItem>();
	
	private final String galleryStorageGroupName = "Images";
	private final String gallerySetting = "GalleryDir";
	private String galleryDir = "";

	private ImageLoader imageLoader;
	private String baseUrl;

	private boolean hasBackendGallerySG = false;
	private boolean backendAndFrontendShareHostname = false;
	private boolean galleryDirPresentInSettings = false;
	
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
		
		baseUrl = locationProfile.getUrl() + "Content/GetImageFile?StorageGroup=" + galleryStorageGroupName + "&FileName=";
		
		if( mImageItems.isEmpty() ) {
			
			refresh();
			
		}
		
	}

	public void refresh() {
		Log.v( TAG, "refresh : enter" );

		setHasBackendGalleryStorageGroup();
		
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
		
        // 800p screen / 3 columns = 266,67 for each
        // 720p screen / 3 columns = 240 for each
        String previewWidth = "256";

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

	private void getImages() {
		Log.v( TAG, "getImages : enter" );
		
		GetFileListTask fileListTask = new GetFileListTask( mContext, mLocationProfile, this );

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				Log.v( TAG, "getImages : getting v026 'Gallery' fileList" );
				
				fileListTask.execute( "Gallery" );
			
				break;
			case v027 :
				Log.v( TAG, "getImages : getting v027 'Images' fileList" );

				fileListTask.execute( "Images" );
			
				break;
			
			default :
				Log.v( TAG, "getImages : getting Default(v026 'Gallery' fileList" );

				fileListTask.execute( "Gallery" );

				break;
		}
		
		Log.v( TAG, "getImages : exit" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.service.content.GetFileListTask.TaskFinishedListener#onGetFileListTaskStarted()
	 */
	@Override
	public void onGetFileListTaskStarted() {
		Log.v( TAG, "onGetFileListTaskStarted : enter" );
		
		Log.v( TAG, "onGetFileListTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.content.GetFileListTask.TaskFinishedListener#onGetFileListTaskFinished(java.util.List)
	 */
	@Override
	public void onGetFileListTaskFinished( List<String> result ) {
		Log.v( TAG, "onGetFileListTaskFinished : enter" );
		
		if( null != result && !result.isEmpty() ) {
			
			mImageItems = new ArrayList<GalleryImageItem>();
			
			// TODO: Add different types of sorting, and filtering
			// String imageUri =
			// mLocationProfileDaoHelper.findConnectedProfile().getUrl() +
			// "Content/GetImageFile?StorageGroup="+gallerySGName+"&Width="+previewWidth+"&FileName=";
			
			Log.v( TAG, "onGetFileListTaskFinished : adding images to adapter" );
			for( String file : result ) {

				// First look for image suffixes, then skip files with
				// reoccurring WxH suffixes made by MythTVs getImageFile
				// scalar.
				if( file.matches( ".+(?i)(jpg|png|gif|bmp)$" ) && !file.matches( ".+(?i)(jpg|png|gif|bmp).\\d+x\\d.(?i)(jpg|png|gif|bmp)$" ) ) {
					GalleryImageItem imageItem = new GalleryImageItem( 0, "", file );
					mImageItems.add( imageItem );
				}
			
			}
			
			
		}
		
		if( hasBackendGallerySG ) {
			Log.v( TAG, "onGetFileListTaskFinished : Storage Group exists" );
			
			notifyDataSetChanged();
			
		} else {
			Log.v( TAG, "onGetFileListTaskFinished : Storage Group DOES NOT exist" );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( mContext );

			if( backendAndFrontendShareHostname ) {
				Log.v( TAG, "onGetFileListTaskFinished : Backend and Frontend are the same" );
				
				if( galleryDirPresentInSettings ) {
					Log.v( TAG, "onGetFileListTaskFinished : Storage Group present in settings" );

					builder.setMessage( mContext.getResources().getString( R.string.gallery_sg_exist_create )
							+ mLocationProfile.getHostname()
							+ mContext.getResources().getString( R.string.gallery_sg_exist_create2 ) );
				} else {
					Log.v( TAG, "onGetFileListTaskFinished : Storage Group NOT present in settings" );

					builder.setMessage( R.string.gallery_sg_create );

					EditText input = new EditText( mContext );
					input.setHint( R.string.gallery_sg_create_hint );
					builder.setView( input );
				
					galleryDir = input.getText().toString();
				}
				
				
				builder.setPositiveButton( R.string.btn_ok, new DialogInterface.OnClickListener() {
			
					/* (non-Javadoc)
					 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
					 */
					public void onClick( DialogInterface dialog, int id ) {
						clickedPosButton( id );
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
				Log.v( TAG, "onGetFileListTaskFinished : Backend and Frontend are NOT the same" );
			
				builder.setMessage( R.string.gallery_sg_error );
			
			}

			AlertDialog dialog = builder.create();
			dialog.show();
		}

		notifyParentEnd();

		Log.v( TAG, "onGetFileListTaskFinished : exit" );
	}

	private void clickedPosButton( int result ) {
		Log.v( TAG, "clickedPosButton : enter" );

		if( galleryDir != null && !"".equals( galleryDir ) ) {
			Log.v( TAG, "clickedPosButton : galleryDir=" + galleryDir );

			CreateStorageGroupTask createStorageGroupTask = new CreateStorageGroupTask( mContext, mLocationProfile, GalleryGridAdapter.this );
			createStorageGroupTask.execute( galleryStorageGroupName, galleryDir );
		}

		Log.v( TAG, "clickedPosButton : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.CreateStorageGroupTask.TaskFinishedListener#onCreateStorageGroupTaskStarted()
	 */
	@Override
	public void onCreateStorageGroupTaskStarted() {
		Log.v( TAG, "onCreateStorageGroupTaskStarted : enter" );
		
		Log.v( TAG, "onCreateStorageGroupTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.CreateStorageGroupTask.TaskFinishedListener#onCreateStorageGroupTaskFinished(boolean)
	 */
	@Override
	public void onCreateStorageGroupTaskFinished( boolean result ) {
		Log.v( TAG, "onCreateStorageGroupTaskFinished : enter" );
		
		if( result ) {
			Log.v( TAG, "onCreateStorageGroupTaskFinished : Storage Group created" );
			
			refresh();
		
		} else {
			Log.v( TAG, "onCreateStorageGroupTaskFinished : Storage Group NOT created" );
			
			AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
			builder.setMessage( R.string.gallery_sg_failed );
			builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

				/* (non-Javadoc)
				 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
				 */
				public void onClick( DialogInterface dialog, int id ) { }

			});
			
			AlertDialog dialog = builder.create();
			dialog.show();

		}
		
		Log.v( TAG, "onCreateStorageGroupTaskFinished : exit" );
	}

	private void setConnectedProfileInHostsList() {
		Log.v( TAG, "setConnectedProfileInHostsList : enter" );
	
		GetHostsTask hostsTask = new GetHostsTask( mContext, mLocationProfile, this );
		hostsTask.execute();
		
		Log.v( TAG, "setConnectedProfileInHostsList : exit, false" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetHostsTask.TaskFinishedListener#onGetHostsTaskStarted()
	 */
	@Override
	public void onGetHostsTaskStarted() {
		Log.v( TAG, "onGetHostsTaskStarted : enter" );
		
		Log.v( TAG, "onGetHostsTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetHostsTask.TaskFinishedListener#onGetHostsTaskFinished(java.util.List)
	 */
	@Override
	public void onGetHostsTaskFinished( List<String> result ) {
		Log.v( TAG, "onGetHostsTaskFinished : enter" );

		if( null != result && !result.isEmpty() ) {
			Log.v( TAG, "onGetHostsTaskFinished : hosts found" );
			
			for( String host : result ) {
			
				if( host.equals( mLocationProfile.getHostname() ) ) {
					Log.v( TAG, "onGetHostsTaskFinished : host found!" );

					backendAndFrontendShareHostname = true;
					
					break;
				}
		
			}

		}

		if( backendAndFrontendShareHostname ) {
			Log.v( TAG, "onGetHostsTaskFinished : Set Storage Directory Present in Settings" );
			
			setGalleryStorageDirectoryPresentInSettings();
			
		}
		
		notifyParentEnd();
		
		Log.v( TAG, "onGetHostsTaskFinished : exit" );
	}

	private void setHasBackendGalleryStorageGroup() {
		Log.v( TAG, "setHasBackendGalleryStorageGroup : enter" );
		
		GetStorageGroupsTask storageGroupTask = new GetStorageGroupsTask( mContext, mLocationProfile, this );
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
			
				storageGroupTask.execute( "Gallery" );
			
				break;
			case v027 :

				storageGroupTask.execute( "Images" );
			
				break;
			
			default :
			
				storageGroupTask.execute( "Gallery" );

				break;
		}
		
		Log.v( TAG, "setHasBackendGalleryStorageGroup : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetStorageGroupsTask.TaskFinishedListener#onGetStorageGroupsTaskStarted()
	 */
	@Override
	public void onGetStorageGroupsTaskStarted() {
		Log.v( TAG, "onGetStorageGroupsTaskStarted : enter" );
		
		Log.v( TAG, "onGetStorageGroupsTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetStorageGroupsTask.TaskFinishedListener#onGetStorageGroupsTaskFinished(java.util.List)
	 */
	@Override
	public void onGetStorageGroupsTaskFinished( List<StorageGroupDirectory> result ) {
		Log.v( TAG, "onGetStorageGroupsTaskFinished : enter" );

		if( null != result && !result.isEmpty() ) {

			ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
			
			for( StorageGroupDirectory sg : result ) {
				
				switch( apiVersion ) {
					case v026 :
						
						if( sg.getGroupName().equals( "Gallery" ) ) {
							Log.v( TAG, "onGetStorageGroupsTaskFinished : v026 Gallery Storage Group found" );
						
							hasBackendGallerySG = true;

						}

						break;

					case v027 :

						if( sg.getGroupName().equals( "Images" ) ) {
							Log.v( TAG, "onGetStorageGroupsTaskFinished : v027 Gallery Storage Group found" );
						
							hasBackendGallerySG = true;

						}
				
						break;
				
					default :
				
						if( sg.getGroupName().equals( "Gallery" ) ) {
							Log.v( TAG, "onGetStorageGroupsTaskFinished : Default(v026) Gallery Storage Group found" );

							hasBackendGallerySG = true;

						}

						break;
				}
	
			}
		
		}

		if( hasBackendGallerySG ) {

			getImages();

		} else {
			
			setConnectedProfileInHostsList();

		}
		
		Log.v( TAG, "onGetStorageGroupsTaskFinished : exit" );
	}

	private void setGalleryStorageDirectoryPresentInSettings() {
		Log.v( TAG, "setGalleryStorageDirectoryPresentInSettings : enter" );
		
		GetSettingTask settingTask = new GetSettingTask( mContext, mLocationProfile, this );
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				Log.v( TAG, "setGalleryStorageDirectoryPresentInSettings : getting v026 'Gallery' setting" );
				
				settingTask.execute( gallerySetting, "Gallery" );
			
				break;
			case v027 :
				Log.v( TAG, "setGalleryStorageDirectoryPresentInSettings : getting v027 'Images' setting" );

				settingTask.execute( gallerySetting, "Images" );
			
				break;
			
			default :
				Log.v( TAG, "setGalleryStorageDirectoryPresentInSettings : getting Default(v026) 'Gallery' setting" );

				settingTask.execute( gallerySetting, "Gallery" );

				break;
		}
		
		Log.v( TAG, "setGalleryStorageDirectoryPresentInSettings : exit" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetSettingTask.TaskFinishedListener#onGetSettingTaskStarted()
	 */
	@Override
	public void onGetSettingTaskStarted() {
		Log.v( TAG, "onGetSettingTaskStarted : enter" );
		
		Log.v( TAG, "onGetSettingTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.myth.GetSettingTask.TaskFinishedListener#onGetSettingTaskFinished(java.lang.String)
	 */
	@Override
	public void onGetSettingTaskFinished( String result ) {
		Log.v( TAG, "onGetSettingTaskFinished : enter" );
		
		if( null != result && !"".equals( result ) ) {
			Log.v( TAG, "onGetSettingTaskFinished : result=" + result );
			
			galleryDir = result;
			galleryDirPresentInSettings = true;
		
			getImages();
		
		}
		
		Log.v( TAG, "onGetSettingTaskFinished : exit" );
	}

}
