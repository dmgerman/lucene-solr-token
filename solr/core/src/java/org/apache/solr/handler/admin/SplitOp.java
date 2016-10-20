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
name|Arrays
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
name|DocCollection
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
name|DocRouter
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
name|Slice
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
name|CoreAdminParams
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|update
operator|.
name|SplitIndexCommand
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
name|cloud
operator|.
name|DocCollection
operator|.
name|DOC_ROUTER
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
name|PATH
import|;
end_import

begin_class
DECL|class|SplitOp
class|class
name|SplitOp
implements|implements
name|CoreAdminHandler
operator|.
name|CoreAdminOp
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
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|CoreAdminHandler
operator|.
name|CallInfo
name|it
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|it
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|pathsArr
init|=
name|params
operator|.
name|getParams
argument_list|(
name|PATH
argument_list|)
decl_stmt|;
name|String
name|rangesStr
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|RANGES
argument_list|)
decl_stmt|;
comment|// ranges=a-b,c-d,e-f
if|if
condition|(
name|rangesStr
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|rangesArr
init|=
name|rangesStr
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|rangesArr
operator|.
name|length
operator|==
literal|0
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
name|BAD_REQUEST
argument_list|,
literal|"There must be at least one range specified to split an index"
argument_list|)
throw|;
block|}
else|else
block|{
name|ranges
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|rangesArr
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|r
range|:
name|rangesArr
control|)
block|{
try|try
block|{
name|ranges
operator|.
name|add
argument_list|(
name|DocRouter
operator|.
name|DEFAULT
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Exception parsing hexadecimal hash range: "
operator|+
name|r
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
name|String
name|splitKey
init|=
name|params
operator|.
name|get
argument_list|(
literal|"split.key"
argument_list|)
decl_stmt|;
name|String
index|[]
name|newCoreNames
init|=
name|params
operator|.
name|getParams
argument_list|(
literal|"targetCore"
argument_list|)
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|pathsArr
operator|==
literal|null
operator|||
name|pathsArr
operator|.
name|length
operator|==
literal|0
operator|)
operator|&&
operator|(
name|newCoreNames
operator|==
literal|null
operator|||
name|newCoreNames
operator|.
name|length
operator|==
literal|0
operator|)
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
name|BAD_REQUEST
argument_list|,
literal|"Either path or targetCore param must be specified"
argument_list|)
throw|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Invoked split action for core: "
operator|+
name|cname
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SolrCore
argument_list|>
name|newCores
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// TODO: allow use of rangesStr in the future
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
literal|null
decl_stmt|;
name|int
name|partitions
init|=
name|pathsArr
operator|!=
literal|null
condition|?
name|pathsArr
operator|.
name|length
else|:
name|newCoreNames
operator|.
name|length
decl_stmt|;
name|DocRouter
name|router
init|=
literal|null
decl_stmt|;
name|String
name|routeFieldName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|ClusterState
name|clusterState
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|String
name|collectionName
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|DocCollection
name|collection
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|String
name|sliceName
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getShardId
argument_list|()
decl_stmt|;
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|sliceName
argument_list|)
decl_stmt|;
name|router
operator|=
name|collection
operator|.
name|getRouter
argument_list|()
operator|!=
literal|null
condition|?
name|collection
operator|.
name|getRouter
argument_list|()
else|:
name|DocRouter
operator|.
name|DEFAULT
expr_stmt|;
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
name|DocRouter
operator|.
name|Range
name|currentRange
init|=
name|slice
operator|.
name|getRange
argument_list|()
decl_stmt|;
name|ranges
operator|=
name|currentRange
operator|!=
literal|null
condition|?
name|router
operator|.
name|partitionRange
argument_list|(
name|partitions
argument_list|,
name|currentRange
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
name|Object
name|routerObj
init|=
name|collection
operator|.
name|get
argument_list|(
name|DOC_ROUTER
argument_list|)
decl_stmt|;
comment|// for back-compat with Solr 4.4
if|if
condition|(
name|routerObj
operator|!=
literal|null
operator|&&
name|routerObj
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|routerProps
init|=
operator|(
name|Map
operator|)
name|routerObj
decl_stmt|;
name|routeFieldName
operator|=
operator|(
name|String
operator|)
name|routerProps
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pathsArr
operator|==
literal|null
condition|)
block|{
name|newCores
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|partitions
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|newCoreName
range|:
name|newCoreNames
control|)
block|{
name|SolrCore
name|newcore
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getCore
argument_list|(
name|newCoreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|newcore
operator|!=
literal|null
condition|)
block|{
name|newCores
operator|.
name|add
argument_list|(
name|newcore
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Core with core name "
operator|+
name|newCoreName
operator|+
literal|" expected but doesn't exist."
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|paths
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|pathsArr
argument_list|)
expr_stmt|;
block|}
name|SplitIndexCommand
name|cmd
init|=
operator|new
name|SplitIndexCommand
argument_list|(
name|req
argument_list|,
name|paths
argument_list|,
name|newCores
argument_list|,
name|ranges
argument_list|,
name|router
argument_list|,
name|routeFieldName
argument_list|,
name|splitKey
argument_list|)
decl_stmt|;
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|split
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
comment|// After the split has completed, someone (here?) should start the process of replaying the buffered updates.
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"ERROR executing split:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|newCores
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SolrCore
name|newCore
range|:
name|newCores
control|)
block|{
name|newCore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

