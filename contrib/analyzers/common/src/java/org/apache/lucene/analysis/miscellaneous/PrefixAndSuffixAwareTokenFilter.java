begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Token
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Links two {@link PrefixAwareTokenFilter}.  *<p/>  *<b>NOTE:</b> This filter might not behave correctly if used with custom Attributes, i.e. Attributes other than  * the ones located in org.apache.lucene.analysis.tokenattributes.   */
end_comment

begin_class
DECL|class|PrefixAndSuffixAwareTokenFilter
specifier|public
class|class
name|PrefixAndSuffixAwareTokenFilter
extends|extends
name|TokenStream
block|{
DECL|field|suffix
specifier|private
name|PrefixAwareTokenFilter
name|suffix
decl_stmt|;
DECL|method|PrefixAndSuffixAwareTokenFilter
specifier|public
name|PrefixAndSuffixAwareTokenFilter
parameter_list|(
name|TokenStream
name|prefix
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|TokenStream
name|suffix
parameter_list|)
block|{
name|super
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|prefix
operator|=
operator|new
name|PrefixAwareTokenFilter
argument_list|(
name|prefix
argument_list|,
name|input
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Token
name|updateSuffixToken
parameter_list|(
name|Token
name|suffixToken
parameter_list|,
name|Token
name|lastInputToken
parameter_list|)
block|{
return|return
name|PrefixAndSuffixAwareTokenFilter
operator|.
name|this
operator|.
name|updateInputToken
argument_list|(
name|suffixToken
argument_list|,
name|lastInputToken
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|suffix
operator|=
operator|new
name|PrefixAwareTokenFilter
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Token
name|updateSuffixToken
parameter_list|(
name|Token
name|suffixToken
parameter_list|,
name|Token
name|lastInputToken
parameter_list|)
block|{
return|return
name|PrefixAndSuffixAwareTokenFilter
operator|.
name|this
operator|.
name|updateSuffixToken
argument_list|(
name|suffixToken
argument_list|,
name|lastInputToken
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|updateInputToken
specifier|public
name|Token
name|updateInputToken
parameter_list|(
name|Token
name|inputToken
parameter_list|,
name|Token
name|lastPrefixToken
parameter_list|)
block|{
name|inputToken
operator|.
name|setStartOffset
argument_list|(
name|lastPrefixToken
operator|.
name|endOffset
argument_list|()
operator|+
name|inputToken
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|inputToken
operator|.
name|setEndOffset
argument_list|(
name|lastPrefixToken
operator|.
name|endOffset
argument_list|()
operator|+
name|inputToken
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|inputToken
return|;
block|}
DECL|method|updateSuffixToken
specifier|public
name|Token
name|updateSuffixToken
parameter_list|(
name|Token
name|suffixToken
parameter_list|,
name|Token
name|lastInputToken
parameter_list|)
block|{
name|suffixToken
operator|.
name|setStartOffset
argument_list|(
name|lastInputToken
operator|.
name|endOffset
argument_list|()
operator|+
name|suffixToken
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|suffixToken
operator|.
name|setEndOffset
argument_list|(
name|lastInputToken
operator|.
name|endOffset
argument_list|()
operator|+
name|suffixToken
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|suffixToken
return|;
block|}
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
return|return
name|suffix
operator|.
name|incrementToken
argument_list|()
return|;
block|}
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
name|suffix
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|suffix
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

