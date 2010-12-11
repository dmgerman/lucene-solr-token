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
name|_TestUtil
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
name|TestIndexWriter
operator|.
name|assertNoUnreferencedFiles
import|;
end_import

begin_comment
comment|/**  * Tests for IndexWriter when the disk runs out of space  */
end_comment

begin_class
DECL|class|TestIndexWriterOnDiskFull
specifier|public
class|class
name|TestIndexWriterOnDiskFull
extends|extends
name|LuceneTestCase
block|{
comment|/*    * Make sure IndexWriter cleans up on hitting a disk    * full exception in addDocument.    * TODO: how to do this on windows with FSDirectory?    */
DECL|method|testAddDocumentOnDiskFull
specifier|public
name|void
name|testAddDocumentOnDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
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
literal|"TEST: pass="
operator|+
name|pass
argument_list|)
expr_stmt|;
block|}
name|boolean
name|doAbort
init|=
name|pass
operator|==
literal|1
decl_stmt|;
name|long
name|diskFree
init|=
literal|200
decl_stmt|;
while|while
condition|(
literal|true
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
literal|"TEST: cycle: diskFree="
operator|+
name|diskFree
argument_list|)
expr_stmt|;
block|}
name|MockDirectoryWrapper
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|diskFree
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
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
name|MergeScheduler
name|ms
init|=
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
block|{
comment|// This test intentionally produces exceptions
comment|// in the threads that CMS launches; we don't
comment|// want to pollute test output with these.
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
block|}
name|boolean
name|hitError
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
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
literal|"TEST: done adding docs; now commit"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"TEST: exception on addDoc"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
name|hitError
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|hitError
condition|)
block|{
if|if
condition|(
name|doAbort
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
literal|"TEST: now rollback"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
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
literal|"TEST: now close"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"TEST: exception on close; retry w/ no disk space limit"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//_TestUtil.syncConcurrentMerges(ms);
if|if
condition|(
name|_TestUtil
operator|.
name|anyFilesExceptWriteLock
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|assertNoUnreferencedFiles
argument_list|(
name|dir
argument_list|,
literal|"after disk full during addDocument"
argument_list|)
expr_stmt|;
comment|// Make sure reader can open the index:
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
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now try again w/ more space:
name|diskFree
operator|+=
literal|500
expr_stmt|;
block|}
else|else
block|{
comment|//_TestUtil.syncConcurrentMerges(writer);
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/*   Test: make sure when we run out of disk space or hit   random IOExceptions in any of the addIndexes(*) calls   that 1) index is not corrupt (searcher can open/search   it) and 2) transactional semantics are followed:   either all or none of the incoming documents were in   fact added.    */
DECL|method|testAddIndexOnDiskFull
specifier|public
name|void
name|testAddIndexOnDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|START_COUNT
init|=
literal|57
decl_stmt|;
name|int
name|NUM_DIR
init|=
literal|50
decl_stmt|;
name|int
name|END_COUNT
init|=
name|START_COUNT
operator|+
name|NUM_DIR
operator|*
literal|25
decl_stmt|;
comment|// Build up a bunch of dirs that have indexes which we
comment|// will then merge together by calling addIndexes(*):
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
name|NUM_DIR
index|]
decl_stmt|;
name|long
name|inputDiskUsage
init|=
literal|0
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
name|NUM_DIR
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|25
condition|;
name|j
operator|++
control|)
block|{
name|addDocWithIndex
argument_list|(
name|writer
argument_list|,
literal|25
operator|*
name|i
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|files
init|=
name|dirs
index|[
name|i
index|]
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|inputDiskUsage
operator|+=
name|dirs
index|[
name|i
index|]
operator|.
name|fileLength
argument_list|(
name|files
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now, build a starting index that has START_COUNT docs.  We
comment|// will then try to addIndexesNoOptimize into a copy of this:
name|MockDirectoryWrapper
name|startDir
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
name|startDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|START_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|addDocWithIndex
argument_list|(
name|writer
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make sure starting index seems to be working properly:
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
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|startDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first docFreq"
argument_list|,
literal|57
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
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
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first number of hits"
argument_list|,
literal|57
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
comment|// Iterate with larger and larger amounts of free
comment|// disk space.  With little free disk space,
comment|// addIndexes will certainly run out of space&
comment|// fail.  Verify that when this happens, index is
comment|// not corrupt and index in fact has added no
comment|// documents.  Then, we increase disk space by 2000
comment|// bytes each iteration.  At some point there is
comment|// enough free disk space and addIndexes should
comment|// succeed and index should show all documents were
comment|// added.
comment|// String[] files = startDir.listAll();
name|long
name|diskUsage
init|=
name|startDir
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|long
name|startDiskUsage
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|files
init|=
name|startDir
operator|.
name|listAll
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|startDiskUsage
operator|+=
name|startDir
operator|.
name|fileLength
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|3
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
comment|// Start with 100 bytes more than we are currently using:
name|long
name|diskFree
init|=
name|diskUsage
operator|+
literal|100
decl_stmt|;
name|int
name|method
init|=
name|iter
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
name|String
name|methodName
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|method
condition|)
block|{
name|methodName
operator|=
literal|"addIndexes(Directory[]) + optimize()"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|1
operator|==
name|method
condition|)
block|{
name|methodName
operator|=
literal|"addIndexes(IndexReader[])"
expr_stmt|;
block|}
else|else
block|{
name|methodName
operator|=
literal|"addIndexes(Directory[])"
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|done
condition|)
block|{
comment|// Make a new dir that will enforce disk usage:
name|MockDirectoryWrapper
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|,
operator|new
name|RAMDirectory
argument_list|(
name|startDir
argument_list|)
argument_list|)
decl_stmt|;
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
argument_list|()
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
name|IOException
name|err
init|=
literal|null
decl_stmt|;
name|MergeScheduler
name|ms
init|=
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
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
literal|2
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
comment|// This test intentionally produces exceptions
comment|// in the threads that CMS launches; we don't
comment|// want to pollute test output with these.
if|if
condition|(
literal|0
operator|==
name|x
condition|)
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
else|else
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|clearSuppressExceptions
argument_list|()
expr_stmt|;
comment|// Two loops: first time, limit disk space&
comment|// throw random IOExceptions; second time, no
comment|// disk space limit:
name|double
name|rate
init|=
literal|0.05
decl_stmt|;
name|double
name|diskRatio
init|=
operator|(
operator|(
name|double
operator|)
name|diskFree
operator|)
operator|/
name|diskUsage
decl_stmt|;
name|long
name|thisDiskFree
decl_stmt|;
name|String
name|testName
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|thisDiskFree
operator|=
name|diskFree
expr_stmt|;
if|if
condition|(
name|diskRatio
operator|>=
literal|2.0
condition|)
block|{
name|rate
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|diskRatio
operator|>=
literal|4.0
condition|)
block|{
name|rate
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|diskRatio
operator|>=
literal|6.0
condition|)
block|{
name|rate
operator|=
literal|0.0
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|testName
operator|=
literal|"disk full test "
operator|+
name|methodName
operator|+
literal|" with disk full at "
operator|+
name|diskFree
operator|+
literal|" bytes"
expr_stmt|;
block|}
else|else
block|{
name|thisDiskFree
operator|=
literal|0
expr_stmt|;
name|rate
operator|=
literal|0.0
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|testName
operator|=
literal|"disk full test "
operator|+
name|methodName
operator|+
literal|" with unlimited disk space"
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\ncycle: "
operator|+
name|testName
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setTrackDiskUsage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|thisDiskFree
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|rate
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
literal|0
operator|==
name|method
condition|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|1
operator|==
name|method
condition|)
block|{
name|IndexReader
name|readers
index|[]
init|=
operator|new
name|IndexReader
index|[
name|dirs
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|readers
index|[
name|i
index|]
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|readers
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
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
literal|"  success!"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|err
operator|=
name|e
expr_stmt|;
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
literal|"  hit IOException: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|1
operator|==
name|x
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|methodName
operator|+
literal|" hit IOException after disk space was freed up"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Make sure all threads from
comment|// ConcurrentMergeScheduler are done
name|_TestUtil
operator|.
name|syncConcurrentMerges
argument_list|(
name|writer
argument_list|)
expr_stmt|;
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
literal|"  now test readers"
argument_list|)
expr_stmt|;
block|}
comment|// Finally, verify index is not corrupt, and, if
comment|// we succeeded, we see all docs added, and if we
comment|// failed, we see either all docs or no docs added
comment|// (transactional semantics):
try|try
block|{
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|": exception when creating IndexReader: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|int
name|result
init|=
name|reader
operator|.
name|docFreq
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|result
operator|!=
name|START_COUNT
condition|)
block|{
name|fail
argument_list|(
name|testName
operator|+
literal|": method did not throw exception but docFreq('aaa') is "
operator|+
name|result
operator|+
literal|" instead of expected "
operator|+
name|START_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// On hitting exception we still may have added
comment|// all docs:
if|if
condition|(
name|result
operator|!=
name|START_COUNT
operator|&&
name|result
operator|!=
name|END_COUNT
condition|)
block|{
name|err
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|": method did throw exception but docFreq('aaa') is "
operator|+
name|result
operator|+
literal|" instead of expected "
operator|+
name|START_COUNT
operator|+
literal|" or "
operator|+
name|END_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
try|try
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm
argument_list|)
argument_list|,
literal|null
argument_list|,
name|END_COUNT
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|": exception when searching: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|int
name|result2
init|=
name|hits
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|result2
operator|!=
name|result
condition|)
block|{
name|fail
argument_list|(
name|testName
operator|+
literal|": method did not throw exception but hits.length for search on term 'aaa' is "
operator|+
name|result2
operator|+
literal|" instead of expected "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// On hitting exception we still may have added
comment|// all docs:
if|if
condition|(
name|result2
operator|!=
name|result
condition|)
block|{
name|err
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|": method did throw exception but hits.length for search on term 'aaa' is "
operator|+
name|result2
operator|+
literal|" instead of expected "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"  count is "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|done
operator|||
name|result
operator|==
name|END_COUNT
condition|)
block|{
break|break;
block|}
block|}
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
literal|"  start disk = "
operator|+
name|startDiskUsage
operator|+
literal|"; input disk = "
operator|+
name|inputDiskUsage
operator|+
literal|"; max used = "
operator|+
name|dir
operator|.
name|getMaxUsedSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|done
condition|)
block|{
comment|// Javadocs state that temp free Directory space
comment|// required is at most 2X total input size of
comment|// indices so let's make sure:
name|assertTrue
argument_list|(
literal|"max free Directory space required exceeded 1X the total input index sizes during "
operator|+
name|methodName
operator|+
literal|": max temp usage = "
operator|+
operator|(
name|dir
operator|.
name|getMaxUsedSizeInBytes
argument_list|()
operator|-
name|startDiskUsage
operator|)
operator|+
literal|" bytes; "
operator|+
literal|"starting disk usage = "
operator|+
name|startDiskUsage
operator|+
literal|" bytes; "
operator|+
literal|"input index disk usage = "
operator|+
name|inputDiskUsage
operator|+
literal|" bytes"
argument_list|,
operator|(
name|dir
operator|.
name|getMaxUsedSizeInBytes
argument_list|()
operator|-
name|startDiskUsage
operator|)
operator|<
literal|2
operator|*
operator|(
name|startDiskUsage
operator|+
name|inputDiskUsage
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// Make sure we don't hit disk full during close below:
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Wait for all BG threads to finish else
comment|// dir.close() will throw IOException because
comment|// there are still open files
name|_TestUtil
operator|.
name|syncConcurrentMerges
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Try again with 5000 more bytes of free space:
name|diskFree
operator|+=
literal|5000
expr_stmt|;
block|}
block|}
name|startDir
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Directory
name|dir
range|:
name|dirs
control|)
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|FailTwiceDuringMerge
specifier|private
specifier|static
class|class
name|FailTwiceDuringMerge
extends|extends
name|MockDirectoryWrapper
operator|.
name|Failure
block|{
DECL|field|didFail1
specifier|public
name|boolean
name|didFail1
decl_stmt|;
DECL|field|didFail2
specifier|public
name|boolean
name|didFail2
decl_stmt|;
annotation|@
name|Override
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|doFail
condition|)
block|{
return|return;
block|}
name|StackTraceElement
index|[]
name|trace
init|=
operator|new
name|Exception
argument_list|()
operator|.
name|getStackTrace
argument_list|()
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
name|trace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"org.apache.lucene.index.SegmentMerger"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
argument_list|)
operator|&&
literal|"mergeTerms"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
operator|&&
operator|!
name|didFail1
condition|)
block|{
name|didFail1
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fake disk full during mergeTerms"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"org.apache.lucene.util.BitVector"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
argument_list|)
operator|&&
literal|"write"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
operator|&&
operator|!
name|didFail2
condition|)
block|{
name|didFail2
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fake disk full while writing BitVector"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// LUCENE-2593
DECL|method|testCorruptionAfterDiskFullDuringMerge
specifier|public
name|void
name|testCorruptionAfterDiskFullDuringMerge
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|//IndexWriter w = new IndexWriter(dir, newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer()).setReaderPooling(true));
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
argument_list|()
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setReaderPooling
argument_list|(
literal|true
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
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
literal|"f"
argument_list|,
literal|"doctor who"
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
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
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
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// disk fills up!
name|FailTwiceDuringMerge
name|ftdm
init|=
operator|new
name|FailTwiceDuringMerge
argument_list|()
decl_stmt|;
name|ftdm
operator|.
name|setDoFail
argument_list|()
expr_stmt|;
name|dir
operator|.
name|failOn
argument_list|(
name|ftdm
argument_list|)
expr_stmt|;
try|try
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"fake disk full IOExceptions not hit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// expected
name|assertTrue
argument_list|(
name|ftdm
operator|.
name|didFail1
argument_list|)
expr_stmt|;
block|}
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|ftdm
operator|.
name|clearDoFail
argument_list|()
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
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-1130: make sure immeidate disk full on creating
comment|// an IndexWriter (hit during DW.ThreadState.init()) is
comment|// OK:
DECL|method|testImmediateDiskFull
specifier|public
name|void
name|testImmediateDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
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
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|dir
operator|.
name|getRecomputedActualSizeInBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
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
literal|"aaa bbb ccc ddd eee fff ggg hhh iii jjj"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit disk full"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
comment|// Without fix for LUCENE-1130: this call will hang:
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit disk full"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
try|try
block|{
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit disk full"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
comment|// Make sure once disk space is avail again, we can
comment|// cleanly close:
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// TODO: these are also in TestIndexWriter... add a simple doc-writing method
comment|// like this to LuceneTestCase?
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
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
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
DECL|method|addDocWithIndex
specifier|private
name|void
name|addDocWithIndex
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|IOException
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
literal|"content"
argument_list|,
literal|"aaa "
operator|+
name|index
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
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|index
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
block|}
end_class

end_unit

