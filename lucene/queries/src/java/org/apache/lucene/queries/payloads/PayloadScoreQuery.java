begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|payloads
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|PostingsEnum
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
name|index
operator|.
name|TermContext
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
name|Explanation
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
name|IndexSearcher
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
name|Query
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
name|ClassicSimilarity
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
operator|.
name|SimScorer
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
name|spans
operator|.
name|FilterSpans
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
name|spans
operator|.
name|SpanCollector
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanScorer
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
name|spans
operator|.
name|SpanWeight
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
name|spans
operator|.
name|Spans
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

begin_comment
comment|/**  * A Query class that uses a {@link PayloadFunction} to modify the score of a wrapped SpanQuery  *  * NOTE: In order to take advantage of this with the default scoring implementation  * ({@link ClassicSimilarity}), you must override {@link ClassicSimilarity#scorePayload(int, int, int, BytesRef)},  * which returns 1 by default.  *  * @see org.apache.lucene.search.similarities.Similarity.SimScorer#computePayloadFactor(int, int, int, BytesRef)  */
end_comment

begin_class
DECL|class|PayloadScoreQuery
specifier|public
class|class
name|PayloadScoreQuery
extends|extends
name|SpanQuery
block|{
DECL|field|wrappedQuery
specifier|private
specifier|final
name|SpanQuery
name|wrappedQuery
decl_stmt|;
DECL|field|function
specifier|private
specifier|final
name|PayloadFunction
name|function
decl_stmt|;
DECL|field|includeSpanScore
specifier|private
specifier|final
name|boolean
name|includeSpanScore
decl_stmt|;
comment|/**    * Creates a new PayloadScoreQuery    * @param wrappedQuery the query to wrap    * @param function a PayloadFunction to use to modify the scores    * @param includeSpanScore include both span score and payload score in the scoring algorithm    */
DECL|method|PayloadScoreQuery
specifier|public
name|PayloadScoreQuery
parameter_list|(
name|SpanQuery
name|wrappedQuery
parameter_list|,
name|PayloadFunction
name|function
parameter_list|,
name|boolean
name|includeSpanScore
parameter_list|)
block|{
name|this
operator|.
name|wrappedQuery
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|wrappedQuery
argument_list|)
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|function
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeSpanScore
operator|=
name|includeSpanScore
expr_stmt|;
block|}
comment|/**    * Creates a new PayloadScoreQuery that includes the underlying span scores    * @param wrappedQuery the query to wrap    * @param function a PayloadFunction to use to modify the scores    */
DECL|method|PayloadScoreQuery
specifier|public
name|PayloadScoreQuery
parameter_list|(
name|SpanQuery
name|wrappedQuery
parameter_list|,
name|PayloadFunction
name|function
parameter_list|)
block|{
name|this
argument_list|(
name|wrappedQuery
argument_list|,
name|function
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|wrappedQuery
operator|.
name|getField
argument_list|()
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
name|Query
name|matchRewritten
init|=
name|wrappedQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrappedQuery
operator|!=
name|matchRewritten
operator|&&
name|matchRewritten
operator|instanceof
name|SpanQuery
condition|)
block|{
return|return
operator|new
name|PayloadScoreQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|matchRewritten
argument_list|,
name|function
argument_list|,
name|includeSpanScore
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
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
name|buffer
operator|.
name|append
argument_list|(
literal|"PayloadScoreQuery("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|wrappedQuery
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
literal|", function: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|function
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", includeSpanScore: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|includeSpanScore
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanWeight
name|innerWeight
init|=
name|wrappedQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|needsScores
condition|)
return|return
name|innerWeight
return|;
return|return
operator|new
name|PayloadSpanWeight
argument_list|(
name|searcher
argument_list|,
name|innerWeight
argument_list|,
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|PayloadScoreQuery
name|other
parameter_list|)
block|{
return|return
name|wrappedQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|wrappedQuery
argument_list|)
operator|&&
name|function
operator|.
name|equals
argument_list|(
name|other
operator|.
name|function
argument_list|)
operator|&&
operator|(
name|includeSpanScore
operator|==
name|other
operator|.
name|includeSpanScore
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|classHash
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Objects
operator|.
name|hashCode
argument_list|(
name|wrappedQuery
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Objects
operator|.
name|hashCode
argument_list|(
name|function
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Objects
operator|.
name|hashCode
argument_list|(
name|includeSpanScore
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|PayloadSpanWeight
specifier|private
class|class
name|PayloadSpanWeight
extends|extends
name|SpanWeight
block|{
DECL|field|innerWeight
specifier|private
specifier|final
name|SpanWeight
name|innerWeight
decl_stmt|;
DECL|method|PayloadSpanWeight
specifier|public
name|PayloadSpanWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|SpanWeight
name|innerWeight
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|PayloadScoreQuery
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
literal|null
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|this
operator|.
name|innerWeight
operator|=
name|innerWeight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTermContexts
specifier|public
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
block|{
name|innerWeight
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|Postings
name|requiredPostings
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|innerWeight
operator|.
name|getSpans
argument_list|(
name|ctx
argument_list|,
name|requiredPostings
operator|.
name|atLeast
argument_list|(
name|Postings
operator|.
name|PAYLOADS
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|SpanScorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Spans
name|spans
init|=
name|getSpans
argument_list|(
name|context
argument_list|,
name|Postings
operator|.
name|PAYLOADS
argument_list|)
decl_stmt|;
if|if
condition|(
name|spans
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|SimScorer
name|docScorer
init|=
name|innerWeight
operator|.
name|getSimScorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|PayloadSpans
name|payloadSpans
init|=
operator|new
name|PayloadSpans
argument_list|(
name|spans
argument_list|,
name|docScorer
argument_list|)
decl_stmt|;
return|return
operator|new
name|PayloadSpanScorer
argument_list|(
name|this
argument_list|,
name|payloadSpans
argument_list|,
name|docScorer
argument_list|)
return|;
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
name|innerWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
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
name|PayloadSpanScorer
name|scorer
init|=
operator|(
name|PayloadSpanScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
operator|||
name|scorer
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|!=
name|doc
condition|)
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No match"
argument_list|)
return|;
name|scorer
operator|.
name|freq
argument_list|()
expr_stmt|;
comment|// force freq calculation
name|Explanation
name|payloadExpl
init|=
name|scorer
operator|.
name|getPayloadExplanation
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeSpanScore
condition|)
block|{
name|SpanWeight
name|innerWeight
init|=
operator|(
operator|(
name|PayloadSpanWeight
operator|)
name|scorer
operator|.
name|getWeight
argument_list|()
operator|)
operator|.
name|innerWeight
decl_stmt|;
name|Explanation
name|innerExpl
init|=
name|innerWeight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scorer
operator|.
name|scoreCurrentDoc
argument_list|()
argument_list|,
literal|"PayloadSpanQuery, product of:"
argument_list|,
name|innerExpl
argument_list|,
name|payloadExpl
argument_list|)
return|;
block|}
return|return
name|scorer
operator|.
name|getPayloadExplanation
argument_list|()
return|;
block|}
block|}
DECL|class|PayloadSpans
specifier|private
class|class
name|PayloadSpans
extends|extends
name|FilterSpans
implements|implements
name|SpanCollector
block|{
DECL|field|docScorer
specifier|private
specifier|final
name|SimScorer
name|docScorer
decl_stmt|;
DECL|field|payloadsSeen
specifier|public
name|int
name|payloadsSeen
decl_stmt|;
DECL|field|payloadScore
specifier|public
name|float
name|payloadScore
decl_stmt|;
DECL|method|PayloadSpans
specifier|private
name|PayloadSpans
parameter_list|(
name|Spans
name|in
parameter_list|,
name|SimScorer
name|docScorer
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|Spans
name|candidate
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
annotation|@
name|Override
DECL|method|doStartCurrentDoc
specifier|protected
name|void
name|doStartCurrentDoc
parameter_list|()
block|{
name|payloadScore
operator|=
literal|0
expr_stmt|;
name|payloadsSeen
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collectLeaf
specifier|public
name|void
name|collectLeaf
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|int
name|position
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|payload
init|=
name|postings
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
return|return;
name|float
name|payloadFactor
init|=
name|docScorer
operator|.
name|computePayloadFactor
argument_list|(
name|docID
argument_list|()
argument_list|,
name|in
operator|.
name|startPosition
argument_list|()
argument_list|,
name|in
operator|.
name|endPosition
argument_list|()
argument_list|,
name|payload
argument_list|)
decl_stmt|;
name|payloadScore
operator|=
name|function
operator|.
name|currentScore
argument_list|(
name|docID
argument_list|()
argument_list|,
name|getField
argument_list|()
argument_list|,
name|in
operator|.
name|startPosition
argument_list|()
argument_list|,
name|in
operator|.
name|endPosition
argument_list|()
argument_list|,
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|,
name|payloadFactor
argument_list|)
expr_stmt|;
name|payloadsSeen
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|doCurrentSpans
specifier|protected
name|void
name|doCurrentSpans
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|collect
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PayloadSpanScorer
specifier|private
class|class
name|PayloadSpanScorer
extends|extends
name|SpanScorer
block|{
DECL|field|spans
specifier|private
specifier|final
name|PayloadSpans
name|spans
decl_stmt|;
DECL|method|PayloadSpanScorer
specifier|private
name|PayloadSpanScorer
parameter_list|(
name|SpanWeight
name|weight
parameter_list|,
name|PayloadSpans
name|spans
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
argument_list|,
name|spans
argument_list|,
name|docScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|spans
operator|=
name|spans
expr_stmt|;
block|}
DECL|method|getPayloadScore
specifier|protected
name|float
name|getPayloadScore
parameter_list|()
block|{
return|return
name|function
operator|.
name|docScore
argument_list|(
name|docID
argument_list|()
argument_list|,
name|getField
argument_list|()
argument_list|,
name|spans
operator|.
name|payloadsSeen
argument_list|,
name|spans
operator|.
name|payloadScore
argument_list|)
return|;
block|}
DECL|method|getPayloadExplanation
specifier|protected
name|Explanation
name|getPayloadExplanation
parameter_list|()
block|{
return|return
name|function
operator|.
name|explain
argument_list|(
name|docID
argument_list|()
argument_list|,
name|getField
argument_list|()
argument_list|,
name|spans
operator|.
name|payloadsSeen
argument_list|,
name|spans
operator|.
name|payloadScore
argument_list|)
return|;
block|}
DECL|method|getSpanScore
specifier|protected
name|float
name|getSpanScore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|scoreCurrentDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scoreCurrentDoc
specifier|protected
name|float
name|scoreCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|includeSpanScore
condition|)
return|return
name|getSpanScore
argument_list|()
operator|*
name|getPayloadScore
argument_list|()
return|;
return|return
name|getPayloadScore
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

