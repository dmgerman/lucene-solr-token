begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cn
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
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
name|*
import|;
end_import

begin_comment
comment|/**  * Title: ChineseTokenizer  * Description: Extract tokens from the Stream using Character.getType()  *              Rule: A Chinese character as a single token  * Copyright:   Copyright (c) 2001  * Company:  *  * The difference between thr ChineseTokenizer and the  * CJKTokenizer (id=23545) is that they have different  * token parsing logic.  *   * Let me use an example. If having a Chinese text  * "C1C2C3C4" to be indexed, the tokens returned from the  * ChineseTokenizer are C1, C2, C3, C4. And the tokens  * returned from the CJKTokenizer are C1C2, C2C3, C3C4.  *  * Therefore the index the CJKTokenizer created is much  * larger.  *  * The problem is that when searching for C1, C1C2, C1C3,  * C4C2, C1C2C3 ... the ChineseTokenizer works, but the  * CJKTokenizer will not work.  *  * @author Yiyi Sun  * @version 1.0  *  */
end_comment

begin_class
DECL|class|ChineseTokenizer
specifier|public
specifier|final
class|class
name|ChineseTokenizer
extends|extends
name|Tokenizer
block|{
DECL|method|ChineseTokenizer
specifier|public
name|ChineseTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|input
operator|=
name|in
expr_stmt|;
block|}
DECL|field|offset
DECL|field|bufferIndex
DECL|field|dataLen
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|,
name|bufferIndex
init|=
literal|0
decl_stmt|,
name|dataLen
init|=
literal|0
decl_stmt|;
DECL|field|MAX_WORD_LEN
specifier|private
specifier|final
specifier|static
name|int
name|MAX_WORD_LEN
init|=
literal|255
decl_stmt|;
DECL|field|IO_BUFFER_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|IO_BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|MAX_WORD_LEN
index|]
decl_stmt|;
DECL|field|ioBuffer
specifier|private
specifier|final
name|char
index|[]
name|ioBuffer
init|=
operator|new
name|char
index|[
name|IO_BUFFER_SIZE
index|]
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|method|push
specifier|private
specifier|final
name|void
name|push
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
name|start
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
comment|// start of token
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// buffer it
block|}
DECL|method|flush
specifier|private
specifier|final
name|Token
name|flush
parameter_list|()
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
comment|//System.out.println(new String(buffer, 0, length));
return|return
operator|new
name|Token
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|length
argument_list|)
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|length
operator|=
literal|0
expr_stmt|;
name|start
operator|=
name|offset
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|char
name|c
decl_stmt|;
name|offset
operator|++
expr_stmt|;
if|if
condition|(
name|bufferIndex
operator|>=
name|dataLen
condition|)
block|{
name|dataLen
operator|=
name|input
operator|.
name|read
argument_list|(
name|ioBuffer
argument_list|)
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
empty_stmt|;
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
return|return
name|flush
argument_list|()
return|;
else|else
name|c
operator|=
name|ioBuffer
index|[
name|bufferIndex
operator|++
index|]
expr_stmt|;
switch|switch
condition|(
name|Character
operator|.
name|getType
argument_list|(
name|c
argument_list|)
condition|)
block|{
case|case
name|Character
operator|.
name|DECIMAL_DIGIT_NUMBER
case|:
case|case
name|Character
operator|.
name|LOWERCASE_LETTER
case|:
case|case
name|Character
operator|.
name|UPPERCASE_LETTER
case|:
name|push
argument_list|(
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|==
name|MAX_WORD_LEN
condition|)
return|return
name|flush
argument_list|()
return|;
break|break;
case|case
name|Character
operator|.
name|OTHER_LETTER
case|:
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|bufferIndex
operator|--
expr_stmt|;
name|offset
operator|--
expr_stmt|;
return|return
name|flush
argument_list|()
return|;
block|}
name|push
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|flush
argument_list|()
return|;
default|default:
if|if
condition|(
name|length
operator|>
literal|0
condition|)
return|return
name|flush
argument_list|()
return|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

