package xyz.anythings.gw.service.util;

import xyz.anythings.base.LogisConstants;
import xyz.anythings.base.entity.Gateway;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.service.impl.ConfigSetService;
import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.model.GatewayInitResIndConfig;
import xyz.anythings.gw.model.IndicatorOnInformation;
import xyz.anythings.gw.service.model.IndOnPickReq;
import xyz.anythings.sys.AnyConstants;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.ValueUtil;

/**
 * 분류 실행 타임의 표시기 설정 정보 
 * 
 * @author shortstop
 */
public class RuntimeIndicatorSetting {
	/**
	 * 기본 색상 로테이션 순서
	 */
	public static String DEFAULT_ROTATION_SEQ = LogisConstants.COLOR_RED + SysConstants.COMMA + LogisConstants.COLOR_BLUE + SysConstants.COMMA + LogisConstants.COLOR_GREEN + SysConstants.COMMA + LogisConstants.COLOR_YELLOW;

	/**
	 * 표시기 점등을 위한 세그먼트 기본 값
	 */
	public static final String[] DEFAULT_SEGMENT_ROLES_ON = {  RuntimeIndicatorSetting.IND_SEGMENT_ROLE_PCS  };
	
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
	 * 표시기 버튼 깜빡임 주기 기본값 (300ms)
	 */
	public static final String DEFAULT_IND_BUTTON_BLINK_INTERVAL = "300";
	/**
	 * 점등 전 문자 표시 시간 기본값 (100ms)
	 */
	public static final String DEFAULT_IND_SHOW_STRING_DELAY_BEFORE_ON = "100";
	/**
	 * 표시기 점등 전 딜레이 기본값 (1sec)
	 */
	public static final String DEFAULT_IND_DELAY_BEFORE_ON = "1";
	/**
	 * 표시기 취소 버튼 터치시 소등까지 딜레이 기본값 (100ms)
	 */
	public static final String DEFAULT_IND_DELAY_CANCEL_BUTTON_OFF = "100";
	/**
	 *  LED 깜빡임 주기 기본값 (100ms)
	 */
	public static final String DEFAULT_IND_LED_BLINK_INTERVAL = "100";
	/**
	 * LED 바 밝기 정도 (1~10) 기본값 1
	 */
	public static final String DEFAULT_IND_LED_BRIGHTNESS = "1";
	/**
	 * 표시기 View Type 기본값 0
	 */
	public static final String DEFAULT_IND_SHOW_VIEW_TYPE = "0";
	/**
	 * 표시기 상태 보고 주기 기본값 300
	 */
	public static final String DEFAULT_IND_HEALTH_PERIOD = "300";
	/**
	 * 릴레이 시퀀스 최재 자리수 기본값 99 
	 */
	public static final String DEFAULT_IND_RELAY_MAX_NO = "99";
	/**
	 * 게이트웨이 펌웨어 최신 릴리즈 버전 기본값
	 */
	public static final String DEFAULT_GW_LATEST_RELEASE_VERSION = "1.0.0";
	/**
	 * 표시기 펌웨어 최신 릴리즈 버전 기본값
	 */
	public static final String DEFAULT_IND_LATEST_RELEASE_VERSION = "1.0.0";
	
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
	
	/****************************************************************************************************************
	 *												작업 배치 범위 내에서 설정 조회  
	 ****************************************************************************************************************/
	
	/**
	 * 작업 배치 범위 내에서 indConfig 값을 조회 후 리턴
	 * 
	 * @param batch
	 * @param indConfig
	 * @param deafultValue
	 * @return
	 */
	public static String getIndConfigValueByBatchScope(JobBatch batch, String indConfig, String deafultValue) {
		return BeanUtil.get(ConfigSetService.class).getIndConfigValue(batch.getId(), indConfig, deafultValue);
	}
	
	/**
	 * 작업 배치 범위 내에서 indConfig 값을 조회 후 리턴
	 * 
	 * @param batchId
	 * @param indConfig
	 * @param deafultValue
	 * @return
	 */
	public static String getIndConfigValueByBatchScope(String batchId, String indConfig, String deafultValue) {
		return BeanUtil.get(ConfigSetService.class).getIndConfigValue(batchId, indConfig, deafultValue);
	}
	
