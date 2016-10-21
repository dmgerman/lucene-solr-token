begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|BLOCK_SIZE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
operator|.
name|ForUtil
operator|.
name|MAX_DATA_SIZE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
operator|.
name|ForUtil
operator|.
name|MAX_ENCODED_SIZE
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
name|Directory
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
name|store
operator|.
name|IOContext
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
name|store
operator|.
name|IndexOutput
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
name|store
operator|.
name|RAMDirectory
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
name|LuceneTestCase
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomNumbers
import|;
end_import

begin_class
DECL|class|TestForUtil
specifier|public
class|class
name|TestForUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEncodeDecode
specifier|public
name|void
name|testEncodeDecode
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iterations
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|float
name|acceptableOverheadRatio
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
operator|(
name|iterations
operator|-
literal|1
operator|)
operator|*
name|BLOCK_SIZE
operator|+
name|ForUtil
operator|.
name|MAX_DATA_SIZE
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|bpv
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|32
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpv
operator|==
literal|0
condition|)
block|{
specifier|final
name|int
name|value
init|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
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
name|BLOCK_SIZE
condition|;
operator|++
name|j
control|)
block|{
name|values
index|[
name|i
operator|*
name|BLOCK_SIZE
operator|+
name|j
index|]
operator|=
name|value
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|BLOCK_SIZE
condition|;
operator|++
name|j
control|)
block|{
name|values
index|[
name|i
operator|*
name|BLOCK_SIZE
operator|+
name|j
index|]
operator|=
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bpv
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|long
name|endPointer
decl_stmt|;
block|{
comment|// encode
name|IndexOutput
name|out
init|=
name|d
operator|.
name|createOutput
argument_list|(
literal|"test.bin"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|ForUtil
name|forUtil
init|=
operator|new
name|ForUtil
argument_list|(
name|acceptableOverheadRatio
argument_list|,
name|out
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|values
argument_list|,
name|i
operator|*
name|BLOCK_SIZE
argument_list|,
name|values
operator|.
name|length
argument_list|)
argument_list|,
operator|new
name|byte
index|[
name|MAX_ENCODED_SIZE
index|]
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|endPointer
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
comment|// decode
name|IndexInput
name|in
init|=
name|d
operator|.
name|openInput
argument_list|(
literal|"test.bin"
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
specifier|final
name|ForUtil
name|forUtil
init|=
operator|new
name|ForUtil
argument_list|(
name|in
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|forUtil
operator|.
name|skipBlock
argument_list|(
name|in
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|int
index|[]
name|restored
init|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
decl_stmt|;
name|forUtil
operator|.
name|readBlock
argument_list|(
name|in
argument_list|,
operator|new
name|byte
index|[
name|MAX_ENCODED_SIZE
index|]
argument_list|,
name|restored
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|values
argument_list|,
name|i
operator|*
name|BLOCK_SIZE
argument_list|,
operator|(
name|i
operator|+
literal|1
operator|)
operator|*
name|BLOCK_SIZE
argument_list|)
argument_list|,
name|Arrays
operator|.
name|copyOf
argument_list|(
name|restored
argument_list|,
name|BLOCK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|endPointer
argument_list|,
name|in
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

