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
name|Random
import|;
end_import

begin_comment
comment|/** Wraps a Scorer with additional checks */
end_comment

begin_class
DECL|class|AssertingScorer
specifier|public
class|class
name|AssertingScorer
extends|extends
name|Scorer
block|{
DECL|enum|IteratorState
DECL|enum constant|START
DECL|enum constant|APPROXIMATING
DECL|enum constant|ITERATING
DECL|enum constant|FINISHED
specifier|static
enum|enum
name|IteratorState
block|{
name|START
block|,
name|APPROXIMATING
block|,
name|ITERATING
block|,
name|FINISHED
block|}
empty_stmt|;
DECL|method|wrap
specifier|public
specifier|static
name|Scorer
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|other
parameter_list|,
name|boolean
name|canScore
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|AssertingScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|,
name|canScore
argument_list|)
return|;
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|Scorer
name|in
decl_stmt|;
DECL|field|needsScores
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|state
name|IteratorState
name|state
init|=
name|IteratorState
operator|.
name|START
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|method|AssertingScorer
specifier|private
name|AssertingScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|in
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|in
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
DECL|method|getIn
specifier|public
name|Scorer
name|getIn
parameter_list|()
block|{
return|return
name|in
return|;
block|}
DECL|method|iterating
name|boolean
name|iterating
parameter_list|()
block|{
comment|// we cannot assert that state == ITERATING because of CachingScorerWrapper
switch|switch
condition|(
name|docID
argument_list|()
condition|)
block|{
case|case
operator|-
literal|1
case|:
case|case
name|NO_MORE_DOCS
case|:
return|return
literal|false
return|;
default|default:
return|return
name|state
operator|!=
name|IteratorState
operator|.
name|APPROXIMATING
return|;
comment|// Matches must be confirmed before calling freq() or score()
block|}
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
assert|assert
name|needsScores
assert|;
assert|assert
name|iterating
argument_list|()
assert|;
specifier|final
name|float
name|score
init|=
name|in
operator|.
name|score
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
operator|:
literal|"NaN score for in="
operator|+
name|in
assert|;
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
comment|// We cannot hide that we hold a single child, else
comment|// collectors (e.g. ToParentBlockJoinCollector) that
comment|// need to walk the scorer tree will miss/skip the
comment|// Scorer we wrap:
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|in
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
return|;
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
assert|assert
name|needsScores
assert|;
assert|assert
name|iterating
argument_list|()
assert|;
return|return
name|in
operator|.
name|freq
argument_list|()
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
return|return
name|in
operator|.
name|docID
argument_list|()
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
assert|assert
name|state
operator|!=
name|IteratorState
operator|.
name|FINISHED
operator|:
literal|"nextDoc() called after NO_MORE_DOCS"
assert|;
name|int
name|nextDoc
init|=
name|in
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
assert|assert
name|nextDoc
operator|>
name|doc
operator|:
literal|"backwards nextDoc from "
operator|+
name|doc
operator|+
literal|" to "
operator|+
name|nextDoc
operator|+
literal|" "
operator|+
name|in
assert|;
if|if
condition|(
name|nextDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|state
operator|=
name|IteratorState
operator|.
name|FINISHED
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|IteratorState
operator|.
name|ITERATING
expr_stmt|;
block|}
assert|assert
name|in
operator|.
name|docID
argument_list|()
operator|==
name|nextDoc
assert|;
return|return
name|doc
operator|=
name|nextDoc
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
assert|assert
name|state
operator|!=
name|IteratorState
operator|.
name|FINISHED
operator|:
literal|"advance() called after NO_MORE_DOCS"
assert|;
assert|assert
name|target
operator|>
name|doc
operator|:
literal|"target must be> docID(), got "
operator|+
name|target
operator|+
literal|"<= "
operator|+
name|doc
assert|;
name|int
name|advanced
init|=
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
assert|assert
name|advanced
operator|>=
name|target
operator|:
literal|"backwards advance from: "
operator|+
name|target
operator|+
literal|" to: "
operator|+
name|advanced
assert|;
if|if
condition|(
name|advanced
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|state
operator|=
name|IteratorState
operator|.
name|FINISHED
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|IteratorState
operator|.
name|ITERATING
expr_stmt|;
block|}
assert|assert
name|in
operator|.
name|docID
argument_list|()
operator|==
name|advanced
assert|;
return|return
name|doc
operator|=
name|advanced
return|;
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
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AssertingScorer("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
specifier|final
name|TwoPhaseIterator
name|in
init|=
name|this
operator|.
name|in
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|inApproximation
init|=
name|in
operator|.
name|approximation
argument_list|()
decl_stmt|;
assert|assert
name|inApproximation
operator|.
name|docID
argument_list|()
operator|==
name|doc
assert|;
specifier|final
name|DocIdSetIterator
name|assertingApproximation
init|=
operator|new
name|DocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|inApproximation
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
name|IteratorState
operator|.
name|FINISHED
operator|:
literal|"advance() called after NO_MORE_DOCS"
assert|;
specifier|final
name|int
name|nextDoc
init|=
name|inApproximation
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
assert|assert
name|nextDoc
operator|>
name|doc
operator|:
literal|"backwards advance from: "
operator|+
name|doc
operator|+
literal|" to: "
operator|+
name|nextDoc
assert|;
if|if
condition|(
name|nextDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|state
operator|=
name|IteratorState
operator|.
name|FINISHED
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|IteratorState
operator|.
name|APPROXIMATING
expr_stmt|;
block|}
assert|assert
name|inApproximation
operator|.
name|docID
argument_list|()
operator|==
name|nextDoc
assert|;
return|return
name|doc
operator|=
name|nextDoc
return|;
block|}
annotation|@
name|Override
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
assert|assert
name|state
operator|!=
name|IteratorState
operator|.
name|FINISHED
operator|:
literal|"advance() called after NO_MORE_DOCS"
assert|;
assert|assert
name|target
operator|>
name|doc
operator|:
literal|"target must be> docID(), got "
operator|+
name|target
operator|+
literal|"<= "
operator|+
name|doc
assert|;
specifier|final
name|int
name|advanced
init|=
name|inApproximation
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
assert|assert
name|advanced
operator|>=
name|target
operator|:
literal|"backwards advance from: "
operator|+
name|target
operator|+
literal|" to: "
operator|+
name|advanced
assert|;
if|if
condition|(
name|advanced
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|state
operator|=
name|IteratorState
operator|.
name|FINISHED
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|IteratorState
operator|.
name|APPROXIMATING
expr_stmt|;
block|}
assert|assert
name|inApproximation
operator|.
name|docID
argument_list|()
operator|==
name|advanced
assert|;
return|return
name|doc
operator|=
name|advanced
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
name|inApproximation
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|assertingApproximation
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|==
name|IteratorState
operator|.
name|APPROXIMATING
assert|;
specifier|final
name|boolean
name|matches
init|=
name|in
operator|.
name|matches
argument_list|()
decl_stmt|;
if|if
condition|(
name|matches
condition|)
block|{
assert|assert
name|AssertingScorer
operator|.
name|this
operator|.
name|in
operator|.
name|docID
argument_list|()
operator|==
name|inApproximation
operator|.
name|docID
argument_list|()
operator|:
literal|"Approximation and scorer don't advance synchronously"
assert|;
name|doc
operator|=
name|inApproximation
operator|.
name|docID
argument_list|()
expr_stmt|;
name|state
operator|=
name|IteratorState
operator|.
name|ITERATING
expr_stmt|;
block|}
return|return
name|matches
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
name|float
name|matchCost
init|=
name|in
operator|.
name|matchCost
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|matchCost
argument_list|)
assert|;
assert|assert
name|matchCost
operator|>=
literal|0
assert|;
return|return
name|matchCost
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AssertingScorer@asTwoPhaseIterator("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

