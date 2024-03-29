begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|SolrJettyTestBase
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
name|SolrDocument
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
name|SolrDocumentList
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
name|params
operator|.
name|SolrParams
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
DECL|class|GetByIdTest
specifier|public
class|class
name|GetByIdTest
extends|extends
name|SolrJettyTestBase
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
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
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
name|getSolrClient
argument_list|()
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"term_s"
argument_list|,
literal|"Microsoft"
argument_list|,
literal|"term2_s"
argument_list|,
literal|"MSFT"
argument_list|)
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"term_s"
argument_list|,
literal|"Apple"
argument_list|,
literal|"term2_s"
argument_list|,
literal|"AAPL"
argument_list|)
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"term_s"
argument_list|,
literal|"Yahoo"
argument_list|,
literal|"term2_s"
argument_list|,
literal|"YHOO"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetId
specifier|public
name|void
name|testGetId
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrDocument
name|rsp
init|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MSFT"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Apple"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AAPL"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetIdWithParams
specifier|public
name|void
name|testGetIdWithParams
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SolrParams
name|ID_FL_ONLY
init|=
name|params
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|SolrDocument
name|rsp
init|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
literal|"0"
argument_list|,
name|ID_FL_ONLY
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
literal|"1"
argument_list|,
name|ID_FL_ONLY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
literal|"2"
argument_list|,
name|ID_FL_ONLY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetIds
specifier|public
name|void
name|testGetIds
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrDocumentList
name|rsp
init|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"0"
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|rsp
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MSFT"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Apple"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AAPL"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Yahoo"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"YHOO"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetIdsWithParams
specifier|public
name|void
name|testGetIdsWithParams
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrDocumentList
name|rsp
init|=
name|getSolrClient
argument_list|()
operator|.
name|getById
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"0"
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
name|params
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rsp
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"term_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"This field should have been removed from the response."
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"term2_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

