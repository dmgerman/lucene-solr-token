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
name|io
operator|.
name|EOFException
import|;
end_import

begin_comment
comment|/** A memory-resident {@link IndexInput} implementation.   *    *  @lucene.internal */
end_comment

begin_class
DECL|class|RAMInputStream
specifier|public
class|class
name|RAMInputStream
extends|extends
name|IndexInput
implements|implements
name|Cloneable
block|{
DECL|field|BUFFER_SIZE
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
name|RAMOutputStream
operator|.
name|BUFFER_SIZE
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|RAMFile
name|file
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|currentBuffer
specifier|private
name|byte
index|[]
name|currentBuffer
decl_stmt|;
DECL|field|currentBufferIndex
specifier|private
name|int
name|currentBufferIndex
decl_stmt|;
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
decl_stmt|;
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
decl_stmt|;
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
decl_stmt|;
DECL|method|RAMInputStream
specifier|public
name|RAMInputStream
parameter_list|(
name|String
name|name
parameter_list|,
name|RAMFile
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|name
argument_list|,
name|f
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|RAMInputStream
name|RAMInputStream
parameter_list|(
name|String
name|name
parameter_list|,
name|RAMFile
name|f
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"RAMInputStream(name="
operator|+
name|name
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|f
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
if|if
condition|(
name|length
operator|/
name|BUFFER_SIZE
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"RAMInputStream too large length="
operator|+
name|length
operator|+
literal|": "
operator|+
name|name
argument_list|)
throw|;
block|}
comment|// make sure that we switch to the
comment|// first needed buffer lazily
name|currentBufferIndex
operator|=
operator|-
literal|1
expr_stmt|;
name|currentBuffer
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
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
block|{
name|currentBufferIndex
operator|++
expr_stmt|;
name|switchCurrentBuffer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|currentBuffer
index|[
name|bufferPosition
operator|++
index|]
return|;
block|}
annotation|@
name|Override
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
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|bufferPosition
operator|>=
name|bufferLength
condition|)
block|{
name|currentBufferIndex
operator|++
expr_stmt|;
name|switchCurrentBuffer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|int
name|remainInBuffer
init|=
name|bufferLength
operator|-
name|bufferPosition
decl_stmt|;
name|int
name|bytesToCopy
init|=
name|len
operator|<
name|remainInBuffer
condition|?
name|len
else|:
name|remainInBuffer
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|currentBuffer
argument_list|,
name|bufferPosition
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|bytesToCopy
expr_stmt|;
name|len
operator|-=
name|bytesToCopy
expr_stmt|;
name|bufferPosition
operator|+=
name|bytesToCopy
expr_stmt|;
block|}
block|}
DECL|method|switchCurrentBuffer
specifier|private
specifier|final
name|void
name|switchCurrentBuffer
parameter_list|(
name|boolean
name|enforceEOF
parameter_list|)
throws|throws
name|IOException
block|{
name|bufferStart
operator|=
operator|(
name|long
operator|)
name|BUFFER_SIZE
operator|*
operator|(
name|long
operator|)
name|currentBufferIndex
expr_stmt|;
if|if
condition|(
name|bufferStart
operator|>
name|length
operator|||
name|currentBufferIndex
operator|>=
name|file
operator|.
name|numBuffers
argument_list|()
condition|)
block|{
comment|// end of file reached, no more buffers left
if|if
condition|(
name|enforceEOF
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Force EOF if a read later takes place at this position
name|currentBufferIndex
operator|--
expr_stmt|;
name|bufferPosition
operator|=
name|BUFFER_SIZE
expr_stmt|;
block|}
block|}
else|else
block|{
name|currentBuffer
operator|=
name|file
operator|.
name|getBuffer
argument_list|(
name|currentBufferIndex
argument_list|)
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|long
name|buflen
init|=
name|length
operator|-
name|bufferStart
decl_stmt|;
name|bufferLength
operator|=
name|buflen
operator|>
name|BUFFER_SIZE
condition|?
name|BUFFER_SIZE
else|:
operator|(
name|int
operator|)
name|buflen
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|currentBufferIndex
operator|<
literal|0
condition|?
literal|0
else|:
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
annotation|@
name|Override
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
name|currentBuffer
operator|==
literal|null
operator|||
name|pos
operator|<
name|bufferStart
operator|||
name|pos
operator|>=
name|bufferStart
operator|+
name|BUFFER_SIZE
condition|)
block|{
name|currentBufferIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|/
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|switchCurrentBuffer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|BUFFER_SIZE
operator|*
operator|(
name|long
operator|)
name|file
operator|.
name|numBuffers
argument_list|()
condition|)
block|{
comment|// do not overwrite bufferPosition if EOF should be thrown on the next read
name|bufferPosition
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|%
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|length
argument_list|<
literal|0
operator|||
name|offset
operator|+
name|length
argument_list|>
name|this
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"slice() "
operator|+
name|sliceDescription
operator|+
literal|" out of bounds: "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
operator|new
name|RAMInputStream
argument_list|(
name|getFullSliceDescription
argument_list|(
name|sliceDescription
argument_list|)
argument_list|,
name|file
argument_list|,
name|offset
operator|+
name|length
argument_list|)
block|{
block|{
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
operator|<
literal|0L
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Seeking to negative position: "
operator|+
name|this
argument_list|)
throw|;
block|}
name|super
operator|.
name|seek
argument_list|(
name|pos
operator|+
name|offset
argument_list|)
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
name|super
operator|.
name|getFilePointer
argument_list|()
operator|-
name|offset
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|super
operator|.
name|length
argument_list|()
operator|-
name|offset
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|ofs
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|slice
argument_list|(
name|sliceDescription
argument_list|,
name|offset
operator|+
name|ofs
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

