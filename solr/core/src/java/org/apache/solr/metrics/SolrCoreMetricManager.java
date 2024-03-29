begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|cloud
operator|.
name|CloudDescriptor
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
name|NodeConfig
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
name|PluginInfo
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
name|core
operator|.
name|SolrInfoBean
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
comment|/**  * Responsible for collecting metrics from {@link SolrMetricProducer}'s  * and exposing metrics to {@link SolrMetricReporter}'s.  */
end_comment

begin_class
DECL|class|SolrCoreMetricManager
specifier|public
class|class
name|SolrCoreMetricManager
implements|implements
name|Closeable
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
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|tag
specifier|private
specifier|final
name|String
name|tag
decl_stmt|;
DECL|field|metricManager
specifier|private
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|registryName
specifier|private
name|String
name|registryName
decl_stmt|;
DECL|field|collectionName
specifier|private
name|String
name|collectionName
decl_stmt|;
DECL|field|shardName
specifier|private
name|String
name|shardName
decl_stmt|;
DECL|field|replicaName
specifier|private
name|String
name|replicaName
decl_stmt|;
DECL|field|leaderRegistryName
specifier|private
name|String
name|leaderRegistryName
decl_stmt|;
DECL|field|cloudMode
specifier|private
name|boolean
name|cloudMode
decl_stmt|;
comment|/**    * Constructs a metric manager.    *    * @param core the metric manager's core    */
DECL|method|SolrCoreMetricManager
specifier|public
name|SolrCoreMetricManager
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|core
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|metricManager
operator|=
name|core
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getMetricManager
argument_list|()
expr_stmt|;
name|initCloudMode
argument_list|()
expr_stmt|;
name|registryName
operator|=
name|createRegistryName
argument_list|(
name|cloudMode
argument_list|,
name|collectionName
argument_list|,
name|shardName
argument_list|,
name|replicaName
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|leaderRegistryName
operator|=
name|createLeaderRegistryName
argument_list|(
name|cloudMode
argument_list|,
name|collectionName
argument_list|,
name|shardName
argument_list|)
expr_stmt|;
block|}
DECL|method|initCloudMode
specifier|private
name|void
name|initCloudMode
parameter_list|()
block|{
name|CloudDescriptor
name|cd
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
if|if
condition|(
name|cd
operator|!=
literal|null
condition|)
block|{
name|cloudMode
operator|=
literal|true
expr_stmt|;
name|collectionName
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
expr_stmt|;
name|shardName
operator|=
name|cd
operator|.
name|getShardId
argument_list|()
expr_stmt|;
comment|//replicaName = cd.getCoreNodeName();
name|String
name|coreName
init|=
name|core
operator|.
name|getName
argument_list|()
decl_stmt|;
name|replicaName
operator|=
name|parseReplicaName
argument_list|(
name|collectionName
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicaName
operator|==
literal|null
condition|)
block|{
name|replicaName
operator|=
name|cd
operator|.
name|getCoreNodeName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Load reporters configured globally and specific to {@link org.apache.solr.core.SolrInfoBean.Group#core}    * group or with a registry name specific to this core.    */
DECL|method|loadReporters
specifier|public
name|void
name|loadReporters
parameter_list|()
block|{
name|NodeConfig
name|nodeConfig
init|=
name|core
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|PluginInfo
index|[]
name|pluginInfos
init|=
name|nodeConfig
operator|.
name|getMetricsConfig
argument_list|()
operator|.
name|getMetricReporters
argument_list|()
decl_stmt|;
name|metricManager
operator|.
name|loadReporters
argument_list|(
name|pluginInfos
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|tag
argument_list|,
name|SolrInfoBean
operator|.
name|Group
operator|.
name|core
argument_list|,
name|registryName
argument_list|)
expr_stmt|;
if|if
condition|(
name|cloudMode
condition|)
block|{
name|metricManager
operator|.
name|loadShardReporters
argument_list|(
name|pluginInfos
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Make sure that metrics already collected that correspond to the old core name    * are carried over and will be used under the new core name.    * This method also reloads reporters so that they use the new core name.    */
DECL|method|afterCoreSetName
specifier|public
name|void
name|afterCoreSetName
parameter_list|()
block|{
name|String
name|oldRegistryName
init|=
name|registryName
decl_stmt|;
name|String
name|oldLeaderRegistryName
init|=
name|leaderRegistryName
decl_stmt|;
name|initCloudMode
argument_list|()
expr_stmt|;
name|registryName
operator|=
name|createRegistryName
argument_list|(
name|cloudMode
argument_list|,
name|collectionName
argument_list|,
name|shardName
argument_list|,
name|replicaName
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|leaderRegistryName
operator|=
name|createLeaderRegistryName
argument_list|(
name|cloudMode
argument_list|,
name|collectionName
argument_list|,
name|shardName
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldRegistryName
operator|.
name|equals
argument_list|(
name|registryName
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// close old reporters
name|metricManager
operator|.
name|closeReporters
argument_list|(
name|oldRegistryName
argument_list|,
name|tag
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldLeaderRegistryName
operator|!=
literal|null
condition|)
block|{
name|metricManager
operator|.
name|closeReporters
argument_list|(
name|oldLeaderRegistryName
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
comment|// load reporters again, using the new core name
name|loadReporters
argument_list|()
expr_stmt|;
block|}
comment|/**    * Registers a mapping of name/metric's with the manager's metric registry.    *    * @param scope     the scope of the metrics to be registered (e.g. `/admin/ping`)    * @param producer  producer of metrics to be registered    */
DECL|method|registerMetricProducer
specifier|public
name|void
name|registerMetricProducer
parameter_list|(
name|String
name|scope
parameter_list|,
name|SolrMetricProducer
name|producer
parameter_list|)
block|{
if|if
condition|(
name|scope
operator|==
literal|null
operator|||
name|producer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"registerMetricProducer() called with illegal arguments: "
operator|+
literal|"scope = "
operator|+
name|scope
operator|+
literal|", producer = "
operator|+
name|producer
argument_list|)
throw|;
block|}
name|producer
operator|.
name|initializeMetrics
argument_list|(
name|metricManager
argument_list|,
name|getRegistryName
argument_list|()
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the registry used by this SolrCore.    */
DECL|method|getRegistry
specifier|public
name|MetricRegistry
name|getRegistry
parameter_list|()
block|{
if|if
condition|(
name|registryName
operator|!=
literal|null
condition|)
block|{
return|return
name|metricManager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Closes reporters specific to this core.    */
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
name|metricManager
operator|.
name|closeReporters
argument_list|(
name|getRegistryName
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
if|if
condition|(
name|getLeaderRegistryName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metricManager
operator|.
name|closeReporters
argument_list|(
name|getLeaderRegistryName
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
return|return
name|core
return|;
block|}
comment|/**    * Metric registry name of the manager.    *    * In order to make it easier for reporting tools to aggregate metrics from    * different cores that logically belong to a single collection we convert the    * core name into a dot-separated hierarchy of: collection name, shard name (with optional split)    * and replica name.    *    *<p>For example, when the core name looks like this but it's NOT a SolrCloud collection:    *<code>my_collection_shard1_1_replica1</code> then this will be used as the registry name (plus    * the required<code>solr.core</code> prefix). However,    * if this is a SolrCloud collection<code>my_collection</code> then the registry name will become    *<code>solr.core.my_collection.shard1_1.replica1</code>.</p>    *    *    * @return the metric registry name of the manager.    */
DECL|method|getRegistryName
specifier|public
name|String
name|getRegistryName
parameter_list|()
block|{
return|return
name|registryName
return|;
block|}
comment|/**    * Metric registry name for leader metrics. This is null if not in cloud mode.    * @return metric registry name for leader metrics    */
DECL|method|getLeaderRegistryName
specifier|public
name|String
name|getLeaderRegistryName
parameter_list|()
block|{
return|return
name|leaderRegistryName
return|;
block|}
comment|/**    * Return a tag specific to this instance.    */
DECL|method|getTag
specifier|public
name|String
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
DECL|method|createRegistryName
specifier|public
specifier|static
name|String
name|createRegistryName
parameter_list|(
name|boolean
name|cloud
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|shardName
parameter_list|,
name|String
name|replicaName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
if|if
condition|(
name|cloud
condition|)
block|{
comment|// build registry name from logical names
return|return
name|SolrMetricManager
operator|.
name|getRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|core
argument_list|,
name|collectionName
argument_list|,
name|shardName
argument_list|,
name|replicaName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|SolrMetricManager
operator|.
name|getRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|core
argument_list|,
name|coreName
argument_list|)
return|;
block|}
block|}
comment|/**    * This method is used by {@link org.apache.solr.core.CoreContainer#rename(String, String)}.    * @param aCore existing core with old name    * @param coreName new name    * @return new registry name    */
DECL|method|createRegistryName
specifier|public
specifier|static
name|String
name|createRegistryName
parameter_list|(
name|SolrCore
name|aCore
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
name|CloudDescriptor
name|cd
init|=
name|aCore
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
name|String
name|replicaName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cd
operator|!=
literal|null
condition|)
block|{
name|replicaName
operator|=
name|parseReplicaName
argument_list|(
name|cd
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
block|}
return|return
name|createRegistryName
argument_list|(
name|cd
operator|!=
literal|null
argument_list|,
name|cd
operator|!=
literal|null
condition|?
name|cd
operator|.
name|getCollectionName
argument_list|()
else|:
literal|null
argument_list|,
name|cd
operator|!=
literal|null
condition|?
name|cd
operator|.
name|getShardId
argument_list|()
else|:
literal|null
argument_list|,
name|replicaName
argument_list|,
name|coreName
argument_list|)
return|;
block|}
DECL|method|parseReplicaName
specifier|public
specifier|static
name|String
name|parseReplicaName
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
if|if
condition|(
name|collectionName
operator|==
literal|null
operator|||
operator|!
name|coreName
operator|.
name|startsWith
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// split "collection1_shard1_1_replica1" into parts
if|if
condition|(
name|coreName
operator|.
name|length
argument_list|()
operator|>
name|collectionName
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|str
init|=
name|coreName
operator|.
name|substring
argument_list|(
name|collectionName
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|str
operator|.
name|lastIndexOf
argument_list|(
literal|"_replica"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
comment|// ?? no _replicaN part ??
return|return
name|str
return|;
block|}
else|else
block|{
return|return
name|str
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
DECL|method|createLeaderRegistryName
specifier|public
specifier|static
name|String
name|createLeaderRegistryName
parameter_list|(
name|boolean
name|cloud
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|shardName
parameter_list|)
block|{
if|if
condition|(
name|cloud
condition|)
block|{
return|return
name|SolrMetricManager
operator|.
name|getRegistryName
argument_list|(
name|SolrInfoBean
operator|.
name|Group
operator|.
name|collection
argument_list|,
name|collectionName
argument_list|,
name|shardName
argument_list|,
literal|"leader"
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

