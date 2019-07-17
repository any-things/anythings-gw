package xyz.anythings.gw.rest;

import java.util.ArrayList;
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

import xyz.anythings.sys.entity.CompanySetting;
import xyz.anythings.sys.rest.CompanySettingController;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.entity.Setting;
import xyz.elidom.sys.rest.SettingController;
import xyz.elidom.util.ValueUtil;

/**
 * MPI 세팅을 위한 컨트롤러
 * 
 * @author shortstop
 */
@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/mpi_setting")
@ServiceDesc(description="MPI Setting API")
public class MpiSettingController {
	
	/**
	 * 쿼리 매니저
	 */
	@Autowired
	private IQueryManager queryManager;
	/**
	 * 설정 컨트롤러 
	 */
	@Autowired
	private SettingController settingCtrl;
	/**
	 * 고객사 별 설정 컨트롤러
	 */
	@Autowired
	private CompanySettingController companySettingCtrl;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search mpi settings by company code")
	public List<Setting> searchMpiSettings(@RequestParam(name = "com_cd", required = false) String comCd) {
		// 1. 고객사 값 체크
		Long domainId = Domain.currentDomainId();
		comCd = ValueUtil.isEmpty(comCd) ? CompanySetting.DEFAULT_COMPANY_CODE : comCd;
		
		// 2. Setting에서 mps.mpi로 시작하는 모든 설정을 조회 
		Query condition = AnyOrmUtil.newConditionForExecution(domainId);
		condition.addFilter(new Filter("name", "like", "mps.mpi"));
		List<Setting> settings = this.queryManager.selectList(Setting.class, condition);
		
		// 3. CompanySetting에서 comCd로 조회, comCd가 없다면 디폴트 컴퍼니값으로 조회
		condition = AnyOrmUtil.newConditionForExecution(domainId);
		condition.addFilter(new Filter("comCd", comCd));	
		List<CompanySetting> comSettings = this.queryManager.selectList(CompanySetting.class, condition);
		
		if(settings == null) {
			settings = new ArrayList<Setting>();
		}
		
		// 4. settings 정보가 없다면 CompanySetting 정보를 리턴 
		if(ValueUtil.isNotEmpty(comSettings)) {
			for(CompanySetting comSetting : comSettings) {
				settings.add(ValueUtil.populate(comSetting, new Setting()));
			}
		}
		
		// 5. 리턴
		return settings;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update at one time")
	public List<Setting> multipleUpdate(@RequestParam(name = "com_cd", required = false) String comCd, @RequestBody List<Setting> list) {
		// 고객사 코드 체크 
		comCd = ValueUtil.isEmpty(comCd) ? CompanySetting.DEFAULT_COMPANY_CODE : comCd;
		List<Setting> settings = new ArrayList<Setting>();
		List<CompanySetting> comSettings = new ArrayList<CompanySetting>();
		
		for(Setting setting : list) {
			String name = setting.getName();
			
			// mps.mpi로 시작되면 설정값 
			if(name.startsWith("mps.mpi")) {
				settings.add(setting);
				
			// com.mpi로 시작되면 고객사 설정값
			} else {
				CompanySetting comSetting = new CompanySetting();
				comSetting.setComCd(comCd);
				comSettings.add(ValueUtil.populate(setting, comSetting));
			}
		}
		
		if(ValueUtil.isNotEmpty(settings)) {
			this.queryManager.upsertBatch(settings);
		}
		
		if(ValueUtil.isNotEmpty(comSettings)) {
			this.queryManager.upsertBatch(comSettings);
		}
		
		// Cache Clear
		this.settingCtrl.clearCache();
		this.companySettingCtrl.clearCache();
		
		return this.searchMpiSettings(comCd);
	}

}
