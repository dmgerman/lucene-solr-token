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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_comment
comment|/**  * Optimize the index.  *<br>Other side effects: none.  */
end_comment

begin_class
DECL|class|OptimizeTask
specifier|public
class|class
name|OptimizeTask
extends|extends
name|PerfTask
block|{
DECL|method|OptimizeTask
specifier|public
name|OptimizeTask
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
DECL|field|maxNumSegments
name|int
name|maxNumSegments
init|=
literal|1
decl_stmt|;
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
name|IndexWriter
name|iw
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
name|iw
operator|.
name|optimize
argument_list|(
name|maxNumSegments
argument_list|)
expr_stmt|;
comment|//System.out.println("optimize called");
return|return
literal|1
return|;
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
name|maxNumSegments
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|params
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
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

