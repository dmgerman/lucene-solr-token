begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IOUtils
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|*
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
name|util
operator|.
name|BytesRef
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
name|SimpleOrderedMap
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
name|schema
operator|.
name|FieldType
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
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|StringReader
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

begin_comment
comment|/**  *  * @deprecated Use {@link org.apache.solr.handler.DocumentAnalysisRequestHandler} instead.  **/
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|AnalysisRequestHandler
specifier|public
class|class
name|AnalysisRequestHandler
extends|extends
name|RequestHandlerBase
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
name|AnalysisRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|inputFactory
specifier|private
name|XMLInputFactory
name|inputFactory
decl_stmt|;
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
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inputFactory
operator|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
try|try
block|{
comment|// The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
comment|// XMLInputFactory, as that implementation tries to cache and reuse the
comment|// XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
comment|// prevents this.
comment|// All other known open-source stax parsers (and the bea ref impl)
comment|// have thread-safe factories.
name|inputFactory
operator|.
name|setProperty
argument_list|(
literal|"reuse-instance"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Other implementations will likely throw this exception since "reuse-instance"
comment|// isimplementation specific.
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to set the 'reuse-instance' property for the input factory: "
operator|+
name|inputFactory
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ContentStream
name|stream
range|:
name|req
operator|.
name|getContentStreams
argument_list|()
control|)
block|{
name|Reader
name|reader
init|=
name|stream
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
name|processContent
argument_list|(
name|parser
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|processContent
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processContent
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
case|:
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
block|{
name|String
name|currTag
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Tokenizing doc..."
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
name|readDoc
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|SchemaField
name|uniq
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|theTokens
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
name|uniq
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|theTokens
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|FieldType
name|ft
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|ft
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|val
range|:
name|vals
control|)
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TokenStream
name|tstream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|name
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tokens
init|=
name|getTokens
argument_list|(
name|tstream
argument_list|)
decl_stmt|;
name|theTokens
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
block|}
block|}
block|}
block|}
DECL|method|getTokens
specifier|static
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|getTokens
parameter_list|(
name|TokenStream
name|tstream
parameter_list|)
throws|throws
name|IOException
block|{
comment|// outer is namedList since order of tokens is important
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tokens
init|=
operator|new
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: support custom attributes
name|CharTermAttribute
name|termAtt
init|=
literal|null
decl_stmt|;
name|TermToBytesRefAttribute
name|bytesAtt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tstream
operator|.
name|hasAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|termAtt
operator|=
name|tstream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tstream
operator|.
name|hasAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|bytesAtt
operator|=
name|tstream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|tstream
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|tstream
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|tstream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
while|while
condition|(
name|tstream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|token
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
literal|"token"
argument_list|,
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|termAtt
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|add
argument_list|(
literal|"value"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bytesAtt
operator|!=
literal|null
condition|)
block|{
name|bytesAtt
operator|.
name|toBytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
comment|// TODO: This is incorrect when numeric fields change in later lucene versions. It should use BytesRef directly!
name|token
operator|.
name|add
argument_list|(
literal|"value"
argument_list|,
name|bytes
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|token
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|add
argument_list|(
literal|"posInc"
argument_list|,
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|add
argument_list|(
literal|"type"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO: handle payloads
block|}
return|return
name|tokens
return|;
block|}
DECL|method|readDoc
name|SolrInputDocument
name|readDoc
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|String
name|attrName
init|=
literal|""
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|boolean
name|isNull
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
comment|// Add everything to the text
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|text
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
elseif|else
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isNull
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|boost
operator|=
literal|1.0f
expr_stmt|;
block|}
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|text
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|localName
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"field"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
expr_stmt|;
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
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
throw|;
block|}
name|String
name|attrVal
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrName
operator|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrVal
operator|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|name
operator|=
name|attrVal
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provide Analysis of text"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision:$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id:$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL:$"
return|;
block|}
block|}
end_class

end_unit

