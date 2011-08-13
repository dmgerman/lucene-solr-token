begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Helper class for tracking autoCommit state.  *   * Note: This is purely an implementation detail of autoCommit and will  * definitely change in the future, so the interface should not be relied-upon  *   * Note: all access must be synchronized.  */
end_comment

begin_class
DECL|class|CommitTracker
specifier|final
class|class
name|CommitTracker
implements|implements
name|Runnable
block|{
DECL|field|log
specifier|protected
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CommitTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// scheduler delay for maxDoc-triggered autocommits
DECL|field|DOC_COMMIT_DELAY_MS
specifier|public
specifier|final
name|int
name|DOC_COMMIT_DELAY_MS
init|=
literal|250
decl_stmt|;
comment|// settings, not final so we can change them in testing
DECL|field|docsUpperBound
name|int
name|docsUpperBound
decl_stmt|;
DECL|field|timeUpperBound
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
argument_list|)
decl_stmt|;
DECL|field|pending
specifier|private
name|ScheduledFuture
name|pending
decl_stmt|;
comment|// state
DECL|field|docsSinceCommit
name|long
name|docsSinceCommit
decl_stmt|;
DECL|field|autoCommitCount
name|int
name|autoCommitCount
init|=
literal|0
decl_stmt|;
DECL|field|lastAddedTime
name|long
name|lastAddedTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|softCommit
specifier|private
name|boolean
name|softCommit
decl_stmt|;
DECL|field|waitSearcher
specifier|private
name|boolean
name|waitSearcher
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
name|waitSearcher
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
name|docsSinceCommit
operator|=
literal|0
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
name|waitSearcher
operator|=
name|waitSearcher
expr_stmt|;
name|SolrCore
operator|.
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
DECL|method|close
specifier|public
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
literal|true
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
specifier|synchronized
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
DECL|method|_scheduleCommitWithin
specifier|private
name|void
name|_scheduleCommitWithin
parameter_list|(
name|long
name|commitMaxTime
parameter_list|)
block|{
comment|// Check if there is a commit already scheduled for longer then this time
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
operator|>=
name|commitMaxTime
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
comment|// schedule a new commit
if|if
condition|(
name|pending
operator|==
literal|null
condition|)
block|{
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
name|boolean
name|addedDocument
parameter_list|(
name|int
name|commitWithin
parameter_list|)
block|{
name|docsSinceCommit
operator|++
expr_stmt|;
name|lastAddedTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|boolean
name|triggered
init|=
literal|false
decl_stmt|;
comment|// maxDocs-triggered autoCommit
if|if
condition|(
name|docsUpperBound
operator|>
literal|0
operator|&&
operator|(
name|docsSinceCommit
operator|>
name|docsUpperBound
operator|)
condition|)
block|{
name|_scheduleCommitWithin
argument_list|(
name|DOC_COMMIT_DELAY_MS
argument_list|)
expr_stmt|;
name|triggered
operator|=
literal|true
expr_stmt|;
block|}
comment|// maxTime-triggered autoCommit
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
name|triggered
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|triggered
return|;
block|}
comment|/** Inform tracker that a commit has occurred, cancel any pending commits */
DECL|method|didCommit
specifier|public
name|void
name|didCommit
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
comment|// let it start another one
block|}
name|docsSinceCommit
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Inform tracker that a rollback has occurred, cancel any pending commits */
DECL|method|didRollback
specifier|public
name|void
name|didRollback
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
comment|// let it start another one
block|}
name|docsSinceCommit
operator|=
literal|0
expr_stmt|;
block|}
comment|/** This is the worker part for the ScheduledFuture **/
DECL|method|run
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|long
name|started
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
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
name|waitSearcher
operator|=
name|waitSearcher
expr_stmt|;
name|command
operator|.
name|softCommit
operator|=
name|softCommit
expr_stmt|;
comment|// no need for command.maxOptimizeSegments = 1; since it is not optimizing
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
name|autoCommitCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"auto commit error..."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|pending
operator|=
literal|null
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// check if docs have been submitted since the commit started
if|if
condition|(
name|lastAddedTime
operator|>
name|started
condition|)
block|{
if|if
condition|(
name|docsUpperBound
operator|>
literal|0
operator|&&
name|docsSinceCommit
operator|>
name|docsUpperBound
condition|)
block|{
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|timeUpperBound
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// to facilitate testing: blocks if called during commit
DECL|method|getCommitCount
specifier|public
specifier|synchronized
name|int
name|getCommitCount
parameter_list|()
block|{
return|return
name|autoCommitCount
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
block|}
end_class

end_unit

