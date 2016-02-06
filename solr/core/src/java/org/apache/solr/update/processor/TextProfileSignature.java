begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_comment
comment|/**  *<p>This implementation is copied from Apache Nutch.</p>  *<p>An implementation of a page signature. It calculates an MD5 hash  * of a plain text "profile" of a page.</p>  *<p>The algorithm to calculate a page "profile" takes the plain text version of  * a page and performs the following steps:  *<ul>  *<li>remove all characters except letters and digits, and bring all characters  * to lower case,</li>  *<li>split the text into tokens (all consecutive non-whitespace characters),</li>  *<li>discard tokens equal or shorter than MIN_TOKEN_LEN (default 2 characters),</li>  *<li>sort the list of tokens by decreasing frequency,</li>  *<li>round down the counts of tokens to the nearest multiple of QUANT  * (<code>QUANT = QUANT_RATE * maxFreq</code>, where<code>QUANT_RATE</code> is 0.01f  * by default, and<code>maxFreq</code> is the maximum token frequency). If  *<code>maxFreq</code> is higher than 1, then QUANT is always higher than 2 (which  * means that tokens with frequency 1 are always discarded).</li>  *<li>tokens, which frequency after quantization falls below QUANT, are discarded.</li>  *<li>create a list of tokens and their quantized frequency, separated by spaces,  * in the order of decreasing frequency.</li>  *</ul>  * This list is then submitted to an MD5 hash calculation.*/
end_comment

begin_class
DECL|class|TextProfileSignature
specifier|public
class|class
name|TextProfileSignature
extends|extends
name|MD5Signature
block|{
DECL|field|quantRate
specifier|private
name|float
name|quantRate
decl_stmt|;
DECL|field|minTokenLen
specifier|private
name|float
name|minTokenLen
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|quantRate
operator|=
name|params
operator|.
name|getFloat
argument_list|(
literal|"quantRate"
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
name|minTokenLen
operator|=
name|params
operator|.
name|getInt
argument_list|(
literal|"minTokenLen"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSignature
specifier|public
name|byte
index|[]
name|getSignature
parameter_list|()
block|{
return|return
name|super
operator|.
name|getSignature
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Token
argument_list|>
name|tokens
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|curToken
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|maxFreq
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
name|content
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|content
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|curToken
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|curToken
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|curToken
operator|.
name|length
argument_list|()
operator|>
name|minTokenLen
condition|)
block|{
comment|// add it
name|String
name|s
init|=
name|curToken
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Token
name|tok
init|=
name|tokens
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|tok
operator|==
literal|null
condition|)
block|{
name|tok
operator|=
operator|new
name|Token
argument_list|(
literal|0
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|tok
argument_list|)
expr_stmt|;
block|}
name|tok
operator|.
name|cnt
operator|++
expr_stmt|;
if|if
condition|(
name|tok
operator|.
name|cnt
operator|>
name|maxFreq
condition|)
name|maxFreq
operator|=
name|tok
operator|.
name|cnt
expr_stmt|;
block|}
name|curToken
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// check the last token
if|if
condition|(
name|curToken
operator|.
name|length
argument_list|()
operator|>
name|minTokenLen
condition|)
block|{
comment|// add it
name|String
name|s
init|=
name|curToken
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Token
name|tok
init|=
name|tokens
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|tok
operator|==
literal|null
condition|)
block|{
name|tok
operator|=
operator|new
name|Token
argument_list|(
literal|0
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|tok
argument_list|)
expr_stmt|;
block|}
name|tok
operator|.
name|cnt
operator|++
expr_stmt|;
if|if
condition|(
name|tok
operator|.
name|cnt
operator|>
name|maxFreq
condition|)
name|maxFreq
operator|=
name|tok
operator|.
name|cnt
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Token
argument_list|>
name|it
init|=
name|tokens
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|profile
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// calculate the QUANT value
name|int
name|quant
init|=
name|Math
operator|.
name|round
argument_list|(
name|maxFreq
operator|*
name|quantRate
argument_list|)
decl_stmt|;
if|if
condition|(
name|quant
operator|<
literal|2
condition|)
block|{
if|if
condition|(
name|maxFreq
operator|>
literal|1
condition|)
name|quant
operator|=
literal|2
expr_stmt|;
else|else
name|quant
operator|=
literal|1
expr_stmt|;
block|}
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Token
name|t
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// round down to the nearest QUANT
name|t
operator|.
name|cnt
operator|=
operator|(
name|t
operator|.
name|cnt
operator|/
name|quant
operator|)
operator|*
name|quant
expr_stmt|;
comment|// discard the frequencies below the QUANT
if|if
condition|(
name|t
operator|.
name|cnt
operator|<
name|quant
condition|)
block|{
continue|continue;
block|}
name|profile
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|profile
argument_list|,
operator|new
name|TokenComparator
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|newText
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|it
operator|=
name|profile
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Token
name|t
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|newText
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|newText
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|newText
operator|.
name|append
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|add
argument_list|(
name|newText
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|Token
specifier|private
specifier|static
class|class
name|Token
block|{
DECL|field|cnt
specifier|public
name|int
name|cnt
decl_stmt|;
DECL|field|val
specifier|public
name|String
name|val
decl_stmt|;
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|cnt
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|this
operator|.
name|cnt
operator|=
name|cnt
expr_stmt|;
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|val
operator|+
literal|" "
operator|+
name|cnt
return|;
block|}
block|}
DECL|class|TokenComparator
specifier|private
specifier|static
class|class
name|TokenComparator
implements|implements
name|Comparator
argument_list|<
name|Token
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Token
name|t1
parameter_list|,
name|Token
name|t2
parameter_list|)
block|{
return|return
name|t2
operator|.
name|cnt
operator|-
name|t1
operator|.
name|cnt
return|;
block|}
block|}
block|}
end_class

end_unit

