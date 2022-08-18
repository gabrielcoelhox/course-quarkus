package io.github.gabrielcoelho.quarkussocial.rest;

import io.github.gabrielcoelho.quarkussocial.domain.model.User;
import io.github.gabrielcoelho.quarkussocial.domain.repository.UserRepository;
import io.github.gabrielcoelho.quarkussocial.rest.dto.CreateUserRequest;
import io.github.gabrielcoelho.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @GET
    public Response findAll(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @POST
    public Response insert(CreateUserRequest userRequest) {

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()){
            ResponseError responseError = ResponseError.createFromValidation(violations);
            return Response.status(400).entity(responseError).build();
        }

        User newUser = new User();
        newUser.setAge(userRequest.getAge());
        newUser.setName(userRequest.getName());
        repository.persist(newUser);
        return Response.ok(newUser).build();
    }

    @PUT
    @Path("{id}")
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userInfo) {
        User userById = repository.findById(id);
        if(userById != null){
            userById.setName(userInfo.getName());
            userById.setAge(userInfo.getAge());
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        User userById = repository.findById(id);

        if (userById != null) {
            repository.delete(userById);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}