begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|XMLResponseParser
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
name|util
operator|.
name|RestTestBase
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
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  * Test that a ConfigSet marked as immutable cannot be modified via  * the known APIs, i.e. SolrConfigHandler and SchemaHandler.  */
end_comment

begin_class
DECL|class|TestConfigSetImmutable
specifier|public
class|class
name|TestConfigSetImmutable
extends|extends
name|RestTestBase
block|{
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
name|File
name|tmpSolrHome
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|tmpConfDir
init|=
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
name|confDir
argument_list|)
decl_stmt|;
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
comment|// make the ConfigSet immutable
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|tmpConfDir
argument_list|,
literal|"configsetprops.json"
argument_list|)
argument_list|,
operator|new
name|StringBuilder
argument_list|(
literal|"{\"immutable\":\"true\"}"
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|createJettyAndHarness
argument_list|(
name|tmpSolrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig-schemaless.xml"
argument_list|,
literal|"schema-rest.xml"
argument_list|,
literal|"/solr"
argument_list|,
literal|true
argument_list|,
literal|null
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
name|client
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|restTestHarness
operator|!=
literal|null
condition|)
block|{
name|restTestHarness
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|restTestHarness
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSolrConfigHandlerImmutable
specifier|public
name|void
name|testSolrConfigHandlerImmutable
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"'create-requesthandler' : { 'name' : '/x', 'class': 'org.apache.solr.handler.DumpRequestHandler' , 'startup' : 'lazy'}\n"
operator|+
literal|"}"
decl_stmt|;
name|String
name|uri
init|=
literal|"/config?wt=json"
decl_stmt|;
name|String
name|response
init|=
name|restTestHarness
operator|.
name|post
argument_list|(
name|uri
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
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"immutable"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSchemaHandlerImmutable
specifier|public
name|void
name|testSchemaHandlerImmutable
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"    'add-field' : {\n"
operator|+
literal|"                 'name':'a1',\n"
operator|+
literal|"                 'type': 'string',\n"
operator|+
literal|"                 'stored':true,\n"
operator|+
literal|"                 'indexed':false\n"
operator|+
literal|"                 },\n"
operator|+
literal|"    }"
decl_stmt|;
name|String
name|response
init|=
name|restTestHarness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
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
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"immutable"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddSchemaFieldsImmutable
specifier|public
name|void
name|testAddSchemaFieldsImmutable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|error
init|=
literal|"error"
decl_stmt|;
comment|// check writing an existing field is okay
name|String
name|updateXMLSafe
init|=
literal|"<add><doc><field name=\"id\">\"testdoc\"</field></doc></add>"
decl_stmt|;
name|String
name|response
init|=
name|restTestHarness
operator|.
name|update
argument_list|(
name|updateXMLSafe
argument_list|)
decl_stmt|;
name|XMLResponseParser
name|parser
init|=
operator|new
name|XMLResponseParser
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|listResponse
init|=
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|listResponse
operator|.
name|get
argument_list|(
name|error
argument_list|)
argument_list|)
expr_stmt|;
comment|// check writing a new field is not okay
name|String
name|updateXMLNotSafe
init|=
literal|"<add><doc><field name=\"id\">\"testdoc\"</field>"
operator|+
literal|"<field name=\"newField67\">\"foobar\"</field></doc></add>"
decl_stmt|;
name|response
operator|=
name|restTestHarness
operator|.
name|update
argument_list|(
name|updateXMLNotSafe
argument_list|)
expr_stmt|;
name|listResponse
operator|=
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|listResponse
operator|.
name|get
argument_list|(
name|error
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listResponse
operator|.
name|get
argument_list|(
name|error
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"immutable"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

