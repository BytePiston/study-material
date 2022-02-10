package com.sap.sptutorial.ioc;

import java.util.Arrays;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.sap.sptutorial.ioc.model.Channel;
import com.sap.sptutorial.ioc.model.Channel.Type;
import com.sap.sptutorial.ioc.model.Customer;
import com.sap.sptutorial.ioc.model.Product;
import com.sap.sptutorial.ioc.model.Purchase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlDefinitionApplication {
	public static void main(String[] args) {
		try (GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:beans.xml")) {

			String[] beanNames = context.getBeanDefinitionNames();
			Arrays.stream(beanNames).forEach(name -> {
				log.info("Bean: {}", name);
			});
			
			Customer c = context.getBean("customer", Customer.class);
			c.setName("Heinz Becker");
			
			Product o = (Product) context.getBean("product");
			o.setName("Red Shoes");
			
			Channel a = (Channel) context.getBean("channel");
			a.setType(Type.DIRECT);
			
			Purchase p = (Purchase) context.getBean("purchase");
			log.info("Purchase found: " + p.toString());
		}	
	}

}
