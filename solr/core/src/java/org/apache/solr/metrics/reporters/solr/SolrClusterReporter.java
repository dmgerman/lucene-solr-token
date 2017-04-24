begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics.reporters.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|reporters
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|Overseer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|ZkController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|SolrZkClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CoreContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrInfoBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
operator|.
name|MetricsCollectorHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|SolrMetricManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|SolrMetricReporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|ID
import|;
end_import

begin_comment
comment|/**  * This reporter sends selected metrics from local registries to {@link Overseer}.  *<p>The following configuration properties are supported:</p>  *<ul>  *<li>handler - (optional str) handler path where reports are sent. Default is  *   {@link MetricsCollectorHandler#HANDLER_PATH}.</li>  *<li>period - (optional int) how often reports are sent, in seconds. Default is 60. Setting this  *   to 0 disables the reporter.</li>  *<li>report - (optional multiple lst) report configuration(s), see below.</li>  *</ul>  * Each report configuration consist of the following properties:  *<ul>  *<li>registry - (required str) regex pattern matching source registries (see {@link SolrMetricManager#registryNames(String...)}),  *   may contain capture groups.</li>  *<li>group - (required str) target registry name where metrics will be grouped. This can be a regex pattern that  *   contains back-references to capture groups collected by<code>registry</code> pattern</li>  *<li>label - (optional str) optional prefix to prepend to metric names, may contain back-references to  *   capture groups collected by<code>registry</code> pattern</li>  *<li>filter - (optional multiple str) regex expression(s) matching selected metrics to be reported.</li>  *</ul>  * NOTE: this reporter uses predefined "overseer" group, and it's always created even if explicit configuration  * is missing. Default configuration uses report specifications from {@link #DEFAULT_REPORTS}.  *<p>Example configuration:</p>  *<pre>  *&lt;reporter name="test" group="overseer"&gt;  *&lt;str name="handler"&gt;/admin/metrics/collector&lt;/str&gt;  *&lt;int name="period"&gt;11&lt;/int&gt;  *&lt;lst name="report"&gt;  *&lt;str name="group"&gt;overseer&lt;/str&gt;  *&lt;str name="label"&gt;jvm&lt;/str&gt;  *&lt;str name="registry"&gt;solr\.jvm&lt;/str&gt;  *&lt;str name="filter"&gt;memory\.total\..*&lt;/str&gt;  *&lt;str name="filter"&gt;memory\.heap\..*&lt;/str&gt;  *&lt;str name="filter"&gt;os\.SystemLoadAverage&lt;/str&gt;  *&lt;str name="filter"&gt;threads\.count&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="report"&gt;  *&lt;str name="group"&gt;overseer&lt;/str&gt;  *&lt;str name="label"&gt;leader.$1&lt;/str&gt;  *&lt;str name="registry"&gt;solr\.core\.(.*)\.leader&lt;/str&gt;  *&lt;str name="filter"&gt;UPDATE\./update/.*&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/reporter&gt;  *</pre>  *  */
end_comment

