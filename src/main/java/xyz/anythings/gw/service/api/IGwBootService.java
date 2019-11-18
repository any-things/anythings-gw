package xyz.anythings.gw.service.api;

import java.util.List;

import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.gw.entity.Indicator;
import xyz.anythings.gw.service.model.GwIndInit;

/**
 * 게이트웨이 부트 서비스 인터페이스
 * 
 * @author shortstop
 */
public interface IGwBootService {
	
	/**
	 * 게이트웨이에서의 부팅 요청에 대한 애플리케이션 측 응답
	 * 
	 * @param gateway 게이트웨이 
	 * @param indList 표시기 리스트
	 * @return
	 */
	public boolean gatewayBootResponse(Gateway gateway, List<GwIndInit> indList);
	
	/**
	 * 게이트웨이에서의 부팅 요청에 대한 애플리케이션 측 응답
	 * 
	 * @param gateway 게이트웨이 
	 * @param batchId 배치 ID
	 * @param indList 표시기 리스트
	 * @return
	 */
	public boolean gatewayBootResponse(Gateway gateway, String batchId, List<GwIndInit> indList);
		
	/**
	 * 게이트웨이 초기화 완료 리포트에 대한 애플리케이션 측 처리
	 * 
	 * @param gateway
	 * @param params
	 */
	public void handleGatewayInitReport(Gateway gateway, Object ... params);

	/**
	 * 표시기 초기화 완료 리포트에 대한 애플리케이션 측 처리
	 * 
	 * @param indicator
	 * @param params
	 */
	public void handleIndicatorInitReport(Indicator indicator, Object ... params);

}
