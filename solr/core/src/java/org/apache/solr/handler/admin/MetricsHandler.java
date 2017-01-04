begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|MetricRegistry
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
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|StrUtils
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
name|SolrCore
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
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|security
operator|.
name|AuthorizationContext
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
name|security
operator|.
name|PermissionNameProvider
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

begin_comment
comment|/**  * Request handler to return metrics  */
end_comment

begin_class
DECL|class|MetricsHandler
specifier|public
class|class
name|MetricsHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|PermissionNameProvider
block|{
DECL|field|container
specifier|final
name|CoreContainer
name|container
decl_stmt|;
DECL|field|metricManager
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|method|MetricsHandler
specifier|public
name|MetricsHandler
parameter_list|()
block|{
name|this
operator|.
name|container
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|metricManager
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|MetricsHandler
specifier|public
name|MetricsHandler
parameter_list|(
name|CoreContainer
name|container
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|metricManager
operator|=
name|this
operator|.
name|container
operator|.
name|getMetricManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPermissionName
specifier|public
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|request
parameter_list|)
block|{
return|return
name|Name
operator|.
name|METRICS_READ_PERM
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|INVALID_STATE
argument_list|,
literal|"Core container instance not initialized"
argument_list|)
throw|;
block|}
name|MetricFilter
name|mustMatchFilter
init|=
name|parseMustMatchFilter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MetricType
argument_list|>
name|metricTypes
init|=
name|parseMetricTypes
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MetricFilter
argument_list|>
name|metricFilters
init|=
name|metricTypes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|MetricType
operator|::
name|asMetricFilter
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|requestedGroups
init|=
name|parseGroups
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|Group
name|group
range|:
name|requestedGroups
control|)
block|{
name|String
name|registryName
init|=
name|SolrMetricManager
operator|.
name|getRegistryName
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
name|Group
operator|.
name|core
condition|)
block|{
comment|// this requires special handling because of the way we create registry name for a core (deeply nested)
name|container
operator|.
name|getAllCoreNames
argument_list|()
operator|.
name|forEach
argument_list|(
name|s
lambda|->
block|{
name|String
name|coreRegistryName
decl_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|container
operator|.
name|getCore
argument_list|(
name|s
argument_list|)
init|)
block|{
name|coreRegistryName
operator|=
name|core
operator|.
name|getCoreMetricManager
argument_list|()
operator|.
name|getRegistryName
argument_list|()
expr_stmt|;
block|}
name|MetricRegistry
name|registry
init|=
name|metricManager
operator|.
name|registry
argument_list|(
name|coreRegistryName
argument_list|)
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
name|coreRegistryName
argument_list|,
name|MetricUtils
operator|.
name|toNamedList
argument_list|(
name|registry
argument_list|,
name|metricFilters
argument_list|,
name|mustMatchFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MetricRegistry
name|registry
init|=
name|metricManager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
name|registryName
argument_list|,
name|MetricUtils
operator|.
name|toNamedList
argument_list|(
name|registry
argument_list|,
name|metricFilters
argument_list|,
name|mustMatchFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|add
argument_list|(
literal|"metrics"
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|parseMustMatchFilter
specifier|private
name|MetricFilter
name|parseMustMatchFilter
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
name|prefix
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"prefix"
argument_list|)
decl_stmt|;
name|MetricFilter
name|mustMatchFilter
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|mustMatchFilter
operator|=
operator|new
name|SolrMetricManager
operator|.
name|PrefixFilter
argument_list|(
name|prefix
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mustMatchFilter
operator|=
name|MetricFilter
operator|.
name|ALL
expr_stmt|;
block|}
return|return
name|mustMatchFilter
return|;
block|}
DECL|method|parseGroups
specifier|private
name|List
argument_list|<
name|Group
argument_list|>
name|parseGroups
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
index|[]
name|groupStr
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupStr
operator|!=
literal|null
operator|&&
name|groupStr
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|groups
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|g
range|:
name|groupStr
control|)
block|{
name|groups
operator|.
name|addAll
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|g
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Group
argument_list|>
name|requestedGroups
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|Group
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
comment|// by default we return all groups
try|try
block|{
if|if
condition|(
name|groups
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|groups
operator|.
name|contains
argument_list|(
literal|"all"
argument_list|)
condition|)
block|{
name|requestedGroups
operator|=
name|groups
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|String
operator|::
name|trim
argument_list|)
operator|.
name|map
argument_list|(
name|Group
operator|::
name|valueOf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid group in: "
operator|+
name|groups
operator|+
literal|" specified. Must be one of (all, jvm, jetty, http, node, core)"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|requestedGroups
return|;
block|}
DECL|method|parseMetricTypes
specifier|private
name|List
argument_list|<
name|MetricType
argument_list|>
name|parseMetricTypes
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
index|[]
name|typeStr
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|types
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeStr
operator|!=
literal|null
operator|&&
name|typeStr
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|types
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|typeStr
control|)
block|{
name|types
operator|.
name|addAll
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|type
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|MetricType
argument_list|>
name|metricTypes
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|MetricType
operator|.
name|all
argument_list|)
decl_stmt|;
comment|// include all metrics by default
try|try
block|{
if|if
condition|(
name|types
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metricTypes
operator|=
name|types
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|String
operator|::
name|trim
argument_list|)
operator|.
name|map
argument_list|(
name|MetricType
operator|::
name|valueOf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid metric type in: "
operator|+
name|types
operator|+
literal|" specified. Must be one of (all, meter, timer, histogram, counter, gauge)"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|metricTypes
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A handler to return all the metrics gathered by Solr"
return|;
block|}
DECL|enum|MetricType
enum|enum
name|MetricType
block|{
DECL|enum constant|histogram
name|histogram
parameter_list|(
name|Histogram
operator|.
name|class
parameter_list|)
operator|,
DECL|enum constant|meter
constructor|meter(Meter.class
block|)
enum|,
DECL|enum constant|timer
name|timer
parameter_list|(
name|Timer
operator|.
name|class
parameter_list|)
operator|,
DECL|enum constant|counter
constructor|counter(Counter.class
block|)
operator|,
DECL|enum constant|gauge
name|gauge
argument_list|(
name|Gauge
operator|.
name|class
argument_list|)
operator|,
DECL|enum constant|all
name|all
argument_list|(
literal|null
argument_list|)
expr_stmt|;
end_class

begin_decl_stmt
DECL|field|klass
specifier|private
specifier|final
name|Class
name|klass
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|MetricType
name|MetricType
argument_list|(
name|Class
name|klass
argument_list|)
block|{
name|this
operator|.
name|klass
operator|=
name|klass
block|;     }
DECL|method|asMetricFilter
specifier|public
name|MetricFilter
name|asMetricFilter
argument_list|()
block|{
return|return
parameter_list|(
name|name
parameter_list|,
name|metric
parameter_list|)
lambda|->
name|klass
operator|==
literal|null
operator|||
name|klass
operator|.
name|isInstance
argument_list|(
name|metric
argument_list|)
return|;
block|}
end_expr_stmt

unit|} }
end_unit

