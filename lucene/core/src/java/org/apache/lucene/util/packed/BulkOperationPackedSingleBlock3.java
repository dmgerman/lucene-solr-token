begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
end_comment

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

begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment

begin_class
DECL|class|BulkOperationPackedSingleBlock3
specifier|final
class|class
name|BulkOperationPackedSingleBlock3
extends|extends
name|BulkOperation
block|{
annotation|@
name|Override
DECL|method|blockCount
specifier|public
name|int
name|blockCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|valueCount
specifier|public
name|int
name|valueCount
parameter_list|()
block|{
return|return
literal|21
return|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|block
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|3
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|6
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|9
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|12
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|15
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|18
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|21
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|24
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|27
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|30
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|33
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|36
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|39
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|42
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|45
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|48
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|51
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|54
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|57
operator|)
operator|&
literal|7L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|block
operator|>>>
literal|60
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|>>>
literal|3
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte1
operator|&
literal|1
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|1
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|4
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|7
operator|)
operator||
operator|(
operator|(
name|byte2
operator|&
literal|3
operator|)
operator|<<
literal|1
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte2
operator|>>>
literal|2
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|>>>
literal|5
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte3
operator|>>>
literal|3
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte3
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte4
operator|&
literal|1
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|1
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|4
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|7
operator|)
operator||
operator|(
operator|(
name|byte5
operator|&
literal|3
operator|)
operator|<<
literal|1
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte5
operator|>>>
literal|2
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|>>>
literal|5
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte6
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte6
operator|>>>
literal|3
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte6
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte7
operator|&
literal|1
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte7
operator|>>>
literal|1
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte7
operator|>>>
literal|4
operator|)
operator|&
literal|7
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|3
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|6
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|9
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|12
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|15
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|18
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|21
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|24
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|27
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|30
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|33
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|36
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|39
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|42
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|45
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|48
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|51
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|54
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|57
operator|)
operator|&
literal|7L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|>>>
literal|60
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|>>>
literal|3
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte1
operator|&
literal|1
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|1
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|4
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|7
operator|)
operator||
operator|(
operator|(
name|byte2
operator|&
literal|3
operator|)
operator|<<
literal|1
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte2
operator|>>>
literal|2
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|>>>
literal|5
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte3
operator|>>>
literal|3
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte3
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte4
operator|&
literal|1
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|1
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|4
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|7
operator|)
operator||
operator|(
operator|(
name|byte5
operator|&
literal|3
operator|)
operator|<<
literal|1
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte5
operator|>>>
literal|2
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|>>>
literal|5
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte6
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte6
operator|>>>
literal|3
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte6
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte7
operator|&
literal|1
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte7
operator|>>>
literal|1
operator|)
operator|&
literal|7
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte7
operator|>>>
literal|4
operator|)
operator|&
literal|7
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|3
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|9
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|15
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|18
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|21
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|27
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|30
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|33
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|36
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|39
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|42
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|45
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|51
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|54
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|57
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|60
operator|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
name|values
index|[
name|valuesOffset
operator|++
index|]
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|3
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|6
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|9
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|12
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|15
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|18
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|21
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|24
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|27
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|30
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|33
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|36
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|39
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|42
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|45
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|48
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|51
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|54
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|57
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|60
operator|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

