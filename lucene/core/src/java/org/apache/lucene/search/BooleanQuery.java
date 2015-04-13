begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|java
operator|.
name|util
operator|.
name|Set
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
operator|.
name|Occur
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
name|similarities
operator|.
name|Similarity
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
name|ToStringUtils
import|;
end_import

begin_comment
comment|/** A Query that matches documents matching boolean combinations of other   * queries, e.g. {@link TermQuery}s, {@link PhraseQuery}s or other   * BooleanQuerys.   */
end_comment

begin_class
DECL|class|BooleanQuery
specifier|public
class|class
name|BooleanQuery
extends|extends
name|Query
implements|implements
name|Iterable
argument_list|<
name|BooleanClause
argument_list|>
block|{
DECL|field|maxClauseCount
specifier|private
specifier|static
name|int
name|maxClauseCount
init|=
literal|1024
decl_stmt|;
comment|/** Thrown when an attempt is made to add more than {@link    * #getMaxClauseCount()} clauses. This typically happens if    * a PrefixQuery, FuzzyQuery, WildcardQuery, or TermRangeQuery     * is expanded to many terms during search.     */
DECL|class|TooManyClauses
specifier|public
specifier|static
class|class
name|TooManyClauses
extends|extends
name|RuntimeException
block|{
DECL|method|TooManyClauses
specifier|public
name|TooManyClauses
parameter_list|()
block|{
name|super
argument_list|(
literal|"maxClauseCount is set to "
operator|+
name|maxClauseCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Return the maximum number of clauses permitted, 1024 by default.    * Attempts to add more than the permitted number of clauses cause {@link    * TooManyClauses} to be thrown.    * @see #setMaxClauseCount(int)    */
DECL|method|getMaxClauseCount
specifier|public
specifier|static
name|int
name|getMaxClauseCount
parameter_list|()
block|{
return|return
name|maxClauseCount
return|;
block|}
comment|/**     * Set the maximum number of clauses permitted per BooleanQuery.    * Default value is 1024.    */
DECL|method|setMaxClauseCount
specifier|public
specifier|static
name|void
name|setMaxClauseCount
parameter_list|(
name|int
name|maxClauseCount
parameter_list|)
block|{
if|if
condition|(
name|maxClauseCount
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxClauseCount must be>= 1"
argument_list|)
throw|;
block|}
name|BooleanQuery
operator|.
name|maxClauseCount
operator|=
name|maxClauseCount
expr_stmt|;
block|}
DECL|field|clauses
specifier|private
name|ArrayList
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|disableCoord
specifier|private
specifier|final
name|boolean
name|disableCoord
decl_stmt|;
comment|/** Constructs an empty boolean query. */
DECL|method|BooleanQuery
specifier|public
name|BooleanQuery
parameter_list|()
block|{
name|disableCoord
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Constructs an empty boolean query.    *    * {@link Similarity#coord(int,int)} may be disabled in scoring, as    * appropriate. For example, this score factor does not make sense for most    * automatically generated queries, like {@link WildcardQuery} and {@link    * FuzzyQuery}.    *    * @param disableCoord disables {@link Similarity#coord(int,int)} in scoring.    */
DECL|method|BooleanQuery
specifier|public
name|BooleanQuery
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
block|{
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
block|}
comment|/** Returns true iff {@link Similarity#coord(int,int)} is disabled in    * scoring for this query instance.    * @see #BooleanQuery(boolean)    */
DECL|method|isCoordDisabled
specifier|public
name|boolean
name|isCoordDisabled
parameter_list|()
block|{
return|return
name|disableCoord
return|;
block|}
comment|/**    * Specifies a minimum number of the optional BooleanClauses    * which must be satisfied.    *    *<p>    * By default no optional clauses are necessary for a match    * (unless there are no required clauses).  If this method is used,    * then the specified number of clauses is required.    *</p>    *<p>    * Use of this method is totally independent of specifying that    * any specific clauses are required (or prohibited).  This number will    * only be compared against the number of matching optional clauses.    *</p>    *    * @param min the number of optional clauses that must match    */
DECL|method|setMinimumNumberShouldMatch
specifier|public
name|void
name|setMinimumNumberShouldMatch
parameter_list|(
name|int
name|min
parameter_list|)
block|{
name|this
operator|.
name|minNrShouldMatch
operator|=
name|min
expr_stmt|;
block|}
DECL|field|minNrShouldMatch
specifier|protected
name|int
name|minNrShouldMatch
init|=
literal|0
decl_stmt|;
comment|/**    * Gets the minimum number of the optional BooleanClauses    * which must be satisfied.    */
DECL|method|getMinimumNumberShouldMatch
specifier|public
name|int
name|getMinimumNumberShouldMatch
parameter_list|()
block|{
return|return
name|minNrShouldMatch
return|;
block|}
comment|/** Adds a clause to a boolean query.    *    * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number    * @see #getMaxClauseCount()    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Query
name|query
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|occur
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Adds a clause to a boolean query.    * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number    * @see #getMaxClauseCount()    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|BooleanClause
name|clause
parameter_list|)
block|{
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|>=
name|maxClauseCount
condition|)
block|{
throw|throw
operator|new
name|TooManyClauses
argument_list|()
throw|;
block|}
name|clauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the set of clauses in this query. */
DECL|method|getClauses
specifier|public
name|BooleanClause
index|[]
name|getClauses
parameter_list|()
block|{
return|return
name|clauses
operator|.
name|toArray
argument_list|(
operator|new
name|BooleanClause
index|[
name|clauses
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Returns the list of clauses in this query. */
DECL|method|clauses
specifier|public
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
parameter_list|()
block|{
return|return
name|clauses
return|;
block|}
comment|/** Returns an iterator on the clauses in this query. It implements the {@link Iterable} interface to    * make it possible to do:    *<pre class="prettyprint">for (BooleanClause clause : booleanQuery) {}</pre>    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
specifier|final
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|downgradeMustClauseToFilter
specifier|private
specifier|static
name|BooleanQuery
name|downgradeMustClauseToFilter
parameter_list|(
name|BooleanQuery
name|bq
parameter_list|)
block|{
name|BooleanQuery
name|clone
init|=
name|bq
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|clauses
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
operator|.
name|clauses
argument_list|()
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|MUST
condition|)
block|{
name|clone
operator|.
name|add
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clone
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|clone
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|query
init|=
name|this
decl_stmt|;
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
comment|// we rewrite MUST clauses to FILTER for caching
name|query
operator|=
name|downgradeMustClauseToFilter
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BooleanWeight
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|disableCoord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|minNrShouldMatch
operator|==
literal|0
operator|&&
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize 1-clause queries
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
comment|// just return clause
name|Query
name|query
init|=
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// rewrite first
if|if
condition|(
name|c
operator|.
name|isScoring
argument_list|()
condition|)
block|{
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
comment|// incorporate boost
if|if
condition|(
name|query
operator|==
name|c
operator|.
name|getQuery
argument_list|()
condition|)
block|{
comment|// if rewrite was no-op
name|query
operator|=
name|query
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// then clone before boost
block|}
comment|// Since the BooleanQuery only has 1 clause, the BooleanQuery will be
comment|// written out. Therefore the rewritten Query's boost must incorporate both
comment|// the clause's boost, and the boost of the BooleanQuery itself
name|query
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
operator|*
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// our single clause is a filter
if|if
condition|(
name|query
operator|.
name|getBoost
argument_list|()
operator|!=
literal|0f
condition|)
block|{
name|query
operator|=
name|query
operator|.
name|clone
argument_list|()
expr_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|query
return|;
block|}
block|}
name|BooleanQuery
name|clone
init|=
literal|null
decl_stmt|;
comment|// recursively rewrite
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
name|c
operator|.
name|getQuery
argument_list|()
condition|)
block|{
comment|// clause rewrote: must clone
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
block|{
comment|// The BooleanQuery clone is lazily initialized so only initialize
comment|// it if a rewritten clause differs from the original clause (and hasn't been
comment|// initialized already).  If nothing differs, the clone isn't needlessly created
name|clone
operator|=
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|clone
operator|.
name|clauses
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|c
operator|.
name|getOccur
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|isScoring
argument_list|()
condition|)
block|{
name|clause
operator|.
name|getQuery
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|clone
specifier|public
name|BooleanQuery
name|clone
parameter_list|()
block|{
name|BooleanQuery
name|clone
init|=
operator|(
name|BooleanQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|clauses
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|clauses
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|needParens
init|=
name|getBoost
argument_list|()
operator|!=
literal|1.0
operator|||
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|needParens
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|c
operator|.
name|getOccur
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|subQuery
init|=
name|c
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|subQuery
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|subQuery
operator|instanceof
name|BooleanQuery
condition|)
block|{
comment|// wrap sub-bools in parens
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|subQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|subQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|!=
name|clauses
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|needParens
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'~'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|BooleanQuery
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BooleanQuery
name|other
init|=
operator|(
name|BooleanQuery
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
name|this
operator|.
name|clauses
operator|.
name|equals
argument_list|(
name|other
operator|.
name|clauses
argument_list|)
operator|&&
name|this
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|==
name|other
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|&&
name|this
operator|.
name|disableCoord
operator|==
name|other
operator|.
name|disableCoord
return|;
block|}
comment|/** Returns a hash code value for this object.*/
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
operator|^
name|clauses
operator|.
name|hashCode
argument_list|()
operator|+
name|getMinimumNumberShouldMatch
argument_list|()
operator|+
operator|(
name|disableCoord
condition|?
literal|17
else|:
literal|0
operator|)
return|;
block|}
block|}
end_class

end_unit

