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
name|File
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
name|store
operator|.
name|Directory
operator|.
name|IndexInputSlicer
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
name|BytesRef
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
comment|/**  * Tests MMapDirectory's MultiMMapIndexInput  *<p>  * Because Java's ByteBuffer uses an int to address the  * values, it's necessary to access a file>  * Integer.MAX_VALUE in size using multiple byte buffers.  */
end_comment

begin_class
DECL|class|TestMultiMMap
specifier|public
class|class
name|TestMultiMMap
extends|extends
name|LuceneTestCase
block|{
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
name|assumeTrue
argument_list|(
literal|"test requires a jre that supports unmapping"
argument_list|,
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
argument_list|)
expr_stmt|;
block|}
DECL|method|testCloneSafety
specifier|public
name|void
name|testCloneSafety
parameter_list|()
throws|throws
name|Exception
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCloneSafety"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|io
operator|.
name|writeVInt
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|one
init|=
name|mmapDir
operator|.
name|openInput
argument_list|(
literal|"bytes"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|IndexInput
name|two
init|=
name|one
operator|.
name|clone
argument_list|()
decl_stmt|;
name|IndexInput
name|three
init|=
name|two
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// clone of clone
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|one
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
try|try
block|{
name|two
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
try|try
block|{
name|three
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
name|two
operator|.
name|close
argument_list|()
expr_stmt|;
name|three
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test double close of master:
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCloneClose
specifier|public
name|void
name|testCloneClose
parameter_list|()
throws|throws
name|Exception
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCloneClose"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|io
operator|.
name|writeVInt
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|one
init|=
name|mmapDir
operator|.
name|openInput
argument_list|(
literal|"bytes"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|IndexInput
name|two
init|=
name|one
operator|.
name|clone
argument_list|()
decl_stmt|;
name|IndexInput
name|three
init|=
name|two
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// clone of clone
name|two
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|one
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|two
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|three
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
name|three
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCloneSliceSafety
specifier|public
name|void
name|testCloneSliceSafety
parameter_list|()
throws|throws
name|Exception
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCloneSliceSafety"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|io
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|io
operator|.
name|writeInt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInputSlicer
name|slicer
init|=
name|mmapDir
operator|.
name|createSlicer
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexInput
name|one
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"first int"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|IndexInput
name|two
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"second int"
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|IndexInput
name|three
init|=
name|one
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// clone of clone
name|IndexInput
name|four
init|=
name|two
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// clone of clone
name|slicer
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|one
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
try|try
block|{
name|two
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
try|try
block|{
name|three
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
try|try
block|{
name|four
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
name|two
operator|.
name|close
argument_list|()
expr_stmt|;
name|three
operator|.
name|close
argument_list|()
expr_stmt|;
name|four
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test double-close of slicer:
name|slicer
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCloneSliceClose
specifier|public
name|void
name|testCloneSliceClose
parameter_list|()
throws|throws
name|Exception
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCloneSliceClose"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|io
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|io
operator|.
name|writeInt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInputSlicer
name|slicer
init|=
name|mmapDir
operator|.
name|createSlicer
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexInput
name|one
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"first int"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|IndexInput
name|two
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"second int"
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|one
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw AlreadyClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ignore
parameter_list|)
block|{
comment|// pass
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|two
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
comment|// reopen a new slice "one":
name|one
operator|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"first int"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|one
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
name|two
operator|.
name|close
argument_list|()
expr_stmt|;
name|slicer
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSeekZero
specifier|public
name|void
name|testSeekZero
parameter_list|()
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
literal|31
condition|;
name|i
operator|++
control|)
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSeekZero"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
operator|<<
name|i
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"zeroBytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|ii
init|=
name|mmapDir
operator|.
name|openInput
argument_list|(
literal|"zeroBytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ii
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSeekSliceZero
specifier|public
name|void
name|testSeekSliceZero
parameter_list|()
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
literal|31
condition|;
name|i
operator|++
control|)
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSeekSliceZero"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
operator|<<
name|i
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"zeroBytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInputSlicer
name|slicer
init|=
name|mmapDir
operator|.
name|createSlicer
argument_list|(
literal|"zeroBytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexInput
name|ii
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"zero-length slice"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ii
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
name|slicer
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSeekEnd
specifier|public
name|void
name|testSeekEnd
parameter_list|()
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
literal|17
condition|;
name|i
operator|++
control|)
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSeekEnd"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
operator|<<
name|i
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
name|i
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|io
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|ii
init|=
name|mmapDir
operator|.
name|openInput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|actual
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
name|i
index|]
decl_stmt|;
name|ii
operator|.
name|readBytes
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
name|ii
operator|.
name|seek
argument_list|(
literal|1
operator|<<
name|i
argument_list|)
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSeekSliceEnd
specifier|public
name|void
name|testSeekSliceEnd
parameter_list|()
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
literal|17
condition|;
name|i
operator|++
control|)
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSeekSliceEnd"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
operator|<<
name|i
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
name|i
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|io
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInputSlicer
name|slicer
init|=
name|mmapDir
operator|.
name|createSlicer
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexInput
name|ii
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"full slice"
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
name|actual
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
name|i
index|]
decl_stmt|;
name|ii
operator|.
name|readBytes
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
name|ii
operator|.
name|seek
argument_list|(
literal|1
operator|<<
name|i
argument_list|)
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
name|slicer
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSeeking
specifier|public
name|void
name|testSeeking
parameter_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSeeking"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
operator|<<
name|i
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
operator|(
name|i
operator|+
literal|1
operator|)
index|]
decl_stmt|;
comment|// make sure we switch buffers
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|io
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|ii
init|=
name|mmapDir
operator|.
name|openInput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|actual
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
operator|(
name|i
operator|+
literal|1
operator|)
index|]
decl_stmt|;
comment|// first read all bytes
name|ii
operator|.
name|readBytes
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|sliceStart
init|=
literal|0
init|;
name|sliceStart
operator|<
name|bytes
operator|.
name|length
condition|;
name|sliceStart
operator|++
control|)
block|{
for|for
control|(
name|int
name|sliceLength
init|=
literal|0
init|;
name|sliceLength
operator|<
name|bytes
operator|.
name|length
operator|-
name|sliceStart
condition|;
name|sliceLength
operator|++
control|)
block|{
name|byte
name|slice
index|[]
init|=
operator|new
name|byte
index|[
name|sliceLength
index|]
decl_stmt|;
name|ii
operator|.
name|seek
argument_list|(
name|sliceStart
argument_list|)
expr_stmt|;
name|ii
operator|.
name|readBytes
argument_list|(
name|slice
argument_list|,
literal|0
argument_list|,
name|slice
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
name|sliceStart
argument_list|,
name|sliceLength
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|slice
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// note instead of seeking to offset and reading length, this opens slices at the
comment|// the various offset+length and just does readBytes.
DECL|method|testSlicedSeeking
specifier|public
name|void
name|testSlicedSeeking
parameter_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSlicedSeeking"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
operator|<<
name|i
argument_list|)
decl_stmt|;
name|IndexOutput
name|io
init|=
name|mmapDir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
operator|(
name|i
operator|+
literal|1
operator|)
index|]
decl_stmt|;
comment|// make sure we switch buffers
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|io
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|ii
init|=
name|mmapDir
operator|.
name|openInput
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|actual
index|[]
init|=
operator|new
name|byte
index|[
literal|1
operator|<<
operator|(
name|i
operator|+
literal|1
operator|)
index|]
decl_stmt|;
comment|// first read all bytes
name|ii
operator|.
name|readBytes
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
name|IndexInputSlicer
name|slicer
init|=
name|mmapDir
operator|.
name|createSlicer
argument_list|(
literal|"bytes"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|sliceStart
init|=
literal|0
init|;
name|sliceStart
operator|<
name|bytes
operator|.
name|length
condition|;
name|sliceStart
operator|++
control|)
block|{
for|for
control|(
name|int
name|sliceLength
init|=
literal|0
init|;
name|sliceLength
operator|<
name|bytes
operator|.
name|length
operator|-
name|sliceStart
condition|;
name|sliceLength
operator|++
control|)
block|{
name|byte
name|slice
index|[]
init|=
operator|new
name|byte
index|[
name|sliceLength
index|]
decl_stmt|;
name|IndexInput
name|input
init|=
name|slicer
operator|.
name|openSlice
argument_list|(
literal|"bytesSlice"
argument_list|,
name|sliceStart
argument_list|,
name|slice
operator|.
name|length
argument_list|)
decl_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|slice
argument_list|,
literal|0
argument_list|,
name|slice
operator|.
name|length
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
name|sliceStart
argument_list|,
name|sliceLength
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|slice
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|slicer
operator|.
name|close
argument_list|()
expr_stmt|;
name|mmapDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testRandomChunkSizes
specifier|public
name|void
name|testRandomChunkSizes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10
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
name|num
condition|;
name|i
operator|++
control|)
name|assertChunking
argument_list|(
name|random
argument_list|()
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertChunking
specifier|private
name|void
name|assertChunking
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|chunkSize
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|path
init|=
name|createTempDir
argument_list|(
literal|"mmap"
operator|+
name|chunkSize
argument_list|)
decl_stmt|;
name|MMapDirectory
name|mmapDir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|chunkSize
argument_list|)
decl_stmt|;
comment|// we will map a lot, try to turn on the unmap hack
if|if
condition|(
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
name|mmapDir
operator|.
name|setUseUnmap
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|,
name|mmapDir
argument_list|)
decl_stmt|;
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
name|Field
name|docid
init|=
name|newStringField
argument_list|(
literal|"docid"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|junk
init|=
name|newStringField
argument_list|(
literal|"junk"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|docid
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|junk
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
literal|100
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|docid
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|junk
operator|.
name|setStringValue
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|int
name|numAsserts
init|=
name|atLeast
argument_list|(
literal|100
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
name|numAsserts
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docID
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|docID
argument_list|,
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|)
operator|.
name|get
argument_list|(
literal|"docid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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

