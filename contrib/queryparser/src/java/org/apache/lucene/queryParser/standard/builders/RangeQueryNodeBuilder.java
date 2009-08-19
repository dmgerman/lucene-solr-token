begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|builders
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
operator|.
name|CompareOperator
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|MultiTermRewriteMethodAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|nodes
operator|.
name|RangeQueryNode
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
name|MultiTermQuery
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
name|TermRangeQuery
import|;
end_import

begin_comment
comment|/**  * Builds a {@link TermRangeQuery} object from a {@link RangeQueryNode} object.  */
end_comment

begin_class
DECL|class|RangeQueryNodeBuilder
specifier|public
class|class
name|RangeQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|RangeQueryNodeBuilder
specifier|public
name|RangeQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|TermRangeQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|RangeQueryNode
name|rangeNode
init|=
operator|(
name|RangeQueryNode
operator|)
name|queryNode
decl_stmt|;
name|ParametricQueryNode
name|upper
init|=
name|rangeNode
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
name|ParametricQueryNode
name|lower
init|=
name|rangeNode
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
name|boolean
name|lowerInclusive
init|=
literal|false
decl_stmt|;
name|boolean
name|upperInclusive
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|upper
operator|.
name|getOperator
argument_list|()
operator|==
name|CompareOperator
operator|.
name|LE
condition|)
block|{
name|upperInclusive
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|lower
operator|.
name|getOperator
argument_list|()
operator|==
name|CompareOperator
operator|.
name|GE
condition|)
block|{
name|lowerInclusive
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|field
init|=
name|rangeNode
operator|.
name|getField
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TermRangeQuery
name|rangeQuery
init|=
operator|new
name|TermRangeQuery
argument_list|(
name|field
argument_list|,
name|lower
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|upper
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|,
name|rangeNode
operator|.
name|getCollator
argument_list|()
argument_list|)
decl_stmt|;
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
init|=
operator|(
name|MultiTermQuery
operator|.
name|RewriteMethod
operator|)
name|queryNode
operator|.
name|getTag
argument_list|(
name|MultiTermRewriteMethodAttribute
operator|.
name|TAG_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|rangeQuery
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
return|return
name|rangeQuery
return|;
block|}
block|}
end_class

end_unit

