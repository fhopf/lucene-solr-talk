Demo Application for Lucene, Solr and Elasticsearch
===================================

This repository contains the example code I use for talks on [Lucene](http://lucene.apache.org), [Solr](http://lucene.apache.org/solr) and [Elasticsearch](http://elasticsearch.org). It consists of a subproject, lucene, that contains the logic for indexing and searching using Lucene. The subproject solr-indexer is used to index data in a running Solr instance, solr provides the configuration as well as some tests. web contains a very simple [Dropwizard](http://dropwizard.codahale.com) web app that can be used to query Lucene as well as Solr. The elasticsearch project is used to run the Elasticsearch instance, elasticsearch-indexer indexes data in Elasticsearch using the Java client.

See the README files in the individual projects for information on running the application. You need to have [Gradle](http://gradle.org) installed.

Note that this code is developed for demonstration purpose and doesn't necessarily reflect all aspects of how to implement a production search service.
