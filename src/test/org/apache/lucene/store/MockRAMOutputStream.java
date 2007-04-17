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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Used by MockRAMDirectory to create an output stream that  * will throw an IOException on fake disk full, track max  * disk space actually used, and maybe throw random  * IOExceptions.  */
end_comment

begin_class
DECL|class|MockRAMOutputStream
specifier|public
class|class
name|MockRAMOutputStream
extends|extends
name|RAMOutputStream
block|{
DECL|field|dir
specifier|private
name|MockRAMDirectory
name|dir
decl_stmt|;
DECL|field|first
specifier|private
name|boolean
name|first
init|=
literal|true
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
DECL|method|MockRAMOutputStream
specifier|public
name|MockRAMOutputStream
parameter_list|(
name|MockRAMDirectory
name|dir
parameter_list|,
name|RAMFile
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now compute actual disk usage& track the maxUsedSize
comment|// in the MockRAMDirectory:
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
name|long
name|freeSpace
init|=
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
operator|&&
name|freeSpace
operator|<
name|len
condition|)
block|{
name|realUsage
operator|+=
name|freeSpace
expr_stmt|;
name|super
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fake disk full at "
operator|+
name|dir
operator|.
name|getRecomputedActualSizeInBytes
argument_list|()
operator|+
literal|" bytes"
argument_list|)
throw|;
block|}
else|else
block|{
name|super
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
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

