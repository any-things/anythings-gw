package xyz.anythings.gw.service.mw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonTypeName(Action.Values.GatewayDepRequestAck)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class GatewayDepRequestAck implements IMessageBody {
	@JsonIgnore
	private String action=Action.Values.GatewayDepRequestAck;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
