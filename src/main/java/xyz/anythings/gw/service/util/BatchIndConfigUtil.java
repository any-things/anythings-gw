package xyz.anythings.gw.service.util;

import xyz.anythings.gw.MwConfigConstants;
import xyz.anythings.gw.MwConstants;
import xyz.anythings.gw.service.api.IIndConfigSetService;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

/**
 * 작업 배치 범위 내 표시기 설정 값 조회 유틸리티
 * 표시기 설정 리스트
 *  - ind.action.delay.before.on				표시기가 점등되기 전 지연되는 시간입니다. (100ms 단위)
 *  - ind.action.delay.cancel.button.off		표시기의 취소 버튼을 눌렀을 때 표시기가 소등되기까지의 지연 시간입니다. (100ms)
 *  - ind.action.send.off.ack.already.off		표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 응답 메시지를 보낼 지 여부입니다.
 *  - ind.action.show.string.before.on			다음 작업을 점등하기 전에 표시될 문자열입니다.
 *  - ind.action.show.string.delay.before.on	점등 전에 문자를 표시할 시간입니다. (100ms 단위)
 *  - ind.action.status.report.interval			표시기의 상태 보고 주기입니다. (초 단위)
 *  - ind.job.color.rotation.seq				표시기 버튼 색상의 로테이션 순서입니다.
 *  - ind.job.color.stocktaking					재고 실사 작업에서 표시기 버튼의 기본 색상입니다.
 *  - ind.job.segment.roles.on					작업 점등 시 각 세그먼트가 나타낼 정보입니다 - 첫번째/두번째/세번째 세그먼트 역할 -> R(릴레이 순서)/B(Box)/P(PCS)
 *  - ind.show.segment1.mapping.role			표시 세그먼트의 첫번째 숫자와 매핑되는 역할 (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
 *  - ind.show.segment2.mapping.role			표시 세그먼트의 두번째 숫자와 매핑되는 역할
 *  - ind.show.segment3.mapping.role			표시 세그먼트의 세번째 숫자와 매핑되는 역할
 *  - ind.show.relay.max.no						최대 릴레이 번호 
 *  - ind.show.button.blink.interval			표시기의 버튼이 깜빡이는 주기입니다. (100ms 단위)
 *  - ind.show.button.on.mode					표시기의 버튼이 점등되는 방식입니다. (B: 깜빡임, S: 항상 켜짐)
 *  - ind.show.fullbox.button.blink				Full Box 처리 후, 표시기가 Full Box 상태로 점등됐을 때 버튼이 깜빡일지 여부입니다.
 *  - ind.show.view-type						표시기 자체적으로 표시 형식을 변경하는 모드
 *  - ind.show.number.alignment					표시기 숫자의 정렬 방향입니다. (R / L)
 *  - ind.led.blink.interval					LED 바가 깜박이는 주기입니다. (100ms 단위)
 *  - ind.led.brightness						LED 바의 밝기입니다. (1~10)
 *  - ind.led.on.mode							LED 바가 점등되는 방식입니다. (B: 깜빡임, S: 항상 켜짐)
 *  - ind.led.use.enabled						LED 바를 사용할지 여부입니다.
 *  - ind.led.use.enabled.racks					LED 바를 사용할 호기 리스트 (콤마로 구분)
 *  - ind.buttons.enable						표시기 버튼 사용 여부
 *  - ind.latest.release.version				표시기 최신 버전 정보 설정
 *  - ind.gw.latest.release.version				Gateway 최신 버전 정보 설정
 * 
 * @author shortstop
 */
public class BatchIndConfigUtil {
	
	/**
	 * 설정 프로파일 서비스
	 */
	public static IIndConfigSetService CONFIG_SET_SVC;
	
	/**
	 * 설정 프로파일 서비스 리턴
	 * 
	 * @return
	 */
	public static IIndConfigSetService getConfigSetService() {
		if(CONFIG_SET_SVC == null) {
			CONFIG_SET_SVC = BeanUtil.get(IIndConfigSetService.class);
		}
		
		return CONFIG_SET_SVC;
	}
	
	/**
	 * 작업 배치 범위 내에 설정 내용을 키로 조회해서 리턴
	 *  
	 * @param batch
	 * @param key
	 * @param exceptionWhenEmptyValue
	 * @return
	 */
	public static String getConfigValue(String batchId, String key, boolean exceptionWhenEmptyValue) {
		IIndConfigSetService configSvc = getConfigSetService();
		
		// 1. 작업 유형에 따른 설정값 조회
		String value = configSvc.getIndConfigValue(batchId, key);
		
		// 2. 설정값이 없다면 exceptionWhenEmptyValue에 따라 예외 처리
		if(exceptionWhenEmptyValue) {
			throw ThrowUtil.newJobConfigNotSet(key);
		}
		
		return value;
	}

