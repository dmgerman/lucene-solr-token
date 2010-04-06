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
name|IntIndexOutput
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
comment|/** Abstract base class that writes fixed-size blocks of ints  *  to an IndexOutput.  While this is a simple approach, a  *  more performant approach would directly create an impl  *  of IntIndexOutput inside Directory.  Wrapping a generic  *  IndexInput will likely cost performance.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FixedIntBlockIndexOutput
specifier|public
specifier|abstract
class|class
name|FixedIntBlockIndexOutput
extends|extends
name|IntIndexOutput
block|{
DECL|field|out
specifier|private
name|IndexOutput
name|out
decl_stmt|;
DECL|field|blockSize
specifier|private
name|int
name|blockSize
decl_stmt|;
DECL|field|pending
specifier|private
name|int
index|[]
name|pending
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|int
name|fixedBlockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|blockSize
operator|=
name|fixedBlockSize
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|pending
operator|=
operator|new
name|int
index|[
name|blockSize
index|]
expr_stmt|;
block|}
DECL|method|flushBlock
specifier|protected
specifier|abstract
name|void
name|flushBlock
parameter_list|(
name|int
index|[]
name|buffer
parameter_list|,
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Index
argument_list|()
return|;
block|}
DECL|class|Index
specifier|private
class|class
name|Index
extends|extends
name|IntIndexOutput
operator|.
name|Index
block|{
DECL|field|fp
name|long
name|fp
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|lastFP
name|long
name|lastFP
decl_stmt|;
DECL|field|lastUpto
name|int
name|lastUpto
decl_stmt|;
annotation|@
name|Override
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|()
throws|throws
name|IOException
block|{
name|fp
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|upto
operator|=
name|FixedIntBlockIndexOutput
operator|.
name|this
operator|.
name|upto
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|IntIndexOutput
operator|.
name|Index
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|Index
name|idx
init|=
operator|(
name|Index
operator|)
name|other
decl_stmt|;
name|lastFP
operator|=
name|fp
operator|=
name|idx
operator|.
name|fp
expr_stmt|;
name|lastUpto
operator|=
name|upto
operator|=
name|idx
operator|.
name|upto
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|IndexOutput
name|indexOut
parameter_list|,
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
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|indexOut
operator|.
name|writeVInt
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fp
operator|==
name|lastFP
condition|)
block|{
comment|// same block
name|indexOut
operator|.
name|writeVLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
assert|assert
name|upto
operator|>=
name|lastUpto
assert|;
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|upto
operator|-
name|lastUpto
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// new block
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|fp
operator|-
name|lastFP
argument_list|)
expr_stmt|;
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|lastUpto
operator|=
name|upto
expr_stmt|;
name|lastFP
operator|=
name|fp
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|pending
index|[
name|upto
operator|++
index|]
operator|=
name|v
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|blockSize
condition|)
block|{
name|flushBlock
argument_list|(
name|pending
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
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
if|if
condition|(
name|upto
operator|>
literal|0
condition|)
block|{
comment|// NOTE: entries in the block after current upto are
comment|// invalid
name|flushBlock
argument_list|(
name|pending
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

