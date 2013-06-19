/**
 * 
 */
package org.mythtv.client.ui.dvr.navigationDrawer;

import org.mythtv.client.ui.navigationDrawer.VersionRow;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public class DvrVersionRow extends VersionRow {

	public DvrVersionRow( Context context, String name, String version ) {
		super(context, name, version);
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.VERSION_ROW.ordinal();
	}
	
}
