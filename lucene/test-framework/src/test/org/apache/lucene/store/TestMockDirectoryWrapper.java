begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Path
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
name|IndexWriter
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
name|util
operator|.
name|IOUtils
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
operator|.
name|Nightly
import|;
end_import

begin_class
DECL|class|TestMockDirectoryWrapper
specifier|public
class|class
name|TestMockDirectoryWrapper
extends|extends
name|BaseDirectoryTestCase
block|{
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MockDirectoryWrapper
name|dir
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|dir
operator|=
name|newMockDirectory
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
name|newMockFSDirectory
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
comment|// we wrap the directory in slow stuff, so only run nightly
annotation|@
name|Override
annotation|@
name|Nightly
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testThreadSafety
argument_list|()
expr_stmt|;
block|}
DECL|method|testDiskFull
specifier|public
name|void
name|testDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test writeBytes
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|3
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|}
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|out
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
comment|// first write should succeed
comment|// close() to ensure the written bytes are not buffered and counted
comment|// against the directory size
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"bar"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
try|try
block|{
name|out
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
name|fail
argument_list|(
literal|"should have failed on disk full"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test copyBytes
name|dir
operator|=
name|newMockDirectory
argument_list|()
expr_stmt|;
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|out
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
operator|new
name|ByteArrayDataInput
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// first copy should succeed
comment|// close() to ensure the written bytes are not buffered and counted
comment|// against the directory size
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"bar"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
try|try
block|{
name|out
operator|.
name|copyBytes
argument_list|(
operator|new
name|ByteArrayDataInput
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed on disk full"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|out
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
DECL|method|testMDWinsideOfMDW
specifier|public
name|void
name|testMDWinsideOfMDW
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add MDW inside another MDW
name|Directory
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
name|newMockDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|iw
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
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

