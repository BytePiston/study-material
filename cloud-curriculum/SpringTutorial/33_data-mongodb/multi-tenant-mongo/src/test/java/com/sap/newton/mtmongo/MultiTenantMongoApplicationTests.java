package com.sap.newton.mtmongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MultiTenantMongoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private TenantProvider TenantProvider;

    @Before
    public void setup() {
        mongoTemplate.getDb().dropDatabase();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTenantCollections() throws Exception {
        TenantProvider.setTenantId("abc");
        assertThat(mongoTemplate.indexOps(Asset.class).getIndexInfo()).isEmpty();
        assertThat(mongoTemplate.indexOps(Metric.class).getIndexInfo()).isEmpty();
        
        TenantProvider.setTenantId("xyz");
        assertThat(mongoTemplate.indexOps(Asset.class).getIndexInfo()).isEmpty();
        assertThat(mongoTemplate.indexOps(Metric.class).getIndexInfo()).isEmpty();
        
        mockMvc.perform(get("/assets").header("tenant-id", "abc")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets").isEmpty());
        mockMvc.perform(
            post("/assets").header("tenant-id", "abc").content(asJsonString(Asset.builder().name("Fritz").build()))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/assets").header("tenant-id", "abc")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets.length()]").value(1));
        mockMvc.perform(get("/assets").header("tenant-id", "abc")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets[0].name").value("Fritz"));

        mockMvc.perform(get("/assets").header("tenant-id", "xyz")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets").isEmpty());
        mockMvc.perform(
            post("/assets").header("tenant-id", "xyz").content(asJsonString(Asset.builder().name("Franz").build()))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/assets").header("tenant-id", "xyz")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets.length()").value(1));
        mockMvc.perform(get("/assets").header("tenant-id", "xyz")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets[0].name").value("Franz"));

        mockMvc.perform(get("/assets").header("tenant-id", "abc")).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.assets.length()").value(1));
        
        TenantProvider.setTenantId("abc");
        assertThat(mongoTemplate.indexOps(Asset.class).getIndexInfo()).isNotEmpty();
        assertThat(mongoTemplate.indexOps(Metric.class).getIndexInfo()).isNotEmpty();
        
        TenantProvider.setTenantId("xyz");
        assertThat(mongoTemplate.indexOps(Asset.class).getIndexInfo()).isNotEmpty();
        assertThat(mongoTemplate.indexOps(Metric.class).getIndexInfo()).isNotEmpty();
    }

}
