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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Check large and special graphs.   */
end_comment

begin_class
DECL|class|TestRamUsageEstimatorOnWildAnimals
specifier|public
class|class
name|TestRamUsageEstimatorOnWildAnimals
extends|extends
name|LuceneTestCase
block|{
DECL|class|ListElement
specifier|public
specifier|static
class|class
name|ListElement
block|{
DECL|field|next
name|ListElement
name|next
decl_stmt|;
block|}
DECL|method|testOverflowMaxChainLength
specifier|public
name|void
name|testOverflowMaxChainLength
parameter_list|()
block|{
name|int
name|UPPERLIMIT
init|=
literal|100000
decl_stmt|;
name|int
name|lower
init|=
literal|0
decl_stmt|;
name|int
name|upper
init|=
name|UPPERLIMIT
decl_stmt|;
while|while
condition|(
name|lower
operator|+
literal|1
operator|<
name|upper
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lower
operator|+
name|upper
operator|)
operator|/
literal|2
decl_stmt|;
try|try
block|{
name|ListElement
name|first
init|=
operator|new
name|ListElement
argument_list|()
decl_stmt|;
name|ListElement
name|last
init|=
name|first
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
name|mid
condition|;
name|i
operator|++
control|)
block|{
name|last
operator|=
operator|(
name|last
operator|.
name|next
operator|=
operator|new
name|ListElement
argument_list|()
operator|)
expr_stmt|;
block|}
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|first
argument_list|)
expr_stmt|;
comment|// cause SOE or pass.
name|lower
operator|=
name|mid
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StackOverflowError
name|e
parameter_list|)
block|{
name|upper
operator|=
name|mid
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lower
operator|+
literal|1
operator|<
name|UPPERLIMIT
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Max object chain length till stack overflow: "
operator|+
name|lower
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

