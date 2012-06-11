begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|TokenFilter
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
name|TokenStream
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
name|CharTermAttribute
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Trims leading and trailing whitespace from Tokens in the stream.  */
end_comment

begin_class
DECL|class|TrimFilter
specifier|public
specifier|final
class|class
name|TrimFilter
extends|extends
name|TokenFilter
block|{
DECL|field|updateOffsets
specifier|final
name|boolean
name|updateOffsets
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
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
DECL|method|TrimFilter
specifier|public
name|TrimFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|boolean
name|updateOffsets
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|updateOffsets
operator|=
name|updateOffsets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
comment|//TODO: Is this the right behavior or should we return false?  Currently, "  ", returns true, so I think this should
comment|//also return true
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
literal|0
decl_stmt|;
name|int
name|endOff
init|=
literal|0
decl_stmt|;
comment|// eat the first characters
comment|//QUESTION: Should we use Character.isWhitespace() instead?
for|for
control|(
name|start
operator|=
literal|0
init|;
name|start
operator|<
name|len
operator|&&
name|termBuffer
index|[
name|start
index|]
operator|<=
literal|' '
condition|;
name|start
operator|++
control|)
block|{     }
comment|// eat the end characters
for|for
control|(
name|end
operator|=
name|len
init|;
name|end
operator|>=
name|start
operator|&&
name|termBuffer
index|[
name|end
operator|-
literal|1
index|]
operator|<=
literal|' '
condition|;
name|end
operator|--
control|)
block|{
name|endOff
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|start
operator|>
literal|0
operator|||
name|end
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|start
operator|<
name|end
condition|)
block|{
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|termBuffer
argument_list|,
name|start
argument_list|,
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termAtt
operator|.
name|setEmpty
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|updateOffsets
operator|&&
name|len
operator|==
name|offsetAtt
operator|.
name|endOffset
argument_list|()
operator|-
name|offsetAtt
operator|.
name|startOffset
argument_list|()
condition|)
block|{
name|int
name|newStart
init|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
operator|+
name|start
decl_stmt|;
name|int
name|newEnd
init|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
operator|-
operator|(
name|start
operator|<
name|end
condition|?
name|endOff
else|:
literal|0
operator|)
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|newStart
argument_list|,
name|newEnd
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

