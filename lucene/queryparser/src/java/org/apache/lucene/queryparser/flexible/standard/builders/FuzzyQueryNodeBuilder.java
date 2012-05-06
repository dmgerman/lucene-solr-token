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
name|nodes
operator|.
name|FuzzyQueryNode
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
name|FuzzyQuery
import|;
end_import

begin_comment
comment|/**  * Builds a {@link FuzzyQuery} object from a {@link FuzzyQueryNode} object.  */
end_comment

begin_class
DECL|class|FuzzyQueryNodeBuilder
specifier|public
class|class
name|FuzzyQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|FuzzyQueryNodeBuilder
specifier|public
name|FuzzyQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|FuzzyQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|FuzzyQueryNode
name|fuzzyNode
init|=
operator|(
name|FuzzyQueryNode
operator|)
name|queryNode
decl_stmt|;
name|String
name|text
init|=
name|fuzzyNode
operator|.
name|getTextAsString
argument_list|()
decl_stmt|;
name|int
name|numEdits
init|=
name|FuzzyQuery
operator|.
name|floatToEdits
argument_list|(
name|fuzzyNode
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|text
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fuzzyNode
operator|.
name|getFieldAsString
argument_list|()
argument_list|,
name|fuzzyNode
operator|.
name|getTextAsString
argument_list|()
argument_list|)
argument_list|,
name|numEdits
argument_list|,
name|fuzzyNode
operator|.
name|getPrefixLength
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

