package xyz.anythings.gw.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.Indicator;
import xyz.anythings.base.entity.Rack;
import xyz.anythings.base.util.LogisEntityUtil;
import xyz.anythings.gw.model.GatewayInitResGwConfig;
import xyz.anythings.gw.model.GatewayInitResIndList;
import xyz.anythings.gw.model.GatewayInitResponse;
import xyz.anythings.gw.service.util.IndServiceUtil;
import xyz.anythings.gw.service.util.IndicatorSetting;
import xyz.anythings.sys.service.AbstractQueryService;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.util.ValueUtil;

/**
 * Gateway, Indicator Boot 프로세스
 * 
 * @author shortstop
 */
@Component
public class GwBootService extends AbstractQueryService {
	
	/**
	 * 표시기 점등 서비스
	 */
	@Autowired
	private IndSendService indSendService;
	
	/**
	 * 배치 ID, 게이트웨이 정보로 게이트웨이 부트
	 *  
	 * @param domainId
	 * @param batchId
	 * @param gwNm
	 */
	public void respondGatewayBoot(Long domainId, String batchId, String gwNm) {
		JobBatch batch = LogisEntityUtil.findEntityById(true, JobBatch.class, batchId);
		Gateway gateway = LogisEntityUtil.findEntityBy(domainId, true, Gateway.class, "gwNm", gwNm);
		this.respondGatewayBoot(batch, gateway);
	}
	
	/**
	 * 배치 ID, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param domainId
	 * @param batchId
	 * @param gateway
	 */
	public void respondGatewayBoot(Long domainId, String batchId, Gateway gateway) {
		JobBatch batch = LogisEntityUtil.findEntityById(true, JobBatch.class, batchId);
		this.respondGatewayBoot(batch, gateway);
	}
	
	/**
	 * 작업 배치, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param batch
	 * @param gateway
	 */
	public void respondGatewayBoot(JobBatch batch, Gateway gateway) {
		// 1. 데이터 
		Long domainId = batch.getDomainId();
		String jobType = batch.getJobType();
		String gwNm = gateway.getGwNm();
		
		// 2. Gateway 초기화 설정 정보 가져오기.
		GatewayInitResponse gwInitRes = new GatewayInitResponse();
		gwInitRes.setGwConf(this.newGatewayInitConfig(gateway));
		
		// 3. Gateway 소속 표시기 List를 설정
		List<GatewayInitResIndList> indList = IndServiceUtil.indListForGwInit(gateway);
		gwInitRes.setIndList(indList);
		
		// 4. Gateway가 관리하는 인디케이터 리스트 및 각각의 Indicator 별 설정 정보 가져오기.
		gwInitRes.setIndConf(IndicatorSetting.getGatewayBootConfig(domainId, jobType));
		
		// 5. Gateway 최신버전 정보 설정.
		String latestGatewayVer = IndicatorSetting.getGwLatestReleaseVersion(domainId);
		gwInitRes.setGwVersion(latestGatewayVer);
		
		// 6. Indicator 최신버전 정보 설정.
		String latestIndVer = IndicatorSetting.getIndLatestReleaseVersion(domainId);
		gwInitRes.setIndVersion(latestIndVer);

		// 7. 현재 시간 설정 - 밀리세컨드 제외
		gwInitRes.setSvrTime((long)(new Date().getTime() / 1000));
		
		// 8. 상태 보고 주기 설정.
		gwInitRes.setHealthPeriod(IndicatorSetting.getIndHealthPeriod(domainId));
		
		// 9. 게이트웨이 초기화 응답 전송 
		this.indSendService.respondGatewayInit(domainId, gwNm, gwInitRes);
	}
	
	/**
	 * 게이트웨이 초기화 응답에 대한 처리.
	 * 
	 * @param domain
	 * @param gwNm
	 */
	public void respondGatewayBoot(Domain domain, String gwNm) {
		// 1. 게이트웨이 조회
		Long domainId = domain.getId();
		Gateway gateway = LogisEntityUtil.findEntityBy(domainId, true, Gateway.class, "gwNm", gwNm);

		// 2. Gateway 초기화 설정 정보 가져오기.
		GatewayInitResponse gwInitRes = new GatewayInitResponse();
		gwInitRes.setGwConf(this.newGatewayInitConfig(gateway));
		
		// 3. Gateway 소속 표시기 List를 설정
		List<GatewayInitResIndList> indList = IndServiceUtil.indListForGwInit(gateway);
		gwInitRes.setIndList(indList);
		
		// 4. Gateway가 관리하는 인디케이터 리스트 및 각각의 Indicator 별 설정 정보 가져오기.
		String jobType = null;
		if(ValueUtil.isNotEmpty(indList)) {
			GatewayInitResIndList girl = indList.get(0);
			jobType = girl.getBizType();
		}
		
		gwInitRes.setIndConf(IndicatorSetting.getGatewayBootConfig(domainId, jobType));
		
		// 5. Gateway 최신버전 정보 설정.
		String latestGatewayVer = IndicatorSetting.getGwLatestReleaseVersion(domainId);
		gwInitRes.setGwVersion(latestGatewayVer);
		
		// 6. Indicator 최신버전 정보 설정.
		String latestIndVer = IndicatorSetting.getIndLatestReleaseVersion(domainId);
		gwInitRes.setIndVersion(latestIndVer);

		// 7. 현재 시간 설정 - 밀리세컨드 제외
		gwInitRes.setSvrTime((long)(new Date().getTime() / 1000));
		
		// 8. 상태 보고 주기 설정.
		gwInitRes.setHealthPeriod(IndicatorSetting.getIndHealthPeriod(domainId));
		
		// 9. 게이트웨이 초기화 응답 전송 
		this.indSendService.respondGatewayInit(domainId, gwNm, gwInitRes);
	}
	
