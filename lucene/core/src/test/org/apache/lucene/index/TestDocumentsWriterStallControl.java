begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|Collections
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
name|AtomicBoolean
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
name|DocumentsWriterStallControl
operator|.
name|MemoryController
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
name|ThreadInterruptedException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeaks
import|;
end_import

begin_comment
comment|/**  * Tests for {@link DocumentsWriterStallControl}  */
end_comment

begin_class
annotation|@
name|ThreadLeaks
argument_list|(
name|failTestIfLeaking
operator|=
literal|true
argument_list|)
DECL|class|TestDocumentsWriterStallControl
specifier|public
class|class
name|TestDocumentsWriterStallControl
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimpleStall
specifier|public
name|void
name|testSimpleStall
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|DocumentsWriterStallControl
name|ctrl
init|=
operator|new
name|DocumentsWriterStallControl
argument_list|()
decl_stmt|;
name|SimpleMemCtrl
name|memCtrl
init|=
operator|new
name|SimpleMemCtrl
argument_list|()
decl_stmt|;
name|memCtrl
operator|.
name|limit
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
literal|20
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
name|Thread
index|[]
name|waitThreads
init|=
name|waitThreads
argument_list|(
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ctrl
argument_list|)
decl_stmt|;
name|start
argument_list|(
name|waitThreads
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctrl
operator|.
name|hasBlocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctrl
operator|.
name|anyStalledThreads
argument_list|()
argument_list|)
expr_stmt|;
name|join
argument_list|(
name|waitThreads
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// now stall threads and wake them up again
name|memCtrl
operator|.
name|netBytes
operator|=
literal|1001
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
literal|100
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
name|waitThreads
operator|=
name|waitThreads
argument_list|(
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ctrl
argument_list|)
expr_stmt|;
name|start
argument_list|(
name|waitThreads
argument_list|)
expr_stmt|;
name|awaitState
argument_list|(
literal|100
argument_list|,
name|Thread
operator|.
name|State
operator|.
name|WAITING
argument_list|,
name|waitThreads
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctrl
operator|.
name|hasBlocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctrl
operator|.
name|anyStalledThreads
argument_list|()
argument_list|)
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
literal|50
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
literal|0
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctrl
operator|.
name|anyStalledThreads
argument_list|()
argument_list|)
expr_stmt|;
name|join
argument_list|(
name|waitThreads
argument_list|,
literal|500
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|DocumentsWriterStallControl
name|ctrl
init|=
operator|new
name|DocumentsWriterStallControl
argument_list|()
decl_stmt|;
name|SimpleMemCtrl
name|memCtrl
init|=
operator|new
name|SimpleMemCtrl
argument_list|()
decl_stmt|;
name|memCtrl
operator|.
name|limit
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
literal|1
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
name|Thread
index|[]
name|stallThreads
init|=
operator|new
name|Thread
index|[
name|atLeast
argument_list|(
literal|3
argument_list|)
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
name|stallThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|threadId
init|=
name|i
decl_stmt|;
name|stallThreads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|baseBytes
init|=
name|threadId
operator|%
literal|2
operator|==
literal|0
condition|?
literal|500
else|:
literal|700
decl_stmt|;
name|SimpleMemCtrl
name|memCtrl
init|=
operator|new
name|SimpleMemCtrl
argument_list|()
decl_stmt|;
name|memCtrl
operator|.
name|limit
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
literal|1
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
literal|0
expr_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
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
name|iters
condition|;
name|j
operator|++
control|)
block|{
name|memCtrl
operator|.
name|netBytes
operator|=
name|baseBytes
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|memCtrl
operator|.
name|netBytes
argument_list|)
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// thread 0 only updates
name|ctrl
operator|.
name|waitIfStalled
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
name|start
argument_list|(
name|stallThreads
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/*      * use a 100 sec timeout to make sure we not hang forever. join will fail in      * that case      */
while|while
condition|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
operator|)
operator|<
literal|100
operator|*
literal|1000
operator|&&
operator|!
name|terminated
argument_list|(
name|stallThreads
argument_list|)
condition|)
block|{
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|join
argument_list|(
name|stallThreads
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nightly
DECL|method|testAccquireReleaseRace
specifier|public
name|void
name|testAccquireReleaseRace
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|DocumentsWriterStallControl
name|ctrl
init|=
operator|new
name|DocumentsWriterStallControl
argument_list|()
decl_stmt|;
name|SimpleMemCtrl
name|memCtrl
init|=
operator|new
name|SimpleMemCtrl
argument_list|()
decl_stmt|;
name|memCtrl
operator|.
name|limit
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
literal|1
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
literal|0
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|checkPoint
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|int
name|numStallers
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|numReleasers
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|numWaiters
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Synchronizer
name|sync
init|=
operator|new
name|Synchronizer
argument_list|(
name|numStallers
operator|+
name|numReleasers
argument_list|,
name|numStallers
operator|+
name|numReleasers
operator|+
name|numWaiters
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numReleasers
operator|+
name|numStallers
operator|+
name|numWaiters
index|]
decl_stmt|;
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
argument_list|)
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
name|numReleasers
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
name|Updater
argument_list|(
name|stop
argument_list|,
name|checkPoint
argument_list|,
name|ctrl
argument_list|,
name|sync
argument_list|,
literal|true
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|numReleasers
init|;
name|i
operator|<
name|numReleasers
operator|+
name|numStallers
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
name|Updater
argument_list|(
name|stop
argument_list|,
name|checkPoint
argument_list|,
name|ctrl
argument_list|,
name|sync
argument_list|,
literal|false
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|numReleasers
operator|+
name|numStallers
init|;
name|i
operator|<
name|numReleasers
operator|+
name|numStallers
operator|+
name|numWaiters
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
name|Waiter
argument_list|(
name|stop
argument_list|,
name|checkPoint
argument_list|,
name|ctrl
argument_list|,
name|sync
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
block|}
name|start
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|20000
argument_list|)
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|checkPoint
operator|.
name|get
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"timed out waiting for update threads - deadlock?"
argument_list|,
name|sync
operator|.
name|updateJoin
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Throwable
name|throwable
range|:
name|exceptions
control|)
block|{
name|throwable
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"got exceptions in threads"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ctrl
operator|.
name|hasBlocked
argument_list|()
operator|&&
name|ctrl
operator|.
name|isHealthy
argument_list|()
condition|)
block|{
name|assertState
argument_list|(
name|numReleasers
argument_list|,
name|numStallers
argument_list|,
name|numWaiters
argument_list|,
name|threads
argument_list|,
name|ctrl
argument_list|)
expr_stmt|;
block|}
name|checkPoint
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|sync
operator|.
name|waiter
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|sync
operator|.
name|leftCheckpoint
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|checkPoint
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sync
operator|.
name|waiter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|==
literal|0
condition|)
block|{
name|sync
operator|.
name|reset
argument_list|(
name|numStallers
operator|+
name|numReleasers
argument_list|,
name|numStallers
operator|+
name|numReleasers
operator|+
name|numWaiters
argument_list|)
expr_stmt|;
name|checkPoint
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|checkPoint
operator|.
name|get
argument_list|()
condition|)
block|{
name|sync
operator|.
name|reset
argument_list|(
name|numStallers
operator|+
name|numReleasers
argument_list|,
name|numStallers
operator|+
name|numReleasers
operator|+
name|numWaiters
argument_list|)
expr_stmt|;
name|checkPoint
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|sync
operator|.
name|updateJoin
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertState
argument_list|(
name|numReleasers
argument_list|,
name|numStallers
argument_list|,
name|numWaiters
argument_list|,
name|threads
argument_list|,
name|ctrl
argument_list|)
expr_stmt|;
name|checkPoint
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sync
operator|.
name|waiter
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|sync
operator|.
name|leftCheckpoint
operator|.
name|await
argument_list|()
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|memCtrl
operator|.
name|limit
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
literal|1
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
literal|0
expr_stmt|;
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
if|if
condition|(
name|threads
index|[
name|i
index|]
operator|.
name|isAlive
argument_list|()
operator|&&
name|threads
index|[
name|i
index|]
operator|instanceof
name|Waiter
condition|)
block|{
if|if
condition|(
name|threads
index|[
name|i
index|]
operator|.
name|getState
argument_list|()
operator|==
name|Thread
operator|.
name|State
operator|.
name|WAITING
condition|)
block|{
name|fail
argument_list|(
literal|"waiter is not released - anyThreadsStalled: "
operator|+
name|ctrl
operator|.
name|anyStalledThreads
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|assertState
specifier|private
name|void
name|assertState
parameter_list|(
name|int
name|numReleasers
parameter_list|,
name|int
name|numStallers
parameter_list|,
name|int
name|numWaiters
parameter_list|,
name|Thread
index|[]
name|threads
parameter_list|,
name|DocumentsWriterStallControl
name|ctrl
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|millisToSleep
init|=
literal|100
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|ctrl
operator|.
name|hasBlocked
argument_list|()
operator|&&
name|ctrl
operator|.
name|isHealthy
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|n
init|=
name|numReleasers
operator|+
name|numStallers
init|;
name|n
operator|<
name|numReleasers
operator|+
name|numStallers
operator|+
name|numWaiters
condition|;
name|n
operator|++
control|)
block|{
if|if
condition|(
name|ctrl
operator|.
name|isThreadQueued
argument_list|(
name|threads
index|[
name|n
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
name|millisToSleep
operator|<
literal|60000
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|millisToSleep
argument_list|)
expr_stmt|;
name|millisToSleep
operator|*=
literal|2
expr_stmt|;
break|break;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"control claims no stalled threads but waiter seems to be blocked "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|class|Waiter
specifier|public
specifier|static
class|class
name|Waiter
extends|extends
name|Thread
block|{
DECL|field|sync
specifier|private
name|Synchronizer
name|sync
decl_stmt|;
DECL|field|ctrl
specifier|private
name|DocumentsWriterStallControl
name|ctrl
decl_stmt|;
DECL|field|checkPoint
specifier|private
name|AtomicBoolean
name|checkPoint
decl_stmt|;
DECL|field|stop
specifier|private
name|AtomicBoolean
name|stop
decl_stmt|;
DECL|field|exceptions
specifier|private
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
decl_stmt|;
DECL|method|Waiter
specifier|public
name|Waiter
parameter_list|(
name|AtomicBoolean
name|stop
parameter_list|,
name|AtomicBoolean
name|checkPoint
parameter_list|,
name|DocumentsWriterStallControl
name|ctrl
parameter_list|,
name|Synchronizer
name|sync
parameter_list|,
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
parameter_list|)
block|{
name|super
argument_list|(
literal|"waiter"
argument_list|)
expr_stmt|;
name|this
operator|.
name|stop
operator|=
name|stop
expr_stmt|;
name|this
operator|.
name|checkPoint
operator|=
name|checkPoint
expr_stmt|;
name|this
operator|.
name|ctrl
operator|=
name|ctrl
expr_stmt|;
name|this
operator|.
name|sync
operator|=
name|sync
expr_stmt|;
name|this
operator|.
name|exceptions
operator|=
name|exceptions
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|ctrl
operator|.
name|waitIfStalled
argument_list|()
expr_stmt|;
if|if
condition|(
name|checkPoint
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|assertTrue
argument_list|(
name|sync
operator|.
name|await
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[Waiter] got interrupted - wait count: "
operator|+
name|sync
operator|.
name|waiter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Updater
specifier|public
specifier|static
class|class
name|Updater
extends|extends
name|Thread
block|{
DECL|field|sync
specifier|private
name|Synchronizer
name|sync
decl_stmt|;
DECL|field|ctrl
specifier|private
name|DocumentsWriterStallControl
name|ctrl
decl_stmt|;
DECL|field|checkPoint
specifier|private
name|AtomicBoolean
name|checkPoint
decl_stmt|;
DECL|field|stop
specifier|private
name|AtomicBoolean
name|stop
decl_stmt|;
DECL|field|release
specifier|private
name|boolean
name|release
decl_stmt|;
DECL|field|exceptions
specifier|private
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
decl_stmt|;
DECL|method|Updater
specifier|public
name|Updater
parameter_list|(
name|AtomicBoolean
name|stop
parameter_list|,
name|AtomicBoolean
name|checkPoint
parameter_list|,
name|DocumentsWriterStallControl
name|ctrl
parameter_list|,
name|Synchronizer
name|sync
parameter_list|,
name|boolean
name|release
parameter_list|,
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
parameter_list|)
block|{
name|super
argument_list|(
literal|"updater"
argument_list|)
expr_stmt|;
name|this
operator|.
name|stop
operator|=
name|stop
expr_stmt|;
name|this
operator|.
name|checkPoint
operator|=
name|checkPoint
expr_stmt|;
name|this
operator|.
name|ctrl
operator|=
name|ctrl
expr_stmt|;
name|this
operator|.
name|sync
operator|=
name|sync
expr_stmt|;
name|this
operator|.
name|release
operator|=
name|release
expr_stmt|;
name|this
operator|.
name|exceptions
operator|=
name|exceptions
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|SimpleMemCtrl
name|memCtrl
init|=
operator|new
name|SimpleMemCtrl
argument_list|()
decl_stmt|;
name|memCtrl
operator|.
name|limit
operator|=
literal|1000
expr_stmt|;
name|memCtrl
operator|.
name|netBytes
operator|=
name|release
condition|?
literal|1
else|:
literal|2000
expr_stmt|;
name|memCtrl
operator|.
name|flushBytes
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|memCtrl
operator|.
name|netBytes
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|int
name|internalIters
init|=
name|release
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|atLeast
argument_list|(
literal|5
argument_list|)
else|:
literal|1
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
name|internalIters
condition|;
name|i
operator|++
control|)
block|{
name|ctrl
operator|.
name|updateStalled
argument_list|(
name|memCtrl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkPoint
operator|.
name|get
argument_list|()
condition|)
block|{
name|sync
operator|.
name|updateJoin
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|sync
operator|.
name|await
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[Updater] got interrupted - wait count: "
operator|+
name|sync
operator|.
name|waiter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|sync
operator|.
name|leftCheckpoint
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|sync
operator|.
name|updateJoin
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|terminated
specifier|public
specifier|static
name|boolean
name|terminated
parameter_list|(
name|Thread
index|[]
name|threads
parameter_list|)
block|{
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
if|if
condition|(
name|Thread
operator|.
name|State
operator|.
name|TERMINATED
operator|!=
name|thread
operator|.
name|getState
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|start
specifier|public
specifier|static
name|void
name|start
parameter_list|(
name|Thread
index|[]
name|tostart
parameter_list|)
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|Thread
name|thread
range|:
name|tostart
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// let them start
block|}
DECL|method|join
specifier|public
specifier|static
name|void
name|join
parameter_list|(
name|Thread
index|[]
name|toJoin
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|Thread
name|thread
range|:
name|toJoin
control|)
block|{
name|thread
operator|.
name|join
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitThreads
specifier|public
specifier|static
name|Thread
index|[]
name|waitThreads
parameter_list|(
name|int
name|num
parameter_list|,
specifier|final
name|DocumentsWriterStallControl
name|ctrl
parameter_list|)
block|{
name|Thread
index|[]
name|array
init|=
operator|new
name|Thread
index|[
name|num
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|array
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ctrl
operator|.
name|waitIfStalled
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
DECL|method|awaitState
specifier|public
specifier|static
name|void
name|awaitState
parameter_list|(
name|long
name|timeout
parameter_list|,
name|Thread
operator|.
name|State
name|state
parameter_list|,
name|Thread
modifier|...
name|threads
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|t
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t
operator|<=
name|timeout
condition|)
block|{
name|boolean
name|done
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
if|if
condition|(
name|thread
operator|.
name|getState
argument_list|()
operator|!=
name|state
condition|)
block|{
name|done
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|done
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|fail
argument_list|(
literal|"timed out waiting for state: "
operator|+
name|state
operator|+
literal|" timeout: "
operator|+
name|timeout
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
DECL|class|SimpleMemCtrl
specifier|private
specifier|static
class|class
name|SimpleMemCtrl
implements|implements
name|MemoryController
block|{
DECL|field|netBytes
name|long
name|netBytes
decl_stmt|;
DECL|field|limit
name|long
name|limit
decl_stmt|;
DECL|field|flushBytes
name|long
name|flushBytes
decl_stmt|;
annotation|@
name|Override
DECL|method|netBytes
specifier|public
name|long
name|netBytes
parameter_list|()
block|{
return|return
name|netBytes
return|;
block|}
annotation|@
name|Override
DECL|method|stallLimitBytes
specifier|public
name|long
name|stallLimitBytes
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
annotation|@
name|Override
DECL|method|flushBytes
specifier|public
name|long
name|flushBytes
parameter_list|()
block|{
return|return
name|flushBytes
return|;
block|}
block|}
DECL|class|Synchronizer
specifier|private
specifier|static
specifier|final
class|class
name|Synchronizer
block|{
DECL|field|waiter
specifier|volatile
name|CountDownLatch
name|waiter
decl_stmt|;
DECL|field|updateJoin
specifier|volatile
name|CountDownLatch
name|updateJoin
decl_stmt|;
DECL|field|leftCheckpoint
specifier|volatile
name|CountDownLatch
name|leftCheckpoint
decl_stmt|;
DECL|method|Synchronizer
specifier|public
name|Synchronizer
parameter_list|(
name|int
name|numUpdater
parameter_list|,
name|int
name|numThreads
parameter_list|)
block|{
name|reset
argument_list|(
name|numUpdater
argument_list|,
name|numThreads
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|numUpdaters
parameter_list|,
name|int
name|numThreads
parameter_list|)
block|{
name|this
operator|.
name|waiter
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|updateJoin
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|numUpdaters
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftCheckpoint
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|numUpdaters
argument_list|)
expr_stmt|;
block|}
DECL|method|await
specifier|public
name|boolean
name|await
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|waiter
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

