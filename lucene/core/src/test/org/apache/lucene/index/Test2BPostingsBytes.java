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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|analysis
operator|.
name|TokenStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|compressing
operator|.
name|CompressingCodec
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
name|document
operator|.
name|FieldType
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
name|TextField
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
name|BaseDirectoryWrapper
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
name|Monster
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
name|SuppressCodecs
import|;
end_import

begin_comment
comment|/**  * Test indexes 2B docs with 65k freqs each,   * so you get&gt; Integer.MAX_VALUE postings data for the term  * @lucene.experimental  */
end_comment

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|}
argument_list|)
annotation|@
name|Monster
argument_list|(
literal|"takes ~20GB-30GB of space and 10 minutes"
argument_list|)
DECL|class|Test2BPostingsBytes
specifier|public
class|class
name|Test2BPostingsBytes
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
name|IndexWriterConfig
name|defaultConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Codec
name|defaultCodec
init|=
name|defaultConfig
operator|.
name|getCodec
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
operator|)
operator|.
name|getCodec
argument_list|()
operator|instanceof
name|CompressingCodec
condition|)
block|{
name|Pattern
name|regex
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"maxDocsPerChunk=(\\d+), blockSize=(\\d+)"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|regex
operator|.
name|matcher
argument_list|(
name|defaultCodec
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected CompressingCodec toString() output: "
operator|+
name|defaultCodec
operator|.
name|toString
argument_list|()
argument_list|,
name|matcher
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|maxDocsPerChunk
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|blockSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|product
init|=
name|maxDocsPerChunk
operator|*
name|blockSize
decl_stmt|;
name|assumeTrue
argument_list|(
name|defaultCodec
operator|.
name|getName
argument_list|()
operator|+
literal|" maxDocsPerChunk ("
operator|+
name|maxDocsPerChunk
operator|+
literal|") * blockSize ("
operator|+
name|blockSize
operator|+
literal|")< 16 - this can trigger OOM with -Dtests.heapsize=30g"
argument_list|,
name|product
operator|>=
literal|16
argument_list|)
expr_stmt|;
block|}
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BPostingsBytes1"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|MergePolicy
name|mp
init|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|LogByteSizeMergePolicy
condition|)
block|{
comment|// 1 petabyte:
operator|(
operator|(
name|LogByteSizeMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setMaxMergeMB
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MyTokenStream
name|tokenStream
init|=
operator|new
name|MyTokenStream
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
name|tokenStream
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
literal|1000
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
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
comment|// trick blockPF's little optimization
name|tokenStream
operator|.
name|n
operator|=
literal|65536
expr_stmt|;
block|}
else|else
block|{
name|tokenStream
operator|.
name|n
operator|=
literal|65537
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|oneThousand
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|DirectoryReader
name|subReaders
index|[]
init|=
operator|new
name|DirectoryReader
index|[
literal|1000
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|subReaders
argument_list|,
name|oneThousand
argument_list|)
expr_stmt|;
name|BaseDirectoryWrapper
name|dir2
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BPostingsBytes2"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir2
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir2
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|addIndexesSlowly
argument_list|(
name|w2
argument_list|,
name|subReaders
argument_list|)
expr_stmt|;
name|w2
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
name|oneThousand
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|oneMillion
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|subReaders
operator|=
operator|new
name|DirectoryReader
index|[
literal|2000
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|subReaders
argument_list|,
name|oneMillion
argument_list|)
expr_stmt|;
name|BaseDirectoryWrapper
name|dir3
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BPostingsBytes3"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir3
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir3
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|w3
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir3
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|addIndexesSlowly
argument_list|(
name|w3
argument_list|,
name|subReaders
argument_list|)
expr_stmt|;
name|w3
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w3
operator|.
name|close
argument_list|()
expr_stmt|;
name|oneMillion
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir3
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MyTokenStream
specifier|public
specifier|static
specifier|final
class|class
name|MyTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|index
name|int
name|index
decl_stmt|;
DECL|field|n
name|int
name|n
decl_stmt|;
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|index
operator|<
name|n
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|buffer
argument_list|()
index|[
literal|0
index|]
operator|=
literal|'a'
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|index
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

