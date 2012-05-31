begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
name|*
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
name|document
operator|.
name|DerefBytesDocValuesField
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
name|DocValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|*
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
name|grouping
operator|.
name|dv
operator|.
name|DVAllGroupHeadsCollector
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
name|grouping
operator|.
name|dv
operator|.
name|DVAllGroupsCollector
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
name|grouping
operator|.
name|dv
operator|.
name|DVFirstPassGroupingCollector
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
name|grouping
operator|.
name|dv
operator|.
name|DVSecondPassGroupingCollector
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
name|grouping
operator|.
name|function
operator|.
name|FunctionAllGroupHeadsCollector
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
name|grouping
operator|.
name|function
operator|.
name|FunctionAllGroupsCollector
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
name|grouping
operator|.
name|function
operator|.
name|FunctionFirstPassGroupingCollector
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
name|grouping
operator|.
name|function
operator|.
name|FunctionSecondPassGroupingCollector
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
name|grouping
operator|.
name|term
operator|.
name|TermAllGroupHeadsCollector
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
name|grouping
operator|.
name|term
operator|.
name|TermAllGroupsCollector
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
name|grouping
operator|.
name|term
operator|.
name|TermFirstPassGroupingCollector
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
name|grouping
operator|.
name|term
operator|.
name|TermSecondPassGroupingCollector
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
name|Bits
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
name|lucene
operator|.
name|util
operator|.
name|mutable
operator|.
name|MutableValue
import|;
end_import

