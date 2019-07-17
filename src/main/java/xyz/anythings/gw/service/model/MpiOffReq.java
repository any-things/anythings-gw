package xyz.anythings.gw.service.model;

/**
 * 표시기 소등 모델 
 * 
 * @author shortstop
 */
public class MpiOffReq {

	/**
	 * MPI 코드
	 */
	private String mpiCd;
	/**
	 * gateway path
	 */
	private String gwPath;
	
	public MpiOffReq() {
	}
	
	public MpiOffReq(String mpiCd, String gwPath) {
		this.mpiCd = mpiCd;
		this.gwPath = gwPath;
	}

	public String getMpiCd() {
		return mpiCd;
	}

	public void setMpiCd(String mpiCd) {
		this.mpiCd = mpiCd;
	}

	public String getGwPath() {
		return gwPath;
	}

	public void setGwPath(String gwPath) {
		this.gwPath = gwPath;
	}
}
