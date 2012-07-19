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
name|CopyOnWriteArrayList
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|ReentrantLock
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
name|Analyzer
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
name|DirectoryReader
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

begin_comment
comment|// javadocs
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
name|IndexableField
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
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|SearcherFactory
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/**  * Utility class to manage sharing near-real-time searchers  * across multiple searching thread.  The difference vs  * SearcherManager is that this class enables individual  * requests to wait until specific indexing changes are  * visible.  *  *<p>You must create an IndexWriter, then create a {@link  * NRTManager.TrackingIndexWriter} from it, and pass that to the  * NRTManager.  You may want to create two NRTManagers, once  * that always applies deletes on refresh and one that does  * not.  In this case you should use a single {@link  * NRTManager.TrackingIndexWriter} instance for both.  *  *<p>Then, use {@link #acquire} to obtain the  * {@link IndexSearcher}, and {@link #release} (ideally,  * from within a<code>finally</code> clause) to release it.  *  *<p>NOTE: to use this class, you must call {@link #maybeRefresh()}  * periodically.  The {@link NRTManagerReopenThread} is a  * simple class to do this on a periodic basis, and reopens  * more quickly if a request is waiting.  If you implement  * your own reopener, be sure to call {@link  * #addWaitingListener} so your reopener is notified when a  * caller is waiting for a specific generation  * searcher.</p>  *  * @see SearcherFactory  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|NRTManager
specifier|public
class|class
name|NRTManager
extends|extends
name|ReferenceManager
argument_list|<
name|IndexSearcher
argument_list|>
block|{
DECL|field|MAX_SEARCHER_GEN
specifier|private
specifier|static
specifier|final
name|long
name|MAX_SEARCHER_GEN
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|TrackingIndexWriter
name|writer
decl_stmt|;
DECL|field|waitingListeners
specifier|private
specifier|final
name|List
argument_list|<
name|WaitingListener
argument_list|>
name|waitingListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|WaitingListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|genLock
specifier|private
specifier|final
name|ReentrantLock
name|genLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
empty_stmt|;
DECL|field|newGeneration
specifier|private
specifier|final
name|Condition
name|newGeneration
init|=
name|genLock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
DECL|field|searcherFactory
specifier|private
specifier|final
name|SearcherFactory
name|searcherFactory
decl_stmt|;
DECL|field|searchingGen
specifier|private
specifier|volatile
name|long
name|searchingGen
decl_stmt|;
comment|/**    * Create new NRTManager.    *     * @param writer TrackingIndexWriter to open near-real-time    *        readers    * @param searcherFactory An optional {@link SearcherFactory}. Pass    *<code>null</code> if you don't require the searcher to be warmed    *        before going live or other custom behavior.    */
DECL|method|NRTManager
specifier|public
name|NRTManager
parameter_list|(
name|TrackingIndexWriter
name|writer
parameter_list|,
name|SearcherFactory
name|searcherFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|writer
argument_list|,
name|searcherFactory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: just like {@link    * #NRTManager(TrackingIndexWriter,SearcherFactory)},    * but you can also specify whether each reopened searcher must    * apply deletes.  This is useful for cases where certain    * uses can tolerate seeing some deleted docs, since    * reopen time is faster if deletes need not be applied. */
DECL|method|NRTManager
specifier|public
name|NRTManager
parameter_list|(
name|TrackingIndexWriter
name|writer
parameter_list|,
name|SearcherFactory
name|searcherFactory
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
if|if
condition|(
name|searcherFactory
operator|==
literal|null
condition|)
block|{
name|searcherFactory
operator|=
operator|new
name|SearcherFactory
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|searcherFactory
operator|=
name|searcherFactory
expr_stmt|;
name|current
operator|=
name|SearcherManager
operator|.
name|getSearcher
argument_list|(
name|searcherFactory
argument_list|,
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
operator|.
name|getIndexWriter
argument_list|()
argument_list|,
name|applyAllDeletes
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decRef
specifier|protected
name|void
name|decRef
parameter_list|(
name|IndexSearcher
name|reference
parameter_list|)
throws|throws
name|IOException
block|{
name|reference
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
DECL|method|tryIncRef
specifier|protected
name|boolean
name|tryIncRef
parameter_list|(
name|IndexSearcher
name|reference
parameter_list|)
block|{
return|return
name|reference
operator|.
name|getIndexReader
argument_list|()
operator|.
name|tryIncRef
argument_list|()
return|;
block|}
comment|/** NRTManager invokes this interface to notify it when a    *  caller is waiting for a specific generation searcher    *  to be visible. */
DECL|interface|WaitingListener
specifier|public
specifier|static
interface|interface
name|WaitingListener
block|{
DECL|method|waiting
specifier|public
name|void
name|waiting
parameter_list|(
name|long
name|targetGen
parameter_list|)
function_decl|;
block|}
comment|/** Adds a listener, to be notified when a caller is    *  waiting for a specific generation searcher to be    *  visible. */
DECL|method|addWaitingListener
specifier|public
name|void
name|addWaitingListener
parameter_list|(
name|WaitingListener
name|l
parameter_list|)
block|{
name|waitingListeners
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
comment|/** Remove a listener added with {@link    *  #addWaitingListener}. */
DECL|method|removeWaitingListener
specifier|public
name|void
name|removeWaitingListener
parameter_list|(
name|WaitingListener
name|l
parameter_list|)
block|{
name|waitingListeners
operator|.
name|remove
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
comment|/** Class that tracks changes to a delegated    * IndexWriter.  Create this class (passing your    * IndexWriter), and then pass this class to NRTManager.    * Be sure to make all changes via the    * TrackingIndexWriter, otherwise NRTManager won't know    * about the changes.    *    * @lucene.experimental */
DECL|class|TrackingIndexWriter
specifier|public
specifier|static
class|class
name|TrackingIndexWriter
block|{
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|indexingGen
specifier|private
specifier|final
name|AtomicLong
name|indexingGen
init|=
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|TrackingIndexWriter
specifier|public
name|TrackingIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
DECL|method|updateDocument
specifier|public
name|long
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|d
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|d
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|updateDocument
specifier|public
name|long
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|d
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|updateDocuments
specifier|public
name|long
name|updateDocuments
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
name|docs
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|updateDocuments
specifier|public
name|long
name|updateDocuments
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
name|docs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Term
modifier|...
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|terms
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Query
modifier|...
name|queries
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|queries
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteAll
specifier|public
name|long
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocument
specifier|public
name|long
name|addDocument
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|d
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocuments
specifier|public
name|long
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocument
specifier|public
name|long
name|addDocument
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocuments
specifier|public
name|long
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addIndexes
specifier|public
name|long
name|addIndexes
parameter_list|(
name|Directory
modifier|...
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addIndexes
specifier|public
name|long
name|addIndexes
parameter_list|(
name|IndexReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getIndexWriter
specifier|public
name|IndexWriter
name|getIndexWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
DECL|method|getAndIncrementGeneration
name|long
name|getAndIncrementGeneration
parameter_list|()
block|{
return|return
name|indexingGen
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
block|}
comment|/**    * Waits for the target generation to become visible in    * the searcher.    * If the current searcher is older than the    * target generation, this method will block    * until the searcher is reopened, by another via    * {@link #maybeRefresh} or until the {@link NRTManager} is closed.    *     * @param targetGen the generation to wait for    */
DECL|method|waitForGeneration
specifier|public
name|void
name|waitForGeneration
parameter_list|(
name|long
name|targetGen
parameter_list|)
block|{
name|waitForGeneration
argument_list|(
name|targetGen
argument_list|,
operator|-
literal|1
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Waits for the target generation to become visible in    * the searcher.  If the current searcher is older than    * the target generation, this method will block until the    * searcher has been reopened by another thread via    * {@link #maybeRefresh}, the given waiting time has elapsed, or until    * the NRTManager is closed.    *<p>    * NOTE: if the waiting time elapses before the requested target generation is    * available the current {@link SearcherManager} is returned instead.    *     * @param targetGen    *          the generation to wait for    * @param time    *          the time to wait for the target generation    * @param unit    *          the waiting time's time unit    */
DECL|method|waitForGeneration
specifier|public
name|void
name|waitForGeneration
parameter_list|(
name|long
name|targetGen
parameter_list|,
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
try|try
block|{
specifier|final
name|long
name|curGen
init|=
name|writer
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetGen
operator|>
name|curGen
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"targetGen="
operator|+
name|targetGen
operator|+
literal|" was never returned by this NRTManager instance (current gen="
operator|+
name|curGen
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|genLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|targetGen
operator|>
name|searchingGen
condition|)
block|{
for|for
control|(
name|WaitingListener
name|listener
range|:
name|waitingListeners
control|)
block|{
name|listener
operator|.
name|waiting
argument_list|(
name|targetGen
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|targetGen
operator|>
name|searchingGen
condition|)
block|{
if|if
condition|(
operator|!
name|waitOnGenCondition
argument_list|(
name|time
argument_list|,
name|unit
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|genLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
block|}
DECL|method|waitOnGenCondition
specifier|private
name|boolean
name|waitOnGenCondition
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
assert|assert
name|genLock
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
if|if
condition|(
name|time
operator|<
literal|0
condition|)
block|{
name|newGeneration
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|newGeneration
operator|.
name|await
argument_list|(
name|time
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
comment|/** Returns generation of current searcher. */
DECL|method|getCurrentSearchingGen
specifier|public
name|long
name|getCurrentSearchingGen
parameter_list|()
block|{
return|return
name|searchingGen
return|;
block|}
DECL|field|lastRefreshGen
specifier|private
name|long
name|lastRefreshGen
decl_stmt|;
annotation|@
name|Override
DECL|method|refreshIfNeeded
specifier|protected
name|IndexSearcher
name|refreshIfNeeded
parameter_list|(
name|IndexSearcher
name|referenceToRefresh
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Record gen as of when reopen started:
name|lastRefreshGen
operator|=
name|writer
operator|.
name|getAndIncrementGeneration
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|referenceToRefresh
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
assert|assert
name|r
operator|instanceof
name|DirectoryReader
operator|:
literal|"searcher's IndexReader should be a DirectoryReader, but got "
operator|+
name|r
assert|;
specifier|final
name|DirectoryReader
name|dirReader
init|=
operator|(
name|DirectoryReader
operator|)
name|r
decl_stmt|;
name|IndexSearcher
name|newSearcher
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|dirReader
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
specifier|final
name|IndexReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|dirReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReader
operator|!=
literal|null
condition|)
block|{
name|newSearcher
operator|=
name|SearcherManager
operator|.
name|getSearcher
argument_list|(
name|searcherFactory
argument_list|,
name|newReader
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newSearcher
return|;
block|}
annotation|@
name|Override
DECL|method|afterRefresh
specifier|protected
name|void
name|afterRefresh
parameter_list|()
block|{
name|genLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|searchingGen
operator|!=
name|MAX_SEARCHER_GEN
condition|)
block|{
comment|// update searchingGen:
assert|assert
name|lastRefreshGen
operator|>=
name|searchingGen
assert|;
name|searchingGen
operator|=
name|lastRefreshGen
expr_stmt|;
block|}
comment|// wake up threads if we have a new generation:
name|newGeneration
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|genLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|afterClose
specifier|protected
specifier|synchronized
name|void
name|afterClose
parameter_list|()
throws|throws
name|IOException
block|{
name|genLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// max it out to make sure nobody can wait on another gen
name|searchingGen
operator|=
name|MAX_SEARCHER_GEN
expr_stmt|;
name|newGeneration
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|genLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns<code>true</code> if no changes have occured since this searcher    * ie. reader was opened, otherwise<code>false</code>.    * @see DirectoryReader#isCurrent()     */
DECL|method|isSearcherCurrent
specifier|public
name|boolean
name|isSearcherCurrent
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|IndexReader
name|r
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
assert|assert
name|r
operator|instanceof
name|DirectoryReader
operator|:
literal|"searcher's IndexReader should be a DirectoryReader, but got "
operator|+
name|r
assert|;
return|return
operator|(
operator|(
name|DirectoryReader
operator|)
name|r
operator|)
operator|.
name|isCurrent
argument_list|()
return|;
block|}
finally|finally
block|{
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

