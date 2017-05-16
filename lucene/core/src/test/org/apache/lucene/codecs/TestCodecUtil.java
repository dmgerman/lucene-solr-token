begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|index
operator|.
name|CorruptIndexException
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
name|BufferedChecksumIndexInput
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
name|ChecksumIndexInput
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
name|store
operator|.
name|RAMFile
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
name|RAMInputStream
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
name|RAMOutputStream
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
name|StringHelper
import|;
end_import

begin_comment
comment|/** tests for codecutil methods */
end_comment

begin_class
DECL|class|TestCodecUtil
specifier|public
class|class
name|TestCodecUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|testHeaderLength
specifier|public
name|void
name|testHeaderLength
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
literal|"FooBar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"this is the data"
argument_list|,
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testWriteTooLongHeader
specifier|public
name|void
name|testWriteTooLongHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|tooLong
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
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|tooLong
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|tooLong
operator|.
name|toString
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWriteNonAsciiHeader
specifier|public
name|void
name|testWriteNonAsciiHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"\u1234"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadHeaderWrongMagic
specifier|public
name|void
name|testReadHeaderWrongMagic
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|CorruptIndexException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
literal|"bogus"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testChecksumEntireFile
specifier|public
name|void
name|testChecksumEntireFile
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCheckFooterValid
specifier|public
name|void
name|testCheckFooterValid
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|ChecksumIndexInput
name|input
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|Exception
name|mine
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"fake exception"
argument_list|)
decl_stmt|;
name|RuntimeException
name|expected
init|=
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|mine
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fake exception"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Throwable
name|suppressed
index|[]
init|=
name|expected
operator|.
name|getSuppressed
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suppressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|suppressed
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum passed"
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCheckFooterValidAtFooter
specifier|public
name|void
name|testCheckFooterValidAtFooter
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|ChecksumIndexInput
name|input
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"this is the data"
argument_list|,
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|Exception
name|mine
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"fake exception"
argument_list|)
decl_stmt|;
name|RuntimeException
name|expected
init|=
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|mine
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fake exception"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Throwable
name|suppressed
index|[]
init|=
name|expected
operator|.
name|getSuppressed
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suppressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|suppressed
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum passed"
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCheckFooterValidPastFooter
specifier|public
name|void
name|testCheckFooterValidPastFooter
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|ChecksumIndexInput
name|input
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"this is the data"
argument_list|,
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
comment|// bogusly read a byte too far (can happen)
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|Exception
name|mine
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"fake exception"
argument_list|)
decl_stmt|;
name|RuntimeException
name|expected
init|=
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|mine
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fake exception"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Throwable
name|suppressed
index|[]
init|=
name|expected
operator|.
name|getSuppressed
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suppressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|suppressed
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum status indeterminate"
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCheckFooterInvalid
specifier|public
name|void
name|testCheckFooterInvalid
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|CodecUtil
operator|.
name|FOOTER_MAGIC
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
literal|1234567
argument_list|)
expr_stmt|;
comment|// write a bogus checksum
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|ChecksumIndexInput
name|input
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"this is the data"
argument_list|,
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|Exception
name|mine
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"fake exception"
argument_list|)
decl_stmt|;
name|RuntimeException
name|expected
init|=
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|mine
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fake exception"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Throwable
name|suppressed
index|[]
init|=
name|expected
operator|.
name|getSuppressed
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suppressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|suppressed
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum failed"
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSegmentHeaderLength
specifier|public
name|void
name|testSegmentHeaderLength
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|output
argument_list|,
literal|"FooBar"
argument_list|,
literal|5
argument_list|,
name|StringHelper
operator|.
name|randomId
argument_list|()
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"this is the data"
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|indexHeaderLength
argument_list|(
literal|"FooBar"
argument_list|,
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"this is the data"
argument_list|,
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testWriteTooLongSuffix
specifier|public
name|void
name|testWriteTooLongSuffix
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|tooLong
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
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|tooLong
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|output
argument_list|,
literal|"foobar"
argument_list|,
literal|5
argument_list|,
name|StringHelper
operator|.
name|randomId
argument_list|()
argument_list|,
name|tooLong
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWriteVeryLongSuffix
specifier|public
name|void
name|testWriteVeryLongSuffix
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|justLongEnough
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
literal|255
condition|;
name|i
operator|++
control|)
block|{
name|justLongEnough
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|id
init|=
name|StringHelper
operator|.
name|randomId
argument_list|()
decl_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|output
argument_list|,
literal|"foobar"
argument_list|,
literal|5
argument_list|,
name|id
argument_list|,
name|justLongEnough
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|input
argument_list|,
literal|"foobar"
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
name|id
argument_list|,
name|justLongEnough
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|CodecUtil
operator|.
name|indexHeaderLength
argument_list|(
literal|"foobar"
argument_list|,
name|justLongEnough
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testWriteNonAsciiSuffix
specifier|public
name|void
name|testWriteNonAsciiSuffix
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|output
argument_list|,
literal|"foobar"
argument_list|,
literal|5
argument_list|,
name|StringHelper
operator|.
name|randomId
argument_list|()
argument_list|,
literal|"\u1234"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadBogusCRC
specifier|public
name|void
name|testReadBogusCRC
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
comment|// bad
name|output
operator|.
name|writeLong
argument_list|(
literal|1L
operator|<<
literal|32
argument_list|)
expr_stmt|;
comment|// bad
name|output
operator|.
name|writeLong
argument_list|(
operator|-
operator|(
literal|1L
operator|<<
literal|32
operator|)
argument_list|)
expr_stmt|;
comment|// bad
name|output
operator|.
name|writeLong
argument_list|(
operator|(
literal|1L
operator|<<
literal|32
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// ok
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
comment|// read 3 bogus values
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|expectThrows
argument_list|(
name|CorruptIndexException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|readCRC
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|// good value
name|CodecUtil
operator|.
name|readCRC
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|testWriteBogusCRC
specifier|public
name|void
name|testWriteBogusCRC
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
specifier|final
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|AtomicLong
name|fakeChecksum
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// wrap the index input where we control the checksum for mocking
name|IndexOutput
name|fakeOutput
init|=
operator|new
name|IndexOutput
argument_list|(
literal|"fake"
argument_list|,
literal|"fake"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|output
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fakeChecksum
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|fakeChecksum
operator|.
name|set
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
comment|// bad
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeCRC
argument_list|(
name|fakeOutput
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|fakeChecksum
operator|.
name|set
argument_list|(
literal|1L
operator|<<
literal|32
argument_list|)
expr_stmt|;
comment|// bad
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeCRC
argument_list|(
name|fakeOutput
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|fakeChecksum
operator|.
name|set
argument_list|(
operator|-
operator|(
literal|1L
operator|<<
literal|32
operator|)
argument_list|)
expr_stmt|;
comment|// bad
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CodecUtil
operator|.
name|writeCRC
argument_list|(
name|fakeOutput
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|fakeChecksum
operator|.
name|set
argument_list|(
operator|(
literal|1L
operator|<<
literal|32
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// ok
name|CodecUtil
operator|.
name|writeCRC
argument_list|(
name|fakeOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|testTruncatedFileThrowsCorruptIndexException
specifier|public
name|void
name|testTruncatedFileThrowsCorruptIndexException
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
operator|new
name|RAMInputStream
argument_list|(
literal|"file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|CorruptIndexException
name|e
init|=
name|expectThrows
argument_list|(
name|CorruptIndexException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"misplaced codec footer (file truncated?): length=0 but footerLength==16 (resource=RAMInputStream(name=file))"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|CorruptIndexException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"misplaced codec footer (file truncated?): length=0 but footerLength==16 (resource=RAMInputStream(name=file))"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

