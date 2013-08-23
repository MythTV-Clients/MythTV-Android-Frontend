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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import org.mythtv.R;
import org.mythtv.db.preferences.LocationProfileDaoHelper;

import java.util.ArrayList;

/**
 * @author Espen A. Fossen
 */
public class GalleryGridAdapter extends BaseAdapter {

    private ArrayList<GalleryImageItem> imageItems;
    private ImageLoader imageLoader;
    private final Context mContext;
    private String baseUrl;
    private LocationProfileDaoHelper mLocationProfileDaoHelper;
    private String gallerySGName = "Gallery";
    private String previewWidth = "256";

    final DisplayImageOptions options = new DisplayImageOptions.Builder()
    //			.showStubImage( R.drawable.ic_stub )
    //			.showImageForEmptyUri( R.drawable.ic_empty )
    //			.showImageOnFail( R.drawable.ic_error )
    			.cacheInMemory(true)
    			.cacheOnDisc(true)
    //			.displayer( new RoundedBitmapDisplayer( 20 ) )
    			.build();


    public GalleryGridAdapter(Context c) {
        this.mContext = c;
        this.imageItems = GalleryActivity.images;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(c));
        mLocationProfileDaoHelper = new LocationProfileDaoHelper(mContext);
        baseUrl = mLocationProfileDaoHelper.findConnectedProfile().getUrl() + "Content/GetImageFile?StorageGroup="+gallerySGName+"&FileName=";
    }

    public int getCount() {
        return imageItems.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.activity_gallery_griditem, null);

            holder = new ViewHolder();
            // Use with griditem_tree
            //holder.title = (TextView) convertView.findViewById(R.id.grid_item_text);
            holder.image = (ImageView) convertView.findViewById(R.id.grid_item_image);

            holder.image.setImageResource(imageItems.get(position).getImageId());
            // Use with griditem_tree
            //holder.title.setText(imageItems.get(position).getTitle());
            int h = mContext.getResources().getDisplayMetrics().densityDpi;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(new GridView.LayoutParams(params));

            holder.image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent galleryImageIntent = new Intent(mContext, GalleryPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putString("baseUrl", baseUrl);
                    galleryImageIntent.putExtras(bundle);
                    mContext.startActivity(galleryImageIntent);
                }
            });
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        imageLoader.displayImage(baseUrl + imageItems.get(position).getUrl() + "&Width=" + previewWidth, holder.image,
                options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Animation anim = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
                holder.image.setAnimation(anim);
                anim.start();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        ImageView image;
    }
}

