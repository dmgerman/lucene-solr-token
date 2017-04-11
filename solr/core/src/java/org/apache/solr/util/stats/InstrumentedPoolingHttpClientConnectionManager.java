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
annotation|@
name|Override
DECL|method|initializeMetrics
specifier|public
name|void
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
name|manager
operator|.
name|registerGauge
argument_list|(
literal|null
argument_list|,
name|registry
argument_list|,
parameter_list|()
lambda|->
name|getTotalStats
argument_list|()
operator|.
name|getAvailable
argument_list|()
argument_list|,
literal|true
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"availableConnections"
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
comment|// this acquires a lock on the connection pool; remove if contention sucks
name|manager
operator|.
name|registerGauge
argument_list|(
literal|null
argument_list|,
name|registry
argument_list|,
parameter_list|()
lambda|->
name|getTotalStats
argument_list|()
operator|.
name|getLeased
argument_list|()
argument_list|,
literal|true
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"leasedConnections"
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|registerGauge
argument_list|(
literal|null
argument_list|,
name|registry
argument_list|,
parameter_list|()
lambda|->
name|getTotalStats
argument_list|()
operator|.
name|getMax
argument_list|()
argument_list|,
literal|true
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"maxConnections"
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|registerGauge
argument_list|(
literal|null
argument_list|,
name|registry
argument_list|,
parameter_list|()
lambda|->
name|getTotalStats
argument_list|()
operator|.
name|getPending
argument_list|()
argument_list|,
literal|true
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"pendingConnections"
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

