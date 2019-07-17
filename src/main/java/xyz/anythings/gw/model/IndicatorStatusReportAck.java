package xyz.anythings.gw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonTypeName(Action.Values.IndicatorStatusReportAck)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class IndicatorStatusReportAck implements IMessageBody {
	@JsonIgnore
	private String action=Action.Values.IndicatorStatusReportAck;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
