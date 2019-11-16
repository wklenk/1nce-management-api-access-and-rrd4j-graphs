/*
  1nce-management-api-access-and-rrd4j-graphs - Simple solution to read out NB-IoT data traffic consumption from
  the 1NCE management API, store it in a round-robin database and show the result in a graph.

  Copyright (C) 2019  Wolfgang Klenk <wolfgang.klenk@gmail.com>

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.

*/
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
