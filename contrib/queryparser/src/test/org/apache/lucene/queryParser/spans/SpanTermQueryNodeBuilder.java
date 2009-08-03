begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|spans
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
name|index
operator|.
name|Term
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
name|original
operator|.
name|builders
operator|.
name|OriginalQueryBuilder
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
name|SpanTermQuery
import|;
end_import

begin_comment
comment|/**  * This builder creates {@link SpanTermQuery}s from a {@link FieldQueryNode}  * object.  */
end_comment

begin_class
DECL|class|SpanTermQueryNodeBuilder
specifier|public
class|class
name|SpanTermQueryNodeBuilder
implements|implements
name|OriginalQueryBuilder
block|{
DECL|method|build
specifier|public
name|SpanTermQuery
name|build
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|FieldQueryNode
name|fieldQueryNode
init|=
operator|(
name|FieldQueryNode
operator|)
name|node
decl_stmt|;
return|return
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldQueryNode
operator|.
name|getFieldAsString
argument_list|()
argument_list|,
name|fieldQueryNode
operator|.
name|getTextAsString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

