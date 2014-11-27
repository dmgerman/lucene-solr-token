begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexOutput
import|;
end_import

begin_comment
comment|/**  * Intentionally slow IndexOutput for testing.  */
end_comment

begin_class
DECL|class|ThrottledIndexOutput
specifier|public
class|class
name|ThrottledIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|DEFAULT_MIN_WRITTEN_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_WRITTEN_BYTES
init|=
literal|1024
decl_stmt|;
DECL|field|bytesPerSecond
specifier|private
specifier|final
name|int
name|bytesPerSecond
decl_stmt|;
DECL|field|delegate
specifier|private
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|flushDelayMillis
specifier|private
name|long
name|flushDelayMillis
decl_stmt|;
DECL|field|closeDelayMillis
specifier|private
name|long
name|closeDelayMillis
decl_stmt|;
DECL|field|seekDelayMillis
specifier|private
name|long
name|seekDelayMillis
decl_stmt|;
DECL|field|pendingBytes
specifier|private
name|long
name|pendingBytes
decl_stmt|;
DECL|field|minBytesWritten
specifier|private
name|long
name|minBytesWritten
decl_stmt|;
DECL|field|timeElapsed
specifier|private
name|long
name|timeElapsed
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|method|newFromDelegate
specifier|public
name|ThrottledIndexOutput
name|newFromDelegate
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
block|{
return|return
operator|new
name|ThrottledIndexOutput
argument_list|(
name|bytesPerSecond
argument_list|,
name|flushDelayMillis
argument_list|,
name|closeDelayMillis
argument_list|,
name|seekDelayMillis
argument_list|,
name|minBytesWritten
argument_list|,
name|output
argument_list|)
return|;
block|}
DECL|method|ThrottledIndexOutput
specifier|public
name|ThrottledIndexOutput
parameter_list|(
name|int
name|bytesPerSecond
parameter_list|,
name|long
name|delayInMillis
parameter_list|,
name|IndexOutput
name|delegate
parameter_list|)
block|{
name|this
argument_list|(
name|bytesPerSecond
argument_list|,
name|delayInMillis
argument_list|,
name|delayInMillis
argument_list|,
name|delayInMillis
argument_list|,
name|DEFAULT_MIN_WRITTEN_BYTES
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
DECL|method|ThrottledIndexOutput
specifier|public
name|ThrottledIndexOutput
parameter_list|(
name|int
name|bytesPerSecond
parameter_list|,
name|long
name|delays
parameter_list|,
name|int
name|minBytesWritten
parameter_list|,
name|IndexOutput
name|delegate
parameter_list|)
block|{
name|this
argument_list|(
name|bytesPerSecond
argument_list|,
name|delays
argument_list|,
name|delays
argument_list|,
name|delays
argument_list|,
name|minBytesWritten
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
DECL|method|mBitsToBytes
specifier|public
specifier|static
specifier|final
name|int
name|mBitsToBytes
parameter_list|(
name|int
name|mbits
parameter_list|)
block|{
return|return
name|mbits
operator|*
literal|125000000
return|;
block|}
DECL|method|ThrottledIndexOutput
specifier|public
name|ThrottledIndexOutput
parameter_list|(
name|int
name|bytesPerSecond
parameter_list|,
name|long
name|flushDelayMillis
parameter_list|,
name|long
name|closeDelayMillis
parameter_list|,
name|long
name|seekDelayMillis
parameter_list|,
name|long
name|minBytesWritten
parameter_list|,
name|IndexOutput
name|delegate
parameter_list|)
block|{
assert|assert
name|bytesPerSecond
operator|>
literal|0
assert|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|bytesPerSecond
operator|=
name|bytesPerSecond
expr_stmt|;
name|this
operator|.
name|flushDelayMillis
operator|=
name|flushDelayMillis
expr_stmt|;
name|this
operator|.
name|closeDelayMillis
operator|=
name|closeDelayMillis
expr_stmt|;
name|this
operator|.
name|seekDelayMillis
operator|=
name|seekDelayMillis
expr_stmt|;
name|this
operator|.
name|minBytesWritten
operator|=
name|minBytesWritten
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
try|try
block|{
name|sleep
argument_list|(
name|closeDelayMillis
operator|+
name|getDelay
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|delegate
operator|.
name|close
argument_list|()
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
name|delegate
operator|.
name|getFilePointer
argument_list|()
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
name|bytes
index|[
literal|0
index|]
operator|=
name|b
expr_stmt|;
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
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
specifier|final
name|long
name|before
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|// TODO: sometimes, write only half the bytes, then
comment|// sleep, then 2nd half, then sleep, so we sometimes
comment|// interrupt having only written not all bytes
name|delegate
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
name|timeElapsed
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|before
expr_stmt|;
name|pendingBytes
operator|+=
name|length
expr_stmt|;
name|sleep
argument_list|(
name|getDelay
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getDelay
specifier|protected
name|long
name|getDelay
parameter_list|(
name|boolean
name|closing
parameter_list|)
block|{
if|if
condition|(
name|pendingBytes
operator|>
literal|0
operator|&&
operator|(
name|closing
operator|||
name|pendingBytes
operator|>
name|minBytesWritten
operator|)
condition|)
block|{
name|long
name|actualBps
init|=
operator|(
name|timeElapsed
operator|/
name|pendingBytes
operator|)
operator|*
literal|1000000000l
decl_stmt|;
comment|// nano to sec
if|if
condition|(
name|actualBps
operator|>
name|bytesPerSecond
condition|)
block|{
name|long
name|expected
init|=
operator|(
name|pendingBytes
operator|*
literal|1000l
operator|/
name|bytesPerSecond
operator|)
decl_stmt|;
specifier|final
name|long
name|delay
init|=
name|expected
operator|-
operator|(
name|timeElapsed
operator|/
literal|1000000l
operator|)
decl_stmt|;
name|pendingBytes
operator|=
literal|0
expr_stmt|;
name|timeElapsed
operator|=
literal|0
expr_stmt|;
return|return
name|delay
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|sleep
specifier|private
specifier|static
specifier|final
name|void
name|sleep
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
if|if
condition|(
name|ms
operator|<=
literal|0
condition|)
return|return;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|ms
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|copyBytes
specifier|public
name|void
name|copyBytes
parameter_list|(
name|DataInput
name|input
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|copyBytes
argument_list|(
name|input
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
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
return|return
name|delegate
operator|.
name|getChecksum
argument_list|()
return|;
block|}
block|}
end_class

end_unit

