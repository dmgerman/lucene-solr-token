begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.blockpacked
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blockpacked
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
name|Arrays
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
name|codecs
operator|.
name|MultiLevelSkipListReader
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
comment|/**  * Implements the skip list reader for block postings format  * that stores positions and payloads.  *   * Although this skipper uses MultiLevelSkipListReader as an interface,   * its definition of skip position will be a little different.   *  * For example, when skipInterval = blockSize = 3, df = 2*skipInterval = 6,   *   * 0 1 2 3 4 5  * d d d d d d    (posting list)  *     ^     ^    (skip point in MultiLeveSkipWriter)  *       ^        (skip point in BlockSkipWriter)  *  * In this case, MultiLevelSkipListReader will use the last document as a skip point,   * while BlockSkipReader should assume no skip point will comes.   *  * If we use the interface directly in BlockSkipReader, it may silly try to read   * another skip data after the only skip point is loaded.   *  * To illustrate this, we can call skipTo(d[5]), since skip point d[3] has smaller docId,  * and numSkipped+blockSize== df, the MultiLevelSkipListReader will assume the skip list  * isn't exhausted yet, and try to load a non-existed skip point  *  * Therefore, we'll trim df before passing it to the interface. see trim(int)  *  */
end_comment

begin_class
DECL|class|BlockPackedSkipReader
specifier|final
class|class
name|BlockPackedSkipReader
extends|extends
name|MultiLevelSkipListReader
block|{
DECL|field|DEBUG
specifier|private
name|boolean
name|DEBUG
init|=
name|BlockPackedPostingsReader
operator|.
name|DEBUG
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|docPointer
specifier|private
name|long
name|docPointer
index|[]
decl_stmt|;
DECL|field|posPointer
specifier|private
name|long
name|posPointer
index|[]
decl_stmt|;
DECL|field|payPointer
specifier|private
name|long
name|payPointer
index|[]
decl_stmt|;
DECL|field|posBufferUpto
specifier|private
name|int
name|posBufferUpto
index|[]
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
index|[]
decl_stmt|;
DECL|field|payloadByteUpto
specifier|private
name|int
name|payloadByteUpto
index|[]
decl_stmt|;
DECL|field|lastPosPointer
specifier|private
name|long
name|lastPosPointer
decl_stmt|;
DECL|field|lastPayPointer
specifier|private
name|long
name|lastPayPointer
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
name|lastStartOffset
decl_stmt|;
DECL|field|lastPayloadByteUpto
specifier|private
name|int
name|lastPayloadByteUpto
decl_stmt|;
DECL|field|lastDocPointer
specifier|private
name|long
name|lastDocPointer
decl_stmt|;
DECL|field|lastPosBufferUpto
specifier|private
name|int
name|lastPosBufferUpto
decl_stmt|;
DECL|method|BlockPackedSkipReader
specifier|public
name|BlockPackedSkipReader
parameter_list|(
name|IndexInput
name|skipStream
parameter_list|,
name|int
name|maxSkipLevels
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|boolean
name|hasPos
parameter_list|,
name|boolean
name|hasOffsets
parameter_list|,
name|boolean
name|hasPayloads
parameter_list|)
block|{
name|super
argument_list|(
name|skipStream
argument_list|,
name|maxSkipLevels
argument_list|,
name|blockSize
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|docPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
if|if
condition|(
name|hasPos
condition|)
block|{
name|posPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|posBufferUpto
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|payloadByteUpto
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
else|else
block|{
name|payloadByteUpto
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|startOffset
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
else|else
block|{
name|startOffset
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|hasOffsets
operator|||
name|hasPayloads
condition|)
block|{
name|payPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
else|else
block|{
name|payPointer
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|posPointer
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Trim original docFreq to tell skipReader read proper number of skip points.    *    * Since our definition in BlockSkip* is a little different from MultiLevelSkip*    * This trimed docFreq will prevent skipReader from:    * 1. silly reading a non-existed skip point after the last block boundary    * 2. moving into the vInt block    *    */
DECL|method|trim
specifier|protected
name|int
name|trim
parameter_list|(
name|int
name|df
parameter_list|)
block|{
return|return
name|df
operator|%
name|blockSize
operator|==
literal|0
condition|?
name|df
operator|-
literal|1
else|:
name|df
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|long
name|skipPointer
parameter_list|,
name|long
name|docBasePointer
parameter_list|,
name|long
name|posBasePointer
parameter_list|,
name|long
name|payBasePointer
parameter_list|,
name|int
name|df
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|skipPointer
argument_list|,
name|trim
argument_list|(
name|df
argument_list|)
argument_list|)
expr_stmt|;
name|lastDocPointer
operator|=
name|docBasePointer
expr_stmt|;
name|lastPosPointer
operator|=
name|posBasePointer
expr_stmt|;
name|lastPayPointer
operator|=
name|payBasePointer
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|docPointer
argument_list|,
name|docBasePointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|posPointer
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|posPointer
argument_list|,
name|posBasePointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|payPointer
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|payPointer
argument_list|,
name|payBasePointer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|posBasePointer
operator|==
literal|0
assert|;
block|}
block|}
comment|/** Returns the doc pointer of the doc to which the last call of     * {@link MultiLevelSkipListReader#skipTo(int)} has skipped.  */
DECL|method|getDocPointer
specifier|public
name|long
name|getDocPointer
parameter_list|()
block|{
return|return
name|lastDocPointer
return|;
block|}
DECL|method|getPosPointer
specifier|public
name|long
name|getPosPointer
parameter_list|()
block|{
return|return
name|lastPosPointer
return|;
block|}
DECL|method|getPosBufferUpto
specifier|public
name|int
name|getPosBufferUpto
parameter_list|()
block|{
return|return
name|lastPosBufferUpto
return|;
block|}
DECL|method|getPayPointer
specifier|public
name|long
name|getPayPointer
parameter_list|()
block|{
return|return
name|lastPayPointer
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|lastStartOffset
return|;
block|}
DECL|method|getPayloadByteUpto
specifier|public
name|int
name|getPayloadByteUpto
parameter_list|()
block|{
return|return
name|lastPayloadByteUpto
return|;
block|}
annotation|@
name|Override
DECL|method|seekChild
specifier|protected
name|void
name|seekChild
parameter_list|(
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|seekChild
argument_list|(
name|level
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"seekChild level="
operator|+
name|level
argument_list|)
expr_stmt|;
block|}
name|docPointer
index|[
name|level
index|]
operator|=
name|lastDocPointer
expr_stmt|;
if|if
condition|(
name|posPointer
operator|!=
literal|null
condition|)
block|{
name|posPointer
index|[
name|level
index|]
operator|=
name|lastPosPointer
expr_stmt|;
name|posBufferUpto
index|[
name|level
index|]
operator|=
name|lastPosBufferUpto
expr_stmt|;
if|if
condition|(
name|startOffset
operator|!=
literal|null
condition|)
block|{
name|startOffset
index|[
name|level
index|]
operator|=
name|lastStartOffset
expr_stmt|;
block|}
if|if
condition|(
name|payloadByteUpto
operator|!=
literal|null
condition|)
block|{
name|payloadByteUpto
index|[
name|level
index|]
operator|=
name|lastPayloadByteUpto
expr_stmt|;
block|}
if|if
condition|(
name|payPointer
operator|!=
literal|null
condition|)
block|{
name|payPointer
index|[
name|level
index|]
operator|=
name|lastPayPointer
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|setLastSkipData
specifier|protected
name|void
name|setLastSkipData
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|super
operator|.
name|setLastSkipData
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|lastDocPointer
operator|=
name|docPointer
index|[
name|level
index|]
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"setLastSkipData level="
operator|+
name|level
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  lastDocPointer="
operator|+
name|lastDocPointer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|posPointer
operator|!=
literal|null
condition|)
block|{
name|lastPosPointer
operator|=
name|posPointer
index|[
name|level
index|]
expr_stmt|;
name|lastPosBufferUpto
operator|=
name|posBufferUpto
index|[
name|level
index|]
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  lastPosPointer="
operator|+
name|lastPosPointer
operator|+
literal|" lastPosBUfferUpto="
operator|+
name|lastPosBufferUpto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payPointer
operator|!=
literal|null
condition|)
block|{
name|lastPayPointer
operator|=
name|payPointer
index|[
name|level
index|]
expr_stmt|;
block|}
if|if
condition|(
name|startOffset
operator|!=
literal|null
condition|)
block|{
name|lastStartOffset
operator|=
name|startOffset
index|[
name|level
index|]
expr_stmt|;
block|}
if|if
condition|(
name|payloadByteUpto
operator|!=
literal|null
condition|)
block|{
name|lastPayloadByteUpto
operator|=
name|payloadByteUpto
index|[
name|level
index|]
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|readSkipData
specifier|protected
name|int
name|readSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexInput
name|skipStream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"readSkipData level="
operator|+
name|level
argument_list|)
expr_stmt|;
block|}
name|int
name|delta
init|=
name|skipStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  delta="
operator|+
name|delta
argument_list|)
expr_stmt|;
block|}
name|docPointer
index|[
name|level
index|]
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  docFP="
operator|+
name|docPointer
index|[
name|level
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|posPointer
operator|!=
literal|null
condition|)
block|{
name|posPointer
index|[
name|level
index|]
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  posFP="
operator|+
name|posPointer
index|[
name|level
index|]
argument_list|)
expr_stmt|;
block|}
name|posBufferUpto
index|[
name|level
index|]
operator|=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  posBufferUpto="
operator|+
name|posBufferUpto
index|[
name|level
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payloadByteUpto
operator|!=
literal|null
condition|)
block|{
name|payloadByteUpto
index|[
name|level
index|]
operator|=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|startOffset
operator|!=
literal|null
condition|)
block|{
name|startOffset
index|[
name|level
index|]
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|payPointer
operator|!=
literal|null
condition|)
block|{
name|payPointer
index|[
name|level
index|]
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|delta
return|;
block|}
block|}
end_class

end_unit

