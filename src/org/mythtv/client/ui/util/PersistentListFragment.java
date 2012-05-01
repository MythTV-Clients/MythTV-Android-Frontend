/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */

package org.mythtv.client.ui.util;

import org.mythtv.client.MainApplication;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class PersistentListFragment extends ListFragment {

	static public final String STATE_CHECKED = "org.mythtv.client.STATE_CHECKED";

	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		l.setItemChecked( position, true );
	}

	@Override
	public void onSaveInstanceState( Bundle state ) {
		state.putInt( STATE_CHECKED, getListView().getCheckedItemPosition() );
	}

	protected void restoreState( Bundle state ) {
		if( state != null ) {
			int position = state.getInt( STATE_CHECKED, -1 );

			if( position > -1 ) {
				getListView().setItemChecked( position, true );
			}
		}
	}

	public void enablePersistentSelection() {
		getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
	}

	public MainApplication getApplicationContext() {
		return (MainApplication) getActivity().getApplicationContext();
	}

}