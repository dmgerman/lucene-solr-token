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
name|Map
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
name|TermAutomatonQuery
operator|.
name|EnumAndScorer
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
name|TermAutomatonQuery
operator|.
name|TermAutomatonWeight
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
name|ArrayUtil
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
name|RamUsageEstimator
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
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|RunAutomaton
import|;
end_import

begin_comment
comment|// TODO: add two-phase and needsScores support. maybe use conjunctionDISI internally?
end_comment

begin_class
DECL|class|TermAutomatonScorer
class|class
name|TermAutomatonScorer
extends|extends
name|Scorer
block|{
DECL|field|subs
specifier|private
specifier|final
name|EnumAndScorer
index|[]
name|subs
decl_stmt|;
DECL|field|subsOnDoc
specifier|private
specifier|final
name|EnumAndScorer
index|[]
name|subsOnDoc
decl_stmt|;
DECL|field|docIDQueue
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|EnumAndScorer
argument_list|>
name|docIDQueue
decl_stmt|;
DECL|field|posQueue
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|EnumAndScorer
argument_list|>
name|posQueue
decl_stmt|;
DECL|field|runAutomaton
specifier|private
specifier|final
name|RunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|idToTerm
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BytesRef
argument_list|>
name|idToTerm
decl_stmt|;
comment|// We reuse this array to check for matches starting from an initial
comment|// position; we increase posShift every time we move to a new possible
comment|// start:
DECL|field|positions
specifier|private
name|PosState
index|[]
name|positions
decl_stmt|;
DECL|field|posShift
name|int
name|posShift
decl_stmt|;
comment|// This is -1 if wildcard (null) terms were not used, else it's the id
comment|// of the wildcard term:
DECL|field|anyTermID
specifier|private
specifier|final
name|int
name|anyTermID
decl_stmt|;
DECL|field|docScorer
specifier|private
specifier|final
name|Similarity
operator|.
name|SimScorer
name|docScorer
decl_stmt|;
DECL|field|numSubsOnDoc
specifier|private
name|int
name|numSubsOnDoc
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
DECL|field|docID
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|method|TermAutomatonScorer
specifier|public
name|TermAutomatonScorer
parameter_list|(
name|TermAutomatonWeight
name|weight
parameter_list|,
name|EnumAndScorer
index|[]
name|subs
parameter_list|,
name|int
name|anyTermID
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|BytesRef
argument_list|>
name|idToTerm
parameter_list|,
name|Similarity
operator|.
name|SimScorer
name|docScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
comment|//System.out.println("  automaton:\n" + weight.automaton.toDot());
name|this
operator|.
name|runAutomaton
operator|=
operator|new
name|TermRunAutomaton
argument_list|(
name|weight
operator|.
name|automaton
argument_list|,
name|subs
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
name|this
operator|.
name|idToTerm
operator|=
name|idToTerm
expr_stmt|;
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|docIDQueue
operator|=
operator|new
name|DocIDQueue
argument_list|(
name|subs
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|posQueue
operator|=
operator|new
name|PositionQueue
argument_list|(
name|subs
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|anyTermID
operator|=
name|anyTermID
expr_stmt|;
name|this
operator|.
name|subsOnDoc
operator|=
operator|new
name|EnumAndScorer
index|[
name|subs
operator|.
name|length
index|]
expr_stmt|;
name|this
operator|.
name|positions
operator|=
operator|new
name|PosState
index|[
literal|4
index|]
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
name|this
operator|.
name|positions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|positions
index|[
name|i
index|]
operator|=
operator|new
name|PosState
argument_list|()
expr_stmt|;
block|}
name|long
name|cost
init|=
literal|0
decl_stmt|;
comment|// Init docIDQueue:
for|for
control|(
name|EnumAndScorer
name|sub
range|:
name|subs
control|)
block|{
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|cost
operator|+=
name|sub
operator|.
name|posEnum
operator|.
name|cost
argument_list|()
expr_stmt|;
name|subsOnDoc
index|[
name|numSubsOnDoc
operator|++
index|]
operator|=
name|sub
expr_stmt|;
block|}
block|}
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
comment|/** Sorts by docID so we can quickly pull out all scorers that are on    *  the same (lowest) docID. */
DECL|class|DocIDQueue
specifier|private
specifier|static
class|class
name|DocIDQueue
extends|extends
name|PriorityQueue
argument_list|<
name|EnumAndScorer
argument_list|>
block|{
DECL|method|DocIDQueue
specifier|public
name|DocIDQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|EnumAndScorer
name|a
parameter_list|,
name|EnumAndScorer
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|posEnum
operator|.
name|docID
argument_list|()
operator|<
name|b
operator|.
name|posEnum
operator|.
name|docID
argument_list|()
return|;
block|}
block|}
comment|/** Sorts by position so we can visit all scorers on one doc, by    *  position. */
DECL|class|PositionQueue
specifier|private
specifier|static
class|class
name|PositionQueue
extends|extends
name|PriorityQueue
argument_list|<
name|EnumAndScorer
argument_list|>
block|{
DECL|method|PositionQueue
specifier|public
name|PositionQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|EnumAndScorer
name|a
parameter_list|,
name|EnumAndScorer
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|pos
operator|<
name|b
operator|.
name|pos
return|;
block|}
block|}
comment|/** Pops all enums positioned on the current (minimum) doc */
DECL|method|popCurrentDoc
specifier|private
name|void
name|popCurrentDoc
parameter_list|()
block|{
assert|assert
name|numSubsOnDoc
operator|==
literal|0
assert|;
assert|assert
name|docIDQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
name|subsOnDoc
index|[
name|numSubsOnDoc
operator|++
index|]
operator|=
name|docIDQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|docID
operator|=
name|subsOnDoc
index|[
literal|0
index|]
operator|.
name|posEnum
operator|.
name|docID
argument_list|()
expr_stmt|;
while|while
condition|(
name|docIDQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|docIDQueue
operator|.
name|top
argument_list|()
operator|.
name|posEnum
operator|.
name|docID
argument_list|()
operator|==
name|docID
condition|)
block|{
name|subsOnDoc
index|[
name|numSubsOnDoc
operator|++
index|]
operator|=
name|docIDQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Pushes all previously pop'd enums back into the docIDQueue */
DECL|method|pushCurrentDoc
specifier|private
name|void
name|pushCurrentDoc
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSubsOnDoc
condition|;
name|i
operator|++
control|)
block|{
name|docIDQueue
operator|.
name|add
argument_list|(
name|subsOnDoc
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|numSubsOnDoc
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
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
name|docID
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
name|cost
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
comment|// we only need to advance docs that are positioned since all docs in the
comment|// pq are guaranteed to be beyond the current doc already
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSubsOnDoc
condition|;
name|i
operator|++
control|)
block|{
name|EnumAndScorer
name|sub
init|=
name|subsOnDoc
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|posEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|sub
operator|.
name|posLeft
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|freq
argument_list|()
operator|-
literal|1
expr_stmt|;
name|sub
operator|.
name|pos
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
block|}
name|pushCurrentDoc
argument_list|()
expr_stmt|;
return|return
name|doNext
argument_list|()
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
comment|// Both positioned docs and docs in the pq might be behind target
comment|// 1. Advance the PQ
if|if
condition|(
name|docIDQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|EnumAndScorer
name|top
init|=
name|docIDQueue
operator|.
name|top
argument_list|()
decl_stmt|;
while|while
condition|(
name|top
operator|.
name|posEnum
operator|.
name|docID
argument_list|()
operator|<
name|target
condition|)
block|{
if|if
condition|(
name|top
operator|.
name|posEnum
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|top
operator|.
name|posLeft
operator|=
name|top
operator|.
name|posEnum
operator|.
name|freq
argument_list|()
operator|-
literal|1
expr_stmt|;
name|top
operator|.
name|pos
operator|=
name|top
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
name|top
operator|=
name|docIDQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
comment|// 2. Advance subsOnDoc
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSubsOnDoc
condition|;
name|i
operator|++
control|)
block|{
name|EnumAndScorer
name|sub
init|=
name|subsOnDoc
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|posEnum
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|sub
operator|.
name|posLeft
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|freq
argument_list|()
operator|-
literal|1
expr_stmt|;
name|sub
operator|.
name|pos
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
block|}
name|pushCurrentDoc
argument_list|()
expr_stmt|;
return|return
name|doNext
argument_list|()
return|;
block|}
specifier|private
name|int
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|numSubsOnDoc
operator|==
literal|0
assert|;
assert|assert
name|docIDQueue
operator|.
name|top
argument_list|()
operator|.
name|posEnum
operator|.
name|docID
argument_list|()
operator|>
name|docID
assert|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  doNext: cycle");
name|popCurrentDoc
argument_list|()
expr_stmt|;
comment|//System.out.println("    docID=" + docID);
if|if
condition|(
name|docID
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|docID
return|;
block|}
name|countMatches
argument_list|()
expr_stmt|;
if|if
condition|(
name|freq
operator|>
literal|0
condition|)
block|{
return|return
name|docID
return|;
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
name|numSubsOnDoc
condition|;
name|i
operator|++
control|)
block|{
name|EnumAndScorer
name|sub
init|=
name|subsOnDoc
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|posEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|sub
operator|.
name|posLeft
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|freq
argument_list|()
operator|-
literal|1
expr_stmt|;
name|sub
operator|.
name|pos
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
block|}
name|pushCurrentDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|getPosition
specifier|private
name|PosState
name|getPosition
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|positions
index|[
name|pos
operator|-
name|posShift
index|]
return|;
block|}
DECL|method|shift
specifier|private
name|void
name|shift
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|limit
init|=
name|pos
operator|-
name|posShift
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|posShift
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|countMatches
specifier|private
name|void
name|countMatches
parameter_list|()
throws|throws
name|IOException
block|{
name|freq
operator|=
literal|0
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
name|numSubsOnDoc
condition|;
name|i
operator|++
control|)
block|{
name|posQueue
operator|.
name|add
argument_list|(
name|subsOnDoc
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// System.out.println("\ncountMatches: " + numSubsOnDoc + " terms in doc=" + docID + " anyTermID=" + anyTermID + " id=" + reader.document(docID).get("id"));
comment|// System.out.println("\ncountMatches: " + numSubsOnDoc + " terms in doc=" + docID + " anyTermID=" + anyTermID);
name|int
name|lastPos
init|=
operator|-
literal|1
decl_stmt|;
name|posShift
operator|=
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|posQueue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|EnumAndScorer
name|sub
init|=
name|posQueue
operator|.
name|pop
argument_list|()
decl_stmt|;
comment|// This is a graph intersection, and pos is the state this token
comment|// leaves from.  Until index stores posLength (which we could
comment|// stuff into a payload using a simple TokenFilter), this token
comment|// always transitions from state=pos to state=pos+1:
specifier|final
name|int
name|pos
init|=
name|sub
operator|.
name|pos
decl_stmt|;
if|if
condition|(
name|posShift
operator|==
operator|-
literal|1
condition|)
block|{
name|posShift
operator|=
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|+
literal|1
operator|-
name|posShift
operator|>=
name|positions
operator|.
name|length
condition|)
block|{
name|PosState
index|[]
name|newPositions
init|=
operator|new
name|PosState
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|pos
operator|+
literal|1
operator|-
name|posShift
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|positions
argument_list|,
literal|0
argument_list|,
name|newPositions
argument_list|,
literal|0
argument_list|,
name|positions
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|positions
operator|.
name|length
init|;
name|i
operator|<
name|newPositions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|newPositions
index|[
name|i
index|]
operator|=
operator|new
name|PosState
argument_list|()
expr_stmt|;
block|}
name|positions
operator|=
name|newPositions
expr_stmt|;
block|}
comment|// System.out.println("  term=" + idToTerm.get(sub.termID).utf8ToString() + " pos=" + pos + " (count=" + getPosition(pos).count + " lastPos=" + lastPos + ") posQueue.size=" + posQueue.size() + " posShift=" + posShift);
name|PosState
name|posState
decl_stmt|;
name|PosState
name|nextPosState
decl_stmt|;
comment|// Maybe advance ANY matches:
if|if
condition|(
name|lastPos
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|anyTermID
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|startLastPos
init|=
name|lastPos
decl_stmt|;
while|while
condition|(
name|lastPos
operator|<
name|pos
condition|)
block|{
name|posState
operator|=
name|getPosition
argument_list|(
name|lastPos
argument_list|)
expr_stmt|;
if|if
condition|(
name|posState
operator|.
name|count
operator|==
literal|0
operator|&&
name|lastPos
operator|>
name|startLastPos
condition|)
block|{
comment|// Petered out...
name|lastPos
operator|=
name|pos
expr_stmt|;
break|break;
block|}
comment|// System.out.println("  iter lastPos=" + lastPos + " count=" + posState.count);
name|nextPosState
operator|=
name|getPosition
argument_list|(
name|lastPos
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Advance all states from lastPos -> pos, if they had an any arc:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|posState
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|state
init|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|posState
operator|.
name|states
index|[
name|i
index|]
argument_list|,
name|anyTermID
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// System.out.println("    add pos=" + (lastPos+1) + " state=" + state);
name|nextPosState
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
name|lastPos
operator|++
expr_stmt|;
block|}
block|}
block|}
name|posState
operator|=
name|getPosition
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|nextPosState
operator|=
name|getPosition
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// If there are no pending matches at neither this position or the
comment|// next position, then it's safe to shift back to positions[0]:
if|if
condition|(
name|posState
operator|.
name|count
operator|==
literal|0
operator|&&
name|nextPosState
operator|.
name|count
operator|==
literal|0
condition|)
block|{
name|shift
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|posState
operator|=
name|getPosition
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|nextPosState
operator|=
name|getPosition
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Match current token:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|posState
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
comment|// System.out.println("    check cur state=" + posState.states[i]);
name|int
name|state
init|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|posState
operator|.
name|states
index|[
name|i
index|]
argument_list|,
name|sub
operator|.
name|termID
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// System.out.println("      --> " + state);
name|nextPosState
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
comment|// System.out.println("      *** (1)");
name|freq
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|// Also consider starting a new match from this position:
name|int
name|state
init|=
name|runAutomaton
operator|.
name|step
argument_list|(
literal|0
argument_list|,
name|sub
operator|.
name|termID
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// System.out.println("  add init state=" + state);
name|nextPosState
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
comment|// System.out.println("      *** (2)");
name|freq
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sub
operator|.
name|posLeft
operator|>
literal|0
condition|)
block|{
comment|// Put this sub back into the posQueue:
name|sub
operator|.
name|pos
operator|=
name|sub
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|sub
operator|.
name|posLeft
operator|--
expr_stmt|;
name|posQueue
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|lastPos
operator|=
name|pos
expr_stmt|;
block|}
name|int
name|limit
init|=
name|lastPos
operator|+
literal|1
operator|-
name|posShift
decl_stmt|;
comment|// reset
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
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
literal|"TermAutomatonScorer("
operator|+
name|weight
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
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
name|docID
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
comment|// TODO: we could probably do better here, e.g. look @ freqs of actual terms involved in this doc and score differently
return|return
name|docScorer
operator|.
name|score
argument_list|(
name|docID
argument_list|,
name|freq
argument_list|)
return|;
block|}
DECL|class|TermRunAutomaton
specifier|static
class|class
name|TermRunAutomaton
extends|extends
name|RunAutomaton
block|{
DECL|method|TermRunAutomaton
specifier|public
name|TermRunAutomaton
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|int
name|termCount
parameter_list|)
block|{
name|super
argument_list|(
name|a
argument_list|,
name|termCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PosState
specifier|private
specifier|static
class|class
name|PosState
block|{
comment|// Which automaton states we are in at this position
DECL|field|states
name|int
index|[]
name|states
init|=
operator|new
name|int
index|[
literal|2
index|]
decl_stmt|;
comment|// How many states
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|state
parameter_list|)
block|{
if|if
condition|(
name|states
operator|.
name|length
operator|==
name|count
condition|)
block|{
name|states
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|states
argument_list|)
expr_stmt|;
block|}
name|states
index|[
name|count
operator|++
index|]
operator|=
name|state
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

