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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|DataOutput
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
name|IndexInput
import|;
end_import

begin_comment
comment|/** Represents a logical byte[] as a series of pages.  You  *  can write-once into the logical byte[] (append only),  *  using copy, and then retrieve slices (BytesRef) into it  *  using fill.  *  * @lucene.internal  **/
end_comment

begin_class
DECL|class|PagedBytes
specifier|public
specifier|final
class|class
name|PagedBytes
block|{
DECL|field|blocks
specifier|private
specifier|final
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|blockEnd
specifier|private
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|blockEnd
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|blockBits
specifier|private
specifier|final
name|int
name|blockBits
decl_stmt|;
DECL|field|blockMask
specifier|private
specifier|final
name|int
name|blockMask
decl_stmt|;
DECL|field|didSkipBytes
specifier|private
name|boolean
name|didSkipBytes
decl_stmt|;
DECL|field|frozen
specifier|private
name|boolean
name|frozen
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|field|currentBlock
specifier|private
name|byte
index|[]
name|currentBlock
decl_stmt|;
DECL|field|EMPTY_BYTES
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
DECL|class|Reader
specifier|public
specifier|final
specifier|static
class|class
name|Reader
block|{
DECL|field|blocks
specifier|private
specifier|final
name|byte
index|[]
index|[]
name|blocks
decl_stmt|;
DECL|field|blockEnds
specifier|private
specifier|final
name|int
index|[]
name|blockEnds
decl_stmt|;
DECL|field|blockBits
specifier|private
specifier|final
name|int
name|blockBits
decl_stmt|;
DECL|field|blockMask
specifier|private
specifier|final
name|int
name|blockMask
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|Reader
specifier|public
name|Reader
parameter_list|(
name|PagedBytes
name|pagedBytes
parameter_list|)
block|{
name|blocks
operator|=
operator|new
name|byte
index|[
name|pagedBytes
operator|.
name|blocks
operator|.
name|size
argument_list|()
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
name|pagedBytes
operator|.
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|blockEnds
operator|=
operator|new
name|int
index|[
name|blocks
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockEnds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blockEnds
index|[
name|i
index|]
operator|=
name|pagedBytes
operator|.
name|blockEnd
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|blockBits
operator|=
name|pagedBytes
operator|.
name|blockBits
expr_stmt|;
name|blockMask
operator|=
name|pagedBytes
operator|.
name|blockMask
expr_stmt|;
name|blockSize
operator|=
name|pagedBytes
operator|.
name|blockSize
expr_stmt|;
block|}
comment|/**      * Gets a slice out of {@link PagedBytes} starting at<i>start</i> with a      * given length. Iff the slice spans across a block border this method will      * allocate sufficient resources and copy the paged data.      *<p>      * Slices spanning more than one block are not supported.      *</p>      * @lucene.internal       **/
DECL|method|fillSlice
specifier|public
name|BytesRef
name|fillSlice
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|long
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
assert|assert
name|length
operator|>=
literal|0
operator|:
literal|"length="
operator|+
name|length
assert|;
assert|assert
name|length
operator|<=
name|blockSize
operator|+
literal|1
assert|;
specifier|final
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|&
name|blockMask
argument_list|)
decl_stmt|;
name|b
operator|.
name|length
operator|=
name|length
expr_stmt|;
if|if
condition|(
name|blockSize
operator|-
name|offset
operator|>=
name|length
condition|)
block|{
comment|// Within block
name|b
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
index|]
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
else|else
block|{
comment|// Split
name|b
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|b
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
index|[
name|index
index|]
argument_list|,
name|offset
argument_list|,
name|b
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|blockSize
operator|-
name|offset
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
index|[
literal|1
operator|+
name|index
index|]
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|bytes
argument_list|,
name|blockSize
operator|-
name|offset
argument_list|,
name|length
operator|-
operator|(
name|blockSize
operator|-
name|offset
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
comment|/**      * Reads length as 1 or 2 byte vInt prefix, starting at<i>start</i>.      *<p>      *<b>Note:</b> this method does not support slices spanning across block      * borders.      *</p>      *       * @return the given {@link BytesRef}      *       * @lucene.internal      **/
DECL|method|fill
specifier|public
name|BytesRef
name|fill
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|long
name|start
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|&
name|blockMask
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|block
init|=
name|b
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
name|b
operator|.
name|length
operator|=
name|block
index|[
name|offset
index|]
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|length
operator|=
operator|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|0x7f
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block
index|[
literal|1
operator|+
name|offset
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
operator|+
literal|2
expr_stmt|;
assert|assert
name|b
operator|.
name|length
operator|>
literal|0
assert|;
block|}
return|return
name|b
return|;
block|}
comment|/**      * Reads length as 1 or 2 byte vInt prefix, starting at<i>start</i>. *      *<p>      *<b>Note:</b> this method does not support slices spanning across block      * borders.      *</p>      *       * @return the internal block number of the slice.      * @lucene.internal      **/
DECL|method|fillAndGetIndex
specifier|public
name|int
name|fillAndGetIndex
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|long
name|start
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|&
name|blockMask
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|block
init|=
name|b
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
name|b
operator|.
name|length
operator|=
name|block
index|[
name|offset
index|]
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|length
operator|=
operator|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|0x7f
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block
index|[
literal|1
operator|+
name|offset
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
operator|+
literal|2
expr_stmt|;
assert|assert
name|b
operator|.
name|length
operator|>
literal|0
assert|;
block|}
return|return
name|index
return|;
block|}
comment|/**      * Reads length as 1 or 2 byte vInt prefix, starting at<i>start</i> and      * returns the start offset of the next part, suitable as start parameter on      * next call to sequentially read all {@link BytesRef}.      *       *<p>      *<b>Note:</b> this method does not support slices spanning across block      * borders.      *</p>      *       * @return the start offset of the next part, suitable as start parameter on      *         next call to sequentially read all {@link BytesRef}.      * @lucene.internal      **/
DECL|method|fillAndGetStart
specifier|public
name|long
name|fillAndGetStart
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|long
name|start
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|&
name|blockMask
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|block
init|=
name|b
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
name|b
operator|.
name|length
operator|=
name|block
index|[
name|offset
index|]
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
operator|+
literal|1
expr_stmt|;
name|start
operator|+=
literal|1L
operator|+
name|b
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|length
operator|=
operator|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|0x7f
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block
index|[
literal|1
operator|+
name|offset
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|b
operator|.
name|offset
operator|=
name|offset
operator|+
literal|2
expr_stmt|;
name|start
operator|+=
literal|2L
operator|+
name|b
operator|.
name|length
expr_stmt|;
assert|assert
name|b
operator|.
name|length
operator|>
literal|0
assert|;
block|}
return|return
name|start
return|;
block|}
comment|/**      * Gets a slice out of {@link PagedBytes} starting at<i>start</i>, the      * length is read as 1 or 2 byte vInt prefix. Iff the slice spans across a      * block border this method will allocate sufficient resources and copy the      * paged data.      *<p>      * Slices spanning more than one block are not supported.      *</p>      *       * @lucene.internal      **/
DECL|method|fillSliceWithPrefix
specifier|public
name|BytesRef
name|fillSliceWithPrefix
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|long
name|start
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
call|(
name|int
call|)
argument_list|(
name|start
operator|&
name|blockMask
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|block
init|=
name|blocks
index|[
name|index
index|]
decl_stmt|;
specifier|final
name|int
name|length
decl_stmt|;
if|if
condition|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
name|length
operator|=
name|block
index|[
name|offset
index|]
expr_stmt|;
name|offset
operator|=
name|offset
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|length
operator|=
operator|(
operator|(
name|block
index|[
name|offset
index|]
operator|&
literal|0x7f
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block
index|[
literal|1
operator|+
name|offset
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|offset
operator|=
name|offset
operator|+
literal|2
expr_stmt|;
assert|assert
name|length
operator|>
literal|0
assert|;
block|}
assert|assert
name|length
operator|>=
literal|0
operator|:
literal|"length="
operator|+
name|length
assert|;
name|b
operator|.
name|length
operator|=
name|length
expr_stmt|;
comment|// NOTE: even though copyUsingLengthPrefix always
comment|// allocs a new block if the byte[] to be added won't
comment|// fit in current block,
comment|// VarDerefBytesImpl.finishInternal does its own
comment|// prefix + byte[] writing which can span two blocks,
comment|// so we support that here on decode:
comment|// we could some day relax that and span two blocks:
if|if
condition|(
name|blockSize
operator|-
name|offset
operator|>=
name|length
condition|)
block|{
comment|// Within block
name|b
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|b
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// Split
name|b
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|b
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
index|[
name|index
index|]
argument_list|,
name|offset
argument_list|,
name|b
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|blockSize
operator|-
name|offset
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
index|[
literal|1
operator|+
name|index
index|]
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|bytes
argument_list|,
name|blockSize
operator|-
name|offset
argument_list|,
name|length
operator|-
operator|(
name|blockSize
operator|-
name|offset
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
comment|/** @lucene.internal */
DECL|method|getBlocks
specifier|public
name|byte
index|[]
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
comment|/** @lucene.internal */
DECL|method|getBlockEnds
specifier|public
name|int
index|[]
name|getBlockEnds
parameter_list|()
block|{
return|return
name|blockEnds
return|;
block|}
block|}
comment|/** 1<<blockBits must be bigger than biggest single    *  BytesRef slice that will be pulled */
DECL|method|PagedBytes
specifier|public
name|PagedBytes
parameter_list|(
name|int
name|blockBits
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
literal|1
operator|<<
name|blockBits
expr_stmt|;
name|this
operator|.
name|blockBits
operator|=
name|blockBits
expr_stmt|;
name|blockMask
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
name|upto
operator|=
name|blockSize
expr_stmt|;
block|}
comment|/** Read this many bytes from in */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|long
name|byteCount
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|byteCount
operator|>
literal|0
condition|)
block|{
name|int
name|left
init|=
name|blockSize
operator|-
name|upto
decl_stmt|;
if|if
condition|(
name|left
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|currentBlock
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
name|left
operator|=
name|blockSize
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|<
name|byteCount
condition|)
block|{
name|in
operator|.
name|readBytes
argument_list|(
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|left
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|upto
operator|=
name|blockSize
expr_stmt|;
name|byteCount
operator|-=
name|left
expr_stmt|;
block|}
else|else
block|{
name|in
operator|.
name|readBytes
argument_list|(
name|currentBlock
argument_list|,
name|upto
argument_list|,
operator|(
name|int
operator|)
name|byteCount
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|byteCount
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/** Copy BytesRef in */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|byteCount
init|=
name|bytes
operator|.
name|length
decl_stmt|;
name|int
name|bytesUpto
init|=
name|bytes
operator|.
name|offset
decl_stmt|;
while|while
condition|(
name|byteCount
operator|>
literal|0
condition|)
block|{
name|int
name|left
init|=
name|blockSize
operator|-
name|upto
decl_stmt|;
if|if
condition|(
name|left
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|currentBlock
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
name|left
operator|=
name|blockSize
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|<
name|byteCount
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytesUpto
argument_list|,
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|upto
operator|=
name|blockSize
expr_stmt|;
name|byteCount
operator|-=
name|left
expr_stmt|;
name|bytesUpto
operator|+=
name|left
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytesUpto
argument_list|,
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|byteCount
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|byteCount
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/** Copy BytesRef in, setting BytesRef out to the result.    * Do not use this if you will use freeze(true).    * This only supports bytes.length<= blockSize */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|BytesRef
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|left
init|=
name|blockSize
operator|-
name|upto
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|>
name|left
operator|||
name|currentBlock
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|currentBlock
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|didSkipBytes
operator|=
literal|true
expr_stmt|;
block|}
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
name|left
operator|=
name|blockSize
expr_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|<=
name|blockSize
assert|;
comment|// TODO: we could also support variable block sizes
block|}
name|out
operator|.
name|bytes
operator|=
name|currentBlock
expr_stmt|;
name|out
operator|.
name|offset
operator|=
name|upto
expr_stmt|;
name|out
operator|.
name|length
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
comment|/** Commits final byte[], trimming it if necessary and if trim=true */
DECL|method|freeze
specifier|public
name|Reader
name|freeze
parameter_list|(
name|boolean
name|trim
parameter_list|)
block|{
if|if
condition|(
name|frozen
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already frozen"
argument_list|)
throw|;
block|}
if|if
condition|(
name|didSkipBytes
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot freeze when copy(BytesRef, BytesRef) was used"
argument_list|)
throw|;
block|}
if|if
condition|(
name|trim
operator|&&
name|upto
operator|<
name|blockSize
condition|)
block|{
specifier|final
name|byte
index|[]
name|newBlock
init|=
operator|new
name|byte
index|[
name|upto
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|currentBlock
argument_list|,
literal|0
argument_list|,
name|newBlock
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|currentBlock
operator|=
name|newBlock
expr_stmt|;
block|}
if|if
condition|(
name|currentBlock
operator|==
literal|null
condition|)
block|{
name|currentBlock
operator|=
name|EMPTY_BYTES
expr_stmt|;
block|}
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|frozen
operator|=
literal|true
expr_stmt|;
name|currentBlock
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|Reader
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|getPointer
specifier|public
name|long
name|getPointer
parameter_list|()
block|{
if|if
condition|(
name|currentBlock
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
operator|(
name|blocks
operator|.
name|size
argument_list|()
operator|*
operator|(
operator|(
name|long
operator|)
name|blockSize
operator|)
operator|)
operator|+
name|upto
return|;
block|}
block|}
comment|/** Copy bytes in, writing the length as a 1 or 2 byte    *  vInt prefix. */
DECL|method|copyUsingLengthPrefix
specifier|public
name|long
name|copyUsingLengthPrefix
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|>=
literal|32768
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"max length is 32767 (got "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|upto
operator|+
name|bytes
operator|.
name|length
operator|+
literal|2
operator|>
name|blockSize
condition|)
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|+
literal|2
operator|>
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"block size "
operator|+
name|blockSize
operator|+
literal|" is too small to store length "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" bytes"
argument_list|)
throw|;
block|}
if|if
condition|(
name|currentBlock
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|long
name|pointer
init|=
name|getPointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|<
literal|128
condition|)
block|{
name|currentBlock
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|bytes
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|currentBlock
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|bytes
operator|.
name|length
operator|>>
literal|8
operator|)
argument_list|)
expr_stmt|;
name|currentBlock
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|bytes
operator|.
name|length
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
return|return
name|pointer
return|;
block|}
DECL|class|PagedBytesDataInput
specifier|public
specifier|final
class|class
name|PagedBytesDataInput
extends|extends
name|DataInput
block|{
DECL|field|currentBlockIndex
specifier|private
name|int
name|currentBlockIndex
decl_stmt|;
DECL|field|currentBlockUpto
specifier|private
name|int
name|currentBlockUpto
decl_stmt|;
DECL|field|currentBlock
specifier|private
name|byte
index|[]
name|currentBlock
decl_stmt|;
DECL|method|PagedBytesDataInput
name|PagedBytesDataInput
parameter_list|()
block|{
name|currentBlock
operator|=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|PagedBytesDataInput
name|clone
init|=
name|getDataInput
argument_list|()
decl_stmt|;
name|clone
operator|.
name|setPosition
argument_list|(
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Returns the current byte position. */
DECL|method|getPosition
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|currentBlockIndex
operator|*
name|blockSize
operator|+
name|currentBlockUpto
return|;
block|}
comment|/** Seek to a position previously obtained from      *  {@link #getPosition}. */
DECL|method|setPosition
specifier|public
name|void
name|setPosition
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|currentBlockIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|>>
name|blockBits
argument_list|)
expr_stmt|;
name|currentBlock
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|currentBlockIndex
argument_list|)
expr_stmt|;
name|currentBlockUpto
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|&
name|blockMask
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
if|if
condition|(
name|currentBlockUpto
operator|==
name|blockSize
condition|)
block|{
name|nextBlock
argument_list|()
expr_stmt|;
block|}
return|return
name|currentBlock
index|[
name|currentBlockUpto
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
block|{
assert|assert
name|b
operator|.
name|length
operator|>=
name|offset
operator|+
name|len
assert|;
specifier|final
name|int
name|offsetEnd
init|=
name|offset
operator|+
name|len
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|blockLeft
init|=
name|blockSize
operator|-
name|currentBlockUpto
decl_stmt|;
specifier|final
name|int
name|left
init|=
name|offsetEnd
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|blockLeft
operator|<
name|left
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|currentBlock
argument_list|,
name|currentBlockUpto
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|blockLeft
argument_list|)
expr_stmt|;
name|nextBlock
argument_list|()
expr_stmt|;
name|offset
operator|+=
name|blockLeft
expr_stmt|;
block|}
else|else
block|{
comment|// Last block
name|System
operator|.
name|arraycopy
argument_list|(
name|currentBlock
argument_list|,
name|currentBlockUpto
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|currentBlockUpto
operator|+=
name|left
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|nextBlock
specifier|private
name|void
name|nextBlock
parameter_list|()
block|{
name|currentBlockIndex
operator|++
expr_stmt|;
name|currentBlockUpto
operator|=
literal|0
expr_stmt|;
name|currentBlock
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|currentBlockIndex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PagedBytesDataOutput
specifier|public
specifier|final
class|class
name|PagedBytesDataOutput
extends|extends
name|DataOutput
block|{
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
block|{
if|if
condition|(
name|upto
operator|==
name|blockSize
condition|)
block|{
if|if
condition|(
name|currentBlock
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
name|currentBlock
index|[
name|upto
operator|++
index|]
operator|=
name|b
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
assert|assert
name|b
operator|.
name|length
operator|>=
name|offset
operator|+
name|length
assert|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|upto
operator|==
name|blockSize
condition|)
block|{
if|if
condition|(
name|currentBlock
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|int
name|offsetEnd
init|=
name|offset
operator|+
name|length
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
name|offsetEnd
operator|-
name|offset
decl_stmt|;
specifier|final
name|int
name|blockLeft
init|=
name|blockSize
operator|-
name|upto
decl_stmt|;
if|if
condition|(
name|blockLeft
operator|<
name|left
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
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|blockLeft
argument_list|)
expr_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|currentBlock
argument_list|)
expr_stmt|;
name|blockEnd
operator|.
name|add
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|currentBlock
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
name|offset
operator|+=
name|blockLeft
expr_stmt|;
block|}
else|else
block|{
comment|// Last block
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|currentBlock
argument_list|,
name|upto
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|left
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/** Return the current byte position. */
DECL|method|getPosition
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
if|if
condition|(
name|currentBlock
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|blocks
operator|.
name|size
argument_list|()
operator|*
name|blockSize
operator|+
name|upto
return|;
block|}
block|}
block|}
comment|/** Returns a DataInput to read values from this    *  PagedBytes instance. */
DECL|method|getDataInput
specifier|public
name|PagedBytesDataInput
name|getDataInput
parameter_list|()
block|{
if|if
condition|(
operator|!
name|frozen
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"must call freeze() before getDataInput"
argument_list|)
throw|;
block|}
return|return
operator|new
name|PagedBytesDataInput
argument_list|()
return|;
block|}
comment|/** Returns a DataOutput that you may use to write into    *  this PagedBytes instance.  If you do this, you should    *  not call the other writing methods (eg, copy);    *  results are undefined. */
DECL|method|getDataOutput
specifier|public
name|PagedBytesDataOutput
name|getDataOutput
parameter_list|()
block|{
if|if
condition|(
name|frozen
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot get DataOutput after freeze()"
argument_list|)
throw|;
block|}
return|return
operator|new
name|PagedBytesDataOutput
argument_list|()
return|;
block|}
block|}
end_class

end_unit

