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
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestLSBRadixSorter
specifier|public
class|class
name|TestLSBRadixSorter
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|(
name|LSBRadixSorter
name|sorter
parameter_list|,
name|int
name|maxLen
parameter_list|)
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
literal|10
condition|;
operator|++
name|iter
control|)
block|{
name|int
name|off
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
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
name|maxLen
argument_list|)
decl_stmt|;
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|off
operator|+
name|len
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|numBits
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|31
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxValue
init|=
operator|(
literal|1
operator|<<
name|numBits
operator|)
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
name|arr
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|arr
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
name|maxValue
argument_list|)
expr_stmt|;
block|}
name|test
argument_list|(
name|sorter
argument_list|,
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|(
name|LSBRadixSorter
name|sorter
parameter_list|,
name|int
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|expected
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|off
operator|+
name|len
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|actual
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|off
operator|+
name|len
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|test
argument_list|(
operator|new
name|LSBRadixSorter
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testOne
specifier|public
name|void
name|testOne
parameter_list|()
block|{
name|test
argument_list|(
operator|new
name|LSBRadixSorter
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwo
specifier|public
name|void
name|testTwo
parameter_list|()
block|{
name|test
argument_list|(
operator|new
name|LSBRadixSorter
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|test
argument_list|(
operator|new
name|LSBRadixSorter
argument_list|()
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
block|{
name|test
argument_list|(
operator|new
name|LSBRadixSorter
argument_list|()
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|method|testSorted
specifier|public
name|void
name|testSorted
parameter_list|()
block|{
name|LSBRadixSorter
name|sorter
init|=
operator|new
name|LSBRadixSorter
argument_list|()
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
literal|10
condition|;
operator|++
name|iter
control|)
block|{
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
literal|10000
index|]
decl_stmt|;
name|int
name|a
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
name|arr
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|a
operator|+=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|a
expr_stmt|;
block|}
specifier|final
name|int
name|off
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
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
name|arr
operator|.
name|length
operator|-
name|off
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|sorter
argument_list|,
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

