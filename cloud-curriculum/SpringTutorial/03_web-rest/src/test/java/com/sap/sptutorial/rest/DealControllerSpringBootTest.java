package com.sap.sptutorial.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DealControllerSpringBootTest {

	private Deal newdeal;
	private DealCollection expectedDeals;
	private final String DEAL_ID = "D_AB1234";

	@Autowired
	private TestRestTemplate testTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DealRepository dealRepository;

	@Before
	public void setup() {
		expectedDeals = new DealCollection();
		newdeal = Deal.builder().id(DEAL_ID).build();
		expectedDeals.add(newdeal);
		dealRepository.add(newdeal);
	}

	@Test
	public void testGetDeals() throws Exception {
		testTemplate.execute("/deal", HttpMethod.GET, request -> {
			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		}, response -> {
			assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
			DocumentContext jsonBody = JsonPath.parse(response.getBody());
			assertThat(jsonBody.read("$.deals.[0].id", String.class)).isEqualTo(DEAL_ID);
			return null;
		});
	}

	@Test
	public void testGetDealById() throws Exception {
		testTemplate.execute("/deal/" + DEAL_ID, HttpMethod.GET, request -> {
			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		}, response -> {
			assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
			DocumentContext jsonBody = JsonPath.parse(response.getBody());
			assertThat(jsonBody.read("$.id", String.class)).isEqualTo(DEAL_ID);
			return null;
		});
	}

	@Test
	public void testCreateDeal() throws Exception {
		testTemplate.execute("/deal", HttpMethod.POST, request -> {
			request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			request.getBody().write(objectMapper.writeValueAsBytes(newdeal));
		}, response -> {
			assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
			DocumentContext jsonBody = JsonPath.parse(response.getBody());
			assertThat(jsonBody.read("$.id", String.class)).isEqualTo(DEAL_ID);
			return null;
		});
	}
}
