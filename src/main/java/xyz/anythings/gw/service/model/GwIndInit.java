package xyz.anythings.gw.service.model;

/**
 * 게이트웨이 표시기 초기화 정보
 * 
 * @author shortstop
 */
public class GwIndInit {
	/**
	 * 인디케이터 id
	 */
	private String id;
	/**
	 * 무선 주파수 채널 
	 */
	private String channel;
	/**
	 * PAN 번호
	 */
	private String pan;
	/**
	 * 업무 유형 
	 */
	private String bizType;
	/**
	 * 뷰 형식 - 표시기 숫자 표현 형식 (0 : 기본, 1 : 박스/낱개, 2 : 누적수량/낱개)
	 */
	private String viewType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

}
