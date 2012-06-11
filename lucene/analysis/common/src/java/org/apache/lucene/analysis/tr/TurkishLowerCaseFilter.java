begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.tr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tr
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

begin_comment
comment|/**  * Normalizes Turkish token text to lower case.  *<p>  * Turkish and Azeri have unique casing behavior for some characters. This  * filter applies Turkish lowercase rules. For more information, see<a  * href="http://en.wikipedia.org/wiki/Turkish_dotted_and_dotless_I"  *>http://en.wikipedia.org/wiki/Turkish_dotted_and_dotless_I</a>  *</p>  */
end_comment

begin_class
DECL|class|TurkishLowerCaseFilter
specifier|public
specifier|final
class|class
name|TurkishLowerCaseFilter
extends|extends
name|TokenFilter
block|{
DECL|field|LATIN_CAPITAL_LETTER_I
specifier|private
specifier|static
specifier|final
name|int
name|LATIN_CAPITAL_LETTER_I
init|=
literal|'\u0049'
decl_stmt|;
DECL|field|LATIN_SMALL_LETTER_I
specifier|private
specifier|static
specifier|final
name|int
name|LATIN_SMALL_LETTER_I
init|=
literal|'\u0069'
decl_stmt|;
DECL|field|LATIN_SMALL_LETTER_DOTLESS_I
specifier|private
specifier|static
specifier|final
name|int
name|LATIN_SMALL_LETTER_DOTLESS_I
init|=
literal|'\u0131'
decl_stmt|;
DECL|field|COMBINING_DOT_ABOVE
specifier|private
specifier|static
specifier|final
name|int
name|COMBINING_DOT_ABOVE
init|=
literal|'\u0307'
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
comment|/**    * Create a new TurkishLowerCaseFilter, that normalizes Turkish token text     * to lower case.    *     * @param in TokenStream to filter    */
DECL|method|TurkishLowerCaseFilter
specifier|public
name|TurkishLowerCaseFilter
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
name|boolean
name|iOrAfter
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|termAtt
operator|.
name|length
argument_list|()
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
name|length
condition|;
control|)
block|{
specifier|final
name|int
name|ch
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|buffer
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|iOrAfter
operator|=
operator|(
name|ch
operator|==
name|LATIN_CAPITAL_LETTER_I
operator|||
operator|(
name|iOrAfter
operator|&&
name|Character
operator|.
name|getType
argument_list|(
name|ch
argument_list|)
operator|==
name|Character
operator|.
name|NON_SPACING_MARK
operator|)
operator|)
expr_stmt|;
if|if
condition|(
name|iOrAfter
condition|)
block|{
comment|// all the special I turkish handling happens here.
switch|switch
condition|(
name|ch
condition|)
block|{
comment|// remove COMBINING_DOT_ABOVE to mimic composed lowercase
case|case
name|COMBINING_DOT_ABOVE
case|:
name|length
operator|=
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
argument_list|,
name|length
argument_list|)
expr_stmt|;
continue|continue;
comment|// i itself, it depends if it is followed by COMBINING_DOT_ABOVE
comment|// if it is, we will make it small i and later remove the dot
case|case
name|LATIN_CAPITAL_LETTER_I
case|:
if|if
condition|(
name|isBeforeDot
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|LATIN_SMALL_LETTER_I
expr_stmt|;
block|}
else|else
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|LATIN_SMALL_LETTER_DOTLESS_I
expr_stmt|;
comment|// below is an optimization. no COMBINING_DOT_ABOVE follows,
comment|// so don't waste time calculating Character.getType(), etc
name|iOrAfter
operator|=
literal|false
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
continue|continue;
block|}
block|}
name|i
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|ch
argument_list|)
argument_list|,
name|buffer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/**    * lookahead for a combining dot above.    * other NSMs may be in between.    */
DECL|method|isBeforeDot
specifier|private
name|boolean
name|isBeforeDot
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|pos
init|;
name|i
operator|<
name|len
condition|;
control|)
block|{
specifier|final
name|int
name|ch
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|getType
argument_list|(
name|ch
argument_list|)
operator|!=
name|Character
operator|.
name|NON_SPACING_MARK
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|ch
operator|==
name|COMBINING_DOT_ABOVE
condition|)
return|return
literal|true
return|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * delete a character in-place.    * rarely happens, only if COMBINING_DOT_ABOVE is found after an i    */
DECL|method|delete
specifier|private
name|int
name|delete
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
name|len
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|s
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|s
argument_list|,
name|pos
argument_list|,
name|len
operator|-
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|len
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

