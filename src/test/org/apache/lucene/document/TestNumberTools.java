begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestNumberTools
specifier|public
class|class
name|TestNumberTools
extends|extends
name|TestCase
block|{
DECL|method|testNearZero
specifier|public
name|void
name|testNearZero
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
operator|-
literal|100
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
operator|-
literal|100
init|;
name|j
operator|<=
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|subtestTwoLongs
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMax
specifier|public
name|void
name|testMax
parameter_list|()
block|{
comment|// make sure the constants convert to their equivelents
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|NumberTools
operator|.
name|stringToLong
argument_list|(
name|NumberTools
operator|.
name|MAX_STRING_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NumberTools
operator|.
name|MAX_STRING_VALUE
argument_list|,
name|NumberTools
operator|.
name|longToString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|// test near MAX, too
for|for
control|(
name|long
name|l
init|=
name|Long
operator|.
name|MAX_VALUE
init|;
name|l
operator|>
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|10000
condition|;
name|l
operator|--
control|)
block|{
name|subtestTwoLongs
argument_list|(
name|l
argument_list|,
name|l
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMin
specifier|public
name|void
name|testMin
parameter_list|()
block|{
comment|// make sure the constants convert to their equivelents
name|assertEquals
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|NumberTools
operator|.
name|stringToLong
argument_list|(
name|NumberTools
operator|.
name|MIN_STRING_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NumberTools
operator|.
name|MIN_STRING_VALUE
argument_list|,
name|NumberTools
operator|.
name|longToString
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|// test near MIN, too
for|for
control|(
name|long
name|l
init|=
name|Long
operator|.
name|MIN_VALUE
init|;
name|l
operator|<
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10000
condition|;
name|l
operator|++
control|)
block|{
name|subtestTwoLongs
argument_list|(
name|l
argument_list|,
name|l
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|subtestTwoLongs
specifier|private
specifier|static
name|void
name|subtestTwoLongs
parameter_list|(
name|long
name|i
parameter_list|,
name|long
name|j
parameter_list|)
block|{
comment|// convert to strings
name|String
name|a
init|=
name|NumberTools
operator|.
name|longToString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|b
init|=
name|NumberTools
operator|.
name|longToString
argument_list|(
name|j
argument_list|)
decl_stmt|;
comment|// are they the right length?
name|assertEquals
argument_list|(
name|NumberTools
operator|.
name|STR_SIZE
argument_list|,
name|a
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NumberTools
operator|.
name|STR_SIZE
argument_list|,
name|b
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// are they the right order?
if|if
condition|(
name|i
operator|<
name|j
condition|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|>
name|j
condition|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
comment|// can we convert them back to longs?
name|long
name|i2
init|=
name|NumberTools
operator|.
name|stringToLong
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|long
name|j2
init|=
name|NumberTools
operator|.
name|stringToLong
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|i2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
argument_list|,
name|j2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

