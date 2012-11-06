begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

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
name|impl
operator|.
name|HttpSolrServer
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  *<p> Test for Loading core properties from a properties file</p>  *  *  * @since solr 1.4  */
end_comment

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
name|SolrIgnoredThreadsFilter
operator|.
name|class
block|}
argument_list|)
DECL|class|TestSolrCoreProperties
specifier|public
class|class
name|TestSolrCoreProperties
extends|extends
name|LuceneTestCase
block|{
DECL|field|CONF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CONF_DIR
init|=
literal|"."
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|solrJetty
name|JettySolrRunner
name|solrJetty
decl_stmt|;
DECL|field|client
name|SolrServer
name|client
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
name|setUpMe
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|solrJetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|getHomeDir
argument_list|()
argument_list|,
literal|"/solr"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|solrJetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|url
init|=
literal|"http://127.0.0.1:"
operator|+
name|solrJetty
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|"/solr"
decl_stmt|;
name|client
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
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
name|solrJetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|AbstractSolrTestCase
operator|.
name|recurseDelete
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|SolrServerException
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|res
init|=
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|homeDir
name|File
name|homeDir
decl_stmt|;
DECL|field|confDir
name|File
name|confDir
decl_stmt|;
DECL|field|dataDir
name|File
name|dataDir
decl_stmt|;
comment|/**    * if masterPort is null, this instance is a master -- otherwise this instance is a slave, and assumes the master is    * on localhost at the specified port.    */
DECL|method|getHomeDir
specifier|public
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|homeDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|+
literal|"schema-replication1.xml"
return|;
block|}
DECL|method|getConfDir
specifier|public
name|String
name|getConfDir
parameter_list|()
block|{
return|return
name|confDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|+
literal|"solrconfig-solcoreproperties.xml"
return|;
block|}
DECL|method|setUpMe
specifier|public
name|void
name|setUpMe
parameter_list|()
throws|throws
name|Exception
block|{
name|homeDir
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
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
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
literal|"/collection1"
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
literal|"/collection1"
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"foo.foo1"
argument_list|,
literal|"f1"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"foo.foo2"
argument_list|,
literal|"f2"
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|confDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrcore.properties"
argument_list|)
decl_stmt|;
name|p
operator|.
name|store
argument_list|(
name|fos
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

