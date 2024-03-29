begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.perfield
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|perfield
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
name|util
operator|.
name|ArrayList
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|PostingsFormat
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
name|asserting
operator|.
name|AssertingCodec
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
name|blockterms
operator|.
name|LuceneVarGapFixedInterval
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
name|memory
operator|.
name|DirectPostingsFormat
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
name|memory
operator|.
name|MemoryPostingsFormat
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
name|IntPoint
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
name|StringField
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
name|index
operator|.
name|DirectoryReader
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
name|FieldInfo
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
name|Fields
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
name|LogDocMergePolicy
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
name|MergeState
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
name|index
operator|.
name|SegmentReadState
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
name|SegmentWriteState
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
name|Term
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *   *  */
end_comment

begin_comment
comment|//TODO: would be better in this test to pull termsenums and instanceof or something?
end_comment

begin_comment
comment|// this way we can verify PFPF is doing the right thing.
end_comment

begin_comment
comment|// for now we do termqueries.
end_comment

begin_class
DECL|class|TestPerFieldPostingsFormat2
specifier|public
class|class
name|TestPerFieldPostingsFormat2
extends|extends
name|LuceneTestCase
block|{
DECL|method|newWriter
specifier|private
name|IndexWriter
name|newWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|LogDocMergePolicy
name|logByteSizeMergePolicy
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|logByteSizeMergePolicy
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
comment|// make sure we use plain
comment|// files
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|logByteSizeMergePolicy
argument_list|)
expr_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|writer
return|;
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
block|}
DECL|method|addDocs2
specifier|private
name|void
name|addDocs2
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
block|}
DECL|method|addDocs3
specifier|private
name|void
name|addDocs3
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
block|}
comment|/*    * Test that heterogeneous index segments are merge successfully    */
annotation|@
name|Test
DECL|method|testMergeUnusedPerFieldCodec
specifier|public
name|void
name|testMergeUnusedPerFieldCodec
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwconf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setCodec
argument_list|(
operator|new
name|MockCodec
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
name|newWriter
argument_list|(
name|dir
argument_list|,
name|iwconf
argument_list|)
decl_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|addDocs3
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|addDocs2
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
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
comment|/*    * Test that heterogeneous index segments are merged sucessfully    */
comment|// TODO: not sure this test is that great, we should probably peek inside PerFieldPostingsFormat or something?!
annotation|@
name|Test
DECL|method|testChangeCodecAndMerge
specifier|public
name|void
name|testChangeCodecAndMerge
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
literal|"TEST: make new index"
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|iwconf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setCodec
argument_list|(
operator|new
name|MockCodec
argument_list|()
argument_list|)
decl_stmt|;
name|iwconf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
comment|//((LogMergePolicy) iwconf.getMergePolicy()).setMergeFactor(10);
name|IndexWriter
name|writer
init|=
name|newWriter
argument_list|(
name|dir
argument_list|,
name|iwconf
argument_list|)
decl_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
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
literal|"TEST: addDocs3"
argument_list|)
expr_stmt|;
block|}
name|addDocs3
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Codec
name|codec
init|=
name|iwconf
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|iwconf
operator|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
comment|//((LogMergePolicy) iwconf.getMergePolicy()).setNoCFSRatio(0.0);
comment|//((LogMergePolicy) iwconf.getMergePolicy()).setMergeFactor(10);
name|iwconf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwconf
operator|.
name|setCodec
argument_list|(
operator|new
name|MockCodec2
argument_list|()
argument_list|)
expr_stmt|;
comment|// uses standard for field content
name|writer
operator|=
name|newWriter
argument_list|(
name|dir
argument_list|,
name|iwconf
argument_list|)
expr_stmt|;
comment|// swap in new codec for currently written segments
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
literal|"TEST: add docs w/ Standard codec for content field"
argument_list|)
expr_stmt|;
block|}
name|addDocs2
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|codec
operator|=
name|iwconf
operator|.
name|getCodec
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|////
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
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
literal|"TEST: add more docs w/ new codec"
argument_list|)
expr_stmt|;
block|}
name|addDocs2
argument_list|(
name|writer
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
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
literal|"TEST: now optimize"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertQuery
specifier|public
name|void
name|assertQuery
parameter_list|(
name|Term
name|t
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"\nTEST: assertQuery "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TopDocs
name|search
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
argument_list|,
name|num
operator|+
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|num
argument_list|,
name|search
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MockCodec
specifier|public
specifier|static
class|class
name|MockCodec
extends|extends
name|AssertingCodec
block|{
DECL|field|luceneDefault
specifier|final
name|PostingsFormat
name|luceneDefault
init|=
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|direct
specifier|final
name|PostingsFormat
name|direct
init|=
operator|new
name|DirectPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|memory
specifier|final
name|PostingsFormat
name|memory
init|=
operator|new
name|MemoryPostingsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
return|return
name|direct
return|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"content"
argument_list|)
condition|)
block|{
return|return
name|memory
return|;
block|}
else|else
block|{
return|return
name|luceneDefault
return|;
block|}
block|}
block|}
DECL|class|MockCodec2
specifier|public
specifier|static
class|class
name|MockCodec2
extends|extends
name|AssertingCodec
block|{
DECL|field|luceneDefault
specifier|final
name|PostingsFormat
name|luceneDefault
init|=
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|direct
specifier|final
name|PostingsFormat
name|direct
init|=
operator|new
name|DirectPostingsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
return|return
name|direct
return|;
block|}
else|else
block|{
return|return
name|luceneDefault
return|;
block|}
block|}
block|}
comment|/*    * Test per field codec support - adding fields with random codecs    */
annotation|@
name|Test
DECL|method|testStressPerFieldCodec
specifier|public
name|void
name|testStressPerFieldCodec
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docsPerRound
init|=
literal|97
decl_stmt|;
name|int
name|numRounds
init|=
name|atLeast
argument_list|(
literal|1
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
name|numRounds
condition|;
name|i
operator|++
control|)
block|{
name|int
name|num
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|30
argument_list|,
literal|60
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|config
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
name|newWriter
argument_list|(
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docsPerRound
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|num
condition|;
name|k
operator|++
control|)
block|{
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setTokenized
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|""
operator|+
name|k
argument_list|,
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|128
argument_list|)
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|*
name|docsPerRound
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSameCodecDifferentInstance
specifier|public
name|void
name|testSameCodecDifferentInstance
parameter_list|()
throws|throws
name|Exception
block|{
name|Codec
name|codec
init|=
operator|new
name|AssertingCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|MemoryPostingsFormat
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"date"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|MemoryPostingsFormat
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getPostingsFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|doTestMixedPostings
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
DECL|method|testSameCodecDifferentParams
specifier|public
name|void
name|testSameCodecDifferentParams
parameter_list|()
throws|throws
name|Exception
block|{
name|Codec
name|codec
init|=
operator|new
name|AssertingCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|LuceneVarGapFixedInterval
argument_list|(
literal|1
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"date"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|LuceneVarGapFixedInterval
argument_list|(
literal|2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getPostingsFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|doTestMixedPostings
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestMixedPostings
specifier|private
name|void
name|doTestMixedPostings
parameter_list|(
name|Codec
name|codec
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
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
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
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
comment|// turn on vectors for the checkindex cross-check
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|Field
name|dateField
init|=
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dateField
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// checkindex
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testMergeCalledOnTwoFormats
specifier|public
name|void
name|testMergeCalledOnTwoFormats
parameter_list|()
throws|throws
name|IOException
block|{
name|MergeRecordingPostingsFormatWrapper
name|pf1
init|=
operator|new
name|MergeRecordingPostingsFormatWrapper
argument_list|(
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
name|MergeRecordingPostingsFormatWrapper
name|pf2
init|=
operator|new
name|MergeRecordingPostingsFormatWrapper
argument_list|(
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
operator|new
name|AssertingCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
switch|switch
condition|(
name|field
condition|)
block|{
case|case
literal|"f1"
case|:
case|case
literal|"f2"
case|:
return|return
name|pf1
return|;
case|case
literal|"f3"
case|:
case|case
literal|"f4"
case|:
return|return
name|pf2
return|;
default|default:
return|return
name|super
operator|.
name|getPostingsFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iwriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f1"
argument_list|,
literal|"val1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f2"
argument_list|,
literal|"val2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntPoint
argument_list|(
literal|"f3"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Points are not indexed as postings and should not appear in the merge fields
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f4"
argument_list|,
literal|"val4"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f1"
argument_list|,
literal|"val5"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f2"
argument_list|,
literal|"val6"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntPoint
argument_list|(
literal|"f3"
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f4"
argument_list|,
literal|"val8"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pf1
operator|.
name|nbMergeCalls
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f1"
argument_list|,
literal|"f2"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|pf1
operator|.
name|fieldNames
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pf2
operator|.
name|nbMergeCalls
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"f4"
argument_list|)
argument_list|,
name|pf2
operator|.
name|fieldNames
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MergeRecordingPostingsFormatWrapper
specifier|private
specifier|static
specifier|final
class|class
name|MergeRecordingPostingsFormatWrapper
extends|extends
name|PostingsFormat
block|{
DECL|field|delegate
specifier|private
specifier|final
name|PostingsFormat
name|delegate
decl_stmt|;
DECL|field|fieldNames
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nbMergeCalls
name|int
name|nbMergeCalls
init|=
literal|0
decl_stmt|;
DECL|method|MergeRecordingPostingsFormatWrapper
name|MergeRecordingPostingsFormatWrapper
parameter_list|(
name|PostingsFormat
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FieldsConsumer
name|consumer
init|=
name|delegate
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
return|return
operator|new
name|FieldsConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|write
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|nbMergeCalls
operator|++
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|mergeState
operator|.
name|mergeFieldInfos
control|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

