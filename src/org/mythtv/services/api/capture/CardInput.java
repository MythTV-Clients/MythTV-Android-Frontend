/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.capture;

/**
 * @author Daniel Frey
 *
 */
public class CardInput {

	private int cardId;
	private int sourceId;
	private String inputName;
	private String externalCommand;
	private String changerDevice;
	private String changerModel;
	private String Hosthame;
	private String tuneChannel;
	private String startChannel;
	private String displayName;
	private boolean dishnetEIT;
	private int recordingPriority;
	private int quicktune;
	private int schedOrder;
	private int liveTVOrder;

	public CardInput() { }

	/**
	 * @return the cardId
	 */
	public int getCardId() {
		return cardId;
	}

	/**
	 * @param cardId the cardId to set
	 */
	public void setCardId( int cardId ) {
		this.cardId = cardId;
	}

	/**
	 * @return the sourceId
	 */
	public int getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId( int sourceId ) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the inputName
	 */
	public String getInputName() {
		return inputName;
	}

	/**
	 * @param inputName the inputName to set
	 */
	public void setInputName( String inputName ) {
		this.inputName = inputName;
	}

	/**
	 * @return the externalCommand
	 */
	public String getExternalCommand() {
		return externalCommand;
	}

	/**
	 * @param externalCommand the externalCommand to set
	 */
	public void setExternalCommand( String externalCommand ) {
		this.externalCommand = externalCommand;
	}

	/**
	 * @return the changerDevice
	 */
	public String getChangerDevice() {
		return changerDevice;
	}

	/**
	 * @param changerDevice the changerDevice to set
	 */
	public void setChangerDevice( String changerDevice ) {
		this.changerDevice = changerDevice;
	}

	/**
	 * @return the changerModel
	 */
	public String getChangerModel() {
		return changerModel;
	}

	/**
	 * @param changerModel the changerModel to set
	 */
	public void setChangerModel( String changerModel ) {
		this.changerModel = changerModel;
	}

	/**
	 * @return the hosthame
	 */
	public String getHosthame() {
		return Hosthame;
	}

	/**
	 * @param hosthame the hosthame to set
	 */
	public void setHosthame( String hosthame ) {
		Hosthame = hosthame;
	}

	/**
	 * @return the tuneChannel
	 */
	public String getTuneChannel() {
		return tuneChannel;
	}

	/**
	 * @param tuneChannel the tuneChannel to set
	 */
	public void setTuneChannel( String tuneChannel ) {
		this.tuneChannel = tuneChannel;
	}

	/**
	 * @return the startChannel
	 */
	public String getStartChannel() {
		return startChannel;
	}

	/**
	 * @param startChannel the startChannel to set
	 */
	public void setStartChannel( String startChannel ) {
		this.startChannel = startChannel;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	/**
	 * @return the dishnetEIT
	 */
	public boolean isDishnetEIT() {
		return dishnetEIT;
	}

	/**
	 * @param dishnetEIT the dishnetEIT to set
	 */
	public void setDishnetEIT( boolean dishnetEIT ) {
		this.dishnetEIT = dishnetEIT;
	}

	/**
	 * @return the recordingPriority
	 */
	public int getRecordingPriority() {
		return recordingPriority;
	}

	/**
	 * @param recordingPriority the recordingPriority to set
	 */
	public void setRecordingPriority( int recordingPriority ) {
		this.recordingPriority = recordingPriority;
	}

	/**
	 * @return the quicktune
	 */
	public int getQuicktune() {
		return quicktune;
	}

	/**
	 * @param quicktune the quicktune to set
	 */
	public void setQuicktune( int quicktune ) {
		this.quicktune = quicktune;
	}

	/**
	 * @return the schedOrder
	 */
	public int getSchedOrder() {
		return schedOrder;
	}

	/**
	 * @param schedOrder the schedOrder to set
	 */
	public void setSchedOrder( int schedOrder ) {
		this.schedOrder = schedOrder;
	}

	/**
	 * @return the liveTVOrder
	 */
	public int getLiveTVOrder() {
		return liveTVOrder;
	}

	/**
	 * @param liveTVOrder the liveTVOrder to set
	 */
	public void setLiveTVOrder( int liveTVOrder ) {
		this.liveTVOrder = liveTVOrder;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append( "CardInput [cardId=" );
		builder.append( cardId );
		builder.append( ", sourceId=" );
		builder.append( sourceId );
		builder.append( ", " );
		
		if( inputName != null ) {
			builder.append( "inputName=" );
			builder.append( inputName );
			builder.append( ", " );
		}
		
		if( externalCommand != null ) {
			builder.append( "externalCommand=" );
			builder.append( externalCommand );
			builder.append( ", " );
		}
		
		if( changerDevice != null ) {
			builder.append( "changerDevice=" );
			builder.append( changerDevice );
			builder.append( ", " );
		}
		
		if( changerModel != null ) {
			builder.append( "changerModel=" );
			builder.append( changerModel );
			builder.append( ", " );
		}
		
		if( Hosthame != null ) {
			builder.append( "Hosthame=" );
			builder.append( Hosthame );
			builder.append( ", " );
		}
		
		if( tuneChannel != null ) {
			builder.append( "tuneChannel=" );
			builder.append( tuneChannel );
			builder.append( ", " );
		}
		
		if( startChannel != null ) {
			builder.append( "startChannel=" );
			builder.append( startChannel );
			builder.append( ", " );
		}
		
		if( displayName != null ) {
			builder.append( "displayName=" );
			builder.append( displayName );
			builder.append( ", " );
		}
		
		builder.append( "dishnetEIT=" );
		builder.append( dishnetEIT );
		builder.append( ", recordingPriority=" );
		builder.append( recordingPriority );
		builder.append( ", quicktune=" );
		builder.append( quicktune );
		builder.append( ", schedOrder=" );
		builder.append( schedOrder );
		builder.append( ", liveTVOrder=" );
		builder.append( liveTVOrder );
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
