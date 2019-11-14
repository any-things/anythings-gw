package xyz.anythings.gw.service.api;

import xyz.anythings.gw.entity.IndConfigSet;

/**
 * 설정 프로파일 서비스 API 정의
 * 
 * @author shortstop
 */
public interface IIndConfigSetService {
		
	/**
	 * 스테이지 범위 내 표시기 설정 프로파일 초기화
	 * 
	 * @param domainId
	 * @return
	 */
	public int buildStageIndConfigSet(Long domainId);
	
	/**
	 * 스테이지 디폴트 표시기 설정 프로파일 초기화
	 * 
	 * @param configSet
	 * @return
	 */
	public IndConfigSet buildStageIndConfigSet(IndConfigSet configSet);
	
	/**
	 * templateConfigSetId로 표시기 설정 프로파일 복사
	 * 
	 * @param domainId
	 * @param templateConfigSetId
	 * @param targetSetCd
	 * @param targetSetNm
	 * @return
	 */
	public IndConfigSet copyIndConfigSet(Long domainId, String templateConfigSetId, String targetSetCd, String targetSetNm);
	
	/**
	 * 작업 배치 정보로 표시기 설정 프로파일 생성
	 * 
	 * @param batchId
	 * @param configSet
	 * @return
	 */
	public IndConfigSet buildIndConfigSet(String batchId, IndConfigSet configSet);
	
	/**
	 * 작업 배치와 설정 키로 표시기 설정 값 조회
	 * 
	 * @param batchId
	 * @param key
	 * @return
	 */
	public String getIndConfigValue(String batchId, String key);
	
	/**
	 * 작업 배치와 설정 키로 표시기 설정 값 조회
	 * 
	 * @param batchId
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getIndConfigValue(String batchId, String key, String defaultValue);
	
	/**
	 * 작업 배치 정보로 표시기 설정 프로파일 리셋 (캐쉬 리셋)
	 * 
	 * @param batchId
	 */
	public void clearIndConfigSet(String batchId);

}
