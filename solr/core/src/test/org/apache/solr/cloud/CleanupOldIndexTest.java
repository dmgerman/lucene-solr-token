begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
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
name|DocCollection
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
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|SnapShooter
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

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
DECL|class|CleanupOldIndexTest
specifier|public
class|class
name|CleanupOldIndexTest
extends|extends
name|SolrCloudTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we restart jetty and expect to find on disk data - need a local fs directory
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|configureCluster
argument_list|(
literal|2
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf1"
argument_list|,
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"cloud-dynamic"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
DECL|field|COLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION
init|=
literal|"oldindextest"
decl_stmt|;
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|COLLECTION
argument_list|,
literal|"conf1"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|setDefaultCollection
argument_list|(
name|COLLECTION
argument_list|)
expr_stmt|;
comment|// TODO make this configurable on StoppableIndexingThread
name|int
index|[]
name|maxDocList
init|=
operator|new
name|int
index|[]
block|{
literal|300
block|,
literal|700
block|,
literal|1200
block|}
decl_stmt|;
name|int
name|maxDoc
init|=
name|maxDocList
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDocList
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
decl_stmt|;
name|StoppableIndexingThread
name|indexThread
init|=
operator|new
name|StoppableIndexingThread
argument_list|(
literal|null
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
literal|"1"
argument_list|,
literal|true
argument_list|,
name|maxDoc
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// give some time to index...
name|int
index|[]
name|waitTimes
init|=
operator|new
name|int
index|[]
block|{
literal|200
block|,
literal|2000
block|,
literal|3000
block|}
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTimes
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|waitTimes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// create some "old" index directories
name|JettySolrRunner
name|jetty
init|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|CoreContainer
name|coreContainer
init|=
name|jetty
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|File
name|dataDir
init|=
literal|null
decl_stmt|;
try|try
init|(
name|SolrCore
name|solrCore
init|=
name|coreContainer
operator|.
name|getCore
argument_list|(
name|coreContainer
operator|.
name|getCoreDescriptors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
init|)
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|solrCore
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|dataDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|msInDay
init|=
literal|60
operator|*
literal|60
operator|*
literal|24L
decl_stmt|;
name|String
name|timestamp1
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|SnapShooter
operator|.
name|DATE_FMT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|1
operator|*
name|msInDay
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|timestamp2
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|SnapShooter
operator|.
name|DATE_FMT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|2
operator|*
name|msInDay
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|oldIndexDir1
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index."
operator|+
name|timestamp1
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|oldIndexDir1
argument_list|)
expr_stmt|;
name|File
name|oldIndexDir2
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index."
operator|+
name|timestamp2
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|oldIndexDir2
argument_list|)
expr_stmt|;
comment|// verify the "old" index directories exist
name|assertTrue
argument_list|(
name|oldIndexDir1
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|oldIndexDir2
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// bring shard replica down
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// wait a moment - lets allow some docs to be indexed so replication time is non 0
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTimes
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|waitTimes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// bring shard replica up
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// make sure replication can start
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
comment|// stop indexing threads
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|waitForState
argument_list|(
name|COLLECTION
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|oldIndexDir1
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|oldIndexDir2
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

