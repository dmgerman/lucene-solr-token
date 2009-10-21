begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_comment
comment|/** Implements parallel search over a set of<code>Searchables</code>.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods.  */
end_comment

begin_class
DECL|class|ParallelMultiSearcher
specifier|public
class|class
name|ParallelMultiSearcher
extends|extends
name|MultiSearcher
block|{
DECL|field|searchables
specifier|private
name|Searchable
index|[]
name|searchables
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
comment|/** Creates a searchable which searches<i>searchables</i>. */
DECL|method|ParallelMultiSearcher
specifier|public
name|ParallelMultiSearcher
parameter_list|(
name|Searchable
modifier|...
name|searchables
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|searchables
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchables
operator|=
name|searchables
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|getStarts
argument_list|()
expr_stmt|;
block|}
comment|/**    * TODO: parallelize this one too    */
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|/**    * A search implementation which spans a new thread for each    * Searchable, waits for each search to complete and merge    * the results back together.    */
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|HitQueue
name|hq
init|=
operator|new
name|HitQueue
argument_list|(
name|nDocs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
name|MultiSearcherThread
index|[]
name|msta
init|=
operator|new
name|MultiSearcherThread
index|[
name|searchables
operator|.
name|length
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
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searchable
comment|// Assume not too many searchables and cost of creating a thread is by far inferior to a search
name|msta
index|[
name|i
index|]
operator|=
operator|new
name|MultiSearcherThread
argument_list|(
name|searchables
index|[
name|i
index|]
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|hq
argument_list|,
name|i
argument_list|,
name|starts
argument_list|,
literal|"MultiSearcher thread #"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|msta
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|msta
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// In 3.0 we will change this to throw
comment|// InterruptedException instead
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|IOException
name|ioe
init|=
name|msta
index|[
name|i
index|]
operator|.
name|getIOException
argument_list|()
decl_stmt|;
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
name|totalHits
operator|+=
name|msta
index|[
name|i
index|]
operator|.
name|hits
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// if one search produced an IOException, rethrow it
throw|throw
name|ioe
throw|;
block|}
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
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
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
name|float
name|maxScore
init|=
operator|(
name|totalHits
operator|==
literal|0
operator|)
condition|?
name|Float
operator|.
name|NEGATIVE_INFINITY
else|:
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
decl_stmt|;
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
comment|/**    * A search implementation allowing sorting which spans a new thread for each    * Searchable, waits for each search to complete and merges    * the results back together.    */
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
comment|// don't specify the fields - we'll wait to do this until we get results
name|FieldDocSortedHitQueue
name|hq
init|=
operator|new
name|FieldDocSortedHitQueue
argument_list|(
literal|null
argument_list|,
name|nDocs
argument_list|)
decl_stmt|;
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
name|MultiSearcherThread
index|[]
name|msta
init|=
operator|new
name|MultiSearcherThread
index|[
name|searchables
operator|.
name|length
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
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searchable
comment|// Assume not too many searchables and cost of creating a thread is by far inferior to a search
name|msta
index|[
name|i
index|]
operator|=
operator|new
name|MultiSearcherThread
argument_list|(
name|searchables
index|[
name|i
index|]
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|hq
argument_list|,
name|sort
argument_list|,
name|i
argument_list|,
name|starts
argument_list|,
literal|"MultiSearcher thread #"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|msta
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
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
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|msta
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// In 3.0 we will change this to throw
comment|// InterruptedException instead
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|IOException
name|ioe
init|=
name|msta
index|[
name|i
index|]
operator|.
name|getIOException
argument_list|()
decl_stmt|;
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
name|totalHits
operator|+=
name|msta
index|[
name|i
index|]
operator|.
name|hits
argument_list|()
expr_stmt|;
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxScore
argument_list|,
name|msta
index|[
name|i
index|]
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if one search produced an IOException, rethrow it
throw|throw
name|ioe
throw|;
block|}
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
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
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
operator|new
name|TopFieldDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|hq
operator|.
name|getFields
argument_list|()
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
comment|/** Lower-level search API.   *   *<p>{@link Collector#collect(int)} is called for every matching document.   *   *<p>Applications should only use this if they need<i>all</i> of the   * matching documents.  The high-level search API ({@link   * Searcher#search(Query)}) is usually more efficient, as it skips   * non-high-scoring hits.   *   * @param weight to match documents   * @param filter if non-null, a bitset used to eliminate some documents   * @param collector to receive hits   *    * TODO: parallelize this one too   */
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
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
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|start
init|=
name|starts
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Collector
name|hc
init|=
operator|new
name|Collector
argument_list|()
block|{
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
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
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|start
operator|+
name|docBase
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|searchables
index|[
name|i
index|]
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|hc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * TODO: this one could be parallelized too    * @see org.apache.lucene.search.Searchable#rewrite(org.apache.lucene.search.Query)    */
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|original
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * A thread subclass for searching a single searchable   */
end_comment

begin_class
DECL|class|MultiSearcherThread
class|class
name|MultiSearcherThread
extends|extends
name|Thread
block|{
DECL|field|searchable
specifier|private
name|Searchable
name|searchable
decl_stmt|;
DECL|field|weight
specifier|private
name|Weight
name|weight
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
decl_stmt|;
DECL|field|nDocs
specifier|private
name|int
name|nDocs
decl_stmt|;
DECL|field|docs
specifier|private
name|TopDocs
name|docs
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|;
DECL|field|hq
specifier|private
name|PriorityQueue
argument_list|<
name|?
extends|extends
name|ScoreDoc
argument_list|>
name|hq
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|ioe
specifier|private
name|IOException
name|ioe
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|method|MultiSearcherThread
specifier|public
name|MultiSearcherThread
parameter_list|(
name|Searchable
name|searchable
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|HitQueue
name|hq
parameter_list|,
name|int
name|i
parameter_list|,
name|int
index|[]
name|starts
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchable
operator|=
name|searchable
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|nDocs
operator|=
name|nDocs
expr_stmt|;
name|this
operator|.
name|hq
operator|=
name|hq
expr_stmt|;
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
block|}
DECL|method|MultiSearcherThread
specifier|public
name|MultiSearcherThread
parameter_list|(
name|Searchable
name|searchable
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|FieldDocSortedHitQueue
name|hq
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|i
parameter_list|,
name|int
index|[]
name|starts
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchable
operator|=
name|searchable
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|nDocs
operator|=
name|nDocs
expr_stmt|;
name|this
operator|.
name|hq
operator|=
name|hq
expr_stmt|;
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|docs
operator|=
operator|(
name|sort
operator|==
literal|null
operator|)
condition|?
name|searchable
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|)
else|:
name|searchable
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|sort
argument_list|)
expr_stmt|;
block|}
comment|// Store the IOException for later use by the caller of this thread
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|this
operator|.
name|ioe
operator|=
name|ioe
expr_stmt|;
block|}
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
name|TopFieldDocs
name|docsFields
init|=
operator|(
name|TopFieldDocs
operator|)
name|docs
decl_stmt|;
comment|// If one of the Sort fields is FIELD_DOC, need to fix its values, so that
comment|// it will break ties by doc Id properly. Otherwise, it will compare to
comment|// 'relative' doc Ids, that belong to two different searchables.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docsFields
operator|.
name|fields
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|docsFields
operator|.
name|fields
index|[
name|j
index|]
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|DOC
condition|)
block|{
comment|// iterate over the score docs and change their fields value
for|for
control|(
name|int
name|j2
init|=
literal|0
init|;
name|j2
operator|<
name|docs
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j2
operator|++
control|)
block|{
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|docs
operator|.
name|scoreDocs
index|[
name|j2
index|]
decl_stmt|;
name|fd
operator|.
name|fields
index|[
name|j
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|fd
operator|.
name|fields
index|[
name|j
index|]
operator|)
operator|.
name|intValue
argument_list|()
operator|+
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
operator|(
operator|(
name|FieldDocSortedHitQueue
operator|)
name|hq
operator|)
operator|.
name|setFields
argument_list|(
name|docsFields
operator|.
name|fields
argument_list|)
expr_stmt|;
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|docs
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// merge scoreDocs into hq
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|scoreDoc
operator|.
name|doc
operator|+=
name|starts
index|[
name|i
index|]
expr_stmt|;
comment|// convert doc
comment|//it would be so nice if we had a thread-safe insert
synchronized|synchronized
init|(
name|hq
init|)
block|{
comment|// this cast is bad, because we assume that the list has correct type.
comment|// Because of that we have the @SuppressWarnings :-(
if|if
condition|(
name|scoreDoc
operator|==
operator|(
operator|(
name|PriorityQueue
argument_list|<
name|ScoreDoc
argument_list|>
operator|)
name|hq
operator|)
operator|.
name|insertWithOverflow
argument_list|(
name|scoreDoc
argument_list|)
condition|)
break|break;
block|}
comment|// no more scores> minScore
block|}
block|}
block|}
DECL|method|hits
specifier|public
name|int
name|hits
parameter_list|()
block|{
return|return
name|docs
operator|.
name|totalHits
return|;
block|}
DECL|method|getMaxScore
specifier|public
name|float
name|getMaxScore
parameter_list|()
block|{
return|return
name|docs
operator|.
name|getMaxScore
argument_list|()
return|;
block|}
DECL|method|getIOException
specifier|public
name|IOException
name|getIOException
parameter_list|()
block|{
return|return
name|ioe
return|;
block|}
block|}
end_class

end_unit

