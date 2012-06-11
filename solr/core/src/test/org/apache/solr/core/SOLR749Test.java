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
name|search
operator|.
name|QParserPlugin
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
name|search
operator|.
name|FooQParserPlugin
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
name|search
operator|.
name|ValueSourceParser
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

begin_comment
comment|/**  * Test for https://issues.apache.org/jira/browse/SOLR-749  *  **/
end_comment

begin_class
DECL|class|SOLR749Test
specifier|public
class|class
name|SOLR749Test
extends|extends
name|SolrTestCaseJ4
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
literal|"solrconfig-SOLR-749.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstruction
specifier|public
name|void
name|testConstruction
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"core is null and it shouldn't be"
argument_list|,
name|core
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|QParserPlugin
name|parserPlugin
init|=
name|core
operator|.
name|getQueryPlugin
argument_list|(
name|QParserPlugin
operator|.
name|DEFAULT_QTYPE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"parserPlugin is null and it shouldn't be"
argument_list|,
name|parserPlugin
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"parserPlugin is not an instanceof "
operator|+
name|FooQParserPlugin
operator|.
name|class
argument_list|,
name|parserPlugin
operator|instanceof
name|FooQParserPlugin
argument_list|)
expr_stmt|;
name|ValueSourceParser
name|vsp
init|=
name|core
operator|.
name|getValueSourceParser
argument_list|(
literal|"boost"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"vsp is null and it shouldn't be"
argument_list|,
name|vsp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"vsp is not an instanceof "
operator|+
name|DummyValueSourceParser
operator|.
name|class
argument_list|,
name|vsp
operator|instanceof
name|DummyValueSourceParser
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

