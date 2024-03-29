begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|Locale
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
name|DataInput
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
name|DataOutput
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
name|BytesRefBuilder
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

begin_class
DECL|class|SimpleTextUtil
class|class
name|SimpleTextUtil
block|{
DECL|field|NEWLINE
specifier|public
specifier|final
specifier|static
name|byte
name|NEWLINE
init|=
literal|10
decl_stmt|;
DECL|field|ESCAPE
specifier|public
specifier|final
specifier|static
name|byte
name|ESCAPE
init|=
literal|92
decl_stmt|;
DECL|field|CHECKSUM
specifier|final
specifier|static
name|BytesRef
name|CHECKSUM
init|=
operator|new
name|BytesRef
argument_list|(
literal|"checksum "
argument_list|)
decl_stmt|;
DECL|method|write
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|,
name|BytesRefBuilder
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|scratch
operator|.
name|copyChars
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|BytesRef
name|b
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
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|byte
name|bx
init|=
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|bx
operator|==
name|NEWLINE
operator|||
name|bx
operator|==
name|ESCAPE
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|ESCAPE
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
name|bx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeNewline
specifier|public
specifier|static
name|void
name|writeNewline
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|NEWLINE
argument_list|)
expr_stmt|;
block|}
DECL|method|readLine
specifier|public
specifier|static
name|void
name|readLine
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|BytesRefBuilder
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|byte
name|b
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|grow
argument_list|(
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|==
name|ESCAPE
condition|)
block|{
name|scratch
operator|.
name|setByteAt
argument_list|(
name|upto
operator|++
argument_list|,
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|b
operator|==
name|NEWLINE
condition|)
block|{
break|break;
block|}
else|else
block|{
name|scratch
operator|.
name|setByteAt
argument_list|(
name|upto
operator|++
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|scratch
operator|.
name|setLength
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
DECL|method|writeChecksum
specifier|public
specifier|static
name|void
name|writeChecksum
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|BytesRefBuilder
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Pad with zeros so different checksum values use the
comment|// same number of bytes
comment|// (BaseIndexFileFormatTestCase.testMergeStability cares):
name|String
name|checksum
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%020d"
argument_list|,
name|out
operator|.
name|getChecksum
argument_list|()
argument_list|)
decl_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|CHECKSUM
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|checksum
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFooter
specifier|public
specifier|static
name|void
name|checkFooter
parameter_list|(
name|ChecksumIndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|String
name|expectedChecksum
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%020d"
argument_list|,
name|input
operator|.
name|getChecksum
argument_list|()
argument_list|)
decl_stmt|;
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|CHECKSUM
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"SimpleText failure: expected checksum line but got "
operator|+
name|scratch
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|String
name|actualChecksum
init|=
operator|new
name|BytesRef
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|CHECKSUM
operator|.
name|length
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|CHECKSUM
operator|.
name|length
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|expectedChecksum
operator|.
name|equals
argument_list|(
name|actualChecksum
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"SimpleText checksum failure: "
operator|+
name|actualChecksum
operator|+
literal|" != "
operator|+
name|expectedChecksum
argument_list|,
name|input
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|length
argument_list|()
operator|!=
name|input
operator|.
name|getFilePointer
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Unexpected stuff at the end of file, please be careful with your text editor!"
argument_list|,
name|input
argument_list|)
throw|;
block|}
block|}
comment|/** Inverse of {@link BytesRef#toString}. */
DECL|method|fromBytesRefString
specifier|public
specifier|static
name|BytesRef
name|fromBytesRefString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"string "
operator|+
name|s
operator|+
literal|" was not created from BytesRef.toString?"
argument_list|)
throw|;
block|}
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'['
operator|||
name|s
operator|.
name|charAt
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"string "
operator|+
name|s
operator|+
literal|" was not created from BytesRef.toString?"
argument_list|)
throw|;
block|}
name|String
index|[]
name|parts
init|=
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|parts
operator|.
name|length
index|]
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
name|parts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