	/**
	 * Gateway 초기화 리포트에 대한 처리.(Gateway Version 정보 Update)
	 * 
	 * @param domain
	 * @param gwNm
	 * @param version
	 */
	public void handleGatewayInitReport(Domain domain, String gwNm, String version) {
		// Gateway 정보 조회
		Gateway gateway = LogisEntityUtil.findEntityByCode(domain.getId(), true, Gateway.class, "gwNm", gwNm);
		
		if(gateway != null) {
			if (ValueUtil.isNotEqual(gateway.getVersion(), version)) {
				// Gateway Version 정보 업데이트
				gateway.setVersion(version);
				this.queryManager.update(gateway, OrmConstants.ENTITY_FIELD_VERSION);
			}
			
			this.indicatorsOnByGateway(domain, gateway);
		}
	}
	
	/**
	 * 게이트웨이 별로 이전 작업 (표시기) 상황을 다시 조회해서 재점등
	 * 
	 * @param domain
	 * @param gateway
	 */
	public void indicatorsOnByGateway(Domain domain, Gateway gateway) {
		// 1. Gateway 정보로 호기 리스트 추출
		Long domainId = domain.getId();
		String sql = "select distinct(rack_cd) as rack_cd from cells where domain_id = :domainId and ind_cd in (select ind_cd from indicators where domain_id = :domainId and gw_cd = :gwCd) order by rack_cd";
		List<String> rackCdList = this.queryManager.selectListBySql(sql, ValueUtil.newMap("domainId,gwCd", domainId, gateway.getGwCd()), String.class, 0, 0);
		
		// 2. 호기로 부터 현재 작업 중인 배치 추출 
		for(String rackCd : rackCdList) {
			// 2-1. 호기 체크
			Rack rack = LogisEntityUtil.findEntityByCode(domainId, true, Rack.class, "rackCd", rackCd);
			
			if(rack == null || ValueUtil.isEmpty(rack.getBatchId())) {
				continue;
			}
			
			// 2-2. TODO 이벤트 전달하여 해당 모듈 (DAS, DPS, 반품 등)에서 처리하도록 수정 필요
			
			// 2-2. 작업 배치 및 상태 체크
			// JobBatch batch = LogisEntityUtil.findEntityById(true, JobBatch.class, rack.getBatchId());
			
			// if(batch == null || ValueUtil.isNotEqual(batch.getStatus(), JobBatch.STATUS_RUNNING)) {
			//	continue;
			// }
			
			// 2-3. 호기 코드, 게이트웨이 코드로 표시기 이전 상태 복원
			// this.getAssortService(batch).restoreMpiOn(batch, gateway);
		}
	}

	/**
	 * Indicator 초기화 리포트에 대한 처리. (Indicator Version 정보 Update)
	 * 
	 * @param domain
	 * @param indCd
	 * @param version
	 */
	public void handleIndInitReport(Domain domain, String indCd, String version) {
		Indicator indicator = LogisEntityUtil.findEntityByCode(domain.getId(), true, Indicator.class, "indCd", indCd);
		
		if (indicator != null && ValueUtil.isNotEqual(indicator.getVersion(), version)) {
			indicator.setVersion(version);
			this.queryManager.update(indicator, OrmConstants.ENTITY_FIELD_VERSION);
		}
	}

	/**
	 * Gateway 초기화 설정 정보 생성
	 * 
	 * @param gateway
	 * @return
	 */
	private GatewayInitResGwConfig newGatewayInitConfig(Gateway gateway) {
		GatewayInitResGwConfig initConfig = new GatewayInitResGwConfig();
		initConfig.setId(gateway.getGwNm());
		initConfig.setChannel(gateway.getChannelNo());
		initConfig.setPan(gateway.getPanNo());
		return initConfig;
	}

}
