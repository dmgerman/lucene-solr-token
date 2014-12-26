begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|CollectionStatistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermStatistics
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
name|ShardParams
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|component
operator|.
name|ShardRequest
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
name|component
operator|.
name|ShardResponse
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
name|search
operator|.
name|FastLRUCache
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
name|search
operator|.
name|SolrCache
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
name|search
operator|.
name|SolrIndexSearcher
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
comment|/**  * Unlike {@link ExactStatsCache} this implementation preserves term stats  * across queries in a set of LRU caches, and based on surface features of a  * query it determines the need to send additional RPC-s. As a result the  * additional RPC-s are needed much less frequently.  *   *<p>  * Query terms and their stats are maintained in a set of maps. At the query  * front-end there will be as many maps as there are shards, each maintaining  * the respective shard statistics. At each shard server there is a single map  * that is updated with the global statistics on every request.  */
end_comment

begin_class
DECL|class|LRUStatsCache
specifier|public
class|class
name|LRUStatsCache
extends|extends
name|ExactStatsCache
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LRUStatsCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// local stats obtained from shard servers
DECL|field|perShardTermStats
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrCache
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
name|perShardTermStats
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|perShardColStats
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
argument_list|>
name|perShardColStats
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// global stats synchronized from the master
DECL|field|currentGlobalTermStats
specifier|private
specifier|final
name|FastLRUCache
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|currentGlobalTermStats
init|=
operator|new
name|FastLRUCache
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|currentGlobalColStats
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|currentGlobalColStats
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// local term context (caching term lookups)
DECL|field|lruCacheInitArgs
specifier|private
specifier|final
name|Map
name|lruCacheInitArgs
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|StatsSource
name|get
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## GET total={}, cache {}"
argument_list|,
name|currentGlobalColStats
argument_list|,
name|currentGlobalTermStats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|LRUStatsSource
argument_list|(
name|currentGlobalTermStats
argument_list|,
name|currentGlobalColStats
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
comment|// TODO: make this configurable via PluginInfo
name|lruCacheInitArgs
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|currentGlobalTermStats
operator|.
name|init
argument_list|(
name|lruCacheInitArgs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addToGlobalTermStats
specifier|protected
name|void
name|addToGlobalTermStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|Entry
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|e
parameter_list|)
block|{
name|currentGlobalTermStats
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addToPerShardColStats
specifier|protected
name|void
name|addToPerShardColStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|shard
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|colStats
parameter_list|)
block|{
name|perShardColStats
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|colStats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPerShardColStats
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|getPerShardColStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
return|return
name|perShardColStats
operator|.
name|get
argument_list|(
name|shard
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addToPerShardTermStats
specifier|protected
name|void
name|addToPerShardTermStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|shard
parameter_list|,
name|String
name|termStatsString
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|termStats
init|=
name|StatsUtil
operator|.
name|termStatsMapFromString
argument_list|(
name|termStatsString
argument_list|)
decl_stmt|;
if|if
condition|(
name|termStats
operator|!=
literal|null
condition|)
block|{
name|SolrCache
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|cache
init|=
name|perShardTermStats
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
comment|// initialize
name|cache
operator|=
operator|new
name|FastLRUCache
argument_list|<>
argument_list|()
expr_stmt|;
name|cache
operator|.
name|init
argument_list|(
name|lruCacheInitArgs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|perShardTermStats
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|e
range|:
name|termStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getPerShardTermStats
specifier|protected
name|TermStats
name|getPerShardTermStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|t
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|SolrCache
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|cache
init|=
name|perShardTermStats
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
return|return
name|cache
operator|.
name|get
argument_list|(
name|t
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addToGlobalColStats
specifier|protected
name|void
name|addToGlobalColStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|Entry
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|e
parameter_list|)
block|{
name|currentGlobalColStats
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|printStats
specifier|protected
name|void
name|printStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## MERGED: perShardColStats={}, perShardTermStats={}"
argument_list|,
name|perShardColStats
argument_list|,
name|perShardTermStats
argument_list|)
expr_stmt|;
block|}
DECL|class|LRUStatsSource
specifier|static
class|class
name|LRUStatsSource
extends|extends
name|StatsSource
block|{
DECL|field|termStatsCache
specifier|private
specifier|final
name|SolrCache
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|termStatsCache
decl_stmt|;
DECL|field|colStatsCache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|colStatsCache
decl_stmt|;
DECL|method|LRUStatsSource
specifier|public
name|LRUStatsSource
parameter_list|(
name|SolrCache
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|termStatsCache
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|colStatsCache
parameter_list|)
block|{
name|this
operator|.
name|termStatsCache
operator|=
name|termStatsCache
expr_stmt|;
name|this
operator|.
name|colStatsCache
operator|=
name|colStatsCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termStatistics
specifier|public
name|TermStatistics
name|termStatistics
parameter_list|(
name|SolrIndexSearcher
name|localSearcher
parameter_list|,
name|Term
name|term
parameter_list|,
name|TermContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|TermStats
name|termStats
init|=
name|termStatsCache
operator|.
name|get
argument_list|(
name|term
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|termStats
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## Missing global termStats info: {}, using local"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|localSearcher
operator|.
name|localTermStatistics
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|termStats
operator|.
name|toTermStatistics
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|collectionStatistics
specifier|public
name|CollectionStatistics
name|collectionStatistics
parameter_list|(
name|SolrIndexSearcher
name|localSearcher
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|CollectionStats
name|colStats
init|=
name|colStatsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|colStats
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## Missing global colStats info: {}, using local"
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|localSearcher
operator|.
name|localCollectionStatistics
argument_list|(
name|field
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|colStats
operator|.
name|toCollectionStatistics
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
