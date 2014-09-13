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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|FileChannel
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

begin_comment
comment|// javadoc
end_comment

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
name|IOContext
operator|.
name|Context
import|;
end_import

begin_comment
comment|// TODO
end_comment

begin_comment
comment|//   - newer Linux kernel versions (after 2.6.29) have
end_comment

begin_comment
comment|//     improved MADV_SEQUENTIAL (and hopefully also
end_comment

begin_comment
comment|//     FADV_SEQUENTIAL) interaction with the buffer
end_comment

begin_comment
comment|//     cache; we should explore using that instead of direct
end_comment

begin_comment
comment|//     IO when context is merge
end_comment

begin_comment
comment|/**  * A {@link Directory} implementation for all Unixes that uses  * DIRECT I/O to bypass OS level IO caching during  * merging.  For all other cases (searching, writing) we delegate  * to the provided Directory instance.  *  *<p>See<a  * href="{@docRoot}/overview-summary.html#NativeUnixDirectory">Overview</a>  * for more details.  *  *<p>To use this you must compile  * NativePosixUtil.cpp (exposes Linux-specific APIs through  * JNI) for your platform, by running<code>ant  * build-native-unix</code>, and then putting the resulting  *<code>libNativePosixUtil.so</code> (from  *<code>lucene/build/native</code>) onto your dynamic  * linker search path.  *  *<p><b>WARNING</b>: this code is very new and quite easily  * could contain horrible bugs.  For example, here's one  * known issue: if you use seek in<code>IndexOutput</code>, and then  * write more than one buffer's worth of bytes, then the  * file will be wrong.  Lucene does not do this today (only writes  * small number of bytes after seek), but that may change.  *  *<p>This directory passes Solr and Lucene tests on Linux  * and OS X; other Unixes should work but have not been  * tested!  Use at your own risk.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NativeUnixDirectory
specifier|public
class|class
name|NativeUnixDirectory
extends|extends
name|FSDirectory
block|{
comment|// TODO: this is OS dependent, but likely 512 is the LCD
DECL|field|ALIGN
specifier|private
specifier|final
specifier|static
name|long
name|ALIGN
init|=
literal|512
decl_stmt|;
DECL|field|ALIGN_NOT_MASK
specifier|private
specifier|final
specifier|static
name|long
name|ALIGN_NOT_MASK
init|=
operator|~
operator|(
name|ALIGN
operator|-
literal|1
operator|)
decl_stmt|;
comment|/** Default buffer size before writing to disk (256 KB);    *  larger means less IO load but more RAM and direct    *  buffer storage space consumed during merging. */
DECL|field|DEFAULT_MERGE_BUFFER_SIZE
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MERGE_BUFFER_SIZE
init|=
literal|262144
decl_stmt|;
comment|/** Default min expected merge size before direct IO is    *  used (10 MB): */
DECL|field|DEFAULT_MIN_BYTES_DIRECT
specifier|public
specifier|final
specifier|static
name|long
name|DEFAULT_MIN_BYTES_DIRECT
init|=
literal|10
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|mergeBufferSize
specifier|private
specifier|final
name|int
name|mergeBufferSize
decl_stmt|;
DECL|field|minBytesDirect
specifier|private
specifier|final
name|long
name|minBytesDirect
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|Directory
name|delegate
decl_stmt|;
comment|/** Create a new NIOFSDirectory for the named location.    *     * @param path the path of the directory    * @param mergeBufferSize Size of buffer to use for    *    merging.  See {@link #DEFAULT_MERGE_BUFFER_SIZE}.    * @param minBytesDirect Merges, or files to be opened for    *   reading, smaller than this will    *   not use direct IO.  See {@link    *   #DEFAULT_MIN_BYTES_DIRECT}    * @param delegate fallback Directory for non-merges    * @throws IOException If there is a low-level I/O error    */
DECL|method|NativeUnixDirectory
specifier|public
name|NativeUnixDirectory
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|mergeBufferSize
parameter_list|,
name|long
name|minBytesDirect
parameter_list|,
name|Directory
name|delegate
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|delegate
operator|.
name|getLockFactory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|mergeBufferSize
operator|&
name|ALIGN
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"mergeBufferSize must be 0 mod "
operator|+
name|ALIGN
operator|+
literal|" (got: "
operator|+
name|mergeBufferSize
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|mergeBufferSize
operator|=
name|mergeBufferSize
expr_stmt|;
name|this
operator|.
name|minBytesDirect
operator|=
name|minBytesDirect
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
comment|/** Create a new NIOFSDirectory for the named location.    *     * @param path the path of the directory    * @param delegate fallback Directory for non-merges    * @throws IOException If there is a low-level I/O error    */
DECL|method|NativeUnixDirectory
specifier|public
name|NativeUnixDirectory
parameter_list|(
name|Path
name|path
parameter_list|,
name|Directory
name|delegate
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
name|DEFAULT_MERGE_BUFFER_SIZE
argument_list|,
name|DEFAULT_MIN_BYTES_DIRECT
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|context
operator|.
name|context
operator|!=
name|Context
operator|.
name|MERGE
operator|||
name|context
operator|.
name|mergeInfo
operator|.
name|estimatedMergeBytes
operator|<
name|minBytesDirect
operator|||
name|fileLength
argument_list|(
name|name
argument_list|)
operator|<
name|minBytesDirect
condition|)
block|{
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NativeUnixIndexInput
argument_list|(
name|getDirectory
argument_list|()
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|,
name|mergeBufferSize
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
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
if|if
condition|(
name|context
operator|.
name|context
operator|!=
name|Context
operator|.
name|MERGE
operator|||
name|context
operator|.
name|mergeInfo
operator|.
name|estimatedMergeBytes
operator|<
name|minBytesDirect
condition|)
block|{
return|return
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
name|ensureCanWrite
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|NativeUnixIndexOutput
argument_list|(
name|getDirectory
argument_list|()
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|,
name|mergeBufferSize
argument_list|)
return|;
block|}
block|}
DECL|class|NativeUnixIndexOutput
specifier|private
specifier|final
specifier|static
class|class
name|NativeUnixIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|buffer
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|fos
specifier|private
specifier|final
name|FileOutputStream
name|fos
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|FileChannel
name|channel
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
comment|//private final File path;
DECL|field|bufferPos
specifier|private
name|int
name|bufferPos
decl_stmt|;
DECL|field|filePos
specifier|private
name|long
name|filePos
decl_stmt|;
DECL|field|fileLength
specifier|private
name|long
name|fileLength
decl_stmt|;
DECL|field|isOpen
specifier|private
name|boolean
name|isOpen
decl_stmt|;
DECL|method|NativeUnixIndexOutput
specifier|public
name|NativeUnixIndexOutput
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|//this.path = path;
specifier|final
name|FileDescriptor
name|fd
init|=
name|NativePosixUtil
operator|.
name|open_direct
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|fd
argument_list|)
expr_stmt|;
comment|//fos = new FileOutputStream(path);
name|channel
operator|=
name|fos
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|buffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
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
name|isOpen
operator|=
literal|true
expr_stmt|;
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
assert|assert
name|bufferPos
operator|==
name|buffer
operator|.
name|position
argument_list|()
operator|:
literal|"bufferPos="
operator|+
name|bufferPos
operator|+
literal|" vs buffer.position()="
operator|+
name|buffer
operator|.
name|position
argument_list|()
assert|;
name|buffer
operator|.
name|put
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|bufferPos
operator|==
name|bufferSize
condition|)
block|{
name|dump
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|src
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
name|int
name|toWrite
init|=
name|len
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|left
init|=
name|bufferSize
operator|-
name|bufferPos
decl_stmt|;
if|if
condition|(
name|left
operator|<=
name|toWrite
condition|)
block|{
name|buffer
operator|.
name|put
argument_list|(
name|src
argument_list|,
name|offset
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|toWrite
operator|-=
name|left
expr_stmt|;
name|offset
operator|+=
name|left
expr_stmt|;
name|bufferPos
operator|=
name|bufferSize
expr_stmt|;
name|dump
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|put
argument_list|(
name|src
argument_list|,
name|offset
argument_list|,
name|toWrite
argument_list|)
expr_stmt|;
name|bufferPos
operator|+=
name|toWrite
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|//@Override
comment|//public void setLength() throws IOException {
comment|//   TODO -- how to impl this?  neither FOS nor
comment|//   FileChannel provides an API?
comment|//}
DECL|method|dump
specifier|private
name|void
name|dump
parameter_list|()
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
specifier|final
name|long
name|limit
init|=
name|filePos
operator|+
name|buffer
operator|.
name|limit
argument_list|()
decl_stmt|;
if|if
condition|(
name|limit
operator|>
name|fileLength
condition|)
block|{
comment|// this dump extends the file
name|fileLength
operator|=
name|limit
expr_stmt|;
block|}
else|else
block|{
comment|// we had seek'd back& wrote some changes
block|}
comment|// must always round to next block
name|buffer
operator|.
name|limit
argument_list|(
call|(
name|int
call|)
argument_list|(
operator|(
name|buffer
operator|.
name|limit
argument_list|()
operator|+
name|ALIGN
operator|-
literal|1
operator|)
operator|&
name|ALIGN_NOT_MASK
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|buffer
operator|.
name|limit
argument_list|()
operator|&
name|ALIGN_NOT_MASK
operator|)
operator|==
name|buffer
operator|.
name|limit
argument_list|()
operator|:
literal|"limit="
operator|+
name|buffer
operator|.
name|limit
argument_list|()
operator|+
literal|" vs "
operator|+
operator|(
name|buffer
operator|.
name|limit
argument_list|()
operator|&
name|ALIGN_NOT_MASK
operator|)
assert|;
assert|assert
operator|(
name|filePos
operator|&
name|ALIGN_NOT_MASK
operator|)
operator|==
name|filePos
assert|;
comment|//System.out.println(Thread.currentThread().getName() + ": dump to " + filePos + " limit=" + buffer.limit() + " fos=" + fos);
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|filePos
argument_list|)
expr_stmt|;
name|filePos
operator|+=
name|bufferPos
expr_stmt|;
name|bufferPos
operator|=
literal|0
expr_stmt|;
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//System.out.println("dump: done");
comment|// TODO: the case where we'd seek'd back, wrote an
comment|// entire buffer, we must here read the next buffer;
comment|// likely Lucene won't trip on this since we only
comment|// write smallish amounts on seeking back
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
name|filePos
operator|+
name|bufferPos
return|;
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this directory currently does not work at all!"
argument_list|)
throw|;
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
name|isOpen
condition|)
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|dump
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
comment|//System.out.println("direct close set len=" + fileLength + " vs " + channel.size() + " path=" + path);
name|channel
operator|.
name|truncate
argument_list|(
name|fileLength
argument_list|)
expr_stmt|;
comment|//System.out.println("  now: " + channel.size());
block|}
finally|finally
block|{
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//System.out.println("  final len=" + path.length());
block|}
block|}
block|}
block|}
block|}
block|}
DECL|class|NativeUnixIndexInput
specifier|private
specifier|final
specifier|static
class|class
name|NativeUnixIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|buffer
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|fis
specifier|private
specifier|final
name|FileInputStream
name|fis
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|FileChannel
name|channel
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|field|isOpen
specifier|private
name|boolean
name|isOpen
decl_stmt|;
DECL|field|isClone
specifier|private
name|boolean
name|isClone
decl_stmt|;
DECL|field|filePos
specifier|private
name|long
name|filePos
decl_stmt|;
DECL|field|bufferPos
specifier|private
name|int
name|bufferPos
decl_stmt|;
DECL|method|NativeUnixIndexInput
specifier|public
name|NativeUnixIndexInput
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"NativeUnixIndexInput(path=\""
operator|+
name|path
operator|+
literal|"\")"
argument_list|)
expr_stmt|;
specifier|final
name|FileDescriptor
name|fd
init|=
name|NativePosixUtil
operator|.
name|open_direct
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|channel
operator|=
name|fis
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|buffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|isOpen
operator|=
literal|true
expr_stmt|;
name|isClone
operator|=
literal|false
expr_stmt|;
name|filePos
operator|=
operator|-
name|bufferSize
expr_stmt|;
name|bufferPos
operator|=
name|bufferSize
expr_stmt|;
comment|//System.out.println("D open " + path + " this=" + this);
block|}
comment|// for clone
DECL|method|NativeUnixIndexInput
specifier|public
name|NativeUnixIndexInput
parameter_list|(
name|NativeUnixIndexInput
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|other
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fis
operator|=
literal|null
expr_stmt|;
name|channel
operator|=
name|other
operator|.
name|channel
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|other
operator|.
name|bufferSize
expr_stmt|;
name|buffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|filePos
operator|=
operator|-
name|bufferSize
expr_stmt|;
name|bufferPos
operator|=
name|bufferSize
expr_stmt|;
name|isOpen
operator|=
literal|true
expr_stmt|;
name|isClone
operator|=
literal|true
expr_stmt|;
comment|//System.out.println("D clone this=" + this);
name|seek
argument_list|(
name|other
operator|.
name|getFilePointer
argument_list|()
argument_list|)
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
name|isOpen
operator|&&
operator|!
name|isClone
condition|)
block|{
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|isClone
condition|)
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|filePos
operator|+
name|bufferPos
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
name|pos
operator|!=
name|getFilePointer
argument_list|()
condition|)
block|{
specifier|final
name|long
name|alignedPos
init|=
name|pos
operator|&
name|ALIGN_NOT_MASK
decl_stmt|;
name|filePos
operator|=
name|alignedPos
operator|-
name|bufferSize
expr_stmt|;
specifier|final
name|int
name|delta
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
name|alignedPos
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|!=
literal|0
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|bufferPos
operator|=
name|delta
expr_stmt|;
block|}
else|else
block|{
comment|// force refill on next read
name|bufferPos
operator|=
name|bufferSize
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
try|try
block|{
return|return
name|channel
operator|.
name|size
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IOException during length(): "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
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
comment|// NOTE: we don't guard against EOF here... ie the
comment|// "final" buffer will typically be filled to less
comment|// than bufferSize
if|if
condition|(
name|bufferPos
operator|==
name|bufferSize
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
block|}
assert|assert
name|bufferPos
operator|==
name|buffer
operator|.
name|position
argument_list|()
operator|:
literal|"bufferPos="
operator|+
name|bufferPos
operator|+
literal|" vs buffer.position()="
operator|+
name|buffer
operator|.
name|position
argument_list|()
assert|;
name|bufferPos
operator|++
expr_stmt|;
return|return
name|buffer
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|filePos
operator|+=
name|bufferSize
expr_stmt|;
name|bufferPos
operator|=
literal|0
expr_stmt|;
assert|assert
operator|(
name|filePos
operator|&
name|ALIGN_NOT_MASK
operator|)
operator|==
name|filePos
operator|:
literal|"filePos="
operator|+
name|filePos
operator|+
literal|" anded="
operator|+
operator|(
name|filePos
operator|&
name|ALIGN_NOT_MASK
operator|)
assert|;
comment|//System.out.println("X refill filePos=" + filePos);
name|int
name|n
decl_stmt|;
try|try
block|{
name|n
operator|=
name|channel
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|filePos
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|n
operator|<
literal|0
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
name|buffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
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
name|dst
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
name|int
name|toRead
init|=
name|len
decl_stmt|;
comment|//System.out.println("\nX readBytes len=" + len + " fp=" + getFilePointer() + " size=" + length() + " this=" + this);
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|left
init|=
name|bufferSize
operator|-
name|bufferPos
decl_stmt|;
if|if
condition|(
name|left
operator|<
name|toRead
condition|)
block|{
comment|//System.out.println("  copy " + left);
name|buffer
operator|.
name|get
argument_list|(
name|dst
argument_list|,
name|offset
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|toRead
operator|-=
name|left
expr_stmt|;
name|offset
operator|+=
name|left
expr_stmt|;
name|refill
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("  copy " + toRead);
name|buffer
operator|.
name|get
argument_list|(
name|dst
argument_list|,
name|offset
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|bufferPos
operator|+=
name|toRead
expr_stmt|;
comment|//System.out.println("  readBytes done");
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|NativeUnixIndexInput
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|NativeUnixIndexInput
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IOException during clone: "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
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
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: is this the right thing to do?
return|return
name|BufferedIndexInput
operator|.
name|wrap
argument_list|(
name|sliceDescription
argument_list|,
name|this
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

