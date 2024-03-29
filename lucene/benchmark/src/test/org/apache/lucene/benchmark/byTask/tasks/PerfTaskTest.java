begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|benchmark
operator|.
name|BenchmarkTestCase
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import

begin_comment
comment|/** Tests the functionality of the abstract {@link PerfTask}. */
end_comment

begin_class
DECL|class|PerfTaskTest
specifier|public
class|class
name|PerfTaskTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|class|MyPerfTask
specifier|private
specifier|static
specifier|final
class|class
name|MyPerfTask
extends|extends
name|PerfTask
block|{
DECL|method|MyPerfTask
specifier|public
name|MyPerfTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|0
return|;
block|}
DECL|method|getLogStep
specifier|public
name|int
name|getLogStep
parameter_list|()
block|{
return|return
name|logStep
return|;
block|}
block|}
DECL|method|createPerfRunData
specifier|private
name|PerfRunData
name|createPerfRunData
parameter_list|(
name|boolean
name|setLogStep
parameter_list|,
name|int
name|logStepVal
parameter_list|,
name|boolean
name|setTaskLogStep
parameter_list|,
name|int
name|taskLogStepVal
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|setLogStep
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"log.step"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|logStepVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|setTaskLogStep
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"log.step.MyPerf"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|taskLogStepVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|props
operator|.
name|setProperty
argument_list|(
literal|"directory"
argument_list|,
literal|"RAMDirectory"
argument_list|)
expr_stmt|;
comment|// no accidental FS dir.
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
return|return
operator|new
name|PerfRunData
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|doLogStepTest
specifier|private
name|void
name|doLogStepTest
parameter_list|(
name|boolean
name|setLogStep
parameter_list|,
name|int
name|logStepVal
parameter_list|,
name|boolean
name|setTaskLogStep
parameter_list|,
name|int
name|taskLogStepVal
parameter_list|,
name|int
name|expLogStepValue
parameter_list|)
throws|throws
name|Exception
block|{
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|setLogStep
argument_list|,
name|logStepVal
argument_list|,
name|setTaskLogStep
argument_list|,
name|taskLogStepVal
argument_list|)
decl_stmt|;
name|MyPerfTask
name|mpt
init|=
operator|new
name|MyPerfTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expLogStepValue
argument_list|,
name|mpt
operator|.
name|getLogStep
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLogStep
specifier|public
name|void
name|testLogStep
parameter_list|()
throws|throws
name|Exception
block|{
name|doLogStepTest
argument_list|(
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
name|PerfTask
operator|.
name|DEFAULT_LOG_STEP
argument_list|)
expr_stmt|;
name|doLogStepTest
argument_list|(
literal|true
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|doLogStepTest
argument_list|(
literal|true
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|doLogStepTest
argument_list|(
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|,
operator|-
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|doLogStepTest
argument_list|(
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

