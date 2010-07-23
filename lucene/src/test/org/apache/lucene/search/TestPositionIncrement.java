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
name|Reader
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
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|MultiFields
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
name|DocsAndPositionsEnum
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
name|queryParser
operator|.
name|QueryParser
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
name|search
operator|.
name|payloads
operator|.
name|PayloadSpanUtil
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
name|search
operator|.
name|spans
operator|.
name|Spans
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
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|RegExp
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

begin_comment
comment|/**  * Term position unit test.  *  *  * @version $Revision$  */
end_comment

begin_class
DECL|class|TestPositionIncrement
specifier|public
class|class
name|TestPositionIncrement
extends|extends
name|LuceneTestCase
block|{
DECL|field|VERBOSE
specifier|final
specifier|static
name|boolean
name|VERBOSE
init|=
literal|false
decl_stmt|;
DECL|method|testSetPosition
specifier|public
name|void
name|testSetPosition
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
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
return|return
operator|new
name|TokenStream
argument_list|()
block|{
specifier|private
specifier|final
name|String
index|[]
name|TOKENS
init|=
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"5"
block|}
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|INCREMENTS
init|=
block|{
literal|0
block|,
literal|2
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
decl_stmt|;
specifier|private
name|int
name|i
init|=
literal|0
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|i
operator|==
name|TOKENS
operator|.
name|length
condition|)
return|return
literal|false
return|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
name|TOKENS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|i
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|INCREMENTS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|Directory
name|store
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|newRandom
argument_list|()
argument_list|,
name|store
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"bogus"
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
name|d
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|pos
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
argument_list|,
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"1"
argument_list|)
argument_list|)
decl_stmt|;
name|pos
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
comment|// first token should be at position 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|pos
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|pos
operator|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
argument_list|,
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|pos
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
comment|// second token should be at position 2
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pos
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
decl_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// same as previous, just specify positions explicitely.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// specifying correct positions should find the phrase.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// phrase query would find it when correct positions are specified.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// phrase query should fail for non existing searched term
comment|// even if there exist another searched terms in the same searched position.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"9"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// multi-phrase query should succed for non existing searched term
comment|// because there exist another searched terms in the same searched position.
name|MultiPhraseQuery
name|mq
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|mq
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"9"
argument_list|)
block|}
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|mq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// should not find "1 2" because there is a gap of 1 in the index
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"field"
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|,
name|stopStopList
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// omitted stop word cannot help because stop filter swallows the increments.
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// query parser alone won't help, because stop filter swallows the increments.
name|qp
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// stop filter alone won't help, because query parser swallows the increments.
name|qp
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// when both qp qnd stopFilter propagate increments, we should find the doc.
name|qp
operator|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"field"
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|,
name|stopStopList
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|qp
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// stoplist that accepts case-insensitive "stop"
DECL|field|stopStopList
specifier|private
specifier|static
specifier|final
name|CharacterRunAutomaton
name|stopStopList
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"[sS][tT][oO][pP]"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|testPayloadsPos0
specifier|public
name|void
name|testPayloadsPos0
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|newRandom
argument_list|()
argument_list|,
name|dir
argument_list|,
operator|new
name|TestPayloadAnalyzer
argument_list|()
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
name|Field
argument_list|(
literal|"content"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"a a b c d e a f g h i j a b k k"
argument_list|)
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
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|DocsAndPositionsEnum
name|tp
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|r
argument_list|)
argument_list|,
literal|"content"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|nextDoc
argument_list|()
operator|!=
name|tp
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// "a" occurs 4 times
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tp
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|expected
init|=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|// only one doc has "a"
name|assertEquals
argument_list|(
name|tp
operator|.
name|NO_MORE_DOCS
argument_list|,
name|tp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
name|is
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|stq1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|stq2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"k"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
index|[]
name|sqs
init|=
block|{
name|stq1
block|,
name|stq2
block|}
decl_stmt|;
name|SpanNearQuery
name|snq
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|sqs
argument_list|,
literal|30
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|boolean
name|sawZero
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\ngetPayloadSpans test"
argument_list|)
expr_stmt|;
block|}
name|Spans
name|pspans
init|=
name|snq
operator|.
name|getSpans
argument_list|(
name|is
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|pspans
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"doc "
operator|+
name|pspans
operator|.
name|doc
argument_list|()
operator|+
literal|": span "
operator|+
name|pspans
operator|.
name|start
argument_list|()
operator|+
literal|" to "
operator|+
name|pspans
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloads
init|=
name|pspans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|sawZero
operator||=
name|pspans
operator|.
name|start
argument_list|()
operator|==
literal|0
expr_stmt|;
for|for
control|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|byte
index|[]
name|bytes
range|:
name|payloads
control|)
block|{
name|count
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|VERBOSE
condition|)
block|{
comment|// do nothing
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  payload: "
operator|+
operator|new
name|String
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sawZero
argument_list|)
expr_stmt|;
comment|// System.out.println("\ngetSpans test");
name|Spans
name|spans
init|=
name|snq
operator|.
name|getSpans
argument_list|(
name|is
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|sawZero
operator|=
literal|false
expr_stmt|;
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|sawZero
operator||=
name|spans
operator|.
name|start
argument_list|()
operator|==
literal|0
expr_stmt|;
comment|// System.out.println(spans.doc() + " - " + spans.start() + " - " +
comment|// spans.end());
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sawZero
argument_list|)
expr_stmt|;
comment|// System.out.println("\nPayloadSpanUtil test");
name|sawZero
operator|=
literal|false
expr_stmt|;
name|PayloadSpanUtil
name|psu
init|=
operator|new
name|PayloadSpanUtil
argument_list|(
name|is
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|pls
init|=
name|psu
operator|.
name|getPayloadsForQuery
argument_list|(
name|snq
argument_list|)
decl_stmt|;
name|count
operator|=
name|pls
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|byte
index|[]
name|bytes
range|:
name|pls
control|)
block|{
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
comment|//System.out.println(s);
name|sawZero
operator||=
name|s
operator|.
name|equals
argument_list|(
literal|"pos: 0"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sawZero
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|.
name|getIndexReader
argument_list|()
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
end_class

begin_class
DECL|class|TestPayloadAnalyzer
specifier|final
class|class
name|TestPayloadAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
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
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|PayloadFilter
argument_list|(
name|result
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|PayloadFilter
specifier|final
class|class
name|PayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|i
name|int
name|i
decl_stmt|;
DECL|field|posIncrAttr
specifier|final
name|PositionIncrementAttribute
name|posIncrAttr
decl_stmt|;
DECL|field|payloadAttr
specifier|final
name|PayloadAttribute
name|payloadAttr
decl_stmt|;
DECL|field|termAttr
specifier|final
name|CharTermAttribute
name|termAttr
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
name|pos
operator|=
literal|0
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
name|posIncrAttr
operator|=
name|input
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|payloadAttr
operator|=
name|input
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|termAttr
operator|=
name|input
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
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
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|payloadAttr
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
operator|(
literal|"pos: "
operator|+
name|pos
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|posIncr
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|posIncr
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|posIncr
operator|=
literal|0
expr_stmt|;
block|}
name|posIncrAttr
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|posIncr
expr_stmt|;
if|if
condition|(
name|TestPositionIncrement
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"term="
operator|+
name|termAttr
operator|+
literal|" pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

