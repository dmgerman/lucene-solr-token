begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|XmlUpdateRequestHandler
operator|.
name|COMMIT_WITHIN
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
comment|/**  * Update handler which uses the JavaBin format  *  * @version $Id$  * @see org.apache.solr.client.solrj.request.JavaBinUpdateRequestCodec  * @see org.apache.solr.common.util.JavaBinCodec  * @since solr 1.4  */
end_comment

begin_class
DECL|class|BinaryUpdateRequestHandler
specifier|public
class|class
name|BinaryUpdateRequestHandler
extends|extends
name|ContentStreamHandlerBase
block|{
DECL|method|newLoader
specifier|protected
name|ContentStreamLoader
name|newLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
specifier|final
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
return|return
operator|new
name|ContentStreamLoader
argument_list|()
block|{
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
block|}
return|;
block|}
DECL|method|parseAndLoadDocs
specifier|private
name|void
name|parseAndLoadDocs
parameter_list|(
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
name|update
operator|=
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|stream
argument_list|,
operator|new
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingDocumentHandler
argument_list|()
block|{
specifier|private
name|AddUpdateCommand
name|addCmd
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|document
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
name|addCmd
operator|==
literal|null
condition|)
block|{
name|addCmd
operator|=
name|getAddCommand
argument_list|(
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
argument_list|)
expr_stmt|;
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
name|delete
argument_list|(
name|update
operator|.
name|getDeleteById
argument_list|()
argument_list|,
name|processor
argument_list|,
literal|true
argument_list|)
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
name|delete
argument_list|(
name|update
operator|.
name|getDeleteQuery
argument_list|()
argument_list|,
name|processor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAddCommand
specifier|private
name|AddUpdateCommand
name|getAddCommand
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|AddUpdateCommand
name|addCmd
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|boolean
name|overwrite
init|=
literal|true
decl_stmt|;
comment|// the default
name|Boolean
name|overwritePending
init|=
literal|null
decl_stmt|;
name|Boolean
name|overwriteCommitted
init|=
literal|null
decl_stmt|;
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
name|overwrite
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
name|COMMIT_WITHIN
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// check if these flags are set
if|if
condition|(
name|overwritePending
operator|!=
literal|null
operator|&&
name|overwriteCommitted
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|overwritePending
operator|!=
name|overwriteCommitted
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
literal|"can't have different values for 'overwritePending' and 'overwriteCommitted'"
argument_list|)
throw|;
block|}
name|overwrite
operator|=
name|overwritePending
expr_stmt|;
block|}
name|addCmd
operator|.
name|overwriteCommitted
operator|=
name|overwrite
expr_stmt|;
name|addCmd
operator|.
name|overwritePending
operator|=
name|overwrite
expr_stmt|;
name|addCmd
operator|.
name|allowDups
operator|=
operator|!
name|overwrite
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
name|List
argument_list|<
name|String
argument_list|>
name|l
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|boolean
name|isId
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|s
range|:
name|l
control|)
block|{
name|DeleteUpdateCommand
name|delcmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
if|if
condition|(
name|isId
condition|)
block|{
name|delcmd
operator|.
name|id
operator|=
name|s
expr_stmt|;
block|}
else|else
block|{
name|delcmd
operator|.
name|query
operator|=
name|s
expr_stmt|;
block|}
name|processor
operator|.
name|processDelete
argument_list|(
name|delcmd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add/Update multiple documents with javabin format"
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
block|}
end_class

end_unit

