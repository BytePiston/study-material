package com.sap.sptutorial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import com.sap.sptutorial.mvc.Deal;
import com.sap.sptutorial.mvc.DealController;
import com.sap.sptutorial.mvc.DealRepository;

public class SimpleDealControllerTest {
	private DealRepository dealRepository;
	private DealController dealController;
	private List<Deal> expectedDeals;
	private final String DEAL_ID = "D_AB1234";
	
	@Before
	public void setup() {
		dealRepository = mock(DealRepository.class);
		dealController = new DealController(dealRepository); // ! Explicit Injection!!
		expectedDeals = Arrays.asList(Deal.builder().id(DEAL_ID).build());
		when(dealRepository.findDeals(0, Integer.MAX_VALUE)).thenReturn(expectedDeals);
	}
	
	@Test
	public void testGetDeals() throws Exception {
		Model model = new BindingAwareModelMap();
		String view = dealController.getDeals(model);
		assertEquals("deals", view);
		assertTrue(model.containsAttribute("deals"));
		assertEquals(expectedDeals, model.asMap().get("deals"));
		verify(dealRepository, times(1)).findDeals(0, Integer.MAX_VALUE);
	}

	@Test
	public void testCreateDeal() throws Exception {
		Deal newdeal = Deal.builder().id(DEAL_ID).build();
		String view = dealController.createDeal(newdeal);
		assertEquals("redirect:/deal", view);
		verify(dealRepository).add(newdeal);
	}
}
