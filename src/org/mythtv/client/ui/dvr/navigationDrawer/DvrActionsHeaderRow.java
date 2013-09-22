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
