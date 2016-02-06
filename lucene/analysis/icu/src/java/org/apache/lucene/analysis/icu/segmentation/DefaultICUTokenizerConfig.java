begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
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
name|InputStream
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UScript
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedBreakIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|ULocale
import|;
end_import

begin_comment
comment|/**  * Default {@link ICUTokenizerConfig} that is generally applicable  * to many languages.  *<p>  * Generally tokenizes Unicode text according to UAX#29   * ({@link BreakIterator#getWordInstance(ULocale) BreakIterator.getWordInstance(ULocale.ROOT)}),   * but with the following tailorings:  *<ul>  *<li>Thai, Lao, Myanmar, and CJK text is broken into words with a dictionary.   *<li>Khmer text is broken into syllables  *   based on custom BreakIterator rules.  *</ul>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DefaultICUTokenizerConfig
specifier|public
class|class
name|DefaultICUTokenizerConfig
extends|extends
name|ICUTokenizerConfig
block|{
comment|/** Token type for words containing ideographic characters */
DECL|field|WORD_IDEO
specifier|public
specifier|static
specifier|final
name|String
name|WORD_IDEO
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|IDEOGRAPHIC
index|]
decl_stmt|;
comment|/** Token type for words containing Japanese hiragana */
DECL|field|WORD_HIRAGANA
specifier|public
specifier|static
specifier|final
name|String
name|WORD_HIRAGANA
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|HIRAGANA
index|]
decl_stmt|;
comment|/** Token type for words containing Japanese katakana */
DECL|field|WORD_KATAKANA
specifier|public
specifier|static
specifier|final
name|String
name|WORD_KATAKANA
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|KATAKANA
index|]
decl_stmt|;
comment|/** Token type for words containing Korean hangul  */
DECL|field|WORD_HANGUL
specifier|public
specifier|static
specifier|final
name|String
name|WORD_HANGUL
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|HANGUL
index|]
decl_stmt|;
comment|/** Token type for words that contain letters */
DECL|field|WORD_LETTER
specifier|public
specifier|static
specifier|final
name|String
name|WORD_LETTER
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|ALPHANUM
index|]
decl_stmt|;
comment|/** Token type for words that appear to be numbers */
DECL|field|WORD_NUMBER
specifier|public
specifier|static
specifier|final
name|String
name|WORD_NUMBER
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|NUM
index|]
decl_stmt|;
comment|/*    * the default breakiterators in use. these can be expensive to    * instantiate, cheap to clone.    */
comment|// we keep the cjk breaking separate, thats because it cannot be customized (because dictionary
comment|// is only triggered when kind = WORD, but kind = LINE by default and we have no non-evil way to change it)
DECL|field|cjkBreakIterator
specifier|private
specifier|static
specifier|final
name|BreakIterator
name|cjkBreakIterator
init|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|ULocale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|// the same as ROOT, except no dictionary segmentation for cjk
DECL|field|defaultBreakIterator
specifier|private
specifier|static
specifier|final
name|BreakIterator
name|defaultBreakIterator
init|=
name|readBreakIterator
argument_list|(
literal|"Default.brk"
argument_list|)
decl_stmt|;
DECL|field|khmerBreakIterator
specifier|private
specifier|static
specifier|final
name|BreakIterator
name|khmerBreakIterator
init|=
name|readBreakIterator
argument_list|(
literal|"Khmer.brk"
argument_list|)
decl_stmt|;
comment|// TODO: deprecate this boolean? you only care if you are doing super-expert stuff...
DECL|field|cjkAsWords
specifier|private
specifier|final
name|boolean
name|cjkAsWords
decl_stmt|;
comment|/**     * Creates a new config. This object is lightweight, but the first    * time the class is referenced, breakiterators will be initialized.    * @param cjkAsWords true if cjk text should undergo dictionary-based segmentation,     *                   otherwise text will be segmented according to UAX#29 defaults.    *                   If this is true, all Han+Hiragana+Katakana words will be tagged as    *                   IDEOGRAPHIC.    */
DECL|method|DefaultICUTokenizerConfig
specifier|public
name|DefaultICUTokenizerConfig
parameter_list|(
name|boolean
name|cjkAsWords
parameter_list|)
block|{
name|this
operator|.
name|cjkAsWords
operator|=
name|cjkAsWords
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|combineCJ
specifier|public
name|boolean
name|combineCJ
parameter_list|()
block|{
return|return
name|cjkAsWords
return|;
block|}
annotation|@
name|Override
DECL|method|getBreakIterator
specifier|public
name|BreakIterator
name|getBreakIterator
parameter_list|(
name|int
name|script
parameter_list|)
block|{
switch|switch
condition|(
name|script
condition|)
block|{
case|case
name|UScript
operator|.
name|KHMER
case|:
return|return
operator|(
name|BreakIterator
operator|)
name|khmerBreakIterator
operator|.
name|clone
argument_list|()
return|;
case|case
name|UScript
operator|.
name|JAPANESE
case|:
return|return
operator|(
name|BreakIterator
operator|)
name|cjkBreakIterator
operator|.
name|clone
argument_list|()
return|;
default|default:
return|return
operator|(
name|BreakIterator
operator|)
name|defaultBreakIterator
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|(
name|int
name|script
parameter_list|,
name|int
name|ruleStatus
parameter_list|)
block|{
switch|switch
condition|(
name|ruleStatus
condition|)
block|{
case|case
name|RuleBasedBreakIterator
operator|.
name|WORD_IDEO
case|:
return|return
name|WORD_IDEO
return|;
case|case
name|RuleBasedBreakIterator
operator|.
name|WORD_KANA
case|:
return|return
name|script
operator|==
name|UScript
operator|.
name|HIRAGANA
condition|?
name|WORD_HIRAGANA
else|:
name|WORD_KATAKANA
return|;
case|case
name|RuleBasedBreakIterator
operator|.
name|WORD_LETTER
case|:
return|return
name|script
operator|==
name|UScript
operator|.
name|HANGUL
condition|?
name|WORD_HANGUL
else|:
name|WORD_LETTER
return|;
case|case
name|RuleBasedBreakIterator
operator|.
name|WORD_NUMBER
case|:
return|return
name|WORD_NUMBER
return|;
default|default:
comment|/* some other custom code */
return|return
literal|"<OTHER>"
return|;
block|}
block|}
DECL|method|readBreakIterator
specifier|private
specifier|static
name|RuleBasedBreakIterator
name|readBreakIterator
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|InputStream
name|is
init|=
name|DefaultICUTokenizerConfig
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|RuleBasedBreakIterator
name|bi
init|=
name|RuleBasedBreakIterator
operator|.
name|getInstanceFromCompiledRules
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|bi
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

