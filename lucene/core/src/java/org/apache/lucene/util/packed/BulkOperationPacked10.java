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
DECL|class|BulkOperationPacked10
specifier|final
class|class
name|BulkOperationPacked10
extends|extends
name|BulkOperationPacked
block|{
DECL|method|BulkOperationPacked10
specifier|public
name|BulkOperationPacked10
parameter_list|()
block|{
name|super
argument_list|(
literal|10
argument_list|)
expr_stmt|;
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
name|block0
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
name|block0
operator|>>>
literal|54
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
name|block0
operator|>>>
literal|44
operator|)
operator|&
literal|1023L
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
name|block0
operator|>>>
literal|34
operator|)
operator|&
literal|1023L
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
name|block0
operator|>>>
literal|24
operator|)
operator|&
literal|1023L
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
name|block0
operator|>>>
literal|14
operator|)
operator|&
literal|1023L
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
name|block0
operator|>>>
literal|4
operator|)
operator|&
literal|1023L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block1
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
operator|(
operator|(
name|block0
operator|&
literal|15L
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|block1
operator|>>>
literal|58
operator|)
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
name|block1
operator|>>>
literal|48
operator|)
operator|&
literal|1023L
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
name|block1
operator|>>>
literal|38
operator|)
operator|&
literal|1023L
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
name|block1
operator|>>>
literal|28
operator|)
operator|&
literal|1023L
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
name|block1
operator|>>>
literal|18
operator|)
operator|&
literal|1023L
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
name|block1
operator|>>>
literal|8
operator|)
operator|&
literal|1023L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block2
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
operator|(
operator|(
name|block1
operator|&
literal|255L
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|block2
operator|>>>
literal|62
operator|)
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
name|block2
operator|>>>
literal|52
operator|)
operator|&
literal|1023L
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
name|block2
operator|>>>
literal|42
operator|)
operator|&
literal|1023L
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
name|block2
operator|>>>
literal|32
operator|)
operator|&
literal|1023L
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
name|block2
operator|>>>
literal|22
operator|)
operator|&
literal|1023L
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
name|block2
operator|>>>
literal|12
operator|)
operator|&
literal|1023L
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
name|block2
operator|>>>
literal|2
operator|)
operator|&
literal|1023L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block3
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
operator|(
operator|(
name|block2
operator|&
literal|3L
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block3
operator|>>>
literal|56
operator|)
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
name|block3
operator|>>>
literal|46
operator|)
operator|&
literal|1023L
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
name|block3
operator|>>>
literal|36
operator|)
operator|&
literal|1023L
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
name|block3
operator|>>>
literal|26
operator|)
operator|&
literal|1023L
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
name|block3
operator|>>>
literal|16
operator|)
operator|&
literal|1023L
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
name|block3
operator|>>>
literal|6
operator|)
operator|&
literal|1023L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block4
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
operator|(
operator|(
name|block3
operator|&
literal|63L
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|block4
operator|>>>
literal|60
operator|)
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
name|block4
operator|>>>
literal|50
operator|)
operator|&
literal|1023L
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
name|block4
operator|>>>
literal|40
operator|)
operator|&
literal|1023L
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
name|block4
operator|>>>
literal|30
operator|)
operator|&
literal|1023L
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
name|block4
operator|>>>
literal|20
operator|)
operator|&
literal|1023L
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
name|block4
operator|>>>
literal|10
operator|)
operator|&
literal|1023L
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
name|block4
operator|&
literal|1023L
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte1
operator|>>>
literal|6
operator|)
expr_stmt|;
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte1
operator|&
literal|63
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte2
operator|>>>
literal|4
operator|)
expr_stmt|;
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte2
operator|&
literal|15
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|byte3
operator|>>>
literal|2
operator|)
expr_stmt|;
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte3
operator|&
literal|3
operator|)
operator|<<
literal|8
operator|)
operator||
name|byte4
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
name|block0
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
name|block0
operator|>>>
literal|54
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|44
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|34
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|24
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|14
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|4
operator|)
operator|&
literal|1023L
expr_stmt|;
specifier|final
name|long
name|block1
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
operator|(
operator|(
name|block0
operator|&
literal|15L
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|block1
operator|>>>
literal|58
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|48
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|38
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|28
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|18
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|8
operator|)
operator|&
literal|1023L
expr_stmt|;
specifier|final
name|long
name|block2
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
operator|(
operator|(
name|block1
operator|&
literal|255L
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|block2
operator|>>>
literal|62
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|52
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|42
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|32
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|22
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|12
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|2
operator|)
operator|&
literal|1023L
expr_stmt|;
specifier|final
name|long
name|block3
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
operator|(
operator|(
name|block2
operator|&
literal|3L
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block3
operator|>>>
literal|56
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|46
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|36
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|26
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|16
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|6
operator|)
operator|&
literal|1023L
expr_stmt|;
specifier|final
name|long
name|block4
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
operator|(
operator|(
name|block3
operator|&
literal|63L
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|block4
operator|>>>
literal|60
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|50
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|40
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|30
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|20
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|10
operator|)
operator|&
literal|1023L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block4
operator|&
literal|1023L
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
specifier|final
name|long
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte1
operator|>>>
literal|6
operator|)
expr_stmt|;
specifier|final
name|long
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte1
operator|&
literal|63
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte2
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte2
operator|&
literal|15
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|byte3
operator|>>>
literal|2
operator|)
expr_stmt|;
specifier|final
name|long
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte3
operator|&
literal|3
operator|)
operator|<<
literal|8
operator|)
operator||
name|byte4
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

