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
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|common
operator|.
name|SolrInputDocument
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

begin_class
DECL|class|TestChildDocTransformer
specifier|public
class|class
name|TestChildDocTransformer
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|ID_FIELD
specifier|private
specifier|static
name|String
name|ID_FIELD
init|=
literal|"id"
decl_stmt|;
DECL|field|titleVals
specifier|private
name|String
index|[]
name|titleVals
init|=
operator|new
name|String
index|[
literal|2
index|]
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
DECL|method|testParentFilter
specifier|public
name|void
name|testParentFilter
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|titleVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|titleVals
index|[
name|i
index|]
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
name|createIndex
argument_list|(
name|titleVals
argument_list|)
expr_stmt|;
name|testParentFilterJSON
argument_list|()
expr_stmt|;
name|testParentFilterXML
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllParams
specifier|public
name|void
name|testAllParams
parameter_list|()
throws|throws
name|Exception
block|{
name|createSimpleIndex
argument_list|()
expr_stmt|;
name|testChildDoctransformerJSON
argument_list|()
expr_stmt|;
name|testChildDoctransformerXML
argument_list|()
expr_stmt|;
block|}
DECL|method|testChildDoctransformerXML
specifier|private
name|void
name|testChildDoctransformerXML
parameter_list|()
block|{
name|String
name|test1
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"//*[@numFound='1']"
block|,
literal|"/response/result/doc[1]/doc[1]/int[@name='id']='2'"
block|,
literal|"/response/result/doc[1]/doc[2]/int[@name='id']='3'"
block|,
literal|"/response/result/doc[1]/doc[3]/int[@name='id']='4'"
block|,
literal|"/response/result/doc[1]/doc[4]/int[@name='id']='5'"
block|,
literal|"/response/result/doc[1]/doc[5]/int[@name='id']='6'"
block|,
literal|"/response/result/doc[1]/doc[6]/int[@name='id']='7'"
block|}
decl_stmt|;
name|String
name|test2
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"//*[@numFound='1']"
block|,
literal|"/response/result/doc[1]/doc[1]/int[@name='id']='2'"
block|,
literal|"/response/result/doc[1]/doc[2]/int[@name='id']='4'"
block|,
literal|"/response/result/doc[1]/doc[3]/int[@name='id']='6'"
block|}
decl_stmt|;
name|String
name|test3
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"//*[@numFound='1']"
block|,
literal|"/response/result/doc[1]/doc[1]/int[@name='id']='3'"
block|,
literal|"/response/result/doc[1]/doc[2]/int[@name='id']='5'"
block|}
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"*,[child parentFilter=\"subject:parentDocument\"]"
argument_list|)
argument_list|,
name|test1
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"subject,[child parentFilter=\"subject:parentDocument\" childFilter=\"title:foo\"]"
argument_list|)
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"subject,[child parentFilter=\"subject:parentDocument\" childFilter=\"title:bar\" limit=2]"
argument_list|)
argument_list|,
name|test3
argument_list|)
expr_stmt|;
block|}
DECL|method|testChildDoctransformerJSON
specifier|private
name|void
name|testChildDoctransformerJSON
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|test1
init|=
operator|new
name|String
index|[]
block|{
literal|"/response/docs/[0]/_childDocuments_/[0]/id==2"
block|,
literal|"/response/docs/[0]/_childDocuments_/[1]/id==3"
block|,
literal|"/response/docs/[0]/_childDocuments_/[2]/id==4"
block|,
literal|"/response/docs/[0]/_childDocuments_/[3]/id==5"
block|,
literal|"/response/docs/[0]/_childDocuments_/[4]/id==6"
block|,
literal|"/response/docs/[0]/_childDocuments_/[5]/id==7"
block|}
decl_stmt|;
name|String
index|[]
name|test2
init|=
operator|new
name|String
index|[]
block|{
literal|"/response/docs/[0]/_childDocuments_/[0]/id==2"
block|,
literal|"/response/docs/[0]/_childDocuments_/[1]/id==4"
block|,
literal|"/response/docs/[0]/_childDocuments_/[2]/id==6"
block|}
decl_stmt|;
name|String
index|[]
name|test3
init|=
operator|new
name|String
index|[]
block|{
literal|"/response/docs/[0]/_childDocuments_/[0]/id==3"
block|,
literal|"/response/docs/[0]/_childDocuments_/[1]/id==5"
block|}
decl_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"*,[child parentFilter=\"subject:parentDocument\"]"
argument_list|)
argument_list|,
name|test1
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"subject,[child parentFilter=\"subject:parentDocument\" childFilter=\"title:foo\"]"
argument_list|)
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"subject,[child parentFilter=\"subject:parentDocument\" childFilter=\"title:bar\" limit=2]"
argument_list|)
argument_list|,
name|test3
argument_list|)
expr_stmt|;
block|}
DECL|method|createSimpleIndex
specifier|private
name|void
name|createSimpleIndex
parameter_list|()
block|{
name|SolrInputDocument
name|parentDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|parentDocument
operator|.
name|addField
argument_list|(
name|ID_FIELD
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|parentDocument
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|,
literal|"parentDocument"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|childDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|childDocument
operator|.
name|addField
argument_list|(
name|ID_FIELD
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|childDocument
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childDocument
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|parentDocument
operator|.
name|addChildDocument
argument_list|(
name|childDocument
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Long
name|version
init|=
name|addAndGetVersion
argument_list|(
name|parentDocument
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Failed to add document to the index"
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
literal|7
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|createIndex
specifier|private
specifier|static
name|void
name|createIndex
parameter_list|(
name|String
index|[]
name|titleVals
parameter_list|)
block|{
name|String
index|[]
name|parentIDS
init|=
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"4"
block|}
decl_stmt|;
name|String
index|[]
name|childDocIDS
init|=
operator|new
name|String
index|[]
block|{
literal|"2"
block|,
literal|"5"
block|}
decl_stmt|;
name|String
index|[]
name|grandChildIDS
init|=
operator|new
name|String
index|[]
block|{
literal|"3"
block|,
literal|"6"
block|}
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
name|parentIDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|parentDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|parentDocument
operator|.
name|addField
argument_list|(
name|ID_FIELD
argument_list|,
name|parentIDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|parentDocument
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|,
literal|"parentDocument"
argument_list|)
expr_stmt|;
name|parentDocument
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
name|titleVals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|childDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|childDocument
operator|.
name|addField
argument_list|(
name|ID_FIELD
argument_list|,
name|childDocIDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|childDocument
operator|.
name|addField
argument_list|(
literal|"cat"
argument_list|,
literal|"childDocument"
argument_list|)
expr_stmt|;
name|childDocument
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
name|titleVals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|grandChildDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|grandChildDocument
operator|.
name|addField
argument_list|(
name|ID_FIELD
argument_list|,
name|grandChildIDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|childDocument
operator|.
name|addChildDocument
argument_list|(
name|grandChildDocument
argument_list|)
expr_stmt|;
name|parentDocument
operator|.
name|addChildDocument
argument_list|(
name|childDocument
argument_list|)
expr_stmt|;
try|try
block|{
name|Long
name|version
init|=
name|addAndGetVersion
argument_list|(
name|parentDocument
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Failed to add document to the index"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
operator|(
name|parentIDS
operator|.
name|length
operator|+
name|childDocIDS
operator|.
name|length
operator|+
name|grandChildIDS
operator|.
name|length
operator|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testParentFilterJSON
specifier|private
name|void
name|testParentFilterJSON
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|tests
init|=
operator|new
name|String
index|[]
block|{
literal|"/response/docs/[0]/id==1"
block|,
literal|"/response/docs/[0]/_childDocuments_/[0]/id==2"
block|,
literal|"/response/docs/[0]/_childDocuments_/[0]/cat/[0]/=='childDocument'"
block|,
literal|"/response/docs/[0]/_childDocuments_/[0]/title/[0]/=='"
operator|+
name|titleVals
index|[
literal|0
index|]
operator|+
literal|"'"
block|,
literal|"/response/docs/[1]/id==4"
block|,
literal|"/response/docs/[1]/_childDocuments_/[0]/id==5"
block|,
literal|"/response/docs/[1]/_childDocuments_/[0]/cat/[0]/=='childDocument'"
block|,
literal|"/response/docs/[1]/_childDocuments_/[0]/title/[0]/=='"
operator|+
name|titleVals
index|[
literal|1
index|]
operator|+
literal|"'"
block|}
decl_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"*,[child childFilter='cat:childDocument' parentFilter=\"subject:parentDocument\"]"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"id,[child childFilter='cat:childDocument' parentFilter=\"subject:parentDocument\"]"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
DECL|method|testParentFilterXML
specifier|private
name|void
name|testParentFilterXML
parameter_list|()
block|{
name|String
name|tests
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"//*[@numFound='2']"
block|,
literal|"/response/result/doc[1]/int[@name='id']='1'"
block|,
literal|"/response/result/doc[1]/doc[1]/int[@name='id']='2'"
block|,
literal|"/response/result/doc[1]/doc[1]/arr[@name='cat']/str[1]='childDocument'"
block|,
literal|"/response/result/doc[1]/doc[1]/arr[@name='title']/str[1]='"
operator|+
name|titleVals
index|[
literal|0
index|]
operator|+
literal|"'"
block|,
literal|"/response/result/doc[2]/int[@name='id']='4'"
block|,
literal|"/response/result/doc[2]/doc[1]/int[@name='id']='5'"
block|,
literal|"/response/result/doc[2]/doc[1]/arr[@name='cat']/str[1]='childDocument'"
block|,
literal|"/response/result/doc[2]/doc[1]/arr[@name='title']/str[1]='"
operator|+
name|titleVals
index|[
literal|1
index|]
operator|+
literal|"'"
block|}
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"*,[child childFilter='cat:childDocument' parentFilter=\"subject:parentDocument\"]"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"fq"
argument_list|,
literal|"subject:\"parentDocument\" "
argument_list|,
literal|"fl"
argument_list|,
literal|"id,[child childFilter='cat:childDocument' parentFilter=\"subject:parentDocument\"]"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

