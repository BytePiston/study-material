package com.sap.sptutorial.odata2;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComplexTypeDefinitions {

    @Bean
    public ComplexType getCountryProjectionComplexType() {
        ComplexType salesOrderProjection = new ComplexType();
        salesOrderProjection.setName("SalesOrderProjection");
        List<Property> properties = new ArrayList<>();
        salesOrderProjection.setProperties(properties);

        properties.add(new SimpleProperty().setName("OrderId")
                .setType(EdmSimpleTypeKind.Int64));

        properties.add(new SimpleProperty().setName("OrderTitle")
                .setType(EdmSimpleTypeKind.String));

        properties.add(new SimpleProperty().setName("CustomerName")
                .setType(EdmSimpleTypeKind.String));

        return salesOrderProjection;
    }
}
