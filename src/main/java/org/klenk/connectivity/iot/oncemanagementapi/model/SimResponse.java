package org.klenk.connectivity.iot.oncemanagementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotaStatus {
        private Long id;
        private String description;
        private String thresholdReachedDate;
        private String quotaExceededDate;
    }


    private String iccid;
    private String imsi;
    private String msisdn;
    private String imei;

    @JsonProperty("imei_lock")
    private boolean imeiLock;

    private String status;

    @JsonProperty("activation_date")
    private String activationDate;

    @JsonProperty("ip_address")
    private String ipAddress;

    private Long currentQuota;

    private QuotaStatus quotaStatus;

    private String label;
}
