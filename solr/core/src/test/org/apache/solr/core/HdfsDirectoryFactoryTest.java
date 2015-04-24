begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|NoLockFactory
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
name|cloud
operator|.
name|hdfs
operator|.
name|HdfsTestUtil
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
name|util
operator|.
name|NamedList
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
name|DirectoryFactory
operator|.
name|DirContext
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|MockCoreContainer
operator|.
name|MockCoreDescriptor
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
name|junit
operator|.
name|Test
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
name|BadHdfsThreadsFilter
operator|.
name|class
comment|// hdfs currently leaks thread(s)
block|}
argument_list|)
DECL|class|HdfsDirectoryFactoryTest
specifier|public
class|class
name|HdfsDirectoryFactoryTest
extends|extends
name|SolrTestCaseJ4
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
argument_list|,
literal|false
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
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitArgsOrSysPropConfig
specifier|public
name|void
name|testInitArgsOrSysPropConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsDirectoryFactory
name|hdfsFactory
init|=
operator|new
name|HdfsDirectoryFactory
argument_list|()
decl_stmt|;
comment|// test sys prop config
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|HdfsTestUtil
operator|.
name|getURI
argument_list|(
name|dfsCluster
argument_list|)
operator|+
literal|"/solr1"
argument_list|)
expr_stmt|;
name|hdfsFactory
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|dataHome
init|=
name|hdfsFactory
operator|.
name|getDataHome
argument_list|(
operator|new
name|MockCoreDescriptor
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dataHome
operator|.
name|endsWith
argument_list|(
literal|"/solr1/mock/data"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|)
expr_stmt|;
comment|// test init args config
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|HdfsTestUtil
operator|.
name|getURI
argument_list|(
name|dfsCluster
argument_list|)
operator|+
literal|"/solr2"
argument_list|)
expr_stmt|;
name|hdfsFactory
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|dataHome
operator|=
name|hdfsFactory
operator|.
name|getDataHome
argument_list|(
operator|new
name|MockCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataHome
operator|.
name|endsWith
argument_list|(
literal|"/solr2/mock/data"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test sys prop and init args config - init args wins
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|HdfsTestUtil
operator|.
name|getURI
argument_list|(
name|dfsCluster
argument_list|)
operator|+
literal|"/solr1"
argument_list|)
expr_stmt|;
name|hdfsFactory
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|dataHome
operator|=
name|hdfsFactory
operator|.
name|getDataHome
argument_list|(
operator|new
name|MockCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataHome
operator|.
name|endsWith
argument_list|(
literal|"/solr2/mock/data"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|)
expr_stmt|;
comment|// set conf dir by sys prop
name|Path
name|confDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|CONFIG_DIRECTORY
argument_list|,
name|confDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|hdfsFactory
operator|.
name|create
argument_list|(
name|HdfsTestUtil
operator|.
name|getURI
argument_list|(
name|dfsCluster
argument_list|)
operator|+
literal|"/solr"
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|confDir
operator|.
name|toString
argument_list|()
argument_list|,
name|hdfsFactory
operator|.
name|getConfDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// check bool and int getConf impls
name|nl
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|hdfsFactory
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|hdfsFactory
operator|.
name|getConfig
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|hdfsFactory
operator|.
name|getConfig
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|hdfsFactory
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hdfsFactory
operator|.
name|getConfig
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|hdfsFactory
operator|.
name|getConfig
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hdfsFactory
operator|.
name|getConfig
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|hdfsFactory
operator|.
name|getConfig
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|BLOCKCACHE_ENABLED
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|hdfsFactory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

