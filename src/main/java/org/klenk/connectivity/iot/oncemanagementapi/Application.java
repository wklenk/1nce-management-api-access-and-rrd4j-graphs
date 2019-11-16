package org.klenk.connectivity.iot.oncemanagementapi;

import lombok.extern.slf4j.Slf4j;
import org.klenk.connectivity.iot.oncemanagementapi.service.Rrd4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application implements ApplicationRunner {

	@Autowired
	Rrd4jService rrd4jService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		rrd4jService.createRrdDb(args.containsOption("override"));
	}
}
