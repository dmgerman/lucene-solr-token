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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
comment|/** Abstract base class for input from a file in a {@link Directory}.  A  * random-access input stream.  Used for all Lucene index input operations.  * @see Directory  * @see OutputStream  */
end_comment

begin_class
DECL|class|InputStream
specifier|public
specifier|abstract
class|class
name|InputStream
implements|implements
name|Cloneable
block|{
DECL|field|BUFFER_SIZE
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
name|OutputStream
operator|.
name|BUFFER_SIZE
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|chars
specifier|private
name|char
index|[]
name|chars
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
DECL|field|length
specifier|protected
name|long
name|length
decl_stmt|;
comment|// set by subclasses
comment|/** Reads and returns a single byte.    * @see OutputStream#writeByte(byte)    */
DECL|method|readByte
specifier|public
specifier|final
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
comment|/** Reads a specified number of bytes into an array at the specified offset.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @see OutputStream#writeBytes(byte[],int)    */
DECL|method|readBytes
specifier|public
specifier|final
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
if|if
condition|(
name|len
operator|<
name|BUFFER_SIZE
condition|)
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
name|len
condition|;
name|i
operator|++
control|)
comment|// read byte-by-byte
name|b
index|[
name|i
operator|+
name|offset
index|]
operator|=
operator|(
name|byte
operator|)
name|readByte
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// read all-at-once
name|long
name|start
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
name|seekInternal
argument_list|(
name|start
argument_list|)
expr_stmt|;
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
name|start
operator|+
name|len
expr_stmt|;
comment|// adjust stream variables
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
comment|/** Reads four bytes and returns an int.    * @see OutputStream#writeInt(int)    */
DECL|method|readInt
specifier|public
specifier|final
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
return|;
block|}
comment|/** Reads an int stored in variable-length format.  Reads between one and    * five bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    * @see OutputStream#writeVInt(int)    */
DECL|method|readVInt
specifier|public
specifier|final
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/** Reads eight bytes and returns a long.    * @see OutputStream#writeLong(long)    */
DECL|method|readLong
specifier|public
specifier|final
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|readInt
argument_list|()
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|readInt
argument_list|()
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
comment|/** Reads a long stored in variable-length format.  Reads between one and    * nine bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported. */
DECL|method|readVLong
specifier|public
specifier|final
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|long
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/** Reads a string.    * @see OutputStream#writeString(String)    */
DECL|method|readString
specifier|public
specifier|final
name|String
name|readString
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|chars
operator|==
literal|null
operator|||
name|length
operator|>
name|chars
operator|.
name|length
condition|)
name|chars
operator|=
operator|new
name|char
index|[
name|length
index|]
expr_stmt|;
name|readChars
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/** Reads UTF-8 encoded characters into an array.    * @param buffer the array to read characters into    * @param start the offset in the array to start storing characters    * @param length the number of characters to read    * @see OutputStream#writeChars(String,int,int)    */
DECL|method|readChars
specifier|public
specifier|final
name|void
name|readChars
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|b
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0xE0
operator|)
operator|!=
literal|0xE0
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
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
name|BUFFER_SIZE
decl_stmt|;
if|if
condition|(
name|end
operator|>
name|length
condition|)
comment|// don't read past EOF
name|end
operator|=
name|length
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
operator|==
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
name|buffer
operator|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
expr_stmt|;
comment|// allocate buffer lazily
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
comment|/** Closes the stream to futher operations. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current position in this file, where the next read will    * occur.    * @see #seek(long)    */
DECL|method|getFilePointer
specifier|public
specifier|final
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
comment|/** Sets current position in this file, where the next read will occur.    * @see #getFilePointer()    */
DECL|method|seek
specifier|public
specifier|final
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
comment|/** The number of bytes in the file. */
DECL|method|length
specifier|public
specifier|final
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/** Returns a clone of this stream.    *    *<p>Clones of a stream access the same data, and are positioned at the same    * point as the stream they were cloned from.    *    *<p>Expert: Subclasses must ensure that clones may be positioned at    * different points in the input from each other and from the stream they    * were cloned from.    */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|InputStream
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|InputStream
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{}
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|clone
operator|.
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
block|}
name|clone
operator|.
name|chars
operator|=
literal|null
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

