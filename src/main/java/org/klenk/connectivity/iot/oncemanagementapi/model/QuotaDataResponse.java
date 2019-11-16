package org.klenk.connectivity.iot.oncemanagementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//
// Response structure for calls to
// /management-api/v1/sims/{iccid}/quota/data"
//
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotaDataResponse {

    @JsonProperty("volume")
    Double volume;

    @JsonProperty("total_volume")
    Double totalVolume;

    @JsonProperty("expiry_date")
    String expiryDate;

    @JsonProperty("last_status_change_date")
    String lastStatusChangeDate;
}