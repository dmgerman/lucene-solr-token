begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
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
name|SolrInputDocument
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
name|update
operator|.
name|CdcrUpdateLog
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
name|update
operator|.
name|UpdateLog
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
name|update
operator|.
name|processor
operator|.
name|CdcrUpdateProcessor
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
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_comment
comment|/**  * The replication logic. Given a {@link org.apache.solr.handler.CdcrReplicatorState}, it reads all the new entries  * in the update log and forward them to the target cluster. If an error occurs, the replication is stopped and  * will be tried again later.  */
end_comment

begin_class
DECL|class|CdcrReplicator
specifier|public
class|class
name|CdcrReplicator
implements|implements
name|Runnable
block|{
DECL|field|state
specifier|private
specifier|final
name|CdcrReplicatorState
name|state
decl_stmt|;
DECL|field|batchSize
specifier|private
specifier|final
name|int
name|batchSize
decl_stmt|;
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
DECL|method|CdcrReplicator
specifier|public
name|CdcrReplicator
parameter_list|(
name|CdcrReplicatorState
name|state
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|CdcrUpdateLog
operator|.
name|CdcrLogReader
name|logReader
init|=
name|state
operator|.
name|getLogReader
argument_list|()
decl_stmt|;
name|CdcrUpdateLog
operator|.
name|CdcrLogReader
name|subReader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|logReader
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Log reader for target {} is not initialised, it will be ignored."
argument_list|,
name|state
operator|.
name|getTargetCollection
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
comment|// create update request
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
comment|// Add the param to indicate the {@link CdcrUpdateProcessor} to keep the provided version number
name|req
operator|.
name|setParam
argument_list|(
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Start the benchmark timer
name|state
operator|.
name|getBenchmarkTimer
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
name|counter
init|=
literal|0
decl_stmt|;
name|subReader
operator|=
name|logReader
operator|.
name|getSubReader
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
name|batchSize
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|o
init|=
name|subReader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
break|break;
comment|// we have reached the end of the update logs, we should close the batch
if|if
condition|(
name|isDelete
argument_list|(
name|o
argument_list|)
condition|)
block|{
comment|/*           * Deletes are sent one at a time.           */
comment|// First send out current batch of SolrInputDocument, the non-deletes.
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
name|req
operator|.
name|getDocuments
argument_list|()
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
operator|&&
name|docs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|subReader
operator|.
name|resetToLastPosition
argument_list|()
expr_stmt|;
comment|// Push back the delete for now.
name|this
operator|.
name|sendRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// Send the batch update request
name|logReader
operator|.
name|forwardSeek
argument_list|(
name|subReader
argument_list|)
expr_stmt|;
comment|// Advance the main reader to just before the delete.
name|o
operator|=
name|subReader
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Read the delete again
name|counter
operator|+=
name|docs
operator|.
name|size
argument_list|()
expr_stmt|;
name|req
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Process Delete
name|this
operator|.
name|processUpdate
argument_list|(
name|o
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|this
operator|.
name|sendRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|logReader
operator|.
name|forwardSeek
argument_list|(
name|subReader
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
name|req
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|processUpdate
argument_list|(
name|o
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Send the final batch out.
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
name|req
operator|.
name|getDocuments
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|docs
operator|!=
literal|null
operator|&&
name|docs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|this
operator|.
name|sendRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|counter
operator|+=
name|docs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// we might have read a single commit operation and reached the end of the update logs
name|logReader
operator|.
name|forwardSeek
argument_list|(
name|subReader
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Forwarded {} updates to target {}"
argument_list|,
name|counter
argument_list|,
name|state
operator|.
name|getTargetCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// report error and update error stats
name|this
operator|.
name|handleException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// stop the benchmark timer
name|state
operator|.
name|getBenchmarkTimer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// ensure that the subreader is closed and the associated pointer is removed
if|if
condition|(
name|subReader
operator|!=
literal|null
condition|)
name|subReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sendRequest
specifier|private
name|void
name|sendRequest
parameter_list|(
name|UpdateRequest
name|req
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
throws|,
name|CdcrReplicatorException
block|{
name|UpdateResponse
name|rsp
init|=
name|req
operator|.
name|process
argument_list|(
name|state
operator|.
name|getClient
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getStatus
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|CdcrReplicatorException
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
throw|;
block|}
name|state
operator|.
name|resetConsecutiveErrors
argument_list|()
expr_stmt|;
block|}
DECL|method|isDelete
specifier|private
name|boolean
name|isDelete
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|List
name|entry
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
name|int
name|operationAndFlags
init|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|oper
init|=
name|operationAndFlags
operator|&
name|UpdateLog
operator|.
name|OPERATION_MASK
decl_stmt|;
return|return
name|oper
operator|==
name|UpdateLog
operator|.
name|DELETE_BY_QUERY
operator|||
name|oper
operator|==
name|UpdateLog
operator|.
name|DELETE
return|;
block|}
DECL|method|handleException
specifier|private
name|void
name|handleException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|CdcrReplicatorException
condition|)
block|{
name|UpdateRequest
name|req
init|=
operator|(
operator|(
name|CdcrReplicatorException
operator|)
name|e
operator|)
operator|.
name|req
decl_stmt|;
name|UpdateResponse
name|rsp
init|=
operator|(
operator|(
name|CdcrReplicatorException
operator|)
name|e
operator|)
operator|.
name|rsp
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to forward update request {} to target: {}. Got response {}"
argument_list|,
name|req
argument_list|,
name|state
operator|.
name|getTargetCollection
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|state
operator|.
name|reportError
argument_list|(
name|CdcrReplicatorState
operator|.
name|ErrorType
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|CloudSolrClient
operator|.
name|RouteException
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to forward update request to target: "
operator|+
name|state
operator|.
name|getTargetCollection
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|state
operator|.
name|reportError
argument_list|(
name|CdcrReplicatorState
operator|.
name|ErrorType
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to forward update request to target: "
operator|+
name|state
operator|.
name|getTargetCollection
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|state
operator|.
name|reportError
argument_list|(
name|CdcrReplicatorState
operator|.
name|ErrorType
operator|.
name|INTERNAL
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processUpdate
specifier|private
name|UpdateRequest
name|processUpdate
parameter_list|(
name|Object
name|o
parameter_list|,
name|UpdateRequest
name|req
parameter_list|)
block|{
comment|// should currently be a List<Oper,Ver,Doc/Id>
name|List
name|entry
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
name|int
name|operationAndFlags
init|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|oper
init|=
name|operationAndFlags
operator|&
name|UpdateLog
operator|.
name|OPERATION_MASK
decl_stmt|;
name|long
name|version
init|=
operator|(
name|Long
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// record the operation in the benchmark timer
name|state
operator|.
name|getBenchmarkTimer
argument_list|()
operator|.
name|incrementCounter
argument_list|(
name|oper
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|oper
condition|)
block|{
case|case
name|UpdateLog
operator|.
name|ADD
case|:
block|{
comment|// the version is already attached to the document
name|SolrInputDocument
name|sdoc
init|=
operator|(
name|SolrInputDocument
operator|)
name|entry
operator|.
name|get
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|sdoc
argument_list|)
expr_stmt|;
return|return
name|req
return|;
block|}
case|case
name|UpdateLog
operator|.
name|DELETE
case|:
block|{
name|byte
index|[]
name|idBytes
init|=
operator|(
name|byte
index|[]
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
operator|new
name|String
argument_list|(
name|idBytes
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|VERSION_FIELD
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|req
return|;
block|}
case|case
name|UpdateLog
operator|.
name|DELETE_BY_QUERY
case|:
block|{
name|String
name|query
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|req
operator|.
name|deleteByQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|VERSION_FIELD
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|req
return|;
block|}
case|case
name|UpdateLog
operator|.
name|COMMIT
case|:
block|{
return|return
literal|null
return|;
block|}
default|default:
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unknown Operation! "
operator|+
name|oper
argument_list|)
throw|;
block|}
block|}
comment|/**    * Exception to catch update request issues with the target cluster.    */
DECL|class|CdcrReplicatorException
specifier|public
class|class
name|CdcrReplicatorException
extends|extends
name|Exception
block|{
DECL|field|req
specifier|private
specifier|final
name|UpdateRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|private
specifier|final
name|UpdateResponse
name|rsp
decl_stmt|;
DECL|method|CdcrReplicatorException
specifier|public
name|CdcrReplicatorException
parameter_list|(
name|UpdateRequest
name|req
parameter_list|,
name|UpdateResponse
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