begin_comment
comment|/**  * Convenience class to perform grouping in a non distributed environment.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GroupingSearch
specifier|public
class|class
name|GroupingSearch
block|{
DECL|field|groupField
specifier|private
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|groupFunction
specifier|private
specifier|final
name|ValueSource
name|groupFunction
decl_stmt|;
DECL|field|valueSourceContext
specifier|private
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|valueSourceContext
decl_stmt|;
DECL|field|groupEndDocs
specifier|private
specifier|final
name|Filter
name|groupEndDocs
decl_stmt|;
DECL|field|docValuesType
specifier|private
specifier|final
name|DocValues
operator|.
name|Type
name|docValuesType
decl_stmt|;
DECL|field|diskResidentDocValues
specifier|private
specifier|final
name|boolean
name|diskResidentDocValues
decl_stmt|;
DECL|field|groupSort
specifier|private
name|Sort
name|groupSort
init|=
name|Sort
operator|.
name|RELEVANCE
decl_stmt|;
DECL|field|sortWithinGroup
specifier|private
name|Sort
name|sortWithinGroup
decl_stmt|;
DECL|field|groupDocsOffset
specifier|private
name|int
name|groupDocsOffset
decl_stmt|;
DECL|field|groupDocsLimit
specifier|private
name|int
name|groupDocsLimit
init|=
literal|1
decl_stmt|;
DECL|field|fillSortFields
specifier|private
name|boolean
name|fillSortFields
decl_stmt|;
DECL|field|includeScores
specifier|private
name|boolean
name|includeScores
init|=
literal|true
decl_stmt|;
DECL|field|includeMaxScore
specifier|private
name|boolean
name|includeMaxScore
init|=
literal|true
decl_stmt|;
DECL|field|maxCacheRAMMB
specifier|private
name|Double
name|maxCacheRAMMB
decl_stmt|;
DECL|field|maxDocsToCache
specifier|private
name|Integer
name|maxDocsToCache
decl_stmt|;
DECL|field|cacheScores
specifier|private
name|boolean
name|cacheScores
decl_stmt|;
DECL|field|allGroups
specifier|private
name|boolean
name|allGroups
decl_stmt|;
DECL|field|allGroupHeads
specifier|private
name|boolean
name|allGroupHeads
decl_stmt|;
DECL|field|initialSize
specifier|private
name|int
name|initialSize
init|=
literal|128
decl_stmt|;
DECL|field|matchingGroups
specifier|private
name|Collection
argument_list|<
name|?
argument_list|>
name|matchingGroups
decl_stmt|;
DECL|field|matchingGroupHeads
specifier|private
name|Bits
name|matchingGroupHeads
decl_stmt|;
comment|/**    * Constructs a<code>GroupingSearch</code> instance that groups documents by index terms using the {@link FieldCache}.    * The group field can only have one token per document. This means that the field must not be analysed.    *    * @param groupField The name of the field to group by.    */
DECL|method|GroupingSearch
specifier|public
name|GroupingSearch
parameter_list|(
name|String
name|groupField
parameter_list|)
block|{
name|this
argument_list|(
name|groupField
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a<code>GroupingSearch</code> instance that groups documents by doc values.    * This constructor can only be used when the groupField    * is a<code>*DocValuesField</code> (eg, {@link DerefBytesDocValuesField}.    *    * @param groupField            The name of the field to group by that contains doc values    * @param docValuesType         The doc values type of the specified groupField    * @param diskResidentDocValues Whether the values to group by should be disk resident    */
DECL|method|GroupingSearch
specifier|public
name|GroupingSearch
parameter_list|(
name|String
name|groupField
parameter_list|,
name|DocValues
operator|.
name|Type
name|docValuesType
parameter_list|,
name|boolean
name|diskResidentDocValues
parameter_list|)
block|{
name|this
argument_list|(
name|groupField
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|docValuesType
argument_list|,
name|diskResidentDocValues
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a<code>GroupingSearch</code> instance that groups documents by function using a {@link ValueSource}    * instance.    *    * @param groupFunction      The function to group by specified as {@link ValueSource}    * @param valueSourceContext The context of the specified groupFunction    */
DECL|method|GroupingSearch
specifier|public
name|GroupingSearch
parameter_list|(
name|ValueSource
name|groupFunction
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|valueSourceContext
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|groupFunction
argument_list|,
name|valueSourceContext
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for grouping documents by doc block.    * This constructor can only be used when documents belonging in a group are indexed in one block.    *    * @param groupEndDocs The filter that marks the last document in all doc blocks    */
DECL|method|GroupingSearch
specifier|public
name|GroupingSearch
parameter_list|(
name|Filter
name|groupEndDocs
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|groupEndDocs
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|GroupingSearch
specifier|private
name|GroupingSearch
parameter_list|(
name|String
name|groupField
parameter_list|,
name|ValueSource
name|groupFunction
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|valueSourceContext
parameter_list|,
name|Filter
name|groupEndDocs
parameter_list|,
name|DocValues
operator|.
name|Type
name|docValuesType
parameter_list|,
name|boolean
name|diskResidentDocValues
parameter_list|)
block|{
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|groupFunction
operator|=
name|groupFunction
expr_stmt|;
name|this
operator|.
name|valueSourceContext
operator|=
name|valueSourceContext
expr_stmt|;
name|this
operator|.
name|groupEndDocs
operator|=
name|groupEndDocs
expr_stmt|;
name|this
operator|.
name|docValuesType
operator|=
name|docValuesType
expr_stmt|;
name|this
operator|.
name|diskResidentDocValues
operator|=
name|diskResidentDocValues
expr_stmt|;
block|}
comment|/**    * Executes a grouped search. Both the first pass and second pass are executed on the specified searcher.    *    * @param searcher    The {@link org.apache.lucene.search.IndexSearcher} instance to execute the grouped search on.    * @param query       The query to execute with the grouping    * @param groupOffset The group offset    * @param groupLimit  The number of groups to return from the specified group offset    * @return the grouped result as a {@link TopGroups} instance    * @throws IOException If any I/O related errors occur    */
DECL|method|search
specifier|public
parameter_list|<
name|T
parameter_list|>
name|TopGroups
argument_list|<
name|T
argument_list|>
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|groupOffset
parameter_list|,
name|int
name|groupLimit
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|,
name|query
argument_list|,
name|groupOffset
argument_list|,
name|groupLimit
argument_list|)
return|;
block|}
comment|/**    * Executes a grouped search. Both the first pass and second pass are executed on the specified searcher.    *    * @param searcher    The {@link org.apache.lucene.search.IndexSearcher} instance to execute the grouped search on.    * @param filter      The filter to execute with the grouping    * @param query       The query to execute with the grouping    * @param groupOffset The group offset    * @param groupLimit  The number of groups to return from the specified group offset    * @return the grouped result as a {@link TopGroups} instance    * @throws IOException If any I/O related errors occur    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|search
specifier|public
parameter_list|<
name|T
parameter_list|>
name|TopGroups
argument_list|<
name|T
argument_list|>
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|groupOffset
parameter_list|,
name|int
name|groupLimit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|groupField
operator|!=
literal|null
operator|||
name|groupFunction
operator|!=
literal|null
condition|)
block|{
return|return
name|groupByFieldOrFunction
argument_list|(
name|searcher
argument_list|,
name|filter
argument_list|,
name|query
argument_list|,
name|groupOffset
argument_list|,
name|groupLimit
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|groupEndDocs
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|TopGroups
argument_list|<
name|T
argument_list|>
operator|)
name|groupByDocBlock
argument_list|(
name|searcher
argument_list|,
name|filter
argument_list|,
name|query
argument_list|,
name|groupOffset
argument_list|,
name|groupLimit
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Either groupField, groupFunction or groupEndDocs must be set."
argument_list|)
throw|;
comment|// This can't happen...
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|groupByFieldOrFunction
specifier|protected
name|TopGroups
name|groupByFieldOrFunction
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|groupOffset
parameter_list|,
name|int
name|groupLimit
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|topN
init|=
name|groupOffset
operator|+
name|groupLimit
decl_stmt|;
specifier|final
name|AbstractFirstPassGroupingCollector
name|firstPassCollector
decl_stmt|;
specifier|final
name|AbstractAllGroupsCollector
name|allGroupsCollector
decl_stmt|;
specifier|final
name|AbstractAllGroupHeadsCollector
name|allGroupHeadsCollector
decl_stmt|;
if|if
condition|(
name|groupFunction
operator|!=
literal|null
condition|)
block|{
name|firstPassCollector
operator|=
operator|new
name|FunctionFirstPassGroupingCollector
argument_list|(
name|groupFunction
argument_list|,
name|valueSourceContext
argument_list|,
name|groupSort
argument_list|,
name|topN
argument_list|)
expr_stmt|;
if|if
condition|(
name|allGroups
condition|)
block|{
name|allGroupsCollector
operator|=
operator|new
name|FunctionAllGroupsCollector
argument_list|(
name|groupFunction
argument_list|,
name|valueSourceContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupsCollector
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|allGroupHeads
condition|)
block|{
name|allGroupHeadsCollector
operator|=
operator|new
name|FunctionAllGroupHeadsCollector
argument_list|(
name|groupFunction
argument_list|,
name|valueSourceContext
argument_list|,
name|sortWithinGroup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupHeadsCollector
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|docValuesType
operator|!=
literal|null
condition|)
block|{
name|firstPassCollector
operator|=
name|DVFirstPassGroupingCollector
operator|.
name|create
argument_list|(
name|groupSort
argument_list|,
name|topN
argument_list|,
name|groupField
argument_list|,
name|docValuesType
argument_list|,
name|diskResidentDocValues
argument_list|)
expr_stmt|;
if|if
condition|(
name|allGroups
condition|)
block|{
name|allGroupsCollector
operator|=
name|DVAllGroupsCollector
operator|.
name|create
argument_list|(
name|groupField
argument_list|,
name|docValuesType
argument_list|,
name|diskResidentDocValues
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupsCollector
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|allGroupHeads
condition|)
block|{
name|allGroupHeadsCollector
operator|=
name|DVAllGroupHeadsCollector
operator|.
name|create
argument_list|(
name|groupField
argument_list|,
name|sortWithinGroup
argument_list|,
name|docValuesType
argument_list|,
name|diskResidentDocValues
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupHeadsCollector
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|firstPassCollector
operator|=
operator|new
name|TermFirstPassGroupingCollector
argument_list|(
name|groupField
argument_list|,
name|groupSort
argument_list|,
name|topN
argument_list|)
expr_stmt|;
if|if
condition|(
name|allGroups
condition|)
block|{
name|allGroupsCollector
operator|=
operator|new
name|TermAllGroupsCollector
argument_list|(
name|groupField
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupsCollector
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|allGroupHeads
condition|)
block|{
name|allGroupHeadsCollector
operator|=
name|TermAllGroupHeadsCollector
operator|.
name|create
argument_list|(
name|groupField
argument_list|,
name|sortWithinGroup
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupHeadsCollector
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|final
name|Collector
name|firstRound
decl_stmt|;
if|if
condition|(
name|allGroupHeads
operator|||
name|allGroups
condition|)
block|{
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<
name|Collector
argument_list|>
argument_list|()
decl_stmt|;
name|collectors
operator|.
name|add
argument_list|(
name|firstPassCollector
argument_list|)
expr_stmt|;
if|if
condition|(
name|allGroupHeads
condition|)
block|{
name|collectors
operator|.
name|add
argument_list|(
name|allGroupsCollector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allGroupHeads
condition|)
block|{
name|collectors
operator|.
name|add
argument_list|(
name|allGroupHeadsCollector
argument_list|)
expr_stmt|;
block|}
name|firstRound
operator|=
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collectors
operator|.
name|toArray
argument_list|(
operator|new
name|Collector
index|[
name|collectors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|firstRound
operator|=
name|firstPassCollector
expr_stmt|;
block|}
name|CachingCollector
name|cachedCollector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|maxCacheRAMMB
operator|!=
literal|null
operator|||
name|maxDocsToCache
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|maxCacheRAMMB
operator|!=
literal|null
condition|)
block|{
name|cachedCollector
operator|=
name|CachingCollector
operator|.
name|create
argument_list|(
name|firstRound
argument_list|,
name|cacheScores
argument_list|,
name|maxCacheRAMMB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cachedCollector
operator|=
name|CachingCollector
operator|.
name|create
argument_list|(
name|firstRound
argument_list|,
name|cacheScores
argument_list|,
name|maxDocsToCache
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|cachedCollector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|firstRound
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allGroups
condition|)
block|{
name|matchingGroups
operator|=
name|allGroupsCollector
operator|.
name|getGroups
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|matchingGroups
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|allGroupHeads
condition|)
block|{
name|matchingGroupHeads
operator|=
name|allGroupHeadsCollector
operator|.
name|retrieveGroupHeads
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matchingGroupHeads
operator|=
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|SearchGroup
argument_list|>
name|topSearchGroups
init|=
name|firstPassCollector
operator|.
name|getTopGroups
argument_list|(
name|groupOffset
argument_list|,
name|fillSortFields
argument_list|)
decl_stmt|;
if|if
condition|(
name|topSearchGroups
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|TopGroups
argument_list|(
operator|new
name|SortField
index|[
literal|0
index|]
argument_list|,
operator|new
name|SortField
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|GroupDocs
index|[
literal|0
index|]
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
return|;
block|}
name|int
name|topNInsideGroup
init|=
name|groupDocsOffset
operator|+
name|groupDocsLimit
decl_stmt|;
name|AbstractSecondPassGroupingCollector
name|secondPassCollector
decl_stmt|;
if|if
condition|(
name|groupFunction
operator|!=
literal|null
condition|)
block|{
name|secondPassCollector
operator|=
operator|new
name|FunctionSecondPassGroupingCollector
argument_list|(
operator|(
name|Collection
operator|)
name|topSearchGroups
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|topNInsideGroup
argument_list|,
name|includeScores
argument_list|,
name|includeMaxScore
argument_list|,
name|fillSortFields
argument_list|,
name|groupFunction
argument_list|,
name|valueSourceContext
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docValuesType
operator|!=
literal|null
condition|)
block|{
name|secondPassCollector
operator|=
name|DVSecondPassGroupingCollector
operator|.
name|create
argument_list|(
name|groupField
argument_list|,
name|diskResidentDocValues
argument_list|,
name|docValuesType
argument_list|,
operator|(
name|Collection
operator|)
name|topSearchGroups
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|topNInsideGroup
argument_list|,
name|includeScores
argument_list|,
name|includeMaxScore
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|secondPassCollector
operator|=
operator|new
name|TermSecondPassGroupingCollector
argument_list|(
name|groupField
argument_list|,
operator|(
name|Collection
operator|)
name|topSearchGroups
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|topNInsideGroup
argument_list|,
name|includeScores
argument_list|,
name|includeMaxScore
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cachedCollector
operator|!=
literal|null
operator|&&
name|cachedCollector
operator|.
name|isCached
argument_list|()
condition|)
block|{
name|cachedCollector
operator|.
name|replay
argument_list|(
name|secondPassCollector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|secondPassCollector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allGroups
condition|)
block|{
return|return
operator|new
name|TopGroups
argument_list|(
name|secondPassCollector
operator|.
name|getTopGroups
argument_list|(
name|groupDocsOffset
argument_list|)
argument_list|,
name|matchingGroups
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|secondPassCollector
operator|.
name|getTopGroups
argument_list|(
name|groupDocsOffset
argument_list|)
return|;
block|}
block|}
DECL|method|groupByDocBlock
specifier|protected
name|TopGroups
argument_list|<
name|?
argument_list|>
name|groupByDocBlock
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|groupOffset
parameter_list|,
name|int
name|groupLimit
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|topN
init|=
name|groupOffset
operator|+
name|groupLimit
decl_stmt|;
name|BlockGroupingCollector
name|c
init|=
operator|new
name|BlockGroupingCollector
argument_list|(
name|groupSort
argument_list|,
name|topN
argument_list|,
name|includeScores
argument_list|,
name|groupEndDocs
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|int
name|topNInsideGroup
init|=
name|groupDocsOffset
operator|+
name|groupDocsLimit
decl_stmt|;
return|return
name|c
operator|.
name|getTopGroups
argument_list|(
name|sortWithinGroup
argument_list|,
name|groupOffset
argument_list|,
name|groupDocsOffset
argument_list|,
name|topNInsideGroup
argument_list|,
name|fillSortFields
argument_list|)
return|;
block|}
comment|/**    * Enables caching for the second pass search. The cache will not grow over a specified limit in MB.    * The cache is filled during the first pass searched and then replayed during the second pass searched.    * If the cache grows beyond the specified limit, then the cache is purged and not used in the second pass search.    *    * @param maxCacheRAMMB The maximum amount in MB the cache is allowed to hold    * @param cacheScores   Whether to cache the scores    * @return<code>this</code>    */
DECL|method|setCachingInMB
specifier|public
name|GroupingSearch
name|setCachingInMB
parameter_list|(
name|double
name|maxCacheRAMMB
parameter_list|,
name|boolean
name|cacheScores
parameter_list|)
block|{
name|this
operator|.
name|maxCacheRAMMB
operator|=
name|maxCacheRAMMB
expr_stmt|;
name|this
operator|.
name|maxDocsToCache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|cacheScores
operator|=
name|cacheScores
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Enables caching for the second pass search. The cache will not contain more than the maximum specified documents.    * The cache is filled during the first pass searched and then replayed during the second pass searched.    * If the cache grows beyond the specified limit, then the cache is purged and not used in the second pass search.    *    * @param maxDocsToCache The maximum number of documents the cache is allowed to hold    * @param cacheScores    Whether to cache the scores    * @return<code>this</code>    */
DECL|method|setCaching
specifier|public
name|GroupingSearch
name|setCaching
parameter_list|(
name|int
name|maxDocsToCache
parameter_list|,
name|boolean
name|cacheScores
parameter_list|)
block|{
name|this
operator|.
name|maxDocsToCache
operator|=
name|maxDocsToCache
expr_stmt|;
name|this
operator|.
name|maxCacheRAMMB
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|cacheScores
operator|=
name|cacheScores
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Disables any enabled cache.    *    * @return<code>this</code>    */
DECL|method|disableCaching
specifier|public
name|GroupingSearch
name|disableCaching
parameter_list|()
block|{
name|this
operator|.
name|maxCacheRAMMB
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|maxDocsToCache
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Specifies how groups are sorted.    * Defaults to {@link Sort#RELEVANCE}.    *    * @param groupSort The sort for the groups.    * @return<code>this</code>    */
DECL|method|setGroupSort
specifier|public
name|GroupingSearch
name|setGroupSort
parameter_list|(
name|Sort
name|groupSort
parameter_list|)
block|{
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Specified how documents inside a group are sorted.    * Defaults to {@link Sort#RELEVANCE}.    *    * @param sortWithinGroup The sort for documents inside a group    * @return<code>this</code>    */
DECL|method|setSortWithinGroup
specifier|public
name|GroupingSearch
name|setSortWithinGroup
parameter_list|(
name|Sort
name|sortWithinGroup
parameter_list|)
block|{
name|this
operator|.
name|sortWithinGroup
operator|=
name|sortWithinGroup
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Specifies the offset for documents inside a group.    *    * @param groupDocsOffset The offset for documents inside a    * @return<code>this</code>    */
DECL|method|setGroupDocsOffset
specifier|public
name|GroupingSearch
name|setGroupDocsOffset
parameter_list|(
name|int
name|groupDocsOffset
parameter_list|)
block|{
name|this
operator|.
name|groupDocsOffset
operator|=
name|groupDocsOffset
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Specifies the number of documents to return inside a group from the specified groupDocsOffset.    *    * @param groupDocsLimit The number of documents to return inside a group    * @return<code>this</code>    */
DECL|method|setGroupDocsLimit
specifier|public
name|GroupingSearch
name|setGroupDocsLimit
parameter_list|(
name|int
name|groupDocsLimit
parameter_list|)
block|{
name|this
operator|.
name|groupDocsLimit
operator|=
name|groupDocsLimit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Whether to also fill the sort fields per returned group and groups docs.    *    * @param fillSortFields Whether to also fill the sort fields per returned group and groups docs    * @return<code>this</code>    */
DECL|method|setFillSortFields
specifier|public
name|GroupingSearch
name|setFillSortFields
parameter_list|(
name|boolean
name|fillSortFields
parameter_list|)
block|{
name|this
operator|.
name|fillSortFields
operator|=
name|fillSortFields
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Whether to include the scores per doc inside a group.    *    * @param includeScores Whether to include the scores per doc inside a group    * @return<code>this</code>    */
DECL|method|setIncludeScores
specifier|public
name|GroupingSearch
name|setIncludeScores
parameter_list|(
name|boolean
name|includeScores
parameter_list|)
block|{
name|this
operator|.
name|includeScores
operator|=
name|includeScores
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Whether to include the score of the most relevant document per group.    *    * @param includeMaxScore Whether to include the score of the most relevant document per group    * @return<code>this</code>    */
DECL|method|setIncludeMaxScore
specifier|public
name|GroupingSearch
name|setIncludeMaxScore
parameter_list|(
name|boolean
name|includeMaxScore
parameter_list|)
block|{
name|this
operator|.
name|includeMaxScore
operator|=
name|includeMaxScore
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Whether to also co0.0mpute all groups matching the query.    * This can be used to determine the number of groups, which can be used for accurate pagination.    *<p/>    * When grouping by doc block the number of groups are automatically included in the {@link TopGroups} and this    * option doesn't have any influence.    *    * @param allGroups to also compute all groups matching the query    * @return<code>this</code>    */
DECL|method|setAllGroups
specifier|public
name|GroupingSearch
name|setAllGroups
parameter_list|(
name|boolean
name|allGroups
parameter_list|)
block|{
name|this
operator|.
name|allGroups
operator|=
name|allGroups
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * If {@link #setAllGroups(boolean)} was set to<code>true</code> then all matching groups are returned, otherwise    * an empty collection is returned.    *    * @param<T> The group value type. This can be a {@link BytesRef} or a {@link MutableValue} instance. If grouping    *            by doc block this the group value is always<code>null</code>.    * @return all matching groups are returned, or an empty collection    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|getAllMatchingGroups
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Collection
argument_list|<
name|T
argument_list|>
name|getAllMatchingGroups
parameter_list|()
block|{
return|return
operator|(
name|Collection
argument_list|<
name|T
argument_list|>
operator|)
name|matchingGroups
return|;
block|}
comment|/**    * Whether to compute all group heads (most relevant document per group) matching the query.    *<p/>    * This feature isn't enabled when grouping by doc block.    *    * @param allGroupHeads Whether to compute all group heads (most relevant document per group) matching the query    * @return<code>this</code>    */
DECL|method|setAllGroupHeads
specifier|public
name|GroupingSearch
name|setAllGroupHeads
parameter_list|(
name|boolean
name|allGroupHeads
parameter_list|)
block|{
name|this
operator|.
name|allGroupHeads
operator|=
name|allGroupHeads
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the matching group heads if {@link #setAllGroupHeads(boolean)} was set to true or an empty bit set.    *    * @return The matching group heads if {@link #setAllGroupHeads(boolean)} was set to true or an empty bit set    */
DECL|method|getAllGroupHeads
specifier|public
name|Bits
name|getAllGroupHeads
parameter_list|()
block|{
return|return
name|matchingGroupHeads
return|;
block|}
comment|/**    * Sets the initial size of some internal used data structures.    * This prevents growing data structures many times. This can improve the performance of the grouping at the cost of    * more initial RAM.    *<p/>    * The {@link #setAllGroups} and {@link #setAllGroupHeads} features use this option.    * Defaults to 128.    *    * @param initialSize The initial size of some internal used data structures    * @return<code>this</code>    */
DECL|method|setInitialSize
specifier|public
name|GroupingSearch
name|setInitialSize
parameter_list|(
name|int
name|initialSize
parameter_list|)
block|{
name|this
operator|.
name|initialSize
operator|=
name|initialSize
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

