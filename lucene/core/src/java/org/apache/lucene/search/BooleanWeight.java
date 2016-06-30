begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Expert: the Weight for BooleanQuery, used to  * normalize, score and explain these queries.  */
end_comment

begin_class
DECL|class|BooleanWeight
specifier|final
class|class
name|BooleanWeight
extends|extends
name|Weight
block|{
comment|/** The Similarity implementation. */
DECL|field|similarity
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|query
specifier|final
name|BooleanQuery
name|query
decl_stmt|;
DECL|field|weights
specifier|final
name|ArrayList
argument_list|<
name|Weight
argument_list|>
name|weights
decl_stmt|;
DECL|field|needsScores
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|method|BooleanWeight
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
argument_list|(
name|needsScores
argument_list|)
expr_stmt|;
name|weights
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|query
control|)
block|{
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
name|clause
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
name|i
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|sum
return|;
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
name|boost
parameter_list|)
block|{
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
name|boost
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
return|return
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
return|;
block|}
block|}
DECL|method|disableScoring
specifier|static
name|BulkScorer
name|disableScoring
parameter_list|(
specifier|final
name|BulkScorer
name|scorer
parameter_list|)
block|{
return|return
operator|new
name|BulkScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|score
parameter_list|(
specifier|final
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafCollector
name|noScoreCollector
init|=
operator|new
name|LeafCollector
argument_list|()
block|{
name|FakeScorer
name|fake
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|fake
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|fake
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
name|scorer
operator|.
name|score
argument_list|(
name|noScoreCollector
argument_list|,
name|acceptDocs
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|// Return a BulkScorer for the optional clauses only,
comment|// or null if it is not applicable
comment|// pkg-private for forcing use of BooleanScorer in tests
DECL|method|optionalBulkScorer
name|BulkScorer
name|optionalBulkScorer
parameter_list|(
name|LeafReaderContext
name|context
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
if|if
condition|(
name|c
operator|.
name|getOccur
argument_list|()
operator|!=
name|Occur
operator|.
name|SHOULD
condition|)
block|{
continue|continue;
block|}
name|BulkScorer
name|subScorer
init|=
name|w
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|!=
literal|null
condition|)
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
name|getMinimumNumberShouldMatch
argument_list|()
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
return|return
name|optional
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
operator|new
name|BooleanScorer
argument_list|(
name|this
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
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
comment|// Return a BulkScorer for the required clauses only,
comment|// or null if it is not applicable
DECL|method|requiredBulkScorer
specifier|private
name|BulkScorer
name|requiredBulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BulkScorer
name|scorer
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|query
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
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
comment|// we don't have a BulkScorer for conjunctions
return|return
literal|null
return|;
block|}
name|scorer
operator|=
name|w
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
comment|// no matches
return|return
literal|null
return|;
block|}
if|if
condition|(
name|c
operator|.
name|isScoring
argument_list|()
operator|==
literal|false
operator|&&
name|needsScores
condition|)
block|{
name|scorer
operator|=
name|disableScoring
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|scorer
return|;
block|}
comment|/** Try to build a boolean scorer for this weight. Returns null if {@link BooleanScorer}    *  cannot be used. */
DECL|method|booleanScorer
name|BulkScorer
name|booleanScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numOptionalClauses
init|=
name|query
operator|.
name|getClauses
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numRequiredClauses
init|=
name|query
operator|.
name|getClauses
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|size
argument_list|()
operator|+
name|query
operator|.
name|getClauses
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|BulkScorer
name|positiveScorer
decl_stmt|;
if|if
condition|(
name|numRequiredClauses
operator|==
literal|0
condition|)
block|{
name|positiveScorer
operator|=
name|optionalBulkScorer
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|positiveScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// TODO: what is the right heuristic here?
specifier|final
name|long
name|costThreshold
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
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
name|positiveScorer
operator|.
name|cost
argument_list|()
operator|<
name|costThreshold
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
name|numRequiredClauses
operator|==
literal|1
operator|&&
name|numOptionalClauses
operator|==
literal|0
operator|&&
name|query
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|==
literal|0
condition|)
block|{
name|positiveScorer
operator|=
name|requiredBulkScorer
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: there are some cases where BooleanScorer
comment|// would handle conjunctions faster than
comment|// BooleanScorer2...
return|return
literal|null
return|;
block|}
if|if
condition|(
name|positiveScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|query
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
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|Scorer
name|scorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|prohibited
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|prohibited
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|positiveScorer
return|;
block|}
else|else
block|{
name|Scorer
name|prohibitedScorer
init|=
name|opt
argument_list|(
name|prohibited
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|prohibitedScorer
operator|.
name|twoPhaseIterator
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// ReqExclBulkScorer can't deal efficiently with two-phased prohibited clauses
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ReqExclBulkScorer
argument_list|(
name|positiveScorer
argument_list|,
name|prohibitedScorer
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BulkScorer
name|bulkScorer
init|=
name|booleanScorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|bulkScorer
operator|!=
literal|null
condition|)
block|{
comment|// bulk scoring is applicable, use it
return|return
name|bulkScorer
return|;
block|}
else|else
block|{
comment|// use a Scorer-based impl (BS2)
return|return
name|super
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
return|;
block|}
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
name|getMinimumNumberShouldMatch
argument_list|()
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
argument_list|)
argument_list|,
name|prohibited
argument_list|)
return|;
block|}
comment|// conjunction-disjunction mix:
comment|// we create the required and optional pieces, and then
comment|// combine the two: if minNrShouldMatch> 0, then it's a conjunction: because the
comment|// optional side must match. otherwise it's required + optional
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
argument_list|)
decl_stmt|;
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
condition|)
block|{
return|return
name|req
return|;
block|}
if|if
condition|(
name|requiredScoring
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Scores are needed but we only have a filter clause
comment|// BooleanWeight expects that calling score() is ok so we need to wrap
comment|// to prevent score() from being propagated
return|return
operator|new
name|FilterScorer
argument_list|(
name|req
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0f
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
return|return
name|req
return|;
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
return|return
name|optional
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
elseif|else
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
name|needsScores
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

