package xyz.anythings.gw;

import xyz.anythings.base.LogisBaseConfigConstants;

/**
 * 물류 게이트웨이 모듈 설정 키 관련 상수 정의
 *
 * @author shortstop
 */
public class LogisGwConfigConstants extends LogisBaseConfigConstants {
	
	/**
	 * 메시징 미들웨어와 통신할 큐 이름
	 */
	public static String MW_SYSTEM_QUEUE_NAME = "mq.system.receive.queue.name";
	/**
	 * 미들웨어 메시지 로깅 활성화 여부
	 */
	public static final String MW_RECEIVE_LOGGING_ENABLED = "mw.receive.logging.enabled";

	/**********************************************************************
	 * 								1. 전체 설정 
	 **********************************************************************/
	/**
	 * 표시기 점등 세그먼트 역할 - 첫번째/두번째/세번째 세그먼트 역할 -> R(릴레이 순서)/B(Box)/P(PCS)/S(문자열)
	 */
	public static final String MPI_SEGMENT_ROLE_ON = "mps.mpi.job.segment.roles.on";
	/**
	 * 표시기 표시 자리 (L:Left, R:Right)
	 */
	public static final String MPI_NUMBER_ALIGNMENT = "mps.mpi.show.number.alignment";
	/**
	 * 표시기 버튼 점등 모드 (B:깜빡임, S:정지)
	 */
	public static final String MPI_BUTTON_ON_MODE = "mps.mpi.show.button.on.mode";
	/**
	 * 표시기 버튼 깜빡임 주기 (100ms)
	 */
	public static final String MPI_BUTTON_BLINK_INTERVAL = "mps.mpi.show.button.blink.interval";
	/**
	 * 표시기 점등 전 표시 문자
	 */
	public static final String MPI_SHOW_CHARS_BEFORE_ON = "mps.mpi.action.show.chars.before.on";
	/**
	 * 표시기 점등 전 문자 표시 시간
	 */
	public static final String MPI_SHOW_CHARS_DELAY_BEFORE_ON = "mps.mpi.action.show.chars.delay.before.on";
	/**
	 * 표시기 점등 전 딜레이 (100ms)
	 */
	public static final String MPI_DELAY_BEFORE_ON = "mps.mpi.action.delay.before.on";
	/**
	 * 표시기 취소 버튼 터치시 소등까지 딜레이 (100ms)
	 */
	public static final String MPI_DELAY_CANCEL_BUTTON_OFF = "mps.mpi.action.delay.cancel.button.off";
	/**
	 * 표시기 Full Box 터치시 버튼 깜빡임 여부 (true / false)
	 */
	public static final String MPI_FULLBOX_BUTTON_BLINK = "mps.mpi.show.fullbox.button.blink";
	/**
	 * 표시기가 이미 소등된 경우 소등 요청을 받았을 때 ACK를 응답할 지 여부
	 */
	public static final String MPI_SEND_OFF_ACK_ALREADY_OFF = "mps.mpi.action.send.off.ack.already.off";
	/**
	 * LED Bar를 사용할 지 여부 설정
	 */
	public static final String LED_BAR_USE_ENABLED = "mps.mpi.ledbar.use.enabled";
	/**
	 * LED Bar를 사용할 호기
	 */
	public static final String LED_BAR_USE_ENABLED_REGIONS = "mps.mpi.ledbar.use.enabled.regions";
	/**
	 * LED 바 점등 모드 (B:깜빡임, S:정지)
	 */
	public static final String MPI_LEDBAR_ON_MODE = "mps.mpi.ledbar.on.mode";
	/**
	 * LED 바 깜빡임 주기 (100ms)
	 */
	public static final String MPI_LEDBAR_BLINK_INTERVAL = "mps.mpi.ledbar.blink.interval";
	/**
	 * LED 바 밝기 정도 (1~10)
	 */
	public static final String MPI_LEDBAR_BRIGHTNESS = "mps.mpi.ledbar.brightness";
	/**
	 * 표시기 상태 보고 주기.
	 */
	public static final String MPI_HEALTH_PERIOD = "mps.mpi.action.status.report.interval";
	/**
	 * DAS 업무 기본 표시기 색상
	 */
	public static final String MPI_DEFAULT_COLOR_DAS = "mps.mpi.job.color.das";
	/**
	 * DPS 업무 기본 표시기 색상
	 */
	public static final String MPI_DEFAULT_COLOR_DPS = "mps.mpi.job.color.dps";
	/**
	 * 반품 업무 기본 표시기 색상
	 */
	public static final String MPI_DEFAULT_COLOR_RTN = "mps.mpi.job.color.rtn";
	/**
	 * 재고 실사 기본 표시기 색상
	 */
	public static final String MPI_DEFAULT_COLOR_STOCKTAKING = "mps.mpi.job.color.stocktaking";
	/**
	 * 표시기 색상 로테이션 순서
	 */
	public static final String MPI_COLOR_ROTATION_SEQ = "mps.mpi.job.color.rotation.seq";
	/**
	 * 표시기 최신 버전 정보 설정.
	 */
	public static final String MPI_LATEST_RELEASE_VERSION = "mps.device.mpi.latest.release.version";
	/**
	 * 표시기 교체 시 교체 메시지 사용 여부 
	 */
	public static final String MPI_ALTER_MESSAGE_ENABLED = "mps.mpi.alter.message.enabled";
	/**
	 * 표시기 연속 full 요청 blocking 시간 (초)
	 */
	public static final String MPS_MPI_BLOCK_SEC_CONTINOUS_FULL_REQ = "mps.mpi.block.sec.continous.full.request";
	/**
	 * 표시기 버튼 사용여부
	 */
	public static final String MPS_MPI_BUTTONS_ENABLE = "mps.mpi.buttons.enable";
	/**
	 * 재고 조정시 MPI LED 타입
	 */
	public static final String STOCK_ADJUSTMENT_LED_COLOR = "mps.stock.show.adjustment.led.color";
	/**
	 * Gateway 최신 버전 정보 설정.
	 */
	public static final String GATEWAY_LATEST_RELEASE_VERSION = "mps.device.gateway.latest.release.version";
	
