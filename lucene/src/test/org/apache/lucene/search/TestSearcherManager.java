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
name|Executors
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
name|analysis
operator|.
name|MockAnalyzer
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
name|Document
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
name|ConcurrentMergeScheduler
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
name|IndexWriter
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
name|ThreadedIndexingAndSearchingTestCase
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
name|AlreadyClosedException
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
name|Directory
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
operator|.
name|UseNoMemoryExpensiveCodec
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
name|NamedThreadFactory
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
name|_TestUtil
import|;
end_import

begin_class
annotation|@
name|UseNoMemoryExpensiveCodec
DECL|class|TestSearcherManager
specifier|public
class|class
name|TestSearcherManager
extends|extends
name|ThreadedIndexingAndSearchingTestCase
block|{
DECL|field|warmCalled
name|boolean
name|warmCalled
decl_stmt|;
DECL|field|pruner
specifier|private
name|SearcherLifetimeManager
operator|.
name|Pruner
name|pruner
decl_stmt|;
DECL|method|testSearcherManager
specifier|public
name|void
name|testSearcherManager
parameter_list|()
throws|throws
name|Exception
block|{
name|pruner
operator|=
operator|new
name|SearcherLifetimeManager
operator|.
name|PruneByAge
argument_list|(
name|TEST_NIGHTLY
condition|?
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
else|:
literal|1
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
literal|"TestSearcherManager"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFinalSearcher
specifier|protected
name|IndexSearcher
name|getFinalSearcher
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isNRT
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|mgr
operator|.
name|maybeReopen
argument_list|()
operator|||
name|mgr
operator|.
name|isSearcherCurrent
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|mgr
operator|.
name|acquire
argument_list|()
return|;
block|}
DECL|field|mgr
specifier|private
name|SearcherManager
name|mgr
decl_stmt|;
DECL|field|lifetimeMGR
specifier|private
name|SearcherLifetimeManager
name|lifetimeMGR
decl_stmt|;
DECL|field|pastSearchers
specifier|private
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|pastSearchers
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isNRT
specifier|private
name|boolean
name|isNRT
decl_stmt|;
annotation|@
name|Override
DECL|method|doAfterWriter
specifier|protected
name|void
name|doAfterWriter
parameter_list|(
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SearcherWarmer
name|warmer
init|=
operator|new
name|SearcherWarmer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|warm
parameter_list|(
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|TestSearcherManager
operator|.
name|this
operator|.
name|warmCalled
operator|=
literal|true
expr_stmt|;
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"united"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// TODO: can we randomize the applyAllDeletes?  But
comment|// somehow for final searcher we must apply
comment|// deletes...
name|mgr
operator|=
operator|new
name|SearcherManager
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
expr_stmt|;
name|isNRT
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// SearcherManager needs to see empty commit:
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|mgr
operator|=
operator|new
name|SearcherManager
argument_list|(
name|dir
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
expr_stmt|;
name|isNRT
operator|=
literal|false
expr_stmt|;
block|}
name|lifetimeMGR
operator|=
operator|new
name|SearcherLifetimeManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSearching
specifier|protected
name|void
name|doSearching
parameter_list|(
name|ExecutorService
name|es
parameter_list|,
specifier|final
name|long
name|stopTime
parameter_list|)
throws|throws
name|Exception
block|{
name|Thread
name|reopenThread
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
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|mgr
operator|.
name|maybeReopen
argument_list|()
condition|)
block|{
name|lifetimeMGR
operator|.
name|prune
argument_list|(
name|pruner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: reopen thread hit exc"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|reopenThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|reopenThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|runSearchThreads
argument_list|(
name|stopTime
argument_list|)
expr_stmt|;
name|reopenThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCurrentSearcher
specifier|protected
name|IndexSearcher
name|getCurrentSearcher
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
comment|// NOTE: not best practice to call maybeReopen
comment|// synchronous to your search threads, but still we
comment|// test as apps will presumably do this for
comment|// simplicity:
if|if
condition|(
name|mgr
operator|.
name|maybeReopen
argument_list|()
condition|)
block|{
name|lifetimeMGR
operator|.
name|prune
argument_list|(
name|pruner
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexSearcher
name|s
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|pastSearchers
init|)
block|{
while|while
condition|(
name|pastSearchers
operator|.
name|size
argument_list|()
operator|!=
literal|0
operator|&&
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.25
condition|)
block|{
comment|// 1/4 of the time pull an old searcher, ie, simulate
comment|// a user doing a follow-on action on a previous
comment|// search (drilling down/up, clicking next/prev page,
comment|// etc.)
specifier|final
name|Long
name|token
init|=
name|pastSearchers
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|pastSearchers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|=
name|lifetimeMGR
operator|.
name|acquire
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
comment|// Searcher was pruned
name|pastSearchers
operator|.
name|remove
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|s
operator|=
name|mgr
operator|.
name|acquire
argument_list|()
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|Long
name|token
init|=
name|lifetimeMGR
operator|.
name|record
argument_list|(
name|s
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|pastSearchers
init|)
block|{
if|if
condition|(
operator|!
name|pastSearchers
operator|.
name|contains
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|pastSearchers
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
DECL|method|releaseSearcher
specifier|protected
name|void
name|releaseSearcher
parameter_list|(
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|warmCalled
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now close SearcherManager"
argument_list|)
expr_stmt|;
block|}
name|mgr
operator|.
name|close
argument_list|()
expr_stmt|;
name|lifetimeMGR
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testIntermediateClose
specifier|public
name|void
name|testIntermediateClose
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// Test can deadlock if we use SMS:
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|CountDownLatch
name|awaitEnterWarm
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|awaitClose
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|ExecutorService
name|es
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|NamedThreadFactory
argument_list|(
literal|"testIntermediateClose"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SearcherWarmer
name|warmer
init|=
operator|new
name|SearcherWarmer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|warm
parameter_list|(
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|awaitEnterWarm
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|awaitClose
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|SearcherManager
name|searcherManager
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|SearcherManager
argument_list|(
name|dir
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
else|:
operator|new
name|SearcherManager
argument_list|(
name|writer
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|searcherManager
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcherManager
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|AtomicBoolean
name|success
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|triedReopen
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Throwable
index|[]
name|exc
init|=
operator|new
name|Throwable
index|[
literal|1
index|]
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
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
name|triedReopen
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|searcherManager
operator|.
name|maybeReopen
argument_list|()
expr_stmt|;
name|success
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAIL: unexpected exc"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|exc
index|[
literal|0
index|]
operator|=
name|e
expr_stmt|;
comment|// use success as the barrier here to make sure we see the write
name|success
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitEnterWarm
operator|.
name|await
argument_list|()
expr_stmt|;
name|searcherManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|awaitClose
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
try|try
block|{
name|searcherManager
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|assertFalse
argument_list|(
name|success
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|triedReopen
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|""
operator|+
name|exc
index|[
literal|0
index|]
argument_list|,
name|exc
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|es
operator|!=
literal|null
condition|)
block|{
name|es
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|es
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

