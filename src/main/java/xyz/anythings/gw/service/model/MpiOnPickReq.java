package xyz.anythings.gw.service.model;

/**
 * 표시기 점등 요청 모델
 * 
 * @author shortstop
 */
public class MpiOnPickReq {

	/**
	 * JobProcessId
	 */
	private String jobProcessId;
	/**
	 * 고객사 코드
	 */
	private String comCd;
	/**
	 * 실행 순서
	 */
	private Integer processSeq;
	/**
	 * MPI 코드
	 */
	private String mpiCd;
	/**
	 * MPI 색상 
	 */
	private String mpiColor;
	/**
	 * 피킹 수량  
	 */
	private Integer pickQty;
	/**
	 * 박스 입수 수량 
	 */
	private Integer boxInQty;
	/**
	 * gateway path
	 */
	private String gwPath;
	
	public MpiOnPickReq() {
	}
	
	public String getJobProcessId() {
		return jobProcessId;
	}
	
	public void setJobProcessId(String jobProcessId) {
		this.jobProcessId = jobProcessId;
	}
	
	public String getComCd() {
		return comCd;
	}

	public void setComCd(String comCd) {
		this.comCd = comCd;
	}

	public Integer getProcessSeq() {
		return processSeq;
	}

	public void setProcessSeq(Integer processSeq) {
		this.processSeq = processSeq;
	}

	public String getMpiCd() {
		return mpiCd;
	}
	
	public void setMpiCd(String mpiCd) {
		this.mpiCd = mpiCd;
	}
	
	public String getMpiColor() {
		return mpiColor;
	}
	
	public void setMpiColor(String mpiColor) {
		this.mpiColor = mpiColor;
	}
	
	public Integer getPickQty() {
		return pickQty;
	}
	
	public void setPickQty(Integer pickQty) {
		this.pickQty = pickQty;
	}
	
	public Integer getBoxInQty() {
		return boxInQty;
	}
	
	public void setBoxInQty(Integer boxInQty) {
		this.boxInQty = boxInQty;
	}
	
	public String getGwPath() {
		return gwPath;
	}
	
	public void setGwPath(String gwPath) {
		this.gwPath = gwPath;
	}
	
}
