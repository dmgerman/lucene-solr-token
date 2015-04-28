begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|Store
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
name|IntField
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
name|CodecReader
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
name|LeafReaderContext
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
name|NoMergePolicy
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
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|MockDirectoryWrapper
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
name|RandomInts
import|;
end_import

begin_class
DECL|class|TestCompressingStoredFieldsFormat
specifier|public
class|class
name|TestCompressingStoredFieldsFormat
extends|extends
name|BaseStoredFieldsFormatTestCase
block|{
DECL|field|SECOND
specifier|static
specifier|final
name|long
name|SECOND
init|=
literal|1000L
decl_stmt|;
DECL|field|HOUR
specifier|static
specifier|final
name|long
name|HOUR
init|=
literal|60
operator|*
literal|60
operator|*
name|SECOND
decl_stmt|;
DECL|field|DAY
specifier|static
specifier|final
name|long
name|DAY
init|=
literal|24
operator|*
name|HOUR
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|)
return|;
block|}
DECL|method|testDeletePartiallyWrittenFilesIfAbort
specifier|public
name|void
name|testDeletePartiallyWrittenFilesIfAbort
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
comment|// test explicitly needs files to always be actually deleted
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
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|iwConf
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
name|iwConf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setCodec
argument_list|(
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// disable CFS because this test checks file names
name|iwConf
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Cannot use RIW because this test wants CFS to stay off:
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwConf
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|validDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|validDoc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|validDoc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// make sure that #writeField will fail to trigger an abort
specifier|final
name|Document
name|invalidDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|invalidDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"invalid"
argument_list|,
name|fieldType
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
comment|// TODO: really bad& scary that this causes IW to
comment|// abort the segment!!  We should fix this.
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|invalidDoc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
name|iae
argument_list|,
name|iw
operator|.
name|getTragicException
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Writer should be closed by tragedy
name|assertFalse
argument_list|(
name|iw
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testZFloat
specifier|public
name|void
name|testZFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|5
index|]
decl_stmt|;
comment|// we never need more than 5 bytes
name|ByteArrayDataOutput
name|out
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
comment|// round-trip small integer values
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
init|;
name|i
operator|<
name|Short
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
name|float
name|f
init|=
operator|(
name|float
operator|)
name|i
decl_stmt|;
name|CompressingStoredFieldsWriter
operator|.
name|writeZFloat
argument_list|(
name|out
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|g
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZFloat
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|g
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that compression actually works
if|if
condition|(
name|i
operator|>=
operator|-
literal|1
operator|&&
name|i
operator|<=
literal|123
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|// single byte compression
block|}
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// round-trip special values
name|float
name|special
index|[]
init|=
block|{
operator|-
literal|0.0f
block|,
operator|+
literal|0.0f
block|,
name|Float
operator|.
name|NEGATIVE_INFINITY
block|,
name|Float
operator|.
name|POSITIVE_INFINITY
block|,
name|Float
operator|.
name|MIN_VALUE
block|,
name|Float
operator|.
name|MAX_VALUE
block|,
name|Float
operator|.
name|NaN
block|,     }
decl_stmt|;
for|for
control|(
name|float
name|f
range|:
name|special
control|)
block|{
name|CompressingStoredFieldsWriter
operator|.
name|writeZFloat
argument_list|(
name|out
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|g
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZFloat
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|g
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// round-trip random values
name|Random
name|r
init|=
name|random
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|float
name|f
init|=
name|r
operator|.
name|nextFloat
argument_list|()
operator|*
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|-
literal|50
operator|)
decl_stmt|;
name|CompressingStoredFieldsWriter
operator|.
name|writeZFloat
argument_list|(
name|out
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"length="
operator|+
name|out
operator|.
name|getPosition
argument_list|()
operator|+
literal|", f="
operator|+
name|f
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
operator|<=
operator|(
operator|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
operator|>>>
literal|31
operator|)
operator|==
literal|1
condition|?
literal|5
else|:
literal|4
operator|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|g
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZFloat
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|g
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testZDouble
specifier|public
name|void
name|testZDouble
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|9
index|]
decl_stmt|;
comment|// we never need more than 9 bytes
name|ByteArrayDataOutput
name|out
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
comment|// round-trip small integer values
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
init|;
name|i
operator|<
name|Short
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
name|double
name|x
init|=
operator|(
name|double
operator|)
name|i
decl_stmt|;
name|CompressingStoredFieldsWriter
operator|.
name|writeZDouble
argument_list|(
name|out
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|y
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZDouble
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|x
argument_list|)
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that compression actually works
if|if
condition|(
name|i
operator|>=
operator|-
literal|1
operator|&&
name|i
operator|<=
literal|124
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|// single byte compression
block|}
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// round-trip special values
name|double
name|special
index|[]
init|=
block|{
operator|-
literal|0.0d
block|,
operator|+
literal|0.0d
block|,
name|Double
operator|.
name|NEGATIVE_INFINITY
block|,
name|Double
operator|.
name|POSITIVE_INFINITY
block|,
name|Double
operator|.
name|MIN_VALUE
block|,
name|Double
operator|.
name|MAX_VALUE
block|,
name|Double
operator|.
name|NaN
block|}
decl_stmt|;
for|for
control|(
name|double
name|x
range|:
name|special
control|)
block|{
name|CompressingStoredFieldsWriter
operator|.
name|writeZDouble
argument_list|(
name|out
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|y
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZDouble
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|x
argument_list|)
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// round-trip random values
name|Random
name|r
init|=
name|random
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|x
init|=
name|r
operator|.
name|nextDouble
argument_list|()
operator|*
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|-
literal|50
operator|)
decl_stmt|;
name|CompressingStoredFieldsWriter
operator|.
name|writeZDouble
argument_list|(
name|out
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"length="
operator|+
name|out
operator|.
name|getPosition
argument_list|()
operator|+
literal|", d="
operator|+
name|x
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
operator|<=
operator|(
name|x
operator|<
literal|0
condition|?
literal|9
else|:
literal|8
operator|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|y
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZDouble
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|x
argument_list|)
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// same with floats
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|x
init|=
call|(
name|double
call|)
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
operator|*
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|-
literal|50
operator|)
argument_list|)
decl_stmt|;
name|CompressingStoredFieldsWriter
operator|.
name|writeZDouble
argument_list|(
name|out
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"length="
operator|+
name|out
operator|.
name|getPosition
argument_list|()
operator|+
literal|", d="
operator|+
name|x
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
operator|<=
literal|5
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|y
init|=
name|CompressingStoredFieldsReader
operator|.
name|readZDouble
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|x
argument_list|)
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTLong
specifier|public
name|void
name|testTLong
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
comment|// we never need more than 10 bytes
name|ByteArrayDataOutput
name|out
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
comment|// round-trip small integer values
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
init|;
name|i
operator|<
name|Short
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|long
name|mul
range|:
operator|new
name|long
index|[]
block|{
name|SECOND
block|,
name|HOUR
block|,
name|DAY
block|}
control|)
block|{
name|long
name|l1
init|=
operator|(
name|long
operator|)
name|i
operator|*
name|mul
decl_stmt|;
name|CompressingStoredFieldsWriter
operator|.
name|writeTLong
argument_list|(
name|out
argument_list|,
name|l1
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|l2
init|=
name|CompressingStoredFieldsReader
operator|.
name|readTLong
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|l1
argument_list|,
name|l2
argument_list|)
expr_stmt|;
comment|// check that compression actually works
if|if
condition|(
name|i
operator|>=
operator|-
literal|16
operator|&&
name|i
operator|<=
literal|15
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|// single byte compression
block|}
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
comment|// round-trip random values
name|Random
name|r
init|=
name|random
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|numBits
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|65
argument_list|)
decl_stmt|;
name|long
name|l1
init|=
name|r
operator|.
name|nextLong
argument_list|()
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|numBits
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
switch|switch
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|l1
operator|*=
name|SECOND
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|l1
operator|*=
name|HOUR
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|l1
operator|*=
name|DAY
expr_stmt|;
break|break;
default|default:
break|break;
block|}
name|CompressingStoredFieldsWriter
operator|.
name|writeTLong
argument_list|(
name|out
argument_list|,
name|l1
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|l2
init|=
name|CompressingStoredFieldsReader
operator|.
name|readTLong
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|eof
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|l1
argument_list|,
name|l2
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * writes some tiny segments with incomplete compressed blocks,    * and ensures merge recompresses them.    */
DECL|method|testChunkCleanup
specifier|public
name|void
name|testChunkCleanup
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
name|iwConf
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
name|iwConf
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
comment|// we have to enforce certain things like maxDocsPerChunk to cause dirty chunks to be created
comment|// by this test.
name|iwConf
operator|.
name|setCodec
argument_list|(
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
operator|*
literal|1024
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|,
literal|8
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
name|iwConf
argument_list|)
decl_stmt|;
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
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
literal|5
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
operator|new
name|StoredField
argument_list|(
literal|"text"
argument_list|,
literal|"not very long at all"
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
comment|// force flush
name|DirectoryReader
name|ir2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ir2
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|ir2
expr_stmt|;
comment|// examine dirty counts:
for|for
control|(
name|LeafReaderContext
name|leaf
range|:
name|ir2
operator|.
name|leaves
argument_list|()
control|)
block|{
name|CodecReader
name|sr
init|=
operator|(
name|CodecReader
operator|)
name|leaf
operator|.
name|reader
argument_list|()
decl_stmt|;
name|CompressingStoredFieldsReader
name|reader
init|=
operator|(
name|CompressingStoredFieldsReader
operator|)
name|sr
operator|.
name|getFieldsReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|getNumChunks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|getNumDirtyChunks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|getConfig
argument_list|()
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ir2
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|ir2
expr_stmt|;
name|CodecReader
name|sr
init|=
name|getOnlySegmentReader
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|CompressingStoredFieldsReader
name|reader
init|=
operator|(
name|CompressingStoredFieldsReader
operator|)
name|sr
operator|.
name|getFieldsReader
argument_list|()
decl_stmt|;
comment|// we could get lucky, and have zero, but typically one.
name|assertTrue
argument_list|(
name|reader
operator|.
name|getNumDirtyChunks
argument_list|()
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
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
block|}
block|}
end_class

end_unit
