begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|SolrTestCaseJ4
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|CoreContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|AbstractEmbeddedSolrServerTestCase
specifier|public
specifier|abstract
class|class
name|AbstractEmbeddedSolrServerTestCase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|SOLR_HOME
specifier|protected
specifier|static
specifier|final
name|Path
name|SOLR_HOME
init|=
name|getFile
argument_list|(
literal|"solrj/solr/shared"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|cores
specifier|protected
name|CoreContainer
name|cores
init|=
literal|null
decl_stmt|;
DECL|field|tempDir
specifier|protected
name|File
name|tempDir
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|SOLR_HOME
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"configSetBase"
argument_list|,
name|SOLR_HOME
operator|.
name|resolve
argument_list|(
literal|"../configsets"
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Solr home: "
operator|+
name|SOLR_HOME
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//The index is always stored within a temporary directory
name|tempDir
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"data1"
argument_list|)
decl_stmt|;
name|File
name|dataDir2
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"data2"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"dataDir1"
argument_list|,
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"dataDir2"
argument_list|,
name|dataDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tempDir"
argument_list|,
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cores
operator|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|SOLR_HOME
argument_list|,
name|getSolrXml
argument_list|()
argument_list|)
expr_stmt|;
comment|//cores.setPersistent(false);
block|}
DECL|method|getSolrXml
specifier|protected
name|Path
name|getSolrXml
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|SOLR_HOME
operator|.
name|resolve
argument_list|(
literal|"solr.xml"
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"dataDir1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"dataDir2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|)
expr_stmt|;
name|deleteAdditionalFiles
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteAdditionalFiles
specifier|protected
name|void
name|deleteAdditionalFiles
parameter_list|()
block|{    }
DECL|method|getSolrCore0
specifier|protected
name|SolrClient
name|getSolrCore0
parameter_list|()
block|{
return|return
name|getSolrCore
argument_list|(
literal|"core0"
argument_list|)
return|;
block|}
DECL|method|getSolrCore1
specifier|protected
name|SolrClient
name|getSolrCore1
parameter_list|()
block|{
return|return
name|getSolrCore
argument_list|(
literal|"core1"
argument_list|)
return|;
block|}
DECL|method|getSolrCore
specifier|protected
name|SolrClient
name|getSolrCore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

