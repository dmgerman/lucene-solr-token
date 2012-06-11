begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.nodes
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
name|nodes
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
name|nodes
operator|.
name|ModifierQueryNode
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
name|processors
operator|.
name|GroupQueryNodeProcessor
import|;
end_import

begin_comment
comment|/**  * A {@link BooleanModifierNode} has the same behaviour as  * {@link ModifierQueryNode}, it only indicates that this modifier was added by  * {@link GroupQueryNodeProcessor} and not by the user.<br/>  *   * @see ModifierQueryNode  */
end_comment

begin_class
DECL|class|BooleanModifierNode
specifier|public
class|class
name|BooleanModifierNode
extends|extends
name|ModifierQueryNode
block|{
DECL|method|BooleanModifierNode
specifier|public
name|BooleanModifierNode
parameter_list|(
name|QueryNode
name|node
parameter_list|,
name|Modifier
name|mod
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|,
name|mod
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

