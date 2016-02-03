begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|TestDirectory
specifier|public
class|class
name|TestDirectory
extends|extends
name|LuceneTestCase
block|{
comment|// Test that different instances of FSDirectory can coexist on the same
comment|// path, can read, write, and lock files.
DECL|method|testDirectInstantiation
specifier|public
name|void
name|testDirectInstantiation
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|path
init|=
name|createTempDir
argument_list|(
literal|"testDirectInstantiation"
argument_list|)
decl_stmt|;
name|assumeFalse
argument_list|(
literal|"test deletes files through different FSDir instances"
argument_list|,
name|TestUtil
operator|.
name|hasVirusChecker
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|largeBuffer
init|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
operator|*
literal|1024
argument_list|)
index|]
decl_stmt|,
name|largeReadBuffer
init|=
operator|new
name|byte
index|[
name|largeBuffer
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
name|largeBuffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|largeBuffer
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
comment|// automatically loops with modulo
block|}
specifier|final
name|FSDirectory
index|[]
name|dirs
init|=
operator|new
name|FSDirectory
index|[]
block|{
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|)
block|,
operator|new
name|NIOFSDirectory
argument_list|(
name|path
argument_list|)
block|,
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|)
block|}
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
name|FSDirectory
name|dir
init|=
name|dirs
index|[
name|i
index|]
decl_stmt|;
name|dir
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
name|fname
init|=
literal|"foo."
operator|+
name|i
decl_stmt|;
name|String
name|lockname
init|=
literal|"foo"
operator|+
name|i
operator|+
literal|".lck"
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fname
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|largeBuffer
argument_list|,
name|largeBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dirs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|FSDirectory
name|d2
init|=
name|dirs
index|[
name|j
index|]
decl_stmt|;
name|d2
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|d2
argument_list|,
name|fname
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
operator|+
name|largeBuffer
operator|.
name|length
argument_list|,
name|d2
operator|.
name|fileLength
argument_list|(
name|fname
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't do read tests if unmapping is not supported!
if|if
condition|(
name|d2
operator|instanceof
name|MMapDirectory
operator|&&
operator|!
operator|(
operator|(
name|MMapDirectory
operator|)
name|d2
operator|)
operator|.
name|getUseUnmap
argument_list|()
condition|)
continue|continue;
name|IndexInput
name|input
init|=
name|d2
operator|.
name|openInput
argument_list|(
name|fname
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|input
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
comment|// read array with buffering enabled
name|Arrays
operator|.
name|fill
argument_list|(
name|largeReadBuffer
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|largeReadBuffer
argument_list|,
literal|0
argument_list|,
name|largeReadBuffer
operator|.
name|length
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|largeBuffer
argument_list|,
name|largeReadBuffer
argument_list|)
expr_stmt|;
comment|// read again without using buffer
name|input
operator|.
name|seek
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|largeReadBuffer
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|largeReadBuffer
argument_list|,
literal|0
argument_list|,
name|largeReadBuffer
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|largeBuffer
argument_list|,
name|largeReadBuffer
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// delete with a different dir
name|dirs
index|[
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
name|dirs
operator|.
name|length
index|]
operator|.
name|deleteFile
argument_list|(
name|fname
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dirs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|FSDirectory
name|d2
init|=
name|dirs
index|[
name|j
index|]
decl_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|d2
argument_list|,
name|fname
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Lock
name|lock
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
name|lockname
argument_list|)
decl_stmt|;
for|for
control|(
name|Directory
name|other
range|:
name|dirs
control|)
block|{
try|try
block|{
name|other
operator|.
name|obtainLock
argument_list|(
name|lockname
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didnt get exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|e
parameter_list|)
block|{
comment|// OK
block|}
block|}
name|lock
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now lock with different dir
name|lock
operator|=
name|dirs
index|[
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
name|dirs
operator|.
name|length
index|]
operator|.
name|obtainLock
argument_list|(
name|lockname
argument_list|)
expr_stmt|;
name|lock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|FSDirectory
name|dir
init|=
name|dirs
index|[
name|i
index|]
decl_stmt|;
name|dir
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|dir
operator|.
name|isOpen
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-1468
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
DECL|method|testCopySubdir
specifier|public
name|void
name|testCopySubdir
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|(
literal|"testsubdir"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"subdir"
argument_list|)
argument_list|)
expr_stmt|;
name|FSDirectory
name|fsDir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|RAMDirectory
name|ramDir
init|=
operator|new
name|RAMDirectory
argument_list|(
name|fsDir
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|ramDir
operator|.
name|listAll
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|files
operator|.
name|contains
argument_list|(
literal|"subdir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1468
DECL|method|testNotDirectory
specifier|public
name|void
name|testNotDirectory
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|(
literal|"testnotdir"
argument_list|)
decl_stmt|;
name|Directory
name|fsDir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexOutput
name|out
init|=
name|fsDir
operator|.
name|createOutput
argument_list|(
literal|"afile"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|fsDir
argument_list|,
literal|"afile"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"afile"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|nsde
parameter_list|)
block|{
comment|// Expected
block|}
block|}
finally|finally
block|{
name|fsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

