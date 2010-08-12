begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|IndexReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|LuceneTestCase
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
name|store
operator|.
name|MockRAMDirectory
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
name|analysis
operator|.
name|MockAnalyzer
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
name|analysis
operator|.
name|MockTokenizer
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
name|document
operator|.
name|Field
import|;
end_import

begin_class
DECL|class|TestHighFreqTerms
specifier|public
class|class
name|TestHighFreqTerms
extends|extends
name|LuceneTestCase
block|{
DECL|field|writer
specifier|private
specifier|static
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
name|MockRAMDirectory
name|dir
init|=
literal|null
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
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
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|indexDocs
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/******************** Tests for getHighFreqTerms **********************************/
comment|// test without specifying field (i.e. if we pass in field=null it should examine all fields)
comment|// the term "diff" in the field "different_field" occurs 20 times and is the highest df term
DECL|method|testFirstTermHighestDocFreqAllFields
specifier|public
specifier|static
name|void
name|testFirstTermHighestDocFreqAllFields
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Term with highest docfreq is first"
argument_list|,
literal|20
argument_list|,
name|terms
index|[
literal|0
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstTermHighestDocFreq
specifier|public
specifier|static
name|void
name|testFirstTermHighestDocFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Term with highest docfreq is first"
argument_list|,
literal|10
argument_list|,
name|terms
index|[
literal|0
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrderedByDocFreqDescending
specifier|public
specifier|static
name|void
name|testOrderedByDocFreqDescending
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"out of order "
operator|+
name|terms
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|docFreq
operator|+
literal|"should be>= "
operator|+
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|,
name|terms
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|docFreq
operator|>=
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testNumTerms
specifier|public
specifier|static
name|void
name|testNumTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"length of terms array equals numTerms :"
operator|+
name|numTerms
argument_list|,
name|numTerms
argument_list|,
name|terms
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetHighFreqTerms
specifier|public
specifier|static
name|void
name|testGetHighFreqTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|termtext
init|=
name|terms
index|[
name|i
index|]
operator|.
name|termtext
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
comment|// hardcoded highTF or highTFmedDF
if|if
condition|(
name|termtext
operator|.
name|contains
argument_list|(
literal|"highTF"
argument_list|)
condition|)
block|{
if|if
condition|(
name|termtext
operator|.
name|contains
argument_list|(
literal|"medDF"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"doc freq is not as expected"
argument_list|,
literal|5
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"doc freq is not as expected"
argument_list|,
literal|1
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|n
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|termtext
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc freq is not as expected"
argument_list|,
name|getExpecteddocFreq
argument_list|(
name|n
argument_list|)
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/********************Test sortByTotalTermFreq**********************************/
DECL|method|testFirstTermHighestTotalTermFreq
specifier|public
specifier|static
name|void
name|testFirstTermHighestTotalTermFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|20
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|TermStats
index|[]
name|termsWithTotalTermFreq
init|=
name|HighFreqTerms
operator|.
name|sortByTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Term with highest totalTermFreq is first"
argument_list|,
literal|200
argument_list|,
name|termsWithTotalTermFreq
index|[
literal|0
index|]
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstTermHighestTotalTermFreqDifferentField
specifier|public
specifier|static
name|void
name|testFirstTermHighestTotalTermFreqDifferentField
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|20
decl_stmt|;
name|String
name|field
init|=
literal|"different_field"
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|TermStats
index|[]
name|termsWithTotalTermFreq
init|=
name|HighFreqTerms
operator|.
name|sortByTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Term with highest totalTermFreq is first"
operator|+
name|termsWithTotalTermFreq
index|[
literal|0
index|]
operator|.
name|getTermText
argument_list|()
argument_list|,
literal|150
argument_list|,
name|termsWithTotalTermFreq
index|[
literal|0
index|]
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrderedByTermFreqDescending
specifier|public
specifier|static
name|void
name|testOrderedByTermFreqDescending
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|TermStats
index|[]
name|termsWithTF
init|=
name|HighFreqTerms
operator|.
name|sortByTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
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
name|termsWithTF
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// check that they are sorted by descending termfreq order
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"out of order"
operator|+
name|termsWithTF
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|"> "
operator|+
name|termsWithTF
index|[
name|i
index|]
argument_list|,
name|termsWithTF
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|totalTermFreq
operator|>
name|termsWithTF
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testGetTermFreqOrdered
specifier|public
specifier|static
name|void
name|testGetTermFreqOrdered
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numTerms
init|=
literal|12
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|HighFreqTerms
operator|.
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|TermStats
index|[]
name|termsWithTF
init|=
name|HighFreqTerms
operator|.
name|sortByTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
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
name|termsWithTF
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|text
init|=
name|termsWithTF
index|[
name|i
index|]
operator|.
name|termtext
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|contains
argument_list|(
literal|"highTF"
argument_list|)
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|contains
argument_list|(
literal|"medDF"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"total term freq is expected"
argument_list|,
literal|125
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"total term freq is expected"
argument_list|,
literal|200
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|n
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc freq is expected"
argument_list|,
name|getExpecteddocFreq
argument_list|(
name|n
argument_list|)
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"total term freq is expected"
argument_list|,
name|getExpectedtotalTermFreq
argument_list|(
name|n
argument_list|)
argument_list|,
name|termsWithTF
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/********************Tests for getTotalTermFreq**********************************/
DECL|method|testGetTotalTermFreq
specifier|public
specifier|static
name|void
name|testGetTotalTermFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|term
init|=
literal|"highTF"
decl_stmt|;
name|BytesRef
name|termtext
init|=
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|long
name|totalTermFreq
init|=
name|HighFreqTerms
operator|.
name|getTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|termtext
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"highTf tf should be 200"
argument_list|,
literal|200
argument_list|,
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetTotalTermFreqBadTerm
specifier|public
specifier|static
name|void
name|testGetTotalTermFreqBadTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|term
init|=
literal|"foobar"
decl_stmt|;
name|BytesRef
name|termtext
init|=
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|String
name|field
init|=
literal|"FIELD_1"
decl_stmt|;
name|long
name|totalTermFreq
init|=
name|HighFreqTerms
operator|.
name|getTotalTermFreq
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|termtext
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"totalTermFreq should be 0 for term not in index"
argument_list|,
literal|0
argument_list|,
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
comment|/********************Testing Utils**********************************/
DECL|method|indexDocs
specifier|private
specifier|static
name|void
name|indexDocs
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|Exception
block|{
comment|/**      * Generate 10 documents where term n  has a docFreq of n and a totalTermFreq of n*2 (squared).       */
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|getContent
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"FIELD_1"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|//add a different field
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"different_field"
argument_list|,
literal|"diff"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|//add 10 more docs with the term "diff" this will make it have the highest docFreq if we don't ask for the
comment|//highest freq terms for a specific field.
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"different_field"
argument_list|,
literal|"diff"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// add some docs where tf< df so we can see if sorting works
comment|// highTF low df
name|int
name|highTF
init|=
literal|200
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|content
init|=
literal|""
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
name|highTF
condition|;
name|i
operator|++
control|)
block|{
name|content
operator|+=
literal|"highTF "
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"FIELD_1"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// highTF medium df =5
name|int
name|medium_df
init|=
literal|5
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
name|medium_df
condition|;
name|i
operator|++
control|)
block|{
name|int
name|tf
init|=
literal|25
decl_stmt|;
name|Document
name|newdoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|newcontent
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tf
condition|;
name|j
operator|++
control|)
block|{
name|newcontent
operator|+=
literal|"highTFmedDF "
expr_stmt|;
block|}
name|newdoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"FIELD_1"
argument_list|,
name|newcontent
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|newdoc
argument_list|)
expr_stmt|;
block|}
comment|// add a doc with high tf in field different_field
name|int
name|targetTF
init|=
literal|150
decl_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|content
operator|=
literal|""
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
name|targetTF
condition|;
name|i
operator|++
control|)
block|{
name|content
operator|+=
literal|"TF150 "
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"different_field"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    *  getContent    *  return string containing numbers 1 to i with each number n occurring n times.    *  i.e. for input of 3 return string "3 3 3 2 2 1"     */
DECL|method|getContent
specifier|private
specifier|static
name|String
name|getContent
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|String
name|s
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|10
init|;
name|j
operator|>=
name|i
condition|;
name|j
operator|--
control|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|j
condition|;
name|k
operator|++
control|)
block|{
comment|// if j is 3 we return "3 3 3"
name|s
operator|+=
name|String
operator|.
name|valueOf
argument_list|(
name|j
argument_list|)
operator|+
literal|" "
expr_stmt|;
block|}
block|}
return|return
name|s
return|;
block|}
DECL|method|getExpectedtotalTermFreq
specifier|private
specifier|static
name|int
name|getExpectedtotalTermFreq
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|getExpecteddocFreq
argument_list|(
name|i
argument_list|)
operator|*
name|i
return|;
block|}
DECL|method|getExpecteddocFreq
specifier|private
specifier|static
name|int
name|getExpecteddocFreq
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|i
return|;
block|}
block|}
end_class

end_unit

