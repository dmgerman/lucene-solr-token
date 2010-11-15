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
name|IOException
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
name|Field
operator|.
name|Index
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
name|CheckIndex
operator|.
name|Status
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
name|CheckIndex
operator|.
name|Status
operator|.
name|SegmentInfoStatus
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|codecs
operator|.
name|mockintblock
operator|.
name|MockFixedIntBlockCodec
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
name|codecs
operator|.
name|mockintblock
operator|.
name|MockVariableIntBlockCodec
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
name|codecs
operator|.
name|mocksep
operator|.
name|MockSepCodec
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
name|codecs
operator|.
name|pulsing
operator|.
name|PulsingCodec
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
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextCodec
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
name|codecs
operator|.
name|standard
operator|.
name|StandardCodec
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
name|_TestUtil
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

begin_class
DECL|class|TestPerFieldCodecSupport
specifier|public
class|class
name|TestPerFieldCodecSupport
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
name|setUseCompoundFile
argument_list|(
literal|false
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
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
comment|/*    * Test is hetrogenous index segements are merge sucessfully    */
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
name|CodecProvider
name|provider
init|=
operator|new
name|MockCodecProvider
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwconf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|provider
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
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
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
comment|/*    * Test is hetrogenous index segements are merge sucessfully    */
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
name|CodecProvider
name|provider
init|=
operator|new
name|MockCodecProvider
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwconf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|provider
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
operator|(
operator|(
name|LogMergePolicy
operator|)
name|iwconf
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
literal|10
argument_list|)
expr_stmt|;
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
argument_list|,
name|provider
argument_list|)
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
argument_list|,
name|provider
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
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|assertCodecPerField
argument_list|(
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|,
name|provider
argument_list|)
argument_list|,
literal|"content"
argument_list|,
name|provider
operator|.
name|lookup
argument_list|(
literal|"MockSep"
argument_list|)
argument_list|)
expr_stmt|;
name|iwconf
operator|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|iwconf
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|iwconf
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setUseCompoundDocStore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|iwconf
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|iwconf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|provider
operator|=
operator|new
name|MockCodecProvider2
argument_list|()
expr_stmt|;
comment|// uses standard for field content
name|iwconf
operator|.
name|setCodecProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
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
name|Codec
name|origContentCodec
init|=
name|provider
operator|.
name|lookup
argument_list|(
literal|"MockSep"
argument_list|)
decl_stmt|;
name|Codec
name|newContentCodec
init|=
name|provider
operator|.
name|lookup
argument_list|(
literal|"Standard"
argument_list|)
decl_stmt|;
name|assertHybridCodecPerField
argument_list|(
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|,
name|provider
argument_list|)
argument_list|,
literal|"content"
argument_list|,
name|origContentCodec
argument_list|,
name|origContentCodec
argument_list|,
name|newContentCodec
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
argument_list|,
name|provider
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
argument_list|,
name|provider
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
argument_list|,
name|provider
argument_list|)
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
argument_list|,
name|provider
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
argument_list|,
name|provider
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
argument_list|,
name|provider
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
name|optimize
argument_list|()
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
name|assertCodecPerFieldOptimized
argument_list|(
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|,
name|provider
argument_list|)
argument_list|,
literal|"content"
argument_list|,
name|newContentCodec
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
argument_list|,
name|provider
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
argument_list|,
name|provider
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
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertCodecPerFieldOptimized
specifier|public
name|void
name|assertCodecPerFieldOptimized
parameter_list|(
name|Status
name|checkIndex
parameter_list|,
name|String
name|field
parameter_list|,
name|Codec
name|codec
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|checkIndex
operator|.
name|segmentInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CodecProvider
name|provider
init|=
name|checkIndex
operator|.
name|segmentInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|codec
operator|.
name|provider
decl_stmt|;
name|assertEquals
argument_list|(
name|codec
argument_list|,
name|provider
operator|.
name|lookup
argument_list|(
name|provider
operator|.
name|getFieldCodec
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCodecPerField
specifier|public
name|void
name|assertCodecPerField
parameter_list|(
name|Status
name|checkIndex
parameter_list|,
name|String
name|field
parameter_list|,
name|Codec
name|codec
parameter_list|)
block|{
for|for
control|(
name|SegmentInfoStatus
name|info
range|:
name|checkIndex
operator|.
name|segmentInfos
control|)
block|{
specifier|final
name|CodecProvider
name|provider
init|=
name|info
operator|.
name|codec
operator|.
name|provider
decl_stmt|;
name|assertEquals
argument_list|(
name|codec
argument_list|,
name|provider
operator|.
name|lookup
argument_list|(
name|provider
operator|.
name|getFieldCodec
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertHybridCodecPerField
specifier|public
name|void
name|assertHybridCodecPerField
parameter_list|(
name|Status
name|checkIndex
parameter_list|,
name|String
name|field
parameter_list|,
name|Codec
modifier|...
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SegmentInfoStatus
argument_list|>
name|segmentInfos
init|=
name|checkIndex
operator|.
name|segmentInfos
decl_stmt|;
name|assertEquals
argument_list|(
name|segmentInfos
operator|.
name|size
argument_list|()
argument_list|,
name|codec
operator|.
name|length
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
name|codec
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SegmentCodecs
name|codecInfo
init|=
name|segmentInfos
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|codec
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
name|checkIndex
operator|.
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfos
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|FIELD_INFOS_EXTENSION
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"faild for segment index: "
operator|+
name|i
argument_list|,
name|codec
index|[
name|i
index|]
argument_list|,
name|codecInfo
operator|.
name|codecs
index|[
name|fieldInfo
operator|.
name|codecId
index|]
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|CodecProvider
name|codecs
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|,
name|codecs
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MockCodecProvider
specifier|public
specifier|static
class|class
name|MockCodecProvider
extends|extends
name|CodecProvider
block|{
DECL|method|MockCodecProvider
specifier|public
name|MockCodecProvider
parameter_list|()
block|{
name|StandardCodec
name|standardCodec
init|=
operator|new
name|StandardCodec
argument_list|()
decl_stmt|;
name|setDefaultFieldCodec
argument_list|(
name|standardCodec
operator|.
name|name
argument_list|)
expr_stmt|;
name|SimpleTextCodec
name|simpleTextCodec
init|=
operator|new
name|SimpleTextCodec
argument_list|()
decl_stmt|;
name|MockSepCodec
name|mockSepCodec
init|=
operator|new
name|MockSepCodec
argument_list|()
decl_stmt|;
name|register
argument_list|(
name|standardCodec
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|mockSepCodec
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|simpleTextCodec
argument_list|)
expr_stmt|;
name|setFieldCodec
argument_list|(
literal|"id"
argument_list|,
name|simpleTextCodec
operator|.
name|name
argument_list|)
expr_stmt|;
name|setFieldCodec
argument_list|(
literal|"content"
argument_list|,
name|mockSepCodec
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MockCodecProvider2
specifier|public
specifier|static
class|class
name|MockCodecProvider2
extends|extends
name|CodecProvider
block|{
DECL|method|MockCodecProvider2
specifier|public
name|MockCodecProvider2
parameter_list|()
block|{
name|StandardCodec
name|standardCodec
init|=
operator|new
name|StandardCodec
argument_list|()
decl_stmt|;
name|setDefaultFieldCodec
argument_list|(
name|standardCodec
operator|.
name|name
argument_list|)
expr_stmt|;
name|SimpleTextCodec
name|simpleTextCodec
init|=
operator|new
name|SimpleTextCodec
argument_list|()
decl_stmt|;
name|MockSepCodec
name|mockSepCodec
init|=
operator|new
name|MockSepCodec
argument_list|()
decl_stmt|;
name|register
argument_list|(
name|standardCodec
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|mockSepCodec
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|simpleTextCodec
argument_list|)
expr_stmt|;
name|setFieldCodec
argument_list|(
literal|"id"
argument_list|,
name|simpleTextCodec
operator|.
name|name
argument_list|)
expr_stmt|;
name|setFieldCodec
argument_list|(
literal|"content"
argument_list|,
name|standardCodec
operator|.
name|name
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
name|Index
index|[]
name|indexValue
init|=
operator|new
name|Index
index|[]
block|{
name|Index
operator|.
name|ANALYZED
block|,
name|Index
operator|.
name|ANALYZED_NO_NORMS
block|,
name|Index
operator|.
name|NOT_ANALYZED
block|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
block|}
decl_stmt|;
specifier|final
name|int
name|docsPerRound
init|=
literal|97
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|CodecProvider
name|provider
init|=
operator|new
name|CodecProvider
argument_list|()
decl_stmt|;
name|Codec
index|[]
name|codecs
init|=
operator|new
name|Codec
index|[]
block|{
operator|new
name|StandardCodec
argument_list|()
block|,
operator|new
name|SimpleTextCodec
argument_list|()
block|,
operator|new
name|MockSepCodec
argument_list|()
block|,
operator|new
name|PulsingCodec
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
block|,
operator|new
name|MockVariableIntBlockCodec
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
block|,
operator|new
name|MockFixedIntBlockCodec
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Codec
name|codec
range|:
name|codecs
control|)
block|{
name|provider
operator|.
name|register
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|30
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|j
operator|++
control|)
block|{
name|provider
operator|.
name|setFieldCodec
argument_list|(
literal|""
operator|+
name|j
argument_list|,
name|codecs
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|codecs
operator|.
name|length
argument_list|)
index|]
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
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
name|config
operator|.
name|setCodecProvider
argument_list|(
name|provider
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
literal|30
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|k
operator|++
control|)
block|{
name|Field
name|field
init|=
name|newField
argument_list|(
literal|""
operator|+
name|k
argument_list|,
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|,
literal|128
argument_list|)
argument_list|,
name|indexValue
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|indexValue
operator|.
name|length
argument_list|)
index|]
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
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|optimize
argument_list|()
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
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

