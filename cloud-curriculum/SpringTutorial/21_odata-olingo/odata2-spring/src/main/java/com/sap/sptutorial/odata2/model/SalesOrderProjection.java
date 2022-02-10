package com.sap.sptutorial.odata2.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SalesOrderProjection {

    @Getter
    private final Long orderId;

    @Getter
    private final String orderTitle;

    @Getter
    private final String customerName;

}
