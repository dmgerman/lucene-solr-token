begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|File
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
name|WhitespaceAnalyzer
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
operator|.
name|MaxFieldLength
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestIndexSplitter
specifier|public
class|class
name|TestIndexSplitter
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|tmpDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"testfilesplitter"
argument_list|)
decl_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|destDir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"testfilesplitterdest"
argument_list|)
decl_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|destDir
argument_list|)
expr_stmt|;
name|destDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FSDirectory
name|fsDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|fsDir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
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
literal|100
condition|;
name|x
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|TestIndexWriterReader
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"index"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|100
init|;
name|x
operator|<
literal|150
condition|;
name|x
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|TestIndexWriterReader
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"index2"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|150
init|;
name|x
operator|<
literal|200
condition|;
name|x
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|TestIndexWriterReader
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"index3"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|iw
operator|.
name|getReader
argument_list|()
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// we should have 2 segments now
name|IndexSplitter
name|is
init|=
operator|new
name|IndexSplitter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|String
name|splitSegName
init|=
name|is
operator|.
name|infos
operator|.
name|info
argument_list|(
literal|1
argument_list|)
operator|.
name|name
decl_stmt|;
name|is
operator|.
name|split
argument_list|(
name|destDir
argument_list|,
operator|new
name|String
index|[]
block|{
name|splitSegName
block|}
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
name|destDir
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// now test cmdline
name|File
name|destDir2
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"testfilesplitterdest2"
argument_list|)
decl_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|destDir2
argument_list|)
expr_stmt|;
name|destDir2
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|IndexSplitter
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
name|dir
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|destDir2
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|splitSegName
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|destDir2
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
name|destDir2
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// now remove the copied segment from src
name|IndexSplitter
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
name|dir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-d"
block|,
name|splitSegName
block|}
argument_list|)
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

