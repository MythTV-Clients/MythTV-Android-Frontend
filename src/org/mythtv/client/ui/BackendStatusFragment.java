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
import java.util.zip.Inflater;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.services.api.dvr.Encoder;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.status.Backends;
import org.mythtv.services.api.status.Job;
import org.mythtv.services.api.status.Job.Status;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class BackendStatusFragment extends AbstractMythFragment {

	private static final String TAG = BackendStatusFragment.class.getSimpleName();
	
	private ProgramHelper mProgramHelper = ProgramHelper.getInstance();
	private View mView;
	private LocationProfile mLocationProfile;
	private ListView mListViewEncoders;
	private ListView mListViewUpcomingRecordings;
	private ListView mListViewJobQueue;
	
	/**
	 * Sets the height of a listview to match the height of all it's children.
	 * DO NOT CALL THIS ON LONG LISTS!
	 * @param listView
	 */
	private static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.d( TAG, "onCreateView : enter" );
		
		mView = inflater.inflate( R.layout.fragment_backend_status, container, false );
		
		mListViewEncoders = (ListView)mView.findViewById(R.id.listview_encoders);
		mListViewUpcomingRecordings = (ListView)mView.findViewById(R.id.listview_upcoming_recordings);
		mListViewJobQueue = (ListView)mView.findViewById(R.id.listview_job_queue);
		
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
    	
		LayoutInflater inflater = LayoutInflater.from(this.getActivity());
		
		// Set encoder list
		List<Encoder> encoders = result.getEncoders().getEncoders();
		if (null != encoders) {
			mListViewEncoders.setAdapter(new EncoderArrayAdapter(this
					.getActivity(), R.layout.encoder_listview_item, encoders));
		}
		
		// Set Upcoming recordings list
		List<Program> programs = result.getScheduled().getPrograms();
		if(null != programs){
			mListViewUpcomingRecordings.setAdapter(new SchedualedProgramArrayAdapter(this.getActivity(), R.layout.upcoming_row_small_txt, programs));
		}
		
		List<Job> jobs = result.getJobQueue().getJobs();
		if(null != jobs){
			mListViewJobQueue.setAdapter(new JobArrayAdapter(this.getActivity(), R.layout.job_row, jobs));
		}
		
		//update listview heights to match children
		setListViewHeightBasedOnChildren(mListViewEncoders);
		setListViewHeightBasedOnChildren(mListViewUpcomingRecordings);
		setListViewHeightBasedOnChildren(mListViewJobQueue);
    }
	
	/**
	 * 
	 * @author Thomas G. Kenny Jr
	 *
	 */
	private class EncoderArrayAdapter extends ArrayAdapter<Encoder>
	{
		private List<Encoder> mEncoders;
		private Context mContext;
		private LayoutInflater mInflater;
		
		public EncoderArrayAdapter(Context context, int textViewResourceId,
				List<Encoder> objects) {
			super(context, textViewResourceId, objects);
			mEncoders = objects;
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = (View)mInflater.inflate(R.layout.encoder_listview_item, null, true);
			
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
	
	/**
	 * 
	 * @author Thomas G. Kenny Jr
	 *
	 */
	private class SchedualedProgramArrayAdapter extends ArrayAdapter<Program>
	{
		private List<Program> mPrograms;
		private Context mContext;
		private LayoutInflater mInflater;
		
		public SchedualedProgramArrayAdapter(Context context, int textViewResourceId,
				List<Program> objects) {
			super(context, textViewResourceId, objects);
			mPrograms = objects;
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = (View)mInflater.inflate(R.layout.upcoming_row_small_txt, null, true);
			
			Program program = this.mPrograms.get(position);
			
			//set category color
			View category = view.findViewById(R.id.upcoming_category);
			if(null != category) category.setBackgroundColor(mProgramHelper.getCategoryColor( program.getCategory() ));
			
			//set upcoming_title
			TextView tView = (TextView)view.findViewById(R.id.upcoming_title);
			if(null != tView) {
				tView.setText(program.getTitle());
			}
			
			//set upcoming_sub_title
			tView = (TextView)view.findViewById(R.id.upcoming_sub_title);
			if(null != tView) {
				tView.setText(program.getSubTitle());
			}
			
			//set upcoming_channel
			tView = (TextView)view.findViewById(R.id.upcoming_channel);
			if(null != tView) {
				tView.setText(program.getChannelInfo().getCallSign());
			}
			
			//set upcoming_start_time
			tView = (TextView)view.findViewById(R.id.upcoming_start_time);
			if(null != tView) {
				tView.setText(program.getStartTime().toString("hh:mm"));
			}
			
			//set upcoming_duration
			tView = (TextView)view.findViewById(R.id.upcoming_duration);
			if(null != tView) {
				tView.setText(Long.toString(program.getDurationInMinutes()));
			}
			
			return view;
			
		}
	}
	
	
	/**
	 * 
	 * @author Thomas G. Kenny Jr
	 *
	 */
	private class JobArrayAdapter extends ArrayAdapter<Job>
	{
		private List<Job> mJobs;
		private Context mContext;
		private LayoutInflater mInflater;
		
		public JobArrayAdapter(Context context, int textViewResourceId,
				List<Job> objects) {
			super(context, textViewResourceId, objects);
			mJobs = objects;
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}
		
		public String getJobStatusStr(Job.Status status){
			switch(status){
			case ABORTED:
				return mContext.getString(R.string.job_queue_status_aborted);
			case ABORTING:
				return mContext.getString(R.string.job_queue_status_aborting);
			case CANCELLED:
				return mContext.getString(R.string.job_queue_status_cancelled);
			case DONE:
				return mContext.getString(R.string.job_queue_status_done);
			case ERRORED:
				return mContext.getString(R.string.job_queue_status_errored);
			case ERRORING:
				return mContext.getString(R.string.job_queue_status_erroring);
			case FINISHED:
				return mContext.getString(R.string.job_queue_status_finished);
			case NO_FLAGS:
				return mContext.getString(R.string.job_queue_status_no_flags);
			case PAUSED:
				return mContext.getString(R.string.job_queue_status_paused);
			case PENDING:
				return mContext.getString(R.string.job_queue_status_pending);
			case QUEUED:
				return mContext.getString(R.string.job_queue_status_queued);
			case RETRY:
				return mContext.getString(R.string.job_queue_status_retry);
			case RUNNING:
				return mContext.getString(R.string.job_queue_status_running);
			case STARTING:
				return mContext.getString(R.string.job_queue_status_starting);
			case STOPPING:
				return mContext.getString(R.string.job_queue_status_stopping);
			default:
				return mContext.getString(R.string.job_queue_status_unknown);
			}
		}
		
		class ViewHolder
		{
			public TextView title;
			public TextView type;
			public TextView status;
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if(convertView == null){
				convertView = (View)mInflater.inflate(R.layout.job_row, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.textView_job_program_title);
				holder.type = (TextView)convertView.findViewById(R.id.textView_job_type);
				holder.status = (TextView)convertView.findViewById(R.id.textView_job_status);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			Job job = this.mJobs.get(position);
			
			holder.title.setText(job.getProgram() != null ? job.getProgram().getTitle() : "");
			holder.type.setText(job.getType() != null ? job.getType().name() : "");
			holder.status.setText(getJobStatusStr(job.getStatus()));
			
			return convertView;
			
		}
	}

}
