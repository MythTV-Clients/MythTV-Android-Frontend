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
public class DvrRecordingsLastUpdateActionRow extends DvrLastUpdateActionRow {

	/**
	 * @param context
	 * @param etag
	 */
	public DvrRecordingsLastUpdateActionRow( Context context, EtagInfoDelegate etag ) {
		super( context, etag );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.RECORDINGS_LAST_UPDATE_ROW.ordinal();
	}

}
