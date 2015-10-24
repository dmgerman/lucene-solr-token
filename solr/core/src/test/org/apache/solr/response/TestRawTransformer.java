begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|request
operator|.
name|SolrQueryRequest
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

begin_comment
comment|/**  * Tests Raw JSON output for fields when used with and without the unique key field.  *  * See SOLR-7993  */
end_comment

begin_class
DECL|class|TestRawTransformer
specifier|public
class|class
name|TestRawTransformer
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
literal|"solrconfig-doctransformers.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomTransformer
specifier|public
name|void
name|testCustomTransformer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Build a simple index
name|int
name|max
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|sdoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|sdoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|,
literal|"{poffL:[{offL:[{oGUID:\"79D5A31D-B3E4-4667-B812-09DF4336B900\",oID:\"OO73XRX\",prmryO:1,oRank:1,addTp:\"Office\",addCd:\"AA4GJ5T\",ad1:\"102 S 3rd St Ste 100\",city:\"Carson City\",st:\"MI\",zip:\"48811\",lat:43.176885,lng:-84.842919,phL:[\"(989) 584-1308\"],faxL:[\"(989) 584-6453\"]}]}]}"
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"title_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
name|jsonAdd
argument_list|(
name|sdoc
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
name|max
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"subject:[json]"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
decl_stmt|;
name|String
name|strResponse
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"response does not contain right JSON encoding: "
operator|+
name|strResponse
argument_list|,
name|strResponse
operator|.
name|contains
argument_list|(
literal|"\"subject\":[{poffL:[{offL:[{oGUID:\"7"
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,subject"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|strResponse
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"response does not contain right JSON encoding: "
operator|+
name|strResponse
argument_list|,
name|strResponse
operator|.
name|contains
argument_list|(
literal|"subject\":[\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

