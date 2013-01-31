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
name|IndexInput
import|;
end_import

begin_comment
comment|/** Represents a logical byte[] as a series of pages.  You  *  can write-once into the logical byte[] (append only),  *  using copy, and then retrieve slices (BytesRef) into it  *  using fill.  *  * @lucene.internal  **/
end_comment

begin_comment
comment|// TODO: refactor this, byteblockpool, fst.bytestore, and any
end_comment

begin_comment
comment|// other "shift/mask big arrays". there are too many of these classes!
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
comment|/** Provides methods to read BytesRefs from a frozen    *  PagedBytes.    *    * @see #freeze */
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
specifier|private
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
name|void
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
block|}
comment|/**      * Reads length as 1 or 2 byte vInt prefix, starting at<i>start</i>.      *<p>      *<b>Note:</b> this method does not support slices spanning across block      * borders.      *</p>      *       * @lucene.internal      **/
comment|// TODO: this really needs to be refactored into fieldcacheimpl
DECL|method|fill
specifier|public
name|void
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
block|}
block|}
comment|/** 1&lt;&lt;blockBits must be bigger than biggest single    *  BytesRef slice that will be pulled */
DECL|method|PagedBytes
specifier|public
name|PagedBytes
parameter_list|(
name|int
name|blockBits
parameter_list|)
block|{
assert|assert
name|blockBits
operator|>
literal|0
operator|&&
name|blockBits
operator|<=
literal|31
operator|:
name|blockBits
assert|;
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
name|PagedBytes
operator|.
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
comment|// TODO: this really needs to be refactored into fieldcacheimpl!
DECL|method|copyUsingLengthPrefix
specifier|public
name|long
name|copyUsingLengthPrefix
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
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
block|}
end_class

end_unit

