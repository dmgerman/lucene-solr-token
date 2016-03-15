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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|TopDocs
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
name|SolrException
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
name|MultiMapSolrParams
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|UpdateParams
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
name|ContentStream
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
name|ContentStreamBase
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
name|handler
operator|.
name|UpdateRequestHandler
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
name|request
operator|.
name|SolrQueryRequestBase
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
name|SolrQueryResponse
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
name|SolrIndexSearcher
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

begin_comment
comment|/**  * Tests for {@link ClassificationUpdateProcessor} and {@link ClassificationUpdateProcessorFactory}  */
end_comment

begin_class
DECL|class|ClassificationUpdateProcessorFactoryTest
specifier|public
class|class
name|ClassificationUpdateProcessorFactoryTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|// field names are used in accordance with the solrconfig and schema supplied
DECL|field|ID
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
DECL|field|TITLE
specifier|private
specifier|static
specifier|final
name|String
name|TITLE
init|=
literal|"title"
decl_stmt|;
DECL|field|CONTENT
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT
init|=
literal|"content"
decl_stmt|;
DECL|field|AUTHOR
specifier|private
specifier|static
specifier|final
name|String
name|AUTHOR
init|=
literal|"author"
decl_stmt|;
DECL|field|CLASS
specifier|private
specifier|static
specifier|final
name|String
name|CLASS
init|=
literal|"cat"
decl_stmt|;
DECL|field|CHAIN
specifier|private
specifier|static
specifier|final
name|String
name|CHAIN
init|=
literal|"classification"
decl_stmt|;
DECL|field|cFactoryToTest
specifier|private
name|ClassificationUpdateProcessorFactory
name|cFactoryToTest
init|=
operator|new
name|ClassificationUpdateProcessorFactory
argument_list|()
decl_stmt|;
DECL|field|args
specifier|private
name|NamedList
name|args
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-classification.xml"
argument_list|,
literal|"schema-classification.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
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
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|initArgs
specifier|public
name|void
name|initArgs
parameter_list|()
block|{
name|args
operator|.
name|add
argument_list|(
literal|"inputFields"
argument_list|,
literal|"inputField1,inputField2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"classField"
argument_list|,
literal|"classField1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"algorithm"
argument_list|,
literal|"bayes"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"knn.k"
argument_list|,
literal|"9"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"knn.minDf"
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"knn.minTf"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFullInit
specifier|public
name|void
name|testFullInit
parameter_list|()
block|{
name|cFactoryToTest
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
index|[]
name|inputFieldNames
init|=
name|cFactoryToTest
operator|.
name|getInputFieldNames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"inputField1"
argument_list|,
name|inputFieldNames
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"inputField2"
argument_list|,
name|inputFieldNames
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"classField1"
argument_list|,
name|cFactoryToTest
operator|.
name|getClassFieldName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bayes"
argument_list|,
name|cFactoryToTest
operator|.
name|getAlgorithm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|cFactoryToTest
operator|.
name|getMinDf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|cFactoryToTest
operator|.
name|getMinTf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|cFactoryToTest
operator|.
name|getK
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitEmptyInputField
specifier|public
name|void
name|testInitEmptyInputField
parameter_list|()
block|{
name|args
operator|.
name|removeAll
argument_list|(
literal|"inputFields"
argument_list|)
expr_stmt|;
try|try
block|{
name|cFactoryToTest
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Classification UpdateProcessor 'inputFields' can not be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInitEmptyClassField
specifier|public
name|void
name|testInitEmptyClassField
parameter_list|()
block|{
name|args
operator|.
name|removeAll
argument_list|(
literal|"classField"
argument_list|)
expr_stmt|;
try|try
block|{
name|cFactoryToTest
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Classification UpdateProcessor 'classField' can not be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
block|{
name|args
operator|.
name|removeAll
argument_list|(
literal|"algorithm"
argument_list|)
expr_stmt|;
name|args
operator|.
name|removeAll
argument_list|(
literal|"knn.k"
argument_list|)
expr_stmt|;
name|args
operator|.
name|removeAll
argument_list|(
literal|"knn.minDf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|removeAll
argument_list|(
literal|"knn.minTf"
argument_list|)
expr_stmt|;
name|cFactoryToTest
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"knn"
argument_list|,
name|cFactoryToTest
operator|.
name|getAlgorithm
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cFactoryToTest
operator|.
name|getMinDf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cFactoryToTest
operator|.
name|getMinTf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|cFactoryToTest
operator|.
name|getK
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicClassification
specifier|public
name|void
name|testBasicClassification
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareTrainedIndex
argument_list|()
expr_stmt|;
comment|// To be classified,we index documents without a class and verify the expected one is returned
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"10"
argument_list|,
name|TITLE
argument_list|,
literal|"word4 word4 word4"
argument_list|,
name|CONTENT
argument_list|,
literal|"word5 word5 "
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name1 Surname1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"11"
argument_list|,
name|TITLE
argument_list|,
literal|"word1 word1"
argument_list|,
name|CONTENT
argument_list|,
literal|"word2 word2"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name Surname"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|doc10
init|=
name|getDoc
argument_list|(
literal|"10"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"class2"
argument_list|,
name|doc10
operator|.
name|get
argument_list|(
name|CLASS
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|doc11
init|=
name|getDoc
argument_list|(
literal|"11"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"class1"
argument_list|,
name|doc11
operator|.
name|get
argument_list|(
name|CLASS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Index some example documents with a class manually assigned.    * This will be our trained model.    *    * @throws Exception If there is a low-level I/O error    */
DECL|method|prepareTrainedIndex
specifier|private
name|void
name|prepareTrainedIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|//class1
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"1"
argument_list|,
name|TITLE
argument_list|,
literal|"word1 word1 word1"
argument_list|,
name|CONTENT
argument_list|,
literal|"word2 word2 word2"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name Surname"
argument_list|,
name|CLASS
argument_list|,
literal|"class1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"2"
argument_list|,
name|TITLE
argument_list|,
literal|"word1 word1"
argument_list|,
name|CONTENT
argument_list|,
literal|"word2 word2"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name Surname"
argument_list|,
name|CLASS
argument_list|,
literal|"class1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"3"
argument_list|,
name|TITLE
argument_list|,
literal|"word1 word1 word1"
argument_list|,
name|CONTENT
argument_list|,
literal|"word2"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name Surname"
argument_list|,
name|CLASS
argument_list|,
literal|"class1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"4"
argument_list|,
name|TITLE
argument_list|,
literal|"word1 word1 word1"
argument_list|,
name|CONTENT
argument_list|,
literal|"word2 word2 word2"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name Surname"
argument_list|,
name|CLASS
argument_list|,
literal|"class1"
argument_list|)
argument_list|)
expr_stmt|;
comment|//class2
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"5"
argument_list|,
name|TITLE
argument_list|,
literal|"word4 word4 word4"
argument_list|,
name|CONTENT
argument_list|,
literal|"word5 word5"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name1 Surname1"
argument_list|,
name|CLASS
argument_list|,
literal|"class2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"6"
argument_list|,
name|TITLE
argument_list|,
literal|"word4 word4"
argument_list|,
name|CONTENT
argument_list|,
literal|"word5"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name1 Surname1"
argument_list|,
name|CLASS
argument_list|,
literal|"class2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"7"
argument_list|,
name|TITLE
argument_list|,
literal|"word4 word4 word4"
argument_list|,
name|CONTENT
argument_list|,
literal|"word5 word5 word5"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name1 Surname1"
argument_list|,
name|CLASS
argument_list|,
literal|"class2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
name|ID
argument_list|,
literal|"8"
argument_list|,
name|TITLE
argument_list|,
literal|"word4"
argument_list|,
name|CONTENT
argument_list|,
literal|"word5 word5 word5 word5"
argument_list|,
name|AUTHOR
argument_list|,
literal|"Name1 Surname1"
argument_list|,
name|CLASS
argument_list|,
literal|"class2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getDoc
specifier|private
name|Document
name|getDoc
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
init|)
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|ID
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|doc1
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ScoreDoc
name|scoreDoc
init|=
name|doc1
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
return|return
name|searcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
return|;
block|}
block|}
DECL|method|addDoc
specifier|static
name|void
name|addDoc
parameter_list|(
name|String
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|MultiMapSolrParams
name|mmparams
init|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|,
operator|new
name|String
index|[]
block|{
name|CHAIN
block|}
argument_list|)
expr_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
operator|(
name|SolrParams
operator|)
name|mmparams
argument_list|)
block|{     }
decl_stmt|;
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

