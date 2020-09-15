package br.com.mmf.endpoint;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.mmf.entity.Fruit;
import br.com.mmf.service.FruitService;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitEndpoint {
	
	@Inject
	private FruitService fruitService;

    @GET
    public Response fruits() {
        return Response.ok(fruitService.getAll()).status(Status.OK).build();
    }
    
    @POST
    public Response create(Fruit fruit) {
    	return Response.ok(fruitService.save(fruit)).status(Status.CREATED).build();
    }
    
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") UUID id, Fruit fruit) {
    	return Response.ok(fruitService.update(id, fruit)).status(Status.OK).build();
    }

}
