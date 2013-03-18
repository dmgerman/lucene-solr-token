begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Maps
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
name|request
operator|.
name|UpdateRequest
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
name|SolrInputDocument
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
name|SolrCore
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|SignatureUpdateProcessorFactoryTest
operator|.
name|addDoc
import|;
end_import

begin_class
DECL|class|TestPartialUpdateDeduplication
specifier|public
class|class
name|TestPartialUpdateDeduplication
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
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartialUpdates
specifier|public
name|void
name|testPartialUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|SignatureUpdateProcessorFactoryTest
operator|.
name|checkNumDocs
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|chain
init|=
literal|"dedupe"
decl_stmt|;
comment|// partial update
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"2a"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"set"
argument_list|,
literal|"Hello Dude man!"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v_t"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|boolean
name|exception_ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|addDoc
argument_list|(
name|req
operator|.
name|getXML
argument_list|()
argument_list|,
name|chain
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception_ok
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Should have gotten an exception with partial update on signature generating field"
argument_list|,
name|exception_ok
argument_list|)
expr_stmt|;
name|SignatureUpdateProcessorFactoryTest
operator|.
name|checkNumDocs
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2a"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi'"
argument_list|)
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"2a"
argument_list|)
expr_stmt|;
name|map
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"set"
argument_list|,
literal|"name changed"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|req
operator|.
name|getXML
argument_list|()
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|SignatureUpdateProcessorFactoryTest
operator|.
name|checkNumDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

