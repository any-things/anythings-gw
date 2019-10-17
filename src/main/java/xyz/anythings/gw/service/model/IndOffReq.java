package xyz.anythings.gw.service.model;

/**
 * 표시기 소등 모델 
 * 
 * @author shortstop
 */
public class IndOffReq {

	/**
	 * Indicator 코드
	 */
	private String indCd;
	/**
	 * gateway path
	 */
	private String gwPath;
	
	public IndOffReq() {
	}
	
	public IndOffReq(String indCd, String gwPath) {
		this.indCd = indCd;
		this.gwPath = gwPath;
	}

	public String getIndCd() {
		return indCd;
	}

	public void setIndCd(String indCd) {
		this.indCd = indCd;
	}

	public String getGwPath() {
		return gwPath;
	}

	public void setGwPath(String gwPath) {
		this.gwPath = gwPath;
	}
}
