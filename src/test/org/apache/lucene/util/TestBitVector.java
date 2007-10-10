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
name|RAMDirectory
import|;
end_import

begin_comment
comment|/**  *<code>TestBitVector</code> tests the<code>BitVector</code>, obviously.  *  *   * @version $Id$  */
end_comment

begin_class
DECL|class|TestBitVector
specifier|public
class|class
name|TestBitVector
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestBitVector
specifier|public
name|TestBitVector
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test the default constructor on BitVectors of various sizes.      * @throws Exception      */
DECL|method|testConstructSize
specifier|public
name|void
name|testConstructSize
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConstructOfSize
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|doTestConstructOfSize
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|doTestConstructOfSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestConstructOfSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestConstructOfSize
specifier|private
name|void
name|doTestConstructOfSize
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|BitVector
name|bv
init|=
operator|new
name|BitVector
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|n
argument_list|,
name|bv
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test the get() and set() methods on BitVectors of various sizes.      * @throws Exception      */
DECL|method|testGetSet
specifier|public
name|void
name|testGetSet
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestGetSetVectorOfSize
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|doTestGetSetVectorOfSize
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|doTestGetSetVectorOfSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestGetSetVectorOfSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestGetSetVectorOfSize
specifier|private
name|void
name|doTestGetSetVectorOfSize
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|BitVector
name|bv
init|=
operator|new
name|BitVector
argument_list|(
name|n
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
name|bv
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// ensure a set bit can be git'
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test the clear() method on BitVectors of various sizes.      * @throws Exception      */
DECL|method|testClear
specifier|public
name|void
name|testClear
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestClearVectorOfSize
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|doTestClearVectorOfSize
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|doTestClearVectorOfSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestClearVectorOfSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestClearVectorOfSize
specifier|private
name|void
name|doTestClearVectorOfSize
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|BitVector
name|bv
init|=
operator|new
name|BitVector
argument_list|(
name|n
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
name|bv
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// ensure a set bit is cleared
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|bv
operator|.
name|clear
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test the count() method on BitVectors of various sizes.      * @throws Exception      */
DECL|method|testCount
specifier|public
name|void
name|testCount
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCountVectorOfSize
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|doTestCountVectorOfSize
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|doTestCountVectorOfSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestCountVectorOfSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestCountVectorOfSize
specifier|private
name|void
name|doTestCountVectorOfSize
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|BitVector
name|bv
init|=
operator|new
name|BitVector
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// test count when incrementally setting bits
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bv
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bv
operator|=
operator|new
name|BitVector
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|// test count when setting then clearing bits
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bv
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|clear
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test writing and construction to/from Directory.      * @throws Exception      */
DECL|method|testWriteRead
specifier|public
name|void
name|testWriteRead
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestWriteRead
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestWriteRead
specifier|private
name|void
name|doTestWriteRead
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|BitVector
name|bv
init|=
operator|new
name|BitVector
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// test count when incrementally setting bits
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bv
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|write
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
expr_stmt|;
name|BitVector
name|compare
init|=
operator|new
name|BitVector
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
decl_stmt|;
comment|// compare bit vectors with bits set incrementally
name|assertTrue
argument_list|(
name|doCompare
argument_list|(
name|bv
argument_list|,
name|compare
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test r/w when size/count cause switching between bit-set and d-gaps file formats.        * @throws Exception      */
DECL|method|testDgaps
specifier|public
name|void
name|testDgaps
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestDgaps
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doTestDgaps
argument_list|(
literal|10
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doTestDgaps
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doTestDgaps
argument_list|(
literal|1000
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|doTestDgaps
argument_list|(
literal|10000
argument_list|,
literal|40
argument_list|,
literal|43
argument_list|)
expr_stmt|;
name|doTestDgaps
argument_list|(
literal|100000
argument_list|,
literal|415
argument_list|,
literal|418
argument_list|)
expr_stmt|;
name|doTestDgaps
argument_list|(
literal|1000000
argument_list|,
literal|3123
argument_list|,
literal|3126
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestDgaps
specifier|private
name|void
name|doTestDgaps
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|count1
parameter_list|,
name|int
name|count2
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|BitVector
name|bv
init|=
operator|new
name|BitVector
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
name|count1
condition|;
name|i
operator|++
control|)
block|{
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bv
operator|.
name|write
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
expr_stmt|;
comment|// gradually increase number of set bits
for|for
control|(
name|int
name|i
init|=
name|count1
init|;
name|i
operator|<
name|count2
condition|;
name|i
operator|++
control|)
block|{
name|BitVector
name|bv2
init|=
operator|new
name|BitVector
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doCompare
argument_list|(
name|bv
argument_list|,
name|bv2
argument_list|)
argument_list|)
expr_stmt|;
name|bv
operator|=
name|bv2
expr_stmt|;
name|bv
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|write
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
expr_stmt|;
block|}
comment|// now start decreasing number of set bits
for|for
control|(
name|int
name|i
init|=
name|count2
operator|-
literal|1
init|;
name|i
operator|>=
name|count1
condition|;
name|i
operator|--
control|)
block|{
name|BitVector
name|bv2
init|=
operator|new
name|BitVector
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doCompare
argument_list|(
name|bv
argument_list|,
name|bv2
argument_list|)
argument_list|)
expr_stmt|;
name|bv
operator|=
name|bv2
expr_stmt|;
name|bv
operator|.
name|clear
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|bv
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|bv
operator|.
name|write
argument_list|(
name|d
argument_list|,
literal|"TESTBV"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Compare two BitVectors.      * This should really be an equals method on the BitVector itself.      * @param bv One bit vector      * @param compare The second to compare      */
DECL|method|doCompare
specifier|private
name|boolean
name|doCompare
parameter_list|(
name|BitVector
name|bv
parameter_list|,
name|BitVector
name|compare
parameter_list|)
block|{
name|boolean
name|equal
init|=
literal|true
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
name|bv
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// bits must be equal
if|if
condition|(
name|bv
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|compare
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|equal
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
return|return
name|equal
return|;
block|}
block|}
end_class

end_unit

