begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.nl
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|nl
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  * A stemmer for Dutch words. The algorithm is an implementation of  * the<a href="http://snowball.tartarus.org/algorithms/dutch/stemmer.html">dutch stemming</a>  * algorithm in Martin Porter's snowball project.  *   * @author Edwin de Jonge (ejne at cbs.nl)  */
end_comment

begin_class
DECL|class|DutchStemmer
specifier|public
class|class
name|DutchStemmer
block|{
comment|/**    * Buffer for the terms while stemming them.    */
DECL|field|sb
specifier|private
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
DECL|field|_removedE
specifier|private
name|boolean
name|_removedE
decl_stmt|;
DECL|field|_stemDict
specifier|private
name|Map
name|_stemDict
decl_stmt|;
DECL|field|_R1
specifier|private
name|int
name|_R1
decl_stmt|;
DECL|field|_R2
specifier|private
name|int
name|_R2
decl_stmt|;
comment|//TODO convert to internal
comment|/*    * Stemms the given term to an unique<tt>discriminator</tt>.    *    * @param term The term that should be stemmed.    * @return Discriminator for<tt>term</tt>    */
DECL|method|stem
specifier|public
name|String
name|stem
parameter_list|(
name|String
name|term
parameter_list|)
block|{
name|term
operator|=
name|term
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isStemmable
argument_list|(
name|term
argument_list|)
condition|)
return|return
name|term
return|;
if|if
condition|(
name|_stemDict
operator|!=
literal|null
operator|&&
name|_stemDict
operator|.
name|containsKey
argument_list|(
name|term
argument_list|)
condition|)
if|if
condition|(
name|_stemDict
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|instanceof
name|String
condition|)
return|return
operator|(
name|String
operator|)
name|_stemDict
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
else|else
return|return
literal|null
return|;
comment|// Reset the StringBuffer.
name|sb
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|term
argument_list|)
expr_stmt|;
comment|// Stemming starts here...
name|substitute
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|storeYandI
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|_R1
operator|=
name|getRIndex
argument_list|(
name|sb
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|_R1
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|3
argument_list|,
name|_R1
argument_list|)
expr_stmt|;
name|step1
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|step2
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|_R2
operator|=
name|getRIndex
argument_list|(
name|sb
argument_list|,
name|_R1
argument_list|)
expr_stmt|;
name|step3a
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|step3b
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|step4
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|reStoreYandI
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|enEnding
specifier|private
name|boolean
name|enEnding
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
name|String
index|[]
name|enend
init|=
operator|new
name|String
index|[]
block|{
literal|"ene"
block|,
literal|"en"
block|}
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
name|enend
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|end
init|=
name|enend
index|[
name|i
index|]
decl_stmt|;
name|String
name|s
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|s
operator|.
name|length
argument_list|()
operator|-
name|end
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
name|end
argument_list|)
operator|&&
name|index
operator|>=
name|_R1
operator|&&
name|isValidEnEnding
argument_list|(
name|sb
argument_list|,
name|index
operator|-
literal|1
argument_list|)
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
name|end
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|unDouble
argument_list|(
name|sb
argument_list|,
name|index
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|step1
specifier|private
name|void
name|step1
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
if|if
condition|(
name|_R1
operator|>=
name|sb
operator|.
name|length
argument_list|()
condition|)
return|return;
name|String
name|s
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|lengthR1
init|=
name|sb
operator|.
name|length
argument_list|()
operator|-
name|_R1
decl_stmt|;
name|int
name|index
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"heden"
argument_list|)
condition|)
block|{
name|sb
operator|.
name|replace
argument_list|(
name|_R1
argument_list|,
name|lengthR1
operator|+
name|_R1
argument_list|,
name|sb
operator|.
name|substring
argument_list|(
name|_R1
argument_list|,
name|lengthR1
operator|+
name|_R1
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"heden"
argument_list|,
literal|"heid"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|enEnding
argument_list|(
name|sb
argument_list|)
condition|)
return|return;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"se"
argument_list|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|)
operator|>=
name|_R1
operator|&&
name|isValidSEnding
argument_list|(
name|sb
argument_list|,
name|index
operator|-
literal|1
argument_list|)
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|2
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"s"
argument_list|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
operator|>=
name|_R1
operator|&&
name|isValidSEnding
argument_list|(
name|sb
argument_list|,
name|index
operator|-
literal|1
argument_list|)
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Delete suffix e if in R1 and    * preceded by a non-vowel, and then undouble the ending    *    * @param sb String being stemmed    */
DECL|method|step2
specifier|private
name|void
name|step2
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
name|_removedE
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|_R1
operator|>=
name|sb
operator|.
name|length
argument_list|()
condition|)
return|return;
name|String
name|s
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|index
operator|>=
name|_R1
operator|&&
name|s
operator|.
name|endsWith
argument_list|(
literal|"e"
argument_list|)
operator|&&
operator|!
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
name|unDouble
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|_removedE
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Delete "heid"    *    * @param sb String being stemmed    */
DECL|method|step3a
specifier|private
name|void
name|step3a
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
if|if
condition|(
name|_R2
operator|>=
name|sb
operator|.
name|length
argument_list|()
condition|)
return|return;
name|String
name|s
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|4
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"heid"
argument_list|)
operator|&&
name|index
operator|>=
name|_R2
operator|&&
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|1
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|4
argument_list|)
expr_stmt|;
comment|//remove heid
name|enEnding
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *<p>A d-suffix, or derivational suffix, enables a new word,    * often with a different grammatical category, or with a different    * sense, to be built from another word. Whether a d-suffix can be    * attached is discovered not from the rules of grammar, but by    * referring to a dictionary. So in English, ness can be added to    * certain adjectives to form corresponding nouns (littleness,    * kindness, foolishness ...) but not to all adjectives    * (not for example, to big, cruel, wise ...) d-suffixes can be    * used to change meaning, often in rather exotic ways.</p>    * Remove "ing", "end", "ig", "lijk", "baar" and "bar"    *    * @param sb String being stemmed    */
DECL|method|step3b
specifier|private
name|void
name|step3b
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
if|if
condition|(
name|_R2
operator|>=
name|sb
operator|.
name|length
argument_list|()
condition|)
return|return;
name|String
name|s
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"end"
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"ing"
argument_list|)
operator|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|3
operator|)
operator|>=
name|_R2
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|3
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|2
argument_list|)
operator|==
literal|'i'
operator|&&
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|1
argument_list|)
operator|==
literal|'g'
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|3
argument_list|)
operator|!=
literal|'e'
operator|&
name|index
operator|-
literal|2
operator|>=
name|_R2
condition|)
block|{
name|index
operator|-=
literal|2
expr_stmt|;
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|unDouble
argument_list|(
name|sb
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"ig"
argument_list|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|)
operator|>=
name|_R2
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|1
argument_list|)
operator|!=
literal|'e'
condition|)
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|2
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"lijk"
argument_list|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|4
operator|)
operator|>=
name|_R2
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|4
argument_list|)
expr_stmt|;
name|step2
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"baar"
argument_list|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|4
operator|)
operator|>=
name|_R2
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|4
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"bar"
argument_list|)
operator|&&
operator|(
name|index
operator|=
name|s
operator|.
name|length
argument_list|()
operator|-
literal|3
operator|)
operator|>=
name|_R2
condition|)
block|{
if|if
condition|(
name|_removedE
condition|)
name|sb
operator|.
name|delete
argument_list|(
name|index
argument_list|,
name|index
operator|+
literal|3
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|/**    * undouble vowel    * If the words ends CVD, where C is a non-vowel, D is a non-vowel other than I, and V is double a, e, o or u, remove one of the vowels from V (for example, maan -> man, brood -> brod).    *    * @param sb String being stemmed    */
DECL|method|step4
specifier|private
name|void
name|step4
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|<
literal|4
condition|)
return|return;
name|String
name|end
init|=
name|sb
operator|.
name|substring
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|char
name|c
init|=
name|end
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|char
name|v1
init|=
name|end
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|char
name|v2
init|=
name|end
operator|.
name|charAt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|char
name|d
init|=
name|end
operator|.
name|charAt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|v1
operator|==
name|v2
operator|&&
name|d
operator|!=
literal|'I'
operator|&&
name|v1
operator|!=
literal|'i'
operator|&&
name|isVowel
argument_list|(
name|v1
argument_list|)
operator|&&
operator|!
name|isVowel
argument_list|(
name|d
argument_list|)
operator|&&
operator|!
name|isVowel
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Checks if a term could be stemmed.    *    * @return true if, and only if, the given term consists in letters.    */
DECL|method|isStemmable
specifier|private
name|boolean
name|isStemmable
parameter_list|(
name|String
name|term
parameter_list|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|term
operator|.
name|length
argument_list|()
condition|;
name|c
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetter
argument_list|(
name|term
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Substitute Ã¤, Ã«, Ã¯, Ã¶, Ã¼, Ã¡ , Ã©, Ã­, Ã³, Ãº    */
DECL|method|substitute
specifier|private
name|void
name|substitute
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|)
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
name|buffer
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|'Ã¤'
case|:
case|case
literal|'Ã¡'
case|:
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'Ã«'
case|:
case|case
literal|'Ã©'
case|:
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'e'
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'Ã¼'
case|:
case|case
literal|'Ãº'
case|:
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'u'
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'Ã¯'
case|:
case|case
literal|'i'
case|:
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'i'
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'Ã¶'
case|:
case|case
literal|'Ã³'
case|:
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'o'
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/*private boolean isValidSEnding(StringBuffer sb) {     return isValidSEnding(sb, sb.length() - 1);   }*/
DECL|method|isValidSEnding
specifier|private
name|boolean
name|isValidSEnding
parameter_list|(
name|StringBuffer
name|sb
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|char
name|c
init|=
name|sb
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|isVowel
argument_list|(
name|c
argument_list|)
operator|||
name|c
operator|==
literal|'j'
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/*private boolean isValidEnEnding(StringBuffer sb) {     return isValidEnEnding(sb, sb.length() - 1);   }*/
DECL|method|isValidEnEnding
specifier|private
name|boolean
name|isValidEnEnding
parameter_list|(
name|StringBuffer
name|sb
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|char
name|c
init|=
name|sb
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|isVowel
argument_list|(
name|c
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|c
operator|<
literal|3
condition|)
return|return
literal|false
return|;
comment|// ends with "gem"?
if|if
condition|(
name|c
operator|==
literal|'m'
operator|&&
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|2
argument_list|)
operator|==
literal|'g'
operator|&&
name|sb
operator|.
name|charAt
argument_list|(
name|index
operator|-
literal|1
argument_list|)
operator|==
literal|'e'
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|unDouble
specifier|private
name|void
name|unDouble
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
name|unDouble
argument_list|(
name|sb
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|unDouble
specifier|private
name|void
name|unDouble
parameter_list|(
name|StringBuffer
name|sb
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
name|String
name|s
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"kk"
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"tt"
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"dd"
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"nn"
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"mm"
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"ff"
argument_list|)
condition|)
block|{
name|sb
operator|.
name|delete
argument_list|(
name|endIndex
operator|-
literal|1
argument_list|,
name|endIndex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRIndex
specifier|private
name|int
name|getRIndex
parameter_list|(
name|StringBuffer
name|sb
parameter_list|,
name|int
name|start
parameter_list|)
block|{
if|if
condition|(
name|start
operator|==
literal|0
condition|)
name|start
operator|=
literal|1
expr_stmt|;
name|int
name|i
init|=
name|start
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|sb
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//first non-vowel preceded by a vowel
if|if
condition|(
operator|!
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|&&
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|i
operator|+
literal|1
return|;
block|}
block|}
return|return
name|i
operator|+
literal|1
return|;
block|}
DECL|method|storeYandI
specifier|private
name|void
name|storeYandI
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'y'
condition|)
name|sb
operator|.
name|setCharAt
argument_list|(
literal|0
argument_list|,
literal|'Y'
argument_list|)
expr_stmt|;
name|int
name|last
init|=
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|last
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|'i'
case|:
block|{
if|if
condition|(
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
operator|&&
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
condition|)
name|sb
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'I'
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'y'
case|:
block|{
if|if
condition|(
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
name|sb
operator|.
name|setCharAt
argument_list|(
name|i
argument_list|,
literal|'Y'
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|last
operator|>
literal|0
operator|&&
name|sb
operator|.
name|charAt
argument_list|(
name|last
argument_list|)
operator|==
literal|'y'
operator|&&
name|isVowel
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|last
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
name|sb
operator|.
name|setCharAt
argument_list|(
name|last
argument_list|,
literal|'Y'
argument_list|)
expr_stmt|;
block|}
DECL|method|reStoreYandI
specifier|private
name|void
name|reStoreYandI
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
name|String
name|tmp
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sb
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|tmp
operator|.
name|replaceAll
argument_list|(
literal|"I"
argument_list|,
literal|"i"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"Y"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isVowel
specifier|private
name|boolean
name|isVowel
parameter_list|(
name|char
name|c
parameter_list|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'e'
case|:
case|case
literal|'a'
case|:
case|case
literal|'o'
case|:
case|case
literal|'i'
case|:
case|case
literal|'u'
case|:
case|case
literal|'y'
case|:
case|case
literal|'Ã¨'
case|:
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|setStemDictionary
name|void
name|setStemDictionary
parameter_list|(
name|Map
name|dict
parameter_list|)
block|{
name|_stemDict
operator|=
name|dict
expr_stmt|;
block|}
block|}
end_class

end_unit

