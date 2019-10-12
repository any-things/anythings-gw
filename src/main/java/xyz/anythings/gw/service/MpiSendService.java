package xyz.anythings.gw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.MPI;
import xyz.anythings.gw.MwConstants;
import xyz.anythings.gw.model.GatewayDepRequest;
import xyz.anythings.gw.model.GatewayInitResponse;
import xyz.anythings.gw.model.IndicatorDepRequest;
import xyz.anythings.gw.model.IndicatorOffRequest;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.model.IndicatorOnRequest;
import xyz.anythings.gw.model.LedOffRequest;
import xyz.anythings.gw.model.LedOnRequest;
import xyz.anythings.gw.model.MiddlewareConnInfoModRequest;
import xyz.anythings.gw.model.TimesyncResponse;
import xyz.anythings.gw.service.model.MpiOffReq;
import xyz.anythings.gw.service.util.IndicatorSetting;
import xyz.anythings.gw.service.util.MwMessageUtil;
import xyz.anythings.sys.service.AbstractQueryService;
import xyz.elidom.rabbitmq.message.MessageProperties;
import xyz.elidom.sys.util.ValueUtil;

/**
 * 표시기 인터페이스 관련 서비스
 * 1) 표시기 점등 요청
 * 2) 표시기 소등 요청
 * 
 * @author shortstop
 */
@Component
public class MpiSendService extends AbstractQueryService {

	/**
	 * 미들웨어로 메시지를 전송하기 위한 유틸리티
	 */
	@Autowired
	private MwSender mwMsgSender;
	
	/**********************************************************************
	 * 							1. 표시기 On 요청
	 **********************************************************************/	
	
	/**
	 * 여러 표시기 한꺼번에 재고 실사용 점등 요청
	 * 
	 * @param domainId
	 * @param stockMpiOnList
	 */
	public void requestStockMpiOn(Long domainId, Map<String, List<IndicatorOnInformation>> stockMpiOnList) {
		if (ValueUtil.isNotEmpty(stockMpiOnList)) {
			stockMpiOnList.forEach((gwPath, stockOnList) -> {
				MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
				this.mwMsgSender.send(domainId, property, new IndicatorOnRequest(MwConstants.JOB_TYPE_DPS, MwConstants.MPI_ACTION_TYPE_STOCK, stockOnList));
			});
		}
	}
	
	/**
	 * 여러 표시기에 한꺼번에 분류 처리를 위한 점등 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param actionType
	 * @param mpiOnForPickList - key : gwPath, value : mpiOnInfo 
	 */
	public void requestMpisOn(Long domainId, String jobType, String actionType, Map<String, List<IndicatorOnInformation>> mpiOnForPickList) {
		if (ValueUtil.isNotEmpty(mpiOnForPickList)) {
			mpiOnForPickList.forEach((gwPath, mpiOnList) -> {
				MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
				this.mwMsgSender.send(domainId, property, new IndicatorOnRequest(jobType, actionType, mpiOnList));
			});
		}
	}
	
	/**
	 * 여러 표시기에 한꺼번에 분류 처리를 위한 점등 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param actionType
	 * @param mpiOnForPickList - key : gwPath, value : mpiOnInfo 
	 */
	public void requestMpisInspectOn(Long domainId, String jobType, Map<String, List<IndicatorOnInformation>> mpiOnForPickList) {
		if (ValueUtil.isNotEmpty(mpiOnForPickList)) {
			mpiOnForPickList.forEach((gwPath, mpiOnList) -> {
				MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
				for(IndicatorOnInformation indOnInfo : mpiOnList) {
					indOnInfo.setBtnMode(IndicatorSetting.MPI_BUTTON_MODE_STOP);
				}
				this.mwMsgSender.send(domainId, property, new IndicatorOnRequest(jobType, MwConstants.MPI_ACTION_TYPE_INSPECT, mpiOnList));
			});
		}
	}
	
	/**
	 * 하나의 표시기에 분류 처리를 위한 점등 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param color
	 * @param boxQty
	 * @param eaQty
	 */
	public void requestPickMpiOn(Long domainId, String jobType, String mpiCd, String bizId, String color, Integer boxQty, Integer eaQty) {
		this.requestCommonMpiOn(domainId, jobType, mpiCd, bizId, MwConstants.MPI_ACTION_TYPE_PICK, color, boxQty, eaQty);
	}
	
