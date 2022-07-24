package si.outfit7.beans;


import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.outfit7.types.AdsResponse;
import si.outfit7.util.Authenticator;

/**
 * CDI bean that encapsulate logic for communication with partner REST API service.
 * @author Goran Corkovic
 */
@RequestScoped
public class ClientCDI {

	private static final Logger LOG = LoggerFactory.getLogger(ClientCDI.class.getName());

	@Inject
	@ConfigProperty(name="clientRestUrl")
	private String clientRestUrl;

	@Inject
	@ConfigProperty(name="username")
	private String username;

	@Inject
	@ConfigProperty(name="password")
	private String password;

	@Inject
	@ConfigProperty(name="clientResponseOK")
	private String clientResponseOK;

	public boolean isAdsAvailable(String cc) {

		/*
    	 Create REST client and retrieve data.
        */
		Client client = ClientBuilder.newClient().register(new Authenticator(username, password));
		Response response = client.target(clientRestUrl)
				.queryParam("countryCode", cc)
				.request().accept(MediaType.APPLICATION_JSON).get();

		/*
			Check if HTTP response is 200 and then get response data.
		 */
		if(response.getStatus() == 200) {
			AdsResponse replyString = response.readEntity(AdsResponse.class);

			/*
				Check if user device is supported by partner
			 */
			if(replyString != null && replyString.getAds() != null &&
					!replyString.getAds().isEmpty() && replyString.getAds().equalsIgnoreCase(clientResponseOK))
				return true;
			else
				return false;

		} else {
			/*
				If partners REST service return other than HTTP 200 than return false and add error in log
			*/
			LOG.error("Client's REST service {} return HTTP code {}", clientRestUrl, response.getStatus());
			return false;
		}
	}
}
