begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/* Derived from org.apache.lucene.util.PriorityQueue of March 2005 */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
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
name|Scorer
import|;
end_import

begin_comment
comment|/** A ScorerDocQueue maintains a partial ordering of its Scorers such that the   least Scorer can always be found in constant time.  Put()'s and pop()'s   require log(size) time. The ordering is by Scorer.doc().  */
end_comment

begin_class
DECL|class|ScorerDocQueue
specifier|public
class|class
name|ScorerDocQueue
block|{
comment|// later: SpansQueue for spans with doc and term positions
DECL|field|heap
specifier|private
specifier|final
name|HeapedScorerDoc
index|[]
name|heap
decl_stmt|;
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|class|HeapedScorerDoc
specifier|private
class|class
name|HeapedScorerDoc
block|{
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|method|HeapedScorerDoc
name|HeapedScorerDoc
parameter_list|(
name|Scorer
name|s
parameter_list|)
block|{
name|this
argument_list|(
name|s
argument_list|,
name|s
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|HeapedScorerDoc
name|HeapedScorerDoc
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
DECL|method|adjust
name|void
name|adjust
parameter_list|()
block|{
name|doc
operator|=
name|scorer
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|topHSD
specifier|private
name|HeapedScorerDoc
name|topHSD
decl_stmt|;
comment|// same as heap[1], only for speed
comment|/** Create a ScorerDocQueue with a maximum size. */
DECL|method|ScorerDocQueue
specifier|public
name|ScorerDocQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
comment|// assert maxSize>= 0;
name|size
operator|=
literal|0
expr_stmt|;
name|int
name|heapSize
init|=
name|maxSize
operator|+
literal|1
decl_stmt|;
name|heap
operator|=
operator|new
name|HeapedScorerDoc
index|[
name|heapSize
index|]
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
name|topHSD
operator|=
name|heap
index|[
literal|1
index|]
expr_stmt|;
comment|// initially null
block|}
comment|/**    * Adds a Scorer to a ScorerDocQueue in log(size) time.    * If one tries to add more Scorers than maxSize    * a RuntimeException (ArrayIndexOutOfBound) is thrown.    */
DECL|method|put
specifier|public
specifier|final
name|void
name|put
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|size
operator|++
expr_stmt|;
name|heap
index|[
name|size
index|]
operator|=
operator|new
name|HeapedScorerDoc
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|upHeap
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a Scorer to the ScorerDocQueue in log(size) time if either    * the ScorerDocQueue is not full, or not lessThan(scorer, top()).    * @param scorer    * @return true if scorer is added, false otherwise.    */
DECL|method|insert
specifier|public
name|boolean
name|insert
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<
name|maxSize
condition|)
block|{
name|put
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|int
name|docNr
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|size
operator|>
literal|0
operator|)
operator|&&
operator|(
operator|!
operator|(
name|docNr
operator|<
name|topHSD
operator|.
name|doc
operator|)
operator|)
condition|)
block|{
comment|// heap[1] is top()
name|heap
index|[
literal|1
index|]
operator|=
operator|new
name|HeapedScorerDoc
argument_list|(
name|scorer
argument_list|,
name|docNr
argument_list|)
expr_stmt|;
name|downHeap
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/** Returns the least Scorer of the ScorerDocQueue in constant time.    * Should not be used when the queue is empty.    */
DECL|method|top
specifier|public
specifier|final
name|Scorer
name|top
parameter_list|()
block|{
comment|// assert size> 0;
return|return
name|topHSD
operator|.
name|scorer
return|;
block|}
comment|/** Returns document number of the least Scorer of the ScorerDocQueue    * in constant time.    * Should not be used when the queue is empty.    */
DECL|method|topDoc
specifier|public
specifier|final
name|int
name|topDoc
parameter_list|()
block|{
comment|// assert size> 0;
return|return
name|topHSD
operator|.
name|doc
return|;
block|}
DECL|method|topScore
specifier|public
specifier|final
name|float
name|topScore
parameter_list|()
throws|throws
name|IOException
block|{
comment|// assert size> 0;
return|return
name|topHSD
operator|.
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
DECL|method|topNextAndAdjustElsePop
specifier|public
specifier|final
name|boolean
name|topNextAndAdjustElsePop
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|checkAdjustElsePop
argument_list|(
name|topHSD
operator|.
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
return|;
block|}
DECL|method|topSkipToAndAdjustElsePop
specifier|public
specifier|final
name|boolean
name|topSkipToAndAdjustElsePop
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|checkAdjustElsePop
argument_list|(
name|topHSD
operator|.
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
return|;
block|}
DECL|method|checkAdjustElsePop
specifier|private
name|boolean
name|checkAdjustElsePop
parameter_list|(
name|boolean
name|cond
parameter_list|)
block|{
if|if
condition|(
name|cond
condition|)
block|{
comment|// see also adjustTop
name|topHSD
operator|.
name|doc
operator|=
name|topHSD
operator|.
name|scorer
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// see also popNoResult
name|heap
index|[
literal|1
index|]
operator|=
name|heap
index|[
name|size
index|]
expr_stmt|;
comment|// move last to first
name|heap
index|[
name|size
index|]
operator|=
literal|null
expr_stmt|;
name|size
operator|--
expr_stmt|;
block|}
name|downHeap
argument_list|()
expr_stmt|;
return|return
name|cond
return|;
block|}
comment|/** Removes and returns the least scorer of the ScorerDocQueue in log(size)    * time.    * Should not be used when the queue is empty.    */
DECL|method|pop
specifier|public
specifier|final
name|Scorer
name|pop
parameter_list|()
block|{
comment|// assert size> 0;
name|Scorer
name|result
init|=
name|topHSD
operator|.
name|scorer
decl_stmt|;
name|popNoResult
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Removes the least scorer of the ScorerDocQueue in log(size) time.    * Should not be used when the queue is empty.    */
DECL|method|popNoResult
specifier|private
specifier|final
name|void
name|popNoResult
parameter_list|()
block|{
name|heap
index|[
literal|1
index|]
operator|=
name|heap
index|[
name|size
index|]
expr_stmt|;
comment|// move last to first
name|heap
index|[
name|size
index|]
operator|=
literal|null
expr_stmt|;
name|size
operator|--
expr_stmt|;
name|downHeap
argument_list|()
expr_stmt|;
comment|// adjust heap
block|}
comment|/** Should be called when the scorer at top changes doc() value.    * Still log(n) worst case, but it's at least twice as fast to<pre>    *  { pq.top().change(); pq.adjustTop(); }    *</pre> instead of<pre>    *  { o = pq.pop(); o.change(); pq.push(o); }    *</pre>    */
DECL|method|adjustTop
specifier|public
specifier|final
name|void
name|adjustTop
parameter_list|()
block|{
comment|// assert size> 0;
name|topHSD
operator|.
name|adjust
argument_list|()
expr_stmt|;
name|downHeap
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the number of scorers currently stored in the ScorerDocQueue. */
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/** Removes all entries from the ScorerDocQueue. */
DECL|method|clear
specifier|public
specifier|final
name|void
name|clear
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
operator|<=
name|size
condition|;
name|i
operator|++
control|)
block|{
name|heap
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|size
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|upHeap
specifier|private
specifier|final
name|void
name|upHeap
parameter_list|()
block|{
name|int
name|i
init|=
name|size
decl_stmt|;
name|HeapedScorerDoc
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save bottom node
name|int
name|j
init|=
name|i
operator|>>>
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|j
operator|>
literal|0
operator|)
operator|&&
operator|(
name|node
operator|.
name|doc
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|doc
operator|)
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
comment|// shift parents down
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|j
operator|>>>
literal|1
expr_stmt|;
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
name|topHSD
operator|=
name|heap
index|[
literal|1
index|]
expr_stmt|;
block|}
DECL|method|downHeap
specifier|private
specifier|final
name|void
name|downHeap
parameter_list|()
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
name|HeapedScorerDoc
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save top node
name|int
name|j
init|=
name|i
operator|<<
literal|1
decl_stmt|;
comment|// find smaller child
name|int
name|k
init|=
name|j
operator|+
literal|1
decl_stmt|;
if|if
condition|(
operator|(
name|k
operator|<=
name|size
operator|)
operator|&&
operator|(
name|heap
index|[
name|k
index|]
operator|.
name|doc
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|doc
operator|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|j
operator|<=
name|size
operator|)
operator|&&
operator|(
name|heap
index|[
name|j
index|]
operator|.
name|doc
operator|<
name|node
operator|.
name|doc
operator|)
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
comment|// shift up child
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|i
operator|<<
literal|1
expr_stmt|;
name|k
operator|=
name|j
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
operator|(
name|heap
index|[
name|k
index|]
operator|.
name|doc
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|doc
operator|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
name|topHSD
operator|=
name|heap
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
end_class

end_unit

