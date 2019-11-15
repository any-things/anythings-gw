package xyz.anythings.gw.service.api;

import java.util.List;

import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.gw.model.GatewayInitResIndList;

/**
 * 게이트웨이 부트 서비스 인터페이스
 * 
 * @author shortstop
 */
public interface IGwBootService {
	/**
	 * 게이트웨이, batchId, 표시기 정보 리스트로 게이트웨이 리부팅
	 * 
	 * @param gateway
	 * @param batchId
	 * @param indList
	 */
	public void respondGatewayBoot(Gateway gateway, String batchId, List<GatewayInitResIndList> indList);
	
	/**
	 * 게이트웨이 별로 이전 작업 (표시기) 상황을 다시 조회해서 재점등
	 * 
	 * @param gateway
	 */
	public void restoreIndicatorsOn(Gateway gateway);
	
	/**
	 * 게이트웨이 초기화 리포트에 대한 처리.(Gateway Version 정보 Update)
	 * 
	 * @param domainId
	 * @param gwNm
	 * @param version
	 */
	public void handleGatewayInitReport(Long domainId, String gwNm, String version);

	/**
	 * 표시기 초기화 리포트에 대한 처리. (Indicator Version 정보 Update)
	 * 
	 * @param domainId
	 * @param indCd
	 * @param version
	 */
	public void handleIndInitReport(Long domainId, String indCd, String version);

}
