begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|transform
operator|.
name|*
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
DECL|class|ReturnFieldsTest
specifier|public
class|class
name|ReturnFieldsTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|// :TODO: datatypes produced by the functions used may change
comment|/**    * values of the fl param that mean all real fields    */
DECL|field|ALL_REAL_FIELDS
specifier|private
specifier|static
name|String
index|[]
name|ALL_REAL_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"*"
block|}
decl_stmt|;
comment|/**    * values of the fl param that mean all real fields and score    */
DECL|field|SCORE_AND_REAL_FIELDS
specifier|private
specifier|static
name|String
index|[]
name|SCORE_AND_REAL_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|"score"
block|,
literal|"score,*"
block|,
literal|"*,score"
block|}
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
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|String
name|v
init|=
literal|"how now brown cow"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
name|v
argument_list|,
literal|"text_np"
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|=
literal|"now cow"
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
name|v
argument_list|,
literal|"text_np"
argument_list|,
name|v
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
DECL|method|testCopyRename
specifier|public
name|void
name|testCopyRename
parameter_list|()
throws|throws
name|Exception
block|{
comment|// original
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"*[count(//doc/str)=1] "
argument_list|,
literal|"*//doc[1]/str[1][.='1'] "
argument_list|)
expr_stmt|;
comment|// rename
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"xxx:id"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"*[count(//doc/str)=1] "
argument_list|,
literal|"*//doc[1]/str[1][.='1'] "
argument_list|)
expr_stmt|;
comment|// original and copy
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,xxx:id"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"*[count(//doc/str)=2] "
argument_list|,
literal|"*//doc[1]/str[1][.='1'] "
argument_list|,
literal|"*//doc[1]/str[2][.='1'] "
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"xxx:id,id"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"*[count(//doc/str)=2] "
argument_list|,
literal|"*//doc[1]/str[1][.='1'] "
argument_list|,
literal|"*//doc[1]/str[2][.='1'] "
argument_list|)
expr_stmt|;
comment|// two copies
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"xxx:id,yyy:id"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"*[count(//doc/str)=2] "
argument_list|,
literal|"*//doc[1]/str[1][.='1'] "
argument_list|,
literal|"*//doc[1]/str[2][.='1'] "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeparators
specifier|public
name|void
name|testSeparators
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id name test subject score"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ScoreAugmenter
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,name,test,subject,score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ScoreAugmenter
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,name test,subject score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ScoreAugmenter
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id, name  test , subject,score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ScoreAugmenter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWilcards
specifier|public
name|void
name|testWilcards
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|" * "
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that we want wildcards
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,aaa*,*bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"aaaxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxxaaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxxbbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"bbbxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"bb"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testManyParameters
specifier|public
name|void
name|testManyParameters
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id name"
argument_list|,
literal|"fl"
argument_list|,
literal|"test subject"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ScoreAugmenter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunctions
specifier|public
name|void
name|testFunctions
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id sum(1,1)"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ValueSourceAugmenter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sum(1,1)"
argument_list|,
operator|(
operator|(
name|ValueSourceAugmenter
operator|)
name|rf
operator|.
name|getTransformer
argument_list|()
operator|)
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransformers
specifier|public
name|void
name|testTransformers
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"[explain]"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[explain]"
argument_list|,
name|rf
operator|.
name|getTransformer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"[shard],id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[shard]"
argument_list|,
name|rf
operator|.
name|getTransformer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"[docid]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[docid]"
argument_list|,
name|rf
operator|.
name|getTransformer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"mydocid:[docid]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mydocid"
argument_list|,
name|rf
operator|.
name|getTransformer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"[docid][shard]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|DocTransformers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|DocTransformers
operator|)
name|rf
operator|.
name|getTransformer
argument_list|()
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"[xxxxx]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAliases
specifier|public
name|void
name|testAliases
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"newId:id newName:name newTest:test newSubject:subject"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newId"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newTest"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newSubject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"newId:id newName:name newTest:test newSubject:subject score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newId"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newTest"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"newSubject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|DocTransformers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
operator|(
operator|(
name|DocTransformers
operator|)
name|rf
operator|.
name|getTransformer
argument_list|()
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 4 rename and score
block|}
comment|// hyphens in field names are not supported in all contexts, but we wanted
comment|// the simplest case of fl=foo-bar to work
annotation|@
name|Test
DECL|method|testHyphenInFieldName
specifier|public
name|void
name|testHyphenInFieldName
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id-test"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id-test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrailingDotInFieldName
specifier|public
name|void
name|testTrailingDotInFieldName
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id.test"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"test:id.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"test.id:id.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"test.id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrailingDollarInFieldName
specifier|public
name|void
name|testTrailingDollarInFieldName
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id$test"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id$test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

