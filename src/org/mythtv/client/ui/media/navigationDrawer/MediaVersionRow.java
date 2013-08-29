/**
 * 
 */
package org.mythtv.client.ui.media.navigationDrawer;

import org.mythtv.client.ui.navigationDrawer.VersionRow;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public class MediaVersionRow extends VersionRow {

	public MediaVersionRow( Context context, String name, String version ) {
		super(context, name, version);
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return MediaRowType.VERSION_ROW.ordinal();
	}
	
}
