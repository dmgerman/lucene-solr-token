begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.pt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pt
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
comment|/**  * Light Stemmer for Portuguese  *<p>  * This stemmer implements the "UniNE" algorithm in:  *<i>Light Stemming Approaches for the French, Portuguese, German and Hungarian Languages</i>  * Jacques Savoy  */
end_comment

begin_class
DECL|class|PortugueseLightStemmer
specifier|public
class|class
name|PortugueseLightStemmer
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
if|if
condition|(
name|len
operator|<
literal|4
condition|)
return|return
name|len
return|;
name|len
operator|=
name|removeSuffix
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
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
literal|'a'
condition|)
name|len
operator|=
name|normFeminine
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|4
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
literal|'e'
case|:
case|case
literal|'a'
case|:
case|case
literal|'o'
case|:
name|len
operator|--
expr_stmt|;
break|break;
block|}
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
literal|'Ã '
case|:
case|case
literal|'Ã¡'
case|:
case|case
literal|'Ã¢'
case|:
case|case
literal|'Ã¤'
case|:
case|case
literal|'Ã£'
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
literal|'Ã²'
case|:
case|case
literal|'Ã³'
case|:
case|case
literal|'Ã´'
case|:
case|case
literal|'Ã¶'
case|:
case|case
literal|'Ãµ'
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
literal|'Ã¨'
case|:
case|case
literal|'Ã©'
case|:
case|case
literal|'Ãª'
case|:
case|case
literal|'Ã«'
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
literal|'Ã¹'
case|:
case|case
literal|'Ãº'
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
case|case
literal|'Ã¬'
case|:
case|case
literal|'Ã­'
case|:
case|case
literal|'Ã®'
case|:
case|case
literal|'Ã¯'
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
literal|'Ã§'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'c'
expr_stmt|;
break|break;
block|}
return|return
name|len
return|;
block|}
DECL|method|removeSuffix
specifier|private
name|int
name|removeSuffix
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
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"es"
argument_list|)
condition|)
switch|switch
condition|(
name|s
index|[
name|len
operator|-
literal|3
index|]
condition|)
block|{
case|case
literal|'r'
case|:
case|case
literal|'s'
case|:
case|case
literal|'l'
case|:
case|case
literal|'z'
case|:
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
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ns"
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
literal|'m'
expr_stmt|;
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
literal|4
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"eis"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã©is"
argument_list|)
operator|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'l'
expr_stmt|;
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
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ais"
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
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã³is"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'o'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'l'
expr_stmt|;
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
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"is"
argument_list|)
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
if|if
condition|(
name|len
operator|>
literal|3
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ãµes"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã£es"
argument_list|)
operator|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'Ã£'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'o'
expr_stmt|;
return|return
name|len
return|;
block|}
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
literal|"mente"
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
literal|3
operator|&&
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'s'
condition|)
return|return
name|len
operator|-
literal|1
return|;
return|return
name|len
return|;
block|}
DECL|method|normFeminine
specifier|private
name|int
name|normFeminine
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
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"inha"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"iaca"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"eira"
argument_list|)
operator|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'o'
expr_stmt|;
return|return
name|len
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|6
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
literal|"osa"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ica"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ida"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ada"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"iva"
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
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'o'
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
literal|"ona"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'Ã£'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'o'
expr_stmt|;
return|return
name|len
operator|-
literal|1
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
literal|"ora"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|1
return|;
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"esa"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'Ãª'
expr_stmt|;
return|return
name|len
operator|-
literal|1
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
literal|"na"
argument_list|)
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'o'
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

