begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.wikipedia.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|wikipedia
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
name|FlagsAttribute
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
name|TermAttribute
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
name|TypeAttribute
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
name|AttributeSource
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Extension of StandardTokenizer that is aware of Wikipedia syntax.  It is based off of the  * Wikipedia tutorial available at http://en.wikipedia.org/wiki/Wikipedia:Tutorial, but it may not be complete.  *<p/>  *<p/>  * EXPERIMENTAL !!!!!!!!!  * NOTE: This Tokenizer is considered experimental and the grammar is subject to change in the trunk and in follow up releases.  */
end_comment

begin_class
DECL|class|WikipediaTokenizer
specifier|public
specifier|final
class|class
name|WikipediaTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|INTERNAL_LINK
specifier|public
specifier|static
specifier|final
name|String
name|INTERNAL_LINK
init|=
literal|"il"
decl_stmt|;
DECL|field|EXTERNAL_LINK
specifier|public
specifier|static
specifier|final
name|String
name|EXTERNAL_LINK
init|=
literal|"el"
decl_stmt|;
comment|//The URL part of the link, i.e. the first token
DECL|field|EXTERNAL_LINK_URL
specifier|public
specifier|static
specifier|final
name|String
name|EXTERNAL_LINK_URL
init|=
literal|"elu"
decl_stmt|;
DECL|field|CITATION
specifier|public
specifier|static
specifier|final
name|String
name|CITATION
init|=
literal|"ci"
decl_stmt|;
DECL|field|CATEGORY
specifier|public
specifier|static
specifier|final
name|String
name|CATEGORY
init|=
literal|"c"
decl_stmt|;
DECL|field|BOLD
specifier|public
specifier|static
specifier|final
name|String
name|BOLD
init|=
literal|"b"
decl_stmt|;
DECL|field|ITALICS
specifier|public
specifier|static
specifier|final
name|String
name|ITALICS
init|=
literal|"i"
decl_stmt|;
DECL|field|BOLD_ITALICS
specifier|public
specifier|static
specifier|final
name|String
name|BOLD_ITALICS
init|=
literal|"bi"
decl_stmt|;
DECL|field|HEADING
specifier|public
specifier|static
specifier|final
name|String
name|HEADING
init|=
literal|"h"
decl_stmt|;
DECL|field|SUB_HEADING
specifier|public
specifier|static
specifier|final
name|String
name|SUB_HEADING
init|=
literal|"sh"
decl_stmt|;
DECL|field|ALPHANUM_ID
specifier|public
specifier|static
specifier|final
name|int
name|ALPHANUM_ID
init|=
literal|0
decl_stmt|;
DECL|field|APOSTROPHE_ID
specifier|public
specifier|static
specifier|final
name|int
name|APOSTROPHE_ID
init|=
literal|1
decl_stmt|;
DECL|field|ACRONYM_ID
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM_ID
init|=
literal|2
decl_stmt|;
DECL|field|COMPANY_ID
specifier|public
specifier|static
specifier|final
name|int
name|COMPANY_ID
init|=
literal|3
decl_stmt|;
DECL|field|EMAIL_ID
specifier|public
specifier|static
specifier|final
name|int
name|EMAIL_ID
init|=
literal|4
decl_stmt|;
DECL|field|HOST_ID
specifier|public
specifier|static
specifier|final
name|int
name|HOST_ID
init|=
literal|5
decl_stmt|;
DECL|field|NUM_ID
specifier|public
specifier|static
specifier|final
name|int
name|NUM_ID
init|=
literal|6
decl_stmt|;
DECL|field|CJ_ID
specifier|public
specifier|static
specifier|final
name|int
name|CJ_ID
init|=
literal|7
decl_stmt|;
DECL|field|INTERNAL_LINK_ID
specifier|public
specifier|static
specifier|final
name|int
name|INTERNAL_LINK_ID
init|=
literal|8
decl_stmt|;
DECL|field|EXTERNAL_LINK_ID
specifier|public
specifier|static
specifier|final
name|int
name|EXTERNAL_LINK_ID
init|=
literal|9
decl_stmt|;
DECL|field|CITATION_ID
specifier|public
specifier|static
specifier|final
name|int
name|CITATION_ID
init|=
literal|10
decl_stmt|;
DECL|field|CATEGORY_ID
specifier|public
specifier|static
specifier|final
name|int
name|CATEGORY_ID
init|=
literal|11
decl_stmt|;
DECL|field|BOLD_ID
specifier|public
specifier|static
specifier|final
name|int
name|BOLD_ID
init|=
literal|12
decl_stmt|;
DECL|field|ITALICS_ID
specifier|public
specifier|static
specifier|final
name|int
name|ITALICS_ID
init|=
literal|13
decl_stmt|;
DECL|field|BOLD_ITALICS_ID
specifier|public
specifier|static
specifier|final
name|int
name|BOLD_ITALICS_ID
init|=
literal|14
decl_stmt|;
DECL|field|HEADING_ID
specifier|public
specifier|static
specifier|final
name|int
name|HEADING_ID
init|=
literal|15
decl_stmt|;
DECL|field|SUB_HEADING_ID
specifier|public
specifier|static
specifier|final
name|int
name|SUB_HEADING_ID
init|=
literal|16
decl_stmt|;
DECL|field|EXTERNAL_LINK_URL_ID
specifier|public
specifier|static
specifier|final
name|int
name|EXTERNAL_LINK_URL_ID
init|=
literal|17
decl_stmt|;
comment|/** String token types that correspond to token type int constants */
DECL|field|TOKEN_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TOKEN_TYPES
init|=
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<APOSTROPHE>"
block|,
literal|"<ACRONYM>"
block|,
literal|"<COMPANY>"
block|,
literal|"<EMAIL>"
block|,
literal|"<HOST>"
block|,
literal|"<NUM>"
block|,
literal|"<CJ>"
block|,
name|INTERNAL_LINK
block|,
name|EXTERNAL_LINK
block|,
name|CITATION
block|,
name|CATEGORY
block|,
name|BOLD
block|,
name|ITALICS
block|,
name|BOLD_ITALICS
block|,
name|HEADING
block|,
name|SUB_HEADING
block|,
name|EXTERNAL_LINK_URL
block|}
decl_stmt|;
comment|/** @deprecated Please use {@link #TOKEN_TYPES} instead */
DECL|field|tokenImage
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|tokenImage
init|=
name|TOKEN_TYPES
decl_stmt|;
comment|/**    * Only output tokens    */
DECL|field|TOKENS_ONLY
specifier|public
specifier|static
specifier|final
name|int
name|TOKENS_ONLY
init|=
literal|0
decl_stmt|;
comment|/**    * Only output untokenized tokens, which are tokens that would normally be split into several tokens    */
DECL|field|UNTOKENIZED_ONLY
specifier|public
specifier|static
specifier|final
name|int
name|UNTOKENIZED_ONLY
init|=
literal|1
decl_stmt|;
comment|/**    * Output the both the untokenized token and the splits    */
DECL|field|BOTH
specifier|public
specifier|static
specifier|final
name|int
name|BOTH
init|=
literal|2
decl_stmt|;
comment|/**    * This flag is used to indicate that the produced "Token" would, if {@link #TOKENS_ONLY} was used, produce multiple tokens.    */
DECL|field|UNTOKENIZED_TOKEN_FLAG
specifier|public
specifier|static
specifier|final
name|int
name|UNTOKENIZED_TOKEN_FLAG
init|=
literal|1
decl_stmt|;
comment|/**    * A private instance of the JFlex-constructed scanner    */
DECL|field|scanner
specifier|private
specifier|final
name|WikipediaTokenizerImpl
name|scanner
decl_stmt|;
DECL|field|tokenOutput
specifier|private
name|int
name|tokenOutput
init|=
name|TOKENS_ONLY
decl_stmt|;
DECL|field|untokenizedTypes
specifier|private
name|Set
name|untokenizedTypes
init|=
name|Collections
operator|.
name|EMPTY_SET
decl_stmt|;
DECL|field|tokens
specifier|private
name|Iterator
name|tokens
init|=
literal|null
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|flagsAtt
specifier|private
name|FlagsAttribute
name|flagsAtt
decl_stmt|;
comment|/**    * Creates a new instance of the {@link WikipediaTokenizer}. Attaches the    *<code>input</code> to a newly created JFlex scanner.    *    * @param input The Input Reader    */
DECL|method|WikipediaTokenizer
specifier|public
name|WikipediaTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|TOKENS_ONLY
argument_list|,
name|Collections
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instance of the {@link org.apache.lucene.wikipedia.analysis.WikipediaTokenizer}.  Attaches the    *<code>input</code> to a the newly created JFlex scanner.    *    * @param input The input    * @param tokenOutput One of {@link #TOKENS_ONLY}, {@link #UNTOKENIZED_ONLY}, {@link #BOTH}    * @param untokenizedTypes    */
DECL|method|WikipediaTokenizer
specifier|public
name|WikipediaTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|int
name|tokenOutput
parameter_list|,
name|Set
name|untokenizedTypes
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|WikipediaTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|tokenOutput
argument_list|,
name|untokenizedTypes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instance of the {@link org.apache.lucene.wikipedia.analysis.WikipediaTokenizer}.  Attaches the    *<code>input</code> to a the newly created JFlex scanner. Uses the given {@link org.apache.lucene.util.AttributeSource.AttributeFactory}.    *    * @param input The input    * @param tokenOutput One of {@link #TOKENS_ONLY}, {@link #UNTOKENIZED_ONLY}, {@link #BOTH}    * @param untokenizedTypes    */
DECL|method|WikipediaTokenizer
specifier|public
name|WikipediaTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|int
name|tokenOutput
parameter_list|,
name|Set
name|untokenizedTypes
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
name|scanner
operator|=
operator|new
name|WikipediaTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|tokenOutput
argument_list|,
name|untokenizedTypes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instance of the {@link org.apache.lucene.wikipedia.analysis.WikipediaTokenizer}.  Attaches the    *<code>input</code> to a the newly created JFlex scanner. Uses the given {@link AttributeSource}.    *    * @param input The input    * @param tokenOutput One of {@link #TOKENS_ONLY}, {@link #UNTOKENIZED_ONLY}, {@link #BOTH}    * @param untokenizedTypes    */
DECL|method|WikipediaTokenizer
specifier|public
name|WikipediaTokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|,
name|int
name|tokenOutput
parameter_list|,
name|Set
name|untokenizedTypes
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|WikipediaTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|tokenOutput
argument_list|,
name|untokenizedTypes
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|int
name|tokenOutput
parameter_list|,
name|Set
name|untokenizedTypes
parameter_list|)
block|{
name|this
operator|.
name|tokenOutput
operator|=
name|tokenOutput
expr_stmt|;
name|this
operator|.
name|untokenizedTypes
operator|=
name|untokenizedTypes
expr_stmt|;
name|this
operator|.
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|posIncrAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|flagsAtt
operator|=
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/*   * (non-Javadoc)   *   * @see org.apache.lucene.analysis.TokenStream#next()   */
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tokens
operator|!=
literal|null
operator|&&
name|tokens
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AttributeSource
operator|.
name|State
name|state
init|=
operator|(
name|AttributeSource
operator|.
name|State
operator|)
name|tokens
operator|.
name|next
argument_list|()
decl_stmt|;
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|int
name|tokenType
init|=
name|scanner
operator|.
name|getNextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenType
operator|==
name|WikipediaTokenizerImpl
operator|.
name|YYEOF
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|type
init|=
name|WikipediaTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|tokenType
index|]
decl_stmt|;
if|if
condition|(
name|tokenOutput
operator|==
name|TOKENS_ONLY
operator|||
name|untokenizedTypes
operator|.
name|contains
argument_list|(
name|type
argument_list|)
operator|==
literal|false
condition|)
block|{
name|setupToken
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tokenOutput
operator|==
name|UNTOKENIZED_ONLY
operator|&&
name|untokenizedTypes
operator|.
name|contains
argument_list|(
name|type
argument_list|)
operator|==
literal|true
condition|)
block|{
name|collapseTokens
argument_list|(
name|tokenType
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tokenOutput
operator|==
name|BOTH
condition|)
block|{
comment|//collapse into a single token, add it to tokens AND output the individual tokens
comment|//output the untokenized Token first
name|collapseAndSaveTokens
argument_list|(
name|tokenType
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|scanner
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|collapseAndSaveTokens
specifier|private
name|void
name|collapseAndSaveTokens
parameter_list|(
name|int
name|tokenType
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
comment|//collapse
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|32
argument_list|)
decl_stmt|;
name|int
name|numAdded
init|=
name|scanner
operator|.
name|setText
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
comment|//TODO: how to know how much whitespace to add
name|int
name|theStart
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
name|int
name|lastPos
init|=
name|theStart
operator|+
name|numAdded
decl_stmt|;
name|int
name|tmpTokType
decl_stmt|;
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
name|List
name|tmp
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|setupSavedToken
argument_list|(
literal|0
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
comment|//while we can get a token and that token is the same type and we have not transitioned to a new wiki-item of the same type
while|while
condition|(
operator|(
name|tmpTokType
operator|=
name|scanner
operator|.
name|getNextToken
argument_list|()
operator|)
operator|!=
name|WikipediaTokenizerImpl
operator|.
name|YYEOF
operator|&&
name|tmpTokType
operator|==
name|tokenType
operator|&&
name|scanner
operator|.
name|getNumWikiTokensSeen
argument_list|()
operator|>
name|numSeen
condition|)
block|{
name|int
name|currPos
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
comment|//append whitespace
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|currPos
operator|-
name|lastPos
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|numAdded
operator|=
name|scanner
operator|.
name|setText
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|setupSavedToken
argument_list|(
name|scanner
operator|.
name|getPositionIncrement
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
name|numSeen
operator|++
expr_stmt|;
name|lastPos
operator|=
name|currPos
operator|+
name|numAdded
expr_stmt|;
block|}
comment|//trim the buffer
name|String
name|s
init|=
name|buffer
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|theStart
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|theStart
operator|+
name|s
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
name|UNTOKENIZED_TOKEN_FLAG
argument_list|)
expr_stmt|;
comment|//The way the loop is written, we will have proceeded to the next token.  We need to pushback the scanner to lastPos
if|if
condition|(
name|tmpTokType
operator|!=
name|WikipediaTokenizerImpl
operator|.
name|YYEOF
condition|)
block|{
name|scanner
operator|.
name|yypushback
argument_list|(
name|scanner
operator|.
name|yylength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tokens
operator|=
name|tmp
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|setupSavedToken
specifier|private
name|void
name|setupSavedToken
parameter_list|(
name|int
name|positionInc
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|setupToken
argument_list|()
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|positionInc
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|collapseTokens
specifier|private
name|void
name|collapseTokens
parameter_list|(
name|int
name|tokenType
parameter_list|)
throws|throws
name|IOException
block|{
comment|//collapse
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|32
argument_list|)
decl_stmt|;
name|int
name|numAdded
init|=
name|scanner
operator|.
name|setText
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
comment|//TODO: how to know how much whitespace to add
name|int
name|theStart
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
name|int
name|lastPos
init|=
name|theStart
operator|+
name|numAdded
decl_stmt|;
name|int
name|tmpTokType
decl_stmt|;
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
comment|//while we can get a token and that token is the same type and we have not transitioned to a new wiki-item of the same type
while|while
condition|(
operator|(
name|tmpTokType
operator|=
name|scanner
operator|.
name|getNextToken
argument_list|()
operator|)
operator|!=
name|WikipediaTokenizerImpl
operator|.
name|YYEOF
operator|&&
name|tmpTokType
operator|==
name|tokenType
operator|&&
name|scanner
operator|.
name|getNumWikiTokensSeen
argument_list|()
operator|>
name|numSeen
condition|)
block|{
name|int
name|currPos
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
comment|//append whitespace
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|currPos
operator|-
name|lastPos
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|numAdded
operator|=
name|scanner
operator|.
name|setText
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|numSeen
operator|++
expr_stmt|;
name|lastPos
operator|=
name|currPos
operator|+
name|numAdded
expr_stmt|;
block|}
comment|//trim the buffer
name|String
name|s
init|=
name|buffer
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|theStart
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|theStart
operator|+
name|s
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
name|UNTOKENIZED_TOKEN_FLAG
argument_list|)
expr_stmt|;
comment|//The way the loop is written, we will have proceeded to the next token.  We need to pushback the scanner to lastPos
if|if
condition|(
name|tmpTokType
operator|!=
name|WikipediaTokenizerImpl
operator|.
name|YYEOF
condition|)
block|{
name|scanner
operator|.
name|yypushback
argument_list|(
name|scanner
operator|.
name|yylength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tokens
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|setupToken
specifier|private
name|void
name|setupToken
parameter_list|()
block|{
name|scanner
operator|.
name|getText
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|start
operator|+
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*   * (non-Javadoc)   *   * @see org.apache.lucene.analysis.TokenStream#reset()   */
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
name|scanner
operator|.
name|yyreset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

