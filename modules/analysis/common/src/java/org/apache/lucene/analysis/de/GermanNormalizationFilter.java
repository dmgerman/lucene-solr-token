begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|StemmerUtil
import|;
end_import

begin_comment
comment|/**  * Normalizes German characters according to the heuristics  * of the<a href="http://snowball.tartarus.org/algorithms/german2/stemmer.html">  * German2 snowball algorithm</a>.  * It allows for the fact that Ã¤, Ã¶ and Ã¼ are sometimes written as ae, oe and ue.  *<p>  *<ul>  *<li> 'Ã' is replaced by 'ss'  *<li> 'Ã¤', 'Ã¶', 'Ã¼' are replaced by 'a', 'o', 'u', respectively.  *<li> 'ae' and 'oe' are replaced by 'a', and 'o', respectively.  *<li> 'ue' is replaced by 'u', when not following a vowel or q.  *</ul>  *<p>  * This is useful if you want this normalization without using  * the German2 stemmer, or perhaps no stemming at all.  */
end_comment

begin_class
DECL|class|GermanNormalizationFilter
specifier|public
specifier|final
class|class
name|GermanNormalizationFilter
extends|extends
name|TokenFilter
block|{
comment|// FSM with 3 states:
DECL|field|N
specifier|private
specifier|static
specifier|final
name|int
name|N
init|=
literal|0
decl_stmt|;
comment|/* ordinary state */
DECL|field|V
specifier|private
specifier|static
specifier|final
name|int
name|V
init|=
literal|1
decl_stmt|;
comment|/* stops 'u' from entering umlaut state */
DECL|field|U
specifier|private
specifier|static
specifier|final
name|int
name|U
init|=
literal|2
decl_stmt|;
comment|/* umlaut state, allows e-deletion */
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
DECL|method|GermanNormalizationFilter
specifier|public
name|GermanNormalizationFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|state
init|=
name|N
decl_stmt|;
name|char
name|buffer
index|[]
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
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|buffer
index|[
name|i
index|]
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'a'
case|:
case|case
literal|'o'
case|:
name|state
operator|=
name|U
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
name|state
operator|=
operator|(
name|state
operator|==
name|N
operator|)
condition|?
name|U
else|:
name|V
expr_stmt|;
break|break;
case|case
literal|'e'
case|:
if|if
condition|(
name|state
operator|==
name|U
condition|)
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|--
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|state
operator|=
name|V
expr_stmt|;
break|break;
case|case
literal|'i'
case|:
case|case
literal|'q'
case|:
case|case
literal|'y'
case|:
name|state
operator|=
name|V
expr_stmt|;
break|break;
case|case
literal|'Ã¤'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
name|state
operator|=
name|V
expr_stmt|;
break|break;
case|case
literal|'Ã¶'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'o'
expr_stmt|;
name|state
operator|=
name|V
expr_stmt|;
break|break;
case|case
literal|'Ã¼'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'u'
expr_stmt|;
name|state
operator|=
name|V
expr_stmt|;
break|break;
case|case
literal|'Ã'
case|:
name|buffer
index|[
name|i
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
name|buffer
operator|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
literal|1
operator|+
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|length
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|i
argument_list|,
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
operator|(
name|length
operator|-
name|i
operator|)
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
literal|'s'
expr_stmt|;
name|length
operator|++
expr_stmt|;
name|state
operator|=
name|N
expr_stmt|;
break|break;
default|default:
name|state
operator|=
name|N
expr_stmt|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

