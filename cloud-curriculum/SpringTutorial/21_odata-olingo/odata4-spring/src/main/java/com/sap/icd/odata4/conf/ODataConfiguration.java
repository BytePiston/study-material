package com.sap.icd.odata4.conf;

import java.util.ArrayList;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.core.ServiceMetadataImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sap.icd.odata4.edm.EdmTypeConverter;
import com.sap.icd.odata4.edm.SpringDataEdmProvider;
import com.sap.icd.odata4.example.Namespace;
import com.sap.icd.odata4.processor.HandlerFactory;
import com.sap.icd.odata4.processor.SpringDataCollectionProcessor;
import com.sap.icd.odata4.processor.SpringDataEntityProcessor;
import com.sap.icd.odata4.processor.SpringDataPrimitiveProcessor;
import com.sap.icd.odata4.processor.SpringDataProcessor;

import lombok.val;

@Configuration
@val
public class ODataConfiguration extends WebMvcConfigurerAdapter {
    @Value("${odata.servicePath}")
    public String odataServicePath;

    @Autowired
    public ApplicationContext applicationContext;

    @Bean
    public EdmTypeConverter edmTypeConverter() {
        return new EdmTypeConverter();
    }

    @Bean
    public SpringDataEdmProvider springDataEdmProvider(
            ResourceMappings resourceMappings,
            PersistentEntities persistentEntities, Repositories repositories) {
        SpringDataEdmProvider edmProvider = new SpringDataEdmProvider(
                edmTypeConverter(), repositories, persistentEntities,
                resourceMappings);
        edmProvider.setNamespaceName(Namespace.class.getPackage().getName());
        edmProvider.setContainerName("container");
        return edmProvider;
    }

    @Bean
    public ServiceMetadata serviceMetadata(SpringDataEdmProvider edmProvider) {
        return new ServiceMetadataImpl(edmProvider,
                new ArrayList<EdmxReference>(), null);
    }

    @Bean
    public OData odata() {
        return OData.newInstance();
    }

    @Bean
    public SpringDataProcessor[] processors(SpringDataEdmProvider edmProvider,
            PersistentEntities persistentEntities, Repositories repositories,
            RepositoryInvokerFactory invokerFactory) {
        SpringDataProcessor[] processors = new SpringDataProcessor[3];
        processors[0] = new SpringDataCollectionProcessor(odata(), serviceMetadata(edmProvider),
                edmProvider, repositories, persistentEntities, invokerFactory, projectionFactory());
        processors[1] = new SpringDataEntityProcessor(odata(), serviceMetadata(edmProvider),
                edmProvider, repositories, persistentEntities, invokerFactory, projectionFactory());
        processors[2] = new SpringDataPrimitiveProcessor(odata(), serviceMetadata(edmProvider),
                edmProvider, repositories, persistentEntities, invokerFactory, projectionFactory());
        return processors;
    }

    @Bean
    public HandlerFactory handlerFactory(ServiceMetadata serviceMetadata,
            SpringDataProcessor[] processors) {
        HandlerFactory handlerFactory = new HandlerFactory(odata(),
                serviceMetadata, processors, odataServicePath);
        return handlerFactory;
    }

    @SuppressWarnings("deprecation")
    @Bean
    public ProjectionFactory projectionFactory() {
        SpelAwareProxyProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        projectionFactory.setBeanFactory(applicationContext);
        projectionFactory.setResourceLoader(applicationContext);
        return projectionFactory;
    }
}
