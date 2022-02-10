package com.sap.sptutorial.ioc;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.sap.sptutorial.ioc.model.Customer;
import com.sap.sptutorial.ioc.model.Purchase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManualDefinitionApplication {
    public static void main(String[] args) {
        /*
         * First thing we need in Spring is a Bean Factory
         */
        DefaultListableBeanFactory context = new DefaultListableBeanFactory();

        /*
         * Now we create Bean Definitions for Customer and Purchase The Bean
         * Definitions are the Bean Metadata Note how we define the dependency
         * of Purchase to Customer
         */
        GenericBeanDefinition customerDefinition = new GenericBeanDefinition();
        customerDefinition.setBeanClass(Customer.class);
        context.registerBeanDefinition("customer", customerDefinition);

        GenericBeanDefinition purchaseDefinition = new GenericBeanDefinition();
        purchaseDefinition.setBeanClass(Purchase.class);
        purchaseDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        RuntimeBeanReference customerReference = new RuntimeBeanReference(
                "customer");
        cavs.addGenericArgumentValue(customerReference);
        purchaseDefinition.setConstructorArgumentValues(cavs);
        context.registerBeanDefinition("purchase", purchaseDefinition);

        /*
         * Let's see the bean definitions we got
         */
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.stream(beanNames).forEach(name -> {
            log.info("Bean: {}", name);
        });

        /*
         * It's time to do something with our beans
         */
        Customer c = (Customer) context.getBean("customer");
        Purchase p1 = (Purchase) context.getBean("purchase");
        Purchase p2 = (Purchase) context.getBean("purchase");
        c.setName("Heinz");
        log.info("Purchase found: " + p1.toString());
        log.info("Purchase found: " + p2.toString());
    }

}
