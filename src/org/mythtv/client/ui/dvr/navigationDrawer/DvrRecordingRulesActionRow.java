/**
 * 
 */
package org.mythtv.client.ui.dvr.navigationDrawer;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public class DvrRecordingRulesActionRow extends DvrActionRow {

	private static final String TITLE = "Recording Rules";
	private static final String FRAGMENT = "org.mythtv.client.ui.dvr.RecordingRulesFragment";
	
	public DvrRecordingRulesActionRow( Context context, String action ) {
		super( context, action );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return DvrRowType.RECORDING_RULES_ROW.ordinal();
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getTitle()
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.ActionRow#getFragment()
	 */
	public String getFragment() {
		return FRAGMENT;
	}
	
}
