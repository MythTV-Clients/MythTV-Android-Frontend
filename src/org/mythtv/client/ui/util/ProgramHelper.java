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
		 * @return the category
		 */
		public String getCategory() {
			return category;
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

	private Context mContext;
	private static Resources mResources;
	
	public static ProgramHelper createInstance( Context context ) {
		return new ProgramHelper( context );
	}

	protected ProgramHelper( Context context ) {
		mContext = context;
		mResources = context.getResources();
	}
	
	public int getCategoryColor( String category ) {
		return getCategory( category ).getColor();
	}

	// internal helpers
	
	private Category getCategory( String category ) {
		return Category.fromString( category );
	}
	
}
