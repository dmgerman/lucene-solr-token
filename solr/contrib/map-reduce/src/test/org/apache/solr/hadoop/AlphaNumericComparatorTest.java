begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package

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
name|junit
operator|.
name|Assert
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

begin_class
DECL|class|AlphaNumericComparatorTest
specifier|public
class|class
name|AlphaNumericComparatorTest
extends|extends
name|Assert
block|{
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
block|{
name|Comparator
name|c
init|=
operator|new
name|AlphaNumericComparator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard1"
argument_list|,
literal|"shard1"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|//assertTrue(c.compare("shard01", "shard1") == 0);
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard10"
argument_list|,
literal|"shard10"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard1"
argument_list|,
literal|"shard2"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard9"
argument_list|,
literal|"shard10"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard09"
argument_list|,
literal|"shard10"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard019"
argument_list|,
literal|"shard10"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard10"
argument_list|,
literal|"shard11"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard10z"
argument_list|,
literal|"shard10z"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard10z"
argument_list|,
literal|"shard11z"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard10a"
argument_list|,
literal|"shard10z"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard10z"
argument_list|,
literal|"shard10a"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard1z"
argument_list|,
literal|"shard1z"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|compare
argument_list|(
literal|"shard2"
argument_list|,
literal|"shard1"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

