begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_comment
comment|// javadoc
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|CharacterIterator
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

begin_comment
comment|/**   * A CharacterIterator used internally for use with {@link BreakIterator}  * @lucene.internal  */
end_comment

begin_class
DECL|class|CharArrayIterator
specifier|public
specifier|abstract
class|class
name|CharArrayIterator
implements|implements
name|CharacterIterator
block|{
DECL|field|array
specifier|private
name|char
name|array
index|[]
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|method|getText
specifier|public
name|char
index|[]
name|getText
parameter_list|()
block|{
return|return
name|array
return|;
block|}
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**    * Set a new region of text to be examined by this iterator    *     * @param array text buffer to examine    * @param start offset into buffer    * @param length maximum length to examine    */
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
specifier|final
name|char
name|array
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|start
operator|+
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|current
specifier|public
name|char
name|current
parameter_list|()
block|{
return|return
operator|(
name|index
operator|==
name|limit
operator|)
condition|?
name|DONE
else|:
name|jreBugWorkaround
argument_list|(
name|array
index|[
name|index
index|]
argument_list|)
return|;
block|}
DECL|method|jreBugWorkaround
specifier|protected
specifier|abstract
name|char
name|jreBugWorkaround
parameter_list|(
name|char
name|ch
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|first
specifier|public
name|char
name|first
parameter_list|()
block|{
name|index
operator|=
name|start
expr_stmt|;
return|return
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBeginIndex
specifier|public
name|int
name|getBeginIndex
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getEndIndex
specifier|public
name|int
name|getEndIndex
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|getIndex
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
operator|-
name|start
return|;
block|}
annotation|@
name|Override
DECL|method|last
specifier|public
name|char
name|last
parameter_list|()
block|{
name|index
operator|=
operator|(
name|limit
operator|==
name|start
operator|)
condition|?
name|limit
else|:
name|limit
operator|-
literal|1
expr_stmt|;
return|return
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|char
name|next
parameter_list|()
block|{
if|if
condition|(
operator|++
name|index
operator|>=
name|limit
condition|)
block|{
name|index
operator|=
name|limit
expr_stmt|;
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|current
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|previous
specifier|public
name|char
name|previous
parameter_list|()
block|{
if|if
condition|(
operator|--
name|index
operator|<
name|start
condition|)
block|{
name|index
operator|=
name|start
expr_stmt|;
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|current
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setIndex
specifier|public
name|char
name|setIndex
parameter_list|(
name|int
name|position
parameter_list|)
block|{
if|if
condition|(
name|position
argument_list|<
name|getBeginIndex
operator|(
operator|)
operator|||
name|position
argument_list|>
name|getEndIndex
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal Position: "
operator|+
name|position
argument_list|)
throw|;
name|index
operator|=
name|start
operator|+
name|position
expr_stmt|;
return|return
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|CharArrayIterator
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|CharArrayIterator
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
comment|// CharacterIterator does not allow you to throw CloneNotSupported
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a new CharArrayIterator that works around JRE bugs    * in a manner suitable for {@link BreakIterator#getSentenceInstance()}    */
DECL|method|newSentenceInstance
specifier|public
specifier|static
name|CharArrayIterator
name|newSentenceInstance
parameter_list|()
block|{
if|if
condition|(
name|HAS_BUGGY_BREAKITERATORS
condition|)
block|{
return|return
operator|new
name|CharArrayIterator
argument_list|()
block|{
comment|// work around this for now by lying about all surrogates to
comment|// the sentence tokenizer, instead we treat them all as
comment|// SContinue so we won't break around them.
annotation|@
name|Override
specifier|protected
name|char
name|jreBugWorkaround
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
name|ch
operator|>=
literal|0xD800
operator|&&
name|ch
operator|<=
literal|0xDFFF
condition|?
literal|0x002C
else|:
name|ch
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|CharArrayIterator
argument_list|()
block|{
comment|// no bugs
annotation|@
name|Override
specifier|protected
name|char
name|jreBugWorkaround
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
name|ch
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * Create a new CharArrayIterator that works around JRE bugs    * in a manner suitable for {@link BreakIterator#getWordInstance()}    */
DECL|method|newWordInstance
specifier|public
specifier|static
name|CharArrayIterator
name|newWordInstance
parameter_list|()
block|{
if|if
condition|(
name|HAS_BUGGY_BREAKITERATORS
condition|)
block|{
return|return
operator|new
name|CharArrayIterator
argument_list|()
block|{
comment|// work around this for now by lying about all surrogates to the word,
comment|// instead we treat them all as ALetter so we won't break around them.
annotation|@
name|Override
specifier|protected
name|char
name|jreBugWorkaround
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
name|ch
operator|>=
literal|0xD800
operator|&&
name|ch
operator|<=
literal|0xDFFF
condition|?
literal|0x0041
else|:
name|ch
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|CharArrayIterator
argument_list|()
block|{
comment|// no bugs
annotation|@
name|Override
specifier|protected
name|char
name|jreBugWorkaround
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
name|ch
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * True if this JRE has a buggy BreakIterator implementation    */
DECL|field|HAS_BUGGY_BREAKITERATORS
specifier|public
specifier|static
specifier|final
name|boolean
name|HAS_BUGGY_BREAKITERATORS
decl_stmt|;
static|static
block|{
name|boolean
name|v
decl_stmt|;
try|try
block|{
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|bi
operator|.
name|setText
argument_list|(
literal|"\udb40\udc53"
argument_list|)
expr_stmt|;
name|bi
operator|.
name|next
argument_list|()
expr_stmt|;
name|v
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|v
operator|=
literal|true
expr_stmt|;
block|}
name|HAS_BUGGY_BREAKITERATORS
operator|=
name|v
expr_stmt|;
block|}
block|}
end_class

end_unit

