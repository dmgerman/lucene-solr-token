begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|ConcurrentMap
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|concurrentlinkedhashmap
operator|.
name|ConcurrentLinkedHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|concurrentlinkedhashmap
operator|.
name|EvictionListener
import|;
end_import

begin_class
DECL|class|BlockCache
specifier|public
class|class
name|BlockCache
block|{
DECL|field|_128M
specifier|public
specifier|static
specifier|final
name|int
name|_128M
init|=
literal|134217728
decl_stmt|;
DECL|field|_32K
specifier|public
specifier|static
specifier|final
name|int
name|_32K
init|=
literal|32768
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|BlockCacheKey
argument_list|,
name|BlockCacheLocation
argument_list|>
name|cache
decl_stmt|;
DECL|field|banks
specifier|private
specifier|final
name|ByteBuffer
index|[]
name|banks
decl_stmt|;
DECL|field|locks
specifier|private
specifier|final
name|BlockLocks
index|[]
name|locks
decl_stmt|;
DECL|field|lockCounters
specifier|private
specifier|final
name|AtomicInteger
index|[]
name|lockCounters
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|numberOfBlocksPerBank
specifier|private
specifier|final
name|int
name|numberOfBlocksPerBank
decl_stmt|;
DECL|field|maxEntries
specifier|private
specifier|final
name|int
name|maxEntries
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|Metrics
name|metrics
decl_stmt|;
DECL|method|BlockCache
specifier|public
name|BlockCache
parameter_list|(
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|long
name|totalMemory
parameter_list|)
block|{
name|this
argument_list|(
name|metrics
argument_list|,
name|directAllocation
argument_list|,
name|totalMemory
argument_list|,
name|_128M
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockCache
specifier|public
name|BlockCache
parameter_list|(
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|long
name|totalMemory
parameter_list|,
name|int
name|slabSize
parameter_list|)
block|{
name|this
argument_list|(
name|metrics
argument_list|,
name|directAllocation
argument_list|,
name|totalMemory
argument_list|,
name|slabSize
argument_list|,
name|_32K
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockCache
specifier|public
name|BlockCache
parameter_list|(
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|long
name|totalMemory
parameter_list|,
name|int
name|slabSize
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|numberOfBlocksPerBank
operator|=
name|slabSize
operator|/
name|blockSize
expr_stmt|;
name|int
name|numberOfBanks
init|=
call|(
name|int
call|)
argument_list|(
name|totalMemory
operator|/
name|slabSize
argument_list|)
decl_stmt|;
name|banks
operator|=
operator|new
name|ByteBuffer
index|[
name|numberOfBanks
index|]
expr_stmt|;
name|locks
operator|=
operator|new
name|BlockLocks
index|[
name|numberOfBanks
index|]
expr_stmt|;
name|lockCounters
operator|=
operator|new
name|AtomicInteger
index|[
name|numberOfBanks
index|]
expr_stmt|;
name|maxEntries
operator|=
operator|(
name|numberOfBlocksPerBank
operator|*
name|numberOfBanks
operator|)
operator|-
literal|1
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
name|numberOfBanks
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|directAllocation
condition|)
block|{
name|banks
index|[
name|i
index|]
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|numberOfBlocksPerBank
operator|*
name|blockSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|banks
index|[
name|i
index|]
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|numberOfBlocksPerBank
operator|*
name|blockSize
argument_list|)
expr_stmt|;
block|}
name|locks
index|[
name|i
index|]
operator|=
operator|new
name|BlockLocks
argument_list|(
name|numberOfBlocksPerBank
argument_list|)
expr_stmt|;
name|lockCounters
index|[
name|i
index|]
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
block|}
name|EvictionListener
argument_list|<
name|BlockCacheKey
argument_list|,
name|BlockCacheLocation
argument_list|>
name|listener
init|=
operator|new
name|EvictionListener
argument_list|<
name|BlockCacheKey
argument_list|,
name|BlockCacheLocation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onEviction
parameter_list|(
name|BlockCacheKey
name|key
parameter_list|,
name|BlockCacheLocation
name|location
parameter_list|)
block|{
name|releaseLocation
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|cache
operator|=
operator|new
name|ConcurrentLinkedHashMap
operator|.
name|Builder
argument_list|<
name|BlockCacheKey
argument_list|,
name|BlockCacheLocation
argument_list|>
argument_list|()
operator|.
name|maximumWeightedCapacity
argument_list|(
name|maxEntries
argument_list|)
operator|.
name|listener
argument_list|(
name|listener
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
DECL|method|releaseLocation
specifier|private
name|void
name|releaseLocation
parameter_list|(
name|BlockCacheLocation
name|location
parameter_list|)
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|bankId
init|=
name|location
operator|.
name|getBankId
argument_list|()
decl_stmt|;
name|int
name|block
init|=
name|location
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|location
operator|.
name|setRemoved
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|locks
index|[
name|bankId
index|]
operator|.
name|clear
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|lockCounters
index|[
name|bankId
index|]
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|blockCacheEviction
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|blockCacheSize
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|+
name|blockOffset
operator|>
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Buffer size exceeded, expecting max ["
operator|+
name|blockSize
operator|+
literal|"] got length ["
operator|+
name|length
operator|+
literal|"] with blockOffset ["
operator|+
name|blockOffset
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|BlockCacheLocation
name|location
init|=
name|cache
operator|.
name|get
argument_list|(
name|blockCacheKey
argument_list|)
decl_stmt|;
name|boolean
name|newLocation
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|newLocation
operator|=
literal|true
expr_stmt|;
name|location
operator|=
operator|new
name|BlockCacheLocation
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|findEmptyLocation
argument_list|(
name|location
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|location
operator|.
name|isRemoved
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|bankId
init|=
name|location
operator|.
name|getBankId
argument_list|()
decl_stmt|;
name|int
name|bankOffset
init|=
name|location
operator|.
name|getBlock
argument_list|()
operator|*
name|blockSize
decl_stmt|;
name|ByteBuffer
name|bank
init|=
name|getBank
argument_list|(
name|bankId
argument_list|)
decl_stmt|;
name|bank
operator|.
name|position
argument_list|(
name|bankOffset
operator|+
name|blockOffset
argument_list|)
expr_stmt|;
name|bank
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|newLocation
condition|)
block|{
name|releaseLocation
argument_list|(
name|cache
operator|.
name|put
argument_list|(
name|blockCacheKey
operator|.
name|clone
argument_list|()
argument_list|,
name|location
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|blockCacheSize
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|fetch
specifier|public
name|boolean
name|fetch
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|BlockCacheLocation
name|location
init|=
name|cache
operator|.
name|get
argument_list|(
name|blockCacheKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|location
operator|.
name|isRemoved
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|bankId
init|=
name|location
operator|.
name|getBankId
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|location
operator|.
name|getBlock
argument_list|()
operator|*
name|blockSize
decl_stmt|;
name|location
operator|.
name|touch
argument_list|()
expr_stmt|;
name|ByteBuffer
name|bank
init|=
name|getBank
argument_list|(
name|bankId
argument_list|)
decl_stmt|;
name|bank
operator|.
name|position
argument_list|(
name|offset
operator|+
name|blockOffset
argument_list|)
expr_stmt|;
name|bank
operator|.
name|get
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|fetch
specifier|public
name|boolean
name|fetch
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
block|{
name|checkLength
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
return|;
block|}
DECL|method|findEmptyLocation
specifier|private
name|boolean
name|findEmptyLocation
parameter_list|(
name|BlockCacheLocation
name|location
parameter_list|)
block|{
comment|// This is a tight loop that will try and find a location to
comment|// place the block before giving up
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|OUTER
label|:
for|for
control|(
name|int
name|bankId
init|=
literal|0
init|;
name|bankId
operator|<
name|banks
operator|.
name|length
condition|;
name|bankId
operator|++
control|)
block|{
name|AtomicInteger
name|bitSetCounter
init|=
name|lockCounters
index|[
name|bankId
index|]
decl_stmt|;
name|BlockLocks
name|bitSet
init|=
name|locks
index|[
name|bankId
index|]
decl_stmt|;
if|if
condition|(
name|bitSetCounter
operator|.
name|get
argument_list|()
operator|==
name|numberOfBlocksPerBank
condition|)
block|{
comment|// if bitset is full
continue|continue
name|OUTER
continue|;
block|}
comment|// this check needs to spin, if a lock was attempted but not obtained
comment|// the rest of the bank should not be skipped
name|int
name|bit
init|=
name|bitSet
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|INNER
label|:
while|while
condition|(
name|bit
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bit
operator|>=
name|numberOfBlocksPerBank
condition|)
block|{
comment|// bit set is full
continue|continue
name|OUTER
continue|;
block|}
if|if
condition|(
operator|!
name|bitSet
operator|.
name|set
argument_list|(
name|bit
argument_list|)
condition|)
block|{
comment|// lock was not obtained
comment|// this restarts at 0 because another block could have been unlocked
comment|// while this was executing
name|bit
operator|=
name|bitSet
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
continue|continue
name|INNER
continue|;
block|}
else|else
block|{
comment|// lock obtained
name|location
operator|.
name|setBankId
argument_list|(
name|bankId
argument_list|)
expr_stmt|;
name|location
operator|.
name|setBlock
argument_list|(
name|bit
argument_list|)
expr_stmt|;
name|bitSetCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|checkLength
specifier|private
name|void
name|checkLength
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
operator|!=
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Buffer wrong size, expecting ["
operator|+
name|blockSize
operator|+
literal|"] got ["
operator|+
name|buffer
operator|.
name|length
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|getBank
specifier|private
name|ByteBuffer
name|getBank
parameter_list|(
name|int
name|bankId
parameter_list|)
block|{
return|return
name|banks
index|[
name|bankId
index|]
operator|.
name|duplicate
argument_list|()
return|;
block|}
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

