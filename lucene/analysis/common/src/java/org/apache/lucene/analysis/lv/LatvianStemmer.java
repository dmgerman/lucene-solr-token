begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.lv
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|lv
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|StemmerUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Light stemmer for Latvian.  *<p>  * This is a light version of the algorithm in Karlis Kreslin's PhD thesis  *<i>A stemming algorithm for Latvian</i> with the following modifications:  *<ul>  *<li>Only explicitly stems noun and adjective morphology  *<li>Stricter length/vowel checks for the resulting stems (verb etc suffix stripping is removed)  *<li>Removes only the primary inflectional suffixes: case and number for nouns ;   *       case, number, gender, and definitiveness for adjectives.  *<li>Palatalization is only handled when a declension II,V,VI noun suffix is removed.  *</ul>  */
end_comment

begin_class
DECL|class|LatvianStemmer
specifier|public
class|class
name|LatvianStemmer
block|{
comment|/**    * Stem a latvian word. returns the new adjusted length.    */
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|numVowels
init|=
name|numVowels
argument_list|(
name|s
argument_list|,
name|len
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
name|affixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Affix
name|affix
init|=
name|affixes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|numVowels
operator|>
name|affix
operator|.
name|vc
operator|&&
name|len
operator|>=
name|affix
operator|.
name|affix
operator|.
name|length
operator|+
literal|3
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
name|affix
operator|.
name|affix
argument_list|)
condition|)
block|{
name|len
operator|-=
name|affix
operator|.
name|affix
operator|.
name|length
expr_stmt|;
return|return
name|affix
operator|.
name|palatalizes
condition|?
name|unpalatalize
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
else|:
name|len
return|;
block|}
block|}
return|return
name|len
return|;
block|}
DECL|field|affixes
specifier|static
specifier|final
name|Affix
name|affixes
index|[]
init|=
block|{
operator|new
name|Affix
argument_list|(
literal|"ajiem"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ajai"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ajam"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ajÄm"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ajos"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ajÄs"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"iem"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ajÄ"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ais"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ai"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ei"
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Äm"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"am"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Äm"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Ä«m"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"im"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"um"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"us"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"as"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Äs"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"es"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"os"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ij"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Ä«s"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Äs"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"is"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"ie"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"u"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"i"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"e"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Ä"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Ä"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Ä«"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Å«"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"o"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"s"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|Affix
argument_list|(
literal|"Å¡"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
block|,   }
decl_stmt|;
DECL|class|Affix
specifier|static
class|class
name|Affix
block|{
DECL|field|affix
name|char
name|affix
index|[]
decl_stmt|;
comment|// suffix
DECL|field|vc
name|int
name|vc
decl_stmt|;
comment|// vowel count of the suffix
DECL|field|palatalizes
name|boolean
name|palatalizes
decl_stmt|;
comment|// true if we should fire palatalization rules.
DECL|method|Affix
name|Affix
parameter_list|(
name|String
name|affix
parameter_list|,
name|int
name|vc
parameter_list|,
name|boolean
name|palatalizes
parameter_list|)
block|{
name|this
operator|.
name|affix
operator|=
name|affix
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|this
operator|.
name|vc
operator|=
name|vc
expr_stmt|;
name|this
operator|.
name|palatalizes
operator|=
name|palatalizes
expr_stmt|;
block|}
block|}
comment|/**    * Most cases are handled except for the ambiguous ones:    *<ul>    *<li> s -&gt; Å¡    *<li> t -&gt; Å¡    *<li> d -&gt; Å¾    *<li> z -&gt; Å¾    *</ul>    */
DECL|method|unpalatalize
specifier|private
name|int
name|unpalatalize
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// we check the character removed: if its -u then
comment|// its 2,5, or 6 gen pl., and these two can only apply then.
if|if
condition|(
name|s
index|[
name|len
index|]
operator|==
literal|'u'
condition|)
block|{
comment|// kÅ¡ -> kst
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"kÅ¡"
argument_list|)
condition|)
block|{
name|len
operator|++
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'s'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'t'
expr_stmt|;
return|return
name|len
return|;
block|}
comment|// ÅÅ -> nn
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ÅÅ"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'n'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'n'
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
comment|// otherwise all other rules
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"pj"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"bj"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"mj"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"vj"
argument_list|)
condition|)
block|{
comment|// labial consonant
return|return
name|len
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¡Å"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'s'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'n'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¾Å"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'z'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'n'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¡Ä¼"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'s'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'l'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¾Ä¼"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'z'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'l'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ä¼Å"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'l'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'n'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ä¼Ä¼"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'l'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'l'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'Ä'
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'c'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'Ä¼'
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'l'
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'Å'
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'n'
expr_stmt|;
return|return
name|len
return|;
block|}
return|return
name|len
return|;
block|}
comment|/**    * Count the vowels in the string, we always require at least    * one in the remaining stem to accept it.    */
DECL|method|numVowels
specifier|private
name|int
name|numVowels
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
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
name|len
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|s
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'a'
case|:
case|case
literal|'e'
case|:
case|case
literal|'i'
case|:
case|case
literal|'o'
case|:
case|case
literal|'u'
case|:
case|case
literal|'Ä'
case|:
case|case
literal|'Ä«'
case|:
case|case
literal|'Ä'
case|:
case|case
literal|'Å«'
case|:
name|n
operator|++
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
block|}
end_class

end_unit

