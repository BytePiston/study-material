package com.sap.sptutorial.odata2;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataProcessor;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetComplexPropertyUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityLinkCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityLinkUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetLinksCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetLinksUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetFunctionImportUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetMediaResourceUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetMetadataUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetServiceDocumentUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetSimplePropertyUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.sap.icd.odatav2.spring.config.StandardODataJPAProcessor;

@Primary
@Component
@RequestScope
public class MyProcessor extends ODataSingleProcessor {

    private StandardODataJPAProcessor standardODataJpaProcessor;

    public MyProcessor(StandardODataJPAProcessor standardODataJpaProcessor) {
        this.standardODataJpaProcessor = standardODataJpaProcessor;
    }

    @Override
    public void setContext(ODataContext context) {
        standardODataJpaProcessor.setContext(context);
    }

    @Override
    public ODataContext getContext() {
        return standardODataJpaProcessor.getContext();
    }

    @Override
    public ODataResponse executeBatch(BatchHandler handler, String contentType,
            InputStream content) throws ODataException {
        return standardODataJpaProcessor.executeBatch(handler, contentType,
                content);
    }

    @Override
    public BatchResponsePart executeChangeSet(BatchHandler handler,
            List<ODataRequest> requests) throws ODataException {
        return standardODataJpaProcessor.executeChangeSet(handler, requests);
    }

    @Override
    public ODataResponse executeFunctionImport(GetFunctionImportUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.executeFunctionImport(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse executeFunctionImportValue(
            GetFunctionImportUriInfo uriInfo, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.executeFunctionImportValue(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse readEntitySimplePropertyValue(
            GetSimplePropertyUriInfo uriInfo, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.readEntitySimplePropertyValue(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse updateEntitySimplePropertyValue(
            PutMergePatchUriInfo uriInfo, InputStream content,
            String requestContentType, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.updateEntitySimplePropertyValue(
                uriInfo, content, requestContentType, contentType);
    }

    @Override
    public ODataResponse deleteEntitySimplePropertyValue(DeleteUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor
                .deleteEntitySimplePropertyValue(uriInfo, contentType);
    }

    @Override
    public ODataResponse readEntitySimpleProperty(
            GetSimplePropertyUriInfo uriInfo, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.readEntitySimpleProperty(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse updateEntitySimpleProperty(
            PutMergePatchUriInfo uriInfo, InputStream content,
            String requestContentType, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.updateEntitySimpleProperty(uriInfo,
                content, requestContentType, contentType);
    }

    @Override
    public ODataResponse readEntityMedia(GetMediaResourceUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readEntityMedia(uriInfo, contentType);
    }

    @Override
    public ODataResponse updateEntityMedia(PutMergePatchUriInfo uriInfo,
            InputStream content, String requestContentType, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.updateEntityMedia(uriInfo, content,
                requestContentType, contentType);
    }

    @Override
    public ODataResponse deleteEntityMedia(DeleteUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.deleteEntityMedia(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse readEntityLinks(GetEntitySetLinksUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readEntityLinks(uriInfo, contentType);
    }

    @Override
    public ODataResponse countEntityLinks(GetEntitySetLinksCountUriInfo uriInfo,
            String contentType) throws ODataException {
        // TODO Auto-generated method stub
        return super.countEntityLinks(uriInfo, contentType);
    }

    @Override
    public ODataResponse createEntityLink(PostUriInfo uriInfo,
            InputStream content, String requestContentType, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.createEntityLink(uriInfo, content,
                requestContentType, contentType);
    }

    @Override
    public ODataResponse readEntityLink(GetEntityLinkUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readEntityLink(uriInfo, contentType);
    }

    @Override
    public ODataResponse existsEntityLink(GetEntityLinkCountUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.existsEntityLink(uriInfo, contentType);
    }

    @Override
    public ODataResponse updateEntityLink(PutMergePatchUriInfo uriInfo,
            InputStream content, String requestContentType, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.updateEntityLink(uriInfo, content,
                requestContentType, contentType);
    }

    @Override
    public ODataResponse deleteEntityLink(DeleteUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.deleteEntityLink(uriInfo, contentType);
    }

    @Override
    public ODataResponse readEntityComplexProperty(
            GetComplexPropertyUriInfo uriInfo, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.readEntityComplexProperty(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse updateEntityComplexProperty(
            PutMergePatchUriInfo uriInfo, InputStream content,
            String requestContentType, boolean merge, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.updateEntityComplexProperty(uriInfo,
                content, requestContentType, merge, contentType);
    }

    @Override
    public ODataResponse readEntitySet(GetEntitySetUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readEntitySet(uriInfo, contentType);
    }

    @Override
    public ODataResponse countEntitySet(GetEntitySetCountUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.countEntitySet(uriInfo, contentType);
    }

    @Override
    public ODataResponse createEntity(PostUriInfo uriInfo, InputStream content,
            String requestContentType, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.createEntity(uriInfo, content,
                requestContentType, contentType);
    }

    @Override
    public ODataResponse readEntity(GetEntityUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readEntity(uriInfo, contentType);
    }

    @Override
    public ODataResponse existsEntity(GetEntityCountUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.existsEntity(uriInfo, contentType);
    }

    @Override
    public ODataResponse updateEntity(PutMergePatchUriInfo uriInfo,
            InputStream content, String requestContentType, boolean merge,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.updateEntity(uriInfo, content,
                requestContentType, merge, contentType);
    }

    @Override
    public ODataResponse deleteEntity(DeleteUriInfo uriInfo, String contentType)
            throws ODataException {
        return standardODataJpaProcessor.deleteEntity(uriInfo, contentType);
    }

    @Override
    public ODataResponse readServiceDocument(GetServiceDocumentUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readServiceDocument(uriInfo,
                contentType);
    }

    @Override
    public ODataResponse readMetadata(GetMetadataUriInfo uriInfo,
            String contentType) throws ODataException {
        return standardODataJpaProcessor.readMetadata(uriInfo, contentType);
    }

    @Override
    public List<String> getCustomContentTypes(
            Class<? extends ODataProcessor> processorFeature)
            throws ODataException {
        return standardODataJpaProcessor
                .getCustomContentTypes(processorFeature);
    }
}
