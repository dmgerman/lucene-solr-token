begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
package|;
end_package

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
name|concurrent
operator|.
name|BlockingQueue
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
name|ExecutionException
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
name|Future
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  *   * Class to be used inside a  * {@link org.apache.lucene.gdata.search.index.GDataIndexer} to process the task  * queue. This class calls the commit method of the indexer if commit is  * scheduled.  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|IndexTask
class|class
name|IndexTask
implements|implements
name|Runnable
block|{
DECL|field|INNERLOG
specifier|private
specifier|static
specifier|final
name|Log
name|INNERLOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|IndexTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|stopped
specifier|private
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|GDataIndexer
name|indexer
decl_stmt|;
DECL|field|commit
specifier|protected
name|AtomicBoolean
name|commit
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|optimize
specifier|protected
name|AtomicBoolean
name|optimize
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/*      * keep protected for subclassing      */
DECL|field|taskQueue
specifier|protected
specifier|final
name|BlockingQueue
argument_list|<
name|Future
argument_list|<
name|IndexDocument
argument_list|>
argument_list|>
name|taskQueue
decl_stmt|;
DECL|method|IndexTask
name|IndexTask
parameter_list|(
specifier|final
name|GDataIndexer
name|indexer
parameter_list|,
specifier|final
name|BlockingQueue
argument_list|<
name|Future
argument_list|<
name|IndexDocument
argument_list|>
argument_list|>
name|taskQueue
parameter_list|)
block|{
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|taskQueue
operator|=
name|taskQueue
expr_stmt|;
block|}
comment|/**      * @see java.lang.Runnable#run()      */
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|this
operator|.
name|stopped
operator|.
name|get
argument_list|()
operator|||
name|this
operator|.
name|taskQueue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
try|try
block|{
comment|/*                  * get the future from the queue and wait until processing has                  * been done                  */
name|Future
argument_list|<
name|IndexDocument
argument_list|>
name|future
init|=
name|getTask
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|!=
literal|null
condition|)
block|{
name|IndexDocument
name|document
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|setOptimize
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|processDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|/*                      * the document contains the info for commit or optimize -->                      * this comes from the controller                      */
if|if
condition|(
name|document
operator|==
literal|null
operator|||
name|document
operator|.
name|commitAfter
argument_list|()
condition|)
name|this
operator|.
name|indexer
operator|.
name|commit
argument_list|(
name|document
operator|==
literal|null
condition|?
literal|false
else|:
name|this
operator|.
name|optimize
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|commit
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
name|this
operator|.
name|indexer
operator|.
name|commit
argument_list|(
name|this
operator|.
name|optimize
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|INNERLOG
operator|.
name|warn
argument_list|(
literal|"Queue is interrupted exiting IndexTask -- "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GdataIndexerException
name|e
parameter_list|)
block|{
comment|/*                  *                   * TODO fire callback here as well                  */
name|INNERLOG
operator|.
name|error
argument_list|(
literal|"can not retrieve Field from IndexDocument  "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|/*                  * TODO callback for fail this exception is caused by an                  * exception while processing the document. call back for failed                  * docs should be placed here                  */
name|INNERLOG
operator|.
name|error
argument_list|(
literal|"Future throws execution exception "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|INNERLOG
operator|.
name|error
argument_list|(
literal|"IOException thrown while processing document "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|/*                  * catch all to prevent the thread from dieing                  */
name|INNERLOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception while processing document -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|this
operator|.
name|indexer
operator|.
name|commit
argument_list|(
name|this
operator|.
name|optimize
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|INNERLOG
operator|.
name|warn
argument_list|(
literal|"commit on going down failed - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|setOptimize
specifier|protected
name|void
name|setOptimize
parameter_list|(
name|IndexDocument
name|document
parameter_list|)
block|{
if|if
condition|(
name|document
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|optimize
operator|.
name|set
argument_list|(
name|document
operator|.
name|optimizeAfter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * keep this protected for subclassing see TimedIndexTask!      */
DECL|method|getTask
specifier|protected
name|Future
argument_list|<
name|IndexDocument
argument_list|>
name|getTask
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|this
operator|.
name|taskQueue
operator|.
name|take
argument_list|()
return|;
block|}
DECL|method|processDocument
specifier|private
name|void
name|processDocument
parameter_list|(
name|IndexDocument
name|document
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*          * a null document is used for waking up the task if the indexer has          * been destroyed to finish up and commit. should I change this?! -->          * see TimedIndexTask#getTask() also!!          */
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
name|INNERLOG
operator|.
name|warn
argument_list|(
literal|"Can not process document -- is null -- run commit"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|document
operator|.
name|isDelete
argument_list|()
condition|)
block|{
name|this
operator|.
name|indexer
operator|.
name|deleteDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|document
operator|.
name|isInsert
argument_list|()
condition|)
block|{
name|this
operator|.
name|indexer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|document
operator|.
name|isUpdate
argument_list|()
condition|)
block|{
name|this
operator|.
name|indexer
operator|.
name|updateDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
return|return;
block|}
comment|/*          * that should not happen -- anyway skip the document and write it to          * the log          */
name|INNERLOG
operator|.
name|warn
argument_list|(
literal|"IndexDocument has no Action "
operator|+
name|document
argument_list|)
expr_stmt|;
block|}
DECL|method|isStopped
specifier|protected
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|this
operator|.
name|stopped
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|stop
specifier|protected
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|stopped
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

