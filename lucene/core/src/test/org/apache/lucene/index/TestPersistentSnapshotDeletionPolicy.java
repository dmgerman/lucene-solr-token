begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
DECL|class|TestPersistentSnapshotDeletionPolicy
specifier|public
class|class
name|TestPersistentSnapshotDeletionPolicy
extends|extends
name|TestSnapshotDeletionPolicy
block|{
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|getDeletionPolicy
specifier|private
name|SnapshotDeletionPolicy
name|getDeletionPolicy
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testExistingSnapshots
specifier|public
name|void
name|testExistingSnapshots
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numSnapshots
init|=
literal|3
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
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
argument_list|(
name|dir
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|PersistentSnapshotDeletionPolicy
name|psdp
init|=
operator|(
name|PersistentSnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|psdp
operator|.
name|getLastSaveFile
argument_list|()
argument_list|)
expr_stmt|;
name|prepareIndexAndSnapshots
argument_list|(
name|psdp
argument_list|,
name|writer
argument_list|,
name|numSnapshots
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|psdp
operator|.
name|getLastSaveFile
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make sure only 1 save file exists:
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|PersistentSnapshotDeletionPolicy
operator|.
name|SNAPSHOTS_PREFIX
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
argument_list|)
expr_stmt|;
comment|// Make sure we fsync:
name|dir
operator|.
name|crash
argument_list|()
expr_stmt|;
name|dir
operator|.
name|clearCrash
argument_list|()
expr_stmt|;
comment|// Re-initialize and verify snapshots were persisted
name|psdp
operator|=
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|)
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
name|psdp
argument_list|)
argument_list|)
expr_stmt|;
name|psdp
operator|=
operator|(
name|PersistentSnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|numSnapshots
argument_list|,
name|psdp
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
name|psdp
operator|.
name|getSnapshotCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertSnapshotExists
argument_list|(
name|dir
argument_list|,
name|psdp
argument_list|,
name|numSnapshots
argument_list|,
literal|false
argument_list|)
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
operator|.
name|add
argument_list|(
name|psdp
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numSnapshots
operator|+
literal|1
argument_list|,
name|psdp
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
operator|+
literal|1
argument_list|,
name|psdp
operator|.
name|getSnapshotCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertSnapshotExists
argument_list|(
name|dir
argument_list|,
name|psdp
argument_list|,
name|numSnapshots
operator|+
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoSnapshotInfos
specifier|public
name|void
name|testNoSnapshotInfos
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
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE
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
DECL|method|testMissingSnapshots
specifier|public
name|void
name|testMissingSnapshots
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
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testExceptionDuringSave
specifier|public
name|void
name|testExceptionDuringSave
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|failOn
argument_list|(
operator|new
name|MockDirectoryWrapper
operator|.
name|Failure
argument_list|()
block|{
annotation|@
name|Override
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
name|StackTraceElement
index|[]
name|trace
init|=
name|Thread
operator|.
name|currentThread
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
name|PersistentSnapshotDeletionPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
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
literal|"persist"
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
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"now fail on purpose"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
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
name|getConfig
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|)
argument_list|)
argument_list|)
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
name|PersistentSnapshotDeletionPolicy
name|psdp
init|=
operator|(
name|PersistentSnapshotDeletionPolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
try|try
block|{
name|psdp
operator|.
name|snapshot
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"now fail on purpose"
argument_list|)
condition|)
block|{
comment|// ok
block|}
else|else
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|psdp
operator|.
name|getSnapshotCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnapshotRelease
specifier|public
name|void
name|testSnapshotRelease
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
argument_list|(
name|dir
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|PersistentSnapshotDeletionPolicy
name|psdp
init|=
operator|(
name|PersistentSnapshotDeletionPolicy
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
name|psdp
argument_list|,
name|writer
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|psdp
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
name|psdp
operator|=
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have no snapshots !"
argument_list|,
literal|0
argument_list|,
name|psdp
operator|.
name|getSnapshotCount
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
DECL|method|testSnapshotReleaseByGeneration
specifier|public
name|void
name|testSnapshotReleaseByGeneration
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
argument_list|(
name|dir
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|PersistentSnapshotDeletionPolicy
name|psdp
init|=
operator|(
name|PersistentSnapshotDeletionPolicy
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
name|psdp
argument_list|,
name|writer
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|psdp
operator|.
name|release
argument_list|(
name|snapshots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|psdp
operator|=
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have no snapshots !"
argument_list|,
literal|0
argument_list|,
name|psdp
operator|.
name|getSnapshotCount
argument_list|()
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

