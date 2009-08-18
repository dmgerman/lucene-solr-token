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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|TermAttribute
import|;
end_import

begin_comment
comment|/**  * A {@link TokenFilter} with a stop word table.    *<ul>  *<li>Numeric tokens are removed.  *<li>English tokens must be larger than 1 character.  *<li>One Chinese character as one Chinese word.  *</ul>  * TO DO:  *<ol>  *<li>Add Chinese stop words, such as \ue400  *<li>Dictionary based Chinese word extraction  *<li>Intelligent Chinese word extraction  *</ol>  *   * @version 1.0  *  */
end_comment

begin_class
DECL|class|ChineseFilter
specifier|public
specifier|final
class|class
name|ChineseFilter
extends|extends
name|TokenFilter
block|{
comment|// Only English now, Chinese to be added later.
DECL|field|STOP_WORDS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|STOP_WORDS
init|=
block|{
literal|"and"
block|,
literal|"are"
block|,
literal|"as"
block|,
literal|"at"
block|,
literal|"be"
block|,
literal|"but"
block|,
literal|"by"
block|,
literal|"for"
block|,
literal|"if"
block|,
literal|"in"
block|,
literal|"into"
block|,
literal|"is"
block|,
literal|"it"
block|,
literal|"no"
block|,
literal|"not"
block|,
literal|"of"
block|,
literal|"on"
block|,
literal|"or"
block|,
literal|"such"
block|,
literal|"that"
block|,
literal|"the"
block|,
literal|"their"
block|,
literal|"then"
block|,
literal|"there"
block|,
literal|"these"
block|,
literal|"they"
block|,
literal|"this"
block|,
literal|"to"
block|,
literal|"was"
block|,
literal|"will"
block|,
literal|"with"
block|}
decl_stmt|;
DECL|field|stopTable
specifier|private
name|Map
name|stopTable
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|ChineseFilter
specifier|public
name|ChineseFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stopTable
operator|=
operator|new
name|HashMap
argument_list|(
name|STOP_WORDS
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|stopTable
operator|.
name|put
argument_list|(
name|STOP_WORDS
index|[
name|i
index|]
argument_list|,
name|STOP_WORDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|text
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
comment|// why not key off token type here assuming ChineseTokenizer comes first?
if|if
condition|(
name|stopTable
operator|.
name|get
argument_list|(
name|text
argument_list|)
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|Character
operator|.
name|getType
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
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
comment|// English word/token should larger than 1 character.
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
break|break;
case|case
name|Character
operator|.
name|OTHER_LETTER
case|:
comment|// One Chinese character as one Chinese word.
comment|// Chinese word extraction to be added later here.
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

