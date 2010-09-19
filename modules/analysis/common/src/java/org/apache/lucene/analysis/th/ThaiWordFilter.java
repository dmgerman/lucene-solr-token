begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.th
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|th
package|;
end_package

begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Character
operator|.
name|UnicodeBlock
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|text
operator|.
name|Segment
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
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
name|core
operator|.
name|LowerCaseFilter
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
name|PositionIncrementAttribute
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
name|util
operator|.
name|AttributeSource
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
comment|/**  * {@link TokenFilter} that use {@link java.text.BreakIterator} to break each   * Token that is Thai into separate Token(s) for each Thai word.  *<p>Please note: Since matchVersion 3.1 on, this filter no longer lowercases non-thai text.  * {@link ThaiAnalyzer} will insert a {@link LowerCaseFilter} before this filter  * so the behaviour of the Analyzer does not change. With version 3.1, the filter handles  * position increments correctly.  *<p>WARNING: this filter may not be supported by all JREs.  *    It is known to work with Sun/Oracle and Harmony JREs.  *    If your application needs to be fully portable, consider using ICUTokenizer instead,  *    which uses an ICU Thai BreakIterator that will always be available.  */
end_comment

begin_class
DECL|class|ThaiWordFilter
specifier|public
specifier|final
class|class
name|ThaiWordFilter
extends|extends
name|TokenFilter
block|{
comment|/**     * True if the JRE supports a working dictionary-based breakiterator for Thai.    * If this is false, this filter will not work at all!    */
DECL|field|DBBI_AVAILABLE
specifier|public
specifier|static
specifier|final
name|boolean
name|DBBI_AVAILABLE
decl_stmt|;
DECL|field|proto
specifier|private
specifier|static
specifier|final
name|BreakIterator
name|proto
init|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"th"
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
comment|// check that we have a working dictionary-based break iterator for thai
name|proto
operator|.
name|setText
argument_list|(
literal|"à¸ à¸²à¸©à¸²à¹à¸à¸¢"
argument_list|)
expr_stmt|;
name|DBBI_AVAILABLE
operator|=
name|proto
operator|.
name|isBoundary
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|field|breaker
specifier|private
specifier|final
name|BreakIterator
name|breaker
init|=
operator|(
name|BreakIterator
operator|)
name|proto
operator|.
name|clone
argument_list|()
decl_stmt|;
DECL|field|charIterator
specifier|private
specifier|final
name|Segment
name|charIterator
init|=
operator|new
name|Segment
argument_list|()
decl_stmt|;
DECL|field|handlePosIncr
specifier|private
specifier|final
name|boolean
name|handlePosIncr
decl_stmt|;
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
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clonedToken
specifier|private
name|AttributeSource
name|clonedToken
init|=
literal|null
decl_stmt|;
DECL|field|clonedTermAtt
specifier|private
name|CharTermAttribute
name|clonedTermAtt
init|=
literal|null
decl_stmt|;
DECL|field|clonedOffsetAtt
specifier|private
name|OffsetAttribute
name|clonedOffsetAtt
init|=
literal|null
decl_stmt|;
DECL|field|hasMoreTokensInClone
specifier|private
name|boolean
name|hasMoreTokensInClone
init|=
literal|false
decl_stmt|;
comment|/** Creates a new ThaiWordFilter that also lowercases non-thai text.    * @deprecated Use the ctor with {@code matchVersion} instead!    */
annotation|@
name|Deprecated
DECL|method|ThaiWordFilter
specifier|public
name|ThaiWordFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new ThaiWordFilter with the specified match version. */
DECL|method|ThaiWordFilter
specifier|public
name|ThaiWordFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|?
name|input
else|:
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DBBI_AVAILABLE
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This JRE does not have support for Thai segmentation"
argument_list|)
throw|;
name|handlePosIncr
operator|=
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
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
name|hasMoreTokensInClone
condition|)
block|{
name|int
name|start
init|=
name|breaker
operator|.
name|current
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|clonedToken
operator|.
name|copyTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|clonedTermAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|clonedOffsetAtt
operator|.
name|startOffset
argument_list|()
operator|+
name|start
argument_list|,
name|clonedOffsetAtt
operator|.
name|startOffset
argument_list|()
operator|+
name|end
argument_list|)
expr_stmt|;
if|if
condition|(
name|handlePosIncr
condition|)
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|hasMoreTokensInClone
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|termAtt
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|termAtt
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|!=
name|UnicodeBlock
operator|.
name|THAI
condition|)
block|{
return|return
literal|true
return|;
block|}
name|hasMoreTokensInClone
operator|=
literal|true
expr_stmt|;
comment|// we lazy init the cloned token, as in ctor not all attributes may be added
if|if
condition|(
name|clonedToken
operator|==
literal|null
condition|)
block|{
name|clonedToken
operator|=
name|cloneAttributes
argument_list|()
expr_stmt|;
name|clonedTermAtt
operator|=
name|clonedToken
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|clonedOffsetAtt
operator|=
name|clonedToken
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|copyTo
argument_list|(
name|clonedToken
argument_list|)
expr_stmt|;
block|}
comment|// reinit CharacterIterator
name|charIterator
operator|.
name|array
operator|=
name|clonedTermAtt
operator|.
name|buffer
argument_list|()
expr_stmt|;
name|charIterator
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|charIterator
operator|.
name|count
operator|=
name|clonedTermAtt
operator|.
name|length
argument_list|()
expr_stmt|;
name|breaker
operator|.
name|setText
argument_list|(
name|charIterator
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|termAtt
operator|.
name|setLength
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|clonedOffsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|clonedOffsetAtt
operator|.
name|startOffset
argument_list|()
operator|+
name|end
argument_list|)
expr_stmt|;
comment|// position increment keeps as it is for first token
return|return
literal|true
return|;
block|}
return|return
literal|false
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
name|hasMoreTokensInClone
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

