package com.sap.sptutorial.rest;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;

public class DealControllerUnitTest {
	private Deal newdeal;
	private DealRepository dealRepository;
	private DealController dealController;
	private DealCollection expectedDeals;
	private MockMvc mockMvc;

	private final String DEAL_ID = "D_AB1234";

	@Before
	public void setup() {
		dealRepository = mock(DealRepository.class);
		dealController = new DealController(dealRepository);
		expectedDeals = new DealCollection();
		newdeal = Deal.builder().id(DEAL_ID).build();
		expectedDeals.add(newdeal);
		when(dealRepository.findAll(0, Integer.MAX_VALUE)).thenReturn(expectedDeals);
		when(dealRepository.find(DEAL_ID)).thenReturn(newdeal);
		mockMvc = standaloneSetup(dealController).build();
	}

	@Test
	public void testGetDeals() throws Exception {
		mockMvc.perform(get("/deal").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.deals[0].id", is(DEAL_ID)));
		verify(dealRepository).findAll(0, Integer.MAX_VALUE);
	}

	@Test
	public void testGetDealById() throws Exception {
		mockMvc.perform(get("/deal/" + DEAL_ID).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(DEAL_ID)));
		verify(dealRepository).find(DEAL_ID);
	}

	@Test
	public void testCreateDeal() throws Exception {
		val mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		mockMvc.perform(post("/deal").content(mapper.writeValueAsBytes(newdeal)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(DEAL_ID)));
		verify(dealRepository).add(any(Deal.class));
	}

}