	/**
	 * 작업 배치 범위 내에서 다음 표시기 버튼 색상을 추출
	 *
	 * @param batch
	 * @param currentColor
	 * @return
	 */
	public static String getNextIndColor(JobBatch batch, String currentColor) {
		String[] colorRotations = getIndColorRotations(batch);

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
	 * 작업 배치 범위 내에서 표시기 색상 로테이션 값 배열
	 *
	 * @param batch
	 * @return
	 */
	public static String[] getIndColorRotations(JobBatch batch) {
		return getIndColorRotationSeq(batch).split(SysConstants.COMMA);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 색상 로테이션 값 리턴
	 *
	 * @param batch
	 * @return
	 */
	public static String getIndColorRotationSeq(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch.getId(), MwConfigConstants.IND_COLOR_ROTATION_SEQ, DEFAULT_ROTATION_SEQ);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 점등을 위한 세그먼트 역할
	 *
	 * @param batch
	 * @return
	 */
	public static String[] getIndSegmentRolesOn(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch.getId(), MwConfigConstants.IND_SEGMENT_ROLE_ON, null);
		return ValueUtil.isEmpty(value) ? null : value.split(SysConstants.COMMA);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 세그먼트 사용 개수
	 * 
	 * @param batch
	 * @return
	 */
	public static int getIndSegmentCount(JobBatch batch) {
		return getIndSegmentRolesOn(batch).length;
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 넘버 표시 정렬
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndNumberAlignment(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_NUMBER_ALIGNMENT, IND_NUMBER_ALIGNMENT_LEFT);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 버튼 점등 모드 (B : Blink, S : Static)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndButtonOnMode(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_BUTTON_ON_MODE, IND_BUTTON_MODE_BLINK);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 버튼 깜빡임 주기 (300ms)
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndButtonBlinkInterval(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_BUTTON_BLINK_INTERVAL, DEFAULT_IND_BUTTON_BLINK_INTERVAL);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 점등 전 표시 문자
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndShowStringBeforeOn(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_SHOW_STRING_BEFORE_ON, null);
	}
	
	/**
	 * 작업 배치 범위 내에서 점등 전 문자 표시 시간 (100ms)
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndShowStringDelayBeforeOn(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_SHOW_STRING_DELAY_BEFORE_ON, DEFAULT_IND_SHOW_STRING_DELAY_BEFORE_ON);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 점등 전 딜레이 (1sec)
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndDelayBeforeOn(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_DELAY_BEFORE_ON, DEFAULT_IND_DELAY_BEFORE_ON);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 취소 버튼 터치시 소등까지 딜레이 (100ms)
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndDelayCancelButtonOff(JobBatch batch) {
				String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_DELAY_CANCEL_BUTTON_OFF, DEFAULT_IND_DELAY_CANCEL_BUTTON_OFF);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 풀 박스 터치시 버튼 깜빡임 여부 (true / false)
	 * 
	 * @param batch
	 * @return
	 */
	public static Boolean isIndFullboxButtonBlink(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_FULLBOX_BUTTON_BLINK, AnyConstants.FALSE_STRING);
		return ValueUtil.toBoolean(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 ACK를 응답할 지 여부 (true / false)
	 * 
	 * @param batch
	 * @return
	 */
	public static Boolean isIndSendOffAckAlreadyOff(JobBatch batch) {
				String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_SEND_OFF_ACK_ALREADY_OFF, AnyConstants.FALSE_STRING);
		return ValueUtil.toBoolean(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 LED 점등 모드 (B: 깜빡임, S: 정지)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndLedOnMode(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_LED_ON_MODE, IND_BUTTON_MODE_STOP);
	}
	
	/**
	 * 작업 배치 범위 내에서 LED 깜빡임 주기
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndLedBlinkInterval(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_LED_BLINK_INTERVAL, DEFAULT_IND_LED_BLINK_INTERVAL);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 LED 바 밝기 정도 (1~10)
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndLedBrightness(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_LED_BRIGHTNESS, DEFAULT_IND_LED_BRIGHTNESS);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 View Type
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndDisplayViewType(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_SHOW_VIEW_TYPE, DEFAULT_IND_SHOW_VIEW_TYPE);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 상태 보고 주기 
	 * 
	 * @param batch
	 * @return
	 */
	public static Integer getIndHealthPeriod(JobBatch batch) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_HEALTH_PERIOD, DEFAULT_IND_HEALTH_PERIOD);
		return ValueUtil.toInteger(value);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 점등 정보로 부터 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param batch
	 * @param indOnReq
	 * @param indOnInfo
	 */
	public static void setIndOnQty(JobBatch batch, IndOnPickReq indOnReq, IndicatorOnInformation indOnInfo) {
		setIndOnQty(indOnInfo, batch, indOnReq.getProcessSeq(), indOnReq.getBoxInQty(), indOnReq.getPickQty());
	}

	/**
	 * 작업 배치 범위 내에서 표시기 점등 옵션으로 표시기 점등 정보 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param indOnInfo
	 * @param batch
	 * @param relaySeq
	 * @param boxInQty
	 * @param pickQty
	 */
	public static void setIndOnQty(IndicatorOnInformation indOnInfo, JobBatch batch, Integer relaySeq, Integer boxInQty, Integer pickQty) {
		String[] onSegments = RuntimeIndicatorSetting.getIndSegmentRolesOn(batch);
		indOnInfo.setSegRole(onSegments);
		
		for(String segment : onSegments) {
			// 세그먼트가 릴레이 번호라면
			if(ValueUtil.isEqualIgnoreCase(segment, RuntimeIndicatorSetting.IND_SEGMENT_ROLE_RELAY_SEQ)) {
				indOnInfo.setOrgRelay(fitRelaySeq(batch, relaySeq));

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
	 * 릴레이 시퀀스 자리수가 최대 릴레이 값이 넘었다면 1로 리셋하고 그렇지 않으면 릴레이 값을 리턴한다.
	 * 
	 * @param batch
	 * @param relaySeq
	 * @return
	 */
	public static Integer fitRelaySeq(JobBatch batch, Integer relaySeq) {
		String value = getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_RELAY_MAX_NO, DEFAULT_IND_RELAY_MAX_NO);
		int relayMaxNo = ValueUtil.toInteger(value);
		return (relaySeq > relayMaxNo) ? 1 : relaySeq;
	}
	
	/**
	 * 작업 배치 범위 내에서 작업 유형별 표시기 표현 형식 - 0 : 기본, 1 : 박스 / 낱개, 2 : 누적수량 / 낱개
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndViewType(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_SHOW_VIEW_TYPE, DEFAULT_IND_SHOW_VIEW_TYPE);
	}
	
	/**
	 * 작업 배치 범위 내에서 라우터 펌웨어 최신 릴리즈 버전
	 *
	 * @param batch
	 * @param gateway
	 * @return
	 */
	public static String getGwLatestReleaseVersion(JobBatch batch, Gateway gateway) {
		String version = gateway.getVersion();
		
		if(ValueUtil.isEmpty(version)) {
			return getIndConfigValueByBatchScope(batch, MwConfigConstants.GW_LATEST_RELEASE_VERSION, DEFAULT_GW_LATEST_RELEASE_VERSION);
		} else {
			return version;
		}
	}

	/**
	 * 작업 배치 범위 내에서 표시기 펌웨어 최신 릴리즈 버전
	 *
	 * @param batch
	 * @return
	 */
	public static String getIndLatestReleaseVersion(JobBatch batch) {
		return getIndConfigValueByBatchScope(batch, MwConfigConstants.IND_LATEST_RELEASE_VERSION, DEFAULT_IND_LATEST_RELEASE_VERSION);
	}
	
	/**
	 * 작업 배치 범위 내에서 표시기 점등 정보 indOnInfo에 relaySeq, boxQty, eaQty 등을 설정한다.
	 *
	 * @param indOnInfo
	 * @param relaySeq
	 * @param boxQty
	 * @param eaQty
	 */
	public static void setIndOnQty(IndicatorOnInformation indOnInfo, Integer relaySeq, Integer boxQty, Integer eaQty) {
		indOnInfo.setOrgRelay(relaySeq);
		indOnInfo.setOrgBoxQty(boxQty);
		indOnInfo.setOrgEaQty(eaQty);
	}

	/**
	 * 게이트웨이 부트시에 게이트웨이에 내려 줄 부트 설정을 생성하여 리턴 
	 * 
	 * @param batch
	 * @param gateway
	 * @return
	 */
	public static GatewayInitResIndConfig getGatewayBootConfig(JobBatch batch, Gateway gateway) {
		GatewayInitResIndConfig config = new GatewayInitResIndConfig();
		config.setAlignment(getIndNumberAlignment(batch));
		config.setSegRole(getIndSegmentRolesOn(batch));
		config.setBtnMode(getIndButtonOnMode(batch));
		config.setBtnIntvl(getIndButtonBlinkInterval(batch));
		config.setBfOnMsg(getIndShowStringBeforeOn(batch));
		config.setBfOnMsgT(getIndShowStringDelayBeforeOn(batch));
		config.setBfOnDelay(getIndDelayBeforeOn(batch));
		config.setCnclDelay(getIndDelayCancelButtonOff(batch));
		config.setBlinkIfFull(isIndFullboxButtonBlink(batch));
		config.setOffUseRes(isIndSendOffAckAlreadyOff(batch));
		config.setLedBarMode(getIndLedOnMode(batch));
		config.setLedBarIntvl(getIndLedBlinkInterval(batch));
		config.setLedBarBrtns(getIndLedBrightness(batch));
		config.setViewType(getIndViewType(batch));
		return config;
	}

}
