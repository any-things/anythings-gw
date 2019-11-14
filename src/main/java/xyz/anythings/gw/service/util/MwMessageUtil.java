package xyz.anythings.gw.service.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.model.MessageObject;
import xyz.anythings.gw.service.model.IndOnPickReq;
import xyz.anythings.gw.service.model.IndOnStockReq;
import xyz.elidom.exception.server.ElidomServiceException;
import xyz.elidom.rabbitmq.client.event.SystemMessageReceiveEvent;
import xyz.elidom.rabbitmq.message.MessageProperties;
import xyz.elidom.sys.util.EnvUtil;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.util.ValueUtil;

/**
 * 미들웨어 통신을 위한 메시지 생성 유틸리티 
 * 
 * @author shortstop
 */
public class MwMessageUtil {
	
	/**
	 * 시스템 메시지 큐 
	 */
	private static String SYSTEM_QUEUE = null;
	
	/**
	 * 미들웨어 시스템 큐
	 * 
	 * @return
	 */
	public static String getMwSystemQueue() {
		if(SYSTEM_QUEUE == null) {
			SYSTEM_QUEUE = EnvUtil.getValue(MwConfigConstants.MW_SYSTEM_QUEUE_NAME);
		}
		
		if(SYSTEM_QUEUE == null) {
			throw ThrowUtil.newValidationErrorWithNoLog("Environment key [" + MwConfigConstants.MW_SYSTEM_QUEUE_NAME + "] has no value");
		}
		
		return SYSTEM_QUEUE;
	}
	
	/**
	 * Convert MessageObject to JSON String.
	 * 
	 * @param msgObj
	 * @return
	 */
	public static String messageObjectToJson(MessageObject msgObj) {
		try {
			return new ObjectMapper().writeValueAsString(msgObj);
		} catch (JsonProcessingException e) {
			throw new ElidomServiceException(e.getMessage(), e);
		}
	}
	
	/**
	 * Parse SystemMessageReceiveEvent to MessageObject.
	 * 
	 * @param event
	 * @return
	 */
	public static MessageObject toMessageObject(SystemMessageReceiveEvent event) {
		try {
			return new ObjectMapper().readValue(new String(event.getMessage().getBody()), MessageObject.class);
		} catch (Exception e) {
			throw new ElidomServiceException(e.getMessage(), e);
		}
	}

	/**
	 * 메시징 서버로 던질 메시지의 공통 전송 요청 메시지 프로퍼티를 생성
	 * 
	 * @param msgDestId
	 * @return
	 */
	public static MessageProperties newReqMessageProp(String msgDestId) {
		return MwMessageUtil.newMessageProp(msgDestId, new Date().getTime(), false);
	}

	/**
	 * 메시징 서버로 던질 메시지의 공통 전송 요청 메시지 프로퍼티를 생성
	 * 
	 * @param msgDestId
	 * @param transmissionTime
	 * @return
	 */
	public static MessageProperties newReqMessageProp(String msgDestId, long transmissionTime) {
		return MwMessageUtil.newMessageProp(msgDestId, transmissionTime, false);
	}

	/**
	 * 메시징 서버로 던질 메시지의 공통 응답 메시지 프로퍼티를 생성
	 * 
	 * @param msgDestId
	 * @return
	 */
	public static MessageProperties newResMessageProp(String msgDestId) {
		return MwMessageUtil.newMessageProp(msgDestId, new Date().getTime(), true);
	}

	/**
	 * 메시징 서버로 던질 메시지의 공통 응답 메시지 프로퍼티를 생성
	 * 
	 * @param msgDestId
	 * @param transmissionTime
	 * @return
	 */
	public static MessageProperties newResMessageProp(String msgDestId, long transmissionTime) {
		return MwMessageUtil.newMessageProp(msgDestId, transmissionTime, true);
	}

	/**
	 * 메시징 서버로 던질 메시지의 공통 메시지 프로퍼티를 생성
	 * 
	 * @param msgDestId
	 * @param isReply
	 * @return
	 */
	public static MessageProperties newMessageProp(String msgDestId, boolean isReply) {
		return MwMessageUtil.newMessageProp(msgDestId, new Date().getTime(), isReply);
	}
	
	/**
	 * 메시징 서버로 던질 메시지의 공통 메시지 프로퍼티를 생성
	 * 
	 * @param msgId
	 * @param msgDestId
	 * @param isReply
	 * @return
	 */
	public static MessageProperties newMessageProp(String msgId, String msgDestId, boolean isReply) {
		return MwMessageUtil.newMessageProp(msgId, msgDestId, new Date().getTime(), isReply);
	}

