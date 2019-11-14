package xyz.anythings.gw.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import xyz.anythings.gw.entity.IndConfig;
import xyz.anythings.gw.entity.IndConfigSet;
import xyz.anythings.gw.service.api.IIndConfigSetService;
import xyz.anythings.sys.service.AbstractExecutionService;
import xyz.anythings.sys.util.AnyEntityUtil;
import xyz.anythings.sys.util.AnyValueUtil;
import xyz.elidom.util.ValueUtil;

/**
 * IConfigSetService 구현
 * 
 * @author shortstop
 */
@Component
public class IndConfigSetService extends AbstractExecutionService implements IIndConfigSetService {
	
	/**
	 * 표시기 프로파일 셋 Copy Fields
	 */
	private static final String[] IND_CONFIG_SET_COPY_FIELDS = new String[] { "stageCd", "indType", "jobType", "equipType", "equipCd", "comCd", "confSetCd", "confSetNm", "remark" };
	/**
	 * ConfigSet 
	 */
	private static final String[] CONFIG_COPY_FIELDS = new String[] { "category", "name", "description", "value", "remark", "config" };
	
	/**
	 * 배치 ID - 표시기 설정 셋
	 */
	private Map<String, IndConfigSet> batchIndConfigSet = new HashMap<String, IndConfigSet>();
	
	@Override
	public int buildStageIndConfigSet(Long domainId) {
		String sql = "select id,domain_id,conf_set_cd,conf_set_nm,stage_cd,ind_type from ind_config_set where domain_id = :domainId and default_flag = :defaultFlag and stage_cd is not null and equip_type is null and equip_cd is null and job_type is null and com_cd is null";
		List<IndConfigSet> confSetList = AnyEntityUtil.searchItems(domainId, false, IndConfigSet.class, sql, "domainId,defaultFlag", domainId, true);
		
		if(ValueUtil.isNotEmpty(confSetList)) {
			for(IndConfigSet confSet : confSetList) {
				this.buildStageIndConfigSet(confSet);
			}
		}
		
		return confSetList.size();
	}

	@Override
	public IndConfigSet buildStageIndConfigSet(IndConfigSet configSet) {
		List<IndConfig> items = AnyEntityUtil.searchDetails(configSet.getDomainId(), IndConfig.class, "indConfigSetId", configSet.getId());
		configSet.setItems(items);
		this.batchIndConfigSet.put(configSet.getStageCd(), configSet);
		return configSet;
	}
	
	@Override
	public IndConfigSet copyIndConfigSet(Long domainId, String templateConfigSetId, String targetSetCd, String targetSetNm) {
		// 1. templateConfigSetId로 템플릿 설정을 조회 
		IndConfigSet sourceSet = AnyEntityUtil.findEntityById(true, IndConfigSet.class, templateConfigSetId);
		IndConfigSet targetSet = AnyValueUtil.populate(sourceSet, new IndConfigSet(), IND_CONFIG_SET_COPY_FIELDS);
		targetSet.setConfSetCd(targetSetCd);
		targetSet.setConfSetNm(targetSetNm);
		this.queryManager.insert(targetSet);
		// 2. 템플릿 설정 생성
		this.cloneIndConfigItems(sourceSet, targetSet);
		// 3. 복사한 JobConfigSet 리턴
		return targetSet;
	}

	@Override
	public IndConfigSet buildIndConfigSet(String batchId, IndConfigSet configSet) {
		/*if(ValueUtil.isNotEmpty(batch.getIndConfigSetId())) {
			IndConfigSet configSet = AnyEntityUtil.findEntityById(true, IndConfigSet.class, batch.getIndConfigSetId());
			List<IndConfig> sourceItems = AnyEntityUtil.searchDetails(configSet.getDomainId(), IndConfig.class, "indConfigSetId", configSet.getId());
			configSet.setItems(sourceItems);
			this.batchIndConfigSet.put(batch.getId(), configSet);
			return configSet;
		} else {
			throw new ElidomRuntimeException("작업 배치 [" + batch.getId() + "]에 표시기 설정 프로파일이 설정되지 않았습니다.");
		}*/
		return null;
	}

	/*@Override
	public String getIndConfigValue(JobBatch batch, String key) {
		IndConfigSet configSet = this.batchIndConfigSet.get(batch.getId());
		
		if(configSet == null) {
			configSet = AnyEntityUtil.findEntityById(true, IndConfigSet.class, batch.getIndConfigSetId());
			configSet.setItems(AnyEntityUtil.searchDetails(batch.getDomainId(), IndConfig.class, "indConfigSetId", batch.getIndConfigSetId()));
			this.batchIndConfigSet.put(batch.getId(), configSet);
		}
		
		return configSet == null ? null : configSet.findValue(key);
	}*/
	
	@Override
	public String getIndConfigValue(String batchId, String key) {
		IndConfigSet configSet = this.batchIndConfigSet.get(batchId);
		
		/*if(configSet == null) {
			JobBatch batch = AnyEntityUtil.findEntityBy(Domain.currentDomainId(), true, false, JobBatch.class, "id,indConfigSetId", "id", batchId);
			configSet = AnyEntityUtil.findEntityById(true, IndConfigSet.class, batch.getIndConfigSetId());
			configSet.setItems(AnyEntityUtil.searchDetails(batch.getDomainId(), IndConfig.class, "indConfigSetId", batch.getIndConfigSetId()));
			this.batchIndConfigSet.put(batchId, configSet);
		}*/
		
		return configSet == null ? null : configSet.findValue(key);
	}

	/*@Override
	public String getIndConfigValue(JobBatch batch, String key, String defaultValue) {
		String value = this.getIndConfigValue(batch, key);
		return ValueUtil.isEmpty(value) ? defaultValue : value;
	}*/

	@Override
	public String getIndConfigValue(String batchId, String key, String defaultValue) {
		String value = this.getIndConfigValue(batchId, key);
		return ValueUtil.isEmpty(value) ? defaultValue : value;
	}

	@Override
	public void clearIndConfigSet(String batchId) {
		this.batchIndConfigSet.remove(batchId);
	}
	
	/**
	 * sourceSet의 설정 항목을 targetSet의 설정 항목으로 복사
	 * 
	 * @param sourceSet
	 * @param targetSet
	 */
	protected void cloneIndConfigItems(IndConfigSet sourceSet, IndConfigSet targetSet) {
		List<IndConfig> sourceItems = AnyEntityUtil.searchDetails(sourceSet.getDomainId(), IndConfig.class, "indConfigSetId", sourceSet.getId());
		
		if(ValueUtil.isNotEmpty(sourceItems)) {
			List<IndConfig> targetItems = new ArrayList<IndConfig>(sourceItems.size());
						
			for(IndConfig sourceItem : sourceItems) {
				IndConfig targetItem = AnyValueUtil.populate(sourceItem, new IndConfig(), CONFIG_COPY_FIELDS);
				targetItem.setIndConfigSetId(targetSet.getId());
				targetItems.add(targetItem);
			}
			
			this.queryManager.insertBatch(targetItems);
			targetSet.setItems(targetItems);
		}
	}

}
