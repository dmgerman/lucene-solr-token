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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Payload
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
name|index
operator|.
name|TermPositions
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** A Token is an occurence of a term from the text of a field.  It consists of   a term's text, the start and end offset of the term in the text of the field,   and a type string.<p>   The start and end offsets permit applications to re-associate a token with   its source text, e.g., to display highlighted query terms in a document   browser, or to show matching text fragments in a KWIC (KeyWord In Context)   display, etc.<p>   The type is an interned string, assigned by a lexical analyzer   (a.k.a. tokenizer), naming the lexical or syntactic class that the token   belongs to.  For example an end of sentence marker token might be implemented   with type "eos".  The default token type is "word".<p>   A Token can optionally have metadata (a.k.a. Payload) in the form of a variable   length byte array. Use {@link TermPositions#getPayloadLength()} and    {@link TermPositions#getPayload(byte[], int)} to retrieve the payloads from the index.<br><br><p><font color="#FF0000">   WARNING: The status of the<b>Payloads</b> feature is experimental.    The APIs introduced here might change in the future and will not be    supported anymore in such a case.</font>    @see org.apache.lucene.index.Payload   */
end_comment

begin_comment
comment|// TODO: Remove warning after API has been finalized
end_comment

begin_class
DECL|class|Token
specifier|public
class|class
name|Token
implements|implements
name|Cloneable
block|{
DECL|field|termText
name|String
name|termText
decl_stmt|;
comment|// the text of the term
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
comment|// start in source text
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
comment|// end in source text
DECL|field|type
name|String
name|type
init|=
literal|"word"
decl_stmt|;
comment|// lexical type
DECL|field|payload
name|Payload
name|payload
decl_stmt|;
DECL|field|positionIncrement
specifier|private
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
comment|/** Constructs a Token with the given term text, and start& end offsets.       The type defaults to "word." */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|termText
operator|=
name|text
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
block|}
comment|/** Constructs a Token with the given text, start and end offsets,& type. */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|String
name|typ
parameter_list|)
block|{
name|termText
operator|=
name|text
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
name|type
operator|=
name|typ
expr_stmt|;
block|}
comment|/** Set the position increment.  This determines the position of this token    * relative to the previous Token in a {@link TokenStream}, used in phrase    * searching.    *    *<p>The default value is one.    *    *<p>Some common uses for this are:<ul>    *    *<li>Set it to zero to put multiple terms in the same position.  This is    * useful if, e.g., a word has multiple stems.  Searches for phrases    * including either stem will match.  In this case, all but the first stem's    * increment should be set to zero: the increment of the first instance    * should be one.  Repeating a token with an increment of zero can also be    * used to boost the scores of matches on that token.    *    *<li>Set it to values greater than one to inhibit exact phrase matches.    * If, for example, one does not want phrases to match across removed stop    * words, then one could build a stop word filter that removes stop words and    * also sets the increment to the number of stop words removed before each    * non-stop word.  Then exact phrase queries will only match when the terms    * occur with no intervening stop words.    *    *</ul>    * @see org.apache.lucene.index.TermPositions    */
DECL|method|setPositionIncrement
specifier|public
name|void
name|setPositionIncrement
parameter_list|(
name|int
name|positionIncrement
parameter_list|)
block|{
if|if
condition|(
name|positionIncrement
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Increment must be zero or greater: "
operator|+
name|positionIncrement
argument_list|)
throw|;
name|this
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
block|}
comment|/** Returns the position increment of this Token.    * @see #setPositionIncrement    */
DECL|method|getPositionIncrement
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
name|positionIncrement
return|;
block|}
comment|/** Sets the Token's term text. */
DECL|method|setTermText
specifier|public
name|void
name|setTermText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|termText
operator|=
name|text
expr_stmt|;
block|}
comment|/** Returns the Token's term text. */
DECL|method|termText
specifier|public
specifier|final
name|String
name|termText
parameter_list|()
block|{
return|return
name|termText
return|;
block|}
comment|/** Returns this Token's starting offset, the position of the first character     corresponding to this token in the source text.      Note that the difference between endOffset() and startOffset() may not be     equal to termText.length(), as the term text may have been altered by a     stemmer or some other filter. */
DECL|method|startOffset
specifier|public
specifier|final
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/** Returns this Token's ending offset, one greater than the position of the     last character corresponding to this token in the source text. */
DECL|method|endOffset
specifier|public
specifier|final
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/** Returns this Token's lexical type.  Defaults to "word". */
DECL|method|type
specifier|public
specifier|final
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**     * Sets this Token's payload.    *<p><font color="#FF0000">    * WARNING: The status of the<b>Payloads</b> feature is experimental.     * The APIs introduced here might change in the future and will not be     * supported anymore in such a case.</font>    */
comment|// TODO: Remove warning after API has been finalized
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|Payload
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
comment|/**     * Returns this Token's payload.     *<p><font color="#FF0000">    * WARNING: The status of the<b>Payloads</b> feature is experimental.     * The APIs introduced here might change in the future and will not be     * supported anymore in such a case.</font>    */
comment|// TODO: Remove warning after API has been finalized
DECL|method|getPayload
specifier|public
name|Payload
name|getPayload
parameter_list|()
block|{
return|return
name|this
operator|.
name|payload
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
operator|+
name|termText
operator|+
literal|","
operator|+
name|startOffset
operator|+
literal|","
operator|+
name|endOffset
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
literal|"word"
argument_list|)
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",type="
operator|+
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|positionIncrement
operator|!=
literal|1
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",posIncr="
operator|+
name|positionIncrement
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// shouldn't happen since we implement Cloneable
block|}
block|}
block|}
end_class

end_unit

