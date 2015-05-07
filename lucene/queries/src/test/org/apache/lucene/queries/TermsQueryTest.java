begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|Collections
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
operator|.
name|Store
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
name|StringField
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
name|RandomIndexWriter
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|IndexSearcher
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
name|Query
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
name|QueryUtils
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
name|Sort
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
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|IOUtils
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
name|util
operator|.
name|RamUsageTester
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
name|TestUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
import|;
end_import

begin_class
DECL|class|TermsQueryTest
specifier|public
class|class
name|TermsQueryTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDuel
specifier|public
name|void
name|testDuel
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
specifier|final
name|List
argument_list|<
name|Term
argument_list|>
name|allTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
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
name|numTerms
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|String
name|field
init|=
name|usually
argument_list|()
condition|?
literal|"f"
else|:
literal|"g"
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|allTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|Term
name|term
init|=
name|allTerms
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allTerms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numTerms
operator|>
literal|1
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|allTerms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// may occasionally happen if all documents got the same term
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|dir
argument_list|)
expr_stmt|;
continue|continue;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|float
name|boost
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|10
decl_stmt|;
specifier|final
name|int
name|numQueryTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|queryTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numQueryTerms
condition|;
operator|++
name|j
control|)
block|{
name|queryTerms
operator|.
name|add
argument_list|(
name|allTerms
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allTerms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|queryTerms
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Query
name|q1
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
argument_list|)
decl_stmt|;
name|q1
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|q2
init|=
operator|new
name|TermsQuery
argument_list|(
name|queryTerms
argument_list|)
decl_stmt|;
name|q2
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
DECL|method|assertSameMatches
specifier|private
name|void
name|assertSameMatches
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|,
name|boolean
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|TopDocs
name|td1
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
name|maxDoc
argument_list|,
name|scores
condition|?
name|Sort
operator|.
name|RELEVANCE
else|:
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
name|td2
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|maxDoc
argument_list|,
name|scores
condition|?
name|Sort
operator|.
name|RELEVANCE
else|:
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|td1
operator|.
name|totalHits
argument_list|,
name|td2
operator|.
name|totalHits
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
name|td1
operator|.
name|scoreDocs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|,
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|scores
condition|)
block|{
name|assertEquals
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|10e-7
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|termsQuery
specifier|private
name|TermsQuery
name|termsQuery
parameter_list|(
name|boolean
name|singleField
parameter_list|,
name|Term
modifier|...
name|terms
parameter_list|)
block|{
return|return
name|termsQuery
argument_list|(
name|singleField
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
return|;
block|}
DECL|method|termsQuery
specifier|private
name|TermsQuery
name|termsQuery
parameter_list|(
name|boolean
name|singleField
parameter_list|,
name|Collection
argument_list|<
name|Term
argument_list|>
name|termList
parameter_list|)
block|{
if|if
condition|(
operator|!
name|singleField
condition|)
block|{
return|return
operator|new
name|TermsQuery
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|termList
argument_list|)
argument_list|)
return|;
block|}
specifier|final
name|TermsQuery
name|filter
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|bytes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|termList
control|)
block|{
name|bytes
operator|.
name|add
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
name|field
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|TermsQuery
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|filter
return|;
block|}
DECL|method|testHashCodeAndEquals
specifier|public
name|void
name|testHashCodeAndEquals
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|singleField
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|uniqueTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
literal|"field"
operator|+
operator|(
name|singleField
condition|?
literal|"1"
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|)
decl_stmt|;
name|String
name|string
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|string
argument_list|)
argument_list|)
expr_stmt|;
name|uniqueTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|string
argument_list|)
argument_list|)
expr_stmt|;
name|TermsQuery
name|left
init|=
name|termsQuery
argument_list|(
name|singleField
condition|?
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
else|:
literal|false
argument_list|,
name|uniqueTerms
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|terms
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|TermsQuery
name|right
init|=
name|termsQuery
argument_list|(
name|singleField
condition|?
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
else|:
literal|false
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|right
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|right
operator|.
name|hashCode
argument_list|()
argument_list|,
name|left
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|uniqueTerms
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|List
argument_list|<
name|Term
argument_list|>
name|asList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|uniqueTerms
argument_list|)
decl_stmt|;
name|asList
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TermsQuery
name|notEqual
init|=
name|termsQuery
argument_list|(
name|singleField
condition|?
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
else|:
literal|false
argument_list|,
name|asList
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|left
operator|.
name|equals
argument_list|(
name|notEqual
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|right
operator|.
name|equals
argument_list|(
name|notEqual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|TermsQuery
name|tq1
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"thing"
argument_list|,
literal|"apple"
argument_list|)
argument_list|)
decl_stmt|;
name|TermsQuery
name|tq2
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"thing"
argument_list|,
literal|"orange"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tq1
operator|.
name|hashCode
argument_list|()
operator|==
name|tq2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// different fields with the same term should have differing hashcodes
name|tq1
operator|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"thing1"
argument_list|,
literal|"apple"
argument_list|)
argument_list|)
expr_stmt|;
name|tq2
operator|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"thing2"
argument_list|,
literal|"apple"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tq1
operator|.
name|hashCode
argument_list|()
operator|==
name|tq2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleFieldEquals
specifier|public
name|void
name|testSingleFieldEquals
parameter_list|()
block|{
comment|// Two terms with the same hash code
name|assertEquals
argument_list|(
literal|"AaAaBB"
operator|.
name|hashCode
argument_list|()
argument_list|,
literal|"BBBBBB"
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|TermsQuery
name|left
init|=
name|termsQuery
argument_list|(
literal|true
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"AaAaAa"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"AaAaBB"
argument_list|)
argument_list|)
decl_stmt|;
name|TermsQuery
name|right
init|=
name|termsQuery
argument_list|(
literal|true
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"AaAaAa"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"BBBBBB"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|left
operator|.
name|equals
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|TermsQuery
name|termsQuery
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"field1:a field1:b field1:c"
argument_list|,
name|termsQuery
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDedup
specifier|public
name|void
name|testDedup
parameter_list|()
block|{
name|Query
name|query1
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query2
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|query1
argument_list|,
name|query2
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrderDoesNotMatter
specifier|public
name|void
name|testOrderDoesNotMatter
parameter_list|()
block|{
comment|// order of terms if different
name|Query
name|query1
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query2
init|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|query1
argument_list|,
name|query2
argument_list|)
expr_stmt|;
comment|// order of fields is different
name|query1
operator|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"bar"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|query2
operator|=
operator|new
name|TermsQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"bar"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|query1
argument_list|,
name|query2
argument_list|)
expr_stmt|;
block|}
DECL|method|testRamBytesUsed
specifier|public
name|void
name|testRamBytesUsed
parameter_list|()
block|{
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
literal|1000
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
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
name|numTerms
condition|;
operator|++
name|i
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|RandomStrings
operator|.
name|randomUnicodeOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TermsQuery
name|query
init|=
operator|new
name|TermsQuery
argument_list|(
name|terms
argument_list|)
decl_stmt|;
specifier|final
name|long
name|actualRamBytesUsed
init|=
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|long
name|expectedRamBytesUsed
init|=
name|query
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
comment|// error margin within 5%
name|assertEquals
argument_list|(
name|actualRamBytesUsed
argument_list|,
name|expectedRamBytesUsed
argument_list|,
name|actualRamBytesUsed
operator|/
literal|20
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

