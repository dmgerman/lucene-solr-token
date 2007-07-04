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
name|IOException
import|;
end_import

begin_comment
comment|/** Base implementation class for buffered {@link IndexInput}. */
end_comment

begin_class
DECL|class|BufferedIndexInput
specifier|public
specifier|abstract
class|class
name|BufferedIndexInput
extends|extends
name|IndexInput
block|{
comment|/** Default buffer size */
DECL|field|BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
init|=
name|BUFFER_SIZE
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
init|=
literal|0
decl_stmt|;
comment|// position in file of buffer
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
init|=
literal|0
decl_stmt|;
comment|// end of valid bytes
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
init|=
literal|0
decl_stmt|;
comment|// next byte to read
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bufferPosition
operator|>=
name|bufferLength
condition|)
name|refill
argument_list|()
expr_stmt|;
return|return
name|buffer
index|[
name|bufferPosition
operator|++
index|]
return|;
block|}
DECL|method|BufferedIndexInput
specifier|public
name|BufferedIndexInput
parameter_list|()
block|{}
comment|/** Inits BufferedIndexInput with a specific bufferSize */
DECL|method|BufferedIndexInput
specifier|public
name|BufferedIndexInput
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|checkBufferSize
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
block|}
comment|/** Change the buffer size used by this IndexInput */
DECL|method|setBufferSize
specifier|public
name|void
name|setBufferSize
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
assert|assert
name|buffer
operator|==
literal|null
operator|||
name|bufferSize
operator|==
name|buffer
operator|.
name|length
assert|;
if|if
condition|(
name|newSize
operator|!=
name|bufferSize
condition|)
block|{
name|checkBufferSize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
name|bufferSize
operator|=
name|newSize
expr_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
comment|// Resize the existing buffer and carefully save as
comment|// many bytes as possible starting from the current
comment|// bufferPosition
name|byte
index|[]
name|newBuffer
init|=
operator|new
name|byte
index|[
name|newSize
index|]
decl_stmt|;
specifier|final
name|int
name|leftInBuffer
init|=
name|bufferLength
operator|-
name|bufferPosition
decl_stmt|;
specifier|final
name|int
name|numToCopy
decl_stmt|;
if|if
condition|(
name|leftInBuffer
operator|>
name|newSize
condition|)
name|numToCopy
operator|=
name|newSize
expr_stmt|;
else|else
name|numToCopy
operator|=
name|leftInBuffer
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|bufferStart
operator|+=
name|bufferPosition
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
name|numToCopy
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
block|}
block|}
comment|/** Returns buffer size.  @see #setBufferSize */
DECL|method|getBufferSize
specifier|public
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|bufferSize
return|;
block|}
DECL|method|checkBufferSize
specifier|private
name|void
name|checkBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
if|if
condition|(
name|bufferSize
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bufferSize must be greater than 0 (got "
operator|+
name|bufferSize
operator|+
literal|")"
argument_list|)
throw|;
block|}
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|boolean
name|useBuffer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|len
operator|<=
operator|(
name|bufferLength
operator|-
name|bufferPosition
operator|)
condition|)
block|{
comment|// the buffer contains enough data to satisfy this request
if|if
condition|(
name|len
operator|>
literal|0
condition|)
comment|// to allow b to be null if len is 0...
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bufferPosition
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
comment|// the buffer does not have enough data. First serve all we've got.
name|int
name|available
init|=
name|bufferLength
operator|-
name|bufferPosition
decl_stmt|;
if|if
condition|(
name|available
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|available
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|available
expr_stmt|;
name|len
operator|-=
name|available
expr_stmt|;
name|bufferPosition
operator|+=
name|available
expr_stmt|;
block|}
comment|// and now, read the remaining 'len' bytes:
if|if
condition|(
name|useBuffer
operator|&&
name|len
operator|<
name|bufferSize
condition|)
block|{
comment|// If the amount left to read is small enough, and
comment|// we are allowed to use our buffer, do it in the usual
comment|// buffered way: fill the buffer and copy from it:
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|bufferLength
operator|<
name|len
condition|)
block|{
comment|// Throw an exception when refill() could not read len bytes:
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bufferPosition
operator|=
name|len
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// The amount left to read is larger than the buffer
comment|// or we've been asked to not use our buffer -
comment|// there's no performance reason not to read it all
comment|// at once. Note that unlike the previous code of
comment|// this function, there is no need to do a seek
comment|// here, because there's no need to reread what we
comment|// had in the buffer.
name|long
name|after
init|=
name|bufferStart
operator|+
name|bufferPosition
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|after
operator|>
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
name|readInternal
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bufferStart
operator|=
name|after
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
literal|0
expr_stmt|;
comment|// trigger refill() on read
block|}
block|}
block|}
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|bufferStart
operator|+
name|bufferPosition
decl_stmt|;
name|long
name|end
init|=
name|start
operator|+
name|bufferSize
decl_stmt|;
if|if
condition|(
name|end
operator|>
name|length
argument_list|()
condition|)
comment|// don't read past EOF
name|end
operator|=
name|length
argument_list|()
expr_stmt|;
name|bufferLength
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferLength
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|bufferSize
index|]
expr_stmt|;
comment|// allocate buffer lazily
name|seekInternal
argument_list|(
name|bufferStart
argument_list|)
expr_stmt|;
block|}
name|readInternal
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
name|bufferStart
operator|=
name|start
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Expert: implements buffer refill.  Reads bytes from the current position    * in the input.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param length the number of bytes to read    */
DECL|method|readInternal
specifier|protected
specifier|abstract
name|void
name|readInternal
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
function_decl|;
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|bufferStart
operator|&&
name|pos
operator|<
operator|(
name|bufferStart
operator|+
name|bufferLength
operator|)
condition|)
name|bufferPosition
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
name|bufferStart
argument_list|)
expr_stmt|;
comment|// seek within buffer
else|else
block|{
name|bufferStart
operator|=
name|pos
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
literal|0
expr_stmt|;
comment|// trigger refill() on read()
name|seekInternal
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Expert: implements seek.  Sets current position in this file, where the    * next {@link #readInternal(byte[],int,int)} will occur.    * @see #readInternal(byte[],int,int)    */
DECL|method|seekInternal
specifier|protected
specifier|abstract
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|BufferedIndexInput
name|clone
init|=
operator|(
name|BufferedIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
name|clone
operator|.
name|bufferLength
operator|=
literal|0
expr_stmt|;
name|clone
operator|.
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|clone
operator|.
name|bufferStart
operator|=
name|getFilePointer
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

