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
package org.mythtv.db.captureCard;

/**
 * @author Daniel Frey
 *
 */
public enum CaptureCardEnpoint {
	ADD_CAPTURE_CARD( "AddCaptureCard" ),
	ADD_CARD_INPUT( "AddCardInput" ),
	GET_CAPTURE_CARD( "GetCaptureCard" ),
	GET_CAPTURE_CARD_LIST( "GetCaptureCardList" ),
	REMOVE_CAPTURE_CARD( "RemoveCaptureCard" ),
	REMOVE_CARD_INPUT( "RemoveCardInput"),
	UPDATE_CAPTURE_CARD( "UpdateCaptureCard" ),
	UPDATE_CARD_INPUT( "UpdateCardInput" );
	
	private String endpoint;
	
	private CaptureCardEnpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
