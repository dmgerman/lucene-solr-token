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
comment|/**  * Copyright 2005 Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Hits
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
name|Similarity
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
name|DefaultSimilarity
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_comment
comment|/**  * Test of the DisjunctionMaxQuery.  *  */
end_comment

begin_class
DECL|class|TestDisjunctionMaxQuery
specifier|public
class|class
name|TestDisjunctionMaxQuery
extends|extends
name|TestCase
block|{
comment|/** threshold for comparing floats */
DECL|field|SCORE_COMP_THRESH
specifier|public
specifier|static
specifier|final
name|float
name|SCORE_COMP_THRESH
init|=
literal|0.0000f
decl_stmt|;
comment|/**      * Similarity to eliminate tf, idf and lengthNorm effects to      * isolate test case.      *      *<p>      * same as TestRankingSimilarity in TestRanking.zip from      * http://issues.apache.org/jira/browse/LUCENE-323      *</p>      * @author Williams      */
DECL|class|TestSimilarity
specifier|private
specifier|static
class|class
name|TestSimilarity
extends|extends
name|DefaultSimilarity
block|{
DECL|method|TestSimilarity
specifier|public
name|TestSimilarity
parameter_list|()
block|{         }
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
if|if
condition|(
name|freq
operator|>
literal|0.0f
condition|)
return|return
literal|1.0f
return|;
else|else
return|return
literal|0.0f
return|;
block|}
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
block|}
DECL|field|sim
specifier|public
name|Similarity
name|sim
init|=
operator|new
name|TestSimilarity
argument_list|()
decl_stmt|;
DECL|field|index
specifier|public
name|Directory
name|index
decl_stmt|;
DECL|field|r
specifier|public
name|IndexReader
name|r
decl_stmt|;
DECL|field|s
specifier|public
name|IndexSearcher
name|s
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|index
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
name|index
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
comment|// hed is the most important field, dek is secondary
comment|// d1 is an "ok" match for:  albino elephant
block|{
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"d1"
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
block|}
comment|// d2 is a "good" match for:  albino elephant
block|{
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"d2"
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
block|}
comment|// d3 is a "better" match for:  albino elephant
block|{
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"d3"
argument_list|)
argument_list|)
expr_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
block|}
comment|// d4 is the "best" match for:  albino elephant
block|{
name|Document
name|d4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"d4"
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d4
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|s
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleEqualScores1
specifier|public
name|void
name|testSimpleEqualScores1
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"all docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|h
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
name|h
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"score #"
operator|+
name|i
operator|+
literal|" is not the same"
argument_list|,
name|score
argument_list|,
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testSimpleEqualScores1"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testSimpleEqualScores2
specifier|public
name|void
name|testSimpleEqualScores2
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"3 docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|3
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|h
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
name|h
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"score #"
operator|+
name|i
operator|+
literal|" is not the same"
argument_list|,
name|score
argument_list|,
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testSimpleEqualScores2"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testSimpleEqualScores3
specifier|public
name|void
name|testSimpleEqualScores3
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"all docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|h
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
name|h
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"score #"
operator|+
name|i
operator|+
literal|" is not the same"
argument_list|,
name|score
argument_list|,
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testSimpleEqualScores3"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testSimpleTiebreaker
specifier|public
name|void
name|testSimpleTiebreaker
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.01f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"3 docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|3
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first"
argument_list|,
literal|"d2"
argument_list|,
name|h
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|score0
init|=
name|h
operator|.
name|score
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|float
name|score1
init|=
name|h
operator|.
name|score
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
name|h
operator|.
name|score
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"d2 does not have better score then others: "
operator|+
name|score0
operator|+
literal|">? "
operator|+
name|score1
argument_list|,
name|score0
operator|>
name|score1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d4 and d1 don't have equal scores"
argument_list|,
name|score1
argument_list|,
name|score2
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testSimpleTiebreaker"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testBooleanRequiredEqualScores
specifier|public
name|void
name|testBooleanRequiredEqualScores
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
block|{
name|DisjunctionMaxQuery
name|q1
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|{
name|DisjunctionMaxQuery
name|q2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q2
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"3 docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|3
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|h
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
name|h
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"score #"
operator|+
name|i
operator|+
literal|" is not the same"
argument_list|,
name|score
argument_list|,
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testBooleanRequiredEqualScores1"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testBooleanOptionalNoTiebreaker
specifier|public
name|void
name|testBooleanOptionalNoTiebreaker
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
block|{
name|DisjunctionMaxQuery
name|q1
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|{
name|DisjunctionMaxQuery
name|q2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"4 docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|h
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
name|h
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
comment|/* note: -1 */
name|assertEquals
argument_list|(
literal|"score #"
operator|+
name|i
operator|+
literal|" is not the same"
argument_list|,
name|score
argument_list|,
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"wrong last"
argument_list|,
literal|"d1"
argument_list|,
name|h
operator|.
name|doc
argument_list|(
name|h
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|score1
init|=
name|h
operator|.
name|score
argument_list|(
name|h
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"d1 does not have worse score then others: "
operator|+
name|score
operator|+
literal|">? "
operator|+
name|score1
argument_list|,
name|score
operator|>
name|score1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testBooleanOptionalNoTiebreaker"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testBooleanOptionalWithTiebreaker
specifier|public
name|void
name|testBooleanOptionalWithTiebreaker
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
block|{
name|DisjunctionMaxQuery
name|q1
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.01f
argument_list|)
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|{
name|DisjunctionMaxQuery
name|q2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.01f
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"4 docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score0
init|=
name|h
operator|.
name|score
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|float
name|score1
init|=
name|h
operator|.
name|score
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
name|h
operator|.
name|score
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|float
name|score3
init|=
name|h
operator|.
name|score
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|doc0
init|=
name|h
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|doc1
init|=
name|h
operator|.
name|doc
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|doc2
init|=
name|h
operator|.
name|doc
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|doc3
init|=
name|h
operator|.
name|doc
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc0 should be d2 or d4: "
operator|+
name|doc0
argument_list|,
name|doc0
operator|.
name|equals
argument_list|(
literal|"d2"
argument_list|)
operator|||
name|doc0
operator|.
name|equals
argument_list|(
literal|"d4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"doc1 should be d2 or d4: "
operator|+
name|doc0
argument_list|,
name|doc1
operator|.
name|equals
argument_list|(
literal|"d2"
argument_list|)
operator|||
name|doc1
operator|.
name|equals
argument_list|(
literal|"d4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"score0 and score1 should match"
argument_list|,
name|score0
argument_list|,
name|score1
argument_list|,
name|SCORE_COMP_THRESH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong third"
argument_list|,
literal|"d3"
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"d3 does not have worse score then d2 and d4: "
operator|+
name|score1
operator|+
literal|">? "
operator|+
name|score2
argument_list|,
name|score1
operator|>
name|score2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong fourth"
argument_list|,
literal|"d1"
argument_list|,
name|doc3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"d1 does not have worse score then d3: "
operator|+
name|score2
operator|+
literal|">? "
operator|+
name|score3
argument_list|,
name|score2
operator|>
name|score3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testBooleanOptionalWithTiebreaker"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|testBooleanOptionalWithTiebreakerAndBoost
specifier|public
name|void
name|testBooleanOptionalWithTiebreakerAndBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
block|{
name|DisjunctionMaxQuery
name|q1
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.01f
argument_list|)
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"albino"
argument_list|,
literal|1.5f
argument_list|)
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"albino"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|{
name|DisjunctionMaxQuery
name|q2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.01f
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"hed"
argument_list|,
literal|"elephant"
argument_list|,
literal|1.5f
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"dek"
argument_list|,
literal|"elephant"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|q2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"4 docs should match "
operator|+
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score0
init|=
name|h
operator|.
name|score
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|float
name|score1
init|=
name|h
operator|.
name|score
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
name|h
operator|.
name|score
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|float
name|score3
init|=
name|h
operator|.
name|score
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|doc0
init|=
name|h
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|doc1
init|=
name|h
operator|.
name|doc
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|doc2
init|=
name|h
operator|.
name|doc
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|doc3
init|=
name|h
operator|.
name|doc
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"doc0 should be d4: "
argument_list|,
literal|"d4"
argument_list|,
name|doc0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc1 should be d3: "
argument_list|,
literal|"d3"
argument_list|,
name|doc1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc2 should be d2: "
argument_list|,
literal|"d2"
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc3 should be d1: "
argument_list|,
literal|"d1"
argument_list|,
name|doc3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"d4 does not have a better score then d3: "
operator|+
name|score0
operator|+
literal|">? "
operator|+
name|score1
argument_list|,
name|score0
operator|>
name|score1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"d3 does not have a better score then d2: "
operator|+
name|score1
operator|+
literal|">? "
operator|+
name|score2
argument_list|,
name|score1
operator|>
name|score2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"d3 does not have a better score then d1: "
operator|+
name|score2
operator|+
literal|">? "
operator|+
name|score3
argument_list|,
name|score2
operator|>
name|score3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|printHits
argument_list|(
literal|"testBooleanOptionalWithTiebreakerAndBoost"
argument_list|,
name|h
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/** macro */
DECL|method|tq
specifier|protected
name|Query
name|tq
parameter_list|(
name|String
name|f
parameter_list|,
name|String
name|t
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|f
argument_list|,
name|t
argument_list|)
argument_list|)
return|;
block|}
comment|/** macro */
DECL|method|tq
specifier|protected
name|Query
name|tq
parameter_list|(
name|String
name|f
parameter_list|,
name|String
name|t
parameter_list|,
name|float
name|b
parameter_list|)
block|{
name|Query
name|q
init|=
name|tq
argument_list|(
name|f
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|q
return|;
block|}
DECL|method|printHits
specifier|protected
name|void
name|printHits
parameter_list|(
name|String
name|test
parameter_list|,
name|Hits
name|h
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"------- "
operator|+
name|test
operator|+
literal|" -------"
argument_list|)
expr_stmt|;
name|DecimalFormat
name|f
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"0.000000000"
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
name|h
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|float
name|score
init|=
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"#"
operator|+
name|i
operator|+
literal|": "
operator|+
name|f
operator|.
name|format
argument_list|(
name|score
argument_list|)
operator|+
literal|" - "
operator|+
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

