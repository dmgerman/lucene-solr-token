begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
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
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|charfilter
operator|.
name|BaseCharFilter
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
name|CharStream
import|;
end_import

begin_comment
comment|/**  * CharFilter that uses a regular expression for the target of replace string.  * The pattern match will be done in each "block" in char stream.  *   *<p>  * ex1) source="aa&nbsp;&nbsp;bb&nbsp;aa&nbsp;bb", pattern="(aa)\\s+(bb)" replacement="$1#$2"<br/>  * output="aa#bb&nbsp;aa#bb"  *</p>  *   * NOTE: If you produce a phrase that has different length to source string  * and the field is used for highlighting for a term of the phrase, you will  * face a trouble.  *   *<p>  * ex2) source="aa123bb", pattern="(aa)\\d+(bb)" replacement="$1&nbsp;$2"<br/>  * output="aa&nbsp;bb"<br/>  * and you want to search bb and highlight it, you will get<br/>  * highlight snippet="aa1&lt;em&gt;23bb&lt;/em&gt;"  *</p>  *   * @since Solr 1.5  */
end_comment

begin_class
DECL|class|PatternReplaceCharFilter
specifier|public
class|class
name|PatternReplaceCharFilter
extends|extends
name|BaseCharFilter
block|{
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|replacement
specifier|private
specifier|final
name|String
name|replacement
decl_stmt|;
DECL|field|maxBlockChars
specifier|private
specifier|final
name|int
name|maxBlockChars
decl_stmt|;
DECL|field|blockDelimiters
specifier|private
specifier|final
name|String
name|blockDelimiters
decl_stmt|;
DECL|field|DEFAULT_MAX_BLOCK_CHARS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_BLOCK_CHARS
init|=
literal|10000
decl_stmt|;
DECL|field|buffer
specifier|private
name|LinkedList
argument_list|<
name|Character
argument_list|>
name|buffer
decl_stmt|;
DECL|field|nextCharCounter
specifier|private
name|int
name|nextCharCounter
decl_stmt|;
DECL|field|blockBuffer
specifier|private
name|char
index|[]
name|blockBuffer
decl_stmt|;
DECL|field|blockBufferLength
specifier|private
name|int
name|blockBufferLength
decl_stmt|;
DECL|field|replaceBlockBuffer
specifier|private
name|String
name|replaceBlockBuffer
decl_stmt|;
DECL|field|replaceBlockBufferOffset
specifier|private
name|int
name|replaceBlockBufferOffset
decl_stmt|;
DECL|method|PatternReplaceCharFilter
specifier|public
name|PatternReplaceCharFilter
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|String
name|replacement
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|pattern
argument_list|,
name|replacement
argument_list|,
name|DEFAULT_MAX_BLOCK_CHARS
argument_list|,
literal|null
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|PatternReplaceCharFilter
specifier|public
name|PatternReplaceCharFilter
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|String
name|replacement
parameter_list|,
name|int
name|maxBlockChars
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|pattern
argument_list|,
name|replacement
argument_list|,
name|maxBlockChars
argument_list|,
literal|null
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|PatternReplaceCharFilter
specifier|public
name|PatternReplaceCharFilter
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|String
name|replacement
parameter_list|,
name|String
name|blockDelimiters
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|pattern
argument_list|,
name|replacement
argument_list|,
name|DEFAULT_MAX_BLOCK_CHARS
argument_list|,
name|blockDelimiters
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|PatternReplaceCharFilter
specifier|public
name|PatternReplaceCharFilter
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|String
name|replacement
parameter_list|,
name|int
name|maxBlockChars
parameter_list|,
name|String
name|blockDelimiters
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
expr_stmt|;
if|if
condition|(
name|maxBlockChars
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxBlockChars should be greater than 0, but it is "
operator|+
name|maxBlockChars
argument_list|)
throw|;
name|this
operator|.
name|maxBlockChars
operator|=
name|maxBlockChars
expr_stmt|;
name|this
operator|.
name|blockDelimiters
operator|=
name|blockDelimiters
expr_stmt|;
name|blockBuffer
operator|=
operator|new
name|char
index|[
name|maxBlockChars
index|]
expr_stmt|;
block|}
DECL|method|prepareReplaceBlock
specifier|private
name|boolean
name|prepareReplaceBlock
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|replaceBlockBuffer
operator|!=
literal|null
operator|&&
name|replaceBlockBuffer
operator|.
name|length
argument_list|()
operator|>
name|replaceBlockBufferOffset
condition|)
return|return
literal|true
return|;
comment|// prepare block buffer
name|blockBufferLength
operator|=
literal|0
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|c
init|=
name|nextChar
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
break|break;
name|blockBuffer
index|[
name|blockBufferLength
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
comment|// end of block?
name|boolean
name|foundDelimiter
init|=
operator|(
name|blockDelimiters
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|blockDelimiters
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
operator|&&
name|blockDelimiters
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
decl_stmt|;
if|if
condition|(
name|foundDelimiter
operator|||
name|blockBufferLength
operator|>=
name|maxBlockChars
condition|)
break|break;
block|}
comment|// block buffer available?
if|if
condition|(
name|blockBufferLength
operator|==
literal|0
condition|)
return|return
literal|false
return|;
name|replaceBlockBuffer
operator|=
name|getReplaceBlock
argument_list|(
name|blockBuffer
argument_list|,
literal|0
argument_list|,
name|blockBufferLength
argument_list|)
expr_stmt|;
name|replaceBlockBufferOffset
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|prepareReplaceBlock
argument_list|()
condition|)
block|{
return|return
name|replaceBlockBuffer
operator|.
name|charAt
argument_list|(
name|replaceBlockBufferOffset
operator|++
argument_list|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|char
index|[]
name|tmp
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
name|int
name|l
init|=
name|input
operator|.
name|read
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
operator|-
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
condition|;
name|i
operator|++
control|)
name|pushLastChar
argument_list|(
name|tmp
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|l
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off
operator|+
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
break|break;
name|cbuf
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
name|l
operator|++
expr_stmt|;
block|}
return|return
name|l
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|l
return|;
block|}
DECL|method|nextChar
specifier|private
name|int
name|nextChar
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
operator|&&
operator|!
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nextCharCounter
operator|++
expr_stmt|;
return|return
name|buffer
operator|.
name|removeFirst
argument_list|()
operator|.
name|charValue
argument_list|()
return|;
block|}
name|int
name|c
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|!=
operator|-
literal|1
condition|)
name|nextCharCounter
operator|++
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|pushLastChar
specifier|private
name|void
name|pushLastChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|LinkedList
argument_list|<
name|Character
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|buffer
operator|.
name|addLast
argument_list|(
operator|new
name|Character
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getReplaceBlock
name|String
name|getReplaceBlock
parameter_list|(
name|String
name|block
parameter_list|)
block|{
name|char
index|[]
name|blockChars
init|=
name|block
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
return|return
name|getReplaceBlock
argument_list|(
name|blockChars
argument_list|,
literal|0
argument_list|,
name|blockChars
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|getReplaceBlock
name|String
name|getReplaceBlock
parameter_list|(
name|char
name|block
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|StringBuffer
name|replaceBlock
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|sourceBlock
init|=
operator|new
name|String
argument_list|(
name|block
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|sourceBlock
argument_list|)
decl_stmt|;
name|int
name|lastMatchOffset
init|=
literal|0
decl_stmt|,
name|lastDiff
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|m
operator|.
name|appendReplacement
argument_list|(
name|replaceBlock
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
comment|// record cumulative diff for the offset correction
name|int
name|diff
init|=
name|replaceBlock
operator|.
name|length
argument_list|()
operator|-
name|lastMatchOffset
operator|-
name|lastDiff
operator|-
operator|(
name|m
operator|.
name|end
argument_list|(
literal|0
argument_list|)
operator|-
name|lastMatchOffset
operator|)
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
name|int
name|prevCumulativeDiff
init|=
name|getLastCumulativeDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|diff
condition|;
name|i
operator|++
control|)
block|{
name|addOffCorrectMap
argument_list|(
name|nextCharCounter
operator|-
name|length
operator|+
name|m
operator|.
name|end
argument_list|(
literal|0
argument_list|)
operator|+
name|i
operator|-
name|prevCumulativeDiff
argument_list|,
name|prevCumulativeDiff
operator|-
literal|1
operator|-
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|addOffCorrectMap
argument_list|(
name|nextCharCounter
operator|-
name|length
operator|+
name|m
operator|.
name|end
argument_list|(
literal|0
argument_list|)
operator|+
name|diff
operator|-
name|prevCumulativeDiff
argument_list|,
name|prevCumulativeDiff
operator|-
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
comment|// save last offsets
name|lastMatchOffset
operator|=
name|m
operator|.
name|end
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|lastDiff
operator|=
name|diff
expr_stmt|;
block|}
comment|// copy remaining of the part of source block
name|m
operator|.
name|appendTail
argument_list|(
name|replaceBlock
argument_list|)
expr_stmt|;
return|return
name|replaceBlock
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

