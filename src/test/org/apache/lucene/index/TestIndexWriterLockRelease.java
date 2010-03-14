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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|FSDirectory
import|;
end_import

begin_comment
comment|/**  * This tests the patch for issue #LUCENE-715 (IndexWriter does not  * release its write lock when trying to open an index which does not yet  * exist).  */
end_comment

begin_class
DECL|class|TestIndexWriterLockRelease
specifier|public
class|class
name|TestIndexWriterLockRelease
extends|extends
name|LuceneTestCase
block|{
DECL|field|__test_dir
specifier|private
name|java
operator|.
name|io
operator|.
name|File
name|__test_dir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
if|if
condition|(
name|this
operator|.
name|__test_dir
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|__test_dir
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"testIndexWriter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|__test_dir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"test directory \""
operator|+
name|this
operator|.
name|__test_dir
operator|.
name|getPath
argument_list|()
operator|+
literal|"\" already exists (please remove by hand)"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|__test_dir
operator|.
name|mkdirs
argument_list|()
operator|&&
operator|!
name|this
operator|.
name|__test_dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to create test directory \""
operator|+
name|this
operator|.
name|__test_dir
operator|.
name|getPath
argument_list|()
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|__test_dir
operator|!=
literal|null
condition|)
block|{
name|File
index|[]
name|files
init|=
name|this
operator|.
name|__test_dir
operator|.
name|listFiles
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
operator|++
name|i
control|)
block|{
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to remove file in test directory \""
operator|+
name|this
operator|.
name|__test_dir
operator|.
name|getPath
argument_list|()
operator|+
literal|"\" (please remove by hand)"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|__test_dir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to remove test directory \""
operator|+
name|this
operator|.
name|__test_dir
operator|.
name|getPath
argument_list|()
operator|+
literal|"\" (please remove by hand)"
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testIndexWriterLockRelease
specifier|public
name|void
name|testIndexWriterLockRelease
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDirectory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|this
operator|.
name|__test_dir
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
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
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
try|try
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
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
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e1
parameter_list|)
block|{             }
block|}
finally|finally
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

