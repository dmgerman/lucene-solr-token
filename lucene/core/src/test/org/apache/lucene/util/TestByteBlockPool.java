begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ArrayList
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

begin_class
DECL|class|TestByteBlockPool
specifier|public
class|class
name|TestByteBlockPool
extends|extends
name|LuceneTestCase
block|{
DECL|method|testReadAndWrite
specifier|public
name|void
name|testReadAndWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|Counter
name|bytesUsed
init|=
name|Counter
operator|.
name|newCounter
argument_list|()
decl_stmt|;
name|ByteBlockPool
name|pool
init|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
decl_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
name|boolean
name|reuseFirst
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
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
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|List
argument_list|<
name|BytesRef
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|maxLength
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|ref
init|=
operator|new
name|BytesRefBuilder
argument_list|()
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|value
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|ref
operator|.
name|copyChars
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|pool
operator|.
name|append
argument_list|(
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// verify
name|long
name|position
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|expected
range|:
name|list
control|)
block|{
name|ref
operator|.
name|grow
argument_list|(
name|expected
operator|.
name|length
argument_list|)
expr_stmt|;
name|ref
operator|.
name|setLength
argument_list|(
name|expected
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|pool
operator|.
name|readBytes
argument_list|(
name|position
argument_list|,
name|ref
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|ref
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
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
name|ref
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|ref
operator|.
name|setByteAt
argument_list|(
name|i
argument_list|,
name|pool
operator|.
name|readByte
argument_list|(
name|position
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|position
operator|+=
name|ref
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|pool
operator|.
name|reset
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|reuseFirst
argument_list|)
expr_stmt|;
if|if
condition|(
name|reuseFirst
condition|)
block|{
name|assertEquals
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|,
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
comment|// prepare for next iter
block|}
block|}
block|}
block|}
end_class

end_unit

