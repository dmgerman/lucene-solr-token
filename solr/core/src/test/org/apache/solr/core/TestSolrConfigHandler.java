begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|RestTestBase
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
name|RestTestHarness
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
name|servlet
operator|.
name|ServletHolder
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|ConfigOverlay
operator|.
name|getObjectByPath
import|;
end_import

begin_class
DECL|class|TestSolrConfigHandler
specifier|public
class|class
name|TestSolrConfigHandler
extends|extends
name|RestTestBase
block|{
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|File
name|tmpSolrHome
decl_stmt|;
DECL|field|tmpConfDir
specifier|private
specifier|static
name|File
name|tmpConfDir
decl_stmt|;
DECL|field|collection
specifier|private
specifier|static
specifier|final
name|String
name|collection
init|=
literal|"collection1"
decl_stmt|;
DECL|field|confDir
specifier|private
specifier|static
specifier|final
name|String
name|confDir
init|=
name|collection
operator|+
literal|"/conf"
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|tmpSolrHome
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|tmpConfDir
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|tmpSolrHome
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
comment|// '/schema/*' matches '/schema', '/schema/', and '/schema/whatever...'
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|createJettyAndHarness
argument_list|(
name|tmpSolrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-rest.xml"
argument_list|,
literal|"/solr"
argument_list|,
literal|true
argument_list|,
name|extraServlets
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
block|}
name|server
operator|=
literal|null
expr_stmt|;
name|restTestHarness
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testProperty
specifier|public
name|void
name|testProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|" 'set-property' : { 'updateHandler.autoCommit.maxDocs':100, 'updateHandler.autoCommit.maxTime':10 } \n"
operator|+
literal|" }"
decl_stmt|;
name|RestTestHarness
name|harness
init|=
name|restTestHarness
decl_stmt|;
name|String
name|response
init|=
name|harness
operator|.
name|post
argument_list|(
literal|"/config?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|response
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|getRespMap
argument_list|(
literal|"/config/overlay?wt=json"
argument_list|,
name|harness
argument_list|)
operator|.
name|get
argument_list|(
literal|"overlay"
argument_list|)
decl_stmt|;
name|Map
name|props
init|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"props"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getObjectByPath
argument_list|(
name|props
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"updateHandler"
argument_list|,
literal|"autoCommit"
argument_list|,
literal|"maxDocs"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getObjectByPath
argument_list|(
name|props
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"updateHandler"
argument_list|,
literal|"autoCommit"
argument_list|,
literal|"maxTime"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|Map
operator|)
name|getRespMap
argument_list|(
literal|"/config?wt=json"
argument_list|,
name|harness
argument_list|)
operator|.
name|get
argument_list|(
literal|"solrConfig"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"updateHandler"
argument_list|,
literal|"autoCommit"
argument_list|,
literal|"maxDocs"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"updateHandler"
argument_list|,
literal|"autoCommit"
argument_list|,
literal|"maxTime"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|" 'unset-property' :  'updateHandler.autoCommit.maxDocs'} \n"
operator|+
literal|" }"
expr_stmt|;
name|response
operator|=
name|harness
operator|.
name|post
argument_list|(
literal|"/config?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|response
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|Map
operator|)
name|getRespMap
argument_list|(
literal|"/config/overlay?wt=json"
argument_list|,
name|harness
argument_list|)
operator|.
name|get
argument_list|(
literal|"overlay"
argument_list|)
expr_stmt|;
name|props
operator|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"props"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getObjectByPath
argument_list|(
name|props
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"updateHandler"
argument_list|,
literal|"autoCommit"
argument_list|,
literal|"maxDocs"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getObjectByPath
argument_list|(
name|props
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"updateHandler"
argument_list|,
literal|"autoCommit"
argument_list|,
literal|"maxTime"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUserProp
specifier|public
name|void
name|testUserProp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|" 'set-user-property' : { 'my.custom.variable.a':'MODIFIEDA',"
operator|+
literal|" 'my.custom.variable.b':'MODIFIEDB' } \n"
operator|+
literal|" }"
decl_stmt|;
name|RestTestHarness
name|harness
init|=
name|restTestHarness
decl_stmt|;
name|String
name|response
init|=
name|harness
operator|.
name|post
argument_list|(
literal|"/config?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|response
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|getRespMap
argument_list|(
literal|"/config/overlay?wt=json"
argument_list|,
name|harness
argument_list|)
operator|.
name|get
argument_list|(
literal|"overlay"
argument_list|)
decl_stmt|;
name|Map
name|props
init|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"userProps"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"my.custom.variable.a"
argument_list|)
argument_list|,
literal|"MODIFIEDA"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"my.custom.variable.b"
argument_list|)
argument_list|,
literal|"MODIFIEDB"
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|Map
operator|)
name|getRespMap
argument_list|(
literal|"/dump?wt=json&json.nl=map&initArgs=true"
argument_list|,
name|harness
argument_list|)
operator|.
name|get
argument_list|(
literal|"initArgs"
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MODIFIEDA"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MODIFIEDB"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getRespMap
specifier|public
specifier|static
name|Map
name|getRespMap
parameter_list|(
name|String
name|path
parameter_list|,
name|RestTestHarness
name|restHarness
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|response
init|=
name|restHarness
operator|.
name|query
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
