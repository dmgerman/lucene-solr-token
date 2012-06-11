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
name|store
operator|.
name|IndexInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/* Reads directly from disk on each get */
end_comment

begin_class
DECL|class|DirectPackedReader
specifier|final
class|class
name|DirectPackedReader
extends|extends
name|PackedInts
operator|.
name|ReaderImpl
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|startPointer
specifier|private
specifier|final
name|long
name|startPointer
decl_stmt|;
DECL|field|BLOCK_BITS
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_BITS
init|=
name|Packed64
operator|.
name|BLOCK_BITS
decl_stmt|;
DECL|field|MOD_MASK
specifier|private
specifier|static
specifier|final
name|int
name|MOD_MASK
init|=
name|Packed64
operator|.
name|MOD_MASK
decl_stmt|;
comment|// masks[n-1] masks for bottom n bits
DECL|field|masks
specifier|private
specifier|final
name|long
index|[]
name|masks
decl_stmt|;
DECL|method|DirectPackedReader
specifier|public
name|DirectPackedReader
parameter_list|(
name|int
name|bitsPerValue
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|long
name|v
init|=
literal|1
decl_stmt|;
name|masks
operator|=
operator|new
name|long
index|[
name|bitsPerValue
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bitsPerValue
condition|;
name|i
operator|++
control|)
block|{
name|v
operator|*=
literal|2
expr_stmt|;
name|masks
index|[
name|i
index|]
operator|=
name|v
operator|-
literal|1
expr_stmt|;
block|}
name|startPointer
operator|=
name|in
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|long
name|majorBitPos
init|=
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
decl_stmt|;
specifier|final
name|int
name|elementPos
init|=
call|(
name|int
call|)
argument_list|(
name|majorBitPos
operator|>>>
name|BLOCK_BITS
argument_list|)
decl_stmt|;
comment|// / BLOCK_SIZE
specifier|final
name|int
name|bitPos
init|=
call|(
name|int
call|)
argument_list|(
name|majorBitPos
operator|&
name|MOD_MASK
argument_list|)
decl_stmt|;
comment|// % BLOCK_SIZE);
specifier|final
name|long
name|result
decl_stmt|;
try|try
block|{
name|in
operator|.
name|seek
argument_list|(
name|startPointer
operator|+
operator|(
name|elementPos
operator|<<
literal|3
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|l1
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bits1
init|=
literal|64
operator|-
name|bitPos
decl_stmt|;
if|if
condition|(
name|bits1
operator|>=
name|bitsPerValue
condition|)
block|{
comment|// not split
name|result
operator|=
name|l1
operator|>>
operator|(
name|bits1
operator|-
name|bitsPerValue
operator|)
operator|&
name|masks
index|[
name|bitsPerValue
operator|-
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|bits2
init|=
name|bitsPerValue
operator|-
name|bits1
decl_stmt|;
specifier|final
name|long
name|result1
init|=
operator|(
name|l1
operator|&
name|masks
index|[
name|bits1
operator|-
literal|1
index|]
operator|)
operator|<<
name|bits2
decl_stmt|;
specifier|final
name|long
name|l2
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|result2
init|=
name|l2
operator|>>
operator|(
literal|64
operator|-
name|bits2
operator|)
operator|&
name|masks
index|[
name|bits2
operator|-
literal|1
index|]
decl_stmt|;
name|result
operator|=
name|result1
operator||
name|result2
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

