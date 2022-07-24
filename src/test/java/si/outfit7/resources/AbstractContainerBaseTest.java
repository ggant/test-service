package si.outfit7.resources;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Abstract class that define common container and url used in all test scenarios.
 * @author Goran Corkovic
 */
public abstract class AbstractContainerBaseTest {


    public static final GenericContainer testService;

    public static final String url;

    static {
        testService = new GenericContainer(DockerImageName.parse("corkovic/test-service:1.0"))
                .withExposedPorts(9082)
                .withEnv("username", "fun7user")
                .withEnv("counter", "5")
                .withEnv("supportTimeZone", "Europe/Ljubljana")
                .withEnv("supportHourFrom", "9")
                .withEnv("supportHourTo", "15")
                .withEnv("clientRestUrl","https://us-central1-o7tools.cloudfunctions.net/fun7-ad-partner")
                .withEnv("clientResponseOK", "sure, why not!")
                .withEnv("ccParameter", "US")
                .withEnv("db_serverName", "microservice-web-db-server.database.windows.net")
                .withEnv("db_portNumber", "1433")
                .withEnv("db_databaseName", "microservice-web-db")
                .withEnv("db_username", "sqladmin@microservice-web-db-server")
                .withEnv("password", "fun7pass")
                .withEnv("trustStorePass", "changeit")
                .withEnv("db_password", "Sqlgeslo123!")
                .withEnv("keyStorePass", "secret")
                .waitingFor(Wait.forHttp("/health/ready"));

        testService.start();

        //url = "http://" + testService.getHost() +":" + testService.getMappedPort(9082);
        url = "http://20.23.64.180:8080";
    }
}
