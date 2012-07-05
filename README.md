Demo Application for Lucene and Solr
===================================

This repository contains the example code I used for my talk on [Lucene](http://lucene.apache.org) and [Solr](http://lucene.apache.org/solr) I gave for the [Java User Group Karlsruhe](http://jug-ka.de). It consists of a subproject, lucene, that contains the logic for indexing and searching using Lucene. The subproject solr-indexer is used to index data in a running Solr instance, solr provides the configuration as well as some tests. web contains a very simple [Dropwizard](http://dropwizard.codahale.com) web app that can be used to query Lucene as well as Solr.

See the README files in the individual projects for information on running the application. You need to have [Maven](http://maven.apache.org) as well as [Gradle](http://gradle.org) installed.

Note that this code is developed for demonstration purpose and doesn't necessarily reflect all aspects of how to implement a production search service.
