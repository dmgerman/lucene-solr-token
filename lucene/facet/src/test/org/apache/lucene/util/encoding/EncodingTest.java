begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|util
operator|.
name|BytesRef
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
name|IntsRef
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
name|junit
operator|.
name|BeforeClass
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

begin_class
DECL|class|EncodingTest
specifier|public
class|class
name|EncodingTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|uniqueSortedData
DECL|field|data
specifier|private
specifier|static
name|IntsRef
name|uniqueSortedData
decl_stmt|,
name|data
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassEncodingTest
specifier|public
specifier|static
name|void
name|beforeClassEncodingTest
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|capacity
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|data
operator|=
operator|new
name|IntsRef
argument_list|(
name|capacity
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|.
name|ints
index|[
name|i
index|]
operator|=
name|i
operator|+
literal|1
expr_stmt|;
comment|// small values
block|}
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
name|data
operator|.
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|.
name|ints
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
operator|+
literal|1
expr_stmt|;
comment|// some encoders don't allow 0
block|}
name|data
operator|.
name|length
operator|=
name|data
operator|.
name|ints
operator|.
name|length
expr_stmt|;
name|uniqueSortedData
operator|=
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|uniqueSortedData
operator|.
name|ints
argument_list|)
expr_stmt|;
name|uniqueSortedData
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|int
name|prev
init|=
operator|-
literal|1
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
name|uniqueSortedData
operator|.
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|uniqueSortedData
operator|.
name|ints
index|[
name|i
index|]
operator|!=
name|prev
condition|)
block|{
name|uniqueSortedData
operator|.
name|ints
index|[
name|uniqueSortedData
operator|.
name|length
operator|++
index|]
operator|=
name|uniqueSortedData
operator|.
name|ints
index|[
name|i
index|]
expr_stmt|;
name|prev
operator|=
name|uniqueSortedData
operator|.
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
DECL|method|encoderTest
specifier|private
specifier|static
name|void
name|encoderTest
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|,
name|IntsRef
name|data
parameter_list|,
name|IntsRef
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ensure toString is implemented
name|String
name|toString
init|=
name|encoder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|toString
operator|.
name|startsWith
argument_list|(
name|encoder
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
name|IntDecoder
name|decoder
init|=
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
decl_stmt|;
name|toString
operator|=
name|decoder
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|toString
operator|.
name|startsWith
argument_list|(
name|decoder
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// some initial capacity - encoders should grow the byte[]
name|IntsRef
name|values
init|=
operator|new
name|IntsRef
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// some initial capacity - decoders should grow the int[]
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
comment|// run 2 iterations to catch encoders/decoders which don't reset properly
name|encoding
argument_list|(
name|encoder
argument_list|,
name|data
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|decoding
argument_list|(
name|bytes
argument_list|,
name|values
argument_list|,
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|intsEquals
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|encoding
specifier|private
specifier|static
name|void
name|encoding
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|,
name|IntsRef
name|data
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IntsRef
name|values
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// randomly set the offset
name|values
operator|=
operator|new
name|IntsRef
argument_list|(
name|data
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
operator|.
name|ints
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|ints
argument_list|,
literal|1
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|values
operator|.
name|offset
operator|=
literal|1
expr_stmt|;
comment|// ints start at index 1
name|values
operator|.
name|length
operator|=
name|data
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
comment|// need to copy the array because it may be modified by encoders (e.g. sorting)
name|values
operator|=
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|values
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|decoding
specifier|private
specifier|static
name|void
name|decoding
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|IntsRef
name|values
parameter_list|,
name|IntDecoder
name|decoder
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// randomly set the offset and length to other than 0,0
name|bytes
operator|.
name|grow
argument_list|(
name|bytes
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// ensure that we have enough capacity to shift values by 1
name|bytes
operator|.
name|offset
operator|=
literal|1
expr_stmt|;
comment|// bytes start at index 1 (must do that after grow)
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|bytes
argument_list|,
literal|1
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|1
expr_stmt|;
block|}
name|decoder
operator|.
name|decode
argument_list|(
name|bytes
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offset
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
expr_stmt|;
comment|// decoders should not mess with offsets
block|}
annotation|@
name|Test
DECL|method|testVInt8
specifier|public
name|void
name|testVInt8
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|,
name|data
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// cover negative numbers;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|IntEncoder
name|enc
init|=
operator|new
name|VInt8IntEncoder
argument_list|()
decl_stmt|;
name|IntsRef
name|values
init|=
operator|new
name|IntsRef
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|values
operator|.
name|ints
index|[
name|values
operator|.
name|length
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|enc
operator|.
name|encode
argument_list|(
name|values
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|IntDecoder
name|dec
init|=
name|enc
operator|.
name|createMatchingDecoder
argument_list|()
decl_stmt|;
name|values
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|dec
operator|.
name|decode
argument_list|(
name|bytes
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|values
operator|.
name|ints
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleInt
specifier|public
name|void
name|testSimpleInt
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SimpleIntEncoder
argument_list|()
argument_list|,
name|data
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueValues
specifier|public
name|void
name|testSortingUniqueValues
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueDGap
specifier|public
name|void
name|testSortingUniqueDGap
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueDGapEightFlags
specifier|public
name|void
name|testSortingUniqueDGapEightFlags
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|EightFlagsIntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueDGapFourFlags
specifier|public
name|void
name|testSortingUniqueDGapFourFlags
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|FourFlagsIntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueDGapNOnes4
specifier|public
name|void
name|testSortingUniqueDGapNOnes4
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|NOnesIntEncoder
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueDGapNOnes3
specifier|public
name|void
name|testSortingUniqueDGapNOnes3
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|NOnesIntEncoder
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortingUniqueDGapVInt
specifier|public
name|void
name|testSortingUniqueDGapVInt
parameter_list|()
throws|throws
name|Exception
block|{
name|encoderTest
argument_list|(
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapVInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|,
name|uniqueSortedData
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

