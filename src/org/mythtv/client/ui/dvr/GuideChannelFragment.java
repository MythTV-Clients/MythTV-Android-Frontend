/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.channel.model.ChannelInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * @author dmfrey
 *
 */
public class GuideChannelFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = GuideChannelFragment.class.getSimpleName();

	private SharedPreferences mSharedPreferences;
	private boolean allowFastScrolling = false;
	private boolean downloadIcons = false;
	
	private ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private ProgramGuideCursorAdapter mAdapter;
	private OnChannelScrollListener mOnChannelScrollListener;
	
	private LocationProfile mLocationProfile;
	
	public interface OnChannelScrollListener {
		
		void channelScroll( int first, int last, int screenCount, int totalCount );
		
		void channelSelect( int channelId );
		
	}
	
	/**
	 * 
	 */
	public GuideChannelFragment() { }

	/**
	 * @param mOnChannelScrollListener the mOnChannelScrollListener to set
	 */
	public void setOnChannelScrollListener( OnChannelScrollListener listener ) {
		this.mOnChannelScrollListener = listener;
	}

	public ChannelInfo getChannel( int position ) {
		long id = mAdapter.getItemId( position );
		
		return mChannelDaoHelper.findOne( getActivity(), mLocationProfile, id ); 
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		
		switch( id ) {
		case 0 :
		    Log.v( TAG, "onCreateLoader : getting channels" );

			projection = new String[] { ChannelConstants._ID, ChannelConstants.FIELD_CHAN_ID + " AS " + ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_ID, ChannelConstants.FIELD_CHAN_NUM + " AS " + ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_NUM, ChannelConstants.FIELD_CALLSIGN + " AS " + ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CALLSIGN };
			selection = ChannelConstants.FIELD_VISIBLE + " = ? AND " + ChannelConstants.FIELD_MASTER_HOSTNAME + " = ?";
			selectionArgs = new String[] { "1", mLocationProfile.getHostname() };
			sortOrder = ChannelConstants.FIELD_CHAN_NUM_FORMATTED;

			Log.v( TAG, "onCreateLoader : exit" );
			return new CursorLoader( getActivity(), ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );

		default : 
		    Log.v( TAG, "onCreateLoader : exit, invalid id" );

		    return null;
		}
			
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		mAdapter.swapCursor( cursor );
		
		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		mAdapter.swapCursor( null );
		
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		allowFastScrolling = mSharedPreferences.getBoolean( "preference_program_guide_fast_channel_scrolling", false );
//		downloadIcons = mSharedPreferences.getBoolean( "preference_program_guide_channel_icon_download", false );
//		Log.v( TAG, "download : downloadIcons=" + downloadIcons );

		options = new DisplayImageOptions.Builder()
			.cacheInMemory( true )
			.cacheOnDisc( true )
			.build();

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		mAdapter = new ProgramGuideCursorAdapter( getActivity() );
	    setListAdapter( mAdapter );

		getLoaderManager().initLoader( 0, null, this );

		getListView().setFastScrollEnabled( allowFastScrolling );
		getListView().setOnScrollListener( new PauseOnScrollListener( imageLoader, false, true, new OnScrollListener() {

			/* (non-Javadoc)
			 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
			 */
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
				Log.v( TAG, "onScroll : enter" );

				//mOnChannelScrollListener.channelScroll( firstVisibleItem, visibleItemCount, totalItemCount );

				Log.v( TAG, "onScroll : exit" );
			}

			/* (non-Javadoc)
			 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
			 */
			@Override
			public void onScrollStateChanged( AbsListView  view, int scrollState ) {
				Log.v( TAG, "onScrollStateChanged : enter" );

				Log.v( TAG, "onScrollStateChanged : scrollState=" + scrollState );
		        
				int first = view.getFirstVisiblePosition();
		        int last = view.getLastVisiblePosition();
		        int count = view.getChildCount();

		        if( scrollState == SCROLL_STATE_IDLE ) {
		        	mOnChannelScrollListener.channelScroll( first, last, count, mAdapter.getCount() );
		        }

				Log.v( TAG, "onScrollStateChanged : exit" );
			}
			
		}));
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers
	
	private class ProgramGuideCursorAdapter extends CursorAdapter {
		
		private List<View> selectedViews = new ArrayList<View>();
		
		private Drawable mBackground;
		private Drawable mBackgroundSelected;
		private ChannelInfo mSelectedChannel;
		private LayoutInflater mInflater;

		public ProgramGuideCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
			
			mBackground =  getResources().getDrawable( R.drawable.program_guide_channel_header_back );
			mBackgroundSelected =  getResources().getDrawable( R.drawable.program_guide_channel_header_back_selected );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			
			final ChannelInfo channel = ChannelDaoHelper.convertCursorToChannelInfo( cursor );

	        final ViewHolder mHolder = (ViewHolder) view.getTag();
				        
			mHolder.channel.setText( channel.getChannelNumber() );
			mHolder.callsign.setText( channel.getCallSign() );
			if(mSelectedChannel == null || mSelectedChannel.getChannelId() != channel.getChannelId()){
				view.setBackgroundDrawable( mBackground );
			}else {
				view.setBackgroundDrawable( mBackgroundSelected );
				selectedViews.add(view);
			}

			if( downloadIcons ) {
				
				if( null != channel.getIconUrl() && !"".equals( channel.getIconUrl() ) ) {
				
					String imageUri = mLocationProfileDaoHelper.findConnectedProfile( getActivity() ).getUrl() + "Guide/GetChannelIcon?ChanId=" + channel.getChannelId() + "&Width=32&Height=32";
					imageLoader.displayImage( imageUri, mHolder.icon, options, new SimpleImageLoadingListener() {

						/* (non-Javadoc)
						 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(android.graphics.Bitmap)
						 */
						@Override
						public void onLoadingComplete( String imageUri, View view, Bitmap loadedImage ) {
							/*	
							 * mHolder.icon.setVisibility( View.GONE );
							 * mHolder.icon.setVisibility( View.VISIBLE );
							 */
						}

						/* (non-Javadoc)
						 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingFailed(com.nostra13.universalimageloader.core.assist.FailReason)
						 */
						@Override
						public void onLoadingFailed( String imageUri, View view, FailReason failReason ) {
							/*	
							 * mHolder.icon.setVisibility( View.VISIBLE );
							 * mHolder.icon.setVisibility( View.GONE );
							 */
						}

					});
				
				}
				
			}
			
			mHolder.row.setOnClickListener( new OnClickListener() {
				
				/* (non-Javadoc)
				 * @see android.view.View.OnClickListener#onClick(android.view.View)
				 */
				@Override
				public void onClick( View v ) {

					mOnChannelScrollListener.channelSelect( channel.getChannelId() );
					mSelectedChannel = channel;
					
					for( View view : selectedViews ) {
						view.setBackgroundDrawable( mBackground );
					}
					selectedViews.clear();
					
					v.setBackgroundDrawable( mBackgroundSelected );
					
					selectedViews.add( v );
				}
				
			});
			
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			
	        View view = mInflater.inflate( R.layout.program_guide_channel_row, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.row = (LinearLayout) view.findViewById( R.id.program_guide_channel );
			refHolder.channel = (TextView) view.findViewById( R.id.program_guide_channel_number );
			refHolder.icon = (ImageView) view.findViewById( R.id.program_guide_channel_icon );
			refHolder.callsign = (TextView) view.findViewById( R.id.program_guide_channel_callsign );
			
			view.setTag( refHolder );

			return view;
		}

	}

	private static class ViewHolder {
		
		LinearLayout row;
		TextView channel;
		ImageView icon;
		TextView callsign;
		
	}

}
