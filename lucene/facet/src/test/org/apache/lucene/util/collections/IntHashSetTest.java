begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.collections
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|collections
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetTestCase
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
DECL|class|IntHashSetTest
specifier|public
class|class
name|IntHashSetTest
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|test0
specifier|public
name|void
name|test0
parameter_list|()
block|{
name|IntHashSet
name|set0
init|=
operator|new
name|IntHashSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|set0
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|set0
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
block|{
name|IntHashSet
name|set0
init|=
operator|new
name|IntHashSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|set0
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|set0
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|set0
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test2
specifier|public
name|void
name|test2
parameter_list|()
block|{
name|IntHashSet
name|set0
init|=
operator|new
name|IntHashSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|set0
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|set0
operator|.
name|add
argument_list|(
operator|-
literal|382
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|set0
operator|.
name|remove
argument_list|(
operator|-
literal|382
argument_list|)
expr_stmt|;
name|set0
operator|.
name|remove
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test3
specifier|public
name|void
name|test3
parameter_list|()
block|{
name|IntHashSet
name|set0
init|=
operator|new
name|IntHashSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set0
operator|.
name|isEmpty
argument_list|()
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|set0
operator|.
name|add
argument_list|(
name|i
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
literal|1000
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|set0
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|set0
operator|.
name|size
argument_list|()
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|assertFalse
argument_list|(
name|set0
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|set0
operator|.
name|contains
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|test4
specifier|public
name|void
name|test4
parameter_list|()
block|{
name|IntHashSet
name|set1
init|=
operator|new
name|IntHashSet
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|set2
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
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
name|ArrayHashMapTest
operator|.
name|RANDOM_TEST_NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|500
decl_stmt|;
name|boolean
name|shouldAdd
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldAdd
condition|)
block|{
name|set1
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|set2
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set1
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|set2
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|set2
operator|.
name|size
argument_list|()
argument_list|,
name|set1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|value
range|:
name|set2
control|)
block|{
name|assertTrue
argument_list|(
name|set1
operator|.
name|contains
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRegularJavaSet
specifier|public
name|void
name|testRegularJavaSet
parameter_list|()
block|{
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
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
literal|100
condition|;
operator|++
name|j
control|)
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
name|ArrayHashMapTest
operator|.
name|RANDOM_TEST_NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|5000
decl_stmt|;
name|boolean
name|shouldAdd
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldAdd
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMySet
specifier|public
name|void
name|testMySet
parameter_list|()
block|{
name|IntHashSet
name|set
init|=
operator|new
name|IntHashSet
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
literal|100
condition|;
operator|++
name|j
control|)
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
name|ArrayHashMapTest
operator|.
name|RANDOM_TEST_NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|5000
decl_stmt|;
name|boolean
name|shouldAdd
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldAdd
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testToArray
specifier|public
name|void
name|testToArray
parameter_list|()
block|{
name|IntHashSet
name|set
init|=
operator|new
name|IntHashSet
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
literal|100
condition|;
operator|++
name|j
control|)
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
name|ArrayHashMapTest
operator|.
name|RANDOM_TEST_NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|5000
decl_stmt|;
name|boolean
name|shouldAdd
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldAdd
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|int
index|[]
name|vals
init|=
name|set
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|,
name|vals
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
index|[]
name|realValues
init|=
operator|new
name|int
index|[
name|set
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
index|[]
name|unrealValues
init|=
name|set
operator|.
name|toArray
argument_list|(
name|realValues
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|realValues
argument_list|,
name|unrealValues
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|value
range|:
name|vals
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|remove
argument_list|(
name|value
argument_list|)
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
name|vals
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|realValues
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testZZRegularJavaSet
specifier|public
name|void
name|testZZRegularJavaSet
parameter_list|()
block|{
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
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
literal|100
condition|;
operator|++
name|j
control|)
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
name|ArrayHashMapTest
operator|.
name|RANDOM_TEST_NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|5000
decl_stmt|;
name|boolean
name|shouldAdd
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldAdd
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testZZMySet
specifier|public
name|void
name|testZZMySet
parameter_list|()
block|{
name|IntHashSet
name|set
init|=
operator|new
name|IntHashSet
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
literal|100
condition|;
operator|++
name|j
control|)
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
name|ArrayHashMapTest
operator|.
name|RANDOM_TEST_NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|5000
decl_stmt|;
name|boolean
name|shouldAdd
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldAdd
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

