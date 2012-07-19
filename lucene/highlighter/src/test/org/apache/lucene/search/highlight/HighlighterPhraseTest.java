begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|FieldType
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
name|DirectoryReader
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
name|search
operator|.
name|Collector
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
name|PhraseQuery
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
name|FixedBitSet
import|;
end_import

begin_class
DECL|class|HighlighterPhraseTest
specifier|public
class|class
name|HighlighterPhraseTest
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
literal|"text"
decl_stmt|;
DECL|method|testConcurrentPhrase
specifier|public
name|void
name|testConcurrentPhrase
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox jumped"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamConcurrent
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"jumped"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|setSlop
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|indexReader
operator|.
name|getTermVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
operator|new
name|TokenStreamConcurrent
argument_list|()
argument_list|,
name|TEXT
argument_list|)
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testConcurrentSpan
specifier|public
name|void
name|testConcurrentSpan
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox jumped"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamConcurrent
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|phraseQuery
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
name|FIELD
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"jumped"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|bitset
init|=
operator|new
name|FixedBitSet
argument_list|(
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
specifier|private
name|int
name|baseDoc
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|bitset
operator|.
name|set
argument_list|(
name|this
operator|.
name|baseDoc
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|baseDoc
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
name|scorer
parameter_list|)
block|{
comment|// Do Nothing
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bitset
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|indexReader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|position
init|=
name|bitset
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|position
operator|>=
literal|0
operator|&&
name|position
operator|<
name|maxDoc
operator|-
literal|1
condition|;
name|position
operator|=
name|bitset
operator|.
name|nextSetBit
argument_list|(
name|position
operator|+
literal|1
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|position
argument_list|)
expr_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|indexReader
operator|.
name|getTermVector
argument_list|(
name|position
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
operator|new
name|TokenStreamConcurrent
argument_list|()
argument_list|,
name|TEXT
argument_list|)
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testSparsePhrase
specifier|public
name|void
name|testSparsePhrase
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamSparse
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"did"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"jump"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|setSlop
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|indexReader
operator|.
name|getTermVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
operator|new
name|TokenStreamSparse
argument_list|()
argument_list|,
name|TEXT
argument_list|)
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testSparsePhraseWithNoPositions
specifier|public
name|void
name|testSparsePhraseWithNoPositions
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
name|TEXT
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"did"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"jump"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|indexReader
operator|.
name|getTermVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"the fox<B>did</B> not<B>jump</B>"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testSparseSpan
specifier|public
name|void
name|testSparseSpan
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamSparse
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|phraseQuery
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
name|FIELD
argument_list|,
literal|"did"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"jump"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|indexReader
operator|.
name|getTermVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
operator|new
name|TokenStreamSparse
argument_list|()
argument_list|,
name|TEXT
argument_list|)
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|class|TokenStreamSparse
specifier|private
specifier|static
specifier|final
class|class
name|TokenStreamSparse
extends|extends
name|TokenStream
block|{
DECL|field|tokens
specifier|private
name|Token
index|[]
name|tokens
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|positionIncrementAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|positionIncrementAttribute
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TokenStreamSparse
specifier|public
name|TokenStreamSparse
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
name|this
operator|.
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|i
operator|>=
name|this
operator|.
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|startOffset
argument_list|()
argument_list|,
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|i
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
operator|new
name|Token
index|[]
block|{
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'t'
block|,
literal|'h'
block|,
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'f'
block|,
literal|'o'
block|,
literal|'x'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'d'
block|,
literal|'i'
block|,
literal|'d'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'j'
block|,
literal|'u'
block|,
literal|'m'
block|,
literal|'p'
block|}
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
block|}
expr_stmt|;
name|this
operator|.
name|tokens
index|[
literal|3
index|]
operator|.
name|setPositionIncrement
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TokenStreamConcurrent
specifier|private
specifier|static
specifier|final
class|class
name|TokenStreamConcurrent
extends|extends
name|TokenStream
block|{
DECL|field|tokens
specifier|private
name|Token
index|[]
name|tokens
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|positionIncrementAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|positionIncrementAttribute
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TokenStreamConcurrent
specifier|public
name|TokenStreamConcurrent
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
name|this
operator|.
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|i
operator|>=
name|this
operator|.
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|startOffset
argument_list|()
argument_list|,
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|i
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
operator|new
name|Token
index|[]
block|{
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'t'
block|,
literal|'h'
block|,
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'f'
block|,
literal|'o'
block|,
literal|'x'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'j'
block|,
literal|'u'
block|,
literal|'m'
block|,
literal|'p'
block|}
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|8
argument_list|,
literal|14
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'j'
block|,
literal|'u'
block|,
literal|'m'
block|,
literal|'p'
block|,
literal|'e'
block|,
literal|'d'
block|}
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
literal|8
argument_list|,
literal|14
argument_list|)
block|}
expr_stmt|;
name|this
operator|.
name|tokens
index|[
literal|3
index|]
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

