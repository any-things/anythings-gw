package xyz.anythings.gw.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.anythings.sys.entity.ScopeSetting;
import xyz.anythings.sys.rest.ScopeSettingController;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.util.ValueUtil;

/**
 * Indicator 세팅을 위한 컨트롤러
 * 
 * @author shortstop
 */
@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/mpi_setting")
@ServiceDesc(description="MPI Setting API")
public class MpiSettingController extends AbstractRestService {
	
	/**
	 * 쿼리 매니저
	 */
	@Autowired
	private IQueryManager queryManager;
	/**
	 * 범위 설정 컨트롤러
	 */
	@Autowired
	private ScopeSettingController scopeSettingCtrl;
	
	/**
	 * 범위 설정 유형 - MPI
	 */
	private static final String MPI_SCOPE_TYPE = "Indicator";
	
	@Override
	protected Class<?> entityClass() {
		return ScopeSetting.class;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search mpi settings by company code")
	public List<ScopeSetting> searchMpiSettings(@RequestParam(name = "job_type", required = false) String jobType) {
		
		// ScopeSetting에서 ScopeType이 Indicator인 모든 설정을 조회
		Query condition = AnyOrmUtil.newConditionForExecution(Domain.currentDomainId());
		condition.addFilter("scopeType", MPI_SCOPE_TYPE);
		jobType = ValueUtil.isEmpty(jobType) ? ScopeSetting.DEFAULT_SCOPE_NAME : jobType;
		condition.addFilter("scopeName", jobType);
		return this.queryManager.selectList(ScopeSetting.class, condition);
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update multiple")
	public List<ScopeSetting> multipleUpdate(@RequestBody List<ScopeSetting> list) {
		
		if(ValueUtil.isEmpty(list)) {
			return null;
		}
		
		for(ScopeSetting setting : list) {
			setting.setDomainId(Domain.currentDomainId());
			setting.setScopeType(MPI_SCOPE_TYPE);
			setting.setScopeName(ScopeSetting.DEFAULT_SCOPE_NAME);			
		}
		
		this.cudMultipleData(ScopeSetting.class, list);
		
		// Cache Clear
		this.scopeSettingCtrl.clearCache();
		return this.searchMpiSettings(null);
	}

}
