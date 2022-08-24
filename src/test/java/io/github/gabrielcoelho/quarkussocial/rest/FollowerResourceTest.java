package io.github.gabrielcoelho.quarkussocial.rest;

import io.github.gabrielcoelho.quarkussocial.domain.model.Follower;
import io.github.gabrielcoelho.quarkussocial.domain.model.User;
import io.github.gabrielcoelho.quarkussocial.domain.repository.FollowerRepository;
import io.github.gabrielcoelho.quarkussocial.domain.repository.UserRepository;
import io.github.gabrielcoelho.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    public void setUp() {
        //Usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //O seguidor
        var follower = new User();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        //Cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Should return 409 when Follower Id is equal to User id")
    public void sameUserAsFollowerTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 on list user followers and User id does not exist")
    public void userNotFoundWhenTryingToFollowTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var nonexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", nonexistentUserId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    public void followUserTest() {

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and User id does not exist")
    public void userNotFoundWhenListingFollowersTest() {
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    public void listFollowersTest() {
        var response =
                given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName("Should return 404 on unfollow user and User id doen't exist")
    public void userNotFoundWhenUnfollowingAUserTest() {
        var inexistentUserId = 999;

        given()
            .pathParam("userId", inexistentUserId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should Unfollow an user")
    public void unfollowUserTest() {
        given()
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}