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
name|NoLockFactory
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

begin_class
DECL|class|TestCrash
specifier|public
class|class
name|TestCrash
extends|extends
name|LuceneTestCase
block|{
DECL|method|initIndex
specifier|private
name|IndexWriter
name|initIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|initialCommit
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|initIndex
argument_list|(
name|random
argument_list|,
name|newMockDirectory
argument_list|(
name|random
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|initialCommit
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|initIndex
specifier|private
name|IndexWriter
name|initIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|MockDirectoryWrapper
name|dir
parameter_list|,
name|boolean
name|initialCommit
parameter_list|,
name|boolean
name|commitOnClose
parameter_list|)
throws|throws
name|IOException
block|{
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
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setCommitOnClose
argument_list|(
name|commitOnClose
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
if|if
condition|(
name|initialCommit
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|newTextField
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
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
literal|157
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
DECL|method|crash
specifier|private
name|void
name|crash
parameter_list|(
specifier|final
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MockDirectoryWrapper
name|dir
init|=
operator|(
name|MockDirectoryWrapper
operator|)
name|writer
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|ConcurrentMergeScheduler
name|cms
init|=
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
name|cms
operator|.
name|sync
argument_list|()
expr_stmt|;
name|dir
operator|.
name|crash
argument_list|()
expr_stmt|;
name|cms
operator|.
name|sync
argument_list|()
expr_stmt|;
name|dir
operator|.
name|clearCrash
argument_list|()
expr_stmt|;
block|}
DECL|method|testCrashWhileIndexing
specifier|public
name|void
name|testCrashWhileIndexing
parameter_list|()
throws|throws
name|IOException
block|{
comment|// This test relies on being able to open a reader before any commit
comment|// happened, so we must create an initial commit just to allow that, but
comment|// before any documents were added.
name|IndexWriter
name|writer
init|=
name|initIndex
argument_list|(
name|random
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
operator|(
name|MockDirectoryWrapper
operator|)
name|writer
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
comment|// We create leftover files because merging could be
comment|// running when we crash:
name|dir
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|crash
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|<
literal|157
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make a new dir, copying from the crashed dir, and
comment|// open IW on it, to confirm IW "recovers" after a
comment|// crash:
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir2
argument_list|)
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
DECL|method|testWriterAfterCrash
specifier|public
name|void
name|testWriterAfterCrash
parameter_list|()
throws|throws
name|IOException
block|{
comment|// This test relies on being able to open a reader before any commit
comment|// happened, so we must create an initial commit just to allow that, but
comment|// before any documents were added.
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
literal|"TEST: initIndex"
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
name|initIndex
argument_list|(
name|random
argument_list|()
argument_list|,
literal|true
argument_list|)
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
literal|"TEST: done initIndex"
argument_list|)
expr_stmt|;
block|}
name|MockDirectoryWrapper
name|dir
init|=
operator|(
name|MockDirectoryWrapper
operator|)
name|writer
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
comment|// We create leftover files because merging could be
comment|// running / store files could be open when we crash:
name|dir
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
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
literal|"TEST: now crash"
argument_list|)
expr_stmt|;
block|}
name|crash
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|=
name|initIndex
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
literal|false
argument_list|,
literal|true
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
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|<
literal|314
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make a new dir, copying from the crashed dir, and
comment|// open IW on it, to confirm IW "recovers" after a
comment|// crash:
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir2
argument_list|)
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
DECL|method|testCrashAfterReopen
specifier|public
name|void
name|testCrashAfterReopen
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
name|initIndex
argument_list|(
name|random
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
operator|(
name|MockDirectoryWrapper
operator|)
name|writer
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
comment|// We create leftover files because merging could be
comment|// running when we crash:
name|dir
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
name|initIndex
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|314
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|crash
argument_list|(
name|writer
argument_list|)
expr_stmt|;
comment|/*     System.out.println("\n\nTEST: open reader");     String[] l = dir.list();     Arrays.sort(l);     for(int i=0;i<l.length;i++)       System.out.println("file " + i + " = " + l[i] + " " +     dir.fileLength(l[i]) + " bytes");     */
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|>=
literal|157
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make a new dir, copying from the crashed dir, and
comment|// open IW on it, to confirm IW "recovers" after a
comment|// crash:
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir2
argument_list|)
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
DECL|method|testCrashAfterClose
specifier|public
name|void
name|testCrashAfterClose
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
name|initIndex
argument_list|(
name|random
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
operator|(
name|MockDirectoryWrapper
operator|)
name|writer
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|crash
argument_list|()
expr_stmt|;
comment|/*     String[] l = dir.list();     Arrays.sort(l);     for(int i=0;i<l.length;i++)       System.out.println("file " + i + " = " + l[i] + " " + dir.fileLength(l[i]) + " bytes");     */
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|157
argument_list|,
name|reader
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCrashAfterCloseNoWait
specifier|public
name|void
name|testCrashAfterCloseNoWait
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
argument_list|(
name|random
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
name|initIndex
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|crash
argument_list|()
expr_stmt|;
comment|/*     String[] l = dir.list();     Arrays.sort(l);     for(int i=0;i<l.length;i++)       System.out.println("file " + i + " = " + l[i] + " " + dir.fileLength(l[i]) + " bytes");     */
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|157
argument_list|,
name|reader
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

