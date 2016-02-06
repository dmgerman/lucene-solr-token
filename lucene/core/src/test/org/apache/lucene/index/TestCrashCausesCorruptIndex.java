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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|TopDocs
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
name|FSDirectory
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
name|FilterDirectory
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
name|IOContext
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
name|IndexOutput
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
DECL|class|TestCrashCausesCorruptIndex
specifier|public
class|class
name|TestCrashCausesCorruptIndex
extends|extends
name|LuceneTestCase
block|{
DECL|field|path
name|Path
name|path
decl_stmt|;
comment|/**    * LUCENE-3627: This test fails.    */
DECL|method|testCrashCorruptsIndexing
specifier|public
name|void
name|testCrashCorruptsIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|path
operator|=
name|createTempDir
argument_list|(
literal|"testCrashCorruptsIndexing"
argument_list|)
expr_stmt|;
name|indexAndCrashOnCreateOutputSegments2
argument_list|()
expr_stmt|;
name|searchForFleas
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|indexAfterRestart
argument_list|()
expr_stmt|;
name|searchForFleas
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
comment|/**    * index 1 document and commit.    * prepare for crashing.    * index 1 more document, and upon commit, creation of segments_2 will crash.    */
DECL|method|indexAndCrashOnCreateOutputSegments2
specifier|private
name|void
name|indexAndCrashOnCreateOutputSegments2
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|realDirectory
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|CrashAfterCreateOutput
name|crashAfterCreateOutput
init|=
operator|new
name|CrashAfterCreateOutput
argument_list|(
name|realDirectory
argument_list|)
decl_stmt|;
comment|// NOTE: cannot use RandomIndexWriter because it
comment|// sometimes commits:
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|crashAfterCreateOutput
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
comment|// writes segments_1:
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|crashAfterCreateOutput
operator|.
name|setCrashAfterCreateOutput
argument_list|(
literal|"pending_segments_2"
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// tries to write segments_2 but hits fake exc:
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit CrashingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CrashingException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// writes segments_3
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|realDirectory
argument_list|,
literal|"segments_2"
argument_list|)
argument_list|)
expr_stmt|;
name|crashAfterCreateOutput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Attempts to index another 1 document.    */
DECL|method|indexAfterRestart
specifier|private
name|void
name|indexAfterRestart
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|realDirectory
init|=
name|newFSDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// LUCENE-3627 (before the fix): this line fails because
comment|// it doesn't know what to do with the created but empty
comment|// segments_2 file
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|realDirectory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// currently the test fails above.
comment|// however, to test the fix, the following lines should pass as well.
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|realDirectory
argument_list|,
literal|"segments_2"
argument_list|)
argument_list|)
expr_stmt|;
name|realDirectory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Run an example search.    */
DECL|method|searchForFleas
specifier|private
name|void
name|searchForFleas
parameter_list|(
specifier|final
name|int
name|expectedTotalHits
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|realDirectory
init|=
name|newFSDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|realDirectory
argument_list|)
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|TEXT_FIELD
argument_list|,
literal|"fleas"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|topDocs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedTotalHits
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|realDirectory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|TEXT_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|TEXT_FIELD
init|=
literal|"text"
decl_stmt|;
comment|/**    * Gets a document with content "my dog has fleas".    */
DECL|method|getDocument
specifier|private
name|Document
name|getDocument
parameter_list|()
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|TEXT_FIELD
argument_list|,
literal|"my dog has fleas"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
comment|/**    * The marker RuntimeException that we use in lieu of an    * actual machine crash.    */
DECL|class|CrashingException
specifier|private
specifier|static
class|class
name|CrashingException
extends|extends
name|RuntimeException
block|{
DECL|method|CrashingException
specifier|public
name|CrashingException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test class provides direct access to "simulating" a crash right after     * realDirectory.createOutput(..) has been called on a certain specified name.    */
DECL|class|CrashAfterCreateOutput
specifier|private
specifier|static
class|class
name|CrashAfterCreateOutput
extends|extends
name|FilterDirectory
block|{
DECL|field|crashAfterCreateOutput
specifier|private
name|String
name|crashAfterCreateOutput
decl_stmt|;
DECL|method|CrashAfterCreateOutput
specifier|public
name|CrashAfterCreateOutput
parameter_list|(
name|Directory
name|realDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|realDirectory
argument_list|)
expr_stmt|;
block|}
DECL|method|setCrashAfterCreateOutput
specifier|public
name|void
name|setCrashAfterCreateOutput
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|crashAfterCreateOutput
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|cxt
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|indexOutput
init|=
name|in
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|cxt
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|crashAfterCreateOutput
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|crashAfterCreateOutput
argument_list|)
condition|)
block|{
comment|// CRASH!
name|indexOutput
operator|.
name|close
argument_list|()
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
operator|new
name|Throwable
argument_list|()
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|CrashingException
argument_list|(
literal|"crashAfterCreateOutput "
operator|+
name|crashAfterCreateOutput
argument_list|)
throw|;
block|}
return|return
name|indexOutput
return|;
block|}
block|}
block|}
end_class

end_unit

