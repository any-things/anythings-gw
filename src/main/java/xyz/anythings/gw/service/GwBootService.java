package xyz.anythings.gw.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.base.entity.Region;
import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.gw.entity.MPI;
import xyz.anythings.gw.model.GatewayInitResGwConfig;
import xyz.anythings.gw.model.GatewayInitResIndList;
import xyz.anythings.gw.model.GatewayInitResponse;
import xyz.anythings.gw.service.util.MpiSetting;
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
	
	@Autowired
	private MpiSendService mpiSendService;
	
	/**
	 * 작업 유형, 게이트웨이 정보로 게이트웨이 부트
	 * 
	 * @param domain
	 * @param jobType
	 * @param gwNm
	 */
	public void respondGatewayBoot(Long domainId, String jobType, String gwNm) {		
		// 1. 게이트웨이 조회 
		Gateway gateway = this.queryManager.selectByCondition(true, Gateway.class, ValueUtil.newMap("domainId,gwNm", domainId, gwNm));

		// 2. Gateway 초기화 설정 정보 가져오기.
		GatewayInitResponse gwInitRes = new GatewayInitResponse();
		gwInitRes.setGwConf(this.newGatewayInitConfig(gateway));
		
		// 3. Gateway 소속 MPI List를 설정
		List<GatewayInitResIndList> indList = gateway.mpiListForGwInit();
		gwInitRes.setIndList(indList);
		
		// 4. Gateway가 관리하는 인디케이터 리스트 및 각각의 Indicator 별 설정 정보 가져오기.
		gwInitRes.setIndConf(MpiSetting.getGatewayBootConfig(domainId, jobType));
		
		// 5. Gateway 최신버전 정보 설정.
		String latestGatewayVer = MpiSetting.getGatewayLatestReleaseVersion(domainId);
		gwInitRes.setGwVersion(latestGatewayVer);
		
		// 6. Indicator 최신버전 정보 설정.
		String latestMpiVer = MpiSetting.getMpiLatestReleaseVersion(domainId);
		gwInitRes.setIndVersion(latestMpiVer);

		// 7. 현재 시간 설정 - 밀리세컨드 제외
		gwInitRes.setSvrTime((long)(new Date().getTime() / 1000));
		
		// 8. 상태 보고 주기 설정.
		gwInitRes.setHealthPeriod(MpiSetting.getMpiHealthPeriod(domainId));
		
		// 9. 게이트웨이 초기화 응답 전송 
		this.mpiSendService.respondGatewayInit(domainId, gwNm, gwInitRes);
	}
	
	/**
	 * 게이트웨이 초기화 응답에 대한 처리.
	 * 
	 * @param domain
	 * @param gwNm
	 */
	public void respondGatewayBoot(Domain domain, String gwNm) {
		Long domainId = domain.getId();
		
		// 1. 게이트웨이 조회 
		Gateway gateway = this.queryManager.selectByCondition(true, Gateway.class, ValueUtil.newMap("domainId,gwNm", domainId, gwNm));

		// 2. Gateway 초기화 설정 정보 가져오기.
		GatewayInitResponse gwInitRes = new GatewayInitResponse();
		gwInitRes.setGwConf(this.newGatewayInitConfig(gateway));
		
		// 3. Gateway 소속 MPI List를 설정
		List<GatewayInitResIndList> indList = gateway.mpiListForGwInit();
		gwInitRes.setIndList(indList);
		
		// 4. Gateway가 관리하는 인디케이터 리스트 및 각각의 Indicator 별 설정 정보 가져오기.
		String jobType = null;
		if(ValueUtil.isNotEmpty(indList)) {
			GatewayInitResIndList girl = indList.get(0);
			jobType = girl.getBizType();
		}
		
		// TODO 이 부분을 자연스럽게 수정 필요
		/*if(jobType == null) {
			jobType = MpsConstants.JOB_TYPE_DAS;
		} else if(MpsConstants.isDps2JobType(jobType)) {
			jobType = MpsConstants.JOB_TYPE_DAS2;
		} else if(MpsConstants.isRtn3JobType(jobType)) {
			jobType = MpsConstants.JOB_TYPE_RTN;
		}*/
		
		gwInitRes.setIndConf(MpiSetting.getGatewayBootConfig(domainId, jobType));
		
		// 5. Gateway 최신버전 정보 설정.
		String latestGatewayVer = MpiSetting.getGatewayLatestReleaseVersion(domainId);
		gwInitRes.setGwVersion(latestGatewayVer);
		
		// 6. Indicator 최신버전 정보 설정.
		String latestMpiVer = MpiSetting.getMpiLatestReleaseVersion(domainId);
		gwInitRes.setIndVersion(latestMpiVer);

		// 7. 현재 시간 설정 - 밀리세컨드 제외
		gwInitRes.setSvrTime((long)(new Date().getTime() / 1000));
		
		// 8. 상태 보고 주기 설정.
		gwInitRes.setHealthPeriod(MpiSetting.getMpiHealthPeriod(domainId));
		
		// 9. 게이트웨이 초기화 응답 전송 
		this.mpiSendService.respondGatewayInit(domainId, gwNm, gwInitRes);
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
		Gateway gateway = Gateway.findByName(domain.getId(), gwNm);
		
		if(gateway != null) {
			if (ValueUtil.isNotEqual(gateway.getVersion(), version)) {
				// Gateway Version 정보 업데이트
				gateway.setVersion(version);
				this.queryManager.update(gateway, OrmConstants.ENTITY_FIELD_VERSION);
			}
			
			this.mpisOnByGateway(domain, gateway);
		}
	}
	
	/**
	 * 게이트웨이 별로 이전 작업 (표시기) 상황을 다시 조회해서 재점등
	 * 
	 * @param domain
	 * @param gateway
	 */
	public void mpisOnByGateway(Domain domain, Gateway gateway) {
		// 1. Gateway 정보로 호기 리스트 추출
		Long domainId = domain.getId();
		String sql = "select distinct(region_cd) as region_cd from tb_location where domain_id = :domainId and mpi_cd in (select mpi_cd from tb_mpi where domain_id = :domainId and gw_cd = :gwCd) order by region_cd";
		List<String> regionCdList = this.queryManager.selectListBySql(sql, ValueUtil.newMap("domainId,gwCd", domain.getId(), gateway.getGwCd()), String.class, 0, 0);
		
		// 2. 호기로 부터 현재 작업 중인 배치 추출 
		for(String regionCd : regionCdList) {
			// 2-1. 호기 체크
			Region region = Region.findByRegionCd(domainId, regionCd, false);
			if(region == null || ValueUtil.isEmpty(region.getBatchId())) {
				continue;
			}
			
			// 2-2. TODO 이벤트 전달하여 해당 모듈 (DAS, DPS, 반품 등)에서 처리하도록 수정 필요
			
//			// 2-2. 작업 배치 및 상태 체크
//			JobBatch batch = JobBatch.find(domainId, region.getBatchId(), false);
//			
//			if(batch == null || ValueUtil.isNotEqual(batch.getStatus(), JobBatch.STATUS_RUNNING)) {
//				continue;
//			}
//			
//			// 2-3. 호기 코드, 게이트웨이 코드로 표시기 이전 상태 복원
//			this.getAssortService(batch).restoreMpiOn(batch, gateway);
		}
	}

	/**
	 * Indicator 초기화 리포트에 대한 처리. (Indicator Version 정보 Update)
	 * 
	 * @param domain
	 * @param mpiCd
	 * @param version
	 */
	public void handleMpiInitReport(Domain domain, String mpiCd, String version) {
		MPI mpi = MPI.findByCd(domain.getId(), mpiCd);
		
		if (mpi != null && ValueUtil.isNotEqual(mpi.getVersion(), version)) {
			mpi.setVersion(version);
			this.queryManager.update(mpi, OrmConstants.ENTITY_FIELD_VERSION);
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
