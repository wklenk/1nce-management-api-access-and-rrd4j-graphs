package org.klenk.connectivity.iot.oncemanagementapi.service;

import lombok.extern.slf4j.Slf4j;
import org.klenk.connectivity.iot.oncemanagementapi.configuration.OnceRestTemplateConfig;
import org.klenk.connectivity.iot.oncemanagementapi.model.QuotaDataResponse;
import org.klenk.connectivity.iot.oncemanagementapi.model.SimResponse;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.rrd4j.data.Variable;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Service
public class Rrd4jService {

    public static final String HTTPS_PORTAL_1NCE_COM_MANAGEMENT_API_SIMS = "https://portal.1nce.com/management-api/v1/sims";
    public static final String HTTPS_PORTAL_1NCE_COM_MANAGEMENT_API_SIMS_QUOTA_DATA = "https://portal.1nce.com/management-api/v1/sims/{iccid}/quota/data";

    public static final String RRDPATH = "./data-traffic-consumption.rrd";

    private static final Color[] lineColors = {
            Color.MAGENTA, Color.GREEN, Color.BLUE, Color.BLACK, Color.CYAN,
            Color.DARK_GRAY, Color.ORANGE, Color.PINK, Color.GRAY, Color.LIGHT_GRAY
    };


    private final OnceRestTemplateConfig.OnceRestTemplate restTemplate;

    public Rrd4jService(OnceRestTemplateConfig.OnceRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Creates a RRDB for all SIM cards that where listed by the Management API
     *
     * @param  override If true, create a new RRDB even there already exists one.
     *
     * @throws IOException
     */
    public void createRrdDb(boolean override) throws IOException {

        File rrdbFile = new File(RRDPATH);

        // If the round-robin database already exists and the user explicitly does NOT override it,
        // then we don't create a new one.
        if (!override && rrdbFile.exists()) {
            log.info("Round-robin database {} already exists.", RRDPATH);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(HTTPS_PORTAL_1NCE_COM_MANAGEMENT_API_SIMS)
                .queryParam("page", 1)
                .queryParam("pageSize", 100);

        ResponseEntity<SimResponse[]> responseEntity
                = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, entity, SimResponse[].class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("Couldn't get list of your SIM cards. HTTP Status Code: {}", responseEntity.getStatusCode());
            return;
        }

        SimResponse[] simResponseList = responseEntity.getBody();

        Instant now = Instant.now();
        Instant twentyFourHoursAgo = now.minus(24, ChronoUnit.HOURS);

        RrdDef rrdDef = new RrdDef(RRDPATH, 300);
        rrdDef.setStartTime(Util.getTimestamp(new Date(twentyFourHoursAgo.toEpochMilli())) - 1);

        // Add the SIMs as data source
        Arrays.stream(simResponseList)
                    .forEach(sim -> {
                        String ds = sim.getIccid();
                        if (!StringUtils.isEmpty(sim.getLabel())) {
                            ds = sim.getLabel();
                        }

                        rrdDef.addDatasource(ds, DsType.COUNTER, 600, Double.NaN, Double.NaN);
                    });

        rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, 12 * 24); // 5 minutes * 12 * 24 = 1 day
        rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 12, 24 * 14); // 1 hour * 24 * 14 = 14 days
        try (RrdDb rrdDb = RrdDb.getBuilder().setRrdDef(rrdDef).build()) {
        }
    }

    public void fetchAndUpdateConsumption() throws IOException{

        // Get data sources as defined in existing RRDB
        String[] dsNames;
        try ( RrdDb rrDb = RrdDb.of(RRDPATH) ) {
            dsNames = rrDb.getDsNames();

            Sample sample = rrDb.createSample();

            for (String dsName : dsNames) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                final HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<QuotaDataResponse> responseEntity
                        = restTemplate.exchange(
                        HTTPS_PORTAL_1NCE_COM_MANAGEMENT_API_SIMS_QUOTA_DATA, HttpMethod.GET, entity, QuotaDataResponse.class, dsName);

                if (responseEntity.getStatusCode() != HttpStatus.OK) {
                    log.warn("Cannot get quota data for ICCID {} from Management API. HTTP Status Code: {}",
                            dsName, responseEntity.getStatusCode());
                    continue;
                }

                QuotaDataResponse quotaData = responseEntity.getBody();

                log.info(quotaData.toString());
                // Convert from Megabytes to bytes and store in RRDB
                double consumption = quotaData.getTotalVolume() - quotaData.getVolume();
                sample.setValue(dsName, consumption * 1024 * 1024);
            }

            log.info("sample={}", sample.dump());
            sample.update();
        }
    }

    public BufferedImage createBufferedImageOfGraph(long startTimeSeconds, long endTimeSeconds,
                                                    int imageWidth, int imageHeight) throws IOException {

        String[] dsNames;
        try ( RrdDb rrDb = RrdDb.of(RRDPATH) ) {
            dsNames = rrDb.getDsNames();
        }

        RrdGraphDef graphDef = new RrdGraphDef();
        graphDef.setTimeSpan(startTimeSeconds, endTimeSeconds);

        graphDef.setTitle("Flatrate data volume consumption");
        graphDef.setVerticalLabel("Bytes per second");

        graphDef.setWidth(imageWidth);
        graphDef.setHeight(imageHeight);

        int colorIndex = 0;
        for (String dsName : dsNames) {
            graphDef.datasource(dsName, RRDPATH, dsName, ConsolFun.AVERAGE);
            graphDef.area(dsName, lineColors[colorIndex % lineColors.length], "Data volume consumption rate of SIM " + dsName + "\\l");
            colorIndex++;
        }

        graphDef.hrule(1.662, Color.RED, "Consumption rate resulting in 50 MB/year\\l");

        graphDef.comment("\\r");

        for (String dsName : dsNames) {
            graphDef.datasource("legend_average_" + dsName, dsName, new Variable.AVERAGE());
            graphDef.gprint("legend_average_" + dsName, "Average consumption rate of SIM " + dsName + ": %.3f%s\\l");
        }

        RrdGraph graph = new RrdGraph(graphDef);
        BufferedImage bufferedImage = new BufferedImage(
                graph.getRrdGraphInfo().getWidth(),
                graph.getRrdGraphInfo().getHeight(),
                BufferedImage.TYPE_INT_RGB);
        graph.render(bufferedImage.getGraphics());

        return bufferedImage;
    }


}
