begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.attributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|attributes
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|streaming
operator|.
name|CategoryAttributesStream
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This class transforms an {@link Iterable} of {@link CategoryPath} objects  * into an {@link Iterable} of {@link CategoryAttribute} objects, which can be  * used to construct a {@link CategoryAttributesStream}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryAttributesIterable
specifier|public
class|class
name|CategoryAttributesIterable
implements|implements
name|Iterable
argument_list|<
name|CategoryAttribute
argument_list|>
block|{
DECL|field|inputIterable
specifier|private
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|inputIterable
decl_stmt|;
DECL|method|CategoryAttributesIterable
specifier|public
name|CategoryAttributesIterable
parameter_list|(
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|inputIterable
parameter_list|)
block|{
name|this
operator|.
name|inputIterable
operator|=
name|inputIterable
expr_stmt|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|CategoryAttributesIterator
argument_list|(
name|this
operator|.
name|inputIterable
argument_list|)
return|;
block|}
DECL|class|CategoryAttributesIterator
specifier|private
specifier|static
class|class
name|CategoryAttributesIterator
implements|implements
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
block|{
DECL|field|internalIterator
specifier|private
name|Iterator
argument_list|<
name|CategoryPath
argument_list|>
name|internalIterator
decl_stmt|;
DECL|field|categoryAttributeImpl
specifier|private
name|CategoryAttributeImpl
name|categoryAttributeImpl
decl_stmt|;
DECL|method|CategoryAttributesIterator
specifier|public
name|CategoryAttributesIterator
parameter_list|(
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|inputIterable
parameter_list|)
block|{
name|this
operator|.
name|internalIterator
operator|=
name|inputIterable
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|categoryAttributeImpl
operator|=
operator|new
name|CategoryAttributeImpl
argument_list|()
expr_stmt|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|this
operator|.
name|internalIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|CategoryAttribute
name|next
parameter_list|()
block|{
name|this
operator|.
name|categoryAttributeImpl
operator|.
name|setCategoryPath
argument_list|(
name|this
operator|.
name|internalIterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|categoryAttributeImpl
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|this
operator|.
name|internalIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

