package xyz.anythings.gw.service.api;

import xyz.anythings.gw.entity.Deployment;

/**
 * 펌웨어 배포 관련 서비스 API.
 * 
 * @author shortstop
 */
public interface IFirmwareDeployService {

	/**
	 * 펌웨어 배포
	 * 
	 * @param deployment
	 */
	public void deployFirmware(Deployment deployment);
}
