begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * This class can be used if the token attributes of a TokenStream  * are intended to be consumed more than once. It caches  * all token attribute states locally in a List when the first call to  * {@link #incrementToken()} is called. Subsequent calls will used the cache.  *<p>  *<em>Important:</em> Like any proper TokenFilter, {@link #reset()} propagates  * to the input, although only before {@link #incrementToken()} is called the  * first time. Prior to  Lucene 5, it was never propagated.  */
end_comment

begin_class
DECL|class|CachingTokenFilter
specifier|public
specifier|final
class|class
name|CachingTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|cache
specifier|private
name|List
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
name|cache
init|=
literal|null
decl_stmt|;
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
name|iterator
init|=
literal|null
decl_stmt|;
DECL|field|finalState
specifier|private
name|AttributeSource
operator|.
name|State
name|finalState
decl_stmt|;
comment|/**    * Create a new CachingTokenFilter around<code>input</code>. As with    * any normal TokenFilter, do<em>not</em> call reset on the input; this filter    * will do it normally.    */
DECL|method|CachingTokenFilter
specifier|public
name|CachingTokenFilter
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
comment|/**    * Propagates reset if incrementToken has not yet been called. Otherwise    * it rewinds the iterator to the beginning of the cached list.    */
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
comment|//first time
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|iterator
operator|=
name|cache
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** The first time called, it'll read and cache all tokens from the input. */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
comment|//first-time
comment|// fill cache lazily
name|cache
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|fillCache
argument_list|()
expr_stmt|;
name|iterator
operator|=
name|cache
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// the cache is exhausted, return false
return|return
literal|false
return|;
block|}
comment|// Since the TokenFilter can be reset, the tokens need to be preserved as immutable.
name|restoreState
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
if|if
condition|(
name|finalState
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|finalState
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fillCache
specifier|private
name|void
name|fillCache
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|cache
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// capture final state
name|input
operator|.
name|end
argument_list|()
expr_stmt|;
name|finalState
operator|=
name|captureState
argument_list|()
expr_stmt|;
block|}
comment|/** If the underlying token stream was consumed and cached. */
DECL|method|isCached
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
return|return
name|cache
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

