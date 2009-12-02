begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.bg
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|bg
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Light Stemmer for Bulgarian.  *<p>  * Implements the algorithm described in:    *<i>  * Searching Strategies for the Bulgarian Language  *</i>  * http://members.unine.ch/jacques.savoy/Papers/BUIR.pdf  */
end_comment

begin_class
DECL|class|BulgarianStemmer
specifier|public
class|class
name|BulgarianStemmer
block|{
comment|/**    * Stem an input buffer of Bulgarian text.    *     * @param s input buffer    * @param len length of input buffer    * @return length of input buffer after normalization    */
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
specifier|final
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
comment|// do not stem
return|return
name|len
return|;
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
literal|"Ð¸ÑÐ°"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
name|len
operator|=
name|removeArticle
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
if|if
condition|(
name|len
operator|>
literal|3
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
literal|"Ñ"
argument_list|)
condition|)
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ð°"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ð¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ðµ"
argument_list|)
condition|)
name|len
operator|--
expr_stmt|;
block|}
comment|// the rule to rewrite ÐµÐ½ -> Ð½ is duplicated in the paper.
comment|// in the perl implementation referenced by the paper, this is fixed.
comment|// (it is fixed here as well)
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
literal|"ÐµÐ½"
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
literal|'Ð½'
expr_stmt|;
comment|// replace with Ð½
name|len
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'Ñ'
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
comment|// replace ÑN with N
name|len
operator|--
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
comment|/**    * Mainly remove the definite article    * @param s input buffer    * @param len length of input buffer    * @return new stemmed length    */
DECL|method|removeArticle
specifier|private
name|int
name|removeArticle
parameter_list|(
specifier|final
name|char
name|s
index|[]
parameter_list|,
specifier|final
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
literal|"Ð¸ÑÑ"
argument_list|)
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
literal|"ÑÑ"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ÑÐ¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ÑÐµ"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ÑÐ°"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ð¸Ñ"
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
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ÑÑ"
argument_list|)
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
DECL|method|removePlural
specifier|private
name|int
name|removePlural
parameter_list|(
specifier|final
name|char
name|s
index|[]
parameter_list|,
specifier|final
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
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ð¾Ð²ÑÐ¸"
argument_list|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
comment|// replace with Ð¾
if|if
condition|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ð¾Ð²Ðµ"
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
literal|"ÐµÐ²Ðµ"
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
literal|'Ð¹'
expr_stmt|;
comment|// replace with Ð¹
return|return
name|len
operator|-
literal|2
return|;
block|}
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
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ð¸ÑÐ°"
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
literal|"ÑÐ°"
argument_list|)
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
literal|"ÑÐ¸"
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
literal|'Ðº'
expr_stmt|;
comment|// replace with Ðº
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
literal|"Ð·Ð¸"
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
literal|'Ð³'
expr_stmt|;
comment|// replace with Ð³
return|return
name|len
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|==
literal|'Ðµ'
operator|&&
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'Ð¸'
condition|)
block|{
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'Ñ'
expr_stmt|;
comment|// replace Ðµ with Ñ, remove Ð¸
return|return
name|len
operator|-
literal|1
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
literal|"ÑÐ¸"
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
literal|'Ñ'
expr_stmt|;
comment|// replace with Ñ
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
literal|"Ð¸"
argument_list|)
condition|)
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
DECL|method|endsWith
specifier|private
name|boolean
name|endsWith
parameter_list|(
specifier|final
name|char
name|s
index|[]
parameter_list|,
specifier|final
name|int
name|len
parameter_list|,
specifier|final
name|String
name|suffix
parameter_list|)
block|{
specifier|final
name|int
name|suffixLen
init|=
name|suffix
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|suffixLen
operator|>
name|len
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
name|suffixLen
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
if|if
condition|(
name|s
index|[
name|len
operator|-
operator|(
name|suffixLen
operator|-
name|i
operator|)
index|]
operator|!=
name|suffix
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

