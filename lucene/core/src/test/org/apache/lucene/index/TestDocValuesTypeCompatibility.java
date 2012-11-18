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
name|ByteDocValuesField
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
name|DerefBytesDocValuesField
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
name|DoubleDocValuesField
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
name|FloatDocValuesField
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
name|IntDocValuesField
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
name|LongDocValuesField
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
name|PackedLongDocValuesField
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
name|ShortDocValuesField
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
name|SortedBytesDocValuesField
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
name|StraightBytesDocValuesField
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
name|DocValues
operator|.
name|Type
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
name|BytesRef
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
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * Tests compatibility of {@link DocValues.Type} during indexing  */
end_comment

begin_class
DECL|class|TestDocValuesTypeCompatibility
specifier|public
class|class
name|TestDocValuesTypeCompatibility
extends|extends
name|LuceneTestCase
block|{
DECL|method|testAddCompatibleIntTypes
specifier|public
name|void
name|testAddCompatibleIntTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numIter
init|=
name|atLeast
argument_list|(
literal|10
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
name|numIter
condition|;
name|i
operator|++
control|)
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
operator|*
name|numDocs
argument_list|)
expr_stmt|;
comment|// make sure we hit the same DWPT
comment|// here
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMPerThreadHardLimitMB
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Type
index|[]
name|types
init|=
operator|new
name|Type
index|[]
block|{
name|Type
operator|.
name|VAR_INTS
block|,
name|Type
operator|.
name|FIXED_INTS_16
block|,
name|Type
operator|.
name|FIXED_INTS_64
block|,
name|Type
operator|.
name|FIXED_INTS_16
block|,
name|Type
operator|.
name|FIXED_INTS_8
block|}
decl_stmt|;
name|Type
name|maxType
init|=
name|types
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|types
operator|.
name|length
argument_list|)
index|]
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
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|getRandomIntsField
argument_list|(
name|maxType
argument_list|,
name|j
operator|==
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|getRandomIntsField
specifier|public
name|Field
name|getRandomIntsField
parameter_list|(
name|Type
name|maxType
parameter_list|,
name|boolean
name|force
parameter_list|)
block|{
switch|switch
condition|(
name|maxType
condition|)
block|{
case|case
name|VAR_INTS
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_64
case|:
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
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|LongDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_32
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|==
literal|0
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|IntDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_16
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|==
literal|0
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|ShortDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_8
case|:
return|return
operator|new
name|ByteDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
DECL|method|testAddCompatibleDoubleTypes
specifier|public
name|void
name|testAddCompatibleDoubleTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numIter
init|=
name|atLeast
argument_list|(
literal|10
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
name|numIter
condition|;
name|i
operator|++
control|)
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
operator|*
name|numDocs
argument_list|)
expr_stmt|;
comment|// make sure we hit the same DWPT
comment|// here
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMPerThreadHardLimitMB
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Type
index|[]
name|types
init|=
operator|new
name|Type
index|[]
block|{
name|Type
operator|.
name|FLOAT_64
block|,
name|Type
operator|.
name|FLOAT_32
block|}
decl_stmt|;
name|Type
name|maxType
init|=
name|types
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|types
operator|.
name|length
argument_list|)
index|]
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
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|getRandomFloatField
argument_list|(
name|maxType
argument_list|,
name|j
operator|==
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|getRandomFloatField
specifier|public
name|Field
name|getRandomFloatField
parameter_list|(
name|Type
name|maxType
parameter_list|,
name|boolean
name|force
parameter_list|)
block|{
switch|switch
condition|(
name|maxType
condition|)
block|{
case|case
name|FLOAT_64
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_32
case|:
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
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|LongDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
block|}
case|case
name|FLOAT_32
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|==
literal|0
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|IntDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_16
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|==
literal|0
operator|||
name|force
condition|)
block|{
return|return
operator|new
name|ShortDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
return|;
block|}
case|case
name|FIXED_INTS_8
case|:
return|return
operator|new
name|ByteDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
DECL|method|testAddCompatibleDoubleTypes2
specifier|public
name|void
name|testAddCompatibleDoubleTypes2
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numIter
init|=
name|atLeast
argument_list|(
literal|10
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
name|numIter
condition|;
name|i
operator|++
control|)
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
operator|*
name|numDocs
argument_list|)
expr_stmt|;
comment|// make sure we hit the same DWPT
comment|// here
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMPerThreadHardLimitMB
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Field
index|[]
name|fields
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|DoubleDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1.0
argument_list|)
block|,
operator|new
name|IntDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
block|,
operator|new
name|ShortDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
block|,
operator|new
name|ByteDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|}
decl_stmt|;
name|int
name|base
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|fields
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
name|fields
index|[
name|base
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
name|int
name|f
init|=
name|base
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|fields
operator|.
name|length
operator|-
name|base
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
name|fields
index|[
name|f
index|]
argument_list|)
expr_stmt|;
block|}
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
block|}
comment|// nocommit remove this test?  simple dv doesn't let you
comment|// change b/w sorted& binary?
annotation|@
name|Ignore
DECL|method|testAddCompatibleByteTypes
specifier|public
name|void
name|testAddCompatibleByteTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numIter
init|=
name|atLeast
argument_list|(
literal|10
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
name|numIter
condition|;
name|i
operator|++
control|)
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
operator|*
name|numDocs
argument_list|)
expr_stmt|;
comment|// make sure we hit the same DWPT
comment|// here
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMPerThreadHardLimitMB
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|boolean
name|mustBeFixed
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|int
name|maxSize
init|=
literal|2
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|Field
name|bytesField
init|=
name|getRandomBytesField
argument_list|(
name|mustBeFixed
argument_list|,
name|maxSize
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
name|bytesField
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
name|bytesField
operator|=
name|getRandomBytesField
argument_list|(
name|mustBeFixed
argument_list|,
name|maxSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
name|bytesField
argument_list|)
expr_stmt|;
block|}
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
block|}
DECL|method|getRandomBytesField
specifier|public
name|Field
name|getRandomBytesField
parameter_list|(
name|boolean
name|mustBeFixed
parameter_list|,
name|int
name|maxSize
parameter_list|,
name|boolean
name|mustBeVariableIfNotFixed
parameter_list|)
block|{
name|int
name|size
init|=
name|mustBeFixed
condition|?
name|maxSize
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
operator|+
literal|1
decl_stmt|;
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|boolean
name|fixed
init|=
name|mustBeFixed
condition|?
literal|true
else|:
name|mustBeVariableIfNotFixed
condition|?
literal|false
else|:
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
name|fixed
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
name|fixed
argument_list|)
return|;
default|default:
return|return
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
name|fixed
argument_list|)
return|;
block|}
block|}
DECL|method|testIncompatibleTypesBytes
specifier|public
name|void
name|testIncompatibleTypesBytes
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
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
comment|// make sure we hit the same DWPT
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMPerThreadHardLimitMB
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocsIndexed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
try|try
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|getRandomIndexableDVField
argument_list|()
argument_list|)
expr_stmt|;
name|numDocsIndexed
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Incompatible DocValues type:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|open
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
name|numDocsIndexed
argument_list|,
name|open
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|open
operator|.
name|close
argument_list|()
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|Field
modifier|...
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|indexableField
range|:
name|fields
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|indexableField
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
DECL|method|getRandomIndexableDVField
specifier|public
name|Field
name|getRandomIndexableDVField
parameter_list|()
block|{
name|int
name|size
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|1
decl_stmt|;
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Type
index|[]
name|values
init|=
name|Type
operator|.
name|values
argument_list|()
decl_stmt|;
name|Type
name|t
init|=
name|values
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|values
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
switch|switch
condition|(
name|t
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
literal|true
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
literal|true
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
literal|true
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|bytesRef
argument_list|,
literal|false
argument_list|)
return|;
case|case
name|FIXED_INTS_16
case|:
return|return
operator|new
name|ShortDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
return|;
case|case
name|FIXED_INTS_32
case|:
return|return
operator|new
name|IntDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
case|case
name|FIXED_INTS_64
case|:
return|return
operator|new
name|LongDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
case|case
name|FIXED_INTS_8
case|:
return|return
operator|new
name|ByteDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
return|return
operator|new
name|FloatDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1.0f
argument_list|)
return|;
case|case
name|FLOAT_64
case|:
return|return
operator|new
name|DoubleDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1.0f
argument_list|)
return|;
case|case
name|VAR_INTS
case|:
return|return
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

