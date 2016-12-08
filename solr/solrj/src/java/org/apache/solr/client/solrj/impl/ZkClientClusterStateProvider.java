begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
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
name|impl
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|cloud
operator|.
name|Aliases
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
name|ClusterState
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
name|ZkStateReader
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
name|ZooKeeperException
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

begin_class
DECL|class|ZkClientClusterStateProvider
specifier|public
class|class
name|ZkClientClusterStateProvider
implements|implements
name|CloudSolrClient
operator|.
name|ClusterStateProvider
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
DECL|field|zkStateReader
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|zkHost
name|String
name|zkHost
decl_stmt|;
DECL|field|zkConnectTimeout
name|int
name|zkConnectTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|zkClientTimeout
name|int
name|zkClientTimeout
init|=
literal|10000
decl_stmt|;
DECL|method|ZkClientClusterStateProvider
specifier|public
name|ZkClientClusterStateProvider
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|zkHosts
parameter_list|,
name|String
name|chroot
parameter_list|)
block|{
name|zkHost
operator|=
name|buildZkHostString
argument_list|(
name|zkHosts
argument_list|,
name|chroot
argument_list|)
expr_stmt|;
block|}
DECL|method|ZkClientClusterStateProvider
specifier|public
name|ZkClientClusterStateProvider
parameter_list|(
name|String
name|zkHost
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState
specifier|public
name|ClusterState
operator|.
name|CollectionRef
name|getState
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
return|return
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionRef
argument_list|(
name|collection
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|liveNodes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|()
block|{
return|return
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAlias
specifier|public
name|String
name|getAlias
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|Aliases
name|aliases
init|=
name|zkStateReader
operator|.
name|getAliases
argument_list|()
decl_stmt|;
return|return
name|aliases
operator|.
name|getCollectionAlias
argument_list|(
name|collection
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getClusterProperties
parameter_list|()
block|{
return|return
name|zkStateReader
operator|.
name|getClusterProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCollectionName
specifier|public
name|String
name|getCollectionName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Aliases
name|aliases
init|=
name|zkStateReader
operator|.
name|getAliases
argument_list|()
decl_stmt|;
if|if
condition|(
name|aliases
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionAliases
init|=
name|aliases
operator|.
name|getCollectionAliasMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectionAliases
operator|!=
literal|null
operator|&&
name|collectionAliases
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|collectionAliases
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|name
return|;
block|}
comment|/**    * Download a named config from Zookeeper to a location on the filesystem    * @param configName    the name of the config    * @param downloadPath  the path to write config files to    * @throws IOException  if an I/O exception occurs    */
DECL|method|downloadConfig
specifier|public
name|void
name|downloadConfig
parameter_list|(
name|String
name|configName
parameter_list|,
name|Path
name|downloadPath
parameter_list|)
throws|throws
name|IOException
block|{
name|connect
argument_list|()
expr_stmt|;
name|zkStateReader
operator|.
name|getConfigManager
argument_list|()
operator|.
name|downloadConfigDir
argument_list|(
name|configName
argument_list|,
name|downloadPath
argument_list|)
expr_stmt|;
block|}
comment|/**    * Upload a set of config files to Zookeeper and give it a name    *    * NOTE: You should only allow trusted users to upload configs.  If you    * are allowing client access to zookeeper, you should protect the    * /configs node against unauthorised write access.    *    * @param configPath {@link java.nio.file.Path} to the config files    * @param configName the name of the config    * @throws IOException if an IO error occurs    */
DECL|method|uploadConfig
specifier|public
name|void
name|uploadConfig
parameter_list|(
name|Path
name|configPath
parameter_list|,
name|String
name|configName
parameter_list|)
throws|throws
name|IOException
block|{
name|connect
argument_list|()
expr_stmt|;
name|zkStateReader
operator|.
name|getConfigManager
argument_list|()
operator|.
name|uploadConfigDir
argument_list|(
name|configPath
argument_list|,
name|configName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|connect
specifier|public
name|void
name|connect
parameter_list|()
block|{
if|if
condition|(
name|zkStateReader
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|zkStateReader
operator|==
literal|null
condition|)
block|{
name|ZkStateReader
name|zk
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zk
operator|=
operator|new
name|ZkStateReader
argument_list|(
name|zkHost
argument_list|,
name|zkClientTimeout
argument_list|,
name|zkConnectTimeout
argument_list|)
expr_stmt|;
name|zk
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
name|zk
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Cluster at {} ready"
argument_list|,
name|zkHost
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|zk
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|zk
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|zk
operator|!=
literal|null
condition|)
name|zk
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// do not wrap because clients may be relying on the underlying exception being thrown
throw|throw
name|e
throw|;
block|}
block|}
block|}
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
name|zkStateReader
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|zkStateReader
operator|!=
literal|null
condition|)
name|zkStateReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|buildZkHostString
specifier|static
name|String
name|buildZkHostString
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|zkHosts
parameter_list|,
name|String
name|chroot
parameter_list|)
block|{
if|if
condition|(
name|zkHosts
operator|==
literal|null
operator|||
name|zkHosts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot create CloudSearchClient without valid ZooKeeper host; none specified!"
argument_list|)
throw|;
block|}
name|StringBuilder
name|zkBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|lastIndexValue
init|=
name|zkHosts
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|zkHost
range|:
name|zkHosts
control|)
block|{
name|zkBuilder
operator|.
name|append
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|lastIndexValue
condition|)
block|{
name|zkBuilder
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|chroot
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|chroot
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|zkBuilder
operator|.
name|append
argument_list|(
name|chroot
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The chroot must start with a forward slash."
argument_list|)
throw|;
block|}
block|}
comment|/* Log the constructed connection string and then initialize. */
specifier|final
name|String
name|zkHostString
init|=
name|zkBuilder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Final constructed zkHost string: "
operator|+
name|zkHostString
argument_list|)
expr_stmt|;
return|return
name|zkHostString
return|;
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
name|zkHost
return|;
block|}
block|}
end_class

end_unit
