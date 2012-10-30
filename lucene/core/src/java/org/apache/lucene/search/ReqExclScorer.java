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

begin_comment
comment|/** A Scorer for queries with a required subscorer  * and an excluding (prohibited) sub DocIdSetIterator.  *<br>  * This<code>Scorer</code> implements {@link Scorer#advance(int)},  * and it uses the skipTo() on the given scorers.  */
end_comment

begin_class
DECL|class|ReqExclScorer
class|class
name|ReqExclScorer
extends|extends
name|Scorer
block|{
DECL|field|reqScorer
specifier|private
name|Scorer
name|reqScorer
decl_stmt|;
DECL|field|exclDisi
specifier|private
name|DocIdSetIterator
name|exclDisi
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Construct a<code>ReqExclScorer</code>.    * @param reqScorer The scorer that must match, except where    * @param exclDisi indicates exclusion.    */
DECL|method|ReqExclScorer
specifier|public
name|ReqExclScorer
parameter_list|(
name|Scorer
name|reqScorer
parameter_list|,
name|DocIdSetIterator
name|exclDisi
parameter_list|)
block|{
name|super
argument_list|(
name|reqScorer
operator|.
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|reqScorer
operator|=
name|reqScorer
expr_stmt|;
name|this
operator|.
name|exclDisi
operator|=
name|exclDisi
expr_stmt|;
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
if|if
condition|(
name|reqScorer
operator|==
literal|null
condition|)
block|{
return|return
name|doc
return|;
block|}
name|doc
operator|=
name|reqScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|reqScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted, nothing left
return|return
name|doc
return|;
block|}
if|if
condition|(
name|exclDisi
operator|==
literal|null
condition|)
block|{
return|return
name|doc
return|;
block|}
return|return
name|doc
operator|=
name|toNonExcluded
argument_list|()
return|;
block|}
comment|/** Advance to non excluded doc.    *<br>On entry:    *<ul>    *<li>reqScorer != null,    *<li>exclScorer != null,    *<li>reqScorer was advanced once via next() or skipTo()    *      and reqScorer.doc() may still be excluded.    *</ul>    * Advances reqScorer a non excluded required doc, if any.    * @return true iff there is a non excluded required doc.    */
DECL|method|toNonExcluded
specifier|private
name|int
name|toNonExcluded
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|exclDoc
init|=
name|exclDisi
operator|.
name|docID
argument_list|()
decl_stmt|;
name|int
name|reqDoc
init|=
name|reqScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
comment|// may be excluded
do|do
block|{
if|if
condition|(
name|reqDoc
operator|<
name|exclDoc
condition|)
block|{
return|return
name|reqDoc
return|;
comment|// reqScorer advanced to before exclScorer, ie. not excluded
block|}
elseif|else
if|if
condition|(
name|reqDoc
operator|>
name|exclDoc
condition|)
block|{
name|exclDoc
operator|=
name|exclDisi
operator|.
name|advance
argument_list|(
name|reqDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|exclDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|exclDisi
operator|=
literal|null
expr_stmt|;
comment|// exhausted, no more exclusions
return|return
name|reqDoc
return|;
block|}
if|if
condition|(
name|exclDoc
operator|>
name|reqDoc
condition|)
block|{
return|return
name|reqDoc
return|;
comment|// not excluded
block|}
block|}
block|}
do|while
condition|(
operator|(
name|reqDoc
operator|=
name|reqScorer
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
do|;
name|reqScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted, nothing left
return|return
name|NO_MORE_DOCS
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
name|doc
return|;
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #nextDoc()} is called the first time.    * @return The score of the required scorer.    */
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
return|return
name|reqScorer
operator|.
name|score
argument_list|()
return|;
comment|// reqScorer may be null when next() or skipTo() already return false
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
name|reqScorer
operator|.
name|freq
argument_list|()
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
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|reqScorer
argument_list|,
literal|"FILTERED"
argument_list|)
argument_list|)
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
if|if
condition|(
name|reqScorer
operator|==
literal|null
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|exclDisi
operator|==
literal|null
condition|)
block|{
return|return
name|doc
operator|=
name|reqScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
if|if
condition|(
name|reqScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|reqScorer
operator|=
literal|null
expr_stmt|;
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|doc
operator|=
name|toNonExcluded
argument_list|()
return|;
block|}
block|}
end_class

end_unit

