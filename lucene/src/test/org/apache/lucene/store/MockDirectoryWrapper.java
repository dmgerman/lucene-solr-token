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
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * This is a Directory Wrapper that adds methods  * intended to be used only by unit tests.  */
end_comment

begin_class
DECL|class|MockDirectoryWrapper
specifier|public
class|class
name|MockDirectoryWrapper
extends|extends
name|Directory
block|{
DECL|field|delegate
specifier|final
name|Directory
name|delegate
decl_stmt|;
DECL|field|maxSize
name|long
name|maxSize
decl_stmt|;
comment|// Max actual bytes used. This is set by MockRAMOutputStream:
DECL|field|maxUsedSize
name|long
name|maxUsedSize
decl_stmt|;
DECL|field|randomIOExceptionRate
name|double
name|randomIOExceptionRate
decl_stmt|;
DECL|field|randomState
name|Random
name|randomState
decl_stmt|;
DECL|field|noDeleteOpenFile
name|boolean
name|noDeleteOpenFile
init|=
literal|true
decl_stmt|;
DECL|field|preventDoubleWrite
name|boolean
name|preventDoubleWrite
init|=
literal|true
decl_stmt|;
DECL|field|trackDiskUsage
name|boolean
name|trackDiskUsage
init|=
literal|false
decl_stmt|;
DECL|field|unSyncedFiles
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|unSyncedFiles
decl_stmt|;
DECL|field|createdFiles
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|createdFiles
decl_stmt|;
DECL|field|crashed
specifier|volatile
name|boolean
name|crashed
decl_stmt|;
comment|// NOTE: we cannot initialize the Map here due to the
comment|// order in which our constructor actually does this
comment|// member initialization vs when it calls super.  It seems
comment|// like super is called, then our members are initialized:
DECL|field|openFiles
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|openFiles
decl_stmt|;
DECL|method|init
specifier|private
specifier|synchronized
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|createdFiles
operator|==
literal|null
condition|)
name|createdFiles
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|unSyncedFiles
operator|==
literal|null
condition|)
name|unSyncedFiles
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|MockDirectoryWrapper
specifier|public
name|MockDirectoryWrapper
parameter_list|(
name|Directory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|setTrackDiskUsage
specifier|public
name|void
name|setTrackDiskUsage
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|trackDiskUsage
operator|=
name|v
expr_stmt|;
block|}
comment|/** If set to true, we throw an IOException if the same    *  file is opened by createOutput, ever. */
DECL|method|setPreventDoubleWrite
specifier|public
name|void
name|setPreventDoubleWrite
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|preventDoubleWrite
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
if|if
condition|(
name|crashed
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot sync after crash"
argument_list|)
throw|;
name|unSyncedFiles
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|sync
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
specifier|synchronized
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
if|if
condition|(
name|crashed
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot sync after crash"
argument_list|)
throw|;
name|unSyncedFiles
operator|.
name|removeAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
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
literal|"MockDirWrapper("
operator|+
name|delegate
operator|+
literal|")"
return|;
block|}
DECL|method|sizeInBytes
specifier|public
specifier|synchronized
specifier|final
name|long
name|sizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|delegate
operator|instanceof
name|RAMDirectory
condition|)
return|return
operator|(
operator|(
name|RAMDirectory
operator|)
name|delegate
operator|)
operator|.
name|sizeInBytes
argument_list|()
return|;
else|else
block|{
comment|// hack
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|delegate
operator|.
name|listAll
argument_list|()
control|)
name|size
operator|+=
name|delegate
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
block|}
comment|/** Simulates a crash of OS or machine by overwriting    *  unsynced files. */
DECL|method|crash
specifier|public
specifier|synchronized
name|void
name|crash
parameter_list|()
throws|throws
name|IOException
block|{
name|crashed
operator|=
literal|true
expr_stmt|;
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|unSyncedFiles
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|unSyncedFiles
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|delegate
operator|instanceof
name|RAMDirectory
operator|)
condition|)
block|{
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
name|deleteFile
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|%
literal|3
operator|==
literal|1
condition|)
block|{
comment|// Zero out file entirely
name|long
name|length
init|=
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|zeroes
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|long
name|upto
init|=
literal|0
decl_stmt|;
name|IndexOutput
name|out
init|=
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|length
condition|)
block|{
specifier|final
name|int
name|limit
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|length
operator|-
name|upto
argument_list|,
name|zeroes
operator|.
name|length
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|zeroes
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|limit
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|%
literal|3
operator|==
literal|2
condition|)
block|{
comment|// Truncate the file:
name|IndexOutput
name|out
init|=
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|out
operator|.
name|setLength
argument_list|(
name|fileLength
argument_list|(
name|name
argument_list|)
operator|/
literal|2
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|RAMDirectory
name|ramDir
init|=
operator|(
name|RAMDirectory
operator|)
name|delegate
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|RAMFile
name|file
init|=
name|ramDir
operator|.
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
name|deleteFile
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|%
literal|3
operator|==
literal|1
condition|)
block|{
comment|// Zero out file entirely
specifier|final
name|int
name|numBuffers
init|=
name|file
operator|.
name|numBuffers
argument_list|()
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
name|numBuffers
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|buffer
init|=
name|file
operator|.
name|getBuffer
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|count
operator|%
literal|3
operator|==
literal|2
condition|)
block|{
comment|// Truncate the file:
name|file
operator|.
name|setLength
argument_list|(
name|file
operator|.
name|getLength
argument_list|()
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|clearCrash
specifier|public
specifier|synchronized
name|void
name|clearCrash
parameter_list|()
throws|throws
name|IOException
block|{
name|crashed
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|setMaxSizeInBytes
specifier|public
name|void
name|setMaxSizeInBytes
parameter_list|(
name|long
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
DECL|method|getMaxSizeInBytes
specifier|public
name|long
name|getMaxSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxSize
return|;
block|}
comment|/**    * Returns the peek actual storage used (bytes) in this    * directory.    */
DECL|method|getMaxUsedSizeInBytes
specifier|public
name|long
name|getMaxUsedSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxUsedSize
return|;
block|}
DECL|method|resetMaxUsedSizeInBytes
specifier|public
name|void
name|resetMaxUsedSizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|maxUsedSize
operator|=
name|getRecomputedActualSizeInBytes
argument_list|()
expr_stmt|;
block|}
comment|/**    * Emulate windows whereby deleting an open file is not    * allowed (raise IOException).   */
DECL|method|setNoDeleteOpenFile
specifier|public
name|void
name|setNoDeleteOpenFile
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|noDeleteOpenFile
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getNoDeleteOpenFile
specifier|public
name|boolean
name|getNoDeleteOpenFile
parameter_list|()
block|{
return|return
name|noDeleteOpenFile
return|;
block|}
comment|/**    * If 0.0, no exceptions will be thrown.  Else this should    * be a double 0.0 - 1.0.  We will randomly throw an    * IOException on the first write to an OutputStream based    * on this probability.    */
DECL|method|setRandomIOExceptionRate
specifier|public
name|void
name|setRandomIOExceptionRate
parameter_list|(
name|double
name|rate
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|randomIOExceptionRate
operator|=
name|rate
expr_stmt|;
comment|// seed so we have deterministic behaviour:
name|randomState
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomIOExceptionRate
specifier|public
name|double
name|getRandomIOExceptionRate
parameter_list|()
block|{
return|return
name|randomIOExceptionRate
return|;
block|}
DECL|method|maybeThrowIOException
name|void
name|maybeThrowIOException
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|randomIOExceptionRate
operator|>
literal|0.0
condition|)
block|{
name|int
name|number
init|=
name|Math
operator|.
name|abs
argument_list|(
name|randomState
operator|.
name|nextInt
argument_list|()
operator|%
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|number
operator|<
name|randomIOExceptionRate
operator|*
literal|1000
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"a random IOException"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
specifier|synchronized
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteFile
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFile
specifier|private
specifier|synchronized
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|forced
parameter_list|)
throws|throws
name|IOException
block|{
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
if|if
condition|(
name|crashed
operator|&&
operator|!
name|forced
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot delete after crash"
argument_list|)
throw|;
if|if
condition|(
name|unSyncedFiles
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
name|unSyncedFiles
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|forced
condition|)
block|{
if|if
condition|(
name|noDeleteOpenFile
operator|&&
name|openFiles
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MockDirectoryWrapper: file \""
operator|+
name|name
operator|+
literal|"\" is still open: cannot delete"
argument_list|)
throw|;
block|}
block|}
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
specifier|synchronized
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|crashed
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot createOutput after crash"
argument_list|)
throw|;
name|init
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|preventDoubleWrite
operator|&&
name|createdFiles
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"segments.gen"
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"file \""
operator|+
name|name
operator|+
literal|"\" was already written to"
argument_list|)
throw|;
block|}
if|if
condition|(
name|noDeleteOpenFile
operator|&&
name|openFiles
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MockDirectoryWrapper: file \""
operator|+
name|name
operator|+
literal|"\" is still open: cannot overwrite"
argument_list|)
throw|;
if|if
condition|(
name|crashed
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot createOutput after crash"
argument_list|)
throw|;
name|unSyncedFiles
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|createdFiles
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|delegate
operator|instanceof
name|RAMDirectory
condition|)
block|{
name|RAMDirectory
name|ramdir
init|=
operator|(
name|RAMDirectory
operator|)
name|delegate
decl_stmt|;
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|(
name|ramdir
argument_list|)
decl_stmt|;
name|RAMFile
name|existing
init|=
name|ramdir
operator|.
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// Enforce write once:
if|if
condition|(
name|existing
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"segments.gen"
argument_list|)
operator|&&
name|preventDoubleWrite
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"file "
operator|+
name|name
operator|+
literal|" already exists"
argument_list|)
throw|;
else|else
block|{
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|ramdir
operator|.
name|sizeInBytes
operator|.
name|getAndAdd
argument_list|(
operator|-
name|existing
operator|.
name|sizeInBytes
argument_list|)
expr_stmt|;
name|existing
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
block|}
name|ramdir
operator|.
name|fileMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MockIndexOutputWrapper
argument_list|(
name|this
argument_list|,
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
specifier|synchronized
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
else|else
block|{
if|if
condition|(
name|openFiles
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Integer
name|v
init|=
name|openFiles
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|v
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openFiles
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MockIndexInputWrapper
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/** Provided for testing purposes.  Use sizeInBytes() instead. */
DECL|method|getRecomputedSizeInBytes
specifier|public
specifier|synchronized
specifier|final
name|long
name|getRecomputedSizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|delegate
operator|instanceof
name|RAMDirectory
operator|)
condition|)
return|return
name|sizeInBytes
argument_list|()
return|;
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|RAMFile
name|file
range|:
operator|(
operator|(
name|RAMDirectory
operator|)
name|delegate
operator|)
operator|.
name|fileMap
operator|.
name|values
argument_list|()
control|)
block|{
name|size
operator|+=
name|file
operator|.
name|getSizeInBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/** Like getRecomputedSizeInBytes(), but, uses actual file    * lengths rather than buffer allocations (which are    * quantized up to nearest    * RAMOutputStream.BUFFER_SIZE (now 1024) bytes.    */
DECL|method|getRecomputedActualSizeInBytes
specifier|public
specifier|final
specifier|synchronized
name|long
name|getRecomputedActualSizeInBytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|delegate
operator|instanceof
name|RAMDirectory
operator|)
condition|)
return|return
name|sizeInBytes
argument_list|()
return|;
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|RAMFile
name|file
range|:
operator|(
operator|(
name|RAMDirectory
operator|)
name|delegate
operator|)
operator|.
name|fileMap
operator|.
name|values
argument_list|()
control|)
name|size
operator|+=
name|file
operator|.
name|length
expr_stmt|;
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|noDeleteOpenFile
operator|&&
name|openFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// RuntimeException instead of IOException because
comment|// super() does not throw IOException currently:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"MockRAMDirectory: cannot close: there are still open files: "
operator|+
name|openFiles
argument_list|)
throw|;
block|}
name|open
operator|=
literal|false
expr_stmt|;
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|open
name|boolean
name|open
init|=
literal|true
decl_stmt|;
DECL|method|isOpen
specifier|public
specifier|synchronized
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|open
return|;
block|}
comment|/**    * Objects that represent fail-able conditions. Objects of a derived    * class are created and registered with the mock directory. After    * register, each object will be invoked once for each first write    * of a file, giving the object a chance to throw an IOException.    */
DECL|class|Failure
specifier|public
specifier|static
class|class
name|Failure
block|{
comment|/**      * eval is called on the first write of every new file.      */
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**      * reset should set the state of the failure to its default      * (freshly constructed) state. Reset is convenient for tests      * that want to create one failure object and then reuse it in      * multiple cases. This, combined with the fact that Failure      * subclasses are often anonymous classes makes reset difficult to      * do otherwise.      *      * A typical example of use is      * Failure failure = new Failure() { ... };      * ...      * mock.failOn(failure.reset())      */
DECL|method|reset
specifier|public
name|Failure
name|reset
parameter_list|()
block|{
return|return
name|this
return|;
block|}
DECL|field|doFail
specifier|protected
name|boolean
name|doFail
decl_stmt|;
DECL|method|setDoFail
specifier|public
name|void
name|setDoFail
parameter_list|()
block|{
name|doFail
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|clearDoFail
specifier|public
name|void
name|clearDoFail
parameter_list|()
block|{
name|doFail
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|field|failures
name|ArrayList
argument_list|<
name|Failure
argument_list|>
name|failures
decl_stmt|;
comment|/**    * add a Failure object to the list of objects to be evaluated    * at every potential failure point    */
DECL|method|failOn
specifier|synchronized
specifier|public
name|void
name|failOn
parameter_list|(
name|Failure
name|fail
parameter_list|)
block|{
if|if
condition|(
name|failures
operator|==
literal|null
condition|)
block|{
name|failures
operator|=
operator|new
name|ArrayList
argument_list|<
name|Failure
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|failures
operator|.
name|add
argument_list|(
name|fail
argument_list|)
expr_stmt|;
block|}
comment|/**    * Iterate through the failures list, giving each object a    * chance to throw an IOE    */
DECL|method|maybeThrowDeterministicException
specifier|synchronized
name|void
name|maybeThrowDeterministicException
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|failures
operator|!=
literal|null
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
name|failures
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|failures
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
specifier|synchronized
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fileExists
specifier|public
specifier|synchronized
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fileModified
specifier|public
specifier|synchronized
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|touchFile
specifier|public
specifier|synchronized
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|touchFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fileLength
specifier|public
specifier|synchronized
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|public
specifier|synchronized
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearLock
specifier|public
specifier|synchronized
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLockFactory
specifier|public
specifier|synchronized
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
block|{
name|delegate
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockFactory
specifier|public
specifier|synchronized
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockFactory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLockID
specifier|public
specifier|synchronized
name|String
name|getLockID
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
specifier|synchronized
name|void
name|copy
parameter_list|(
name|Directory
name|to
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|copy
argument_list|(
name|to
argument_list|,
name|src
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

