begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

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
name|config
operator|.
name|QueryConfigHandler
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
name|FieldableNode
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
name|GroupQueryNode
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
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
import|;
end_import

begin_comment
comment|/**  * This processor is used to expand terms so the query looks for the same term  * in different fields. It also boosts a query based on its field.<br/>  *<br/>  * This processor looks for every {@link FieldableNode} contained in the query  * node tree. If a {@link FieldableNode} is found, it checks if there is a  * {@link ConfigurationKeys#MULTI_FIELDS} defined in the {@link QueryConfigHandler}. If  * there is, the {@link FieldableNode} is cloned N times and the clones are  * added to a {@link BooleanQueryNode} together with the original node. N is  * defined by the number of fields that it will be expanded to. The  * {@link BooleanQueryNode} is returned.<br/>  *   * @see ConfigurationKeys#MULTI_FIELDS  */
end_comment

begin_class
DECL|class|MultiFieldQueryNodeProcessor
specifier|public
class|class
name|MultiFieldQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|field|processChildren
specifier|private
name|boolean
name|processChildren
init|=
literal|true
decl_stmt|;
DECL|method|MultiFieldQueryNodeProcessor
specifier|public
name|MultiFieldQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
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
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|processChildren
specifier|protected
name|void
name|processChildren
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|this
operator|.
name|processChildren
condition|)
block|{
name|super
operator|.
name|processChildren
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|processChildren
operator|=
literal|true
expr_stmt|;
block|}
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
if|if
condition|(
name|node
operator|instanceof
name|FieldableNode
condition|)
block|{
name|this
operator|.
name|processChildren
operator|=
literal|false
expr_stmt|;
name|FieldableNode
name|fieldNode
init|=
operator|(
name|FieldableNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|fieldNode
operator|.
name|getField
argument_list|()
operator|==
literal|null
condition|)
block|{
name|CharSequence
index|[]
name|fields
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|MULTI_FIELDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS should be set on the QueryConfigHandler"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|fieldNode
operator|.
name|setField
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|fieldNode
return|;
block|}
else|else
block|{
name|LinkedList
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
operator|new
name|LinkedList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|fieldNode
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|fieldNode
operator|=
operator|(
name|FieldableNode
operator|)
name|fieldNode
operator|.
name|cloneTree
argument_list|()
expr_stmt|;
name|fieldNode
operator|.
name|setField
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|fieldNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
comment|// should never happen
block|}
block|}
return|return
operator|new
name|GroupQueryNode
argument_list|(
operator|new
name|BooleanQueryNode
argument_list|(
name|children
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
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

