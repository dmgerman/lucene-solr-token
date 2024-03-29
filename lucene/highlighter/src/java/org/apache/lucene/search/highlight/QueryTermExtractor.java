begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|MultiReader
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
name|BoostQuery
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
name|Query
import|;
end_import

begin_comment
comment|/**  * Utility class used to extract the terms used in a query, plus any weights.  * This class will not find terms for MultiTermQuery, TermRangeQuery and PrefixQuery classes  * so the caller must pass a rewritten query (see Query.rewrite) to obtain a list of   * expanded terms.   *   */
end_comment

begin_class
DECL|class|QueryTermExtractor
specifier|public
specifier|final
class|class
name|QueryTermExtractor
block|{
comment|/** for term extraction */
DECL|field|EMPTY_INDEXSEARCHER
specifier|private
specifier|static
specifier|final
name|IndexSearcher
name|EMPTY_INDEXSEARCHER
decl_stmt|;
static|static
block|{
try|try
block|{
name|IndexReader
name|emptyReader
init|=
operator|new
name|MultiReader
argument_list|()
decl_stmt|;
name|EMPTY_INDEXSEARCHER
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|emptyReader
argument_list|)
expr_stmt|;
name|EMPTY_INDEXSEARCHER
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
block|}
comment|/**    * Extracts all terms texts of a given Query into an array of WeightedTerms    *    * @param query      Query to extract term texts from    * @return an array of the terms used in a query, plus their weights.    */
DECL|method|getTerms
specifier|public
specifier|static
specifier|final
name|WeightedTerm
index|[]
name|getTerms
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
return|return
name|getTerms
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Extracts all terms texts of a given Query into an array of WeightedTerms    *    * @param query      Query to extract term texts from    * @param reader used to compute IDF which can be used to a) score selected fragments better    * b) use graded highlights eg changing intensity of font color    * @param fieldName the field on which Inverse Document Frequency (IDF) calculations are based    * @return an array of the terms used in a query, plus their weights.    */
DECL|method|getIdfWeightedTerms
specifier|public
specifier|static
specifier|final
name|WeightedTerm
index|[]
name|getIdfWeightedTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|WeightedTerm
index|[]
name|terms
init|=
name|getTerms
argument_list|(
name|query
argument_list|,
literal|false
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|int
name|totalNumDocs
init|=
name|reader
operator|.
name|maxDoc
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|int
name|docFreq
init|=
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|term
argument_list|)
argument_list|)
decl_stmt|;
comment|//IDF algorithm taken from ClassicSimilarity class
name|float
name|idf
init|=
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|totalNumDocs
operator|/
call|(
name|double
call|)
argument_list|(
name|docFreq
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1.0
argument_list|)
decl_stmt|;
name|terms
index|[
name|i
index|]
operator|.
name|weight
operator|*=
name|idf
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//ignore
block|}
block|}
return|return
name|terms
return|;
block|}
comment|/**    * Extracts all terms texts of a given Query into an array of WeightedTerms    *    * @param query      Query to extract term texts from    * @param prohibited<code>true</code> to extract "prohibited" terms, too    * @param fieldName  The fieldName used to filter query terms    * @return an array of the terms used in a query, plus their weights.    */
DECL|method|getTerms
specifier|public
specifier|static
specifier|final
name|WeightedTerm
index|[]
name|getTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|boolean
name|prohibited
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|HashSet
argument_list|<
name|WeightedTerm
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|getTerms
argument_list|(
name|query
argument_list|,
literal|1f
argument_list|,
name|terms
argument_list|,
name|prohibited
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|WeightedTerm
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Extracts all terms texts of a given Query into an array of WeightedTerms    *    * @param query      Query to extract term texts from    * @param prohibited<code>true</code> to extract "prohibited" terms, too    * @return an array of the terms used in a query, plus their weights.    */
DECL|method|getTerms
specifier|public
specifier|static
specifier|final
name|WeightedTerm
index|[]
name|getTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|boolean
name|prohibited
parameter_list|)
block|{
return|return
name|getTerms
argument_list|(
name|query
argument_list|,
name|prohibited
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getTerms
specifier|private
specifier|static
specifier|final
name|void
name|getTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|float
name|boost
parameter_list|,
name|HashSet
argument_list|<
name|WeightedTerm
argument_list|>
name|terms
parameter_list|,
name|boolean
name|prohibited
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|query
operator|instanceof
name|BoostQuery
condition|)
block|{
name|BoostQuery
name|boostQuery
init|=
operator|(
name|BoostQuery
operator|)
name|query
decl_stmt|;
name|getTerms
argument_list|(
name|boostQuery
operator|.
name|getQuery
argument_list|()
argument_list|,
name|boost
operator|*
name|boostQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|terms
argument_list|,
name|prohibited
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
name|getTermsFromBooleanQuery
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|query
argument_list|,
name|boost
argument_list|,
name|terms
argument_list|,
name|prohibited
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
else|else
block|{
name|HashSet
argument_list|<
name|Term
argument_list|>
name|nonWeightedTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|EMPTY_INDEXSEARCHER
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|nonWeightedTerms
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Should not happen on an empty index"
argument_list|,
name|bogus
argument_list|)
throw|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iter
init|=
name|nonWeightedTerms
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Term
name|term
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|fieldName
operator|==
literal|null
operator|)
operator|||
operator|(
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|)
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|WeightedTerm
argument_list|(
name|boost
argument_list|,
name|term
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ignore
parameter_list|)
block|{
comment|//this is non-fatal for our purposes
block|}
block|}
comment|/**    * extractTerms is currently the only query-independent means of introspecting queries but it only reveals    * a list of terms for that query - not the boosts each individual term in that query may or may not have.    * "Container" queries such as BooleanQuery should be unwrapped to get at the boost info held    * in each child element.    * Some discussion around this topic here:    * http://www.gossamer-threads.com/lists/lucene/java-dev/34208?search_string=introspection;#34208    * Unfortunately there seemed to be limited interest in requiring all Query objects to implement    * something common which would allow access to child queries so what follows here are query-specific    * implementations for accessing embedded query elements.    */
DECL|method|getTermsFromBooleanQuery
specifier|private
specifier|static
specifier|final
name|void
name|getTermsFromBooleanQuery
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|float
name|boost
parameter_list|,
name|HashSet
argument_list|<
name|WeightedTerm
argument_list|>
name|terms
parameter_list|,
name|boolean
name|prohibited
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
name|query
control|)
block|{
if|if
condition|(
name|prohibited
operator|||
name|clause
operator|.
name|getOccur
argument_list|()
operator|!=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
condition|)
name|getTerms
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|boost
argument_list|,
name|terms
argument_list|,
name|prohibited
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

