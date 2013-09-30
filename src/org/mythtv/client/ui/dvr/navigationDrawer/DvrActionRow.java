/**
 * 
 */
package org.mythtv.client.ui.dvr.navigationDrawer;

import org.mythtv.client.ui.navigationDrawer.ActionRow;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public abstract class DvrActionRow extends ActionRow {

	public DvrActionRow( Context context, int actionResId, boolean implemented ) {
		super( context, actionResId, implemented );
	}

}