	/**
	 * MPI 표시 모드 - 0: default (or undefined) 1: 박스입수,낱개수량을 계산하여 박스수량/낱개수량 형태로 표시 2: 누적수량/낱개수량(반품) 3: 작업잔량/낱개수량
	 */
	public static final String MPI_DISPLAY_MPI_VIEW_TYPE = "com.mpi.display.mpi-view-type";
	
	/**********************************************************************
	 * 							2. MPI 관련 설정
	 **********************************************************************/	

	/**
	 * 표시기 점등 세그먼트 역할 - 첫번째/두번째/세번째 세그먼트 역할 -> R(릴레이 순서)/B(Box)/P(PCS)
	 */
	public static final String COM_MPI_SEGMENT_ROLE_ON = "com.mpi.job.segment.roles.on";
	/**
	 * 표시 세그먼트의 첫번째 숫자와 매핑되는 역할
	 * (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String COM_MPI_DISP_SEGMENT1_MAPPING_ROLE = "com.mpi.display.segment1.mapping.role";
	/**
	 * 표시 세그먼트의 첫번째 숫자와 매핑되는 역할
	 * (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String COM_MPI_DISP_SEGMENT2_MAPPING_ROLE = "com.mpi.display.segment2.mapping.role";
	/**
	 * 표시 세그먼트의 첫번째 숫자와 매핑되는 역할
	 * (T: 총 수량, R: 총 남은 수량, F: 총 처리한 수량, P: 방금 전 처리한 낱개 수량, B: 방금 전 처리한 박스 수량)
	 */
	public static final String COM_MPI_DISP_SEGMENT3_MAPPING_ROLE = "com.mpi.display.segment3.mapping.role";
	
}
