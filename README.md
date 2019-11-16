# 1nce-management-api-access-and-rrd4j-graphs
Simple solution to read out NB-IoT data traffic consumption from the 1NCE management API, store it in a round-robin 
database and show the result in a graph.


## How to configure
Put your `client-id` and `client-credentials` in configuration file `application.yml`:

    ok-http-client:
    #  proxy:
    #    host: localhost
    #    port: 3128
    
    1nce:
      client-id: your-client-id
      client-secret: your-client-secret
      token-endpoint: https://portal.1nce.com/management-api/oauth/token

# How to build
You need maven and a Java JDK to compile:

    $ mvn clean package
    
# How to run
    
    $ java -jar target/1nce-management-api-access-and-rrd4j-graphs-0.0.1-SNAPSHOT.jar
    
On the first start, you may see something like this:
    
    ...
    Tomcat started on port(s): 8080 (http) with context path ''
    route=Route{/a.b.c.d:xxxx} response=Response{protocol=http/1.1, code=401, message=, url=https://portal.1nce.com/management-api/v1/sims?page=1&pageSize=100}
    Request did NOT use an Authorization header.
    Requesting a new Access Token by client credentials
    Token endpoint returned status 200 OK and tokens OnceAccessTokenService.Tokens(accessToken=206bed3b-11e6-4253-97c4-cb410f6f08ab, tokenType=bearer, expiresIn=3560, scope=all, appToken=eyJhbGc...)
    QuotaDataResponse(volume=488.29297, totalVolume=500.0, expiryDate=2028-11-20 00:00:00, lastStatusChangeDate=2018-08-20 11:25:03)
    QuotaDataResponse(volume=499.98907, totalVolume=500.0, expiryDate=2028-11-20 00:00:00, lastStatusChangeDate=2018-08-20 11:25:03)
    sample=update "./data-traffic-consumption.rrd" 1573889068:1.2275710689279974E7:11460.935679972172

If a RRDB file with name `data-traffic-consumption.rrd` already exist in the current directory, then it won't be overriden.
If you explicitly want to start with a new RRDB file, start the application with option `--override`.    

## Implementation details
The implementation is done using the Spring Boot framework. 

On startup, it creates a HTTP REST endpoint at port 8080.

Every 5 minutes a task is executed that fetches the current data traffic consumption values for each SIM card
from the 1nce management API and stores it in a round-robin database.
To display the graph, just use your web browser and go to http://localhost:8080 (or wherever you have deployed the application). 

