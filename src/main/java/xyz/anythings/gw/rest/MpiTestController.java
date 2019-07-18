package xyz.anythings.gw.rest;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.anythings.base.entity.JobProcess;
import xyz.anythings.base.entity.Location;
import xyz.anythings.gw.LogisGwConstants;
import xyz.anythings.gw.model.Action;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.service.MpiSendService;
import xyz.anythings.gw.service.model.MpiOffReq;
import xyz.anythings.gw.service.model.MpiTest;
import xyz.anythings.gw.service.model.MpiTest.MpiAction;
import xyz.anythings.gw.service.model.MpiTest.MpiTarget;
import xyz.anythings.gw.service.util.MpiServiceUtil;
import xyz.anythings.sys.AnyConstants;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.util.FormatUtil;
import xyz.elidom.util.ValueUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/mpi_test")
@ServiceDesc(description = "MPI Test Service API")
public class MpiTestController {
	
	/**
	 * 쿼리 매니저
	 */
	@Autowired
	private IQueryManager queryManager;
	/**
	 * 표시기 요청 관련 서비스
	 */
	@Autowired
	private MpiSendService mpiSendService;

	@RequestMapping(value = "/unit_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "MPI Unit Test")
	public Map<String, Object> unitTest(@RequestBody MpiTest mpiTest) {
		String action = mpiTest.getAction().getAction();
		String sendMsg = null;
		boolean success = true;
		
		if(ValueUtil.isEqualIgnoreCase(action, Action.Values.IndicatorOnRequest)) {
			sendMsg = this.testOn(mpiTest);
			
		} else if (ValueUtil.isEqualIgnoreCase(action, Action.Values.IndicatorOffRequest)) {
			sendMsg = this.testOff(mpiTest);
			
		} else if(ValueUtil.isEqualIgnoreCase(action, Action.Values.LedOnRequest)) {
			sendMsg = this.testLedOn(mpiTest);
			
		} else if(ValueUtil.isEqualIgnoreCase(action, Action.Values.LedOffRequest)) {
			sendMsg = this.testLedOff(mpiTest);
			
		} else {
			success = false;
			sendMsg = "Invalid action!";
		}
		
		return ValueUtil.newMap("success,send_msg", success, sendMsg);
	}

	/**
	 * 표시기 점등 테스트
	 * 
	 * @param mpiTest
	 * @return
	 */
	private String testOn(MpiTest mpiTest) {
		Map<String, List<IndicatorOnInformation>> indOnList = this.createIndOnInfoList(mpiTest);
		
		if(ValueUtil.isNotEmpty(indOnList)) {
			return this.indicatorOnByInfo(Domain.currentDomainId(), mpiTest.getAction().getActionType(), mpiTest.getJobType(), indOnList);
		} else {
			return "표시기 점등할 정보가 없습니다.";
		}
	}
	
	/**
	 * 표시기 소등 테스트
	 * 
	 * @param mpiTest
	 * @return
	 */
	private String testOff(MpiTest mpiTest) {
		List<MpiOffReq> indOffList = this.createIndOffInfoList(mpiTest);
		String msg = null;
		
		if(ValueUtil.isNotEmpty(indOffList)) {
			msg = FormatUtil.toUnderScoreJsonString(indOffList);
			boolean forceFlag = (mpiTest.getAction().getForceFlag() == null) ? false : mpiTest.getAction().getForceFlag().booleanValue();
			this.mpiSendService.requestOffByMpiList(Domain.currentDomainId(), indOffList, forceFlag);
		} else {
			msg = "표시기 소등할 정보가 없습니다.";
		}
		
		return msg;
	}
	
	/**
	 * LED 바 점등 테스트
	 * 
	 * @param mpiTest
	 * @return
	 */
	private String testLedOn(MpiTest mpiTest) {
		List<MpiOffReq> ledOnList = this.createIndOffInfoList(mpiTest);
		
		String msg = null;
		
		if(ValueUtil.isNotEmpty(ledOnList)) {
			msg = FormatUtil.toUnderScoreJsonString(ledOnList);
			this.mpiSendService.requestLedListOn(Domain.currentDomainId(), ledOnList, 10);
		} else {
			msg = "LED 점등할 정보가 없습니다.";
		}
		
		return msg;
	}
	
	/**
	 * LED 바 소등 테스트
	 * 
	 * @param mpiTest
	 * @return
	 */
	private String testLedOff(MpiTest mpiTest) {
		List<MpiOffReq> ledOffList = this.createIndOffInfoList(mpiTest);
		
		String msg = null;
		
		if(ValueUtil.isNotEmpty(ledOffList)) {
			msg = FormatUtil.toUnderScoreJsonString(ledOffList);
			this.mpiSendService.requestLedListOff(Domain.currentDomainId(), ledOffList);
		} else {
			msg = "LED 소등할 정보가 없습니다.";
		}
		
		return msg;
	}
	
	/**
	 * 표시기 점등 ...
	 * 
	 * @param actionType
	 * @param jobType
	 * @param indOnList
	 * @return
	 */
	private String indicatorOnByInfo(Long domainId, String actionType, String jobType, Map<String, List<IndicatorOnInformation>> indOnInfo) {
		if(ValueUtil.isEqualIgnoreCase(actionType, LogisGwConstants.MPI_ACTION_TYPE_PICK)) {
			this.mpiSendService.requestMpisOn(domainId, jobType, actionType, indOnInfo);
			return FormatUtil.toUnderScoreJsonString(indOnInfo);
		} else {
			if(this.indicatorOn(domainId, actionType, jobType, indOnInfo)) {
				return FormatUtil.toUnderScoreJsonString(indOnInfo);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * 표시기 점등
	 * 
	 * @param domainId
	 * @param actionType
	 * @param jobType
	 * @param indOnInfo
	 */
	private boolean indicatorOn(Long domainId, String actionType, String jobType, Map<String, List<IndicatorOnInformation>> indOnInfo) {
		Iterator<String> indOnIter = indOnInfo.keySet().iterator();
		int count = 0;
		
		while(indOnIter.hasNext()) {
			String gwPath = indOnIter.next();
			List<IndicatorOnInformation> infoList = indOnInfo.get(gwPath);
			
			for(IndicatorOnInformation info : infoList) {
				String mpiCd = info.getId();
				
				switch(actionType) {
					case "mpi_cd" : {
					}
					
					case "loc_cd" : {
					}
					
					case LogisGwConstants.MPI_ACTION_TYPE_STR_SHOW : {
						this.mpiSendService.requestShowString(domainId, jobType, gwPath, mpiCd, mpiCd, info.getViewStr());
						count++;
						break;
					}
					
					case LogisGwConstants.MPI_BIZ_FLAG_FULL : {
						this.mpiSendService.requestFullbox(domainId, jobType, mpiCd, mpiCd, info.getColor());
						count++;
						break;
					}
					
					case LogisGwConstants.MPI_BIZ_FLAG_END : {
						this.mpiSendService.requestMpiEndDisplay(domainId, jobType, mpiCd, mpiCd, false);
						count++;
						break;
					}
					
					case LogisGwConstants.MPI_ACTION_TYPE_NOBOX : {
						this.mpiSendService.requestMpiNoBoxDisplay(domainId, jobType, mpiCd);
						count++;
						break;
					}
					
					case LogisGwConstants.MPI_ACTION_TYPE_ERRBOX : {
						this.mpiSendService.requestMpiErrBoxDisplay(domainId, jobType, mpiCd);
						count++;
						break;
					}
					
					case LogisGwConstants.MPI_ACTION_TYPE_DISPLAY : {
						this.mpiSendService.requestDisplayBothDirectionQty(domainId, jobType, mpiCd, mpiCd, info.getOrgRelay(), info.getOrgEaQty());
						count++;
						break;
					}
				}
			}
		}
		
		return count > 0;
	}
	
	/**
	 * 표시기 점등을 위한 모델을 생성한다.
	 * 
	 * @param mpiTest
	 * @return
	 */
	private Map<String, List<IndicatorOnInformation>> createIndOnInfoList(MpiTest mpiTest) {
		MpiAction action = mpiTest.getAction();
		
		switch(action.getActionType()) {
			case LogisGwConstants.MPI_ACTION_TYPE_PICK : {
				return this.createIndOnMpiInfoList(mpiTest);
			}
			
			case LogisGwConstants.MPI_BIZ_FLAG_FULL : {
				return this.createIndOnMpiInfoList(mpiTest);
			}
			
			case LogisGwConstants.MPI_BIZ_FLAG_END : {
				return this.createIndOnMpiInfoList(mpiTest);
			}
			
			case LogisGwConstants.MPI_ACTION_TYPE_NOBOX : {
				return this.createIndOnMpiInfoList(mpiTest);
			}
			
			case LogisGwConstants.MPI_ACTION_TYPE_ERRBOX : {
				return this.createIndOnMpiInfoList(mpiTest);
			}
			
			case LogisGwConstants.MPI_ACTION_TYPE_DISPLAY : {
				return this.createIndOnDisplayInfoList(mpiTest);
			}
			
			case LogisGwConstants.MPI_ACTION_TYPE_STR_SHOW : {
				return this.createIndOnShowStrInfoList(mpiTest);
			}
			
			case "mpi_cd" : {
				return this.createIndOnShowStrInfoList(mpiTest);
			}
			
			case "loc_cd" : {
				return this.createIndOnShowStrInfoList(mpiTest);
			}
		}
		
		return null;
	}
	
	/**
	 * 표시기 점등 (피킹)을 위한 모델을 생성
	 * 
	 * @param mpiTest
	 * @return
	 */
	private Map<String, List<IndicatorOnInformation>> createIndOnMpiInfoList(MpiTest mpiTest) {
		MpiAction action = mpiTest.getAction();
		MpiTarget target = mpiTest.getTarget();
		
		String btnColor = action.getBtnColor();
		Integer firstQty = ValueUtil.toInteger(action.getFirstQty());
		Integer secondQty = ValueUtil.toInteger(action.getSecondQty());
		
		Map<String, Object> params = ValueUtil.newMap(target.getTargetType(), target.getTargetIdList());
		params.put("domainId", Domain.currentDomainId());
		params.put("activeFlag", true);
		List<JobProcess> jobList = this.queryManager.selectListBySql(this.getMpiOnQuery(), params, JobProcess.class, 0, 0);
		
		for(JobProcess job : jobList) {
			job.setId(UUID.randomUUID().toString());
			job.setComCd(mpiTest.getComCd());
			job.setMpiColor(btnColor);
			job.setBoxInQty(1);
			job.setProcessSeq(firstQty);
			job.setPickQty(secondQty);
			job.setPickedQty(0);
		}
		
		return MpiServiceUtil.buildMpiOnList(false, LogisGwConstants.JOB_TYPE_DAS, jobList, false);
	}
	
	/**
	 * 표시기 점등 (문자열 표시)을 위한 모델을 생성
	 * 
	 * @param mpiTest
	 * @return
	 */
	private Map<String, List<IndicatorOnInformation>> createIndOnShowStrInfoList(MpiTest mpiTest) {
		Map<String, List<IndicatorOnInformation>> mpiOnInfoMap = this.createIndOnMpiInfoList(mpiTest);
		Iterator<List<IndicatorOnInformation>> valueIter = mpiOnInfoMap.values().iterator();
		MpiAction action = mpiTest.getAction();
		
		while(valueIter.hasNext()) {
			List<IndicatorOnInformation> indOnInfoList = valueIter.next();
			for(IndicatorOnInformation indOnInfo : indOnInfoList) {
				String viewStr = null;
				
				if(ValueUtil.isEqualIgnoreCase(action.getActionType(), "mpi_cd")) {
					viewStr = indOnInfo.getId();
							
				} else if(ValueUtil.isEqualIgnoreCase(action.getActionType(), "loc_cd")) {
					Location loc = Location.findByMpiCd(Domain.currentDomainId(), indOnInfo.getId(), false, false);
					if(loc != null) {
						String[] locCdArr = loc.getLocCd().split(AnyConstants.DASH);
						String firstData = locCdArr[0];
						String secondData = locCdArr[1];
						firstData = firstData.length() > 3 ? firstData.substring(firstData.length() - 3, firstData.length()) : firstData;
						secondData = secondData.length() > 3 ? secondData.substring(secondData.length() - 3, secondData.length()) : secondData;
						viewStr = StringUtils.leftPad(firstData, 3) + StringUtils.leftPad(secondData, 3);
					} else {
						viewStr = "======";
					}
					
				} else {
					String firstData = ValueUtil.toNotNull(action.getFirstQty());
					String secondData = ValueUtil.toNotNull(action.getSecondQty());	
					viewStr = firstData + secondData;
				}
				
				indOnInfo.setViewStr(viewStr);
			}
		}
		
		return mpiOnInfoMap;
	}
	
	/**
	 * 표시기 점등 (좌측 문자열, 우측 숫자 표시)을 위한 모델을 생성
	 * 
	 * @param mpiTest
	 * @return
	 */
	private Map<String, List<IndicatorOnInformation>> createIndOnDisplayInfoList(MpiTest mpiTest) {
		Map<String, List<IndicatorOnInformation>> mpiOnInfoMap = this.createIndOnMpiInfoList(mpiTest);
		Iterator<List<IndicatorOnInformation>> valueIter = mpiOnInfoMap.values().iterator();
		
		MpiAction action = mpiTest.getAction();
		Integer firstQty = ValueUtil.toInteger(action.getFirstQty());
		Integer secondQty = ValueUtil.toInteger(action.getSecondQty());
		
		while(valueIter.hasNext()) {
			List<IndicatorOnInformation> indOnInfoList = valueIter.next();
			for(IndicatorOnInformation indOnInfo : indOnInfoList) {
				indOnInfo.setOrgRelay(firstQty);
				indOnInfo.setOrgEaQty(secondQty);
			}
		}
		
		return mpiOnInfoMap;
	}
	
	/**
	 * 표시기 소등을 위한 모델을 생성한다.
	 * 
	 * @return
	 */
	private List<MpiOffReq> createIndOffInfoList(MpiTest mpiTest) {
		MpiTarget target = mpiTest.getTarget();
		Map<String, Object> params = ValueUtil.newMap(target.getTargetType(), target.getTargetIdList());
		params.put("domainId", Domain.currentDomainId());
		params.put("activeFlag", true);
		return this.queryManager.selectListBySql(this.getMpiOffQuery(), params, MpiOffReq.class, 0, 0);
	}
	
	/**
	 * MPI 리스트 조회를 위한 쿼리
	 * 
	 * @return
	 */
	private String getMpiOnQuery() {
		StringJoiner sql = new StringJoiner(SysConstants.LINE_SEPARATOR);
		return 
		sql.add("SELECT")
		   .add("	GATE.domain_id, GATE.gw_nm as gw_path, MPI.mpi_cd, LOC.loc_cd as loc_cd")
		   .add("FROM")
		   .add("	TB_LOCATION LOC")
		   .add("	INNER JOIN TB_MPI MPI ON LOC.DOMAIN_ID = MPI.DOMAIN_ID AND LOC.MPI_CD = MPI.MPI_CD")
		   .add("	INNER JOIN TB_GATEWAY GATE ON MPI.DOMAIN_ID = GATE.DOMAIN_ID AND MPI.GW_CD = GATE.GW_CD")
		   .add("WHERE")
		   .add("	LOC.domain_id = :domainId")
		   .add("	AND LOC.active_flag = :activeFlag")
		   .add("	#if($region)")
		   .add("	AND LOC.region_cd in (:region)")
		   .add("	#end")
		   .add("	#if($gateway)")
		   .add("	AND GATE.GW_CD in (:gateway)")
		   .add("	#end")
		   .add("	#if($equip_zone)")
		   .add("	AND LOC.EQUIP_ZONE_CD in (:equip_zone)")
		   .add("	#end")
		   .add("	#if($work_zone)")
		   .add("	AND LOC.ZONE_CD in (:work_zone)")
		   .add("	#end")
		   .add("	#if($location)")
		   .add("	AND LOC.LOC_CD in (:location)")
		   .add("	#end")
		   .add("	#if($indicator)")
		   .add("	AND MPI.MPI_CD in (:indicator)")
		   .add("	#end")		   
		   .add("GROUP BY")
		   .add("	GATE.domain_id, GATE.gw_nm, MPI.mpi_cd, LOC.loc_cd")
		   .add("ORDER BY")
		   .add("	GATE.gw_nm, LOC.loc_cd").toString();
	}
	
	/**
	 * MPI Off를 위한 리스트 조회를 위한 쿼리
	 * 
	 * @return
	 */
	private String getMpiOffQuery() {
		StringJoiner sql = new StringJoiner(SysConstants.LINE_SEPARATOR);
		return 
		sql.add("SELECT")
		   .add("	GATE.gw_nm as gw_path, MPI.mpi_cd, LOC.loc_cd")
		   .add("FROM")
		   .add("	TB_LOCATION LOC")
		   .add("	INNER JOIN TB_MPI MPI ON LOC.DOMAIN_ID = MPI.DOMAIN_ID AND LOC.MPI_CD = MPI.MPI_CD")
		   .add("	INNER JOIN TB_GATEWAY GATE ON MPI.DOMAIN_ID = GATE.DOMAIN_ID AND MPI.GW_CD = GATE.GW_CD")
		   .add("WHERE")
		   .add("	LOC.domain_id = :domainId")
		   .add("	AND LOC.active_flag = :activeFlag")
		   .add("	#if($region)")
		   .add("	AND LOC.region_cd in (:region)")
		   .add("	#end")
		   .add("	#if($gateway)")
		   .add("	AND GATE.GW_CD in (:gateway)")
		   .add("	#end")
		   .add("	#if($equip_zone)")
		   .add("	AND LOC.EQUIP_ZONE_CD in (:equip_zone)")
		   .add("	#end")
		   .add("	#if($work_zone)")
		   .add("	AND LOC.ZONE_CD in (:work_zone)")
		   .add("	#end")
		   .add("	#if($location)")
		   .add("	AND LOC.LOC_CD in (:location)")
		   .add("	#end")
		   .add("	#if($indicator)")
		   .add("	AND MPI.MPI_CD in (:indicator)")
		   .add("	#end")		   
		   .add("GROUP BY")
		   .add("	GATE.gw_nm, MPI.mpi_cd, LOC.loc_cd")
		   .add("ORDER BY")
		   .add("	GATE.gw_nm, LOC.loc_cd").toString();
	}
}
