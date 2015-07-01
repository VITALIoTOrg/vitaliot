package eu.vital.orchestrator.job;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class SchedulerBean {

	private static final String propertiesFile = "quartz/quartz.properties";

	@Inject
	private Logger log;

	private Scheduler scheduler;

	@Inject
	private JobFactory cdiJobFactory;

	@PostConstruct
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void createQuartzService() throws IllegalStateException {
		try {
			Properties properties = loadProperties(propertiesFile);
			StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
			schedulerFactory.initialize(properties);

			scheduler = schedulerFactory.getScheduler();
			scheduler.setJobFactory(cdiJobFactory);
			scheduler.start();

		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to initialize Scheduler", e);
			throw new IllegalStateException("Failed to initialize Scheduler - ", e);
		}
		log.info("QuartzService created.");
	}

	@PreDestroy
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void destroyService() throws IllegalStateException {
		try {
			log.info("Destroy QuartzService...");
			scheduler.shutdown();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to initialize Scheduler", e);
			throw new IllegalStateException("Failed to destroy Scheduler - ", e);
		}
		log.info("QuartzService destroyed.");
	}

	private Properties loadProperties(String propFileName) throws IOException {
		Properties props = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream == null) {
			throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
		}
		props.load(inputStream);
		return props;
	}

}
