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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|codecs
operator|.
name|MutablePointsReader
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
name|ArrayUtil
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
name|StringHelper
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestMutablePointsReaderUtils
specifier|public
class|class
name|TestMutablePointsReaderUtils
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
condition|;
operator|++
name|iter
control|)
block|{
name|doTestSort
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTestSort
specifier|private
name|void
name|doTestSort
parameter_list|()
block|{
specifier|final
name|int
name|bytesPerDim
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|16
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
decl_stmt|;
name|Point
index|[]
name|points
init|=
name|createRandomPoints
argument_list|(
literal|1
argument_list|,
name|bytesPerDim
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|DummyPointsReader
name|reader
init|=
operator|new
name|DummyPointsReader
argument_list|(
name|points
argument_list|)
decl_stmt|;
name|MutablePointsReaderUtils
operator|.
name|sort
argument_list|(
name|maxDoc
argument_list|,
name|bytesPerDim
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
name|points
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|points
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Point
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Point
name|o1
parameter_list|,
name|Point
name|o2
parameter_list|)
block|{
name|int
name|cmp
init|=
name|o1
operator|.
name|packedValue
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|packedValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|Integer
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|doc
argument_list|,
name|o2
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|cmp
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|points
argument_list|,
name|reader
operator|.
name|points
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|points
argument_list|,
name|reader
operator|.
name|points
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortByDim
specifier|public
name|void
name|testSortByDim
parameter_list|()
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
condition|;
operator|++
name|iter
control|)
block|{
name|doTestSortByDim
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTestSortByDim
specifier|private
name|void
name|doTestSortByDim
parameter_list|()
block|{
specifier|final
name|int
name|numDims
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bytesPerDim
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|16
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
decl_stmt|;
name|Point
index|[]
name|points
init|=
name|createRandomPoints
argument_list|(
name|numDims
argument_list|,
name|bytesPerDim
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|int
index|[]
name|commonPrefixLengths
init|=
operator|new
name|int
index|[
name|numDims
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
name|commonPrefixLengths
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|commonPrefixLengths
index|[
name|i
index|]
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|firstValue
init|=
name|points
index|[
literal|0
index|]
operator|.
name|packedValue
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
operator|++
name|dim
control|)
block|{
name|int
name|offset
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
name|BytesRef
name|packedValue
init|=
name|points
index|[
name|i
index|]
operator|.
name|packedValue
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|firstValue
operator|.
name|bytes
argument_list|,
name|firstValue
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|packedValue
operator|.
name|bytes
argument_list|,
name|packedValue
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|commonPrefixLengths
index|[
name|dim
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|DummyPointsReader
name|reader
init|=
operator|new
name|DummyPointsReader
argument_list|(
name|points
argument_list|)
decl_stmt|;
specifier|final
name|int
name|sortedDim
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDims
argument_list|)
decl_stmt|;
name|MutablePointsReaderUtils
operator|.
name|sortByDim
argument_list|(
name|sortedDim
argument_list|,
name|bytesPerDim
argument_list|,
name|commonPrefixLengths
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
name|points
operator|.
name|length
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|offset
init|=
name|sortedDim
operator|*
name|bytesPerDim
decl_stmt|;
name|BytesRef
name|previousValue
init|=
name|reader
operator|.
name|points
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|packedValue
decl_stmt|;
name|BytesRef
name|currentValue
init|=
name|reader
operator|.
name|points
index|[
name|i
index|]
operator|.
name|packedValue
decl_stmt|;
name|int
name|cmp
init|=
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|previousValue
operator|.
name|bytes
argument_list|,
name|previousValue
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|currentValue
operator|.
name|bytes
argument_list|,
name|currentValue
operator|.
name|offset
operator|+
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|reader
operator|.
name|points
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|doc
operator|-
name|reader
operator|.
name|points
index|[
name|i
index|]
operator|.
name|doc
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|cmp
operator|<=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPartition
specifier|public
name|void
name|testPartition
parameter_list|()
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
condition|;
operator|++
name|iter
control|)
block|{
name|doTestPartition
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTestPartition
specifier|private
name|void
name|doTestPartition
parameter_list|()
block|{
specifier|final
name|int
name|numDims
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bytesPerDim
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|16
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
decl_stmt|;
name|Point
index|[]
name|points
init|=
name|createRandomPoints
argument_list|(
name|numDims
argument_list|,
name|bytesPerDim
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|int
name|commonPrefixLength
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytesPerDim
argument_list|)
decl_stmt|;
specifier|final
name|int
name|splitDim
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDims
argument_list|)
decl_stmt|;
name|BytesRef
name|firstValue
init|=
name|points
index|[
literal|0
index|]
operator|.
name|packedValue
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|points
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|BytesRef
name|packedValue
init|=
name|points
index|[
name|i
index|]
operator|.
name|packedValue
decl_stmt|;
name|int
name|offset
init|=
name|splitDim
operator|*
name|bytesPerDim
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|firstValue
operator|.
name|bytes
argument_list|,
name|firstValue
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|packedValue
operator|.
name|bytes
argument_list|,
name|packedValue
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|commonPrefixLength
argument_list|)
expr_stmt|;
block|}
name|DummyPointsReader
name|reader
init|=
operator|new
name|DummyPointsReader
argument_list|(
name|points
argument_list|)
decl_stmt|;
specifier|final
name|int
name|pivot
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|points
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|MutablePointsReaderUtils
operator|.
name|partition
argument_list|(
name|maxDoc
argument_list|,
name|splitDim
argument_list|,
name|bytesPerDim
argument_list|,
name|commonPrefixLength
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
name|points
operator|.
name|length
argument_list|,
name|pivot
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|pivotValue
init|=
name|reader
operator|.
name|points
index|[
name|pivot
index|]
operator|.
name|packedValue
decl_stmt|;
name|int
name|offset
init|=
name|splitDim
operator|*
name|bytesPerDim
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
name|points
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|BytesRef
name|value
init|=
name|reader
operator|.
name|points
index|[
name|i
index|]
operator|.
name|packedValue
decl_stmt|;
name|int
name|cmp
init|=
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|value
operator|.
name|bytes
argument_list|,
name|value
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|pivotValue
operator|.
name|bytes
argument_list|,
name|pivotValue
operator|.
name|offset
operator|+
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|reader
operator|.
name|points
index|[
name|i
index|]
operator|.
name|doc
operator|-
name|reader
operator|.
name|points
index|[
name|pivot
index|]
operator|.
name|doc
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
name|pivot
condition|)
block|{
name|assertTrue
argument_list|(
name|cmp
operator|<=
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|>
name|pivot
condition|)
block|{
name|assertTrue
argument_list|(
name|cmp
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cmp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createRandomPoints
specifier|private
specifier|static
name|Point
index|[]
name|createRandomPoints
parameter_list|(
name|int
name|numDims
parameter_list|,
name|int
name|bytesPerDim
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
specifier|final
name|int
name|packedBytesLength
init|=
name|numDims
operator|*
name|bytesPerDim
decl_stmt|;
specifier|final
name|int
name|numPoints
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|Point
index|[]
name|points
init|=
operator|new
name|Point
index|[
name|numPoints
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
name|numPoints
condition|;
operator|++
name|i
control|)
block|{
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|points
index|[
name|i
index|]
operator|=
operator|new
name|Point
argument_list|(
name|value
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|points
return|;
block|}
DECL|class|Point
specifier|private
specifier|static
class|class
name|Point
block|{
DECL|field|packedValue
specifier|final
name|BytesRef
name|packedValue
decl_stmt|;
DECL|field|doc
specifier|final
name|int
name|doc
decl_stmt|;
DECL|method|Point
name|Point
parameter_list|(
name|byte
index|[]
name|packedValue
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
comment|// use a non-null offset to make sure MutablePointsReaderUtils does not ignore it
name|this
operator|.
name|packedValue
operator|=
operator|new
name|BytesRef
argument_list|(
name|packedValue
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|packedValue
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
expr_stmt|;
name|this
operator|.
name|packedValue
operator|.
name|offset
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|packedValue
operator|.
name|length
operator|=
name|packedValue
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|instanceof
name|Point
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Point
name|that
init|=
operator|(
name|Point
operator|)
name|obj
decl_stmt|;
return|return
name|packedValue
operator|.
name|equals
argument_list|(
name|that
operator|.
name|packedValue
argument_list|)
operator|&&
name|doc
operator|==
name|that
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|packedValue
operator|.
name|hashCode
argument_list|()
operator|+
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"value="
operator|+
name|packedValue
operator|+
literal|" doc="
operator|+
name|doc
return|;
block|}
block|}
DECL|class|DummyPointsReader
specifier|private
specifier|static
class|class
name|DummyPointsReader
extends|extends
name|MutablePointsReader
block|{
DECL|field|points
specifier|private
specifier|final
name|Point
index|[]
name|points
decl_stmt|;
DECL|method|DummyPointsReader
name|DummyPointsReader
parameter_list|(
name|Point
index|[]
name|points
parameter_list|)
block|{
name|this
operator|.
name|points
operator|=
name|points
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|void
name|getValue
parameter_list|(
name|int
name|i
parameter_list|,
name|BytesRef
name|packedValue
parameter_list|)
block|{
name|packedValue
operator|.
name|bytes
operator|=
name|points
index|[
name|i
index|]
operator|.
name|packedValue
operator|.
name|bytes
expr_stmt|;
name|packedValue
operator|.
name|offset
operator|=
name|points
index|[
name|i
index|]
operator|.
name|packedValue
operator|.
name|offset
expr_stmt|;
name|packedValue
operator|.
name|length
operator|=
name|points
index|[
name|i
index|]
operator|.
name|packedValue
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getByteAt
specifier|public
name|byte
name|getByteAt
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|k
parameter_list|)
block|{
name|BytesRef
name|packedValue
init|=
name|points
index|[
name|i
index|]
operator|.
name|packedValue
decl_stmt|;
return|return
name|packedValue
operator|.
name|bytes
index|[
name|packedValue
operator|.
name|offset
operator|+
name|k
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getDocID
specifier|public
name|int
name|getDocID
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|points
index|[
name|i
index|]
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|swap
specifier|public
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|ArrayUtil
operator|.
name|swap
argument_list|(
name|points
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getMinPackedValue
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getMaxPackedValue
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getNumDimensions
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getBytesPerDimension
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit
