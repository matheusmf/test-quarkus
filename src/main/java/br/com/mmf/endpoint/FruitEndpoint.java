package br.com.mmf.endpoint;

import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import br.com.mmf.entity.Fruit;
import br.com.mmf.service.FruitService;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class FruitEndpoint extends ApplicationEndpoint {
	
	@Inject
	private FruitService fruitService;

    @GET
    public Response fruits() {
        return Response.ok(fruitService.getAll()).status(Status.OK).build();
    }
    
    @POST
    @RolesAllowed({ "ROLE_CI" })
    public Response create(@Context SecurityContext sec, Fruit fruit) {
    	fruit.setCreatedBy(getCurrentUser(sec).getId());
    	return Response.ok(fruitService.save(fruit)).status(Status.CREATED).build();
    }
    
    @PUT
    @Path("{id}")
    @RolesAllowed("ROLE_CI")
    public Response update(@Context SecurityContext sec, @PathParam("id") UUID id, Fruit fruit) {
    	fruit.setLastModifiedBy(getCurrentUser(sec).getId());
    	return Response.ok(fruitService.update(id, fruit)).status(Status.OK).build();
    }

}
