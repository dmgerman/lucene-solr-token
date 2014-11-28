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
name|analysis
operator|.
name|tokenattributes
operator|.
name|FlagsAttribute
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
name|PackedTokenAttributeImpl
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
name|PayloadAttribute
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
name|DocsAndPositionsEnum
import|;
end_import

begin_comment
comment|// for javadoc
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
name|Attribute
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
name|AttributeImpl
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
name|AttributeReflector
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**    A Token is an occurrence of a term from the text of a field.  It consists of   a term's text, the start and end offset of the term in the text of the field,   and a type string.<p>   The start and end offsets permit applications to re-associate a token with   its source text, e.g., to display highlighted query terms in a document   browser, or to show matching text fragments in a<abbr title="KeyWord In Context">KWIC</abbr>   display, etc.<p>   The type is a string, assigned by a lexical analyzer   (a.k.a. tokenizer), naming the lexical or syntactic class that the token   belongs to.  For example an end of sentence marker token might be implemented   with type "eos".  The default token type is "word".<p>   A Token can optionally have metadata (a.k.a. payload) in the form of a variable   length byte array. Use {@link DocsAndPositionsEnum#getPayload()} to retrieve the    payloads from the index.<br><br><p><b>NOTE:</b> As of 2.9, Token implements all {@link Attribute} interfaces   that are part of core Lucene and can be found in the {@code tokenattributes} subpackage.   Even though it is not necessary to use Token anymore, with the new TokenStream API it can   be used as convenience class that implements all {@link Attribute}s, which is especially useful   to easily switch from the old to the new TokenStream API.     A few things to note:<ul><li>clear() initializes all of the fields to default values. This was changed in contrast to Lucene 2.4, but should affect no one.</li><li>Because<code>TokenStreams</code> can be chained, one cannot assume that the<code>Token's</code> current type is correct.</li><li>The startOffset and endOffset represent the start and offset in the source text, so be careful in adjusting them.</li><li>When caching a reusable token, clone it. When injecting a cached token into a stream that can be reset, clone it again.</li></ul></p><p><b>Please note:</b> With Lucene 3.1, the<code>{@linkplain #toString toString()}</code> method had to be changed to match the   {@link CharSequence} interface introduced by the interface {@link org.apache.lucene.analysis.tokenattributes.CharTermAttribute}.   This method now only prints the term text, no additional information anymore.</p>   @deprecated This class is outdated and no longer used since Lucene 2.9. Nuke it finally! */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Token
specifier|public
class|class
name|Token
extends|extends
name|PackedTokenAttributeImpl
implements|implements
name|FlagsAttribute
implements|,
name|PayloadAttribute
block|{
DECL|field|flags
specifier|private
name|int
name|flags
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
comment|/** Constructs a Token will null text. */
DECL|method|Token
specifier|public
name|Token
parameter_list|()
block|{   }
comment|/** Constructs a Token with the given term text, start    *  and end offsets.  The type defaults to "word."    *<b>NOTE:</b> for better indexing speed you should    *  instead use the char[] termBuffer methods to set the    *  term text.    *  @param text term text    *  @param start start offset in the source text    *  @param end end offset in the source text    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|CharSequence
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|setOffset
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    * @see FlagsAttribute    */
annotation|@
name|Override
DECL|method|getFlags
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
comment|/**    * {@inheritDoc}    * @see FlagsAttribute    */
annotation|@
name|Override
DECL|method|setFlags
specifier|public
name|void
name|setFlags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    * @see PayloadAttribute    */
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
return|return
name|this
operator|.
name|payload
return|;
block|}
comment|/**    * {@inheritDoc}    * @see PayloadAttribute    */
annotation|@
name|Override
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|BytesRef
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
comment|/** Resets the term text, payload, flags, positionIncrement, positionLength,    * startOffset, endOffset and token type to default.    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|flags
operator|=
literal|0
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|Token
condition|)
block|{
specifier|final
name|Token
name|other
init|=
operator|(
name|Token
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|flags
operator|==
name|other
operator|.
name|flags
operator|&&
operator|(
name|payload
operator|==
literal|null
condition|?
name|other
operator|.
name|payload
operator|==
literal|null
else|:
name|payload
operator|.
name|equals
argument_list|(
name|other
operator|.
name|payload
argument_list|)
operator|)
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|)
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|code
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|flags
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|payload
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|code
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Token
name|clone
parameter_list|()
block|{
specifier|final
name|Token
name|t
init|=
operator|(
name|Token
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|payload
operator|=
name|payload
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
comment|/**    * Copy the prototype token's fields into this one. Note: Payloads are shared.    * @param prototype source Token to copy fields from    */
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|Token
name|prototype
parameter_list|)
block|{
comment|// this is a bad hack to emulate no cloning of payload!
name|prototype
operator|.
name|copyToWithoutPayloadClone
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|copyToWithoutPayloadClone
specifier|private
name|void
name|copyToWithoutPayloadClone
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|super
operator|.
name|copyTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
operator|(
operator|(
name|FlagsAttribute
operator|)
name|target
operator|)
operator|.
name|setFlags
argument_list|(
name|flags
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PayloadAttribute
operator|)
name|target
operator|)
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|super
operator|.
name|copyTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
operator|(
operator|(
name|FlagsAttribute
operator|)
name|target
operator|)
operator|.
name|setFlags
argument_list|(
name|flags
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PayloadAttribute
operator|)
name|target
operator|)
operator|.
name|setPayload
argument_list|(
operator|(
name|payload
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|payload
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|super
operator|.
name|reflectWith
argument_list|(
name|reflector
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|,
literal|"flags"
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|,
literal|"payload"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
comment|/** Convenience factory that returns<code>Token</code> as implementation for the basic    * attributes and return the default impl (with&quot;Impl&quot; appended) for all other    * attributes.    * @since 3.0    */
DECL|field|TOKEN_ATTRIBUTE_FACTORY
specifier|public
specifier|static
specifier|final
name|AttributeFactory
name|TOKEN_ATTRIBUTE_FACTORY
init|=
name|AttributeFactory
operator|.
name|getStaticImplementation
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

