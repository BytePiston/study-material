package com.sap.sptutorial.rest.deal;

import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DealRepositoryRestConfigurerAdapter
        extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureValidatingRepositoryEventListener(
            ValidatingRepositoryEventListener validatingListener) {
        super.configureValidatingRepositoryEventListener(validatingListener);
        validatingListener.addValidator("beforeCreate", new Validator() {

            @Override
            public boolean supports(Class<?> clazz) {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
                log.info("validation of " + target.toString());
            }

        });
    }

}