	/**
	 * 표시기가 점등되기 전 지연되는 시간입니다. (100ms 단위)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getIndOnDelayTime(String batchId) {
		// ind.action.delay.before.on
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_DELAY_BEFORE_ON, true);
		return ValueUtil.toInteger(intVal);
	}

	/**
	 * 표시기의 취소 버튼을 눌렀을 때 표시기가 소등되기까지의 지연 시간입니다. (100ms)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getIndOnDelayTimeCancelPushed(String batchId) {
		// ind.action.delay.cancel.button.off
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_DELAY_CANCEL_BUTTON_OFF, true);
		return ValueUtil.toInteger(intVal);
	}

	/**
	 * 표시기가 이미 소등된 상태에서 소등 요청을 받았을 때 응답 메시지를 보낼 지 여부입니다.
	 * 
	 * @param batch
	 * @return
	 */
	public static boolean isNoackWhenAlreadyOffEnabled(String batchId) {
		// ind.action.send.off.ack.already.off
		String boolVal = getConfigValue(batchId, MwConfigConstants.IND_SEND_OFF_ACK_ALREADY_OFF, true);
		return ValueUtil.toBoolean(boolVal);
	}
	
	/**
	 * 다음 작업을 점등하기 전에 표시될 문자열입니다.
	 * 
	 * @param batch
	 * @return
	 */
	public static String getDisplayStringBeforeIndOn(String batchId) {
		// ind.action.show.string.before.on
		return getConfigValue(batchId, MwConfigConstants.IND_SHOW_STRING_BEFORE_ON, true);
	}

	/**
	 * 점등 전에 문자를 표시할 시간입니다. (100ms 단위)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getDisplayIntervalBeforeIndOn(String batchId) {
		// ind.action.show.string.delay.before.on
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_SHOW_STRING_DELAY_BEFORE_ON, true);
		return ValueUtil.toInteger(intVal);
	}

	/**
	 * 표시기의 상태 보고 주기입니다. (초 단위)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getIndStateReportInterval(String batchId) {
		// ind.action.status.report.interval
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_HEALTH_PERIOD, true);
		return ValueUtil.toInteger(intVal);
	}

	/**
	 * 표시기 버튼 색상의 로테이션 순서입니다.
	 * 
	 * @param batch
	 * @return
	 */
	public static String[] getIndButtonColorForRotation(String batchId) {
		// ind.job.color.rotation.seq
		String strVal = getConfigValue(batchId, MwConfigConstants.IND_COLOR_ROTATION_SEQ, true);
		return strVal.split(MwConstants.COMMA);
	}
	
	/**
	 * 재고 실사 작업에서 표시기 버튼의 기본 색상입니다.
	 * TODO - 스테이지 범위의 표시기 설정으로 이동 필요
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndColorForStocktaking(String batchId) {
		// ind.job.color.stocktaking
		return getConfigValue(batchId, MwConfigConstants.IND_DEFAULT_COLOR_STOCKTAKING, true);
	}
	
	/**
	 * 세그먼트 사용 개수
	 * 
	 * @param batch
	 * @return
	 */
	public static int getSegmentCount(String batchId) {
		return getSegmentRoles(batchId).length;
	}
	
	/**
	 * 작업 점등 시 각 세그먼트가 나타낼 정보입니다 - 첫번째/두번째/세번째 세그먼트 역할 -> R(릴레이 순서)/B(Box)/P(PCS)
	 * 
	 * @param batch
	 * @return
	 */
	public static String[] getSegmentRoles(String batchId) {
		// ind.job.segment.roles.on
		String strVal = getConfigValue(batchId, MwConfigConstants.IND_SEGMENT_ROLE_ON, true);
		return strVal.split(MwConstants.COMMA);
	}
	
