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
name|Map
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
name|IndexReader
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
name|lucene
operator|.
name|search
operator|.
name|MatchAllDocsQuery
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
name|QueryRescorer
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
name|Rescorer
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
name|TopDocsCollector
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
name|Weight
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
name|util
operator|.
name|BytesRef
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
name|QueryElevationComponent
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
name|SolrRequestInfo
import|;
end_import

begin_comment
comment|/* * *  Syntax: q=*:*&rq={!rerank reRankQuery=$rqq reRankDocs=300 reRankWeight=3} * */
end_comment

begin_class
DECL|class|ReRankQParserPlugin
specifier|public
class|class
name|ReRankQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"rerank"
decl_stmt|;
DECL|field|defaultQuery
specifier|private
specifier|static
name|Query
name|defaultQuery
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
DECL|field|RERANK_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|RERANK_QUERY
init|=
literal|"reRankQuery"
decl_stmt|;
DECL|field|RERANK_DOCS
specifier|public
specifier|static
specifier|final
name|String
name|RERANK_DOCS
init|=
literal|"reRankDocs"
decl_stmt|;
DECL|field|RERANK_DOCS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|RERANK_DOCS_DEFAULT
init|=
literal|200
decl_stmt|;
DECL|field|RERANK_WEIGHT
specifier|public
specifier|static
specifier|final
name|String
name|RERANK_WEIGHT
init|=
literal|"reRankWeight"
decl_stmt|;
DECL|field|RERANK_WEIGHT_DEFAULT
specifier|public
specifier|static
specifier|final
name|double
name|RERANK_WEIGHT_DEFAULT
init|=
literal|2.0d
decl_stmt|;
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
name|ReRankQParser
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
DECL|class|ReRankQParser
specifier|private
class|class
name|ReRankQParser
extends|extends
name|QParser
block|{
DECL|method|ReRankQParser
specifier|public
name|ReRankQParser
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
name|String
name|reRankQueryString
init|=
name|localParams
operator|.
name|get
argument_list|(
name|RERANK_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|reRankQueryString
operator|==
literal|null
operator|||
name|reRankQueryString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
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
name|RERANK_QUERY
operator|+
literal|" parameter is mandatory"
argument_list|)
throw|;
block|}
name|QParser
name|reRankParser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|reRankQueryString
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|reRankQuery
init|=
name|reRankParser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|int
name|reRankDocs
init|=
name|localParams
operator|.
name|getInt
argument_list|(
name|RERANK_DOCS
argument_list|,
name|RERANK_DOCS_DEFAULT
argument_list|)
decl_stmt|;
name|reRankDocs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|reRankDocs
argument_list|)
expr_stmt|;
comment|//
name|double
name|reRankWeight
init|=
name|localParams
operator|.
name|getDouble
argument_list|(
name|RERANK_WEIGHT
argument_list|,
name|RERANK_WEIGHT_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReRankQuery
argument_list|(
name|reRankQuery
argument_list|,
name|reRankDocs
argument_list|,
name|reRankWeight
argument_list|)
return|;
block|}
block|}
DECL|class|ReRankQueryRescorer
specifier|private
specifier|final
class|class
name|ReRankQueryRescorer
extends|extends
name|QueryRescorer
block|{
DECL|field|reRankWeight
specifier|final
name|double
name|reRankWeight
decl_stmt|;
DECL|method|ReRankQueryRescorer
specifier|public
name|ReRankQueryRescorer
parameter_list|(
name|Query
name|reRankQuery
parameter_list|,
name|double
name|reRankWeight
parameter_list|)
block|{
name|super
argument_list|(
name|reRankQuery
argument_list|)
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|combine
specifier|protected
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
block|{
name|float
name|score
init|=
name|firstPassScore
decl_stmt|;
if|if
condition|(
name|secondPassMatches
condition|)
block|{
name|score
operator|+=
name|reRankWeight
operator|*
name|secondPassScore
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
DECL|class|ReRankQuery
specifier|private
specifier|final
class|class
name|ReRankQuery
extends|extends
name|RankQuery
block|{
DECL|field|mainQuery
specifier|private
name|Query
name|mainQuery
init|=
name|defaultQuery
decl_stmt|;
DECL|field|reRankQuery
specifier|final
specifier|private
name|Query
name|reRankQuery
decl_stmt|;
DECL|field|reRankDocs
specifier|final
specifier|private
name|int
name|reRankDocs
decl_stmt|;
DECL|field|reRankWeight
specifier|final
specifier|private
name|double
name|reRankWeight
decl_stmt|;
DECL|field|reRankQueryRescorer
specifier|final
specifier|private
name|Rescorer
name|reRankQueryRescorer
decl_stmt|;
DECL|field|boostedPriority
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
decl_stmt|;
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|classHash
argument_list|()
operator|+
name|mainQuery
operator|.
name|hashCode
argument_list|()
operator|+
name|reRankQuery
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|int
operator|)
name|reRankWeight
operator|+
name|reRankDocs
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|ReRankQuery
name|rrq
parameter_list|)
block|{
return|return
name|mainQuery
operator|.
name|equals
argument_list|(
name|rrq
operator|.
name|mainQuery
argument_list|)
operator|&&
name|reRankQuery
operator|.
name|equals
argument_list|(
name|rrq
operator|.
name|reRankQuery
argument_list|)
operator|&&
name|reRankWeight
operator|==
name|rrq
operator|.
name|reRankWeight
operator|&&
name|reRankDocs
operator|==
name|rrq
operator|.
name|reRankDocs
return|;
block|}
DECL|method|ReRankQuery
specifier|public
name|ReRankQuery
parameter_list|(
name|Query
name|reRankQuery
parameter_list|,
name|int
name|reRankDocs
parameter_list|,
name|double
name|reRankWeight
parameter_list|)
block|{
name|this
operator|.
name|reRankQuery
operator|=
name|reRankQuery
expr_stmt|;
name|this
operator|.
name|reRankDocs
operator|=
name|reRankDocs
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
name|this
operator|.
name|reRankQueryRescorer
operator|=
operator|new
name|ReRankQueryRescorer
argument_list|(
name|reRankQuery
argument_list|,
name|reRankWeight
argument_list|)
expr_stmt|;
block|}
DECL|method|wrap
specifier|public
name|RankQuery
name|wrap
parameter_list|(
name|Query
name|_mainQuery
parameter_list|)
block|{
if|if
condition|(
name|_mainQuery
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|mainQuery
operator|=
name|_mainQuery
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getMergeStrategy
specifier|public
name|MergeStrategy
name|getMergeStrategy
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getTopDocsCollector
specifier|public
name|TopDocsCollector
name|getTopDocsCollector
parameter_list|(
name|int
name|len
parameter_list|,
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|boostedPriority
operator|==
literal|null
condition|)
block|{
name|SolrRequestInfo
name|info
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|Map
name|context
init|=
name|info
operator|.
name|getReq
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|this
operator|.
name|boostedPriority
operator|=
operator|(
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
operator|)
name|context
operator|.
name|get
argument_list|(
name|QueryElevationComponent
operator|.
name|BOOSTED_PRIORITY
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ReRankCollector
argument_list|(
name|reRankDocs
argument_list|,
name|len
argument_list|,
name|reRankQueryRescorer
argument_list|,
name|cmd
argument_list|,
name|searcher
argument_list|,
name|boostedPriority
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// default initialCapacity of 16 won't be enough
name|sb
operator|.
name|append
argument_list|(
literal|"{!"
argument_list|)
operator|.
name|append
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" mainQuery='"
argument_list|)
operator|.
name|append
argument_list|(
name|mainQuery
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RERANK_QUERY
argument_list|)
operator|.
name|append
argument_list|(
literal|"='"
argument_list|)
operator|.
name|append
argument_list|(
name|reRankQuery
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RERANK_DOCS
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|reRankDocs
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RERANK_WEIGHT
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|reRankWeight
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
name|mainQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
name|mainQuery
condition|)
block|{
return|return
operator|new
name|ReRankQuery
argument_list|(
name|reRankQuery
argument_list|,
name|reRankDocs
argument_list|,
name|reRankWeight
argument_list|)
operator|.
name|wrap
argument_list|(
name|q
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|mainWeight
init|=
name|mainQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReRankWeight
argument_list|(
name|mainQuery
argument_list|,
name|reRankQueryRescorer
argument_list|,
name|searcher
argument_list|,
name|mainWeight
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

