begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrIgnoredThreadsFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
import|;
end_import

begin_class
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|SolrIgnoredThreadsFilter
operator|.
name|class
block|}
argument_list|)
DECL|class|TestEmbeddedSolrServer
specifier|public
class|class
name|TestEmbeddedSolrServer
extends|extends
name|AbstractEmbeddedSolrServerTestCase
block|{
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestEmbeddedSolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getSolrCore1
specifier|protected
name|EmbeddedSolrServer
name|getSolrCore1
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrXml
specifier|protected
name|File
name|getSolrXml
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|,
literal|"solr.xml"
argument_list|)
return|;
block|}
DECL|method|testGetCoreContainer
specifier|public
name|void
name|testGetCoreContainer
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cores
argument_list|,
operator|(
operator|(
name|EmbeddedSolrServer
operator|)
name|getSolrCore0
argument_list|()
operator|)
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cores
argument_list|,
operator|(
operator|(
name|EmbeddedSolrServer
operator|)
name|getSolrCore1
argument_list|()
operator|)
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShutdown
specifier|public
name|void
name|testShutdown
parameter_list|()
block|{
name|EmbeddedSolrServer
name|solrServer
init|=
operator|(
name|EmbeddedSolrServer
operator|)
name|getSolrCore0
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrCore
argument_list|>
name|solrCores
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrCore
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrCore
name|solrCore
range|:
name|cores
operator|.
name|getCores
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|solrCore
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|solrCores
operator|.
name|add
argument_list|(
name|solrCore
argument_list|)
expr_stmt|;
block|}
name|solrServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrCore
name|solrCore
range|:
name|solrCores
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|solrCore
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

