package com.sap.newton.aggregation;

import org.springframework.hateoas.ResourceSupport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetView extends ResourceSupport {
    private String assetId;
    private String name;
    private String state;
    private String errors;
    private String infos;
    private String warnings;
}
