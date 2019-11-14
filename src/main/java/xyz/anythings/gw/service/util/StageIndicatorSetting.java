package xyz.anythings.gw.service.util;

import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.gw.model.GatewayInitResIndConfig;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.service.model.IndOnPickReq;
import xyz.anythings.sys.AnyConstants;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.util.ValueUtil;

/**
 * 스테이지 범위 내의 표시기 설정 정보
 * 
 * @author shortstop
 */
public class StageIndicatorSetting {
	
	/**
	 * 스테이지 범위 내에서 indConfig 값을 조회 후 리턴
	 * 
	 * @param domainId
	 * @param stageCd
	 * @param indConfig
	 * @param deafultValue
	 * @return
	 */
	public static String getIndConfigValueByStageScope(Long domainId, String stageCd, String indConfig, String deafultValue) {
		//return BeanUtil.get(ConfigSetService.class).getIndConfigValue(batchId, indConfig, deafultValue);
		// TODO
		return null;
	}
	
	/**
	 * 스테이지 범위 내에서 indConfig 값을 조회 후 리턴
	 * 
	 * @param gateway
	 * @param indConfig
	 * @param deafultValue
	 * @return
	 */
	public static String getIndConfigValueByStageScope(Gateway gateway, String indConfig, String deafultValue) {
		return getIndConfigValueByStageScope(gateway.getDomainId(), gateway.getStageCd(), indConfig, deafultValue);
	}
	
