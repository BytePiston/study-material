package com.sap.newton.aggregation;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class AssetResourceProcesssor implements ResourceProcessor<Resource<Asset>>{
    private EntityLinks entityLinks;

    public AssetResourceProcesssor(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Asset> process(Resource<Asset> resource) {
        resource.add(entityLinks.linkToSingleResource(AssetView.class, resource.getContent().getId()).withRel("assetWithAlertCount"  ));
        return resource;
    }

}
