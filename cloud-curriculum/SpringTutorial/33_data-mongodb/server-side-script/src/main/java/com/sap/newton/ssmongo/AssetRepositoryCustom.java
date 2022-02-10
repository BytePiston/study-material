package com.sap.newton.ssmongo;

import java.util.Collection;

public interface AssetRepositoryCustom {
    void addMetrics(String assetName, Collection<String> metricNames);
}
