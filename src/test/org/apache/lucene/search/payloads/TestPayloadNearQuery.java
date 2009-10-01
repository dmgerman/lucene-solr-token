begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
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
name|io
operator|.
name|Reader
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
name|LowerCaseTokenizer
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
name|Token
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
name|Payload
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
name|Searcher
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
name|store
operator|.
name|RAMDirectory
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

begin_class
DECL|class|TestPayloadNearQuery
specifier|public
class|class
name|TestPayloadNearQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|similarity
specifier|private
name|BoostingSimilarity
name|similarity
init|=
operator|new
name|BoostingSimilarity
argument_list|()
decl_stmt|;
DECL|field|payload2
specifier|private
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
DECL|method|TestPayloadNearQuery
specifier|public
name|TestPayloadNearQuery
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|class|PayloadAnalyzer
specifier|private
class|class
name|PayloadAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|PayloadFilter
argument_list|(
name|result
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|class|PayloadFilter
specifier|private
class|class
name|PayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|numSeen
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
DECL|field|payAtt
specifier|protected
name|PayloadAttribute
name|payAtt
decl_stmt|;
DECL|method|PayloadFilter
specifier|public
name|PayloadFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
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
operator|==
literal|true
condition|)
block|{
if|if
condition|(
name|numSeen
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|payAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
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
name|Payload
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
block|}
DECL|method|newPhraseQuery
specifier|private
name|PayloadNearQuery
name|newPhraseQuery
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|phrase
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
name|int
name|n
decl_stmt|;
name|String
index|[]
name|words
init|=
name|phrase
operator|.
name|split
argument_list|(
literal|"[\\s]+"
argument_list|)
decl_stmt|;
name|SpanQuery
name|clauses
index|[]
init|=
operator|new
name|SpanQuery
index|[
name|words
operator|.
name|length
index|]
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
index|[
name|i
index|]
operator|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|words
index|[
name|i
index|]
argument_list|)
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PayloadNearQuery
argument_list|(
name|clauses
argument_list|,
literal|0
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
DECL|method|setUp
specifier|protected
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
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|PayloadAnalyzer
name|analyzer
init|=
operator|new
name|PayloadAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
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
literal|1000
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"twenty two"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// all 10 hits should have score = 3 because adjacent terms have payloads of 2,4
comment|// and all the similarity factors are set to 1
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be 10 hits"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|" hundred"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// all should have score = 3 because adjacent terms have payloads of 2,4
comment|// and all the similarity factors are set to 1
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be 100 hits"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
comment|//				System.out.println("Doc: " + doc.toString());
comment|//				System.out.println("Explain: " + searcher.explain(query, doc.doc));
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testLongerSpan
specifier|public
name|void
name|testLongerSpan
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"nine hundred ninety nine"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
comment|//		System.out.println("Doc: " + doc.toString());
comment|//		System.out.println("Explain: " + searcher.explain(query, doc.doc));
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"there should only be one hit"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// should have score = 3 because adjacent terms have payloads of 2,4
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplexNested
specifier|public
name|void
name|testComplexNested
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
comment|// combine ordered and unordered spans with some nesting to make sure all payloads are counted
name|SpanQuery
name|q1
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"nine hundred"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"ninety nine"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanQuery
name|q3
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"nine ninety"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
name|q4
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"hundred nine"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|PayloadNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
name|q2
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|PayloadNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q3
block|,
name|q4
block|}
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
block|}
decl_stmt|;
name|query
operator|=
operator|new
name|PayloadNearQuery
argument_list|(
name|clauses
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// should be only 1 hit - doc 999
name|assertTrue
argument_list|(
literal|"should only be one hit"
argument_list|,
name|hits
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// the score should be 3 - the average of all the underlying payloads
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
comment|//		System.out.println("Doc: " + doc.toString());
comment|//		System.out.println("Explain: " + searcher.explain(query, doc.doc));
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
comment|// must be static for weight serialization tests
DECL|class|BoostingSimilarity
specifier|static
class|class
name|BoostingSimilarity
extends|extends
name|DefaultSimilarity
block|{
comment|// TODO: Remove warning after API has been finalized
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|//we know it is size 4 here, so ignore the offset/length
return|return
name|payload
index|[
literal|0
index|]
return|;
block|}
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
comment|//Make everything else 1 so we see the effect of the payload
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
literal|1
return|;
block|}
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
literal|1
return|;
block|}
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
literal|1
return|;
block|}
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
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
literal|1
return|;
block|}
comment|// idf used for phrase queries
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|Collection
name|terms
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

