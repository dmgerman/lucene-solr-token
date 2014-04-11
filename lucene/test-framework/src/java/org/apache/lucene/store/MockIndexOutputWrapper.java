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

begin_comment
comment|/**  * Used by MockRAMDirectory to create an output stream that  * will throw an IOException on fake disk full, track max  * disk space actually used, and maybe throw random  * IOExceptions.  */
end_comment

begin_class
DECL|class|MockIndexOutputWrapper
specifier|public
class|class
name|MockIndexOutputWrapper
extends|extends
name|IndexOutput
block|{
DECL|field|dir
specifier|private
name|MockDirectoryWrapper
name|dir
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|first
specifier|private
name|boolean
name|first
init|=
literal|true
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|singleByte
name|byte
index|[]
name|singleByte
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
comment|/** Construct an empty output buffer. */
DECL|method|MockIndexOutputWrapper
specifier|public
name|MockIndexOutputWrapper
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|,
name|IndexOutput
name|delegate
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|checkCrashed
specifier|private
name|void
name|checkCrashed
parameter_list|()
throws|throws
name|IOException
block|{
comment|// If MockRAMDir crashed since we were opened, then don't write anything
if|if
condition|(
name|dir
operator|.
name|crashed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MockRAMDirectory was crashed; cannot write to "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
DECL|method|checkDiskFull
specifier|private
name|void
name|checkDiskFull
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|freeSpace
init|=
name|dir
operator|.
name|maxSize
operator|==
literal|0
condition|?
literal|0
else|:
name|dir
operator|.
name|maxSize
operator|-
name|dir
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|long
name|realUsage
init|=
literal|0
decl_stmt|;
comment|// Enforce disk full:
if|if
condition|(
name|dir
operator|.
name|maxSize
operator|!=
literal|0
operator|&&
name|freeSpace
operator|<=
name|len
condition|)
block|{
comment|// Compute the real disk free.  This will greatly slow
comment|// down our test but makes it more accurate:
name|realUsage
operator|=
name|dir
operator|.
name|getRecomputedActualSizeInBytes
argument_list|()
expr_stmt|;
name|freeSpace
operator|=
name|dir
operator|.
name|maxSize
operator|-
name|realUsage
expr_stmt|;
block|}
if|if
condition|(
name|dir
operator|.
name|maxSize
operator|!=
literal|0
operator|&&
name|freeSpace
operator|<=
name|len
condition|)
block|{
if|if
condition|(
name|freeSpace
operator|>
literal|0
condition|)
block|{
name|realUsage
operator|+=
name|freeSpace
expr_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
operator|(
name|int
operator|)
name|freeSpace
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delegate
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|realUsage
operator|>
name|dir
operator|.
name|maxUsedSize
condition|)
block|{
name|dir
operator|.
name|maxUsedSize
operator|=
name|realUsage
expr_stmt|;
block|}
name|String
name|message
init|=
literal|"fake disk full at "
operator|+
name|dir
operator|.
name|getRecomputedActualSizeInBytes
argument_list|()
operator|+
literal|" bytes when writing "
operator|+
name|name
operator|+
literal|" (file length="
operator|+
name|delegate
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|freeSpace
operator|>
literal|0
condition|)
block|{
name|message
operator|+=
literal|"; wrote "
operator|+
name|freeSpace
operator|+
literal|" of "
operator|+
name|len
operator|+
literal|" bytes"
expr_stmt|;
block|}
name|message
operator|+=
literal|")"
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": MDW: now throw fake disk full"
argument_list|)
expr_stmt|;
operator|new
name|Throwable
argument_list|()
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
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
name|dir
operator|.
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|dir
operator|.
name|trackDiskUsage
condition|)
block|{
comment|// Now compute actual disk usage& track the maxUsedSize
comment|// in the MockDirectoryWrapper:
name|long
name|size
init|=
name|dir
operator|.
name|getRecomputedActualSizeInBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|dir
operator|.
name|maxUsedSize
condition|)
block|{
name|dir
operator|.
name|maxUsedSize
operator|=
name|size
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|removeIndexOutput
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
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
name|dir
operator|.
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|flush
argument_list|()
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
name|singleByte
index|[
literal|0
index|]
operator|=
name|b
expr_stmt|;
name|writeBytes
argument_list|(
name|singleByte
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
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkCrashed
argument_list|()
expr_stmt|;
name|checkDiskFull
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
literal|null
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|.
name|randomState
operator|.
name|nextInt
argument_list|(
literal|200
argument_list|)
operator|==
literal|0
condition|)
block|{
specifier|final
name|int
name|half
init|=
name|len
operator|/
literal|2
decl_stmt|;
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|half
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
operator|+
name|half
argument_list|,
name|len
operator|-
name|half
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
if|if
condition|(
name|first
condition|)
block|{
comment|// Maybe throw random exception; only do this on first
comment|// write to a new file:
name|first
operator|=
literal|false
expr_stmt|;
name|dir
operator|.
name|maybeThrowIOException
argument_list|(
name|name
argument_list|)
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
name|checkCrashed
argument_list|()
expr_stmt|;
name|checkDiskFull
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
name|input
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|copyBytes
argument_list|(
name|input
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|dir
operator|.
name|maybeThrowDeterministicException
argument_list|()
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MockIndexOutputWrapper("
operator|+
name|delegate
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

