/**
 * 
 */
package org.mythtv.services.api.capture.impl;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
abstract class CaptureCardMixin {
	@JsonCreator
	CaptureCardMixin(
			@JsonProperty( "CardId" ) int id, 
			@JsonProperty( "VideoDevice" ) String videoDevice, 
			@JsonProperty( "AudioDevice" ) String audioDevice,
			@JsonProperty( "VBIDevice" ) String vbiDevice,
			@JsonProperty( "CardType" ) String cartType,
			@JsonProperty( "AudioRateLimit" ) int audioRateLimit,
			@JsonProperty( "HostName" ) String hostName,
			@JsonProperty( "DVBSWFilter" ) int dvbSwFilter,
			@JsonProperty( "DVBSatType" ) int dvbSatType,
			@JsonProperty( "DVBWaitForSeqStart" ) boolean dvbWaitForSeqStart,
			@JsonProperty( "SkipBTAudio" ) boolean skipBtAudio,
			@JsonProperty( "DVBOnDemand" ) boolean dvbOnDemand,
			@JsonProperty( "DVBDiSEqCType" ) int dvbDiSEqCType,
			@JsonProperty( "FirewireSpeed" ) int firewireSpeed, 
			@JsonProperty( "FirewireModel" ) String firewireModel,
			@JsonProperty( "FirewireConnection" ) int firewireConnection,
			@JsonProperty( "SignalTimeout" ) int signalTimeout,
			@JsonProperty( "ChannelTimeout" ) int channelTimeout,
			@JsonProperty( "DVBTuningDelay" ) int dvbTuningDelay,
			@JsonProperty( "Contrast" ) int contrast,
			@JsonProperty( "Brightness" ) int brightness,
			@JsonProperty( "Colour" ) int colour,
			@JsonProperty( "Hue" ) int hue,
			@JsonProperty( "DiSEqCId" ) int diSEqCId,
			@JsonProperty( "DVBEITScan" ) boolean dvbEitScan ) {}
}
