package com.sap.newton.aggregation;

import java.util.Collection;

import org.bson.types.ObjectId;

public interface AssetRepositoryCustom {
    Collection<AssetView> findAllWithStateCount();
    AssetView findWithStateCount(ObjectId id);
}
