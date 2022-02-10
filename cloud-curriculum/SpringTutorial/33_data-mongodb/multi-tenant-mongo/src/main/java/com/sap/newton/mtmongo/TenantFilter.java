package com.sap.newton.mtmongo;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class TenantFilter implements Filter {
    private TenantProvider tenantProvider;
    private MongoIndexEnsurer indexEnsurer;
    private HashMap<String, Boolean> knownTenantIds;

    public TenantFilter(TenantProvider tenantProvider, MongoIndexEnsurer indexEnsurer) {
        this.tenantProvider = tenantProvider;
        this.indexEnsurer = indexEnsurer;
        this.knownTenantIds = new HashMap<>();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String tenantId = httpRequest.getHeader("tenant-id");
            if (tenantId != null) {
                tenantProvider.setTenantId(tenantId);
                if (!knownTenantIds.containsKey(tenantId)) {
                    indexEnsurer.ensureIndicesForTenant(tenantId);
                    knownTenantIds.put(tenantId, true);
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
