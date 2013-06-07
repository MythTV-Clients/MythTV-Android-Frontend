/**
 * 
 */
package org.mythtv.client.service.delegate;

import org.mythtv.client.service.DvrService;

import android.content.Context;
import android.util.Log;

/**
 * @author dmfrey
 *
 */
public class DvrServiceImpl extends AbstractBaseService implements DvrService {
	
	private static final String TAG = DvrServiceImpl.class.getSimpleName();
	
	private Context mContext;
	
	public DvrServiceImpl( Context context ) {
		Log.v( TAG, "initialize : enter" );
		
		this.mContext = context;

		Log.v( TAG, "initialize : exit" );
	}
	
}