	/**
	 * 메시징 서버로 던질 메시지의 공통 메시지 프로퍼티를 생성  
	 * 
	 * @param msgDestId 목적지 고유 ID
	 * @param transmissionTime 메시지 전송 시간
	 * @param isReply 응답 메시지 여부
	 * @return
	 */
	public static MessageProperties newMessageProp(String msgDestId, long transmissionTime, boolean isReply) {
		return MwMessageUtil.newMessageProp(null, msgDestId, transmissionTime, isReply);
	}
	
	/**
	 * 메시징 서버로 던질 메시지의 공통 메시지 프로퍼티를 생성 
	 * 
	 * @param msgId 메시지 고유 ID
	 * @param msgDestId 목적지 고유 ID
	 * @param transmissionTime 메시지 전송 시간
	 * @param isReply 응답 메시지 여부
	 * @return
	 */
	public static MessageProperties newMessageProp(String msgId, String msgDestId, long transmissionTime, boolean isReply) {
		MessageProperties properties = new MessageProperties();
		properties.setId(ValueUtil.isEmpty(msgId) ? UUID.randomUUID().toString() : msgId);
		properties.setTime(transmissionTime);
		properties.setDestId(msgDestId);
		properties.setSourceId(MwMessageUtil.getMwSystemQueue());
		properties.setIsReply(isReply);
		return properties;
	}
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param indCd
	 * @param bizId
	 * @param color
	 * @param orgRelay
	 * @param orgBoxQty
	 * @param orgEaQty
	 * @return
	 */
	public static IndicatorOnInformation newIndOnInfo(String indCd, String bizId, String color, Integer orgRelay, Integer orgBoxQty, Integer orgEaQty) {
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(indCd);
		indOnInfo.setBizId(bizId);
		indOnInfo.setColor(color);
		indOnInfo.setOrgRelay(orgRelay);
		indOnInfo.setOrgBoxQty(orgBoxQty);
		indOnInfo.setOrgEaQty(orgEaQty);
		return indOnInfo;
	}
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param indCd
	 * @param bizId
	 * @param color
	 * @param orgRelay
	 * @param orgEaQty
	 * @return
	 */
	public static IndicatorOnInformation newIndOnInfo(String indCd, String bizId, String color, Integer orgRelay, Integer orgEaQty) {
		return newIndOnInfo(indCd, bizId, color, orgRelay, null, orgEaQty);
	}
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param indCd
	 * @param bizId
	 * @param color
	 * @param orgEaQty
	 * @return
	 */
	public static IndicatorOnInformation newIndOnInfo(String indCd, String bizId, String color, Integer orgEaQty) {
		return newIndOnInfo(indCd, bizId, color, null, null, orgEaQty);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 점등 모델 생성
	 * 
	 * @param batchId
	 * @param indOnPick
	 * @return
	 */
	public static IndicatorOnInformation newIndOnInfo(String batchId, IndOnPickReq indOnPick) {
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(indOnPick.getIndCd());
		indOnInfo.setBizId(indOnPick.getJobInstanceId());
		indOnInfo.setColor(indOnPick.getColorCd());
		// 작업 배치 범위 내에서 indOnPick 정보에 수량 설정에 따른 orgRelay, orgBoxQty, orgEaQty 값 설정
		RuntimeIndicatorSetting.setIndOnQty(batchId, indOnPick, indOnInfo);
		return indOnInfo;
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 점등 모델 생성
	 * 
	 * @param domainId
	 * @param stageCd
	 * @param indOnPick
	 * @return
	 */
	public static IndicatorOnInformation newIndOnInfo(Long domainId, String stageCd, IndOnPickReq indOnPick) {
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(indOnPick.getIndCd());
		indOnInfo.setBizId(indOnPick.getJobInstanceId());
		indOnInfo.setColor(indOnPick.getColorCd());
		// 스테이지 범위 내에서 indOnPick 정보에 수량 설정에 따른 orgRelay, orgBoxQty, orgEaQty 값 설정
		StageIndicatorSetting.setIndOnQty(domainId, stageCd, indOnPick, indOnInfo);
		return indOnInfo;
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 점등 모델 생성
	 * 
	 * @param indConfigSetId
	 * @param indOnPick
	 * @return
	 */
	/*public static IndicatorOnInformation newIndOnInfo(String indConfigSetId, IndOnPickReq indOnPick) {
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(indOnPick.getIndCd());
		indOnInfo.setBizId(indOnPick.getJobInstanceId());
		indOnInfo.setColor(indOnPick.getColorCd());
		// 표시기 설정 ID 범위 내에서 indOnPick 정보에 수량 설정에 따른 orgRelay, orgBoxQty, orgEaQty 값 설정
		ConfigIndicatorSetting.setIndOnQty(indConfigSetId, indOnPick, indOnInfo);
		return indOnInfo;
	}*/
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param indCd
	 * @param bizId
	 * @param color
	 * @param segRole
	 * @param firstSegNo
	 * @param secondSegNo
	 * @param thirdSegNo
	 * @return
	 */
	public static IndicatorOnInformation newIndOnInfo(
			String indCd, 
			String bizId, 
			String color, 
			String[] segRole, 
			Integer firstSegNo, 
			Integer secondSegNo, 
			Integer thirdSegNo) {
		
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(indCd);
		indOnInfo.setBizId(bizId);
		indOnInfo.setColor(color);
		indOnInfo.setSegRole(segRole);
		indOnInfo.setOrgRelay(firstSegNo);
		indOnInfo.setOrgBoxQty(secondSegNo);
		indOnInfo.setOrgEaQty(thirdSegNo);
		return indOnInfo;
	}

	/**
	 * 작업 배치 범위 내에서 IndOnPickReq 배열을 gwPath - IndOnPickReq 리스트로 그루핑하여 리턴
	 * 
	 * @param batch
	 * @param indOnReqList
	 * @return
	 */
	public static Map<String, List<IndicatorOnInformation>> 
		groupPickingByGwPath(String batchId, List<IndOnPickReq> indOnReqList) {
		
		Map<String, List<IndicatorOnInformation>> groupGwIndOnList = 
				new HashMap<String, List<IndicatorOnInformation>>();

		for (IndOnPickReq indOnPick : indOnReqList) {
			String gwPath = indOnPick.getGwPath();
			
			List<IndicatorOnInformation> indOnList = 
					groupGwIndOnList.containsKey(gwPath) ? 
							groupGwIndOnList.get(gwPath) : new ArrayList<IndicatorOnInformation>();

			IndicatorOnInformation indOnInfo = newIndOnInfo(batchId, indOnPick);
			indOnList.add(indOnInfo);
			groupGwIndOnList.put(gwPath, indOnList);
		}

		return groupGwIndOnList;
	}

	/**
	 * IndicatorOnInformation 배열을 gwPath - IndicatorOnInformation 리스트로 그루핑하여 리턴
	 * 
	 * @param bizId
	 * @param color
	 * @param indOnStockReqList
	 * @return key : gatewayPath, value : IndicatorOnInformation List
	 */
	public static Map<String, List<IndicatorOnInformation>> groupStockByGwPath(
			String bizId, String color, List<IndOnStockReq> indOnStockReqList) {
		
		Map<String, List<IndicatorOnInformation>> gwIndOnListGroup = 
				new HashMap<String, List<IndicatorOnInformation>>();

		for (IndOnStockReq target : indOnStockReqList) {
			String gwPath = target.getGwPath();
			
			List<IndicatorOnInformation> indOnList = 
					gwIndOnListGroup.containsKey(gwPath) ? 
							gwIndOnListGroup.get(gwPath) : new ArrayList<IndicatorOnInformation>();

			IndicatorOnInformation stockInfo = 
					newIndOnInfo(target.getIndCd(), bizId, target.getColorCd(), target.getAllocQty(), target.getLoadQty());

			indOnList.add(stockInfo);
			gwIndOnListGroup.put(gwPath, indOnList);
		}

		return gwIndOnListGroup;
	}
	
	/**
	 * 표시기 설정 셋 ID 범위 내에서 IndOnPickReq 배열을 gwPath - IndOnPickReq 리스트로 그루핑하여 리턴
	 * 
	 * @param indConfigSetId
	 * @param indOnReqList
	 * @return
	 */
	public static Map<String, List<IndicatorOnInformation>> groupTestByGwPath(String indConfigSetId, List<IndOnPickReq> indOnReqList) {
		
		Map<String, List<IndicatorOnInformation>> groupGwIndOnList = 
				new HashMap<String, List<IndicatorOnInformation>>();

		for (IndOnPickReq indOnPick : indOnReqList) {
			String gwPath = indOnPick.getGwPath();
			
			List<IndicatorOnInformation> indOnList = 
					groupGwIndOnList.containsKey(gwPath) ? 
							groupGwIndOnList.get(gwPath) : new ArrayList<IndicatorOnInformation>();

			IndicatorOnInformation indOnInfo = newIndOnInfo(indConfigSetId, indOnPick);
			indOnList.add(indOnInfo);
			groupGwIndOnList.put(gwPath, indOnList);
		}

		return groupGwIndOnList;
	}

}
