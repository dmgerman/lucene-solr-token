begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.tst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|tst
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
name|util
operator|.
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * The class creates a TST node.  */
end_comment

begin_class
DECL|class|TernaryTreeNode
specifier|public
class|class
name|TernaryTreeNode
block|{
comment|/** Creates a new empty node */
DECL|method|TernaryTreeNode
specifier|public
name|TernaryTreeNode
parameter_list|()
block|{}
comment|/** the character stored by a node. */
DECL|field|splitchar
name|char
name|splitchar
decl_stmt|;
comment|/** a reference object to the node containing character smaller than this node's character. */
DECL|field|loKid
name|TernaryTreeNode
name|loKid
decl_stmt|;
comment|/**     *  a reference object to the node containing character next to this node's character as     *  occurring in the inserted token.    */
DECL|field|eqKid
name|TernaryTreeNode
name|eqKid
decl_stmt|;
comment|/** a reference object to the node containing character higher than this node's character. */
DECL|field|hiKid
name|TernaryTreeNode
name|hiKid
decl_stmt|;
comment|/**     * used by leaf nodes to store the complete tokens to be added to suggest list while     * auto-completing the prefix.    */
DECL|field|token
name|String
name|token
decl_stmt|;
DECL|field|val
name|Object
name|val
decl_stmt|;
DECL|method|sizeInBytes
name|long
name|sizeInBytes
parameter_list|()
block|{
name|long
name|mem
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|loKid
operator|!=
literal|null
condition|)
block|{
name|mem
operator|+=
name|loKid
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|eqKid
operator|!=
literal|null
condition|)
block|{
name|mem
operator|+=
name|eqKid
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|hiKid
operator|!=
literal|null
condition|)
block|{
name|mem
operator|+=
name|hiKid
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|mem
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|token
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
operator|*
name|token
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|mem
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|mem
return|;
block|}
block|}
end_class

end_unit

