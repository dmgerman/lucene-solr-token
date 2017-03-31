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
name|Collection
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
name|HashMap
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Counter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Histogram
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Meter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|ScheduledReporter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|SolrClientCache
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|stats
operator|.
name|MetricUtils
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

begin_comment
comment|/**  * Implementation of {@link ScheduledReporter} that reports metrics from selected registries and sends  * them periodically as update requests to a selected Solr collection and to a configured handler.  */
end_comment

begin_class
DECL|class|SolrReporter
specifier|public
class|class
name|SolrReporter
extends|extends
name|ScheduledReporter
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
DECL|field|REGISTRY_ID
specifier|public
specifier|static
specifier|final
name|String
name|REGISTRY_ID
init|=
literal|"_registry_"
decl_stmt|;
DECL|field|REPORTER_ID
specifier|public
specifier|static
specifier|final
name|String
name|REPORTER_ID
init|=
literal|"_reporter_"
decl_stmt|;
DECL|field|GROUP_ID
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_ID
init|=
literal|"_group_"
decl_stmt|;
DECL|field|LABEL_ID
specifier|public
specifier|static
specifier|final
name|String
name|LABEL_ID
init|=
literal|"_label_"
decl_stmt|;
comment|/**    * Specification of what registries and what metrics to send.    */
DECL|class|Report
specifier|public
specifier|static
specifier|final
class|class
name|Report
block|{
DECL|field|groupPattern
specifier|public
name|String
name|groupPattern
decl_stmt|;
DECL|field|labelPattern
specifier|public
name|String
name|labelPattern
decl_stmt|;
DECL|field|registryPattern
specifier|public
name|String
name|registryPattern
decl_stmt|;
DECL|field|metricFilters
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|metricFilters
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Create a report specification      * @param groupPattern logical group for these metrics. This is used in {@link MetricsCollectorHandler}      *                     to select the target registry for metrics to aggregate. Must not be null or empty.      *                     It may contain back-references to capture groups from {@code registryPattern}      * @param labelPattern name of this group of metrics. This is used in {@link MetricsCollectorHandler}      *                     to prefix metric names. May be null or empty. It may contain back-references      *                     to capture groups from {@code registryPattern}.      * @param registryPattern pattern for selecting matching registries, see {@link SolrMetricManager#registryNames(String...)}      * @param metricFilters patterns for selecting matching metrics, see {@link org.apache.solr.metrics.SolrMetricManager.RegexFilter}      */
DECL|method|Report
specifier|public
name|Report
parameter_list|(
name|String
name|groupPattern
parameter_list|,
name|String
name|labelPattern
parameter_list|,
name|String
name|registryPattern
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|metricFilters
parameter_list|)
block|{
name|this
operator|.
name|groupPattern
operator|=
name|groupPattern
expr_stmt|;
name|this
operator|.
name|labelPattern
operator|=
name|labelPattern
expr_stmt|;
name|this
operator|.
name|registryPattern
operator|=
name|registryPattern
expr_stmt|;
if|if
condition|(
name|metricFilters
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|metricFilters
operator|.
name|addAll
argument_list|(
name|metricFilters
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fromMap
specifier|public
specifier|static
name|Report
name|fromMap
parameter_list|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|map
parameter_list|)
block|{
name|String
name|groupPattern
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
name|String
name|labelPattern
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"label"
argument_list|)
decl_stmt|;
name|String
name|registryPattern
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"registry"
argument_list|)
decl_stmt|;
name|Object
name|oFilters
init|=
name|map
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|metricFilters
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
if|if
condition|(
name|oFilters
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|oFilters
operator|instanceof
name|String
condition|)
block|{
name|metricFilters
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|(
name|String
operator|)
name|oFilters
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oFilters
operator|instanceof
name|Collection
condition|)
block|{
name|metricFilters
operator|=
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|oFilters
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid report filters, ignoring: "
operator|+
name|oFilters
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|groupPattern
operator|==
literal|null
operator|||
name|registryPattern
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid report configuration, group and registry required!: "
operator|+
name|map
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Report
argument_list|(
name|groupPattern
argument_list|,
name|labelPattern
argument_list|,
name|registryPattern
argument_list|,
name|metricFilters
argument_list|)
return|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|metricManager
specifier|private
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|reports
specifier|private
specifier|final
name|List
argument_list|<
name|Report
argument_list|>
name|reports
decl_stmt|;
DECL|field|reporterId
specifier|private
name|String
name|reporterId
decl_stmt|;
DECL|field|rateUnit
specifier|private
name|TimeUnit
name|rateUnit
decl_stmt|;
DECL|field|durationUnit
specifier|private
name|TimeUnit
name|durationUnit
decl_stmt|;
DECL|field|handler
specifier|private
name|String
name|handler
decl_stmt|;
DECL|field|skipHistograms
specifier|private
name|boolean
name|skipHistograms
decl_stmt|;
DECL|field|skipAggregateValues
specifier|private
name|boolean
name|skipAggregateValues
decl_stmt|;
DECL|field|cloudClient
specifier|private
name|boolean
name|cloudClient
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
comment|/**      * Create a builder for SolrReporter.      * @param metricManager metric manager that is the source of metrics      * @param reports report definitions      * @return builder      */
DECL|method|forReports
specifier|public
specifier|static
name|Builder
name|forReports
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|List
argument_list|<
name|Report
argument_list|>
name|reports
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|metricManager
argument_list|,
name|reports
argument_list|)
return|;
block|}
DECL|method|Builder
specifier|private
name|Builder
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|List
argument_list|<
name|Report
argument_list|>
name|reports
parameter_list|)
block|{
name|this
operator|.
name|metricManager
operator|=
name|metricManager
expr_stmt|;
name|this
operator|.
name|reports
operator|=
name|reports
expr_stmt|;
name|this
operator|.
name|rateUnit
operator|=
name|TimeUnit
operator|.
name|SECONDS
expr_stmt|;
name|this
operator|.
name|durationUnit
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
expr_stmt|;
name|this
operator|.
name|skipHistograms
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|skipAggregateValues
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|cloudClient
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|params
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Additional {@link SolrParams} to add to every request.      * @param params additional params      * @return {@code this}      */
DECL|method|withSolrParams
specifier|public
name|Builder
name|withSolrParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If true then use {@link org.apache.solr.client.solrj.impl.CloudSolrClient} for communication.      * Default is false.      * @param cloudClient use CloudSolrClient when true, {@link org.apache.solr.client.solrj.impl.HttpSolrClient} otherwise.      * @return {@code this}      */
DECL|method|cloudClient
specifier|public
name|Builder
name|cloudClient
parameter_list|(
name|boolean
name|cloudClient
parameter_list|)
block|{
name|this
operator|.
name|cloudClient
operator|=
name|cloudClient
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Histograms are difficult / impossible to aggregate, so it may not be      * worth to report them.      * @param skipHistograms when true then skip histograms from reports      * @return {@code this}      */
DECL|method|skipHistograms
specifier|public
name|Builder
name|skipHistograms
parameter_list|(
name|boolean
name|skipHistograms
parameter_list|)
block|{
name|this
operator|.
name|skipHistograms
operator|=
name|skipHistograms
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Individual values from {@link org.apache.solr.metrics.AggregateMetric} may not be worth to report.      * @param skipAggregateValues when tru then skip reporting individual values from the metric      * @return {@code this}      */
DECL|method|skipAggregateValues
specifier|public
name|Builder
name|skipAggregateValues
parameter_list|(
name|boolean
name|skipAggregateValues
parameter_list|)
block|{
name|this
operator|.
name|skipAggregateValues
operator|=
name|skipAggregateValues
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Handler name to use at the remote end.      *      * @param handler handler name, eg. "/admin/metricsCollector"      * @return {@code this}      */
DECL|method|withHandler
specifier|public
name|Builder
name|withHandler
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
return|return
name|this
return|;
block|}
comment|/**      * Use this id to identify metrics from this instance.      *      * @param reporterId reporter id      * @return {@code this}      */
DECL|method|withReporterId
specifier|public
name|Builder
name|withReporterId
parameter_list|(
name|String
name|reporterId
parameter_list|)
block|{
name|this
operator|.
name|reporterId
operator|=
name|reporterId
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Convert rates to the given time unit.      *      * @param rateUnit a unit of time      * @return {@code this}      */
DECL|method|convertRatesTo
specifier|public
name|Builder
name|convertRatesTo
parameter_list|(
name|TimeUnit
name|rateUnit
parameter_list|)
block|{
name|this
operator|.
name|rateUnit
operator|=
name|rateUnit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Convert durations to the given time unit.      *      * @param durationUnit a unit of time      * @return {@code this}      */
DECL|method|convertDurationsTo
specifier|public
name|Builder
name|convertDurationsTo
parameter_list|(
name|TimeUnit
name|durationUnit
parameter_list|)
block|{
name|this
operator|.
name|durationUnit
operator|=
name|durationUnit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Build it.      * @param client an instance of {@link HttpClient} to be used for making calls.      * @param urlProvider function that returns the base URL of Solr instance to target. May return      *                    null to indicate that reporting should be skipped. Note: this      *                    function will be called every time just before report is sent.      * @return configured instance of reporter      */
DECL|method|build
specifier|public
name|SolrReporter
name|build
parameter_list|(
name|HttpClient
name|client
parameter_list|,
name|Supplier
argument_list|<
name|String
argument_list|>
name|urlProvider
parameter_list|)
block|{
return|return
operator|new
name|SolrReporter
argument_list|(
name|client
argument_list|,
name|urlProvider
argument_list|,
name|metricManager
argument_list|,
name|reports
argument_list|,
name|handler
argument_list|,
name|reporterId
argument_list|,
name|rateUnit
argument_list|,
name|durationUnit
argument_list|,
name|params
argument_list|,
name|skipHistograms
argument_list|,
name|skipAggregateValues
argument_list|,
name|cloudClient
argument_list|)
return|;
block|}
block|}
DECL|field|reporterId
specifier|private
name|String
name|reporterId
decl_stmt|;
DECL|field|handler
specifier|private
name|String
name|handler
decl_stmt|;
DECL|field|urlProvider
specifier|private
name|Supplier
argument_list|<
name|String
argument_list|>
name|urlProvider
decl_stmt|;
DECL|field|clientCache
specifier|private
name|SolrClientCache
name|clientCache
decl_stmt|;
DECL|field|compiledReports
specifier|private
name|List
argument_list|<
name|CompiledReport
argument_list|>
name|compiledReports
decl_stmt|;
DECL|field|metricManager
specifier|private
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|skipHistograms
specifier|private
name|boolean
name|skipHistograms
decl_stmt|;
DECL|field|skipAggregateValues
specifier|private
name|boolean
name|skipAggregateValues
decl_stmt|;
DECL|field|cloudClient
specifier|private
name|boolean
name|cloudClient
decl_stmt|;
DECL|field|params
specifier|private
name|ModifiableSolrParams
name|params
decl_stmt|;
DECL|field|metadata
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
decl_stmt|;
DECL|class|CompiledReport
specifier|private
specifier|static
specifier|final
class|class
name|CompiledReport
block|{
DECL|field|group
name|String
name|group
decl_stmt|;
DECL|field|label
name|String
name|label
decl_stmt|;
DECL|field|registryPattern
name|Pattern
name|registryPattern
decl_stmt|;
DECL|field|filter
name|MetricFilter
name|filter
decl_stmt|;
DECL|method|CompiledReport
name|CompiledReport
parameter_list|(
name|Report
name|report
parameter_list|)
throws|throws
name|PatternSyntaxException
block|{
name|this
operator|.
name|group
operator|=
name|report
operator|.
name|groupPattern
expr_stmt|;
name|this
operator|.
name|label
operator|=
name|report
operator|.
name|labelPattern
expr_stmt|;
name|this
operator|.
name|registryPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|report
operator|.
name|registryPattern
argument_list|)
expr_stmt|;
name|this
operator|.
name|filter
operator|=
operator|new
name|SolrMetricManager
operator|.
name|RegexFilter
argument_list|(
name|report
operator|.
name|metricFilters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CompiledReport{"
operator|+
literal|"group='"
operator|+
name|group
operator|+
literal|'\''
operator|+
literal|", label='"
operator|+
name|label
operator|+
literal|'\''
operator|+
literal|", registryPattern="
operator|+
name|registryPattern
operator|+
literal|", filter="
operator|+
name|filter
operator|+
literal|'}'
return|;
block|}
block|}
DECL|method|SolrReporter
specifier|public
name|SolrReporter
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|Supplier
argument_list|<
name|String
argument_list|>
name|urlProvider
parameter_list|,
name|SolrMetricManager
name|metricManager
parameter_list|,
name|List
argument_list|<
name|Report
argument_list|>
name|metrics
parameter_list|,
name|String
name|handler
parameter_list|,
name|String
name|reporterId
parameter_list|,
name|TimeUnit
name|rateUnit
parameter_list|,
name|TimeUnit
name|durationUnit
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|boolean
name|skipHistograms
parameter_list|,
name|boolean
name|skipAggregateValues
parameter_list|,
name|boolean
name|cloudClient
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|"solr-reporter"
argument_list|,
name|MetricFilter
operator|.
name|ALL
argument_list|,
name|rateUnit
argument_list|,
name|durationUnit
argument_list|)
expr_stmt|;
name|this
operator|.
name|metricManager
operator|=
name|metricManager
expr_stmt|;
name|this
operator|.
name|urlProvider
operator|=
name|urlProvider
expr_stmt|;
name|this
operator|.
name|reporterId
operator|=
name|reporterId
expr_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|handler
operator|=
name|MetricsCollectorHandler
operator|.
name|HANDLER_PATH
expr_stmt|;
block|}
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|clientCache
operator|=
operator|new
name|SolrClientCache
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|compiledReports
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|forEach
argument_list|(
name|report
lambda|->
block|{
name|MetricFilter
name|filter
init|=
operator|new
name|SolrMetricManager
operator|.
name|RegexFilter
argument_list|(
name|report
operator|.
name|metricFilters
argument_list|)
decl_stmt|;
try|try
block|{
name|CompiledReport
name|cs
init|=
operator|new
name|CompiledReport
argument_list|(
name|report
argument_list|)
decl_stmt|;
name|compiledReports
operator|.
name|add
argument_list|(
name|cs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping report with invalid registryPattern: "
operator|+
name|report
operator|.
name|registryPattern
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|skipHistograms
operator|=
name|skipHistograms
expr_stmt|;
name|this
operator|.
name|skipAggregateValues
operator|=
name|skipAggregateValues
expr_stmt|;
name|this
operator|.
name|cloudClient
operator|=
name|cloudClient
expr_stmt|;
name|this
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|this
operator|.
name|params
operator|.
name|set
argument_list|(
name|REPORTER_ID
argument_list|,
name|reporterId
argument_list|)
expr_stmt|;
comment|// allow overrides to take precedence
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|params
operator|.
name|add
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
name|metadata
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|REPORTER_ID
argument_list|,
name|reporterId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|clientCache
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|report
specifier|public
name|void
name|report
parameter_list|()
block|{
name|String
name|url
init|=
name|urlProvider
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// if null then suppress reporting
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|SolrClient
name|solr
decl_stmt|;
if|if
condition|(
name|cloudClient
condition|)
block|{
name|solr
operator|=
name|clientCache
operator|.
name|getCloudSolrClient
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solr
operator|=
name|clientCache
operator|.
name|getHttpSolrClient
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|(
name|handler
argument_list|)
decl_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|compiledReports
operator|.
name|forEach
argument_list|(
name|report
lambda|->
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|registryNames
init|=
name|metricManager
operator|.
name|registryNames
argument_list|(
name|report
operator|.
name|registryPattern
argument_list|)
decl_stmt|;
name|registryNames
operator|.
name|forEach
argument_list|(
name|registryName
lambda|->
block|{
name|String
name|label
init|=
name|report
operator|.
name|label
decl_stmt|;
if|if
condition|(
name|label
operator|!=
literal|null
operator|&&
name|label
operator|.
name|indexOf
argument_list|(
literal|'$'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// label with back-references
name|Matcher
name|m
init|=
name|report
operator|.
name|registryPattern
operator|.
name|matcher
argument_list|(
name|registryName
argument_list|)
decl_stmt|;
name|label
operator|=
name|m
operator|.
name|replaceFirst
argument_list|(
name|label
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|effectiveLabel
init|=
name|label
decl_stmt|;
name|String
name|group
init|=
name|report
operator|.
name|group
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|indexOf
argument_list|(
literal|'$'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// group with back-references
name|Matcher
name|m
init|=
name|report
operator|.
name|registryPattern
operator|.
name|matcher
argument_list|(
name|registryName
argument_list|)
decl_stmt|;
name|group
operator|=
name|m
operator|.
name|replaceFirst
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|effectiveGroup
init|=
name|group
decl_stmt|;
name|MetricUtils
operator|.
name|toSolrInputDocuments
argument_list|(
name|metricManager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|report
operator|.
name|filter
argument_list|)
argument_list|,
name|MetricFilter
operator|.
name|ALL
argument_list|,
name|skipHistograms
argument_list|,
name|skipAggregateValues
argument_list|,
literal|false
argument_list|,
name|metadata
argument_list|,
name|doc
lambda|->
block|{
name|doc
operator|.
name|setField
argument_list|(
name|REGISTRY_ID
argument_list|,
name|registryName
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|GROUP_ID
argument_list|,
name|effectiveGroup
argument_list|)
expr_stmt|;
if|if
condition|(
name|effectiveLabel
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|LABEL_ID
argument_list|,
name|effectiveLabel
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// if no docs added then don't send a report
if|if
condition|(
name|req
operator|.
name|getDocuments
argument_list|()
operator|==
literal|null
operator|||
name|req
operator|.
name|getDocuments
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
comment|//log.info("%%% sending to " + url + ": " + req.getParams());
name|solr
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Error sending metric report"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|report
specifier|public
name|void
name|report
parameter_list|(
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
name|gauges
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counters
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
name|histograms
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
name|meters
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
name|timers
parameter_list|)
block|{
comment|// no-op - we do all the work in report()
block|}
block|}
end_class

end_unit
