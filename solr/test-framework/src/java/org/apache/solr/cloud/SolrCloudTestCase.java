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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Files
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
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|JettyConfig
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
name|CloudSolrClient
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

begin_comment
comment|/**  * Base class for SolrCloud tests  *  * Derived tests should call {@link #configureCluster(int)} in a {@code BeforeClass}  * static method.  This configures and starts a {@link MiniSolrCloudCluster}, available  * via the {@code cluster} variable.  Cluster shutdown is handled automatically.  *  *<pre>  *<code>  *   {@literal @}BeforeClass  *   public static void setupCluster() {  *     configureCluster(NUM_NODES)  *        .addConfig("configname", pathToConfig)  *        .configure();  *   }  *</code>  *</pre>  */
end_comment

begin_class
DECL|class|SolrCloudTestCase
specifier|public
class|class
name|SolrCloudTestCase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|class|Config
specifier|private
specifier|static
class|class
name|Config
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|path
specifier|final
name|Path
name|path
decl_stmt|;
DECL|method|Config
specifier|private
name|Config
parameter_list|(
name|String
name|name
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
block|}
comment|/**    * Builder class for a MiniSolrCloudCluster    */
DECL|class|Builder
specifier|protected
specifier|static
class|class
name|Builder
block|{
DECL|field|nodeCount
specifier|private
specifier|final
name|int
name|nodeCount
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|final
name|Path
name|baseDir
decl_stmt|;
DECL|field|solrxml
specifier|private
name|String
name|solrxml
init|=
name|MiniSolrCloudCluster
operator|.
name|DEFAULT_CLOUD_SOLR_XML
decl_stmt|;
DECL|field|jettyConfig
specifier|private
name|JettyConfig
name|jettyConfig
init|=
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
decl_stmt|;
DECL|field|configs
specifier|private
name|List
argument_list|<
name|Config
argument_list|>
name|configs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Create a builder      * @param nodeCount the number of nodes in the cluster      * @param baseDir   a base directory for the cluster      */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|int
name|nodeCount
parameter_list|,
name|Path
name|baseDir
parameter_list|)
block|{
name|this
operator|.
name|nodeCount
operator|=
name|nodeCount
expr_stmt|;
name|this
operator|.
name|baseDir
operator|=
name|baseDir
expr_stmt|;
block|}
comment|/**      * Use a {@link JettyConfig} to configure the cluster's jetty servers      */
DECL|method|withJettyConfig
specifier|public
name|Builder
name|withJettyConfig
parameter_list|(
name|JettyConfig
name|jettyConfig
parameter_list|)
block|{
name|this
operator|.
name|jettyConfig
operator|=
name|jettyConfig
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Use the provided string as solr.xml content      */
DECL|method|withSolrXml
specifier|public
name|Builder
name|withSolrXml
parameter_list|(
name|String
name|solrXml
parameter_list|)
block|{
name|this
operator|.
name|solrxml
operator|=
name|solrXml
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Read solr.xml from the provided path      */
DECL|method|withSolrXml
specifier|public
name|Builder
name|withSolrXml
parameter_list|(
name|Path
name|solrXml
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|solrxml
operator|=
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|solrXml
argument_list|)
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
return|return
name|this
return|;
block|}
comment|/**      * Upload a collection config before tests start      * @param configName the config name      * @param configPath the path to the config files      */
DECL|method|addConfig
specifier|public
name|Builder
name|addConfig
parameter_list|(
name|String
name|configName
parameter_list|,
name|Path
name|configPath
parameter_list|)
block|{
name|this
operator|.
name|configs
operator|.
name|add
argument_list|(
operator|new
name|Config
argument_list|(
name|configName
argument_list|,
name|configPath
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Configure and run the {@link MiniSolrCloudCluster}      * @throws Exception if an error occurs on startup      */
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|nodeCount
argument_list|,
name|baseDir
argument_list|,
name|solrxml
argument_list|,
name|jettyConfig
argument_list|)
expr_stmt|;
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
for|for
control|(
name|Config
name|config
range|:
name|configs
control|)
block|{
name|client
operator|.
name|uploadConfig
argument_list|(
name|config
operator|.
name|path
argument_list|,
name|config
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** The cluster */
DECL|field|cluster
specifier|protected
specifier|static
name|MiniSolrCloudCluster
name|cluster
decl_stmt|;
comment|/**    * Call this to configure a cluster of n nodes.    *    * NB you must call {@link Builder#configure()} to start the cluster    *    * @param nodeCount the number of nodes    */
DECL|method|configureCluster
specifier|protected
specifier|static
name|Builder
name|configureCluster
parameter_list|(
name|int
name|nodeCount
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|nodeCount
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdownCluster
specifier|public
specifier|static
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|checkClusterConfiguration
specifier|public
name|void
name|checkClusterConfiguration
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"MiniSolrCloudCluster not configured - have you called configureCluster().configure()?"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

