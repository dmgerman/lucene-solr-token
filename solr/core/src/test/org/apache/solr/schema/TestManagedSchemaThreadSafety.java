begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|Future
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
name|AtomicReference
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
name|ZkController
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
name|ZkSolrResourceLoader
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
name|ZkTestServer
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
name|util
operator|.
name|ExecutorUtil
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
name|SolrConfig
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
name|SolrResourceLoader
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
name|LogLevel
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
name|KeeperException
operator|.
name|NoNodeException
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
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestManagedSchemaThreadSafety
specifier|public
class|class
name|TestManagedSchemaThreadSafety
extends|extends
name|SolrTestCaseJ4
block|{
DECL|class|SuspendingZkClient
specifier|private
specifier|static
specifier|final
class|class
name|SuspendingZkClient
extends|extends
name|SolrZkClient
block|{
DECL|field|slowpoke
name|AtomicReference
argument_list|<
name|Thread
argument_list|>
name|slowpoke
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SuspendingZkClient
specifier|private
name|SuspendingZkClient
parameter_list|(
name|String
name|zkServerAddress
parameter_list|,
name|int
name|zkClientTimeout
parameter_list|)
block|{
name|super
argument_list|(
name|zkServerAddress
argument_list|,
name|zkClientTimeout
argument_list|)
expr_stmt|;
block|}
DECL|method|isSlowpoke
name|boolean
name|isSlowpoke
parameter_list|()
block|{
name|Thread
name|youKnow
decl_stmt|;
if|if
condition|(
operator|(
name|youKnow
operator|=
name|slowpoke
operator|.
name|get
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
return|return
name|youKnow
operator|==
name|Thread
operator|.
name|currentThread
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|slowpoke
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getData
specifier|public
name|byte
index|[]
name|getData
parameter_list|(
name|String
name|path
parameter_list|,
name|Watcher
name|watcher
parameter_list|,
name|Stat
name|stat
parameter_list|,
name|boolean
name|retryOnConnLoss
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|byte
index|[]
name|data
decl_stmt|;
try|try
block|{
name|data
operator|=
name|super
operator|.
name|getData
argument_list|(
name|path
argument_list|,
name|watcher
argument_list|,
name|stat
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoNodeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|isSlowpoke
argument_list|()
condition|)
block|{
comment|//System.out.println("suspending "+Thread.currentThread()+" on " + path);
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
return|return
name|data
return|;
block|}
block|}
DECL|field|zkServer
specifier|private
specifier|static
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|loaderPath
specifier|private
specifier|static
name|Path
name|loaderPath
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startZkServer
specifier|public
specifier|static
name|void
name|startZkServer
parameter_list|()
throws|throws
name|Exception
block|{
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|loaderPath
operator|=
name|createTempDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopZkServer
specifier|public
specifier|static
name|void
name|stopZkServer
parameter_list|()
throws|throws
name|Exception
block|{
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|=
literal|null
expr_stmt|;
name|loaderPath
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|LogLevel
argument_list|(
literal|"org.apache.solr.common.cloud.SolrZkClient=debug"
argument_list|)
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|configsetName
init|=
literal|"managed-config"
decl_stmt|;
comment|//
try|try
init|(
name|SolrZkClient
name|client
init|=
operator|new
name|SuspendingZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
literal|30
argument_list|)
init|)
block|{
comment|// we can pick any to load configs, I suppose, but here we check
name|client
operator|.
name|upConfig
argument_list|(
name|configset
argument_list|(
literal|"cloud-managed-upgrade"
argument_list|)
argument_list|,
name|configsetName
argument_list|)
expr_stmt|;
block|}
name|ExecutorService
name|executor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
literal|"threadpool"
argument_list|)
decl_stmt|;
try|try
init|(
name|SolrZkClient
name|raceJudge
init|=
operator|new
name|SuspendingZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
literal|30
argument_list|)
init|)
block|{
name|ZkController
name|zkController
init|=
name|createZkController
argument_list|(
name|raceJudge
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|executor
operator|.
name|submit
argument_list|(
name|indexSchemaLoader
argument_list|(
name|configsetName
argument_list|,
name|zkController
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|?
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createZkController
specifier|private
name|ZkController
name|createZkController
parameter_list|(
name|SolrZkClient
name|client
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ZkController
name|zkController
init|=
name|mock
argument_list|(
name|ZkController
operator|.
name|class
argument_list|,
name|Mockito
operator|.
name|withSettings
argument_list|()
operator|.
name|defaultAnswer
argument_list|(
name|Mockito
operator|.
name|CALLS_REAL_METHODS
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|zkController
operator|.
name|getZkClient
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|client
operator|.
name|exists
argument_list|(
operator|(
name|String
operator|)
name|invocation
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|zkController
argument_list|)
operator|.
name|pathExists
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|zkController
return|;
block|}
DECL|method|indexSchemaLoader
specifier|private
name|Runnable
name|indexSchemaLoader
parameter_list|(
name|String
name|configsetName
parameter_list|,
specifier|final
name|ZkController
name|zkController
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
try|try
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|ZkSolrResourceLoader
argument_list|(
name|loaderPath
argument_list|,
name|configsetName
argument_list|,
name|zkController
argument_list|)
decl_stmt|;
name|SolrConfig
name|solrConfig
init|=
name|SolrConfig
operator|.
name|readFromResourceLoader
argument_list|(
name|loader
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
name|ManagedIndexSchemaFactory
name|factory
init|=
operator|new
name|ManagedIndexSchemaFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|create
argument_list|(
literal|"schema.xml"
argument_list|,
name|solrConfig
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
return|;
block|}
block|}
end_class

end_unit

