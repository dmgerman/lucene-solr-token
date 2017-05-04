begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|ArrayList
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
name|Collector
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
name|DocIdSet
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
name|FieldDoc
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
name|MultiCollector
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
name|Scorer
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
name|SimpleCollector
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
name|TopFieldDocs
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
name|search
operator|.
name|TotalHitCountCollector
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
name|ArrayUtil
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
name|DocIdSetBuilder
import|;
end_import

begin_comment
comment|/** Collects hits for subsequent faceting.  Once you've run  *  a search and collect hits into this, instantiate one of  *  the {@link Facets} subclasses to do the facet  *  counting.  Use the {@code search} utility methods to  *  perform an "ordinary" search but also collect into a  *  {@link Collector}. */
end_comment

begin_comment
comment|// redundant 'implements Collector' to workaround javadocs bugs
end_comment

begin_class
DECL|class|FacetsCollector
specifier|public
class|class
name|FacetsCollector
extends|extends
name|SimpleCollector
implements|implements
name|Collector
block|{
DECL|field|context
specifier|private
name|LeafReaderContext
name|context
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|totalHits
specifier|private
name|int
name|totalHits
decl_stmt|;
DECL|field|scores
specifier|private
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|keepScores
specifier|private
specifier|final
name|boolean
name|keepScores
decl_stmt|;
DECL|field|matchingDocs
specifier|private
specifier|final
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|docsBuilder
specifier|private
name|DocIdSetBuilder
name|docsBuilder
decl_stmt|;
comment|/**    * Holds the documents that were matched in the {@link org.apache.lucene.index.LeafReaderContext}.    * If scores were required, then {@code scores} is not null.    */
DECL|class|MatchingDocs
specifier|public
specifier|final
specifier|static
class|class
name|MatchingDocs
block|{
comment|/** Context for this segment. */
DECL|field|context
specifier|public
specifier|final
name|LeafReaderContext
name|context
decl_stmt|;
comment|/** Which documents were seen. */
DECL|field|bits
specifier|public
specifier|final
name|DocIdSet
name|bits
decl_stmt|;
comment|/** Non-sparse scores array. */
DECL|field|scores
specifier|public
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
comment|/** Total number of hits */
DECL|field|totalHits
specifier|public
specifier|final
name|int
name|totalHits
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MatchingDocs
specifier|public
name|MatchingDocs
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|DocIdSet
name|bits
parameter_list|,
name|int
name|totalHits
parameter_list|,
name|float
index|[]
name|scores
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
block|}
block|}
comment|/** Default constructor */
DECL|method|FacetsCollector
specifier|public
name|FacetsCollector
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Create this; if {@code keepScores} is true then a    *  float[] is allocated to hold score of all hits. */
DECL|method|FacetsCollector
specifier|public
name|FacetsCollector
parameter_list|(
name|boolean
name|keepScores
parameter_list|)
block|{
name|this
operator|.
name|keepScores
operator|=
name|keepScores
expr_stmt|;
block|}
comment|/** True if scores were saved. */
DECL|method|getKeepScores
specifier|public
specifier|final
name|boolean
name|getKeepScores
parameter_list|()
block|{
return|return
name|keepScores
return|;
block|}
comment|/**    * Returns the documents matched by the query, one {@link MatchingDocs} per    * visited segment.    */
DECL|method|getMatchingDocs
specifier|public
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|getMatchingDocs
parameter_list|()
block|{
if|if
condition|(
name|docsBuilder
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|docsBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
name|docsBuilder
operator|=
literal|null
expr_stmt|;
name|scores
operator|=
literal|null
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|matchingDocs
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|docsBuilder
operator|.
name|grow
argument_list|(
literal|1
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|keepScores
condition|)
block|{
if|if
condition|(
name|totalHits
operator|>=
name|scores
operator|.
name|length
condition|)
block|{
name|float
index|[]
name|newScores
init|=
operator|new
name|float
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|totalHits
operator|+
literal|1
argument_list|,
literal|4
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scores
argument_list|,
literal|0
argument_list|,
name|newScores
argument_list|,
literal|0
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
name|scores
operator|=
name|newScores
expr_stmt|;
block|}
name|scores
index|[
name|totalHits
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
name|totalHits
operator|++
expr_stmt|;
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
annotation|@
name|Override
DECL|method|setScorer
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docsBuilder
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|docsBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docsBuilder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|totalHits
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|keepScores
condition|)
block|{
name|scores
operator|=
operator|new
name|float
index|[
literal|64
index|]
expr_stmt|;
comment|// some initial size
block|}
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/** Utility method, to search and also collect all hits    *  into the provided {@link Collector}. */
DECL|method|search
specifier|public
specifier|static
name|TopDocs
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doSearch
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|,
name|q
argument_list|,
name|n
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|fc
argument_list|)
return|;
block|}
comment|/** Utility method, to search and also collect all hits    *  into the provided {@link Collector}. */
DECL|method|search
specifier|public
specifier|static
name|TopFieldDocs
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sort must not be null"
argument_list|)
throw|;
block|}
return|return
operator|(
name|TopFieldDocs
operator|)
name|doSearch
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|,
name|q
argument_list|,
name|n
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|fc
argument_list|)
return|;
block|}
comment|/** Utility method, to search and also collect all hits    *  into the provided {@link Collector}. */
DECL|method|search
specifier|public
specifier|static
name|TopFieldDocs
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|doDocScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sort must not be null"
argument_list|)
throw|;
block|}
return|return
operator|(
name|TopFieldDocs
operator|)
name|doSearch
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|,
name|q
argument_list|,
name|n
argument_list|,
name|sort
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|,
name|fc
argument_list|)
return|;
block|}
comment|/** Utility method, to search and also collect all hits    *  into the provided {@link Collector}. */
DECL|method|searchAfter
specifier|public
specifier|static
name|TopDocs
name|searchAfter
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|ScoreDoc
name|after
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doSearch
argument_list|(
name|searcher
argument_list|,
name|after
argument_list|,
name|q
argument_list|,
name|n
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|fc
argument_list|)
return|;
block|}
comment|/** Utility method, to search and also collect all hits    *  into the provided {@link Collector}. */
DECL|method|searchAfter
specifier|public
specifier|static
name|TopDocs
name|searchAfter
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|ScoreDoc
name|after
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sort must not be null"
argument_list|)
throw|;
block|}
return|return
name|doSearch
argument_list|(
name|searcher
argument_list|,
name|after
argument_list|,
name|q
argument_list|,
name|n
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|fc
argument_list|)
return|;
block|}
comment|/** Utility method, to search and also collect all hits    *  into the provided {@link Collector}. */
DECL|method|searchAfter
specifier|public
specifier|static
name|TopDocs
name|searchAfter
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|ScoreDoc
name|after
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|doDocScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sort must not be null"
argument_list|)
throw|;
block|}
return|return
name|doSearch
argument_list|(
name|searcher
argument_list|,
name|after
argument_list|,
name|q
argument_list|,
name|n
argument_list|,
name|sort
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|,
name|fc
argument_list|)
return|;
block|}
DECL|method|doSearch
specifier|private
specifier|static
name|TopDocs
name|doSearch
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|ScoreDoc
name|after
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|doDocScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|,
name|Collector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|limit
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|limit
operator|==
literal|0
condition|)
block|{
name|limit
operator|=
literal|1
expr_stmt|;
block|}
name|n
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|limit
argument_list|)
expr_stmt|;
if|if
condition|(
name|after
operator|!=
literal|null
operator|&&
name|after
operator|.
name|doc
operator|>=
name|limit
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"after.doc exceeds the number of documents in the reader: after.doc="
operator|+
name|after
operator|.
name|doc
operator|+
literal|" limit="
operator|+
name|limit
argument_list|)
throw|;
block|}
name|TopDocs
name|topDocs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|TotalHitCountCollector
name|totalHitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|totalHitCountCollector
argument_list|,
name|fc
argument_list|)
argument_list|)
expr_stmt|;
name|topDocs
operator|=
operator|new
name|TopDocs
argument_list|(
name|totalHitCountCollector
operator|.
name|getTotalHits
argument_list|()
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TopDocsCollector
argument_list|<
name|?
argument_list|>
name|hitsCollector
decl_stmt|;
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|after
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|after
operator|instanceof
name|FieldDoc
operator|)
condition|)
block|{
comment|// TODO: if we fix type safety of TopFieldDocs we can
comment|// remove this
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"after must be a FieldDoc; got "
operator|+
name|after
argument_list|)
throw|;
block|}
name|boolean
name|fillFields
init|=
literal|true
decl_stmt|;
name|hitsCollector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|n
argument_list|,
operator|(
name|FieldDoc
operator|)
name|after
argument_list|,
name|fillFields
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hitsCollector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|n
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|hitsCollector
argument_list|,
name|fc
argument_list|)
argument_list|)
expr_stmt|;
name|topDocs
operator|=
name|hitsCollector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
block|}
return|return
name|topDocs
return|;
block|}
block|}
end_class

end_unit

