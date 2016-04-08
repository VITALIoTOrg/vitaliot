package eu.vital.orchestrator.job;

import eu.vital.orchestrator.service.AdminService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;

@DisallowConcurrentExecution
public class SyncSystemsJob implements Job {

	@Inject
	AdminService adminService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		adminService.syncSystems();
	}

}

