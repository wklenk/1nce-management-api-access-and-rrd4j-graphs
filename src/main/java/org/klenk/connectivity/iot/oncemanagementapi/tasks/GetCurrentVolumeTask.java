package org.klenk.connectivity.iot.oncemanagementapi.tasks;

import lombok.extern.slf4j.Slf4j;
import org.klenk.connectivity.iot.oncemanagementapi.service.Rrd4jService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class GetCurrentVolumeTask {

    private final Rrd4jService rrd4jService;

    public GetCurrentVolumeTask(Rrd4jService rrd4jService) {
        this.rrd4jService = rrd4jService;
    }

    @Scheduled(fixedRate = 300000, initialDelay = 10000)
    public void getCurrentSimCardVolume() throws IOException {
        rrd4jService.fetchAndUpdateConsumption();
    }
}
