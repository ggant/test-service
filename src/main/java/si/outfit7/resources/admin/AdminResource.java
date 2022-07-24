package si.outfit7.resources.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.outfit7.beans.DatabaseCDI;
import si.outfit7.exception.ErrorEnum;
import si.outfit7.exception.Outfit7Error;
import si.outfit7.exception.Outfit7Exception;
import si.outfit7.types.User;
import si.outfit7.util.Outfit7ExceptionUtil;

@RequestScoped
@Path("/admin/")
public class AdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class.getName());

	@Inject
	DatabaseCDI database;

	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "200",
							description = "OK",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = User.class))),
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
			summary = "Function that returns list of all active users",
			description = "Function checks list active users")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/users")
	public Response getAllUsers() throws Outfit7Exception {
		return Response.ok(database.getAllUsers()).build();
	}

	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "200",
							description = "OK",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = User.class))),
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
			summary = "Function that returns user",
			description = "Function return all data for specified user regarding on input parameter")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/user/{userId}")
	public Response getUserDetails(
			@Parameter(
					description = "User identificator.",
					required = true,
					example = "123",
					schema = @Schema(type = SchemaType.STRING))
			@PathParam("userId") String userId) throws Outfit7Exception {

		//Check if userId is null or empty. If it is empty than return error with error code and description.
		if(userId == null || userId.isEmpty())
			Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-ADMIN-01", "UserId is mandatory parameter");

		/*
		  Find user with userId. Return User if exists.
		  If user doesn't exist log error and return HTTP 400.
		 */
		User user = database.getUserDetails(userId);
		if(user != null)
			return Response.ok(database.getUserDetails(userId)).build();
		else {
			LOG.error("User doesn't exists. UserId: {}.", userId);
			Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-ADMIN-02", "User doesn't exist");
			return null;
		}
	}

	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "200",
							description = "OK",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = String.class))),
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
			summary = "Function delete user",
			description = "Function delete user data for specified user identification")
	@DELETE
	@Produces({MediaType.TEXT_PLAIN})
	@Path("/user/{userId}")
	public Response deleteUser(
			@Parameter(
					description = "User identificator.",
					required = true,
					example = "123",
					schema = @Schema(type = SchemaType.STRING))
			@PathParam("userId") String userId) throws Outfit7Exception {

		//Check if userId is null or empty. If it is null or empty return error with error code and description.
		if(userId == null || userId.isEmpty())
			Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-ADMIN-01", "UserId is mandatory parameter");

		/*
			Find user in database and delete them. If user doesn't exist log error and return HTTP 400.
		 */
		if(database.deleteUser(userId)) {
			return Response.ok("User removed!").build();
		} else {
			LOG.error("Try to delete user that doesn't exists. UserId: {}.", userId);
			Outfit7ExceptionUtil.throwException(ErrorEnum.INPUT_DATA_ERROR, "OUTFIT7-ADMIN-02", "User doesn't exist");
			return null;
		}
	}
}
