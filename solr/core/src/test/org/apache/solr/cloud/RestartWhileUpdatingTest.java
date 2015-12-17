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
name|IOException
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
name|Nightly
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
name|SolrTestCaseJ4
operator|.
name|SuppressObjectReleaseTracker
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
name|common
operator|.
name|SolrInputDocument
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
name|TestInjection
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

begin_class
annotation|@
name|Slow
annotation|@
name|Nightly
annotation|@
name|SuppressObjectReleaseTracker
argument_list|(
name|bugUrl
operator|=
literal|"this is a purposely leaky test"
argument_list|)
DECL|class|RestartWhileUpdatingTest
specifier|public
class|class
name|RestartWhileUpdatingTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
comment|//private static final String DISTRIB_UPDATE_CHAIN = "distrib-update-chain";
DECL|field|threads
specifier|private
name|List
argument_list|<
name|StoppableIndexingThread
argument_list|>
name|threads
decl_stmt|;
DECL|field|stopExpire
specifier|private
specifier|volatile
name|boolean
name|stopExpire
init|=
literal|false
decl_stmt|;
DECL|method|RestartWhileUpdatingTest
specifier|public
name|RestartWhileUpdatingTest
parameter_list|()
throws|throws
name|Exception
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
literal|3
argument_list|)
expr_stmt|;
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
DECL|field|fieldNames
specifier|public
specifier|static
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|"f_i"
block|,
literal|"f_f"
block|,
literal|"f_d"
block|,
literal|"f_l"
block|,
literal|"f_dt"
block|}
decl_stmt|;
DECL|field|randVals
specifier|public
specifier|static
name|RandVal
index|[]
name|randVals
init|=
operator|new
name|RandVal
index|[]
block|{
name|rint
block|,
name|rfloat
block|,
name|rdouble
block|,
name|rlong
block|,
name|rdate
block|}
decl_stmt|;
DECL|method|getFieldNames
specifier|protected
name|String
index|[]
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
DECL|method|getRandValues
specifier|protected
name|RandVal
index|[]
name|getRandValues
parameter_list|()
block|{
return|return
name|randVals
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeRestartWhileUpdatingTest
specifier|public
specifier|static
name|void
name|beforeRestartWhileUpdatingTest
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"leaderVoteWait"
argument_list|,
literal|"300000"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.autoCommit.maxTime"
argument_list|,
literal|"30000"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.autoSoftCommit.maxTime"
argument_list|,
literal|"3000"
argument_list|)
expr_stmt|;
name|TestInjection
operator|.
name|nonGracefullClose
operator|=
literal|"true:60"
expr_stmt|;
name|TestInjection
operator|.
name|failReplicaRequests
operator|=
literal|"true:03"
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterRestartWhileUpdatingTest
specifier|public
specifier|static
name|void
name|afterRestartWhileUpdatingTest
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"leaderVoteWait"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.autoCommit.maxTime"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.autoSoftCommit.maxTime"
argument_list|)
expr_stmt|;
name|TestInjection
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
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
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// start a couple indexing threads
name|int
index|[]
name|maxDocList
init|=
operator|new
name|int
index|[]
block|{
literal|5000
block|,
literal|10000
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
name|int
name|numThreads
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|1
decl_stmt|;
name|threads
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numThreads
argument_list|)
expr_stmt|;
name|Thread
name|expireThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stopExpire
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|//          try {
comment|//            chaosMonkey.expireRandomSession();
comment|//          } catch (KeeperException e) {
comment|//            throw new RuntimeException(e);
comment|//          } catch (InterruptedException e) {
comment|//            throw new RuntimeException(e);
comment|//          }
block|}
block|}
block|}
decl_stmt|;
comment|//  Currently unused
comment|//  expireThread.start();
name|StoppableIndexingThread
name|indexThread
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|indexThread
operator|=
operator|new
name|StoppableIndexingThread
argument_list|(
name|controlClient
argument_list|,
name|cloudClient
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|true
argument_list|,
name|maxDoc
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|indexThread
argument_list|)
expr_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|int
name|restartTimes
init|=
literal|1
decl_stmt|;
comment|//random().nextInt(4) + 1;;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|restartTimes
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|300000
argument_list|)
argument_list|)
expr_stmt|;
name|stopAndStartAllReplicas
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// stop indexing threads
for|for
control|(
name|StoppableIndexingThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
block|}
name|stopExpire
operator|=
literal|true
expr_stmt|;
name|expireThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|320
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|stopAndStartAllReplicas
specifier|public
name|void
name|stopAndStartAllReplicas
parameter_list|()
throws|throws
name|Exception
throws|,
name|InterruptedException
block|{
name|chaosMonkey
operator|.
name|stopAll
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
for|for
control|(
name|StoppableIndexingThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|chaosMonkey
operator|.
name|startAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indexDoc
specifier|protected
name|void
name|indexDoc
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure threads have been stopped...
if|if
condition|(
name|threads
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|StoppableIndexingThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|thread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
block|}
comment|// skip the randoms - they can deadlock...
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

