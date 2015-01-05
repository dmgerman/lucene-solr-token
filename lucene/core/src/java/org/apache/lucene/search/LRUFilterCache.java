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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Set
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
name|LeafReader
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
name|LeafReader
operator|.
name|CoreClosedListener
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
name|util
operator|.
name|Accountable
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
name|Accountables
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
name|RamUsageEstimator
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
name|RoaringDocIdSet
import|;
end_import

begin_comment
comment|/**  * A {@link FilterCache} that evicts filters using a LRU (least-recently-used)  * eviction policy in order to remain under a given maximum size and number of  * bytes used.  *  * This class is thread-safe.  *  * Note that filter eviction runs in linear time with the total number of  * segments that have cache entries so this cache works best with  * {@link FilterCachingPolicy caching policies} that only cache on "large"  * segments, and it is advised to not share this cache across too many indices.  *  * Typical usage looks like this:  *<pre class="prettyprint">  *   final int maxNumberOfCachedFilters = 256;  *   final long maxRamBytesUsed = 50 * 1024L * 1024L; // 50MB  *   // these cache and policy instances can be shared across several filters and readers  *   // it is fine to eg. store them into static variables  *   final FilterCache filterCache = new LRUFilterCache(maxNumberOfCachedFilters, maxRamBytesUsed);  *   final FilterCachingPolicy defaultCachingPolicy = new UsageTrackingFilterCachingPolicy();  *     *   // ...  *     *   // Then at search time  *   Filter myFilter = ...;  *   Filter myCacheFilter = filterCache.doCache(myFilter, defaultCachingPolicy);  *   // myCacheFilter is now a wrapper around the original filter that will interact with the cache  *   IndexSearcher searcher = ...;  *   TopDocs topDocs = searcher.search(new ConstantScoreQuery(myCacheFilter), 10);  *</pre>  *  * This cache exposes some global statistics ({@link #getHitCount() hit count},  * {@link #getMissCount() miss count}, {@link #getCacheSize() number of cache  * entries}, {@link #getCacheCount() total number of DocIdSets that have ever  * been cached}, {@link #getEvictionCount() number of evicted entries}). In  * case you would like to have more fine-grained statistics, such as per-index  * or per-filter-class statistics, it is possible to override various callbacks:  * {@link #onHit}, {@link #onMiss},  * {@link #onFilterCache}, {@link #onFilterEviction},  * {@link #onDocIdSetCache}, {@link #onDocIdSetEviction} and {@link #onClear}.  * It is better to not perform heavy computations in these methods though since  * they are called synchronously and under a lock.  *  * @see FilterCachingPolicy  * @lucene.experimental  */
end_comment

