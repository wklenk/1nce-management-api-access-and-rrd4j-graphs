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
package org.klenk.connectivity.iot.oncemanagementapi.controller;

import org.klenk.connectivity.iot.oncemanagementapi.service.Rrd4jService;
import org.rrd4j.core.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@RestController
public class Rrd4jController {

    public static final int IMAGE_WIDTH = 864;
    public static final int IMAGE_HEIGHT = 480;
    @Autowired
    private Rrd4jService service;

    @GetMapping(value="/", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    byte[] getGraph() throws IOException {

        Instant now = Instant.now();
        Instant twentyFourHoursAgo = now.minus(24, ChronoUnit.HOURS);

        BufferedImage bim = service.createBufferedImageOfGraph(
                Util.getTimestamp(new Date(twentyFourHoursAgo.toEpochMilli())),
                Util.getTimestamp(new Date(now.toEpochMilli())),
                IMAGE_WIDTH,
                IMAGE_HEIGHT
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bim, "png", baos);

            baos.flush();
            return baos.toByteArray();
        }
    }
}
