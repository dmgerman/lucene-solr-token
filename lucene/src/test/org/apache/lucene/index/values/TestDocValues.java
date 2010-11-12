begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|index
operator|.
name|values
operator|.
name|DocValues
operator|.
name|SortedSource
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
name|index
operator|.
name|values
operator|.
name|DocValues
operator|.
name|Source
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
name|FloatsRef
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
name|LongsRef
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
name|UnicodeUtil
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestDocValues
specifier|public
class|class
name|TestDocValues
extends|extends
name|LuceneTestCase
block|{
comment|// TODO -- for sorted test, do our own Sort of the
comment|// values and verify it's identical
DECL|method|testBytesStraight
specifier|public
name|void
name|testBytesStraight
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestBytes
argument_list|(
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runTestBytes
argument_list|(
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testBytesDeref
specifier|public
name|void
name|testBytesDeref
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestBytes
argument_list|(
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runTestBytes
argument_list|(
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testBytesSorted
specifier|public
name|void
name|testBytesSorted
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestBytes
argument_list|(
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runTestBytes
argument_list|(
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestBytes
specifier|public
name|void
name|runTestBytes
parameter_list|(
specifier|final
name|Bytes
operator|.
name|Mode
name|mode
parameter_list|,
specifier|final
name|boolean
name|fixedSize
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
init|=
name|mode
operator|==
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
condition|?
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
else|:
literal|null
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Writer
name|w
init|=
name|Bytes
operator|.
name|getWriter
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|mode
argument_list|,
name|comp
argument_list|,
name|fixedSize
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
literal|220
decl_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|maxDoc
index|]
decl_stmt|;
specifier|final
name|int
name|lenMin
decl_stmt|,
name|lenMax
decl_stmt|;
if|if
condition|(
name|fixedSize
condition|)
block|{
name|lenMin
operator|=
name|lenMax
operator|=
literal|3
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lenMin
operator|=
literal|1
expr_stmt|;
name|lenMax
operator|=
literal|15
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|<=
literal|2
condition|)
block|{
comment|// use prior value
name|s
operator|=
name|values
index|[
literal|2
operator|*
name|random
operator|.
name|nextInt
argument_list|(
name|i
argument_list|)
index|]
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
name|lenMin
argument_list|,
name|lenMax
argument_list|)
expr_stmt|;
block|}
name|values
index|[
literal|2
operator|*
name|i
index|]
operator|=
name|s
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
literal|2
operator|*
name|i
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|DocValues
name|r
init|=
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|mode
argument_list|,
name|fixedSize
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|ValuesEnum
name|bytesEnum
init|=
name|r
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"enum is null"
argument_list|,
name|bytesEnum
argument_list|)
expr_stmt|;
name|ValuesAttribute
name|attr
init|=
name|bytesEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"attribute is null"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|BytesRef
name|ref
init|=
name|attr
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"BytesRef is null - enum not initialized to use bytes"
argument_list|,
name|attr
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|idx
init|=
literal|2
operator|*
name|i
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc: "
operator|+
name|idx
argument_list|,
name|idx
argument_list|,
name|bytesEnum
operator|.
name|advance
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|utf8String
init|=
name|ref
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc: "
operator|+
name|idx
operator|+
literal|" lenLeft: "
operator|+
name|values
index|[
name|idx
index|]
operator|.
name|length
argument_list|()
operator|+
literal|" lenRight: "
operator|+
name|utf8String
operator|.
name|length
argument_list|()
argument_list|,
name|values
index|[
name|idx
index|]
argument_list|,
name|utf8String
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ValuesEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|bytesEnum
operator|.
name|advance
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ValuesEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|bytesEnum
operator|.
name|advance
argument_list|(
name|maxDoc
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|bytesEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Verify we can load source twice:
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|Source
name|s
decl_stmt|;
name|DocValues
operator|.
name|SortedSource
name|ss
decl_stmt|;
if|if
condition|(
name|mode
operator|==
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
condition|)
block|{
name|s
operator|=
name|ss
operator|=
name|getSortedSource
argument_list|(
name|r
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|getSource
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|ss
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|idx
init|=
literal|2
operator|*
name|i
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"doc "
operator|+
name|idx
operator|+
literal|"; value="
operator|+
name|values
index|[
name|idx
index|]
argument_list|,
name|s
operator|.
name|getBytes
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|idx
argument_list|,
name|values
index|[
name|idx
index|]
argument_list|,
name|s
operator|.
name|getBytes
argument_list|(
name|idx
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|idx
argument_list|,
name|values
index|[
name|idx
index|]
argument_list|,
name|ss
operator|.
name|getByOrd
argument_list|(
name|ss
operator|.
name|ord
argument_list|(
name|idx
argument_list|)
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|DocValues
operator|.
name|SortedSource
operator|.
name|LookupResult
name|result
init|=
name|ss
operator|.
name|getByValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|values
index|[
name|idx
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|found
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ss
operator|.
name|ord
argument_list|(
name|idx
argument_list|)
argument_list|,
name|result
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Lookup random strings:
if|if
condition|(
name|mode
operator|==
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
condition|)
block|{
specifier|final
name|int
name|numValues
init|=
name|ss
operator|.
name|getValueCount
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|bytesValue
init|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
name|lenMin
argument_list|,
name|lenMax
argument_list|)
argument_list|)
decl_stmt|;
name|SortedSource
operator|.
name|LookupResult
name|result
init|=
name|ss
operator|.
name|getByValue
argument_list|(
name|bytesValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|found
condition|)
block|{
assert|assert
name|result
operator|.
name|ord
operator|>
literal|0
assert|;
name|assertTrue
argument_list|(
name|bytesValue
operator|.
name|bytesEquals
argument_list|(
name|ss
operator|.
name|getByOrd
argument_list|(
name|result
operator|.
name|ord
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|bytesValue
operator|.
name|utf8ToString
argument_list|()
operator|.
name|equals
argument_list|(
name|values
index|[
literal|2
operator|*
name|k
index|]
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|ss
operator|.
name|ord
argument_list|(
literal|2
operator|*
name|k
argument_list|)
argument_list|,
name|result
operator|.
name|ord
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|count
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|result
operator|.
name|ord
operator|>=
literal|0
assert|;
if|if
condition|(
name|result
operator|.
name|ord
operator|==
literal|0
condition|)
block|{
specifier|final
name|BytesRef
name|firstRef
init|=
name|ss
operator|.
name|getByOrd
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// random string was before our first
name|assertTrue
argument_list|(
name|firstRef
operator|.
name|compareTo
argument_list|(
name|bytesValue
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|ord
operator|==
name|numValues
condition|)
block|{
specifier|final
name|BytesRef
name|lastRef
init|=
name|ss
operator|.
name|getByOrd
argument_list|(
name|numValues
argument_list|)
decl_stmt|;
comment|// random string was after our last
name|assertTrue
argument_list|(
name|lastRef
operator|.
name|compareTo
argument_list|(
name|bytesValue
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// random string fell between two of our values
specifier|final
name|BytesRef
name|before
init|=
operator|(
name|BytesRef
operator|)
name|ss
operator|.
name|getByOrd
argument_list|(
name|result
operator|.
name|ord
argument_list|)
operator|.
name|clone
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|after
init|=
name|ss
operator|.
name|getByOrd
argument_list|(
name|result
operator|.
name|ord
operator|+
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|compareTo
argument_list|(
name|bytesValue
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytesValue
operator|.
name|compareTo
argument_list|(
name|after
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testInts
specifier|public
name|void
name|testInts
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|maxV
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|NUM_VALUES
init|=
literal|1000
decl_stmt|;
specifier|final
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|NUM_VALUES
index|]
decl_stmt|;
for|for
control|(
name|int
name|rx
init|=
literal|1
init|;
name|rx
operator|<
literal|63
condition|;
name|rx
operator|++
operator|,
name|maxV
operator|*=
literal|2
control|)
block|{
for|for
control|(
name|int
name|b
init|=
literal|0
init|;
name|b
operator|<
literal|2
condition|;
name|b
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|boolean
name|useFixedArrays
init|=
name|b
operator|==
literal|0
decl_stmt|;
name|Writer
name|w
init|=
name|Ints
operator|.
name|getWriter
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|useFixedArrays
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
name|NUM_VALUES
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|v
init|=
name|random
operator|.
name|nextLong
argument_list|()
operator|%
operator|(
literal|1
operator|+
name|maxV
operator|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|additionalDocs
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
name|w
operator|.
name|finish
argument_list|(
name|NUM_VALUES
operator|+
name|additionalDocs
argument_list|)
expr_stmt|;
name|DocValues
name|r
init|=
name|Ints
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|useFixedArrays
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|Source
name|s
init|=
name|getSource
argument_list|(
name|r
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
name|NUM_VALUES
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|v
init|=
name|s
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"index "
operator|+
name|i
operator|+
literal|" b: "
operator|+
name|b
argument_list|,
name|values
index|[
name|i
index|]
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|ValuesEnum
name|iEnum
init|=
name|r
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|ValuesAttribute
name|attr
init|=
name|iEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|LongsRef
name|ints
init|=
name|attr
operator|.
name|ints
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
name|NUM_VALUES
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|iEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|ints
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|NUM_VALUES
init|;
name|i
operator|<
name|NUM_VALUES
operator|+
name|additionalDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|iEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|i
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|ValuesEnum
name|iEnum
init|=
name|r
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|ValuesAttribute
name|attr
init|=
name|iEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|LongsRef
name|ints
init|=
name|attr
operator|.
name|ints
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
name|NUM_VALUES
condition|;
name|i
operator|+=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|iEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|ints
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|NUM_VALUES
init|;
name|i
operator|<
name|NUM_VALUES
operator|+
name|additionalDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|iEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|i
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFloats4
specifier|public
name|void
name|testFloats4
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestFloats
argument_list|(
literal|4
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestFloats
specifier|private
name|void
name|runTestFloats
parameter_list|(
name|int
name|precision
parameter_list|,
name|double
name|delta
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Writer
name|w
init|=
name|Floats
operator|.
name|getWriter
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|precision
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_VALUES
init|=
literal|1000
decl_stmt|;
specifier|final
name|double
index|[]
name|values
init|=
operator|new
name|double
index|[
name|NUM_VALUES
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
name|NUM_VALUES
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|double
name|v
init|=
name|precision
operator|==
literal|4
condition|?
name|random
operator|.
name|nextFloat
argument_list|()
else|:
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|additionalValues
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|w
operator|.
name|finish
argument_list|(
name|NUM_VALUES
operator|+
name|additionalValues
argument_list|)
expr_stmt|;
name|DocValues
name|r
init|=
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|NUM_VALUES
operator|+
name|additionalValues
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|Source
name|s
init|=
name|getSource
argument_list|(
name|r
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
name|NUM_VALUES
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|s
operator|.
name|getFloat
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|ValuesEnum
name|fEnum
init|=
name|r
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|ValuesAttribute
name|attr
init|=
name|fEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|FloatsRef
name|floats
init|=
name|attr
operator|.
name|floats
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
name|NUM_VALUES
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|fEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|floats
operator|.
name|get
argument_list|()
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|NUM_VALUES
init|;
name|i
operator|<
name|NUM_VALUES
operator|+
name|additionalValues
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|fEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|floats
operator|.
name|get
argument_list|()
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
name|fEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
block|{
name|ValuesEnum
name|fEnum
init|=
name|r
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|ValuesAttribute
name|attr
init|=
name|fEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|FloatsRef
name|floats
init|=
name|attr
operator|.
name|floats
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
name|NUM_VALUES
condition|;
name|i
operator|+=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|fEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|floats
operator|.
name|get
argument_list|()
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|NUM_VALUES
init|;
name|i
operator|<
name|NUM_VALUES
operator|+
name|additionalValues
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|fEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|floats
operator|.
name|get
argument_list|()
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
name|fEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFloats8
specifier|public
name|void
name|testFloats8
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestFloats
argument_list|(
literal|8
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
DECL|method|getSource
specifier|private
name|Source
name|getSource
parameter_list|(
name|DocValues
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// getSource uses cache internally
return|return
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|values
operator|.
name|load
argument_list|()
else|:
name|values
operator|.
name|getSource
argument_list|()
return|;
block|}
DECL|method|getSortedSource
specifier|private
name|SortedSource
name|getSortedSource
parameter_list|(
name|DocValues
name|values
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
comment|// getSortedSource uses cache internally
return|return
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|values
operator|.
name|loadSorted
argument_list|(
name|comparator
argument_list|)
else|:
name|values
operator|.
name|getSortedSorted
argument_list|(
name|comparator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

