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
comment|/**  * A {@link DocIdSetIterator} which iterates over set bits in a  * bit set.  * @lucene.internal  */
end_comment

begin_class
DECL|class|BitSetIterator
specifier|public
class|class
name|BitSetIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|method|getBitSet
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|BitSet
parameter_list|>
name|T
name|getBitSet
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|iterator
operator|instanceof
name|BitSetIterator
condition|)
block|{
name|BitSet
name|bits
init|=
operator|(
operator|(
name|BitSetIterator
operator|)
name|iterator
operator|)
operator|.
name|bits
decl_stmt|;
assert|assert
name|bits
operator|!=
literal|null
assert|;
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|bits
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|bits
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** If the provided iterator wraps a {@link FixedBitSet}, returns it, otherwise returns null. */
DECL|method|getFixedBitSetOrNull
specifier|public
specifier|static
name|FixedBitSet
name|getFixedBitSetOrNull
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|)
block|{
return|return
name|getBitSet
argument_list|(
name|iterator
argument_list|,
name|FixedBitSet
operator|.
name|class
argument_list|)
return|;
block|}
comment|/** If the provided iterator wraps a {@link SparseFixedBitSet}, returns it, otherwise returns null. */
DECL|method|getSparseFixedBitSetOrNull
specifier|public
specifier|static
name|SparseFixedBitSet
name|getSparseFixedBitSetOrNull
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|)
block|{
return|return
name|getBitSet
argument_list|(
name|iterator
argument_list|,
name|SparseFixedBitSet
operator|.
name|class
argument_list|)
return|;
block|}
DECL|field|bits
specifier|private
specifier|final
name|BitSet
name|bits
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|BitSetIterator
specifier|public
name|BitSetIterator
parameter_list|(
name|BitSet
name|bits
parameter_list|,
name|long
name|cost
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|bits
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|cost
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
name|doc
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
return|return
name|advance
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
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
if|if
condition|(
name|target
operator|>=
name|length
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|doc
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
block|}
end_class

end_unit
