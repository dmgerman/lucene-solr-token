begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|hdfs
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|RecoveryZkTest
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
name|ZkConfigManager
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
name|BadHdfsThreadsFilter
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

begin_class
annotation|@
name|Slow
comment|//@Nightly
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
name|BadHdfsThreadsFilter
operator|.
name|class
comment|// hdfs currently leaks thread(s)
block|}
argument_list|)
DECL|class|HdfsRecoveryZkTest
specifier|public
class|class
name|HdfsRecoveryZkTest
extends|extends
name|RecoveryZkTest
block|{
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
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
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.blockcache.blocksperbank"
argument_list|,
literal|"2048"
argument_list|)
expr_stmt|;
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|()
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|configset
argument_list|(
literal|"cloud-hdfs"
argument_list|)
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|HdfsTestUtil
operator|.
name|getDataDir
argument_list|(
name|dfsCluster
argument_list|,
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// need to close before the MiniDFSCluster
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

