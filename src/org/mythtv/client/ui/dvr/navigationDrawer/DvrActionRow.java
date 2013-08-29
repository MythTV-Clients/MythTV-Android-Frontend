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

	public DvrActionRow( Context context, String action, boolean implemented ) {
		super( context, action, implemented );
	}

}
