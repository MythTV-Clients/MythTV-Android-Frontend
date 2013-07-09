/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.services.api.channel.ChannelInfo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class GuideDataFragment extends MythtvListFragment {

	private static final String TAG = GuideDataFragment.class.getSimpleName();
	
	private ProgramGuideChannelAdapter mAdapter;
	
	/**
	 * 
	 */
	public GuideDataFragment() { }

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		View view = inflater.inflate( R.layout.program_guide_data, null );
		
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mAdapter = new ProgramGuideChannelAdapter( getActivity() );
	    setListAdapter( mAdapter );

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

			ChannelViewHolder refHolder = new ChannelViewHolder();

			View view = convertView;
			if( null == view ) {
				
		        view = mInflater.inflate( R.layout.program_guide_data_row, parent, false );
				
				refHolder.row = (LinearLayout) view.findViewById( R.id.program_guide_data_row );
				
				view.setTag( refHolder );

			} else {
				refHolder = (ChannelViewHolder) view.getTag();
			}
			
			ChannelInfo channel = getItem( position );
			if( null != channel ) {
				
			}
			
			return view;
		}

	}
	
	private static class ChannelViewHolder {
		
		LinearLayout row;
		
	}

	private static class DataItemViewHolder {
		
		View category;
		TextView title;
		TextView subTitle;
		
	}

	private class ProgramGuideChannelRowCursorAdapter extends CursorAdapter {
		
		private LayoutInflater mInflater;

		public ProgramGuideChannelRowCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			return null;
		}

	}

}
