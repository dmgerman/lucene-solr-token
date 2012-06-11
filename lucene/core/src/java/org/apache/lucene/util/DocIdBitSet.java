begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|BitSet
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
name|DocIdSet
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
name|DocIdSetIterator
import|;
end_import

begin_comment
comment|/** Simple DocIdSet and DocIdSetIterator backed by a BitSet */
end_comment

begin_class
DECL|class|DocIdBitSet
specifier|public
class|class
name|DocIdBitSet
extends|extends
name|DocIdSet
implements|implements
name|Bits
block|{
DECL|field|bitSet
specifier|private
specifier|final
name|BitSet
name|bitSet
decl_stmt|;
DECL|method|DocIdBitSet
specifier|public
name|DocIdBitSet
parameter_list|(
name|BitSet
name|bitSet
parameter_list|)
block|{
name|this
operator|.
name|bitSet
operator|=
name|bitSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdBitSetIterator
argument_list|(
name|bitSet
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|Bits
name|bits
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/** This DocIdSet implementation is cacheable. */
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Returns the underlying BitSet.     */
DECL|method|getBitSet
specifier|public
name|BitSet
name|getBitSet
parameter_list|()
block|{
return|return
name|this
operator|.
name|bitSet
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|bitSet
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
comment|// the size may not be correct...
return|return
name|bitSet
operator|.
name|size
argument_list|()
return|;
block|}
DECL|class|DocIdBitSetIterator
specifier|private
specifier|static
class|class
name|DocIdBitSetIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|docId
specifier|private
name|int
name|docId
decl_stmt|;
DECL|field|bitSet
specifier|private
name|BitSet
name|bitSet
decl_stmt|;
DECL|method|DocIdBitSetIterator
name|DocIdBitSetIterator
parameter_list|(
name|BitSet
name|bitSet
parameter_list|)
block|{
name|this
operator|.
name|bitSet
operator|=
name|bitSet
expr_stmt|;
name|this
operator|.
name|docId
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docId
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
comment|// (docId + 1) on next line requires -1 initial value for docNr:
name|int
name|d
init|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
name|docId
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// -1 returned by BitSet.nextSetBit() when exhausted
name|docId
operator|=
name|d
operator|==
operator|-
literal|1
condition|?
name|NO_MORE_DOCS
else|:
name|d
expr_stmt|;
return|return
name|docId
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
name|int
name|d
init|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
name|target
argument_list|)
decl_stmt|;
comment|// -1 returned by BitSet.nextSetBit() when exhausted
name|docId
operator|=
name|d
operator|==
operator|-
literal|1
condition|?
name|NO_MORE_DOCS
else|:
name|d
expr_stmt|;
return|return
name|docId
return|;
block|}
block|}
block|}
end_class

end_unit

