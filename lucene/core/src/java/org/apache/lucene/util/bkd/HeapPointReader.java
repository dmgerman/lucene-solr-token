begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
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

begin_class
DECL|class|HeapPointReader
specifier|final
class|class
name|HeapPointReader
extends|extends
name|PointReader
block|{
DECL|field|curRead
specifier|private
name|int
name|curRead
decl_stmt|;
DECL|field|blocks
specifier|final
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
decl_stmt|;
DECL|field|valuesPerBlock
specifier|final
name|int
name|valuesPerBlock
decl_stmt|;
DECL|field|packedBytesLength
specifier|final
name|int
name|packedBytesLength
decl_stmt|;
DECL|field|ordsLong
specifier|final
name|long
index|[]
name|ordsLong
decl_stmt|;
DECL|field|ords
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|docIDs
specifier|final
name|int
index|[]
name|docIDs
decl_stmt|;
DECL|field|end
specifier|final
name|int
name|end
decl_stmt|;
DECL|field|scratch
specifier|final
name|byte
index|[]
name|scratch
decl_stmt|;
DECL|method|HeapPointReader
name|HeapPointReader
parameter_list|(
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
parameter_list|,
name|int
name|valuesPerBlock
parameter_list|,
name|int
name|packedBytesLength
parameter_list|,
name|int
index|[]
name|ords
parameter_list|,
name|long
index|[]
name|ordsLong
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|valuesPerBlock
operator|=
name|valuesPerBlock
expr_stmt|;
name|this
operator|.
name|ords
operator|=
name|ords
expr_stmt|;
name|this
operator|.
name|ordsLong
operator|=
name|ordsLong
expr_stmt|;
name|this
operator|.
name|docIDs
operator|=
name|docIDs
expr_stmt|;
name|curRead
operator|=
name|start
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|packedBytesLength
operator|=
name|packedBytesLength
expr_stmt|;
name|scratch
operator|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
expr_stmt|;
block|}
DECL|method|writePackedValue
name|void
name|writePackedValue
parameter_list|(
name|int
name|index
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|int
name|block
init|=
name|index
operator|/
name|valuesPerBlock
decl_stmt|;
name|int
name|blockIndex
init|=
name|index
operator|%
name|valuesPerBlock
decl_stmt|;
while|while
condition|(
name|blocks
operator|.
name|size
argument_list|()
operator|<=
name|block
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
operator|new
name|byte
index|[
name|valuesPerBlock
operator|*
name|packedBytesLength
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
argument_list|,
name|blockIndex
operator|*
name|packedBytesLength
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
block|}
DECL|method|readPackedValue
name|void
name|readPackedValue
parameter_list|(
name|int
name|index
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|int
name|block
init|=
name|index
operator|/
name|valuesPerBlock
decl_stmt|;
name|int
name|blockIndex
init|=
name|index
operator|%
name|valuesPerBlock
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
name|block
argument_list|)
argument_list|,
name|blockIndex
operator|*
name|packedBytesLength
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
block|{
name|curRead
operator|++
expr_stmt|;
return|return
name|curRead
operator|<
name|end
return|;
block|}
annotation|@
name|Override
DECL|method|packedValue
specifier|public
name|byte
index|[]
name|packedValue
parameter_list|()
block|{
name|readPackedValue
argument_list|(
name|curRead
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
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
name|docIDs
index|[
name|curRead
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
if|if
condition|(
name|ordsLong
operator|!=
literal|null
condition|)
block|{
return|return
name|ordsLong
index|[
name|curRead
index|]
return|;
block|}
else|else
block|{
return|return
name|ords
index|[
name|curRead
index|]
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
block|}
end_class

end_unit

