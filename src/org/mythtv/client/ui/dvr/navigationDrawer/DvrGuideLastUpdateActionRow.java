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
public class DvrGuideLastUpdateActionRow extends DvrLastUpdateActionRow {

	/**
	 * @param context
	 * @param etag
	 */
	public DvrGuideLastUpdateActionRow( Context context, EtagInfoDelegate etag ) {
		super( context, etag );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.GUIDE_LAST_UPDATE_ROW.ordinal();
	}

}
