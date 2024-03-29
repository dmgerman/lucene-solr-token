begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
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
name|processors
package|;
end_package

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
name|TermRangeQuery
import|;
end_import

begin_comment
comment|// javadocs
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
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|util
operator|.
name|UnescapedCharSequence
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
name|nodes
operator|.
name|TermRangeQueryNode
import|;
end_import

begin_comment
comment|/**  * Processes {@link TermRangeQuery}s with open ranges.  */
end_comment

begin_class
DECL|class|OpenRangeQueryNodeProcessor
specifier|public
class|class
name|OpenRangeQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|field|OPEN_RANGE_TOKEN
specifier|final
specifier|public
specifier|static
name|String
name|OPEN_RANGE_TOKEN
init|=
literal|"*"
decl_stmt|;
DECL|method|OpenRangeQueryNodeProcessor
specifier|public
name|OpenRangeQueryNodeProcessor
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|TermRangeQueryNode
condition|)
block|{
name|TermRangeQueryNode
name|rangeNode
init|=
operator|(
name|TermRangeQueryNode
operator|)
name|node
decl_stmt|;
name|FieldQueryNode
name|lowerNode
init|=
name|rangeNode
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
name|FieldQueryNode
name|upperNode
init|=
name|rangeNode
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
name|CharSequence
name|lowerText
init|=
name|lowerNode
operator|.
name|getText
argument_list|()
decl_stmt|;
name|CharSequence
name|upperText
init|=
name|upperNode
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|OPEN_RANGE_TOKEN
operator|.
name|equals
argument_list|(
name|upperNode
operator|.
name|getTextAsString
argument_list|()
argument_list|)
operator|&&
operator|(
operator|!
operator|(
name|upperText
operator|instanceof
name|UnescapedCharSequence
operator|)
operator|||
operator|!
operator|(
operator|(
name|UnescapedCharSequence
operator|)
name|upperText
operator|)
operator|.
name|wasEscaped
argument_list|(
literal|0
argument_list|)
operator|)
condition|)
block|{
name|upperText
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|OPEN_RANGE_TOKEN
operator|.
name|equals
argument_list|(
name|lowerNode
operator|.
name|getTextAsString
argument_list|()
argument_list|)
operator|&&
operator|(
operator|!
operator|(
name|lowerText
operator|instanceof
name|UnescapedCharSequence
operator|)
operator|||
operator|!
operator|(
operator|(
name|UnescapedCharSequence
operator|)
name|lowerText
operator|)
operator|.
name|wasEscaped
argument_list|(
literal|0
argument_list|)
operator|)
condition|)
block|{
name|lowerText
operator|=
literal|""
expr_stmt|;
block|}
name|lowerNode
operator|.
name|setText
argument_list|(
name|lowerText
argument_list|)
expr_stmt|;
name|upperNode
operator|.
name|setText
argument_list|(
name|upperText
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

