begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
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
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|SolrServerException
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
name|embedded
operator|.
name|JettySolrRunner
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|cloud
operator|.
name|AbstractZkTestCase
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
name|common
operator|.
name|SolrDocument
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
name|common
operator|.
name|cloud
operator|.
name|SolrZkClient
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
name|util
operator|.
name|ExternalPaths
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Collector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Compiler
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|FaultTolerance
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|stdlib
operator|.
name|PipeBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ListMultimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import

begin_class
DECL|class|AbstractSolrMorphlineZkTestBase
specifier|public
specifier|abstract
class|class
name|AbstractSolrMorphlineZkTestBase
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|solrHomeDirectory
specifier|private
specifier|static
specifier|final
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|AbstractSolrMorphlineZkTestBase
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|RESOURCES_DIR
specifier|protected
specifier|static
specifier|final
name|String
name|RESOURCES_DIR
init|=
name|ExternalPaths
operator|.
name|SOURCE_HOME
operator|+
literal|"/contrib/solr-mr/src/test-files"
decl_stmt|;
DECL|field|SOLR_INSTANCE_DIR
specifier|private
specifier|static
specifier|final
name|File
name|SOLR_INSTANCE_DIR
init|=
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/solr"
argument_list|)
decl_stmt|;
DECL|field|SOLR_CONF_DIR
specifier|private
specifier|static
specifier|final
name|File
name|SOLR_CONF_DIR
init|=
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/solr/collection1"
argument_list|)
decl_stmt|;
DECL|field|collector
specifier|protected
name|Collector
name|collector
decl_stmt|;
DECL|field|morphline
specifier|protected
name|Command
name|morphline
decl_stmt|;
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|solrHomeDirectory
operator|.
name|getPath
argument_list|()
return|;
block|}
DECL|method|AbstractSolrMorphlineZkTestBase
specifier|public
name|AbstractSolrMorphlineZkTestBase
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|sliceCount
operator|=
literal|3
expr_stmt|;
name|shardCount
operator|=
literal|3
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
name|solrHomeDirectory
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|SOLR_INSTANCE_DIR
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
name|createTempDir
argument_list|()
expr_stmt|;
block|}
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
literal|"host"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfFiles
argument_list|()
expr_stmt|;
name|collector
operator|=
operator|new
name|Collector
argument_list|()
expr_stmt|;
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"host"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
DECL|method|testDistribSearch
specifier|public
name|void
name|testDistribSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testDistribSearch
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|protected
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{
name|Notifications
operator|.
name|notifyCommitTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|super
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|parse
specifier|protected
name|Command
name|parse
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parse
argument_list|(
name|file
argument_list|,
literal|"collection1"
argument_list|)
return|;
block|}
DECL|method|parse
specifier|protected
name|Command
name|parse
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrLocator
name|locator
init|=
operator|new
name|SolrLocator
argument_list|(
name|createMorphlineContext
argument_list|()
argument_list|)
decl_stmt|;
name|locator
operator|.
name|setCollectionName
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|locator
operator|.
name|setZkHost
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|//locator.setServerUrl(cloudJettys.get(0).url); // TODO: download IndexSchema from solrUrl not yet implemented
comment|//locator.setSolrHomeDir(SOLR_HOME_DIR.getPath());
name|Config
name|config
init|=
operator|new
name|Compiler
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
argument_list|,
name|locator
operator|.
name|toConfig
argument_list|(
literal|"SOLR_LOCATOR"
argument_list|)
argument_list|)
decl_stmt|;
name|config
operator|=
name|config
operator|.
name|getConfigList
argument_list|(
literal|"morphlines"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|createMorphline
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|createMorphline
specifier|private
name|Command
name|createMorphline
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
return|return
operator|new
name|PipeBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|config
argument_list|,
literal|null
argument_list|,
name|collector
argument_list|,
name|createMorphlineContext
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createMorphlineContext
specifier|private
name|MorphlineContext
name|createMorphlineContext
parameter_list|()
block|{
return|return
operator|new
name|MorphlineContext
operator|.
name|Builder
argument_list|()
operator|.
name|setExceptionHandler
argument_list|(
operator|new
name|FaultTolerance
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
name|SolrServerException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMetricRegistry
argument_list|(
operator|new
name|MetricRegistry
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|startSession
specifier|protected
name|void
name|startSession
parameter_list|()
block|{
name|Notifications
operator|.
name|notifyStartSession
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|protected
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
parameter_list|(
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|iter
parameter_list|)
block|{
name|SolrDocument
name|doc
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Record
name|record
init|=
name|toRecord
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|record
operator|.
name|removeAll
argument_list|(
literal|"_version_"
argument_list|)
expr_stmt|;
comment|// the values of this field are unknown and internal to solr
return|return
name|record
operator|.
name|getFields
argument_list|()
return|;
block|}
DECL|method|toRecord
specifier|private
name|Record
name|toRecord
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
block|{
name|Record
name|record
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|doc
operator|.
name|keySet
argument_list|()
control|)
block|{
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|replaceValues
argument_list|(
name|key
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|record
return|;
block|}
annotation|@
name|Override
DECL|method|createJetty
specifier|public
name|JettySolrRunner
name|createJetty
parameter_list|(
name|File
name|solrHome
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|shardList
parameter_list|,
name|String
name|solrConfigOverride
parameter_list|,
name|String
name|schemaOverride
parameter_list|)
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|context
argument_list|,
literal|0
argument_list|,
name|solrConfigOverride
argument_list|,
name|schemaOverride
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|setShards
argument_list|(
name|shardList
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"collection"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"collection"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|method|putConfig
specifier|private
specifier|static
name|void
name|putConfig
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|File
name|solrhome
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
name|name
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|putConfig
specifier|private
specifier|static
name|void
name|putConfig
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|File
name|solrhome
parameter_list|,
name|String
name|srcName
parameter_list|,
name|String
name|destName
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|solrhome
argument_list|,
literal|"conf"
operator|+
name|File
operator|.
name|separator
operator|+
name|srcName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// LOG.info("skipping " + file.getAbsolutePath() +
comment|// " because it doesn't exist");
return|return;
block|}
name|String
name|destPath
init|=
literal|"/configs/conf1/"
operator|+
name|destName
decl_stmt|;
comment|// LOG.info("put " + file.getAbsolutePath() + " to " + destPath);
name|zkClient
operator|.
name|makePath
argument_list|(
name|destPath
argument_list|,
name|file
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|uploadConfFiles
specifier|private
name|void
name|uploadConfFiles
parameter_list|()
throws|throws
name|Exception
block|{
comment|// upload our own config files
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"solrconfig.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"elevate.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_en.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_ar.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_bg.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_ca.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_cz.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_da.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_el.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_es.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_eu.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_de.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_fa.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_fi.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_fr.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_ga.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_gl.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_hi.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_hu.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_hy.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_id.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_it.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_ja.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_lv.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_nl.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_no.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_pt.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_ro.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_ru.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_sv.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_th.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stopwords_tr.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/contractions_ca.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/contractions_fr.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/contractions_ga.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/contractions_it.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/stemdict_nl.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"lang/hyphenations_ga.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"stopwords.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"protwords.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"currency.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"open-exchange-rates.json"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"mapping-ISOLatin1Accent.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"old_synonyms.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|SOLR_CONF_DIR
argument_list|,
literal|"synonyms.txt"
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