	/**
	 * 스테이지 범위 내에서 다음 표시기 버튼 색상을 추출
	 *
	 * @param domainId
	 * @param stageCd
	 * @param currentColor
	 * @return
	 */
	public static String getNextIndColor(Long domainId, String stageCd, String currentColor) {
		String[] colorRotations = getIndColorRotations(domainId, stageCd);

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
	 * 스테이지 범위 내에서 표시기 색상 로테이션 값 배열
	 *
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String[] getIndColorRotations(Long domainId, String stageCd) {
		return getIndColorRotationSeq(domainId, stageCd).split(SysConstants.COMMA);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 색상 로테이션 값 리턴
	 *
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndColorRotationSeq(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_COLOR_ROTATION_SEQ, RuntimeIndicatorSetting.DEFAULT_ROTATION_SEQ);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 점등을 위한 세그먼트 역할
	 *
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String[] getIndSegmentRolesOn(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_SEGMENT_ROLE_ON, null);
		return ValueUtil.isEmpty(value) ? null : value.split(SysConstants.COMMA);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 세그먼트 사용 개수
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static int getIndSegmentCount(Long domainId, String stageCd) {
		return getIndSegmentRolesOn(domainId, stageCd).length;
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 넘버 표시 정렬
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndNumberAlignment(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_NUMBER_ALIGNMENT, RuntimeIndicatorSetting.IND_NUMBER_ALIGNMENT_LEFT);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 버튼 점등 모드 (B : Blink, S : Static)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndButtonOnMode(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_BUTTON_ON_MODE, RuntimeIndicatorSetting.IND_BUTTON_MODE_BLINK);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 버튼 깜빡임 주기 (100ms)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndButtonBlinkInterval(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_BUTTON_BLINK_INTERVAL, RuntimeIndicatorSetting.DEFAULT_IND_BUTTON_BLINK_INTERVAL);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 점등 전 표시 문자
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndShowStringBeforeOn(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_SHOW_STRING_BEFORE_ON, null);
	}
	
	/**
	 * 스테이지 범위 내에서 점등 전 문자 표시 시간 (100ms)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndShowStringDelayBeforeOn(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_SHOW_STRING_DELAY_BEFORE_ON, RuntimeIndicatorSetting.DEFAULT_IND_SHOW_STRING_DELAY_BEFORE_ON);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 점등 전 딜레이 (1sec)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndDelayBeforeOn(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_DELAY_BEFORE_ON, RuntimeIndicatorSetting.DEFAULT_IND_DELAY_BEFORE_ON);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 취소 버튼 터치시 소등까지 딜레이 (100ms)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndDelayCancelButtonOff(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_DELAY_CANCEL_BUTTON_OFF, RuntimeIndicatorSetting.DEFAULT_IND_DELAY_CANCEL_BUTTON_OFF);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 풀 박스 터치시 버튼 깜빡임 여부 (true / false)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Boolean isIndFullboxButtonBlink(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_FULLBOX_BUTTON_BLINK, AnyConstants.FALSE_STRING);
		return ValueUtil.toBoolean(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 ACK를 응답할 지 여부 (true / false)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Boolean isIndSendOffAckAlreadyOff(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_SEND_OFF_ACK_ALREADY_OFF, AnyConstants.FALSE_STRING);
		return ValueUtil.toBoolean(value);
	}
	
	/**
	 * 스테이지 범위 내에서 LED 점등 모드 (B: 깜빡임, S: 정지)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndLedOnMode(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_LED_ON_MODE, RuntimeIndicatorSetting.IND_BUTTON_MODE_STOP);
	}
	
	/**
	 * 스테이지 범위 내에서 LED 깜빡임 주기
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndLedBlinkInterval(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_LED_BLINK_INTERVAL, RuntimeIndicatorSetting.DEFAULT_IND_LED_BLINK_INTERVAL);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 LED 바 밝기 정도 (1~10)
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndLedBrightness(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_LED_BRIGHTNESS, RuntimeIndicatorSetting.DEFAULT_IND_LED_BRIGHTNESS);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 View Type
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndDisplayViewType(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_SHOW_VIEW_TYPE, RuntimeIndicatorSetting.DEFAULT_IND_SHOW_VIEW_TYPE);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 상태 보고 주기 
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static Integer getIndHealthPeriod(Long domainId, String stageCd) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_HEALTH_PERIOD, RuntimeIndicatorSetting.DEFAULT_IND_HEALTH_PERIOD);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 스테이지 범위 내에서 표시기 점등 정보로 부터 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param domainId
	 * @param stageCd
	 * @param indOnReq
	 * @param indOnInfo
	 */
	public static void setIndOnQty(Long domainId, String stageCd, IndOnPickReq indOnReq, IndicatorOnInformation indOnInfo) {
		setIndOnQty(indOnInfo, domainId, stageCd, indOnReq.getProcessSeq(), indOnReq.getBoxInQty(), indOnReq.getPickQty());
	}

	/**
	 * 스테이지 범위 내에서 표시기 점등 옵션으로 표시기 점등 정보 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param indOnInfo
	 * @param domainId
	 * @param stageCd
	 * @param relaySeq
	 * @param boxInQty
	 * @param pickQty
	 */
	public static void setIndOnQty(IndicatorOnInformation indOnInfo, Long domainId, String stageCd, Integer relaySeq, Integer boxInQty, Integer pickQty) {
		String[] onSegments = getIndSegmentRolesOn(domainId, stageCd);
		indOnInfo.setSegRole(onSegments);
		
		for(String segment : onSegments) {
			// 세그먼트가 릴레이 번호라면
			if(ValueUtil.isEqualIgnoreCase(segment, RuntimeIndicatorSetting.IND_SEGMENT_ROLE_RELAY_SEQ)) {
				indOnInfo.setOrgRelay(fitRelaySeq(domainId, stageCd, relaySeq));

			// 세그먼트가 박스 수량이라면
			} else if(ValueUtil.isEqualIgnoreCase(segment, RuntimeIndicatorSetting.IND_SEGMENT_ROLE_BOX)) {
				indOnInfo.setOrgBoxQty(0);
				indOnInfo.setOrgBoxinQty(boxInQty);

			// 세그먼트가 낱개 수량이라면
			} else if(ValueUtil.isEqualIgnoreCase(segment, RuntimeIndicatorSetting.IND_SEGMENT_ROLE_PCS)) {
				indOnInfo.setOrgEaQty(pickQty);
			}
		}
	}
		
	/**
	 * 세그먼트 개수에 따라 릴레이 시퀀스 자리수를 변경한다.
	 * 
	 * @param domainId
	 * @param stageCd
	 * @param relaySeq
	 * @return
	 */
	public static Integer fitRelaySeq(Long domainId, String stageCd, Integer relaySeq) {
		String value = getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_RELAY_MAX_NO, RuntimeIndicatorSetting.DEFAULT_IND_RELAY_MAX_NO);
		int relayMaxNo = ValueUtil.toInteger(value);
		return (relaySeq > relayMaxNo) ? 1 : relaySeq;
	}
	
	/**
	 * 스테이지 코드 범위 내에서 작업 유형별 표시기 표현 형식 - 0 : 기본, 1 : 박스 / 낱개, 2 : 누적수량 / 낱개
	 * 
	 * @param domainId
	 * @param stageCd
	 * @return
	 */
	public static String getIndViewType(Long domainId, String stageCd) {
		return getIndConfigValueByStageScope(domainId, stageCd, MwConfigConstants.IND_SHOW_VIEW_TYPE, RuntimeIndicatorSetting.DEFAULT_IND_SHOW_VIEW_TYPE);		
	}
	
	/**
	 * Gateway 펌웨어 최신 릴리즈 버전
	 *
	 * @param gateway
	 * @return
	 */
	public static String getGwLatestReleaseVersion(Gateway gateway) {
		String version = gateway.getVersion();
		
		if(ValueUtil.isEmpty(version)) {
			return getIndConfigValueByStageScope(gateway.getDomainId(), gateway.getStageCd(), MwConfigConstants.GW_LATEST_RELEASE_VERSION, RuntimeIndicatorSetting.DEFAULT_GW_LATEST_RELEASE_VERSION);
		} else {
			return version;
		}
	}

	/**
	 * Indicator 펌웨어 최신 릴리즈 버전
	 *
	 * @param gateway
	 * @return
	 */
	public static String getIndLatestReleaseVersion(Gateway gateway) {
		return getIndConfigValueByStageScope(gateway.getDomainId(), gateway.getStageCd(), MwConfigConstants.IND_LATEST_RELEASE_VERSION, RuntimeIndicatorSetting.DEFAULT_IND_LATEST_RELEASE_VERSION);
	}
	
	/**
	 * 게이트웨이 부트시에 게이트웨이에 내려 줄 부트 설정을 생성하여 리턴 
	 * 
	 * @param gateway
	 * @return
	 */
	public static GatewayInitResIndConfig getGatewayBootConfig(Gateway gateway) {
		Long domainId = gateway.getDomainId();
		String stageCd = gateway.getStageCd();
		
		GatewayInitResIndConfig config = new GatewayInitResIndConfig();
		config.setAlignment(getIndNumberAlignment(domainId, stageCd));
		config.setSegRole(getIndSegmentRolesOn(domainId, stageCd));
		config.setBtnMode(getIndButtonOnMode(domainId, stageCd));
		config.setBtnIntvl(getIndButtonBlinkInterval(domainId, stageCd));
		config.setBfOnMsg(getIndShowStringBeforeOn(domainId, stageCd));
		config.setBfOnMsgT(getIndShowStringDelayBeforeOn(domainId, stageCd));
		config.setBfOnDelay(getIndDelayBeforeOn(domainId, stageCd));
		config.setCnclDelay(getIndDelayCancelButtonOff(domainId, stageCd));
		config.setBlinkIfFull(isIndFullboxButtonBlink(domainId, stageCd));
		config.setOffUseRes(isIndSendOffAckAlreadyOff(domainId, stageCd));
		config.setLedBarMode(getIndLedOnMode(domainId, stageCd));
		config.setLedBarIntvl(getIndLedBlinkInterval(domainId, stageCd));
		config.setLedBarBrtns(getIndLedBrightness(domainId, stageCd));
		config.setViewType(getIndViewType(domainId, stageCd));
		
		return config;
	}

}
