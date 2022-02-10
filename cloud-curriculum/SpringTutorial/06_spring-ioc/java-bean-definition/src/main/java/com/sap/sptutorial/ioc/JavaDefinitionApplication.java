package com.sap.sptutorial.ioc;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sap.sptutorial.ioc.model.Mailing;
import com.sap.sptutorial.ioc.model.Purchase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaDefinitionApplication {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("test"); // prod
            context.register(SpringConfig.class);
            context.refresh();

            String[] beanNames = context.getBeanDefinitionNames();
            Arrays.stream(beanNames).forEach(name -> {
                log.info("Bean: {}", name);
            });

            Purchase p1 = (Purchase) context.getBean("purchase");
            log.info("Purchase found: " + p1.toString());

            Purchase p2 = (Purchase) context.getBean("purchase2");
            log.info("Purchase found: " + p2.toString());
            
            Mailing m = (Mailing) context.getBean("mailing");
            log.info("Mailing found: " + m.toString());
        }
    }
}
