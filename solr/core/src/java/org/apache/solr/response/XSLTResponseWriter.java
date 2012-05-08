begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharArrayReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharArrayWriter
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
name|Reader
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
name|util
operator|.
name|Map
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
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
name|SolrConfig
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
name|common
operator|.
name|util
operator|.
name|XMLErrorLogger
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
name|util
operator|.
name|xslt
operator|.
name|TransformerProvider
import|;
end_import

begin_comment
comment|/** QueryResponseWriter which captures the output of the XMLWriter  *  (in memory for now, not optimal performancewise), and applies an XSLT transform  *  to it.  */
end_comment

begin_class
DECL|class|XSLTResponseWriter
specifier|public
class|class
name|XSLTResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|field|DEFAULT_CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONTENT_TYPE
init|=
literal|"application/xml"
decl_stmt|;
DECL|field|CONTEXT_TRANSFORMER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CONTEXT_TRANSFORMER_KEY
init|=
literal|"xsltwriter.transformer"
decl_stmt|;
DECL|field|xsltCacheLifetimeSeconds
specifier|private
name|Integer
name|xsltCacheLifetimeSeconds
init|=
literal|null
decl_stmt|;
DECL|field|XSLT_CACHE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|XSLT_CACHE_DEFAULT
init|=
literal|60
decl_stmt|;
DECL|field|XSLT_CACHE_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|XSLT_CACHE_PARAM
init|=
literal|"xsltCacheLifetimeSeconds"
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
name|XSLTResponseWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|xmllog
specifier|private
specifier|static
specifier|final
name|XMLErrorLogger
name|xmllog
init|=
operator|new
name|XMLErrorLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|n
parameter_list|)
block|{
specifier|final
name|SolrParams
name|p
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|xsltCacheLifetimeSeconds
operator|=
name|p
operator|.
name|getInt
argument_list|(
name|XSLT_CACHE_PARAM
argument_list|,
name|XSLT_CACHE_DEFAULT
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"xsltCacheLifetimeSeconds="
operator|+
name|xsltCacheLifetimeSeconds
argument_list|)
expr_stmt|;
block|}
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
name|Transformer
name|t
init|=
literal|null
decl_stmt|;
try|try
block|{
name|t
operator|=
name|getTransformer
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO should our parent interface throw (IO)Exception?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"getTransformer fails in getContentType"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|mediaType
init|=
name|t
operator|.
name|getOutputProperty
argument_list|(
literal|"media-type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mediaType
operator|==
literal|null
operator|||
name|mediaType
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// This did not happen in my tests, mediaTypeFromXslt is set to "text/xml"
comment|// if the XSLT transform does not contain an xsl:output element. Not sure
comment|// if this is standard behavior or if it's just my JVM/libraries
name|mediaType
operator|=
name|DEFAULT_CONTENT_TYPE
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|mediaType
operator|.
name|contains
argument_list|(
literal|"charset"
argument_list|)
condition|)
block|{
name|String
name|encoding
init|=
name|t
operator|.
name|getOutputProperty
argument_list|(
literal|"encoding"
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
operator|||
name|encoding
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|encoding
operator|=
literal|"UTF-8"
expr_stmt|;
block|}
name|mediaType
operator|=
name|mediaType
operator|+
literal|"; charset="
operator|+
name|encoding
expr_stmt|;
block|}
return|return
name|mediaType
return|;
block|}
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
specifier|final
name|Transformer
name|t
init|=
name|getTransformer
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// capture the output of the XMLWriter
specifier|final
name|CharArrayWriter
name|w
init|=
operator|new
name|CharArrayWriter
argument_list|()
decl_stmt|;
name|XMLWriter
operator|.
name|writeResponse
argument_list|(
name|w
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
comment|// and write transformed result to our writer
specifier|final
name|Reader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|CharArrayReader
argument_list|(
name|w
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|StreamSource
name|source
init|=
operator|new
name|StreamSource
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
decl_stmt|;
try|try
block|{
name|t
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|te
parameter_list|)
block|{
specifier|final
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
literal|"XSLT transformation error"
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|te
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
comment|/** Get Transformer from request context, or from TransformerProvider.    *  This allows either getContentType(...) or write(...) to instantiate the Transformer,    *  depending on which one is called first, then the other one reuses the same Transformer    */
DECL|method|getTransformer
specifier|protected
name|Transformer
name|getTransformer
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|xslt
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|TR
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|xslt
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"'"
operator|+
name|CommonParams
operator|.
name|TR
operator|+
literal|"' request parameter is required to use the XSLTResponseWriter"
argument_list|)
throw|;
block|}
comment|// not the cleanest way to achieve this
name|SolrConfig
name|solrConfig
init|=
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
decl_stmt|;
comment|// no need to synchronize access to context, right?
comment|// Nothing else happens with it at the same time
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
name|request
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|Transformer
name|result
init|=
operator|(
name|Transformer
operator|)
name|ctx
operator|.
name|get
argument_list|(
name|CONTEXT_TRANSFORMER_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|TransformerProvider
operator|.
name|instance
operator|.
name|getTransformer
argument_list|(
name|solrConfig
argument_list|,
name|xslt
argument_list|,
name|xsltCacheLifetimeSeconds
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setErrorListener
argument_list|(
name|xmllog
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|put
argument_list|(
name|CONTEXT_TRANSFORMER_KEY
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

