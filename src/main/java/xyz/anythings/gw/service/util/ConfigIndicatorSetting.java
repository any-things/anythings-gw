package xyz.anythings.gw.service.util;

import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.model.GatewayInitResIndConfig;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.service.model.IndOnPickReq;
import xyz.anythings.sys.AnyConstants;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.util.ValueUtil;

/**
 * 표시기 설정 ID 범위의 표시기 설정 정보
 * 
 * @author shortstop
 */
public class ConfigIndicatorSetting {

	/**
	 * 표시기 설정 ID 범위 내에서 indConfig 값을 조회 후 리턴
	 * 
	 * @param indConfigSetId
	 * @param indConfig
	 * @param deafultValue
	 * @return
	 */
	public static String getIndConfigValueByConfigScope(String indConfigSetId, String indConfig, String deafultValue) {
		//return BeanUtil.get(ConfigSetService.class).getIndConfigValue(batchId, indConfig, deafultValue);
		// TODO
		return null;
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 다음 표시기 버튼 색상을 추출
	 *
	 * @param indConfigSetId
	 * @param currentColor
	 * @return
	 */
	public static String getNextIndColor(String indConfigSetId, String currentColor) {
		String[] colorRotations = getIndColorRotations(indConfigSetId);

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
	 * 표시기 설정 ID 범위 내에서 표시기 색상 로테이션 값 배열
	 *
	 * @param indConfigSetId
	 * @return
	 */
	public static String[] getIndColorRotations(String indConfigSetId) {
		return getIndColorRotationSeq(indConfigSetId).split(SysConstants.COMMA);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 색상 로테이션 값 리턴
	 *
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndColorRotationSeq(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_COLOR_ROTATION_SEQ, RuntimeIndicatorSetting.DEFAULT_ROTATION_SEQ);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 점등을 위한 세그먼트 역할
	 *
	 * @param indConfigSetId
	 * @return
	 */
	public static String[] getIndSegmentRolesOn(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_SEGMENT_ROLE_ON, null);
		return ValueUtil.isEmpty(value) ? null : value.split(SysConstants.COMMA);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 세그먼트 사용 개수
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static int getIndSegmentCount(String indConfigSetId) {
		return getIndSegmentRolesOn(indConfigSetId).length;
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 넘버 표시 정렬
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndNumberAlignment(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_NUMBER_ALIGNMENT, RuntimeIndicatorSetting.IND_NUMBER_ALIGNMENT_LEFT);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 버튼 점등 모드 (B : Blink, S : Static)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndButtonOnMode(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_BUTTON_ON_MODE, RuntimeIndicatorSetting.IND_BUTTON_MODE_BLINK);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 버튼 깜빡임 주기 (100ms)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndButtonBlinkInterval(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_BUTTON_BLINK_INTERVAL, RuntimeIndicatorSetting.DEFAULT_IND_BUTTON_BLINK_INTERVAL);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 점등 전 표시 문자
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndShowStringBeforeOn(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_SHOW_STRING_BEFORE_ON, null);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 점등 전 문자 표시 시간 (100ms)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndShowStringDelayBeforeOn(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_SHOW_STRING_DELAY_BEFORE_ON, RuntimeIndicatorSetting.DEFAULT_IND_SHOW_STRING_DELAY_BEFORE_ON);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 점등 전 딜레이 (1sec)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndDelayBeforeOn(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_DELAY_BEFORE_ON, RuntimeIndicatorSetting.DEFAULT_IND_DELAY_BEFORE_ON);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 취소 버튼 터치시 소등까지 딜레이 (100ms)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndDelayCancelButtonOff(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_DELAY_CANCEL_BUTTON_OFF, RuntimeIndicatorSetting.DEFAULT_IND_DELAY_CANCEL_BUTTON_OFF);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 풀 박스 터치시 버튼 깜빡임 여부 (true / false)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Boolean isIndFullboxButtonBlink(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_FULLBOX_BUTTON_BLINK, AnyConstants.FALSE_STRING);
		return ValueUtil.toBoolean(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 ACK를 응답할 지 여부 (true / false)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Boolean isIndSendOffAckAlreadyOff(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_SEND_OFF_ACK_ALREADY_OFF, AnyConstants.FALSE_STRING);
		return ValueUtil.toBoolean(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 LED 점등 모드 (B: 깜빡임, S: 정지)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndLedOnMode(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_LED_ON_MODE, RuntimeIndicatorSetting.IND_BUTTON_MODE_STOP);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 LED 깜빡임 주기
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndLedBlinkInterval(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_LED_BLINK_INTERVAL, RuntimeIndicatorSetting.DEFAULT_IND_LED_BLINK_INTERVAL);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 LED 바 밝기 정도 (1~10)
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndLedBrightness(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_LED_BRIGHTNESS, RuntimeIndicatorSetting.DEFAULT_IND_LED_BRIGHTNESS);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 View Type
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndDisplayViewType(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_SHOW_VIEW_TYPE, RuntimeIndicatorSetting.DEFAULT_IND_SHOW_VIEW_TYPE);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 상태 보고 주기 
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static Integer getIndHealthPeriod(String indConfigSetId) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_HEALTH_PERIOD, RuntimeIndicatorSetting.DEFAULT_IND_HEALTH_PERIOD);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 표시기 점등 정보로 부터 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param indConfigSetId
	 * @param indOnReq
	 * @param indOnInfo
	 */
	public static void setIndOnQty(String indConfigSetId, IndOnPickReq indOnReq, IndicatorOnInformation indOnInfo) {
		setIndOnQty(indOnInfo, indConfigSetId, indOnReq.getProcessSeq(), indOnReq.getBoxInQty(), indOnReq.getPickQty());
	}

	/**
	 * 표시기 설정 ID 범위 내에서 표시기 점등 옵션으로 표시기 점등 정보 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param indOnInfo
	 * @param indConfigSetId
	 * @param relaySeq
	 * @param boxInQty
	 * @param pickQty
	 */
	public static void setIndOnQty(IndicatorOnInformation indOnInfo, String indConfigSetId, Integer relaySeq, Integer boxInQty, Integer pickQty) {
		String[] onSegments = getIndSegmentRolesOn(indConfigSetId);
		indOnInfo.setSegRole(onSegments);
		
		for(String segment : onSegments) {
			// 세그먼트가 릴레이 번호라면
			if(ValueUtil.isEqualIgnoreCase(segment, RuntimeIndicatorSetting.IND_SEGMENT_ROLE_RELAY_SEQ)) {
				indOnInfo.setOrgRelay(fitRelaySeq(indConfigSetId, relaySeq));

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
	 * 표시기 설정 ID 범위 내에서 릴레이 시퀀스 자리수가 최대 릴레이 값이 넘었다면 1로 리셋하고 그렇지 않으면 릴레이 값을 리턴한다
	 * 
	 * @param indConfigSetId
	 * @param relaySeq
	 * @return
	 */
	public static Integer fitRelaySeq(String indConfigSetId, Integer relaySeq) {
		String value = getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_RELAY_MAX_NO, RuntimeIndicatorSetting.DEFAULT_IND_RELAY_MAX_NO);
		int relayMaxNo = ValueUtil.toInteger(value);
		return (relaySeq > relayMaxNo) ? 1 : relaySeq;
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 작업 유형별 표시기 표현 형식 - 0 : 기본, 1 : 박스 / 낱개, 2 : 누적수량 / 낱개
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndViewType(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_SHOW_VIEW_TYPE, RuntimeIndicatorSetting.DEFAULT_IND_SHOW_VIEW_TYPE);		
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 라우터 펌웨어 최신 릴리즈 버전
	 *
	 * @param indConfigSetId
	 * @return
	 */
	public static String getGwLatestReleaseVersion(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.GW_LATEST_RELEASE_VERSION, RuntimeIndicatorSetting.DEFAULT_GW_LATEST_RELEASE_VERSION);
	}

	/**
	 * 표시기 설정 ID 범위 내에서 표시기 펌웨어 최신 릴리즈 버전
	 *
	 * @param indConfigSetId
	 * @return
	 */
	public static String getIndLatestReleaseVersion(String indConfigSetId) {
		return getIndConfigValueByConfigScope(indConfigSetId, MwConfigConstants.IND_LATEST_RELEASE_VERSION, RuntimeIndicatorSetting.DEFAULT_IND_LATEST_RELEASE_VERSION);
	}
	
	/**
	 * 표시기 설정 ID 범위 내에서 게이트웨이 부트시에 게이트웨이에 내려 줄 부트 설정을 생성하여 리턴 
	 * 
	 * @param indConfigSetId
	 * @return
	 */
	public static GatewayInitResIndConfig getGatewayBootConfig(String indConfigSetId) {
		GatewayInitResIndConfig config = new GatewayInitResIndConfig();
		config.setAlignment(getIndNumberAlignment(indConfigSetId));
		config.setSegRole(getIndSegmentRolesOn(indConfigSetId));
		config.setBtnMode(getIndButtonOnMode(indConfigSetId));
		config.setBtnIntvl(getIndButtonBlinkInterval(indConfigSetId));
		config.setBfOnMsg(getIndShowStringBeforeOn(indConfigSetId));
		config.setBfOnMsgT(getIndShowStringDelayBeforeOn(indConfigSetId));
		config.setBfOnDelay(getIndDelayBeforeOn(indConfigSetId));
		config.setCnclDelay(getIndDelayCancelButtonOff(indConfigSetId));
		config.setBlinkIfFull(isIndFullboxButtonBlink(indConfigSetId));
		config.setOffUseRes(isIndSendOffAckAlreadyOff(indConfigSetId));
		config.setLedBarMode(getIndLedOnMode(indConfigSetId));
		config.setLedBarIntvl(getIndLedBlinkInterval(indConfigSetId));
		config.setLedBarBrtns(getIndLedBrightness(indConfigSetId));
		config.setViewType(getIndViewType(indConfigSetId));
		return config;
	}

}
