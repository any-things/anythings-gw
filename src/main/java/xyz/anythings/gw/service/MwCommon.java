package xyz.anythings.gw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.model.MessageObject;
import xyz.anythings.gw.service.util.MwMessageUtil;
import xyz.anythings.sys.AnyConstants;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.sys.util.ValueUtil;

/**
 * 미들웨어 공통 서비스
 * 
 * @author shortstop
 */
public class MwCommon {

	/**
	 * Logger
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * Query Manager
	 */
	@Autowired
	protected IQueryManager queryManager;
		
	/**
	 * 메시지 로깅이 활성화 되어 있는지 도메인 별로 체크
	 * 
	 * @param domainId
	 * @return
	 */
	protected boolean isMessageLoggingEnabled(Long domainId) {
		return 	ValueUtil.toBoolean(SettingUtil.getValue(domainId, MwConfigConstants.MW_LOG_RCV_MSG_ENABLED, AnyConstants.FALSE_STRING));
	}
	
	/**
	 * 메시지 저장
	 * 
	 * @param domainId
	 * @param type
	 * @param message
	 */
	protected void logInfoMessage(Long domainId, MessageObject message) {
		if(this.isMessageLoggingEnabled(domainId)) {
			this.logger.info(MwMessageUtil.messageObjectToJson(message));
		}
	}
}
