package com.sap.newton.mtmongo;

import org.springframework.stereotype.Component;

@Component
public class TenantProvider {
    private static final String SEPARATOR = "?";
    private InheritableThreadLocal<String> tenantId = new InheritableThreadLocal<>();

    public String getTenantCollectionName(String collectionName) {
        if (collectionName == null) {
            throw new IllegalArgumentException("collection name must not be null");
        }
        return getTenantId() + SEPARATOR + collectionName;
    }

    public String getTenantId() {
        String _tenantId = tenantId.get();
        return (_tenantId != null) ? _tenantId : "notenant";
    }

    public void setTenantId(String tenantId) {
        this.tenantId.set(tenantId);
    }
}
