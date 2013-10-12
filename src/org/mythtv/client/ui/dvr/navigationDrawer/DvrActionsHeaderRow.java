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
package org.mythtv.client.ui.dvr.navigationDrawer;

import org.mythtv.client.ui.navigationDrawer.ActionsHeaderRow;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public class DvrActionsHeaderRow extends ActionsHeaderRow {

	/**
	 * @param context
	 * @param headerResId
	 */
	public DvrActionsHeaderRow( Context context, int headerResId ) {
		super( context, headerResId );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.ACTIONS_HEADER_ROW.ordinal();
	}

}
