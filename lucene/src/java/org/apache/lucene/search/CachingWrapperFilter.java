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
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|SoftReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|SegmentReader
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
name|FixedBitSet
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
name|WeakIdentityHashMap
import|;
end_import

begin_comment
comment|/**  * Wraps another filter's result and caches it.  The purpose is to allow  * filters to simply filter, and then wrap with this class  * to add caching.  */
end_comment

begin_class
DECL|class|CachingWrapperFilter
specifier|public
class|class
name|CachingWrapperFilter
extends|extends
name|Filter
block|{
comment|// TODO: make this filter aware of ReaderContext. a cached filter could
comment|// specify the actual readers key or something similar to indicate on which
comment|// level of the readers hierarchy it should be cached.
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|FilterCache
name|cache
init|=
operator|new
name|FilterCache
argument_list|()
decl_stmt|;
DECL|field|recacheDeletes
specifier|private
specifier|final
name|boolean
name|recacheDeletes
decl_stmt|;
DECL|class|FilterCache
specifier|private
specifier|static
class|class
name|FilterCache
implements|implements
name|SegmentReader
operator|.
name|CoreClosedListener
implements|,
name|IndexReader
operator|.
name|ReaderClosedListener
block|{
DECL|field|cache
specifier|private
specifier|final
name|WeakHashMap
argument_list|<
name|Object
argument_list|,
name|WeakIdentityHashMap
argument_list|<
name|Bits
argument_list|,
name|SoftReference
argument_list|<
name|DocIdSet
argument_list|>
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|WeakHashMap
argument_list|<
name|Object
argument_list|,
name|WeakIdentityHashMap
argument_list|<
name|Bits
argument_list|,
name|SoftReference
argument_list|<
name|DocIdSet
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|get
specifier|public
specifier|synchronized
name|DocIdSet
name|get
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Object
name|coreKey
init|=
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
name|WeakIdentityHashMap
argument_list|<
name|Bits
argument_list|,
name|SoftReference
argument_list|<
name|DocIdSet
argument_list|>
argument_list|>
name|innerCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|coreKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerCache
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|SegmentReader
condition|)
block|{
operator|(
operator|(
name|SegmentReader
operator|)
name|reader
operator|)
operator|.
name|addCoreClosedListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
operator|==
literal|null
operator|:
literal|"we only operate on AtomicContext, so all cached readers must be atomic"
assert|;
name|reader
operator|.
name|addReaderClosedListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|innerCache
operator|=
operator|new
name|WeakIdentityHashMap
argument_list|<
name|Bits
argument_list|,
name|SoftReference
argument_list|<
name|DocIdSet
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|coreKey
argument_list|,
name|innerCache
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SoftReference
argument_list|<
name|DocIdSet
argument_list|>
name|innerRef
init|=
name|innerCache
operator|.
name|get
argument_list|(
name|acceptDocs
argument_list|)
decl_stmt|;
return|return
name|innerRef
operator|==
literal|null
condition|?
literal|null
else|:
name|innerRef
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|put
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|DocIdSet
name|value
parameter_list|)
block|{
name|cache
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|acceptDocs
argument_list|,
operator|new
name|SoftReference
argument_list|<
name|DocIdSet
argument_list|>
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
specifier|synchronized
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|cache
operator|.
name|remove
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
specifier|synchronized
name|void
name|onClose
parameter_list|(
name|SegmentReader
name|reader
parameter_list|)
block|{
name|cache
operator|.
name|remove
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Wraps another filter's result and caches it.    * @param filter Filter to cache results of    */
DECL|method|CachingWrapperFilter
specifier|public
name|CachingWrapperFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
argument_list|(
name|filter
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Wraps another filter's result and caches it.  If    *  recacheDeletes is true, then new deletes (for example    *  after {@link IndexReader#openIfChanged}) will be AND'd    *  and cached again.    *    *  @param filter Filter to cache results of    */
DECL|method|CachingWrapperFilter
specifier|public
name|CachingWrapperFilter
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|boolean
name|recacheDeletes
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|recacheDeletes
operator|=
name|recacheDeletes
expr_stmt|;
block|}
comment|/** Provide the DocIdSet to be cached, using the DocIdSet provided    *  by the wrapped Filter.    *<p>This implementation returns the given {@link DocIdSet}, if {@link DocIdSet#isCacheable}    *  returns<code>true</code>, else it copies the {@link DocIdSetIterator} into    *  an {@link FixedBitSet}.    */
DECL|method|docIdSetToCache
specifier|protected
name|DocIdSet
name|docIdSetToCache
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|,
name|IndexReader
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
condition|)
block|{
comment|// this is better than returning null, as the nonnull result can be cached
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
elseif|else
if|if
condition|(
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
comment|// null is allowed to be returned by iterator(),
comment|// in this case we wrap with the empty set,
comment|// which is cacheable.
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
else|else
block|{
specifier|final
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|bits
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
return|return
name|bits
return|;
block|}
block|}
block|}
comment|// for testing
DECL|field|hitCount
DECL|field|missCount
name|int
name|hitCount
decl_stmt|,
name|missCount
decl_stmt|;
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
name|reader
init|=
name|context
operator|.
name|reader
decl_stmt|;
comment|// Only cache if incoming acceptDocs is == live docs;
comment|// if Lucene passes in more interesting acceptDocs in
comment|// the future we don't want to over-cache:
specifier|final
name|boolean
name|doCacheSubAcceptDocs
init|=
name|recacheDeletes
operator|&&
name|acceptDocs
operator|==
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|subAcceptDocs
decl_stmt|;
if|if
condition|(
name|doCacheSubAcceptDocs
condition|)
block|{
name|subAcceptDocs
operator|=
name|acceptDocs
expr_stmt|;
block|}
else|else
block|{
name|subAcceptDocs
operator|=
literal|null
expr_stmt|;
block|}
name|DocIdSet
name|docIdSet
init|=
name|cache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
name|subAcceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|!=
literal|null
condition|)
block|{
name|hitCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|missCount
operator|++
expr_stmt|;
name|docIdSet
operator|=
name|docIdSetToCache
argument_list|(
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|subAcceptDocs
argument_list|)
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|subAcceptDocs
argument_list|,
name|docIdSet
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doCacheSubAcceptDocs
condition|)
block|{
return|return
name|docIdSet
return|;
block|}
else|else
block|{
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|docIdSet
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CachingWrapperFilter("
operator|+
name|filter
operator|+
literal|",recacheDeletes="
operator|+
name|recacheDeletes
operator|+
literal|")"
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|CachingWrapperFilter
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|CachingWrapperFilter
name|other
init|=
operator|(
name|CachingWrapperFilter
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|filter
argument_list|)
operator|&&
name|this
operator|.
name|recacheDeletes
operator|==
name|other
operator|.
name|recacheDeletes
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
operator|(
name|filter
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x1117BF25
operator|)
operator|+
operator|(
name|recacheDeletes
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
block|}
end_class

end_unit

