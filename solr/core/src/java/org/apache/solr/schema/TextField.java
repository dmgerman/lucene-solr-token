begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|Map
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
name|TermToBytesRefAttribute
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
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|SortedSetFieldSource
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
name|search
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
name|util
operator|.
name|QueryBuilder
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
name|query
operator|.
name|SolrRangeQuery
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
name|TextResponseWriter
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
name|QParser
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
name|Sorting
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
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**<code>TextField</code> is the basic type for configurable text analysis.  * Analyzers for field types using this implementation should be defined in the schema.  *  */
end_comment

begin_class
DECL|class|TextField
specifier|public
class|class
name|TextField
extends|extends
name|FieldType
block|{
DECL|field|autoGeneratePhraseQueries
specifier|protected
name|boolean
name|autoGeneratePhraseQueries
decl_stmt|;
comment|/**    * Analyzer set by schema for text types to use when searching fields    * of this type, subclasses can set analyzer themselves or override    * getIndexAnalyzer()    * This analyzer is used to process wildcard, prefix, regex and other multiterm queries. It    * assembles a list of tokenizer +filters that "make sense" for this, primarily accent folding and    * lowercasing filters, and charfilters.    *    * @see #getMultiTermAnalyzer    * @see #setMultiTermAnalyzer    */
DECL|field|multiTermAnalyzer
specifier|protected
name|Analyzer
name|multiTermAnalyzer
init|=
literal|null
decl_stmt|;
DECL|field|isExplicitMultiTermAnalyzer
specifier|private
name|boolean
name|isExplicitMultiTermAnalyzer
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|properties
operator||=
name|TOKENIZED
expr_stmt|;
if|if
condition|(
name|schema
operator|.
name|getVersion
argument_list|()
operator|>
literal|1.1F
operator|&&
comment|// only override if it's not explicitly true
literal|0
operator|==
operator|(
name|trueProperties
operator|&
name|OMIT_TF_POSITIONS
operator|)
condition|)
block|{
name|properties
operator|&=
operator|~
name|OMIT_TF_POSITIONS
expr_stmt|;
block|}
if|if
condition|(
name|schema
operator|.
name|getVersion
argument_list|()
operator|>
literal|1.3F
condition|)
block|{
name|autoGeneratePhraseQueries
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|autoGeneratePhraseQueries
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|autoGeneratePhraseQueriesStr
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"autoGeneratePhraseQueries"
argument_list|)
decl_stmt|;
if|if
condition|(
name|autoGeneratePhraseQueriesStr
operator|!=
literal|null
condition|)
name|autoGeneratePhraseQueries
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|autoGeneratePhraseQueriesStr
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the Analyzer to be used when searching fields of this type when mult-term queries are specified.    *<p>    * This method may be called many times, at any time.    *</p>    * @see #getIndexAnalyzer    */
DECL|method|getMultiTermAnalyzer
specifier|public
name|Analyzer
name|getMultiTermAnalyzer
parameter_list|()
block|{
return|return
name|multiTermAnalyzer
return|;
block|}
DECL|method|setMultiTermAnalyzer
specifier|public
name|void
name|setMultiTermAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|multiTermAnalyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getAutoGeneratePhraseQueries
specifier|public
name|boolean
name|getAutoGeneratePhraseQueries
parameter_list|()
block|{
return|return
name|autoGeneratePhraseQueries
return|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
comment|/* :TODO: maybe warn if isTokenized(), but doesn't use LimitTokenCountFilter in its chain? */
name|field
operator|.
name|checkSortability
argument_list|()
expr_stmt|;
return|return
name|Sorting
operator|.
name|getTextSortField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|reverse
argument_list|,
name|field
operator|.
name|sortMissingLast
argument_list|()
argument_list|,
name|field
operator|.
name|sortMissingFirst
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
return|return
operator|new
name|SortedSetFieldSource
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUninversionType
specifier|public
name|Type
name|getUninversionType
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
return|return
name|Type
operator|.
name|SORTED_SET_BINARY
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|IndexableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
return|return
name|parseFieldQuery
argument_list|(
name|parser
argument_list|,
name|getQueryAnalyzer
argument_list|()
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|externalVal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|term
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|supportsAnalyzers
specifier|protected
name|boolean
name|supportsAnalyzers
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|Analyzer
name|multiAnalyzer
init|=
name|getMultiTermAnalyzer
argument_list|()
decl_stmt|;
name|BytesRef
name|lower
init|=
name|analyzeMultiTerm
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|part1
argument_list|,
name|multiAnalyzer
argument_list|)
decl_stmt|;
name|BytesRef
name|upper
init|=
name|analyzeMultiTerm
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|part2
argument_list|,
name|multiAnalyzer
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
DECL|method|analyzeMultiTerm
specifier|public
specifier|static
name|BytesRef
name|analyzeMultiTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part
parameter_list|,
name|Analyzer
name|analyzerIn
parameter_list|)
block|{
if|if
condition|(
name|part
operator|==
literal|null
operator|||
name|analyzerIn
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
init|(
name|TokenStream
name|source
init|=
name|analyzerIn
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|part
argument_list|)
init|)
block|{
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|source
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
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
literal|"analyzer returned no terms for multiTerm term: "
operator|+
name|part
argument_list|)
throw|;
name|BytesRef
name|bytes
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
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
literal|"analyzer returned too many terms for multiTerm term: "
operator|+
name|part
argument_list|)
throw|;
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
return|return
name|bytes
return|;
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
literal|"error analyzing range part: "
operator|+
name|part
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|parseFieldQuery
specifier|static
name|Query
name|parseFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|)
block|{
comment|// note, this method always worked this way (but nothing calls it?) because it has no idea of quotes...
return|return
operator|new
name|QueryBuilder
argument_list|(
name|analyzer
argument_list|)
operator|.
name|createPhraseQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|)
return|;
block|}
DECL|method|setIsExplicitMultiTermAnalyzer
specifier|public
name|void
name|setIsExplicitMultiTermAnalyzer
parameter_list|(
name|boolean
name|isExplicitMultiTermAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|isExplicitMultiTermAnalyzer
operator|=
name|isExplicitMultiTermAnalyzer
expr_stmt|;
block|}
DECL|method|isExplicitMultiTermAnalyzer
specifier|public
name|boolean
name|isExplicitMultiTermAnalyzer
parameter_list|()
block|{
return|return
name|isExplicitMultiTermAnalyzer
return|;
block|}
annotation|@
name|Override
DECL|method|marshalSortValue
specifier|public
name|Object
name|marshalSortValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|marshalStringSortValue
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|unmarshalSortValue
specifier|public
name|Object
name|unmarshalSortValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|unmarshalStringSortValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

