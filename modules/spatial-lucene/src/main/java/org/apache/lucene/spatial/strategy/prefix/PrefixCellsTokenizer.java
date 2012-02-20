begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.strategy.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|strategy
operator|.
name|prefix
package|;
end_package

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
name|Reader
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
name|Tokenizer
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PrefixCellsTokenizer
class|class
name|PrefixCellsTokenizer
extends|extends
name|Tokenizer
block|{
DECL|method|PrefixCellsTokenizer
specifier|public
name|PrefixCellsTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|char
name|c
init|=
operator|(
name|char
operator|)
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
break|break;
if|if
condition|(
name|c
operator|==
literal|'a'
operator|||
name|c
operator|==
literal|'A'
condition|)
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|c
operator|==
literal|'b'
operator|||
name|c
operator|==
literal|'B'
condition|)
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
literal|'B'
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|c
operator|==
literal|'c'
operator|||
name|c
operator|==
literal|'C'
condition|)
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
literal|'C'
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|c
operator|==
literal|'d'
operator|||
name|c
operator|==
literal|'D'
condition|)
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
literal|'D'
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|c
operator|==
literal|'*'
condition|)
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
literal|'*'
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|c
operator|==
literal|'+'
condition|)
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
literal|'+'
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
comment|// Skip any other character
break|break;
block|}
block|}
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
return|return
name|length
operator|>
literal|0
return|;
comment|// should only happen at the end
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{    }
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

