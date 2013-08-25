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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.MythtvApplicationContext;

import java.util.ArrayList;

/**
 * @author Espen A. Fossen.
 */
public class GalleryPagerActivity extends Activity implements MythtvApplicationContext {

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private static final String TAG = GalleryPagerActivity.class.getSimpleName();
    private static final String STATE_POSITION = "STATE_POSITION";
    private String baseUrl;

    DisplayImageOptions options;

    ViewPager pager;

    @Override
    public MainApplication getMainApplication() {
        return (MainApplication) super.getApplicationContext();
    }

    public GalleryPagerActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate : enter");

        setContentView(R.layout.activity_gallery_pager);

        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("position");
        baseUrl = bundle.getString("baseUrl");

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STATE_POSITION);
        }

        pager = (ViewPager) findViewById(R.id.gallery_pager);
        pager.setAdapter(new ImagePagerAdapter(GalleryActivity.images));
        pager.setCurrentItem(position);

        Log.v(TAG, "onCreate : exit");

    }

        public void onSaveInstanceState(Bundle outState) {
            outState.putInt(STATE_POSITION, pager.getCurrentItem());
        }

    private class ImagePagerAdapter extends PagerAdapter {

   		private ArrayList<GalleryImageItem> images;
   		private LayoutInflater inflater;

   		ImagePagerAdapter(ArrayList<GalleryImageItem> images) {
   			this.images = images;
   			inflater = getLayoutInflater();
   		}

   		@Override
   		public void destroyItem(ViewGroup container, int position, Object object) {
   			container.removeView((View) object);
   		}

        @Override
        public void finishUpdate(ViewGroup container) {
        }

   		@Override
   		public int getCount() {
   			return images.size();
   		}

   		@Override
   		public Object instantiateItem(ViewGroup view, int position) {
   			View imageLayout = inflater.inflate(R.layout.activity_gallery_pager_item, view, false);
   			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
   			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

   			imageLoader.displayImage(baseUrl+images.get(position).getUrl(), imageView, options, new SimpleImageLoadingListener() {

   				@Override
   				public void onLoadingStarted(String imageUri, View view) {
   					spinner.setVisibility(View.VISIBLE);
   				}

   				@Override
   				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
   					String message = null;
   					switch (failReason.getType()) {
   						case IO_ERROR:
   							message = "Input/Output error";
   							break;
   						case DECODING_ERROR:
   							message = "Image can't be decoded";
   							break;
   						case NETWORK_DENIED:
   							message = "Downloads are denied";
   							break;
   						case OUT_OF_MEMORY:
   							message = "Out Of Memory error";
   							break;
   						case UNKNOWN:
   							message = "Unknown error";
   							break;
   					}
   					Toast.makeText(GalleryPagerActivity.this, message, Toast.LENGTH_SHORT).show();

   					spinner.setVisibility(View.GONE);
   				}

   				@Override
   				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
   					spinner.setVisibility(View.GONE);
   				}
   			});

   			view.addView(imageLayout, 0);
   			return imageLayout;
   		}

   		@Override
   		public boolean isViewFromObject(View view, Object object) {
   			return view.equals(object);
   		}

   		@Override
   		public void restoreState(Parcelable state, ClassLoader loader) {
   		}

   		@Override
   		public Parcelable saveState() {
   			return null;
   		}

   		@Override
   		public void startUpdate(ViewGroup container) {
   		}
   	}
}

