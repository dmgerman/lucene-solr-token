begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
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
name|BinaryResponseParser
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
name|SolrDocument
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
name|CommonParams
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
name|JavaBinCodec
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|ReturnFields
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

begin_class
DECL|class|BinaryResponseWriter
specifier|public
class|class
name|BinaryResponseWriter
implements|implements
name|BinaryQueryResponseWriter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|Resolver
name|resolver
init|=
operator|new
name|Resolver
argument_list|(
name|req
argument_list|,
name|response
operator|.
name|getReturnFields
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|OMIT_HEADER
argument_list|,
literal|false
argument_list|)
condition|)
name|response
operator|.
name|removeResponseHeader
argument_list|()
expr_stmt|;
try|try
init|(
name|JavaBinCodec
name|jbc
init|=
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
init|)
block|{
name|jbc
operator|.
name|setWritableDocFields
argument_list|(
name|resolver
argument_list|)
operator|.
name|marshal
argument_list|(
name|response
operator|.
name|getValues
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"This is a binary writer , Cannot write to a characterstream"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|BinaryResponseParser
operator|.
name|BINARY_CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|/* NOOP */
block|}
DECL|class|Resolver
specifier|public
specifier|static
class|class
name|Resolver
implements|implements
name|JavaBinCodec
operator|.
name|ObjectResolver
implements|,
name|JavaBinCodec
operator|.
name|WritableDocFields
block|{
DECL|field|solrQueryRequest
specifier|protected
specifier|final
name|SolrQueryRequest
name|solrQueryRequest
decl_stmt|;
DECL|field|schema
specifier|protected
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|returnFields
specifier|protected
name|ReturnFields
name|returnFields
decl_stmt|;
DECL|method|Resolver
specifier|public
name|Resolver
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|ReturnFields
name|returnFields
parameter_list|)
block|{
name|solrQueryRequest
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|returnFields
operator|=
name|returnFields
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|Object
name|o
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|o
operator|instanceof
name|ResultContext
condition|)
block|{
name|ReturnFields
name|orig
init|=
name|returnFields
decl_stmt|;
name|ResultContext
name|res
init|=
operator|(
name|ResultContext
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|res
operator|.
name|getReturnFields
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|returnFields
operator|=
name|res
operator|.
name|getReturnFields
argument_list|()
expr_stmt|;
block|}
name|writeResults
argument_list|(
name|res
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|returnFields
operator|=
name|orig
expr_stmt|;
return|return
literal|null
return|;
comment|// null means we completely handled it
block|}
if|if
condition|(
name|o
operator|instanceof
name|DocList
condition|)
block|{
name|ResultContext
name|ctx
init|=
operator|new
name|BasicResultContext
argument_list|(
operator|(
name|DocList
operator|)
name|o
argument_list|,
name|returnFields
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|solrQueryRequest
argument_list|)
decl_stmt|;
name|writeResults
argument_list|(
name|ctx
argument_list|,
name|codec
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// null means we completely handled it
block|}
if|if
condition|(
name|o
operator|instanceof
name|IndexableField
condition|)
block|{
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
name|schema
operator|=
name|solrQueryRequest
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|IndexableField
name|f
init|=
operator|(
name|IndexableField
operator|)
name|o
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|o
operator|=
name|DocsStreamer
operator|.
name|getValue
argument_list|(
name|sf
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading a field : "
operator|+
name|o
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|o
return|;
block|}
annotation|@
name|Override
DECL|method|isWritable
specifier|public
name|boolean
name|isWritable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|returnFields
operator|.
name|wantsField
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|wantsAllFields
specifier|public
name|boolean
name|wantsAllFields
parameter_list|()
block|{
return|return
name|returnFields
operator|.
name|wantsAllFields
argument_list|()
return|;
block|}
DECL|method|writeResultsBody
specifier|protected
name|void
name|writeResultsBody
parameter_list|(
name|ResultContext
name|res
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|ARR
argument_list|,
name|res
operator|.
name|getDocList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|docStreamer
init|=
name|res
operator|.
name|getProcessedDocuments
argument_list|()
decl_stmt|;
while|while
condition|(
name|docStreamer
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SolrDocument
name|doc
init|=
name|docStreamer
operator|.
name|next
argument_list|()
decl_stmt|;
name|codec
operator|.
name|writeSolrDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeResults
specifier|public
name|void
name|writeResults
parameter_list|(
name|ResultContext
name|ctx
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|SOLRDOCLST
argument_list|)
expr_stmt|;
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
name|ctx
operator|.
name|getDocList
argument_list|()
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
name|ctx
operator|.
name|getDocList
argument_list|()
operator|.
name|offset
argument_list|()
argument_list|)
expr_stmt|;
name|Float
name|maxScore
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|wantsScores
argument_list|()
condition|)
block|{
name|maxScore
operator|=
name|ctx
operator|.
name|getDocList
argument_list|()
operator|.
name|maxScore
argument_list|()
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|maxScore
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeArray
argument_list|(
name|l
argument_list|)
expr_stmt|;
comment|// this is a seprate function so that streaming responses can use just that part
name|writeResultsBody
argument_list|(
name|ctx
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * TODO -- there may be a way to do this without marshal at all...    *    * @return a response object equivalent to what you get from the XML/JSON/javabin parser. Documents become    *         SolrDocuments, DocList becomes SolrDocumentList etc.    *    * @since solr 1.4    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getParsedResponse
specifier|public
specifier|static
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getParsedResponse
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
try|try
block|{
name|Resolver
name|resolver
init|=
operator|new
name|Resolver
argument_list|(
name|req
argument_list|,
name|rsp
operator|.
name|getReturnFields
argument_list|()
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|JavaBinCodec
name|jbc
init|=
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
init|)
block|{
name|jbc
operator|.
name|setWritableDocFields
argument_list|(
name|resolver
argument_list|)
operator|.
name|marshal
argument_list|(
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|in
init|=
name|out
operator|.
name|toInputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|JavaBinCodec
name|jbc
init|=
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
init|)
block|{
return|return
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|jbc
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

