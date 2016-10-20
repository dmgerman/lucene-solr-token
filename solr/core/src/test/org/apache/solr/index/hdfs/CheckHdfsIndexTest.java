begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.index.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|index
operator|.
name|hdfs
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|fs
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
name|index
operator|.
name|BaseTestCheckIndex
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|store
operator|.
name|hdfs
operator|.
name|HdfsDirectory
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
name|After
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
name|Ignore
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
DECL|class|CheckHdfsIndexTest
specifier|public
class|class
name|CheckHdfsIndexTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|path
specifier|private
specifier|static
name|Path
name|path
decl_stmt|;
DECL|field|testCheckIndex
specifier|private
name|BaseTestCheckIndex
name|testCheckIndex
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|method|CheckHdfsIndexTest
specifier|public
name|CheckHdfsIndexTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|fixShardCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|testCheckIndex
operator|=
operator|new
name|BaseTestCheckIndex
argument_list|()
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
name|path
operator|=
operator|new
name|Path
argument_list|(
name|HdfsTestUtil
operator|.
name|getURI
argument_list|(
name|dfsCluster
argument_list|)
operator|+
literal|"/solr/"
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
name|dfsCluster
operator|=
literal|null
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
name|Configuration
name|conf
init|=
name|HdfsTestUtil
operator|.
name|getClientConfiguration
argument_list|(
name|dfsCluster
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|directory
operator|=
operator|new
name|HdfsDirectory
argument_list|(
name|path
argument_list|,
name|conf
argument_list|)
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
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDataDir
specifier|protected
name|String
name|getDataDir
parameter_list|(
name|String
name|dataDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|HdfsTestUtil
operator|.
name|getDataDir
argument_list|(
name|dfsCluster
argument_list|,
name|dataDir
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
decl_stmt|;
block|{
name|SolrClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|()
operator|.
name|setRequestHandler
argument_list|(
literal|"/admin/system"
argument_list|)
argument_list|)
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|coreInfo
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
decl_stmt|;
name|String
name|indexDir
init|=
call|(
name|String
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|coreInfo
operator|.
name|get
argument_list|(
literal|"directory"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
operator|+
literal|"/index"
decl_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
name|indexDir
block|}
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"CheckHdfsIndex return status"
argument_list|,
literal|0
argument_list|,
name|CheckHdfsIndex
operator|.
name|doMain
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeletedDocs
specifier|public
name|void
name|testDeletedDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|testCheckIndex
operator|.
name|testDeletedDocs
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBogusTermVectors
specifier|public
name|void
name|testBogusTermVectors
parameter_list|()
throws|throws
name|IOException
block|{
name|testCheckIndex
operator|.
name|testBogusTermVectors
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChecksumsOnly
specifier|public
name|void
name|testChecksumsOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|testCheckIndex
operator|.
name|testChecksumsOnly
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChecksumsOnlyVerbose
specifier|public
name|void
name|testChecksumsOnlyVerbose
parameter_list|()
throws|throws
name|IOException
block|{
name|testCheckIndex
operator|.
name|testChecksumsOnlyVerbose
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"We explicitly use a NoLockFactory, so this test doesn't make sense."
argument_list|)
DECL|method|testObtainsLock
specifier|public
name|void
name|testObtainsLock
parameter_list|()
throws|throws
name|IOException
block|{
name|testCheckIndex
operator|.
name|testObtainsLock
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

