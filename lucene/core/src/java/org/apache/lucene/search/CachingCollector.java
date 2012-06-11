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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AtomicReaderContext
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
name|RamUsageEstimator
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

begin_comment
comment|/**  * Caches all docs, and optionally also scores, coming from  * a search, and is then able to replay them to another  * collector.  You specify the max RAM this class may use.  * Once the collection is done, call {@link #isCached}. If  * this returns true, you can use {@link #replay(Collector)}  * against a new collector.  If it returns false, this means  * too much RAM was required and you must instead re-run the  * original search.  *  *<p><b>NOTE</b>: this class consumes 4 (or 8 bytes, if  * scoring is cached) per collected document.  If the result  * set is large this can easily be a very substantial amount  * of RAM!  *   *<p><b>NOTE</b>: this class caches at least 128 documents  * before checking RAM limits.  *   *<p>See the Lucene<tt>modules/grouping</tt> module for more  * details including a full code example.</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CachingCollector
specifier|public
specifier|abstract
class|class
name|CachingCollector
extends|extends
name|Collector
block|{
comment|// Max out at 512K arrays
DECL|field|MAX_ARRAY_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ARRAY_SIZE
init|=
literal|512
operator|*
literal|1024
decl_stmt|;
DECL|field|INITIAL_ARRAY_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_ARRAY_SIZE
init|=
literal|128
decl_stmt|;
DECL|field|EMPTY_INT_ARRAY
specifier|private
specifier|final
specifier|static
name|int
index|[]
name|EMPTY_INT_ARRAY
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|class|SegStart
specifier|private
specifier|static
class|class
name|SegStart
block|{
DECL|field|readerContext
specifier|public
specifier|final
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|end
specifier|public
specifier|final
name|int
name|end
decl_stmt|;
DECL|method|SegStart
specifier|public
name|SegStart
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
block|}
DECL|class|CachedScorer
specifier|private
specifier|static
specifier|final
class|class
name|CachedScorer
extends|extends
name|Scorer
block|{
comment|// NOTE: these members are package-private b/c that way accessing them from
comment|// the outer class does not incur access check by the JVM. The same
comment|// situation would be if they were defined in the outer class as private
comment|// members.
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|method|CachedScorer
specifier|private
name|CachedScorer
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|()
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|float
name|freq
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|// A CachingCollector which caches scores
DECL|class|ScoreCachingCollector
specifier|private
specifier|static
specifier|final
class|class
name|ScoreCachingCollector
extends|extends
name|CachingCollector
block|{
DECL|field|cachedScorer
specifier|private
specifier|final
name|CachedScorer
name|cachedScorer
decl_stmt|;
DECL|field|cachedScores
specifier|private
specifier|final
name|List
argument_list|<
name|float
index|[]
argument_list|>
name|cachedScores
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|curScores
specifier|private
name|float
index|[]
name|curScores
decl_stmt|;
DECL|method|ScoreCachingCollector
name|ScoreCachingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|double
name|maxRAMMB
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|,
name|maxRAMMB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cachedScorer
operator|=
operator|new
name|CachedScorer
argument_list|()
expr_stmt|;
name|cachedScores
operator|=
operator|new
name|ArrayList
argument_list|<
name|float
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|curScores
operator|=
operator|new
name|float
index|[
name|INITIAL_ARRAY_SIZE
index|]
expr_stmt|;
name|cachedScores
operator|.
name|add
argument_list|(
name|curScores
argument_list|)
expr_stmt|;
block|}
DECL|method|ScoreCachingCollector
name|ScoreCachingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|,
name|maxDocsToCache
argument_list|)
expr_stmt|;
name|cachedScorer
operator|=
operator|new
name|CachedScorer
argument_list|()
expr_stmt|;
name|cachedScores
operator|=
operator|new
name|ArrayList
argument_list|<
name|float
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|curScores
operator|=
operator|new
name|float
index|[
name|INITIAL_ARRAY_SIZE
index|]
expr_stmt|;
name|cachedScores
operator|.
name|add
argument_list|(
name|curScores
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|curDocs
operator|==
literal|null
condition|)
block|{
comment|// Cache was too large
name|cachedScorer
operator|.
name|score
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|cachedScorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Allocate a bigger array or abort caching
if|if
condition|(
name|upto
operator|==
name|curDocs
operator|.
name|length
condition|)
block|{
name|base
operator|+=
name|upto
expr_stmt|;
comment|// Compute next array length - don't allocate too big arrays
name|int
name|nextLength
init|=
literal|8
operator|*
name|curDocs
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|nextLength
operator|>
name|MAX_ARRAY_SIZE
condition|)
block|{
name|nextLength
operator|=
name|MAX_ARRAY_SIZE
expr_stmt|;
block|}
if|if
condition|(
name|base
operator|+
name|nextLength
operator|>
name|maxDocsToCache
condition|)
block|{
comment|// try to allocate a smaller array
name|nextLength
operator|=
name|maxDocsToCache
operator|-
name|base
expr_stmt|;
if|if
condition|(
name|nextLength
operator|<=
literal|0
condition|)
block|{
comment|// Too many docs to collect -- clear cache
name|curDocs
operator|=
literal|null
expr_stmt|;
name|curScores
operator|=
literal|null
expr_stmt|;
name|cachedSegs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cachedDocs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cachedScores
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cachedScorer
operator|.
name|score
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|cachedScorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|curDocs
operator|=
operator|new
name|int
index|[
name|nextLength
index|]
expr_stmt|;
name|cachedDocs
operator|.
name|add
argument_list|(
name|curDocs
argument_list|)
expr_stmt|;
name|curScores
operator|=
operator|new
name|float
index|[
name|nextLength
index|]
expr_stmt|;
name|cachedScores
operator|.
name|add
argument_list|(
name|curScores
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
name|curDocs
index|[
name|upto
index|]
operator|=
name|doc
expr_stmt|;
name|cachedScorer
operator|.
name|score
operator|=
name|curScores
index|[
name|upto
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|upto
operator|++
expr_stmt|;
name|cachedScorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replay
specifier|public
name|void
name|replay
parameter_list|(
name|Collector
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|replayInit
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|int
name|curUpto
init|=
literal|0
decl_stmt|;
name|int
name|curBase
init|=
literal|0
decl_stmt|;
name|int
name|chunkUpto
init|=
literal|0
decl_stmt|;
name|curDocs
operator|=
name|EMPTY_INT_ARRAY
expr_stmt|;
for|for
control|(
name|SegStart
name|seg
range|:
name|cachedSegs
control|)
block|{
name|other
operator|.
name|setNextReader
argument_list|(
name|seg
operator|.
name|readerContext
argument_list|)
expr_stmt|;
name|other
operator|.
name|setScorer
argument_list|(
name|cachedScorer
argument_list|)
expr_stmt|;
while|while
condition|(
name|curBase
operator|+
name|curUpto
operator|<
name|seg
operator|.
name|end
condition|)
block|{
if|if
condition|(
name|curUpto
operator|==
name|curDocs
operator|.
name|length
condition|)
block|{
name|curBase
operator|+=
name|curDocs
operator|.
name|length
expr_stmt|;
name|curDocs
operator|=
name|cachedDocs
operator|.
name|get
argument_list|(
name|chunkUpto
argument_list|)
expr_stmt|;
name|curScores
operator|=
name|cachedScores
operator|.
name|get
argument_list|(
name|chunkUpto
argument_list|)
expr_stmt|;
name|chunkUpto
operator|++
expr_stmt|;
name|curUpto
operator|=
literal|0
expr_stmt|;
block|}
name|cachedScorer
operator|.
name|score
operator|=
name|curScores
index|[
name|curUpto
index|]
expr_stmt|;
name|cachedScorer
operator|.
name|doc
operator|=
name|curDocs
index|[
name|curUpto
index|]
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|curDocs
index|[
name|curUpto
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|setScorer
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
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|other
operator|.
name|setScorer
argument_list|(
name|cachedScorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|isCached
argument_list|()
condition|)
block|{
return|return
literal|"CachingCollector ("
operator|+
operator|(
name|base
operator|+
name|upto
operator|)
operator|+
literal|" docs& scores cached)"
return|;
block|}
else|else
block|{
return|return
literal|"CachingCollector (cache was cleared)"
return|;
block|}
block|}
block|}
comment|// A CachingCollector which does not cache scores
DECL|class|NoScoreCachingCollector
specifier|private
specifier|static
specifier|final
class|class
name|NoScoreCachingCollector
extends|extends
name|CachingCollector
block|{
DECL|method|NoScoreCachingCollector
name|NoScoreCachingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|double
name|maxRAMMB
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|,
name|maxRAMMB
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|NoScoreCachingCollector
name|NoScoreCachingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|,
name|maxDocsToCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|curDocs
operator|==
literal|null
condition|)
block|{
comment|// Cache was too large
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Allocate a bigger array or abort caching
if|if
condition|(
name|upto
operator|==
name|curDocs
operator|.
name|length
condition|)
block|{
name|base
operator|+=
name|upto
expr_stmt|;
comment|// Compute next array length - don't allocate too big arrays
name|int
name|nextLength
init|=
literal|8
operator|*
name|curDocs
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|nextLength
operator|>
name|MAX_ARRAY_SIZE
condition|)
block|{
name|nextLength
operator|=
name|MAX_ARRAY_SIZE
expr_stmt|;
block|}
if|if
condition|(
name|base
operator|+
name|nextLength
operator|>
name|maxDocsToCache
condition|)
block|{
comment|// try to allocate a smaller array
name|nextLength
operator|=
name|maxDocsToCache
operator|-
name|base
expr_stmt|;
if|if
condition|(
name|nextLength
operator|<=
literal|0
condition|)
block|{
comment|// Too many docs to collect -- clear cache
name|curDocs
operator|=
literal|null
expr_stmt|;
name|cachedSegs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cachedDocs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|curDocs
operator|=
operator|new
name|int
index|[
name|nextLength
index|]
expr_stmt|;
name|cachedDocs
operator|.
name|add
argument_list|(
name|curDocs
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
name|curDocs
index|[
name|upto
index|]
operator|=
name|doc
expr_stmt|;
name|upto
operator|++
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replay
specifier|public
name|void
name|replay
parameter_list|(
name|Collector
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|replayInit
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|int
name|curUpto
init|=
literal|0
decl_stmt|;
name|int
name|curbase
init|=
literal|0
decl_stmt|;
name|int
name|chunkUpto
init|=
literal|0
decl_stmt|;
name|curDocs
operator|=
name|EMPTY_INT_ARRAY
expr_stmt|;
for|for
control|(
name|SegStart
name|seg
range|:
name|cachedSegs
control|)
block|{
name|other
operator|.
name|setNextReader
argument_list|(
name|seg
operator|.
name|readerContext
argument_list|)
expr_stmt|;
while|while
condition|(
name|curbase
operator|+
name|curUpto
operator|<
name|seg
operator|.
name|end
condition|)
block|{
if|if
condition|(
name|curUpto
operator|==
name|curDocs
operator|.
name|length
condition|)
block|{
name|curbase
operator|+=
name|curDocs
operator|.
name|length
expr_stmt|;
name|curDocs
operator|=
name|cachedDocs
operator|.
name|get
argument_list|(
name|chunkUpto
argument_list|)
expr_stmt|;
name|chunkUpto
operator|++
expr_stmt|;
name|curUpto
operator|=
literal|0
expr_stmt|;
block|}
name|other
operator|.
name|collect
argument_list|(
name|curDocs
index|[
name|curUpto
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|setScorer
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
name|other
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|isCached
argument_list|()
condition|)
block|{
return|return
literal|"CachingCollector ("
operator|+
operator|(
name|base
operator|+
name|upto
operator|)
operator|+
literal|" docs cached)"
return|;
block|}
else|else
block|{
return|return
literal|"CachingCollector (cache was cleared)"
return|;
block|}
block|}
block|}
comment|// TODO: would be nice if a collector defined a
comment|// needsScores() method so we can specialize / do checks
comment|// up front. This is only relevant for the ScoreCaching
comment|// version -- if the wrapped Collector does not need
comment|// scores, it can avoid cachedScorer entirely.
DECL|field|other
specifier|protected
specifier|final
name|Collector
name|other
decl_stmt|;
DECL|field|maxDocsToCache
specifier|protected
specifier|final
name|int
name|maxDocsToCache
decl_stmt|;
DECL|field|cachedSegs
specifier|protected
specifier|final
name|List
argument_list|<
name|SegStart
argument_list|>
name|cachedSegs
init|=
operator|new
name|ArrayList
argument_list|<
name|SegStart
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|cachedDocs
specifier|protected
specifier|final
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|cachedDocs
decl_stmt|;
DECL|field|lastReaderContext
specifier|private
name|AtomicReaderContext
name|lastReaderContext
decl_stmt|;
DECL|field|curDocs
specifier|protected
name|int
index|[]
name|curDocs
decl_stmt|;
DECL|field|upto
specifier|protected
name|int
name|upto
decl_stmt|;
DECL|field|base
specifier|protected
name|int
name|base
decl_stmt|;
DECL|field|lastDocBase
specifier|protected
name|int
name|lastDocBase
decl_stmt|;
comment|/**    * Creates a {@link CachingCollector} which does not wrap another collector.    * The cached documents and scores can later be {@link #replay(Collector)    * replayed}.    *     * @param acceptDocsOutOfOrder    *          whether documents are allowed to be collected out-of-order    */
DECL|method|create
specifier|public
specifier|static
name|CachingCollector
name|create
parameter_list|(
specifier|final
name|boolean
name|acceptDocsOutOfOrder
parameter_list|,
name|boolean
name|cacheScores
parameter_list|,
name|double
name|maxRAMMB
parameter_list|)
block|{
name|Collector
name|other
init|=
operator|new
name|Collector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|acceptDocsOutOfOrder
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
decl_stmt|;
return|return
name|create
argument_list|(
name|other
argument_list|,
name|cacheScores
argument_list|,
name|maxRAMMB
argument_list|)
return|;
block|}
comment|/**    * Create a new {@link CachingCollector} that wraps the given collector and    * caches documents and scores up to the specified RAM threshold.    *     * @param other    *          the Collector to wrap and delegate calls to.    * @param cacheScores    *          whether to cache scores in addition to document IDs. Note that    *          this increases the RAM consumed per doc    * @param maxRAMMB    *          the maximum RAM in MB to consume for caching the documents and    *          scores. If the collector exceeds the threshold, no documents and    *          scores are cached.    */
DECL|method|create
specifier|public
specifier|static
name|CachingCollector
name|create
parameter_list|(
name|Collector
name|other
parameter_list|,
name|boolean
name|cacheScores
parameter_list|,
name|double
name|maxRAMMB
parameter_list|)
block|{
return|return
name|cacheScores
condition|?
operator|new
name|ScoreCachingCollector
argument_list|(
name|other
argument_list|,
name|maxRAMMB
argument_list|)
else|:
operator|new
name|NoScoreCachingCollector
argument_list|(
name|other
argument_list|,
name|maxRAMMB
argument_list|)
return|;
block|}
comment|/**    * Create a new {@link CachingCollector} that wraps the given collector and    * caches documents and scores up to the specified max docs threshold.    *    * @param other    *          the Collector to wrap and delegate calls to.    * @param cacheScores    *          whether to cache scores in addition to document IDs. Note that    *          this increases the RAM consumed per doc    * @param maxDocsToCache    *          the maximum number of documents for caching the documents and    *          possible the scores. If the collector exceeds the threshold,    *          no documents and scores are cached.    */
DECL|method|create
specifier|public
specifier|static
name|CachingCollector
name|create
parameter_list|(
name|Collector
name|other
parameter_list|,
name|boolean
name|cacheScores
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
return|return
name|cacheScores
condition|?
operator|new
name|ScoreCachingCollector
argument_list|(
name|other
argument_list|,
name|maxDocsToCache
argument_list|)
else|:
operator|new
name|NoScoreCachingCollector
argument_list|(
name|other
argument_list|,
name|maxDocsToCache
argument_list|)
return|;
block|}
comment|// Prevent extension from non-internal classes
DECL|method|CachingCollector
specifier|private
name|CachingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|double
name|maxRAMMB
parameter_list|,
name|boolean
name|cacheScores
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
name|cachedDocs
operator|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|curDocs
operator|=
operator|new
name|int
index|[
name|INITIAL_ARRAY_SIZE
index|]
expr_stmt|;
name|cachedDocs
operator|.
name|add
argument_list|(
name|curDocs
argument_list|)
expr_stmt|;
name|int
name|bytesPerDoc
init|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
if|if
condition|(
name|cacheScores
condition|)
block|{
name|bytesPerDoc
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
expr_stmt|;
block|}
name|maxDocsToCache
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|maxRAMMB
operator|*
literal|1024
operator|*
literal|1024
operator|)
operator|/
name|bytesPerDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|CachingCollector
specifier|private
name|CachingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
name|cachedDocs
operator|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|curDocs
operator|=
operator|new
name|int
index|[
name|INITIAL_ARRAY_SIZE
index|]
expr_stmt|;
name|cachedDocs
operator|.
name|add
argument_list|(
name|curDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDocsToCache
operator|=
name|maxDocsToCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|other
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
DECL|method|isCached
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
return|return
name|curDocs
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|other
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastReaderContext
operator|!=
literal|null
condition|)
block|{
name|cachedSegs
operator|.
name|add
argument_list|(
operator|new
name|SegStart
argument_list|(
name|lastReaderContext
argument_list|,
name|base
operator|+
name|upto
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastReaderContext
operator|=
name|context
expr_stmt|;
block|}
comment|/** Reused by the specialized inner classes. */
DECL|method|replayInit
name|void
name|replayInit
parameter_list|(
name|Collector
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isCached
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot replay: cache was cleared because too much RAM was required"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|other
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
operator|&&
name|this
operator|.
name|other
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot replay: given collector does not support "
operator|+
literal|"out-of-order collection, while the wrapped collector does. "
operator|+
literal|"Therefore cached documents may be out-of-order."
argument_list|)
throw|;
block|}
comment|//System.out.println("CC: replay totHits=" + (upto + base));
if|if
condition|(
name|lastReaderContext
operator|!=
literal|null
condition|)
block|{
name|cachedSegs
operator|.
name|add
argument_list|(
operator|new
name|SegStart
argument_list|(
name|lastReaderContext
argument_list|,
name|base
operator|+
name|upto
argument_list|)
argument_list|)
expr_stmt|;
name|lastReaderContext
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Replays the cached doc IDs (and scores) to the given Collector. If this    * instance does not cache scores, then Scorer is not set on    * {@code other.setScorer} as well as scores are not replayed.    *     * @throws IllegalStateException    *           if this collector is not cached (i.e., if the RAM limits were too    *           low for the number of documents + scores to cache).    * @throws IllegalArgumentException    *           if the given Collect's does not support out-of-order collection,    *           while the collector passed to the ctor does.    */
DECL|method|replay
specifier|public
specifier|abstract
name|void
name|replay
parameter_list|(
name|Collector
name|other
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

