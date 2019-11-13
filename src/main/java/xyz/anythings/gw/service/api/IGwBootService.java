package xyz.anythings.gw.service.api;

import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.JobBatch;
import xyz.elidom.sys.entity.Domain;

/**
 * 게이트웨이 부트 서비스 인터페이스
 * 
 * @author shortstop
 */
public interface IGwBootService {
	/**
	 * 배치 ID, 게이트웨이 정보로 게이트웨이 부트
	 *  
	 * @param domainId
	 * @param batchId
	 * @param gwNm
	 */
	public void respondGatewayBoot(Long domainId, String batchId, String gwNm);
	
	/**
	 * 배치 ID, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param domainId
	 * @param batchId
	 * @param gateway
	 */
	public void respondGatewayBoot(Long domainId, String batchId, Gateway gateway);

	/**
	 * 작업 배치, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param batch
	 * @param gwNm
	 */
	public void respondGatewayBoot(JobBatch batch, String gwNm);
	
	/**
	 * 작업 배치, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param batch
	 * @param gateway
	 */
	public void respondGatewayBoot(JobBatch batch, Gateway gateway);
	
	/**
	 * 게이트웨이 초기화 응답에 대한 처리.
	 * 
	 * @param domain
	 * @param gwNm
	 */
	public void respondGatewayBoot(Domain domain, String gwNm);
	
	/**
	 * Gateway, JobBatch, JobType으로 표시기 부팅 
	 * 
	 * @param gateway
	 * @param batch
	 */
	public void respondGatewayBoot(Gateway gateway, JobBatch batch);
	
	/**
	 * Gateway 초기화 리포트에 대한 처리.(Gateway Version 정보 Update)
	 * 
	 * @param domain
	 * @param gwNm
	 * @param version
	 */
	public void handleGatewayInitReport(Domain domain, String gwNm, String version);
	
	/**
	 * 게이트웨이 별로 이전 작업 (표시기) 상황을 다시 조회해서 재점등
	 * 
	 * @param domain
	 * @param gateway
	 */
	public void indicatorsOnByGateway(Domain domain, Gateway gateway);

	/**
	 * Indicator 초기화 리포트에 대한 처리. (Indicator Version 정보 Update)
	 * 
	 * @param domain
	 * @param indCd
	 * @param version
	 */
	public void handleIndInitReport(Domain domain, String indCd, String version);

}
