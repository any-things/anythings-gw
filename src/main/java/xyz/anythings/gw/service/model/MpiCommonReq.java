package xyz.anythings.gw.service.model;

/**
 * 표시기 공통 모델 
 * 
 * @author shortstop
 */
public class MpiCommonReq {

	/**
	 * 도메인 ID
	 */
	private Long domainId;
	/**
	 * MPI 코드
	 */
	private String mpiCd;
	/**
	 * 로케이션 코드
	 */
	private String locCd;
	/**
	 * gateway path
	 */
	private String gwPath;
	
	public MpiCommonReq() {
	}
	
	public MpiCommonReq(Long domainId, String mpiCd, String locCd, String gwPath) {
		this.domainId = domainId;
		this.mpiCd = mpiCd;
		this.locCd = locCd;
		this.gwPath = gwPath;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getMpiCd() {
		return mpiCd;
	}

	public void setMpiCd(String mpiCd) {
		this.mpiCd = mpiCd;
	}

	public String getLocCd() {
		return locCd;
	}

	public void setLocCd(String locCd) {
		this.locCd = locCd;
	}

	public String getGwPath() {
		return gwPath;
	}

	public void setGwPath(String gwPath) {
		this.gwPath = gwPath;
	}
	
}
