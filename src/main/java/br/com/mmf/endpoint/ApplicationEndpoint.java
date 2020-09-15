package br.com.mmf.endpoint;

import javax.ws.rs.core.SecurityContext;

import br.com.mmf.security.UserIndexable;
import br.com.mmf.security.UserPrincipal;

public class ApplicationEndpoint {
	
	public UserIndexable getCurrentUser(SecurityContext sec) {
		return ((UserPrincipal)sec.getUserPrincipal()).getUser();
	}

}
