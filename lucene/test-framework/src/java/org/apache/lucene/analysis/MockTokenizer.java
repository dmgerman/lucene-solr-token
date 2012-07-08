begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_comment
comment|/**  * Tokenizer for testing.  *<p>  * This tokenizer is a replacement for {@link #WHITESPACE}, {@link #SIMPLE}, and {@link #KEYWORD}  * tokenizers. If you are writing a component such as a TokenFilter, its a great idea to test  * it wrapping this tokenizer instead for extra checks. This tokenizer has the following behavior:  *<ul>  *<li>An internal state-machine is used for checking consumer consistency. These checks can  *       be disabled with {@link #setEnableChecks(boolean)}.  *<li>For convenience, optionally lowercases terms that it outputs.  *</ul>  */
end_comment

begin_class
DECL|class|MockTokenizer
specifier|public
class|class
name|MockTokenizer
extends|extends
name|Tokenizer
block|{
comment|/** Acts Similar to WhitespaceTokenizer */
DECL|field|WHITESPACE
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|WHITESPACE
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"[^ \t\r\n]+"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Acts Similar to KeywordTokenizer.    * TODO: Keyword returns an "empty" token for an empty reader...     */
DECL|field|KEYWORD
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|KEYWORD
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|".*"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Acts like LetterTokenizer. */
comment|// the ugly regex below is incomplete Unicode 5.2 [:Letter:]
DECL|field|SIMPLE
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|SIMPLE
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"[A-Za-zÂªÂµÂºÃ-ÃÃ-Ã¶Ã¸-ï¼º]+"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|runAutomaton
specifier|private
specifier|final
name|CharacterRunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|lowerCase
specifier|private
specifier|final
name|boolean
name|lowerCase
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
specifier|final
name|int
name|maxTokenLength
decl_stmt|;
DECL|field|DEFAULT_MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|state
specifier|private
name|int
name|state
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
DECL|field|off
name|int
name|off
init|=
literal|0
decl_stmt|;
comment|// TODO: "register" with LuceneTestCase to ensure all streams are closed() ?
comment|// currently, we can only check that the lifecycle is correct if someone is reusing,
comment|// but not for "one-offs".
DECL|enum|State
specifier|private
specifier|static
enum|enum
name|State
block|{
DECL|enum constant|SETREADER
name|SETREADER
block|,
comment|// consumer set a reader input either via ctor or via reset(Reader)
DECL|enum constant|RESET
name|RESET
block|,
comment|// consumer has called reset()
DECL|enum constant|INCREMENT
name|INCREMENT
block|,
comment|// consumer is consuming, has called incrementToken() == true
DECL|enum constant|INCREMENT_FALSE
name|INCREMENT_FALSE
block|,
comment|// consumer has called incrementToken() which returned false
DECL|enum constant|END
name|END
block|,
comment|// consumer has called end() to perform end of stream operations
DECL|enum constant|CLOSE
name|CLOSE
comment|// consumer has called close() to release any resources
block|}
empty_stmt|;
DECL|field|streamState
specifier|private
name|State
name|streamState
init|=
name|State
operator|.
name|CLOSE
decl_stmt|;
DECL|field|lastOffset
specifier|private
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
comment|// only for asserting
DECL|field|enableChecks
specifier|private
name|boolean
name|enableChecks
init|=
literal|true
decl_stmt|;
DECL|method|MockTokenizer
specifier|public
name|MockTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|,
name|int
name|maxTokenLength
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|runAutomaton
operator|=
name|runAutomaton
expr_stmt|;
name|this
operator|.
name|lowerCase
operator|=
name|lowerCase
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
expr_stmt|;
name|this
operator|.
name|streamState
operator|=
name|State
operator|.
name|SETREADER
expr_stmt|;
name|this
operator|.
name|maxTokenLength
operator|=
name|maxTokenLength
expr_stmt|;
block|}
DECL|method|MockTokenizer
specifier|public
name|MockTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|,
name|int
name|maxTokenLength
parameter_list|)
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|input
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|,
name|maxTokenLength
argument_list|)
expr_stmt|;
block|}
DECL|method|MockTokenizer
specifier|public
name|MockTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
comment|/** Calls {@link #MockTokenizer(Reader, CharacterRunAutomaton, boolean) MockTokenizer(Reader, WHITESPACE, true)} */
DECL|method|MockTokenizer
specifier|public
name|MockTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
assert|assert
operator|!
name|enableChecks
operator|||
operator|(
name|streamState
operator|==
name|State
operator|.
name|RESET
operator|||
name|streamState
operator|==
name|State
operator|.
name|INCREMENT
operator|)
operator|:
literal|"incrementToken() called while in wrong state: "
operator|+
name|streamState
assert|;
name|clearAttributes
argument_list|()
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|startOffset
init|=
name|off
decl_stmt|;
name|int
name|cp
init|=
name|readCodePoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|cp
operator|<
literal|0
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|isTokenChar
argument_list|(
name|cp
argument_list|)
condition|)
block|{
name|int
name|endOffset
decl_stmt|;
do|do
block|{
name|char
name|chars
index|[]
init|=
name|Character
operator|.
name|toChars
argument_list|(
name|normalize
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|termAtt
operator|.
name|append
argument_list|(
name|chars
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|endOffset
operator|=
name|off
expr_stmt|;
if|if
condition|(
name|termAtt
operator|.
name|length
argument_list|()
operator|>=
name|maxTokenLength
condition|)
block|{
break|break;
block|}
name|cp
operator|=
name|readCodePoint
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|cp
operator|>=
literal|0
operator|&&
name|isTokenChar
argument_list|(
name|cp
argument_list|)
condition|)
do|;
name|int
name|correctedStartOffset
init|=
name|correctOffset
argument_list|(
name|startOffset
argument_list|)
decl_stmt|;
name|int
name|correctedEndOffset
init|=
name|correctOffset
argument_list|(
name|endOffset
argument_list|)
decl_stmt|;
assert|assert
name|correctedStartOffset
operator|>=
literal|0
assert|;
assert|assert
name|correctedEndOffset
operator|>=
literal|0
assert|;
assert|assert
name|correctedStartOffset
operator|>=
name|lastOffset
assert|;
name|lastOffset
operator|=
name|correctedStartOffset
expr_stmt|;
assert|assert
name|correctedEndOffset
operator|>=
name|correctedStartOffset
assert|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctedStartOffset
argument_list|,
name|correctedEndOffset
argument_list|)
expr_stmt|;
name|streamState
operator|=
name|State
operator|.
name|INCREMENT
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|streamState
operator|=
name|State
operator|.
name|INCREMENT_FALSE
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|readCodePoint
specifier|protected
name|int
name|readCodePoint
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|ch
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|<
literal|0
condition|)
block|{
return|return
name|ch
return|;
block|}
else|else
block|{
assert|assert
operator|!
name|Character
operator|.
name|isLowSurrogate
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
operator|:
literal|"unpaired low surrogate: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|ch
argument_list|)
assert|;
name|off
operator|++
expr_stmt|;
if|if
condition|(
name|Character
operator|.
name|isHighSurrogate
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
condition|)
block|{
name|int
name|ch2
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch2
operator|>=
literal|0
condition|)
block|{
name|off
operator|++
expr_stmt|;
assert|assert
name|Character
operator|.
name|isLowSurrogate
argument_list|(
operator|(
name|char
operator|)
name|ch2
argument_list|)
operator|:
literal|"unpaired high surrogate: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|ch
argument_list|)
operator|+
literal|", followed by: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|ch2
argument_list|)
assert|;
return|return
name|Character
operator|.
name|toCodePoint
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|,
operator|(
name|char
operator|)
name|ch2
argument_list|)
return|;
block|}
block|}
return|return
name|ch
return|;
block|}
block|}
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
name|state
operator|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|<
literal|0
condition|)
block|{
name|state
operator|=
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|normalize
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|lowerCase
condition|?
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
else|:
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|state
operator|=
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
expr_stmt|;
name|lastOffset
operator|=
name|off
operator|=
literal|0
expr_stmt|;
assert|assert
operator|!
name|enableChecks
operator|||
name|streamState
operator|!=
name|State
operator|.
name|RESET
operator|:
literal|"double reset()"
assert|;
name|streamState
operator|=
name|State
operator|.
name|RESET
expr_stmt|;
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// in some exceptional cases (e.g. TestIndexWriterExceptions) a test can prematurely close()
comment|// these tests should disable this check, by default we check the normal workflow.
comment|// TODO: investigate the CachingTokenFilter "double-close"... for now we ignore this
assert|assert
operator|!
name|enableChecks
operator|||
name|streamState
operator|==
name|State
operator|.
name|END
operator|||
name|streamState
operator|==
name|State
operator|.
name|CLOSE
operator|:
literal|"close() called in wrong state: "
operator|+
name|streamState
assert|;
name|streamState
operator|=
name|State
operator|.
name|CLOSE
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
assert|assert
operator|!
name|enableChecks
operator|||
name|streamState
operator|==
name|State
operator|.
name|CLOSE
operator|:
literal|"setReader() called in wrong state: "
operator|+
name|streamState
assert|;
name|streamState
operator|=
name|State
operator|.
name|SETREADER
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|finalOffset
init|=
name|correctOffset
argument_list|(
name|off
argument_list|)
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
comment|// some tokenizers, such as limiting tokenizers, call end() before incrementToken() returns false.
comment|// these tests should disable this check (in general you should consume the entire stream)
try|try
block|{
assert|assert
operator|!
name|enableChecks
operator|||
name|streamState
operator|==
name|State
operator|.
name|INCREMENT_FALSE
operator|:
literal|"end() called before incrementToken() returned false!"
assert|;
block|}
finally|finally
block|{
name|streamState
operator|=
name|State
operator|.
name|END
expr_stmt|;
block|}
block|}
comment|/**     * Toggle consumer workflow checking: if your test consumes tokenstreams normally you    * should leave this enabled.    */
DECL|method|setEnableChecks
specifier|public
name|void
name|setEnableChecks
parameter_list|(
name|boolean
name|enableChecks
parameter_list|)
block|{
name|this
operator|.
name|enableChecks
operator|=
name|enableChecks
expr_stmt|;
block|}
block|}
end_class

end_unit

