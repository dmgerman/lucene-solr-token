begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Token
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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * CJKTokenizer was modified from StopTokenizer which does a decent job for  * most European languages. It performs other token methods for double-byte  * Characters: the token will return at each two characters with overlap match.<br>  * Example: "java C1C2C3C4" will be segment to: "java" "C1C2" "C2C3" "C3C4" it  * also need filter filter zero length token ""<br>  * for Digit: digit, '+', '#' will token as letter<br>  * for more info on Asia language(Chinese Japanese Korean) text segmentation:  * please search<a  * href="http://www.google.com/search?q=word+chinese+segment">google</a>  *  */
end_comment

begin_class
DECL|class|CJKTokenizer
specifier|public
specifier|final
class|class
name|CJKTokenizer
extends|extends
name|Tokenizer
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** Word token type */
DECL|field|WORD_TYPE
specifier|static
specifier|final
name|int
name|WORD_TYPE
init|=
literal|0
decl_stmt|;
comment|/** Single byte token type */
DECL|field|SINGLE_TOKEN_TYPE
specifier|static
specifier|final
name|int
name|SINGLE_TOKEN_TYPE
init|=
literal|1
decl_stmt|;
comment|/** Double byte token type */
DECL|field|DOUBLE_TOKEN_TYPE
specifier|static
specifier|final
name|int
name|DOUBLE_TOKEN_TYPE
init|=
literal|2
decl_stmt|;
comment|/** Names for token types */
DECL|field|TOKEN_TYPE_NAMES
specifier|static
specifier|final
name|String
index|[]
name|TOKEN_TYPE_NAMES
init|=
block|{
literal|"word"
block|,
literal|"single"
block|,
literal|"double"
block|}
decl_stmt|;
comment|/** Max word length */
DECL|field|MAX_WORD_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WORD_LEN
init|=
literal|255
decl_stmt|;
comment|/** buffer size: */
DECL|field|IO_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|IO_BUFFER_SIZE
init|=
literal|256
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/** word offset, used to imply which character(in ) is parsed */
DECL|field|offset
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|;
comment|/** the index used only for ioBuffer */
DECL|field|bufferIndex
specifier|private
name|int
name|bufferIndex
init|=
literal|0
decl_stmt|;
comment|/** data length */
DECL|field|dataLen
specifier|private
name|int
name|dataLen
init|=
literal|0
decl_stmt|;
comment|/**      * character buffer, store the characters which are used to compose<br>      * the returned Token      */
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
comment|/**      * I/O buffer, used to store the content of the input(one of the<br>      * members of Tokenizer)      */
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
comment|/** word type: single=>ASCII  double=>non-ASCII word=>default */
DECL|field|tokenType
specifier|private
name|int
name|tokenType
init|=
name|WORD_TYPE
decl_stmt|;
comment|/**      * tag: previous character is a cached double-byte character  "C1C2C3C4"      * ----(set the C1 isTokened) C1C2 "C2C3C4" ----(set the C2 isTokened)      * C1C2 C2C3 "C3C4" ----(set the C3 isTokened) "C1C2 C2C3 C3C4"      */
DECL|field|preIsTokened
specifier|private
name|boolean
name|preIsTokened
init|=
literal|false
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Construct a token stream processing the given input.      *      * @param in I/O reader      */
DECL|method|CJKTokenizer
specifier|public
name|CJKTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the next token in the stream, or null at EOS.      * See http://java.sun.com/j2se/1.3/docs/api/java/lang/Character.UnicodeBlock.html      * for detail.      *      * @param reusableToken a reusable token      * @return Token      *      * @throws java.io.IOException - throw IOException when read error<br>      *         happened in the InputStream      *      */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
comment|/** how many character(s) has been stored in buffer */
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// loop until we find a non-empty token
name|int
name|length
init|=
literal|0
decl_stmt|;
comment|/** the position used to create Token */
name|int
name|start
init|=
name|offset
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// loop until we've found a full token
comment|/** current character */
name|char
name|c
decl_stmt|;
comment|/** unicode block of current character for detail */
name|Character
operator|.
name|UnicodeBlock
name|ub
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
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|//get current character
name|c
operator|=
name|ioBuffer
index|[
name|bufferIndex
operator|++
index|]
expr_stmt|;
comment|//get the UnicodeBlock of the current character
name|ub
operator|=
name|Character
operator|.
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|//if the current character is ASCII or Extend ASCII
if|if
condition|(
operator|(
name|ub
operator|==
name|Character
operator|.
name|UnicodeBlock
operator|.
name|BASIC_LATIN
operator|)
operator|||
operator|(
name|ub
operator|==
name|Character
operator|.
name|UnicodeBlock
operator|.
name|HALFWIDTH_AND_FULLWIDTH_FORMS
operator|)
condition|)
block|{
if|if
condition|(
name|ub
operator|==
name|Character
operator|.
name|UnicodeBlock
operator|.
name|HALFWIDTH_AND_FULLWIDTH_FORMS
condition|)
block|{
name|int
name|i
init|=
operator|(
name|int
operator|)
name|c
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|65281
operator|&&
name|i
operator|<=
literal|65374
condition|)
block|{
comment|// convert certain HALFWIDTH_AND_FULLWIDTH_FORMS to BASIC_LATIN
name|i
operator|=
name|i
operator|-
literal|65248
expr_stmt|;
name|c
operator|=
operator|(
name|char
operator|)
name|i
expr_stmt|;
block|}
block|}
comment|// if the current character is a letter or "_" "+" "#"
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
operator|||
operator|(
operator|(
name|c
operator|==
literal|'_'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'+'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'#'
operator|)
operator|)
condition|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
comment|// "javaC1C2C3C4linux"<br>
comment|//      ^--: the current character begin to token the ASCII
comment|// letter
name|start
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tokenType
operator|==
name|DOUBLE_TOKEN_TYPE
condition|)
block|{
comment|// "javaC1C2C3C4linux"<br>
comment|//              ^--: the previous non-ASCII
comment|// : the current character
name|offset
operator|--
expr_stmt|;
name|bufferIndex
operator|--
expr_stmt|;
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
comment|// there is only one non-ASCII has been stored
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
break|break;
block|}
else|else
block|{
break|break;
block|}
block|}
comment|// store the LowerCase(c) in the buffer
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
name|tokenType
operator|=
name|SINGLE_TOKEN_TYPE
expr_stmt|;
comment|// break the procedure if buffer overflowed!
if|if
condition|(
name|length
operator|==
name|MAX_WORD_LEN
condition|)
block|{
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
else|else
block|{
comment|// non-ASCII letter, e.g."C1C2C3C4"
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
condition|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
name|start
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|c
expr_stmt|;
name|tokenType
operator|=
name|DOUBLE_TOKEN_TYPE
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|tokenType
operator|==
name|SINGLE_TOKEN_TYPE
condition|)
block|{
name|offset
operator|--
expr_stmt|;
name|bufferIndex
operator|--
expr_stmt|;
comment|//return the previous ASCII characters
break|break;
block|}
else|else
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|c
expr_stmt|;
name|tokenType
operator|=
name|DOUBLE_TOKEN_TYPE
expr_stmt|;
if|if
condition|(
name|length
operator|==
literal|2
condition|)
block|{
name|offset
operator|--
expr_stmt|;
name|bufferIndex
operator|--
expr_stmt|;
name|preIsTokened
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
comment|// empty the buffer
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|reusableToken
operator|.
name|reinit
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|input
operator|.
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|input
operator|.
name|correctOffset
argument_list|(
name|start
operator|+
name|length
argument_list|)
argument_list|,
name|TOKEN_TYPE_NAMES
index|[
name|tokenType
index|]
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Cycle back and try for the next token (don't
comment|// return an empty string)
block|}
block|}
block|}
end_class

end_unit

