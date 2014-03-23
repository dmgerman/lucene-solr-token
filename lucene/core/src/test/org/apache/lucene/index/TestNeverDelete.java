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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|TestUtil
import|;
end_import

begin_comment
comment|// Make sure if you use NoDeletionPolicy that no file
end_comment

begin_comment
comment|// referenced by a commit point is ever deleted
end_comment

begin_class
DECL|class|TestNeverDelete
specifier|public
class|class
name|TestNeverDelete
extends|extends
name|LuceneTestCase
block|{
DECL|method|testIndexing
specifier|public
name|void
name|testIndexing
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|tmpDir
init|=
name|TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestNeverDelete"
argument_list|)
decl_stmt|;
specifier|final
name|BaseDirectoryWrapper
name|d
init|=
name|newFSDirectory
argument_list|(
name|tmpDir
argument_list|)
decl_stmt|;
comment|// We want to "see" files removed if Lucene removed
comment|// them.  This is still worth running on Windows since
comment|// some files the IR opens and closes.
if|if
condition|(
name|d
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|d
operator|)
operator|.
name|setNoDeleteOpenFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|d
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
argument_list|)
argument_list|)
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|NoDeletionPolicy
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Thread
index|[]
name|indexThreads
init|=
operator|new
name|Thread
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
decl_stmt|;
specifier|final
name|long
name|stopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|atLeast
argument_list|(
literal|1000
argument_list|)
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
name|indexThreads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|indexThreads
index|[
name|x
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
name|int
name|docCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
block|{
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
name|newStringField
argument_list|(
literal|"dc"
argument_list|,
literal|""
operator|+
name|docCount
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"here is some text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
if|if
condition|(
name|docCount
operator|%
literal|13
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|docCount
operator|++
expr_stmt|;
block|}
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
name|indexThreads
index|[
name|x
index|]
operator|.
name|setName
argument_list|(
literal|"Thread "
operator|+
name|x
argument_list|)
expr_stmt|;
name|indexThreads
index|[
name|x
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allFiles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
block|{
specifier|final
name|IndexCommit
name|ic
init|=
name|r
operator|.
name|getIndexCommit
argument_list|()
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
literal|"TEST: check files: "
operator|+
name|ic
operator|.
name|getFileNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|allFiles
operator|.
name|addAll
argument_list|(
name|ic
operator|.
name|getFileNames
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure no old files were removed
for|for
control|(
name|String
name|fileName
range|:
name|allFiles
control|)
block|{
name|assertTrue
argument_list|(
literal|"file "
operator|+
name|fileName
operator|+
literal|" does not exist"
argument_list|,
name|slowFileExists
argument_list|(
name|d
argument_list|,
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|r2
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|r2
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|indexThreads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestUtil
operator|.
name|rmDir
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

