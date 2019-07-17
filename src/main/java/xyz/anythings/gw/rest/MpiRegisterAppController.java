package xyz.anythings.gw.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.anythings.base.entity.Location;
import xyz.anythings.gw.LogisGwConfigConstants;
import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.gw.entity.MPI;
import xyz.anythings.gw.model.IndicatorAlternation;
import xyz.anythings.gw.service.MwSender;
import xyz.anythings.sys.AnyConstants;
import xyz.elidom.exception.ElidomException;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sec.rest.LoginController;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.ExceptionUtil;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@ServiceDesc(description = "MPI Register App Service API")
@RequestMapping("/rest/mpi_app")
public class MpiRegisterAppController extends AbstractRestService {
	
	@Autowired
	private LoginController loginCtrl;
	
	@Autowired
	private MwSender mwMsgSender;
	
	@Override
	protected Class<?> entityClass() {
		return MPI.class;
	}	
	
	@RequestMapping(value = "/signin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Login")
	public Map<String, Object> signin(
			HttpServletRequest req, 
			HttpServletResponse res, 
			@RequestParam(name = "email") String login, 
			@RequestParam(name = "password") String password,
			@RequestParam(name = "domainId", required = false) Long domainId) {
		
		try {
			boolean isValidUser = true;
			if(login.indexOf(AnyConstants.CHAR_UNDER_SCORE) < 3) {
				isValidUser = false;
			}
			
			if(isValidUser) {
				String siteCode = login.substring(0, login.indexOf(AnyConstants.CHAR_UNDER_SCORE));
				Domain domain = this.queryManager.selectByCondition(Domain.class, ValueUtil.newMap("name", siteCode.toUpperCase()));
				if(domain == null) {
					isValidUser = false;
				} else {
					domainId = domain.getId();
				}
			}
			
			if(!isValidUser) {
				throw new BadCredentialsException("표시기 앱을 위한 계정이 아닙니다.");
			}
			
			return this.loginCtrl.login(req, res, login, password, domainId);
			
		} catch(BadCredentialsException ex) {
			ElidomException ee = ExceptionUtil.wrapElidomException(ex);
			String detail = (ex.getCause() != null ? ex.getCause() : ex).getMessage();
			return ValueUtil.newMap("status,code,msg,detail", 401, ee.getTitle(), ee.getMessage(), detail);
			
		} catch(Exception e) {
			// TODO 사용자 계정 잠김 등 메시지 확인 필요...
			ElidomException ee = ExceptionUtil.wrapElidomException(e);
			String detail = (e.getCause() != null ? e.getCause() : e).getMessage();
			return ValueUtil.newMap("status,code,msg,detail", 401, ee.getTitle(), ee.getMessage(), detail);
		}
	}
	
	@RequestMapping(value = "/signout", method = RequestMethod.POST)
	@ApiDesc(description = "Logout")
	public Map<String, Object> signout(HttpServletRequest req, HttpServletResponse res) {
		boolean result = this.loginCtrl.logout(req, res);
		return ValueUtil.newMap("result", result ? AnyConstants.OK_STRING : AnyConstants.NG_STRING);
	}	

