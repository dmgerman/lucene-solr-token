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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Testcase for {@link RecyclingByteBlockAllocator}  */
end_comment

begin_class
DECL|class|TestRecyclingByteBlockAllocator
specifier|public
class|class
name|TestRecyclingByteBlockAllocator
extends|extends
name|LuceneTestCase
block|{
comment|/**    */
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|newAllocator
specifier|private
name|RecyclingByteBlockAllocator
name|newAllocator
parameter_list|()
block|{
return|return
operator|new
name|RecyclingByteBlockAllocator
argument_list|(
literal|1
operator|<<
operator|(
literal|2
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
operator|)
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|97
argument_list|)
argument_list|,
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testAllocate
specifier|public
name|void
name|testAllocate
parameter_list|()
block|{
name|RecyclingByteBlockAllocator
name|allocator
init|=
name|newAllocator
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|block
operator|.
name|length
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|97
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|block
operator|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|block
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"block is returned twice"
argument_list|,
name|set
operator|.
name|add
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
operator|*
operator|(
name|i
operator|+
literal|2
operator|)
argument_list|,
name|allocator
operator|.
name|bytesUsed
argument_list|()
argument_list|)
expr_stmt|;
comment|// zero based + 1
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|allocator
operator|.
name|numBufferedBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocateAndRecycle
specifier|public
name|void
name|testAllocateAndRecycle
parameter_list|()
block|{
name|RecyclingByteBlockAllocator
name|allocator
init|=
name|newAllocator
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
name|allocated
init|=
operator|new
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
decl_stmt|;
name|allocated
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|block
operator|.
name|length
decl_stmt|;
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|97
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|int
name|num
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|39
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
name|block
operator|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|block
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"block is returned twice"
argument_list|,
name|allocated
operator|.
name|add
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
operator|*
operator|(
name|allocated
operator|.
name|size
argument_list|()
operator|+
name|allocator
operator|.
name|numBufferedBlocks
argument_list|()
operator|)
argument_list|,
name|allocator
operator|.
name|bytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
index|[]
name|array
init|=
name|allocated
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
index|[]
argument_list|)
decl_stmt|;
name|int
name|begin
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|begin
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|array
operator|.
name|length
operator|-
name|begin
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|selected
init|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|begin
init|;
name|j
operator|<
name|end
condition|;
name|j
operator|++
control|)
block|{
name|selected
operator|.
name|add
argument_list|(
name|array
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|allocator
operator|.
name|recycleByteBlocks
argument_list|(
name|array
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
name|begin
init|;
name|j
operator|<
name|end
condition|;
name|j
operator|++
control|)
block|{
name|assertNull
argument_list|(
name|array
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b
init|=
name|selected
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|allocated
operator|.
name|remove
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocateAndFree
specifier|public
name|void
name|testAllocateAndFree
parameter_list|()
block|{
name|RecyclingByteBlockAllocator
name|allocator
init|=
name|newAllocator
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
name|allocated
init|=
operator|new
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|freeButAllocated
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
decl_stmt|;
name|allocated
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|block
operator|.
name|length
decl_stmt|;
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|97
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|int
name|num
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|39
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
name|block
operator|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
expr_stmt|;
name|freeButAllocated
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|freeButAllocated
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|block
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"block is returned twice"
argument_list|,
name|allocated
operator|.
name|add
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
operator|*
operator|(
name|allocated
operator|.
name|size
argument_list|()
operator|+
name|allocator
operator|.
name|numBufferedBlocks
argument_list|()
operator|)
argument_list|,
name|allocator
operator|.
name|bytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
index|[]
name|array
init|=
name|allocated
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
index|[]
argument_list|)
decl_stmt|;
name|int
name|begin
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|begin
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|array
operator|.
name|length
operator|-
name|begin
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|begin
init|;
name|j
operator|<
name|end
condition|;
name|j
operator|++
control|)
block|{
name|byte
index|[]
name|b
init|=
name|array
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|allocated
operator|.
name|remove
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|allocator
operator|.
name|recycleByteBlocks
argument_list|(
name|array
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
name|begin
init|;
name|j
operator|<
name|end
condition|;
name|j
operator|++
control|)
block|{
name|assertNull
argument_list|(
name|array
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
comment|// randomly free blocks
name|int
name|numFreeBlocks
init|=
name|allocator
operator|.
name|numBufferedBlocks
argument_list|()
decl_stmt|;
name|int
name|freeBlocks
init|=
name|allocator
operator|.
name|freeBlocks
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
operator|+
name|allocator
operator|.
name|maxBufferedBlocks
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|allocator
operator|.
name|numBufferedBlocks
argument_list|()
argument_list|,
name|numFreeBlocks
operator|-
name|freeBlocks
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

