begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|BytesRef
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
name|BytesRefIterator
import|;
end_import

begin_comment
comment|/**  * A TokenStream used internally by {@link org.apache.lucene.spatial.prefix.PrefixTreeStrategy}.  *  * This is modelled after {@link org.apache.lucene.analysis.NumericTokenStream}.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|BytesRefIteratorTokenStream
class|class
name|BytesRefIteratorTokenStream
extends|extends
name|TokenStream
block|{
comment|// just a wrapper to prevent adding CharTermAttribute
DECL|class|BRAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|BRAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|BRAttributeFactory
name|BRAttributeFactory
parameter_list|(
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|getClass
argument_list|()
operator|+
literal|" does not support CharTermAttribute."
argument_list|)
throw|;
return|return
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
DECL|class|BRTermToBytesRefAttributeImpl
specifier|private
specifier|static
specifier|final
class|class
name|BRTermToBytesRefAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|TermToBytesRefAttribute
block|{
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|setBytesRef
name|void
name|setBytesRef
parameter_list|(
name|BytesRef
name|inputBytes
parameter_list|)
block|{
comment|// shallow clone.  this.bytesRef is final
name|bytes
operator|.
name|bytes
operator|=
name|inputBytes
operator|.
name|bytes
expr_stmt|;
name|bytes
operator|.
name|offset
operator|=
name|inputBytes
operator|.
name|offset
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|inputBytes
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// we keep it untouched as it's fully controlled by the outer class.
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
specifier|final
name|BRTermToBytesRefAttributeImpl
name|a
init|=
operator|(
name|BRTermToBytesRefAttributeImpl
operator|)
name|target
decl_stmt|;
name|a
operator|.
name|setBytesRef
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillBytesRef
specifier|public
name|void
name|fillBytesRef
parameter_list|()
block|{
comment|//nothing to do; it's populated by incrementToken
block|}
annotation|@
name|Override
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|BRTermToBytesRefAttributeImpl
name|clone
parameter_list|()
block|{
comment|// super.clone won't work since we need a new BytesRef reference and it's nice to have it final. The superclass
comment|// has no state to copy anyway.
specifier|final
name|BRTermToBytesRefAttributeImpl
name|clone
init|=
operator|new
name|BRTermToBytesRefAttributeImpl
argument_list|()
decl_stmt|;
name|clone
operator|.
name|setBytesRef
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|clone
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
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|BRTermToBytesRefAttributeImpl
name|other
init|=
operator|(
name|BRTermToBytesRefAttributeImpl
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|bytes
operator|.
name|equals
argument_list|(
name|other
operator|.
name|bytes
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|BytesRefIteratorTokenStream
specifier|public
name|BytesRefIteratorTokenStream
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|BRAttributeFactory
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
argument_list|)
expr_stmt|;
name|addAttributeImpl
argument_list|(
operator|new
name|BRTermToBytesRefAttributeImpl
argument_list|()
argument_list|)
expr_stmt|;
comment|//because non-public constructor
name|bytesAtt
operator|=
operator|(
name|BRTermToBytesRefAttributeImpl
operator|)
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getBytesRefIterator
specifier|public
name|BytesRefIterator
name|getBytesRefIterator
parameter_list|()
block|{
return|return
name|bytesIter
return|;
block|}
DECL|method|setBytesRefIterator
specifier|public
name|BytesRefIteratorTokenStream
name|setBytesRefIterator
parameter_list|(
name|BytesRefIterator
name|iter
parameter_list|)
block|{
name|this
operator|.
name|bytesIter
operator|=
name|iter
expr_stmt|;
return|return
name|this
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
if|if
condition|(
name|bytesIter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setBytesRefIterator() before usage"
argument_list|)
throw|;
name|bytesAtt
operator|.
name|getBytesRef
argument_list|()
operator|.
name|length
operator|=
literal|0
expr_stmt|;
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
if|if
condition|(
name|bytesIter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setBytesRefIterator() before usage"
argument_list|)
throw|;
comment|// this will only clear all other attributes in this TokenStream
name|clearAttributes
argument_list|()
expr_stmt|;
comment|//TODO but there should be no "other" attributes
comment|// get next
name|BytesRef
name|bytes
init|=
name|bytesIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|bytesAtt
operator|.
name|setBytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
comment|//note: we don't bother setting posInc or type attributes.  There's no point to it.
return|return
literal|true
return|;
block|}
block|}
comment|//members
DECL|field|bytesAtt
specifier|private
specifier|final
name|BRTermToBytesRefAttributeImpl
name|bytesAtt
decl_stmt|;
DECL|field|bytesIter
specifier|private
name|BytesRefIterator
name|bytesIter
init|=
literal|null
decl_stmt|;
comment|// null means not initialized
block|}
end_class

end_unit
