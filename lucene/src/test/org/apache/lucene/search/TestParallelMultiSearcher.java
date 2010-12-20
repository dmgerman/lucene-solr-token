begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|ArrayList
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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

begin_comment
comment|/**  * Unit tests for the ParallelMultiSearcher   */
end_comment

begin_class
DECL|class|TestParallelMultiSearcher
specifier|public
class|class
name|TestParallelMultiSearcher
extends|extends
name|TestMultiSearcher
block|{
DECL|field|pools
name|List
argument_list|<
name|ExecutorService
argument_list|>
name|pools
init|=
operator|new
name|ArrayList
argument_list|<
name|ExecutorService
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|ExecutorService
name|exec
range|:
name|pools
control|)
name|exec
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|pools
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMultiSearcherInstance
specifier|protected
name|MultiSearcher
name|getMultiSearcherInstance
parameter_list|(
name|Searcher
index|[]
name|searchers
parameter_list|)
throws|throws
name|IOException
block|{
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|pools
operator|.
name|add
argument_list|(
name|exec
argument_list|)
expr_stmt|;
return|return
operator|new
name|ParallelMultiSearcher
argument_list|(
name|exec
argument_list|,
name|searchers
argument_list|)
return|;
block|}
block|}
end_class

end_unit

