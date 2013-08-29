/**
 * 
 */
package org.mythtv.client.ui.media.navigationDrawer;

import org.mythtv.client.ui.navigationDrawer.ActionsHeaderRow;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public class MediaActionsHeaderRow extends ActionsHeaderRow {

	/**
	 * @param context
	 * @param header
	 */
	public MediaActionsHeaderRow( Context context, String header ) {
		super( context, header );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return MediaRowType.ACTIONS_HEADER_ROW.ordinal();
	}

}
