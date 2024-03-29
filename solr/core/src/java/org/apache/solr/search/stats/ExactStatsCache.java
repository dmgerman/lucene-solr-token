begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|IndexReaderContext
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
name|Query
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
name|client
operator|.
name|solrj
operator|.
name|SolrResponse
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
name|Set
import|;
end_import

begin_comment
comment|/**  * This class implements exact caching of statistics. It requires an additional  * round-trip to parse query at shard servers, and return term statistics for  * query terms (and collection statistics for term fields).  */
end_comment

begin_class
DECL|class|ExactStatsCache
specifier|public
class|class
name|ExactStatsCache
extends|extends
name|StatsCache
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|// experimenting with strategy that takes more RAM, but also doesn't share memory
comment|// across threads
DECL|field|CURRENT_GLOBAL_COL_STATS
specifier|private
specifier|static
specifier|final
name|String
name|CURRENT_GLOBAL_COL_STATS
init|=
literal|"org.apache.solr.stats.currentGlobalColStats"
decl_stmt|;
DECL|field|CURRENT_GLOBAL_TERM_STATS
specifier|private
specifier|static
specifier|final
name|String
name|CURRENT_GLOBAL_TERM_STATS
init|=
literal|"org.apache.solr.stats.currentGlobalTermStats"
decl_stmt|;
DECL|field|PER_SHARD_TERM_STATS
specifier|private
specifier|static
specifier|final
name|String
name|PER_SHARD_TERM_STATS
init|=
literal|"org.apache.solr.stats.perShardTermStats"
decl_stmt|;
DECL|field|PER_SHARD_COL_STATS
specifier|private
specifier|static
specifier|final
name|String
name|PER_SHARD_COL_STATS
init|=
literal|"org.apache.solr.stats.perShardColStats"
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
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|currentGlobalColStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|CURRENT_GLOBAL_COL_STATS
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|currentGlobalTermStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|CURRENT_GLOBAL_TERM_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentGlobalColStats
operator|==
literal|null
condition|)
block|{
name|currentGlobalColStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|currentGlobalTermStats
operator|==
literal|null
condition|)
block|{
name|currentGlobalTermStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning StatsSource. Collection stats={}, Term stats size= {}"
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
name|ExactStatsSource
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
block|{}
annotation|@
name|Override
DECL|method|retrieveStatsRequest
specifier|public
name|ShardRequest
name|retrieveStatsRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_TERM_STATS
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
comment|// don't pass through any shards param
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
return|return
name|sreq
return|;
block|}
annotation|@
name|Override
DECL|method|mergeToGlobalStats
specifier|public
name|void
name|mergeToGlobalStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|List
argument_list|<
name|ShardResponse
argument_list|>
name|responses
parameter_list|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|allTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardResponse
name|r
range|:
name|responses
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Merging to global stats, shard={}, response={}"
argument_list|,
name|r
operator|.
name|getShard
argument_list|()
argument_list|,
name|r
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|shard
init|=
name|r
operator|.
name|getShard
argument_list|()
decl_stmt|;
name|SolrResponse
name|res
init|=
name|r
operator|.
name|getSolrResponse
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
name|res
operator|.
name|getResponse
argument_list|()
decl_stmt|;
comment|// TODO: nl == null if not all shards respond (no server hosting shard)
name|String
name|termStatsString
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|get
argument_list|(
name|TERM_STATS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|termStatsString
operator|!=
literal|null
condition|)
block|{
name|addToPerShardTermStats
argument_list|(
name|req
argument_list|,
name|shard
argument_list|,
name|termStatsString
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Object
argument_list|>
name|terms
init|=
name|nl
operator|.
name|getAll
argument_list|(
name|TERMS_KEY
argument_list|)
decl_stmt|;
name|allTerms
operator|.
name|addAll
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|String
name|colStatsString
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|get
argument_list|(
name|COL_STATS_KEY
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|colStats
init|=
name|StatsUtil
operator|.
name|colStatsMapFromString
argument_list|(
name|colStatsString
argument_list|)
decl_stmt|;
if|if
condition|(
name|colStats
operator|!=
literal|null
condition|)
block|{
name|addToPerShardColStats
argument_list|(
name|req
argument_list|,
name|shard
argument_list|,
name|colStats
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|allTerms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|TERMS_KEY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|allTerms
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|printStats
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
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
operator|(
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
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|PER_SHARD_COL_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|perShardColStats
operator|==
literal|null
condition|)
block|{
name|perShardColStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|PER_SHARD_COL_STATS
argument_list|,
name|perShardColStats
argument_list|)
expr_stmt|;
block|}
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
DECL|method|printStats
specifier|protected
name|void
name|printStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
name|perShardTermStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|PER_SHARD_TERM_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|perShardTermStats
operator|==
literal|null
condition|)
block|{
name|perShardTermStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
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
operator|(
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
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|PER_SHARD_COL_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|perShardColStats
operator|==
literal|null
condition|)
block|{
name|perShardColStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"perShardColStats={}, perShardTermStats={}"
argument_list|,
name|perShardColStats
argument_list|,
name|perShardTermStats
argument_list|)
expr_stmt|;
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
name|perShardTermStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|PER_SHARD_TERM_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|perShardTermStats
operator|==
literal|null
condition|)
block|{
name|perShardTermStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|PER_SHARD_TERM_STATS
argument_list|,
name|perShardTermStats
argument_list|)
expr_stmt|;
block|}
name|perShardTermStats
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|termStats
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|returnLocalStats
specifier|public
name|void
name|returnLocalStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|Query
name|q
init|=
name|rb
operator|.
name|getQuery
argument_list|()
decl_stmt|;
try|try
block|{
name|HashSet
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|q
argument_list|,
literal|true
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|IndexReaderContext
name|context
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|statsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|colMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|terms
control|)
block|{
name|TermContext
name|termContext
init|=
name|TermContext
operator|.
name|build
argument_list|(
name|context
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|TermStatistics
name|tst
init|=
name|searcher
operator|.
name|localTermStatistics
argument_list|(
name|t
argument_list|,
name|termContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|tst
operator|.
name|docFreq
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// skip terms that are not present here
continue|continue;
block|}
name|statsMap
operator|.
name|put
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|TermStats
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|tst
argument_list|)
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
name|TERMS_KEY
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|colMap
operator|.
name|containsKey
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
comment|// collection stats for this field
name|colMap
operator|.
name|put
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|CollectionStats
argument_list|(
name|searcher
operator|.
name|localCollectionStatistics
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|statsMap
operator|.
name|size
argument_list|()
operator|!=
literal|0
operator|&&
name|colMap
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|//Don't add empty keys
name|String
name|termStatsString
init|=
name|StatsUtil
operator|.
name|termStatsMapToString
argument_list|(
name|statsMap
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
name|TERM_STATS_KEY
argument_list|,
name|termStatsString
argument_list|)
expr_stmt|;
name|String
name|colStatsString
init|=
name|StatsUtil
operator|.
name|colStatsMapToString
argument_list|(
name|colMap
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
name|COL_STATS_KEY
argument_list|,
name|colStatsString
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"termStats="
operator|+
name|termStatsString
operator|+
literal|", collectionStats="
operator|+
name|colStatsString
operator|+
literal|", terms="
operator|+
name|terms
operator|+
literal|", numDocs="
operator|+
name|searcher
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error collecting local stats, query='"
operator|+
name|q
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error collecting local stats."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|sendGlobalStats
specifier|public
name|void
name|sendGlobalStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|outgoing
parameter_list|)
block|{
name|outgoing
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_SET_TERM_STATS
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
name|outgoing
operator|.
name|params
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|TERMS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|t
range|:
name|terms
control|)
block|{
name|String
index|[]
name|fv
init|=
name|t
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|fv
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|globalTermStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|globalColStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// aggregate collection stats, only for the field in terms
for|for
control|(
name|String
name|shard
range|:
name|rb
operator|.
name|shards
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|s
init|=
name|getPerShardColStats
argument_list|(
name|rb
argument_list|,
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|e
range|:
name|s
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|fields
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// skip non-relevant fields
continue|continue;
block|}
name|CollectionStats
name|g
init|=
name|globalColStats
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|==
literal|null
condition|)
block|{
name|g
operator|=
operator|new
name|CollectionStats
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|globalColStats
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|g
argument_list|)
expr_stmt|;
block|}
name|g
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|params
operator|.
name|add
argument_list|(
name|COL_STATS_KEY
argument_list|,
name|StatsUtil
operator|.
name|colStatsMapToString
argument_list|(
name|globalColStats
argument_list|)
argument_list|)
expr_stmt|;
comment|// sum up only from relevant shards
for|for
control|(
name|String
name|t
range|:
name|terms
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|TERMS_KEY
argument_list|,
name|t
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|shard
range|:
name|rb
operator|.
name|shards
control|)
block|{
name|TermStats
name|termStats
init|=
name|getPerShardTermStats
argument_list|(
name|rb
operator|.
name|req
argument_list|,
name|t
argument_list|,
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|termStats
operator|==
literal|null
operator|||
name|termStats
operator|.
name|docFreq
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|TermStats
name|g
init|=
name|globalTermStats
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|==
literal|null
condition|)
block|{
name|g
operator|=
operator|new
name|TermStats
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|globalTermStats
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|g
argument_list|)
expr_stmt|;
block|}
name|g
operator|.
name|add
argument_list|(
name|termStats
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"terms={}, termStats={}"
argument_list|,
name|terms
argument_list|,
name|globalTermStats
argument_list|)
expr_stmt|;
comment|// need global TermStats here...
name|params
operator|.
name|add
argument_list|(
name|TERM_STATS_KEY
argument_list|,
name|StatsUtil
operator|.
name|termStatsMapToString
argument_list|(
name|globalTermStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
operator|(
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
operator|)
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|PER_SHARD_COL_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|perShardColStats
operator|==
literal|null
condition|)
block|{
name|perShardColStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
return|return
name|perShardColStats
operator|.
name|get
argument_list|(
name|shard
argument_list|)
return|;
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
name|perShardTermStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|PER_SHARD_TERM_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|perShardTermStats
operator|==
literal|null
condition|)
block|{
name|perShardTermStats
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|Map
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
operator|(
name|cache
operator|!=
literal|null
operator|)
condition|?
name|cache
operator|.
name|get
argument_list|(
name|t
argument_list|)
else|:
literal|null
return|;
comment|//Term doesn't exist in shard
block|}
annotation|@
name|Override
DECL|method|receiveGlobalStats
specifier|public
name|void
name|receiveGlobalStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
name|globalTermStats
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|TERM_STATS_KEY
argument_list|)
decl_stmt|;
name|String
name|globalColStats
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|COL_STATS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|globalColStats
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|colStats
init|=
name|StatsUtil
operator|.
name|colStatsMapFromString
argument_list|(
name|globalColStats
argument_list|)
decl_stmt|;
if|if
condition|(
name|colStats
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|e
range|:
name|colStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|addToGlobalColStats
argument_list|(
name|req
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Global collection stats={}"
argument_list|,
name|globalColStats
argument_list|)
expr_stmt|;
if|if
condition|(
name|globalTermStats
operator|==
literal|null
condition|)
return|return;
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
name|globalTermStats
argument_list|)
decl_stmt|;
if|if
condition|(
name|termStats
operator|!=
literal|null
condition|)
block|{
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
name|addToGlobalTermStats
argument_list|(
name|req
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
name|currentGlobalColStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionStats
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|CURRENT_GLOBAL_COL_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentGlobalColStats
operator|==
literal|null
condition|)
block|{
name|currentGlobalColStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|CURRENT_GLOBAL_COL_STATS
argument_list|,
name|currentGlobalColStats
argument_list|)
expr_stmt|;
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
name|currentGlobalTermStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|TermStats
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|CURRENT_GLOBAL_TERM_STATS
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentGlobalTermStats
operator|==
literal|null
condition|)
block|{
name|currentGlobalTermStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|CURRENT_GLOBAL_TERM_STATS
argument_list|,
name|currentGlobalTermStats
argument_list|)
expr_stmt|;
block|}
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
DECL|class|ExactStatsSource
specifier|protected
specifier|static
class|class
name|ExactStatsSource
extends|extends
name|StatsSource
block|{
DECL|field|termStatsCache
specifier|private
specifier|final
name|Map
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
DECL|method|ExactStatsSource
specifier|public
name|ExactStatsSource
parameter_list|(
name|Map
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
comment|// TermStats == null is also true if term has no docFreq anyway,
comment|// see returnLocalStats, if docFreq == 0, they are not added anyway
comment|// Not sure we need a warning here
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
literal|"Missing global termStats info for term={}, using local stats"
argument_list|,
name|term
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
literal|"Missing global colStats info for field={}, using local"
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

