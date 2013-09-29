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
			
			if( NetworkHelper.getInstance().isNetworkConnected( mContext ) && !mContext.getClass().equals(MythmoteActivity.class) ) {
				mContext.startActivity( new Intent( mContext, MythmoteActivity.class ) );
			}
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
			holder.url.setText(frontend.getUrl().replace("http://", "").replace(":6547/", ""));

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
