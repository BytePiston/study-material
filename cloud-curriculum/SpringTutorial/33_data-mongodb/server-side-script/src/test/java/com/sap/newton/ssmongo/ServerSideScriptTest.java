package com.sap.newton.ssmongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataMongoTest
@ActiveProfiles("test")
public class ServerSideScriptTest {
    @Autowired
    AssetRepository assetRepository;

    @Autowired
    MetricRepository metricRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testAddMetrics() {
        ArrayList<Metric> metrics = new ArrayList<>();
        metrics.add(Metric.builder().name("moment").build());
        metricRepository.save(metrics);
        Asset asset = Asset.builder().name("turbine").build();
        assetRepository.save(asset);
        assetRepository.addMetrics("turbine", Arrays.asList("moment"));

        Collection<Asset> turbines = assetRepository.findByName("turbine");
        Collection<Metric> momentMetrics = metricRepository.findByName("moment");

        assertThat(turbines).flatExtracting("metrics")
                .containsAll(momentMetrics.stream().map(Metric::getId).collect(Collectors.toList()));
    }

}
