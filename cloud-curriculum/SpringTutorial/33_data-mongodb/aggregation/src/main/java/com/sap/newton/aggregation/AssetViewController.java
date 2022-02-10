package com.sap.newton.aggregation;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ExposesResourceFor(AssetView.class)
@RequestMapping("assetViews")
public class AssetViewController {
    private AssetRepository assetRepository;
    private EntityLinks entityLinks;

    public AssetViewController(AssetRepository assetRepository, EntityLinks entityLinks) {
        this.assetRepository = assetRepository;
        this.entityLinks = entityLinks;
    }

    @GetMapping
    public Collection<AssetView> getAssetViews() {
        Collection<AssetView> assetViews = assetRepository.findAllWithStateCount();
        assetViews.forEach(assetView -> {
            Link selfLink = entityLinks.linkToSingleResource(AssetView.class, assetView.getAssetId()).withSelfRel();
            assetView.add(selfLink);
            Link assetLink = entityLinks.linkToSingleResource(Asset.class, assetView.getAssetId()).withRel("asset");
            assetView.add(assetLink);
        });
        return assetViews;
    }

    @GetMapping("{id}")
    public AssetView getAssetView(@PathVariable("id") String id) {
        AssetView assetView = assetRepository.findWithStateCount(new ObjectId(id));
        if (assetView == null) {
            throw new ResourceNotFoundException();
        }
        Link selfLink = entityLinks.linkToSingleResource(AssetView.class, id).withSelfRel();
        assetView.add(selfLink);
        Link assetLink = entityLinks.linkToSingleResource(Asset.class, id).withRel("asset");
        assetView.add(assetLink);
        return assetView;
    }
}
