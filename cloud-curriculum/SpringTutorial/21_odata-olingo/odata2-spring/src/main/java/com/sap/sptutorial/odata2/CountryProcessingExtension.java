package com.sap.sptutorial.odata2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmExtension;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmSchemaView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CountryProcessingExtension implements JPAEdmExtension {

    @Value("classpath:edm-mappings.xml")
    private Resource mappingsResource;
    
    @Autowired
    private List<ComplexType> complexTypes;

    @Override
    public void extendJPAEdmSchema(final JPAEdmSchemaView view) {
        final Schema edmSchema = view.getEdmSchema();
        edmSchema.setComplexTypes(complexTypes);
    }

    @Override
    public void extendWithOperation(JPAEdmSchemaView view) {
        view.registerOperations(SalesOrderOperations.class, null);
    }

    @Override
    public InputStream getJPAEdmMappingModelStream() {
        try {
            return mappingsResource.getInputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}