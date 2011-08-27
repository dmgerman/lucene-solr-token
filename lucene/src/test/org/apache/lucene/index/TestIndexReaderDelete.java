begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|MockDirectoryWrapper
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TestIndexReader
operator|.
name|addDoc
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TestIndexReader
operator|.
name|addDocumentWithFields
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TestIndexReader
operator|.
name|assertTermDocsCount
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TestIndexReader
operator|.
name|createDocument
import|;
end_import

begin_class
DECL|class|TestIndexReaderDelete
specifier|public
class|class
name|TestIndexReaderDelete
extends|extends
name|LuceneTestCase
block|{
DECL|method|deleteReaderReaderConflict
specifier|private
name|void
name|deleteReaderReaderConflict
parameter_list|(
name|boolean
name|optimize
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Term
name|searchTerm1
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
name|Term
name|searchTerm2
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
decl_stmt|;
name|Term
name|searchTerm3
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|)
decl_stmt|;
comment|//  add 100 documents with term : aaa
comment|//  add 100 documents with term : bbb
comment|//  add 100 documents with term : ccc
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|searchTerm1
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
name|searchTerm2
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
name|searchTerm3
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|optimize
condition|)
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
comment|// OPEN TWO READERS
comment|// Both readers get segment info as exists at this time
name|IndexReader
name|reader1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first opened"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first opened"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first opened"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first opened"
argument_list|,
name|reader1
argument_list|,
name|searchTerm1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first opened"
argument_list|,
name|reader1
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first opened"
argument_list|,
name|reader1
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|IndexReader
name|reader2
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first opened"
argument_list|,
literal|100
argument_list|,
name|reader2
operator|.
name|docFreq
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first opened"
argument_list|,
literal|100
argument_list|,
name|reader2
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first opened"
argument_list|,
literal|100
argument_list|,
name|reader2
operator|.
name|docFreq
argument_list|(
name|searchTerm3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first opened"
argument_list|,
name|reader2
argument_list|,
name|searchTerm1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first opened"
argument_list|,
name|reader2
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first opened"
argument_list|,
name|reader2
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// DELETE DOCS FROM READER 2 and CLOSE IT
comment|// delete documents containing term: aaa
comment|// when the reader is closed, the segment info is updated and
comment|// the first reader is now stale
name|reader2
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"after delete 1"
argument_list|,
literal|100
argument_list|,
name|reader2
operator|.
name|docFreq
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"after delete 1"
argument_list|,
literal|100
argument_list|,
name|reader2
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"after delete 1"
argument_list|,
literal|100
argument_list|,
name|reader2
operator|.
name|docFreq
argument_list|(
name|searchTerm3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"after delete 1"
argument_list|,
name|reader2
argument_list|,
name|searchTerm1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"after delete 1"
argument_list|,
name|reader2
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"after delete 1"
argument_list|,
name|reader2
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make sure reader 1 is unchanged since it was open earlier
name|assertEquals
argument_list|(
literal|"after delete 1"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"after delete 1"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"after delete 1"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"after delete 1"
argument_list|,
name|reader1
argument_list|,
name|searchTerm1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"after delete 1"
argument_list|,
name|reader1
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"after delete 1"
argument_list|,
name|reader1
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// ATTEMPT TO DELETE FROM STALE READER
comment|// delete documents containing term: bbb
try|try
block|{
name|reader1
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Delete allowed from a stale index reader"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* success */
block|}
comment|// RECREATE READER AND TRY AGAIN
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader1
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reopened"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reopened"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reopened"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"reopened"
argument_list|,
name|reader1
argument_list|,
name|searchTerm1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"reopened"
argument_list|,
name|reader1
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"reopened"
argument_list|,
name|reader1
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted 2"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted 2"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted 2"
argument_list|,
literal|100
argument_list|,
name|reader1
operator|.
name|docFreq
argument_list|(
name|searchTerm3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted 2"
argument_list|,
name|reader1
argument_list|,
name|searchTerm1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted 2"
argument_list|,
name|reader1
argument_list|,
name|searchTerm2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted 2"
argument_list|,
name|reader1
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Open another reader to confirm that everything is deleted
name|reader2
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"reopened 2"
argument_list|,
name|reader2
argument_list|,
name|searchTerm1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"reopened 2"
argument_list|,
name|reader2
argument_list|,
name|searchTerm2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"reopened 2"
argument_list|,
name|reader2
argument_list|,
name|searchTerm3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader2
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
DECL|method|deleteReaderWriterConflict
specifier|private
name|void
name|deleteReaderWriterConflict
parameter_list|(
name|boolean
name|optimize
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Directory dir = new RAMDirectory();
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
name|Term
name|searchTerm2
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
decl_stmt|;
comment|//  add 100 documents with term : aaa
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|searchTerm
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// OPEN READER AT THIS POINT - this should fix the view of the
comment|// index at the point of having 100 "aaa" documents and 0 "bbb"
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// add 100 documents with term : bbb
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|searchTerm2
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// REQUEST OPTIMIZATION
comment|// This causes a new segment to become current for all subsequent
comment|// searchers. Because of this, deletions made via a previously open
comment|// reader, which would be applied to that reader's segment, are lost
comment|// for subsequent searchers/readers
if|if
condition|(
name|optimize
condition|)
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
comment|// The reader should not see the new data
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// DELETE DOCUMENTS CONTAINING TERM: aaa
comment|// NOTE: the reader was created when only "aaa" documents were in
name|int
name|deleted
init|=
literal|0
decl_stmt|;
try|try
block|{
name|deleted
operator|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Delete allowed on an index reader with stale segment information"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StaleReaderException
name|e
parameter_list|)
block|{
comment|/* success */
block|}
comment|// Re-open index reader and try again. This time it should see
comment|// the new data.
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|deleted
operator|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted count"
argument_list|,
literal|100
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted termDocs"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted termDocs"
argument_list|,
name|reader
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// CREATE A NEW READER and re-test
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted termDocs"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted termDocs"
argument_list|,
name|reader
argument_list|,
name|searchTerm2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
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
DECL|method|testBasicDelete
specifier|public
name|void
name|testBasicDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
comment|//  add 100 documents with term : aaa
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|searchTerm
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// OPEN READER AT THIS POINT - this should fix the view of the
comment|// index at the point of having 100 "aaa" documents and 0 "bbb"
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"first reader"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// DELETE DOCUMENTS CONTAINING TERM: aaa
name|int
name|deleted
init|=
literal|0
decl_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|deleted
operator|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted count"
argument_list|,
literal|100
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted docFreq"
argument_list|,
literal|100
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted termDocs"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// open a 2nd reader to make sure first reader can
comment|// commit its changes (.del) while second reader
comment|// is open:
name|IndexReader
name|reader2
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// CREATE A NEW READER and re-test
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deleted docFreq"
argument_list|,
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermDocsCount
argument_list|(
literal|"deleted termDocs"
argument_list|,
name|reader
argument_list|,
name|searchTerm
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
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
DECL|method|testDeleteReaderReaderConflictUnoptimized
specifier|public
name|void
name|testDeleteReaderReaderConflictUnoptimized
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteReaderReaderConflict
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteReaderReaderConflictOptimized
specifier|public
name|void
name|testDeleteReaderReaderConflictOptimized
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteReaderReaderConflict
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteReaderWriterConflictUnoptimized
specifier|public
name|void
name|testDeleteReaderWriterConflictUnoptimized
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteReaderWriterConflict
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteReaderWriterConflictOptimized
specifier|public
name|void
name|testDeleteReaderWriterConflictOptimized
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteReaderWriterConflict
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiReaderDeletes
specifier|public
name|void
name|testMultiReaderDeletes
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
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
name|newField
argument_list|(
literal|"f"
argument_list|,
literal|"doctor"
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f"
argument_list|,
literal|"who"
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|r
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|r
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"doctor"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|r
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|getLiveDocs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"who"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|getLiveDocs
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|r
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
DECL|method|testUndeleteAll
specifier|public
name|void
name|testUndeleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// nothing has really been deleted thanks to undeleteAll()
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
DECL|method|testUndeleteAllAfterClose
specifier|public
name|void
name|testUndeleteAllAfterClose
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|reader
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// nothing has really been deleted thanks to undeleteAll()
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
DECL|method|testUndeleteAllAfterCloseThenReopen
specifier|public
name|void
name|testUndeleteAllAfterCloseThenReopen
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|reader
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// nothing has really been deleted thanks to undeleteAll()
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
comment|// LUCENE-1647
DECL|method|testIndexReaderUnDeleteAll
specifier|public
name|void
name|testIndexReaderUnDeleteAll
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|flush
argument_list|()
expr_stmt|;
name|reader
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|reader
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
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

end_unit

