begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
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

begin_class
DECL|class|ReusedBufferedIndexOutput
specifier|public
specifier|abstract
class|class
name|ReusedBufferedIndexOutput
extends|extends
name|IndexOutput
block|{
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
specifier|protected
name|byte
index|[]
name|buffer
decl_stmt|;
comment|/** position in the file of buffer */
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
init|=
literal|0
decl_stmt|;
comment|/** end of valid bytes */
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
init|=
literal|0
decl_stmt|;
comment|/** next byte to write */
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
init|=
literal|0
decl_stmt|;
comment|/** total length of the file */
DECL|field|fileLength
specifier|private
name|long
name|fileLength
init|=
literal|0
decl_stmt|;
DECL|method|ReusedBufferedIndexOutput
specifier|public
name|ReusedBufferedIndexOutput
parameter_list|()
block|{
name|this
argument_list|(
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|ReusedBufferedIndexOutput
specifier|public
name|ReusedBufferedIndexOutput
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
name|buffer
operator|=
name|BufferStore
operator|.
name|takeBuffer
argument_list|(
name|this
operator|.
name|bufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|getBufferStart
specifier|protected
name|long
name|getBufferStart
parameter_list|()
block|{
return|return
name|bufferStart
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
comment|/** Write the buffered bytes to cache */
DECL|method|flushBufferToCache
specifier|private
name|void
name|flushBufferToCache
parameter_list|()
throws|throws
name|IOException
block|{
name|writeInternal
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
name|bufferStart
operator|+=
name|bufferLength
expr_stmt|;
name|bufferLength
operator|=
literal|0
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|flushInternal
specifier|protected
specifier|abstract
name|void
name|flushInternal
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBufferToCache
argument_list|()
expr_stmt|;
name|flushInternal
argument_list|()
expr_stmt|;
block|}
DECL|method|closeInternal
specifier|protected
specifier|abstract
name|void
name|closeInternal
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBufferToCache
argument_list|()
expr_stmt|;
name|closeInternal
argument_list|()
expr_stmt|;
name|BufferStore
operator|.
name|putBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
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
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
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
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fileLength
return|;
block|}
annotation|@
name|Override
DECL|method|writeByte
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
if|if
condition|(
name|bufferPosition
operator|>=
name|bufferSize
condition|)
block|{
name|flushBufferToCache
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getFilePointer
argument_list|()
operator|>=
name|fileLength
condition|)
block|{
name|fileLength
operator|++
expr_stmt|;
block|}
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|=
name|b
expr_stmt|;
if|if
condition|(
name|bufferPosition
operator|>
name|bufferLength
condition|)
block|{
name|bufferLength
operator|=
name|bufferPosition
expr_stmt|;
block|}
block|}
comment|/**    * Expert: implements buffer flushing to cache. Writes bytes to the current    * position in the output.    *     * @param b    *          the array of bytes to write    * @param offset    *          the offset in the array of bytes to write    * @param length    *          the number of bytes to write    */
DECL|method|writeInternal
specifier|protected
specifier|abstract
name|void
name|writeInternal
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
annotation|@
name|Override
DECL|method|writeBytes
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
if|if
condition|(
name|getFilePointer
argument_list|()
operator|+
name|length
operator|>
name|fileLength
condition|)
block|{
name|fileLength
operator|=
name|getFilePointer
argument_list|()
operator|+
name|length
expr_stmt|;
block|}
if|if
condition|(
name|length
operator|<=
name|bufferSize
operator|-
name|bufferPosition
condition|)
block|{
comment|// the buffer contains enough space to satisfy this request
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
comment|// to allow b to be null if len is 0...
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|bufferPosition
operator|+=
name|length
expr_stmt|;
if|if
condition|(
name|bufferPosition
operator|>
name|bufferLength
condition|)
block|{
name|bufferLength
operator|=
name|bufferPosition
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// the buffer does not have enough space. First buffer all we've got.
name|int
name|available
init|=
name|bufferSize
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
name|b
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|available
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|available
expr_stmt|;
name|length
operator|-=
name|available
expr_stmt|;
name|bufferPosition
operator|=
name|bufferSize
expr_stmt|;
name|bufferLength
operator|=
name|bufferSize
expr_stmt|;
block|}
name|flushBufferToCache
argument_list|()
expr_stmt|;
comment|// and now, write the remaining 'length' bytes:
if|if
condition|(
name|length
operator|<
name|bufferSize
condition|)
block|{
comment|// If the amount left to write is small enough do it in the usual
comment|// buffered way:
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bufferPosition
operator|=
name|length
expr_stmt|;
name|bufferLength
operator|=
name|length
expr_stmt|;
block|}
else|else
block|{
comment|// The amount left to write is larger than the buffer
comment|// there's no performance reason not to write it all
comment|// at once.
name|writeInternal
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bufferStart
operator|+=
name|length
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
throw|throw
operator|new
name|CloneNotSupportedException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

