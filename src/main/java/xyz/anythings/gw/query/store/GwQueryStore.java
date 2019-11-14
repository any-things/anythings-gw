package xyz.anythings.gw.query.store;

import org.springframework.stereotype.Component;

import xyz.anythings.sys.service.AbstractQueryStore;
import xyz.elidom.sys.SysConstants;

/**
 * 게이트웨이 쿼리 스토어
 * 
 * @author shortstop
 */
@Component
public class GwQueryStore extends AbstractQueryStore {

	@Override
	public void initQueryStore(String databaseType) {
		this.databaseType = databaseType;
		this.basePath = "xyz/anythings/gw/query/" + this.databaseType + SysConstants.SLASH;
		this.defaultBasePath = "xyz/anythings/gw/query/ansi/"; 
	}
	
	/**
	 * 게이트웨이 표시기 코드 리스트 쿼리
	 * 
	 * @return
	 */
	public String getIndCdListQuery() {
		return this.getQueryByPath("gw/GatewayIndCdList");
	}
	
	/**
	 * 표시기 코드 리스트 조회 쿼리
	 * 
	 * @return
	 */
	public String getSearchIndicatorsQuery() {
		return this.getQueryByPath("gw/SearchIndicators");
	}

	/**
	 * 게이트웨이 소속 표시기를 사용하는 모든 설비 리스트를 조회
	 *  
	 * @return
	 */
	public String getEquipListByGateway() {
		return this.getQueryByPath("gw/EquipListByGateway");
	}

	/**
	 * 라우터 리부팅을 위한 표시기 리스트를 조회
	 *  
	 * @return
	 */
	public String getSearchIndListForGwInitQuery() {
		return this.getQueryByPath("gw/SearchIndListForGwInit");
	}

}
