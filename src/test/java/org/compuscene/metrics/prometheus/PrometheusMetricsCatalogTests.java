/*
 * Copyright [2024] [Prometheus Exporter Contributors]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compuscene.metrics.prometheus;

import org.junit.Before;
import org.junit.Test;

public class PrometheusMetricsCatalogTests {

    private PrometheusMetricsCatalog catalog;

    @Before
    public void setUp() {
        catalog = new PrometheusMetricsCatalog("test-cluster", "test-node", "test-id", "es_");
    }

    @Test
    public void testNodeCounterWithPositiveValue() {
        catalog.registerNodeCounter("test_positive", "Test positive counter");
        catalog.setNodeCounter("test_positive", 42.0);
    }

    @Test
    public void testNodeCounterWithNegativeValueDoesNotThrow() {
        catalog.registerNodeCounter("test_negative", "Test negative counter");
        catalog.setNodeCounter("test_negative", -1669444.66);
    }

    @Test
    public void testNodeCounterNegativeThenPositive() {
        catalog.registerNodeCounter("test_mixed", "Test mixed counter");
        catalog.setNodeCounter("test_mixed", -100.0);
        catalog.setNodeCounter("test_mixed", 42.0);
    }

    @Test
    public void testClusterCounterWithNegativeValueDoesNotThrow() {
        catalog.registerClusterCounter("test_cluster_neg", "Test cluster negative counter");
        catalog.setClusterCounter("test_cluster_neg", -999.0);
    }

    @Test
    public void testCounterWithNegativeValueDoesNotThrow() {
        catalog.registerCounter("test_plain_neg", "Test plain negative counter", "label1");
        catalog.setCounter("test_plain_neg", -1.0, "value1");
    }

    @Test
    public void testNodeCounterWithZeroValue() {
        catalog.registerNodeCounter("test_zero", "Test zero counter");
        catalog.setNodeCounter("test_zero", 0.0);
    }
}
