package xyz.anythings.gw.service.util;

import xyz.anythings.base.LogisConstants;
import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.model.GatewayInitResIndConfig;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.service.model.IndOnPickReq;
import xyz.anythings.sys.AnyConstants;
import xyz.anythings.sys.entity.ScopeSetting;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.util.ValueUtil;

/**
 * Indicator 하드웨어 설정 
 * 
 * @author shortstop
 */
public class IndicatorSetting {
	/**
	 * Lock
	 */
	private static Object LOCK = new Object();
	/**
	 * 기본 색상 로테이션 순서
	 */
	public static String DEFAULT_ROTATION_SEQ = LogisConstants.COLOR_RED + SysConstants.COMMA + LogisConstants.COLOR_BLUE + SysConstants.COMMA + LogisConstants.COLOR_GREEN + SysConstants.COMMA + LogisConstants.COLOR_YELLOW;

	/**
	 * 표시기 점등을 위한 세그먼트 기본 값
	 */
	public static final String[] DEFAULT_SEGMENT_ROLES_ON = {  IndicatorSetting.IND_SEGMENT_ROLE_PCS  };
	
	/**
	 * 표시기 수량 표시 단위 - 박스 & 낱개
	 */
	public static final String IND_DISPLAY_QTY_UNIT_BOX = "B";
	/**
	 * 표시기 수량 표시 단위 - 낱개
	 */
	public static final String IND_DISPLAY_QTY_UNIT_PCS = "P";
	
	/**
	 * 표시기 세그먼트 : R (릴레이 번호) 
	 */
	public static final String IND_SEGMENT_ROLE_RELAY_SEQ = "R";
	/**
	 * 표시기 세그먼트 : B (Box) 
	 */
	public static final String IND_SEGMENT_ROLE_BOX = "B";
	/**
	 * 표시기 세그먼트 : P (PCS) 
	 */
	public static final String IND_SEGMENT_ROLE_PCS = "P";
	/**
	 * 표시기 세그먼트 : S (문자열 표시) 
	 */
	public static final String IND_SEGMENT_ROLE_STR = "S";
	
	/**
	 * 표시기 숫자 정렬 방식 (L:Left, R:Rear)
	 */
	public static final String IND_NUMBER_ALIGNMENT_LEFT = "L";
	/**
	 * 표시기 숫자 정렬 방식 (L:Left, R:Rear)
	 */
	public static final String IND_NUMBER_ALIGNMENT_RIGHT = "R";
	
	/**
	 * 표시기 버튼 점등 모드 (B:깜빡임, S:정지)
	 */
	public static final String IND_BUTTON_MODE_BLINK = "B";
	/**
	 * 표시기 버튼 점등 모드 (B:깜빡임, S:정지)
	 */
	public static final String IND_BUTTON_MODE_STOP = "S";	
	
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 사용 안 함 표시 (해당 세그먼트에 표시되는 내용이 없음)
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String IND_DISP_SEGMENT_MAPPING_ROLE_NONE = "N";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 총 주문 수량 - T
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String IND_DISP_SEGMENT_MAPPING_ROLE_TOTAL_DONE = "T";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 총 분류할 (남은) 수량 - R
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String IND_DISP_SEGMENT_MAPPING_ROLE_TOTAL_REMAIN = "R";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 총 처리한 수량 - F
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String IND_DISP_SEGMENT_MAPPING_ROLE_TOTAL_FINISHED = "F";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 방금 전 처리한 낱개 수량 - P
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String IND_DISP_SEGMENT_MAPPING_ROLE_PREVIOUS_PICKED_PCS = "P";
	/**
	 * DAS/RTN 분류 처리 후에 표시할 내용 중 분류 처리 정보에 대한 방금 전 처리한 박스 수량 - B
	 * (N: 사용 안 함, T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String IND_DISP_SEGMENT_MAPPING_ROLE_PREVIOUS_PICKED_BOX = "B";
	
	/**
	 * 다음 표시기 버튼 색상을 추출
	 *
	 * @param domainId
	 * @param currentColor
	 * @return
	 */
	public static String getNextIndColor(Long domainId, String currentColor) {
		String[] colorRotations = getIndColorRotations(domainId);

		if(ValueUtil.isEmpty(currentColor)) {
			return colorRotations[0];
		}

		int currentIdx = 0;
		for(int i = 0 ; i < colorRotations.length ; i++) {
			if(ValueUtil.isEqualIgnoreCase(colorRotations[i], currentColor)) {
				currentIdx = i;
				break;
			}
		}

		currentIdx = (currentIdx == (colorRotations.length - 1)) ? 0 : (currentIdx + 1);
		return colorRotations[currentIdx];
	}
	
