begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  * Simply waits for the specified (via the parameter) amount  * of time.  For example Wait(30s) waits for 30 seconds.  * This is useful with background tasks to control how long  * the tasks run.  *  *<p>You can specify h, m, or s (hours, minutes, seconds) as  *the trailing time unit.  No unit is interpreted as  *seconds.</p>  */
end_comment

begin_class
DECL|class|WaitTask
specifier|public
class|class
name|WaitTask
extends|extends
name|PerfTask
block|{
DECL|field|waitTimeSec
specifier|private
name|double
name|waitTimeSec
decl_stmt|;
DECL|method|WaitTask
specifier|public
name|WaitTask
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
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|int
name|multiplier
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|endsWith
argument_list|(
literal|"s"
argument_list|)
condition|)
block|{
name|multiplier
operator|=
literal|1
expr_stmt|;
name|params
operator|=
name|params
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|params
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|params
operator|.
name|endsWith
argument_list|(
literal|"m"
argument_list|)
condition|)
block|{
name|multiplier
operator|=
literal|60
expr_stmt|;
name|params
operator|=
name|params
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|params
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|params
operator|.
name|endsWith
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
name|multiplier
operator|=
literal|3600
expr_stmt|;
name|params
operator|=
name|params
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|params
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Assume seconds
name|multiplier
operator|=
literal|1
expr_stmt|;
block|}
name|waitTimeSec
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|params
argument_list|)
operator|*
name|multiplier
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"you must specify the wait time, eg: 10.0s, 4.5m, 2h"
argument_list|)
throw|;
block|}
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
name|Thread
operator|.
name|sleep
argument_list|(
call|(
name|long
call|)
argument_list|(
literal|1000
operator|*
name|waitTimeSec
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

