package xyz.anythings.gw.event;

import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.sys.event.model.SysEvent;

/**
 * 게이트웨이 리부팅 이벤트
 * 
 * @author shortstop
 */
public class GatewayRebootEvent extends SysEvent {

	/**
	 * 이벤트 스텝 - 1 : 전 처리 , 2 : 후 처리
	 */
	protected short eventStep;
	/**
	 * 게이트웨이 
	 */
	private Gateway gateway;
	/**
	 * 스테이지 코드
	 */
	private String stageCd;
	/**
	 * 작업 유형 
	 */
	private String jobType;
	/**
	 * 작업 배치
	 */
	private String batchId;
	
	public GatewayRebootEvent(short eventStep, Gateway gateway, String stageCd, String batchId, String jobType) {
		this.eventStep = eventStep;
		this.gateway = gateway;
		this.stageCd = stageCd;
		this.batchId = batchId;
		this.jobType = jobType;
	}

	public short getEventStep() {
		return eventStep;
	}

	public void setEventStep(short eventStep) {
		this.eventStep = eventStep;
	}

	public Gateway getGateway() {
		return gateway;
	}

	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

	public String getStageCd() {
		return stageCd;
	}

	public void setStageCd(String stageCd) {
		this.stageCd = stageCd;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

}
