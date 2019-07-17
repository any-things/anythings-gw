package xyz.anythings.gw.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@Table(name = "tb_mpi", idStrategy = GenerationRule.UUID, uniqueFields="domainId,mpiCd", indexes = {
	@Index(name = "ix_tb_mpi_0", columnList = "mpi_cd,domain_id", unique = true),
	@Index(name = "ix_tb_mpi_1", columnList = "gw_cd")
})
public class MPI extends xyz.elidom.orm.entity.basic.ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 473906924845538030L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "gw_cd", length = 30)
	private String gwCd;

	@Column (name = "mpi_cd", nullable = false, length = 30)
	private String mpiCd;

	@Column (name = "mpi_nm", length = 100)
	private String mpiNm;

	@Column (name = "version", length = 15)
	private String version;

	@Column (name = "battery_rate", length = 3)
	private Integer batteryRate;
	
	@Column (name = "rssi", length = 10)
	private Integer rssi;

	@Column (name = "use_started_at", length = 22)
	private String useStartedAt;

	@Column (name = "use_ended_at", length = 22)
	private String useEndedAt;

	@Column (name = "status", length = 10)
	private String status;

	@Column (name = "remark", length = 1000)
	private String remark;
  
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGwCd() {
		return gwCd;
	}

	public void setGwCd(String gwCd) {
		this.gwCd = gwCd;
	}

	public String getMpiCd() {
		return mpiCd;
	}

	public void setMpiCd(String mpiCd) {
		this.mpiCd = mpiCd;
	}

	public String getMpiNm() {
		return mpiNm;
	}

	public void setMpiNm(String mpiNm) {
		this.mpiNm = mpiNm;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
	}

	public Integer getBatteryRate() {
		return batteryRate;
	}

	public void setBatteryRate(Integer batteryRate) {
		this.batteryRate = batteryRate;
	}

	public String getUseStartedAt() {
		return useStartedAt;
	}

	public void setUseStartedAt(String useStartedAt) {
		this.useStartedAt = useStartedAt;
	}

	public String getUseEndedAt() {
		return useEndedAt;
	}

	public void setUseEndedAt(String useEndedAt) {
		this.useEndedAt = useEndedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String gatewayPath() {
		String sql = "select gw_nm from tb_gateway where domain_id = :domainId and gw_cd = :gwCd";
		return BeanUtil.get(IQueryManager.class).selectBySql(sql, ValueUtil.newMap("domainId,gwCd", this.domainId, this.gwCd), String.class);
	}
	
	public static String findGatewayPath(Long domainId, String mpiCd) {
		String sql = "select gw_nm from tb_gateway where domain_id = :domainId and gw_cd = (select gw_cd from tb_mpi where domain_id = :domainId and mpi_cd = :mpiCd)";
		return BeanUtil.get(IQueryManager.class).selectBySql(sql, ValueUtil.newMap("domainId,mpiCd", domainId, mpiCd), String.class);
	}
	
	public static MPI findByCd(Long domainId, String mpiCd) {
		String sql  = "select * from tb_mpi where domain_id = :domainId and mpi_cd = :mpiCd";
		return BeanUtil.get(IQueryManager.class).selectBySql(sql, ValueUtil.newMap("domainId,mpiCd", domainId, mpiCd), MPI.class);
	}
}
