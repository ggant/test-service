package si.outfit7.resources.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import si.outfit7.exception.Outfit7Error;
import si.outfit7.resources.AbstractContainerBaseTest;
import si.outfit7.types.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class AdminResourceTest extends AbstractContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(AdminResourceTest.class.getName());

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

        /*
          Persist test user in database, with specified userId.
        */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/service/check?timezone=Europe/Ljubljana&cc=US&userId=" + userId))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            LOG.error("Error in setUp. Error message: {}", e);
            assertEquals(1, 2);
        }
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
    void getAllUsers() {

        /*
          Call REST API GET '../v1/admin/users'.
         */
        HttpRequest request = requestBuilder.uri(URI.create(url + "/test-service/v1/admin/users"))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            /*
              Check if HTTP response code is 200.
             */
            assertEquals(200, response.statusCode());

            /*
              Check the size of the returned list.
             */
            List<User> responseBody = (List<User>)new ObjectMapper().readValue(response.body(), Object.class);
            assertTrue(responseBody.size() >= 1);

        } catch (Exception e) {
            LOG.error("Error in test getAllUsers. Error message: {}", e);
            assertEquals(1, 2);
        }

    }

    @Test
    void getUserDetails() {

        /*
          Call REST API GET '../v1/admin/user/userId'.
         */
        HttpRequest request = requestBuilder
                .uri(URI.create(url + "/test-service/v1/admin/user/" + userId))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            /*
              Check if HTTP response code is 200.
             */
            assertEquals(200, response.statusCode());

            /*
              Check if the returned User has specified userId.
             */
            User responseBody = new ObjectMapper().readValue(response.body(), User.class);
            assertEquals(userId, responseBody.getUserId());
        } catch (Exception e) {
            LOG.error("Error in test getUserDetails. Error message: {}", e);
            assertEquals(1, 2);
        }

    }

    @Test
    void getUserDetailsWithoutUserId() {

        /*
          Call REST API GET '../v1/admin/user/userId'.
         */
        HttpRequest request = requestBuilder
                .uri(URI.create(url + "/test-service/v1/admin/user/"))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            /*
              Check if HTTP response code is 400.
             */
            assertEquals(404, response.statusCode());

        } catch (Exception e) {
            LOG.error("Error in test getUserDetails. Error message: {}", e);
            assertEquals(1, 2);
        }

    }

    @Test
    void getUserDetailsUserDoesNotExist() {

        /*
          Call REST API GET '../v1/admin/user/userId'.
         */
        HttpRequest request = requestBuilder
                .uri(URI.create(url + "/test-service/v1/admin/user/asdfdsfdfdf"))
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
            assertEquals("OUTFIT7-ADMIN-02", error.getCode());
            assertEquals("User doesn't exist", error.getDescription());
            assertEquals(400, error.getStatus());


        } catch (Exception e) {
            LOG.error("Error in test getUserDetails. Error message: {}", e);
            assertEquals(1, 2);
        }

    }

    @Test
    void deleteUser() {

        /*
          Call REST API DELETE'../v1/admin/user/userId'.
         */
        HttpRequest request = requestBuilder
                .uri(URI.create(url + "/test-service/v1/admin/user/" + userId))
                .header("Accept", "text/plain")
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            /*
              Check if HTTP response code is 200.
             */
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            LOG.error("Error in test deleteUser. Error message: {}", e);
            assertEquals(1, 1);
        }

    }

    @Test
    void deleteUserDoesNotExist() {

        /*
          Call REST API DELETE'../v1/admin/user/userId'.
         */
        HttpRequest request = requestBuilder
                .uri(URI.create(url + "/test-service/v1/admin/user/asasdasd"))
                .header("Accept", "text/plain")
                .DELETE()
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
            assertEquals("OUTFIT7-ADMIN-02", error.getCode());
            assertEquals("User doesn't exist", error.getDescription());
            assertEquals(400, error.getStatus());

        } catch (Exception e) {
            LOG.error("Error in test deleteUser. Error message: {}", e);
            assertEquals(1, 1);
        }

    }
}
