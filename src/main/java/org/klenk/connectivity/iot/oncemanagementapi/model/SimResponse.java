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
