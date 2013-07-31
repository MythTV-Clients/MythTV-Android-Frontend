/**
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.frontends.Frontend;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.frontends.FrontendConstants;
import org.mythtv.db.frontends.FrontendDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class FrontendsRow implements Row {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
    private FrontendDaoHelper mFrontendDaoHelper = FrontendDaoHelper.getInstance();
    private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	public FrontendsRow( Context context ) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_frontends, null );

    		holder = new ViewHolder();
    		holder.spinner = (Spinner) convertView.findViewById( R.id.navigation_drawer_frontends_spinner );
    		holder.mythmote = (ImageButton) convertView.findViewById( R.id.navigation_drawer_frontends_mythmote );
    		
    		convertView.setTag( holder );
     			
        } else {

    		holder = (ViewHolder) convertView.getTag();
        
        }

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );

		String[] projection = new String[] { FrontendConstants.TABLE_NAME + "." + FrontendConstants._ID, FrontendConstants.TABLE_NAME + "." + FrontendConstants.FIELD_NAME };
        String selection = FrontendConstants.TABLE_NAME + "." + FrontendConstants.FIELD_AVAILABLE + " = ?";
        String[] selectionArgs = new String[] { "1" };
		
		List<Frontend> frontends = mFrontendDaoHelper.findAll( mContext, locationProfile, null, selection, selectionArgs, FrontendConstants.TABLE_NAME + "." + FrontendConstants.FIELD_NAME );
		if( null != frontends && !frontends.isEmpty() ) {
			FrontendAdapter adapter = new FrontendAdapter(mContext, R.layout.frontend_row, frontends);
			holder.spinner.setAdapter( adapter );
		}
        
        return convertView;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return TopLevelRowType.FRONTENDS_ROW.ordinal();
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getTitle()
	 */
	@Override
	public String getTitle() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getFragment()
	 */
	@Override
	public String getFragment() {
		return null;
	}

	private static class ViewHolder {
		
		Spinner spinner;
		ImageButton mythmote;
		
	}
	
	
	
	private class FrontendAdapter extends ArrayAdapter<Frontend> {

		private final String TAG = FrontendAdapter.class.getSimpleName();

		private int layoutResourceId;
		private List<Frontend> frontends = null;

		FrontendAdapter(Context context, int layoutResourceId,
				List<Frontend> frontends) {
			super(context, layoutResourceId, frontends);
			Log.v(TAG, "initialize : enter");

			this.layoutResourceId = layoutResourceId;
			this.frontends = frontends;

			Log.v(TAG, "initialize : exit");
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getFrontendView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getFrontendView(position, convertView, parent);
		}
		
		
		
		private View getFrontendView(int position, View convertView, ViewGroup parent){
			Log.v(TAG, "getFrontendView : enter");
			
			View row = convertView;
			FrontendHolder holder = null;

			if (row == null) {
				Log.v(TAG, "getFrontendView : row is null");

				row = mLayoutInflater.inflate(layoutResourceId, parent, false);

				holder = new FrontendHolder();
				holder.name = (TextView) row.findViewById(R.id.frontend_name);
				holder.url = (TextView) row.findViewById(R.id.frontend_url);
				
				row.setTag(holder);
			} else {
				holder = (FrontendHolder) row.getTag();
			}

			Frontend frontend = frontends.get(position);

			holder.name.setText(frontend.getName());
			holder.url.setText(frontend.getUrl());

			Log.v(TAG, "getFrontendView : exit");
			return row;
		}
		
		

		class FrontendHolder {
			TextView name;
			TextView url;
		}

	}
	
}
