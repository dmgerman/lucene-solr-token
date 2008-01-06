begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
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

begin_comment
comment|/** A grammar-based tokenizer constructed with JFlex  *  *<p> This should be a good tokenizer for most European-language documents:  *  *<ul>  *<li>Splits words at punctuation characters, removing punctuation. However, a   *     dot that's not followed by whitespace is considered part of a token.  *<li>Splits words at hyphens, unless there's a number in the token, in which case  *     the whole token is interpreted as a product number and is not split.  *<li>Recognizes email addresses and internet hostnames as one token.  *</ul>  *  *<p>Many applications have specific tokenizer needs.  If this tokenizer does  * not suit your application, please consider copying this source code  * directory to your project and maintaining your own grammar-based tokenizer.  */
end_comment

begin_class
DECL|class|StandardTokenizer
specifier|public
class|class
name|StandardTokenizer
extends|extends
name|Tokenizer
block|{
comment|/** A private instance of the JFlex-constructed scanner */
DECL|field|scanner
specifier|private
specifier|final
name|StandardTokenizerImpl
name|scanner
decl_stmt|;
comment|/**    * Specifies whether deprecated acronyms should be replaced with HOST type.    * This is false by default to support backward compatibility.    *<p/>    * See http://issues.apache.org/jira/browse/LUCENE-1068    *     * @deprecated this should be removed in the next release (3.0).    */
DECL|field|replaceInvalidAcronym
specifier|private
name|boolean
name|replaceInvalidAcronym
init|=
literal|false
decl_stmt|;
DECL|method|setInput
name|void
name|setInput
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|reader
expr_stmt|;
block|}
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/** Set the max allowed token length.  Any token longer    *  than this is skipped. */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** @see #setMaxTokenLength */
DECL|method|getMaxTokenLength
specifier|public
name|int
name|getMaxTokenLength
parameter_list|()
block|{
return|return
name|maxTokenLength
return|;
block|}
comment|/**      * Creates a new instance of the {@link StandardTokenizer}. Attaches the      *<code>input</code> to a newly created JFlex scanner.      */
DECL|method|StandardTokenizer
specifier|public
name|StandardTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|StandardTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instance of the {@link org.apache.lucene.analysis.standard.StandardTokenizer}.  Attaches    * the<code>input</code> to the newly created JFlex scanner.    *    * @param input The input reader    * @param replaceInvalidAcronym Set to true to replace mischaracterized acronyms with HOST.    *    * See http://issues.apache.org/jira/browse/LUCENE-1068    */
DECL|method|StandardTokenizer
specifier|public
name|StandardTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|boolean
name|replaceInvalidAcronym
parameter_list|)
block|{
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|StandardTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.analysis.TokenStream#next()    */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
name|Token
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|posIncr
init|=
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
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
name|StandardTokenizerImpl
operator|.
name|YYEOF
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|scanner
operator|.
name|yylength
argument_list|()
operator|<=
name|maxTokenLength
condition|)
block|{
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
name|result
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|getText
argument_list|(
name|result
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
name|result
operator|.
name|setStartOffset
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|result
operator|.
name|setEndOffset
argument_list|(
name|start
operator|+
name|result
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// This 'if' should be removed in the next release. For now, it converts
comment|// invalid acronyms to HOST. When removed, only the 'else' part should
comment|// remain.
if|if
condition|(
name|tokenType
operator|==
name|StandardTokenizerImpl
operator|.
name|ACRONYM_DEP
condition|)
block|{
if|if
condition|(
name|replaceInvalidAcronym
condition|)
block|{
name|result
operator|.
name|setType
argument_list|(
name|StandardTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizerImpl
operator|.
name|HOST
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|setTermLength
argument_list|(
name|result
operator|.
name|termLength
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove extra '.'
block|}
else|else
block|{
name|result
operator|.
name|setType
argument_list|(
name|StandardTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizerImpl
operator|.
name|ACRONYM
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|.
name|setType
argument_list|(
name|StandardTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|tokenType
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
comment|// When we skip a too-long term, we still increment the
comment|// position increment
name|posIncr
operator|++
expr_stmt|;
block|}
block|}
comment|/*      * (non-Javadoc)      *      * @see org.apache.lucene.analysis.TokenStream#reset()      */
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
name|input
operator|=
name|reader
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Prior to https://issues.apache.org/jira/browse/LUCENE-1068, StandardTokenizer mischaracterized as acronyms tokens like www.abc.com    * when they should have been labeled as hosts instead.    * @return true if StandardTokenizer now returns these tokens as Hosts, otherwise false    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|isReplaceInvalidAcronym
specifier|public
name|boolean
name|isReplaceInvalidAcronym
parameter_list|()
block|{
return|return
name|replaceInvalidAcronym
return|;
block|}
comment|/**    *    * @param replaceInvalidAcronym Set to true to replace mischaracterized acronyms as HOST.    * @deprecated Remove in 3.X and make true the only valid value    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    */
DECL|method|setReplaceInvalidAcronym
specifier|public
name|void
name|setReplaceInvalidAcronym
parameter_list|(
name|boolean
name|replaceInvalidAcronym
parameter_list|)
block|{
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
block|}
end_class

end_unit

