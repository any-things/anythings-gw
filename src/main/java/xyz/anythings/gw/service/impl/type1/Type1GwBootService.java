package xyz.anythings.gw.service.impl.type1;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.Indicator;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.entity.Rack;
import xyz.anythings.base.event.EventConstants;
import xyz.anythings.base.util.LogisEntityUtil;
import xyz.anythings.gw.event.GatewayRebootEvent;
import xyz.anythings.gw.model.GatewayInitResGwConfig;
import xyz.anythings.gw.model.GatewayInitResIndConfig;
import xyz.anythings.gw.model.GatewayInitResIndList;
import xyz.anythings.gw.model.GatewayInitResponse;
import xyz.anythings.gw.query.store.GwQueryStore;
import xyz.anythings.gw.service.api.IGwBootService;
import xyz.anythings.gw.service.util.GwQueryUtil;
import xyz.anythings.gw.service.util.RuntimeIndicatorSetting;
import xyz.anythings.gw.service.util.StageIndicatorSetting;
import xyz.anythings.sys.event.EventPublisher;
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
public class Type1GwBootService extends AbstractQueryService implements IGwBootService {
	
	/**
	 * Event Publisher
	 */
	@Autowired
	protected EventPublisher eventPublisher;
	/**
	 * 표시기 점등 서비스
	 */
	@Autowired
	private Type1IndicatorRequestService indSendService;
	/**
	 * 게이트웨이 관련 쿼리 스토어
	 */
	@Autowired
	private GwQueryStore gwQueryStore;
	
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
	 * @param gwNm
	 */
	public void respondGatewayBoot(JobBatch batch, String gwNm) {
		Gateway gateway = LogisEntityUtil.findEntityBy(batch.getDomainId(), true, Gateway.class, "gwNm", gwNm);
		this.respondGatewayBoot(gateway, batch);
	}
	
	/**
	 * 작업 배치, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param batch
	 * @param gateway
	 */
	public void respondGatewayBoot(JobBatch batch, Gateway gateway) {
		this.respondGatewayBoot(gateway, batch);
	}
	
	/**
	 * 게이트웨이 초기화 응답에 대한 처리.
	 * 
	 * @param domain
	 * @param gwNm
	 */
	public void respondGatewayBoot(Domain domain, String gwNm) {
		Gateway gateway = LogisEntityUtil.findEntityBy(domain.getId(), true, Gateway.class, "gwNm", gwNm);
		this.respondGatewayBoot(gateway, null);
	}
	
	/**
	 * Gateway, JobBatch, JobType으로 표시기 부팅 
	 * 
	 * @param gateway
	 * @param batch
	 */
	public void respondGatewayBoot(Gateway gateway, JobBatch batch) {
		// 1. domainId
		Long domainId = gateway.getDomainId();
		
		// 2. Gateway 초기화 설정 정보 가져오기.
		GatewayInitResponse gwInitRes = new GatewayInitResponse();
		gwInitRes.setGwConf(this.newGatewayInitConfig(gateway));
		
		// 3. Gateway 소속 표시기 List를 설정
		List<GatewayInitResIndList> indList = (batch != null) ?
				GwQueryUtil.searchIndListForGwInit(gateway) : GwQueryUtil.searchIndListForGwInit(gateway, batch);
		gwInitRes.setIndList(indList);
		
		// 4. Gateway가 관리하는 인디케이터 리스트 및 각각의 Indicator 별 설정 정보 가져오기.
		String jobType = (batch != null) ? batch.getJobType() : null;
		
		if(jobType == null && ValueUtil.isNotEmpty(indList)) {
			GatewayInitResIndList girl = indList.get(0);
			jobType = girl.getBizType();
		}
		
		GatewayInitResIndConfig gwInitResIndConfig = (batch != null) ?
				RuntimeIndicatorSetting.getGatewayBootConfig(batch, gateway) : StageIndicatorSetting.getGatewayBootConfig(gateway);
		gwInitRes.setIndConf(gwInitResIndConfig);
		
		// 5. Gateway 최신버전 정보 설정.
		String latestGatewayVer = (batch != null) ? 
				RuntimeIndicatorSetting.getGwLatestReleaseVersion(batch, gateway) : StageIndicatorSetting.getGwLatestReleaseVersion(gateway);
		gwInitRes.setGwVersion(latestGatewayVer);
		
		// 6. Indicator 최신버전 정보 설정.
		String latestIndVer = (batch != null) ? 
				RuntimeIndicatorSetting.getIndLatestReleaseVersion(batch) : StageIndicatorSetting.getIndLatestReleaseVersion(gateway);
		gwInitRes.setIndVersion(latestIndVer);

		// 7. 현재 시간 설정 - 밀리세컨드 제외
		gwInitRes.setSvrTime((long)(new Date().getTime() / 1000));
		
		// 8. 상태 보고 주기 설정.
		int healthPeriod = (batch != null) ? 
				RuntimeIndicatorSetting.getIndHealthPeriod(batch) : StageIndicatorSetting.getIndHealthPeriod(gateway.getDomainId(), gateway.getStageCd()); 
		gwInitRes.setHealthPeriod(healthPeriod);
		
		// 9. 게이트웨이 초기화 응답 전송 
		this.indSendService.respondGatewayInit(domainId, gateway.getGwNm(), gwInitRes);		
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
		String sql = this.gwQueryStore.getEquipListByGateway();
		// FIXME 설비 타입, 설비 코드를 동시에 받아서 처리하기 ...
		List<String> rackCdList = LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,gwCd,equipType", domainId, gateway.getGwCd(), "Rack");
		
		// 2. 호기로 부터 현재 작업 중인 배치 추출 
		for(String rackCd : rackCdList) {
			// 2-1. 호기 체크
			Rack rack = LogisEntityUtil.findEntityByCode(domainId, true, Rack.class, "rackCd", rackCd);
			
			if(rack == null || ValueUtil.isEmpty(rack.getBatchId())) {
				continue;
			}
			
			// 2-2. 작업 배치 및 상태 체크
			JobBatch batch = LogisEntityUtil.findEntityById(false, JobBatch.class, rack.getBatchId());
			
			if(batch == null || ValueUtil.isNotEqual(batch.getStatus(), JobBatch.STATUS_RUNNING)) {
				continue;
			}
			
			// 2-3. 호기 코드, 게이트웨이 코드로 표시기 이전 상태 복원
			GatewayRebootEvent gwRebbotEvent = new GatewayRebootEvent(EventConstants.EVENT_STEP_AFTER, gateway, batch);
			this.eventPublisher.publishEvent(gwRebbotEvent);
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
