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
name|ArrayList
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
name|List
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

begin_class
DECL|class|TestFrequencyTrackingRingBuffer
specifier|public
class|class
name|TestFrequencyTrackingRingBuffer
extends|extends
name|LuceneTestCase
block|{
DECL|method|assertBuffer
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|assertBuffer
parameter_list|(
name|FrequencyTrackingRingBuffer
argument_list|<
name|T
argument_list|>
name|buffer
parameter_list|,
name|int
name|maxSize
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|items
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|recentItems
decl_stmt|;
if|if
condition|(
name|items
operator|.
name|size
argument_list|()
operator|<=
name|maxSize
condition|)
block|{
name|recentItems
operator|=
name|items
expr_stmt|;
block|}
else|else
block|{
name|recentItems
operator|=
name|items
operator|.
name|subList
argument_list|(
name|items
operator|.
name|size
argument_list|()
operator|-
name|maxSize
argument_list|,
name|items
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|expectedFrequencies
init|=
operator|new
name|HashMap
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|T
name|item
range|:
name|recentItems
control|)
block|{
specifier|final
name|Integer
name|freq
init|=
name|expectedFrequencies
operator|.
name|get
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|freq
operator|==
literal|null
condition|)
block|{
name|expectedFrequencies
operator|.
name|put
argument_list|(
name|item
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectedFrequencies
operator|.
name|put
argument_list|(
name|item
argument_list|,
name|freq
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expectedFrequencies
argument_list|,
name|buffer
operator|.
name|asFrequencyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
specifier|final
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|100
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|maxSize
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numitems
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxitem
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|items
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|FrequencyTrackingRingBuffer
argument_list|<
name|Integer
argument_list|>
name|buffer
init|=
operator|new
name|FrequencyTrackingRingBuffer
argument_list|<>
argument_list|(
name|maxSize
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
name|numitems
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|Integer
name|item
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxitem
argument_list|)
decl_stmt|;
name|items
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
name|assertBuffer
argument_list|(
name|buffer
argument_list|,
name|maxSize
argument_list|,
name|items
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

