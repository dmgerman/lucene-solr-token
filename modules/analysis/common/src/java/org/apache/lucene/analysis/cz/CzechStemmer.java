begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cz
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cz
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Light Stemmer for Czech.  *<p>  * Implements the algorithm described in:    *<i>  * Indexing and stemming approaches for the Czech language  *</i>  * http://portal.acm.org/citation.cfm?id=1598600  *</p>  */
end_comment

begin_class
DECL|class|CzechStemmer
specifier|public
class|class
name|CzechStemmer
block|{
comment|/**    * Stem an input buffer of Czech text.    *     * @param s input buffer    * @param len length of input buffer    * @return length of input buffer after normalization    *     *<p><b>NOTE</b>: Input is expected to be in lowercase,     * but with diacritical marks</p>    */
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
name|len
operator|=
name|removeCase
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|removePossessives
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|normalize
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
DECL|method|removeCase
specifier|private
name|int
name|removeCase
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>
literal|7
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"atech"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|5
return|;
if|if
condition|(
name|len
operator|>
literal|6
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ätem"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"etem"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"atÅ¯m"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|4
return|;
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ech"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ich"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã­ch"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã©ho"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ämi"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"emi"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã©mu"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Äte"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ete"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Äti"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"eti"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã­ho"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"iho"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã­mi"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã­mu"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"imu"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã¡ch"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ata"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"aty"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã½ch"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ama"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ami"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ovÃ©"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ovi"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã½mi"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
if|if
condition|(
name|len
operator|>
literal|4
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"em"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"es"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã©m"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã­m"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¯m"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"at"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã¡m"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"os"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"us"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã½m"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"mi"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ou"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
if|if
condition|(
name|len
operator|>
literal|3
condition|)
block|{
switch|switch
condition|(
name|s
index|[
name|len
operator|-
literal|1
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
literal|'Å¯'
case|:
case|case
literal|'y'
case|:
case|case
literal|'Ã¡'
case|:
case|case
literal|'Ã©'
case|:
case|case
literal|'Ã­'
case|:
case|case
literal|'Ã½'
case|:
case|case
literal|'Ä'
case|:
return|return
name|len
operator|-
literal|1
return|;
block|}
block|}
return|return
name|len
return|;
block|}
DECL|method|removePossessives
specifier|private
name|int
name|removePossessives
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ov"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"in"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¯v"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
return|return
name|len
return|;
block|}
DECL|method|normalize
specifier|private
name|int
name|normalize
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ät"
argument_list|)
condition|)
block|{
comment|// Ät -> ck
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'c'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'k'
expr_stmt|;
return|return
name|len
return|;
block|}
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Å¡t"
argument_list|)
condition|)
block|{
comment|// Å¡t -> sk
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
literal|'k'
expr_stmt|;
return|return
name|len
return|;
block|}
switch|switch
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
condition|)
block|{
case|case
literal|'c'
case|:
comment|// [cÄ] -> k
case|case
literal|'Ä'
case|:
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'k'
expr_stmt|;
return|return
name|len
return|;
case|case
literal|'z'
case|:
comment|// [zÅ¾] -> h
case|case
literal|'Å¾'
case|:
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'h'
expr_stmt|;
return|return
name|len
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|1
operator|&&
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'e'
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
name|s
index|[
name|len
operator|-
literal|1
index|]
expr_stmt|;
comment|// e*> *
return|return
name|len
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|2
operator|&&
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'Å¯'
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'o'
expr_stmt|;
comment|// *Å¯* -> *o*
return|return
name|len
return|;
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

