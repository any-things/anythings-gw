package xyz.anythings.gw.service.util;

import java.util.List;

import xyz.anythings.base.LogisConstants;
import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.util.LogisEntityUtil;
import xyz.anythings.gw.MwConstants;
import xyz.anythings.gw.model.GatewayInitResIndList;
import xyz.anythings.gw.query.store.GwQueryStore;
import xyz.anythings.gw.service.model.IndOffReq;
import xyz.elidom.util.BeanUtil;

/**
 * 라우터 쿼리 유틸리티
 * 
 * @author shortstop
 */
public class GwQueryUtil {

	/**
	 * 라우터 표시기 코드 리스트 리턴
	 * 
	 * @param gw
	 * @return
	 */
	public static List<String> searchIndCdList(Gateway gw) {
		return searchIndCdList(gw.getDomainId(), gw.getGwNm(), null, null);
	}
	
	/**
	 * 라우터 및 설비 소속 표시기 코드 리스트 리턴
	 * 
	 * @param gw
	 * @param equipType
	 * @param equipCd
	 * @return
	 */
	public static List<String> searchIndCdList(Gateway gw, String equipType, String equipCd) {
		return searchIndCdList(gw.getDomainId(), gw.getGwNm(), equipType, equipCd);
	}
	
	/**
	 * 설비 소속 표시기 코드 리스트 리턴
	 * 
	 * @param domainId
	 * @param equipType
	 * @param equipCd
	 * @return
	 */
	public static List<String> searchIndCdList(Long domainId, String equipType, String equipCd) {
		return searchIndCdList(domainId, null, equipType, equipCd);
	}
	
	/**
	 * 라우터 표시기 코드 리스트 리턴
	 * 
	 * @param domainId
	 * @param gwNm
	 * @return
	 */
	public static List<String> searchIndCdList(Long domainId, String gwNm) {
		return searchIndCdList(domainId, gwNm, null, null);
	}
	
	/**
	 * 라우터 및 설비 소속 표시기 코드 리스트 리턴
	 * 
	 * @param domainId
	 * @param gwNm
	 * @param equipType
	 * @param equipCd
	 * @return
	 */
	public static List<String> searchIndCdList(Long domainId, String gwNm, String equipType, String equipCd) {
		String sql = BeanUtil.get(GwQueryStore.class).getIndCdListQuery();
		return LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,gwNm,equipType,equipCd", domainId, gwNm, equipType, equipCd);
	}
	
	/**
	 * 라우터 및 설비 소속 표시기 코드 리스트 리턴
	 * 
	 * @param domainId
	 * @param gwNm
	 * @param equipType
	 * @param equipCd
	 * @param stationCd
	 * @return
	 */
	public static List<String> searchIndCdList(Long domainId, String gwNm, String equipType, String equipCd, String stationCd) {
		String sql = BeanUtil.get(GwQueryStore.class).getIndCdListQuery();
		return LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,gwNm,equipType,equipCd,stationCd", domainId, gwNm, equipType, equipCd, stationCd);
	}
	
	/**
	 * 라우터 및 설비 소속 표시기 코드 리스트 리턴
	 * 
	 * @param domainId
	 * @param gwNm
	 * @param equipType
	 * @param equipCd
	 * @param stationCd
	 * @param equipZone
	 * @return
	 */
	public static List<String> searchIndCdList(Long domainId, String gwNm, String equipType, String equipCd, String stationCd, String equipZone) {
		String sql = BeanUtil.get(GwQueryStore.class).getIndCdListQuery();
		return LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,gwNm,equipType,equipCd,stationCd,equipZone", domainId, gwNm, equipType, equipCd, stationCd, equipZone);
	}
	
	/**
	 * 게이트웨이 리부팅시에 게이트웨이에 내려주기 위한 게이트웨이 소속 표시기 정보 리스트
	 * 
	 * @param gateway
	 * @return
	 */
	public static List<GatewayInitResIndList> searchIndListForGwInit(Gateway gateway) {
		return searchIndListForGwInit(gateway.getDomainId(), gateway.getStageCd(), gateway.getGwNm());
	}
	
	/**
	 * 게이트웨이 리부팅시에 게이트웨이에 내려주기 위한 게이트웨이 소속 표시기 정보 리스트
	 * 
	 * @param domainId
	 * @param stageCd
	 * @param gwPath
	 * @return
	 */
	public static List<GatewayInitResIndList> searchIndListForGwInit(Long domainId, String stageCd, String gwPath) {
		String viewType = StageIndicatorSetting.getIndViewType(domainId, stageCd);
		String sql = BeanUtil.get(GwQueryStore.class).getSearchIndListForGwInitQuery();
		return LogisEntityUtil.searchItems(domainId, true, GatewayInitResIndList.class, sql, "domainId,gwNm,bizType,viewType", domainId, gwPath, LogisConstants.JOB_TYPE_DAS, viewType);
	}
	
