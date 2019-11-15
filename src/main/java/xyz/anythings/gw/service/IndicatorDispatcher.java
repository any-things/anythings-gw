package xyz.anythings.gw.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.gw.entity.IndConfigSet;
import xyz.anythings.gw.service.api.IIndicatorRequestService;
import xyz.anythings.gw.service.impl.IndConfigProfileService;

/**
 * 인디케이터 벤더별 서비스 디스패처
 * 
 * @author shortstop
 */
@Component
public class IndicatorDispatcher implements BeanFactoryAware {

	/**
	 * BeanFactory
	 */
	protected BeanFactory beanFactory;
	/**
	 * 표시기 설정 프로파일 서비스
	 */
	@Autowired
	private IndConfigProfileService indConfigSetService;
	/**
	 * 표시기 점, 소등 요청 서비스 컴포넌트 기본 명 
	 */
	private String indicatorRequestServiceName = "IndicatorRequestService";

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * 표시기 타입으로 표시기 점,소등 요청 서비스 찾아 리턴
	 * 
	 * @param indType
	 * @return
	 */
	public IIndicatorRequestService getIndicatorRequestService(String indType) {
		String indReqSvcName = indType.toLowerCase() + this.indicatorRequestServiceName;
		return (IIndicatorRequestService)this.beanFactory.getBean(indReqSvcName);
	}
	
	/**
	 * 작업 배치 ID로 표시기 점,소등 요청 서비스 찾아 리턴
	 * 
	 * @param batchId
	 * @return
	 */
	public IIndicatorRequestService getIndicatorRequestServiceByBatch(String batchId) {
		IndConfigSet configSet = this.indConfigSetService.getConfigSet(batchId);
		return this.getIndicatorRequestService(configSet.getIndType());
	}
	
	/**
	 * 스테이지 코드로 표시기 점,소등 요청 서비스 찾아 리턴
	 * 
	 * @param stageCd
	 * @return
	 */
	public IIndicatorRequestService getIndicatorRequestServiceByStage(Long domainId, String stageCd) {
		IndConfigSet configSet = this.indConfigSetService.getStageConfigSet(domainId, stageCd);
		return this.getIndicatorRequestService(configSet.getIndType());
	}

}
