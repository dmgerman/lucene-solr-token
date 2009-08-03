begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.original.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
operator|.
name|builders
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
name|messages
operator|.
name|MessageImpl
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
name|builders
operator|.
name|QueryTreeBuilder
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
name|messages
operator|.
name|QueryParserMessages
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
name|AnyQueryNode
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
operator|.
name|TooManyClauses
import|;
end_import

begin_class
DECL|class|AnyQueryNodeBuilder
specifier|public
class|class
name|AnyQueryNodeBuilder
implements|implements
name|OriginalQueryBuilder
block|{
DECL|method|AnyQueryNodeBuilder
specifier|public
name|AnyQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|BooleanQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|AnyQueryNode
name|andNode
init|=
operator|(
name|AnyQueryNode
operator|)
name|queryNode
decl_stmt|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|andNode
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|Object
name|obj
init|=
name|child
operator|.
name|getTag
argument_list|(
name|QueryTreeBuilder
operator|.
name|QUERY_TREE_BUILDER_TAGID
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
condition|)
block|{
name|Query
name|query
init|=
operator|(
name|Query
operator|)
name|obj
decl_stmt|;
try|try
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TooManyClauses
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
comment|/*              * IQQQ.Q0028E_TOO_MANY_BOOLEAN_CLAUSES,              * BooleanQuery.getMaxClauseCount()              */
name|QueryParserMessages
operator|.
name|EMPTY_MESSAGE
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
name|bQuery
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|andNode
operator|.
name|getMinimumMatchingElements
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bQuery
return|;
block|}
block|}
end_class

end_unit

