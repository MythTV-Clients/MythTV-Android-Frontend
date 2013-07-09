/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.services.api.channel.ChannelInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
public class GuideChannelFragment extends MythtvListFragment {

	private static final String TAG = GuideChannelFragment.class.getSimpleName();

	private SharedPreferences mSharedPreferences;
	private boolean downloadIcons;
	
	private ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private ProgramGuideChannelAdapter mAdapter;
	private OnChannelScrollListener mOnChannelScrollListener;
	
	private LocationProfile mLocationProfile;
	
	public interface OnChannelScrollListener {
		
		void channelScroll( int first, int last, int screenCount, int totalCount );
		
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
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		downloadIcons = mSharedPreferences.getBoolean( "preference_program_guide_channel_icon_download", false );
		Log.v( TAG, "download : downloadIcons=" + downloadIcons );


		options = new DisplayImageOptions.Builder()
//			.showStubImage( R.drawable.ic_stub )
//			.showImageForEmptyUri( R.drawable.ic_empty )
//			.showImageOnFail( R.drawable.ic_error )
			.cacheInMemory()
			.cacheOnDisc()
//			.displayer( new RoundedBitmapDisplayer( 20 ) )
			.build();

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		mAdapter = new ProgramGuideChannelAdapter( getActivity() );
	    setListAdapter( mAdapter );

	    getListView().setFastScrollEnabled( true );
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

	public void changeChannels( List<ChannelInfo> channels ) {
		Log.v( TAG, "changeChannels : enter" );

		mAdapter.setChannels( channels );

		Log.v( TAG, "changeChannels : exit" );
	}
	
	// internal helpers
	
	private class ProgramGuideChannelAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;

		private List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
		
		public ProgramGuideChannelAdapter( Context context ) {
						
			mInflater = LayoutInflater.from( context );
		
		}

		public void setChannels( List<ChannelInfo> channels ) {
			this.channels = channels;
		
			notifyDataSetChanged();
		}
		
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return channels.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public ChannelInfo getItem( int position ) {
			if( null != channels && !channels.isEmpty() ) {
				return channels.get( position );
			}
			
			return null;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId( int position ) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {

			ViewHolder refHolder = new ViewHolder();

			View view = convertView;
			if( null == view ) {
				
		        view = mInflater.inflate( R.layout.program_guide_channel, parent, false );
				
				refHolder.channel = (TextView) view.findViewById( R.id.program_guide_channel );
				refHolder.icon = (ImageView) view.findViewById( R.id.program_guide_icon );
				refHolder.callsign = (TextView) view.findViewById( R.id.program_guide_callsign );
				
				view.setTag( refHolder );

			} else {
				refHolder = (ViewHolder) view.getTag();
			}
			
			ChannelInfo channel = getItem( position );
			if( null != channel ) {
				
				refHolder.channel.setText( channel.getChannelNumber() );
				refHolder.callsign.setText( channel.getCallSign() );

				if( downloadIcons ) {
					String imageUri = mLocationProfileDaoHelper.findConnectedProfile( getActivity() ).getUrl() + "Guide/GetChannelIcon?ChanId=" + channel.getChannelId() + "&Width=32&Height=32";
					imageLoader.displayImage( imageUri, refHolder.icon, options, new SimpleImageLoadingListener() {

						/* (non-Javadoc)
						 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(android.graphics.Bitmap)
						 */
						@Override
						public void onLoadingComplete( String imageUri, View view, Bitmap loadedImage ) {
/*							mHolder.icon.setVisibility( View.GONE );
							mHolder.icon.setVisibility( View.VISIBLE );
*/						}

						/* (non-Javadoc)
						 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingFailed(com.nostra13.universalimageloader.core.assist.FailReason)
						 */
						@Override
						public void onLoadingFailed( String imageUri, View view, FailReason failReason ) {
/*							mHolder.icon.setVisibility( View.VISIBLE );
							mHolder.icon.setVisibility( View.GONE );
*/						}

					});
				}

			}
			
			return view;
		}

	}

	private static class ViewHolder {
		
		TextView channel;
		ImageView icon;
		TextView callsign;
		
	}

}
