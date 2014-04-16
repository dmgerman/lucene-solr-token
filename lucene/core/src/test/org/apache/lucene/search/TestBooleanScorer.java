begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Collections
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
name|TextField
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
name|AtomicReaderContext
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
name|BooleanQuery
operator|.
name|BooleanWeight
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
name|Bits
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

begin_class
DECL|class|TestBooleanScorer
specifier|public
class|class
name|TestBooleanScorer
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"category"
decl_stmt|;
DECL|method|testMethod
specifier|public
name|void
name|testMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|}
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
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
name|values
operator|.
name|length
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
name|newStringField
argument_list|(
name|FIELD
argument_list|,
name|values
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|BooleanQuery
name|booleanQuery1
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanQuery1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|booleanQuery1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|booleanQuery1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"9"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of matched documents"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmptyBucketWithMoreDocs
specifier|public
name|void
name|testEmptyBucketWithMoreDocs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test checks the logic of nextDoc() when all sub scorers have docs
comment|// beyond the first bucket (for example). Currently, the code relies on the
comment|// 'more' variable to work properly, and this test ensures that if the logic
comment|// changes, we have a test to back it up.
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|BooleanWeight
name|weight
init|=
operator|(
name|BooleanWeight
operator|)
operator|new
name|BooleanQuery
argument_list|()
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|BulkScorer
index|[]
name|scorers
init|=
operator|new
name|BulkScorer
index|[]
block|{
operator|new
name|BulkScorer
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|score
parameter_list|(
name|LeafCollector
name|c
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|doc
operator|==
operator|-
literal|1
assert|;
name|doc
operator|=
literal|3000
expr_stmt|;
name|FakeScorer
name|fs
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
name|fs
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|fs
operator|.
name|score
operator|=
literal|1.0f
expr_stmt|;
name|c
operator|.
name|setScorer
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|c
operator|.
name|collect
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
function|}};
name|BooleanScorer
name|bs
init|=
operator|new
name|BooleanScorer
argument_list|(
name|weight
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
name|Collections
operator|.
expr|<
name|Scorer
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|scorers
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|BulkScorer
operator|>
name|emptyList
argument_list|()
argument_list|,
name|scorers
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|hits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|bs
operator|.
name|score
argument_list|(
operator|new
name|SimpleCollector
argument_list|()
block|{
name|int
name|docBase
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
block|}
function|@Override       public void collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|hits
operator|.
name|add
argument_list|(
name|docBase
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
function|@Override       protected void doSetNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
function|@Override       public boolean acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
function|}
block|)
empty_stmt|;
name|assertEquals
argument_list|(
literal|"should have only 1 hit"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hit should have been docID=3000"
argument_list|,
literal|3000
argument_list|,
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
parameter_list|()
constructor_decl|;
name|directory
operator|.
name|close
parameter_list|()
constructor_decl|;
block|}
DECL|method|testMoreThan32ProhibitedClauses
specifier|public
name|void
name|testMoreThan32ProhibitedClauses
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|d
argument_list|)
decl_stmt|;
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
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"33"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// we don't wrap with AssertingIndexSearcher in order to have the original scorer in setScorer.
specifier|final
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|term
init|=
literal|0
init|;
name|term
operator|<
literal|33
condition|;
name|term
operator|++
control|)
block|{
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|""
operator|+
name|term
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"33"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|count
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|SimpleCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
comment|// Make sure we got BooleanScorer:
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|scorer
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Scorer is implemented by wrong class"
argument_list|,
name|FakeScorer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
function|@Override       public void collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|count
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
function|@Override       public boolean acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
function|}
block|)
empty_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Throws UOE if Weight.scorer is called */
DECL|class|CrazyMustUseBulkScorerQuery
function|private static class CrazyMustUseBulkScorerQuery extends Query
block|{
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"MustUseBulkScorerQuery"
return|;
block|}
function|@Override     public Weight createWeight
DECL|method|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Weight
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
function|@Override         public Query getQuery
parameter_list|()
block|{
return|return
name|CrazyMustUseBulkScorerQuery
operator|.
name|this
return|;
block|}
function|@Override         public float getValueForNormalization
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
function|@Override         public void normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
block|}
function|@Override         public Scorer scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
function|@Override         public BulkScorer bulkScorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
return|return
operator|new
name|BulkScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
operator|new
name|FakeScorer
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
function|};
block|}
block|}
function|;
block|}
block|}
comment|/** Make sure BooleanScorer can embed another    *  BooleanScorer. */
DECL|method|testEmbeddedBooleanScorer
function|public void testEmbeddedBooleanScorer
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"doctors are people who prescribe medicines of which they know little, to cure diseases of which they know less, in human beings of whom they know nothing"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|BooleanQuery
name|q1
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"little"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"diseases"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|CrazyMustUseBulkScorerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|q2
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|r
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
function|}
end_class

end_unit

