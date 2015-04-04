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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|IndexInput
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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ThreadInterruptedException
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

begin_comment
comment|//
end_comment

begin_comment
comment|// This was developed for Lucene In Action,
end_comment

begin_comment
comment|// http://lucenebook.com
end_comment

begin_comment
comment|//
end_comment

begin_class
DECL|class|TestSnapshotDeletionPolicy
specifier|public
class|class
name|TestSnapshotDeletionPolicy
extends|extends
name|LuceneTestCase
block|{
DECL|field|INDEX_PATH
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_PATH
init|=
literal|"test.snapshots"
decl_stmt|;
DECL|method|getConfig
specifier|protected
name|IndexWriterConfig
name|getConfig
parameter_list|(
name|Random
name|random
parameter_list|,
name|IndexDeletionPolicy
name|dp
parameter_list|)
block|{
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dp
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|dp
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
DECL|method|checkSnapshotExists
specifier|protected
name|void
name|checkSnapshotExists
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexCommit
name|c
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|segFileName
init|=
name|c
operator|.
name|getSegmentsFileName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"segments file not found in directory: "
operator|+
name|segFileName
argument_list|,
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|segFileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMaxDoc
specifier|protected
name|void
name|checkMaxDoc
parameter_list|(
name|IndexCommit
name|commit
parameter_list|,
name|int
name|expectedMaxDoc
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|commit
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|expectedMaxDoc
argument_list|,
name|reader
operator|.
name|maxDoc
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
block|}
block|}
DECL|field|snapshots
specifier|protected
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|snapshots
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|prepareIndexAndSnapshots
specifier|protected
name|void
name|prepareIndexAndSnapshots
parameter_list|(
name|SnapshotDeletionPolicy
name|sdp
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|int
name|numSnapshots
parameter_list|)
throws|throws
name|RuntimeException
throws|,
name|IOException
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
name|numSnapshots
condition|;
name|i
operator|++
control|)
block|{
comment|// create dummy document to trigger commit.
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|snapshots
operator|.
name|add
argument_list|(
name|sdp
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDeletionPolicy
specifier|protected
name|SnapshotDeletionPolicy
name|getDeletionPolicy
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertSnapshotExists
specifier|protected
name|void
name|assertSnapshotExists
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SnapshotDeletionPolicy
name|sdp
parameter_list|,
name|int
name|numSnapshots
parameter_list|,
name|boolean
name|checkIndexCommitSame
parameter_list|)
throws|throws
name|Exception
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
name|numSnapshots
condition|;
name|i
operator|++
control|)
block|{
name|IndexCommit
name|snapshot
init|=
name|snapshots
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|checkMaxDoc
argument_list|(
name|snapshot
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|checkSnapshotExists
argument_list|(
name|dir
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkIndexCommitSame
condition|)
block|{
name|assertSame
argument_list|(
name|snapshot
argument_list|,
name|sdp
operator|.
name|getIndexCommit
argument_list|(
name|snapshot
operator|.
name|getGeneration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|snapshot
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|sdp
operator|.
name|getIndexCommit
argument_list|(
name|snapshot
operator|.
name|getGeneration
argument_list|()
argument_list|)
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSnapshotDeletionPolicy
specifier|public
name|void
name|testSnapshotDeletionPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|fsDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|runTest
argument_list|(
name|random
argument_list|()
argument_list|,
name|fsDir
argument_list|)
expr_stmt|;
name|fsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|runTest
specifier|private
name|void
name|runTest
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// we use the IW unref'ed files check which is unaware of retries:
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Run for ~1 seconds
specifier|final
name|long
name|stopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
decl_stmt|;
name|SnapshotDeletionPolicy
name|dp
init|=
name|getDeletionPolicy
argument_list|()
decl_stmt|;
specifier|final
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|dp
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
comment|// Verify we catch misuse:
try|try
block|{
name|dp
operator|.
name|snapshot
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|dp
operator|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Document
name|doc
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
name|setStoreTermVectors
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
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|customType
argument_list|)
argument_list|)
expr_stmt|;
do|do
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
literal|27
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
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
literal|"addDocument failed"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
do|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// While the above indexing thread is running, take many
comment|// backups:
do|do
block|{
name|backupIndex
argument_list|(
name|dir
argument_list|,
name|dp
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
do|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// Add one more document to force writer to commit a
comment|// final segment, so deletion policy has a chance to
comment|// delete again:
name|Document
name|doc
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
name|setStoreTermVectors
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
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|customType
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
comment|// Make sure we don't have any leftover files in the
comment|// directory:
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestIndexWriter
operator|.
name|assertNoUnreferencedFiles
argument_list|(
name|dir
argument_list|,
literal|"some files were not deleted but should have been"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Example showing how to use the SnapshotDeletionPolicy to take a backup.    * This method does not really do a backup; instead, it reads every byte of    * every file just to test that the files indeed exist and are readable even    * while the index is changing.    */
DECL|method|backupIndex
specifier|public
name|void
name|backupIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SnapshotDeletionPolicy
name|dp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// To backup an index we first take a snapshot:
name|IndexCommit
name|snapshot
init|=
name|dp
operator|.
name|snapshot
argument_list|()
decl_stmt|;
try|try
block|{
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Make sure to release the snapshot, otherwise these
comment|// files will never be deleted during this IndexWriter
comment|// session:
name|dp
operator|.
name|release
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copyFiles
specifier|private
name|void
name|copyFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexCommit
name|cp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// While we hold the snapshot, and nomatter how long
comment|// we take to do the backup, the IndexWriter will
comment|// never delete the files in the snapshot:
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
name|cp
operator|.
name|getFileNames
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|files
control|)
block|{
comment|// NOTE: in a real backup you would not use
comment|// readFile; you would need to use something else
comment|// that copies the file to a backup location.  This
comment|// could even be a spawned shell process (eg "tar",
comment|// "zip") that takes the list of files and builds a
comment|// backup.
name|readFile
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|buffer
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
DECL|method|readFile
specifier|private
name|void
name|readFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|size
init|=
name|dir
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|bytesLeft
init|=
name|size
decl_stmt|;
while|while
condition|(
name|bytesLeft
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|numToRead
decl_stmt|;
if|if
condition|(
name|bytesLeft
operator|<
name|buffer
operator|.
name|length
condition|)
name|numToRead
operator|=
operator|(
name|int
operator|)
name|bytesLeft
expr_stmt|;
else|else
name|numToRead
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|numToRead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bytesLeft
operator|-=
name|numToRead
expr_stmt|;
block|}
comment|// Don't do this in your real backups!  This is just
comment|// to force a backup to take a somewhat long time, to
comment|// make sure we are exercising the fact that the
comment|// IndexWriter should not delete this file even when I
comment|// take my time reading it.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBasicSnapshots
specifier|public
name|void
name|testBasicSnapshots
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numSnapshots
init|=
literal|3
decl_stmt|;
comment|// Create 3 snapshots: snapshot0, snapshot1, snapshot2
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// we verify some files get deleted
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|getDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SnapshotDeletionPolicy
name|sdp
init|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
name|prepareIndexAndSnapshots
argument_list|(
name|sdp
argument_list|,
name|writer
argument_list|,
name|numSnapshots
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|numSnapshots
argument_list|,
name|sdp
operator|.
name|getSnapshots
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numSnapshots
argument_list|,
name|sdp
operator|.
name|getSnapshotCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertSnapshotExists
argument_list|(
name|dir
argument_list|,
name|sdp
argument_list|,
name|numSnapshots
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// open a reader on a snapshot - should succeed.
name|DirectoryReader
operator|.
name|open
argument_list|(
name|snapshots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// open a new IndexWriter w/ no snapshots to keep and assert that all snapshots are gone.
name|sdp
operator|=
name|getDeletionPolicy
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|sdp
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no snapshots should exist"
argument_list|,
literal|1
argument_list|,
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiThreadedSnapshotting
specifier|public
name|void
name|testMultiThreadedSnapshotting
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
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// test relies on files actually being deleted
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|getDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SnapshotDeletionPolicy
name|sdp
init|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|10
index|]
decl_stmt|;
specifier|final
name|IndexCommit
index|[]
name|snapshots
init|=
operator|new
name|IndexCommit
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|finalI
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|snapshots
index|[
name|finalI
index|]
operator|=
name|sdp
operator|.
name|snapshot
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"t"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// Do one last commit, so that after we release all snapshots, we stay w/ one commit
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sdp
operator|.
name|release
argument_list|(
name|snapshots
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
operator|.
name|size
argument_list|()
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
block|}
annotation|@
name|Test
DECL|method|testRollbackToOldSnapshot
specifier|public
name|void
name|testRollbackToOldSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numSnapshots
init|=
literal|2
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SnapshotDeletionPolicy
name|sdp
init|=
name|getDeletionPolicy
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
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|sdp
argument_list|)
argument_list|)
decl_stmt|;
name|prepareIndexAndSnapshots
argument_list|(
name|sdp
argument_list|,
name|writer
argument_list|,
name|numSnapshots
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now open the writer on "snapshot0" - make sure it succeeds
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|sdp
argument_list|)
operator|.
name|setIndexCommit
argument_list|(
name|snapshots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// this does the actual rollback
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
name|assertSnapshotExists
argument_list|(
name|dir
argument_list|,
name|sdp
argument_list|,
name|numSnapshots
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// but 'snapshot1' files will still exist (need to release snapshot before they can be deleted).
name|String
name|segFileName
init|=
name|snapshots
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getSegmentsFileName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"snapshot files should exist in the directory: "
operator|+
name|segFileName
argument_list|,
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|segFileName
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReleaseSnapshot
specifier|public
name|void
name|testReleaseSnapshot
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
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// we rely upon existence of files
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|getDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SnapshotDeletionPolicy
name|sdp
init|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
name|prepareIndexAndSnapshots
argument_list|(
name|sdp
argument_list|,
name|writer
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Create another commit - we must do that, because otherwise the "snapshot"
comment|// files will still remain in the index, since it's the last commit.
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Release
name|String
name|segFileName
init|=
name|snapshots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getSegmentsFileName
argument_list|()
decl_stmt|;
name|sdp
operator|.
name|release
argument_list|(
name|snapshots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"segments file should not be found in dirctory: "
operator|+
name|segFileName
argument_list|,
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|segFileName
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnapshotLastCommitTwice
specifier|public
name|void
name|testSnapshotLastCommitTwice
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
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|getDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SnapshotDeletionPolicy
name|sdp
init|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IndexCommit
name|s1
init|=
name|sdp
operator|.
name|snapshot
argument_list|()
decl_stmt|;
name|IndexCommit
name|s2
init|=
name|sdp
operator|.
name|snapshot
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
comment|// should be the same instance
comment|// create another commit
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// release "s1" should not delete "s2"
name|sdp
operator|.
name|release
argument_list|(
name|s1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
name|checkSnapshotExists
argument_list|(
name|dir
argument_list|,
name|s2
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
block|}
annotation|@
name|Test
DECL|method|testMissingCommits
specifier|public
name|void
name|testMissingCommits
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests the behavior of SDP when commits that are given at ctor are missing
comment|// on onInit().
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// we rely upon existence of files
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|getDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SnapshotDeletionPolicy
name|sdp
init|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IndexCommit
name|s1
init|=
name|sdp
operator|.
name|snapshot
argument_list|()
decl_stmt|;
comment|// create another commit, not snapshotted.
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// open a new writer w/ KeepOnlyLastCommit policy, so it will delete "s1"
comment|// commit.
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"snapshotted commit should not exist"
argument_list|,
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|s1
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
argument_list|)
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

