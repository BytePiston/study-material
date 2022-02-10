package com.sap.sptutorial;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.sptutorial.mvc.Deal;
import com.sap.sptutorial.mvc.DealController;
import com.sap.sptutorial.mvc.DealRepository;

public class DealControllerTest {
	private DealRepository dealRepository;
	private DealController dealController;
	private List<Deal> expectedDeals;
	private MockMvc mockMvc;
	private final String DEAL_ID = "D_AB1234";

	@Before
	public void setup() {
		dealRepository = mock(DealRepository.class);
		dealController = new DealController(dealRepository);
		expectedDeals = Arrays.asList(Deal.builder().id(DEAL_ID).build());
		when(dealRepository.findDeals(0, Integer.MAX_VALUE)).thenReturn(expectedDeals);
		mockMvc = standaloneSetup(dealController).build();
	}

	@Test
	public void testGetDeals() throws Exception {
		mockMvc.perform(get("/deal")).andExpect(status().isOk()).andExpect(view().name("deals"))
				.andExpect(model().attributeExists("deals"))
				.andExpect(model().attribute("deals", IsEqual.equalTo(expectedDeals)));
		verify(dealRepository, times(1)).findDeals(0, Integer.MAX_VALUE);
	}

	@Test
	public void testCreateDeal() throws Exception {
		Deal newdeal = Deal.builder().id(DEAL_ID).build();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		mockMvc.perform(post("/deal").content(mapper.writeValueAsBytes(newdeal))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isFound())
				.andExpect(view().name("redirect:/deal"));
		verify(dealRepository).add(any(Deal.class));
	}

}
