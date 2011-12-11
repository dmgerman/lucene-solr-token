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
name|Closeable
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
name|CorruptIndexException
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
name|IOUtils
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
comment|/**  * Utility class to manage sharing near-real-time searchers  * across multiple searching threads.  *  *<p>NOTE: to use this class, you must call {@link #maybeReopen(boolean)}  * periodically.  The {@link NRTManagerReopenThread} is a  * simple class to do this on a periodic basis.  If you  * implement your own reopener, be sure to call {@link  * #addWaitingListener} so your reopener is notified when a  * caller is waiting for a specific generation searcher.</p>  *  * @see SearcherFactory  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|NRTManager
specifier|public
class|class
name|NRTManager
implements|implements
name|Closeable
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
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|withoutDeletes
specifier|private
specifier|final
name|SearcherManagerRef
name|withoutDeletes
decl_stmt|;
DECL|field|withDeletes
specifier|private
specifier|final
name|SearcherManagerRef
name|withDeletes
decl_stmt|;
DECL|field|indexingGen
specifier|private
specifier|final
name|AtomicLong
name|indexingGen
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
DECL|field|reopenLock
specifier|private
specifier|final
name|ReentrantLock
name|reopenLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|newGeneration
specifier|private
specifier|final
name|Condition
name|newGeneration
init|=
name|reopenLock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**    * Create new NRTManager.    *     * @param writer IndexWriter to open near-real-time    *        readers    * @param searcherFactory An optional {@link SearcherFactory}. Pass    *<code>null</code> if you don't require the searcher to be warmed    *        before going live or other custom behavior.    */
DECL|method|NRTManager
specifier|public
name|NRTManager
parameter_list|(
name|IndexWriter
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
comment|/**    * Expert: just like {@link    * #NRTManager(IndexWriter,SearcherFactory)},    * but you can also specify whether every searcher must    * apply deletes.  This is useful for cases where certain    * uses can tolerate seeing some deleted docs, since    * reopen time is faster if deletes need not be applied. */
DECL|method|NRTManager
specifier|public
name|NRTManager
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|SearcherFactory
name|searcherFactory
parameter_list|,
name|boolean
name|alwaysApplyDeletes
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
name|alwaysApplyDeletes
condition|)
block|{
name|withoutDeletes
operator|=
name|withDeletes
operator|=
operator|new
name|SearcherManagerRef
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
operator|new
name|SearcherManager
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|,
name|searcherFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|withDeletes
operator|=
operator|new
name|SearcherManagerRef
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
operator|new
name|SearcherManager
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|,
name|searcherFactory
argument_list|)
argument_list|)
expr_stmt|;
name|withoutDeletes
operator|=
operator|new
name|SearcherManagerRef
argument_list|(
literal|false
argument_list|,
literal|0
argument_list|,
operator|new
name|SearcherManager
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|,
name|searcherFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexingGen
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
name|boolean
name|requiresDeletes
parameter_list|,
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
name|CorruptIndexException
throws|,
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
name|CorruptIndexException
throws|,
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
comment|/**    * Waits for a given {@link SearcherManager} target generation to be available    * via {@link #getSearcherManager(boolean)}. If the current generation is less    * than the given target generation this method will block until the    * correspondent {@link SearcherManager} is reopened by another thread via    * {@link #maybeReopen(boolean)} or until the {@link NRTManager} is closed.    *     * @param targetGen the generation to wait for    * @param requireDeletes<code>true</code> iff the generation requires deletes to be applied otherwise<code>false</code>    * @return the {@link SearcherManager} with the given target generation    */
DECL|method|waitForGeneration
specifier|public
name|SearcherManager
name|waitForGeneration
parameter_list|(
name|long
name|targetGen
parameter_list|,
name|boolean
name|requireDeletes
parameter_list|)
block|{
return|return
name|waitForGeneration
argument_list|(
name|targetGen
argument_list|,
name|requireDeletes
argument_list|,
operator|-
literal|1
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
return|;
block|}
comment|/**    * Waits for a given {@link SearcherManager} target generation to be available    * via {@link #getSearcherManager(boolean)}. If the current generation is less    * than the given target generation this method will block until the    * correspondent {@link SearcherManager} is reopened by another thread via    * {@link #maybeReopen(boolean)}, the given waiting time has elapsed, or until    * the {@link NRTManager} is closed.    *<p>    * NOTE: if the waiting time elapses before the requested target generation is    * available the latest {@link SearcherManager} is returned instead.    *     * @param targetGen    *          the generation to wait for    * @param requireDeletes    *<code>true</code> iff the generation requires deletes to be    *          applied otherwise<code>false</code>    * @param time    *          the time to wait for the target generation    * @param unit    *          the waiting time's time unit    * @return the {@link SearcherManager} with the given target generation or the    *         latest {@link SearcherManager} if the waiting time elapsed before    *         the requested generation is available.    */
DECL|method|waitForGeneration
specifier|public
name|SearcherManager
name|waitForGeneration
parameter_list|(
name|long
name|targetGen
parameter_list|,
name|boolean
name|requireDeletes
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
name|indexingGen
operator|.
name|get
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
name|reopenLock
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
name|getCurrentSearchingGen
argument_list|(
name|requireDeletes
argument_list|)
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
name|requireDeletes
argument_list|,
name|targetGen
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|targetGen
operator|>
name|getCurrentSearchingGen
argument_list|(
name|requireDeletes
argument_list|)
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
return|return
name|getSearcherManager
argument_list|(
name|requireDeletes
argument_list|)
return|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|reopenLock
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
return|return
name|getSearcherManager
argument_list|(
name|requireDeletes
argument_list|)
return|;
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
name|reopenLock
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
parameter_list|(
name|boolean
name|applyAllDeletes
parameter_list|)
block|{
if|if
condition|(
name|applyAllDeletes
condition|)
block|{
return|return
name|withDeletes
operator|.
name|generation
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|withoutDeletes
operator|.
name|generation
argument_list|,
name|withDeletes
operator|.
name|generation
argument_list|)
return|;
block|}
block|}
DECL|method|maybeReopen
specifier|public
name|boolean
name|maybeReopen
parameter_list|(
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reopenLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|SearcherManagerRef
name|reference
init|=
name|applyAllDeletes
condition|?
name|withDeletes
else|:
name|withoutDeletes
decl_stmt|;
comment|// Mark gen as of when reopen started:
specifier|final
name|long
name|newSearcherGen
init|=
name|indexingGen
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|boolean
name|setSearchGen
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|reference
operator|.
name|generation
operator|==
name|MAX_SEARCHER_GEN
condition|)
block|{
name|newGeneration
operator|.
name|signalAll
argument_list|()
expr_stmt|;
comment|// wake up threads if we have a new generation
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|setSearchGen
operator|=
name|reference
operator|.
name|manager
operator|.
name|isSearcherCurrent
argument_list|()
operator|)
condition|)
block|{
name|setSearchGen
operator|=
name|reference
operator|.
name|manager
operator|.
name|maybeReopen
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|setSearchGen
condition|)
block|{
name|reference
operator|.
name|generation
operator|=
name|newSearcherGen
expr_stmt|;
comment|// update searcher gen
name|newGeneration
operator|.
name|signalAll
argument_list|()
expr_stmt|;
comment|// wake up threads if we have a new generation
block|}
return|return
name|setSearchGen
return|;
block|}
finally|finally
block|{
name|reopenLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Close this NRTManager to future searching. Any searches still in process in    * other threads won't be affected, and they should still call    * {@link SearcherManager#release(IndexSearcher)} after they are done.    *     *<p>    *<b>NOTE</b>: caller must separately close the writer.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reopenLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|withDeletes
argument_list|,
name|withoutDeletes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// make sure we signal even if close throws an exception
name|newGeneration
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reopenLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
assert|assert
name|withDeletes
operator|.
name|generation
operator|==
name|MAX_SEARCHER_GEN
operator|&&
name|withoutDeletes
operator|.
name|generation
operator|==
name|MAX_SEARCHER_GEN
assert|;
block|}
block|}
comment|/**    * Returns a {@link SearcherManager}. If<code>applyAllDeletes</code> is    *<code>true</code> the returned manager is guaranteed to have all deletes    * applied on the last reopen. Otherwise the latest manager with or without deletes    * is returned.    */
DECL|method|getSearcherManager
specifier|public
name|SearcherManager
name|getSearcherManager
parameter_list|(
name|boolean
name|applyAllDeletes
parameter_list|)
block|{
if|if
condition|(
name|applyAllDeletes
condition|)
block|{
return|return
name|withDeletes
operator|.
name|manager
return|;
block|}
else|else
block|{
if|if
condition|(
name|withDeletes
operator|.
name|generation
operator|>
name|withoutDeletes
operator|.
name|generation
condition|)
block|{
return|return
name|withDeletes
operator|.
name|manager
return|;
block|}
else|else
block|{
return|return
name|withoutDeletes
operator|.
name|manager
return|;
block|}
block|}
block|}
DECL|class|SearcherManagerRef
specifier|static
specifier|final
class|class
name|SearcherManagerRef
implements|implements
name|Closeable
block|{
DECL|field|applyDeletes
specifier|final
name|boolean
name|applyDeletes
decl_stmt|;
DECL|field|generation
specifier|volatile
name|long
name|generation
decl_stmt|;
DECL|field|manager
specifier|final
name|SearcherManager
name|manager
decl_stmt|;
DECL|method|SearcherManagerRef
name|SearcherManagerRef
parameter_list|(
name|boolean
name|applyDeletes
parameter_list|,
name|long
name|generation
parameter_list|,
name|SearcherManager
name|manager
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|applyDeletes
operator|=
name|applyDeletes
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|generation
operator|=
name|MAX_SEARCHER_GEN
expr_stmt|;
comment|// max it out to make sure nobody can wait on another gen
name|manager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

