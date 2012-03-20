begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.no
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|no
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Minimal Stemmer for Norwegian bokmÃ¥l (no-nb)  *<p>  * Stems known plural forms for Norwegian nouns only, together with genitiv -s  */
end_comment

begin_class
DECL|class|NorwegianMinimalStemmer
specifier|public
class|class
name|NorwegianMinimalStemmer
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
comment|// Remove genitiv s
if|if
condition|(
name|len
operator|>
literal|4
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
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ene"
argument_list|)
comment|// masc/fem/neutr pl definite (hus-ene)
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
literal|"er"
argument_list|)
operator|||
comment|// masc/fem indefinite
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"en"
argument_list|)
operator|||
comment|// masc/fem definite
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"et"
argument_list|)
comment|// neutr definite
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
comment|// fem definite
case|case
literal|'e'
case|:
comment|// to get correct stem for nouns ending in -e (kake -> kak, kaker -> kak)
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
block|}
end_class

end_unit

