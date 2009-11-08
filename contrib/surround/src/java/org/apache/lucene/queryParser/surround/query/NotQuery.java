begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BooleanQuery
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

begin_class
DECL|class|NotQuery
specifier|public
class|class
name|NotQuery
extends|extends
name|ComposedQuery
block|{
DECL|method|NotQuery
specifier|public
name|NotQuery
parameter_list|(
name|List
name|queries
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
name|super
argument_list|(
name|queries
argument_list|,
literal|true
comment|/* infix */
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
name|List
name|luceneSubQueries
init|=
name|makeLuceneSubQueriesField
argument_list|(
name|fieldName
argument_list|,
name|qf
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|(
name|Query
operator|)
name|luceneSubQueries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|SrndBooleanQuery
operator|.
name|addQueriesToBoolean
argument_list|(
name|bq
argument_list|,
comment|// FIXME: do not allow weights on prohibited subqueries.
name|luceneSubQueries
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|luceneSubQueries
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
comment|// later subqueries: not required, prohibited
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
block|}
end_class

end_unit