	/**
	 * 표시기 색상 로테이션 값 배열
	 *
	 * @param domainId
	 * @return
	 */
	public static String[] getIndColorRotations(Long domainId) {
		return getIndColorRotationSeq(domainId).split(SysConstants.COMMA);
	}
	
	/**
	 * 표시기 색상 로테이션 값
	 *
	 * @param domainId
	 * @return
	 */
	public static String getIndColorRotationSeq(Long domainId) {
		if(DEFAULT_ROTATION_SEQ == null) {
			synchronized(LOCK) {
				if(DEFAULT_ROTATION_SEQ == null) {
					DEFAULT_ROTATION_SEQ = LogisConstants.COLOR_RED + SysConstants.COMMA + LogisConstants.COLOR_BLUE + SysConstants.COMMA + LogisConstants.COLOR_GREEN + SysConstants.COMMA + LogisConstants.COLOR_YELLOW;
				}
			}
		}
		
		return SettingUtil.getValue(domainId, MwConfigConstants.IND_COLOR_ROTATION_SEQ, DEFAULT_ROTATION_SEQ);
	}
	
	/**
	 * Gateway 펌웨어 최신 릴리즈 버전
	 *
	 * @param domainId
	 * @return
	 */
	public static String getGwLatestReleaseVersion(Long domainId) {
		return SettingUtil.getValue(domainId, MwConfigConstants.GW_LATEST_RELEASE_VERSION, "1.0.0");
	}

	/**
	 * Indicator 펌웨어 최신 릴리즈 버전
	 *
	 * @param domainId
	 * @return
	 */
	public static String getIndLatestReleaseVersion(Long domainId) {
		return SettingUtil.getValue(domainId, MwConfigConstants.IND_LATEST_RELEASE_VERSION, "1.0.0");
	}
	
	/**
	 * 표시기 점등을 위한 세그먼트 역할
	 *
	 * @param domainId
	 * @param jobType
	 * @return
	 */
	public static String[] getIndSegmentRolesOn(Long domainId, String jobType) {
		ScopeSetting setting = ScopeSetting.findSetting(domainId, "Indicator", jobType, MwConfigConstants.IND_SEGMENT_ROLE_ON);
		return (setting == null) ? null : setting.getValue().split(SysConstants.COMMA);
	}
	
	/**
	 * 표시기 세그먼트 사용 개수
	 * 
	 * @param domainId
	 * @param jobType
	 * @return
	 */
	public static int getIndSegmentCount(Long domainId, String jobType) {
		return getIndSegmentRolesOn(domainId, jobType).length;
	}
	
