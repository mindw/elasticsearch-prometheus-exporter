[![build status](https://github.com/mindw/elasticsearch-prometheus-exporter/workflows/CI/badge.svg?branch=master)](https://github.com/mindw/elasticsearch-prometheus-exporter/actions/workflows/ci.yml)
[![CodeQL](https://github.com/mindw/elasticsearch-prometheus-exporter/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/mindw/elasticsearch-prometheus-exporter/actions/workflows/codeql-analysis.yml)

# Prometheus Exporter Plugin for Elasticsearch

This is a builtin exporter from Elasticsearch to Prometheus.
It collects all relevant metrics and makes them available to Prometheus via the Elasticsearch REST API.

**Currently, the available metrics are:**

- Cluster status
- Nodes status:
    - JVM
    - Indices (global)
    - Transport
    - HTTP
    - Scripts
    - Process
    - Operating System
    - File System
    - Circuit Breaker
- Indices status
- Cluster settings (
  selected [disk allocation settings](https://www.elastic.co/guide/en/elasticsearch/reference/master/disk-allocator.html)
  only)
- Prometheus HotSpot metrics

## Fork diff with upstream

- Use JDK 21 (must match ElasticSearch JVM)
- 100% compatible metrics names with upstream.
- 99% of metrics names synced with official documentation.
- Added support OpenMetrics 1.0 format (info, enum, units etc.)
- Expose units where applicable.
- Expose indexing pressure metrics.
- Expose adaptive selection metrics.
- Expose discovery metrics.
- Expose JVM cgroups metrics.
- Expose Elasticsearch node version.
- Expand FS metrics with per device metrics
- Get all times in milliseconds to avoid losing sub second precision.
- Fixed `threadpool` metrics
- Add prometheus `client_hotspot` metrics.
- Fixed test suite.
- Updated GitHub actions.
- Expose 8.x introduced metrics.

## TODO

- Expose SLM metrics (default false)
- Expose Snapshot metrics (default false)
- Expose Data streams metrics (default false)

## Compatibility matrix

### Version 9.X

| Elasticsearch  | Plugin   | Release date |
|----------------|----------|--------------|
| 9.0.4          | 9.0.4.0  | Jul 24, 2025 |
| 9.0.3          | 9.0.3.0  | Jul 05, 2025 |

## Install

```
./bin/elasticsearch-plugin install -b \
  https://github.com/mindw/elasticsearch-prometheus-exporter/releases/download/9.0.4.0/prometheus-exporter-9.0.4.0.zip
```

**Do not forget to restart the node after the installation!**

Note that the plugin needs the following special entitlements:

- files: /proc/self/ read

If you have a lot of indices and think this data is irrelevant, you can disable in the main configuration file:

```
prometheus.indices: false
```

To disable exporting cluster settings use:

```
prometheus.cluster.settings: false
```

These settings can be
also [updated dynamically](https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html).

## Uninstall

`./bin/elasticsearch-plugin remove prometheus-exporter`

Do not forget to restart the node after installation!

## Usage

Metrics are directly available at:

    http://<your-elasticsearch-host>:9200/_prometheus/metrics

As a sample result, you get:

```
# HELP es_process_mem_total_virtual_bytes Memory used by ES process
# TYPE es_process_mem_total_virtual_bytes gauge
es_process_mem_total_virtual_bytes{cluster="develop",node="develop01",} 3.626733568E9
# HELP es_indices_indexing_is_throttled_bool Is indexing throttling ?
# TYPE es_indices_indexing_is_throttled_bool gauge
es_indices_indexing_is_throttled_bool{cluster="develop",node="develop01",} 0.0
# HELP es_jvm_gc_collection_time_seconds Time spent for GC collections
# TYPE es_jvm_gc_collection_time_seconds counter
es_jvm_gc_collection_time_seconds{cluster="develop",node="develop01",gc="old",} 0.0
es_jvm_gc_collection_time_seconds{cluster="develop",node="develop01",gc="young",} 0.0
# HELP es_indices_requestcache_memory_size_bytes Memory used for request cache
# TYPE es_indices_requestcache_memory_size_bytes gauge
es_indices_requestcache_memory_size_bytes{cluster="develop",node="develop01",} 0.0
# HELP es_indices_search_open_contexts_number Number of search open contexts
# TYPE es_indices_search_open_contexts_number gauge
es_indices_search_open_contexts_number{cluster="develop",node="develop01",} 0.0
# HELP es_jvm_mem_nonheap_used_bytes Memory used apart from heap
# TYPE es_jvm_mem_nonheap_used_bytes gauge
es_jvm_mem_nonheap_used_bytes{cluster="develop",node="develop01",} 5.5302736E7

...
```

### Configure the Prometheus target

On your Prometheus servers, configure a new job as usual.

For example, if you have a cluster of 3 nodes:

```YAML
- job_name: elasticsearch
  scrape_interval: 10s
  metrics_path: "/_prometheus/metrics"
  static_configs:
  - targets:
    - node1:9200
    - node2:9200
    - node3:9200
```

Of course, you could use the service discovery service instead of a static config.

Just keep in mind that `metrics_path` must be `/_prometheus/metrics`, otherwise Prometheus will find no metric.

## Project sources

The Maven project site is available at [GitHub](https://github.com/vvanholl/elasticsearch-prometheus-exporter).

## Testing

Project contains [integration tests](src/yamlRestTest/resources/rest-api-spec) implemented using
[rest layer](https://github.com/elastic/elasticsearch/blob/master/TESTING.asciidoc#testing-the-rest-layer)
framework.

To run everything similar to the GitHub Actions pipeline you can do:

```
docker run -v $(pwd):/home/gradle gradle:8.8.0-jdk21 su gradle -c 'gradle check'
```

NOTE: Please keep version in sync with `.github/workflows/ci.yml`

Complete test suite is run using:

```
gradle clean check
```

To run individual test file use:

```
gradle :integTest \
  -Dtests.class=org.elasticsearch.rest.PrometheusRestHandlerClientYamlTestSuiteIT \
  -Dtests.method="test {yaml=resthandler/20_metrics/Prometheus metrics can be pulled}"
```

## Credits

This plugin mainly uses the [Prometheus JVM Client](https://github.com/prometheus/client_java).

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
