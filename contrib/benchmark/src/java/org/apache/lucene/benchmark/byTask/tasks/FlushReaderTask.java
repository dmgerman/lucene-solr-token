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
name|HashMap
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
name|IndexReader
import|;
end_import

begin_class
DECL|class|FlushReaderTask
specifier|public
class|class
name|FlushReaderTask
extends|extends
name|PerfTask
block|{
DECL|field|userData
name|String
name|userData
init|=
literal|null
decl_stmt|;
DECL|method|FlushReaderTask
specifier|public
name|FlushReaderTask
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
name|userData
operator|=
name|params
expr_stmt|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|userData
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|OpenReaderTask
operator|.
name|USER_DATA
argument_list|,
name|userData
argument_list|)
expr_stmt|;
name|reader
operator|.
name|flush
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

