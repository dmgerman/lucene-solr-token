begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|HashMap
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
name|CorruptIndexException
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
name|TopDocs
import|;
end_import

begin_comment
comment|/**  * Test FieldScoreQuery search.  *<p>  * Tests here create an index with a few documents, each having  * an int value indexed  field and a float value indexed field.  * The values of these fields are later used for scoring.  *<p>  * The rank tests use Hits to verify that docs are ordered (by score) as expected.  *<p>  * The exact score tests use TopDocs top to verify the exact score.    */
end_comment

begin_class
DECL|class|TestFieldScoreQuery
specifier|public
class|class
name|TestFieldScoreQuery
extends|extends
name|FunctionTestSetup
block|{
comment|/* @override constructor */
DECL|method|TestFieldScoreQuery
specifier|public
name|TestFieldScoreQuery
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
comment|/** Test that FieldScoreQuery of Type.BYTE returns docs in expected order. */
DECL|method|testRankByte
specifier|public
name|void
name|testRankByte
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as byte
name|doTestRank
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|BYTE
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.SHORT returns docs in expected order. */
DECL|method|testRankShort
specifier|public
name|void
name|testRankShort
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as short
name|doTestRank
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.INT returns docs in expected order. */
DECL|method|testRankInt
specifier|public
name|void
name|testRankInt
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestRank
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|INT
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.FLOAT returns docs in expected order. */
DECL|method|testRankFloat
specifier|public
name|void
name|testRankFloat
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field can be parsed as float
name|doTestRank
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
comment|// same values, but in flot format
name|doTestRank
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
block|}
comment|// Test that FieldScoreQuery returns docs in expected order.
DECL|method|doTestRank
specifier|private
name|void
name|doTestRank
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldScoreQuery
operator|.
name|Type
name|tp
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|FieldScoreQuery
argument_list|(
name|field
argument_list|,
name|tp
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"test: "
operator|+
name|q
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|q
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
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
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|prevID
init|=
literal|"ID"
operator|+
operator|(
name|N_DOCS
operator|+
literal|1
operator|)
decl_stmt|;
comment|// greater than all ids of docs in this test
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|h
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|resID
init|=
name|s
operator|.
name|doc
argument_list|(
name|h
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|log
argument_list|(
name|i
operator|+
literal|".   score="
operator|+
name|h
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|"  -  "
operator|+
name|resID
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|h
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"res id "
operator|+
name|resID
operator|+
literal|" should be< prev res id "
operator|+
name|prevID
argument_list|,
name|resID
operator|.
name|compareTo
argument_list|(
name|prevID
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|prevID
operator|=
name|resID
expr_stmt|;
block|}
block|}
comment|/** Test that FieldScoreQuery of Type.BYTE returns the expected scores. */
DECL|method|testExactScoreByte
specifier|public
name|void
name|testExactScoreByte
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as byte
name|doTestExactScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|BYTE
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.SHORT returns the expected scores. */
DECL|method|testExactScoreShort
specifier|public
name|void
name|testExactScoreShort
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as short
name|doTestExactScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.INT returns the expected scores. */
DECL|method|testExactScoreInt
specifier|public
name|void
name|testExactScoreInt
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestExactScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|INT
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.FLOAT returns the expected scores. */
DECL|method|testExactScoreFloat
specifier|public
name|void
name|testExactScoreFloat
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field can be parsed as float
name|doTestExactScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
comment|// same values, but in flot format
name|doTestExactScore
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
block|}
comment|// Test that FieldScoreQuery returns docs with expected score.
DECL|method|doTestExactScore
specifier|private
name|void
name|doTestExactScore
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldScoreQuery
operator|.
name|Type
name|tp
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|FieldScoreQuery
argument_list|(
name|field
argument_list|,
name|tp
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ScoreDoc
name|sd
index|[]
init|=
name|td
operator|.
name|scoreDocs
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
name|sd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|float
name|score
init|=
name|sd
index|[
name|i
index|]
operator|.
name|score
decl_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|document
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|float
name|expectedScore
init|=
name|expectedFieldScore
argument_list|(
name|id
argument_list|)
decl_stmt|;
comment|// "ID7" --> 7.0
name|assertEquals
argument_list|(
literal|"score of "
operator|+
name|id
operator|+
literal|" shuould be "
operator|+
name|expectedScore
operator|+
literal|" != "
operator|+
name|score
argument_list|,
name|expectedScore
argument_list|,
name|score
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test that FieldScoreQuery of Type.BYTE caches/reuses loaded values and consumes the proper RAM resources. */
DECL|method|testCachingByte
specifier|public
name|void
name|testCachingByte
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as byte
name|doTestCaching
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|BYTE
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.SHORT caches/reuses loaded values and consumes the proper RAM resources. */
DECL|method|testCachingShort
specifier|public
name|void
name|testCachingShort
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as short
name|doTestCaching
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.INT caches/reuses loaded values and consumes the proper RAM resources. */
DECL|method|testCachingInt
specifier|public
name|void
name|testCachingInt
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestCaching
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|INT
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.FLOAT caches/reuses loaded values and consumes the proper RAM resources. */
DECL|method|testCachingFloat
specifier|public
name|void
name|testCachingFloat
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values can be parsed as float
name|doTestCaching
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
comment|// same values, but in flot format
name|doTestCaching
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
block|}
comment|// Test that values loaded for FieldScoreQuery are cached properly and consumes the proper RAM resources.
DECL|method|doTestCaching
specifier|private
name|void
name|doTestCaching
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldScoreQuery
operator|.
name|Type
name|tp
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// prepare expected array types for comparison
name|HashMap
name|expectedArrayTypes
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|expectedArrayTypes
operator|.
name|put
argument_list|(
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|BYTE
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|expectedArrayTypes
operator|.
name|put
argument_list|(
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|SHORT
argument_list|,
operator|new
name|short
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|expectedArrayTypes
operator|.
name|put
argument_list|(
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|INT
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|expectedArrayTypes
operator|.
name|put
argument_list|(
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
operator|new
name|float
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Object
name|innerArray
init|=
literal|null
decl_stmt|;
name|boolean
name|warned
init|=
literal|false
decl_stmt|;
comment|// print warning once.
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
name|FieldScoreQuery
name|q
init|=
operator|new
name|FieldScoreQuery
argument_list|(
name|field
argument_list|,
name|tp
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
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
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getSequentialSubReaders
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
name|readers
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|readers
index|[
name|j
index|]
decl_stmt|;
try|try
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|innerArray
operator|=
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|reader
argument_list|)
operator|.
name|getInnerArray
argument_list|()
expr_stmt|;
name|log
argument_list|(
name|i
operator|+
literal|".  compare: "
operator|+
name|innerArray
operator|.
name|getClass
argument_list|()
operator|+
literal|" to "
operator|+
name|expectedArrayTypes
operator|.
name|get
argument_list|(
name|tp
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field values should be cached in the correct array type!"
argument_list|,
name|innerArray
operator|.
name|getClass
argument_list|()
argument_list|,
name|expectedArrayTypes
operator|.
name|get
argument_list|(
name|tp
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
argument_list|(
name|i
operator|+
literal|".  compare: "
operator|+
name|innerArray
operator|+
literal|" to "
operator|+
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|reader
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"field values should be cached and reused!"
argument_list|,
name|innerArray
argument_list|,
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|reader
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|warned
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|testName
argument_list|()
operator|+
literal|" cannot fully test values of "
operator|+
name|q
argument_list|)
expr_stmt|;
name|warned
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// verify new values are reloaded (not reused) for a new reader
name|s
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FieldScoreQuery
name|q
init|=
operator|new
name|FieldScoreQuery
argument_list|(
name|field
argument_list|,
name|tp
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
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
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getSequentialSubReaders
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
name|readers
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|readers
index|[
name|j
index|]
decl_stmt|;
try|try
block|{
name|log
argument_list|(
literal|"compare: "
operator|+
name|innerArray
operator|+
literal|" to "
operator|+
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|reader
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"cached field values should not be reused if reader as changed!"
argument_list|,
name|innerArray
argument_list|,
name|q
operator|.
name|valSrc
operator|.
name|getValues
argument_list|(
name|reader
argument_list|)
operator|.
name|getInnerArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|warned
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|testName
argument_list|()
operator|+
literal|" cannot fully test values of "
operator|+
name|q
argument_list|)
expr_stmt|;
name|warned
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testName
specifier|private
name|String
name|testName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

