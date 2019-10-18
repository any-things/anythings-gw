package xyz.anythings.gw.service.util;

import java.util.List;

import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.util.LogisEntityUtil;
import xyz.anythings.gw.MwConstants;
import xyz.anythings.gw.query.store.GwQueryStore;
import xyz.anythings.gw.service.model.IndOffReq;
import xyz.elidom.util.BeanUtil;

/**
 * 게이트웨이 쿼리 유틸리티
 * 
 * @author shortstop
 */
public class GwQueryUtil {

	/**
	 * 게이트웨이 표시기 코드 리스트 리턴
	 * 
	 * @param gw
	 * @return
	 */
	public static List<String> searchIndCdList(Gateway gw) {
		return searchIndCdList(gw.getDomainId(), gw.getGwNm());
	}
	
	/**
	 * 게이트웨이 표시기 코드 리스트 리턴
	 * 
	 * @param domainId
	 * @param gwNm
	 * @return
	 */
	public static List<String> searchIndCdList(Long domainId, String gwNm) {
		String sql = BeanUtil.get(GwQueryStore.class).getIndCdListQuery();
		return LogisEntityUtil.searchItems(domainId, false, String.class, sql, "domainId,gwNm", gwNm);
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
	 * 호기 및 장비 작업 존 별 게이트웨이 Path 정보 조회
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
	 * 호기 및 호기 작업 존 별 게이트웨이 Path 리스트 조회 
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
