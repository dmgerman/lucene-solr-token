begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|TimeUnit
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RateLimiter
operator|.
name|SimpleRateLimiter
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
comment|/**  * Simple testcase for RateLimiter.SimpleRateLimiter  */
end_comment

begin_class
DECL|class|TestRateLimiter
specifier|public
specifier|final
class|class
name|TestRateLimiter
extends|extends
name|LuceneTestCase
block|{
comment|// LUCENE-6075
DECL|method|testOverflowInt
specifier|public
name|void
name|testOverflowInt
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
operator|new
name|SimpleRateLimiter
argument_list|(
literal|1
argument_list|)
operator|.
name|pause
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|1.5
operator|*
name|Integer
operator|.
name|MAX_VALUE
operator|*
literal|1024
operator|*
literal|1024
operator|/
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have been interrupted"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ThreadInterruptedException
name|tie
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|method|testThreads
specifier|public
name|void
name|testThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|targetMBPerSec
init|=
literal|10.0
operator|+
literal|20
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|SimpleRateLimiter
name|limiter
init|=
operator|new
name|SimpleRateLimiter
argument_list|(
name|targetMBPerSec
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|6
argument_list|)
index|]
decl_stmt|;
specifier|final
name|AtomicLong
name|totBytes
init|=
operator|new
name|AtomicLong
argument_list|()
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
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|long
name|bytesSinceLastPause
init|=
literal|0
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
literal|500
condition|;
name|i
operator|++
control|)
block|{
name|long
name|numBytes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|totBytes
operator|.
name|addAndGet
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
name|bytesSinceLastPause
operator|+=
name|numBytes
expr_stmt|;
if|if
condition|(
name|bytesSinceLastPause
operator|>
name|limiter
operator|.
name|getMinPauseCheckBytes
argument_list|()
condition|)
block|{
name|limiter
operator|.
name|pause
argument_list|(
name|bytesSinceLastPause
argument_list|)
expr_stmt|;
name|bytesSinceLastPause
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|long
name|startNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
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
name|long
name|endNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|double
name|actualMBPerSec
init|=
operator|(
name|totBytes
operator|.
name|get
argument_list|()
operator|/
literal|1024
operator|/
literal|1024.
operator|)
operator|/
operator|(
operator|(
name|endNS
operator|-
name|startNS
operator|)
operator|/
literal|1000000000.0
operator|)
decl_stmt|;
comment|// TODO: this may false trip .... could be we can only assert that it never exceeds the max, so slow jenkins doesn't trip:
name|double
name|ratio
init|=
name|actualMBPerSec
operator|/
name|targetMBPerSec
decl_stmt|;
comment|// Only enforce that it wasn't too fast; if machine is bogged down (can't schedule threads / sleep properly) then it may falsely be too slow:
name|assumeTrue
argument_list|(
literal|"actualMBPerSec="
operator|+
name|actualMBPerSec
operator|+
literal|" targetMBPerSec="
operator|+
name|targetMBPerSec
argument_list|,
literal|0.9
operator|<=
name|ratio
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"targetMBPerSec="
operator|+
name|targetMBPerSec
operator|+
literal|" actualMBPerSec="
operator|+
name|actualMBPerSec
argument_list|,
name|ratio
operator|<=
literal|1.1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

