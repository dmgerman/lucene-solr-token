begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|concurrent
operator|.
name|ExecutionException
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
name|Future
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
name|TimeoutException
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
name|SolrServerException
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
name|impl
operator|.
name|HttpSolrServer
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
name|AbstractUpdateRequest
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
name|CoreAdminRequest
operator|.
name|WaitForState
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
name|SolrException
operator|.
name|ErrorCode
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
name|SafeStopThread
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
name|ZkCoreNodeProps
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
name|CoreDescriptor
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
name|RequestHandlers
operator|.
name|LazyRequestHandlerWrapper
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
name|ReplicationHandler
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
name|request
operator|.
name|SolrRequestHandler
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
name|SolrRequestInfo
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
name|update
operator|.
name|CommitUpdateCommand
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
name|PeerSync
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
name|UpdateLog
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
name|UpdateLog
operator|.
name|RecoveryInfo
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
name|processor
operator|.
name|DistributedUpdateProcessor
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
DECL|class|RecoveryStrategy
specifier|public
class|class
name|RecoveryStrategy
extends|extends
name|Thread
implements|implements
name|SafeStopThread
block|{
DECL|field|MAX_RETRIES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RETRIES
init|=
literal|500
decl_stmt|;
DECL|field|INTERRUPTED
specifier|private
specifier|static
specifier|final
name|int
name|INTERRUPTED
init|=
name|MAX_RETRIES
operator|+
literal|1
decl_stmt|;
DECL|field|START_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|START_TIMEOUT
init|=
literal|100
decl_stmt|;
DECL|field|REPLICATION_HANDLER
specifier|private
specifier|static
specifier|final
name|String
name|REPLICATION_HANDLER
init|=
literal|"/replication"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RecoveryStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|close
specifier|private
specifier|volatile
name|boolean
name|close
init|=
literal|false
decl_stmt|;
DECL|field|zkController
specifier|private
name|ZkController
name|zkController
decl_stmt|;
DECL|field|baseUrl
specifier|private
name|String
name|baseUrl
decl_stmt|;
DECL|field|coreZkNodeName
specifier|private
name|String
name|coreZkNodeName
decl_stmt|;
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|coreName
specifier|private
specifier|volatile
name|String
name|coreName
decl_stmt|;
DECL|field|retries
specifier|private
name|int
name|retries
decl_stmt|;
DECL|field|recoveringAfterStartup
specifier|private
name|boolean
name|recoveringAfterStartup
decl_stmt|;
DECL|field|cc
specifier|private
name|CoreContainer
name|cc
decl_stmt|;
DECL|method|RecoveryStrategy
specifier|public
name|RecoveryStrategy
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|cc
operator|=
name|cc
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|name
expr_stmt|;
name|setName
argument_list|(
literal|"RecoveryThread"
argument_list|)
expr_stmt|;
name|zkController
operator|=
name|cc
operator|.
name|getZkController
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
expr_stmt|;
name|baseUrl
operator|=
name|zkController
operator|.
name|getBaseUrl
argument_list|()
expr_stmt|;
name|coreZkNodeName
operator|=
name|zkController
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"_"
operator|+
name|coreName
expr_stmt|;
block|}
DECL|method|setRecoveringAfterStartup
specifier|public
name|void
name|setRecoveringAfterStartup
parameter_list|(
name|boolean
name|recoveringAfterStartup
parameter_list|)
block|{
name|this
operator|.
name|recoveringAfterStartup
operator|=
name|recoveringAfterStartup
expr_stmt|;
block|}
comment|// make sure any threads stop retrying
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|close
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Stopping recovery for core "
operator|+
name|coreName
operator|+
literal|" zkNodeName="
operator|+
name|coreZkNodeName
argument_list|)
expr_stmt|;
block|}
DECL|method|recoveryFailed
specifier|private
name|void
name|recoveryFailed
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|,
specifier|final
name|ZkController
name|zkController
parameter_list|,
specifier|final
name|String
name|baseUrl
parameter_list|,
specifier|final
name|String
name|shardZkNodeName
parameter_list|,
specifier|final
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Recovery failed - I give up."
argument_list|)
expr_stmt|;
try|try
block|{
name|zkController
operator|.
name|publishAsRecoveryFailed
argument_list|(
name|baseUrl
argument_list|,
name|cd
argument_list|,
name|shardZkNodeName
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|replicate
specifier|private
name|void
name|replicate
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|ZkNodeProps
name|leaderprops
parameter_list|,
name|String
name|baseUrl
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|String
name|leaderBaseUrl
init|=
name|leaderprops
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|leaderCNodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|leaderprops
argument_list|)
decl_stmt|;
name|String
name|leaderUrl
init|=
name|leaderCNodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Attempting to replicate from "
operator|+
name|leaderUrl
argument_list|)
expr_stmt|;
comment|// if we are the leader, either we are trying to recover faster
comment|// then our ephemeral timed out or we are the only node
if|if
condition|(
operator|!
name|leaderBaseUrl
operator|.
name|equals
argument_list|(
name|baseUrl
argument_list|)
condition|)
block|{
comment|// send commit
name|commitOnLeader
argument_list|(
name|leaderUrl
argument_list|)
expr_stmt|;
comment|// use rep handler directly, so we can do this sync rather than async
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|REPLICATION_HANDLER
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|LazyRequestHandlerWrapper
condition|)
block|{
name|handler
operator|=
operator|(
operator|(
name|LazyRequestHandlerWrapper
operator|)
name|handler
operator|)
operator|.
name|getWrappedHandler
argument_list|()
expr_stmt|;
block|}
name|ReplicationHandler
name|replicationHandler
init|=
operator|(
name|ReplicationHandler
operator|)
name|handler
decl_stmt|;
if|if
condition|(
name|replicationHandler
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"Skipping recovery, no "
operator|+
name|REPLICATION_HANDLER
operator|+
literal|" handler found"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|set
argument_list|(
name|ReplicationHandler
operator|.
name|MASTER_URL
argument_list|,
name|leaderUrl
operator|+
literal|"replication"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isClosed
argument_list|()
condition|)
name|retries
operator|=
name|INTERRUPTED
expr_stmt|;
name|boolean
name|success
init|=
name|replicationHandler
operator|.
name|doFetch
argument_list|(
name|solrParams
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// TODO: look into making sure force=true does not download files we already have
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Replication for recovery failed."
argument_list|)
throw|;
block|}
comment|// solrcloud_debug
comment|//      try {
comment|//        RefCounted<SolrIndexSearcher> searchHolder = core.getNewestSearcher(false);
comment|//        SolrIndexSearcher searcher = searchHolder.get();
comment|//        try {
comment|//          System.out.println(core.getCoreDescriptor().getCoreContainer().getZkController().getNodeName() + " replicated "
comment|//              + searcher.search(new MatchAllDocsQuery(), 1).totalHits + " from " + leaderUrl + " gen:" + core.getDeletionPolicy().getLatestCommit().getGeneration() + " data:" + core.getDataDir());
comment|//        } finally {
comment|//          searchHolder.decref();
comment|//        }
comment|//      } catch (Exception e) {
comment|//
comment|//      }
block|}
block|}
DECL|method|commitOnLeader
specifier|private
name|void
name|commitOnLeader
parameter_list|(
name|String
name|leaderUrl
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|SolrServerException
throws|,
name|IOException
block|{
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|leaderUrl
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|UpdateRequest
name|ureq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|getParams
argument_list|()
operator|.
name|set
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|COMMIT_END_POINT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|setAction
argument_list|(
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|.
name|process
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|sendPrepRecoveryCmd
specifier|private
name|void
name|sendPrepRecoveryCmd
parameter_list|(
name|String
name|leaderBaseUrl
parameter_list|,
name|String
name|leaderCoreName
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|SolrServerException
throws|,
name|IOException
block|{
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|leaderBaseUrl
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|45000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|45000
argument_list|)
expr_stmt|;
name|WaitForState
name|prepCmd
init|=
operator|new
name|WaitForState
argument_list|()
decl_stmt|;
name|prepCmd
operator|.
name|setCoreName
argument_list|(
name|leaderCoreName
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setNodeName
argument_list|(
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setCoreNodeName
argument_list|(
name|coreZkNodeName
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setState
argument_list|(
name|ZkStateReader
operator|.
name|RECOVERING
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setCheckLive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setPauseFor
argument_list|(
literal|6000
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|prepCmd
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|cc
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"SolrCore not found - cannot recover:"
operator|+
name|coreName
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// set request info for logging
try|try
block|{
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting recovery process. recoveringAfterStartup="
operator|+
name|recoveringAfterStartup
argument_list|)
expr_stmt|;
name|doRecovery
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|SolrRequestInfo
operator|.
name|clearRequestInfo
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO: perhaps make this grab a new core each time through the loop to handle core reloads?
DECL|method|doRecovery
specifier|public
name|void
name|doRecovery
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|boolean
name|replayed
init|=
literal|false
decl_stmt|;
name|boolean
name|successfulRecovery
init|=
literal|false
decl_stmt|;
name|UpdateLog
name|ulog
decl_stmt|;
name|ulog
operator|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
expr_stmt|;
if|if
condition|(
name|ulog
operator|==
literal|null
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"No UpdateLog found - cannot recover"
argument_list|)
expr_stmt|;
name|recoveryFailed
argument_list|(
name|core
argument_list|,
name|zkController
argument_list|,
name|baseUrl
argument_list|,
name|coreZkNodeName
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|recentVersions
decl_stmt|;
name|UpdateLog
operator|.
name|RecentUpdates
name|recentUpdates
init|=
name|ulog
operator|.
name|getRecentUpdates
argument_list|()
decl_stmt|;
try|try
block|{
name|recentVersions
operator|=
name|recentUpdates
operator|.
name|getVersions
argument_list|(
name|ulog
operator|.
name|numRecordsToKeep
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|recentUpdates
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|startingVersions
init|=
name|ulog
operator|.
name|getStartingVersions
argument_list|()
decl_stmt|;
if|if
condition|(
name|startingVersions
operator|!=
literal|null
operator|&&
name|recoveringAfterStartup
condition|)
block|{
name|int
name|oldIdx
init|=
literal|0
decl_stmt|;
comment|// index of the start of the old list in the current list
name|long
name|firstStartingVersion
init|=
name|startingVersions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|startingVersions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|0
decl_stmt|;
for|for
control|(
init|;
name|oldIdx
operator|<
name|recentVersions
operator|.
name|size
argument_list|()
condition|;
name|oldIdx
operator|++
control|)
block|{
if|if
condition|(
name|recentVersions
operator|.
name|get
argument_list|(
name|oldIdx
argument_list|)
operator|==
name|firstStartingVersion
condition|)
break|break;
block|}
if|if
condition|(
name|oldIdx
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"####### Found new versions added after startup: num="
operator|+
name|oldIdx
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"###### currentVersions="
operator|+
name|recentVersions
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"###### startupVersions="
operator|+
name|startingVersions
argument_list|)
expr_stmt|;
block|}
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|recoveringAfterStartup
condition|)
block|{
comment|// if we're recovering after startup (i.e. we have been down), then we need to know what the last versions were
comment|// when we went down.  We may have received updates since then.
name|recentVersions
operator|=
name|startingVersions
expr_stmt|;
if|if
condition|(
operator|(
name|ulog
operator|.
name|getStartingOperation
argument_list|()
operator|&
name|UpdateLog
operator|.
name|FLAG_GAP
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// last operation at the time of startup had the GAP flag set...
comment|// this means we were previously doing a full index replication
comment|// that probably didn't complete and buffering updates in the meantime.
name|firstTime
operator|=
literal|false
expr_stmt|;
comment|// skip peersync
block|}
block|}
while|while
condition|(
operator|!
name|successfulRecovery
operator|&&
operator|!
name|isClosed
argument_list|()
operator|&&
operator|!
name|isInterrupted
argument_list|()
condition|)
block|{
comment|// don't use interruption or it will close channels though
try|try
block|{
comment|// first thing we just try to sync
name|zkController
operator|.
name|publish
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|RECOVERING
argument_list|)
expr_stmt|;
name|CloudDescriptor
name|cloudDesc
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
name|ZkNodeProps
name|leaderprops
init|=
name|zkStateReader
operator|.
name|getLeaderProps
argument_list|(
name|cloudDesc
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|cloudDesc
operator|.
name|getShardId
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|leaderBaseUrl
init|=
name|leaderprops
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|String
name|leaderCoreName
init|=
name|leaderprops
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|String
name|leaderUrl
init|=
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderBaseUrl
argument_list|,
name|leaderCoreName
argument_list|)
decl_stmt|;
name|sendPrepRecoveryCmd
argument_list|(
name|leaderBaseUrl
argument_list|,
name|leaderCoreName
argument_list|)
expr_stmt|;
comment|// first thing we just try to sync
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
comment|// only try sync the first time through the loop
name|log
operator|.
name|info
argument_list|(
literal|"Attempting to PeerSync from "
operator|+
name|leaderUrl
operator|+
literal|" recoveringAfterStartup="
operator|+
name|recoveringAfterStartup
argument_list|)
expr_stmt|;
comment|// System.out.println("Attempting to PeerSync from " + leaderUrl
comment|// + " i am:" + zkController.getNodeName());
name|PeerSync
name|peerSync
init|=
operator|new
name|PeerSync
argument_list|(
name|core
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|leaderUrl
argument_list|)
argument_list|,
name|ulog
operator|.
name|numRecordsToKeep
argument_list|)
decl_stmt|;
name|peerSync
operator|.
name|setStartingVersions
argument_list|(
name|recentVersions
argument_list|)
expr_stmt|;
name|boolean
name|syncSuccess
init|=
name|peerSync
operator|.
name|sync
argument_list|()
decl_stmt|;
if|if
condition|(
name|syncSuccess
condition|)
block|{
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|commit
argument_list|(
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sync Recovery was successful - registering as Active"
argument_list|)
expr_stmt|;
comment|// System.out
comment|// .println("Sync Recovery was successful - registering as Active "
comment|// + zkController.getNodeName());
comment|// solrcloud_debug
comment|// try {
comment|// RefCounted<SolrIndexSearcher> searchHolder =
comment|// core.getNewestSearcher(false);
comment|// SolrIndexSearcher searcher = searchHolder.get();
comment|// try {
comment|// System.out.println(core.getCoreDescriptor().getCoreContainer().getZkController().getNodeName()
comment|// + " synched "
comment|// + searcher.search(new MatchAllDocsQuery(), 1).totalHits);
comment|// } finally {
comment|// searchHolder.decref();
comment|// }
comment|// } catch (Exception e) {
comment|//
comment|// }
comment|// sync success - register as active and return
name|zkController
operator|.
name|publishAsActive
argument_list|(
name|baseUrl
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|coreZkNodeName
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|successfulRecovery
operator|=
literal|true
expr_stmt|;
name|close
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Sync Recovery was not successful - trying replication"
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("Sync Recovery was not successful - trying replication");
name|log
operator|.
name|info
argument_list|(
literal|"Begin buffering updates"
argument_list|)
expr_stmt|;
name|ulog
operator|.
name|bufferUpdates
argument_list|()
expr_stmt|;
name|replayed
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|replicate
argument_list|(
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|core
argument_list|,
name|leaderprops
argument_list|,
name|leaderUrl
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|ulog
argument_list|)
expr_stmt|;
name|replayed
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Recovery was successful - registering as Active"
argument_list|)
expr_stmt|;
comment|// if there are pending recovery requests, don't advert as active
name|zkController
operator|.
name|publishAsActive
argument_list|(
name|baseUrl
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|coreZkNodeName
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|close
operator|=
literal|true
expr_stmt|;
name|successfulRecovery
operator|=
literal|true
expr_stmt|;
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
name|log
operator|.
name|warn
argument_list|(
literal|"Recovery was interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|retries
operator|=
name|INTERRUPTED
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while trying to recover"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|replayed
condition|)
block|{
try|try
block|{
name|ulog
operator|.
name|dropBufferedUpdates
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|""
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while trying to recover."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|successfulRecovery
condition|)
block|{
comment|// lets pause for a moment and we need to try again...
comment|// TODO: we don't want to retry for some problems?
comment|// Or do a fall off retry...
try|try
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Recovery failed - trying again..."
argument_list|)
expr_stmt|;
name|retries
operator|++
expr_stmt|;
if|if
condition|(
name|retries
operator|>=
name|MAX_RETRIES
condition|)
block|{
if|if
condition|(
name|retries
operator|==
name|INTERRUPTED
condition|)
block|{              }
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Recovery failed - max retries exceeded."
argument_list|)
expr_stmt|;
name|recoveryFailed
argument_list|(
name|core
argument_list|,
name|zkController
argument_list|,
name|baseUrl
argument_list|,
name|coreZkNodeName
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
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
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// if (!isClosed()) Thread.sleep(Math.min(START_TIMEOUT * retries, 60000));
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|Math
operator|.
name|min
argument_list|(
name|retries
argument_list|,
literal|600
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
break|break;
comment|// check if someone closed us
name|Thread
operator|.
name|sleep
argument_list|(
name|START_TIMEOUT
argument_list|)
expr_stmt|;
block|}
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
name|log
operator|.
name|warn
argument_list|(
literal|"Recovery was interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|retries
operator|=
name|INTERRUPTED
expr_stmt|;
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Finished recovery process"
argument_list|)
expr_stmt|;
block|}
DECL|method|replay
specifier|private
name|Future
argument_list|<
name|RecoveryInfo
argument_list|>
name|replay
parameter_list|(
name|UpdateLog
name|ulog
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
name|Future
argument_list|<
name|RecoveryInfo
argument_list|>
name|future
init|=
name|ulog
operator|.
name|applyBufferedUpdates
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|==
literal|null
condition|)
block|{
comment|// no replay needed\
name|log
operator|.
name|info
argument_list|(
literal|"No replay needed"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Replaying buffered documents"
argument_list|)
expr_stmt|;
comment|// wait for replay
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// solrcloud_debug
comment|//    try {
comment|//      RefCounted<SolrIndexSearcher> searchHolder = core.getNewestSearcher(false);
comment|//      SolrIndexSearcher searcher = searchHolder.get();
comment|//      try {
comment|//        System.out.println(core.getCoreDescriptor().getCoreContainer().getZkController().getNodeName() + " replayed "
comment|//            + searcher.search(new MatchAllDocsQuery(), 1).totalHits);
comment|//      } finally {
comment|//        searchHolder.decref();
comment|//      }
comment|//    } catch (Exception e) {
comment|//
comment|//    }
return|return
name|future
return|;
block|}
DECL|method|isClosed
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|close
return|;
block|}
block|}
end_class

end_unit

