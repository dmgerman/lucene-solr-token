begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hu
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*   * This algorithm is updated based on code located at:  * http://members.unine.ch/jacques.savoy/clef/  *   * Full copyright for that code follows:  */
end_comment

begin_comment
comment|/*  * Copyright (c) 2005, Jacques Savoy  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without   * modification, are permitted provided that the following conditions are met:  *  * Redistributions of source code must retain the above copyright notice, this   * list of conditions and the following disclaimer. Redistributions in binary   * form must reproduce the above copyright notice, this list of conditions and  * the following disclaimer in the documentation and/or other materials   * provided with the distribution. Neither the name of the author nor the names   * of its contributors may be used to endorse or promote products derived from   * this software without specific prior written permission.  *   * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE   * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE   * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE   * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR   * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF   * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS   * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN   * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)   * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  * POSSIBILITY OF SUCH DAMAGE.  */
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
comment|/**  * Light Stemmer for Hungarian.  *<p>  * This stemmer implements the "UniNE" algorithm in:  *<i>Light Stemming Approaches for the French, Portuguese, German and Hungarian Languages</i>  * Jacques Savoy  */
end_comment

begin_class
DECL|class|HungarianLightStemmer
specifier|public
class|class
name|HungarianLightStemmer
block|{
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
switch|switch
condition|(
name|s
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'Ã¡'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'Ã«'
case|:
case|case
literal|'Ã©'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'Ã­'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'Ã³'
case|:
case|case
literal|'Å'
case|:
case|case
literal|'Ãµ'
case|:
case|case
literal|'Ã¶'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'Ãº'
case|:
case|case
literal|'Å±'
case|:
case|case
literal|'Å©'
case|:
case|case
literal|'Ã»'
case|:
case|case
literal|'Ã¼'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
block|}
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
name|removePossessive
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|removePlural
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|normalize
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
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
literal|6
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"kent"
argument_list|)
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
condition|)
block|{
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nak"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nek"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"val"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"vel"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ert"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"rol"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ban"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ben"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"bol"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nal"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nel"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"hoz"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"hez"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"tol"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"al"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"el"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|3
index|]
argument_list|)
operator|&&
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|==
name|s
index|[
name|len
operator|-
literal|4
index|]
condition|)
return|return
name|len
operator|-
literal|3
return|;
block|}
block|}
if|if
condition|(
name|len
operator|>
literal|4
condition|)
block|{
if|if
condition|(
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
literal|"et"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ot"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"va"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ve"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ra"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"re"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ba"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"be"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ul"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ig"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
if|if
condition|(
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"on"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"en"
argument_list|)
operator|)
operator|&&
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|3
index|]
argument_list|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
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
literal|'t'
case|:
case|case
literal|'n'
case|:
return|return
name|len
operator|-
literal|1
return|;
case|case
literal|'a'
case|:
case|case
literal|'e'
case|:
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|&&
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|2
index|]
argument_list|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
block|}
block|}
return|return
name|len
return|;
block|}
DECL|method|removePossessive
specifier|private
name|int
name|removePossessive
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
literal|6
condition|)
block|{
if|if
condition|(
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|5
index|]
argument_list|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"atok"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"otok"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"etek"
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
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"itek"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"itok"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|4
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|5
condition|)
block|{
if|if
condition|(
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|4
index|]
argument_list|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"unk"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"tok"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"tek"
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
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|4
index|]
argument_list|)
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"juk"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ink"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|4
condition|)
block|{
if|if
condition|(
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|3
index|]
argument_list|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"am"
argument_list|)
operator|||
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
literal|"om"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ad"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ed"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"od"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"uk"
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
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|3
index|]
argument_list|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nk"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ja"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"je"
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
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"im"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"id"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ik"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|3
condition|)
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
if|if
condition|(
operator|!
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|2
index|]
argument_list|)
condition|)
return|return
name|len
operator|-
literal|1
return|;
break|break;
case|case
literal|'m'
case|:
case|case
literal|'d'
case|:
if|if
condition|(
name|isVowel
argument_list|(
name|s
index|[
name|len
operator|-
literal|2
index|]
argument_list|)
condition|)
return|return
name|len
operator|-
literal|1
return|;
break|break;
case|case
literal|'i'
case|:
return|return
name|len
operator|-
literal|1
return|;
block|}
return|return
name|len
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|removePlural
specifier|private
name|int
name|removePlural
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
literal|3
operator|&&
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'k'
condition|)
switch|switch
condition|(
name|s
index|[
name|len
operator|-
literal|2
index|]
condition|)
block|{
case|case
literal|'a'
case|:
case|case
literal|'o'
case|:
case|case
literal|'e'
case|:
if|if
condition|(
name|len
operator|>
literal|4
condition|)
return|return
name|len
operator|-
literal|2
return|;
comment|/* intentional fallthru */
default|default:
return|return
name|len
operator|-
literal|1
return|;
block|}
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
name|len
operator|>
literal|3
condition|)
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
return|return
name|len
operator|-
literal|1
return|;
block|}
return|return
name|len
return|;
block|}
DECL|method|isVowel
specifier|private
name|boolean
name|isVowel
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
switch|switch
condition|(
name|ch
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
literal|'y'
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

