begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**       * Implements {@link PackedInts.Mutable}, but grows the  * bit count of the underlying packed ints on-demand.  *  *<p>@lucene.internal</p>  */
end_comment

begin_class
DECL|class|GrowableWriter
specifier|public
class|class
name|GrowableWriter
implements|implements
name|PackedInts
operator|.
name|Mutable
block|{
DECL|field|currentMaxValue
specifier|private
name|long
name|currentMaxValue
decl_stmt|;
DECL|field|current
specifier|private
name|PackedInts
operator|.
name|Mutable
name|current
decl_stmt|;
DECL|field|acceptableOverheadRatio
specifier|private
specifier|final
name|float
name|acceptableOverheadRatio
decl_stmt|;
DECL|method|GrowableWriter
specifier|public
name|GrowableWriter
parameter_list|(
name|int
name|startBitsPerValue
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|this
operator|.
name|acceptableOverheadRatio
operator|=
name|acceptableOverheadRatio
expr_stmt|;
name|current
operator|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|valueCount
argument_list|,
name|startBitsPerValue
argument_list|,
name|this
operator|.
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|currentMaxValue
operator|=
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|current
operator|.
name|getBitsPerValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|current
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|current
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getBitsPerValue
specifier|public
name|int
name|getBitsPerValue
parameter_list|()
block|{
return|return
name|current
operator|.
name|getBitsPerValue
argument_list|()
return|;
block|}
DECL|method|getMutable
specifier|public
name|PackedInts
operator|.
name|Mutable
name|getMutable
parameter_list|()
block|{
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|current
operator|.
name|getArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
name|current
operator|.
name|hasArray
argument_list|()
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>=
name|currentMaxValue
condition|)
block|{
name|int
name|bpv
init|=
name|getBitsPerValue
argument_list|()
decl_stmt|;
while|while
condition|(
name|currentMaxValue
operator|<=
name|value
operator|&&
name|currentMaxValue
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|bpv
operator|++
expr_stmt|;
name|currentMaxValue
operator|*=
literal|2
expr_stmt|;
block|}
specifier|final
name|int
name|valueCount
init|=
name|size
argument_list|()
decl_stmt|;
name|PackedInts
operator|.
name|Mutable
name|next
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|valueCount
argument_list|,
name|bpv
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|current
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|current
operator|=
name|next
expr_stmt|;
name|currentMaxValue
operator|=
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|current
operator|.
name|getBitsPerValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|current
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|current
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|resize
specifier|public
name|GrowableWriter
name|resize
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
name|GrowableWriter
name|next
init|=
operator|new
name|GrowableWriter
argument_list|(
name|getBitsPerValue
argument_list|()
argument_list|,
name|newSize
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|()
argument_list|,
name|newSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
block|}
end_class

end_unit