	/**
	 * 하나의 표시기에 검수를 위한 점등 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param color
	 * @param boxQty
	 * @param eaQty
	 */
	public void requestInspectMpiOn(Long domainId, String jobType, String mpiCd, String bizId, String color, Integer boxQty, Integer eaQty) {
		this.requestCommonMpiOn(domainId, jobType, mpiCd, bizId, MwConstants.MPI_ACTION_TYPE_INSPECT, color, boxQty, eaQty);
	}
	
	/**
	 * 하나의 표시기에 액션 타입별 점등 요청 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param actionType
	 * @param color
	 * @param boxQty
	 * @param eaQty
	 */
	public void requestCommonMpiOn(Long domainId, String jobType, String mpiCd, String bizId, String actionType, String color, Integer boxQty, Integer eaQty) {
		String gwPath = MPI.findGatewayPath(domainId, mpiCd);
		requestCommonMpiOn(domainId, jobType, mpiCd, gwPath, bizId, actionType, color, boxQty, eaQty);
	}
	
	/**
	 * 하나의 표시기에 액션 타입별 점등 요청 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param gwPath
	 * @param bizId
	 * @param actionType
	 * @param color
	 * @param boxQty
	 * @param eaQty
	 */
	public void requestCommonMpiOn(Long domainId, String jobType, String mpiCd, String gwPath, String bizId, String actionType, String color, Integer boxQty, Integer eaQty) {
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		List<IndicatorOnInformation> indOnList = new ArrayList<IndicatorOnInformation>(1);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiCd);
		indOnInfo.setBizId(bizId);
		indOnInfo.setColor(color);
		indOnInfo.setOrgBoxQty(boxQty);
		indOnInfo.setOrgEaQty(eaQty);
		indOnList.add(indOnInfo);
		this.mwMsgSender.send(domainId, property, new IndicatorOnRequest(jobType, actionType, indOnList));
	}	
	
	/**********************************************************************
	 * 							2. 표시기 Off 요청
	 **********************************************************************/	
	/**
	 * 표시기 하나에 대한 소등 요청 
	 * 
	 * @param domainId
	 * @param mpiCd
	 * @param forceOff
	 */
	public void requestMpiOff(Long domainId, String mpiCd, boolean forceOff) {
		String gwPath = MPI.findGatewayPath(domainId, mpiCd);
		IndicatorOffRequest mpiOff = new IndicatorOffRequest();
		mpiOff.setIndOff(ValueUtil.newStringList(mpiCd));
		mpiOff.setForceFlag(forceOff);
		this.mwMsgSender.sendRequest(domainId, gwPath, mpiOff);
	}
	
	/**
	 * 표시기 하나에 대한 소등 요청 
	 * 
	 * @param domainId
	 * @param mpiCd
	 */
	public void requestMpiOff(Long domainId, String mpiCd) {
		this.requestMpiOff(domainId, mpiCd, false);
	}
	
	/**
	 * 게이트웨이 리스트로 표시기 소등 요청
	 * 
	 * @param domainId
	 * @param gwPathList
	 * @param forceOff
	 */
	public void requestMpiOff(Long domainId, List<String> gwPathList, boolean forceOff) {
		if(ValueUtil.isNotEmpty(gwPathList)) {
			for(String gwPath : gwPathList) {
				this.requestMpiOff(domainId, gwPath, Gateway.mpiCdList(domainId, gwPath), forceOff);
			}
		}
	}
	
	/**
	 * 게이트웨이에 게이트웨이 소속 모든 표시기 소등 요청
	 * 
	 * @param domainId
	 * @param gwPath
	 * @param mpiCdList
	 * @param forceOff 강제 소등 여부
	 */
	public void requestMpiOff(Long domainId, String gwPath, List<String> mpiCdList, boolean forceOff) {
		if (ValueUtil.isNotEmpty(mpiCdList)) {
			IndicatorOffRequest mpiOff = new IndicatorOffRequest();
			mpiOff.setIndOff(mpiCdList);
			mpiOff.setForceFlag(forceOff);
			// 현재는 forceOff와 endOff가 동일값을 가짐
			mpiOff.setEndOffFlag(forceOff);
			this.mwMsgSender.sendRequest(domainId, gwPath, mpiOff);
		}
	}
	
	/**
	 * 호기별 표시기 Off 요청 전송 
	 * 
	 * @param domainId
	 * @param regionCd 호기 코드
	 * @param forceOff 강제 소등 여부
	 */
	public void requestMpiOffByRegion(Long domainId, String regionCd, boolean forceOff) {
		// 1. 로케이션 정보로 부터 호기, 장비 존, 호기 사이드 코드로 표시기, 게이트웨이 정보를 추출한다. 
		List<MpiOffReq> mpiList = this.searchMpiByWorkZone(domainId, regionCd, null);
		// 2. 표시기별 소등 요청
		this.requestOffByMpiList(domainId, mpiList, forceOff);
	}
	
	/**
	 * 호기 및 장비 작업 존 별 표시기 Off 요청 전송 
	 * 
	 * @param domainId
	 * @param regionCd
	 * @param equipZoneCd
	 * @param sideCd
	 */
	public void requestMpiOffByEquipZone(Long domainId, String regionCd, String equipZoneCd, String sideCd) {
		// 1. 로케이션 정보로 부터 호기, 장비 존, 호기 사이드 코드로 표시기, 게이트웨이 정보를 추출한다. 
		List<MpiOffReq> mpiList = this.searchMpiByEquipZone(domainId, regionCd, equipZoneCd, sideCd);
		// 2. 표시기별 소등 요청
		this.requestOffByMpiList(domainId, mpiList, false);
	}
	
	/**
	 * 호기 및 작업 존 별 표시기 Off 요청 전송 
	 * 
	 * @param domainId
	 * @param regionCd
	 * @param zoneCd
	 */
	public void requestMpiOffByWorkZone(Long domainId, String regionCd, String zoneCd) {
		// 1. 로케이션 정보로 부터 호기, 장비 존, 호기 사이드 코드로 표시기, 게이트웨이 정보를 추출한다. 
		List<MpiOffReq> mpiList = this.searchMpiByWorkZone(domainId, regionCd, zoneCd);
		// 2. 표시기별 소등 요청
		this.requestOffByMpiList(domainId, mpiList, false);
	}
	
	/**
	 * MPI List로 표시기 Off
	 * 
	 * @param domainId
	 * @param mpiList
	 * @param forceOff 강제 소등 여부 
	 */
	public void requestOffByMpiList(Long domainId, List<MpiOffReq> mpiList, boolean forceOff) {
		// 1. 게이트웨이 별로 표시기 리스트를 보내서 소등 요청을 한다.
		Map<String, List<String>> mpisByGwPath = new HashMap<String, List<String>>();
		String prevGwPath = null;
		
		for(MpiOffReq mpi : mpiList) {
			String gwPath = mpi.getGwPath();
			
			if(ValueUtil.isNotEqual(gwPath, prevGwPath)) {
				mpisByGwPath.put(gwPath, ValueUtil.newStringList(mpi.getMpiCd()));
				prevGwPath = gwPath;
			} else {
				mpisByGwPath.get(gwPath).add(mpi.getMpiCd());
			}
		}
		
		// 2. 게이트웨이 별로 MPI 코드 리스트로 소등 요청 
		Iterator<String> gwIter = mpisByGwPath.keySet().iterator();
		while(gwIter.hasNext()) {
			String gwPath = gwIter.next();
			List<String> gwMpiList = mpisByGwPath.get(gwPath);
			this.requestMpiOff(domainId, gwPath, gwMpiList, forceOff);
		}
	}
	
	/**********************************************************************
	 * 							3. 표시기 숫자, 문자 표시 요청
	 **********************************************************************/		
	
	/**
	 * 작업 완료 표시기 표시 요청 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param finalEnd 최종 완료 (End End 표시 후 Fullbox까지 마쳤는지) 여부
	 */
	public void requestMpiEndDisplay(Long domainId, String jobType, String mpiId, String bizId, boolean finalEnd) {
		String gwPath = MPI.findGatewayPath(domainId, mpiId);
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiId);
		indOnInfo.setBizId(bizId);
		indOnInfo.setEndFullBox(!finalEnd);
		IndicatorOnRequest indOnReq = new IndicatorOnRequest(jobType, MwConstants.MPI_BIZ_FLAG_END, ValueUtil.toList(indOnInfo));
		indOnReq.setReadOnly(finalEnd);
		this.mwMsgSender.send(domainId, property, indOnReq);		
	}
	
	/**
	 * 로케이션 별 공박스 매핑 필요 표시 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 */
	public void requestMpiNoBoxDisplay(Long domainId, String jobType, String mpiId) {
		this.requestMpiDisplay(domainId, jobType, mpiId, mpiId, MwConstants.MPI_ACTION_TYPE_NOBOX, false, null, null, null);
	}
	
	/**
	 * Fullbox시에 로케이션-공박스 매핑이 안 된 에러를 표시기에 표시하기 위한 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 */
	public void requestMpiErrBoxDisplay(Long domainId, String jobType, String mpiId) {
		this.requestMpiDisplay(domainId, jobType, mpiId, mpiId, MwConstants.MPI_ACTION_TYPE_ERRBOX, false, null, null, null);
	}
	
	/**
	 * 표시기에 버튼 점등은 되지 않고 eaQty 정보로 표시 - 사용자 터치 반응 안함 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param pickEaQty
	 */
	public void requestMpiDisplayOnly(Long domainId, String jobType, String mpiId, String bizId, Integer pickEaQty) {
		this.requestMpiDisplay(domainId, jobType, mpiId, bizId, MwConstants.MPI_ACTION_TYPE_DISPLAY, true, null, null, pickEaQty);
	}
	
	/**
	 * 세그먼트 정보를 커스터마이징 한 표시기 표시 - 이 때 Fullbox가 되어야 하므로 readOnly는 false로
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param firstSegQty
	 * @param secondSegQty
	 * @param thirdSegQty
	 */
	public void requestMpiSegmentDisplay(Long domainId, String jobType, String mpiId, String bizId, Integer firstSegQty, Integer secondSegQty, Integer thirdSegQty) {
		this.requestMpiDisplay(domainId, jobType, mpiId, bizId, MwConstants.MPI_ACTION_TYPE_DISPLAY, false, firstSegQty, secondSegQty, thirdSegQty);
	}
	
	/**
	 * 각종 옵션으로 표시기에 표시 요청 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param displayActionType
	 * @param readOnly
	 * @param firstSegQty
	 * @param secondSegQty
	 * @param thirdSegQty
	 */
	public void requestMpiDisplay(Long domainId, String jobType, String mpiId, String bizId, String displayActionType, boolean readOnly, Integer firstSegQty, Integer secondSegQty, Integer thirdSegQty) {
		String gwPath = MPI.findGatewayPath(domainId, mpiId);
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		List<IndicatorOnInformation> indOnList = new ArrayList<IndicatorOnInformation>(1);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiId);
		indOnInfo.setBizId(bizId);
		indOnInfo.setOrgRelay(firstSegQty);
		indOnInfo.setOrgBoxQty(secondSegQty);
		indOnInfo.setOrgEaQty(thirdSegQty);
		indOnList.add(indOnInfo);
		IndicatorOnRequest indOnReq = new IndicatorOnRequest(jobType, displayActionType, indOnList);
		indOnReq.setReadOnly(readOnly);
		this.mwMsgSender.send(domainId, property, indOnReq);
	}
	
	/**
	 * 각종 옵션으로 표시기에 표시 요청 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param displayActionType
	 * @param readOnly
	 * @param segRole
	 * @param firstSegQty
	 * @param secondSegQty
	 * @param thirdSegQty
	 */
	public void requestMpiDisplay(Long domainId, String jobType, String mpiId, String bizId, String displayActionType, String[] segRole, boolean readOnly, Integer firstSegQty, Integer secondSegQty, Integer thirdSegQty) {
		String gwPath = MPI.findGatewayPath(domainId, mpiId);
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		List<IndicatorOnInformation> indOnList = new ArrayList<IndicatorOnInformation>(1);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiId);
		indOnInfo.setBizId(bizId);
		indOnInfo.setSegRole(segRole);
		indOnInfo.setOrgRelay(firstSegQty);
		indOnInfo.setOrgBoxQty(secondSegQty);
		indOnInfo.setOrgEaQty(thirdSegQty);
		indOnList.add(indOnInfo);
		IndicatorOnRequest indOnReq = new IndicatorOnRequest(jobType, displayActionType, indOnList);
		indOnReq.setReadOnly(readOnly);
		this.mwMsgSender.send(domainId, property, indOnReq);
	}
	
	/**
	 * 총 처리한 수량 / 방금 처리한 수량을 표시
	 * 
	 * @param domainId
	 * @param gwPath
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param accumQty
	 * @param pickedQty
	 */
	public void requestMpiDisplayAccumQty(Long domainId, String gwPath, String jobType, String mpiId, String bizId, Integer accumQty, Integer pickedQty) {
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		List<IndicatorOnInformation> indOnList = new ArrayList<IndicatorOnInformation>(1);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiId);
		indOnInfo.setBizId(bizId);
		indOnInfo.setSegRole(new String[] { IndicatorSetting.MPI_SEGMENT_ROLE_RELAY_SEQ, IndicatorSetting.MPI_SEGMENT_ROLE_PCS });
		indOnInfo.setOrgAccmQty(accumQty);
		indOnInfo.setOrgEaQty(pickedQty);
		indOnList.add(indOnInfo);
		IndicatorOnRequest indOnReq = new IndicatorOnRequest(jobType, MwConstants.MPI_ACTION_TYPE_DISPLAY, indOnList);
		indOnReq.setReadOnly(true);
		this.mwMsgSender.send(domainId, property, indOnReq);
	}
	
	/**
	 * 총 처리한 수량 / 방금 처리한 수량을 표시
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param accumQty
	 * @param pickedQty
	 */
	public void requestMpiDisplayAccumQty(Long domainId, String jobType, String mpiId, String bizId, Integer accumQty, Integer pickedQty) {
		String gwPath = MPI.findGatewayPath(domainId, mpiId);
		this.requestMpiDisplayAccumQty(domainId, gwPath, jobType, mpiId, bizId, accumQty, pickedQty);
	}
	
	/**
	 * FullBox 표시기 표시 요청 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiId
	 * @param bizId
	 * @param color
	 */
	public void requestFullbox(Long domainId, String jobType, String mpiId, String bizId, String color) {
		this.requestCommonMpiOn(domainId, jobType, mpiId, bizId, MwConstants.MPI_BIZ_FLAG_FULL, color, 0, 0);
	}
	
	/**
	 * 표시기에 문자열 표시 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param displayStr
	 */
	public void requestShowString(Long domainId, String jobType, String mpiCd, String bizId, String displayStr) {
		this.requestShowString(domainId, jobType, null, mpiCd, bizId, displayStr);
	}

	/**
	 * 표시기에 문자열 표시 요청
	 * 
	 * @param domainId
	 * @param jobType
	 * @param gwPath
	 * @param mpiCd
	 * @param bizId
	 * @param displayStr
	 */
	public void requestShowString(Long domainId, String jobType, String gwPath, String mpiCd, String bizId, String displayStr) {
		if(ValueUtil.isEmpty(gwPath)) {
			gwPath = MPI.findGatewayPath(domainId, mpiCd);
		}
		
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		List<IndicatorOnInformation> indOnList = new ArrayList<IndicatorOnInformation>(1);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiCd);
		indOnInfo.setBizId(bizId);
		indOnInfo.setViewStr(displayStr);
		indOnList.add(indOnInfo);
		this.mwMsgSender.send(domainId, property, new IndicatorOnRequest(jobType, MwConstants.MPI_ACTION_TYPE_STR_SHOW, indOnList));
	}

	/**
	 * 표시기 표시 방향과 숫자를 동시에 표시 - 왼쪽은 'L' or 'R' 표시 오른쪽은 숫자 표시
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param leftSideFlag 왼쪽 로케이션 표시용인지 여부
	 * @param rightQty
	 */
	public void requestDisplayDirectionAndQty(Long domainId, String jobType, String mpiCd, String bizId, boolean leftSideFlag, Integer rightQty) {		
		requestDisplayLeftStringRightQty(domainId, jobType, mpiCd, bizId, leftSideFlag ? " L " : " R ", rightQty);
	}
	
	/**
	 * 왼쪽은 문자 오른쪽은 숫자 표시
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param leftStr
	 * @param rightQty
	 */
	public void requestDisplayLeftStringRightQty(Long domainId, String jobType, String mpiCd, String bizId, String leftStr, Integer rightQty) {		
		String gwPath = MPI.findGatewayPath(domainId, mpiCd);
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		List<IndicatorOnInformation> indOnList = new ArrayList<IndicatorOnInformation>(1);
		IndicatorOnInformation indOnInfo = new IndicatorOnInformation();
		indOnInfo.setId(mpiCd);
		indOnInfo.setBizId(bizId);
		indOnInfo.setSegRole(new String[] { IndicatorSetting.MPI_SEGMENT_ROLE_STR, IndicatorSetting.MPI_SEGMENT_ROLE_PCS });
		indOnInfo.setViewStr(leftStr);
		indOnInfo.setOrgEaQty(rightQty);
		indOnList.add(indOnInfo);
		IndicatorOnRequest indOnReq = new IndicatorOnRequest(jobType, MwConstants.MPI_ACTION_TYPE_DISPLAY, indOnList);
		indOnReq.setReadOnly(true);
		this.mwMsgSender.send(domainId, property, indOnReq);
	}
	
	/**
	 * 표시기 표시 방향과 표시 수량을 좌, 우측에 동시에 표시 
	 * 
	 * @param domainId
	 * @param jobType
	 * @param mpiCd
	 * @param bizId
	 * @param leftQty
	 * @param rightQty
	 */
	public void requestDisplayBothDirectionQty(Long domainId, String jobType, String mpiCd, String bizId, Integer leftQty, Integer rightQty) {
		StringBuffer showStr = new StringBuffer();
		if(leftQty != null) {
			showStr.append(MwConstants.MPI_LEFT_SEGMENT).append(StringUtils.leftPad(ValueUtil.toString(leftQty), 2));
		} else {
			showStr.append("   ");
		}
		
		if(rightQty != null) {
			showStr.append(MwConstants.MPI_RIGHT_SEGMENT).append(StringUtils.leftPad(ValueUtil.toString(rightQty), 2));
		} else {
			showStr.append("   ");
		}
		
		this.requestShowString(domainId, jobType, mpiCd, bizId, showStr.toString());
	}

	/**********************************************************************
	 * 							4. 게이트웨이 초기화 요청
	 **********************************************************************/	
	
	/**
	 * 게이트웨이 초기화 응답 전송.
	 * 
	 * @param domainId
	 * @param msgDestId
	 * @param gatewayInitRes
	 */
	public void respondGatewayInit(Long domainId, String msgDestId, GatewayInitResponse gatewayInitRes) {
		this.mwMsgSender.sendRequest(domainId, msgDestId, gatewayInitRes);
	}
	
	/**********************************************************************
	 * 							5. 미들웨어 정보 변경 요청
	 **********************************************************************/	

	/**
	 * 게이트웨이에 미들웨어 접속 정보 변경 요청
	 * 
	 * @param domainId
	 * @param msgDestId
	 * @param mwConnModifyReq
	 */
	public void requestMwConnectionModify(Long domainId, String msgDestId, MiddlewareConnInfoModRequest mwConnModifyReq) {
		this.mwMsgSender.sendRequest(domainId, msgDestId, mwConnModifyReq);
	}
	
	/**********************************************************************
	 * 							6. 게이트웨이 시스템 시간 동기화 
	 **********************************************************************/	

	/**
	 * 게이트웨이와 시스템간의 시간 동기화 응답 요청.
	 * 
	 * @param domainId
	 * @param msgDestId
	 * @param serverTime
	 */
	public void respondTimesync(Long domainId, String msgDestId, long serverTime) {
		this.mwMsgSender.sendRequest(domainId, msgDestId, new TimesyncResponse(serverTime));
	}
	
	/**********************************************************************
	 * 							7. 게이트웨이 / 표시기 펌웨어 배포  
	 **********************************************************************/	
	
	/**
	 * 게이트웨이에 게이트웨이 펌웨어 배포 정보 전송 
	 * 
	 * @parma domainId
	 * @param gwChannel 게이트웨이 구분 채널 
	 * @param gwVersion 게이트웨이 펌웨어 버전 
	 * @param gwFwDownloadUrl 게이트웨이 펌웨어 다운로드 URL
	 * @param filename 파일명
	 * @param forceFlag 강제 업데이트 여부
	 */
	public void deployGatewayFirmware(Long domainId, String gwChannel, String gwVersion, String gwFwDownloadUrl, String filename, Boolean forceFlag) {
		GatewayDepRequest gwDeploy = new GatewayDepRequest();
		gwDeploy.setGwUrl(gwFwDownloadUrl);
		gwDeploy.setVersion(gwVersion);
		gwDeploy.setFilename(filename);
		gwDeploy.setForceFlag(forceFlag);
		this.mwMsgSender.sendRequest(domainId, gwChannel, gwDeploy);
	}
	
	/**
	 * 게이트웨이에 표시기 펌웨어 배포 정보 전송 
	 * 
	 * @param domainId
	 * @param gwChannel 게이트웨이 구분 채널
	 * @param mpiVersion 표시기 펌웨어 버전 
	 * @param mpiFwDownloadUrl 표시기 펌웨어 다운로드 URL
	 * @param filename 파일명
	 * @param forceFlag 강제 업데이트 여부
	 */
	public void deployMpiFirmware(Long domainId, String gwChannel, String mpiVersion, String mpiFwDownloadUrl, String filename, Boolean forceFlag) {
		IndicatorDepRequest mpiDeploy = new IndicatorDepRequest();
		mpiDeploy.setVersion(mpiVersion);
		mpiDeploy.setIndUrl(mpiFwDownloadUrl);
		mpiDeploy.setFilename(filename);
		mpiDeploy.setForceFlag(forceFlag);
		this.mwMsgSender.sendRequest(domainId, gwChannel, mpiDeploy);
	}
	
	/**********************************************************************
	 * 							8. LED 바 점등 / 소등 
	 **********************************************************************/	
	
	/**
	 * 표시기 LED 점등 
	 * 
	 * @param domainId
	 * @param mpiCd
	 * @param ledBarBrightness
	 */
	public void requestLedOn(Long domainId, String mpiCd, Integer ledBarBrightness) {
		LedOnRequest ledOnReq = new LedOnRequest();
		ledOnReq.setId(mpiCd);
		ledOnReq.setLedBarBrtns(ledBarBrightness);
		String gwPath = MPI.findGatewayPath(domainId, mpiCd);
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		this.mwMsgSender.send(domainId, property, ledOnReq);
	}
	
	/**
	 * 표시기 LED 소등 
	 * 
	 * @param domainId
	 * @param mpiCd
	 */
	public void requestLedOff(Long domainId, String mpiCd) {
		LedOffRequest ledOffReq = new LedOffRequest();
		ledOffReq.setId(mpiCd);
		String gwPath = MPI.findGatewayPath(domainId, mpiCd);
		MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
		this.mwMsgSender.send(domainId, property, ledOffReq);		
	}
	
	/**
	 * 표시기 LED 리스트 점등 
	 * 
	 * @param domainId
	 * @param mpiList
	 * @param ledBarBrightness
	 */
	public void requestLedListOn(Long domainId, List<MpiOffReq> mpiList, Integer ledBarBrightness) {
		// 1. 게이트웨이 별로 표시기 리스트를 보내서 점등 요청을 한다.
		Map<String, List<String>> mpisByGwPath = new HashMap<String, List<String>>();
		String prevGwPath = null;
		
		for(MpiOffReq mpi : mpiList) {
			String gwPath = mpi.getGwPath();
			
			if(ValueUtil.isNotEqual(gwPath, prevGwPath)) {
				mpisByGwPath.put(gwPath, ValueUtil.newStringList(mpi.getMpiCd()));
				prevGwPath = gwPath;
			} else {
				mpisByGwPath.get(gwPath).add(mpi.getMpiCd());
			}
		}
		
		// 2. 게이트웨이 별로 MPI 코드 리스트로 소등 요청
		Iterator<String> gwPathIter = mpisByGwPath.keySet().iterator();
		while(gwPathIter.hasNext()) {
			String gwPath = gwPathIter.next();
			List<String> mpiCdList = mpisByGwPath.get(gwPath);
			
			// TODO 아래 부분을 한번에 보내도록 수정
			for(String mpiCd : mpiCdList) {
				LedOnRequest ledOnReq = new LedOnRequest();
				ledOnReq.setId(mpiCd);
				ledOnReq.setLedBarBrtns(ledBarBrightness);
				MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
				this.mwMsgSender.send(domainId, property, ledOnReq);			
			}			
		}
	}
	
	/**
	 * 표시기 LED 리스트 소등 
	 * 
	 * @param domainId
	 * @param mpiList
	 */
	public void requestLedListOff(Long domainId, List<MpiOffReq> mpiList) {
		// 1. 게이트웨이 별로 표시기 리스트를 보내서 점등 요청을 한다.
		Map<String, List<String>> mpisByGwPath = new HashMap<String, List<String>>();
		String prevGwPath = null;
		
		for(MpiOffReq mpi : mpiList) {
			String gwPath = mpi.getGwPath();
			
			if(ValueUtil.isNotEqual(gwPath, prevGwPath)) {
				mpisByGwPath.put(gwPath, ValueUtil.newStringList(mpi.getMpiCd()));
				prevGwPath = gwPath;
			} else {
				mpisByGwPath.get(gwPath).add(mpi.getMpiCd());
			}
		}
		
		// 2. 게이트웨이 별로 MPI 코드 리스트로 소등 요청
		Iterator<String> gwPathIter = mpisByGwPath.keySet().iterator();
		while(gwPathIter.hasNext()) {
			String gwPath = gwPathIter.next();
			List<String> mpiCdList = mpisByGwPath.get(gwPath);
			
			// TODO 아래 부분을 한번에 보내도록 수정
			for(String mpiCd : mpiCdList) {
				LedOffRequest ledOnReq = new LedOffRequest();
				ledOnReq.setId(mpiCd);
				MessageProperties property = MwMessageUtil.newReqMessageProp(gwPath);
				this.mwMsgSender.send(domainId, property, ledOnReq);			
			}			
		}
	}

	/**********************************************************************
	 * 							9. 기타 / Private Methods 
	 **********************************************************************/
	
	/**
	 * 호기 및 장비 작업 존 별 게이트웨이 Path 정보 조회
	 * 
	 * @param domainId
	 * @param regionCd
	 * @param equipZoneCd
	 * @param sideCd
	 * @return
	 */
	public List<String> searchGwByEquipZone(Long domainId, String regionCd, String equipZoneCd, String sideCd) {
		Map<String, Object> params = 
			ValueUtil.newMap("domainId,regionCd,equipZoneCd,sideCd", domainId, regionCd, equipZoneCd, MwConstants.checkSideCdForQuery(domainId, sideCd));
		String sql = this.getSearchMpiQuery(false);
		return queryManager.selectListBySql(sql, params, String.class, 0, 0);
	}
	
	/**
	 * 호기 및 장비 존 코드 사이드 코드로 표시기 리스트를 조회  
	 * 
	 * @param domainId
	 * @param regionCd
	 * @param equipZoneCd
	 * @param sideCd
	 * @return
	 */
	public List<MpiOffReq> searchMpiByEquipZone(Long domainId, String regionCd, String equipZoneCd, String sideCd) {
		Map<String, Object> params = 
			ValueUtil.newMap("domainId,regionCd,equipZoneCd,sideCd", domainId, regionCd, equipZoneCd, MwConstants.checkSideCdForQuery(domainId, sideCd));
		String sql = this.getSearchMpiQuery(true);		
		return queryManager.selectListBySql(sql, params, MpiOffReq.class, 0, 0);
	}	
	
	/**
	 * 호기 및 호기 작업 존 별 게이트웨이 Path 리스트 조회 
	 * 
	 * @param domainId
	 * @param regionCd
	 * @param zoneCd
	 * @return
	 */
	public List<String> searchGwByWorkZone(Long domainId, String regionCd, String zoneCd) {
		Map<String, Object> params = 
			ValueUtil.newMap("domainId,regionCd,zoneCd", domainId, regionCd, zoneCd);
		String sql = this.getSearchMpiQuery(false);
		return queryManager.selectListBySql(sql, params, String.class, 0, 0);
	}
	
	/**
	 * 호기 및 작업 존 코드로 표시기 리스트를 조회
	 * 
	 * @param domainId
	 * @param regionCd
	 * @param zoneCd
	 * @return
	 */
	public List<MpiOffReq> searchMpiByWorkZone(Long domainId, String regionCd, String zoneCd) {
		Map<String, Object> params = 
			ValueUtil.newMap("domainId,regionCd,zoneCd", domainId, regionCd, zoneCd);
		String sql = this.getSearchMpiQuery(true);
		return queryManager.selectListBySql(sql, params, MpiOffReq.class, 0, 0);
	}
	
	/**
	 * 표시기 혹은 게이트웨이 조회 쿼리 
	 * 
	 * @param isMpiQuery 표시기 조회 쿼리 인지 게이트웨이 조회 쿼리 인지...
	 * @return
	 */
	public String getSearchMpiQuery(boolean isMpiQuery) {
		StringBuffer sql = new StringBuffer();
		return 
		sql.append(" SELECT")
		   .append(isMpiQuery ? "	L.MPI_CD, G.GW_NM AS GW_PATH" : "	DISTINCT(G.GW_NM) AS GW_PATH") 
		   .append(" FROM ")
		   .append("	TB_LOCATION L")
		   .append("	INNER JOIN TB_MPI M ON L.DOMAIN_ID = M.DOMAIN_ID AND L.MPI_CD = M.MPI_CD")
		   .append("	INNER JOIN TB_GATEWAY G ON M.DOMAIN_ID = G.DOMAIN_ID AND M.GW_CD = G.GW_CD")
		   .append(" WHERE")
		   .append(" 	M.DOMAIN_ID = :domainId")
		   .append("	AND L.active_flag = 1")
		   .append("	#if($regionCd)")
		   .append(" 	AND L.REGION_CD = :regionCd")
		   .append("	#end")
		   .append(" 	#if($zoneCd)")
		   .append(" 	AND L.ZONE_CD = :zoneCd")
		   .append(" 	#end")
		   .append(" 	#if($gwZoneCd)")
		   .append(" 	AND L.GW_ZONE_CD = :gwZoneCd")
		   .append(" 	#end")
		   .append(" 	#if($equipZoneCd)")
		   .append(" 	AND L.EQUIP_ZONE_CD = :equipZoneCd")
		   .append(" 	#end")
		   .append(" 	#if($sideCd)")
		   .append(" 	AND L.SIDE_CD = :sideCd")
		   .append(" 	#end")
		   .append(isMpiQuery ? " ORDER BY G.GW_NM ASC, L.LOC_SEQ ASC" : " ORDER BY G.GW_NM ASC").toString();
	}
}