package com.sap.sptutorial.rest;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(DealController.class)
public class DealControllerWebMvcTest {
    private Deal newdeal;
    private DealCollection expectedDeals;
    private final String DEAL_ID = "D_AB1234";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private DealRepository dealRepository;

    @Before
    public void setup() {
        expectedDeals = new DealCollection();
        newdeal = Deal.builder().id(DEAL_ID).build();
        expectedDeals.add(newdeal);
        given(dealRepository.findAll(0, Integer.MAX_VALUE)).willReturn(expectedDeals);
        given(dealRepository.find(DEAL_ID)).willReturn(newdeal);
    }

    @Test
    public void testGetDeals() throws Exception {
        mockMvc.perform(get("/deal").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.deals[0].id", is(DEAL_ID)));
    }

    @Test
    public void testGetDealById() throws Exception {
        mockMvc.perform(get("/deal/" + DEAL_ID).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(DEAL_ID)));
    }

    @Test
    public void testCreateDeal() throws Exception {
        mockMvc.perform(post("/deal").content(objectMapper.writeValueAsBytes(newdeal)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(DEAL_ID)));
    }
}

