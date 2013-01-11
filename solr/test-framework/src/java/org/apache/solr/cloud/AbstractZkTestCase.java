begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
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
name|ZkStateReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_comment
comment|/**  * Base test class for ZooKeeper tests.  */
end_comment

begin_class
DECL|class|AbstractZkTestCase
specifier|public
specifier|abstract
class|class
name|AbstractZkTestCase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|10000
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
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
name|AbstractZkTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOLRHOME
specifier|public
specifier|static
name|File
name|SOLRHOME
decl_stmt|;
static|static
block|{
try|try
block|{
name|SOLRHOME
operator|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"TEST_HOME() does not exist - solrj test?"
argument_list|)
expr_stmt|;
comment|// solrj tests not working with TEST_HOME()
comment|// must override getSolrHome
block|}
block|}
DECL|field|zkServer
specifier|protected
specifier|static
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
specifier|static
name|String
name|zkDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|azt_beforeClass
specifier|public
specifier|static
name|void
name|azt_beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|createTempDir
argument_list|()
expr_stmt|;
name|zkDir
operator|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.port"
argument_list|,
literal|"0000"
argument_list|)
expr_stmt|;
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|SOLRHOME
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|buildZooKeeper
specifier|static
name|void
name|buildZooKeeper
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|zkAddress
parameter_list|,
name|String
name|config
parameter_list|,
name|String
name|schema
parameter_list|)
throws|throws
name|Exception
block|{
name|buildZooKeeper
argument_list|(
name|zkHost
argument_list|,
name|zkAddress
argument_list|,
name|SOLRHOME
argument_list|,
name|config
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
comment|// static to share with distrib test
DECL|method|buildZooKeeper
specifier|public
specifier|static
name|void
name|buildZooKeeper
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|zkAddress
parameter_list|,
name|File
name|solrhome
parameter_list|,
name|String
name|config
parameter_list|,
name|String
name|schema
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/solr"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkAddress
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"configName"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
specifier|final
name|ZkNodeProps
name|zkProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/collection1"
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/collection1/shards"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/control_collection"
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/control_collection/shards"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// for now, always upload the config and schema to the canonical names
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
name|config
argument_list|,
literal|"solrconfig.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
name|schema
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
literal|"stopwords.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
literal|"protwords.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
literal|"currency.xml"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
literal|"open-exchange-rates.json"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
literal|"mapping-ISOLatin1Accent.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
argument_list|,
literal|"old_synonyms.txt"
argument_list|)
expr_stmt|;
name|putConfig
argument_list|(
name|zkClient
argument_list|,
name|solrhome
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
specifier|final
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
specifier|final
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
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
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
name|log
operator|.
name|info
argument_list|(
literal|"skipping "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" because it doesn't exist"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|destPath
init|=
literal|"/configs/conf1/"
operator|+
name|destName
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"put "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" to "
operator|+
name|destPath
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|DEBUG
condition|)
block|{
name|printLayout
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|azt_afterClass
specifier|public
specifier|static
name|void
name|azt_afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"jetty.port"
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|=
literal|null
expr_stmt|;
name|zkDir
operator|=
literal|null
expr_stmt|;
comment|// wait just a bit for any zk client threads to outlast timeout
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
DECL|method|printLayout
specifier|protected
name|void
name|printLayout
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|makeSolrZkNode
specifier|public
specifier|static
name|void
name|makeSolrZkNode
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/solr"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|tryCleanSolrZkNode
specifier|public
specifier|static
name|void
name|tryCleanSolrZkNode
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|tryCleanPath
argument_list|(
name|zkHost
argument_list|,
literal|"/solr"
argument_list|)
expr_stmt|;
block|}
DECL|method|tryCleanPath
specifier|static
name|void
name|tryCleanPath
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|clean
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

