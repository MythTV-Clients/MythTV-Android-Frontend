/**
 * 
 */
package org.mythtv.client.ui.media;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MenuItemRefreshAnimated;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * @author dmfrey
 * 
 */
public class PicturesParentFragment extends AbstractMythFragment implements GalleryGridAdapter.OnLoadingImagesListener {

	private static final String TAG = PicturesParentFragment.class.getSimpleName();

	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	private MenuItemRefreshAnimated mMenuItemRefresh;

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

		mMenuItemRefresh = new MenuItemRefreshAnimated( getActivity() );
		
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

		setHasOptionsMenu( true );
		
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

		mGridView = (GridView) getActivity().findViewById( R.id.gallery_gridview );

		mAdapter = new GalleryGridAdapter( getActivity(), mLocationProfile, this );
		mGridView.setAdapter( mAdapter );

		mMenuItemRefresh.startRefreshAnimation();
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		mMenuHelper.refreshMenuItem( getActivity(), menu, this.mMenuItemRefresh );
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case MenuHelper.REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			mMenuItemRefresh.startRefreshAnimation();
			mAdapter.refresh();
			
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.media.GalleryGridAdapter.OnLoadingImagesListener#notifyStart()
	 */
	@Override
	public void notifyStart() {
		Log.v( TAG, "notifyStart : enter" );

		mMenuItemRefresh.startRefreshAnimation();

		Log.v( TAG, "notifyStart : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.media.GalleryGridAdapter.OnLoadingImagesListener#notifyEnd()
	 */
	@Override
	public void notifyEnd() {
		Log.v( TAG, "notifyStart : enter" );
		
		mMenuItemRefresh.stopRefreshAnimation();
		
		Log.v( TAG, "notifyStart : exit" );
	}

}
