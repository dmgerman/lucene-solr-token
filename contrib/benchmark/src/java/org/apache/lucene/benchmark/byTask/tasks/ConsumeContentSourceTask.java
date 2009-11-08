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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|ContentSource
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
name|feeds
operator|.
name|DocData
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
comment|/**  * Consumes a {@link org.apache.lucene.benchmark.byTask.feeds.ContentSource}.  * Supports the following parameters:  *<ul>  *<li>content.source - the content source to use. (mandatory)  *</ul>  */
end_comment

begin_class
DECL|class|ConsumeContentSourceTask
specifier|public
class|class
name|ConsumeContentSourceTask
extends|extends
name|PerfTask
block|{
DECL|field|source
specifier|private
name|ContentSource
name|source
decl_stmt|;
DECL|field|dd
specifier|private
name|DocData
name|dd
init|=
operator|new
name|DocData
argument_list|()
decl_stmt|;
DECL|method|ConsumeContentSourceTask
specifier|public
name|ConsumeContentSourceTask
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
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|String
name|sourceClass
init|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceClass
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"content.source must be defined"
argument_list|)
throw|;
block|}
try|try
block|{
name|source
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|sourceClass
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|ContentSource
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|source
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
return|return
literal|"read "
operator|+
name|recsCount
operator|+
literal|" documents from the content source"
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
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
name|dd
operator|=
name|source
operator|.
name|getNextDocData
argument_list|(
name|dd
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