	/**
	 * 표시기 코드 유효성 체크
	 * 
	 * @param mpiCd
	 * @return
	 */
	private boolean checkMpiCd(String mpiCd) {
		if(ValueUtil.isEmpty(mpiCd) || (mpiCd.length() != 6 && mpiCd.length() != 8) || (mpiCd.length() == 8 && !mpiCd.startsWith("01"))) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 실제 MPI 코드를 리턴
	 * 
	 * @param mpiCd
	 * @return
	 */
	private String getRealMpiCd(String mpiCd) {
		if(mpiCd != null) {
			return mpiCd.length() == 8 ? mpiCd.substring(2) : mpiCd;
		} else {
			return mpiCd;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/find_equip/{equip_cd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find Equipment Code")
	public Object checkEquipment(@PathVariable("equip_cd") String equipCd) {
		if(ValueUtil.isEmpty(equipCd) || (!equipCd.startsWith("01") && !equipCd.startsWith("02"))) {
			return ValueUtil.newMap("id", "none");
		}
				
		boolean isGateway = equipCd.startsWith("02");
		equipCd = equipCd.substring(2);
		
		if(isGateway) {
			Gateway gw = this.findGateway(equipCd);
			if(gw == null) {
				return ValueUtil.newMap("id", "none");
			} else {
				return ValueUtil.newMap("id,gw_cd,gw_ip,channel_no,pan_no,version,gw_zone_cd", gw.getId(), gw.getGwCd(), gw.getGwIp(), gw.getChannelNo(), gw.getPanNo(), gw.getVersion(), gw.getZoneCd());
			}
		} else {
			Map<String, Object> params = ValueUtil.newMap("domainId,mpiCd", Domain.currentDomainId(), equipCd);
			String sql = "select m.id, m.mpi_cd, m.gw_cd, m.version, r.region_cd, r.region_nm, l.loc_cd, l.gw_zone_cd from tb_mpi m left outer join tb_location l on m.domain_id = l.domain_id and m.mpi_cd = l.mpi_cd left outer join tb_region r on l.domain_id = r.domain_id and l.region_cd = r.region_cd where m.domain_id = :domainId and m.mpi_cd = :mpiCd";
			Map mpi = this.queryManager.selectBySql(sql, params, Map.class);
			
			if(mpi == null) {
				return ValueUtil.newMap("id", "none");
			} else {
				return mpi;
			}
		}
	}
	
	@RequestMapping(value = "/find_loc/{loc_cd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find Location by location code")
	public Object checkLocation(@PathVariable("loc_cd") String locCd) {
		// 1. locCd로 로케이션을 조회
		Object loc = this.findLocation(locCd);
		
		// 2. 존재하지 않으면 '로케이션이 존재하지 않습니다.'
		if(loc instanceof Map) {
			return loc;
		}
		
		// 3. 로케이션이 존재하면
		Location location = (Location)loc;
		Long domainId = Domain.currentDomainId();
		String query = "select gw_cd from tb_gateway where domain_id = :domainId and zone_cd = :gwZoneCd";
		Map<String, Object> gwParams = ValueUtil.newMap("domainId,gwZoneCd", domainId, location.getGwZoneCd());
		String gwCd = this.queryManager.selectBySql(query, gwParams, String.class);
		String sql = "select region_nm from tb_region where domain_id = :domainId and region_cd = :regionCd";
		Map<String, Object> regionParams = ValueUtil.newMap("domainId,regionCd", domainId, location.getRegionCd());
		String regionNm = this.queryManager.selectBySql(sql, regionParams, String.class);		
		
		// 4. 이미 등록된 MPI가 있는지 체크 - 있으면 등록된 MPI 정보를 리턴 
		if(ValueUtil.isNotEmpty(location.getMpiCd()) && this.getMpiCount(location.getMpiCd()) > 0) {
			return ValueUtil.newMap("result,mpi_cd,loc_cd,region_cd,region_nm,gw_zone_cd,gw_cd", "change", location.getMpiCd(), location.getLocCd(), location.getRegionCd(), regionNm, location.getGwZoneCd(), gwCd);				
		// 5. 없으면 Location 정보를 리턴
		} else {
			return ValueUtil.newMap("result,id,loc_cd,region_cd,region_nm,gw_zone_cd,gw_cd", SysConstants.OK_STRING, location.getId(), location.getLocCd(), location.getRegionCd(), regionNm, location.getGwZoneCd(), gwCd);
		}
	}
	
	/**
	 * 로케이션 - 표시기/게이트웨이 등록 
	 * 
	 * @param mpiCd
	 * @param locCd
	 * @return
	 */
	@RequestMapping(value = "/register_mpi/{mpi_cd}/{loc_cd}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Register equipment to location")
	public Object registerEquipment(@PathVariable("mpi_cd") String mpiCd, @PathVariable("loc_cd") String locCd) {
		// 1. MPI 코드 체크
		if(!this.checkMpiCd(mpiCd)) {
			return ValueUtil.newMap("result,msg", AnyConstants.NG_STRING, "MPI 코드가 유효하지 않습니다.");
		}
		
		mpiCd = this.getRealMpiCd(mpiCd);
		
		// 2. 로케이션 코드로 로케이션 조회 
		Object loc = this.findLocation(locCd);
		
		// 3. 로케이션 코드로 로케이션 조회시 존재하지 않은 경우 에러 메시지 리턴 
		if(loc instanceof Map) {
			return loc;
		}
		
		// 4. MPI가 이미 등록되어 있는지 체크 
		MPI mpi = this.findMpi(mpiCd);
		
		// 5. 조회한 로케이션으로 게이트웨이 조회 
		Location location = (Location)loc;
		String gwCd = null;
		
		if(ValueUtil.isNotEmpty(location.getGwZoneCd())) {
			String sql = "select gw_cd from (select gw_cd from tb_gateway where domain_id = :domainId and zone_cd = :gwZoneCd) where rownum <= 1";
			Map<String, Object> params = ValueUtil.newMap("domainId,gwZoneCd", location.getDomainId(), location.getGwZoneCd());
			gwCd = this.queryManager.selectBySql(sql, params, String.class);
		}
		
		// 6. MPI 등록
		if(mpi == null) {
			mpi = new MPI();
			mpi.setDomainId(location.getDomainId());
			mpi.setMpiCd(mpiCd);
			mpi.setMpiNm(locCd);
			mpi.setGwCd(gwCd);
			this.queryManager.insert(mpi);
		} else {
			mpi.setGwCd(gwCd);
			this.queryManager.update(mpi, "gwCd", "updaterId", "updatedAt");
		}
		
		// 7. 로케이션의 표시기 정보 업데이트
		location.setMpiCd(mpiCd);
		this.queryManager.update(location, "mpiCd", "updaterId", "updatedAt");
		
		// 8. 결과 메시지 리턴 
		return ValueUtil.newMap("result", SysConstants.OK_STRING);
	}
	
	/**
	 * 표시기 교체 
	 * 
	 * @param prevMpiCd
	 * @param newMpiCd
	 * @return
	 */
	@RequestMapping(value = "/change_mpi/{prev_mpi_cd}/{new_mpi_cd}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Change MPI")
	public Object changeMpi(@PathVariable("prev_mpi_cd") String prevMpiCd, @PathVariable("new_mpi_cd") String newMpiCd) {
		// 1. 이전 MPI 코드 체크
		if(!this.checkMpiCd(prevMpiCd)) {
			return ValueUtil.newMap("success,result,msg", false, AnyConstants.NG_STRING, "이전 MPI 코드가 유효하지 않습니다.");
		}
		
		// 2. 새로운 MPI 코드 체크
		if(!this.checkMpiCd(newMpiCd)) {
			return ValueUtil.newMap("success,result,msg", false, AnyConstants.NG_STRING, "교체할 MPI 코드가 유효하지 않습니다.");
		}
		
		// 3. 실제 MPI 코드 
		prevMpiCd = this.getRealMpiCd(prevMpiCd);
		newMpiCd = this.getRealMpiCd(newMpiCd);
		
		// 4. 이전 표시기 코드로 로케이션 조회
		Location loc = Location.findByMpiCd(Domain.currentDomainId(), prevMpiCd, false, false);
		if(loc == null) {
			return ValueUtil.newMap("success,result,msg", false, AnyConstants.NG_STRING, "표시기에 로케이션이 매핑되어 있지 않아서 로케이션을 찾을 수 없습니다.");
		}
		
		// 5. 기존에 로케이션에 매핑된 MPI를 찾아서 게이트웨이 설정을 null로 업데이트
		String gwCd = null;
		Boolean isPrevMpiExist = false;
		
		// 미들웨어로 보낼 메시지 
		IndicatorAlternation indAlt = new IndicatorAlternation();
		
		// 6. 기존 표시기 코드가 있다면 삭제 
		if(ValueUtil.isNotEmpty(loc.getMpiCd())) {
			MPI prevMpi = this.findMpi(prevMpiCd);
			
			if(prevMpi != null) {
				isPrevMpiExist = true;
				gwCd = prevMpi.getGwCd();
				this.queryManager.delete(prevMpi);
				indAlt.setFrom(prevMpiCd);
			}
		}
		
		// 7. 새로운 MPI 코드가 이미 등록되어 있는지 체크하여 존재하지 않으면 추가
		MPI newMpi = this.findMpi(newMpiCd);
		
		if(newMpi == null) {
			newMpi = new MPI();
			newMpi.setDomainId(loc.getDomainId());
			newMpi.setMpiCd(newMpiCd);
			newMpi.setMpiNm(loc.getLocCd());
			newMpi.setGwCd(gwCd);
			this.queryManager.insert(newMpi);
			
		} else {
			newMpi.setGwCd(gwCd);
			this.queryManager.update(newMpi, "gwCd", "updaterId", "updatedAt");
		}
		
		// 8. 로케이션 정보에 표시기 정보 업데이트
		loc.setMpiCd(newMpiCd);
		this.queryManager.update(loc, "mpiCd", "updaterId", "updatedAt");
		indAlt.setTo(newMpi.getId());
		
		// 9. 기존 MPI가 존재하고, 설정되어 있으면 게이트웨이에 표시기 교체 메시지 보냄 
		if(isPrevMpiExist && ValueUtil.toBoolean(SettingUtil.getValue(LogisGwConfigConstants.MPI_ALTER_MESSAGE_ENABLED, AnyConstants.FALSE_STRING))) {
			Map<String, Object> params = ValueUtil.newMap("domainId,gwCd", loc.getDomainId(), gwCd);
			Gateway gw = this.queryManager.selectByCondition(Gateway.class, params);
			this.mwMsgSender.sendRequest(loc.getDomainId(), gw.getGwNm(), indAlt);
		}

		// 10. 결과 리턴 
		return ValueUtil.newMap("success,result", true, SysConstants.OK_STRING);
	}
	
	/**
	 * 표시기 교체 
	 * 
	 * @param mpiCd
	 * @param locCd
	 * @return
	 */
	@RequestMapping(value = "/change_mpi_loc/{mpi_cd}/{loc_cd}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Change equipment location")
	public Object changeEquipment(@PathVariable("mpi_cd") String mpiCd, @PathVariable("loc_cd") String locCd) {
		// 1. MPI 코드 체크
		if(!this.checkMpiCd(mpiCd)) {
			return ValueUtil.newMap("result,msg", AnyConstants.NG_STRING, "MPI 코드가 유효하지 않습니다.");
		}
				
		mpiCd = this.getRealMpiCd(mpiCd);
		
		// 2. 로케이션 코드로 로케이션 조회
		Object loc = this.findLocation(locCd);
		
		// 3. 로케이션 코드로 로케이션 조회시 존재하지 않은 경우 에러 메시지 리턴 
		if(loc instanceof Map) {
			return loc;
		}
		
		// 4. 기존에 로케이션에 매핑된 MPI를 찾아서 게이트웨이 설정을 null로 업데이트
		Location location = (Location)loc;
		String gwCd = null;
		String prevMpiCd = null;
		Boolean isPrevMpiExist = false;
		
		// 미들웨어로 보낼 메시지 
		IndicatorAlternation indAlt = new IndicatorAlternation();
		
		if(ValueUtil.isNotEmpty(location.getMpiCd())) {
			MPI prevMpi = this.findMpi(location.getMpiCd());
			
			if(prevMpi != null) {
				isPrevMpiExist = true;
				gwCd = prevMpi.getGwCd();
				prevMpiCd = prevMpi.getMpiCd();
				this.queryManager.delete(prevMpi);
				indAlt.setFrom(prevMpiCd);
			}
		}
		
		// 5. 새로운 MPI 코드가 이미 등록되어 있는지 체크하여 존재하지 않으면 추가
		MPI newMpi = this.findMpi(mpiCd);
		
		if(newMpi == null) {
			newMpi = new MPI();
			newMpi.setDomainId(location.getDomainId());
			newMpi.setMpiCd(mpiCd);
			newMpi.setMpiNm(locCd);
			newMpi.setGwCd(gwCd);
			this.queryManager.insert(newMpi);
			
		} else {
			newMpi.setGwCd(gwCd);
			this.queryManager.update(newMpi, "gwCd", "updaterId", "updatedAt");
		}
		
		// 6. 로케이션 정보에 표시기 정보 업데이트
		location.setMpiCd(mpiCd);
		this.queryManager.update(location, "mpiCd", "updaterId", "updatedAt");
		
		indAlt.setTo(newMpi.getId());
		// 7. 기존 MPI가 존재하고, 설정되어 있으면 게이트웨이에 표시기 교체 메시지 보냄 
		if(isPrevMpiExist && ValueUtil.toBoolean(SettingUtil.getValue(LogisGwConfigConstants.MPI_ALTER_MESSAGE_ENABLED, AnyConstants.FALSE_STRING))) {
			Map<String, Object> params = ValueUtil.newMap("domainId,gwCd", location.getDomainId(), gwCd);
			Gateway gw = this.queryManager.selectByCondition(Gateway.class, params);
			this.mwMsgSender.sendRequest(location.getDomainId(), gw.getGwNm(), indAlt);
		}

		// 8. 결과 리턴 
		return ValueUtil.newMap("result", SysConstants.OK_STRING);
	}
	
	/**
	 * 표시기 제거
	 * 
	 * @param mpiCd
	 * @return
	 */
	@RequestMapping(value = "/unregister_mpi/{mpi_cd}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Unregister equipment location")
	public Object unregisterMpi(@PathVariable("mpi_cd") String mpiCd) {
		// 1. MPI 코드 체크
		if(!this.checkMpiCd(mpiCd)) {
			return ValueUtil.newMap("result,msg", AnyConstants.NG_STRING, "MPI 코드가 유효하지 않습니다.");
		}
		
		mpiCd = this.getRealMpiCd(mpiCd);
		MPI mpi = this.findMpi(mpiCd);
		String result = SysConstants.OK_STRING;
		String msg = null;
		
		if(mpi instanceof MPI) {
			Location loc = this.findLocationByMpi(mpiCd);
			if(loc != null) {
				loc.setMpiCd(SysConstants.EMPTY_STRING);
				this.queryManager.update(loc, "mpiCd", "updaterId", "updatedAt");
			}
			
			mpi.setGwCd(SysConstants.EMPTY_STRING);
			this.queryManager.delete(mpi);
			return ValueUtil.newMap("result", result);
			
		} else {
			result = AnyConstants.NG_STRING;
			msg = "해당 기기[" + mpiCd + "]는 서버에 등록되어 있지 않습니다.";
			return ValueUtil.newMap("result,msg", result, msg);
		}
	}
	
	/**
	 * 로케이션 조회 
	 * 
	 * @param locCd
	 * @return
	 */
	private Object findLocation(String locCd) {
		Location loc = null;
		try {
			Map<String, Object> params = ValueUtil.newMap("domainId,locCd", Domain.currentDomainId(), locCd);
			loc = BeanUtil.get(IQueryManager.class).selectByCondition(Location.class, params);
			
			if(loc == null) {
				ThrowUtil.newNotFoundRecord("terms.menu.Location", locCd);
			}
		} catch(Exception e) {
			return ValueUtil.newMap("result,msg", AnyConstants.NG_STRING, e.getMessage());
		}
		
		if(loc == null) {
			return ValueUtil.newMap("result,msg", AnyConstants.NG_STRING, "로케이션 [" + locCd + "]는 존재하지 않습니다.");
		} else {
			return loc;
		}
	}
	
	/**
	 * mpiCd로 로케이션 조회
	 * 
	 * @param mpiCd
	 * @return
	 */
	private Location findLocationByMpi(String mpiCd) {
		Map<String, Object> params = ValueUtil.newMap("domainId,mpiCd", Domain.currentDomainId(), mpiCd);
		return BeanUtil.get(IQueryManager.class).selectByCondition(Location.class, params);
	}
	
	/**
	 * mpiCd로 MPI 조회
	 * 
	 * @param mpiCd
	 * @return
	 */
	private MPI findMpi(String mpiCd) {
		Map<String, Object> params = ValueUtil.newMap("domainId,mpiCd", Domain.currentDomainId(), mpiCd);
		return BeanUtil.get(IQueryManager.class).selectByCondition(MPI.class, params);
	}
	
	/**
	 * gwCd로 게이트웨이 조회
	 * 
	 * @param gwCd
	 * @return
	 */
	private Gateway findGateway(String gwCd) {		
		Map<String, Object> params = ValueUtil.newMap("domainId,gwCd", Domain.currentDomainId(), gwCd);
		return BeanUtil.get(IQueryManager.class).selectByCondition(Gateway.class, params);
	}
	
	/**
	 * mpiCd 개수 조회
	 * 
	 * @param mpiCd
	 * @return
	 */
	private int getMpiCount(String mpiCd) {
		Map<String, Object> params = ValueUtil.newMap("domainId,mpiCd", Domain.currentDomainId(), mpiCd);
		String sql = "select count(*) as cnt from tb_mpi where domain_id = :domainId and mpi_cd = :mpiCd";
		return this.queryManager.selectSizeBySql(sql, params);
	}

}
