begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|util
operator|.
name|CharArraySet
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * A {@link TokenFilter} that decomposes compound words found in many Germanic languages.  *<p>  * "Donaudampfschiff" becomes Donau, dampf, schiff so that you can find  * "Donaudampfschiff" even when you only enter "schiff".   *  It uses a brute-force algorithm to achieve this.  *<p>  * You must specify the required {@link Version} compatibility when creating  * CompoundWordTokenFilterBase:  *<ul>  *<li>As of 3.1, CompoundWordTokenFilterBase correctly handles Unicode 4.0  * supplementary characters in strings and char arrays provided as compound word  * dictionaries.  *</ul>  */
end_comment

begin_class
DECL|class|DictionaryCompoundWordTokenFilter
specifier|public
class|class
name|DictionaryCompoundWordTokenFilter
extends|extends
name|CompoundWordTokenFilterBase
block|{
comment|/**    * Creates a new {@link DictionaryCompoundWordTokenFilter}    *     * @param matchVersion    *          Lucene version to enable correct Unicode 4.0 behavior in the    *          dictionaries if Version> 3.0. See<a    *          href="CompoundWordTokenFilterBase.html#version"    *>CompoundWordTokenFilterBase</a> for details.    * @param input    *          the {@link TokenStream} to process    * @param dictionary    *          the word dictionary to match against.    */
DECL|method|DictionaryCompoundWordTokenFilter
specifier|public
name|DictionaryCompoundWordTokenFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link DictionaryCompoundWordTokenFilter}    *     * @param matchVersion    *          Lucene version to enable correct Unicode 4.0 behavior in the    *          dictionaries if Version> 3.0. See<a    *          href="CompoundWordTokenFilterBase.html#version"    *>CompoundWordTokenFilterBase</a> for details.    * @param input    *          the {@link TokenStream} to process    * @param dictionary    *          the word dictionary to match against.    * @param minWordSize    *          only words longer than this get processed    * @param minSubwordSize    *          only subwords longer than this get to the output stream    * @param maxSubwordSize    *          only subwords shorter than this get to the output stream    * @param onlyLongestMatch    *          Add only the longest matching subword to the stream    */
DECL|method|DictionaryCompoundWordTokenFilter
specifier|public
name|DictionaryCompoundWordTokenFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decompose
specifier|protected
name|void
name|decompose
parameter_list|()
block|{
specifier|final
name|int
name|len
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
operator|<=
name|len
operator|-
name|this
operator|.
name|minSubwordSize
condition|;
operator|++
name|i
control|)
block|{
name|CompoundToken
name|longestMatchToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|this
operator|.
name|minSubwordSize
init|;
name|j
operator|<=
name|this
operator|.
name|maxSubwordSize
condition|;
operator|++
name|j
control|)
block|{
if|if
condition|(
name|i
operator|+
name|j
operator|>
name|len
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|dictionary
operator|.
name|contains
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|onlyLongestMatch
condition|)
block|{
if|if
condition|(
name|longestMatchToken
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|longestMatchToken
operator|.
name|txt
operator|.
name|length
argument_list|()
operator|<
name|j
condition|)
block|{
name|longestMatchToken
operator|=
operator|new
name|CompoundToken
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|longestMatchToken
operator|=
operator|new
name|CompoundToken
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|CompoundToken
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|this
operator|.
name|onlyLongestMatch
operator|&&
name|longestMatchToken
operator|!=
literal|null
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|longestMatchToken
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

