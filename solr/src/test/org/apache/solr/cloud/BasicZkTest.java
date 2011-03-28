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
name|index
operator|.
name|LogMergePolicy
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
name|params
operator|.
name|CommonParams
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
name|update
operator|.
name|SolrIndexWriter
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BasicZkTest
specifier|public
class|class
name|BasicZkTest
extends|extends
name|AbstractZkTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"CLOUD_UPDATE_DELAY"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test using ZooKeeper
name|assertTrue
argument_list|(
literal|"Not using ZooKeeper"
argument_list|,
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|isZooKeeperAware
argument_list|()
argument_list|)
expr_stmt|;
name|ZkController
name|zkController
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
comment|// test merge factor picked up
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrIndexWriter
name|writer
init|=
operator|new
name|SolrIndexWriter
argument_list|(
literal|"testWriter"
argument_list|,
name|core
operator|.
name|getNewIndexDir
argument_list|()
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
literal|false
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|mainIndexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Mergefactor was not picked up"
argument_list|,
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|getMergeFactor
argument_list|()
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test query on empty index"
argument_list|,
name|req
argument_list|(
literal|"qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test escaping of ";"
name|assertU
argument_list|(
literal|"deleting 42 for no reason at all"
argument_list|,
name|delI
argument_list|(
literal|"42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"adding doc#42"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"aa;bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"does commit work?"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"backslash escaping semicolon"
argument_list|,
name|req
argument_list|(
literal|"id:42 AND val_s:aa\\;bb"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"quote escaping semicolon"
argument_list|,
name|req
argument_list|(
literal|"id:42 AND val_s:\"aa;bb\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no escaping semicolon"
argument_list|,
name|req
argument_list|(
literal|"id:42 AND val_s:aa"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test overwrite default of true
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"AAA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"BBB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[.='BBB']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"CCC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"DDD"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[.='DDD']"
argument_list|)
expr_stmt|;
comment|// test deletes
name|String
index|[]
name|adds
init|=
operator|new
name|String
index|[]
block|{
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"105"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"102"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"103"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|, }
decl_stmt|;
for|for
control|(
name|String
name|a
range|:
name|adds
control|)
block|{
name|assertU
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
comment|// try a reconnect from disconnect
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
comment|// ensure zk still thinks node is up
name|assertTrue
argument_list|(
name|zkController
operator|.
name|getCloudState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|zkController
operator|.
name|getCloudState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// test maxint
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[100 TO 110]"
argument_list|,
literal|"rows"
argument_list|,
literal|"2147483647"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
comment|// test big limit
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[100 TO 111]"
argument_list|,
literal|"rows"
argument_list|,
literal|"1147483647"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"102"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"105"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"CLOUD_UPDATE_DELAY"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

