/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.anythings.gw.web.initializer;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import xyz.anythings.base.service.impl.ConfigSetService;
import xyz.anythings.gw.config.ModuleProperties;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.config.ModuleConfigSet;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.api.IEntityFieldCache;
import xyz.elidom.sys.system.service.api.IServiceFinder;

/**
 * 게이트웨이 모듈 Startup시 Framework 초기화 클래스
 * 
 * @author shortstop
 */
@Component
public class AnythingsGwInitializer {

	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(AnythingsGwInitializer.class);

	@Autowired
	@Qualifier("rest")
	private IServiceFinder restFinder;

	@Autowired
	private IEntityFieldCache entityFieldCache;

	@Autowired
	private ModuleProperties module;

	@Autowired
	private ModuleConfigSet configSet;
	
	@Autowired
	private IQueryManager queryManager;
	
	@Autowired
	private ConfigSetService configSetSvc;
	
	@EventListener({ ContextRefreshedEvent.class })
	public void refresh(ContextRefreshedEvent event) {
		this.logger.info("Anythings Gw module initializing ready...");
	}

	@EventListener({ ApplicationReadyEvent.class })
	void ready(ApplicationReadyEvent event) {
		this.logger.info("Anythings Gw module initializing started...");
		
		this.configSet.addConfig(this.module.getName(), this.module);
		this.configSet.setApplicationModule(this.module.getName());
		this.scanServices();
		this.initStageConfigProfiles();
		
		this.logger.info("Anythings Gw initializing finished");
	}

	/**
	 * 모듈 서비스 스캔
	 */
	private void scanServices() {
		this.entityFieldCache.scanEntityFieldsByBasePackage(this.module.getBasePackage());
		this.restFinder.scanServicesByPackage(this.module.getName(), this.module.getBasePackage());
	}
	
	/**
	 * 스테이지 설정 프로파일 초기화
	 */
	private void initStageConfigProfiles() {
		String sql = "select id from domains";
		List<Domain> domainList = this.queryManager.selectListBySql(sql, new HashMap<String, Object>(1), Domain.class, 0, 0);
		
		for(Domain domain : domainList) {
			this.configSetSvc.buildStageIndConfigSet(domain.getId());
		}
	}

}