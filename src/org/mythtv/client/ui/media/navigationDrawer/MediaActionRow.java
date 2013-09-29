/**
 * 
 */
package org.mythtv.client.ui.media.navigationDrawer;

import org.mythtv.client.ui.navigationDrawer.ActionRow;

import android.content.Context;

/**
 * @author dmfrey
 *
 */
public abstract class MediaActionRow extends ActionRow {

	public MediaActionRow( Context context, int actionResId, boolean implemented ) {
		super( context, actionResId, implemented );
	}

}
