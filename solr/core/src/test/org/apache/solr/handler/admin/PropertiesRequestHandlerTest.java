begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|RedactionUtils
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
DECL|class|PropertiesRequestHandlerTest
specifier|public
class|class
name|PropertiesRequestHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"secret123"
decl_stmt|;
DECL|field|REDACT_STRING
specifier|public
specifier|static
specifier|final
name|String
name|REDACT_STRING
init|=
name|RedactionUtils
operator|.
name|getRedactString
argument_list|()
decl_stmt|;
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
annotation|@
name|Test
DECL|method|testRedaction
specifier|public
name|void
name|testRedaction
parameter_list|()
throws|throws
name|Exception
block|{
name|RedactionUtils
operator|.
name|setRedactSystemProperty
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|propName
range|:
operator|new
name|String
index|[]
block|{
literal|"some.password"
block|,
literal|"javax.net.ssl.trustStorePassword"
block|}
control|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|propName
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|properties
init|=
name|readProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to redact "
operator|+
name|propName
argument_list|,
name|REDACT_STRING
argument_list|,
name|properties
operator|.
name|get
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDisabledRedaction
specifier|public
name|void
name|testDisabledRedaction
parameter_list|()
throws|throws
name|Exception
block|{
name|RedactionUtils
operator|.
name|setRedactSystemProperty
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|propName
range|:
operator|new
name|String
index|[]
block|{
literal|"some.password"
block|,
literal|"javax.net.ssl.trustStorePassword"
block|}
control|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|propName
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|properties
init|=
name|readProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to *not* redact "
operator|+
name|propName
argument_list|,
name|PASSWORD
argument_list|,
name|properties
operator|.
name|get
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readProperties
specifier|private
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|readProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/properties"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"xml"
argument_list|)
argument_list|)
decl_stmt|;
name|XMLResponseParser
name|parser
init|=
operator|new
name|XMLResponseParser
argument_list|()
decl_stmt|;
return|return
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"system.properties"
argument_list|)
return|;
block|}
block|}
end_class

end_unit
