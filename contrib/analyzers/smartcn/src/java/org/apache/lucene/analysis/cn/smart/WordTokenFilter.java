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
name|cn
operator|.
name|smart
operator|.
name|hhmm
operator|.
name|SegToken
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
name|OffsetAttribute
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
name|TermAttribute
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
name|TypeAttribute
import|;
end_import

begin_comment
comment|/**  * A {@link TokenFilter} that breaks sentences into words.  *<p><font color="#FF0000">  * WARNING: The status of the analyzers/smartcn<b>analysis.cn.smart</b> package is experimental.   * The APIs and file formats introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *</p>  */
end_comment

begin_class
DECL|class|WordTokenFilter
specifier|public
specifier|final
class|class
name|WordTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|wordSegmenter
specifier|private
name|WordSegmenter
name|wordSegmenter
decl_stmt|;
DECL|field|tokenIter
specifier|private
name|Iterator
argument_list|<
name|SegToken
argument_list|>
name|tokenIter
decl_stmt|;
DECL|field|tokenBuffer
specifier|private
name|List
argument_list|<
name|SegToken
argument_list|>
name|tokenBuffer
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
comment|/**    * Construct a new WordTokenizer.    *     * @param in {@link TokenStream} of sentences     */
DECL|method|WordTokenFilter
specifier|public
name|WordTokenFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|wordSegmenter
operator|=
operator|new
name|WordSegmenter
argument_list|()
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
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
name|tokenIter
operator|==
literal|null
operator|||
operator|!
name|tokenIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// there are no remaining tokens from the current sentence... are there more sentences?
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|// a new sentence is available: process it.
name|tokenBuffer
operator|=
name|wordSegmenter
operator|.
name|segmentSentence
argument_list|(
name|termAtt
operator|.
name|term
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|tokenIter
operator|=
name|tokenBuffer
operator|.
name|iterator
argument_list|()
expr_stmt|;
comment|/*           * it should not be possible to have a sentence with 0 words, check just in case.          * returning EOS isn't the best either, but its the behavior of the original code.          */
if|if
condition|(
operator|!
name|tokenIter
operator|.
name|hasNext
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
comment|// no more sentences, end of stream!
block|}
block|}
comment|// WordTokenFilter must clear attributes, as it is creating new tokens.
name|clearAttributes
argument_list|()
expr_stmt|;
comment|// There are remaining tokens from the current sentence, return the next one.
name|SegToken
name|nextWord
init|=
name|tokenIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|nextWord
operator|.
name|charArray
argument_list|,
literal|0
argument_list|,
name|nextWord
operator|.
name|charArray
operator|.
name|length
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|nextWord
operator|.
name|startOffset
argument_list|,
name|nextWord
operator|.
name|endOffset
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"word"
argument_list|)
expr_stmt|;
return|return
literal|true
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
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tokenIter
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

