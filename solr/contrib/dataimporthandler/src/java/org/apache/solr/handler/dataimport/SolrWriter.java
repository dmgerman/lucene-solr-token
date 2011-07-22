begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
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
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|RollbackUpdateCommand
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
name|UpdateRequestProcessor
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *<p> Writes documents to SOLR.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrWriter
specifier|public
class|class
name|SolrWriter
implements|implements
name|DIHWriter
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
name|SolrWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LAST_INDEX_KEY
specifier|static
specifier|final
name|String
name|LAST_INDEX_KEY
init|=
literal|"last_index_time"
decl_stmt|;
DECL|field|processor
specifier|private
specifier|final
name|UpdateRequestProcessor
name|processor
decl_stmt|;
DECL|field|debugLogger
name|DebugLogger
name|debugLogger
decl_stmt|;
DECL|field|req
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|method|SolrWriter
specifier|public
name|SolrWriter
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|processor
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to call finish() on UpdateRequestProcessor"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|upload
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrInputDocument
name|d
parameter_list|)
block|{
try|try
block|{
name|AddUpdateCommand
name|command
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|command
operator|.
name|solrDoc
operator|=
name|d
expr_stmt|;
name|processor
operator|.
name|processAdd
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
name|log
operator|.
name|warn
argument_list|(
literal|"Error creating document : "
operator|+
name|d
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|deleteDoc
specifier|public
name|void
name|deleteDoc
parameter_list|(
name|Object
name|id
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting document: "
operator|+
name|id
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|delCmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|delCmd
operator|.
name|id
operator|=
name|id
operator|.
name|toString
argument_list|()
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|delCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while deleteing: "
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting documents from Solr with query: "
operator|+
name|query
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|delCmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|delCmd
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|delCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while deleting by query: "
operator|+
name|query
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|optimize
parameter_list|)
block|{
try|try
block|{
name|CommitUpdateCommand
name|commit
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
name|optimize
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while solr commit."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
block|{
try|try
block|{
name|RollbackUpdateCommand
name|rollback
init|=
operator|new
name|RollbackUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processRollback
argument_list|(
name|rollback
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while solr rollback."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doDeleteAll
specifier|public
name|void
name|doDeleteAll
parameter_list|()
block|{
try|try
block|{
name|DeleteUpdateCommand
name|deleteCommand
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|deleteCommand
operator|.
name|query
operator|=
literal|"*:*"
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|deleteCommand
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Exception in full dump while deleting all documents."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getResourceAsString
specifier|static
name|String
name|getResourceAsString
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|sz
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|sz
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|sz
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{        }
block|}
return|return
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|method|getDocCount
specifier|static
name|String
name|getDocCount
parameter_list|()
block|{
if|if
condition|(
name|DocBuilder
operator|.
name|INSTANCE
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|""
operator|+
operator|(
name|DocBuilder
operator|.
name|INSTANCE
operator|.
name|get
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
operator|+
literal|1
operator|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
comment|/* NO-OP */
block|}
annotation|@
name|Override
DECL|method|getDebugLogger
specifier|public
name|DebugLogger
name|getDebugLogger
parameter_list|()
block|{
return|return
name|debugLogger
return|;
block|}
block|}
end_class

end_unit

