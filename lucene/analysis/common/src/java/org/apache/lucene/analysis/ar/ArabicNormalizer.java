begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  *  Normalizer for Arabic.  *<p>  *  Normalization is done in-place for efficiency, operating on a termbuffer.  *<p>  *  Normalization is defined as:  *<ul>  *<li> Normalization of hamza with alef seat to a bare alef.  *<li> Normalization of teh marbuta to heh  *<li> Normalization of dotless yeh (alef maksura) to yeh.  *<li> Removal of Arabic diacritics (the harakat)  *<li> Removal of tatweel (stretching character).  *</ul>  *  */
end_comment

begin_class
DECL|class|ArabicNormalizer
specifier|public
class|class
name|ArabicNormalizer
block|{
DECL|field|ALEF
specifier|public
specifier|static
specifier|final
name|char
name|ALEF
init|=
literal|'\u0627'
decl_stmt|;
DECL|field|ALEF_MADDA
specifier|public
specifier|static
specifier|final
name|char
name|ALEF_MADDA
init|=
literal|'\u0622'
decl_stmt|;
DECL|field|ALEF_HAMZA_ABOVE
specifier|public
specifier|static
specifier|final
name|char
name|ALEF_HAMZA_ABOVE
init|=
literal|'\u0623'
decl_stmt|;
DECL|field|ALEF_HAMZA_BELOW
specifier|public
specifier|static
specifier|final
name|char
name|ALEF_HAMZA_BELOW
init|=
literal|'\u0625'
decl_stmt|;
DECL|field|YEH
specifier|public
specifier|static
specifier|final
name|char
name|YEH
init|=
literal|'\u064A'
decl_stmt|;
DECL|field|DOTLESS_YEH
specifier|public
specifier|static
specifier|final
name|char
name|DOTLESS_YEH
init|=
literal|'\u0649'
decl_stmt|;
DECL|field|TEH_MARBUTA
specifier|public
specifier|static
specifier|final
name|char
name|TEH_MARBUTA
init|=
literal|'\u0629'
decl_stmt|;
DECL|field|HEH
specifier|public
specifier|static
specifier|final
name|char
name|HEH
init|=
literal|'\u0647'
decl_stmt|;
DECL|field|TATWEEL
specifier|public
specifier|static
specifier|final
name|char
name|TATWEEL
init|=
literal|'\u0640'
decl_stmt|;
DECL|field|FATHATAN
specifier|public
specifier|static
specifier|final
name|char
name|FATHATAN
init|=
literal|'\u064B'
decl_stmt|;
DECL|field|DAMMATAN
specifier|public
specifier|static
specifier|final
name|char
name|DAMMATAN
init|=
literal|'\u064C'
decl_stmt|;
DECL|field|KASRATAN
specifier|public
specifier|static
specifier|final
name|char
name|KASRATAN
init|=
literal|'\u064D'
decl_stmt|;
DECL|field|FATHA
specifier|public
specifier|static
specifier|final
name|char
name|FATHA
init|=
literal|'\u064E'
decl_stmt|;
DECL|field|DAMMA
specifier|public
specifier|static
specifier|final
name|char
name|DAMMA
init|=
literal|'\u064F'
decl_stmt|;
DECL|field|KASRA
specifier|public
specifier|static
specifier|final
name|char
name|KASRA
init|=
literal|'\u0650'
decl_stmt|;
DECL|field|SHADDA
specifier|public
specifier|static
specifier|final
name|char
name|SHADDA
init|=
literal|'\u0651'
decl_stmt|;
DECL|field|SUKUN
specifier|public
specifier|static
specifier|final
name|char
name|SUKUN
init|=
literal|'\u0652'
decl_stmt|;
comment|/**    * Normalize an input buffer of Arabic text    *     * @param s input buffer    * @param len length of input buffer    * @return length of input buffer after normalization    */
DECL|method|normalize
specifier|public
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
name|ALEF_MADDA
case|:
case|case
name|ALEF_HAMZA_ABOVE
case|:
case|case
name|ALEF_HAMZA_BELOW
case|:
name|s
index|[
name|i
index|]
operator|=
name|ALEF
expr_stmt|;
break|break;
case|case
name|DOTLESS_YEH
case|:
name|s
index|[
name|i
index|]
operator|=
name|YEH
expr_stmt|;
break|break;
case|case
name|TEH_MARBUTA
case|:
name|s
index|[
name|i
index|]
operator|=
name|HEH
expr_stmt|;
break|break;
case|case
name|TATWEEL
case|:
case|case
name|KASRATAN
case|:
case|case
name|DAMMATAN
case|:
case|case
name|FATHATAN
case|:
case|case
name|FATHA
case|:
case|case
name|DAMMA
case|:
case|case
name|KASRA
case|:
case|case
name|SHADDA
case|:
case|case
name|SUKUN
case|:
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

