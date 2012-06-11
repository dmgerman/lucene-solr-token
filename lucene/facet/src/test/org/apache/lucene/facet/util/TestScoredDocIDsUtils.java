begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|util
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
name|StringField
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|OpenBitSet
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
name|OpenBitSetDISI
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
name|facet
operator|.
name|search
operator|.
name|ScoredDocIDs
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
name|facet
operator|.
name|search
operator|.
name|ScoredDocIDsIterator
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
name|facet
operator|.
name|search
operator|.
name|ScoredDocIdCollector
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestScoredDocIDsUtils
specifier|public
class|class
name|TestScoredDocIDsUtils
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testComplementIterator
specifier|public
name|void
name|testComplementIterator
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|n
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|OpenBitSet
name|bits
init|=
operator|new
name|OpenBitSet
argument_list|(
name|n
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
literal|5
operator|*
name|n
condition|;
name|i
operator|++
control|)
block|{
name|bits
operator|.
name|flip
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|OpenBitSet
name|verify
init|=
operator|new
name|OpenBitSet
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|verify
operator|.
name|or
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|ScoredDocIDs
name|scoredDocIDs
init|=
name|ScoredDocIdsUtils
operator|.
name|createScoredDocIds
argument_list|(
name|bits
argument_list|,
name|n
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|createReaderWithNDocs
argument_list|(
name|random
argument_list|()
argument_list|,
name|n
argument_list|,
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|n
operator|-
name|verify
operator|.
name|cardinality
argument_list|()
argument_list|,
name|ScoredDocIdsUtils
operator|.
name|getComplementSet
argument_list|(
name|scoredDocIDs
argument_list|,
name|reader
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
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
annotation|@
name|Test
DECL|method|testAllDocs
specifier|public
name|void
name|testAllDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxDoc
init|=
literal|3
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|createReaderWithNDocs
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxDoc
argument_list|,
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|ScoredDocIDs
name|all
init|=
name|ScoredDocIdsUtils
operator|.
name|createAllDocsScoredDocIDs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"invalid size"
argument_list|,
name|maxDoc
argument_list|,
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ScoredDocIDsIterator
name|iter
init|=
name|all
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|"invalid doc ID: "
operator|+
name|iter
operator|.
name|getDocID
argument_list|()
argument_list|,
name|doc
operator|++
argument_list|,
name|iter
operator|.
name|getDocID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid score: "
operator|+
name|iter
operator|.
name|getScore
argument_list|()
argument_list|,
name|ScoredDocIDsIterator
operator|.
name|DEFAULT_SCORE
argument_list|,
name|iter
operator|.
name|getScore
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"invalid maxDoc: "
operator|+
name|doc
argument_list|,
name|maxDoc
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|DocIdSet
name|docIDs
init|=
name|all
operator|.
name|getDocIDs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"should be cacheable"
argument_list|,
name|docIDs
operator|.
name|isCacheable
argument_list|()
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|docIDsIter
init|=
name|docIDs
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nextDoc() hasn't been called yet"
argument_list|,
operator|-
literal|1
argument_list|,
name|docIDsIter
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|docIDsIter
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|docIDsIter
operator|.
name|advance
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// if advance is smaller than current doc, advance to cur+1.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|docIDsIter
operator|.
name|advance
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
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
annotation|@
name|Test
DECL|method|testWithDeletions
specifier|public
name|void
name|testWithDeletions
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|N_DOCS
init|=
literal|100
decl_stmt|;
name|DocumentFactory
name|docFactory
init|=
operator|new
name|DocumentFactory
argument_list|(
name|N_DOCS
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|markedDeleted
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
return|return
operator|(
name|docNum
operator|%
literal|3
operator|==
literal|0
operator|||
comment|// every 3rd documents, including first
name|docNum
operator|==
name|numDocs
operator|-
literal|1
operator|||
comment|// last document
name|docNum
operator|==
name|numDocs
operator|/
literal|2
operator|||
comment|// 3 consecutive documents in the middle
name|docNum
operator|==
literal|1
operator|+
name|numDocs
operator|/
literal|2
operator|||
name|docNum
operator|==
literal|2
operator|+
name|numDocs
operator|/
literal|2
operator|)
return|;
block|}
comment|// every 6th document (starting from the 2nd) would contain 'alpha'
annotation|@
name|Override
specifier|public
name|boolean
name|haveAlpha
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
return|return
operator|(
name|docNum
operator|%
literal|6
operator|==
literal|1
operator|)
return|;
block|}
block|}
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|createReaderWithNDocs
argument_list|(
name|random
argument_list|()
argument_list|,
name|N_DOCS
argument_list|,
name|docFactory
argument_list|,
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|ScoredDocIDs
name|allDocs
init|=
name|ScoredDocIdsUtils
operator|.
name|createAllDocsScoredDocIDs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ScoredDocIDsIterator
name|it
init|=
name|allDocs
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|numIteratedDocs
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|next
argument_list|()
condition|)
block|{
name|numIteratedDocs
operator|++
expr_stmt|;
name|int
name|docNum
init|=
name|it
operator|.
name|getDocID
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Deleted docs must not appear in the allDocsScoredDocIds set: "
operator|+
name|docNum
argument_list|,
name|reader
operator|.
name|document
argument_list|(
name|docNum
argument_list|)
operator|.
name|getField
argument_list|(
literal|"del"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of (live) documents"
argument_list|,
name|allDocs
operator|.
name|size
argument_list|()
argument_list|,
name|numIteratedDocs
argument_list|)
expr_stmt|;
comment|// Get all 'alpha' documents
name|ScoredDocIdCollector
name|collector
init|=
name|ScoredDocIdCollector
operator|.
name|create
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|false
argument_list|)
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
name|DocumentFactory
operator|.
name|field
argument_list|,
name|DocumentFactory
operator|.
name|alphaTxt
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|ScoredDocIDs
name|scoredDocIds
init|=
name|collector
operator|.
name|getScoredDocIDs
argument_list|()
decl_stmt|;
name|OpenBitSet
name|resultSet
init|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|scoredDocIds
operator|.
name|getDocIDs
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// Getting the complement set of the query result
name|ScoredDocIDs
name|complementSet
init|=
name|ScoredDocIdsUtils
operator|.
name|getComplementSet
argument_list|(
name|scoredDocIds
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of documents in complement set mismatch"
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
operator|-
name|scoredDocIds
operator|.
name|size
argument_list|()
argument_list|,
name|complementSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// now make sure the documents in the complement set are not deleted
comment|// and not in the original result set
name|ScoredDocIDsIterator
name|compIterator
init|=
name|complementSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Bits
name|live
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
while|while
condition|(
name|compIterator
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|docNum
init|=
name|compIterator
operator|.
name|getDocID
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Complement-Set must not contain deleted documents (doc="
operator|+
name|docNum
operator|+
literal|")"
argument_list|,
name|live
operator|!=
literal|null
operator|&&
operator|!
name|live
operator|.
name|get
argument_list|(
name|docNum
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Complement-Set must not contain docs from the original set (doc="
operator|+
name|docNum
operator|+
literal|")"
argument_list|,
name|reader
operator|.
name|document
argument_list|(
name|docNum
argument_list|)
operator|.
name|getField
argument_list|(
literal|"del"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Complement-Set must not contain docs from the original set (doc="
operator|+
name|docNum
operator|+
literal|")"
argument_list|,
name|resultSet
operator|.
name|fastGet
argument_list|(
name|docNum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
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
comment|/**    * Creates an index with n documents, this method is meant for testing purposes ONLY    */
DECL|method|createReaderWithNDocs
specifier|static
name|IndexReader
name|createReaderWithNDocs
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createReaderWithNDocs
argument_list|(
name|random
argument_list|,
name|nDocs
argument_list|,
operator|new
name|DocumentFactory
argument_list|(
name|nDocs
argument_list|)
argument_list|,
name|directory
argument_list|)
return|;
block|}
DECL|class|DocumentFactory
specifier|private
specifier|static
class|class
name|DocumentFactory
block|{
DECL|field|field
specifier|protected
specifier|final
specifier|static
name|String
name|field
init|=
literal|"content"
decl_stmt|;
DECL|field|delTxt
specifier|protected
specifier|final
specifier|static
name|String
name|delTxt
init|=
literal|"delete"
decl_stmt|;
DECL|field|alphaTxt
specifier|protected
specifier|final
specifier|static
name|String
name|alphaTxt
init|=
literal|"alpha"
decl_stmt|;
DECL|field|deletionMark
specifier|private
specifier|final
specifier|static
name|Field
name|deletionMark
init|=
operator|new
name|StringField
argument_list|(
name|field
argument_list|,
name|delTxt
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|alphaContent
specifier|private
specifier|final
specifier|static
name|Field
name|alphaContent
init|=
operator|new
name|StringField
argument_list|(
name|field
argument_list|,
name|alphaTxt
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|numDocs
specifier|protected
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|method|DocumentFactory
specifier|public
name|DocumentFactory
parameter_list|(
name|int
name|totalNumDocs
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|totalNumDocs
expr_stmt|;
block|}
DECL|method|markedDeleted
specifier|public
name|boolean
name|markedDeleted
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|getDoc
specifier|public
name|Document
name|getDoc
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|markedDeleted
argument_list|(
name|docNum
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|deletionMark
argument_list|)
expr_stmt|;
comment|// Add a special field for docs that are marked for deletion. Later we
comment|// assert that those docs are not returned by all-scored-doc-IDs.
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"del"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docNum
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|haveAlpha
argument_list|(
name|docNum
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|alphaContent
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|haveAlpha
specifier|public
name|boolean
name|haveAlpha
parameter_list|(
name|int
name|docNum
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|createReaderWithNDocs
specifier|static
name|IndexReader
name|createReaderWithNDocs
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|DocumentFactory
name|docFactory
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|nDocs
condition|;
name|docNum
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|docFactory
operator|.
name|getDoc
argument_list|(
name|docNum
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Delete documents marked for deletion
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
name|DocumentFactory
operator|.
name|field
argument_list|,
name|DocumentFactory
operator|.
name|delTxt
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Open a fresh read-only reader with the deletions in place
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
return|;
block|}
block|}
end_class

end_unit

