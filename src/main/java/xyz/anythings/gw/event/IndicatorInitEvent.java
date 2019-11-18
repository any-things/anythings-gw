package xyz.anythings.gw.event;

import xyz.anythings.gw.entity.Indicator;

/**
 * 표시기 초기화 완료 보고에 대한 처리 이벤트
 * 
 * @author shortstop
 */
public class IndicatorInitEvent extends AbstractGatewayEvent {

	/**
	 * 표시기
	 */
	private Indicator indicator;
	
	/**
	 * 생성자
	 * 
	 * @param eventStep
	 * @param indicator
	 */
	public IndicatorInitEvent(short eventStep, Indicator indicator) {
		this.setEventStep(eventStep);
		this.setIndicator(indicator);
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

}
