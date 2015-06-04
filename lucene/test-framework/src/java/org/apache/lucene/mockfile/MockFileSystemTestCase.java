begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|DirectoryStream
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|LuceneTestCase
operator|.
name|SuppressFileSystems
import|;
end_import

begin_comment
comment|/**   * Base class for testing mockfilesystems. This tests things  * that really need to work: Path equals()/hashcode(), directory listing  * glob and filtering, URI conversion, etc.  */
end_comment

begin_class
annotation|@
name|SuppressFileSystems
argument_list|(
literal|"*"
argument_list|)
comment|// we suppress random filesystems and do tests explicitly.
DECL|class|MockFileSystemTestCase
specifier|public
specifier|abstract
class|class
name|MockFileSystemTestCase
extends|extends
name|LuceneTestCase
block|{
comment|/** wraps Path with custom behavior */
DECL|method|wrap
specifier|protected
specifier|abstract
name|Path
name|wrap
parameter_list|(
name|Path
name|path
parameter_list|)
function_decl|;
comment|/** Test that Path.hashcode/equals are sane */
DECL|method|testHashCodeEquals
specifier|public
name|void
name|testHashCodeEquals
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|f1
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|Path
name|f1Again
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|Path
name|f2
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"file2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|f1
argument_list|,
name|f1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|f1
argument_list|,
name|f1Again
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|f1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|f1Again
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f1
operator|.
name|equals
argument_list|(
name|f2
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test that URIs are not corrumpted */
DECL|method|testURI
specifier|public
name|void
name|testURI
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeFalse
argument_list|(
literal|"broken on J9: see https://issues.apache.org/jira/browse/LUCENE-6517"
argument_list|,
name|Constants
operator|.
name|JAVA_VENDOR
operator|.
name|startsWith
argument_list|(
literal|"IBM"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|f1
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|URI
name|uri
init|=
name|f1
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|Path
name|f2
init|=
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|provider
argument_list|()
operator|.
name|getPath
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
name|Charset
operator|.
name|defaultCharset
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|" can't encode chinese"
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
operator|.
name|newEncoder
argument_list|()
operator|.
name|canEncode
argument_list|(
literal|"ä¸­å½"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|f3
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"ä¸­å½"
argument_list|)
decl_stmt|;
name|URI
name|uri2
init|=
name|f3
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|Path
name|f4
init|=
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|provider
argument_list|()
operator|.
name|getPath
argument_list|(
name|uri2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|f3
argument_list|,
name|f4
argument_list|)
expr_stmt|;
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests that newDirectoryStream with a filter works correctly */
DECL|method|testDirectoryStreamFiltered
specifier|public
name|void
name|testDirectoryStreamFiltered
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|stream
control|)
block|{
name|assertTrue
argument_list|(
name|path
operator|instanceof
name|FilterPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"extra"
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
block|}
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests that newDirectoryStream with globbing works correctly */
DECL|method|testDirectoryStreamGlobFiltered
specifier|public
name|void
name|testDirectoryStreamGlobFiltered
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|dir
argument_list|,
literal|"f*"
argument_list|)
init|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|stream
control|)
block|{
name|assertTrue
argument_list|(
name|path
operator|instanceof
name|FilterPath
argument_list|)
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

