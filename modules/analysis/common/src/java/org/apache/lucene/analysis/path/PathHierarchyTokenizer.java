begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.path
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|path
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

begin_comment
comment|/**  *  * Take something like:  *  *<pre>  *  /something/something/else  *</pre>  *  * and make:  *  *<pre>  *  /something  *  /something/something  *  /something/something/else  *</pre>  */
end_comment

begin_class
DECL|class|PathHierarchyTokenizer
specifier|public
class|class
name|PathHierarchyTokenizer
extends|extends
name|Tokenizer
block|{
DECL|method|PathHierarchyTokenizer
specifier|public
name|PathHierarchyTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|,
name|DEFAULT_DELIMITER
argument_list|,
name|DEFAULT_DELIMITER
argument_list|,
name|DEFAULT_SKIP
argument_list|)
expr_stmt|;
block|}
DECL|method|PathHierarchyTokenizer
specifier|public
name|PathHierarchyTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|int
name|skip
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|,
name|DEFAULT_DELIMITER
argument_list|,
name|DEFAULT_DELIMITER
argument_list|,
name|skip
argument_list|)
expr_stmt|;
block|}
DECL|method|PathHierarchyTokenizer
specifier|public
name|PathHierarchyTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|char
name|delimiter
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|bufferSize
argument_list|,
name|delimiter
argument_list|,
name|delimiter
argument_list|,
name|DEFAULT_SKIP
argument_list|)
expr_stmt|;
block|}
DECL|method|PathHierarchyTokenizer
specifier|public
name|PathHierarchyTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|char
name|delimiter
parameter_list|,
name|char
name|replacement
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|,
name|delimiter
argument_list|,
name|replacement
argument_list|,
name|DEFAULT_SKIP
argument_list|)
expr_stmt|;
block|}
DECL|method|PathHierarchyTokenizer
specifier|public
name|PathHierarchyTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|char
name|delimiter
parameter_list|,
name|char
name|replacement
parameter_list|,
name|int
name|skip
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|,
name|delimiter
argument_list|,
name|replacement
argument_list|,
name|skip
argument_list|)
expr_stmt|;
block|}
DECL|method|PathHierarchyTokenizer
specifier|public
name|PathHierarchyTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|char
name|delimiter
parameter_list|,
name|char
name|replacement
parameter_list|,
name|int
name|skip
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
expr_stmt|;
name|this
operator|.
name|skip
operator|=
name|skip
expr_stmt|;
name|resultToken
operator|=
operator|new
name|StringBuilder
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|DEFAULT_DELIMITER
specifier|public
specifier|static
specifier|final
name|char
name|DEFAULT_DELIMITER
init|=
literal|'/'
decl_stmt|;
DECL|field|DEFAULT_SKIP
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SKIP
init|=
literal|0
decl_stmt|;
DECL|field|delimiter
specifier|private
specifier|final
name|char
name|delimiter
decl_stmt|;
DECL|field|replacement
specifier|private
specifier|final
name|char
name|replacement
decl_stmt|;
DECL|field|skip
specifier|private
specifier|final
name|int
name|skip
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
DECL|field|posAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|startPosition
specifier|private
name|int
name|startPosition
init|=
literal|0
decl_stmt|;
DECL|field|finalOffset
specifier|private
name|int
name|finalOffset
init|=
literal|0
decl_stmt|;
DECL|field|skipped
specifier|private
name|int
name|skipped
init|=
literal|0
decl_stmt|;
DECL|field|endDelimiter
specifier|private
name|boolean
name|endDelimiter
init|=
literal|false
decl_stmt|;
DECL|field|resultToken
specifier|private
name|StringBuilder
name|resultToken
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
name|termAtt
operator|.
name|append
argument_list|(
name|resultToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|resultToken
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|length
init|=
literal|0
decl_stmt|;
name|boolean
name|added
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|endDelimiter
condition|)
block|{
name|termAtt
operator|.
name|append
argument_list|(
name|replacement
argument_list|)
expr_stmt|;
name|length
operator|++
expr_stmt|;
name|endDelimiter
operator|=
literal|false
expr_stmt|;
name|added
operator|=
literal|true
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
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
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|skipped
operator|>
name|skip
condition|)
block|{
name|length
operator|+=
name|resultToken
operator|.
name|length
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|startPosition
operator|+
name|length
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|startPosition
argument_list|)
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|added
condition|)
block|{
name|resultToken
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|resultToken
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|added
return|;
block|}
else|else
block|{
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|startPosition
operator|+
name|length
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
name|added
condition|)
block|{
name|added
operator|=
literal|true
expr_stmt|;
name|skipped
operator|++
expr_stmt|;
if|if
condition|(
name|skipped
operator|>
name|skip
condition|)
block|{
name|termAtt
operator|.
name|append
argument_list|(
name|c
operator|==
name|delimiter
condition|?
name|replacement
else|:
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
name|length
operator|++
expr_stmt|;
block|}
else|else
block|{
name|startPosition
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|c
operator|==
name|delimiter
condition|)
block|{
if|if
condition|(
name|skipped
operator|>
name|skip
condition|)
block|{
name|endDelimiter
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|skipped
operator|++
expr_stmt|;
if|if
condition|(
name|skipped
operator|>
name|skip
condition|)
block|{
name|termAtt
operator|.
name|append
argument_list|(
name|replacement
argument_list|)
expr_stmt|;
name|length
operator|++
expr_stmt|;
block|}
else|else
block|{
name|startPosition
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|skipped
operator|>
name|skip
condition|)
block|{
name|termAtt
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
name|length
operator|++
expr_stmt|;
block|}
else|else
block|{
name|startPosition
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
name|length
operator|+=
name|resultToken
operator|.
name|length
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|startPosition
operator|+
name|length
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|startPosition
argument_list|)
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
name|resultToken
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|resultToken
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
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
name|resultToken
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|finalOffset
operator|=
literal|0
expr_stmt|;
name|endDelimiter
operator|=
literal|false
expr_stmt|;
name|skipped
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

