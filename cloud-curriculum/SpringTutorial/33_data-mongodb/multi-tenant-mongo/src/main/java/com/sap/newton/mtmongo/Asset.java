package com.sap.newton.mtmongo;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="#{tenantProvider.getTenantCollectionName('Asset')}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id
    ObjectId id;
    @Indexed
    String name;
}