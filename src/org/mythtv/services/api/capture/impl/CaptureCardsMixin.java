/**
 * 
 */
package org.mythtv.services.api.capture.impl;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.mythtv.services.api.capture.CaptureCard;

/**
 * @author Daniel Frey
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
abstract class CaptureCardsMixin {
	@JsonCreator
	CaptureCardsMixin(
			@JsonProperty( "CaptureCards" ) List<CaptureCard> captureCards ) {}

}
