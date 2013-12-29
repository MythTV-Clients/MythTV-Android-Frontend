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
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.frontends.Frontend;
import org.mythtv.client.ui.frontends.MythmoteActivity;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.frontends.FrontendConstants;
import org.mythtv.db.frontends.FrontendDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.NetworkHelper;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class FrontendsRow implements Row, OnItemSelectedListener {

	private final static String TAG = FrontendsRow.class.getSimpleName();
	
	public static final String EXTRA_LOCATION_NAME = "EXTRA_LOCATION_NAME";
	public static final String EXTRA_LOCATION_ADDRESS = "EXTRA_LOCATION_ADDRESS";
	public static final String EXTRA_LOCATION_PORT = "EXTRA_LOCATION_PORT";
	public static final String EXTRA_LOCATION_MAC = "EXTRA_LOCATION_MAC";
	public static final int DEFAULT_MYTHMOTE_PORT = 6546;
	
	private static Frontend selectedFrontend;
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<Frontend> mFrontends;
	private Spinner mFrontendSpinner;
    private FrontendDaoHelper mFrontendDaoHelper = FrontendDaoHelper.getInstance();
    private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
    
    private OnClickListener mythmoteButtonOnClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			//leave if context is not set
			if(null == mContext) return;
			
			//leave if we don't have a spinner with selected frontend
			if(null == mFrontendSpinner) return;
			
			//get selected item from spinner
			selectedFrontend = (Frontend)mFrontendSpinner.getSelectedItem();
			
			//error if nothing selected
			if(null == selectedFrontend){
				new AlertDialog.Builder( mContext )
    			.setTitle( R.string.frontends_title )
    			.setMessage( R.string.frontend_not_selected )
    			.setNeutralButton(R.string.frontend_not_selected_OK, null)
    			.show();
				return;
			}
			
 			// Use internal mythmote UI that utilizes the frontend services API			
//			if( NetworkHelper.getInstance().isNetworkConnected( mContext ) && !mContext.getClass().equals(MythmoteActivity.class) ) {
//				mContext.startActivity( new Intent( mContext, MythmoteActivity.class ) );
//			}
			
			// Fire external mythmote
			mContext.startActivity(new Intent("tkj.android.homecontrol.mythmote.CONNECT_TO_FRONTEND")
			    .setComponent(ComponentName.unflattenFromString("tkj.android.homecontrol.mythmote/tkj.android.homecontrol.mythmote.MythMote"))
				.putExtra(EXTRA_LOCATION_NAME, selectedFrontend.getNameStripped())
				.putExtra(EXTRA_LOCATION_ADDRESS, selectedFrontend.getHostname())
				.putExtra(EXTRA_LOCATION_PORT, DEFAULT_MYTHMOTE_PORT) // Mythmote port is not the same as services API frontend port
				//.putExtra(EXTRA_LOCATION_MAC, "")
			);
		}
	};
	
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
    		holder.spinner = mFrontendSpinner = (Spinner) convertView.findViewById( R.id.navigation_drawer_frontends_spinner );
    		holder.spinner.setOnItemSelectedListener(this);
    		holder.mythmote = (ImageButton) convertView.findViewById( R.id.navigation_drawer_frontends_mythmote );
    		holder.mythmote.setOnClickListener(mythmoteButtonOnClick);
    		
    		convertView.setTag( holder );
     			
        } else {

    		holder = (ViewHolder) convertView.getTag();
        
        }

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );
		if( null != locationProfile ) {
		
			String[] projection = null; //new String[] { FrontendConstants.TABLE_NAME + "." + FrontendConstants._ID, FrontendConstants.TABLE_NAME + "." + FrontendConstants.FIELD_NAME };
			String selection = FrontendConstants.TABLE_NAME + "." + FrontendConstants.FIELD_AVAILABLE + " = ?";
			String[] selectionArgs = new String[] { "1" };

			mFrontends = mFrontendDaoHelper.findAll( mContext, locationProfile, projection, selection, selectionArgs, FrontendConstants.TABLE_NAME + "." + FrontendConstants.FIELD_NAME );
			if( null != mFrontends && !mFrontends.isEmpty() ) {
				FrontendAdapter adapter = new FrontendAdapter(mContext, R.layout.frontend_row, mFrontends);
				holder.spinner.setAdapter( adapter );
			}
		
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

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#isImplemented()
	 */
	@Override
	public boolean isImplemented() {
		return true;
	}

	private static class ViewHolder {
		
		Spinner spinner;
		ImageButton mythmote;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static Frontend getSelectedFrontend(){
		return selectedFrontend;
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

			holder.name.setText(frontend.getNameStripped());
			holder.url.setText(frontend.getHostname());

			Log.v(TAG, "getFrontendView : exit");
			return row;
		}
		
		

		class FrontendHolder {
			TextView name;
			TextView url;
		}

	}



	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		//leave if we don't have frontends, should never happen
		if(null == mFrontends || mFrontends.size() <= 0){
			Log.e(TAG, "Frontend selected but no frontends in ArrayList");
			return;
		}
		
		//set selected frontend
		if(arg2 >= 0 && arg2 < mFrontends.size()){
			selectedFrontend = mFrontends.get(arg2);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
}
