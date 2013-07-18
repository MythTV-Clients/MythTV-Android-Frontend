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
package org.mythtv.client.ui;

import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.dvr.Encoder;
import org.mythtv.services.api.dvr.Program;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BackendStatusFragment extends AbstractMythFragment {

	private static final String TAG = BackendStatusFragment.class.getSimpleName();
	
	private View mView;
	private LocationProfile mLocationProfile;
	private ListView mListViewEncoders;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.d( TAG, "onCreateView : enter" );
		
		mView = inflater.inflate( R.layout.fragment_backend_status, container, false );
		
		mListViewEncoders = (ListView)mView.findViewById(R.id.listview_encoders);
		
		Log.d( TAG, "onCreateView : exit" );
		return mView;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.d( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		if( null != mView ) {
			
			TextView tView = (TextView) mView.findViewById( R.id.textview_status );
			if( null != tView ) {
//				tView.setText( this.getStatusText() );
			}
			
		}
		
		Log.d( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d( TAG, "onResume : enter" );
		super.onResume();

		if( null != mView ) {
			
			TextView tView = (TextView) mView.findViewById( R.id.textview_status );
			if( null != tView ) {
				tView.setText( this.getStatusText() );
			}
			
		}
	
		Log.d( TAG, "onResume : exit" );
	}

	// internal helpers

	private String getStatusText() {
		Log.v( TAG, "getStatusText : enter" );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		if( null == mLocationProfile ) {
			Log.v( TAG, "getStatusText : exit, no connected profiles found" );

			return "Backend profile is not selected";
		}
		
		BackendStatusTask backendTask = new BackendStatusTask();
		backendTask.execute();
		
		Log.v( TAG, "getStatusText : exit" );
		return ( mLocationProfile.isConnected() ? "Connected to " : "NOT Connected to " ) + mLocationProfile.getName();
	}
	
	/*
     *  (non-Javadoc)
     *  
     *  Called at the end of BackendStatusTask.onPostExecute() when result is not null.
     */
	@Override
    protected void onBackendStatusUpdated(org.mythtv.services.api.status.Status result){
    	
		List<Encoder> encoders = result.getEncoders().getEncoders();
		if (null != encoders) {
			mListViewEncoders.setAdapter(new EncoderArrayAdapter(this
					.getActivity(), R.layout.encoder_listview_item, encoders));
		}
    }
	
	
	private class EncoderArrayAdapter extends ArrayAdapter<Encoder>
	{
		private List<Encoder> mEncoders;
		private Context mContext;
		
		public EncoderArrayAdapter(Context context, int textViewResourceId,
				List<Encoder> objects) {
			super(context, textViewResourceId, objects);
			mEncoders = objects;
			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)inflater.inflate(R.layout.encoder_listview_item, null, true);
			
			Encoder encoder = this.mEncoders.get(position);
			
			//set device label
			TextView tView = (TextView)view.findViewById(R.id.textView_encoder_devicelabel);
			if(null != tView) {
				tView.setText(Integer.toString(encoder.getId()) + " - " + encoder.getDeviceLabel());
			}
			
			//set device host
			tView = (TextView)view.findViewById(R.id.textView_encoder_host);
			if(null != tView) {
				tView.setText(encoder.getHostname());
			}
			
			//set device recording status
			tView = (TextView)view.findViewById(R.id.textView_encoder_rec_status);
			if(null != tView) {
				Program rec = encoder.getRecording();
				if(null != rec){
					tView.setText(rec.getTitle() + " on " + rec.getChannelInfo().getChannelName());
					//+ rec.getEndTime().toString("hh:mm") );
				}else{
					tView.setText("Inactive");
				}
			}
			
			
			
			return view;
			
		}
		
		
	}

}
