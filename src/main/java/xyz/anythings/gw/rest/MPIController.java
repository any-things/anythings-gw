package xyz.anythings.gw.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.anythings.base.entity.Location;
import xyz.anythings.gw.entity.MPI;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.ValueUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/mpi")
@ServiceDesc(description = "MPI Service API")
public class MPIController extends AbstractRestService {
	
	@Override
	protected Class<?> entityClass() {
		return MPI.class;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search (Pagination) By Search Conditions")
	public Page<?> index(@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "select", required = false) String select,
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		Query condition = this.parseQuery(this.entityClass(), page, limit, select, sort, query);
		List<Filter> filters = condition.getFilter();
		Filter regionFilter = null;
		
		for(Filter filter : filters) {
			if(ValueUtil.isEqualIgnoreCase(filter.getName(), "region_cd")) {
				regionFilter = filter;
				break;
			}
		}
		
		if(regionFilter != null) {
			condition.removeFilter("region_cd");
			List<String> mpiCdList = Location.searchMpiByRegion(Domain.currentDomainId(), ValueUtil.toString(regionFilter.getValue()));
			condition.addFilter("mpi_cd", SysConstants.IN, mpiCdList);
		}
		
		return this.search(this.entityClass(), condition);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find one by ID")
	public MPI findOne(@PathVariable("id") String id) {
		return this.getOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check exists By ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/check_import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check Before Import")
	public List<MPI> checkImport(@RequestBody List<MPI> list) {
		Long domainId = Domain.currentDomainId();
		
		for (MPI item : list) {
			Map<String, Object> condition = ValueUtil.newMap("domainId,mpiCd", domainId, item.getMpiCd());
			MPI result = this.queryManager.selectByCondition(MPI.class, condition);
			if(result != null) {
				item.setId(result.getId());
			}
		}
		
		return list;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create")
	public MPI create(@RequestBody MPI input) {
		return this.createOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update")
	public MPI update(@PathVariable("id") String id, @RequestBody MPI input) {
		return this.updateOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete")
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.getClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete multiple at one time")
	public Boolean multipleUpdate(@RequestBody List<MPI> list) {
		return this.cudMultipleData(this.entityClass(), list);
	}
	
//	@RequestMapping(value = "/performance/mpi_on_all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "MPI Performance")
//	public Map<String, Object> mpiOnAllPerformance() {
//		StringJoiner sql = new StringJoiner(SysConstants.LINE_SEPARATOR);
//		sql.add("SELECT")
//		   .add("	GATE.gw_nm as gw_cd, MPI.mpi_cd, LOC.loc_cd as mpi_nm")
//		   .add("FROM")
//		   .add("	TB_LOCATION LOC")
//		   .add("	INNER JOIN TB_MPI MPI ON LOC.MPI_CD = MPI.MPI_CD")
//		   .add("	INNER JOIN TB_GATEWAY GATE ON MPI.GW_CD = GATE.GW_CD")
//		   .add("WHERE")
//		   .add("	LOC.domain_id = :domainId")
//		   .add("	AND LOC.active_flag = 1 AND GATE.pan_no = '00FF1F'")
//		   .add("GROUP BY")
//		   .add("	GATE.gw_nm, MPI.mpi_cd, LOC.loc_cd")
//		   .add("ORDER BY")
//		   .add("	GATE.gw_nm, MPI.mpi_cd, LOC.loc_cd");
//		
//		Long domainId = Domain.currentDomainId();
//		Map<String, Object> params = ValueUtil.newMap("domainId", domainId);
//		List<MPI> mpiList = this.queryManager.selectListBySql(sql.toString(), params, MPI.class, 0, 0);
//		if(!mpiList.isEmpty()) {
//			List<JobProcess> jobList = new ArrayList<JobProcess>();
//			String prevGwPath = null;
//			
//			for(MPI mpi : mpiList) {
//				String gwPath = mpi.getGwCd();
//				
//				// 게이트웨이가 변경된 경우
//				if(!ValueUtil.isEqualIgnoreCase(prevGwPath, gwPath)) {
//					if(prevGwPath != null) {
//						Map<String, List<IndicatorOnInformation>> indOnMap = MpsServiceUtil.buildMpiOnList(false, MpsConstants.JOB_TYPE_DAS, jobList, false);
//						this.mpiSendService.requestMpisOn(domainId, MpsConstants.JOB_TYPE_DAS, "pick", indOnMap);
//						jobList.clear();
//					}
//					
//					prevGwPath = gwPath;
//				}
//				
//				String mpiNm = mpi.getMpiNm();
//				mpiNm = mpiNm.replaceAll("M", "").replaceAll("-", "");
//				String firstStr = mpiNm.substring(0, 3);
//				String secondStr = mpiNm.substring(3);
//				Integer firstNo = ValueUtil.toInteger(firstStr);
//				Integer secondNo = ValueUtil.toInteger(secondStr);
//				
//				JobProcess indOn = new JobProcess();
//				indOn.setComCd("OL");
//				indOn.setBoxInQty(1);
//				indOn.setId(mpi.getId());
//				indOn.setMpiCd(mpi.getMpiCd());
//				indOn.setGwPath(gwPath);
//				indOn.setMpiColor("Y");
//				indOn.setProcessSeq(firstNo);
//				indOn.setPickQty(secondNo);
//				indOn.setPickedQty(0);
//				jobList.add(indOn);
//			}
//			
//			if(!jobList.isEmpty()) {
//				Map<String, List<IndicatorOnInformation>> indOnMap = MpsServiceUtil.buildMpiOnList(false, MpsConstants.JOB_TYPE_DAS, jobList, false);
//				this.mpiSendService.requestMpisOn(domainId, MpsConstants.JOB_TYPE_DAS, "pick", indOnMap);
//			}
//		}
//		
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	@RequestMapping(value = "/performance/mpi_off_all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "MPI Performance")
//	public Map<String, Object> mpiOffAllPerformance() {
//		List<String> gwPathList = this.queryManager.selectListBySql("SELECT gw_nm from TB_GATEWAY", new HashMap<String, Object>(1), String.class, 0, 0);
//		this.mpiSendService.requestMpiOff(Domain.currentDomainId(), gwPathList, false);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	/**
//	 * MPI 성능 테스트 
//	 * 
//	 * @param count
//	 * @param interval
//	 * @param onSeqFlag
//	 * @return
//	 */
//	@RequestMapping(value = "/performance/{live_flag}/{count}/{interval}/{on_seq}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "MPI Performance")
//	public Map<String, Object> mpiPerformance(
//			@PathVariable("live_flag") boolean liveFlag,
//			@PathVariable("count") Integer count, 
//			@PathVariable("interval") Integer interval,
//			@PathVariable("on_seq") boolean onSeqFlag) {
//		
//		// 1. 모든 진행 중인 호기 리스트를 조회 
//		String sql = "select region_cd from tb_region where virtual_flag <> 1 and region_cd ";
//		sql += liveFlag ? "not like" : "like";
//		sql += " 'T042%'";
//		
//		List<Region> regions = this.queryManager.selectListBySql(sql, ValueUtil.newMap("domainId", Domain.currentDomainId()), Region.class, 0, 0);
//		List<String> colorList = CodeConstants.MPI_COLOR_LIST;
//		int colorIdx = 0;
//
//		// 2. 횟수 만큼 호기별 표시기 점등 수행
//		for(int c = 0 ; c < count ; c++) {
//			// 호기별로 모든 표식에 자신의 로케이션 정보 점등 요청 
//			for(Region region : regions) {
//				this.mpiOnByRegion(region.getRegionCd(), colorList.get(colorIdx++), onSeqFlag);
//
//				ThreadUtil.sleep(interval * 1000);
//				
//				this.mpiLightOffByZone(region.getRegionCd(), null);
//				
//				if(colorIdx >= colorList.size()) {
//					colorIdx = 0;
//				}
//			}
//			
//			ThreadUtil.sleep(interval * 1000);
//		}
//		
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	@RequestMapping(value = "/reboot/regions/{region_cd}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Reboot regions")
//	public Map<String, Object> rebootingAll(@PathVariable("region_cd") String regionCd) {
//		List<Region> regions = null;
//		Long domainId = Domain.currentDomainId();
//		
//		if(ValueUtil.isEqualIgnoreCase(regionCd, "ALL")) {
//			String sql = "select * from regions where batch_id is not null and length(batch_id) > 0";
//			regions = this.queryManager.selectListBySql(sql, new HashMap<String, Object>(1), Region.class, 0, 0);
//		} else {
//			regions = ValueUtil.toList(Region.findByRegionCd(domainId, regionCd, true));
//		}
//		
//		for(Region region : regions) {
//			if(ValueUtil.isNotEmpty(region.getBatchId())) {
//				JobBatch batch = JobBatch.find(domainId, region.getBatchId(), true);
//				BeanUtil.get(MpsAssortableService.class).getAssortService(batch).restoreMpiOn(region);
//			}
//		}
//		
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	@SuppressWarnings("unchecked")
//	@RequestMapping(value = "/mpi_on/{region_cd}/{mpi_color}/{on_seq}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "MPI On All MPI of RegionCd")
//	public Map<String, Object> mpiOnByRegion(
//			@PathVariable("region_cd") String regionCd, 
//			@PathVariable("mpi_color") String mpiColor,
//			@PathVariable("on_seq") boolean onSeqFlag) {
//		
//		String query = "[{\"name\":\"region_cd\",\"operator\":\"eq\",\"value\":\"" + regionCd + "\"}]";
//		MPIController mpiCtrl = BeanUtil.get(MPIController.class);
//		Page<MPI> mpiPage = (Page<MPI>)mpiCtrl.index(1, 1000, "domain_id,mpi_cd,mpi_nm,gw_cd", null, query);
//		List<MPI> mpiList = mpiPage.getList();
//		List<JobProcess> jobList = new ArrayList<JobProcess>();
//		Long domainId = Domain.currentDomainId();
//		
//		String regionFirstCd = regionCd.substring(0, 1);
//		String comCd = null;
//		
//		if(!onSeqFlag) {
//			Map<String, Object> params = ValueUtil.newMap("domainId", domainId);
//			String sql = "select * from (select com_cd from tb_domain_company where domain_id = :domainId) where rownum <= 1";
//			List<String> comCdList = this.queryManager.selectListBySql(sql, params, String.class, 1, 1);
//			comCd = comCdList.get(0);
//		}
//				
//		for(MPI mpi : mpiList) {
//			Integer firstNo = null;
//			Integer secondNo = null;
//			
//			try {
//				String mpiNm = mpi.getMpiNm();
//				mpiNm = mpiNm.replaceAll(regionFirstCd, MpsConstants.EMPTY_STRING).replaceAll(MpsConstants.DASH, MpsConstants.EMPTY_STRING);
//				String firstStr = mpiNm.substring(0, 3);
//				String secondStr = mpiNm.substring(3);
//				firstNo = ValueUtil.toInteger(firstStr);
//				secondNo = ValueUtil.toInteger(secondStr);
//			} catch(Exception e) {
//				firstNo = 111;
//				secondNo = 222;
//			}
//			
//			if(onSeqFlag) {
//				this.mpiSendService.requestCommonMpiOn(domainId, MpsConstants.JOB_TYPE_DAS, mpi.getMpiCd(), mpi.gatewayPath(), mpi.getMpiCd(), "pick", mpiColor, firstNo, secondNo);
//			} else {
//				JobProcess indOn = new JobProcess();
//				indOn.setComCd(comCd);
//				indOn.setBoxInQty(1);
//				indOn.setId(mpi.getId());
//				indOn.setMpiCd(mpi.getMpiCd());
//				indOn.setGwPath(mpi.gatewayPath());
//				indOn.setMpiColor(mpiColor);
//				indOn.setProcessSeq(firstNo);
//				indOn.setPickQty(secondNo);
//				indOn.setPickedQty(0);
//				jobList.add(indOn);
//			}
//		}
//		
//		if(!onSeqFlag) {
//			Map<String, List<IndicatorOnInformation>> indOnMap = MpsServiceUtil.buildMpiOnList(false, MpsConstants.JOB_TYPE_DAS, jobList, false);
//			this.mpiSendService.requestMpisOn(domainId, MpsConstants.JOB_TYPE_DAS, "pick", indOnMap);
//		}
//		
//		return ValueUtil.newMap("result,count", SysConstants.OK_STRING, mpiList.size());
//	}
//	
//	/**
//	 * MPI - 호기별 표시기 소등 
//	 * 
//	 * @param regionCd
//	 * @return
//	 */
//	@RequestMapping(value = "/mpi_off/{region_cd}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Light-off MPIs of Region")
//	public Map<String, Object> mpiOff(@PathVariable("region_cd") String regionCd) {
//		this.mpiSendService.requestMpiOffByRegion(Domain.currentDomainId(), regionCd, true);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	/**
//	 * MPI - 호기, 작업 존 별 표시기 단순 소등 
//	 * 
//	 * @param regionCd
//	 * @param zoneCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/off_all/{region_cd}/{equip_zone_cd}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Light-off MPIs of Equipment Work Zone")
//	public Map<String, Object> mpiLightOffByZone(@PathVariable("region_cd") String regionCd, @PathVariable("equip_zone_cd") String equipZoneCd) {
//		this.mpiSendService.requestMpiOffByEquipZone(Domain.currentDomainId(), regionCd, equipZoneCd, null);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	/**
//	 * MPI - 호기, 작업 존 별 표시기 단순 소등 
//	 * 
//	 * @param regionCd
//	 * @param zoneCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/on_all_equip_zone/{region_cd}/{equip_zone_cd}/{color}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Light-on MPIs of Equipment Work Zone")
//	public Map<String, Object> mpiLightOnByEquipZone(
//			@PathVariable("region_cd") String regionCd, 
//			@PathVariable("equip_zone_cd") String equipZoneCd,
//			@PathVariable("color") String color) {
//		
//		Long domainId = Domain.currentDomainId();
//		List<MpiOffReq> mpiCdList = this.mpiSendService.searchMpiByEquipZone(domainId, regionCd, equipZoneCd, null);
//		
//		for(MpiOffReq mpiInfo : mpiCdList) {
//			String mpiCd = mpiInfo.getMpiCd();
//			MPI mpi = MPI.findByCd(domainId, mpiCd);
//			String mpiNm = mpi.getMpiNm();
//			
//			int seg1Qty = 0;
//			int seg2Qty = 0;
//			try {
//				seg1Qty = ValueUtil.isEmpty(mpiNm) ? 0 : ValueUtil.toInteger(mpiNm.substring(0, 3));
//				seg2Qty = ValueUtil.isEmpty(mpiNm) ? 0 : ValueUtil.toInteger(mpiNm.substring(3));
//			} catch(Exception e) {
//			}
//			
//			this.mpiSendService.requestCommonMpiOn(domainId, MpsConstants.JOB_TYPE_DAS, mpiCd, mpiInfo.getGwPath(), mpiCd, "pick", color, seg1Qty, seg2Qty);
//		}
//		
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	/**
//	 * MPI - 호기, 작업 존 별 표시기 단순 소등 
//	 * 
//	 * @param regionCd
//	 * @param zoneCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/on_all_work_zone/{region_cd}/{zone_cd}/{color}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Light-on MPIs of Work Zone")
//	public Map<String, Object> mpiLightOnByWorkZone(
//			@PathVariable("region_cd") String regionCd, 
//			@PathVariable("zone_cd") String zoneCd,
//			@PathVariable("color") String color) {
//		
//		Long domainId = Domain.currentDomainId();
//		List<MpiOffReq> mpiCdList = this.mpiSendService.searchMpiByWorkZone(domainId, regionCd, zoneCd);
//		
//		for(MpiOffReq mpiInfo : mpiCdList) {
//			String mpiCd = mpiInfo.getMpiCd();
//			MPI mpi = MPI.findByCd(domainId, mpiCd);
//			String mpiNm = mpi.getMpiNm();
//			
//			int seg1Qty = 0;
//			int seg2Qty = 0;
//			try {
//				seg1Qty = ValueUtil.isEmpty(mpiNm) ? 0 : ValueUtil.toInteger(mpiNm.substring(0, 3));
//				seg2Qty = ValueUtil.isEmpty(mpiNm) ? 0 : ValueUtil.toInteger(mpiNm.substring(3));
//			} catch(Exception e) {
//			}
//			
//			this.mpiSendService.requestCommonMpiOn(domainId, MpsConstants.JOB_TYPE_DAS, mpiCd, mpiInfo.getGwPath(), mpiCd, "pick", color, seg1Qty, seg2Qty);
//		}
//		
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}	
//	
//	/**
//	 * Indicator On
//	 * 
//	 * @param mpiCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/indicator_on/{mpi_cd}/{color}/{box_qty}/{pcs_qty}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Indicator On")
//	public Map<String, Object> mpiIndicatorOn(
//			@PathVariable("mpi_cd") String mpiCd,
//			@PathVariable("color") String color,
//			@PathVariable("box_qty") Integer boxQty, 
//			@PathVariable("pcs_qty") Integer pcsQty) {
//		
//		Long domainId = Domain.currentDomainId();
//		IndicatorOnInformation mpiOnInfo = new IndicatorOnInformation();
//		mpiOnInfo.setId(mpiCd);
//		mpiOnInfo.setBizId("dca002b1-5b66-4ebd-a020-ea5edf93ddda");
//		mpiOnInfo.setColor(MpsSetting.getDefaultDasMpiColor(domainId));
//		mpiOnInfo.setOrgBoxQty(boxQty);
//		mpiOnInfo.setOrgEaQty(pcsQty);
//		mpiOnInfo.setColor(color.toUpperCase());
//		
//		List<IndicatorOnInformation> mpiOnList = ValueUtil.toList(mpiOnInfo);
//		Map<String, List<IndicatorOnInformation>> indicatorInfo = new HashMap<String, List<IndicatorOnInformation>>(1);
//		String gwPath = MPI.findGatewayPath(domainId, mpiCd);
//		indicatorInfo.put(gwPath, mpiOnList);
//		
//		this.mpiSendService.requestMpisOn(domainId, MpsConstants.JOB_TYPE_DAS, "test", indicatorInfo);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	@RequestMapping(value = "/common/mpi/fullbox/{mpi_cd}/{biz_id}/{color}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Fullbox")
//	public Map<String, Object> mpiFullbox(@PathVariable("mpi_cd") String mpiCd, @PathVariable("biz_id") String bizId, @PathVariable("color") String color) {
//		this.mpiSendService.requestFullbox(Domain.currentDomainId(), MpsConstants.JOB_TYPE_DAS, mpiCd, bizId, color);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	@RequestMapping(value = "/common/mpi/end/{mpi_cd}/{biz_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - End")
//	public Map<String, Object> mpiEnd(@PathVariable("mpi_cd") String mpiCd, @PathVariable("biz_id") String bizId) {
//		this.mpiSendService.requestMpiEndDisplay(Domain.currentDomainId(), MpsConstants.JOB_TYPE_DAS, mpiCd, bizId, true);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	@RequestMapping(value = "/common/mpi/display/{mpi_cd}/{biz_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Display")
//	public Map<String, Object> mpiDisplay(@PathVariable("mpi_cd") String mpiCd, @PathVariable("biz_id") String bizId) {
//		this.mpiSendService.requestMpiDisplayOnly(Domain.currentDomainId(), MpsConstants.JOB_TYPE_DAS, mpiCd, bizId, 123);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	/**
//	 * Indicator Off
//	 * 
//	 * @param mpiCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/indicator_off/{mpi_cd}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - Mpi Off")
//	public Map<String, Object> mpiIndicatorOff(@PathVariable("mpi_cd") String mpiCd) {
//		this.mpiSendService.requestMpiOff(Domain.currentDomainId(), mpiCd);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
//	
//	/**
//	 * LED On
//	 * 
//	 * @param mpiCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/led_on/{mpi_cd}/{brightness}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - LED On by MPI")
//	public Map<String, Object> mpiLedOn(@PathVariable("mpi_cd") String mpiCd, @PathVariable("brightness") String brightness) {
//		this.mpiSendService.requestLedOn(Domain.currentDomainId(), mpiCd, ValueUtil.toInteger(brightness));
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}	
//	
//	/**
//	 * LED Off
//	 * 
//	 * @param mpiCd
//	 * @return
//	 */
//	@RequestMapping(value = "/common/mpi/led_off/{mpi_cd}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Common - LED Off of MPI")
//	public Map<String, Object> mpiLedOff(@PathVariable("mpi_cd") String mpiCd) {
//		this.mpiSendService.requestLedOff(Domain.currentDomainId(), mpiCd);
//		return ValueUtil.newMap("result", SysConstants.OK_STRING);
//	}
	
}