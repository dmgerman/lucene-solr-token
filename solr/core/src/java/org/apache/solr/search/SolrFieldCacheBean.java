begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|Set
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
name|metrics
operator|.
name|MetricsMap
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uninverting
operator|.
name|UninvertingReader
import|;
end_import

begin_comment
comment|/**  * A SolrInfoBean that provides introspection of the Solr FieldCache  *  */
end_comment

begin_class
DECL|class|SolrFieldCacheBean
specifier|public
class|class
name|SolrFieldCacheBean
implements|implements
name|SolrInfoBean
implements|,
name|SolrMetricProducer
block|{
DECL|field|disableEntryList
specifier|private
name|boolean
name|disableEntryList
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"disableSolrFieldCacheMBeanEntryList"
argument_list|)
decl_stmt|;
DECL|field|disableJmxEntryList
specifier|private
name|boolean
name|disableJmxEntryList
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"disableSolrFieldCacheMBeanEntryListJmx"
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|private
name|MetricRegistry
name|registry
decl_stmt|;
DECL|field|metricNames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|metricNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
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
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provides introspection of the Solr FieldCache "
return|;
block|}
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
name|CACHE
return|;
block|}
annotation|@
name|Override
DECL|method|getMetricNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getMetricNames
parameter_list|()
block|{
return|return
name|metricNames
return|;
block|}
annotation|@
name|Override
DECL|method|getMetricRegistry
specifier|public
name|MetricRegistry
name|getMetricRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
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
name|registryName
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|registry
operator|=
name|manager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
expr_stmt|;
name|MetricsMap
name|metricsMap
init|=
operator|new
name|MetricsMap
argument_list|(
parameter_list|(
name|detailed
parameter_list|,
name|map
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|detailed
operator|&&
operator|!
name|disableEntryList
operator|&&
operator|!
name|disableJmxEntryList
condition|)
block|{
name|UninvertingReader
operator|.
name|FieldCacheStats
name|fieldCacheStats
init|=
name|UninvertingReader
operator|.
name|getUninvertedStats
argument_list|()
decl_stmt|;
name|String
index|[]
name|entries
init|=
name|fieldCacheStats
operator|.
name|info
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"entries_count"
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"total_size"
argument_list|,
name|fieldCacheStats
operator|.
name|totalSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|entry
init|=
name|entries
index|[
name|i
index|]
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"entry#"
operator|+
name|i
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
literal|"entries_count"
argument_list|,
name|UninvertingReader
operator|.
name|getUninvertedStatsSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|manager
operator|.
name|register
argument_list|(
name|this
argument_list|,
name|registryName
argument_list|,
name|metricsMap
argument_list|,
literal|true
argument_list|,
literal|"fieldCache"
argument_list|,
name|Category
operator|.
name|CACHE
operator|.
name|toString
argument_list|()
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
