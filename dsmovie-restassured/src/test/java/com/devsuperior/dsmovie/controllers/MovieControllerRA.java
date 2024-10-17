package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

	private String title;
	private Long existingId, nonExistingId, dependentId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;

	private Map<String, Object> postProduct;

	@BeforeEach
	void setUp() throws Exception {
		baseURI = "http://localhost:8080";

		title = "Venom";
		existingId = 1L;
		nonExistingId = 999L;
		dependentId = 3L;

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

		invalidToken = adminToken + "xpto";

		postProduct = new HashMap<>();

		postProduct.put("title", "Test Movie");
		postProduct.put("score", 0.0);
		postProduct.put("count", 0);
		postProduct.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}

	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

		given()
				.get("/movies")
				.then()
				.statusCode(200);
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {

		given()
				.get("movies?title={title}", title)
				.then()
				.statusCode(200)
				.assertThat().body("content.id[0]", is(2))
				.assertThat().body("content.title[0]", equalTo("Venom: Tempo de Carnificina"))
				.assertThat().body("content.score[0]", is(3.3F));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {

		given()
				.get("/movies/{id}", existingId)
				.then()
				.statusCode(200)
				.assertThat().body("id", is(1))
				.assertThat().body("title", equalTo("The Witcher"))
				.assertThat().body("score", is(4.5F));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

		given()
				.get("movies/{id}", nonExistingId)
				.then()
				.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postProduct.put("title", "");

		JSONObject newMovie = new JSONObject(postProduct);

		String productAsString = newMovie.toString();


		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(productAsString)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(422);

	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie = new JSONObject(postProduct);

		String productAsString = newMovie.toString();


		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(productAsString)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie = new JSONObject(postProduct);

		String productAsString = newMovie.toString();


		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(productAsString)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);
	}
}
