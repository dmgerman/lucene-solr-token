begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SeekableByteChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_comment
comment|// javadoc @link
end_comment

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
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
name|Future
import|;
end_import

begin_comment
comment|/** A straightforward implementation of {@link FSDirectory}  *  using {@link Files#newByteChannel(Path, java.nio.file.OpenOption...)}.    *  However, this class has  *  poor concurrent performance (multiple threads will  *  bottleneck) as it synchronizes when multiple threads  *  read from the same file.  It's usually better to use  *  {@link NIOFSDirectory} or {@link MMapDirectory} instead.  *<p>  *<b>NOTE:</b> Accessing this class either directly or  * indirectly from a thread while it's interrupted can close the  * underlying file descriptor immediately if at the same time the thread is  * blocked on IO. The file descriptor will remain closed and subsequent access  * to {@link SimpleFSDirectory} will throw a {@link ClosedChannelException}. If  * your application uses either {@link Thread#interrupt()} or  * {@link Future#cancel(boolean)} you should use the legacy {@code RAFDirectory}  * from the Lucene {@code misc} module in favor of {@link SimpleFSDirectory}.  *</p>  */
end_comment

begin_class
DECL|class|SimpleFSDirectory
specifier|public
class|class
name|SimpleFSDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new SimpleFSDirectory for the named location.    *  The directory is created at the named location if it does not yet exist.    *    * @param path the path of the directory    * @param lockFactory the lock factory to use    * @throws IOException if there is a low-level I/O error    */
DECL|method|SimpleFSDirectory
specifier|public
name|SimpleFSDirectory
parameter_list|(
name|Path
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new SimpleFSDirectory for the named location and {@link FSLockFactory#getDefault()}.    *  The directory is created at the named location if it does not yet exist.    *    * @param path the path of the directory    * @throws IOException if there is a low-level I/O error    */
DECL|method|SimpleFSDirectory
specifier|public
name|SimpleFSDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
name|FSLockFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an IndexInput for the file with the given name. */
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|ensureCanRead
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|SeekableByteChannel
name|channel
init|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|path
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleFSIndexInput
argument_list|(
literal|"SimpleFSIndexInput(path=\""
operator|+
name|path
operator|+
literal|"\")"
argument_list|,
name|channel
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**    * Reads bytes with {@link SeekableByteChannel#read(ByteBuffer)}    */
DECL|class|SimpleFSIndexInput
specifier|static
specifier|final
class|class
name|SimpleFSIndexInput
extends|extends
name|BufferedIndexInput
block|{
comment|/**      * The maximum chunk size for reads of 16384 bytes.      */
DECL|field|CHUNK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|CHUNK_SIZE
init|=
literal|16384
decl_stmt|;
comment|/** the channel we will read from */
DECL|field|channel
specifier|protected
specifier|final
name|SeekableByteChannel
name|channel
decl_stmt|;
comment|/** is this instance a clone and hence does not own the file to close it */
DECL|field|isClone
name|boolean
name|isClone
init|=
literal|false
decl_stmt|;
comment|/** start offset: non-zero in the slice case */
DECL|field|off
specifier|protected
specifier|final
name|long
name|off
decl_stmt|;
comment|/** end offset (start+length) */
DECL|field|end
specifier|protected
specifier|final
name|long
name|end
decl_stmt|;
DECL|field|byteBuf
specifier|private
name|ByteBuffer
name|byteBuf
decl_stmt|;
comment|// wraps the buffer for NIO
DECL|method|SimpleFSIndexInput
specifier|public
name|SimpleFSIndexInput
parameter_list|(
name|String
name|resourceDesc
parameter_list|,
name|SeekableByteChannel
name|channel
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|resourceDesc
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|off
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|channel
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
DECL|method|SimpleFSIndexInput
specifier|public
name|SimpleFSIndexInput
parameter_list|(
name|String
name|resourceDesc
parameter_list|,
name|SeekableByteChannel
name|channel
parameter_list|,
name|long
name|off
parameter_list|,
name|long
name|length
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|resourceDesc
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|off
operator|+
name|length
expr_stmt|;
name|this
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|isClone
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SimpleFSIndexInput
name|clone
parameter_list|()
block|{
name|SimpleFSIndexInput
name|clone
init|=
operator|(
name|SimpleFSIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
return|return
name|clone
return|;
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
name|long
name|offset
parameter_list|,
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
argument_list|()
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
literal|" out of bounds: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
operator|+
literal|",fileLength="
operator|+
name|this
operator|.
name|length
argument_list|()
operator|+
literal|": "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
operator|new
name|SimpleFSIndexInput
argument_list|(
name|getFullSliceDescription
argument_list|(
name|sliceDescription
argument_list|)
argument_list|,
name|channel
argument_list|,
name|off
operator|+
name|offset
argument_list|,
name|length
argument_list|,
name|getBufferSize
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
specifier|final
name|long
name|length
parameter_list|()
block|{
return|return
name|end
operator|-
name|off
return|;
block|}
annotation|@
name|Override
DECL|method|newBuffer
specifier|protected
name|void
name|newBuffer
parameter_list|(
name|byte
index|[]
name|newBuffer
parameter_list|)
block|{
name|super
operator|.
name|newBuffer
argument_list|(
name|newBuffer
argument_list|)
expr_stmt|;
name|byteBuf
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|newBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readInternal
specifier|protected
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
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ByteBuffer
name|bb
decl_stmt|;
comment|// Determine the ByteBuffer we should use
if|if
condition|(
name|b
operator|==
name|buffer
condition|)
block|{
comment|// Use our own pre-wrapped byteBuf:
assert|assert
name|byteBuf
operator|!=
literal|null
assert|;
name|bb
operator|=
name|byteBuf
expr_stmt|;
name|byteBuf
operator|.
name|clear
argument_list|()
operator|.
name|position
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bb
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|channel
init|)
block|{
name|long
name|pos
init|=
name|getFilePointer
argument_list|()
operator|+
name|off
decl_stmt|;
if|if
condition|(
name|pos
operator|+
name|len
operator|>
name|end
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
try|try
block|{
name|channel
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|int
name|readLength
init|=
name|len
decl_stmt|;
while|while
condition|(
name|readLength
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|toRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|CHUNK_SIZE
argument_list|,
name|readLength
argument_list|)
decl_stmt|;
name|bb
operator|.
name|limit
argument_list|(
name|bb
operator|.
name|position
argument_list|()
operator|+
name|toRead
argument_list|)
expr_stmt|;
assert|assert
name|bb
operator|.
name|remaining
argument_list|()
operator|==
name|toRead
assert|;
specifier|final
name|int
name|i
init|=
name|channel
operator|.
name|read
argument_list|(
name|bb
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
comment|// be defensive here, even though we checked before hand, something could have changed
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
operator|+
literal|" off: "
operator|+
name|offset
operator|+
literal|" len: "
operator|+
name|len
operator|+
literal|" pos: "
operator|+
name|pos
operator|+
literal|" chunkLen: "
operator|+
name|toRead
operator|+
literal|" end: "
operator|+
name|end
argument_list|)
throw|;
block|}
assert|assert
name|i
operator|>
literal|0
operator|:
literal|"SeekableByteChannel.read with non zero-length bb.remaining() must always read at least one byte (Channel is in blocking mode, see spec of ReadableByteChannel)"
assert|;
name|pos
operator|+=
name|i
expr_stmt|;
name|readLength
operator|-=
name|i
expr_stmt|;
block|}
assert|assert
name|readLength
operator|==
literal|0
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|+
literal|": "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
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
operator|>
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: pos="
operator|+
name|pos
operator|+
literal|" vs length="
operator|+
name|length
argument_list|()
operator|+
literal|": "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

