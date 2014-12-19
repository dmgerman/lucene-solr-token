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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|AttributeFactory
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
name|java
operator|.
name|io
operator|.
name|Reader
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
comment|/** A Tokenizer is a TokenStream whose input is a Reader.<p>   This is an abstract class; subclasses must override {@link #incrementToken()}<p>   NOTE: Subclasses overriding {@link #incrementToken()} must   call {@link AttributeSource#clearAttributes()} before   setting attributes.  */
end_comment

begin_class
DECL|class|Tokenizer
specifier|public
specifier|abstract
class|class
name|Tokenizer
extends|extends
name|TokenStream
block|{
comment|/** The text source for this Tokenizer. */
DECL|field|input
specifier|protected
name|Reader
name|input
init|=
name|ILLEGAL_STATE_READER
decl_stmt|;
comment|/** Pending reader: not actually assigned to input until reset() */
DECL|field|inputPending
specifier|private
name|Reader
name|inputPending
init|=
name|ILLEGAL_STATE_READER
decl_stmt|;
comment|/**    * Construct a tokenizer with no input, awaiting a call to {@link #setReader(java.io.Reader)}    * to provide input.    */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|()
block|{
comment|//
block|}
comment|/**    * Construct a tokenizer with no input, awaiting a call to {@link #setReader(java.io.Reader)} to    * provide input.    * @param factory attribute factory.    */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    *<b>NOTE:</b>     * The default implementation closes the input Reader, so    * be sure to call<code>super.close()</code> when overriding this method.    */
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
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// LUCENE-2387: don't hold onto Reader after close, so
comment|// GC can reclaim
name|inputPending
operator|=
name|input
operator|=
name|ILLEGAL_STATE_READER
expr_stmt|;
block|}
comment|/** Return the corrected offset. If {@link #input} is a {@link CharFilter} subclass    * this method calls {@link CharFilter#correctOffset}, else returns<code>currentOff</code>.    * @param currentOff offset as seen in the output    * @return corrected offset based on the input    * @see CharFilter#correctOffset    */
DECL|method|correctOffset
specifier|protected
specifier|final
name|int
name|correctOffset
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
operator|(
name|input
operator|instanceof
name|CharFilter
operator|)
condition|?
operator|(
operator|(
name|CharFilter
operator|)
name|input
operator|)
operator|.
name|correctOffset
argument_list|(
name|currentOff
argument_list|)
else|:
name|currentOff
return|;
block|}
comment|/** Expert: Set a new reader on the Tokenizer.  Typically, an    *  analyzer (in its tokenStream method) will use    *  this to re-use a previously created tokenizer. */
DECL|method|setReader
specifier|public
specifier|final
name|void
name|setReader
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"input must not be null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|input
operator|!=
name|ILLEGAL_STATE_READER
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"TokenStream contract violation: close() call missing"
argument_list|)
throw|;
block|}
name|this
operator|.
name|inputPending
operator|=
name|input
expr_stmt|;
name|setReaderTestPoint
argument_list|()
expr_stmt|;
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
name|input
operator|=
name|inputPending
expr_stmt|;
name|inputPending
operator|=
name|ILLEGAL_STATE_READER
expr_stmt|;
block|}
comment|// only used for testing
DECL|method|setReaderTestPoint
name|void
name|setReaderTestPoint
parameter_list|()
block|{}
DECL|field|ILLEGAL_STATE_READER
specifier|private
specifier|static
specifier|final
name|Reader
name|ILLEGAL_STATE_READER
init|=
operator|new
name|Reader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"TokenStream contract violation: reset()/close() call missing, "
operator|+
literal|"reset() called multiple times, or subclass does not call super.reset(). "
operator|+
literal|"Please see Javadocs of TokenStream class for more information about the correct consuming workflow."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{}
block|}
decl_stmt|;
block|}
end_class

end_unit

