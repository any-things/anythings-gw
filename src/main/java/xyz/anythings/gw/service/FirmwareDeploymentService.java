package xyz.anythings.gw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.gw.entity.Deployment;
import xyz.anythings.gw.entity.Gateway;
import xyz.anythings.sys.service.AbstractQueryService;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.sys.util.ValueUtil;

/**
 * 펌웨어 배포 관련 서비스.
 * 
 * @author shortstop
 */
@Component
public class FirmwareDeploymentService extends AbstractQueryService {

	@Autowired
	private MpiSendService mpiSendService;
	
	/**
	 * 펌웨어 배포
	 * 
	 * @param deployment
	 */
	public void deployFirmware(Deployment deployment) {
		Long domainId = deployment.getDomainId();
		String gwCd = deployment.getTargetId();
		
		// 1. Gateway 조회 
		Query condition = AnyOrmUtil.newConditionForExecution(domainId);
		condition.addFilter("gwCd", gwCd);
		Gateway gw = this.queryManager.selectByCondition(Gateway.class, condition);
		
		// 2. Gateway 펌웨어 배포
		if(ValueUtil.isEqualIgnoreCase(deployment.getTargetType(), Deployment.TARGET_TYPE_GW)) {
			this.mpiSendService.deployGatewayFirmware(domainId, gw.getGwNm(), deployment.getVersion(), deployment.computeDownloadUrl(), deployment.getFileName(), deployment.getForceFlag());
		// 3. Gateway 펌웨어 배포			
		} else {
			this.mpiSendService.deployMpiFirmware(domainId, gw.getGwNm(), deployment.getVersion(), deployment.computeDownloadUrl(), deployment.getFileName(), deployment.getForceFlag());
		}
	}
	
}
