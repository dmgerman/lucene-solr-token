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
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|ZkCmdExecutor
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
name|ZkOperation
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
name|AbstractSolrTestCase
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
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
name|WatchedEvent
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
name|Watcher
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
DECL|class|ZkSolrClientTest
specifier|public
class|class
name|ZkSolrClientTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|class|ZkConnection
specifier|static
class|class
name|ZkConnection
implements|implements
name|AutoCloseable
block|{
DECL|field|server
specifier|private
name|ZkTestServer
name|server
init|=
literal|null
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
DECL|method|ZkConnection
name|ZkConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|ZkConnection
name|ZkConnection
parameter_list|(
name|boolean
name|makeRoot
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|makeRoot
condition|)
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
block|}
DECL|method|getServer
specifier|public
name|ZkTestServer
name|getServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
DECL|method|getClient
specifier|public
name|SolrZkClient
name|getClient
parameter_list|()
block|{
return|return
name|zkClient
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testConnect
specifier|public
name|void
name|testConnect
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ZkConnection
name|conn
init|=
operator|new
name|ZkConnection
argument_list|(
literal|false
argument_list|)
init|)
block|{
comment|// do nothing
block|}
block|}
DECL|method|testMakeRootNode
specifier|public
name|void
name|testMakeRootNode
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ZkConnection
name|conn
init|=
operator|new
name|ZkConnection
argument_list|()
init|)
block|{
specifier|final
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|conn
operator|.
name|getServer
argument_list|()
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/solr"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testClean
specifier|public
name|void
name|testClean
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ZkConnection
name|conn
init|=
operator|new
name|ZkConnection
argument_list|()
init|)
block|{
specifier|final
name|SolrZkClient
name|zkClient
init|=
name|conn
operator|.
name|getClient
argument_list|()
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/test/path/here"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/zz/path/here"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|clean
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/test"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/zz"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReconnect
specifier|public
name|void
name|testReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|String
name|shardsPath
init|=
literal|"/collections/collection1/shards"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|shardsPath
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|int
name|zkServerPort
init|=
name|server
operator|.
name|getPort
argument_list|()
decl_stmt|;
comment|// this tests disconnect state
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|80
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Server should be down here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{        }
comment|// bring server back up
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|,
name|zkServerPort
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// TODO: can we do better?
comment|// wait for reconnect
name|Thread
operator|.
name|sleep
argument_list|(
literal|600
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// try again in a bit
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/collections/collection3"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/collections/collection1"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate session expiration
comment|// one option
name|long
name|sessionId
init|=
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|server
operator|.
name|expire
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
comment|// another option
comment|//zkClient.getSolrZooKeeper().getConnection().disconnect();
comment|// this tests expired state
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// pause for reconnect
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection4"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|SessionExpiredException
decl||
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{          }
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
operator|*
name|i
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Node does not exist, but it should"
argument_list|,
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/collections/collection4"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testZkCmdExectutor
specifier|public
name|void
name|testZkCmdExectutor
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|timeout
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|+
literal|5000
decl_stmt|;
name|ZkCmdExecutor
name|zkCmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
name|zkCmdExecutor
operator|.
name|retryOperation
argument_list|(
operator|new
name|ZkOperation
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|>
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|KeeperException
operator|.
name|SessionExpiredException
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|KeeperException
operator|.
name|ConnectionLossException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|SessionExpiredException
name|e
parameter_list|)
block|{                }
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Expected "
operator|+
name|KeeperException
operator|.
name|SessionExpiredException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" but got "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMultipleWatchesAsync
specifier|public
name|void
name|testMultipleWatchesAsync
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ZkConnection
name|conn
init|=
operator|new
name|ZkConnection
argument_list|()
init|)
block|{
specifier|final
name|SolrZkClient
name|zkClient
init|=
name|conn
operator|.
name|getClient
argument_list|()
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numColls
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numColls
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numColls
condition|;
name|i
operator|++
control|)
block|{
name|String
name|collPath
init|=
literal|"/collections/collection"
operator|+
name|i
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|collPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getChildren
argument_list|(
name|collPath
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numColls
condition|;
name|i
operator|++
control|)
block|{
name|String
name|shardsPath
init|=
literal|"/collections/collection"
operator|+
name|i
operator|+
literal|"/shards"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|shardsPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|latch
operator|.
name|await
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWatchChildren
specifier|public
name|void
name|testWatchChildren
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ZkConnection
name|conn
init|=
operator|new
name|ZkConnection
argument_list|()
init|)
block|{
specifier|final
name|SolrZkClient
name|zkClient
init|=
name|conn
operator|.
name|getClient
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|cnt
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getChildren
argument_list|(
literal|"/collections"
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
name|cnt
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// remake watch
try|try
block|{
name|zkClient
operator|.
name|getChildren
argument_list|(
literal|"/collections"
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
decl||
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
block|}
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/collection99/shards"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
comment|//wait until watch has been re-created
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection99/config=collection1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection99/config=collection3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/collection97/shards"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// pause for the watches to fire
name|Thread
operator|.
name|sleep
argument_list|(
literal|700
argument_list|)
expr_stmt|;
if|if
condition|(
name|cnt
operator|.
name|intValue
argument_list|()
operator|<
literal|2
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
comment|// wait a bit more
block|}
if|if
condition|(
name|cnt
operator|.
name|intValue
argument_list|()
operator|<
literal|2
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
comment|// wait a bit more
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cnt
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSkipPathPartsOnMakePath
specifier|public
name|void
name|testSkipPathPartsOnMakePath
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ZkConnection
name|conn
init|=
operator|new
name|ZkConnection
argument_list|()
init|)
block|{
specifier|final
name|SolrZkClient
name|zkClient
init|=
name|conn
operator|.
name|getClient
argument_list|()
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/test"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// should work
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/test/path/here"
argument_list|,
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
operator|(
name|Watcher
operator|)
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|clean
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// should not work
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/test/path/here"
argument_list|,
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
operator|(
name|Watcher
operator|)
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should not be able to create this path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{        }
name|zkClient
operator|.
name|clean
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ZkCmdExecutor
name|zkCmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
try|try
block|{
name|zkCmdExecutor
operator|.
name|ensureExists
argument_list|(
literal|"/collection/collection/leader"
argument_list|,
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|zkClient
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should not be able to create this path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{        }
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collection"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|zkCmdExecutor
operator|.
name|ensureExists
argument_list|(
literal|"/collections/collection/leader"
argument_list|,
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|zkClient
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should not be able to create this path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{        }
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collection/collection"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
name|zkCmdExecutor
operator|.
name|ensureExists
argument_list|(
literal|"/collection/collection"
argument_list|,
name|bytes
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|zkClient
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|returnedBytes
init|=
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/collection/collection"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"We skipped 2 path parts, so data won't be written"
argument_list|,
name|returnedBytes
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collection/collection/leader"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkCmdExecutor
operator|.
name|ensureExists
argument_list|(
literal|"/collection/collection/leader"
argument_list|,
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|zkClient
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|tearDown
argument_list|()
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
throws|throws
name|InterruptedException
block|{
comment|// wait just a bit for any zk client threads to outlast timeout
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

