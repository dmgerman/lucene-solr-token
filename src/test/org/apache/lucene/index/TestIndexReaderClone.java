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
name|index
operator|.
name|SegmentReader
operator|.
name|Norm
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
name|analysis
operator|.
name|SimpleAnalyzer
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * Tests cloning multiple types of readers, modifying the deletedDocs and norms  * and verifies copy on write semantics of the deletedDocs and norms is  * implemented properly  */
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
argument_list|)
decl_stmt|;
name|IndexReader
name|readOnlyReader
init|=
operator|(
name|IndexReader
operator|)
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
name|DirectoryIndexReader
name|r2
init|=
operator|(
name|DirectoryIndexReader
operator|)
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
name|DirectoryIndexReader
name|r2
init|=
operator|(
name|DirectoryIndexReader
operator|)
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
name|DirectoryIndexReader
name|r2
init|=
operator|(
name|DirectoryIndexReader
operator|)
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
comment|// create single-segment index, open non-readOnly
comment|// SegmentReader, add docs, reopen to multireader, then do
comment|// delete
DECL|method|testReopenSegmentReaderToMultiReader
specifier|public
name|void
name|testReopenSegmentReaderToMultiReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir1
argument_list|,
literal|false
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
name|TestIndexReaderReopen
operator|.
name|modifyIndex
argument_list|(
literal|5
argument_list|,
name|dir1
argument_list|)
expr_stmt|;
name|IndexReader
name|reader2
init|=
name|reader1
operator|.
name|reopen
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|reader1
operator|!=
name|reader2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
name|DirectoryIndexReader
name|readOnlyReader
init|=
operator|(
name|DirectoryIndexReader
operator|)
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
name|DirectoryIndexReader
name|readOnlyReader
init|=
operator|(
name|DirectoryIndexReader
operator|)
name|reader
operator|.
name|reopen
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
name|DirectoryIndexReader
name|reader2
init|=
operator|(
name|DirectoryIndexReader
operator|)
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
comment|// optimize the index, then clone to readOnly reader2
DECL|method|testReadOnlyCloneAfterOptimize
specifier|public
name|void
name|testReadOnlyCloneAfterOptimize
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|w
operator|.
name|optimize
argument_list|()
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
DECL|method|testCloneReadOnlyMultiSegmentReader
specifier|public
name|void
name|testCloneReadOnlyMultiSegmentReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
argument_list|)
decl_stmt|;
name|IndexReader
name|readOnlyReader
init|=
operator|(
name|IndexReader
operator|)
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
name|ReadOnlySegmentReader
operator|||
name|r
operator|instanceof
name|ReadOnlyMultiSegmentReader
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
DECL|method|testParallelReader
specifier|public
name|void
name|testParallelReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Directory
name|dir2
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir2
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
argument_list|)
decl_stmt|;
name|IndexReader
name|r2
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|ParallelReader
name|pr1
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr1
operator|.
name|add
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|pr1
operator|.
name|add
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|performDefaultTests
argument_list|(
name|pr1
argument_list|)
expr_stmt|;
name|pr1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * 1. Get a norm from the original reader 2. Clone the original reader 3.    * Delete a document and set the norm of the cloned reader 4. Verify the norms    * are not the same on each reader 5. Verify the doc deleted is only in the    * cloned reader 6. Try to delete a document in the original reader, an    * exception should be thrown    *     * @param r1 IndexReader to perform tests on    * @throws Exception    */
DECL|method|performDefaultTests
specifier|private
name|void
name|performDefaultTests
parameter_list|(
name|IndexReader
name|r1
parameter_list|)
throws|throws
name|Exception
block|{
name|float
name|norm1
init|=
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|r1
operator|.
name|norms
argument_list|(
literal|"field1"
argument_list|)
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
name|IndexReader
name|pr1Clone
init|=
operator|(
name|IndexReader
operator|)
name|r1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|pr1Clone
operator|.
name|deleteDocument
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|pr1Clone
operator|.
name|setNorm
argument_list|(
literal|4
argument_list|,
literal|"field1"
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|r1
operator|.
name|norms
argument_list|(
literal|"field1"
argument_list|)
index|[
literal|4
index|]
argument_list|)
operator|==
name|norm1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|pr1Clone
operator|.
name|norms
argument_list|(
literal|"field1"
argument_list|)
index|[
literal|4
index|]
argument_list|)
operator|!=
name|norm1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|r1
operator|.
name|isDeleted
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pr1Clone
operator|.
name|isDeleted
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// try to update the original reader, which should throw an exception
try|try
block|{
name|r1
operator|.
name|deleteDocument
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Tried to delete doc 11 and an exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
comment|// expectted
block|}
name|pr1Clone
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMixedReaders
specifier|public
name|void
name|testMixedReaders
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir1
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Directory
name|dir2
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir2
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
argument_list|)
decl_stmt|;
name|IndexReader
name|r2
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|MultiReader
name|multiReader
init|=
operator|new
name|MultiReader
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|r1
block|,
name|r2
block|}
argument_list|)
decl_stmt|;
name|performDefaultTests
argument_list|(
name|multiReader
argument_list|)
expr_stmt|;
name|multiReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SegmentReader
name|origSegmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
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
name|deletedDocsRef
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SegmentReader
name|origSegmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
decl_stmt|;
name|origSegmentReader
operator|.
name|deleteDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|origSegmentReader
operator|.
name|setNorm
argument_list|(
literal|4
argument_list|,
literal|"field1"
argument_list|,
literal|0.5f
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
comment|// check the norm refs
name|Norm
name|norm
init|=
operator|(
name|Norm
operator|)
name|clonedSegmentReader
operator|.
name|norms
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|norm
operator|.
name|bytesRef
argument_list|()
operator|.
name|refCount
argument_list|()
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SegmentReader
name|origSegmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
decl_stmt|;
comment|// deletedDocsRef should be null because nothing has updated yet
name|assertNull
argument_list|(
name|origSegmentReader
operator|.
name|deletedDocsRef
argument_list|)
expr_stmt|;
comment|// we deleted a document, so there is now a deletedDocs bitvector and a
comment|// reference to it
name|origSegmentReader
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
comment|// deleting a document creates a new deletedDocs bitvector, the refs goes to
comment|// 1
name|clonedSegmentReader
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
name|deletedDocs
operator|!=
name|clonedSegmentReader
operator|.
name|deletedDocs
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
name|assertTrue
argument_list|(
operator|!
name|origSegmentReader
operator|.
name|isDeleted
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// doc 2 should not be deleted
comment|// in original segmentreader
name|assertTrue
argument_list|(
name|clonedSegmentReader
operator|.
name|isDeleted
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
name|origSegmentReader
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
name|origSegmentReader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try closing the original segment reader to see if it affects the
comment|// clonedSegmentReader
name|clonedSegmentReader
operator|.
name|deleteDocument
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|clonedSegmentReader
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
name|SegmentReader
name|reopenedSegmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|clonedSegmentReader
operator|.
name|reopen
argument_list|()
decl_stmt|;
name|SegmentReader
name|cloneSegmentReader2
init|=
operator|(
name|SegmentReader
operator|)
name|reopenedSegmentReader
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertDelDocsRefCountEquals
argument_list|(
literal|2
argument_list|,
name|cloneSegmentReader2
argument_list|)
expr_stmt|;
name|clonedSegmentReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reopenedSegmentReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|cloneSegmentReader2
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
name|isDeleted
argument_list|(
name|doc
argument_list|)
argument_list|,
name|reader2
operator|.
name|isDeleted
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
name|deletedDocsRef
operator|.
name|refCount
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|TestIndexReaderReopen
operator|.
name|createIndex
argument_list|(
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
operator|(
name|IndexReader
operator|)
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
block|}
end_class

end_unit

