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
name|IndexOptions
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
name|IndexWriterConfig
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
name|LeafReaderContext
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
name|PostingsEnum
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
name|SlowCompositeReaderWrapper
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
name|Scorer
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
name|Weight
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
name|PayloadHelper
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
name|LuceneTestCase
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestTermQueryPostings
specifier|public
class|class
name|TestTermQueryPostings
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
literal|"f"
decl_stmt|;
DECL|field|DOC_FIELDS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DOC_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|"a b c d"
block|,
literal|"a a a a"
block|,
literal|"c d e f"
block|,
literal|"b d a g"
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testTermQueryPositions
specifier|public
name|void
name|testTermQueryPositions
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
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
argument_list|,
name|config
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|content
range|:
name|DOC_FIELDS
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
name|newField
argument_list|(
name|FIELD
argument_list|,
name|content
argument_list|,
name|TextField
operator|.
name|TYPE_NOT_STORED
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
name|reader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|tq
argument_list|,
name|PostingsEnum
operator|.
name|FLAG_POSITIONS
argument_list|)
decl_stmt|;
name|LeafReaderContext
name|ctx
init|=
operator|(
name|LeafReaderContext
operator|)
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|ctx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|reader
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
annotation|@
name|Test
DECL|method|testTermQueryOffsets
specifier|public
name|void
name|testTermQueryOffsets
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
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
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|content
range|:
name|DOC_FIELDS
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
name|newField
argument_list|(
name|FIELD
argument_list|,
name|content
argument_list|,
name|fieldType
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
name|reader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|tq
argument_list|,
name|PostingsEnum
operator|.
name|FLAG_OFFSETS
argument_list|)
decl_stmt|;
name|LeafReaderContext
name|ctx
init|=
operator|(
name|LeafReaderContext
operator|)
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|ctx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|startOffset
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|endOffset
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|startOffset
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|endOffset
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|startOffset
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|endOffset
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|startOffset
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|endOffset
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|startOffset
argument_list|()
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|endOffset
argument_list|()
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|startOffset
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scorer
operator|.
name|endOffset
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|reader
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
annotation|@
name|Test
DECL|method|testTermQueryPayloads
specifier|public
name|void
name|testTermQueryPayloads
parameter_list|()
throws|throws
name|Exception
block|{
name|PayloadHelper
name|helper
init|=
operator|new
name|PayloadHelper
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|helper
operator|.
name|setUp
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|FIELD
argument_list|,
literal|"seventy"
argument_list|)
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|tq
argument_list|,
name|PostingsEnum
operator|.
name|FLAG_PAYLOADS
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|searcher
operator|.
name|leafContexts
control|)
block|{
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|ctx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
continue|continue;
name|scorer
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|BytesRef
name|payload
init|=
name|scorer
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|payload
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|helper
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

