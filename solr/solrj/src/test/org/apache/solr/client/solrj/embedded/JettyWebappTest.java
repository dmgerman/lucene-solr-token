begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|IOUtils
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
name|util
operator|.
name|ExternalPaths
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|bio
operator|.
name|SocketConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|session
operator|.
name|HashSessionIdManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import

begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|JettyWebappTest
specifier|public
class|class
name|JettyWebappTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|port
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|context
specifier|static
specifier|final
name|String
name|context
init|=
literal|"/test"
decl_stmt|;
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|server
name|Server
name|server
decl_stmt|;
annotation|@
name|Override
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
literal|"solr.solr.home"
argument_list|,
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|dataDir
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|dataDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|ExternalPaths
operator|.
name|WEBAPP_HOME
decl_stmt|;
name|server
operator|=
operator|new
name|Server
argument_list|(
name|port
argument_list|)
expr_stmt|;
comment|// insecure: only use for tests!!!!
name|server
operator|.
name|setSessionIdManager
argument_list|(
operator|new
name|HashSessionIdManager
argument_list|(
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|WebAppContext
argument_list|(
name|server
argument_list|,
name|path
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|SocketConnector
name|connector
init|=
operator|new
name|SocketConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setMaxIdleTime
argument_list|(
literal|1000
operator|*
literal|60
operator|*
literal|60
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setSoLingerTime
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|setStopAtShutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|port
operator|=
name|connector
operator|.
name|getLocalPort
argument_list|()
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
try|try
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testAdminUI
specifier|public
name|void
name|testAdminUI
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Currently not an extensive test, but it does fire up the JSP pages and make
comment|// sure they compile ok
name|String
name|adminPath
init|=
literal|"http://127.0.0.1:"
operator|+
name|port
operator|+
name|context
operator|+
literal|"/"
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
operator|new
name|URL
argument_list|(
name|adminPath
argument_list|)
operator|.
name|openStream
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
comment|// real error will be an exception
block|}
block|}
end_class

end_unit

