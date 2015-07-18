begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package

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
name|Test
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|*
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
operator|.
name|HLL
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Serialization smoke-tests.  */
end_comment

begin_class
DECL|class|HLLSerializationTest
specifier|public
class|class
name|HLLSerializationTest
extends|extends
name|LuceneTestCase
block|{
comment|/**      * A smoke-test that covers serialization/deserialization of an HLL      * under all possible parameters.      */
annotation|@
name|Test
annotation|@
name|Slow
annotation|@
name|Nightly
DECL|method|serializationSmokeTest
specifier|public
name|void
name|serializationSmokeTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|randomLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|randomCount
init|=
literal|250
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|randoms
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|(
name|randomCount
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
name|randomCount
condition|;
name|i
operator|++
control|)
block|{
name|randoms
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertCardinality
argument_list|(
name|HLLType
operator|.
name|EMPTY
argument_list|,
name|randoms
argument_list|)
expr_stmt|;
name|assertCardinality
argument_list|(
name|HLLType
operator|.
name|EXPLICIT
argument_list|,
name|randoms
argument_list|)
expr_stmt|;
name|assertCardinality
argument_list|(
name|HLLType
operator|.
name|SPARSE
argument_list|,
name|randoms
argument_list|)
expr_stmt|;
name|assertCardinality
argument_list|(
name|HLLType
operator|.
name|FULL
argument_list|,
name|randoms
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: log2m<=16 was chosen as the max log2m parameter so that the test
comment|//       completes in a reasonable amount of time. Not much is gained by
comment|//       testing larger values - there are no more known serialization
comment|//       related edge cases that appear as log2m gets even larger.
comment|// NOTE: This test completed successfully with log2m<=MAXIMUM_LOG2M_PARAM
comment|//       on 2014-01-30.
DECL|method|assertCardinality
specifier|private
specifier|static
name|void
name|assertCardinality
parameter_list|(
specifier|final
name|HLLType
name|hllType
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Long
argument_list|>
name|items
parameter_list|)
throws|throws
name|CloneNotSupportedException
block|{
for|for
control|(
name|int
name|log2m
init|=
name|MINIMUM_LOG2M_PARAM
init|;
name|log2m
operator|<=
literal|16
condition|;
name|log2m
operator|++
control|)
block|{
for|for
control|(
name|int
name|regw
init|=
name|MINIMUM_REGWIDTH_PARAM
init|;
name|regw
operator|<=
name|MAXIMUM_REGWIDTH_PARAM
condition|;
name|regw
operator|++
control|)
block|{
for|for
control|(
name|int
name|expthr
init|=
name|MINIMUM_EXPTHRESH_PARAM
init|;
name|expthr
operator|<=
name|MAXIMUM_EXPTHRESH_PARAM
condition|;
name|expthr
operator|++
control|)
block|{
for|for
control|(
specifier|final
name|boolean
name|sparse
range|:
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|false
block|}
control|)
block|{
name|HLL
name|hll
init|=
operator|new
name|HLL
argument_list|(
name|log2m
argument_list|,
name|regw
argument_list|,
name|expthr
argument_list|,
name|sparse
argument_list|,
name|hllType
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Long
name|item
range|:
name|items
control|)
block|{
name|hll
operator|.
name|addRaw
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
name|HLL
name|copy
init|=
name|HLL
operator|.
name|fromBytes
argument_list|(
name|hll
operator|.
name|toBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|copy
operator|.
name|cardinality
argument_list|()
argument_list|,
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|copy
operator|.
name|getType
argument_list|()
argument_list|,
name|hll
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|copy
operator|.
name|toBytes
argument_list|()
argument_list|,
name|hll
operator|.
name|toBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|HLL
name|clone
init|=
name|hll
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|clone
operator|.
name|cardinality
argument_list|()
argument_list|,
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clone
operator|.
name|getType
argument_list|()
argument_list|,
name|hll
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|clone
operator|.
name|toBytes
argument_list|()
argument_list|,
name|hll
operator|.
name|toBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit
