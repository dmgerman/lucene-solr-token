begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_comment
comment|/**  * Test to illustrate the problem found when trying to open an IndexWriter in  * a situation where the the property<code>org.apache.lucene.lockDir</code>  * was not set and the one specified by<code>java.io.tmpdir</code> had been  * set to a non-existent path. What I observed is that this combination of  * conditions resulted in a<code>NullPointerException</code> being thrown in  * the<code>create()</code> method in<code>FSDirectory</code>, where  *<code>files.length</code> is de-referenced, but<code>files</code> is  *</code>null</code>.  *  * @author Michael Goddard  */
end_comment

begin_class
DECL|class|FSDirectoryTest
specifier|public
class|class
name|FSDirectoryTest
extends|extends
name|TestCase
block|{
comment|/**      * What happens if the Lucene lockDir doesn't exist?      *      * @throws Exception      */
DECL|method|testNonExistentTmpDir
specifier|public
name|void
name|testNonExistentTmpDir
parameter_list|()
throws|throws
name|Exception
block|{
name|orgApacheLuceneLockDir
operator|=
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.lucene.lockDir"
argument_list|,
name|NON_EXISTENT_DIRECTORY
argument_list|)
expr_stmt|;
name|String
name|exceptionClassName
init|=
name|openIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|exceptionClassName
operator|==
literal|null
operator|||
name|exceptionClassName
operator|.
name|equals
argument_list|(
literal|"java.io.IOException"
argument_list|)
condition|)
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
name|fail
argument_list|(
literal|"Caught an unexpected Exception"
argument_list|)
expr_stmt|;
block|}
comment|/**      * What happens if the Lucene lockDir is a regular file instead of a      * directory?      *      * @throws Exception      */
DECL|method|testTmpDirIsPlainFile
specifier|public
name|void
name|testTmpDirIsPlainFile
parameter_list|()
throws|throws
name|Exception
block|{
name|shouldBeADirectory
operator|=
operator|new
name|File
argument_list|(
name|NON_EXISTENT_DIRECTORY
argument_list|)
expr_stmt|;
name|shouldBeADirectory
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|String
name|exceptionClassName
init|=
name|openIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|exceptionClassName
operator|==
literal|null
operator|||
name|exceptionClassName
operator|.
name|equals
argument_list|(
literal|"java.io.IOException"
argument_list|)
condition|)
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
name|fail
argument_list|(
literal|"Caught an unexpected Exception"
argument_list|)
expr_stmt|;
block|}
DECL|field|FILE_SEP
specifier|public
specifier|static
specifier|final
name|String
name|FILE_SEP
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
decl_stmt|;
DECL|field|NON_EXISTENT_DIRECTORY
specifier|public
specifier|static
specifier|final
name|String
name|NON_EXISTENT_DIRECTORY
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
name|FILE_SEP
operator|+
literal|"highly_improbable_directory_name"
decl_stmt|;
DECL|field|TEST_INDEX_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TEST_INDEX_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
name|FILE_SEP
operator|+
literal|"temp_index"
decl_stmt|;
DECL|field|orgApacheLuceneLockDir
specifier|private
name|String
name|orgApacheLuceneLockDir
decl_stmt|;
DECL|field|shouldBeADirectory
specifier|private
name|File
name|shouldBeADirectory
decl_stmt|;
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|orgApacheLuceneLockDir
operator|!=
literal|null
condition|)
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.lucene.lockDir"
argument_list|,
name|orgApacheLuceneLockDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldBeADirectory
operator|!=
literal|null
operator|&&
name|shouldBeADirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|shouldBeADirectory
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|File
name|deletableIndex
init|=
operator|new
name|File
argument_list|(
name|TEST_INDEX_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletableIndex
operator|.
name|exists
argument_list|()
condition|)
try|try
block|{
name|rmDir
argument_list|(
name|deletableIndex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Open an IndexWriter<br>      * Catch any (expected) IOException<br>      * Close the IndexWriter      */
DECL|method|openIndexWriter
specifier|private
specifier|static
name|String
name|openIndexWriter
parameter_list|()
block|{
name|IndexWriter
name|iw
init|=
literal|null
decl_stmt|;
name|String
name|ret
init|=
literal|null
decl_stmt|;
try|try
block|{
name|iw
operator|=
operator|new
name|IndexWriter
argument_list|(
name|TEST_INDEX_DIR
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
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
name|ret
operator|=
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|ret
operator|=
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|iw
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ignore this
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|rmDir
specifier|private
specifier|static
name|void
name|rmDir
parameter_list|(
name|File
name|dirName
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|dirName
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|dirName
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|contents
init|=
name|dirName
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
name|contents
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|rmDir
argument_list|(
name|contents
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|dirName
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dirName
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

