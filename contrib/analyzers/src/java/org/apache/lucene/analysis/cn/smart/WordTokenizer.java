begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Tokenizer
import|;
end_import

begin_comment
comment|/**  * A {@link Tokenizer} that breaks sentences into words.  */
end_comment

begin_class
DECL|class|WordTokenizer
specifier|public
class|class
name|WordTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|wordSegmenter
specifier|private
name|WordSegmenter
name|wordSegmenter
decl_stmt|;
DECL|field|in
specifier|private
name|TokenStream
name|in
decl_stmt|;
DECL|field|tokenIter
specifier|private
name|Iterator
name|tokenIter
decl_stmt|;
DECL|field|tokenBuffer
specifier|private
name|List
name|tokenBuffer
decl_stmt|;
DECL|field|sentenceToken
specifier|private
name|Token
name|sentenceToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
comment|/**    * Construct a new WordTokenizer.    *     * @param in {@link TokenStream} of sentences    * @param wordSegmenter {@link WordSegmenter} to break sentences into words     */
DECL|method|WordTokenizer
specifier|public
name|WordTokenizer
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|WordSegmenter
name|wordSegmenter
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|wordSegmenter
operator|=
name|wordSegmenter
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tokenIter
operator|!=
literal|null
operator|&&
name|tokenIter
operator|.
name|hasNext
argument_list|()
condition|)
return|return
operator|(
name|Token
operator|)
name|tokenIter
operator|.
name|next
argument_list|()
return|;
else|else
block|{
if|if
condition|(
name|processNextSentence
argument_list|()
condition|)
block|{
return|return
operator|(
name|Token
operator|)
name|tokenIter
operator|.
name|next
argument_list|()
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Process the next input sentence, placing tokens into tokenBuffer    *     * @return true if more tokens were placed into tokenBuffer.    * @throws IOException    */
DECL|method|processNextSentence
specifier|private
name|boolean
name|processNextSentence
parameter_list|()
throws|throws
name|IOException
block|{
name|sentenceToken
operator|=
name|in
operator|.
name|next
argument_list|(
name|sentenceToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|sentenceToken
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|tokenBuffer
operator|=
name|wordSegmenter
operator|.
name|segmentSentence
argument_list|(
name|sentenceToken
argument_list|)
expr_stmt|;
name|tokenIter
operator|=
name|tokenBuffer
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
name|tokenBuffer
operator|!=
literal|null
operator|&&
name|tokenIter
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

