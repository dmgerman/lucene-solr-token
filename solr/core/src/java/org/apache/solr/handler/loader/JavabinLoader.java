begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.loader
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|loader
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
name|request
operator|.
name|JavaBinUpdateRequestCodec
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
name|params
operator|.
name|UpdateParams
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
name|ContentStream
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
name|FastInputStream
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
name|handler
operator|.
name|RequestHandlerUtils
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
name|EOFException
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Update handler which uses the JavaBin format  *  * @see org.apache.solr.client.solrj.request.JavaBinUpdateRequestCodec  * @see org.apache.solr.common.util.JavaBinCodec  */
end_comment

begin_class
DECL|class|JavabinLoader
specifier|public
class|class
name|JavabinLoader
extends|extends
name|ContentStreamLoader
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JavabinLoader
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ContentStream
name|stream
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|stream
operator|.
name|getStream
argument_list|()
expr_stmt|;
name|parseAndLoadDocs
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|is
argument_list|,
name|processor
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseAndLoadDocs
specifier|private
name|void
name|parseAndLoadDocs
parameter_list|(
specifier|final
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|InputStream
name|stream
parameter_list|,
specifier|final
name|UpdateRequestProcessor
name|processor
parameter_list|)
throws|throws
name|IOException
block|{
name|UpdateRequest
name|update
init|=
literal|null
decl_stmt|;
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
name|handler
init|=
operator|new
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
argument_list|()
block|{
specifier|private
name|AddUpdateCommand
name|addCmd
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|UpdateRequest
name|updateRequest
parameter_list|)
block|{
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
comment|// Perhaps commit from the parameters
try|try
block|{
name|RequestHandlerUtils
operator|.
name|handleCommit
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|updateRequest
operator|.
name|getParams
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RequestHandlerUtils
operator|.
name|handleRollback
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|updateRequest
operator|.
name|getParams
argument_list|()
argument_list|,
literal|false
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"ERROR handling commit/rollback"
argument_list|)
throw|;
block|}
return|return;
block|}
if|if
condition|(
name|addCmd
operator|==
literal|null
condition|)
block|{
name|addCmd
operator|=
name|getAddCommand
argument_list|(
name|req
argument_list|,
name|updateRequest
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|addCmd
operator|.
name|solrDoc
operator|=
name|document
expr_stmt|;
try|try
block|{
name|processor
operator|.
name|processAdd
argument_list|(
name|addCmd
argument_list|)
expr_stmt|;
name|addCmd
operator|.
name|clear
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"ERROR adding document "
operator|+
name|document
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|FastInputStream
name|in
init|=
name|FastInputStream
operator|.
name|wrap
argument_list|(
name|stream
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
name|update
operator|=
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
break|break;
comment|// this is expected
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
literal|"Exception while processing update request"
argument_list|,
name|e
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|update
operator|.
name|getDeleteById
argument_list|()
operator|!=
literal|null
operator|||
name|update
operator|.
name|getDeleteQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|delete
argument_list|(
name|req
argument_list|,
name|update
argument_list|,
name|processor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getAddCommand
specifier|private
name|AddUpdateCommand
name|getAddCommand
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|AddUpdateCommand
name|addCmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|addCmd
operator|.
name|overwrite
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|OVERWRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addCmd
operator|.
name|commitWithin
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|UpdateParams
operator|.
name|COMMIT_WITHIN
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|addCmd
return|;
block|}
DECL|method|delete
specifier|private
name|void
name|delete
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequest
name|update
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|update
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|DeleteUpdateCommand
name|delcmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|delcmd
operator|.
name|commitWithin
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|UpdateParams
operator|.
name|COMMIT_WITHIN
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|update
operator|.
name|getDeleteById
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|update
operator|.
name|getDeleteById
argument_list|()
control|)
block|{
name|delcmd
operator|.
name|id
operator|=
name|s
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|delcmd
argument_list|)
expr_stmt|;
block|}
name|delcmd
operator|.
name|id
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|update
operator|.
name|getDeleteQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|update
operator|.
name|getDeleteQuery
argument_list|()
control|)
block|{
name|delcmd
operator|.
name|query
operator|=
name|s
expr_stmt|;
name|processor
operator|.
name|processDelete
argument_list|(
name|delcmd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

