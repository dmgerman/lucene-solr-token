begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TwoPhaseIterator
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

begin_comment
comment|/** Removes matches which overlap with another SpanQuery or which are  * within x tokens before or y tokens after another SpanQuery.  */
end_comment

begin_class
DECL|class|SpanNotQuery
specifier|public
class|class
name|SpanNotQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|include
specifier|private
name|SpanQuery
name|include
decl_stmt|;
DECL|field|exclude
specifier|private
name|SpanQuery
name|exclude
decl_stmt|;
DECL|field|pre
specifier|private
specifier|final
name|int
name|pre
decl_stmt|;
DECL|field|post
specifier|private
specifier|final
name|int
name|post
decl_stmt|;
comment|/** Construct a SpanNotQuery matching spans from<code>include</code> which    * have no overlap with spans from<code>exclude</code>.*/
DECL|method|SpanNotQuery
specifier|public
name|SpanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|)
block|{
name|this
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a SpanNotQuery matching spans from<code>include</code> which    * have no overlap with spans from<code>exclude</code> within    *<code>dist</code> tokens of<code>include</code>. */
DECL|method|SpanNotQuery
specifier|public
name|SpanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|,
name|int
name|dist
parameter_list|)
block|{
name|this
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|,
name|dist
argument_list|,
name|dist
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a SpanNotQuery matching spans from<code>include</code> which    * have no overlap with spans from<code>exclude</code> within    *<code>pre</code> tokens before or<code>post</code> tokens of<code>include</code>. */
DECL|method|SpanNotQuery
specifier|public
name|SpanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|,
name|int
name|pre
parameter_list|,
name|int
name|post
parameter_list|)
block|{
name|this
operator|.
name|include
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|include
argument_list|)
expr_stmt|;
name|this
operator|.
name|exclude
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|exclude
argument_list|)
expr_stmt|;
name|this
operator|.
name|pre
operator|=
operator|(
name|pre
operator|>=
literal|0
operator|)
condition|?
name|pre
else|:
literal|0
expr_stmt|;
name|this
operator|.
name|post
operator|=
operator|(
name|post
operator|>=
literal|0
operator|)
condition|?
name|post
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|include
operator|.
name|getField
argument_list|()
operator|!=
literal|null
operator|&&
name|exclude
operator|.
name|getField
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|include
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|exclude
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Clauses must have same field."
argument_list|)
throw|;
block|}
comment|/** Return the SpanQuery whose matches are filtered. */
DECL|method|getInclude
specifier|public
name|SpanQuery
name|getInclude
parameter_list|()
block|{
return|return
name|include
return|;
block|}
comment|/** Return the SpanQuery whose matches must not overlap those returned. */
DECL|method|getExclude
specifier|public
name|SpanQuery
name|getExclude
parameter_list|()
block|{
return|return
name|exclude
return|;
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
name|include
operator|.
name|getField
argument_list|()
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
literal|"spanNot("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|include
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
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|exclude
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
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|pre
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|post
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
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
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanNotQuery
name|clone
parameter_list|()
block|{
name|SpanNotQuery
name|spanNotQuery
init|=
operator|new
name|SpanNotQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|include
operator|.
name|clone
argument_list|()
argument_list|,
operator|(
name|SpanQuery
operator|)
name|exclude
operator|.
name|clone
argument_list|()
argument_list|,
name|pre
argument_list|,
name|post
argument_list|)
decl_stmt|;
name|spanNotQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spanNotQuery
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
name|SpanCollectorFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanWeight
name|includeWeight
init|=
name|include
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|SpanWeight
name|excludeWeight
init|=
name|exclude
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|,
name|factory
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanNotWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
condition|?
name|getTermContexts
argument_list|(
name|includeWeight
argument_list|,
name|excludeWeight
argument_list|)
else|:
literal|null
argument_list|,
name|factory
argument_list|,
name|includeWeight
argument_list|,
name|excludeWeight
argument_list|)
return|;
block|}
DECL|class|SpanNotWeight
specifier|public
class|class
name|SpanNotWeight
extends|extends
name|SpanWeight
block|{
DECL|field|includeWeight
specifier|final
name|SpanWeight
name|includeWeight
decl_stmt|;
DECL|field|excludeWeight
specifier|final
name|SpanWeight
name|excludeWeight
decl_stmt|;
DECL|method|SpanNotWeight
specifier|public
name|SpanNotWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|terms
parameter_list|,
name|SpanCollectorFactory
name|factory
parameter_list|,
name|SpanWeight
name|includeWeight
parameter_list|,
name|SpanWeight
name|excludeWeight
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SpanNotQuery
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
name|terms
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeWeight
operator|=
name|includeWeight
expr_stmt|;
name|this
operator|.
name|excludeWeight
operator|=
name|excludeWeight
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
name|includeWeight
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
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|,
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|Spans
name|includeSpans
init|=
name|includeWeight
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|collector
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeSpans
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Spans
name|excludeSpans
init|=
name|excludeWeight
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|collector
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludeSpans
operator|==
literal|null
condition|)
block|{
return|return
name|includeSpans
return|;
block|}
name|TwoPhaseIterator
name|excludeTwoPhase
init|=
name|excludeSpans
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|excludeApproximation
init|=
name|excludeTwoPhase
operator|==
literal|null
condition|?
literal|null
else|:
name|excludeTwoPhase
operator|.
name|approximation
argument_list|()
decl_stmt|;
return|return
operator|new
name|FilterSpans
argument_list|(
name|includeSpans
argument_list|)
block|{
comment|// last document we have checked matches() against for the exclusion, and failed
comment|// when using approximations, so we don't call it again, and pass thru all inclusions.
name|int
name|lastApproxDoc
init|=
operator|-
literal|1
decl_stmt|;
name|boolean
name|lastApproxResult
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
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
comment|// TODO: this logic is ugly and sneaky, can we clean it up?
name|int
name|doc
init|=
name|candidate
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>
name|excludeSpans
operator|.
name|docID
argument_list|()
condition|)
block|{
comment|// catch up 'exclude' to the current doc
if|if
condition|(
name|excludeTwoPhase
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|excludeApproximation
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
condition|)
block|{
name|lastApproxDoc
operator|=
name|doc
expr_stmt|;
name|lastApproxResult
operator|=
name|excludeTwoPhase
operator|.
name|matches
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|excludeSpans
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|excludeTwoPhase
operator|!=
literal|null
operator|&&
name|doc
operator|==
name|excludeSpans
operator|.
name|docID
argument_list|()
operator|&&
name|doc
operator|!=
name|lastApproxDoc
condition|)
block|{
comment|// excludeSpans already sitting on our candidate doc, but matches not called yet.
name|lastApproxDoc
operator|=
name|doc
expr_stmt|;
name|lastApproxResult
operator|=
name|excludeTwoPhase
operator|.
name|matches
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|!=
name|excludeSpans
operator|.
name|docID
argument_list|()
operator|||
operator|(
name|doc
operator|==
name|lastApproxDoc
operator|&&
name|lastApproxResult
operator|==
literal|false
operator|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
if|if
condition|(
name|excludeSpans
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|// init exclude start position if needed
name|excludeSpans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|excludeSpans
operator|.
name|endPosition
argument_list|()
operator|<=
name|candidate
operator|.
name|startPosition
argument_list|()
operator|-
name|pre
condition|)
block|{
comment|// exclude end position is before a possible exclusion
if|if
condition|(
name|excludeSpans
operator|.
name|nextStartPosition
argument_list|()
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
comment|// no more exclude at current doc.
block|}
block|}
comment|// exclude end position far enough in current doc, check start position:
if|if
condition|(
name|candidate
operator|.
name|endPosition
argument_list|()
operator|+
name|post
operator|<=
name|excludeSpans
operator|.
name|startPosition
argument_list|()
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
block|}
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
name|includeWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
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
name|SpanNotQuery
name|clone
init|=
literal|null
decl_stmt|;
name|SpanQuery
name|rewrittenInclude
init|=
operator|(
name|SpanQuery
operator|)
name|include
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenInclude
operator|!=
name|include
condition|)
block|{
name|clone
operator|=
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|include
operator|=
name|rewrittenInclude
expr_stmt|;
block|}
name|SpanQuery
name|rewrittenExclude
init|=
operator|(
name|SpanQuery
operator|)
name|exclude
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenExclude
operator|!=
name|exclude
condition|)
block|{
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|exclude
operator|=
name|rewrittenExclude
expr_stmt|;
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|SpanNotQuery
name|other
init|=
operator|(
name|SpanNotQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|include
operator|.
name|equals
argument_list|(
name|other
operator|.
name|include
argument_list|)
operator|&&
name|this
operator|.
name|exclude
operator|.
name|equals
argument_list|(
name|other
operator|.
name|exclude
argument_list|)
operator|&&
name|this
operator|.
name|pre
operator|==
name|other
operator|.
name|pre
operator|&&
name|this
operator|.
name|post
operator|==
name|other
operator|.
name|post
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
name|h
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|h
operator|^=
name|include
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|h
operator|^=
name|exclude
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|h
operator|^=
name|pre
expr_stmt|;
name|h
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|h
operator|^=
name|post
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

