/**
 * 
 */
package org.mythtv.services.api.capture.impl;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.mythtv.services.api.capture.CaptureCards;

/**
 * @author Daniel Frey
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
abstract class CaptureCardListMixin {
	@JsonCreator
	CaptureCardListMixin(
			@JsonProperty( "CaptureCardList" ) CaptureCards captureCards ) {}

}
