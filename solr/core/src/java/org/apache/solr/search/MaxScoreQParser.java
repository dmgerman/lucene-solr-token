begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|DisjunctionMaxQuery
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * @see MaxScoreQParserPlugin  */
end_comment

begin_class
DECL|class|MaxScoreQParser
specifier|public
class|class
name|MaxScoreQParser
extends|extends
name|LuceneQParser
block|{
DECL|field|tie
name|float
name|tie
init|=
literal|0.0f
decl_stmt|;
DECL|method|MaxScoreQParser
specifier|public
name|MaxScoreQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
if|if
condition|(
name|getParam
argument_list|(
literal|"tie"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|tie
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|getParam
argument_list|(
literal|"tie"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Parses the query exactly like the Lucene parser does, but    * delegates all SHOULD clauses to DisjunctionMaxQuery with    * meaning only the clause with the max score will contribute    * to the overall score, unless the tie parameter is specified.    *<br/>    * The max() is only calculated from the SHOULD clauses.    * Any MUST clauses will be passed through as separate    * BooleanClauses and thus always contribute to the score.    * @return the resulting Query    * @throws org.apache.solr.search.SyntaxError if parsing fails    */
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|Query
name|q
init|=
name|super
operator|.
name|parse
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
condition|)
block|{
return|return
name|q
return|;
block|}
name|BooleanQuery
name|obq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|Collection
argument_list|<
name|Query
argument_list|>
name|should
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|prohibOrReq
init|=
operator|new
name|ArrayList
argument_list|<
name|BooleanClause
argument_list|>
argument_list|()
decl_stmt|;
name|BooleanQuery
name|newq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|obq
operator|.
name|getClauses
argument_list|()
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|isProhibited
argument_list|()
operator|||
name|clause
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|prohibOrReq
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|clause
argument_list|)
expr_stmt|;
name|should
operator|.
name|add
argument_list|(
name|bq
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|should
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DisjunctionMaxQuery
name|dmq
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|should
argument_list|,
name|tie
argument_list|)
decl_stmt|;
name|newq
operator|.
name|add
argument_list|(
name|dmq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|BooleanClause
name|c
range|:
name|prohibOrReq
control|)
block|{
name|newq
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|newq
return|;
block|}
block|}
end_class

end_unit

