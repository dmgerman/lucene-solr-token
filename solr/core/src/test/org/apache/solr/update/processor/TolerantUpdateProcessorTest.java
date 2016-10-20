begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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
name|util
operator|.
name|ClientUtils
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|util
operator|.
name|SimpleOrderedMap
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
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|LocalSolrQueryRequest
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
name|SolrRequestHandler
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
name|servlet
operator|.
name|DirectSolrConnection
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
name|update
operator|.
name|AddUpdateCommand
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
name|BaseTestHarness
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
DECL|class|TolerantUpdateProcessorTest
specifier|public
class|class
name|TolerantUpdateProcessorTest
extends|extends
name|UpdateProcessorTestBase
block|{
comment|/**    * List of valid + invalid documents    */
DECL|field|docs
specifier|private
specifier|static
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
literal|null
decl_stmt|;
comment|/**    * IDs of the invalid documents in<code>docs</code>    */
DECL|field|badIds
specifier|private
specifier|static
name|String
index|[]
name|badIds
init|=
literal|null
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
literal|"solrconfig-update-processor-chains.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownClass
specifier|public
specifier|static
name|void
name|tearDownClass
parameter_list|()
block|{
name|docs
operator|=
literal|null
expr_stmt|;
name|badIds
operator|=
literal|null
expr_stmt|;
block|}
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
comment|//expected exception messages
name|ignoreException
argument_list|(
literal|"Error adding field"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"Document is missing mandatory uniqueKey field"
argument_list|)
expr_stmt|;
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
block|{
name|docs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|badIds
operator|=
operator|new
name|String
index|[
literal|10
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// a valid document
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|2
operator|*
name|i
argument_list|)
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"weight"
argument_list|,
literal|1f
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// ... and an invalid one
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|2
operator|*
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"weight"
argument_list|,
literal|1f
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|badIds
index|[
name|i
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
literal|2
operator|*
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
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
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * future proof TolerantUpdateProcessor against new default method impls being added to UpdateProcessor     * to ensure that every method involved in a processor chain life cycle is overridden with     * exception catching/tracking.    */
DECL|method|testReflection
specifier|public
name|void
name|testReflection
parameter_list|()
block|{
for|for
control|(
name|Method
name|method
range|:
name|TolerantUpdateProcessor
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|equals
argument_list|(
name|Object
operator|.
name|class
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertEquals
argument_list|(
literal|"base class(es) has changed, TolerantUpdateProcessor needs updated to ensure it "
operator|+
literal|"overrides all solr update lifcycle methods with exception tracking: "
operator|+
name|method
operator|.
name|toString
argument_list|()
argument_list|,
name|TolerantUpdateProcessor
operator|.
name|class
argument_list|,
name|method
operator|.
name|getDeclaringClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidAdds
specifier|public
name|void
name|testValidAdds
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrInputDocument
name|validDoc
init|=
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"text"
argument_list|,
literal|1f
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
decl_stmt|;
name|add
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
literal|null
argument_list|,
name|validDoc
argument_list|)
expr_stmt|;
name|validDoc
operator|=
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
literal|"2"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"text"
argument_list|,
literal|1f
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"tolerant-chain-max-errors-not-set"
argument_list|,
literal|null
argument_list|,
name|validDoc
argument_list|)
expr_stmt|;
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
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidAdds
specifier|public
name|void
name|testInvalidAdds
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrInputDocument
name|invalidDoc
init|=
name|doc
argument_list|(
name|field
argument_list|(
literal|"text"
argument_list|,
literal|1f
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
decl_stmt|;
comment|//no id
try|try
block|{
comment|// This doc should fail without being tolerant
name|add
argument_list|(
literal|"not-tolerant"
argument_list|,
literal|null
argument_list|,
name|invalidDoc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//expected
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Document is missing mandatory uniqueKey field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SolrInputDocument
index|[]
block|{
name|invalidDoc
block|}
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|"(unknown)"
argument_list|)
expr_stmt|;
comment|//a valid doc
name|SolrInputDocument
name|validDoc
init|=
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"text"
argument_list|,
literal|1f
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
comment|// This batch should fail without being tolerant
name|add
argument_list|(
literal|"not-tolerant"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SolrInputDocument
index|[]
block|{
name|invalidDoc
block|,
name|validDoc
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//expected
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Document is missing mandatory uniqueKey field"
argument_list|)
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
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SolrInputDocument
index|[]
block|{
name|invalidDoc
block|,
name|validDoc
block|}
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|"(unknown)"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify that the good document made it in.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|invalidDoc
operator|=
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
literal|"2"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"weight"
argument_list|,
literal|1f
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|validDoc
operator|=
name|doc
argument_list|(
name|field
argument_list|(
literal|"id"
argument_list|,
literal|1f
argument_list|,
literal|"3"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"weight"
argument_list|,
literal|1f
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|// This batch should fail without being tolerant
name|add
argument_list|(
literal|"not-tolerant"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SolrInputDocument
index|[]
block|{
name|invalidDoc
block|,
name|validDoc
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//no id
name|fail
argument_list|(
literal|"Expecting exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//expected
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Error adding field"
argument_list|)
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
literal|"id:3"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SolrInputDocument
index|[]
block|{
name|invalidDoc
block|,
name|validDoc
block|}
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// The valid document was indexed
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// The invalid document was NOT indexed
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxErrorsDefault
specifier|public
name|void
name|testMaxErrorsDefault
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
comment|// by default the TolerantUpdateProcessor accepts all errors, so this batch should succeed with 10 errors.
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-not-set"
argument_list|,
name|docs
argument_list|,
literal|null
argument_list|,
name|badIds
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
literal|"Shouldn't get an exception for this batch: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
literal|"//result[@numFound='10']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxErrorsSucceed
specifier|public
name|void
name|testMaxErrorsSucceed
parameter_list|()
throws|throws
name|IOException
block|{
name|ModifiableSolrParams
name|requestParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|requestParams
operator|.
name|add
argument_list|(
literal|"maxErrors"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
comment|// still OK
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-not-set"
argument_list|,
name|docs
argument_list|,
name|requestParams
argument_list|,
name|badIds
argument_list|)
expr_stmt|;
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
literal|"//result[@numFound='10']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxErrorsThrowsException
specifier|public
name|void
name|testMaxErrorsThrowsException
parameter_list|()
throws|throws
name|IOException
block|{
name|ModifiableSolrParams
name|requestParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|requestParams
operator|.
name|add
argument_list|(
literal|"maxErrors"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// should fail
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-not-set"
argument_list|,
name|docs
argument_list|,
name|requestParams
argument_list|,
name|badIds
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ERROR: [doc=1] Error adding field 'weight'='b' msg=For input string: \"b\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//the first good documents made it to the index
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
literal|"//result[@numFound='6']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxErrorsInfinite
specifier|public
name|void
name|testMaxErrorsInfinite
parameter_list|()
throws|throws
name|IOException
block|{
name|ModifiableSolrParams
name|requestParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|requestParams
operator|.
name|add
argument_list|(
literal|"maxErrors"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-not-set"
argument_list|,
name|docs
argument_list|,
literal|null
argument_list|,
name|badIds
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
literal|"Shouldn't get an exception for this batch: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
literal|"//result[@numFound='10']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxErrors0
specifier|public
name|void
name|testMaxErrors0
parameter_list|()
throws|throws
name|IOException
block|{
comment|//make the TolerantUpdateProcessor intolerant
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|smallBatch
init|=
name|docs
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|requestParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|requestParams
operator|.
name|add
argument_list|(
literal|"maxErrors"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// should fail
name|assertAddsSucceedWithErrors
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|smallBatch
argument_list|,
name|requestParams
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ERROR: [doc=1] Error adding field 'weight'='b' msg=For input string: \"b\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//the first good documents made it to the index
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
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidDelete
specifier|public
name|void
name|testInvalidDelete
parameter_list|()
throws|throws
name|XPathExpressionException
throws|,
name|SAXException
block|{
name|ignoreException
argument_list|(
literal|"undefined field invalidfield"
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"//arr[@name='errors']"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=0"
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|delQ
argument_list|(
literal|"invalidfield:1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=1"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='type']/text()='DELQ'"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='invalidfield:1'"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='message']/text()='undefined field invalidfield'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidDelete
specifier|public
name|void
name|testValidDelete
parameter_list|()
throws|throws
name|XPathExpressionException
throws|,
name|SAXException
block|{
name|ignoreException
argument_list|(
literal|"undefined field invalidfield"
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"//arr[@name='errors']"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=0"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|response
operator|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|delQ
argument_list|(
literal|"id:1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"//arr[@name='errors']"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=0"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResponse
specifier|public
name|void
name|testResponse
parameter_list|()
throws|throws
name|SAXException
throws|,
name|XPathExpressionException
throws|,
name|IOException
block|{
name|String
name|response
init|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"//arr[@name='errors']"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=0"
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"//int[@name='maxErrors']/text()='10'"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=1"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='(unknown)'"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='message']/text()='Document is missing mandatory uniqueKey field: id'"
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"the quick brown fox"
argument_list|)
argument_list|)
expr_stmt|;
name|StringWriter
name|builder
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"<add>"
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrInputDocument
name|doc
range|:
name|docs
control|)
block|{
name|ClientUtils
operator|.
name|writeXML
argument_list|(
name|doc
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"</add>"
argument_list|)
expr_stmt|;
name|response
operator|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-10"
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='status']=0"
argument_list|,
literal|"//int[@name='maxErrors']/text()='10'"
argument_list|,
literal|"count(//arr[@name='errors']/lst)=10"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='0')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='1'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='2')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='3'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='4')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='5'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='6')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='7'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='8')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='9'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='10')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='11'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='12')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='13'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='14')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='15'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='16')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='17'"
argument_list|,
literal|"not(//arr[@name='errors']/lst/str[@name='id']/text()='18')"
argument_list|,
literal|"//arr[@name='errors']/lst/str[@name='id']/text()='19'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// spot check response when effective maxErrors is unlimited
name|response
operator|=
name|update
argument_list|(
literal|"tolerant-chain-max-errors-not-set"
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"//int[@name='maxErrors']/text()='-1'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|update
specifier|public
name|String
name|update
parameter_list|(
name|String
name|chain
parameter_list|,
name|String
name|xml
parameter_list|)
block|{
name|DirectSolrConnection
name|connection
init|=
operator|new
name|DirectSolrConnection
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update"
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"update.chain"
argument_list|,
name|chain
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|connection
operator|.
name|request
argument_list|(
name|handler
argument_list|,
name|params
argument_list|,
name|xml
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|SolrException
operator|)
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|assertAddsSucceedWithErrors
specifier|private
name|void
name|assertAddsSucceedWithErrors
parameter_list|(
name|String
name|chain
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|,
name|SolrParams
name|requestParams
parameter_list|,
name|String
modifier|...
name|idsShouldFail
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|response
init|=
name|add
argument_list|(
name|chain
argument_list|,
name|requestParams
argument_list|,
name|docs
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|String
argument_list|>
argument_list|>
name|errors
init|=
operator|(
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|String
argument_list|>
argument_list|>
operator|)
name|response
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|errors
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of errors"
argument_list|,
name|idsShouldFail
operator|.
name|length
argument_list|,
name|errors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|addErrorIdsExpected
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|idsShouldFail
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|SimpleOrderedMap
argument_list|<
name|String
argument_list|>
name|err
range|:
name|errors
control|)
block|{
name|assertEquals
argument_list|(
literal|"this method only expects 'add' errors"
argument_list|,
literal|"ADD"
argument_list|,
name|err
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|err
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null err id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"unexpected id"
argument_list|,
name|addErrorIdsExpected
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|protected
name|SolrQueryResponse
name|add
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|,
name|SolrParams
name|requestParams
parameter_list|,
specifier|final
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|add
argument_list|(
name|chain
argument_list|,
name|requestParams
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SolrInputDocument
index|[]
block|{
name|doc
block|}
argument_list|)
argument_list|)
return|;
block|}
DECL|method|add
specifier|protected
name|SolrQueryResponse
name|add
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|,
name|SolrParams
name|requestParams
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|pc
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No Chain named: "
operator|+
name|chain
argument_list|,
name|pc
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestParams
operator|==
literal|null
condition|)
block|{
name|requestParams
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
block|}
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|requestParams
argument_list|)
decl_stmt|;
try|try
block|{
name|UpdateRequestProcessor
name|processor
init|=
name|pc
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrInputDocument
name|doc
range|:
name|docs
control|)
block|{
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|doc
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|processor
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|rsp
return|;
block|}
block|}
end_class

end_unit

