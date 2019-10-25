package xyz.anythings.gw.event;

import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.event.main.BatchRootEvent;

/**
 * 게이트웨이 리부팅 이벤트
 * 
 * @author shortstop
 */
public class GatewayRebootEvent extends BatchRootEvent {

	private Gateway gateway;
	
	public GatewayRebootEvent(short eventStep, Gateway gateway, JobBatch batch) {
		super(eventStep);
		super.setJobBatch(batch);
	}

	public Gateway getGateway() {
		return gateway;
	}

	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

}
