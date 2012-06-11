begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|builders
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
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|BoostQueryNode
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
name|queryparser
operator|.
name|flexible
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
name|Query
import|;
end_import

begin_comment
comment|/**  * This builder basically reads the {@link Query} object set on the  * {@link BoostQueryNode} child using  * {@link QueryTreeBuilder#QUERY_TREE_BUILDER_TAGID} and applies the boost value  * defined in the {@link BoostQueryNode}.  */
end_comment

begin_class
DECL|class|BoostQueryNodeBuilder
specifier|public
class|class
name|BoostQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|BoostQueryNodeBuilder
specifier|public
name|BoostQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|Query
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|BoostQueryNode
name|boostNode
init|=
operator|(
name|BoostQueryNode
operator|)
name|queryNode
decl_stmt|;
name|QueryNode
name|child
init|=
name|boostNode
operator|.
name|getChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Query
name|query
init|=
operator|(
name|Query
operator|)
name|child
operator|.
name|getTag
argument_list|(
name|QueryTreeBuilder
operator|.
name|QUERY_TREE_BUILDER_TAGID
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|boostNode
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

