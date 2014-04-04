begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|index
operator|.
name|IndexReader
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
name|SimpleCollector
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
name|util
operator|.
name|FixedBitSet
import|;
end_import

begin_comment
comment|/**  * This collector specializes in collecting the most relevant document (group head) for each group that match the query.  *  * @lucene.experimental  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|AbstractAllGroupHeadsCollector
specifier|public
specifier|abstract
class|class
name|AbstractAllGroupHeadsCollector
parameter_list|<
name|GH
extends|extends
name|AbstractAllGroupHeadsCollector
operator|.
name|GroupHead
parameter_list|>
extends|extends
name|SimpleCollector
block|{
DECL|field|reversed
specifier|protected
specifier|final
name|int
index|[]
name|reversed
decl_stmt|;
DECL|field|compIDXEnd
specifier|protected
specifier|final
name|int
name|compIDXEnd
decl_stmt|;
DECL|field|temporalResult
specifier|protected
specifier|final
name|TemporalResult
name|temporalResult
decl_stmt|;
DECL|method|AbstractAllGroupHeadsCollector
specifier|protected
name|AbstractAllGroupHeadsCollector
parameter_list|(
name|int
name|numberOfSorts
parameter_list|)
block|{
name|this
operator|.
name|reversed
operator|=
operator|new
name|int
index|[
name|numberOfSorts
index|]
expr_stmt|;
name|this
operator|.
name|compIDXEnd
operator|=
name|numberOfSorts
operator|-
literal|1
expr_stmt|;
name|temporalResult
operator|=
operator|new
name|TemporalResult
argument_list|()
expr_stmt|;
block|}
comment|/**    * @param maxDoc The maxDoc of the top level {@link IndexReader}.    * @return a {@link FixedBitSet} containing all group heads.    */
DECL|method|retrieveGroupHeads
specifier|public
name|FixedBitSet
name|retrieveGroupHeads
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|FixedBitSet
name|bitSet
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|GH
argument_list|>
name|groupHeads
init|=
name|getCollectedGroupHeads
argument_list|()
decl_stmt|;
for|for
control|(
name|GroupHead
name|groupHead
range|:
name|groupHeads
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|groupHead
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|bitSet
return|;
block|}
comment|/**    * @return an int array containing all group heads. The size of the array is equal to number of collected unique groups.    */
DECL|method|retrieveGroupHeads
specifier|public
name|int
index|[]
name|retrieveGroupHeads
parameter_list|()
block|{
name|Collection
argument_list|<
name|GH
argument_list|>
name|groupHeads
init|=
name|getCollectedGroupHeads
argument_list|()
decl_stmt|;
name|int
index|[]
name|docHeads
init|=
operator|new
name|int
index|[
name|groupHeads
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|GroupHead
name|groupHead
range|:
name|groupHeads
control|)
block|{
name|docHeads
index|[
name|i
operator|++
index|]
operator|=
name|groupHead
operator|.
name|doc
expr_stmt|;
block|}
return|return
name|docHeads
return|;
block|}
comment|/**    * @return the number of group heads found for a query.    */
DECL|method|groupHeadsSize
specifier|public
name|int
name|groupHeadsSize
parameter_list|()
block|{
return|return
name|getCollectedGroupHeads
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns the group head and puts it into {@link #temporalResult}.    * If the group head wasn't encountered before then it will be added to the collected group heads.    *<p/>    * The {@link TemporalResult#stop} property will be<code>true</code> if the group head wasn't encountered before    * otherwise<code>false</code>.    *    * @param doc The document to retrieve the group head for.    * @throws IOException If I/O related errors occur    */
DECL|method|retrieveGroupHeadAndAddIfNotExist
specifier|protected
specifier|abstract
name|void
name|retrieveGroupHeadAndAddIfNotExist
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the collected group heads.    * Subsequent calls should return the same group heads.    *    * @return the collected group heads    */
DECL|method|getCollectedGroupHeads
specifier|protected
specifier|abstract
name|Collection
argument_list|<
name|GH
argument_list|>
name|getCollectedGroupHeads
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|retrieveGroupHeadAndAddIfNotExist
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|temporalResult
operator|.
name|stop
condition|)
block|{
return|return;
block|}
name|GH
name|groupHead
init|=
name|temporalResult
operator|.
name|groupHead
decl_stmt|;
comment|// Ok now we need to check if the current doc is more relevant then current doc for this group
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|groupHead
operator|.
name|compare
argument_list|(
name|compIDX
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// Definitely not competitive. So don't even bother to continue
return|return;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|compIDXEnd
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return;
block|}
block|}
name|groupHead
operator|.
name|updateDocHead
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Contains the result of group head retrieval.    * To prevent new object creations of this class for every collect.    */
DECL|class|TemporalResult
specifier|protected
class|class
name|TemporalResult
block|{
DECL|field|groupHead
specifier|public
name|GH
name|groupHead
decl_stmt|;
DECL|field|stop
specifier|public
name|boolean
name|stop
decl_stmt|;
block|}
comment|/**    * Represents a group head. A group head is the most relevant document for a particular group.    * The relevancy is based is usually based on the sort.    *    * The group head contains a group value with its associated most relevant document id.    */
DECL|class|GroupHead
specifier|public
specifier|static
specifier|abstract
class|class
name|GroupHead
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
block|{
DECL|field|groupValue
specifier|public
specifier|final
name|GROUP_VALUE_TYPE
name|groupValue
decl_stmt|;
DECL|field|doc
specifier|public
name|int
name|doc
decl_stmt|;
DECL|method|GroupHead
specifier|protected
name|GroupHead
parameter_list|(
name|GROUP_VALUE_TYPE
name|groupValue
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
comment|/**      * Compares the specified document for a specified comparator against the current most relevant document.      *      * @param compIDX The comparator index of the specified comparator.      * @param doc The specified document.      * @return -1 if the specified document wasn't competitive against the current most relevant document, 1 if the      *         specified document was competitive against the current most relevant document. Otherwise 0.      * @throws IOException If I/O related errors occur      */
DECL|method|compare
specifier|protected
specifier|abstract
name|int
name|compare
parameter_list|(
name|int
name|compIDX
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Updates the current most relevant document with the specified document.      *      * @param doc The specified document      * @throws IOException If I/O related errors occur      */
DECL|method|updateDocHead
specifier|protected
specifier|abstract
name|void
name|updateDocHead
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

