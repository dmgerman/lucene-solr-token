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
name|IndexSearcher
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
name|QueryRequest
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
name|response
operator|.
name|QueryResponse
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
name|handler
operator|.
name|component
operator|.
name|IterativeMergeStrategy
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
name|handler
operator|.
name|component
operator|.
name|MergeStrategy
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
name|Future
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

begin_class
DECL|class|AnalyticsTestQParserPlugin
specifier|public
class|class
name|AnalyticsTestQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|query
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|TestAnalyticsQueryParser
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
DECL|class|TestAnalyticsQueryParser
specifier|static
class|class
name|TestAnalyticsQueryParser
extends|extends
name|QParser
block|{
DECL|method|TestAnalyticsQueryParser
specifier|public
name|TestAnalyticsQueryParser
parameter_list|(
name|String
name|query
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|int
name|base
init|=
name|localParams
operator|.
name|getInt
argument_list|(
literal|"base"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|iterate
init|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"iterate"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|iterate
condition|)
return|return
operator|new
name|TestAnalyticsQuery
argument_list|(
name|base
argument_list|,
operator|new
name|TestIterative
argument_list|()
argument_list|)
return|;
else|else
return|return
operator|new
name|TestAnalyticsQuery
argument_list|(
name|base
argument_list|,
operator|new
name|TestAnalyticsMergeStrategy
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|TestAnalyticsQuery
specifier|static
class|class
name|TestAnalyticsQuery
extends|extends
name|AnalyticsQuery
block|{
DECL|field|base
specifier|private
name|int
name|base
decl_stmt|;
DECL|method|TestAnalyticsQuery
specifier|public
name|TestAnalyticsQuery
parameter_list|(
name|int
name|base
parameter_list|,
name|MergeStrategy
name|mergeStrategy
parameter_list|)
block|{
name|super
argument_list|(
name|mergeStrategy
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
DECL|method|getAnalyticsCollector
specifier|public
name|DelegatingCollector
name|getAnalyticsCollector
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
block|{
return|return
operator|new
name|TestAnalyticsCollector
argument_list|(
name|base
argument_list|,
name|rb
argument_list|)
return|;
block|}
block|}
DECL|class|TestAnalyticsCollector
specifier|static
class|class
name|TestAnalyticsCollector
extends|extends
name|DelegatingCollector
block|{
DECL|field|rb
name|ResponseBuilder
name|rb
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|base
name|int
name|base
decl_stmt|;
DECL|method|TestAnalyticsCollector
specifier|public
name|TestAnalyticsCollector
parameter_list|(
name|int
name|base
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|rb
operator|=
name|rb
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
operator|++
name|count
expr_stmt|;
name|leafDelegate
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|NamedList
name|analytics
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"analytics"
argument_list|,
name|analytics
argument_list|)
expr_stmt|;
name|analytics
operator|.
name|add
argument_list|(
literal|"mycount"
argument_list|,
name|count
operator|+
name|base
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|delegate
operator|instanceof
name|DelegatingCollector
condition|)
block|{
operator|(
operator|(
name|DelegatingCollector
operator|)
name|this
operator|.
name|delegate
operator|)
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|TestAnalyticsMergeStrategy
specifier|static
class|class
name|TestAnalyticsMergeStrategy
implements|implements
name|MergeStrategy
block|{
DECL|method|mergesIds
specifier|public
name|boolean
name|mergesIds
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|handlesMergeFields
specifier|public
name|boolean
name|handlesMergeFields
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
DECL|method|handleMergeFields
specifier|public
name|void
name|handleMergeFields
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{     }
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|shardRequest
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|NamedList
name|merged
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardResponse
name|shardResponse
range|:
name|shardRequest
operator|.
name|responses
control|)
block|{
name|NamedList
name|response
init|=
name|shardResponse
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|NamedList
name|analytics
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"analytics"
argument_list|)
decl_stmt|;
name|Integer
name|c
init|=
operator|(
name|Integer
operator|)
name|analytics
operator|.
name|get
argument_list|(
literal|"mycount"
argument_list|)
decl_stmt|;
name|count
operator|+=
name|c
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|merged
operator|.
name|add
argument_list|(
literal|"mycount"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"analytics"
argument_list|,
name|merged
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestIterative
specifier|static
class|class
name|TestIterative
extends|extends
name|IterativeMergeStrategy
block|{
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardResponse
name|shardResponse
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|NamedList
name|response
init|=
name|shardResponse
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|NamedList
name|analytics
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"analytics"
argument_list|)
decl_stmt|;
name|Integer
name|c
init|=
operator|(
name|Integer
operator|)
name|analytics
operator|.
name|get
argument_list|(
literal|"mycount"
argument_list|)
decl_stmt|;
name|count
operator|+=
name|c
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!count base="
operator|+
name|count
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
comment|/*       *  Call back to all the shards in the response and process the result.        */
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|CallBack
argument_list|>
argument_list|>
name|futures
init|=
name|callBack
argument_list|(
name|sreq
operator|.
name|responses
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|int
name|nextCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|CallBack
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|QueryResponse
name|response
init|=
name|future
operator|.
name|get
argument_list|()
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|NamedList
name|analytics
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"analytics"
argument_list|)
decl_stmt|;
name|Integer
name|c
init|=
operator|(
name|Integer
operator|)
name|analytics
operator|.
name|get
argument_list|(
literal|"mycount"
argument_list|)
decl_stmt|;
name|nextCount
operator|+=
name|c
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|NamedList
name|merged
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|merged
operator|.
name|add
argument_list|(
literal|"mycount"
argument_list|,
name|nextCount
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"analytics"
argument_list|,
name|merged
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
