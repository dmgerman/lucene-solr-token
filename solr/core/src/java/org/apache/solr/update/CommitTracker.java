begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|ScheduledExecutorService
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
name|ScheduledFuture
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
name|AtomicInteger
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|LocalSolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DefaultSolrThreadFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Helper class for tracking autoCommit state.  *   * Note: This is purely an implementation detail of autoCommit and will  * definitely change in the future, so the interface should not be relied-upon  *   * Note: all access must be synchronized.  *   * Public for tests.  */
end_comment

begin_class
DECL|class|CommitTracker
specifier|public
specifier|final
class|class
name|CommitTracker
implements|implements
name|Runnable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|// scheduler delay for maxDoc-triggered autocommits
DECL|field|DOC_COMMIT_DELAY_MS
specifier|public
specifier|static
specifier|final
name|int
name|DOC_COMMIT_DELAY_MS
init|=
literal|1
decl_stmt|;
comment|// settings, not final so we can change them in testing
DECL|field|docsUpperBound
specifier|private
name|int
name|docsUpperBound
decl_stmt|;
DECL|field|timeUpperBound
specifier|private
name|long
name|timeUpperBound
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduler
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"commitScheduler"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|pending
specifier|private
name|ScheduledFuture
name|pending
decl_stmt|;
comment|// state
DECL|field|docsSinceCommit
specifier|private
name|AtomicLong
name|docsSinceCommit
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|autoCommitCount
specifier|private
name|AtomicInteger
name|autoCommitCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|softCommit
specifier|private
specifier|final
name|boolean
name|softCommit
decl_stmt|;
DECL|field|openSearcher
specifier|private
name|boolean
name|openSearcher
decl_stmt|;
DECL|field|WAIT_SEARCHER
specifier|private
specifier|static
specifier|final
name|boolean
name|WAIT_SEARCHER
init|=
literal|true
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|CommitTracker
specifier|public
name|CommitTracker
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|int
name|docsUpperBound
parameter_list|,
name|int
name|timeUpperBound
parameter_list|,
name|boolean
name|openSearcher
parameter_list|,
name|boolean
name|softCommit
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|docsUpperBound
operator|=
name|docsUpperBound
expr_stmt|;
name|this
operator|.
name|timeUpperBound
operator|=
name|timeUpperBound
expr_stmt|;
name|this
operator|.
name|softCommit
operator|=
name|softCommit
expr_stmt|;
name|this
operator|.
name|openSearcher
operator|=
name|openSearcher
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|name
operator|+
literal|" AutoCommit: "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|getOpenSearcher
specifier|public
name|boolean
name|getOpenSearcher
parameter_list|()
block|{
return|return
name|openSearcher
return|;
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
condition|)
block|{
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
block|}
name|scheduler
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/** schedule individual commits */
DECL|method|scheduleCommitWithin
specifier|public
name|void
name|scheduleCommitWithin
parameter_list|(
name|long
name|commitMaxTime
parameter_list|)
block|{
name|_scheduleCommitWithin
argument_list|(
name|commitMaxTime
argument_list|)
expr_stmt|;
block|}
DECL|method|cancelPendingCommit
specifier|public
name|void
name|cancelPendingCommit
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
condition|)
block|{
name|boolean
name|canceled
init|=
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|canceled
condition|)
block|{
name|pending
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|_scheduleCommitWithinIfNeeded
specifier|private
name|void
name|_scheduleCommitWithinIfNeeded
parameter_list|(
name|long
name|commitWithin
parameter_list|)
block|{
name|long
name|ctime
init|=
operator|(
name|commitWithin
operator|>
literal|0
operator|)
condition|?
name|commitWithin
else|:
name|timeUpperBound
decl_stmt|;
if|if
condition|(
name|ctime
operator|>
literal|0
condition|)
block|{
name|_scheduleCommitWithin
argument_list|(
name|ctime
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|_scheduleCommitWithin
specifier|private
name|void
name|_scheduleCommitWithin
parameter_list|(
name|long
name|commitMaxTime
parameter_list|)
block|{
if|if
condition|(
name|commitMaxTime
operator|<=
literal|0
condition|)
return|return;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
operator|&&
name|pending
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|<=
name|commitMaxTime
condition|)
block|{
comment|// There is already a pending commit that will happen first, so
comment|// nothing else to do here.
comment|// log.info("###returning since getDelay()==" + pending.getDelay(TimeUnit.MILLISECONDS) + " less than " + commitMaxTime);
return|return;
block|}
if|if
condition|(
name|pending
operator|!=
literal|null
condition|)
block|{
comment|// we need to schedule a commit to happen sooner than the existing one,
comment|// so lets try to cancel the existing one first.
name|boolean
name|canceled
init|=
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|canceled
condition|)
block|{
comment|// It looks like we can't cancel... it must have just started running!
comment|// this is possible due to thread scheduling delays and a low commitMaxTime.
comment|// Nothing else to do since we obviously can't schedule our commit *before*
comment|// the one that just started running (or has just completed).
comment|// log.info("###returning since cancel failed");
return|return;
block|}
block|}
comment|// log.info("###scheduling for " + commitMaxTime);
comment|// schedule our new commit
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|commitMaxTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Indicate that documents have been added    */
DECL|method|addedDocument
specifier|public
name|void
name|addedDocument
parameter_list|(
name|int
name|commitWithin
parameter_list|)
block|{
comment|// maxDocs-triggered autoCommit.  Use == instead of> so we only trigger once on the way up
if|if
condition|(
name|docsUpperBound
operator|>
literal|0
condition|)
block|{
name|long
name|docs
init|=
name|docsSinceCommit
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|docs
operator|==
name|docsUpperBound
operator|+
literal|1
condition|)
block|{
comment|// reset the count here instead of run() so we don't miss other documents being added
name|docsSinceCommit
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|_scheduleCommitWithin
argument_list|(
name|DOC_COMMIT_DELAY_MS
argument_list|)
expr_stmt|;
block|}
block|}
comment|// maxTime-triggered autoCommit
name|_scheduleCommitWithinIfNeeded
argument_list|(
name|commitWithin
argument_list|)
expr_stmt|;
block|}
comment|/**     * Indicate that documents have been deleted    */
DECL|method|deletedDocument
specifier|public
name|void
name|deletedDocument
parameter_list|(
name|int
name|commitWithin
parameter_list|)
block|{
name|_scheduleCommitWithinIfNeeded
argument_list|(
name|commitWithin
argument_list|)
expr_stmt|;
block|}
comment|/** Inform tracker that a commit has occurred */
DECL|method|didCommit
specifier|public
name|void
name|didCommit
parameter_list|()
block|{   }
comment|/** Inform tracker that a rollback has occurred, cancel any pending commits */
DECL|method|didRollback
specifier|public
name|void
name|didRollback
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
condition|)
block|{
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
comment|// let it start another one
block|}
name|docsSinceCommit
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** This is the worker part for the ScheduledFuture **/
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// log.info("###start commit. pending=null");
name|pending
operator|=
literal|null
expr_stmt|;
comment|// allow a new commit to be scheduled
block|}
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|CommitUpdateCommand
name|command
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|command
operator|.
name|openSearcher
operator|=
name|openSearcher
expr_stmt|;
name|command
operator|.
name|waitSearcher
operator|=
name|WAIT_SEARCHER
expr_stmt|;
name|command
operator|.
name|softCommit
operator|=
name|softCommit
expr_stmt|;
if|if
condition|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|!=
literal|null
operator|&&
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|isLeader
argument_list|()
operator|&&
operator|!
name|softCommit
condition|)
block|{
name|command
operator|.
name|version
operator|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|getVersionInfo
argument_list|()
operator|.
name|getNewClock
argument_list|()
expr_stmt|;
block|}
comment|// no need for command.maxOptimizeSegments = 1; since it is not optimizing
comment|// we increment this *before* calling commit because it was causing a race
comment|// in the tests (the new searcher was registered and the test proceeded
comment|// to check the commit count before we had incremented it.)
name|autoCommitCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|commit
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"auto commit error..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// log.info("###done committing");
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// to facilitate testing: blocks if called during commit
DECL|method|getCommitCount
specifier|public
name|int
name|getCommitCount
parameter_list|()
block|{
return|return
name|autoCommitCount
operator|.
name|get
argument_list|()
return|;
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
name|timeUpperBound
operator|>
literal|0
operator|||
name|docsUpperBound
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|timeUpperBound
operator|>
literal|0
condition|?
operator|(
literal|"if uncommited for "
operator|+
name|timeUpperBound
operator|+
literal|"ms; "
operator|)
else|:
literal|""
operator|)
operator|+
operator|(
name|docsUpperBound
operator|>
literal|0
condition|?
operator|(
literal|"if "
operator|+
name|docsUpperBound
operator|+
literal|" uncommited docs "
operator|)
else|:
literal|""
operator|)
return|;
block|}
else|else
block|{
return|return
literal|"disabled"
return|;
block|}
block|}
DECL|method|getTimeUpperBound
specifier|public
name|long
name|getTimeUpperBound
parameter_list|()
block|{
return|return
name|timeUpperBound
return|;
block|}
DECL|method|getDocsUpperBound
name|int
name|getDocsUpperBound
parameter_list|()
block|{
return|return
name|docsUpperBound
return|;
block|}
DECL|method|setDocsUpperBound
name|void
name|setDocsUpperBound
parameter_list|(
name|int
name|docsUpperBound
parameter_list|)
block|{
name|this
operator|.
name|docsUpperBound
operator|=
name|docsUpperBound
expr_stmt|;
block|}
comment|// only for testing - not thread safe
DECL|method|setTimeUpperBound
specifier|public
name|void
name|setTimeUpperBound
parameter_list|(
name|long
name|timeUpperBound
parameter_list|)
block|{
name|this
operator|.
name|timeUpperBound
operator|=
name|timeUpperBound
expr_stmt|;
block|}
comment|// only for testing - not thread safe
DECL|method|setOpenSearcher
specifier|public
name|void
name|setOpenSearcher
parameter_list|(
name|boolean
name|openSearcher
parameter_list|)
block|{
name|this
operator|.
name|openSearcher
operator|=
name|openSearcher
expr_stmt|;
block|}
block|}
end_class

end_unit

