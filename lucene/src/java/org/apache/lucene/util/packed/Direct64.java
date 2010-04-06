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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Direct wrapping of 32 bit values to a backing array of ints.  */
end_comment

begin_class
DECL|class|Direct64
class|class
name|Direct64
extends|extends
name|PackedInts
operator|.
name|ReaderImpl
implements|implements
name|PackedInts
operator|.
name|Mutable
block|{
DECL|field|blocks
specifier|private
name|long
index|[]
name|blocks
decl_stmt|;
DECL|field|BITS_PER_VALUE
specifier|private
specifier|static
specifier|final
name|int
name|BITS_PER_VALUE
init|=
literal|64
decl_stmt|;
DECL|method|Direct64
specifier|public
name|Direct64
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|blocks
operator|=
operator|new
name|long
index|[
name|valueCount
index|]
expr_stmt|;
block|}
DECL|method|Direct64
specifier|public
name|Direct64
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|valueCount
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|long
index|[]
name|blocks
init|=
operator|new
name|long
index|[
name|valueCount
index|]
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
name|blocks
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
comment|/**    * Creates an array backed by the given blocks.    *</p><p>    * Note: The blocks are used directly, so changes to the given block will    * affect the structure.    * @param blocks   used as the internal backing array.    */
DECL|method|Direct64
specifier|public
name|Direct64
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|)
block|{
name|super
argument_list|(
name|blocks
operator|.
name|length
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
name|blocks
index|[
name|index
index|]
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
specifier|final
name|int
name|index
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
name|blocks
index|[
name|index
index|]
operator|=
name|value
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|blocks
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|blocks
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

