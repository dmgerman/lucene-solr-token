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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|BytesRefHash
operator|.
name|MaxBytesLengthExceededException
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
comment|/**  *  */
end_comment

begin_class
DECL|class|TestBytesRefHash
specifier|public
class|class
name|TestBytesRefHash
extends|extends
name|LuceneTestCase
block|{
DECL|field|hash
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|pool
name|ByteBlockPool
name|pool
decl_stmt|;
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
name|pool
operator|=
name|newPool
argument_list|()
expr_stmt|;
name|hash
operator|=
name|newHash
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
DECL|method|newPool
specifier|private
name|ByteBlockPool
name|newPool
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|pool
operator|!=
literal|null
condition|?
name|pool
else|:
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|RecyclingByteBlockAllocator
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newHash
specifier|private
name|BytesRefHash
name|newHash
parameter_list|(
name|ByteBlockPool
name|blockPool
parameter_list|)
block|{
specifier|final
name|int
name|initSize
init|=
literal|2
operator|<<
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
return|return
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|BytesRefHash
argument_list|(
name|blockPool
argument_list|)
else|:
operator|new
name|BytesRefHash
argument_list|(
name|blockPool
argument_list|,
name|initSize
argument_list|,
operator|new
name|BytesRefHash
operator|.
name|DirectBytesStartArray
argument_list|(
name|initSize
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Test method for {@link org.apache.lucene.util.BytesRefHash#size()}.    */
annotation|@
name|Test
DECL|method|testSize
specifier|public
name|void
name|testSize
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
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
specifier|final
name|int
name|mod
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
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
name|mod
operator|==
literal|0
condition|)
block|{
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|reinit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Test method for    * {@link org.apache.lucene.util.BytesRefHash#get(org.apache.lucene.util.BytesRefHash.Entry)}    * .    */
annotation|@
name|Test
DECL|method|testGet
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
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
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|strings
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
init|=
literal|0
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|>=
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|strings
operator|.
name|put
argument_list|(
name|str
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|uniqueCount
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|strings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ref
argument_list|,
name|hash
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|reinit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test method for {@link org.apache.lucene.util.BytesRefHash#compact()}.    */
annotation|@
name|Test
DECL|method|testCompact
specifier|public
name|void
name|testCompact
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
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
name|int
name|numEntries
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|size
init|=
literal|797
decl_stmt|;
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
specifier|final
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|bits
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|bits
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|numEntries
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numEntries
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numEntries
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
index|[]
name|compact
init|=
name|hash
operator|.
name|compact
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|numEntries
operator|<
name|compact
operator|.
name|length
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
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|compact
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|reinit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test method for    * {@link org.apache.lucene.util.BytesRefHash#sort(java.util.Comparator)}.    */
annotation|@
name|Test
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
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
name|SortedSet
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
comment|// We use the UTF-16 comparator here, because we need to be able to
comment|// compare to native String.compareTo() [UTF-16]:
name|int
index|[]
name|sort
init|=
name|hash
operator|.
name|sort
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|strings
operator|.
name|size
argument_list|()
operator|<
name|sort
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ref
argument_list|,
name|hash
operator|.
name|get
argument_list|(
name|sort
index|[
name|i
operator|++
index|]
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|reinit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test method for    * {@link org.apache.lucene.util.BytesRefHash#add(org.apache.lucene.util.BytesRef)}    * .    */
annotation|@
name|Test
DECL|method|testAdd
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
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
name|Set
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
init|=
literal|0
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|>=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|uniqueCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str
argument_list|,
name|hash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertAllIn
argument_list|(
name|strings
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|reinit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|MaxBytesLengthExceededException
operator|.
name|class
argument_list|)
DECL|method|testLargeValue
specifier|public
name|void
name|testLargeValue
parameter_list|()
block|{
name|int
index|[]
name|sizes
init|=
operator|new
name|int
index|[]
block|{
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
block|,
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
operator|-
literal|33
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|31
argument_list|)
block|,
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
operator|-
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|37
argument_list|)
block|}
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
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
name|sizes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ref
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|sizes
index|[
name|i
index|]
index|]
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|sizes
index|[
name|i
index|]
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MaxBytesLengthExceededException
name|e
parameter_list|)
block|{
if|if
condition|(
name|i
operator|<
name|sizes
operator|.
name|length
operator|-
literal|1
condition|)
name|fail
argument_list|(
literal|"unexpected exception at size: "
operator|+
name|sizes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/**    * Test method for    * {@link org.apache.lucene.util.BytesRefHash#addByPoolOffset(int)}    * .    */
annotation|@
name|Test
DECL|method|testAddByPoolOffset
specifier|public
name|void
name|testAddByPoolOffset
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRefHash
name|offsetHash
init|=
name|newHash
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
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
name|Set
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
init|=
literal|0
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|>=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|offsetKey
init|=
name|offsetHash
operator|.
name|addByPoolOffset
argument_list|(
name|hash
operator|.
name|byteStart
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|offsetKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsetHash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|uniqueCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str
argument_list|,
name|hash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|offsetKey
init|=
name|offsetHash
operator|.
name|addByPoolOffset
argument_list|(
name|hash
operator|.
name|byteStart
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|-
name|offsetKey
operator|)
operator|-
literal|1
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str
argument_list|,
name|hash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|offsetKey
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertAllIn
argument_list|(
name|strings
argument_list|,
name|hash
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|BytesRef
name|bytesRef
init|=
name|offsetHash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ref
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|offsetHash
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|offsetHash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hash
operator|.
name|reinit
argument_list|()
expr_stmt|;
comment|// init for the next round
name|offsetHash
operator|.
name|reinit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertAllIn
specifier|private
name|void
name|assertAllIn
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|,
name|BytesRefHash
name|hash
parameter_list|)
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
comment|// add again to check duplicates
name|assertEquals
argument_list|(
name|string
argument_list|,
name|hash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"key: "
operator|+
name|key
operator|+
literal|" count: "
operator|+
name|count
operator|+
literal|" string: "
operator|+
name|string
argument_list|,
name|key
operator|<
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

