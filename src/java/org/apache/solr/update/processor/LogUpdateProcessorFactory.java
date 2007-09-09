begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|logging
operator|.
name|Level
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
name|SolrParams
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
name|util
operator|.
name|DOMUtil
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
name|util
operator|.
name|NamedList
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
name|request
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
name|AddUpdateCommand
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
name|CommitUpdateCommand
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
name|DeleteUpdateCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * A logging processor.  This keeps track of all commands that have passed through  * the chain and prints them on finish();  *   * If the Log level is not INFO the processor will not be created or added to the chain  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|LogUpdateProcessorFactory
specifier|public
class|class
name|LogUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
block|{
DECL|field|maxNumToLog
name|int
name|maxNumToLog
init|=
literal|8
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|,
specifier|final
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|args
init|=
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|maxNumToLog
operator|=
name|params
operator|.
name|getInt
argument_list|(
literal|"maxNumToLog"
argument_list|,
name|maxNumToLog
argument_list|)
expr_stmt|;
block|}
block|}
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
name|boolean
name|doLog
init|=
name|LogUpdateProcessor
operator|.
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
decl_stmt|;
comment|// LogUpdateProcessor.log.severe("Will Log=" + doLog);
if|if
condition|(
name|doLog
condition|)
block|{
comment|// only create the log processor if we will use it
return|return
operator|new
name|LogUpdateProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|this
argument_list|,
name|next
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_class
DECL|class|LogUpdateProcessor
class|class
name|LogUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|req
specifier|private
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|private
specifier|final
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|next
specifier|private
specifier|final
name|UpdateRequestProcessor
name|next
decl_stmt|;
DECL|field|toLog
specifier|private
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|toLog
decl_stmt|;
DECL|field|numAdds
name|int
name|numAdds
decl_stmt|;
DECL|field|numDeletes
name|int
name|numDeletes
decl_stmt|;
comment|// hold on to the added list for logging and the response
DECL|field|adds
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|adds
decl_stmt|;
DECL|field|deletes
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deletes
decl_stmt|;
DECL|field|maxNumToLog
specifier|private
specifier|final
name|int
name|maxNumToLog
decl_stmt|;
DECL|method|LogUpdateProcessor
specifier|public
name|LogUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|LogUpdateProcessorFactory
name|factory
parameter_list|,
name|UpdateRequestProcessor
name|next
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
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
name|maxNumToLog
operator|=
name|factory
operator|.
name|maxNumToLog
expr_stmt|;
comment|// TODO: make configurable
comment|// TODO: make log level configurable as well, or is that overkill?
comment|// (ryan) maybe?  I added it mostly to show that it *can* be configurable
name|this
operator|.
name|toLog
operator|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
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
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
comment|// Add a list of added id's to the response
if|if
condition|(
name|adds
operator|==
literal|null
condition|)
block|{
name|adds
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|toLog
operator|.
name|add
argument_list|(
literal|"add"
argument_list|,
name|adds
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|adds
operator|.
name|size
argument_list|()
operator|<
name|maxNumToLog
condition|)
block|{
name|adds
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|getPrintableId
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numAdds
operator|++
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
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|id
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|deletes
operator|==
literal|null
condition|)
block|{
name|deletes
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|toLog
operator|.
name|add
argument_list|(
literal|"delete"
argument_list|,
name|deletes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deletes
operator|.
name|size
argument_list|()
operator|<
name|maxNumToLog
condition|)
block|{
name|deletes
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|toLog
operator|.
name|size
argument_list|()
operator|<
name|maxNumToLog
condition|)
block|{
name|toLog
operator|.
name|add
argument_list|(
literal|"deleteByQuery"
argument_list|,
name|cmd
operator|.
name|query
argument_list|)
expr_stmt|;
block|}
block|}
name|numDeletes
operator|++
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
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|toLog
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|optimize
condition|?
literal|"optimize"
else|:
literal|"commit"
argument_list|,
literal|""
argument_list|)
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
name|next
operator|!=
literal|null
condition|)
name|next
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|// TODO: right now, update requests are logged twice...
comment|// this will slow down things compared to Solr 1.2
comment|// we should have extra log info on the SolrQueryResponse, to
comment|// be logged by SolrCore
comment|// if id lists were truncated, show how many more there were
if|if
condition|(
name|numAdds
operator|>
name|maxNumToLog
condition|)
block|{
name|adds
operator|.
name|add
argument_list|(
literal|"...("
operator|+
operator|(
name|numAdds
operator|-
name|adds
operator|.
name|size
argument_list|()
operator|)
operator|+
literal|" more)"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numDeletes
operator|>
name|maxNumToLog
condition|)
block|{
name|deletes
operator|.
name|add
argument_list|(
literal|"...("
operator|+
operator|(
name|numDeletes
operator|-
name|deletes
operator|.
name|size
argument_list|()
operator|)
operator|+
literal|" more)"
argument_list|)
expr_stmt|;
block|}
name|long
name|elapsed
init|=
name|rsp
operator|.
name|getEndTime
argument_list|()
operator|-
name|req
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|""
operator|+
name|toLog
operator|+
literal|" 0 "
operator|+
operator|(
name|elapsed
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

