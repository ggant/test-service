package si.outfit7.resources.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import si.outfit7.exception.Outfit7Error;
import si.outfit7.resources.AbstractContainerBaseTest;
import si.outfit7.types.Status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
class ServiceResourceTest extends AbstractContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceResourceTest.class.getName());

    private HttpClient client = HttpClient.newHttpClient();

    private HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

    private String userId;

    @BeforeEach
    void setUp() {
        /*
          Generate random value for userId.
         */
        Random random = new Random();
        int myRandInt = random.nextInt(10000);
        userId = String.valueOf(myRandInt+1);
    }

    @AfterEach
    void tearDown() {
        /*
          After each test delete test data.
         */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/admin/user/" + userId))
                .header("Accept", "text/plain")
                .DELETE()
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            LOG.error("Error in tearDown. Error message: {}", e);
        }
    }

    @Test
    void checkServices() {

        /*
          Call REST API '../v1/service/check'.
         */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/service/check?timezone=Europe/Ljubljana&cc=US&userId=" + userId))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            /*
              Check if HTTP response code is 200.
             */
            assertEquals(200, response.statusCode());
            Status status = new ObjectMapper().readValue(response.body(), Status.class);

        } catch (Exception e) {
            LOG.error("Error in test checkServices. Error message: {}", e);
            assertEquals(1, 2);
        }
    }

    @Test
    void checkServicesWithoutTimeZone() {

        /*
          Call REST API without query parameter: timezone, '../v1/service/check'.
         */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/service/check?cc=US&userId=" + userId))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            /*
              Check if HTTP response code is 400.
             */
            assertEquals(400, response.statusCode());

            /*
              Checking error message
             */
            Outfit7Error error = new ObjectMapper().readValue(response.body(), Outfit7Error.class);
            assertEquals("OUTFIT7-SERVICE-01", error.getCode());
            assertEquals("timezone is mandatory parameter", error.getDescription());
            assertEquals(400, error.getStatus());

        } catch (Exception e) {
            LOG.error("Error in test checkServices. Error message: {}", e);
            assertEquals(1, 2);
        }
    }

    @Test
    void checkServicesWithoutUserId() {

        /*
          Call REST API without query parameter: timezone, '../v1/service/check'.
         */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/service/check?timezone=Europe/Ljubljana&cc=US"))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            /*
              Check if HTTP response code is 400.
             */
            assertEquals(400, response.statusCode());

            /*
              Checking error message
             */
            Outfit7Error error = new ObjectMapper().readValue(response.body(), Outfit7Error.class);
            assertEquals("OUTFIT7-SERVICE-02", error.getCode());
            assertEquals("userId is mandatory parameter", error.getDescription());
            assertEquals(400, error.getStatus());

        } catch (Exception e) {
            LOG.error("Error in test checkServices. Error message: {}", e);
            assertEquals(1, 2);
        }
    }

    @Test
    void checkServicesWithoutCC() {

        /*
          Call REST API without query parameter: timezone, '../v1/service/check'.
         */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/service/check?timezone=Europe/Ljubljana&userId=" + userId))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            /*
              Check if HTTP response code is 400.
             */
            assertEquals(400, response.statusCode());

            /*
              Checking error message
             */
            Outfit7Error error = new ObjectMapper().readValue(response.body(), Outfit7Error.class);
            assertEquals("OUTFIT7-SERVICE-03", error.getCode());
            assertEquals("Code country is mandatory parameter", error.getDescription());
            assertEquals(400, error.getStatus());

        } catch (Exception e) {
            LOG.error("Error in test checkServices. Error message: {}", e);
            assertEquals(1, 2);
        }
    }
}
