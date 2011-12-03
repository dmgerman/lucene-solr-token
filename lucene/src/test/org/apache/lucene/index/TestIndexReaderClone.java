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
name|LockObtainFailedException
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * Tests cloning multiple types of readers, modifying the liveDocs and norms  * and verifies copy on write semantics of the liveDocs and norms is  * implemented properly  */
end_comment

begin_class
DECL|class|TestIndexReaderClone
specifier|public
class|class
name|TestIndexReaderClone
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCloneReadOnlySegmentReader
specifier|public
name|void
name|testCloneReadOnlySegmentReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexReader
name|readOnlyReader
init|=
name|reader
operator|.
name|clone
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isReadOnly
argument_list|(
name|readOnlyReader
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"reader isn't read only"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|readOnlyReader
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"deleting from the original should not have worked"
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|readOnlyReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open non-readOnly reader1, clone to non-readOnly
comment|// reader2, make sure we can change reader2
DECL|method|testCloneNoChangesStillReadOnly
specifier|public
name|void
name|testCloneNoChangesStillReadOnly
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|r1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexReader
name|r2
init|=
name|r1
operator|.
name|clone
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|r2
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"deleting from the cloned should have worked"
argument_list|)
expr_stmt|;
block|}
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open non-readOnly reader1, clone to non-readOnly
comment|// reader2, make sure we can change reader1
DECL|method|testCloneWriteToOrig
specifier|public
name|void
name|testCloneWriteToOrig
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|r1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexReader
name|r2
init|=
name|r1
operator|.
name|clone
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|r1
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"deleting from the original should have worked"
argument_list|)
expr_stmt|;
block|}
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open non-readOnly reader1, clone to non-readOnly
comment|// reader2, make sure we can change reader2
DECL|method|testCloneWriteToClone
specifier|public
name|void
name|testCloneWriteToClone
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|r1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexReader
name|r2
init|=
name|r1
operator|.
name|clone
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|r2
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"deleting from the original should have worked"
argument_list|)
expr_stmt|;
block|}
comment|// should fail because reader1 holds the write lock
name|assertTrue
argument_list|(
literal|"first reader should not be able to delete"
argument_list|,
operator|!
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|r1
argument_list|)
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// should fail because we are now stale (reader1
comment|// committed changes)
name|assertTrue
argument_list|(
literal|"first reader should not be able to delete"
argument_list|,
operator|!
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|r1
argument_list|)
argument_list|)
expr_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open non-readOnly reader1, clone to readOnly reader2
DECL|method|testCloneWriteableToReadOnly
specifier|public
name|void
name|testCloneWriteableToReadOnly
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexReader
name|readOnlyReader
init|=
name|reader
operator|.
name|clone
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isReadOnly
argument_list|(
name|readOnlyReader
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"reader isn't read only"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|readOnlyReader
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"deleting from the original should not have worked"
argument_list|)
expr_stmt|;
block|}
comment|// this readonly reader shouldn't have a write lock
if|if
condition|(
name|readOnlyReader
operator|.
name|hasChanges
condition|)
block|{
name|fail
argument_list|(
literal|"readOnlyReader has a write lock"
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|readOnlyReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open non-readOnly reader1, reopen to readOnly reader2
DECL|method|testReopenWriteableToReadOnly
specifier|public
name|void
name|testReopenWriteableToReadOnly
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docCount
operator|-
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|readOnlyReader
init|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|readOnlyReader
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isReadOnly
argument_list|(
name|readOnlyReader
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"reader isn't read only"
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|readOnlyReader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docCount
operator|-
literal|1
argument_list|,
name|readOnlyReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|readOnlyReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open readOnly reader1, clone to non-readOnly reader2
DECL|method|testCloneReadOnlyToWriteable
specifier|public
name|void
name|testCloneReadOnlyToWriteable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|reader1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexReader
name|reader2
init|=
name|reader1
operator|.
name|clone
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|isReadOnly
argument_list|(
name|reader2
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"reader should not be read only"
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"deleting from the original reader should not have worked"
argument_list|,
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|reader1
argument_list|)
argument_list|)
expr_stmt|;
comment|// this readonly reader shouldn't yet have a write lock
if|if
condition|(
name|reader2
operator|.
name|hasChanges
condition|)
block|{
name|fail
argument_list|(
literal|"cloned reader should not have write lock"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"deleting from the cloned reader should have worked"
argument_list|,
name|deleteWorked
argument_list|(
literal|1
argument_list|,
name|reader2
argument_list|)
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// open non-readOnly reader1 on multi-segment index, then
comment|// fully merge the index, then clone to readOnly reader2
DECL|method|testReadOnlyCloneAfterFullMerge
specifier|public
name|void
name|testReadOnlyCloneAfterFullMerge
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|reader1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
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
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader2
init|=
name|reader1
operator|.
name|clone
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isReadOnly
argument_list|(
name|reader2
argument_list|)
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteWorked
specifier|private
specifier|static
name|boolean
name|deleteWorked
parameter_list|(
name|int
name|doc
parameter_list|,
name|IndexReader
name|r
parameter_list|)
block|{
name|boolean
name|exception
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// trying to delete from the original reader should throw an exception
name|r
operator|.
name|deleteDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|exception
operator|=
literal|true
expr_stmt|;
block|}
return|return
operator|!
name|exception
return|;
block|}
DECL|method|testCloneReadOnlyDirectoryReader
specifier|public
name|void
name|testCloneReadOnlyDirectoryReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexReader
name|readOnlyReader
init|=
name|reader
operator|.
name|clone
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isReadOnly
argument_list|(
name|readOnlyReader
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"reader isn't read only"
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|readOnlyReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|isReadOnly
specifier|public
specifier|static
name|boolean
name|isReadOnly
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
if|if
condition|(
name|r
operator|instanceof
name|SegmentReader
condition|)
block|{
return|return
operator|(
operator|(
name|SegmentReader
operator|)
name|r
operator|)
operator|.
name|readOnly
return|;
block|}
elseif|else
if|if
condition|(
name|r
operator|instanceof
name|DirectoryReader
condition|)
block|{
return|return
operator|(
operator|(
name|DirectoryReader
operator|)
name|r
operator|)
operator|.
name|readOnly
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|testSegmentReaderUndeleteall
specifier|public
name|void
name|testSegmentReaderUndeleteall
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SegmentReader
name|origSegmentReader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|origSegmentReader
operator|.
name|deleteDocument
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|1
argument_list|,
name|origSegmentReader
argument_list|)
expr_stmt|;
name|origSegmentReader
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|origSegmentReader
operator|.
name|liveDocsRef
argument_list|)
expr_stmt|;
name|origSegmentReader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// need to test norms?
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSegmentReaderCloseReferencing
specifier|public
name|void
name|testSegmentReaderCloseReferencing
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SegmentReader
name|origSegmentReader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|origSegmentReader
operator|.
name|deleteDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|SegmentReader
name|clonedSegmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|origSegmentReader
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|2
argument_list|,
name|origSegmentReader
argument_list|)
expr_stmt|;
name|origSegmentReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|1
argument_list|,
name|origSegmentReader
argument_list|)
expr_stmt|;
name|clonedSegmentReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSegmentReaderDelDocsReferenceCounting
specifier|public
name|void
name|testSegmentReaderDelDocsReferenceCounting
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|origReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentReader
name|origSegmentReader
init|=
name|getOnlySegmentReader
argument_list|(
name|origReader
argument_list|)
decl_stmt|;
comment|// liveDocsRef should be null because nothing has updated yet
name|assertNull
argument_list|(
name|origSegmentReader
operator|.
name|liveDocsRef
argument_list|)
expr_stmt|;
comment|// we deleted a document, so there is now a liveDocs bitvector and a
comment|// reference to it
name|origReader
operator|.
name|deleteDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|1
argument_list|,
name|origSegmentReader
argument_list|)
expr_stmt|;
comment|// the cloned segmentreader should have 2 references, 1 to itself, and 1 to
comment|// the original segmentreader
name|IndexReader
name|clonedReader
init|=
operator|(
name|IndexReader
operator|)
name|origReader
operator|.
name|clone
argument_list|()
decl_stmt|;
name|SegmentReader
name|clonedSegmentReader
init|=
name|getOnlySegmentReader
argument_list|(
name|clonedReader
argument_list|)
decl_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|2
argument_list|,
name|origSegmentReader
argument_list|)
expr_stmt|;
comment|// deleting a document creates a new liveDocs bitvector, the refs goes to
comment|// 1
name|clonedReader
operator|.
name|deleteDocument
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|1
argument_list|,
name|origSegmentReader
argument_list|)
expr_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|1
argument_list|,
name|clonedSegmentReader
argument_list|)
expr_stmt|;
comment|// make sure the deletedocs objects are different (copy
comment|// on write)
name|assertTrue
argument_list|(
name|origSegmentReader
operator|.
name|liveDocs
operator|!=
name|clonedSegmentReader
operator|.
name|liveDocs
argument_list|)
expr_stmt|;
name|assertDocDeleted
argument_list|(
name|origSegmentReader
argument_list|,
name|clonedSegmentReader
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|origSegmentReader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// doc 2 should not be deleted
comment|// in original segmentreader
name|assertFalse
argument_list|(
name|clonedSegmentReader
operator|.
name|getLiveDocs
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// doc 2 should be deleted in
comment|// cloned segmentreader
comment|// deleting a doc from the original segmentreader should throw an exception
try|try
block|{
name|origReader
operator|.
name|deleteDocument
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|lbfe
parameter_list|)
block|{
comment|// expected
block|}
name|origReader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try closing the original segment reader to see if it affects the
comment|// clonedSegmentReader
name|clonedReader
operator|.
name|deleteDocument
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|clonedReader
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|1
argument_list|,
name|clonedSegmentReader
argument_list|)
expr_stmt|;
comment|// test a reopened reader
name|IndexReader
name|reopenedReader
init|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|clonedReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|reopenedReader
operator|==
literal|null
condition|)
block|{
name|reopenedReader
operator|=
name|clonedReader
expr_stmt|;
block|}
name|IndexReader
name|cloneReader2
init|=
operator|(
name|IndexReader
operator|)
name|reopenedReader
operator|.
name|clone
argument_list|()
decl_stmt|;
name|SegmentReader
name|cloneSegmentReader2
init|=
name|getOnlySegmentReader
argument_list|(
name|cloneReader2
argument_list|)
decl_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|2
argument_list|,
name|cloneSegmentReader2
argument_list|)
expr_stmt|;
name|clonedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reopenedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|cloneReader2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-1648
DECL|method|testCloneWithDeletes
specifier|public
name|void
name|testCloneWithDeletes
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|origReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|origReader
operator|.
name|deleteDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|clonedReader
init|=
operator|(
name|IndexReader
operator|)
name|origReader
operator|.
name|clone
argument_list|()
decl_stmt|;
name|origReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|clonedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|r
argument_list|)
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
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertDocDeleted
specifier|private
name|void
name|assertDocDeleted
parameter_list|(
name|SegmentReader
name|reader
parameter_list|,
name|SegmentReader
name|reader2
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|,
name|reader2
operator|.
name|getLiveDocs
argument_list|()
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDelDocsRefCountEquals
specifier|private
name|void
name|assertDelDocsRefCountEquals
parameter_list|(
name|int
name|refCount
parameter_list|,
name|SegmentReader
name|reader
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|refCount
argument_list|,
name|reader
operator|.
name|liveDocsRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCloneSubreaders
specifier|public
name|void
name|testCloneSubreaders
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// acquire write lock
name|IndexReader
index|[]
name|subs
init|=
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
assert|assert
name|subs
operator|.
name|length
operator|>
literal|1
assert|;
name|IndexReader
index|[]
name|clones
init|=
operator|new
name|IndexReader
index|[
name|subs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|subs
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|clones
index|[
name|x
index|]
operator|=
operator|(
name|IndexReader
operator|)
name|subs
index|[
name|x
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|subs
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|clones
index|[
name|x
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLucene1516Bug
specifier|public
name|void
name|testLucene1516Bug
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|r1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|r1
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|IndexReader
name|r2
init|=
name|r1
operator|.
name|clone
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|r1
operator|.
name|deleteDocument
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|r1
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|r1
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|r1
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCloseStoredFields
specifier|public
name|void
name|testCloseStoredFields
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
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
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|)
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
literal|"field"
argument_list|,
literal|"yes it's stored"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r1
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
name|IndexReader
name|r2
init|=
name|r1
operator|.
name|clone
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
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

