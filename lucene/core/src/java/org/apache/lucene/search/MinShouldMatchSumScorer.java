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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ScorerPriorityQueue
operator|.
name|leftNode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ScorerPriorityQueue
operator|.
name|parentNode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ScorerPriorityQueue
operator|.
name|rightNode
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ScorerPriorityQueue
operator|.
name|ScorerWrapper
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

begin_comment
comment|/**  * A {@link Scorer} for {@link BooleanQuery} when  * {@link BooleanQuery#setMinimumNumberShouldMatch(int) minShouldMatch} is  * between 2 and the total number of clauses.  *  * This implementation keeps sub scorers in 3 different places:  *  - lead: a linked list of scorer that are positioned on the desired doc ID  *  - tail: a heap that contains at most minShouldMatch - 1 scorers that are  *    behind the desired doc ID. These scorers are ordered by cost so that we  *    can advance the least costly ones first.  *  - head: a heap that contains scorers which are beyond the desired doc ID,  *    ordered by doc ID in order to move quickly to the next candidate.  *  * Finding the next match consists of first setting the desired doc ID to the  * least entry in 'head' and then advance 'tail' until there is a match.  */
end_comment

begin_class
DECL|class|MinShouldMatchSumScorer
specifier|final
class|class
name|MinShouldMatchSumScorer
extends|extends
name|Scorer
block|{
DECL|method|cost
specifier|private
specifier|static
name|long
name|cost
parameter_list|(
name|Collection
argument_list|<
name|Scorer
argument_list|>
name|scorers
parameter_list|,
name|int
name|minShouldMatch
parameter_list|)
block|{
comment|// the idea here is the following: a boolean query c1,c2,...cn with minShouldMatch=m
comment|// could be rewritten to:
comment|// (c1 AND (c2..cn|msm=m-1)) OR (!c1 AND (c2..cn|msm=m))
comment|// if we assume that clauses come in ascending cost, then
comment|// the cost of the first part is the cost of c1 (because the cost of a conjunction is
comment|// the cost of the least costly clause)
comment|// the cost of the second part is the cost of finding m matches among the c2...cn
comment|// remaining clauses
comment|// since it is a disjunction overall, the total cost is the sum of the costs of these
comment|// two parts
comment|// If we recurse infinitely, we find out that the cost of a msm query is the sum of the
comment|// costs of the num_scorers - minShouldMatch + 1 least costly scorers
specifier|final
name|PriorityQueue
argument_list|<
name|Scorer
argument_list|>
name|pq
init|=
operator|new
name|PriorityQueue
argument_list|<
name|Scorer
argument_list|>
argument_list|(
name|scorers
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
name|Scorer
name|a
parameter_list|,
name|Scorer
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
name|Scorer
name|scorer
range|:
name|scorers
control|)
block|{
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Scorer
name|scorer
init|=
name|pq
operator|.
name|pop
argument_list|()
init|;
name|scorer
operator|!=
literal|null
condition|;
name|scorer
operator|=
name|pq
operator|.
name|pop
argument_list|()
control|)
block|{
name|cost
operator|+=
name|scorer
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
DECL|field|minShouldMatch
specifier|final
name|int
name|minShouldMatch
decl_stmt|;
DECL|field|coord
specifier|final
name|float
index|[]
name|coord
decl_stmt|;
comment|// list of scorers which 'lead' the iteration and are currently
comment|// positioned on 'doc'
DECL|field|lead
name|ScorerWrapper
name|lead
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
comment|// current doc ID of the leads
DECL|field|freq
name|int
name|freq
decl_stmt|;
comment|// number of scorers on the desired doc ID
comment|// priority queue of scorers that are too advanced compared to the current
comment|// doc. Ordered by doc ID.
DECL|field|head
specifier|final
name|ScorerPriorityQueue
name|head
decl_stmt|;
comment|// priority queue of scorers which are behind the current doc.
comment|// Ordered by cost.
DECL|field|tail
specifier|final
name|ScorerWrapper
index|[]
name|tail
decl_stmt|;
DECL|field|tailSize
name|int
name|tailSize
decl_stmt|;
DECL|field|childScorers
specifier|final
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|childScorers
decl_stmt|;
DECL|field|cost
specifier|final
name|long
name|cost
decl_stmt|;
DECL|method|MinShouldMatchSumScorer
name|MinShouldMatchSumScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Collection
argument_list|<
name|Scorer
argument_list|>
name|scorers
parameter_list|,
name|int
name|minShouldMatch
parameter_list|,
name|float
index|[]
name|coord
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
if|if
condition|(
name|minShouldMatch
operator|>
name|scorers
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minShouldMatch should be<= the number of scorers"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minShouldMatch
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minShouldMatch should be>= 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minShouldMatch
operator|=
name|minShouldMatch
expr_stmt|;
name|this
operator|.
name|coord
operator|=
name|coord
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|head
operator|=
operator|new
name|ScorerPriorityQueue
argument_list|(
name|scorers
operator|.
name|size
argument_list|()
operator|-
name|minShouldMatch
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// there can be at most minShouldMatch - 1 scorers beyond the current position
comment|// otherwise we might be skipping over matching documents
name|tail
operator|=
operator|new
name|ScorerWrapper
index|[
name|minShouldMatch
operator|-
literal|1
index|]
expr_stmt|;
for|for
control|(
name|Scorer
name|scorer
range|:
name|scorers
control|)
block|{
name|addLead
argument_list|(
operator|new
name|ScorerWrapper
argument_list|(
name|scorer
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ChildScorer
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Scorer
name|scorer
range|:
name|scorers
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|scorer
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|childScorers
operator|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|children
argument_list|)
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|cost
argument_list|(
name|scorers
argument_list|,
name|minShouldMatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
specifier|final
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|childScorers
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We are moving to the next doc ID, so scorers in 'lead' need to go in
comment|// 'tail'. If there is not enough space in 'tail', then we take the least
comment|// costly scorers and advance them.
for|for
control|(
name|ScorerWrapper
name|s
init|=
name|lead
init|;
name|s
operator|!=
literal|null
condition|;
name|s
operator|=
name|s
operator|.
name|next
control|)
block|{
specifier|final
name|ScorerWrapper
name|evicted
init|=
name|insertTailWithOverFlow
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|evicted
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|evicted
operator|.
name|doc
operator|==
name|doc
condition|)
block|{
name|evicted
operator|.
name|doc
operator|=
name|evicted
operator|.
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|evicted
operator|.
name|doc
operator|=
name|evicted
operator|.
name|scorer
operator|.
name|advance
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|head
operator|.
name|add
argument_list|(
name|evicted
argument_list|)
expr_stmt|;
block|}
block|}
name|setDocAndFreq
argument_list|()
expr_stmt|;
return|return
name|doNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Same logic as in nextDoc
for|for
control|(
name|ScorerWrapper
name|s
init|=
name|lead
init|;
name|s
operator|!=
literal|null
condition|;
name|s
operator|=
name|s
operator|.
name|next
control|)
block|{
specifier|final
name|ScorerWrapper
name|evicted
init|=
name|insertTailWithOverFlow
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|evicted
operator|!=
literal|null
condition|)
block|{
name|evicted
operator|.
name|doc
operator|=
name|evicted
operator|.
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|head
operator|.
name|add
argument_list|(
name|evicted
argument_list|)
expr_stmt|;
block|}
block|}
comment|// But this time there might also be scorers in 'head' behind the desired
comment|// target so we need to do the same thing that we did on 'lead' on 'head'
name|ScorerWrapper
name|headTop
init|=
name|head
operator|.
name|top
argument_list|()
decl_stmt|;
while|while
condition|(
name|headTop
operator|.
name|doc
operator|<
name|target
condition|)
block|{
specifier|final
name|ScorerWrapper
name|evicted
init|=
name|insertTailWithOverFlow
argument_list|(
name|headTop
argument_list|)
decl_stmt|;
comment|// We know that the tail is full since it contains at most
comment|// minShouldMatch - 1 entries and we just moved at least minShouldMatch
comment|// entries to it, so evicted is not null
name|evicted
operator|.
name|doc
operator|=
name|evicted
operator|.
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|headTop
operator|=
name|head
operator|.
name|updateTop
argument_list|(
name|evicted
argument_list|)
expr_stmt|;
block|}
name|setDocAndFreq
argument_list|()
expr_stmt|;
return|return
name|doNext
argument_list|()
return|;
block|}
DECL|method|addLead
specifier|private
name|void
name|addLead
parameter_list|(
name|ScorerWrapper
name|lead
parameter_list|)
block|{
name|lead
operator|.
name|next
operator|=
name|this
operator|.
name|lead
expr_stmt|;
name|this
operator|.
name|lead
operator|=
name|lead
expr_stmt|;
name|freq
operator|+=
literal|1
expr_stmt|;
block|}
DECL|method|pushBackLeads
specifier|private
name|void
name|pushBackLeads
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|ScorerWrapper
name|s
init|=
name|lead
init|;
name|s
operator|!=
literal|null
condition|;
name|s
operator|=
name|s
operator|.
name|next
control|)
block|{
name|addTail
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|advanceTail
specifier|private
name|void
name|advanceTail
parameter_list|(
name|ScorerWrapper
name|top
parameter_list|)
throws|throws
name|IOException
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|doc
operator|==
name|doc
condition|)
block|{
name|addLead
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|head
operator|.
name|add
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|advanceTail
specifier|private
name|void
name|advanceTail
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ScorerWrapper
name|top
init|=
name|popTail
argument_list|()
decl_stmt|;
name|advanceTail
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
comment|/** Reinitializes head, freq and doc from 'head' */
DECL|method|setDocAndFreq
specifier|private
name|void
name|setDocAndFreq
parameter_list|()
block|{
assert|assert
name|head
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
comment|// The top of `head` defines the next potential match
comment|// pop all documents which are on this doc
name|lead
operator|=
name|head
operator|.
name|pop
argument_list|()
expr_stmt|;
name|lead
operator|.
name|next
operator|=
literal|null
expr_stmt|;
name|freq
operator|=
literal|1
expr_stmt|;
name|doc
operator|=
name|lead
operator|.
name|doc
expr_stmt|;
while|while
condition|(
name|head
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|head
operator|.
name|top
argument_list|()
operator|.
name|doc
operator|==
name|doc
condition|)
block|{
name|addLead
argument_list|(
name|head
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Advance tail to the lead until there is a match. */
DECL|method|doNext
specifier|private
name|int
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|freq
operator|<
name|minShouldMatch
condition|)
block|{
assert|assert
name|freq
operator|>
literal|0
assert|;
if|if
condition|(
name|freq
operator|+
name|tailSize
operator|>=
name|minShouldMatch
condition|)
block|{
comment|// a match on doc is still possible, try to
comment|// advance scorers from the tail
name|advanceTail
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// no match on doc is possible anymore, move to the next potential match
name|pushBackLeads
argument_list|()
expr_stmt|;
name|setDocAndFreq
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
comment|/** Advance all entries from the tail to know about all matches on the    *  current doc. */
DECL|method|updateFreq
specifier|private
name|void
name|updateFreq
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|freq
operator|>=
name|minShouldMatch
assert|;
comment|// we return the next doc when there are minShouldMatch matching clauses
comment|// but some of the clauses in 'tail' might match as well
comment|// in general we want to advance least-costly clauses first in order to
comment|// skip over non-matching documents as fast as possible. However here,
comment|// we are advancing everything anyway so iterating over clauses in
comment|// (roughly) cost-descending order might help avoid some permutations in
comment|// the head heap
for|for
control|(
name|int
name|i
init|=
name|tailSize
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|advanceTail
argument_list|(
name|tail
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|tailSize
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we need to know about all matches
name|updateFreq
argument_list|()
expr_stmt|;
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we need to know about all matches
name|updateFreq
argument_list|()
expr_stmt|;
name|double
name|score
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScorerWrapper
name|s
init|=
name|lead
init|;
name|s
operator|!=
literal|null
condition|;
name|s
operator|=
name|s
operator|.
name|next
control|)
block|{
name|score
operator|+=
name|s
operator|.
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
return|return
name|coord
index|[
name|freq
index|]
operator|*
operator|(
name|float
operator|)
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
assert|assert
name|doc
operator|==
name|lead
operator|.
name|doc
assert|;
return|return
name|doc
return|;
block|}
comment|/** Insert an entry in 'tail' and evict the least-costly scorer if full. */
DECL|method|insertTailWithOverFlow
specifier|private
name|ScorerWrapper
name|insertTailWithOverFlow
parameter_list|(
name|ScorerWrapper
name|s
parameter_list|)
block|{
if|if
condition|(
name|tailSize
operator|<
name|tail
operator|.
name|length
condition|)
block|{
name|addTail
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|tail
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
specifier|final
name|ScorerWrapper
name|top
init|=
name|tail
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|top
operator|.
name|cost
operator|<
name|s
operator|.
name|cost
condition|)
block|{
name|tail
index|[
literal|0
index|]
operator|=
name|s
expr_stmt|;
name|downHeapCost
argument_list|(
name|tail
argument_list|,
name|tailSize
argument_list|)
expr_stmt|;
return|return
name|top
return|;
block|}
block|}
return|return
name|s
return|;
block|}
comment|/** Add an entry to 'tail'. Fails if over capacity. */
DECL|method|addTail
specifier|private
name|void
name|addTail
parameter_list|(
name|ScorerWrapper
name|s
parameter_list|)
block|{
name|tail
index|[
name|tailSize
index|]
operator|=
name|s
expr_stmt|;
name|upHeapCost
argument_list|(
name|tail
argument_list|,
name|tailSize
argument_list|)
expr_stmt|;
name|tailSize
operator|+=
literal|1
expr_stmt|;
block|}
comment|/** Pop the least-costly scorer from 'tail'. */
DECL|method|popTail
specifier|private
name|ScorerWrapper
name|popTail
parameter_list|()
block|{
assert|assert
name|tailSize
operator|>
literal|0
assert|;
specifier|final
name|ScorerWrapper
name|result
init|=
name|tail
index|[
literal|0
index|]
decl_stmt|;
name|tail
index|[
literal|0
index|]
operator|=
name|tail
index|[
operator|--
name|tailSize
index|]
expr_stmt|;
name|downHeapCost
argument_list|(
name|tail
argument_list|,
name|tailSize
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Heap helpers */
DECL|method|upHeapCost
specifier|private
specifier|static
name|void
name|upHeapCost
parameter_list|(
name|ScorerWrapper
index|[]
name|heap
parameter_list|,
name|int
name|i
parameter_list|)
block|{
specifier|final
name|ScorerWrapper
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|long
name|nodeCost
init|=
name|node
operator|.
name|cost
decl_stmt|;
name|int
name|j
init|=
name|parentNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
while|while
condition|(
name|j
operator|>=
literal|0
operator|&&
name|nodeCost
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|cost
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|parentNode
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
DECL|method|downHeapCost
specifier|private
specifier|static
name|void
name|downHeapCost
parameter_list|(
name|ScorerWrapper
index|[]
name|heap
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|ScorerWrapper
name|node
init|=
name|heap
index|[
literal|0
index|]
decl_stmt|;
name|int
name|j
init|=
name|leftNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|j
operator|<
name|size
condition|)
block|{
name|int
name|k
init|=
name|rightNode
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|<
name|size
operator|&&
name|heap
index|[
name|k
index|]
operator|.
name|cost
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|cost
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
if|if
condition|(
name|heap
index|[
name|j
index|]
operator|.
name|cost
operator|<
name|node
operator|.
name|cost
condition|)
block|{
do|do
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|leftNode
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|k
operator|=
name|rightNode
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|<
name|size
operator|&&
name|heap
index|[
name|k
index|]
operator|.
name|cost
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|cost
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
block|}
do|while
condition|(
name|j
operator|<
name|size
operator|&&
name|heap
index|[
name|j
index|]
operator|.
name|cost
operator|<
name|node
operator|.
name|cost
condition|)
do|;
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

