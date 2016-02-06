begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|TwoPhaseIterator
import|;
end_import

begin_comment
comment|/**  * {@link Scorer} which returns the result of {@link FunctionValues#floatVal(int)} as  * the score for a document, and which filters out documents that don't match {@link #matches(int)}.  * This Scorer has a {@link TwoPhaseIterator}.  This is similar to {@link FunctionQuery},  * but this one has no {@link org.apache.lucene.search.Weight} normalization factors/multipliers  * and that one doesn't filter either.  *<p>  * Note: If the scores are needed, then the underlying value will probably be  * fetched/computed twice -- once to filter and next to return the score.  If that's non-trivial then  * consider wrapping it in an implementation that will cache the current value.  *</p>  *  * @see FunctionQuery  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ValueSourceScorer
specifier|public
specifier|abstract
class|class
name|ValueSourceScorer
extends|extends
name|Scorer
block|{
DECL|field|values
specifier|protected
specifier|final
name|FunctionValues
name|values
decl_stmt|;
DECL|field|twoPhaseIterator
specifier|private
specifier|final
name|TwoPhaseIterator
name|twoPhaseIterator
decl_stmt|;
DECL|field|disi
specifier|private
specifier|final
name|DocIdSetIterator
name|disi
decl_stmt|;
comment|//TODO use LeafReaderContext not IndexReader?
DECL|method|ValueSourceScorer
specifier|protected
name|ValueSourceScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|FunctionValues
name|values
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//no weight
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
specifier|final
name|DocIdSetIterator
name|approximation
init|=
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// no approximation!
name|this
operator|.
name|twoPhaseIterator
operator|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
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
return|return
name|ValueSourceScorer
operator|.
name|this
operator|.
name|matches
argument_list|(
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|100
return|;
comment|// TODO: use cost of ValueSourceScorer.this.matches()
block|}
block|}
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
expr_stmt|;
block|}
comment|/** Override to decide if this document matches. It's called by {@link TwoPhaseIterator#matches()}. */
DECL|method|matches
specifier|public
specifier|abstract
name|boolean
name|matches
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|disi
return|;
block|}
annotation|@
name|Override
DECL|method|twoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseIterator
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
name|disi
operator|.
name|docID
argument_list|()
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
comment|// (same as FunctionQuery, but no qWeight)  TODO consider adding configurable qWeight
name|float
name|score
init|=
name|values
operator|.
name|floatVal
argument_list|(
name|disi
operator|.
name|docID
argument_list|()
argument_list|)
decl_stmt|;
comment|// Current Lucene priority queues can't handle NaN and -Infinity, so
comment|// map to -Float.MAX_VALUE. This conditional handles both -infinity
comment|// and NaN since comparisons with NaN are always false.
return|return
name|score
operator|>
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|?
name|score
else|:
operator|-
name|Float
operator|.
name|MAX_VALUE
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
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