begin_class
DECL|class|SolrClusterReporter
specifier|public
class|class
name|SolrClusterReporter
extends|extends
name|SolrMetricReporter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|CLUSTER_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_GROUP
init|=
name|SolrMetricManager
operator|.
name|overridableRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|cluster
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_REPORTS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|SolrReporter
operator|.
name|Report
argument_list|>
name|DEFAULT_REPORTS
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrReporter
operator|.
name|Report
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
operator|new
name|SolrReporter
operator|.
name|Report
argument_list|(
name|CLUSTER_GROUP
argument_list|,
literal|"jetty"
argument_list|,
name|SolrMetricManager
operator|.
name|overridableRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|jetty
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// all metrics
name|add
argument_list|(
operator|new
name|SolrReporter
operator|.
name|Report
argument_list|(
name|CLUSTER_GROUP
argument_list|,
literal|"jvm"
argument_list|,
name|SolrMetricManager
operator|.
name|overridableRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|jvm
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"memory\\.total\\..*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"memory\\.heap\\..*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"os\\.SystemLoadAverage"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"os\\.FreePhysicalMemorySize"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"os\\.FreeSwapSpaceSize"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"os\\.OpenFileDescriptorCount"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"threads\\.count"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|SolrReporter
operator|.
name|Report
argument_list|(
name|CLUSTER_GROUP
argument_list|,
literal|"node"
argument_list|,
name|SolrMetricManager
operator|.
name|overridableRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|node
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"CONTAINER\\.cores\\..*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"CONTAINER\\.fs\\..*"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|SolrReporter
operator|.
name|Report
argument_list|(
name|CLUSTER_GROUP
argument_list|,
literal|"leader.$1"
argument_list|,
literal|"solr\\.collection\\.(.*)\\.leader"
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"UPDATE\\./update/.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"QUERY\\./select.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"INDEX\\..*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"TLOG\\..*"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|field|handler
specifier|private
name|String
name|handler
init|=
name|MetricsCollectorHandler
operator|.
name|HANDLER_PATH
decl_stmt|;
DECL|field|period
specifier|private
name|int
name|period
init|=
name|SolrMetricManager
operator|.
name|DEFAULT_CLOUD_REPORTER_PERIOD
decl_stmt|;
DECL|field|reports
specifier|private
name|List
argument_list|<
name|SolrReporter
operator|.
name|Report
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reporter
specifier|private
name|SolrReporter
name|reporter
decl_stmt|;
comment|/**    * Create a reporter for metrics managed in a named registry.    *    * @param metricManager metric manager    * @param registryName  this is ignored    */
DECL|method|SolrClusterReporter
specifier|public
name|SolrClusterReporter
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registryName
parameter_list|)
block|{
name|super
argument_list|(
name|metricManager
argument_list|,
name|registryName
argument_list|)
expr_stmt|;
block|}
DECL|method|setHandler
specifier|public
name|void
name|setHandler
parameter_list|(
name|String
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
DECL|method|setPeriod
specifier|public
name|void
name|setPeriod
parameter_list|(
name|int
name|period
parameter_list|)
block|{
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
block|}
DECL|method|setReport
specifier|public
name|void
name|setReport
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|>
name|reportConfig
parameter_list|)
block|{
if|if
condition|(
name|reportConfig
operator|==
literal|null
operator|||
name|reportConfig
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|reportConfig
operator|.
name|forEach
argument_list|(
name|map
lambda|->
block|{
name|SolrReporter
operator|.
name|Report
name|r
init|=
name|SolrReporter
operator|.
name|Report
operator|.
name|fromMap
argument_list|(
name|map
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|reports
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|setReport
specifier|public
name|void
name|setReport
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
if|if
condition|(
name|map
operator|==
literal|null
operator|||
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|SolrReporter
operator|.
name|Report
name|r
init|=
name|SolrReporter
operator|.
name|Report
operator|.
name|fromMap
argument_list|(
name|map
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|reports
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
comment|// for unit tests
DECL|method|getPeriod
name|int
name|getPeriod
parameter_list|()
block|{
return|return
name|period
return|;
block|}
DECL|method|getReports
name|List
argument_list|<
name|SolrReporter
operator|.
name|Report
argument_list|>
name|getReports
parameter_list|()
block|{
return|return
name|reports
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|protected
name|void
name|validate
parameter_list|()
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
name|reports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// set defaults
name|reports
operator|=
name|DEFAULT_REPORTS
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|reporter
operator|!=
literal|null
condition|)
block|{
name|reporter
operator|.
name|close
argument_list|()
expr_stmt|;
empty_stmt|;
block|}
block|}
DECL|method|setCoreContainer
specifier|public
name|void
name|setCoreContainer
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
if|if
condition|(
name|reporter
operator|!=
literal|null
condition|)
block|{
name|reporter
operator|.
name|close
argument_list|()
expr_stmt|;
empty_stmt|;
block|}
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Reporter disabled for registry "
operator|+
name|registryName
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// start reporter only in cloud mode
if|if
condition|(
operator|!
name|cc
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not ZK-aware, not starting..."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|period
operator|<
literal|1
condition|)
block|{
comment|// don't start it
name|log
operator|.
name|info
argument_list|(
literal|"Turning off node reporter, period="
operator|+
name|period
argument_list|)
expr_stmt|;
return|return;
block|}
name|HttpClient
name|httpClient
init|=
name|cc
operator|.
name|getUpdateShardHandler
argument_list|()
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|ZkController
name|zk
init|=
name|cc
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|String
name|reporterId
init|=
name|zk
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|reporter
operator|=
name|SolrReporter
operator|.
name|Builder
operator|.
name|forReports
argument_list|(
name|metricManager
argument_list|,
name|reports
argument_list|)
operator|.
name|convertRatesTo
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|convertDurationsTo
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|withHandler
argument_list|(
name|handler
argument_list|)
operator|.
name|withReporterId
argument_list|(
name|reporterId
argument_list|)
operator|.
name|setCompact
argument_list|(
literal|true
argument_list|)
operator|.
name|cloudClient
argument_list|(
literal|false
argument_list|)
comment|// we want to send reports specifically to a selected leader instance
operator|.
name|skipAggregateValues
argument_list|(
literal|true
argument_list|)
comment|// we don't want to transport details of aggregates
operator|.
name|skipHistograms
argument_list|(
literal|true
argument_list|)
comment|// we don't want to transport histograms
operator|.
name|build
argument_list|(
name|httpClient
argument_list|,
operator|new
name|OverseerUrlSupplier
argument_list|(
name|zk
argument_list|)
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|start
argument_list|(
name|period
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
comment|// TODO: fix this when there is an elegant way to retrieve URL of a node that runs Overseer leader.
comment|// package visibility for unit tests
DECL|class|OverseerUrlSupplier
specifier|static
class|class
name|OverseerUrlSupplier
implements|implements
name|Supplier
argument_list|<
name|String
argument_list|>
block|{
DECL|field|DEFAULT_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_INTERVAL
init|=
literal|30000000
decl_stmt|;
comment|// 30s
DECL|field|zk
specifier|private
name|ZkController
name|zk
decl_stmt|;
DECL|field|lastKnownUrl
specifier|private
name|String
name|lastKnownUrl
init|=
literal|null
decl_stmt|;
DECL|field|lastCheckTime
specifier|private
name|long
name|lastCheckTime
init|=
literal|0
decl_stmt|;
DECL|field|interval
specifier|private
name|long
name|interval
init|=
name|DEFAULT_INTERVAL
decl_stmt|;
DECL|method|OverseerUrlSupplier
name|OverseerUrlSupplier
parameter_list|(
name|ZkController
name|zk
parameter_list|)
block|{
name|this
operator|.
name|zk
operator|=
name|zk
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|()
block|{
if|if
condition|(
name|zk
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// primitive caching for lastKnownUrl
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastKnownUrl
operator|!=
literal|null
operator|&&
operator|(
name|now
operator|-
name|lastCheckTime
operator|)
operator|<
name|interval
condition|)
block|{
return|return
name|lastKnownUrl
return|;
block|}
if|if
condition|(
operator|!
name|zk
operator|.
name|isConnected
argument_list|()
condition|)
block|{
return|return
name|lastKnownUrl
return|;
block|}
name|lastCheckTime
operator|=
name|now
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|zk
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|ZkNodeProps
name|props
decl_stmt|;
try|try
block|{
name|props
operator|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|Overseer
operator|.
name|OVERSEER_ELECT
operator|+
literal|"/leader"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not obtain overseer's address, skipping."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|lastKnownUrl
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
name|lastKnownUrl
return|;
block|}
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
return|return
name|lastKnownUrl
return|;
block|}
name|String
name|oid
init|=
name|props
operator|.
name|getStr
argument_list|(
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|oid
operator|==
literal|null
condition|)
block|{
return|return
name|lastKnownUrl
return|;
block|}
name|String
index|[]
name|ids
init|=
name|oid
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
comment|// unknown format
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown format of leader id, skipping: "
operator|+
name|oid
argument_list|)
expr_stmt|;
return|return
name|lastKnownUrl
return|;
block|}
comment|// convert nodeName back to URL
name|String
name|url
init|=
name|zk
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ids
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// check that it's parseable
try|try
block|{
operator|new
name|java
operator|.
name|net
operator|.
name|URL
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|mue
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Malformed Overseer's leader URL: url"
argument_list|,
name|mue
argument_list|)
expr_stmt|;
return|return
name|lastKnownUrl
return|;
block|}
name|lastKnownUrl
operator|=
name|url
expr_stmt|;
return|return
name|url
return|;
block|}
block|}
block|}
end_class

end_unit

