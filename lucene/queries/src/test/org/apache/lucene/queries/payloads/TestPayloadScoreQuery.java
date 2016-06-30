begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|payloads
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenFilter
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
name|TokenStream
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
name|Tokenizer
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
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|FieldInvertState
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
name|NoMergePolicy
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
name|CollectionStatistics
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
name|Explanation
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
name|TermStatistics
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
name|search
operator|.
name|similarities
operator|.
name|ClassicSimilarity
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
name|spans
operator|.
name|SpanContainingQuery
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanTermQuery
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
name|English
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

begin_class
DECL|class|TestPayloadScoreQuery
specifier|public
class|class
name|TestPayloadScoreQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|checkQuery
specifier|private
specifier|static
name|void
name|checkQuery
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|PayloadFunction
name|function
parameter_list|,
name|int
index|[]
name|expectedDocs
parameter_list|,
name|float
index|[]
name|expectedScores
parameter_list|)
throws|throws
name|IOException
block|{
name|checkQuery
argument_list|(
name|query
argument_list|,
name|function
argument_list|,
literal|true
argument_list|,
name|expectedDocs
argument_list|,
name|expectedScores
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQuery
specifier|private
specifier|static
name|void
name|checkQuery
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|PayloadFunction
name|function
parameter_list|,
name|boolean
name|includeSpanScore
parameter_list|,
name|int
index|[]
name|expectedDocs
parameter_list|,
name|float
index|[]
name|expectedScores
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
literal|"Expected docs and scores arrays must be the same length!"
argument_list|,
name|expectedDocs
operator|.
name|length
operator|==
name|expectedScores
operator|.
name|length
argument_list|)
expr_stmt|;
name|PayloadScoreQuery
name|psq
init|=
operator|new
name|PayloadScoreQuery
argument_list|(
name|query
argument_list|,
name|function
argument_list|,
name|includeSpanScore
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|psq
argument_list|,
name|expectedDocs
operator|.
name|length
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
name|hits
operator|.
name|scoreDocs
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
name|expectedDocs
operator|.
name|length
operator|-
literal|1
condition|)
name|fail
argument_list|(
literal|"Unexpected hit in document "
operator|+
name|hits
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
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
operator|!=
name|expectedDocs
index|[
name|i
index|]
condition|)
name|fail
argument_list|(
literal|"Unexpected hit in document "
operator|+
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad score in document "
operator|+
name|expectedDocs
index|[
name|i
index|]
argument_list|,
name|expectedScores
index|[
name|i
index|]
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hits
operator|.
name|scoreDocs
operator|.
name|length
operator|>
name|expectedDocs
operator|.
name|length
condition|)
name|fail
argument_list|(
literal|"Unexpected hit in document "
operator|+
name|hits
operator|.
name|scoreDocs
index|[
name|expectedDocs
operator|.
name|length
index|]
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|psq
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTermQuery
specifier|public
name|void
name|testTermQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|SpanTermQuery
name|q
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"eighteen"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|PayloadFunction
name|fn
range|:
operator|new
name|PayloadFunction
index|[]
block|{
operator|new
name|AveragePayloadFunction
argument_list|()
block|,
operator|new
name|MaxPayloadFunction
argument_list|()
block|,
operator|new
name|MinPayloadFunction
argument_list|()
block|}
control|)
block|{
name|checkQuery
argument_list|(
name|q
argument_list|,
name|fn
argument_list|,
operator|new
name|int
index|[]
block|{
literal|118
block|,
literal|218
block|,
literal|18
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|,
literal|2.0f
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOrQuery
specifier|public
name|void
name|testOrQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|SpanOrQuery
name|q
init|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"eighteen"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nineteen"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|PayloadFunction
name|fn
range|:
operator|new
name|PayloadFunction
index|[]
block|{
operator|new
name|AveragePayloadFunction
argument_list|()
block|,
operator|new
name|MaxPayloadFunction
argument_list|()
block|,
operator|new
name|MinPayloadFunction
argument_list|()
block|}
control|)
block|{
name|checkQuery
argument_list|(
name|q
argument_list|,
name|fn
argument_list|,
operator|new
name|int
index|[]
block|{
literal|118
block|,
literal|119
block|,
literal|218
block|,
literal|219
block|,
literal|18
block|,
literal|19
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|,
literal|4.0f
block|,
literal|4.0f
block|,
literal|2.0f
block|,
literal|2.0f
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNearQuery
specifier|public
name|void
name|testNearQuery
parameter_list|()
throws|throws
name|IOException
block|{
comment|//   2     4
comment|// twenty two
comment|//  2     4      4     4
comment|// one hundred twenty two
name|SpanNearQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"twenty"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|22
block|,
literal|122
block|,
literal|222
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|,
literal|4.0f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MinPayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|122
block|,
literal|222
block|,
literal|22
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|,
literal|2.0f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|122
block|,
literal|222
block|,
literal|22
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|,
literal|3.0f
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedNearQuery
specifier|public
name|void
name|testNestedNearQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// (one OR hundred) NEAR (twenty two) ~ 1
comment|//  2    4        4    4
comment|// one hundred twenty two
comment|// two hundred twenty two
name|SpanNearQuery
name|q
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"hundred"
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"twenty"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// check includeSpanScore makes a difference here
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|MultiplyingSimilarity
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|122
block|,
literal|222
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|41.802513122558594f
block|,
literal|34.13160705566406f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MinPayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|222
block|,
literal|122
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|34.13160705566406f
block|,
literal|20.901256561279297f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|122
block|,
literal|222
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|38.3189697265625f
block|,
literal|34.13160705566406f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|new
name|int
index|[]
block|{
literal|122
block|,
literal|222
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MinPayloadFunction
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|new
name|int
index|[]
block|{
literal|222
block|,
literal|122
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|2.0f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|new
name|int
index|[]
block|{
literal|222
block|,
literal|122
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|3.666666f
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSpanContainingQuery
specifier|public
name|void
name|testSpanContainingQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// twenty WITHIN ((one OR hundred) NEAR two)~2
name|SpanContainingQuery
name|q
init|=
operator|new
name|SpanContainingQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"hundred"
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"twenty"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|222
block|,
literal|122
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|3.666666f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|122
block|,
literal|222
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|4.0f
block|}
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|q
argument_list|,
operator|new
name|MinPayloadFunction
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|222
block|,
literal|122
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|4.0f
block|,
literal|2.0f
block|}
argument_list|)
expr_stmt|;
block|}
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|static
name|BoostingSimilarity
name|similarity
init|=
operator|new
name|BoostingSimilarity
argument_list|()
decl_stmt|;
DECL|field|payload2
specifier|private
specifier|static
name|byte
index|[]
name|payload2
init|=
operator|new
name|byte
index|[]
block|{
literal|2
block|}
decl_stmt|;
DECL|field|payload4
specifier|private
specifier|static
name|byte
index|[]
name|payload4
init|=
operator|new
name|byte
index|[]
block|{
literal|4
block|}
decl_stmt|;
DECL|class|PayloadAnalyzer
specifier|private
specifier|static
class|class
name|PayloadAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|result
argument_list|,
operator|new
name|PayloadFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|PayloadFilter
specifier|private
specifier|static
class|class
name|PayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|numSeen
specifier|private
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
DECL|field|payAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payAtt
decl_stmt|;
DECL|method|PayloadFilter
specifier|public
name|PayloadFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|payAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|numSeen
operator|%
literal|4
operator|==
literal|0
condition|)
block|{
name|payAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payload2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payload4
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numSeen
operator|++
expr_stmt|;
name|result
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|numSeen
operator|=
literal|0
expr_stmt|;
block|}
block|}
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
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
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|PayloadAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
argument_list|)
decl_stmt|;
comment|//writer.infoStream = System.out;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|300
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
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|txt
init|=
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|' '
operator|+
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field2"
argument_list|,
name|txt
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
DECL|class|MultiplyingSimilarity
specifier|static
class|class
name|MultiplyingSimilarity
extends|extends
name|ClassicSimilarity
block|{
annotation|@
name|Override
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
comment|//we know it is size 4 here, so ignore the offset/length
return|return
name|payload
operator|.
name|bytes
index|[
name|payload
operator|.
name|offset
index|]
return|;
block|}
block|}
DECL|class|BoostingSimilarity
specifier|static
class|class
name|BoostingSimilarity
extends|extends
name|MultiplyingSimilarity
block|{
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
comment|//Make everything else 1 so we see the effect of the payload
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
annotation|@
name|Override
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
comment|// idf used for phrase queries
annotation|@
name|Override
DECL|method|idfExplain
specifier|public
name|Explanation
name|idfExplain
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
index|[]
name|termStats
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
literal|1.0f
argument_list|,
literal|"Inexplicable"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|idfExplain
specifier|public
name|Explanation
name|idfExplain
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
literal|1.0f
argument_list|,
literal|"Inexplicable"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

