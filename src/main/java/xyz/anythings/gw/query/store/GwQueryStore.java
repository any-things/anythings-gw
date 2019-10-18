package xyz.anythings.gw.query.store;

import org.springframework.stereotype.Component;

import xyz.anythings.base.query.store.AbstractQueryStore;
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

}
