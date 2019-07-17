package xyz.anythings.gw.service.util;

import xyz.anythings.gw.LogisGwConfigConstants;
import xyz.anythings.gw.model.GatewayInitResIndConfig;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.service.model.MpiOnPickReq;
import xyz.anythings.sys.AnyConstants;
import xyz.anythings.sys.entity.CompanySetting;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.util.ValueUtil;

/**
 * 표시기 하드웨어 설정 
 * 
 * @author shortstop
 */
public class MpiSetting {
	/**
	 * 표시기 점등을 위한 세그먼트 기본 값
	 */
	public static final String[] DEFAULT_SEGMENT_ROLES_ON = {  MpiSetting.MPI_SEGMENT_ROLE_PCS  };
	
	/**
	 * 표시기 수량 표시 단위 - 박스 & 낱개
	 */
	public static final String MPI_DISPLAY_QTY_UNIT_BOX = "B";
	/**
	 * 표시기 수량 표시 단위 - 낱개
	 */
	public static final String MPI_DISPLAY_QTY_UNIT_PCS = "P";
	
	/**
	 * 표시기 세그먼트 : R (릴레이 번호) 
	 */
	public static final String MPI_SEGMENT_ROLE_RELAY_SEQ = "R";
	/**
	 * 표시기 세그먼트 : B (Box) 
	 */
	public static final String MPI_SEGMENT_ROLE_BOX = "B";
	/**
	 * 표시기 세그먼트 : P (PCS) 
	 */
	public static final String MPI_SEGMENT_ROLE_PCS = "P";
	/**
	 * 표시기 세그먼트 : S (문자열 표시) 
	 */
	public static final String MPI_SEGMENT_ROLE_STR = "S";
	
	/**
	 * 표시기 숫자 정렬 방식 (L:Left, R:Rear)
	 */
	public static final String MPI_NUMBER_ALIGNMENT_LEFT = "L";
	/**
	 * 표시기 숫자 정렬 방식 (L:Left, R:Rear)
	 */
	public static final String MPI_NUMBER_ALIGNMENT_RIGHT = "R";
	
	/**
	 * 표시기 버튼 점등 모드 (B:깜빡임, S:정지)
	 */
	public static final String MPI_BUTTON_MODE_BLINK = "B";
	/**
	 * 표시기 버튼 점등 모드 (B:깜빡임, S:정지)
	 */
	public static final String MPI_BUTTON_MODE_STOP = "S";	
	
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 사용 안 함 표시 (해당 세그먼트에 표시되는 내용이 없음)
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String MPI_DISP_SEGMENT_MAPPING_ROLE_NONE = "N";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 총 주문 수량 - T
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String MPI_DISP_SEGMENT_MAPPING_ROLE_TOTAL_DONE = "T";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 총 분류할 (남은) 수량 - R
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String MPI_DISP_SEGMENT_MAPPING_ROLE_TOTAL_REMAIN = "R";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 총 처리한 수량 - F
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String MPI_DISP_SEGMENT_MAPPING_ROLE_TOTAL_FINISHED = "F";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 방금 전 처리한 낱개 수량 - P
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String MPI_DISP_SEGMENT_MAPPING_ROLE_PREVIOUS_PICKED_PCS = "P";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 방금 전 처리한 박스 수량 - B
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String MPI_DISP_SEGMENT_MAPPING_ROLE_PREVIOUS_PICKED_BOX = "B";
	
	/**
	 * Gateway 펌웨어 최신 릴리즈 버전
	 *
	 * @param domainId
	 * @return
	 */
	public static String getGatewayLatestReleaseVersion(Long domainId) {
		return SettingUtil.getValue(domainId, LogisGwConfigConstants.GATEWAY_LATEST_RELEASE_VERSION, "1.0.0");
	}

