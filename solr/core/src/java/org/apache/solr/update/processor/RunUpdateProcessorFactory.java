begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|response
operator|.
name|SolrQueryResponse
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
name|*
import|;
end_import

begin_comment
comment|/**  * Executes the update commands using the underlying UpdateHandler.  * Almost all processor chains should end with an instance of   *<code>RunUpdateProcessorFactory</code> unless the user is explicitly   * executing the update commands in an alternative custom   *<code>UpdateRequestProcessorFactory</code>  *   * @since solr 1.3  * @see DistributingUpdateProcessorFactory  */
end_comment

begin_class
DECL|class|RunUpdateProcessorFactory
specifier|public
class|class
name|RunUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
block|{
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|RunUpdateProcessor
argument_list|(
name|req
argument_list|,
name|next
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|RunUpdateProcessor
class|class
name|RunUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|req
specifier|private
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|updateHandler
specifier|private
specifier|final
name|UpdateHandler
name|updateHandler
decl_stmt|;
DECL|field|changesSinceCommit
specifier|private
name|boolean
name|changesSinceCommit
init|=
literal|false
decl_stmt|;
DECL|method|RunUpdateProcessor
specifier|public
name|RunUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|updateHandler
operator|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|AtomicUpdateDocumentMerger
operator|.
name|isAtomicUpdate
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"RunUpdateProcessor has received an AddUpdateCommand containing a document that appears to still contain Atomic document update operations, most likely because DistributedUpdateProcessorFactory was explicitly disabled from this updateRequestProcessorChain"
argument_list|)
throw|;
block|}
name|updateHandler
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|changesSinceCommit
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processDelete
specifier|public
name|void
name|processDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|isDeleteById
argument_list|()
condition|)
block|{
name|updateHandler
operator|.
name|delete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateHandler
operator|.
name|deleteByQuery
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|changesSinceCommit
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processMergeIndexes
specifier|public
name|void
name|processMergeIndexes
parameter_list|(
name|MergeIndexesCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|updateHandler
operator|.
name|mergeIndexes
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|super
operator|.
name|processMergeIndexes
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|updateHandler
operator|.
name|commit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|super
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|softCommit
condition|)
block|{
comment|// a hard commit means we don't need to flush the transaction log
name|changesSinceCommit
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * @since Solr 1.4    */
annotation|@
name|Override
DECL|method|processRollback
specifier|public
name|void
name|processRollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|updateHandler
operator|.
name|rollback
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|super
operator|.
name|processRollback
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|changesSinceCommit
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|changesSinceCommit
operator|&&
name|updateHandler
operator|.
name|getUpdateLog
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|updateHandler
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|finish
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

