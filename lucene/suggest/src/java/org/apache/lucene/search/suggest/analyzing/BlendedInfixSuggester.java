begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
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
name|Comparator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|document
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
name|lucene
operator|.
name|document
operator|.
name|TextField
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
name|BinaryDocValues
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
name|IndexOptions
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
name|MultiDocValues
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
name|PostingsEnum
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
name|Terms
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
name|TermsEnum
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
name|BooleanClause
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
name|BooleanQuery
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
name|FieldDoc
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
name|IndexSearcher
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
name|TopFieldDocs
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
name|suggest
operator|.
name|Lookup
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
name|store
operator|.
name|Directory
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

begin_comment
comment|// TODO:
end_comment

begin_comment
comment|// - allow to use the search score
end_comment

begin_comment
comment|/**  * Extension of the AnalyzingInfixSuggester which transforms the weight  * after search to take into account the position of the searched term into  * the indexed text.  * Please note that it increases the number of elements searched and applies the  * ponderation after. It might be costly for long suggestions.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlendedInfixSuggester
specifier|public
class|class
name|BlendedInfixSuggester
extends|extends
name|AnalyzingInfixSuggester
block|{
comment|/**    * Coefficient used for linear blending    */
DECL|field|LINEAR_COEF
specifier|protected
specifier|static
name|double
name|LINEAR_COEF
init|=
literal|0.10
decl_stmt|;
DECL|field|exponent
specifier|private
name|Double
name|exponent
init|=
literal|2.0
decl_stmt|;
comment|/**    * Default factor    */
DECL|field|DEFAULT_NUM_FACTOR
specifier|public
specifier|static
name|int
name|DEFAULT_NUM_FACTOR
init|=
literal|10
decl_stmt|;
comment|/**    * Factor to multiply the number of searched elements    */
DECL|field|numFactor
specifier|private
specifier|final
name|int
name|numFactor
decl_stmt|;
comment|/**    * Type of blender used by the suggester    */
DECL|field|blenderType
specifier|private
specifier|final
name|BlenderType
name|blenderType
decl_stmt|;
comment|/**    * The different types of blender.    */
DECL|enum|BlenderType
specifier|public
specifier|static
enum|enum
name|BlenderType
block|{
comment|/** Application dependent; override {@link      *  #calculateCoefficient} to compute it. */
DECL|enum constant|CUSTOM
name|CUSTOM
block|,
comment|/** weight*(1 - 0.10*position) */
DECL|enum constant|POSITION_LINEAR
name|POSITION_LINEAR
block|,
comment|/** weight/(1+position) */
DECL|enum constant|POSITION_RECIPROCAL
name|POSITION_RECIPROCAL
block|,
comment|/** weight/pow(1+position, exponent) */
DECL|enum constant|POSITION_EXPONENTIAL_RECIPROCAL
name|POSITION_EXPONENTIAL_RECIPROCAL
comment|// TODO:
comment|//SCORE
block|}
comment|/**    * Create a new instance, loading from a previously built    * directory, if it exists.    */
DECL|method|BlendedInfixSuggester
specifier|public
name|BlendedInfixSuggester
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|blenderType
operator|=
name|BlenderType
operator|.
name|POSITION_LINEAR
expr_stmt|;
name|this
operator|.
name|numFactor
operator|=
name|DEFAULT_NUM_FACTOR
expr_stmt|;
block|}
comment|/**    * Create a new instance, loading from a previously built    * directory, if it exists.    *    * @param blenderType Type of blending strategy, see BlenderType for more precisions    * @param numFactor   Factor to multiply the number of searched elements before ponderate    * @param commitOnBuild Call commit after the index has finished building. This would persist the    *                      suggester index to disk and future instances of this suggester can use this pre-built dictionary.    * @throws IOException If there are problems opening the underlying Lucene index.    */
DECL|method|BlendedInfixSuggester
specifier|public
name|BlendedInfixSuggester
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|,
name|int
name|minPrefixChars
parameter_list|,
name|BlenderType
name|blenderType
parameter_list|,
name|int
name|numFactor
parameter_list|,
name|boolean
name|commitOnBuild
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|minPrefixChars
argument_list|,
name|commitOnBuild
argument_list|)
expr_stmt|;
name|this
operator|.
name|blenderType
operator|=
name|blenderType
expr_stmt|;
name|this
operator|.
name|numFactor
operator|=
name|numFactor
expr_stmt|;
block|}
comment|/**    * Create a new instance, loading from a previously built    * directory, if it exists.    *    * @param blenderType Type of blending strategy, see BlenderType for more precisions    * @param numFactor   Factor to multiply the number of searched elements before ponderate    * @param exponent exponent used only when blenderType is  BlenderType.POSITION_EXPONENTIAL_RECIPROCAL    * @param commitOnBuild Call commit after the index has finished building. This would persist the    *                      suggester index to disk and future instances of this suggester can use this pre-built dictionary.    * @param allTermsRequired All terms in the suggest query must be matched.    * @param highlight Highlight suggest query in suggestions.    * @throws IOException If there are problems opening the underlying Lucene index.    */
DECL|method|BlendedInfixSuggester
specifier|public
name|BlendedInfixSuggester
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|,
name|int
name|minPrefixChars
parameter_list|,
name|BlenderType
name|blenderType
parameter_list|,
name|int
name|numFactor
parameter_list|,
name|Double
name|exponent
parameter_list|,
name|boolean
name|commitOnBuild
parameter_list|,
name|boolean
name|allTermsRequired
parameter_list|,
name|boolean
name|highlight
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|minPrefixChars
argument_list|,
name|commitOnBuild
argument_list|,
name|allTermsRequired
argument_list|,
name|highlight
argument_list|)
expr_stmt|;
name|this
operator|.
name|blenderType
operator|=
name|blenderType
expr_stmt|;
name|this
operator|.
name|numFactor
operator|=
name|numFactor
expr_stmt|;
if|if
condition|(
name|exponent
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|exponent
operator|=
name|exponent
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Don't * numFactor here since we do it down below, once, in the call chain:
return|return
name|super
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|contexts
argument_list|,
name|onlyMorePopular
argument_list|,
name|num
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|int
name|num
parameter_list|,
name|boolean
name|allTermsRequired
parameter_list|,
name|boolean
name|doHighlight
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Don't * numFactor here since we do it down below, once, in the call chain:
return|return
name|super
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|contexts
argument_list|,
name|num
argument_list|,
name|allTermsRequired
argument_list|,
name|doHighlight
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|BooleanClause
operator|.
name|Occur
argument_list|>
name|contextInfo
parameter_list|,
name|int
name|num
parameter_list|,
name|boolean
name|allTermsRequired
parameter_list|,
name|boolean
name|doHighlight
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Don't * numFactor here since we do it down below, once, in the call chain:
return|return
name|super
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|contextInfo
argument_list|,
name|num
argument_list|,
name|allTermsRequired
argument_list|,
name|doHighlight
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|BooleanQuery
name|contextQuery
parameter_list|,
name|int
name|num
parameter_list|,
name|boolean
name|allTermsRequired
parameter_list|,
name|boolean
name|doHighlight
parameter_list|)
throws|throws
name|IOException
block|{
comment|/** We need to do num * numFactor here only because it is the last call in the lookup chain*/
return|return
name|super
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|contextQuery
argument_list|,
name|num
operator|*
name|numFactor
argument_list|,
name|allTermsRequired
argument_list|,
name|doHighlight
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTextFieldType
specifier|protected
name|FieldType
name|getTextFieldType
parameter_list|()
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|ft
return|;
block|}
annotation|@
name|Override
DECL|method|createResults
specifier|protected
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|createResults
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TopFieldDocs
name|hits
parameter_list|,
name|int
name|num
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|boolean
name|doHighlight
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|matchedTokens
parameter_list|,
name|String
name|prefixToken
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeSet
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|results
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|LOOKUP_COMP
argument_list|)
decl_stmt|;
comment|// we reduce the num to the one initially requested
name|int
name|actualNum
init|=
name|num
operator|/
name|numFactor
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
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|BinaryDocValues
name|textDV
init|=
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|TEXT_FIELD_NAME
argument_list|)
decl_stmt|;
assert|assert
name|textDV
operator|!=
literal|null
assert|;
name|textDV
operator|.
name|advance
argument_list|(
name|fd
operator|.
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|text
init|=
name|textDV
operator|.
name|binaryValue
argument_list|()
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|long
name|weight
init|=
operator|(
name|Long
operator|)
name|fd
operator|.
name|fields
index|[
literal|0
index|]
decl_stmt|;
comment|// This will just be null if app didn't pass payloads to build():
comment|// TODO: maybe just stored fields?  they compress...
name|BinaryDocValues
name|payloadsDV
init|=
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
literal|"payloads"
argument_list|)
decl_stmt|;
name|BytesRef
name|payload
decl_stmt|;
if|if
condition|(
name|payloadsDV
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|payloadsDV
operator|.
name|advance
argument_list|(
name|fd
operator|.
name|doc
argument_list|)
operator|==
name|fd
operator|.
name|doc
condition|)
block|{
name|payload
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|payloadsDV
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
operator|new
name|BytesRef
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
name|double
name|coefficient
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|startsWith
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
comment|// if hit starts with the key, we don't change the score
name|coefficient
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|coefficient
operator|=
name|createCoefficient
argument_list|(
name|searcher
argument_list|,
name|fd
operator|.
name|doc
argument_list|,
name|matchedTokens
argument_list|,
name|prefixToken
argument_list|)
expr_stmt|;
block|}
name|long
name|score
init|=
call|(
name|long
call|)
argument_list|(
name|weight
operator|*
name|coefficient
argument_list|)
decl_stmt|;
name|LookupResult
name|result
decl_stmt|;
if|if
condition|(
name|doHighlight
condition|)
block|{
name|result
operator|=
operator|new
name|LookupResult
argument_list|(
name|text
argument_list|,
name|highlight
argument_list|(
name|text
argument_list|,
name|matchedTokens
argument_list|,
name|prefixToken
argument_list|)
argument_list|,
name|score
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|LookupResult
argument_list|(
name|text
argument_list|,
name|score
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
name|boundedTreeAdd
argument_list|(
name|results
argument_list|,
name|result
argument_list|,
name|actualNum
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|results
operator|.
name|descendingSet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Add an element to the tree respecting a size limit    *    * @param results the tree to add in    * @param result the result we try to add    * @param num size limit    */
DECL|method|boundedTreeAdd
specifier|private
specifier|static
name|void
name|boundedTreeAdd
parameter_list|(
name|TreeSet
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|results
parameter_list|,
name|Lookup
operator|.
name|LookupResult
name|result
parameter_list|,
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|results
operator|.
name|size
argument_list|()
operator|>=
name|num
condition|)
block|{
if|if
condition|(
name|results
operator|.
name|first
argument_list|()
operator|.
name|value
operator|<
name|result
operator|.
name|value
condition|)
block|{
name|results
operator|.
name|pollFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
name|results
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the coefficient to transform the weight.    *    * @param doc id of the document    * @param matchedTokens tokens found in the query    * @param prefixToken unfinished token in the query    * @return the coefficient    * @throws IOException If there are problems reading term vectors from the underlying Lucene index.    */
DECL|method|createCoefficient
specifier|private
name|double
name|createCoefficient
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|int
name|doc
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|matchedTokens
parameter_list|,
name|String
name|prefixToken
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|tv
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getTermVector
argument_list|(
name|doc
argument_list|,
name|TEXT_FIELD_NAME
argument_list|)
decl_stmt|;
name|TermsEnum
name|it
init|=
name|tv
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Integer
name|position
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
comment|// find the closest token position
while|while
condition|(
operator|(
name|term
operator|=
name|it
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|docTerm
init|=
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
name|matchedTokens
operator|.
name|contains
argument_list|(
name|docTerm
argument_list|)
operator|||
operator|(
name|prefixToken
operator|!=
literal|null
operator|&&
name|docTerm
operator|.
name|startsWith
argument_list|(
name|prefixToken
argument_list|)
operator|)
condition|)
block|{
name|PostingsEnum
name|docPosEnum
init|=
name|it
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|OFFSETS
argument_list|)
decl_stmt|;
name|docPosEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
comment|// use the first occurrence of the term
name|int
name|p
init|=
name|docPosEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|<
name|position
condition|)
block|{
name|position
operator|=
name|p
expr_stmt|;
block|}
block|}
block|}
comment|// create corresponding coefficient based on position
return|return
name|calculateCoefficient
argument_list|(
name|position
argument_list|)
return|;
block|}
comment|/**    * Calculate the weight coefficient based on the position of the first matching word.    * Subclass should override it to adapt it to particular needs    * @param position of the first matching word in text    * @return the coefficient    */
DECL|method|calculateCoefficient
specifier|protected
name|double
name|calculateCoefficient
parameter_list|(
name|int
name|position
parameter_list|)
block|{
name|double
name|coefficient
decl_stmt|;
switch|switch
condition|(
name|blenderType
condition|)
block|{
case|case
name|POSITION_LINEAR
case|:
name|coefficient
operator|=
literal|1
operator|-
name|LINEAR_COEF
operator|*
name|position
expr_stmt|;
break|break;
case|case
name|POSITION_RECIPROCAL
case|:
name|coefficient
operator|=
literal|1.
operator|/
operator|(
name|position
operator|+
literal|1
operator|)
expr_stmt|;
break|break;
case|case
name|POSITION_EXPONENTIAL_RECIPROCAL
case|:
name|coefficient
operator|=
literal|1.
operator|/
name|Math
operator|.
name|pow
argument_list|(
operator|(
name|position
operator|+
literal|1.0
operator|)
argument_list|,
name|exponent
argument_list|)
expr_stmt|;
break|break;
default|default:
name|coefficient
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|coefficient
return|;
block|}
DECL|field|LOOKUP_COMP
specifier|private
specifier|static
name|Comparator
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|LOOKUP_COMP
init|=
operator|new
name|LookUpComparator
argument_list|()
decl_stmt|;
DECL|class|LookUpComparator
specifier|private
specifier|static
class|class
name|LookUpComparator
implements|implements
name|Comparator
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Lookup
operator|.
name|LookupResult
name|o1
parameter_list|,
name|Lookup
operator|.
name|LookupResult
name|o2
parameter_list|)
block|{
comment|// order on weight
if|if
condition|(
name|o1
operator|.
name|value
operator|>
name|o2
operator|.
name|value
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|o1
operator|.
name|value
operator|<
name|o2
operator|.
name|value
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// otherwise on alphabetic order
name|int
name|keyCompare
init|=
name|CHARSEQUENCE_COMPARATOR
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|key
argument_list|,
name|o2
operator|.
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyCompare
operator|!=
literal|0
condition|)
block|{
return|return
name|keyCompare
return|;
block|}
comment|// if same weight and title, use the payload if there is one
if|if
condition|(
name|o1
operator|.
name|payload
operator|!=
literal|null
condition|)
block|{
return|return
name|o1
operator|.
name|payload
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|payload
argument_list|)
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

