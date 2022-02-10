package com.sap.icd.odata4.processor;

import java.util.Arrays;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;

public class HandlerFactory {
    private final OData                 odata;
    private final ServiceMetadata       serviceMetadata;
    private final SpringDataProcessor[] processors;
    private final String                odataServicePath;

    public HandlerFactory(OData odata, ServiceMetadata serviceMetadata,
            SpringDataProcessor[] processors, String odataServicePath) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
        this.processors = processors;
        this.odataServicePath = odataServicePath;
    }

    public ODataHttpHandler getHandlerInstance() {
        HandlerWrapper odataHandler = new HandlerWrapper(odata, serviceMetadata,
                odataServicePath);
        Arrays.stream(processors).forEach(processor -> {
            odataHandler.register(processor);
        });
        return odataHandler;
    }
}
