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
package org.mythtv.db.content;

import org.mythtv.provider.MythtvProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class ArtworkConstants implements BaseColumns {

	public static final String TABLE_NAME = "artwork";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";

	public static final String FIELD_URL = "URL";
	public static final String FIELD_URL_DATA_TYPE = "TEXT";

	public static final String FIELD_FILE_NAME = "FILE_NAME";
	public static final String FIELD_FILE_NAME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_STORAGE_GROUP = "STORAGE_GROUP";
	public static final String FIELD_STORAGE_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_TYPE_DATA_TYPE = "TEXT";
	
}
