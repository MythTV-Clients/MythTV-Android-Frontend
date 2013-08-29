/**
 * 
 */
package org.mythtv.client.ui.media;

import java.util.ArrayList;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * @author dmfrey
 * 
 */
public class PicturesParentFragment extends AbstractMythFragment {

	private static final String TAG = PicturesParentFragment.class.getSimpleName();

	public static ArrayList<GalleryImageItem> images = new ArrayList<GalleryImageItem>();

	public static boolean IMAGE_LIST_DOWNLOADED = false;

	private LocationProfile mLocationProfile;

	private GalleryGridAdapter mAdapter;
	private GridView mGridView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		// inflate recordings activity/parent fragment view
		View view = inflater.inflate( R.layout.activity_gallery, container, false );

		Log.v( TAG, "onCreateView : exit" );
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

		mGridView = (GridView) getActivity().findViewById( R.id.gallery_gridview );

		mAdapter = new GalleryGridAdapter( getActivity(), mLocationProfile );
		mGridView.setAdapter( mAdapter );

		Log.v( TAG, "onActivityCreated : exit" );
	}

}
