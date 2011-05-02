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
DECL|field|workDir
name|File
name|workDir
decl_stmt|;
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
name|workDir
operator|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestMultiMMap"
argument_list|)
expr_stmt|;
name|workDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
DECL|method|testRandomChunkSizes
specifier|public
name|void
name|testRandomChunkSizes
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
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|i
operator|++
control|)
name|assertChunking
argument_list|(
name|random
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
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
name|File
operator|.
name|createTempFile
argument_list|(
literal|"mmap"
operator|+
name|chunkSize
argument_list|,
literal|"tmp"
argument_list|,
name|workDir
argument_list|)
decl_stmt|;
name|path
operator|.
name|delete
argument_list|()
expr_stmt|;
name|path
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|MMapDirectory
name|dir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setMaxChunkSize
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
comment|// we will map a lot, try to turn on the unmap hack
if|if
condition|(
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
name|dir
operator|.
name|setUseUnmap
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|Field
name|junk
init|=
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
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
name|setValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|junk
operator|.
name|setValue
argument_list|(
name|_TestUtil
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
name|close
argument_list|()
expr_stmt|;
name|int
name|numAsserts
init|=
literal|100
operator|*
name|RANDOM_MULTIPLIER
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

