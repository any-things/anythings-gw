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
	 * @param mpiCd
	 * @param bizId
	 * @param color
	 * @param orgRelay
	 * @param orgBoxQty
	 * @param orgEaQty
	 * @return
	 */
	public static IndicatorOnInformation newMpiOnInfo(String mpiCd, String bizId, String color, Integer orgRelay, Integer orgBoxQty, Integer orgEaQty) {
		IndicatorOnInformation mpiOnInfo = new IndicatorOnInformation();
		mpiOnInfo.setId(mpiCd);
		mpiOnInfo.setBizId(bizId);
		mpiOnInfo.setColor(color);
		mpiOnInfo.setOrgRelay(orgRelay);
		mpiOnInfo.setOrgBoxQty(orgBoxQty);
		mpiOnInfo.setOrgEaQty(orgEaQty);
		return mpiOnInfo;
	}
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param mpiCd
	 * @param bizId
	 * @param color
	 * @param orgRelay
	 * @param orgEaQty
	 * @return
	 */
	public static IndicatorOnInformation newMpiOnInfo(String mpiCd, String bizId, String color, Integer orgRelay, Integer orgEaQty) {
		return newMpiOnInfo(mpiCd, bizId, color, orgRelay, null, orgEaQty);
	}
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param mpiCd
	 * @param bizId
	 * @param color
	 * @param orgEaQty
	 * @return
	 */
	public static IndicatorOnInformation newMpiOnInfo(String mpiCd, String bizId, String color, Integer orgEaQty) {
		return newMpiOnInfo(mpiCd, bizId, color, null, null, orgEaQty);
	}
	
	/**
	 * 표시기 점등 모델 생성
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiOnPick
	 * @return
	 */
	public static IndicatorOnInformation newMpiOnInfo(Long domainId, String jobType, IndOnPickReq mpiOnPick) {
		IndicatorOnInformation mpiOnInfo = new IndicatorOnInformation();
		mpiOnInfo.setId(mpiOnPick.getIndCd());
		mpiOnInfo.setBizId(mpiOnPick.getJobInstanceId());
		mpiOnInfo.setColor(mpiOnPick.getColorCd());
		// mpiOnPick 정보로 mpiOnInfo에 orgRelay, orgBoxQty, orgEaQty 값 설정
		IndicatorSetting.setMpiOnQty(domainId, jobType, mpiOnPick, mpiOnInfo);
		return mpiOnInfo;
	}
	
	/**
	 * 표시기 점등 모델 생성 
	 * 
	 * @param mpiCd
	 * @param bizId
	 * @param color
	 * @param segRole
	 * @param firstSegNo
	 * @param secondSegNo
	 * @param thirdSegNo
	 * @return
	 */
	public static IndicatorOnInformation newMpiOnInfo(
			String mpiCd, 
			String bizId, 
			String color, 
			String[] segRole, 
			Integer firstSegNo, 
			Integer secondSegNo, 
			Integer thirdSegNo) {
		
		IndicatorOnInformation mpiOnInfo = new IndicatorOnInformation();
		mpiOnInfo.setId(mpiCd);
		mpiOnInfo.setBizId(bizId);
		mpiOnInfo.setColor(color);
		mpiOnInfo.setSegRole(segRole);
		mpiOnInfo.setOrgRelay(firstSegNo);
		mpiOnInfo.setOrgBoxQty(secondSegNo);
		mpiOnInfo.setOrgEaQty(thirdSegNo);
		return mpiOnInfo;
	}

	/**
	 * jobType에 따라 MpiLightOnReq 배열을 gwPath - MpiLightOnReq 리스트로 그루핑하여 리턴
	 * 
	 * @param domainId
	 * @param jobType DAS, DPS, RTN
	 * @param mpiOnReqList
	 * @return
	 */
	public static Map<String, List<IndicatorOnInformation>> 
		groupPickingByGwPath(Long domainId, String jobType, List<IndOnPickReq> mpiOnReqList) {
		
		Map<String, List<IndicatorOnInformation>> groupGwMpiOnList = 
				new HashMap<String, List<IndicatorOnInformation>>();

		for (IndOnPickReq mpiOnPick : mpiOnReqList) {
			String gwPath = mpiOnPick.getGwPath();
			
			List<IndicatorOnInformation> mpiOnList = 
					groupGwMpiOnList.containsKey(gwPath) ? 
							groupGwMpiOnList.get(gwPath) : new ArrayList<IndicatorOnInformation>();

			IndicatorOnInformation mpiOnInfo = newMpiOnInfo(domainId, jobType, mpiOnPick);
			mpiOnList.add(mpiOnInfo);
			groupGwMpiOnList.put(gwPath, mpiOnList);
		}

		return groupGwMpiOnList;
	}

	/**
	 * MpiLightOnStockReq 배열을 gwPath - MpiLightOnStockReq 리스트로 그루핑하여 리턴
	 * 
	 * @param bizId
	 * @param mpiColor
	 * @param mpiOnStockReqList
	 * @return key : gatewayPath, value : IndicatorOnInformation List
	 */
	public static Map<String, List<IndicatorOnInformation>> groupStockByGatewayPath(
			String bizId, String mpiColor, List<IndOnStockReq> mpiOnStockReqList) {
		
		Map<String, List<IndicatorOnInformation>> gwMpiOnListGroup = 
				new HashMap<String, List<IndicatorOnInformation>>();

		for (IndOnStockReq target : mpiOnStockReqList) {
			String gwPath = target.getGwPath();
			
			List<IndicatorOnInformation> mpiOnList = 
					gwMpiOnListGroup.containsKey(gwPath) ? 
							gwMpiOnListGroup.get(gwPath) : new ArrayList<IndicatorOnInformation>();

			IndicatorOnInformation stockInfo = 
					newMpiOnInfo(target.getIndCd(), bizId, target.getColorCd(), target.getAllocQty(), target.getLoadQty());

			mpiOnList.add(stockInfo);
			gwMpiOnListGroup.put(gwPath, mpiOnList);
		}

		return gwMpiOnListGroup;
	}

}
