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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|CodecUtil
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
name|NumericDocValuesField
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
name|CompoundFileDirectory
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
name|IndexInput
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

begin_comment
comment|/**  * Test that a plain default puts codec headers in all files.  */
end_comment

begin_class
DECL|class|TestAllFilesHaveCodecHeader
specifier|public
class|class
name|TestAllFilesHaveCodecHeader
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
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// Else we might remove .cfe but not the corresponding .cfs, causing false exc when trying to verify headers:
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|conf
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
name|conf
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|conf
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
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|bodyField
init|=
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|FieldType
name|vectorsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|vectorsField
init|=
operator|new
name|Field
argument_list|(
literal|"vectors"
argument_list|,
literal|""
argument_list|,
name|vectorsType
argument_list|)
decl_stmt|;
name|Field
name|dvField
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|5
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
name|bodyField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|vectorsField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvField
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
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|bodyField
operator|.
name|setStringValue
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dvField
operator|.
name|setLongValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|vectorsField
operator|.
name|setStringValue
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|riw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
operator|==
literal|0
condition|)
block|{
name|riw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// TODO: we should make a new format with a clean header...
comment|// if (random().nextInt(20) == 0) {
comment|//  riw.deleteDocuments(new Term("id", Integer.toString(i)));
comment|// }
block|}
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkHeaders
argument_list|(
name|dir
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkHeaders
specifier|private
name|void
name|checkHeaders
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namesToExtensions
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|equals
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
condition|)
block|{
continue|continue;
comment|// write.lock has no header, thats ok
block|}
if|if
condition|(
name|file
operator|.
name|endsWith
argument_list|(
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
condition|)
block|{
name|CompoundFileDirectory
name|cfsDir
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|dir
argument_list|,
name|file
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|checkHeaders
argument_list|(
name|cfsDir
argument_list|,
name|namesToExtensions
argument_list|)
expr_stmt|;
comment|// recurse into cfs
name|cfsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexInput
name|in
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|in
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|val
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|file
operator|+
literal|" has no codec header, instead found: "
operator|+
name|val
argument_list|,
name|CodecUtil
operator|.
name|CODEC_MAGIC
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|String
name|codecName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|codecName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|extension
init|=
name|IndexFileNames
operator|.
name|getExtension
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|extension
operator|==
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|file
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
argument_list|)
expr_stmt|;
name|extension
operator|=
literal|"<segments> (not a real extension, designates segments file)"
expr_stmt|;
block|}
name|String
name|previous
init|=
name|namesToExtensions
operator|.
name|put
argument_list|(
name|codecName
argument_list|,
name|extension
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
operator|&&
operator|!
name|previous
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"extensions "
operator|+
name|previous
operator|+
literal|" and "
operator|+
name|extension
operator|+
literal|" share same codecName "
operator|+
name|codecName
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

