begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Collection
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
name|MetricRegistry
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
name|config
operator|.
name|Registry
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
name|conn
operator|.
name|socket
operator|.
name|ConnectionSocketFactory
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
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
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
name|SolrMetricProducer
import|;
end_import

begin_comment
comment|/**  * Sub-class of PoolingHttpClientConnectionManager which tracks metrics interesting to Solr.  * Inspired by dropwizard metrics-httpclient library implementation.  */
end_comment

begin_class
DECL|class|InstrumentedPoolingHttpClientConnectionManager
specifier|public
class|class
name|InstrumentedPoolingHttpClientConnectionManager
extends|extends
name|PoolingHttpClientConnectionManager
implements|implements
name|SolrMetricProducer
block|{
DECL|field|metricsRegistry
specifier|protected
name|MetricRegistry
name|metricsRegistry
decl_stmt|;
DECL|method|InstrumentedPoolingHttpClientConnectionManager
specifier|public
name|InstrumentedPoolingHttpClientConnectionManager
parameter_list|(
name|Registry
argument_list|<
name|ConnectionSocketFactory
argument_list|>
name|socketFactoryRegistry
parameter_list|)
block|{
name|super
argument_list|(
name|socketFactoryRegistry
argument_list|)
expr_stmt|;
block|}
DECL|method|getMetricsRegistry
specifier|public
name|MetricRegistry
name|getMetricsRegistry
parameter_list|()
block|{
return|return
name|metricsRegistry
return|;
block|}
DECL|method|setMetricsRegistry
specifier|public
name|void
name|setMetricsRegistry
parameter_list|(
name|MetricRegistry
name|metricRegistry
parameter_list|)
block|{
name|this
operator|.
name|metricsRegistry
operator|=
name|metricRegistry
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|initializeMetrics
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|initializeMetrics
parameter_list|(
name|SolrMetricManager
name|manager
parameter_list|,
name|String
name|registry
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|this
operator|.
name|metricsRegistry
operator|=
name|manager
operator|.
name|registry
argument_list|(
name|registry
argument_list|)
expr_stmt|;
name|metricsRegistry
operator|.
name|register
argument_list|(
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"availableConnections"
argument_list|,
name|scope
argument_list|)
argument_list|,
call|(
name|Gauge
argument_list|<
name|Integer
argument_list|>
call|)
argument_list|()
operator|->
block|{
comment|// this acquires a lock on the connection pool; remove if contention sucks
return|return
name|getTotalStats
argument_list|()
operator|.
name|getAvailable
argument_list|()
return|;
block|}
block|)
function|;
name|metricsRegistry
operator|.
name|register
argument_list|(
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"leasedConnections"
argument_list|,
name|scope
argument_list|)
argument_list|,
call|(
name|Gauge
argument_list|<
name|Integer
argument_list|>
call|)
argument_list|()
operator|->
block|{
comment|// this acquires a lock on the connection pool; remove if contention sucks
return|return
name|getTotalStats
argument_list|()
operator|.
name|getLeased
argument_list|()
return|;
block|}
block|)
class|;
end_class

begin_expr_stmt
name|metricsRegistry
operator|.
name|register
argument_list|(
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"maxConnections"
argument_list|,
name|scope
argument_list|)
argument_list|,
call|(
name|Gauge
argument_list|<
name|Integer
argument_list|>
call|)
argument_list|()
operator|->
block|{
comment|// this acquires a lock on the connection pool; remove if contention sucks
return|return
name|getTotalStats
argument_list|()
operator|.
name|getMax
argument_list|()
return|;
block|}
end_expr_stmt

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|metricsRegistry
operator|.
name|register
argument_list|(
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"pendingConnections"
argument_list|,
name|scope
argument_list|)
argument_list|,
call|(
name|Gauge
argument_list|<
name|Integer
argument_list|>
call|)
argument_list|()
operator|->
block|{
comment|// this acquires a lock on the connection pool; remove if contention sucks
return|return
name|getTotalStats
argument_list|()
operator|.
name|getPending
argument_list|()
return|;
block|}
end_expr_stmt

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_return
return|return
name|Arrays
operator|.
name|asList
argument_list|(
literal|"availableConnections"
argument_list|,
literal|"leasedConnections"
argument_list|,
literal|"maxConnections"
argument_list|,
literal|"pendingConnections"
argument_list|)
return|;
end_return

begin_function
unit|}    @
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|OTHER
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
end_function

unit|}
end_unit
