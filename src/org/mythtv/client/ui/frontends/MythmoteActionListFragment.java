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
package org.mythtv.client.ui.frontends;

import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.MainMenuFragment;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.frontend.Action;
import org.mythtv.services.api.frontend.FrontendActionList;
import org.springframework.http.ResponseEntity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the list of all actions reported by the frontend.
 * 
 * @author pot8oe
 *
 */
public class MythmoteActionListFragment extends AbstractFrontendFragment{
	private final static String TAG = "MythmoteActionListFragment";
	
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View mView = inflater.inflate(R.layout.fragment_mythmote_action_list, container, false);
		
		mListView = (ListView)mView.findViewById(R.id.listViewMythmoteActionList);
		
		final Frontend fe = MainMenuFragment.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null != fe){
			new GetActionListAsyncTask().execute(fe.getUrl());
		}
		
		return mView;
	}
	
	/**
	 * 
	 * @param list
	 */
	private void setActionList(List<Action> list){
		if(null != mListView && null != list){
			mListView.setAdapter(new ActionListAdapter(list));
		}
	}
	
	/**
	 * 
	 * @author pot8oe
	 *
	 */
	private class GetActionListAsyncTask extends AsyncTask<String,Void,ResponseEntity<FrontendActionList>>{

		@Override
		protected ResponseEntity<FrontendActionList> doInBackground(String... params) {
			try {
				ETagInfo eTag = ETagInfo.createEmptyETag();
				return mMythtvServiceHelper.getMythServicesApi( getActivity() ).frontendOperations().getActionList(params[0], eTag);
			} catch( Exception e ) {
				Log.e( TAG, e.getMessage() );
				showAlertDialog( "Get Status Error", e.getMessage() );
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<FrontendActionList> result) {
			if(null != mListView && null != result){
				setActionList(result.getBody().getActions());
			}
			super.onPostExecute(result);
		}
		
	}
	
	
	
	/**
	 * 
	 * @author pot8oe
	 *
	 */
	private class ActionListAdapter extends BaseAdapter{
		
		private List<Action> mList;
		
		public ActionListAdapter(List<Action> list){
			mList=list;
		}

		@Override
		public int getCount() {
			return null!=mList ? mList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return null!=mList ? mList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Action action = mList.get(position);
			
			TextView tView = new TextView(parent.getContext());
			tView.setText(action.getKey());
			
			return tView;
		}
		
	}
	
}
