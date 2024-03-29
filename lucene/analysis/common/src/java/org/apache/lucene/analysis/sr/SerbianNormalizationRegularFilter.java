begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.sr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sr
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
comment|/**  * Normalizes Serbian Cyrillic to Latin.  *  * Note that it expects lowercased input.  */
end_comment

begin_class
DECL|class|SerbianNormalizationRegularFilter
specifier|public
specifier|final
class|class
name|SerbianNormalizationRegularFilter
extends|extends
name|TokenFilter
block|{
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
DECL|method|SerbianNormalizationRegularFilter
specifier|public
name|SerbianNormalizationRegularFilter
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
literal|'Ð°'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'Ð±'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'b'
expr_stmt|;
break|break;
case|case
literal|'Ð²'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'v'
expr_stmt|;
break|break;
case|case
literal|'Ð³'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'g'
expr_stmt|;
break|break;
case|case
literal|'Ð´'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'d'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'Ä'
expr_stmt|;
break|break;
case|case
literal|'Ðµ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'Ð¶'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'Å¾'
expr_stmt|;
break|break;
case|case
literal|'Ð·'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'z'
expr_stmt|;
break|break;
case|case
literal|'Ð¸'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'Ðº'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'k'
expr_stmt|;
break|break;
case|case
literal|'Ð»'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'l'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
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
block|{
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
block|}
name|buffer
index|[
name|i
index|]
operator|=
literal|'l'
expr_stmt|;
name|buffer
index|[
operator|++
name|i
index|]
operator|=
literal|'j'
expr_stmt|;
name|length
operator|++
expr_stmt|;
break|break;
case|case
literal|'Ð¼'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'m'
expr_stmt|;
break|break;
case|case
literal|'Ð½'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'n'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
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
block|{
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
block|}
name|buffer
index|[
name|i
index|]
operator|=
literal|'n'
expr_stmt|;
name|buffer
index|[
operator|++
name|i
index|]
operator|=
literal|'j'
expr_stmt|;
name|length
operator|++
expr_stmt|;
break|break;
case|case
literal|'Ð¾'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'Ð¿'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'p'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'r'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'s'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'t'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'Ä'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'f'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'h'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'c'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'Ä'
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
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
block|{
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
block|}
name|buffer
index|[
name|i
index|]
operator|=
literal|'d'
expr_stmt|;
name|buffer
index|[
operator|++
name|i
index|]
operator|=
literal|'Å¾'
expr_stmt|;
name|length
operator|++
expr_stmt|;
break|break;
case|case
literal|'Ñ'
case|:
name|buffer
index|[
name|i
index|]
operator|=
literal|'Å¡'
expr_stmt|;
break|break;
default|default:
break|break;
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

