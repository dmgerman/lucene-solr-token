begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.spans
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
name|BooleanQueryNode
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
name|FieldQueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|builders
operator|.
name|StandardQueryBuilder
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
comment|/**  * Sets up a query tree builder to build a span query tree from a query node  * tree.<br>  *<br>  *   * The defined map is:<br>  * - every BooleanQueryNode instance is delegated to the SpanOrQueryNodeBuilder<br>  * - every FieldQueryNode instance is delegated to the SpanTermQueryNodeBuilder<br>  *   */
end_comment

begin_class
DECL|class|SpansQueryTreeBuilder
specifier|public
class|class
name|SpansQueryTreeBuilder
extends|extends
name|QueryTreeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|SpansQueryTreeBuilder
specifier|public
name|SpansQueryTreeBuilder
parameter_list|()
block|{
name|setBuilder
argument_list|(
name|BooleanQueryNode
operator|.
name|class
argument_list|,
operator|new
name|SpanOrQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|FieldQueryNode
operator|.
name|class
argument_list|,
operator|new
name|SpanTermQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|SpanQuery
name|build
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
operator|(
name|SpanQuery
operator|)
name|super
operator|.
name|build
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
block|}
end_class

end_unit

