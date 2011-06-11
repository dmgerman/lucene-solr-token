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
name|CharReader
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
name|CharStream
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
name|lucene
operator|.
name|index
operator|.
name|Payload
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
name|Attribute
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
name|AttributeImpl
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
name|AttributeSource
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
name|AttributeReflector
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
name|CharsRef
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
name|ArrayUtil
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
name|analysis
operator|.
name|CharFilterFactory
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
name|analysis
operator|.
name|TokenFilterFactory
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
name|analysis
operator|.
name|TokenizerChain
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
name|analysis
operator|.
name|TokenizerFactory
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
name|schema
operator|.
name|FieldType
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A base class for all analysis request handlers.  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|AnalysisRequestHandlerBase
specifier|public
specifier|abstract
class|class
name|AnalysisRequestHandlerBase
extends|extends
name|RequestHandlerBase
block|{
DECL|field|EMPTY_BYTES_SET
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|EMPTY_BYTES_SET
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
name|rsp
operator|.
name|add
argument_list|(
literal|"analysis"
argument_list|,
name|doAnalysis
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Performs the analysis based on the given solr request and returns the analysis result as a named list.    *    * @param req The solr request.    *    * @return The analysis result as a named list.    *    * @throws Exception When analysis fails.    */
DECL|method|doAnalysis
specifier|protected
specifier|abstract
name|NamedList
name|doAnalysis
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Analyzes the given value using the given Analyzer.    *    * @param value   Value to analyze    * @param context The {@link AnalysisContext analysis context}.    *    * @return NamedList containing the tokens produced by analyzing the given value    */
DECL|method|analyzeValue
specifier|protected
name|NamedList
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|analyzeValue
parameter_list|(
name|String
name|value
parameter_list|,
name|AnalysisContext
name|context
parameter_list|)
block|{
name|Analyzer
name|analyzer
init|=
name|context
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|TokenizerChain
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|analyzer
argument_list|)
condition|)
block|{
name|TokenStream
name|tokenStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tokenStream
operator|=
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|context
operator|.
name|getFieldName
argument_list|()
argument_list|,
operator|new
name|StringReader
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|tokenStream
operator|.
name|reset
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
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|>
argument_list|>
name|namedList
init|=
operator|new
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|namedList
operator|.
name|add
argument_list|(
name|tokenStream
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|convertTokensToNamedLists
argument_list|(
name|analyzeTokenStream
argument_list|(
name|tokenStream
argument_list|)
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|namedList
return|;
block|}
name|TokenizerChain
name|tokenizerChain
init|=
operator|(
name|TokenizerChain
operator|)
name|analyzer
decl_stmt|;
name|CharFilterFactory
index|[]
name|cfiltfacs
init|=
name|tokenizerChain
operator|.
name|getCharFilterFactories
argument_list|()
decl_stmt|;
name|TokenizerFactory
name|tfac
init|=
name|tokenizerChain
operator|.
name|getTokenizerFactory
argument_list|()
decl_stmt|;
name|TokenFilterFactory
index|[]
name|filtfacs
init|=
name|tokenizerChain
operator|.
name|getTokenFilterFactories
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|namedList
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|cfiltfacs
operator|!=
literal|null
condition|)
block|{
name|String
name|source
init|=
name|value
decl_stmt|;
for|for
control|(
name|CharFilterFactory
name|cfiltfac
range|:
name|cfiltfacs
control|)
block|{
name|CharStream
name|reader
init|=
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|source
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|=
name|cfiltfac
operator|.
name|create
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|source
operator|=
name|writeCharStream
argument_list|(
name|namedList
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
name|TokenStream
name|tokenStream
init|=
name|tfac
operator|.
name|create
argument_list|(
name|tokenizerChain
operator|.
name|charStream
argument_list|(
operator|new
name|StringReader
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AttributeSource
argument_list|>
name|tokens
init|=
name|analyzeTokenStream
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
name|namedList
operator|.
name|add
argument_list|(
name|tokenStream
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|convertTokensToNamedLists
argument_list|(
name|tokens
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|ListBasedTokenStream
name|listBasedTokenStream
init|=
operator|new
name|ListBasedTokenStream
argument_list|(
name|tokens
argument_list|)
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|tokenFilterFactory
range|:
name|filtfacs
control|)
block|{
for|for
control|(
specifier|final
name|AttributeSource
name|tok
range|:
name|tokens
control|)
block|{
name|tok
operator|.
name|getAttribute
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
argument_list|)
operator|.
name|freezeStage
argument_list|()
expr_stmt|;
block|}
name|tokenStream
operator|=
name|tokenFilterFactory
operator|.
name|create
argument_list|(
name|listBasedTokenStream
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|analyzeTokenStream
argument_list|(
name|tokenStream
argument_list|)
expr_stmt|;
name|namedList
operator|.
name|add
argument_list|(
name|tokenStream
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|convertTokensToNamedLists
argument_list|(
name|tokens
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|listBasedTokenStream
operator|=
operator|new
name|ListBasedTokenStream
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
return|return
name|namedList
return|;
block|}
comment|/**    * Analyzes the given text using the given analyzer and returns the produced tokens.    *    * @param query    The query to analyze.    * @param analyzer The analyzer to use.    */
DECL|method|getQueryTokenSet
specifier|protected
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|getQueryTokenSet
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|tokens
init|=
operator|new
name|HashSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TermToBytesRefAttribute
name|bytesAtt
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|bytesAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
try|try
block|{
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|bytesAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occured while iterating over tokenstream"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
return|return
name|tokens
return|;
block|}
comment|/**    * Analyzes the given TokenStream, collecting the Tokens it produces.    *    * @param tokenStream TokenStream to analyze    *    * @return List of tokens produced from the TokenStream    */
DECL|method|analyzeTokenStream
specifier|private
name|List
argument_list|<
name|AttributeSource
argument_list|>
name|analyzeTokenStream
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|AttributeSource
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|AttributeSource
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TokenTrackingAttribute
name|trackerAtt
init|=
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// for backwards compatibility, add all "common" attributes
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
try|try
block|{
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|position
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|trackerAtt
operator|.
name|setActPosition
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|tokenStream
operator|.
name|cloneAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occured while iterating over tokenstream"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
return|return
name|tokens
return|;
block|}
comment|// a static mapping of the reflected attribute keys to the names used in Solr 1.4
DECL|field|ATTRIBUTE_MAPPING
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ATTRIBUTE_MAPPING
init|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#startOffset"
argument_list|,
literal|"start"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#endOffset"
argument_list|,
literal|"end"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TypeAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#type"
argument_list|,
literal|"type"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#position"
argument_list|,
literal|"position"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionHistory"
argument_list|,
literal|"positionHistory"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**    * Converts the list of Tokens to a list of NamedLists representing the tokens.    *    * @param tokens  Tokens to convert    * @param context The analysis context    *    * @return List of NamedLists containing the relevant information taken from the tokens    */
DECL|method|convertTokensToNamedLists
specifier|private
name|List
argument_list|<
name|NamedList
argument_list|>
name|convertTokensToNamedLists
parameter_list|(
specifier|final
name|List
argument_list|<
name|AttributeSource
argument_list|>
name|tokenList
parameter_list|,
name|AnalysisContext
name|context
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|NamedList
argument_list|>
name|tokensNamedLists
init|=
operator|new
name|ArrayList
argument_list|<
name|NamedList
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|fieldType
init|=
name|context
operator|.
name|getFieldType
argument_list|()
decl_stmt|;
specifier|final
name|AttributeSource
index|[]
name|tokens
init|=
name|tokenList
operator|.
name|toArray
argument_list|(
operator|new
name|AttributeSource
index|[
name|tokenList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// sort the tokens by absoulte position
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|tokens
argument_list|,
operator|new
name|Comparator
argument_list|<
name|AttributeSource
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|AttributeSource
name|a
parameter_list|,
name|AttributeSource
name|b
parameter_list|)
block|{
return|return
name|arrayCompare
argument_list|(
name|a
operator|.
name|getAttribute
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
argument_list|)
operator|.
name|getPositions
argument_list|()
argument_list|,
name|b
operator|.
name|getAttribute
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
argument_list|)
operator|.
name|getPositions
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|int
name|arrayCompare
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
index|[]
name|b
parameter_list|)
block|{
name|int
name|p
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|stop
init|=
name|Math
operator|.
name|min
argument_list|(
name|a
operator|.
name|length
argument_list|,
name|b
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|p
operator|<
name|stop
condition|)
block|{
name|int
name|diff
init|=
name|a
index|[
name|p
index|]
operator|-
name|b
index|[
name|p
index|]
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
return|return
name|diff
return|;
name|p
operator|++
expr_stmt|;
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|a
operator|.
name|length
operator|-
name|b
operator|.
name|length
return|;
block|}
block|}
argument_list|)
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
name|tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|AttributeSource
name|token
init|=
name|tokens
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|tokenNamedList
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|TermToBytesRefAttribute
name|termAtt
init|=
name|token
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|rawBytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
specifier|final
name|String
name|text
init|=
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|rawBytes
argument_list|,
operator|new
name|CharsRef
argument_list|(
name|rawBytes
operator|.
name|length
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|tokenNamedList
operator|.
name|add
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|hasAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
specifier|final
name|String
name|rawText
init|=
name|token
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rawText
operator|.
name|equals
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|tokenNamedList
operator|.
name|add
argument_list|(
literal|"raw_text"
argument_list|,
name|rawText
argument_list|)
expr_stmt|;
block|}
block|}
name|tokenNamedList
operator|.
name|add
argument_list|(
literal|"raw_bytes"
argument_list|,
name|rawBytes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getTermsToMatch
argument_list|()
operator|.
name|contains
argument_list|(
name|rawBytes
argument_list|)
condition|)
block|{
name|tokenNamedList
operator|.
name|add
argument_list|(
literal|"match"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|token
operator|.
name|reflectWith
argument_list|(
operator|new
name|AttributeReflector
argument_list|()
block|{
specifier|public
name|void
name|reflect
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// leave out position and bytes term
if|if
condition|(
name|TermToBytesRefAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
return|return;
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
return|return;
if|if
condition|(
name|PositionIncrementAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
return|return;
name|String
name|k
init|=
name|attClass
operator|.
name|getName
argument_list|()
operator|+
literal|'#'
operator|+
name|key
decl_stmt|;
comment|// map keys for "standard attributes":
if|if
condition|(
name|ATTRIBUTE_MAPPING
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
condition|)
block|{
name|k
operator|=
name|ATTRIBUTE_MAPPING
operator|.
name|get
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Payload
condition|)
block|{
specifier|final
name|Payload
name|p
init|=
operator|(
name|Payload
operator|)
name|value
decl_stmt|;
name|value
operator|=
operator|new
name|BytesRef
argument_list|(
name|p
operator|.
name|getData
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|tokenNamedList
operator|.
name|add
argument_list|(
name|k
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|tokensNamedLists
operator|.
name|add
argument_list|(
name|tokenNamedList
argument_list|)
expr_stmt|;
block|}
return|return
name|tokensNamedLists
return|;
block|}
DECL|method|writeCharStream
specifier|private
name|String
name|writeCharStream
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|out
parameter_list|,
name|CharStream
name|input
parameter_list|)
block|{
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
do|do
block|{
try|try
block|{
name|len
operator|=
name|input
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|BUFFER_SIZE
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
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|len
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|len
operator|==
name|BUFFER_SIZE
condition|)
do|;
name|out
operator|.
name|add
argument_list|(
name|input
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// ================================================= Inner classes =================================================
comment|/**    * TokenStream that iterates over a list of pre-existing Tokens    * @lucene.internal    */
DECL|class|ListBasedTokenStream
specifier|protected
specifier|final
specifier|static
class|class
name|ListBasedTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|tokens
specifier|private
specifier|final
name|List
argument_list|<
name|AttributeSource
argument_list|>
name|tokens
decl_stmt|;
DECL|field|tokenIterator
specifier|private
name|Iterator
argument_list|<
name|AttributeSource
argument_list|>
name|tokenIterator
decl_stmt|;
comment|/**      * Creates a new ListBasedTokenStream which uses the given tokens as its token source.      *      * @param tokens Source of tokens to be used      */
DECL|method|ListBasedTokenStream
name|ListBasedTokenStream
parameter_list|(
name|List
argument_list|<
name|AttributeSource
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
name|tokenIterator
operator|=
name|tokens
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tokenIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|AttributeSource
name|next
init|=
name|tokenIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
name|atts
init|=
name|next
operator|.
name|getAttributeClassesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|atts
operator|.
name|hasNext
argument_list|()
condition|)
comment|// make sure all att impls in the token exist here
name|addAttribute
argument_list|(
name|atts
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|.
name|copyTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tokenIterator
operator|=
name|tokens
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** This is an {@link Attribute} used to track the positions of tokens    * in the analysis chain.    * @lucene.internal This class is only public for usage by the {@link AttributeSource} API.    */
DECL|interface|TokenTrackingAttribute
specifier|public
interface|interface
name|TokenTrackingAttribute
extends|extends
name|Attribute
block|{
DECL|method|freezeStage
name|void
name|freezeStage
parameter_list|()
function_decl|;
DECL|method|setActPosition
name|void
name|setActPosition
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
DECL|method|getPositions
name|int
index|[]
name|getPositions
parameter_list|()
function_decl|;
DECL|method|reset
name|void
name|reset
parameter_list|(
name|int
index|[]
name|basePositions
parameter_list|,
name|int
name|position
parameter_list|)
function_decl|;
block|}
comment|/** Implementation of {@link TokenTrackingAttribute}.    * @lucene.internal This class is only public for usage by the {@link AttributeSource} API.    */
DECL|class|TokenTrackingAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|TokenTrackingAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|TokenTrackingAttribute
block|{
DECL|field|basePositions
specifier|private
name|int
index|[]
name|basePositions
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
init|=
literal|0
decl_stmt|;
DECL|method|freezeStage
specifier|public
name|void
name|freezeStage
parameter_list|()
block|{
name|this
operator|.
name|basePositions
operator|=
name|getPositions
argument_list|()
expr_stmt|;
name|this
operator|.
name|position
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|setActPosition
specifier|public
name|void
name|setActPosition
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|this
operator|.
name|position
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|getPositions
specifier|public
name|int
index|[]
name|getPositions
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|positions
init|=
operator|new
name|int
index|[
name|basePositions
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|basePositions
argument_list|,
literal|0
argument_list|,
name|positions
argument_list|,
literal|0
argument_list|,
name|basePositions
operator|.
name|length
argument_list|)
expr_stmt|;
name|positions
index|[
name|basePositions
operator|.
name|length
index|]
operator|=
name|position
expr_stmt|;
return|return
name|positions
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
index|[]
name|basePositions
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|this
operator|.
name|basePositions
operator|=
name|basePositions
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// we do nothing here, as all attribute values are controlled externally by consumer
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|positions
init|=
name|getPositions
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|positions
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|p
range|:
name|positions
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|reflector
operator|.
name|reflect
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
argument_list|,
literal|"positionHistory"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|TokenTrackingAttribute
operator|.
name|class
argument_list|,
literal|"position"
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|TokenTrackingAttribute
name|t
init|=
operator|(
name|TokenTrackingAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|reset
argument_list|(
name|basePositions
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Serves as the context of an analysis process. This context contains the following constructs    */
DECL|class|AnalysisContext
specifier|protected
specifier|static
class|class
name|AnalysisContext
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|fieldType
specifier|private
specifier|final
name|FieldType
name|fieldType
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|termsToMatch
specifier|private
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termsToMatch
decl_stmt|;
comment|/**      * Constructs a new AnalysisContext with a given field tpe, analyzer and       * termsToMatch. By default the field name in this context will be       * {@code null}. During the analysis processs, The produced tokens will       * be compaired to the terms in the {@code termsToMatch} set. When found,       * these tokens will be marked as a match.      *      * @param fieldType    The type of the field the analysis is performed on.      * @param analyzer     The analyzer to be used.      * @param termsToMatch Holds all the terms that should match during the       *                     analysis process.      */
DECL|method|AnalysisContext
specifier|public
name|AnalysisContext
parameter_list|(
name|FieldType
name|fieldType
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termsToMatch
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|fieldType
argument_list|,
name|analyzer
argument_list|,
name|termsToMatch
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs an AnalysisContext with a given field name, field type       * and analyzer. By default this context will hold no terms to match      *      * @param fieldName The name of the field the analysis is performed on       *                  (may be {@code null}).      * @param fieldType The type of the field the analysis is performed on.      * @param analyzer  The analyzer to be used during the analysis process.      *      */
DECL|method|AnalysisContext
specifier|public
name|AnalysisContext
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|fieldType
argument_list|,
name|analyzer
argument_list|,
name|EMPTY_BYTES_SET
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new AnalysisContext with a given field tpe, analyzer and      * termsToMatch. During the analysis processs, The produced tokens will be       * compaired to the termes in the {@code termsToMatch} set. When found,       * these tokens will be marked as a match.      *      * @param fieldName    The name of the field the analysis is performed on       *                     (may be {@code null}).      * @param fieldType    The type of the field the analysis is performed on.      * @param analyzer     The analyzer to be used.      * @param termsToMatch Holds all the terms that should match during the       *                     analysis process.      */
DECL|method|AnalysisContext
specifier|public
name|AnalysisContext
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termsToMatch
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|termsToMatch
operator|=
name|termsToMatch
expr_stmt|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|getFieldType
specifier|public
name|FieldType
name|getFieldType
parameter_list|()
block|{
return|return
name|fieldType
return|;
block|}
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|getTermsToMatch
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|getTermsToMatch
parameter_list|()
block|{
return|return
name|termsToMatch
return|;
block|}
block|}
block|}
end_class

end_unit

