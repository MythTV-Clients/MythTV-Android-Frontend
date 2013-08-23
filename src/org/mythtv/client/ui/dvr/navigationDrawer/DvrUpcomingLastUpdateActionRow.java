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
public class DvrUpcomingLastUpdateActionRow extends DvrLastUpdateActionRow {

	/**
	 * @param context
	 * @param etag
	 */
	public DvrUpcomingLastUpdateActionRow( Context context, EtagInfoDelegate etag ) {
		super( context, etag );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.UPCOMING_LAST_UPDATE_ROW.ordinal();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.navigationDrawer.DvrLastUpdateActionRow#sendParent()
	 */
	@Override
	protected void sendParent() {
		listener.refresh( this );
	}

}