	/**
	 * 표시기 넘버 표시 정렬
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getIndNumberAlignment(Long domainId) {
		return SettingUtil.getValue(domainId, MwConfigConstants.IND_NUMBER_ALIGNMENT, IND_NUMBER_ALIGNMENT_LEFT);
	}
	
	/**
	 * 표시기 버튼 점등 모드 (B : Blink, S : Static)
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getIndButtonOnMode(Long domainId) {
		return SettingUtil.getValue(domainId, MwConfigConstants.IND_BUTTON_ON_MODE, IND_BUTTON_MODE_BLINK);
	}
	
	/**
	 * 표시기 버튼 깜빡임 주기 (100ms)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndButtonBlinkInterval(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, MwConfigConstants.IND_BUTTON_BLINK_INTERVAL, "300"));
	}
	
	/**
	 * 표시기 점등 전 표시 문자
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getIndShowStringBeforeOn(Long domainId) {
		return SettingUtil.getValue(domainId, MwConfigConstants.IND_SHOW_STRING_BEFORE_ON, null);
	}
	
	/**
	 * 점등 전 문자 표시 시간 (100ms)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndShowStringDelayBeforeOn(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, MwConfigConstants.IND_SHOW_STRING_DELAY_BEFORE_ON, "100"));
	}
	
	/**
	 * 표시기 점등 전 딜레이 (1sec)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndDelayBeforeOn(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, MwConfigConstants.IND_DELAY_BEFORE_ON, "1"));
	}
	
	/**
	 * 표시기 취소 버튼 터치시 소등까지 딜레이 (100ms)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndDelayCancelButtonOff(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, MwConfigConstants.IND_DELAY_CANCEL_BUTTON_OFF, "100"));
	}
	
	/**
	 * 표시기 풀 박스 터치시 버튼 깜빡임 여부 (true / false)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Boolean isIndFullboxButtonBlink(Long domainId) {
		return ValueUtil.toBoolean(SettingUtil.getValue(domainId, MwConfigConstants.IND_FULLBOX_BUTTON_BLINK, AnyConstants.FALSE_STRING));
	}
	
	/**
	 * 표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 ACK를 응답할 지 여부 (true / false)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Boolean isIndSendOffAckAlreadyOff(Long domainId) {
		return ValueUtil.toBoolean(SettingUtil.getValue(domainId, MwConfigConstants.IND_SEND_OFF_ACK_ALREADY_OFF, AnyConstants.FALSE_STRING));
	}
	
	/**
	 * LED 점등 모드 (B: 깜빡임, S: 정지)
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getIndLedOnMode(Long domainId) {
		return SettingUtil.getValue(domainId, MwConfigConstants.IND_LED_ON_MODE, IND_BUTTON_MODE_STOP);
	}
	
	/**
	 * LED 깜빡임 주기
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndLedBlinkInterval(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, MwConfigConstants.IND_LED_BLINK_INTERVAL, "100"));
	}
	
	/**
	 * LED 바 밝기 정도 (1~10)
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndLedBrightness(Long domainId) {
		return ValueUtil.toInteger(SettingUtil.getValue(domainId, MwConfigConstants.IND_LED_BRIGHTNESS, "1"));
	}
	
	/**
	 * 표시기 View Type
	 * 
	 * @param domainId
	 * @return
	 */
	public static String getIndDisplayViewType(Long domainId) {
		return ValueUtil.toString(SettingUtil.getValue(domainId, MwConfigConstants.IND_SHOW_VIEW_TYPE, "0"));
	}
	
	/**
	 * 표시기 상태 보고 주기 
	 * 
	 * @param domainId
	 * @return
	 */
	public static Integer getIndHealthPeriod(Long domainId) {
		return Integer.parseInt(SettingUtil.getValue(domainId, MwConfigConstants.IND_HEALTH_PERIOD, "300"));
	}
	
