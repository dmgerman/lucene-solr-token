begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Iterator
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
name|spans
operator|.
name|SpanNearQuery
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

begin_comment
comment|/** Factory for NEAR queries */
end_comment

begin_class
DECL|class|DistanceQuery
specifier|public
class|class
name|DistanceQuery
extends|extends
name|ComposedQuery
implements|implements
name|DistanceSubQuery
block|{
DECL|method|DistanceQuery
specifier|public
name|DistanceQuery
parameter_list|(
name|List
argument_list|<
name|SrndQuery
argument_list|>
name|queries
parameter_list|,
name|boolean
name|infix
parameter_list|,
name|int
name|opDistance
parameter_list|,
name|String
name|opName
parameter_list|,
name|boolean
name|ordered
parameter_list|)
block|{
name|super
argument_list|(
name|queries
argument_list|,
name|infix
argument_list|,
name|opName
argument_list|)
expr_stmt|;
name|this
operator|.
name|opDistance
operator|=
name|opDistance
expr_stmt|;
comment|/* the distance indicated in the operator */
name|this
operator|.
name|ordered
operator|=
name|ordered
expr_stmt|;
block|}
DECL|field|opDistance
specifier|private
name|int
name|opDistance
decl_stmt|;
DECL|method|getOpDistance
specifier|public
name|int
name|getOpDistance
parameter_list|()
block|{
return|return
name|opDistance
return|;
block|}
DECL|field|ordered
specifier|private
name|boolean
name|ordered
decl_stmt|;
DECL|method|subQueriesOrdered
specifier|public
name|boolean
name|subQueriesOrdered
parameter_list|()
block|{
return|return
name|ordered
return|;
block|}
annotation|@
name|Override
DECL|method|distanceSubQueryNotAllowed
specifier|public
name|String
name|distanceSubQueryNotAllowed
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
argument_list|>
name|sqi
init|=
name|getSubQueriesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|sqi
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|leq
init|=
name|sqi
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|leq
operator|instanceof
name|DistanceSubQuery
condition|)
block|{
name|DistanceSubQuery
name|dsq
init|=
operator|(
name|DistanceSubQuery
operator|)
name|leq
decl_stmt|;
name|String
name|m
init|=
name|dsq
operator|.
name|distanceSubQueryNotAllowed
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
return|return
name|m
return|;
block|}
block|}
else|else
block|{
return|return
literal|"Operator "
operator|+
name|getOperatorName
argument_list|()
operator|+
literal|" does not allow subquery "
operator|+
name|leq
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
comment|/* subqueries acceptable */
block|}
annotation|@
name|Override
DECL|method|addSpanQueries
specifier|public
name|void
name|addSpanQueries
parameter_list|(
name|SpanNearClauseFactory
name|sncf
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|snq
init|=
name|getSpanNearQuery
argument_list|(
name|sncf
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|sncf
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|getWeight
argument_list|()
argument_list|,
name|sncf
operator|.
name|getBasicQueryFactory
argument_list|()
argument_list|)
decl_stmt|;
name|sncf
operator|.
name|addSpanQuery
argument_list|(
name|snq
argument_list|)
expr_stmt|;
block|}
DECL|method|getSpanNearQuery
specifier|public
name|Query
name|getSpanNearQuery
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|float
name|boost
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanQuery
index|[]
name|spanClauses
init|=
operator|new
name|SpanQuery
index|[
name|getNrSubQueries
argument_list|()
index|]
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|sqi
init|=
name|getSubQueriesIterator
argument_list|()
decl_stmt|;
name|int
name|qi
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|sqi
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpanNearClauseFactory
name|sncf
init|=
operator|new
name|SpanNearClauseFactory
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
name|qf
argument_list|)
decl_stmt|;
operator|(
operator|(
name|DistanceSubQuery
operator|)
name|sqi
operator|.
name|next
argument_list|()
operator|)
operator|.
name|addSpanQueries
argument_list|(
name|sncf
argument_list|)
expr_stmt|;
if|if
condition|(
name|sncf
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|/* distance operator requires all sub queries */
while|while
condition|(
name|sqi
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|/* produce evt. error messages but ignore results */
operator|(
operator|(
name|DistanceSubQuery
operator|)
name|sqi
operator|.
name|next
argument_list|()
operator|)
operator|.
name|addSpanQueries
argument_list|(
name|sncf
argument_list|)
expr_stmt|;
name|sncf
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|SrndQuery
operator|.
name|theEmptyLcnQuery
return|;
block|}
name|spanClauses
index|[
name|qi
index|]
operator|=
name|sncf
operator|.
name|makeSpanClause
argument_list|()
expr_stmt|;
name|qi
operator|++
expr_stmt|;
block|}
name|SpanNearQuery
name|r
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|spanClauses
argument_list|,
name|getOpDistance
argument_list|()
operator|-
literal|1
argument_list|,
name|subQueriesOrdered
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|makeLuceneQueryFieldNoBoost
specifier|public
name|Query
name|makeLuceneQueryFieldNoBoost
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
return|return
operator|new
name|DistanceRewriteQuery
argument_list|(
name|this
argument_list|,
name|fieldName
argument_list|,
name|qf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

