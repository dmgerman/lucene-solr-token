begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.intblock
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|intblock
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Naive int block API that writes vInts.  This is  *  expected to give poor performance; it's really only for  *  testing the pluggability.  One should typically use pfor instead. */
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
name|index
operator|.
name|codecs
operator|.
name|sep
operator|.
name|IntIndexInput
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
name|IntsRef
import|;
end_import

begin_comment
comment|/** Abstract base class that reads fixed-size blocks of ints  *  from an IndexInput.  While this is a simple approach, a  *  more performant approach would directly create an impl  *  of IntIndexInput inside Directory.  Wrapping a generic  *  IndexInput will likely cost performance.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FixedIntBlockIndexInput
specifier|public
specifier|abstract
class|class
name|FixedIntBlockIndexInput
extends|extends
name|IntIndexInput
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|blockSize
specifier|protected
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|FixedIntBlockIndexInput
specifier|public
name|FixedIntBlockIndexInput
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|blockSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|Reader
name|reader
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
name|blockSize
index|]
decl_stmt|;
specifier|final
name|IndexInput
name|clone
init|=
operator|(
name|IndexInput
operator|)
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// TODO: can this be simplified?
return|return
operator|new
name|Reader
argument_list|(
name|clone
argument_list|,
name|buffer
argument_list|,
name|this
operator|.
name|getBlockReader
argument_list|(
name|clone
argument_list|,
name|buffer
argument_list|)
argument_list|)
return|;
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
block|{
return|return
operator|new
name|Index
argument_list|()
return|;
block|}
DECL|method|getBlockReader
specifier|protected
specifier|abstract
name|BlockReader
name|getBlockReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|interface|BlockReader
specifier|public
interface|interface
name|BlockReader
block|{
DECL|method|readBlock
specifier|public
name|void
name|readBlock
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|Reader
specifier|private
specifier|static
class|class
name|Reader
extends|extends
name|IntIndexInput
operator|.
name|Reader
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|pending
specifier|protected
specifier|final
name|int
index|[]
name|pending
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|seekPending
specifier|private
name|boolean
name|seekPending
decl_stmt|;
DECL|field|pendingFP
specifier|private
name|long
name|pendingFP
decl_stmt|;
DECL|field|pendingUpto
specifier|private
name|int
name|pendingUpto
decl_stmt|;
DECL|field|lastBlockFP
specifier|private
name|long
name|lastBlockFP
decl_stmt|;
DECL|field|blockReader
specifier|private
specifier|final
name|BlockReader
name|blockReader
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|bulkResult
specifier|private
specifier|final
name|IntsRef
name|bulkResult
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
DECL|method|Reader
specifier|public
name|Reader
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|,
specifier|final
name|int
index|[]
name|pending
parameter_list|,
specifier|final
name|BlockReader
name|blockReader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|pending
operator|=
name|pending
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|pending
operator|.
name|length
expr_stmt|;
name|bulkResult
operator|.
name|ints
operator|=
name|pending
expr_stmt|;
name|this
operator|.
name|blockReader
operator|=
name|blockReader
expr_stmt|;
name|upto
operator|=
name|blockSize
expr_stmt|;
block|}
DECL|method|seek
name|void
name|seek
parameter_list|(
specifier|final
name|long
name|fp
parameter_list|,
specifier|final
name|int
name|upto
parameter_list|)
block|{
name|pendingFP
operator|=
name|fp
expr_stmt|;
name|pendingUpto
operator|=
name|upto
expr_stmt|;
name|seekPending
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeSeek
specifier|private
name|void
name|maybeSeek
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|seekPending
condition|)
block|{
if|if
condition|(
name|pendingFP
operator|!=
name|lastBlockFP
condition|)
block|{
comment|// need new block
name|in
operator|.
name|seek
argument_list|(
name|pendingFP
argument_list|)
expr_stmt|;
name|lastBlockFP
operator|=
name|pendingFP
expr_stmt|;
name|blockReader
operator|.
name|readBlock
argument_list|()
expr_stmt|;
block|}
name|upto
operator|=
name|pendingUpto
expr_stmt|;
name|seekPending
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|maybeSeek
argument_list|()
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|blockSize
condition|)
block|{
name|lastBlockFP
operator|=
name|in
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|blockReader
operator|.
name|readBlock
argument_list|()
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|pending
index|[
name|upto
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|IntsRef
name|read
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|maybeSeek
argument_list|()
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|blockSize
condition|)
block|{
name|blockReader
operator|.
name|readBlock
argument_list|()
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
name|bulkResult
operator|.
name|offset
operator|=
name|upto
expr_stmt|;
if|if
condition|(
name|upto
operator|+
name|count
operator|<
name|blockSize
condition|)
block|{
name|bulkResult
operator|.
name|length
operator|=
name|count
expr_stmt|;
name|upto
operator|+=
name|count
expr_stmt|;
block|}
else|else
block|{
name|bulkResult
operator|.
name|length
operator|=
name|blockSize
operator|-
name|upto
expr_stmt|;
name|upto
operator|=
name|blockSize
expr_stmt|;
block|}
return|return
name|bulkResult
return|;
block|}
block|}
DECL|class|Index
specifier|private
class|class
name|Index
extends|extends
name|IntIndexInput
operator|.
name|Index
block|{
DECL|field|fp
specifier|private
name|long
name|fp
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
annotation|@
name|Override
DECL|method|read
specifier|public
name|void
name|read
parameter_list|(
specifier|final
name|IndexInput
name|indexIn
parameter_list|,
specifier|final
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|absolute
condition|)
block|{
name|fp
operator|=
name|indexIn
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|upto
operator|=
name|indexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|delta
init|=
name|indexIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|delta
operator|==
literal|0
condition|)
block|{
comment|// same block
name|upto
operator|+=
name|indexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// new block
name|fp
operator|+=
name|delta
expr_stmt|;
name|upto
operator|=
name|indexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|upto
operator|<
name|blockSize
assert|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
specifier|final
name|IntIndexInput
operator|.
name|Reader
name|other
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|Reader
operator|)
name|other
operator|)
operator|.
name|seek
argument_list|(
name|fp
argument_list|,
name|upto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
specifier|final
name|IntIndexInput
operator|.
name|Index
name|other
parameter_list|)
block|{
specifier|final
name|Index
name|idx
init|=
operator|(
name|Index
operator|)
name|other
decl_stmt|;
name|fp
operator|=
name|idx
operator|.
name|fp
expr_stmt|;
name|upto
operator|=
name|idx
operator|.
name|upto
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
name|Index
name|other
init|=
operator|new
name|Index
argument_list|()
decl_stmt|;
name|other
operator|.
name|fp
operator|=
name|fp
expr_stmt|;
name|other
operator|.
name|upto
operator|=
name|upto
expr_stmt|;
return|return
name|other
return|;
block|}
block|}
block|}
end_class

end_unit

