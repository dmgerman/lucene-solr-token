begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_comment
comment|/**  * N-Gram version of edit distance based on paper by Grzegorz Kondrak,   * "N-gram similarity and distance". Proceedings of the Twelfth International   * Conference on String Processing and Information Retrieval (SPIRE 2005), pp. 115-126,   * Buenos Aires, Argentina, November 2005.   * http://www.cs.ualberta.ca/~kondrak/papers/spire05.pdf  *   * This implementation uses the position-based optimization to compute partial  * matches of n-gram sub-strings and adds a null-character prefix of size n-1   * so that the first character is contained in the same number of n-grams as   * a middle character.  Null-character prefix matches are discounted so that   * strings with no matching characters will return a distance of 0.  *   */
end_comment

begin_class
DECL|class|NGramDistance
specifier|public
class|class
name|NGramDistance
implements|implements
name|StringDistance
block|{
DECL|field|n
specifier|private
name|int
name|n
decl_stmt|;
comment|/**    * Creates an N-Gram distance measure using n-grams of the specified size.    * @param size The size of the n-gram to be used to compute the string distance.    */
DECL|method|NGramDistance
specifier|public
name|NGramDistance
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|size
expr_stmt|;
block|}
comment|/**    * Creates an N-Gram distance measure using n-grams of size 2.    */
DECL|method|NGramDistance
specifier|public
name|NGramDistance
parameter_list|()
block|{
name|this
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|getDistance
specifier|public
name|float
name|getDistance
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
specifier|final
name|int
name|sl
init|=
name|source
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|tl
init|=
name|target
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|sl
operator|==
literal|0
operator|||
name|tl
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|sl
operator|==
name|tl
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
name|int
name|cost
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|sl
operator|<
name|n
operator|||
name|tl
operator|<
name|n
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|ni
init|=
name|Math
operator|.
name|min
argument_list|(
name|sl
argument_list|,
name|tl
argument_list|)
init|;
name|i
operator|<
name|ni
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|source
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
name|target
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|cost
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|(
name|float
operator|)
name|cost
operator|/
name|Math
operator|.
name|max
argument_list|(
name|sl
argument_list|,
name|tl
argument_list|)
return|;
block|}
name|char
index|[]
name|sa
init|=
operator|new
name|char
index|[
name|sl
operator|+
name|n
operator|-
literal|1
index|]
decl_stmt|;
name|float
name|p
index|[]
decl_stmt|;
comment|//'previous' cost array, horizontally
name|float
name|d
index|[]
decl_stmt|;
comment|// cost array, horizontally
name|float
name|_d
index|[]
decl_stmt|;
comment|//placeholder to assist in swapping p and d
comment|//construct sa with prefix
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sa
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|n
operator|-
literal|1
condition|)
block|{
name|sa
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
comment|//add prefix
block|}
else|else
block|{
name|sa
index|[
name|i
index|]
operator|=
name|source
operator|.
name|charAt
argument_list|(
name|i
operator|-
name|n
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|p
operator|=
operator|new
name|float
index|[
name|sl
operator|+
literal|1
index|]
expr_stmt|;
name|d
operator|=
operator|new
name|float
index|[
name|sl
operator|+
literal|1
index|]
expr_stmt|;
comment|// indexes into strings s and t
name|int
name|i
decl_stmt|;
comment|// iterates through source
name|int
name|j
decl_stmt|;
comment|// iterates through target
name|char
index|[]
name|t_j
init|=
operator|new
name|char
index|[
name|n
index|]
decl_stmt|;
comment|// jth n-gram of t
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<=
name|sl
condition|;
name|i
operator|++
control|)
block|{
name|p
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<=
name|tl
condition|;
name|j
operator|++
control|)
block|{
comment|//construct t_j n-gram
if|if
condition|(
name|j
operator|<
name|n
condition|)
block|{
for|for
control|(
name|int
name|ti
init|=
literal|0
init|;
name|ti
operator|<
name|n
operator|-
name|j
condition|;
name|ti
operator|++
control|)
block|{
name|t_j
index|[
name|ti
index|]
operator|=
literal|0
expr_stmt|;
comment|//add prefix
block|}
for|for
control|(
name|int
name|ti
init|=
name|n
operator|-
name|j
init|;
name|ti
operator|<
name|n
condition|;
name|ti
operator|++
control|)
block|{
name|t_j
index|[
name|ti
index|]
operator|=
name|target
operator|.
name|charAt
argument_list|(
name|ti
operator|-
operator|(
name|n
operator|-
name|j
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|t_j
operator|=
name|target
operator|.
name|substring
argument_list|(
name|j
operator|-
name|n
argument_list|,
name|j
argument_list|)
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
name|d
index|[
literal|0
index|]
operator|=
name|j
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|sl
condition|;
name|i
operator|++
control|)
block|{
name|cost
operator|=
literal|0
expr_stmt|;
name|int
name|tn
init|=
name|n
decl_stmt|;
comment|//compare sa to t_j
for|for
control|(
name|int
name|ni
init|=
literal|0
init|;
name|ni
operator|<
name|n
condition|;
name|ni
operator|++
control|)
block|{
if|if
condition|(
name|sa
index|[
name|i
operator|-
literal|1
operator|+
name|ni
index|]
operator|!=
name|t_j
index|[
name|ni
index|]
condition|)
block|{
name|cost
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sa
index|[
name|i
operator|-
literal|1
operator|+
name|ni
index|]
operator|==
literal|0
condition|)
block|{
comment|//discount matches on prefix
name|tn
operator|--
expr_stmt|;
block|}
block|}
name|float
name|ec
init|=
operator|(
name|float
operator|)
name|cost
operator|/
name|tn
decl_stmt|;
comment|// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
name|d
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|,
name|p
index|[
name|i
index|]
operator|+
literal|1
argument_list|)
argument_list|,
name|p
index|[
name|i
operator|-
literal|1
index|]
operator|+
name|ec
argument_list|)
expr_stmt|;
block|}
comment|// copy current distance counts to 'previous row' distance counts
name|_d
operator|=
name|p
expr_stmt|;
name|p
operator|=
name|d
expr_stmt|;
name|d
operator|=
name|_d
expr_stmt|;
block|}
comment|// our last action in the above loop was to switch d and p, so p now
comment|// actually has the most recent cost counts
return|return
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|p
index|[
name|sl
index|]
operator|/
name|Math
operator|.
name|max
argument_list|(
name|tl
argument_list|,
name|sl
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

