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
DECL|class|BulkOperationPackedSingleBlock2
specifier|final
class|class
name|BulkOperationPackedSingleBlock2
extends|extends
name|BulkOperation
block|{
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
DECL|method|valueCount
specifier|public
name|int
name|valueCount
parameter_list|()
block|{
return|return
literal|32
return|;
block|}
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
literal|3L
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
literal|2
operator|)
operator|&
literal|3L
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
literal|4
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|8
operator|)
operator|&
literal|3L
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
literal|10
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|14
operator|)
operator|&
literal|3L
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
literal|16
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|20
operator|)
operator|&
literal|3L
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
literal|22
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|26
operator|)
operator|&
literal|3L
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
literal|28
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|32
operator|)
operator|&
literal|3L
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
literal|34
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|38
operator|)
operator|&
literal|3L
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
literal|40
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|44
operator|)
operator|&
literal|3L
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
literal|46
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|50
operator|)
operator|&
literal|3L
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
literal|52
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|56
operator|)
operator|&
literal|3L
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
literal|58
operator|)
operator|&
literal|3L
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
literal|60
operator|)
operator|&
literal|3L
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
literal|62
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte1
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte1
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|&
literal|3
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
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte4
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte4
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|&
literal|3
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
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte6
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte6
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte7
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte7
operator|>>>
literal|6
expr_stmt|;
block|}
block|}
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
literal|3L
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
literal|2
operator|)
operator|&
literal|3L
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
literal|4
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|8
operator|)
operator|&
literal|3L
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
literal|10
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|14
operator|)
operator|&
literal|3L
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
literal|16
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|20
operator|)
operator|&
literal|3L
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
literal|22
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|26
operator|)
operator|&
literal|3L
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
literal|28
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|32
operator|)
operator|&
literal|3L
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
literal|34
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|38
operator|)
operator|&
literal|3L
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
literal|40
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|44
operator|)
operator|&
literal|3L
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
literal|46
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|50
operator|)
operator|&
literal|3L
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
literal|52
operator|)
operator|&
literal|3L
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
literal|3L
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
literal|56
operator|)
operator|&
literal|3L
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
literal|58
operator|)
operator|&
literal|3L
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
literal|60
operator|)
operator|&
literal|3L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|>>>
literal|62
expr_stmt|;
block|}
block|}
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
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte1
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte1
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|&
literal|3
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
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte4
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte4
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|&
literal|3
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
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte6
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte6
operator|>>>
literal|6
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte7
operator|&
literal|3
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
literal|2
operator|)
operator|&
literal|3
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
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte7
operator|>>>
literal|6
expr_stmt|;
block|}
block|}
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
literal|2
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
literal|4
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
literal|8
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
literal|10
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
literal|14
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
literal|16
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
literal|20
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
literal|22
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
literal|26
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
literal|28
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
literal|32
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
literal|34
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
literal|38
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
literal|40
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
literal|44
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
literal|46
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
literal|50
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
literal|52
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
literal|56
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
literal|58
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
literal|62
operator|)
expr_stmt|;
block|}
block|}
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
literal|2
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|4
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
literal|8
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|10
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
literal|14
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|16
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
literal|20
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|22
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
literal|26
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|28
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
literal|32
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|34
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
literal|38
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|40
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
literal|44
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|46
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
literal|50
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|52
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
literal|56
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|58
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
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|62
operator|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

