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

import org.mythtv.db.http.model.EtagInfoDelegate;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public class DvrRecordingRulesLastUpdateActionRow extends DvrLastUpdateActionRow {

	/**
	 * @param context
	 * @param etag
	 */
	public DvrRecordingRulesLastUpdateActionRow( Context context, EtagInfoDelegate etag ) {
		super( context, etag, true );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.RECORDINGS_LAST_UPDATE_ROW.ordinal();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.navigationDrawer.DvrLastUpdateActionRow#sendParent()
	 */
	@Override
	protected void sendParent() {
		listener.refresh( this );
	}

}
