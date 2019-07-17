package xyz.anythings.gw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.gw.model.IMessageBody;
import xyz.anythings.gw.model.MessageObject;
import xyz.anythings.gw.service.util.MwMessageUtil;
import xyz.elidom.rabbitmq.client.SystemClient;
import xyz.elidom.rabbitmq.message.MessageProperties;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.rest.DomainController;
import xyz.elidom.util.ValueUtil;

/**
 * 미들웨어로 메시지 전송하는 Sender
 * 
 * @author shortstop
 */
@Component
public class MwSender extends MwCommon {
	/**
	 * 미들웨어 시스템 클라이언트
	 */
	@Autowired
	private SystemClient mwSystemClient;
	/**
	 * 도메인 컨트롤러
	 */
	@Autowired
	private DomainController domainCtrl;
	
	private Domain domain; 
	
	/**
	 * 도메인 별 미들웨어 가상 호스트 코드를 조회
	 * 
	 * @param domainId
	 * @return
	 */
	public String getVirtualHost(Long domainId) {
		domain = this.domainCtrl.findOne(domainId, null);
		return domain.getMwSiteCd();
	}	
	
	/**
	 * 미들웨어를 통해 메시지 전송 목적지 msgDestId로 메시지 msgBody 내용을 전송 요청한다. 
	 * 
	 * @param virtualHost 각 사이트 (도메인) 별로 도메인 별로 가상 호스트 코드가 결정된다.
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendRequest(String virtualHost, String msgDestId, IMessageBody msgBody) {
		this.send(virtualHost, MwMessageUtil.newMessageProp(msgDestId, false), msgBody);
	}
	
	/**
	 * 미들웨어를 통해 메시지 전송 목적지 msgDestId로 메시지 msgBody 내용을 전송요청한다.
	 * 
	 * @param domainId
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendRequest(Long domainId, String msgDestId, IMessageBody msgBody) {
		String virtualHost = this.getVirtualHost(domainId);
		this.sendRequest(virtualHost, msgDestId, msgBody);
	}
	
	/**
	 * 미들웨어를 통해 메시지 전송 목적지 msgDestId로 메시지 msgBody 내용을 전송요청한다. 
	 * 
	 * @param virtualHost 각 사이트 (도메인) 별로 도메인 별로 가상 호스트 코드가 결정된다.
	 * @param msgId
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendRequest(String virtualHost, String msgId, String msgDestId, IMessageBody msgBody) {
		this.send(virtualHost, MwMessageUtil.newMessageProp(msgId, msgDestId, false), msgBody);
	}
	
	/**
	 * 미들웨어를 통해 메시지 전송 목적지 msgDestId로 메시지 msgBody 내용을 전송요청한다.
	 * 
	 * @param domainId
	 * @param msgId
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendRequest(Long domainId, String msgId, String msgDestId, IMessageBody msgBody) {
		String virtualHost = this.getVirtualHost(domainId);
		this.sendRequest(virtualHost, msgId, msgDestId, msgBody);
	}

	/**
	 * 미들웨어를 통해 메시지 msgBody 내용으로 메시지 전송 목적지 msgDestId에 응답을 요청한다. 
	 * 
	 * @param virtualHost
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendResponse(String virtualHost, String msgDestId, IMessageBody msgBody) {
		this.send(virtualHost, MwMessageUtil.newMessageProp(msgDestId, true), msgBody);
	}
	
	/**
	 * 미들웨어를 통해 메시지 msgBody 내용으로 메시지 전송 목적지 msgDestId에 응답을 요청한다.
	 * 
	 * @param domainId
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendResponse(Long domainId, String msgDestId, IMessageBody msgBody) {
		String virtualHost = this.getVirtualHost(domainId);
		this.sendResponse(virtualHost, msgDestId, msgBody);
	}
	
	/**
	 * 미들웨어를 통해 ID가 msgId인 메시지를 msgBody 내용으로 메시지 전송 목적지 msgDestId에 응답을 요청한다.
	 * 
	 * @param virtualHost
	 * @param msgId
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendResponse(String virtualHost, String msgId, String msgDestId, IMessageBody msgBody) {
		this.send(virtualHost, MwMessageUtil.newMessageProp(msgId, msgDestId, true), msgBody);
	}
	
	/**
	 * 미들웨어를 통해 ID가 msgId인 메시지를 msgBody 내용으로 메시지 전송 목적지 msgDestId에 응답을 요청한다.
	 * 
	 * @param domainId
	 * @param msgId
	 * @param msgDestId
	 * @param msgBody
	 */
	public void sendResponse(Long domainId, String msgId, String msgDestId, IMessageBody msgBody) {
		String virtualHost = this.getVirtualHost(domainId);
		this.sendResponse(virtualHost, msgId, msgDestId, msgBody);
	}

	/**
	 * 미들웨어를 통해 프로퍼티가 property이고 본문이 msgBody인 메시지를 전송한다.
	 * 
	 * @param virtualHost
	 * @param msgProp
	 * @param msgBody
	 */
	public void send(String virtualHost, MessageProperties msgProp, IMessageBody msgBody) {
		MessageObject message = new MessageObject();
		message.setProperties(msgProp);
		
		if (ValueUtil.isNotEmpty(msgBody)) {
			message.setBody(msgBody);
		}

		String value = MwMessageUtil.messageObjectToJson(message);
		this.mwSystemClient.sendMessage(virtualHost, msgProp.getDestId(), value);
	}
	
	/**
	 * 미들웨어를 통해 프로퍼티가 property이고 본문이 msgBody인 메시지를 전송한다.
	 * 
	 * @param domainId
	 * @param msgProp
	 * @param msgBody
	 */
	public void send(Long domainId, MessageProperties msgProp, IMessageBody msgBody) {
		String vHost = this.getVirtualHost(domainId);
		this.send(vHost, msgProp, msgBody);
	}
}
