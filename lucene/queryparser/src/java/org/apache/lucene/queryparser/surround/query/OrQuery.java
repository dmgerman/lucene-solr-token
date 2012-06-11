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
name|BooleanClause
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

begin_class
DECL|class|OrQuery
specifier|public
class|class
name|OrQuery
extends|extends
name|ComposedQuery
implements|implements
name|DistanceSubQuery
block|{
DECL|method|OrQuery
specifier|public
name|OrQuery
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
name|String
name|opName
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
block|}
annotation|@
name|Override
DECL|method|makeLuceneQueryFieldNoBoost
specifier|public
name|Query
name|makeLuceneQueryFieldNoBoost
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
return|return
name|SrndBooleanQuery
operator|.
name|makeBooleanQuery
argument_list|(
comment|/* subqueries can be individually boosted */
name|makeLuceneSubQueriesField
argument_list|(
name|fieldName
argument_list|,
name|qf
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
return|;
block|}
DECL|method|distanceSubQueryNotAllowed
specifier|public
name|String
name|distanceSubQueryNotAllowed
parameter_list|()
block|{
name|Iterator
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
name|SrndQuery
name|leq
init|=
operator|(
name|SrndQuery
operator|)
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
name|String
name|m
init|=
operator|(
operator|(
name|DistanceSubQuery
operator|)
name|leq
operator|)
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
literal|"subquery not allowed: "
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
block|}
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
name|Iterator
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
block|}
block|}
block|}
end_class

end_unit

