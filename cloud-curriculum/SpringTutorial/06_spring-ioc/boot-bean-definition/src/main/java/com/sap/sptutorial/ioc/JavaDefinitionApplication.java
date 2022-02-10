package com.sap.sptutorial.ioc;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sap.sptutorial.ioc.model.Mailing;
import com.sap.sptutorial.ioc.model.Purchase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class JavaDefinitionApplication implements ApplicationContextAware {
    public static void main(String[] args) {
        SpringApplication.run(JavaDefinitionApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
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
