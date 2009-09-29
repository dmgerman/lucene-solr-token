begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|MultiCoreExampleTestBase
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
name|SolrServer
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
name|impl
operator|.
name|CommonsHttpSolrServer
import|;
end_import

begin_comment
comment|/**  * TODO? perhaps use:  *  http://docs.codehaus.org/display/JETTY/ServletTester  * rather then open a real connection?  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|MultiCoreExampleJettyTest
specifier|public
class|class
name|MultiCoreExampleJettyTest
extends|extends
name|MultiCoreExampleTestBase
block|{
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
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
literal|"/example"
decl_stmt|;
DECL|method|setUp
annotation|@
name|Override
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
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|context
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
annotation|@
name|Override
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
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// stop the server
block|}
annotation|@
name|Override
DECL|method|getSolrCore
specifier|protected
name|SolrServer
name|getSolrCore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|createServer
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrCore0
specifier|protected
name|SolrServer
name|getSolrCore0
parameter_list|()
block|{
return|return
name|createServer
argument_list|(
literal|"core0"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrCore1
specifier|protected
name|SolrServer
name|getSolrCore1
parameter_list|()
block|{
return|return
name|createServer
argument_list|(
literal|"core1"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrAdmin
specifier|protected
name|SolrServer
name|getSolrAdmin
parameter_list|()
block|{
return|return
name|createServer
argument_list|(
literal|""
argument_list|)
return|;
block|}
DECL|method|createServer
specifier|private
name|SolrServer
name|createServer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
literal|"http://localhost:"
operator|+
name|port
operator|+
name|context
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
name|CommonsHttpSolrServer
name|s
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|s
operator|.
name|setConnectionTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// 1/10th sec
name|s
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|s
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

