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
package org.klenk.connectivity.iot.oncemanagementapi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("1nce")
@Data
@ConstructorBinding
public class OnceAuthenticationProperties {

    private String clientId;
    private String clientSecret;
    private String tokenEndpoint;
}
