//package xyz.anythings.gw.rest.bak;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import xyz.anythings.gw.entity.bak.AttachTemp;
//import xyz.anythings.gw.entity.bak.Deployment;
//import xyz.elidom.core.CoreMessageConstants;
//import xyz.elidom.dbist.dml.Page;
//import xyz.elidom.exception.server.ElidomServiceException;
//import xyz.elidom.orm.system.annotation.service.ApiDesc;
//import xyz.elidom.orm.system.annotation.service.ServiceDesc;
//import xyz.elidom.sys.system.service.AbstractRestService;
//import xyz.elidom.sys.util.ThrowUtil;
//import xyz.elidom.sys.util.ValueUtil;
//
//@RestController
//@Transactional
//@ResponseStatus(HttpStatus.OK)
//@RequestMapping("/rest/deployment")
//@ServiceDesc(description = "Deployment Service API")
//public class DeploymentController extends AbstractRestService {
//	/**
//	 * Logger
//	 */
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	
//	@Override
//	protected Class<?> entityClass() {
//		return Deployment.class;
//	}
//
//	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Search (Pagination) By Search Conditions")
//	public Page<?> index(@RequestParam(name = "page", required = false) Integer page,
//			@RequestParam(name = "limit", required = false) Integer limit,
//			@RequestParam(name = "select", required = false) String select,
//			@RequestParam(name = "sort", required = false) String sort,
//			@RequestParam(name = "query", required = false) String query) {
//		return this.search(this.entityClass(), page, limit, select, sort, query);
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Find one by ID")
//	public Deployment findOne(@PathVariable("id") String id) {
//		return this.getOne(this.entityClass(), id);
//	}
//
//	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Check exists By ID")
//	public Boolean isExist(@PathVariable("id") String id) {
//		return this.isExistOne(this.entityClass(), id);
//	}
//
//	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseStatus(HttpStatus.CREATED)
//	@ApiDesc(description = "Create")
//	public Deployment create(@RequestBody Deployment input) {
//		return this.createOne(input);
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Update")
//	public Deployment update(@PathVariable("id") String id, @RequestBody Deployment input) {
//		return this.updateOne(input);
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Delete")
//	public void delete(@PathVariable("id") String id) {
//		this.deleteOne(this.getClass(), id);
//	}
//
//	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Create, Update or Delete multiple at one time")
//	public Boolean multipleUpdate(@RequestBody List<Deployment> list) {
//		return this.cudMultipleData(this.entityClass(), list);
//	}
//	
//	@RequestMapping(value = "/reserve_list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Reserve multiple at one time")
//	public Map<String, Object> multipleReserve(@RequestBody List<String> deploymentIdList) {
//		for(String deploymentId : deploymentIdList) {
//			this.reserve(deploymentId);
//		}
//		
//		return ValueUtil.newMap("success", true);
//	}
//	
//	@RequestMapping(value = "/deploy_list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiDesc(description = "Deploy multiple at one time")
//	public Map<String, Object> multipleDeploy(@RequestBody List<String> deploymentIdList) {
//		for(String deploymentId : deploymentIdList) {
//			this.deployNow(deploymentId);
//		}
//		
//		return ValueUtil.newMap("success", true);
//	}
//	
//	@RequestMapping(value = "/reserve/{id}", method = RequestMethod.POST)
//	public Map<String, Object> reserve(@PathVariable("id") String id) {
//		Deployment deployment = this.queryManager.select(Deployment.class, id);
//		if(deployment == null) {
//			throw ThrowUtil.newNotFoundRecord("terms.menu.Deployment", id);
//		}
//		
//		/*Boolean success = deployment.reserve();
//		return ValueUtil.newMap("success", success);*/
//		
//		throw ThrowUtil.newValidationErrorWithNoLog("이 기능은 지원하지 않습니다.");
//	}
//	
//	@RequestMapping(value = "/deploy_now/{id}", method = RequestMethod.POST)
//	public Map<String, Object> deployNow(@PathVariable("id") String id) {
//		Deployment deployment = this.queryManager.select(Deployment.class, id);
//		if(deployment == null) {
//			throw ThrowUtil.newNotFoundRecord("terms.menu.Deployment", id);
//		}
//		
//		Boolean success = deployment.deployNow();
//		return ValueUtil.newMap("success", success);
//	}
//	
//	@RequestMapping(value = "/upload_file", method = RequestMethod.POST)
//	public @ResponseBody Map<String, Object> create(
//			HttpServletRequest req, 
//			HttpServletResponse res,
//			@RequestParam("file") MultipartFile[] files) {
//		
//		if(files == null || files.length == 0) {
//			return ValueUtil.newMap("attach_temp_id,file_name,error,msg", "", "", "첨부파일 업로드 실패", "파일 내용 없음");
//		}
//		
//		MultipartFile file = files[0];
//		if(files[0].getSize() == 0) {
//			return ValueUtil.newMap("attach_temp_id,file_name,error,msg", "", "", "첨부파일 업로드 실패", "파일 내용 없음");
//		}
//		
//		AttachTemp attachTemp = new AttachTemp();
//		attachTemp.setFileName(file.getOriginalFilename());
//		attachTemp.setFileSize(file.getSize());
//		
//		try {
//			byte[] fileData = file.getBytes();
//			attachTemp.setFileData(fileData);
//		} catch (IOException e) {
//			this.logger.error("Failed to read attachment file!", e);
//			return ValueUtil.newMap("attach_temp_id,file_name,error,msg", "", "", "첨부파일 업로드 실패", e.getMessage());
//		}
//		
//		this.queryManager.insert(attachTemp);
//		return ValueUtil.newMap("attach_temp_id,file_name,file_size", attachTemp.getId(), attachTemp.getFileName(), attachTemp.getFileSize());
//	}
//	
//	/**
//	 * Deployment ID를 받아서 파일을 다운로드한다.  
//	 * 
//	 * @param req
//	 * @param res
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/download_file/{id}")
//	public @ResponseBody Object downloadFile(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") String id) {
//		// 게이트웨이에서 다운로드시에는 도메인 정보가 없기 때문에 native query로 배포정보를 조회한다.
//		Deployment deployment = this.queryManager.selectBySql("select * from tb_deployment where id = :id", ValueUtil.newMap("id", id), Deployment.class);
//		String name = deployment.getFileName();
//		
//		// Setting Response Header
//		res.setCharacterEncoding("UTF-8");
//		res.setContentType("text/plain;charset=UTF-8");
//		res.addHeader("Content-Type", "application/octet-stream");
//		res.addHeader("Content-Transfer-Encoding", "binary;");
//		res.addHeader("Content-Length", Long.toString(deployment.getFileSize()));
//		res.setHeader("Pragma", "cache");
//		res.setHeader("Cache-Control", "public");
//		
//		ServletOutputStream outStream = null;
//		ByteArrayInputStream inStream = null;
//		byte[] buffer = new byte[4096];
//		
//		try {
//			res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
//			outStream = res.getOutputStream();
//			
//			inStream = new ByteArrayInputStream(deployment.getFileData());
//			int startIndex = 0;
//			int readCount = 0;
//			
//			do {
//				readCount = inStream.read(buffer);
//				if(readCount == -1) {
//					break;
//				}
//				
//				outStream.write(deployment.getFileData(), startIndex, readCount);
//				startIndex = startIndex + readCount;
//				
//			} while(true);
//			
//			outStream.flush();
//
//		} catch (Exception e) {
//			throw new ElidomServiceException(CoreMessageConstants.FILE_DOWNLOAD_ERROR, "File Download 실행 중, 에러가 발생하였습니다.", e);	
//		}
//		
//		return id;
//	}	
//
//}