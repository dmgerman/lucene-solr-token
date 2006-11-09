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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|TestConstantScoreRangeQuery
specifier|public
class|class
name|TestConstantScoreRangeQuery
extends|extends
name|BaseTestRangeFilter
block|{
comment|/** threshold for comparing floats */
DECL|field|SCORE_COMP_THRESH
specifier|public
specifier|static
specifier|final
name|float
name|SCORE_COMP_THRESH
init|=
literal|1e-6f
decl_stmt|;
DECL|method|TestConstantScoreRangeQuery
specifier|public
name|TestConstantScoreRangeQuery
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
DECL|method|TestConstantScoreRangeQuery
specifier|public
name|TestConstantScoreRangeQuery
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|field|small
name|Directory
name|small
decl_stmt|;
DECL|method|assertEquals
name|void
name|assertEquals
parameter_list|(
name|String
name|m
parameter_list|,
name|float
name|e
parameter_list|,
name|float
name|a
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|m
argument_list|,
name|e
argument_list|,
name|a
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEquals
specifier|static
specifier|public
name|void
name|assertEquals
parameter_list|(
name|String
name|m
parameter_list|,
name|int
name|e
parameter_list|,
name|int
name|a
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|m
argument_list|,
name|e
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
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
name|String
index|[]
name|data
init|=
operator|new
name|String
index|[]
block|{
literal|"A 1 2 3 4 5 6"
block|,
literal|"Z       4 5 6"
block|,
literal|null
block|,
literal|"B   2   4 5 6"
block|,
literal|"Y     3   5 6"
block|,
literal|null
block|,
literal|"C     3     6"
block|,
literal|"X       4 5 6"
block|}
decl_stmt|;
name|small
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|small
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
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
name|data
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
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|//Field.Keyword("id",String.valueOf(i)));
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"all"
argument_list|,
literal|"all"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|//Field.Keyword("all","all"));
if|if
condition|(
literal|null
operator|!=
name|data
index|[
name|i
index|]
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"data"
argument_list|,
name|data
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|//Field.Text("data",data[i]));
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** macro for readability */
DECL|method|csrq
specifier|public
specifier|static
name|Query
name|csrq
parameter_list|(
name|String
name|f
parameter_list|,
name|String
name|l
parameter_list|,
name|String
name|h
parameter_list|,
name|boolean
name|il
parameter_list|,
name|boolean
name|ih
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreRangeQuery
argument_list|(
name|f
argument_list|,
name|l
argument_list|,
name|h
argument_list|,
name|il
argument_list|,
name|ih
argument_list|)
return|;
block|}
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|QueryUtils
operator|.
name|check
argument_list|(
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|,
literal|"6"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"A"
argument_list|,
literal|"Z"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|,
literal|"6"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"A"
argument_list|,
literal|"Z"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualScores
specifier|public
name|void
name|testEqualScores
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: uses index build in *this* setUp
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Hits
name|result
decl_stmt|;
comment|// some hits match more terms then others, score should be the same
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|,
literal|"6"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|result
operator|.
name|length
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of results"
argument_list|,
literal|6
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|result
operator|.
name|score
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numHits
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"score for "
operator|+
name|i
operator|+
literal|" was not the same"
argument_list|,
name|score
argument_list|,
name|result
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBoost
specifier|public
name|void
name|testBoost
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: uses index build in *this* setUp
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// test for correct application of query normalization
comment|// must use a non score normalizing method for this.
name|Query
name|q
init|=
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|,
literal|"6"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"score for doc "
operator|+
name|doc
operator|+
literal|" was not correct"
argument_list|,
literal|1.0f
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//
comment|// Ensure that boosting works to score one clause of a query higher
comment|// than another.
comment|//
name|Query
name|q1
init|=
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"A"
argument_list|,
literal|"A"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
decl_stmt|;
comment|// matches document #0
name|q1
operator|.
name|setBoost
argument_list|(
literal|.1f
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"Z"
argument_list|,
literal|"Z"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
decl_stmt|;
comment|// matches document #1
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|bq
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
name|bq
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|search
operator|.
name|search
argument_list|(
name|bq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
operator|>
name|hits
operator|.
name|score
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|q1
operator|=
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"A"
argument_list|,
literal|"A"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
expr_stmt|;
comment|// matches document #0
name|q1
operator|.
name|setBoost
argument_list|(
literal|10f
argument_list|)
expr_stmt|;
name|q2
operator|=
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"Z"
argument_list|,
literal|"Z"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
expr_stmt|;
comment|// matches document #1
name|bq
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bq
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
name|bq
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|hits
operator|=
name|search
operator|.
name|search
argument_list|(
name|bq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
operator|>
name|hits
operator|.
name|score
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanOrderUnAffected
specifier|public
name|void
name|testBooleanOrderUnAffected
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: uses index build in *this* setUp
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// first do a regular RangeQuery which uses term expansion so
comment|// docs with more terms in range get higher scores
name|Query
name|rq
init|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|,
name|T
argument_list|)
decl_stmt|;
name|Hits
name|expected
init|=
name|search
operator|.
name|search
argument_list|(
name|rq
argument_list|)
decl_stmt|;
name|int
name|numHits
init|=
name|expected
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// now do a boolean where which also contains a
comment|// ConstantScoreRangeQuery and make sure hte order is the same
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|rq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|//T, F);
name|q
operator|.
name|add
argument_list|(
name|csrq
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|,
literal|"6"
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|//T, F);
name|Hits
name|actual
init|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong numebr of hits"
argument_list|,
name|numHits
argument_list|,
name|actual
operator|.
name|length
argument_list|()
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
name|numHits
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"mismatch in docid for hit#"
operator|+
name|i
argument_list|,
name|expected
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|,
name|actual
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRangeQueryId
specifier|public
name|void
name|testRangeQueryId
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: uses index build in *super* setUp
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|medId
init|=
operator|(
operator|(
name|maxId
operator|-
name|minId
operator|)
operator|/
literal|2
operator|)
decl_stmt|;
name|String
name|minIP
init|=
name|pad
argument_list|(
name|minId
argument_list|)
decl_stmt|;
name|String
name|maxIP
init|=
name|pad
argument_list|(
name|maxId
argument_list|)
decl_stmt|;
name|String
name|medIP
init|=
name|pad
argument_list|(
name|medId
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"num of docs"
argument_list|,
name|numDocs
argument_list|,
literal|1
operator|+
name|maxId
operator|-
name|minId
argument_list|)
expr_stmt|;
name|Hits
name|result
decl_stmt|;
comment|// test id, bounded on both ends
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"find all"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but last"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but first"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but ends"
argument_list|,
name|numDocs
operator|-
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med and up"
argument_list|,
literal|1
operator|+
name|maxId
operator|-
name|medId
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|medIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"up to med"
argument_list|,
literal|1
operator|+
name|medId
operator|-
name|minId
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// unbounded id
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min and up"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max and down"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
literal|null
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not min, but up"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not max, but down"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med and up, not max"
argument_list|,
name|maxId
operator|-
name|medId
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|medIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not min, up to med"
argument_list|,
name|medId
operator|-
name|minId
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// very small sets
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|minIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|medIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med,med,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|maxIP
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|minIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
name|minIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nul,min,F,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|maxIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|maxIP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,nul,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|medIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med,med,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeQueryRand
specifier|public
name|void
name|testRangeQueryRand
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTE: uses index build in *super* setUp
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|minRP
init|=
name|pad
argument_list|(
name|minR
argument_list|)
decl_stmt|;
name|String
name|maxRP
init|=
name|pad
argument_list|(
name|maxR
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"num of docs"
argument_list|,
name|numDocs
argument_list|,
literal|1
operator|+
name|maxId
operator|-
name|minId
argument_list|)
expr_stmt|;
name|Hits
name|result
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|)
argument_list|)
decl_stmt|;
comment|// test extremes, bounded on both ends
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"find all"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but biggest"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but smallest"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but extremes"
argument_list|,
name|numDocs
operator|-
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// unbounded
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"smallest and up"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
literal|null
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"biggest and down"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
literal|null
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not smallest, but up"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
literal|null
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not biggest, but down"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// very small sets
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|minRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|maxRP
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|minRP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
literal|null
argument_list|,
name|minRP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nul,min,F,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|maxRP
argument_list|,
name|maxRP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|(
literal|"rand"
argument_list|,
name|maxRP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,nul,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

