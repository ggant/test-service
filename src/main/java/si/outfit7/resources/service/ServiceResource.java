package si.outfit7.resources.service;


import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.outfit7.beans.ClientCDI;
import si.outfit7.beans.DatabaseCDI;
import si.outfit7.exception.ErrorEnum;
import si.outfit7.exception.Outfit7Error;
import si.outfit7.exception.Outfit7Exception;
import si.outfit7.types.Status;
import si.outfit7.types.User;
import si.outfit7.util.Outfit7ExceptionUtil;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Implementation of REST API interface used by users
 * @author Goran Corkovic
 */
@RequestScoped
@Path("/service/")
public class ServiceResource {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceResource.class.getName());

	@Inject
	@ConfigProperty(name="counter")
	private int counter;

	@Inject
	@ConfigProperty(name="supportHourFrom")
	private int supportHourFrom;

	@Inject
	@ConfigProperty(name="supportHourTo")
	private int supportHourTo;

	@Inject
	@ConfigProperty(name="ccParameter")
	private String ccParameter;

	@Inject
	@ConfigProperty(name="supportTimeZone")
	private String supportTimeZone;

	@Inject
	private DatabaseCDI database;

	@Inject
	private ClientCDI client;

	@PersistenceContext(unitName = "test-service-jpa")
	private EntityManager em;

		@APIResponses(
			value = {
					@APIResponse(
							responseCode = "200",
							description = "OK",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Status.class))),
					@APIResponse(
							responseCode = "400",
							description = "Bad request",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Outfit7Error.class))),
					@APIResponse(
							responseCode = "500",
							description = "Internal error.",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Outfit7Error.class))) })
	@Operation(
			summary = "Function that check the state of the services",
			description = "Function checks what features are unlocked for user regarding on input parameters")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/check")
	public Response checkServices(
			@Parameter(
					description = "Timezone of the user.",
					required = true,
					example = "Europe/Ljubljana",
					schema = @Schema(type = SchemaType.STRING))
			@QueryParam("timezone") String timezone,
			@Parameter(
					description = "User identification.",
					required = true,
					example = "123",
					schema = @Schema(type = SchemaType.STRING))
			@QueryParam("userId") String userId,
			@Parameter(
					description = "Country code of the user.",
					required = true,
					example = "SI",
					schema = @Schema(type = SchemaType.STRING))
			@QueryParam("cc") String cc ) throws Outfit7Exception {

			//Check if timezone is null or empty. If it is empty than return error with error code and description.
			if(timezone == null || timezone.isEmpty())
				Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-SERVICE-01", "timezone is mandatory parameter");

			//Check if userId is null or empty. If it is empty than return error with error code and description.
			if(userId == null || userId.isEmpty())
				Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-SERVICE-02", "userId is mandatory parameter");

			//Check if country code is null or empty. If it is empty than return error with error code and description.
			if(cc == null || cc.isEmpty())
				Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-SERVICE-03", "Code country is mandatory parameter");

			//Check if contry code have only two characters, else return error with error code and description.
			if(cc.length() != 2)
				Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-SERVICE-04", "Code country can have only two characters");

			/*
				Find user with userId, if exist then increase API counter for one,
				if doesn't exist then create new user in database with all input parameters and set counter on zero.
			 */
			User user = database.getUserDetails(userId);
			if(user != null) {
				user.setCounter(user.getCounter()+1);
				user.setTimezone(timezone);
				user.setCc(cc);
				user = database.updateUser(user);
			} else {
				user = new User();
				user.setUserId(userId);
				user.setTimezone(timezone);
				user.setCc(cc);
				user.setCounter(1);
				user = database.createUser(user);
			}

			//Create response.
			Status status = new Status();

			/*
				Check if user is skilled player (Has called this API more than 5 times)
				and is located in parametrized country(ex. US).
			 */
			if(user.getCounter() > counter && cc.equalsIgnoreCase(ccParameter))
				status.setMultiplayer("enabled");
			else
				status.setMultiplayer("disabled");

			/*
				Check if customer support for specified user is available.
			 */
			if(isCustomerSupportAvailable(timezone))
				status.setUserSupport("enabled");
			else
				status.setUserSupport("disabled");

			/*
				Check if external partner supports user device.
			 */
			if(client.isAdsAvailable(cc))
				status.setAds("enabled");
			else
				status.setAds("disabled");

			return Response.ok(status).build();
	}

	private boolean isCustomerSupportAvailable(String timezone) throws Outfit7Exception {
		/*
			Get all possible available zones
		 */
		Set<String> allZoneIds = ZoneId.getAvailableZoneIds();

		/*
			Check if input timezone exists and move forward, if not than throw error with error code and description.
		 */
		allZoneIds.stream().filter(zone -> {
			return zone.equalsIgnoreCase(timezone);
		}).findAny().orElseThrow(() -> {
			return new Outfit7Exception(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-SERVICE-05", "Timezone doesn't exist.");
		});

		/*
			Get the current time of user based on his timezone,
			then convert that time to customer support time zone (ex. Europe/Ljubljana).
		 */
		ZonedDateTime currentTimeInLjubljana = ZonedDateTime.now(ZoneId.of(timezone)).withZoneSameInstant(ZoneId.of(supportTimeZone));

		/*
		  Check if support is available.
		 */
		if(isWorkingDay(currentTimeInLjubljana) && currentTimeInLjubljana.getHour() >= supportHourFrom && currentTimeInLjubljana.getHour() < supportHourTo)
			return true;
		else
			return false;

	}

	private boolean isWorkingDay(ZonedDateTime time) {
		if(time == null)
			return false;

		/*
		  Check if input day is not Saturday or Sunday
		 */
		if(time.getDayOfWeek().equals(DayOfWeek.SATURDAY) || time.getDayOfWeek().equals(DayOfWeek.SUNDAY))
			return false;
		else
			return true;
	}

}