begin_class
DECL|class|LRUFilterCache
specifier|public
class|class
name|LRUFilterCache
implements|implements
name|FilterCache
implements|,
name|Accountable
block|{
comment|// memory usage of a simple query-wrapper filter around a term query
DECL|field|FILTER_DEFAULT_RAM_BYTES_USED
specifier|static
specifier|final
name|long
name|FILTER_DEFAULT_RAM_BYTES_USED
init|=
literal|216
decl_stmt|;
DECL|field|HASHTABLE_RAM_BYTES_PER_ENTRY
specifier|static
specifier|final
name|long
name|HASHTABLE_RAM_BYTES_PER_ENTRY
init|=
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
comment|// key + value
operator|*
literal|2
decl_stmt|;
comment|// hash tables need to be oversized to avoid collisions, assume 2x capacity
DECL|field|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
specifier|static
specifier|final
name|long
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
init|=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
comment|// previous& next references
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|maxRamBytesUsed
specifier|private
specifier|final
name|long
name|maxRamBytesUsed
decl_stmt|;
comment|// maps filters that are contained in the cache to a singleton so that this
comment|// cache does not store several copies of the same filter
DECL|field|uniqueFilters
specifier|private
specifier|final
name|Map
argument_list|<
name|Filter
argument_list|,
name|Filter
argument_list|>
name|uniqueFilters
decl_stmt|;
comment|// The contract between this set and the per-leaf caches is that per-leaf caches
comment|// are only allowed to store sub-sets of the filters that are contained in
comment|// mostRecentlyUsedFilters. This is why write operations are performed under a lock
DECL|field|mostRecentlyUsedFilters
specifier|private
specifier|final
name|Set
argument_list|<
name|Filter
argument_list|>
name|mostRecentlyUsedFilters
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|LeafCache
argument_list|>
name|cache
decl_stmt|;
comment|// these variables are volatile so that we do not need to sync reads
comment|// but increments need to be performed under the lock
DECL|field|ramBytesUsed
specifier|private
specifier|volatile
name|long
name|ramBytesUsed
decl_stmt|;
DECL|field|hitCount
specifier|private
specifier|volatile
name|long
name|hitCount
decl_stmt|;
DECL|field|missCount
specifier|private
specifier|volatile
name|long
name|missCount
decl_stmt|;
DECL|field|cacheCount
specifier|private
specifier|volatile
name|long
name|cacheCount
decl_stmt|;
DECL|field|cacheSize
specifier|private
specifier|volatile
name|long
name|cacheSize
decl_stmt|;
comment|/**    * Create a new instance that will cache at most<code>maxSize</code> filters    * with at most<code>maxRamBytesUsed</code> bytes of memory.    */
DECL|method|LRUFilterCache
specifier|public
name|LRUFilterCache
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|long
name|maxRamBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
name|this
operator|.
name|maxRamBytesUsed
operator|=
name|maxRamBytesUsed
expr_stmt|;
name|uniqueFilters
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|Filter
argument_list|,
name|Filter
argument_list|>
argument_list|(
literal|16
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mostRecentlyUsedFilters
operator|=
name|uniqueFilters
operator|.
name|keySet
argument_list|()
expr_stmt|;
name|cache
operator|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Expert: callback when there is a cache hit on a given filter.    * Implementing this method is typically useful in order to compute more    * fine-grained statistics about the filter cache.    * @see #onMiss    * @lucene.experimental    */
DECL|method|onHit
specifier|protected
name|void
name|onHit
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|hitCount
operator|+=
literal|1
expr_stmt|;
block|}
comment|/**    * Expert: callback when there is a cache miss on a given filter.    * @see #onHit    * @lucene.experimental    */
DECL|method|onMiss
specifier|protected
name|void
name|onMiss
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
assert|assert
name|filter
operator|!=
literal|null
assert|;
name|missCount
operator|+=
literal|1
expr_stmt|;
block|}
comment|/**    * Expert: callback when a filter is added to this cache.    * Implementing this method is typically useful in order to compute more    * fine-grained statistics about the filter cache.    * @see #onFilterEviction    * @lucene.experimental    */
DECL|method|onFilterCache
specifier|protected
name|void
name|onFilterCache
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|ramBytesUsed
operator|+=
name|ramBytesUsed
expr_stmt|;
block|}
comment|/**    * Expert: callback when a filter is evicted from this cache.    * @see #onFilterCache    * @lucene.experimental    */
DECL|method|onFilterEviction
specifier|protected
name|void
name|onFilterEviction
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|ramBytesUsed
operator|-=
name|ramBytesUsed
expr_stmt|;
block|}
comment|/**    * Expert: callback when a {@link DocIdSet} is added to this cache.    * Implementing this method is typically useful in order to compute more    * fine-grained statistics about the filter cache.    * @see #onDocIdSetEviction    * @lucene.experimental    */
DECL|method|onDocIdSetCache
specifier|protected
name|void
name|onDocIdSetCache
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
name|cacheSize
operator|+=
literal|1
expr_stmt|;
name|cacheCount
operator|+=
literal|1
expr_stmt|;
name|this
operator|.
name|ramBytesUsed
operator|+=
name|ramBytesUsed
expr_stmt|;
block|}
comment|/**    * Expert: callback when one or more {@link DocIdSet}s are removed from this    * cache.    * @see #onDocIdSetCache    * @lucene.experimental    */
DECL|method|onDocIdSetEviction
specifier|protected
name|void
name|onDocIdSetEviction
parameter_list|(
name|Object
name|readerCoreKey
parameter_list|,
name|int
name|numEntries
parameter_list|,
name|long
name|sumRamBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|ramBytesUsed
operator|-=
name|sumRamBytesUsed
expr_stmt|;
name|cacheSize
operator|-=
name|numEntries
expr_stmt|;
block|}
comment|/**    * Expert: callback when the cache is completely cleared.    * @lucene.experimental    */
DECL|method|onClear
specifier|protected
name|void
name|onClear
parameter_list|()
block|{
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
name|cacheSize
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Whether evictions are required. */
DECL|method|requiresEviction
name|boolean
name|requiresEviction
parameter_list|()
block|{
specifier|final
name|int
name|size
init|=
name|mostRecentlyUsedFilters
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|size
operator|>
name|maxSize
operator|||
name|ramBytesUsed
argument_list|()
operator|>
name|maxRamBytesUsed
return|;
block|}
block|}
DECL|method|get
specifier|synchronized
name|DocIdSet
name|get
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
block|{
specifier|final
name|Object
name|readerKey
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
specifier|final
name|LeafCache
name|leafCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|readerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafCache
operator|==
literal|null
condition|)
block|{
name|onMiss
argument_list|(
name|readerKey
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// this get call moves the filter to the most-recently-used position
specifier|final
name|Filter
name|singleton
init|=
name|uniqueFilters
operator|.
name|get
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|==
literal|null
condition|)
block|{
name|onMiss
argument_list|(
name|readerKey
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSet
name|cached
init|=
name|leafCache
operator|.
name|get
argument_list|(
name|singleton
argument_list|)
decl_stmt|;
if|if
condition|(
name|cached
operator|==
literal|null
condition|)
block|{
name|onMiss
argument_list|(
name|readerKey
argument_list|,
name|singleton
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|onHit
argument_list|(
name|readerKey
argument_list|,
name|singleton
argument_list|)
expr_stmt|;
block|}
return|return
name|cached
return|;
block|}
DECL|method|putIfAbsent
specifier|synchronized
name|void
name|putIfAbsent
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|,
name|DocIdSet
name|set
parameter_list|)
block|{
comment|// under a lock to make sure that mostRecentlyUsedFilters and cache remain sync'ed
assert|assert
name|set
operator|.
name|isCacheable
argument_list|()
assert|;
name|Filter
name|singleton
init|=
name|uniqueFilters
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|==
literal|null
condition|)
block|{
name|onFilterCache
argument_list|(
name|singleton
argument_list|,
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|ramBytesUsed
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filter
operator|=
name|singleton
expr_stmt|;
block|}
specifier|final
name|Object
name|key
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
name|LeafCache
name|leafCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafCache
operator|==
literal|null
condition|)
block|{
name|leafCache
operator|=
operator|new
name|LeafCache
argument_list|(
name|key
argument_list|)
expr_stmt|;
specifier|final
name|LeafCache
name|previous
init|=
name|cache
operator|.
name|put
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|leafCache
argument_list|)
decl_stmt|;
name|ramBytesUsed
operator|+=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
assert|assert
name|previous
operator|==
literal|null
assert|;
comment|// we just created a new leaf cache, need to register a close listener
name|context
operator|.
name|reader
argument_list|()
operator|.
name|addCoreClosedListener
argument_list|(
operator|new
name|CoreClosedListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|Object
name|ownerCoreCacheKey
parameter_list|)
block|{
name|clearCoreCacheKey
argument_list|(
name|ownerCoreCacheKey
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|leafCache
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|set
argument_list|)
expr_stmt|;
name|evictIfNecessary
argument_list|()
expr_stmt|;
block|}
DECL|method|evictIfNecessary
specifier|synchronized
name|void
name|evictIfNecessary
parameter_list|()
block|{
comment|// under a lock to make sure that mostRecentlyUsedFilters and cache keep sync'ed
if|if
condition|(
name|requiresEviction
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|Filter
argument_list|>
name|iterator
init|=
name|mostRecentlyUsedFilters
operator|.
name|iterator
argument_list|()
decl_stmt|;
do|do
block|{
specifier|final
name|Filter
name|filter
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|onEviction
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|requiresEviction
argument_list|()
condition|)
do|;
block|}
block|}
comment|/**    * Remove all cache entries for the given core cache key.    */
DECL|method|clearCoreCacheKey
specifier|public
specifier|synchronized
name|void
name|clearCoreCacheKey
parameter_list|(
name|Object
name|coreKey
parameter_list|)
block|{
specifier|final
name|LeafCache
name|leafCache
init|=
name|cache
operator|.
name|remove
argument_list|(
name|coreKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafCache
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|-=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
name|onDocIdSetEviction
argument_list|(
name|coreKey
argument_list|,
name|leafCache
operator|.
name|cache
operator|.
name|size
argument_list|()
argument_list|,
name|leafCache
operator|.
name|ramBytesUsed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove all cache entries for the given filter.    */
DECL|method|clearFilter
specifier|public
specifier|synchronized
name|void
name|clearFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
specifier|final
name|Filter
name|singleton
init|=
name|uniqueFilters
operator|.
name|remove
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
name|onEviction
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onEviction
specifier|private
name|void
name|onEviction
parameter_list|(
name|Filter
name|singleton
parameter_list|)
block|{
name|onFilterEviction
argument_list|(
name|singleton
argument_list|,
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|ramBytesUsed
argument_list|(
name|singleton
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|leafCache
operator|.
name|remove
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Clear the content of this cache.    */
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mostRecentlyUsedFilters
operator|.
name|clear
argument_list|()
expr_stmt|;
name|onClear
argument_list|()
expr_stmt|;
block|}
comment|// pkg-private for testing
DECL|method|assertConsistent
specifier|synchronized
name|void
name|assertConsistent
parameter_list|()
block|{
if|if
condition|(
name|requiresEviction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"requires evictions: size="
operator|+
name|mostRecentlyUsedFilters
operator|.
name|size
argument_list|()
operator|+
literal|", maxSize="
operator|+
name|maxSize
operator|+
literal|", ramBytesUsed="
operator|+
name|ramBytesUsed
argument_list|()
operator|+
literal|", maxRamBytesUsed="
operator|+
name|maxRamBytesUsed
argument_list|)
throw|;
block|}
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|Filter
argument_list|>
name|keys
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|keys
operator|.
name|addAll
argument_list|(
name|leafCache
operator|.
name|cache
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|keys
operator|.
name|removeAll
argument_list|(
name|mostRecentlyUsedFilters
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"One leaf cache contains more keys than the top-level cache: "
operator|+
name|keys
argument_list|)
throw|;
block|}
block|}
name|long
name|recomputedRamBytesUsed
init|=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|*
name|cache
operator|.
name|size
argument_list|()
operator|+
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
operator|*
name|uniqueFilters
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|Filter
name|filter
range|:
name|mostRecentlyUsedFilters
control|)
block|{
name|recomputedRamBytesUsed
operator|+=
name|ramBytesUsed
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|recomputedRamBytesUsed
operator|+=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|*
name|leafCache
operator|.
name|cache
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|DocIdSet
name|set
range|:
name|leafCache
operator|.
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|recomputedRamBytesUsed
operator|+=
name|set
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|recomputedRamBytesUsed
operator|!=
name|ramBytesUsed
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"ramBytesUsed mismatch : "
operator|+
name|ramBytesUsed
operator|+
literal|" != "
operator|+
name|recomputedRamBytesUsed
argument_list|)
throw|;
block|}
name|long
name|recomputedCacheSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|recomputedCacheSize
operator|+=
name|leafCache
operator|.
name|cache
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|recomputedCacheSize
operator|!=
name|getCacheSize
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"cacheSize mismatch : "
operator|+
name|getCacheSize
argument_list|()
operator|+
literal|" != "
operator|+
name|recomputedCacheSize
argument_list|)
throw|;
block|}
block|}
comment|// pkg-private for testing
comment|// return the list of cached filters in LRU order
DECL|method|cachedFilters
specifier|synchronized
name|List
argument_list|<
name|Filter
argument_list|>
name|cachedFilters
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|mostRecentlyUsedFilters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doCache
specifier|public
name|Filter
name|doCache
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|FilterCachingPolicy
name|policy
parameter_list|)
block|{
while|while
condition|(
name|filter
operator|instanceof
name|CachingWrapperFilter
condition|)
block|{
comment|// should we throw an exception instead?
name|filter
operator|=
operator|(
operator|(
name|CachingWrapperFilter
operator|)
name|filter
operator|)
operator|.
name|in
expr_stmt|;
block|}
return|return
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|,
name|policy
argument_list|)
return|;
block|}
comment|/**    *  Provide the DocIdSet to be cached, using the DocIdSet provided    *  by the wrapped Filter.<p>This implementation returns the given {@link DocIdSet},    *  if {@link DocIdSet#isCacheable} returns<code>true</code>, else it calls    *  {@link #cacheImpl(DocIdSetIterator, org.apache.lucene.index.LeafReader)}    *<p>Note: This method returns {@linkplain DocIdSet#EMPTY} if the given docIdSet    *  is<code>null</code> or if {@link DocIdSet#iterator()} return<code>null</code>. The empty    *  instance is use as a placeholder in the cache instead of the<code>null</code> value.    */
DECL|method|docIdSetToCache
specifier|protected
name|DocIdSet
name|docIdSetToCache
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docIdSet
operator|==
literal|null
operator|||
name|docIdSet
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
return|return
name|docIdSet
return|;
block|}
else|else
block|{
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|cacheImpl
argument_list|(
name|it
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"segment"
argument_list|,
name|cache
argument_list|)
return|;
block|}
block|}
comment|/**    * Return the number of bytes used by the given filter. The default    * implementation returns {@link Accountable#ramBytesUsed()} if the filter    * implements {@link Accountable} and<code>1024</code> otherwise.    */
DECL|method|ramBytesUsed
specifier|protected
name|long
name|ramBytesUsed
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|instanceof
name|Accountable
condition|)
block|{
return|return
operator|(
operator|(
name|Accountable
operator|)
name|filter
operator|)
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
return|return
name|FILTER_DEFAULT_RAM_BYTES_USED
return|;
block|}
comment|/**    * Default cache implementation: uses {@link RoaringDocIdSet}.    */
DECL|method|cacheImpl
specifier|protected
name|DocIdSet
name|cacheImpl
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|iterator
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Return the total number of times that a {@link Filter} has been looked up    * in this {@link FilterCache}. Note that this number is incremented once per    * segment so running a cached filter only once will increment this counter    * by the number of segments that are wrapped by the searcher.    * Note that by definition, {@link #getTotalCount()} is the sum of    * {@link #getHitCount()} and {@link #getMissCount()}.    * @see #getHitCount()    * @see #getMissCount()    */
DECL|method|getTotalCount
specifier|public
specifier|final
name|long
name|getTotalCount
parameter_list|()
block|{
return|return
name|getHitCount
argument_list|()
operator|+
name|getMissCount
argument_list|()
return|;
block|}
comment|/**    * Over the {@link #getTotalCount() total} number of times that a filter has    * been looked up, return how many times a cached {@link DocIdSet} has been    * found and returned.    * @see #getTotalCount()    * @see #getMissCount()    */
DECL|method|getHitCount
specifier|public
specifier|final
name|long
name|getHitCount
parameter_list|()
block|{
return|return
name|hitCount
return|;
block|}
comment|/**    * Over the {@link #getTotalCount() total} number of times that a filter has    * been looked up, return how many times this filter was not contained in the    * cache.    * @see #getTotalCount()    * @see #getHitCount()    */
DECL|method|getMissCount
specifier|public
specifier|final
name|long
name|getMissCount
parameter_list|()
block|{
return|return
name|missCount
return|;
block|}
comment|/**    * Return the total number of {@link DocIdSet}s which are currently stored    * in the cache.    * @see #getCacheCount()    * @see #getEvictionCount()    */
DECL|method|getCacheSize
specifier|public
specifier|final
name|long
name|getCacheSize
parameter_list|()
block|{
return|return
name|cacheSize
return|;
block|}
comment|/**    * Return the total number of cache entries that have been generated and put    * in the cache. It is highly desirable to have a {@link #getHitCount() hit    * count} that is much higher than the {@link #getCacheCount() cache count}    * as the opposite would indicate that the filter cache makes efforts in order    * to cache filters but then they do not get reused.    * @see #getCacheSize()    * @see #getEvictionCount()    */
DECL|method|getCacheCount
specifier|public
specifier|final
name|long
name|getCacheCount
parameter_list|()
block|{
return|return
name|cacheCount
return|;
block|}
comment|/**    * Return the number of cache entries that have been removed from the cache    * either in order to stay under the maximum configured size/ram usage, or    * because a segment has been closed. High numbers of evictions might mean    * that filters are not reused or that the {@link FilterCachingPolicy    * caching policy} caches too aggressively on NRT segments which get merged    * early.    * @see #getCacheCount()    * @see #getCacheSize()    */
DECL|method|getEvictionCount
specifier|public
specifier|final
name|long
name|getEvictionCount
parameter_list|()
block|{
return|return
name|getCacheCount
argument_list|()
operator|-
name|getCacheSize
argument_list|()
return|;
block|}
comment|// this class is not thread-safe, everything but ramBytesUsed needs to be called under a lock
DECL|class|LeafCache
specifier|private
class|class
name|LeafCache
implements|implements
name|Accountable
block|{
DECL|field|key
specifier|private
specifier|final
name|Object
name|key
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Filter
argument_list|,
name|DocIdSet
argument_list|>
name|cache
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|volatile
name|long
name|ramBytesUsed
decl_stmt|;
DECL|method|LeafCache
name|LeafCache
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|cache
operator|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|onDocIdSetCache
specifier|private
name|void
name|onDocIdSetCache
parameter_list|(
name|long
name|ramBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|ramBytesUsed
operator|+=
name|ramBytesUsed
expr_stmt|;
name|LRUFilterCache
operator|.
name|this
operator|.
name|onDocIdSetCache
argument_list|(
name|key
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|onDocIdSetEviction
specifier|private
name|void
name|onDocIdSetEviction
parameter_list|(
name|long
name|ramBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|ramBytesUsed
operator|-=
name|ramBytesUsed
expr_stmt|;
name|LRUFilterCache
operator|.
name|this
operator|.
name|onDocIdSetEviction
argument_list|(
name|key
argument_list|,
literal|1
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|get
name|DocIdSet
name|get
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|putIfAbsent
name|void
name|putIfAbsent
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|DocIdSet
name|set
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|set
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// the set was actually put
name|onDocIdSetCache
argument_list|(
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|set
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|remove
name|void
name|remove
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|DocIdSet
name|removed
init|=
name|cache
operator|.
name|remove
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|!=
literal|null
condition|)
block|{
name|onDocIdSetEviction
argument_list|(
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|removed
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
block|}
DECL|class|CachingWrapperFilter
specifier|private
class|class
name|CachingWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|in
specifier|private
specifier|final
name|Filter
name|in
decl_stmt|;
DECL|field|policy
specifier|private
specifier|final
name|FilterCachingPolicy
name|policy
decl_stmt|;
DECL|method|CachingWrapperFilter
name|CachingWrapperFilter
parameter_list|(
name|Filter
name|in
parameter_list|,
name|FilterCachingPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|.
name|ord
operator|==
literal|0
condition|)
block|{
name|policy
operator|.
name|onUse
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|DocIdSet
name|set
init|=
name|get
argument_list|(
name|in
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// do not apply acceptDocs yet, we want the cached filter to not take them into account
name|set
operator|=
name|in
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|policy
operator|.
name|shouldCache
argument_list|(
name|in
argument_list|,
name|context
argument_list|,
name|set
argument_list|)
condition|)
block|{
name|set
operator|=
name|docIdSetToCache
argument_list|(
name|set
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// null values are not supported
name|set
operator|=
name|DocIdSet
operator|.
name|EMPTY
expr_stmt|;
block|}
comment|// it might happen that another thread computed the same set in parallel
comment|// although this might incur some CPU overhead, it is probably better
comment|// this way than trying to lock and preventing other filters to be
comment|// computed at the same time?
name|putIfAbsent
argument_list|(
name|in
argument_list|,
name|context
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
operator|==
name|DocIdSet
operator|.
name|EMPTY
condition|?
literal|null
else|:
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|set
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|instanceof
name|CachingWrapperFilter
operator|&&
name|in
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|CachingWrapperFilter
operator|)
name|obj
operator|)
operator|.
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|in
operator|.
name|hashCode
argument_list|()
operator|^
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