	/**
	 * mpiOnReq 정보로 부터 mpiOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param domainId
	 * @param jobType
	 * @param mpiOnReq
	 * @param mpiOnInfo
	 */
	public static void setMpiOnQty(Long domainId, String jobType, IndOnPickReq mpiOnReq, IndicatorOnInformation mpiOnInfo) {
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
	 * 작업 유형 (jobType) 따른 표시기 점등 옵션으로 표시기 점등 정보 mpiOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param mpiOnInfo
	 * @param domainId
	 * @param jobType
	 * @param relaySeq
	 * @param boxInQty
	 * @param pickQty
	 */
	public static void setMpiOnQty(IndicatorOnInformation mpiOnInfo, Long domainId, String jobType, Integer relaySeq, Integer boxInQty, Integer pickQty) {
		String[] onSegments = IndicatorSetting.getIndSegmentRolesOn(domainId, jobType);
		mpiOnInfo.setSegRole(onSegments);
		
		for(String segment : onSegments) {
			// 세그먼트가 릴레이 번호라면
			if(ValueUtil.isEqualIgnoreCase(segment, IndicatorSetting.IND_SEGMENT_ROLE_RELAY_SEQ)) {
				mpiOnInfo.setOrgRelay(fitRelaySeq(domainId, onSegments.length, relaySeq));

			// 세그먼트가 박스 수량이라면
			} else if(ValueUtil.isEqualIgnoreCase(segment, IndicatorSetting.IND_SEGMENT_ROLE_BOX)) {
				mpiOnInfo.setOrgBoxQty(0);
				mpiOnInfo.setOrgBoxinQty(boxInQty);

			// 세그먼트가 낱개 수량이라면
			} else if(ValueUtil.isEqualIgnoreCase(segment, IndicatorSetting.IND_SEGMENT_ROLE_PCS)) {
				mpiOnInfo.setOrgEaQty(pickQty);
			}
		}
	}
	
	/**
	 * 세그먼트 개수에 따라 릴레이 시퀀스 자리수를 변경한다.
	 * 
	 * @param domainId
	 * @param segmentCount
	 * @param relaySeq
	 * @return
	 */
	public static Integer fitRelaySeq(Long domainId, int segmentCount, Integer relaySeq) {
		// TODO IND_RELAY_MAX_NO 제약으로 처리 ...
		int limitNo = (segmentCount == 3) ? 100 : 1000;

		if(relaySeq == limitNo) {
			return 0;
			
		} else if(relaySeq > limitNo) {
			String relaySeqStr = relaySeq.toString();
			relaySeqStr = relaySeqStr.substring(relaySeqStr.length() - segmentCount);
			return ValueUtil.toInteger(relaySeqStr);
			
		} else {
			return relaySeq;
		}
	}
	
	/**
	 * 작업 유형별 표시기 표현 형식 - 0 : 기본, 1 : 박스 / 낱개, 2 : 누적수량 / 낱개
	 * 
	 * @param domainId
	 * @param jobType
	 * @return
	 */
	public static String getMpiViewType(Long domainId, String jobType) {
		ScopeSetting setting = ScopeSetting.findSetting(domainId, "Indicator", jobType, "com.mps." + jobType.toLowerCase() + ".mpi.view-type");
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
		config.setAlignment(IndicatorSetting.getIndNumberAlignment(domainId));
		config.setSegRole(IndicatorSetting.getIndSegmentRolesOn(domainId, jobType));
		config.setBtnMode(IndicatorSetting.getIndButtonOnMode(domainId));
		config.setBtnIntvl(getIndButtonBlinkInterval(domainId));
		config.setBfOnMsg(getIndShowStringBeforeOn(domainId));
		config.setBfOnMsgT(getIndShowStringDelayBeforeOn(domainId));
		config.setBfOnDelay(getIndDelayBeforeOn(domainId));
		config.setCnclDelay(getIndDelayCancelButtonOff(domainId));
		config.setBlinkIfFull(isIndFullboxButtonBlink(domainId));
		config.setOffUseRes(isIndSendOffAckAlreadyOff(domainId));
		config.setLedBarMode(getIndLedOnMode(domainId));
		config.setLedBarIntvl(getIndLedBlinkInterval(domainId));
		config.setLedBarBrtns(getIndLedBrightness(domainId));
		config.setViewType(IndicatorSetting.getMpiViewType(domainId, jobType));
		return config;
	}
}
