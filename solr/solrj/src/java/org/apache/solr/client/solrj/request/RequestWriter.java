begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
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
name|SolrRequest
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
name|util
operator|.
name|ClientUtils
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
name|ContentStreamBase
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_comment
comment|/**  * A RequestWriter is used to write requests to Solr.  *<p/>  * A subclass can override the methods in this class to supply a custom format in which a request can be sent.  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|RequestWriter
specifier|public
class|class
name|RequestWriter
block|{
DECL|field|UTF_8
specifier|public
specifier|static
specifier|final
name|Charset
name|UTF_8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|(
name|SolrRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|req
operator|instanceof
name|UpdateRequest
condition|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|req
decl_stmt|;
if|if
condition|(
name|isEmpty
argument_list|(
name|updateRequest
argument_list|)
condition|)
return|return
literal|null
return|;
name|List
argument_list|<
name|ContentStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|LazyContentStream
argument_list|(
name|updateRequest
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
return|return
name|req
operator|.
name|getContentStreams
argument_list|()
return|;
block|}
DECL|method|isEmpty
specifier|private
name|boolean
name|isEmpty
parameter_list|(
name|UpdateRequest
name|updateRequest
parameter_list|)
block|{
return|return
name|isNull
argument_list|(
name|updateRequest
operator|.
name|getDocuments
argument_list|()
argument_list|)
operator|&&
name|isNull
argument_list|(
name|updateRequest
operator|.
name|getDeleteById
argument_list|()
argument_list|)
operator|&&
name|isNull
argument_list|(
name|updateRequest
operator|.
name|getDeleteQuery
argument_list|()
argument_list|)
operator|&&
name|updateRequest
operator|.
name|getDocIterator
argument_list|()
operator|==
literal|null
return|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|(
name|SolrRequest
name|req
parameter_list|)
block|{
return|return
name|req
operator|.
name|getPath
argument_list|()
return|;
block|}
DECL|method|getContentStream
specifier|public
name|ContentStream
name|getContentStream
parameter_list|(
name|UpdateRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|req
operator|.
name|getXML
argument_list|()
argument_list|)
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|SolrRequest
name|request
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|request
operator|instanceof
name|UpdateRequest
condition|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|request
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|updateRequest
operator|.
name|writeXML
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getUpdateContentType
specifier|public
name|String
name|getUpdateContentType
parameter_list|()
block|{
return|return
name|ClientUtils
operator|.
name|TEXT_XML
return|;
block|}
DECL|class|LazyContentStream
specifier|public
class|class
name|LazyContentStream
implements|implements
name|ContentStream
block|{
DECL|field|contentStream
name|ContentStream
name|contentStream
init|=
literal|null
decl_stmt|;
DECL|field|req
name|UpdateRequest
name|req
init|=
literal|null
decl_stmt|;
DECL|method|LazyContentStream
specifier|public
name|LazyContentStream
parameter_list|(
name|UpdateRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
DECL|method|getDelegate
specifier|private
name|ContentStream
name|getDelegate
parameter_list|()
block|{
if|if
condition|(
name|contentStream
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|contentStream
operator|=
name|getContentStream
argument_list|(
name|req
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
name|RuntimeException
argument_list|(
literal|"Unable to write xml into a stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|contentStream
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getSourceInfo
specifier|public
name|String
name|getSourceInfo
parameter_list|()
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|getSourceInfo
argument_list|()
return|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|getUpdateContentType
argument_list|()
return|;
block|}
DECL|method|getSize
specifier|public
name|Long
name|getSize
parameter_list|()
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|getSize
argument_list|()
return|;
block|}
DECL|method|getStream
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|getStream
argument_list|()
return|;
block|}
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|getReader
argument_list|()
return|;
block|}
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|req
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isNull
specifier|protected
name|boolean
name|isNull
parameter_list|(
name|List
name|l
parameter_list|)
block|{
return|return
name|l
operator|==
literal|null
operator|||
name|l
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

end_unit

