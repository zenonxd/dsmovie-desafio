package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ScoreControllerRA {

	private Long existingMovieId, nonExistingMovieId, dependentId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;

	private Map<String, Object> putScore;

	@BeforeEach
	void setUp() throws Exception {
		baseURI = "http://localhost:8080";

		existingMovieId = 1L;
		nonExistingMovieId = 999L;
		dependentId = 3L;

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

		invalidToken = adminToken + "xpto";

		putScore = new HashMap<>();

		putScore.put("movieId", 1);
		putScore.put("score", 3);


	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {

		JSONObject newScore = new JSONObject(putScore);

		String scoreAsString = newScore.toString();

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(scoreAsString)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("scores")
				.then()
				.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScore.put("movieId", null);

		JSONObject newScore = new JSONObject(putScore);

		String scoreAsString = newScore.toString();

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(scoreAsString)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("scores")
				.then()
				.statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		putScore.put("score", -5);

		JSONObject newScore = new JSONObject(putScore);

		String scoreAsString = newScore.toString();

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(scoreAsString)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("scores")
				.then()
				.statusCode(422);
	}
}
