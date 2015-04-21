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
name|Arrays
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
name|LeafReaderContext
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * Expert: the Weight for BooleanQuery, used to  * normalize, score and explain these queries.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BooleanWeight
specifier|public
class|class
name|BooleanWeight
extends|extends
name|Weight
block|{
comment|/** The Similarity implementation. */
DECL|field|similarity
specifier|protected
name|Similarity
name|similarity
decl_stmt|;
DECL|field|query
specifier|protected
specifier|final
name|BooleanQuery
name|query
decl_stmt|;
DECL|field|weights
specifier|protected
name|ArrayList
argument_list|<
name|Weight
argument_list|>
name|weights
decl_stmt|;
DECL|field|maxCoord
specifier|protected
name|int
name|maxCoord
decl_stmt|;
comment|// num optional + num required
DECL|field|disableCoord
specifier|private
specifier|final
name|boolean
name|disableCoord
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|coords
specifier|private
specifier|final
name|float
name|coords
index|[]
decl_stmt|;
DECL|method|BooleanWeight
specifier|public
name|BooleanWeight
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
name|weights
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
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
name|query
operator|.
name|clauses
argument_list|()
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
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
name|searcher
operator|.
name|createWeight
argument_list|(
name|c
operator|.
name|getQuery
argument_list|()
argument_list|,
name|needsScores
operator|&&
name|c
operator|.
name|isScoring
argument_list|()
argument_list|)
decl_stmt|;
name|weights
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|isScoring
argument_list|()
condition|)
block|{
name|maxCoord
operator|++
expr_stmt|;
block|}
block|}
comment|// precompute coords (0..N, N).
comment|// set disableCoord when its explicit, scores are not needed, no scoring clauses, or the sim doesn't use it.
name|coords
operator|=
operator|new
name|float
index|[
name|maxCoord
operator|+
literal|1
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|coords
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
name|coords
index|[
literal|0
index|]
operator|=
literal|0f
expr_stmt|;
if|if
condition|(
name|maxCoord
operator|>
literal|0
operator|&&
name|needsScores
operator|&&
name|disableCoord
operator|==
literal|false
condition|)
block|{
comment|// compute coords from the similarity, look for any actual ones.
name|boolean
name|seenActualCoord
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|coords
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|coords
index|[
name|i
index|]
operator|=
name|coord
argument_list|(
name|i
argument_list|,
name|maxCoord
argument_list|)
expr_stmt|;
name|seenActualCoord
operator||=
operator|(
name|coords
index|[
name|i
index|]
operator|!=
literal|1F
operator|)
expr_stmt|;
block|}
name|this
operator|.
name|disableCoord
operator|=
name|seenActualCoord
operator|==
literal|false
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|disableCoord
operator|=
literal|true
expr_stmt|;
block|}
block|}
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
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|query
operator|.
name|clauses
argument_list|()
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|isScoring
argument_list|()
operator|||
operator|(
name|needsScores
operator|==
literal|false
operator|&&
name|clause
operator|.
name|isProhibited
argument_list|()
operator|==
literal|false
operator|)
condition|)
block|{
name|weights
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
literal|0.0f
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
name|weights
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// call sumOfSquaredWeights for all clauses in case of side effects
name|float
name|s
init|=
name|weights
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValueForNormalization
argument_list|()
decl_stmt|;
comment|// sum sub weights
if|if
condition|(
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isScoring
argument_list|()
condition|)
block|{
comment|// only add to sum for scoring clauses
name|sum
operator|+=
name|s
expr_stmt|;
block|}
block|}
name|sum
operator|*=
name|query
operator|.
name|getBoost
argument_list|()
operator|*
name|query
operator|.
name|getBoost
argument_list|()
expr_stmt|;
comment|// boost each sub-weight
return|return
name|sum
return|;
block|}
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
if|if
condition|(
name|overlap
operator|==
literal|0
condition|)
block|{
comment|// special case that there are only non-scoring clauses
return|return
literal|0F
return|;
block|}
elseif|else
if|if
condition|(
name|maxOverlap
operator|==
literal|1
condition|)
block|{
comment|// LUCENE-4300: in most cases of maxOverlap=1, BQ rewrites itself away,
comment|// so coord() is not applied. But when BQ cannot optimize itself away
comment|// for a single clause (minNrShouldMatch, prohibited clauses, etc), it's
comment|// important not to apply coord(1,1) for consistency, it might not be 1.0F
return|return
literal|1F
return|;
block|}
else|else
block|{
comment|// common case: use the similarity to compute the coord
return|return
name|similarity
operator|.
name|coord
argument_list|(
name|overlap
argument_list|,
name|maxOverlap
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|topLevelBoost
operator|*=
name|query
operator|.
name|getBoost
argument_list|()
expr_stmt|;
comment|// incorporate boost
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
comment|// normalize all clauses, (even if non-scoring in case of side affects)
name|w
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|minShouldMatch
init|=
name|query
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|coord
init|=
literal|0
decl_stmt|;
name|float
name|sum
init|=
literal|0.0f
decl_stmt|;
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
name|int
name|matchCount
init|=
literal|0
decl_stmt|;
name|int
name|shouldMatchCount
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Weight
argument_list|>
name|wIter
init|=
name|weights
operator|.
name|iterator
argument_list|()
init|;
name|wIter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Weight
name|w
init|=
name|wIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|BooleanClause
name|c
init|=
name|cIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Explanation
name|e
init|=
name|w
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|isMatch
argument_list|()
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|isScoring
argument_list|()
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|e
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|coord
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|Explanation
operator|.
name|match
argument_list|(
literal|0f
argument_list|,
literal|"match on required clause, product of:"
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
literal|0f
argument_list|,
name|Occur
operator|.
name|FILTER
operator|+
literal|" clause"
argument_list|)
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"match on prohibited clause ("
operator|+
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|matchCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|SHOULD
condition|)
block|{
name|shouldMatchCount
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"no match on required clause ("
operator|+
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fail
condition|)
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Failure to meet condition(s) of required/prohibited clause(s)"
argument_list|,
name|subs
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|matchCount
operator|==
literal|0
condition|)
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No matching clauses"
argument_list|,
name|subs
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|shouldMatchCount
operator|<
name|minShouldMatch
condition|)
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Failure to match minimum number of optional clauses: "
operator|+
name|minShouldMatch
argument_list|,
name|subs
argument_list|)
return|;
block|}
else|else
block|{
comment|// we have a match
name|Explanation
name|result
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|sum
argument_list|,
literal|"sum of:"
argument_list|,
name|subs
argument_list|)
decl_stmt|;
specifier|final
name|float
name|coordFactor
init|=
name|disableCoord
condition|?
literal|1.0f
else|:
name|coord
argument_list|(
name|coord
argument_list|,
name|maxCoord
argument_list|)
decl_stmt|;
if|if
condition|(
name|coordFactor
operator|!=
literal|1f
condition|)
block|{
name|result
operator|=
name|Explanation
operator|.
name|match
argument_list|(
name|sum
operator|*
name|coordFactor
argument_list|,
literal|"product of:"
argument_list|,
name|result
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|coordFactor
argument_list|,
literal|"coord("
operator|+
name|coord
operator|+
literal|"/"
operator|+
name|maxCoord
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/** Try to build a boolean scorer for this weight. Returns null if {@link BooleanScorer}    *  cannot be used. */
comment|// pkg-private for forcing use of BooleanScorer in tests
DECL|method|booleanScorer
name|BooleanScorer
name|booleanScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BulkScorer
argument_list|>
name|optional
init|=
operator|new
name|ArrayList
argument_list|<
name|BulkScorer
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
name|BooleanClause
name|c
init|=
name|cIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|BulkScorer
name|subScorer
init|=
name|w
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
comment|// TODO: there are some cases where BooleanScorer
comment|// would handle conjunctions faster than
comment|// BooleanScorer2...
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
comment|// TODO: there are some cases where BooleanScorer could do this faster
return|return
literal|null
return|;
block|}
else|else
block|{
name|optional
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|optional
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|query
operator|.
name|minNrShouldMatch
operator|>
name|optional
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|BooleanScorer
argument_list|(
name|this
argument_list|,
name|disableCoord
argument_list|,
name|maxCoord
argument_list|,
name|optional
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|query
operator|.
name|minNrShouldMatch
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BooleanScorer
name|bulkScorer
init|=
name|booleanScorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|bulkScorer
operator|!=
literal|null
condition|)
block|{
comment|// BooleanScorer is applicable
comment|// TODO: what is the right heuristic here?
specifier|final
name|long
name|costThreshold
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|minNrShouldMatch
operator|<=
literal|1
condition|)
block|{
comment|// when all clauses are optional, use BooleanScorer aggressively
comment|// TODO: is there actually a threshold under which we should rather
comment|// use the regular scorer?
name|costThreshold
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// when a minimum number of clauses should match, BooleanScorer is
comment|// going to score all windows that have at least minNrShouldMatch
comment|// matches in the window. But there is no way to know if there is
comment|// an intersection (all clauses might match a different doc ID and
comment|// there will be no matches in the end) so we should only use
comment|// BooleanScorer if matches are very dense
name|costThreshold
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
operator|/
literal|3
expr_stmt|;
block|}
if|if
condition|(
name|bulkScorer
operator|.
name|cost
argument_list|()
operator|>
name|costThreshold
condition|)
block|{
return|return
name|bulkScorer
return|;
block|}
block|}
return|return
name|super
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// initially the user provided value,
comment|// but if minNrShouldMatch == optional.size(),
comment|// we will optimize and move these to required, making this 0
name|int
name|minShouldMatch
init|=
name|query
operator|.
name|minNrShouldMatch
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|required
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// clauses that are required AND participate in scoring, subset of 'required'
name|List
argument_list|<
name|Scorer
argument_list|>
name|requiredScoring
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|prohibited
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|optional
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
name|BooleanClause
name|c
init|=
name|cIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|required
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|isScoring
argument_list|()
condition|)
block|{
name|requiredScoring
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|prohibited
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|optional
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
block|}
comment|// scorer simplifications:
if|if
condition|(
name|optional
operator|.
name|size
argument_list|()
operator|==
name|minShouldMatch
condition|)
block|{
comment|// any optional clauses are in fact required
name|required
operator|.
name|addAll
argument_list|(
name|optional
argument_list|)
expr_stmt|;
name|requiredScoring
operator|.
name|addAll
argument_list|(
name|optional
argument_list|)
expr_stmt|;
name|optional
operator|.
name|clear
argument_list|()
expr_stmt|;
name|minShouldMatch
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|required
operator|.
name|isEmpty
argument_list|()
operator|&&
name|optional
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no required and optional clauses.
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|optional
operator|.
name|size
argument_list|()
operator|<
name|minShouldMatch
condition|)
block|{
comment|// either>1 req scorer, or there are 0 req scorers and at least 1
comment|// optional scorer. Therefore if there are not enough optional scorers
comment|// no documents will be matched by the query
return|return
literal|null
return|;
block|}
comment|// we don't need scores, so if we have required clauses, drop optional clauses completely
if|if
condition|(
operator|!
name|needsScores
operator|&&
name|minShouldMatch
operator|==
literal|0
operator|&&
name|required
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optional
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// three cases: conjunction, disjunction, or mix
comment|// pure conjunction
if|if
condition|(
name|optional
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|excl
argument_list|(
name|req
argument_list|(
name|required
argument_list|,
name|requiredScoring
argument_list|,
name|disableCoord
argument_list|)
argument_list|,
name|prohibited
argument_list|)
return|;
block|}
comment|// pure disjunction
if|if
condition|(
name|required
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|excl
argument_list|(
name|opt
argument_list|(
name|optional
argument_list|,
name|minShouldMatch
argument_list|,
name|disableCoord
argument_list|)
argument_list|,
name|prohibited
argument_list|)
return|;
block|}
comment|// conjunction-disjunction mix:
comment|// we create the required and optional pieces with coord disabled, and then
comment|// combine the two: if minNrShouldMatch> 0, then it's a conjunction: because the
comment|// optional side must match. otherwise it's required + optional, factoring the
comment|// number of optional terms into the coord calculation
name|Scorer
name|req
init|=
name|excl
argument_list|(
name|req
argument_list|(
name|required
argument_list|,
name|requiredScoring
argument_list|,
literal|true
argument_list|)
argument_list|,
name|prohibited
argument_list|)
decl_stmt|;
name|Scorer
name|opt
init|=
name|opt
argument_list|(
name|optional
argument_list|,
name|minShouldMatch
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// TODO: clean this up: it's horrible
if|if
condition|(
name|disableCoord
condition|)
block|{
if|if
condition|(
name|minShouldMatch
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|this
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|req
argument_list|,
name|opt
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|req
argument_list|,
name|opt
argument_list|)
argument_list|,
literal|1F
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ReqOptSumScorer
argument_list|(
name|req
argument_list|,
name|opt
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|optional
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|minShouldMatch
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|this
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|req
argument_list|,
name|opt
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|req
argument_list|,
name|opt
argument_list|)
argument_list|,
name|coord
argument_list|(
name|requiredScoring
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|,
name|maxCoord
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|float
name|coordReq
init|=
name|coord
argument_list|(
name|requiredScoring
operator|.
name|size
argument_list|()
argument_list|,
name|maxCoord
argument_list|)
decl_stmt|;
name|float
name|coordBoth
init|=
name|coord
argument_list|(
name|requiredScoring
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|,
name|maxCoord
argument_list|)
decl_stmt|;
return|return
operator|new
name|BooleanTopLevelScorers
operator|.
name|ReqSingleOptScorer
argument_list|(
name|req
argument_list|,
name|opt
argument_list|,
name|coordReq
argument_list|,
name|coordBoth
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|minShouldMatch
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|BooleanTopLevelScorers
operator|.
name|CoordinatingConjunctionScorer
argument_list|(
name|this
argument_list|,
name|coords
argument_list|,
name|req
argument_list|,
name|requiredScoring
operator|.
name|size
argument_list|()
argument_list|,
name|opt
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BooleanTopLevelScorers
operator|.
name|ReqMultiOptScorer
argument_list|(
name|req
argument_list|,
name|opt
argument_list|,
name|requiredScoring
operator|.
name|size
argument_list|()
argument_list|,
name|coords
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Create a new scorer for the given required clauses. Note that    *  {@code requiredScoring} is a subset of {@code required} containing    *  required clauses that should participate in scoring. */
DECL|method|req
specifier|private
name|Scorer
name|req
parameter_list|(
name|List
argument_list|<
name|Scorer
argument_list|>
name|required
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|requiredScoring
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
block|{
if|if
condition|(
name|required
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Scorer
name|req
init|=
name|required
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|needsScores
operator|==
literal|false
operator|||
operator|(
name|requiredScoring
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|disableCoord
operator|||
name|maxCoord
operator|==
literal|1
operator|)
operator|)
condition|)
block|{
return|return
name|req
return|;
block|}
else|else
block|{
return|return
operator|new
name|BooleanTopLevelScorers
operator|.
name|BoostedScorer
argument_list|(
name|req
argument_list|,
name|coord
argument_list|(
name|requiredScoring
operator|.
name|size
argument_list|()
argument_list|,
name|maxCoord
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|this
argument_list|,
name|required
argument_list|,
name|requiredScoring
argument_list|,
name|disableCoord
condition|?
literal|1.0F
else|:
name|coord
argument_list|(
name|requiredScoring
operator|.
name|size
argument_list|()
argument_list|,
name|maxCoord
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|excl
specifier|private
name|Scorer
name|excl
parameter_list|(
name|Scorer
name|main
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|prohibited
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|prohibited
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|main
return|;
block|}
elseif|else
if|if
condition|(
name|prohibited
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|ReqExclScorer
argument_list|(
name|main
argument_list|,
name|prohibited
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|float
name|coords
index|[]
init|=
operator|new
name|float
index|[
name|prohibited
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|coords
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
return|return
operator|new
name|ReqExclScorer
argument_list|(
name|main
argument_list|,
operator|new
name|DisjunctionSumScorer
argument_list|(
name|this
argument_list|,
name|prohibited
argument_list|,
name|coords
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|opt
specifier|private
name|Scorer
name|opt
parameter_list|(
name|List
argument_list|<
name|Scorer
argument_list|>
name|optional
parameter_list|,
name|int
name|minShouldMatch
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|optional
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Scorer
name|opt
init|=
name|optional
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|disableCoord
operator|&&
name|maxCoord
operator|>
literal|1
condition|)
block|{
return|return
operator|new
name|BooleanTopLevelScorers
operator|.
name|BoostedScorer
argument_list|(
name|opt
argument_list|,
name|coord
argument_list|(
literal|1
argument_list|,
name|maxCoord
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|opt
return|;
block|}
block|}
else|else
block|{
name|float
name|coords
index|[]
decl_stmt|;
if|if
condition|(
name|disableCoord
condition|)
block|{
comment|// sneaky: when we do a mixed conjunction/disjunction, we need a fake for the disjunction part.
name|coords
operator|=
operator|new
name|float
index|[
name|optional
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|coords
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|coords
operator|=
name|this
operator|.
name|coords
expr_stmt|;
block|}
if|if
condition|(
name|minShouldMatch
operator|>
literal|1
condition|)
block|{
return|return
operator|new
name|MinShouldMatchSumScorer
argument_list|(
name|this
argument_list|,
name|optional
argument_list|,
name|minShouldMatch
argument_list|,
name|coords
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DisjunctionSumScorer
argument_list|(
name|this
argument_list|,
name|optional
argument_list|,
name|coords
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

