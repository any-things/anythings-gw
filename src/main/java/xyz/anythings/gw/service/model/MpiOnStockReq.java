package xyz.anythings.gw.service.model;

/**
 * 재고 조정 관련 표시기 점등 요청 모델
 * 
 * @author shortstop
 */
public class MpiOnStockReq {

	/**
	 * stockId
	 */
	private String stockId;
	/**
	 * MPI 코드
	 */
	private String mpiCd;
	/**
	 * MPI 색상 
	 */
	private String mpiColor;
	/**
	 * 적치 수량
	 */
	private Integer loadQty;
	/**
	 * 할당 수량  
	 */
	private Integer allocQty;
	/**
	 * gateway path
	 */
	private String gwPath;
	
	public String getStockId() {
		return stockId;
	}
	
	public void setStockId(String stockId) {
		this.stockId = stockId;
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
	
	public Integer getLoadQty() {
		return loadQty;
	}

	public void setLoadQty(Integer loadQty) {
		this.loadQty = loadQty;
	}

	public Integer getAllocQty() {
		return allocQty;
	}

	public void setAllocQty(Integer allocQty) {
		this.allocQty = allocQty;
	}

	public String getGwPath() {
		return gwPath;
	}
	
	public void setGwPath(String gwPath) {
		this.gwPath = gwPath;
	}	
	
}
