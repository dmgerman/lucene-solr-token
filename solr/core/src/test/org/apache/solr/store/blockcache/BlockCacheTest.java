begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Random
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
name|AtomicBoolean
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
name|AtomicLong
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|BlockCacheTest
specifier|public
class|class
name|BlockCacheTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testBlockCache
specifier|public
name|void
name|testBlockCache
parameter_list|()
block|{
name|int
name|blocksInTest
init|=
literal|2000000
decl_stmt|;
name|int
name|blockSize
init|=
literal|1024
decl_stmt|;
name|int
name|slabSize
init|=
name|blockSize
operator|*
literal|4096
decl_stmt|;
name|long
name|totalMemory
init|=
literal|2
operator|*
name|slabSize
decl_stmt|;
name|BlockCache
name|blockCache
init|=
operator|new
name|BlockCache
argument_list|(
operator|new
name|Metrics
argument_list|()
argument_list|,
literal|true
argument_list|,
name|totalMemory
argument_list|,
name|slabSize
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|byte
index|[]
name|newData
init|=
operator|new
name|byte
index|[
name|blockSize
index|]
decl_stmt|;
name|AtomicLong
name|hitsInCache
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|AtomicLong
name|missesInCache
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|long
name|storeTime
init|=
literal|0
decl_stmt|;
name|long
name|fetchTime
init|=
literal|0
decl_stmt|;
name|int
name|passes
init|=
literal|10000
decl_stmt|;
name|BlockCacheKey
name|blockCacheKey
init|=
operator|new
name|BlockCacheKey
argument_list|()
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
name|passes
condition|;
name|j
operator|++
control|)
block|{
name|long
name|block
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|blocksInTest
argument_list|)
decl_stmt|;
name|int
name|file
init|=
literal|0
decl_stmt|;
name|blockCacheKey
operator|.
name|setBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockCache
operator|.
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|buffer
argument_list|)
condition|)
block|{
name|hitsInCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missesInCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|byte
index|[]
name|testData
init|=
name|testData
argument_list|(
name|random
argument_list|,
name|blockSize
argument_list|,
name|newData
argument_list|)
decl_stmt|;
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
name|blockCache
operator|.
name|store
argument_list|(
name|blockCacheKey
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|storeTime
operator|+=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t1
operator|)
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
continue|continue;
comment|// for now, updating existing blocks is not supported... see SOLR-10121
name|long
name|t3
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockCache
operator|.
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|buffer
argument_list|)
condition|)
block|{
name|fetchTime
operator|+=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t3
operator|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|testData
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cache Hits    = "
operator|+
name|hitsInCache
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cache Misses  = "
operator|+
name|missesInCache
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Store         = "
operator|+
operator|(
name|storeTime
operator|/
operator|(
name|double
operator|)
name|passes
operator|)
operator|/
literal|1000000.0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Fetch         = "
operator|+
operator|(
name|fetchTime
operator|/
operator|(
name|double
operator|)
name|passes
operator|)
operator|/
literal|1000000.0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"# of Elements = "
operator|+
name|blockCache
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testData
specifier|private
specifier|static
name|byte
index|[]
name|testData
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|size
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|)
block|{
name|random
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
return|return
name|buf
return|;
block|}
comment|// given a position, return the appropriate byte.
comment|// always returns the same thing so we don't actually have to store the bytes redundantly to check them.
DECL|method|getByte
specifier|private
specifier|static
name|byte
name|getByte
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
comment|// knuth multiplicative hash method, then take top 8 bits
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
operator|(
operator|(
name|int
operator|)
name|pos
operator|)
operator|*
call|(
name|int
call|)
argument_list|(
literal|2654435761L
argument_list|)
operator|)
operator|>>
literal|24
argument_list|)
return|;
comment|// just the lower bits of the block number, to aid in debugging...
comment|// return (byte)(pos>>10);
block|}
annotation|@
name|Test
DECL|method|testBlockCacheConcurrent
specifier|public
name|void
name|testBlockCacheConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|blocksInTest
init|=
literal|400
decl_stmt|;
comment|// pick something bigger than 256, since that would lead to a slab size of 64 blocks and the bitset locks would consist of a single word.
specifier|final
name|int
name|blockSize
init|=
literal|64
decl_stmt|;
specifier|final
name|int
name|slabSize
init|=
name|blocksInTest
operator|*
name|blockSize
operator|/
literal|4
decl_stmt|;
specifier|final
name|long
name|totalMemory
init|=
literal|2
operator|*
name|slabSize
decl_stmt|;
comment|// 2 slabs of memory, so only half of what is needed for all blocks
comment|/***     final int blocksInTest = 16384;  // pick something bigger than 256, since that would lead to a slab size of 64 blocks and the bitset locks would consist of a single word.     final int blockSize = 1024;     final int slabSize = blocksInTest * blockSize / 4;     final long totalMemory = 2 * slabSize;  // 2 slabs of memory, so only half of what is needed for all blocks     ***/
specifier|final
name|int
name|nThreads
init|=
literal|64
decl_stmt|;
specifier|final
name|int
name|nReads
init|=
literal|1000000
decl_stmt|;
specifier|final
name|int
name|readsPerThread
init|=
name|nReads
operator|/
name|nThreads
decl_stmt|;
specifier|final
name|int
name|readLastBlockOdds
init|=
literal|10
decl_stmt|;
comment|// odds (1 in N) of the next block operation being on the same block as the previous operation... helps flush concurrency issues
specifier|final
name|int
name|showErrors
init|=
literal|50
decl_stmt|;
comment|// show first 50 validation failures
specifier|final
name|BlockCache
name|blockCache
init|=
operator|new
name|BlockCache
argument_list|(
operator|new
name|Metrics
argument_list|()
argument_list|,
literal|true
argument_list|,
name|totalMemory
argument_list|,
name|slabSize
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|hitsInCache
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|missesInCache
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|storeFails
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|lastBlock
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|validateFails
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|file
init|=
literal|0
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|nThreads
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|threadnum
init|=
name|i
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|rnd
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
name|Random
name|r
decl_stmt|;
name|BlockCacheKey
name|blockCacheKey
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|blockSize
index|]
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|=
operator|new
name|BlockCacheKey
argument_list|()
expr_stmt|;
name|blockCacheKey
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setPath
argument_list|(
literal|"/foo.txt"
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|readsPerThread
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|test
parameter_list|(
name|int
name|iter
parameter_list|)
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|test
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|test
parameter_list|()
block|{
name|long
name|block
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|blocksInTest
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
name|readLastBlockOdds
argument_list|)
operator|==
literal|0
condition|)
name|block
operator|=
name|lastBlock
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// some percent of the time, try to read the last block another thread was just reading/writing
name|lastBlock
operator|.
name|set
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|int
name|blockOffset
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|blockSize
argument_list|)
decl_stmt|;
name|long
name|globalOffset
init|=
name|block
operator|*
name|blockSize
operator|+
name|blockOffset
decl_stmt|;
name|int
name|len
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|blockSize
operator|-
name|blockOffset
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// TODO: bias toward smaller reads?
name|blockCacheKey
operator|.
name|setBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockCache
operator|.
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|buffer
argument_list|,
name|blockOffset
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
condition|)
block|{
name|hitsInCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// validate returned bytes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|long
name|globalPos
init|=
name|globalOffset
operator|+
name|i
decl_stmt|;
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|!=
name|getByte
argument_list|(
name|globalPos
argument_list|)
condition|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|validateFails
operator|.
name|incrementAndGet
argument_list|()
operator|<=
name|showErrors
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: read was "
operator|+
literal|"block="
operator|+
name|block
operator|+
literal|" blockOffset="
operator|+
name|blockOffset
operator|+
literal|" len="
operator|+
name|len
operator|+
literal|" globalPos="
operator|+
name|globalPos
operator|+
literal|" localReadOffset="
operator|+
name|i
operator|+
literal|" got="
operator|+
name|buffer
index|[
name|i
index|]
operator|+
literal|" expected="
operator|+
name|getByte
argument_list|(
name|globalPos
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
name|missesInCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// OK, we should "get" the data and then cache the block
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockSize
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|getByte
argument_list|(
name|block
operator|*
name|blockSize
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|boolean
name|cached
init|=
name|blockCache
operator|.
name|store
argument_list|(
name|blockCacheKey
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cached
condition|)
block|{
name|storeFails
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"# of Elements = "
operator|+
name|blockCache
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cache Hits = "
operator|+
name|hitsInCache
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cache Misses = "
operator|+
name|missesInCache
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cache Store Fails = "
operator|+
name|storeFails
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Blocks with Errors = "
operator|+
name|validateFails
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|failed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|Val
specifier|static
class|class
name|Val
block|{
DECL|field|key
name|long
name|key
decl_stmt|;
DECL|field|live
name|AtomicBoolean
name|live
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
block|}
comment|// Sanity test the underlying concurrent map that BlockCache is using, in the same way that we use it.
annotation|@
name|Test
DECL|method|testCacheConcurrent
specifier|public
name|void
name|testCacheConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
comment|// TODO: introduce more randomness in cache size, hit rate, etc
specifier|final
name|int
name|blocksInTest
init|=
literal|400
decl_stmt|;
specifier|final
name|int
name|maxEntries
init|=
name|blocksInTest
operator|/
literal|2
decl_stmt|;
specifier|final
name|int
name|nThreads
init|=
literal|64
decl_stmt|;
specifier|final
name|int
name|nReads
init|=
literal|1000000
decl_stmt|;
specifier|final
name|int
name|readsPerThread
init|=
name|nReads
operator|/
name|nThreads
decl_stmt|;
specifier|final
name|int
name|readLastBlockOdds
init|=
literal|10
decl_stmt|;
comment|// odds (1 in N) of the next block operation being on the same block as the previous operation... helps flush concurrency issues
specifier|final
name|int
name|updateAnywayOdds
init|=
literal|3
decl_stmt|;
comment|// sometimes insert a new entry for the key even if one was found
specifier|final
name|int
name|invalidateOdds
init|=
literal|20
decl_stmt|;
comment|// sometimes invalidate an entry
specifier|final
name|AtomicLong
name|hits
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|removals
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|inserts
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|RemovalListener
argument_list|<
name|Long
argument_list|,
name|Val
argument_list|>
name|listener
init|=
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|,
name|removalCause
parameter_list|)
lambda|->
block|{
assert|assert
name|v
operator|.
name|key
operator|==
name|k
assert|;
if|if
condition|(
operator|!
name|v
operator|.
name|live
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"listener called more than once! k="
operator|+
name|k
operator|+
literal|" v="
operator|+
name|v
operator|+
literal|" removalCause="
operator|+
name|removalCause
argument_list|)
throw|;
comment|// return;  // use this variant if listeners may be called more than once
block|}
name|removals
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
decl_stmt|;
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Cache
argument_list|<
name|Long
argument_list|,
name|Val
argument_list|>
name|cache
init|=
name|Caffeine
operator|.
name|newBuilder
argument_list|()
operator|.
name|removalListener
argument_list|(
name|listener
argument_list|)
operator|.
name|maximumSize
argument_list|(
name|maxEntries
argument_list|)
operator|.
name|executor
argument_list|(
name|Runnable
operator|::
name|run
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|lastBlock
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|maxObservedSize
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|nThreads
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|seed
init|=
name|rnd
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
name|Random
name|r
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|readsPerThread
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|test
parameter_list|(
name|int
name|iter
parameter_list|)
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|test
argument_list|()
expr_stmt|;
block|}
block|}
name|boolean
name|odds
parameter_list|(
name|int
name|odds
parameter_list|)
block|{
return|return
name|odds
operator|>
literal|0
operator|&&
name|r
operator|.
name|nextInt
argument_list|(
name|odds
argument_list|)
operator|==
literal|0
return|;
block|}
name|long
name|getBlock
parameter_list|()
block|{
name|long
name|block
decl_stmt|;
if|if
condition|(
name|odds
argument_list|(
name|readLastBlockOdds
argument_list|)
condition|)
block|{
name|block
operator|=
name|lastBlock
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// some percent of the time, try to read the last block another thread was just reading/writing
block|}
else|else
block|{
name|block
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|blocksInTest
argument_list|)
expr_stmt|;
name|lastBlock
operator|.
name|set
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
return|return
name|block
return|;
block|}
specifier|public
name|void
name|test
parameter_list|()
block|{
name|Long
name|k
init|=
name|getBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|odds
argument_list|(
name|invalidateOdds
argument_list|)
condition|)
block|{
comment|// This tests that invalidate always ends up calling the removal listener exactly once
comment|// even if the entry may be in the process of concurrent removal in a different thread.
comment|// This also inadvertently tests concurrently inserting, getting, and invalidating the same key, which we don't need in Solr's BlockCache.
name|cache
operator|.
name|invalidate
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|Val
name|v
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|hits
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
assert|assert
name|k
operator|.
name|equals
argument_list|(
name|v
operator|.
name|key
argument_list|)
assert|;
block|}
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|odds
argument_list|(
name|updateAnywayOdds
argument_list|)
condition|)
block|{
name|v
operator|=
operator|new
name|Val
argument_list|()
expr_stmt|;
name|v
operator|.
name|key
operator|=
name|k
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|inserts
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|long
name|sz
init|=
name|cache
operator|.
name|estimatedSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|sz
operator|>
name|maxObservedSize
operator|.
name|get
argument_list|()
condition|)
name|maxObservedSize
operator|.
name|set
argument_list|(
name|sz
argument_list|)
expr_stmt|;
comment|// race condition here, but an estimate is OK
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// Thread.sleep(1000); // need to wait if executor is used for listener?
name|long
name|cacheSize
init|=
name|cache
operator|.
name|estimatedSize
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done! # of Elements = "
operator|+
name|cacheSize
operator|+
literal|" inserts="
operator|+
name|inserts
operator|.
name|get
argument_list|()
operator|+
literal|" removals="
operator|+
name|removals
operator|.
name|get
argument_list|()
operator|+
literal|" hits="
operator|+
name|hits
operator|.
name|get
argument_list|()
operator|+
literal|" maxObservedSize="
operator|+
name|maxObservedSize
argument_list|)
expr_stmt|;
assert|assert
name|inserts
operator|.
name|get
argument_list|()
operator|-
name|removals
operator|.
name|get
argument_list|()
operator|==
name|cacheSize
assert|;
name|assertFalse
argument_list|(
name|failed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