	/**
	 * Indicator 펌웨어 최신 릴리즈 버전
	 *
	 * @param domainId
	 * @return
	 */
	public static String getMpiLatestReleaseVersion(Long domainId) {
		return SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_LATEST_RELEASE_VERSION, "1.0.0");
	}
	
	/**
	 * 점등 세그먼트 역할
	 * 
	 * @param domainId
	 * @return
	 */
	public static String[] getMpiSegmentRolesOn(Long domainId) {
		String segmentRolesOn = SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_SEGMENT_ROLE_ON);
		return ValueUtil.isEmpty(segmentRolesOn) ? DEFAULT_SEGMENT_ROLES_ON : segmentRolesOn.split(SysConstants.COMMA);
	}
	
	/**
	 * 세그먼트 사용 개수
	 * 
	 * @param domainId
	 * @return
	 */
	public static int getMpiSegmentCount(Long domainId) {
		return getMpiSegmentRolesOn(domainId).length;
	}
	
	/**
	 * 표시기 넘버 표시 정렬
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getMpiNumberAlignment(Long domainId) {
		return SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_NUMBER_ALIGNMENT, MPI_NUMBER_ALIGNMENT_LEFT);
	}
	
	/**
	 * 표시기 버튼 점등 모드 (B,S)
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getMpiButtonOnMode(Long domainId) {
		return SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_BUTTON_ON_MODE, MPI_BUTTON_MODE_BLINK);
	}
	
	/**
	 * 표시기 버튼 깜빡임 주기 (100ms)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiButtonBlinkInterval(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_BUTTON_BLINK_INTERVAL, "300"));
	}
	
	/**
	 * 표시기 점등 전 표시 문자
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getMpiShowCharactersBeforeOn(Long domainId) {
		return SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_SHOW_CHARS_BEFORE_ON, null);
	}
	
	/**
	 * 점등 전 문자 표시 시간 (100ms)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiShowCharactersDelayBeforeOn(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_SHOW_CHARS_DELAY_BEFORE_ON, "100"));
	}
	
	/**
	 * 표시기 점등 전 딜레이 (1sec)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiDelayBeforeOn(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_DELAY_BEFORE_ON, "1"));
	}
	
	/**
	 * 표시기 취소 버튼 터치시 소등까지 딜레이 (100ms)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiDelayCancelButtonOff(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_DELAY_CANCEL_BUTTON_OFF, "100"));
	}
	
	/**
	 * 표시기 Full Box 터치시 버튼 깜빡임 여부 (true / false)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Boolean isMpiFullboxButtonBlink(Long domainId) {
		return ValueUtil.toBoolean(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_FULLBOX_BUTTON_BLINK, AnyConstants.FALSE_STRING));
	}
	
	/**
	 * 표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 ACK를 응답할 지 여부 (true / false)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Boolean isMpiSendOffAckAlreadyOff(Long domainId) {
		return ValueUtil.toBoolean(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_SEND_OFF_ACK_ALREADY_OFF, AnyConstants.FALSE_STRING));
	}
	
	/**
	 * LED 바 점등 모드 (B:깜빡임, S:정지)
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getMpiLedBarOnMode(Long domainId) {
		return SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_LEDBAR_ON_MODE, MPI_BUTTON_MODE_STOP);
	}
	
	/**
	 * LED 바 깜빡임 주기
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiLedBarBlinkInterval(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_LEDBAR_BLINK_INTERVAL, "100"));
	}
	
	/**
	 * LED 바 밝기 정도 (1~10)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiLedBarBrightness(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_LEDBAR_BRIGHTNESS, "1"));
	}
	
	/**
	 * 표시기 View Type
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getMpiDisplayViewType(Long domainId) {
		return ValueUtil.toString(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_DISPLAY_MPI_VIEW_TYPE, "0"));
	}
	
	/**
	 * 표시기 상태 보고 주기 
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getMpiHealthPeriod(Long domainId) {
		return Integer.parseInt(SettingUtil.getValue(domainId, LogisGwConfigConstants.MPI_HEALTH_PERIOD, "300"));
	}
	
	/**
	 * 표시기 점등을 위한 세그먼트 역할
	 *
	 * @param domainId
	 * @param comCd
	 * @return
	 */
	public static String[] getMpiSegmentRolesOn(Long domainId, String comCd) {
		CompanySetting setting = CompanySetting.findSetting(domainId, comCd, LogisGwConfigConstants.COM_MPI_SEGMENT_ROLE_ON);
		return (setting == null) ? MpiSetting.getMpiSegmentRolesOn(domainId) : setting.getValue().split(SysConstants.COMMA);
	}
	
	/**
	 * mpiOnReq 정보로 부터 mpiOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param domainId
	 * @param jobType
	 * @param mpiOnReq
	 * @param mpiOnInfo
	 */
	public static void setMpiOnQty(Long domainId, String jobType, MpiOnPickReq mpiOnReq, IndicatorOnInformation mpiOnInfo) {
		setMpiOnQty(mpiOnInfo, domainId, mpiOnReq.getComCd(), mpiOnReq.getProcessSeq(), mpiOnReq.getBoxInQty(), mpiOnReq.getPickQty());
	}

	/**
	 * 표시기 점등 정보 mpiOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param mpiOnInfo
	 * @param relaySeq
	 * @param boxQty
	 * @param eaQty
	 */
	public static void setMpiOnQty(IndicatorOnInformation mpiOnInfo, Integer relaySeq, Integer boxQty, Integer eaQty) {
		mpiOnInfo.setOrgRelay(relaySeq);
		mpiOnInfo.setOrgBoxQty(boxQty);
		mpiOnInfo.setOrgEaQty(eaQty);
	}

	/**
	 * 고객사 코드(comCd)에 따른 표시기 점등 옵션으로 표시기 점등 정보 mpiOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param mpiOnInfo
	 * @param domainId
	 * @param comCd
	 * @param processSeq
	 * @param boxInQty
	 * @param pickQty
	 */
	public static void setMpiOnQty(IndicatorOnInformation mpiOnInfo, Long domainId, String comCd, Integer processSeq, Integer boxInQty, Integer pickQty) {
		String[] onSegments = MpiSetting.getMpiSegmentRolesOn(domainId, comCd);
		mpiOnInfo.setSegRole(onSegments);
		
		for(String segment : onSegments) {
			// 세그먼트가 릴레이 번호라면
			if(ValueUtil.isEqualIgnoreCase(segment, MpiSetting.MPI_SEGMENT_ROLE_RELAY_SEQ)) {
				mpiOnInfo.setOrgRelay(fitRelaySeq(onSegments.length, processSeq));

			// 세그먼트가 박스 수량이라면
			} else if(ValueUtil.isEqualIgnoreCase(segment, MpiSetting.MPI_SEGMENT_ROLE_BOX)) {
				mpiOnInfo.setOrgBoxQty(0);
				mpiOnInfo.setOrgBoxinQty(boxInQty);

			// 세그먼트가 낱개 수량이라면
			} else if(ValueUtil.isEqualIgnoreCase(segment, MpiSetting.MPI_SEGMENT_ROLE_PCS)) {
				mpiOnInfo.setOrgEaQty(pickQty);
			}
		}
	}
	
	/**
	 * 세그먼트 개수에 따라 릴레이 시퀀스 자리수를 변경한다.
	 *
	 * @param segmentCount
	 * @param processSeq
	 * @return
	 */
	public static Integer fitRelaySeq(int segmentCount, Integer processSeq) {
		int limitNo = (segmentCount == 3) ? 100 : 1000;

		if(processSeq == limitNo) {
			return 0;
		} else if(processSeq > limitNo) {
			String relaySeqStr = processSeq.toString();
			relaySeqStr = relaySeqStr.substring(relaySeqStr.length() - segmentCount);
			return ValueUtil.toInteger(relaySeqStr);
		} else {
			return processSeq;
		}
	}
	
	/**
	 * TODO 이 부분을 모듈화해야 함 -> 각 모듈에서 Override할 수 있도록
	 * 작업 유형별 표시기 표현 형식 - 0 : 기본, 1 : 박스 / 낱개, 2 : 누적수량 / 낱개
	 * 
	 * @param domainId
	 * @param comCd
	 * @param jobType
	 * @return
	 */
	public static String getMpiViewType(Long domainId, String comCd, String jobType) {
		CompanySetting setting = CompanySetting.findSetting(domainId, comCd, "com.mps." + jobType.toLowerCase() + ".mpi.view-type");
		return (setting == null || ValueUtil.isEmpty(setting.getValue())) ? "0" : setting.getValue();
	}
	
	/**
	 * 게이트웨이 부트시에 게이트웨이에 내려 줄 부트 설정을 생성하여 리턴 
	 * 
	 * @param domainId
	 * @param jobType
	 * @return
	 */
	public static GatewayInitResIndConfig getGatewayBootConfig(Long domainId, String jobType) {
		GatewayInitResIndConfig config = new GatewayInitResIndConfig();
		config.setAlignment(MpiSetting.getMpiNumberAlignment(domainId));
		config.setSegRole(MpiSetting.getMpiSegmentRolesOn(domainId));
		config.setBtnMode(MpiSetting.getMpiButtonOnMode(domainId));
		config.setBtnIntvl(getMpiButtonBlinkInterval(domainId));
		config.setBfOnMsg(getMpiShowCharactersBeforeOn(domainId));
		config.setBfOnMsgT(getMpiShowCharactersDelayBeforeOn(domainId));
		config.setBfOnDelay(getMpiDelayBeforeOn(domainId));
		config.setCnclDelay(getMpiDelayCancelButtonOff(domainId));
		config.setBlinkIfFull(isMpiFullboxButtonBlink(domainId));
		config.setOffUseRes(isMpiSendOffAckAlreadyOff(domainId));
		config.setLedBarMode(getMpiLedBarOnMode(domainId));
		config.setLedBarIntvl(getMpiLedBarBlinkInterval(domainId));
		config.setLedBarBrtns(getMpiLedBarBrightness(domainId));
		config.setViewType(MpiSetting.getMpiViewType(domainId, null, jobType));
		return config;
	}
}
