begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|BytesTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * TokenStream from a canned list of binary (BytesRef-based)  * tokens.  */
end_comment

begin_class
DECL|class|CannedBinaryTokenStream
specifier|public
specifier|final
class|class
name|CannedBinaryTokenStream
extends|extends
name|TokenStream
block|{
comment|/** Represents a binary token. */
DECL|class|BinaryToken
specifier|public
specifier|final
specifier|static
class|class
name|BinaryToken
block|{
DECL|field|term
name|BytesRef
name|term
decl_stmt|;
DECL|field|posInc
name|int
name|posInc
decl_stmt|;
DECL|field|posLen
name|int
name|posLen
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|method|BinaryToken
specifier|public
name|BinaryToken
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|posInc
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|posLen
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|BinaryToken
specifier|public
name|BinaryToken
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|posInc
parameter_list|,
name|int
name|posLen
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|posInc
operator|=
name|posInc
expr_stmt|;
name|this
operator|.
name|posLen
operator|=
name|posLen
expr_stmt|;
block|}
block|}
DECL|field|tokens
specifier|private
specifier|final
name|BinaryToken
index|[]
name|tokens
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
init|=
literal|0
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|BytesTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|BytesTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posLengthAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLengthAtt
init|=
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CannedBinaryTokenStream
specifier|public
name|CannedBinaryTokenStream
parameter_list|(
name|BinaryToken
modifier|...
name|tokens
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|<
name|tokens
operator|.
name|length
condition|)
block|{
specifier|final
name|BinaryToken
name|token
init|=
name|tokens
index|[
name|upto
operator|++
index|]
decl_stmt|;
comment|// TODO: can we just capture/restoreState so
comment|// we get all attrs...?
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setBytesRef
argument_list|(
name|token
operator|.
name|term
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|token
operator|.
name|posInc
argument_list|)
expr_stmt|;
name|posLengthAtt
operator|.
name|setPositionLength
argument_list|(
name|token
operator|.
name|posLen
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|,
name|token
operator|.
name|endOffset
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

