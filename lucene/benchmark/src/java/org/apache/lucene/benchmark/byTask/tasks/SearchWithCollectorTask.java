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
name|QueryMaker
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Collector
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
name|search
operator|.
name|TopScoreDocCollector
import|;
end_import

begin_comment
comment|/**  * Does search w/ a custom collector  */
end_comment

begin_class
DECL|class|SearchWithCollectorTask
specifier|public
class|class
name|SearchWithCollectorTask
extends|extends
name|SearchTask
block|{
DECL|field|clnName
specifier|protected
name|String
name|clnName
decl_stmt|;
DECL|method|SearchWithCollectorTask
specifier|public
name|SearchWithCollectorTask
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
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|//check to make sure either the doc is being stored
name|PerfRunData
name|runData
init|=
name|getRunData
argument_list|()
decl_stmt|;
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|clnName
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"collector.class"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|withCollector
specifier|public
name|boolean
name|withCollector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|createCollector
specifier|protected
name|Collector
name|createCollector
parameter_list|()
throws|throws
name|Exception
block|{
name|Collector
name|collector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|clnName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"topScoreDocOrdered"
argument_list|)
operator|==
literal|true
condition|)
block|{
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numHits
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clnName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"topScoreDocUnOrdered"
argument_list|)
operator|==
literal|true
condition|)
block|{
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numHits
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clnName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|collector
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|clnName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Collector
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|collector
operator|=
name|super
operator|.
name|createCollector
argument_list|()
expr_stmt|;
block|}
return|return
name|collector
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryMaker
specifier|public
name|QueryMaker
name|getQueryMaker
parameter_list|()
block|{
return|return
name|getRunData
argument_list|()
operator|.
name|getQueryMaker
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|withRetrieve
specifier|public
name|boolean
name|withRetrieve
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|withSearch
specifier|public
name|boolean
name|withSearch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|withTraverse
specifier|public
name|boolean
name|withTraverse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|withWarm
specifier|public
name|boolean
name|withWarm
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

