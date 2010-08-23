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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
import|;
end_import

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
DECL|method|TestBooleanScorer
specifier|public
name|TestBooleanScorer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|Directory
name|directory
init|=
name|newDirectory
argument_list|(
name|random
argument_list|)
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
operator|new
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
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
name|close
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
operator|new
name|IndexSearcher
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
name|Similarity
name|sim
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|Scorer
index|[]
name|scorers
init|=
operator|new
name|Scorer
index|[]
block|{
operator|new
name|Scorer
argument_list|(
name|sim
argument_list|)
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
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
function|@Override public int docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
function|@Override public int nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doc
operator|=
name|doc
operator|==
operator|-
literal|1
condition|?
literal|3000
else|:
name|NO_MORE_DOCS
return|;
block|}
function|@Override public int advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doc
operator|=
name|target
operator|<=
literal|3000
condition|?
literal|3000
else|:
name|NO_MORE_DOCS
return|;
block|}
function|}};
name|BooleanScorer
name|bs
init|=
operator|new
name|BooleanScorer
argument_list|(
name|sim
argument_list|,
literal|1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|scorers
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"should have received 3000"
argument_list|,
literal|3000
argument_list|,
name|bs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"should have received NO_MORE_DOCS"
argument_list|,
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|bs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

