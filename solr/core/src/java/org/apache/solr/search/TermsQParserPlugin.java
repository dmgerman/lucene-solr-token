begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|Term
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
name|TermsQuery
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
name|AutomatonQuery
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
name|DocValuesTermsQuery
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
name|MatchNoDocsQuery
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
name|Query
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
name|TermQuery
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
name|BytesRefBuilder
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
name|automaton
operator|.
name|Automata
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
name|automaton
operator|.
name|Automaton
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
name|FieldType
import|;
end_import

begin_comment
comment|/**  * Finds documents whose specified field has any of the specified values. It's like  * {@link TermQParserPlugin} but multi-valued, and supports a variety of internal algorithms.  *<br>Parameters:  *<br><code>f</code>: The field name (mandatory)  *<br><code>separator</code>: the separator delimiting the values in the query string, defaulting to a comma.  * If it's a " " then it splits on any consecutive whitespace.  *<br><code>method</code>: Any of termsFilter (default), booleanQuery, automaton, docValuesTermsFilter.  *<p>  * Note that if no values are specified then the query matches no documents.  */
end_comment

begin_class
DECL|class|TermsQParserPlugin
specifier|public
class|class
name|TermsQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"terms"
decl_stmt|;
comment|/** The separator to use in the underlying suggester */
DECL|field|SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"separator"
decl_stmt|;
comment|/** Choose the internal algorithm */
DECL|field|METHOD
specifier|private
specifier|static
specifier|final
name|String
name|METHOD
init|=
literal|"method"
decl_stmt|;
DECL|enum|Method
specifier|private
specifier|static
enum|enum
name|Method
block|{
DECL|enum constant|termsFilter
name|termsFilter
block|{
annotation|@
name|Override
name|Filter
name|makeFilter
parameter_list|(
name|String
name|fname
parameter_list|,
name|BytesRef
index|[]
name|bytesRefs
parameter_list|)
block|{
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermsQuery
argument_list|(
name|fname
argument_list|,
name|bytesRefs
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|booleanQuery
name|booleanQuery
block|{
annotation|@
name|Override
name|Filter
name|makeFilter
parameter_list|(
name|String
name|fname
parameter_list|,
name|BytesRef
index|[]
name|byteRefs
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|byteRef
range|:
name|byteRefs
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fname
argument_list|,
name|byteRef
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|automaton
name|automaton
block|{
annotation|@
name|Override
name|Filter
name|makeFilter
parameter_list|(
name|String
name|fname
parameter_list|,
name|BytesRef
index|[]
name|byteRefs
parameter_list|)
block|{
name|Automaton
name|union
init|=
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|byteRefs
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|AutomatonQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fname
argument_list|)
argument_list|,
name|union
argument_list|)
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|docValuesTermsFilter
name|docValuesTermsFilter
block|{
comment|//on 4x this is FieldCacheTermsFilter but we use the 5x name any way
annotation|@
name|Override
name|Filter
name|makeFilter
parameter_list|(
name|String
name|fname
parameter_list|,
name|BytesRef
index|[]
name|byteRefs
parameter_list|)
block|{
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|DocValuesTermsQuery
argument_list|(
name|fname
argument_list|,
name|byteRefs
argument_list|)
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|makeFilter
specifier|abstract
name|Filter
name|makeFilter
parameter_list|(
name|String
name|fname
parameter_list|,
name|BytesRef
index|[]
name|byteRefs
parameter_list|)
function_decl|;
block|}
annotation|@
name|Override
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|QParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|fname
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|F
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|String
name|separator
init|=
name|localParams
operator|.
name|get
argument_list|(
name|SEPARATOR
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|String
name|qstr
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
decl_stmt|;
comment|//never null
name|Method
name|method
init|=
name|Method
operator|.
name|valueOf
argument_list|(
name|localParams
operator|.
name|get
argument_list|(
name|METHOD
argument_list|,
name|Method
operator|.
name|termsFilter
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//TODO pick the default method based on various heuristics from benchmarks
comment|//if space then split on all whitespace& trim, otherwise strictly interpret
specifier|final
name|boolean
name|sepIsSpace
init|=
name|separator
operator|.
name|equals
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|sepIsSpace
condition|)
name|qstr
operator|=
name|qstr
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|qstr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
specifier|final
name|String
index|[]
name|splitVals
init|=
name|sepIsSpace
condition|?
name|qstr
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
else|:
name|qstr
operator|.
name|split
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|separator
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|splitVals
operator|.
name|length
operator|>
literal|0
assert|;
name|BytesRef
index|[]
name|bytesRefs
init|=
operator|new
name|BytesRef
index|[
name|splitVals
operator|.
name|length
index|]
decl_stmt|;
name|BytesRefBuilder
name|term
init|=
operator|new
name|BytesRefBuilder
argument_list|()
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
name|splitVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|stringVal
init|=
name|splitVals
index|[
name|i
index|]
decl_stmt|;
comment|//logic same as TermQParserPlugin
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
name|ft
operator|.
name|readableToIndexed
argument_list|(
name|stringVal
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|term
operator|.
name|copyChars
argument_list|(
name|stringVal
argument_list|)
expr_stmt|;
block|}
name|bytesRefs
index|[
name|i
index|]
operator|=
name|term
operator|.
name|toBytesRef
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SolrConstantScoreQuery
argument_list|(
name|method
operator|.
name|makeFilter
argument_list|(
name|fname
argument_list|,
name|bytesRefs
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