	/**
	 * 게이트웨이 리부팅시에 게이트웨이에 내려주기 위한 게이트웨이 및 설비 소속 표시기 정보 리스트
	 * 
	 * @param gateway
	 * @param batch
	 * @return
	 */
	public static List<GatewayInitResIndList> searchIndListForGwInit(Gateway gateway, JobBatch batch) {
		Long domainId = gateway.getDomainId();
		String viewType = RuntimeIndicatorSetting.getIndViewType(batch);
		String sql = BeanUtil.get(GwQueryStore.class).getSearchIndListForGwInitQuery();
		return LogisEntityUtil.searchItems(domainId, true, GatewayInitResIndList.class, sql, "domainId,gwCd,bizType,viewType,equipType,equipCd", domainId, gateway.getGwCd(), batch.getJobType(), viewType, batch.getEquipType(), batch.getEquipCd());
	}

	/**
	 * Gateway Code로 Gateway Path 조회
	 * 
	 * @param domainId
	 * @param gwCd
	 * @return
	 */
	public static String findGatewayPathByGwCd(Long domainId, String gwCd) {
		Gateway gw = LogisEntityUtil.findEntityBy(domainId, true, Gateway.class, "gw_nm", "domainId,gwCd", domainId, gwCd);
		return gw.getGwNm();
	}
	
	/**
	 * Indicator Code로 Gateway Path 조회
	 * 
	 * @param domainId
	 * @param indCd
	 * @return
	 */
	public static String findGatewayPathByIndCd(Long domainId, String indCd) {
		String sql = "select gw_nm from gateways where domain_id = :domainId and gw_cd = (select gw_cd from indicators where domain_id = :domainId and ind_cd = :indCd)";
		return LogisEntityUtil.findItem(domainId, false, String.class, sql, "gw_nm", "domainId,indCd", domainId, indCd);
	}
	
	/**
	 * 호기 및 장비 작업 존 별 라우터 Path 정보 조회
	 * 
	 * @param domainId
	 * @param rackCd
	 * @param equipZoneCd
	 * @param sideCd
	 * @return
	 */
	public static List<String> searchGwByEquipZone(Long domainId, String rackCd, String equipZoneCd, String sideCd) {
		sideCd = MwConstants.checkSideCdForQuery(domainId, sideCd);
		String sql = BeanUtil.get(GwQueryStore.class).getSearchIndicatorsQuery();
		return LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,rackCd,equipZone,sideCd,activeFlag", domainId, rackCd, equipZoneCd, sideCd);
	}
	
	/**
	 * 호기 및 장비 존 코드 사이드 코드로 표시기 리스트를 조회  
	 * 
	 * @param domainId
	 * @param rackCd
	 * @param equipZoneCd
	 * @param sideCd
	 * @return
	 */
	public static List<IndOffReq> searchIndByEquipZone(Long domainId, String rackCd, String equipZoneCd, String sideCd) {
		sideCd = MwConstants.checkSideCdForQuery(domainId, sideCd);
		String sql = BeanUtil.get(GwQueryStore.class).getSearchIndicatorsQuery();
		return LogisEntityUtil.searchItems(domainId, false, IndOffReq.class, sql, "domainId,rackCd,equipZone,sideCd,activeFlag,indQueryFlag", domainId, rackCd, equipZoneCd, sideCd, true, true);
	}	
	
	/**
	 * 호기 및 호기 작업 존 별 라우터 Path 리스트 조회 
	 * 
	 * @param domainId
	 * @param rackCd
	 * @param stationCd
	 * @return
	 */
	public static List<String> searchGwByStation(Long domainId, String rackCd, String stationCd) {
		String sql = BeanUtil.get(GwQueryStore.class).getSearchIndicatorsQuery();
		return LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,rackCd,stationCd,activeFlag", domainId, rackCd, stationCd, true);
	}
	
	/**
	 * 호기 및 작업 존 코드로 표시기 리스트를 조회
	 * 
	 * @param domainId
	 * @param rackCd
	 * @param zoneCd
	 * @return
	 */
	public static List<IndOffReq> searchIndByStation(Long domainId, String rackCd, String stationCd) {
		String sql = BeanUtil.get(GwQueryStore.class).getSearchIndicatorsQuery();
		return LogisEntityUtil.searchItems(domainId, false, IndOffReq.class, sql, "domainId,rackCd,stationCd,activeFlag,indQueryFlag", domainId, rackCd, stationCd, true, true);
	}

}
