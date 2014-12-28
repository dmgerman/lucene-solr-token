begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lucene50
operator|.
name|Lucene50StoredFieldsFormat
operator|.
name|Mode
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
name|StoredField
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
name|BaseStoredFieldsFormatTestCase
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
name|StoredDocument
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_class
DECL|class|TestLucene50StoredFieldsFormatHighCompression
specifier|public
class|class
name|TestLucene50StoredFieldsFormatHighCompression
extends|extends
name|BaseStoredFieldsFormatTestCase
block|{
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
operator|new
name|Lucene50Codec
argument_list|(
name|Mode
operator|.
name|BEST_COMPRESSION
argument_list|)
return|;
block|}
comment|/**    * Change compression params (leaving it the same for old segments)    * and tests that nothing breaks.    */
DECL|method|testMixedCompressions
specifier|public
name|void
name|testMixedCompressions
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
condition|;
name|i
operator|++
control|)
block|{
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
operator|new
name|Lucene50Codec
argument_list|(
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|Mode
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
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
name|StoredField
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|StoredDocument
name|doc
init|=
name|ir
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"field2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// checkindex
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testInvalidOptions
specifier|public
name|void
name|testInvalidOptions
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|Lucene50Codec
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
operator|new
name|Lucene50StoredFieldsFormat
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

