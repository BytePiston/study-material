package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sap.sptutorial.jpa.hb.HibernateApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HibernateApplication.class)
@WebAppConfiguration
public class EclipselinkApplicationTests {

	@Test
	public void contextLoads() {
	}

}
