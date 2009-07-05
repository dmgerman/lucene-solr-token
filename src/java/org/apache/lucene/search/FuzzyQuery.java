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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|PriorityQueue
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** Implements the fuzzy search query. The similiarity measurement  * is based on the Levenshtein (edit distance) algorithm.  */
end_comment

begin_class
DECL|class|FuzzyQuery
specifier|public
class|class
name|FuzzyQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|defaultMinSimilarity
specifier|public
specifier|final
specifier|static
name|float
name|defaultMinSimilarity
init|=
literal|0.5f
decl_stmt|;
DECL|field|defaultPrefixLength
specifier|public
specifier|final
specifier|static
name|int
name|defaultPrefixLength
init|=
literal|0
decl_stmt|;
DECL|field|minimumSimilarity
specifier|private
name|float
name|minimumSimilarity
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
decl_stmt|;
DECL|field|termLongEnough
specifier|private
name|boolean
name|termLongEnough
init|=
literal|false
decl_stmt|;
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
comment|/**    * Create a new FuzzyQuery that will match terms with a similarity     * of at least<code>minimumSimilarity</code> to<code>term</code>.    * If a<code>prefixLength</code>&gt; 0 is specified, a common prefix    * of that length is also required.    *     * @param term the term to search for    * @param minimumSimilarity a value between 0 and 1 to set the required similarity    *  between the query term and the matching terms. For example, for a    *<code>minimumSimilarity</code> of<code>0.5</code> a term of the same length    *  as the query term is considered similar to the query term if the edit distance    *  between both terms is less than<code>length(term)*0.5</code>    * @param prefixLength length of common (non-fuzzy) prefix    * @throws IllegalArgumentException if minimumSimilarity is&gt;= 1 or&lt; 0    * or if prefixLength&lt; 0    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|minimumSimilarity
parameter_list|,
name|int
name|prefixLength
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
comment|// will be removed in 3.0
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
if|if
condition|(
name|minimumSimilarity
operator|>=
literal|1.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity>= 1"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|minimumSimilarity
operator|<
literal|0.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity< 0"
argument_list|)
throw|;
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength< 0"
argument_list|)
throw|;
if|if
condition|(
name|term
operator|.
name|text
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|1.0f
operator|/
operator|(
literal|1.0f
operator|-
name|minimumSimilarity
operator|)
condition|)
block|{
name|this
operator|.
name|termLongEnough
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|minimumSimilarity
operator|=
name|minimumSimilarity
expr_stmt|;
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, float) FuzzyQuery(term, minimumSimilarity, 0)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|minimumSimilarity
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|this
argument_list|(
name|term
argument_list|,
name|minimumSimilarity
argument_list|,
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, float) FuzzyQuery(term, 0.5f, 0)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|defaultMinSimilarity
argument_list|,
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the minimum similarity that is required for this query to match.    * @return float value between 0.0 and 1.0    */
DECL|method|getMinSimilarity
specifier|public
name|float
name|getMinSimilarity
parameter_list|()
block|{
return|return
name|minimumSimilarity
return|;
block|}
comment|/**    * Returns the non-fuzzy prefix length. This is the number of characters at the start    * of a term that must be identical (not fuzzy) to the query term if the query    * is to match that term.     */
DECL|method|getPrefixLength
specifier|public
name|int
name|getPrefixLength
parameter_list|()
block|{
return|return
name|prefixLength
return|;
block|}
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FuzzyTermEnum
argument_list|(
name|reader
argument_list|,
name|getTerm
argument_list|()
argument_list|,
name|minimumSimilarity
argument_list|,
name|prefixLength
argument_list|)
return|;
block|}
comment|/**    * Returns the pattern term.    */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
DECL|method|setConstantScoreRewrite
specifier|public
name|void
name|setConstantScoreRewrite
parameter_list|(
name|boolean
name|constantScoreRewrite
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"FuzzyQuery cannot rewrite to a constant score query"
argument_list|)
throw|;
block|}
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
operator|!
name|termLongEnough
condition|)
block|{
comment|// can't match
return|return
operator|new
name|BooleanQuery
argument_list|()
return|;
block|}
name|FilteredTermEnum
name|enumerator
init|=
name|getEnum
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|maxClauseCount
init|=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
decl_stmt|;
name|ScoreTermQueue
name|stQueue
init|=
operator|new
name|ScoreTermQueue
argument_list|(
name|maxClauseCount
argument_list|)
decl_stmt|;
name|ScoreTerm
name|reusableST
init|=
literal|null
decl_stmt|;
try|try
block|{
do|do
block|{
name|float
name|score
init|=
literal|0.0f
decl_stmt|;
name|Term
name|t
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|score
operator|=
name|enumerator
operator|.
name|difference
argument_list|()
expr_stmt|;
if|if
condition|(
name|reusableST
operator|==
literal|null
condition|)
block|{
name|reusableST
operator|=
operator|new
name|ScoreTerm
argument_list|(
name|t
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|score
operator|>=
name|reusableST
operator|.
name|score
condition|)
block|{
comment|// reusableST holds the last "rejected" entry, so, if
comment|// this new score is not better than that, there's no
comment|// need to try inserting it
name|reusableST
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|reusableST
operator|.
name|term
operator|=
name|t
expr_stmt|;
block|}
else|else
block|{
continue|continue;
block|}
name|reusableST
operator|=
operator|(
name|ScoreTerm
operator|)
name|stQueue
operator|.
name|insertWithOverflow
argument_list|(
name|reusableST
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|stQueue
operator|.
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ScoreTerm
name|st
init|=
operator|(
name|ScoreTerm
operator|)
name|stQueue
operator|.
name|pop
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|st
operator|.
name|term
argument_list|)
decl_stmt|;
comment|// found a match
name|tq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
operator|*
name|st
operator|.
name|score
argument_list|)
expr_stmt|;
comment|// set the boost
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// add to query
block|}
return|return
name|query
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
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
name|Float
operator|.
name|toString
argument_list|(
name|minimumSimilarity
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|ScoreTerm
specifier|protected
specifier|static
class|class
name|ScoreTerm
block|{
DECL|field|term
specifier|public
name|Term
name|term
decl_stmt|;
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
DECL|method|ScoreTerm
specifier|public
name|ScoreTerm
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
block|}
DECL|class|ScoreTermQueue
specifier|protected
specifier|static
class|class
name|ScoreTermQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|ScoreTermQueue
specifier|public
name|ScoreTermQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.apache.lucene.util.PriorityQueue#lessThan(java.lang.Object, java.lang.Object)      */
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
name|ScoreTerm
name|termA
init|=
operator|(
name|ScoreTerm
operator|)
name|a
decl_stmt|;
name|ScoreTerm
name|termB
init|=
operator|(
name|ScoreTerm
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|termA
operator|.
name|score
operator|==
name|termB
operator|.
name|score
condition|)
return|return
name|termA
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|termB
operator|.
name|term
argument_list|)
operator|>
literal|0
return|;
else|else
return|return
name|termA
operator|.
name|score
operator|<
name|termB
operator|.
name|score
return|;
block|}
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minimumSimilarity
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|prefixLength
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|term
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FuzzyQuery
name|other
init|=
operator|(
name|FuzzyQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minimumSimilarity
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|minimumSimilarity
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|prefixLength
operator|!=
name|other
operator|.
name|prefixLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|term
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

