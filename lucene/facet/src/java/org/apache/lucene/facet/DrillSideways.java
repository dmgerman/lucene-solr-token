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
name|Collection
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
name|concurrent
operator|.
name|Callable
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
name|ExecutorService
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesFacetCounts
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
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesFacetField
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
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesReaderState
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
name|facet
operator|.
name|taxonomy
operator|.
name|FastTaxonomyFacetCounts
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|CollectorManager
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
name|FilterCollector
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
name|MultiCollectorManager
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
name|util
operator|.
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/**  * Computes drill down and sideways counts for the provided  * {@link DrillDownQuery}.  Drill sideways counts include  * alternative values/aggregates for the drill-down  * dimensions so that a dimension does not disappear after  * the user drills down into it.  *<p> Use one of the static search  * methods to do the search, and then get the hits and facet  * results from the returned {@link DrillSidewaysResult}.  *<p><b>NOTE</b>: this allocates one {@link  * FacetsCollector} for each drill-down, plus one.  If your  * index has high number of facet labels then this will  * multiply your memory usage.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DrillSideways
specifier|public
class|class
name|DrillSideways
block|{
comment|/**    * {@link IndexSearcher} passed to constructor.    */
DECL|field|searcher
specifier|protected
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/**    * {@link TaxonomyReader} passed to constructor.    */
DECL|field|taxoReader
specifier|protected
specifier|final
name|TaxonomyReader
name|taxoReader
decl_stmt|;
comment|/**    * {@link SortedSetDocValuesReaderState} passed to    * constructor; can be null.    */
DECL|field|state
specifier|protected
specifier|final
name|SortedSetDocValuesReaderState
name|state
decl_stmt|;
comment|/**    * {@link FacetsConfig} passed to constructor.    */
DECL|field|config
specifier|protected
specifier|final
name|FacetsConfig
name|config
decl_stmt|;
comment|// These are only used for multi-threaded search
DECL|field|executor
specifier|private
specifier|final
name|ExecutorService
name|executor
decl_stmt|;
comment|/**    * Create a new {@code DrillSideways} instance.    */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|)
block|{
name|this
argument_list|(
name|searcher
argument_list|,
name|config
argument_list|,
name|taxoReader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new {@code DrillSideways} instance, assuming the categories were    * indexed with {@link SortedSetDocValuesFacetField}.    */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|SortedSetDocValuesReaderState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|searcher
argument_list|,
name|config
argument_list|,
literal|null
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new {@code DrillSideways} instance, where some    * dimensions were indexed with {@link    * SortedSetDocValuesFacetField} and others were indexed    * with {@link FacetField}.    */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|SortedSetDocValuesReaderState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|searcher
argument_list|,
name|config
argument_list|,
name|taxoReader
argument_list|,
name|state
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new {@code DrillSideways} instance, where some    * dimensions were indexed with {@link    * SortedSetDocValuesFacetField} and others were indexed    * with {@link FacetField}.    *<p>    * Use this constructor to use the concurrent implementation and/or the CollectorManager    */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|SortedSetDocValuesReaderState
name|state
parameter_list|,
name|ExecutorService
name|executor
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|taxoReader
operator|=
name|taxoReader
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
comment|/**    * Subclass can override to customize per-dim Facets    * impl.    */
DECL|method|buildFacetsResult
specifier|protected
name|Facets
name|buildFacetsResult
parameter_list|(
name|FacetsCollector
name|drillDowns
parameter_list|,
name|FacetsCollector
index|[]
name|drillSideways
parameter_list|,
name|String
index|[]
name|drillSidewaysDims
parameter_list|)
throws|throws
name|IOException
block|{
name|Facets
name|drillDownFacets
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|drillSidewaysFacets
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|taxoReader
operator|!=
literal|null
condition|)
block|{
name|drillDownFacets
operator|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|drillDowns
argument_list|)
expr_stmt|;
if|if
condition|(
name|drillSideways
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|drillSideways
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|drillSidewaysFacets
operator|.
name|put
argument_list|(
name|drillSidewaysDims
index|[
name|i
index|]
argument_list|,
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|drillSideways
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|drillDownFacets
operator|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|drillDowns
argument_list|)
expr_stmt|;
if|if
condition|(
name|drillSideways
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|drillSideways
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|drillSidewaysFacets
operator|.
name|put
argument_list|(
name|drillSidewaysDims
index|[
name|i
index|]
argument_list|,
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|drillSideways
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|drillSidewaysFacets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|drillDownFacets
return|;
block|}
else|else
block|{
return|return
operator|new
name|MultiFacets
argument_list|(
name|drillSidewaysFacets
argument_list|,
name|drillDownFacets
argument_list|)
return|;
block|}
block|}
comment|/**    * Search, collecting hits with a {@link Collector}, and    * computing drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|Collector
name|hitCollector
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
name|query
operator|.
name|getDims
argument_list|()
decl_stmt|;
name|FacetsCollector
name|drillDownCollector
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
if|if
condition|(
name|drillDownDims
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// There are no drill-down dims, so there is no
comment|// drill-sideways to compute:
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|hitCollector
argument_list|,
name|drillDownCollector
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|buildFacetsResult
argument_list|(
name|drillDownCollector
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|Query
name|baseQuery
init|=
name|query
operator|.
name|getBaseQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseQuery
operator|==
literal|null
condition|)
block|{
comment|// TODO: we could optimize this pure-browse case by
comment|// making a custom scorer instead:
name|baseQuery
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
block|}
name|Query
index|[]
name|drillDownQueries
init|=
name|query
operator|.
name|getDrillDownQueries
argument_list|()
decl_stmt|;
name|FacetsCollector
index|[]
name|drillSidewaysCollectors
init|=
operator|new
name|FacetsCollector
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|drillSidewaysCollectors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|drillSidewaysCollectors
index|[
name|i
index|]
operator|=
operator|new
name|FacetsCollector
argument_list|()
expr_stmt|;
block|}
name|DrillSidewaysQuery
name|dsq
init|=
operator|new
name|DrillSidewaysQuery
argument_list|(
name|baseQuery
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownQueries
argument_list|,
name|scoreSubDocsAtOnce
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|hitCollector
operator|.
name|needsScores
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// this is a horrible hack in order to make sure IndexSearcher will not
comment|// attempt to cache the DrillSidewaysQuery
name|hitCollector
operator|=
operator|new
name|FilterCollector
argument_list|(
name|hitCollector
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|dsq
argument_list|,
name|hitCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|buildFacetsResult
argument_list|(
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownDims
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Search, sorting by {@link Sort}, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|Query
name|filter
parameter_list|,
name|FieldDoc
name|after
parameter_list|,
name|int
name|topN
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|doDocScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|,
name|filter
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
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
comment|// the collector does not alow numHits = 0
block|}
specifier|final
name|int
name|fTopN
init|=
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
comment|// We have an executor, let use the multi-threaded version
specifier|final
name|CollectorManager
argument_list|<
name|TopFieldCollector
argument_list|,
name|TopFieldDocs
argument_list|>
name|collectorManager
init|=
operator|new
name|CollectorManager
argument_list|<
name|TopFieldCollector
argument_list|,
name|TopFieldDocs
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TopFieldCollector
name|newCollector
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|fTopN
argument_list|,
name|after
argument_list|,
literal|true
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TopFieldDocs
name|reduce
parameter_list|(
name|Collection
argument_list|<
name|TopFieldCollector
argument_list|>
name|collectors
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TopFieldDocs
index|[]
name|topFieldDocs
init|=
operator|new
name|TopFieldDocs
index|[
name|collectors
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TopFieldCollector
name|collector
range|:
name|collectors
control|)
name|topFieldDocs
index|[
name|pos
operator|++
index|]
operator|=
name|collector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
return|return
name|TopDocs
operator|.
name|merge
argument_list|(
name|sort
argument_list|,
name|topN
argument_list|,
name|topFieldDocs
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ConcurrentDrillSidewaysResult
argument_list|<
name|TopFieldDocs
argument_list|>
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|collectorManager
argument_list|)
decl_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|r
operator|.
name|facets
argument_list|,
name|r
operator|.
name|collectorResult
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|TopFieldCollector
name|hitCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|fTopN
argument_list|,
name|after
argument_list|,
literal|true
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|)
decl_stmt|;
name|DrillSidewaysResult
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|hitCollector
argument_list|)
decl_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|r
operator|.
name|facets
argument_list|,
name|hitCollector
operator|.
name|topDocs
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|search
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|topN
argument_list|)
return|;
block|}
block|}
comment|/**    * Search, sorting by score, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
literal|null
argument_list|,
name|query
argument_list|,
name|topN
argument_list|)
return|;
block|}
comment|/**    * Search, sorting by score, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|ScoreDoc
name|after
parameter_list|,
name|DrillDownQuery
name|query
parameter_list|,
name|int
name|topN
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
comment|// the collector does not alow numHits = 0
block|}
specifier|final
name|int
name|fTopN
init|=
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
comment|// We have an executor, let use the multi-threaded version
specifier|final
name|CollectorManager
argument_list|<
name|TopScoreDocCollector
argument_list|,
name|TopDocs
argument_list|>
name|collectorManager
init|=
operator|new
name|CollectorManager
argument_list|<
name|TopScoreDocCollector
argument_list|,
name|TopDocs
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TopScoreDocCollector
name|newCollector
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|fTopN
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TopDocs
name|reduce
parameter_list|(
name|Collection
argument_list|<
name|TopScoreDocCollector
argument_list|>
name|collectors
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TopDocs
index|[]
name|topDocs
init|=
operator|new
name|TopDocs
index|[
name|collectors
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TopScoreDocCollector
name|collector
range|:
name|collectors
control|)
name|topDocs
index|[
name|pos
operator|++
index|]
operator|=
name|collector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
return|return
name|TopDocs
operator|.
name|merge
argument_list|(
name|topN
argument_list|,
name|topDocs
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ConcurrentDrillSidewaysResult
argument_list|<
name|TopDocs
argument_list|>
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|collectorManager
argument_list|)
decl_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|r
operator|.
name|facets
argument_list|,
name|r
operator|.
name|collectorResult
argument_list|)
return|;
block|}
else|else
block|{
name|TopScoreDocCollector
name|hitCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|topN
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|DrillSidewaysResult
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|hitCollector
argument_list|)
decl_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|r
operator|.
name|facets
argument_list|,
name|hitCollector
operator|.
name|topDocs
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Override this and return true if your collector    * (e.g., {@code ToParentBlockJoinCollector}) expects all    * sub-scorers to be positioned on the document being    * collected.  This will cause some performance loss;    * default is false.    */
DECL|method|scoreSubDocsAtOnce
specifier|protected
name|boolean
name|scoreSubDocsAtOnce
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Result of a drill sideways search, including the    * {@link Facets} and {@link TopDocs}.    */
DECL|class|DrillSidewaysResult
specifier|public
specifier|static
class|class
name|DrillSidewaysResult
block|{
comment|/**      * Combined drill down and sideways results.      */
DECL|field|facets
specifier|public
specifier|final
name|Facets
name|facets
decl_stmt|;
comment|/**      * Hits.      */
DECL|field|hits
specifier|public
specifier|final
name|TopDocs
name|hits
decl_stmt|;
comment|/**      * Sole constructor.      */
DECL|method|DrillSidewaysResult
specifier|public
name|DrillSidewaysResult
parameter_list|(
name|Facets
name|facets
parameter_list|,
name|TopDocs
name|hits
parameter_list|)
block|{
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
block|}
block|}
DECL|class|CallableCollector
specifier|private
specifier|static
class|class
name|CallableCollector
implements|implements
name|Callable
argument_list|<
name|CallableResult
argument_list|>
block|{
DECL|field|pos
specifier|private
specifier|final
name|int
name|pos
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|collectorManager
specifier|private
specifier|final
name|CollectorManager
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|collectorManager
decl_stmt|;
DECL|method|CallableCollector
specifier|private
name|CallableCollector
parameter_list|(
name|int
name|pos
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|CollectorManager
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|collectorManager
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|collectorManager
operator|=
name|collectorManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call
specifier|public
name|CallableResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|CallableResult
argument_list|(
name|pos
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collectorManager
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|CallableResult
specifier|private
specifier|static
class|class
name|CallableResult
block|{
DECL|field|pos
specifier|private
specifier|final
name|int
name|pos
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|Object
name|result
decl_stmt|;
DECL|method|CallableResult
specifier|private
name|CallableResult
parameter_list|(
name|int
name|pos
parameter_list|,
name|Object
name|result
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
block|}
DECL|method|getDrillDownQuery
specifier|private
name|DrillDownQuery
name|getDrillDownQuery
parameter_list|(
specifier|final
name|DrillDownQuery
name|query
parameter_list|,
name|Query
index|[]
name|queries
parameter_list|,
specifier|final
name|String
name|excludedDimension
parameter_list|)
block|{
specifier|final
name|DrillDownQuery
name|ddl
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|,
name|query
operator|.
name|getBaseQuery
argument_list|()
argument_list|)
decl_stmt|;
name|query
operator|.
name|getDims
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|dim
parameter_list|,
name|pos
parameter_list|)
lambda|->
block|{
if|if
condition|(
operator|!
name|dim
operator|.
name|equals
argument_list|(
name|excludedDimension
argument_list|)
condition|)
name|ddl
operator|.
name|add
argument_list|(
name|dim
argument_list|,
name|queries
index|[
name|pos
index|]
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|ddl
operator|.
name|getDims
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|queries
operator|.
name|length
condition|?
literal|null
else|:
name|ddl
return|;
block|}
comment|/** Runs a search, using a {@link CollectorManager} to gather and merge search results */
DECL|method|search
specifier|public
parameter_list|<
name|R
parameter_list|>
name|ConcurrentDrillSidewaysResult
argument_list|<
name|R
argument_list|>
name|search
parameter_list|(
specifier|final
name|DrillDownQuery
name|query
parameter_list|,
specifier|final
name|CollectorManager
argument_list|<
name|?
argument_list|,
name|R
argument_list|>
name|hitCollectorManager
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
name|query
operator|.
name|getDims
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|CallableCollector
argument_list|>
name|callableCollectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|drillDownDims
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// Add the main DrillDownQuery
name|callableCollectors
operator|.
name|add
argument_list|(
operator|new
name|CallableCollector
argument_list|(
operator|-
literal|1
argument_list|,
name|searcher
argument_list|,
name|query
argument_list|,
operator|new
name|MultiCollectorManager
argument_list|(
operator|new
name|FacetsCollectorManager
argument_list|()
argument_list|,
name|hitCollectorManager
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|Query
index|[]
name|filters
init|=
name|query
operator|.
name|getDrillDownQueries
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dim
range|:
name|drillDownDims
operator|.
name|keySet
argument_list|()
control|)
name|callableCollectors
operator|.
name|add
argument_list|(
operator|new
name|CallableCollector
argument_list|(
name|i
operator|++
argument_list|,
name|searcher
argument_list|,
name|getDrillDownQuery
argument_list|(
name|query
argument_list|,
name|filters
argument_list|,
name|dim
argument_list|)
argument_list|,
operator|new
name|FacetsCollectorManager
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FacetsCollector
name|mainFacetsCollector
decl_stmt|;
specifier|final
name|FacetsCollector
index|[]
name|facetsCollectors
init|=
operator|new
name|FacetsCollector
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|R
name|collectorResult
decl_stmt|;
try|try
block|{
comment|// Run the query pool
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|CallableResult
argument_list|>
argument_list|>
name|futures
init|=
name|executor
operator|.
name|invokeAll
argument_list|(
name|callableCollectors
argument_list|)
decl_stmt|;
comment|// Extract the results
specifier|final
name|Object
index|[]
name|mainResults
init|=
operator|(
name|Object
index|[]
operator|)
name|futures
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|result
decl_stmt|;
name|mainFacetsCollector
operator|=
operator|(
name|FacetsCollector
operator|)
name|mainResults
index|[
literal|0
index|]
expr_stmt|;
name|collectorResult
operator|=
operator|(
name|R
operator|)
name|mainResults
index|[
literal|1
index|]
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<
name|futures
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CallableResult
name|result
init|=
name|futures
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|facetsCollectors
index|[
name|result
operator|.
name|pos
index|]
operator|=
operator|(
name|FacetsCollector
operator|)
name|result
operator|.
name|result
expr_stmt|;
block|}
comment|// Fill the null results with the mainFacetsCollector
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|facetsCollectors
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|facetsCollectors
index|[
name|i
index|]
operator|==
literal|null
condition|)
name|facetsCollectors
index|[
name|i
index|]
operator|=
name|mainFacetsCollector
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// build the facets and return the result
return|return
operator|new
name|ConcurrentDrillSidewaysResult
argument_list|<>
argument_list|(
name|buildFacetsResult
argument_list|(
name|mainFacetsCollector
argument_list|,
name|facetsCollectors
argument_list|,
name|drillDownDims
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
name|collectorResult
argument_list|)
return|;
block|}
comment|/**    * Result of a concurrent drill sideways search, including the    * {@link Facets} and {@link TopDocs}.    */
DECL|class|ConcurrentDrillSidewaysResult
specifier|public
specifier|static
class|class
name|ConcurrentDrillSidewaysResult
parameter_list|<
name|R
parameter_list|>
extends|extends
name|DrillSidewaysResult
block|{
comment|/** The merged search results */
DECL|field|collectorResult
specifier|public
specifier|final
name|R
name|collectorResult
decl_stmt|;
comment|/**      * Sole constructor.      */
DECL|method|ConcurrentDrillSidewaysResult
name|ConcurrentDrillSidewaysResult
parameter_list|(
name|Facets
name|facets
parameter_list|,
name|TopDocs
name|hits
parameter_list|,
name|R
name|collectorResult
parameter_list|)
block|{
name|super
argument_list|(
name|facets
argument_list|,
name|hits
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectorResult
operator|=
name|collectorResult
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

