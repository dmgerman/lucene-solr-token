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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntFloatHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntIntHashMap
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
name|LeafReaderContext
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
name|LeafCollector
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
name|ScoreDoc
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
name|Sort
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
name|TopDocs
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
name|TopFieldCollector
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
name|TopScoreDocCollector
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
name|SolrRequestInfo
import|;
end_import

begin_comment
comment|/* A TopDocsCollector used by reranking queries. */
end_comment

begin_class
DECL|class|ReRankCollector
specifier|public
class|class
name|ReRankCollector
extends|extends
name|TopDocsCollector
block|{
DECL|field|mainCollector
specifier|final
specifier|private
name|TopDocsCollector
name|mainCollector
decl_stmt|;
DECL|field|searcher
specifier|final
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reRankDocs
specifier|final
specifier|private
name|int
name|reRankDocs
decl_stmt|;
DECL|field|length
specifier|final
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|boostedPriority
specifier|final
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
decl_stmt|;
DECL|field|reRankQueryRescorer
specifier|final
specifier|private
name|Rescorer
name|reRankQueryRescorer
decl_stmt|;
DECL|method|ReRankCollector
specifier|public
name|ReRankCollector
parameter_list|(
name|int
name|reRankDocs
parameter_list|,
name|int
name|length
parameter_list|,
name|Rescorer
name|reRankQueryRescorer
parameter_list|,
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|reRankDocs
operator|=
name|reRankDocs
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|boostedPriority
operator|=
name|boostedPriority
expr_stmt|;
name|Sort
name|sort
init|=
name|cmd
operator|.
name|getSort
argument_list|()
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|mainCollector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
name|sort
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|mainCollector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|reRankQueryRescorer
operator|=
name|reRankQueryRescorer
expr_stmt|;
block|}
DECL|method|getTotalHits
specifier|public
name|int
name|getTotalHits
parameter_list|()
block|{
return|return
name|mainCollector
operator|.
name|getTotalHits
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mainCollector
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
try|try
block|{
name|TopDocs
name|mainDocs
init|=
name|mainCollector
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mainDocs
operator|.
name|totalHits
operator|==
literal|0
operator|||
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|mainDocs
return|;
block|}
name|ScoreDoc
index|[]
name|mainScoreDocs
init|=
name|mainDocs
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|reRankScoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|Math
operator|.
name|min
argument_list|(
name|mainScoreDocs
operator|.
name|length
argument_list|,
name|reRankDocs
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|mainDocs
operator|.
name|scoreDocs
operator|=
name|reRankScoreDocs
expr_stmt|;
name|TopDocs
name|rescoredDocs
init|=
name|reRankQueryRescorer
operator|.
name|rescore
argument_list|(
name|searcher
argument_list|,
name|mainDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//Lower howMany to return if we've collected fewer documents.
name|howMany
operator|=
name|Math
operator|.
name|min
argument_list|(
name|howMany
argument_list|,
name|mainScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|boostedPriority
operator|!=
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
name|Map
name|requestContext
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|requestContext
operator|=
name|info
operator|.
name|getReq
argument_list|()
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|IntIntHashMap
name|boostedDocs
init|=
name|QueryElevationComponent
operator|.
name|getBoostDocs
argument_list|(
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
argument_list|,
name|boostedPriority
argument_list|,
name|requestContext
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
operator|new
name|BoostedComp
argument_list|(
name|boostedDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
argument_list|,
name|rescoredDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|howMany
operator|==
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
return|return
name|rescoredDocs
return|;
comment|// Just return the rescoredDocs
block|}
elseif|else
if|if
condition|(
name|howMany
operator|>
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
comment|//We need to return more then we've reRanked, so create the combined page.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//lay down the initial docs
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//overlay the re-ranked docs.
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
else|else
block|{
comment|//We've rescored more then we need to return.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|howMany
argument_list|)
expr_stmt|;
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
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
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|BoostedComp
specifier|public
specifier|static
class|class
name|BoostedComp
implements|implements
name|Comparator
block|{
DECL|field|boostedMap
name|IntFloatHashMap
name|boostedMap
decl_stmt|;
DECL|method|BoostedComp
specifier|public
name|BoostedComp
parameter_list|(
name|IntIntHashMap
name|boostedDocs
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|boostedMap
operator|=
operator|new
name|IntFloatHashMap
argument_list|(
name|boostedDocs
operator|.
name|size
argument_list|()
operator|*
literal|2
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
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|idx
decl_stmt|;
if|if
condition|(
operator|(
name|idx
operator|=
name|boostedDocs
operator|.
name|indexOf
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|boostedMap
operator|.
name|put
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|,
name|maxScore
operator|+
name|boostedDocs
operator|.
name|indexGet
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|ScoreDoc
name|doc1
init|=
operator|(
name|ScoreDoc
operator|)
name|o1
decl_stmt|;
name|ScoreDoc
name|doc2
init|=
operator|(
name|ScoreDoc
operator|)
name|o2
decl_stmt|;
name|float
name|score1
init|=
name|doc1
operator|.
name|score
decl_stmt|;
name|float
name|score2
init|=
name|doc2
operator|.
name|score
decl_stmt|;
name|int
name|idx
decl_stmt|;
if|if
condition|(
operator|(
name|idx
operator|=
name|boostedMap
operator|.
name|indexOf
argument_list|(
name|doc1
operator|.
name|doc
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|score1
operator|=
name|boostedMap
operator|.
name|indexGet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|idx
operator|=
name|boostedMap
operator|.
name|indexOf
argument_list|(
name|doc2
operator|.
name|doc
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|score2
operator|=
name|boostedMap
operator|.
name|indexGet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
name|Float
operator|.
name|compare
argument_list|(
name|score1
argument_list|,
name|score2
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

