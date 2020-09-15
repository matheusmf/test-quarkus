package br.com.mmf.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {
	
	private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());;
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<Object>());;
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<Object>());;
     

	private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

	@Context
	UriInfo uriInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			String jwt = getJwtFromRequest(requestContext);
			if (jwt != null && !jwt.isEmpty() && validateToken(jwt)) {
				UserIndexable user = getUserFromJWT(jwt);
				this.setSecurityContext(requestContext, user);
				this.allowEndpoints(requestContext, user);
			} else {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} catch (Exception e) {
			e.printStackTrace();
			requestContext.abortWith(SERVER_ERROR);
		}

	}

	private void setSecurityContext(ContainerRequestContext requestContext, UserIndexable user) {
		requestContext.setSecurityContext(new SecurityContext() {
			@Override
			public Principal getUserPrincipal() {
				return new UserPrincipal(user);
			}

			@Override
			public boolean isUserInRole(String role) {
				return user.getRoles().contains(role);
			}

			@Override
			public boolean isSecure() {
				return uriInfo.getAbsolutePath().toString().startsWith("https");
			}

			@Override
			public String getAuthenticationScheme() {
				return "Token-Based-Auth-Scheme";
			}
		});
	}

	private void allowEndpoints(ContainerRequestContext requestContext, UserIndexable user) {
		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		Method method = methodInvoker.getMethod();
		// Access allowed for all
		if (!method.isAnnotationPresent(PermitAll.class)) {
			// Access denied for all
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}

			// Verify user access
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
				int rolesSetSize = rolesSet.size();
				List<String> userRoles = user.getRoles();
				rolesSet.removeAll(userRoles);
				

				// Is user valid?
				if (rolesSetSize == rolesSet.size()) {
					requestContext.abortWith(ACCESS_DENIED);
					return;
				}
			}
		}
	}

	private String getJwtFromRequest(ContainerRequestContext requestContext) {
		String bearerToken = requestContext.getHeaders().getFirst("Authorization");
		if (bearerToken != null && !bearerToken.isEmpty() && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	private boolean validateToken(String authToken) {
		try {
			// log.debug("jwt key: " + jwtKey.substring(0, 2));
			Jwts.parser().setSigningKey("MMP5_JWT_KEY").parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}

	private UserIndexable getUserFromJWT(String token)
			throws JsonParseException, JsonMappingException, IOException, JSONException {
		String base64EncodedBody = token.split("\\.")[1];
		Base64 base64Url = new Base64(true);
		JSONObject jsonToken = new JSONObject(new String(base64Url.decode(base64EncodedBody)));
		JSONObject userJson = jsonToken.getJSONObject("user");

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		UserIndexable user = mapper.readValue(userJson.toString(), UserIndexable.class);
		if(user.getRoles() != null && !user.getRoles().isEmpty()) {
			List<String> roles = new ArrayList<>(user.getRoles());
			user.getRoles().clear();
			roles.forEach(role -> user.getRoles().add(String.format("ROLE_%s", role)));
		}
		return user;
	}

}
