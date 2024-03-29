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
name|Collection
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
name|OptionalLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_class
DECL|class|Boolean2ScorerSupplier
specifier|final
class|class
name|Boolean2ScorerSupplier
extends|extends
name|ScorerSupplier
block|{
DECL|field|weight
specifier|private
specifier|final
name|BooleanWeight
name|weight
decl_stmt|;
DECL|field|subs
specifier|private
specifier|final
name|Map
argument_list|<
name|BooleanClause
operator|.
name|Occur
argument_list|,
name|Collection
argument_list|<
name|ScorerSupplier
argument_list|>
argument_list|>
name|subs
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|minShouldMatch
specifier|private
specifier|final
name|int
name|minShouldMatch
decl_stmt|;
DECL|field|cost
specifier|private
name|long
name|cost
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|Boolean2ScorerSupplier
name|Boolean2ScorerSupplier
parameter_list|(
name|BooleanWeight
name|weight
parameter_list|,
name|Map
argument_list|<
name|Occur
argument_list|,
name|Collection
argument_list|<
name|ScorerSupplier
argument_list|>
argument_list|>
name|subs
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|int
name|minShouldMatch
parameter_list|)
block|{
if|if
condition|(
name|minShouldMatch
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minShouldMatch must be positive, but got: "
operator|+
name|minShouldMatch
argument_list|)
throw|;
block|}
if|if
condition|(
name|minShouldMatch
operator|!=
literal|0
operator|&&
name|minShouldMatch
operator|>=
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minShouldMatch must be strictly less than the number of SHOULD clauses"
argument_list|)
throw|;
block|}
if|if
condition|(
name|needsScores
operator|==
literal|false
operator|&&
name|minShouldMatch
operator|==
literal|0
operator|&&
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|size
argument_list|()
operator|+
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot pass purely optional clauses if scores are not needed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|size
argument_list|()
operator|+
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|size
argument_list|()
operator|+
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"There should be at least one positive clause"
argument_list|)
throw|;
block|}
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|this
operator|.
name|minShouldMatch
operator|=
name|minShouldMatch
expr_stmt|;
block|}
DECL|method|computeCost
specifier|private
name|long
name|computeCost
parameter_list|()
block|{
name|OptionalLong
name|minRequiredCost
init|=
name|Stream
operator|.
name|concat
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|stream
argument_list|()
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|stream
argument_list|()
argument_list|)
operator|.
name|mapToLong
argument_list|(
name|ScorerSupplier
operator|::
name|cost
argument_list|)
operator|.
name|min
argument_list|()
decl_stmt|;
if|if
condition|(
name|minRequiredCost
operator|.
name|isPresent
argument_list|()
operator|&&
name|minShouldMatch
operator|==
literal|0
condition|)
block|{
return|return
name|minRequiredCost
operator|.
name|getAsLong
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|Collection
argument_list|<
name|ScorerSupplier
argument_list|>
name|optionalScorers
init|=
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
decl_stmt|;
specifier|final
name|long
name|shouldCost
init|=
name|MinShouldMatchSumScorer
operator|.
name|cost
argument_list|(
name|optionalScorers
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|ScorerSupplier
operator|::
name|cost
argument_list|)
argument_list|,
name|optionalScorers
operator|.
name|size
argument_list|()
argument_list|,
name|minShouldMatch
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|minRequiredCost
operator|.
name|orElse
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|shouldCost
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
if|if
condition|(
name|cost
operator|==
operator|-
literal|1
condition|)
block|{
name|cost
operator|=
name|computeCost
argument_list|()
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Scorer
name|get
parameter_list|(
name|boolean
name|randomAccess
parameter_list|)
throws|throws
name|IOException
block|{
comment|// three cases: conjunction, disjunction, or mix
comment|// pure conjunction
if|if
condition|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
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
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|,
name|randomAccess
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
return|;
block|}
comment|// pure disjunction
if|if
condition|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|isEmpty
argument_list|()
operator|&&
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
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
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|,
name|minShouldMatch
argument_list|,
name|needsScores
argument_list|,
name|randomAccess
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
return|;
block|}
comment|// conjunction-disjunction mix:
comment|// we create the required and optional pieces, and then
comment|// combine the two: if minNrShouldMatch> 0, then it's a conjunction: because the
comment|// optional side must match. otherwise it's required + optional
if|if
condition|(
name|minShouldMatch
operator|>
literal|0
condition|)
block|{
name|boolean
name|reqRandomAccess
init|=
literal|true
decl_stmt|;
name|boolean
name|msmRandomAccess
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|randomAccess
operator|==
literal|false
condition|)
block|{
comment|// We need to figure out whether the MUST/FILTER or the SHOULD clauses would lead the iteration
specifier|final
name|long
name|reqCost
init|=
name|Stream
operator|.
name|concat
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|stream
argument_list|()
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|stream
argument_list|()
argument_list|)
operator|.
name|mapToLong
argument_list|(
name|ScorerSupplier
operator|::
name|cost
argument_list|)
operator|.
name|min
argument_list|()
operator|.
name|getAsLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|msmCost
init|=
name|MinShouldMatchSumScorer
operator|.
name|cost
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|ScorerSupplier
operator|::
name|cost
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|minShouldMatch
argument_list|)
decl_stmt|;
name|reqRandomAccess
operator|=
name|reqCost
operator|>
name|msmCost
expr_stmt|;
name|msmRandomAccess
operator|=
name|msmCost
operator|>
name|reqCost
expr_stmt|;
block|}
name|Scorer
name|req
init|=
name|excl
argument_list|(
name|req
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|,
name|reqRandomAccess
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
decl_stmt|;
name|Scorer
name|opt
init|=
name|opt
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|,
name|minShouldMatch
argument_list|,
name|needsScores
argument_list|,
name|msmRandomAccess
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|weight
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
assert|assert
name|needsScores
assert|;
return|return
operator|new
name|ReqOptSumScorer
argument_list|(
name|excl
argument_list|(
name|req
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|,
name|randomAccess
argument_list|)
argument_list|,
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
argument_list|,
name|opt
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|,
name|minShouldMatch
argument_list|,
name|needsScores
argument_list|,
literal|true
argument_list|)
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
name|Collection
argument_list|<
name|ScorerSupplier
argument_list|>
name|requiredNoScoring
parameter_list|,
name|Collection
argument_list|<
name|ScorerSupplier
argument_list|>
name|requiredScoring
parameter_list|,
name|boolean
name|randomAccess
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|requiredNoScoring
operator|.
name|size
argument_list|()
operator|+
name|requiredScoring
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
operator|(
name|requiredNoScoring
operator|.
name|isEmpty
argument_list|()
condition|?
name|requiredScoring
else|:
name|requiredNoScoring
operator|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
name|randomAccess
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
name|long
name|minCost
init|=
name|Math
operator|.
name|min
argument_list|(
name|requiredNoScoring
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|ScorerSupplier
operator|::
name|cost
argument_list|)
operator|.
name|min
argument_list|()
operator|.
name|orElse
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|requiredScoring
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|ScorerSupplier
operator|::
name|cost
argument_list|)
operator|.
name|min
argument_list|()
operator|.
name|orElse
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|requiredScorers
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
name|scoringScorers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScorerSupplier
name|s
range|:
name|requiredNoScoring
control|)
block|{
name|requiredScorers
operator|.
name|add
argument_list|(
name|s
operator|.
name|get
argument_list|(
name|randomAccess
operator|||
name|s
operator|.
name|cost
argument_list|()
operator|>
name|minCost
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ScorerSupplier
name|s
range|:
name|requiredScoring
control|)
block|{
name|Scorer
name|scorer
init|=
name|s
operator|.
name|get
argument_list|(
name|randomAccess
operator|||
name|s
operator|.
name|cost
argument_list|()
operator|>
name|minCost
argument_list|)
decl_stmt|;
name|requiredScorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|scoringScorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|weight
argument_list|,
name|requiredScorers
argument_list|,
name|scoringScorers
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
name|Collection
argument_list|<
name|ScorerSupplier
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
else|else
block|{
return|return
operator|new
name|ReqExclScorer
argument_list|(
name|main
argument_list|,
name|opt
argument_list|(
name|prohibited
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
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
name|Collection
argument_list|<
name|ScorerSupplier
argument_list|>
name|optional
parameter_list|,
name|int
name|minShouldMatch
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|boolean
name|randomAccess
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
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
name|randomAccess
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
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|optionalScorers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|PriorityQueue
argument_list|<
name|ScorerSupplier
argument_list|>
name|pq
init|=
operator|new
name|PriorityQueue
argument_list|<
name|ScorerSupplier
argument_list|>
argument_list|(
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|size
argument_list|()
operator|-
name|minShouldMatch
operator|+
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|ScorerSupplier
name|a
parameter_list|,
name|ScorerSupplier
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|cost
argument_list|()
operator|>
name|b
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|ScorerSupplier
name|scorer
range|:
name|subs
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
control|)
block|{
name|ScorerSupplier
name|overflow
init|=
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|scorer
argument_list|)
decl_stmt|;
if|if
condition|(
name|overflow
operator|!=
literal|null
condition|)
block|{
name|optionalScorers
operator|.
name|add
argument_list|(
name|overflow
operator|.
name|get
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|ScorerSupplier
name|scorer
range|:
name|pq
control|)
block|{
name|optionalScorers
operator|.
name|add
argument_list|(
name|scorer
operator|.
name|get
argument_list|(
name|randomAccess
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MinShouldMatchSumScorer
argument_list|(
name|weight
argument_list|,
name|optionalScorers
argument_list|,
name|minShouldMatch
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|optionalScorers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScorerSupplier
name|scorer
range|:
name|optional
control|)
block|{
name|optionalScorers
operator|.
name|add
argument_list|(
name|scorer
operator|.
name|get
argument_list|(
name|randomAccess
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DisjunctionSumScorer
argument_list|(
name|weight
argument_list|,
name|optionalScorers
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

