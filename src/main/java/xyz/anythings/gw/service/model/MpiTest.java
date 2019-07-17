package xyz.anythings.gw.service.model;

import java.util.List;

import xyz.elidom.util.FormatUtil;

/**
 * 표시기 테스트 모델
 * 
 * @author shortstop
 */
public class MpiTest {
	/**
	 * MPI 타겟 유형 - 호기 : region
	 */
	public static final String TARGET_TYPE_REGION = "region";
	/**
	 * MPI 타겟 유형 - 게이트웨이 : gateway
	 */
	public static final String TARGET_TYPE_GATEWAY = "gateway";
	/**
	 * MPI 타겟 유형 - 장비 존 : equip_zone
	 */
	public static final String TARGET_TYPE_EQUIP_ZONE = "equip_zone";
	/**
	 * MPI 타겟 유형 - 작업 존 : work_zone
	 */
	public static final String TARGET_TYPE_WORK_ZONE = "work_zone";
	/**
	 * MPI 타겟 유형 - 로케이션 : location
	 */
	public static final String TARGET_TYPE_LOCATION = "location";
	/**
	 * MPI 타겟 유형 - 표시기 : indicator
	 */
	public static final String TARGET_TYPE_INDICATOR = "indicator";
	
	/**
	 * 고객사 코드 
	 */
	private String comCd;
	
	/**
	 * 작업 유형 
	 */
	private String jobType;
	
	/**
	 * MPI 점등 타겟
	 */
	private MpiTarget target;
	/**
	 * MPI 액션
	 */
	private MpiAction action;
	
	public String getComCd() {
		return comCd;
	}

	public void setComCd(String comCd) {
		this.comCd = comCd;
	}

	public MpiTarget getTarget() {
		return target;
	}

	public void setTarget(MpiTarget target) {
		this.target = target;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public MpiAction getAction() {
		return action;
	}

	public void setAction(MpiAction action) {
		this.action = action;
	}

	/**
	 * MPI Target
	 * 
	 * @author shortstop
	 */
	public class MpiTarget {
		/**
		 * MPI Target Type
		 */
		String targetType;
		/**
		 * MPI Target ID List
		 */
		List<String> targetIdList;
		
		public String getTargetType() {
			return targetType;
		}
		
		public void setTargetType(String targetType) {
			this.targetType = targetType;
		}
		
		public List<String> getTargetIdList() {
			return targetIdList;
		}
		
		public void setTargetIdList(List<String> targetIdList) {
			this.targetIdList = targetIdList;
		}
	}
	
	/**
	 * MPI 점등/소등 액션
	 * 
	 * @author shortstop
	 */
	public class MpiAction {
		/**
		 * 점등 / 소등
		 */
		String action;
		/**
		 * Action Type
		 */
		String actionType;
		/**
		 * 표시기 점등 색깔
		 */
		String btnColor;
		/**
		 * 릴레이 수량
		 */
		String firstQty;
		/**
		 * 박스 수량 
		 */
		String secondQty;
		/**
		 * 강제 소등 여부
		 */
		Boolean forceFlag;
		
		public String getAction() {
			return action;
		}
		
		public void setAction(String action) {
			this.action = action;
		}
		
		public String getActionType() {
			return actionType;
		}
		
		public void setActionType(String actionType) {
			this.actionType = actionType;
		}
		
		public String getBtnColor() {
			return btnColor;
		}
		
		public void setBtnColor(String btnColor) {
			this.btnColor = btnColor;
		}
		
		public String getFirstQty() {
			return firstQty;
		}
		
		public void setFirstQty(String firstQty) {
			this.firstQty = firstQty;
		}
		
		public String getSecondQty() {
			return secondQty;
		}
		
		public void setSecondQty(String secondQty) {
			this.secondQty = secondQty;
		}
		
		public Boolean getForceFlag() {
			return forceFlag;
		}
		
		public void setForceFlag(Boolean forceFlag) {
			this.forceFlag = forceFlag;
		}
	}
	
	/**
	 * @Override
	 */
	public String toString() {
		return FormatUtil.toUnderScoreJsonString(this);
	}
}
