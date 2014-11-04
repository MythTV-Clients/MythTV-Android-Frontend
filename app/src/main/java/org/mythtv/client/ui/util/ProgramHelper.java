/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
/**
 * 
 */
package org.mythtv.client.ui.util;

import org.mythtv.R;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author Daniel Frey
 *
 */
public class ProgramHelper {

	private static enum Category {
		ACTION( "Action", mResources.getColor( R.color.program_category_Action ) ),
		ADULT( "Adult", mResources.getColor( R.color.program_category_Adult ) ),
		ANIMALS( "Animals", mResources.getColor( R.color.program_category_Animals ) ),
		ART_MUSIC( "Art/Music", mResources.getColor( R.color.program_category_Art_Music ) ),
		BUSINESS( "Business", mResources.getColor( R.color.program_category_Business ) ),
		CHILDREN( "Children", mResources.getColor( R.color.program_category_Children ) ),
		COMEDY( "Comedy", mResources.getColor( R.color.program_category_Comedy ) ),
		COOKING( "Cooking", mResources.getColor( R.color.program_category_Food ) ),
		CRIME_MYSTERY( "Crime/Mystery", mResources.getColor( R.color.program_category_Crime_Mystery ) ),
		CRIME_DRAMA( "Crime drama", mResources.getColor( R.color.program_category_Crime_Mystery ) ),
		DOCUMENTARY( "Documentary", mResources.getColor( R.color.program_category_Documentary ) ),
		DRAMA( "Drama", mResources.getColor( R.color.program_category_Drama ) ),
		EDUCATIONAL( "Educational", mResources.getColor( R.color.program_category_Educational ) ),
		FOOD( "Food", mResources.getColor( R.color.program_category_Food ) ),
		GAME( "Game", mResources.getColor( R.color.program_category_Game ) ),
		HEALTH_MEDICAL( "Health/Medical", mResources.getColor( R.color.program_category_Health_Medical ) ),
		HISTORY( "History", mResources.getColor( R.color.program_category_History ) ),
		HORROR( "Horror", mResources.getColor( R.color.program_category_Horror ) ),
		HOWTO( "HowTo", mResources.getColor( R.color.program_category_HowTo ) ),
		MISC( "Misc", mResources.getColor( R.color.program_category_Misc ) ),
		MOVIE( "Movie", mResources.getColor( R.color.program_category_Movie ) ),
		MUSIC( "Music", mResources.getColor( R.color.program_category_Art_Music ) ),
		NEWS( "News", mResources.getColor( R.color.program_category_News ) ),
		REALITY( "Reality", mResources.getColor( R.color.program_category_Reality ) ),
		ROMANCE( "Romance", mResources.getColor( R.color.program_category_Romance ) ),
		SCIENCE_NATURE( "Science/Nature", mResources.getColor( R.color.program_category_Science_Nature ) ),
		SCIFI_FANTASY( "SciFi/Fantasy", mResources.getColor( R.color.program_category_SciFi_Fantasy ) ),
		SHOPPING( "Shopping", mResources.getColor( R.color.program_category_Shopping ) ),
		SITCOM( "Sitcom", mResources.getColor( R.color.program_category_Comedy ) ),
		SOAPS( "Soaps", mResources.getColor( R.color.program_category_Soaps ) ),
		SPIRITUAL( "Spiritual", mResources.getColor( R.color.program_category_Spiritual ) ),
		SPORTS( "Sports", mResources.getColor( R.color.program_category_Sports ) ),
		TALK( "Talk", mResources.getColor( R.color.program_category_Talk ) ),
		TRAVEL( "Travel", mResources.getColor( R.color.program_category_Travel ) ),
		UNKNOWN( "Unknown", mResources.getColor( R.color.program_category_Unknown ) ),
		WAR( "War", mResources.getColor( R.color.program_category_War ) ),
		WESTERN( "Western", mResources.getColor( R.color.program_category_Western ) );
		
		private String category;
		private int color;
		
		private Category( String category, int color ) {
			this.category = category;
			this.color = color;
		}

		/**
		 * @return the color
		 */
		public int getColor() {
			return color;
		}

		/**
		 * @param category
		 * @return
		 */
		public static Category fromString( String category ) {
			if( null != category ) {
				for( Category c : Category.values() ) {
					if( category.equalsIgnoreCase( c.category ) ) {
						return c;
					}
				}
			}
			
			return UNKNOWN;
		}
		
	}

	private static ProgramHelper singleton = null;

	private Context mContext;
	private static Resources mResources;
	
	/**
	 * Returns the one and only ProgramHelper. init() must be called before 
	 * any 
	 * @return
	 */
	public static ProgramHelper getInstance() {
		if( null == singleton ) {
			
			synchronized( ProgramHelper.class ) {

				if( null == singleton ) {
					singleton = new ProgramHelper();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private ProgramHelper() { }
	
	/**
	 * Must be called once at the beginning of the application. Subsequent 
	 * calls to this will have no effect.
	 * 
	 * @param context
	 */
	public void init( Context context ) {
		
		//ignore any additional calls to init
		if( this.isInitialized() ) 
			return;
		
		this.mContext = context;
		ProgramHelper.mResources = mContext.getResources();
	}
	
	/**
	 * Returns true if ProgramHelper has already been initialized
	 * @return
	 */
	public boolean isInitialized(){
		return null != this.mContext;
	}
	
	/**
	 * @param category
	 * @return
	 */
	public int getCategoryColor( String category ) {
		return getCategory( category ).getColor();
	}

	// internal helpers
	
	private Category getCategory( String category ) {
		return Category.fromString( category );
	}
	
}