	/**
	 * 표시 세그먼트의 첫 번째 숫자와 매핑되는 역할 (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getSegment1RoleForDisplay(String batchId) {
		// ind.show.segment1.mapping.role
		return getConfigValue(batchId, MwConfigConstants.IND_SEGMENT1_MAPPING_ROLE, true);
	}

	/**
	 * 표시 세그먼트의 두 번째 숫자와 매핑되는 역할 (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getSegment2RoleForDisplay(String batchId) {
		// ind.show.segment2.mapping.role
		return getConfigValue(batchId, MwConfigConstants.IND_SEGMENT2_MAPPING_ROLE, true);
	}

	/**
	 * 표시 세그먼트의 세 번째 숫자와 매핑되는 역할 (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getSegment3RoleForDisplay(String batchId) {
		// ind.show.segment3.mapping.role
		return getConfigValue(batchId, MwConfigConstants.IND_SEGMENT3_MAPPING_ROLE, true);
	}
	
	/**
	 * 최대 릴레이 번호
	 * 
	 * @param batch
	 * @return
	 */
	public static int getMaxRelayNo(String batchId) {
		// ind.show.relay.max.no
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_RELAY_MAX_NO, true);
		return ValueUtil.isEmpty(intVal) ? -1 : ValueUtil.toInteger(intVal);
	}
	
	/**
	 * 표시기의 버튼이 깜빡이는 주기입니다. (100ms 단위)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getButtonBlinkInterval(String batchId) {
		// ind.show.button.blink.interval
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_BUTTON_BLINK_INTERVAL, true);
		return ValueUtil.toInteger(intVal);
	}
	
	/**
	 * 표시기의 버튼이 점등되는 방식입니다. (B: 깜빡임, S: 항상 켜짐)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getButtonOnMode(String batchId) {
		// ind.show.button.on.mode
		return getConfigValue(batchId, MwConfigConstants.IND_BUTTON_ON_MODE, true);
	}
	
	/**
	 * 표시기 Full Box 터치시 버튼 깜빡임 여부
	 * 
	 * @param batch
	 * @return
	 */
	public static boolean isFullButtonBlink(String batchId) {
		// ind.show.fullbox.button.blink
		String boolVal = getConfigValue(batchId, MwConfigConstants.IND_FULLBOX_BUTTON_BLINK, true);
		return ValueUtil.toBoolean(boolVal);
	}
	
	/**
	 * 표시기 자체적으로 표시 형식을 변경하는 모드
	 * 
	 * @param batch
	 * @return
	 */
	public static String getShowViewType(String batchId) {
		// ind.show.view-type
		return getConfigValue(batchId, MwConfigConstants.IND_SHOW_VIEW_TYPE, true);
	}
	
	/**
	 * 표시기 숫자의 정렬 방향입니다. (R / L)
	 * 
	 * @param batch
	 * @return
	 */
	public static String getShowNumberAlignment(String batchId) {
		// ind.show.number.alignment
		return getConfigValue(batchId, MwConfigConstants.IND_NUMBER_ALIGNMENT, true);
	}
	
	/**
	 * LED 바가 깜박이는 주기입니다. (100ms 단위)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getLedBlinkInterval(String batchId) {
		// ind.led.blink.interval
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_LED_BLINK_INTERVAL, true);
		return ValueUtil.toInteger(intVal);
	}

	/**
	 * LED 바의 밝기입니다. (1~10)
	 * 
	 * @param batch
	 * @return
	 */
	public static int getLedBrightness(String batchId) {
		// ind.led.brightness
		String intVal = getConfigValue(batchId, MwConfigConstants.IND_LED_BRIGHTNESS, true);
		return ValueUtil.toInteger(intVal);
	}
	
	/**
	 * LED 바를 깜빡이게 할 지 여부
	 * 
	 * @param batch
	 * @return
	 */
	public static boolean isLedBlink(String batchId) {
		// ind.show.fullbox.button.blink
		String boolVal = getConfigValue(batchId, MwConfigConstants.IND_FULLBOX_BUTTON_BLINK, true);
		return ValueUtil.toBoolean(boolVal);
	}
	
	/**
	 * LED 바를 사용할지 여부입니다.
	 * 
	 * @param batch
	 * @return
	 */
	public static boolean isUseLed(String batchId) {
		// ind.led.use.enabled			
		String boolVal = getConfigValue(batchId, MwConfigConstants.IND_LED_USE_ENABLED, true);
		return ValueUtil.toBoolean(boolVal);
	}

	/**
	 * LED 바를 사용할 호기 리스트 (콤마로 구분)
	 * 
	 * @param batch
	 * @return
	 */
	public static String[] getRackOfUsingLed(String batchId) {
		// ind.led.use.enabled.racks
		String strVal = getConfigValue(batchId, MwConfigConstants.IND_LED_USE_ENABLED_RACKS, true);
		return ValueUtil.isEmpty(strVal) ? null : strVal.split(MwConstants.COMMA);
	}
		
	/**
	 * 표시기 버튼 사용 여부
	 * 
	 * @param batch
	 * @return
	 */
	public static boolean isUseButton(String batchId) {
		// ind.buttons.enable
		String boolVal = getConfigValue(batchId, MwConfigConstants.IND_BUTTONS_ENABLE, true);
		return ValueUtil.toBoolean(boolVal);
	}
	
	/**
	 * 표시기 최신 버전 정보 설정
	 * 
	 * @param batch
	 * @return
	 */
	public static String getIndLatestReleaseVersion(String batchId) {
		// ind.latest.release.version
		return getConfigValue(batchId, MwConfigConstants.IND_LATEST_RELEASE_VERSION, true);
	}
	
	/**
	 * Gateway 최신 버전 정보 설정
	 * 
	 * @param batch
	 * @return
	 */
	public static String getGwLatestReleaseVersion(String batchId) {
		// ind.gw.latest.release.version
		return getConfigValue(batchId, MwConfigConstants.GW_LATEST_RELEASE_VERSION, true);
	}

}
