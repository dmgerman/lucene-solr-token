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
name|Collections
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
comment|/** Internal document-at-a-time scorers used to deal with stupid coord() computation */
end_comment

begin_class
DECL|class|BooleanTopLevelScorers
class|class
name|BooleanTopLevelScorers
block|{
comment|/**     * Used when there is more than one scorer in a query, but a segment    * only had one non-null scorer. This just wraps that scorer directly    * to factor in coord().    */
DECL|class|BoostedScorer
specifier|static
class|class
name|BoostedScorer
extends|extends
name|FilterScorer
block|{
DECL|field|boost
specifier|final
name|float
name|boost
decl_stmt|;
DECL|method|BoostedScorer
name|BoostedScorer
parameter_list|(
name|Scorer
name|in
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
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
return|return
name|in
operator|.
name|score
argument_list|()
operator|*
name|boost
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
name|in
argument_list|,
literal|"BOOSTED"
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Used when there is more than one scorer in a query, but a segment    * only had one non-null scorer.    */
DECL|class|BoostedBulkScorer
specifier|static
class|class
name|BoostedBulkScorer
extends|extends
name|BulkScorer
block|{
DECL|field|in
specifier|final
name|BulkScorer
name|in
decl_stmt|;
DECL|field|boost
specifier|final
name|float
name|boost
decl_stmt|;
DECL|method|BoostedBulkScorer
name|BoostedBulkScorer
parameter_list|(
name|BulkScorer
name|scorer
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|int
name|score
parameter_list|(
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
name|wrapped
init|=
operator|new
name|FilterLeafCollector
argument_list|(
name|collector
argument_list|)
block|{
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
name|super
operator|.
name|setScorer
argument_list|(
operator|new
name|BoostedScorer
argument_list|(
name|scorer
argument_list|,
name|boost
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
name|in
operator|.
name|score
argument_list|(
name|wrapped
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
block|}
comment|/**     * Used when there are both mandatory and optional clauses, but minShouldMatch    * dictates that some of the optional clauses must match. The query is a conjunction,    * but must compute coord based on how many optional subscorers matched (freq).    */
DECL|class|CoordinatingConjunctionScorer
specifier|static
class|class
name|CoordinatingConjunctionScorer
extends|extends
name|ConjunctionScorer
block|{
DECL|field|coords
specifier|private
specifier|final
name|float
name|coords
index|[]
decl_stmt|;
DECL|field|reqCount
specifier|private
specifier|final
name|int
name|reqCount
decl_stmt|;
DECL|field|req
specifier|private
specifier|final
name|Scorer
name|req
decl_stmt|;
DECL|field|opt
specifier|private
specifier|final
name|Scorer
name|opt
decl_stmt|;
DECL|method|CoordinatingConjunctionScorer
name|CoordinatingConjunctionScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|float
name|coords
index|[]
parameter_list|,
name|Scorer
name|req
parameter_list|,
name|int
name|reqCount
parameter_list|,
name|Scorer
name|opt
parameter_list|)
block|{
name|super
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
expr_stmt|;
name|this
operator|.
name|coords
operator|=
name|coords
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|reqCount
operator|=
name|reqCount
expr_stmt|;
name|this
operator|.
name|opt
operator|=
name|opt
expr_stmt|;
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
return|return
operator|(
name|req
operator|.
name|score
argument_list|()
operator|+
name|opt
operator|.
name|score
argument_list|()
operator|)
operator|*
name|coords
index|[
name|reqCount
operator|+
name|opt
operator|.
name|freq
argument_list|()
index|]
return|;
block|}
block|}
comment|/**     * Used when there are mandatory clauses with one optional clause: we compute    * coord based on whether the optional clause matched or not.    */
DECL|class|ReqSingleOptScorer
specifier|static
class|class
name|ReqSingleOptScorer
extends|extends
name|ReqOptSumScorer
block|{
comment|// coord factor if just the required part matches
DECL|field|coordReq
specifier|private
specifier|final
name|float
name|coordReq
decl_stmt|;
comment|// coord factor if both required and optional part matches
DECL|field|coordBoth
specifier|private
specifier|final
name|float
name|coordBoth
decl_stmt|;
DECL|method|ReqSingleOptScorer
specifier|public
name|ReqSingleOptScorer
parameter_list|(
name|Scorer
name|reqScorer
parameter_list|,
name|Scorer
name|optScorer
parameter_list|,
name|float
name|coordReq
parameter_list|,
name|float
name|coordBoth
parameter_list|)
block|{
name|super
argument_list|(
name|reqScorer
argument_list|,
name|optScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|coordReq
operator|=
name|coordReq
expr_stmt|;
name|this
operator|.
name|coordBoth
operator|=
name|coordBoth
expr_stmt|;
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
comment|// TODO: sum into a double and cast to float if we ever send required clauses to BS1
name|int
name|curDoc
init|=
name|reqScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
name|float
name|score
init|=
name|reqScorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|int
name|optScorerDoc
init|=
name|optIterator
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|optScorerDoc
operator|<
name|curDoc
condition|)
block|{
name|optScorerDoc
operator|=
name|optIterator
operator|.
name|advance
argument_list|(
name|curDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|optScorerDoc
operator|==
name|curDoc
condition|)
block|{
name|score
operator|=
operator|(
name|score
operator|+
name|optScorer
operator|.
name|score
argument_list|()
operator|)
operator|*
name|coordBoth
expr_stmt|;
block|}
else|else
block|{
name|score
operator|=
name|score
operator|*
name|coordReq
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
comment|/**     * Used when there are mandatory clauses with optional clauses: we compute    * coord based on how many optional subscorers matched (freq).    */
DECL|class|ReqMultiOptScorer
specifier|static
class|class
name|ReqMultiOptScorer
extends|extends
name|ReqOptSumScorer
block|{
DECL|field|requiredCount
specifier|private
specifier|final
name|int
name|requiredCount
decl_stmt|;
DECL|field|coords
specifier|private
specifier|final
name|float
name|coords
index|[]
decl_stmt|;
DECL|method|ReqMultiOptScorer
specifier|public
name|ReqMultiOptScorer
parameter_list|(
name|Scorer
name|reqScorer
parameter_list|,
name|Scorer
name|optScorer
parameter_list|,
name|int
name|requiredCount
parameter_list|,
name|float
name|coords
index|[]
parameter_list|)
block|{
name|super
argument_list|(
name|reqScorer
argument_list|,
name|optScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredCount
operator|=
name|requiredCount
expr_stmt|;
name|this
operator|.
name|coords
operator|=
name|coords
expr_stmt|;
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
comment|// TODO: sum into a double and cast to float if we ever send required clauses to BS1
name|int
name|curDoc
init|=
name|reqScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
name|float
name|score
init|=
name|reqScorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|int
name|optScorerDoc
init|=
name|optIterator
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|optScorerDoc
operator|<
name|curDoc
condition|)
block|{
name|optScorerDoc
operator|=
name|optIterator
operator|.
name|advance
argument_list|(
name|curDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|optScorerDoc
operator|==
name|curDoc
condition|)
block|{
name|score
operator|=
operator|(
name|score
operator|+
name|optScorer
operator|.
name|score
argument_list|()
operator|)
operator|*
name|coords
index|[
name|requiredCount
operator|+
name|optScorer
operator|.
name|freq
argument_list|()
index|]
expr_stmt|;
block|}
else|else
block|{
name|score
operator|=
name|score
operator|*
name|coords
index|[
name|requiredCount
index|]
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
block|}
end_class

end_unit

