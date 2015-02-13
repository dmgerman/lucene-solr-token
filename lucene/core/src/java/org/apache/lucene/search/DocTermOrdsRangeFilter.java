begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
operator|.
name|LeafReaderContext
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
name|DocValues
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
name|SortedSetDocValues
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
name|Bits
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
comment|/**  * A range filter built on top of a cached multi-valued term field (from {@link org.apache.lucene.index.LeafReader#getSortedSetDocValues}).  *   *<p>Like {@link DocValuesRangeFilter}, this is just a specialized range query versus  *    using a TermRangeQuery with {@link DocTermOrdsRewriteMethod}: it will only do  *    two ordinal to term lookups.</p>  */
end_comment

begin_class
DECL|class|DocTermOrdsRangeFilter
specifier|public
specifier|abstract
class|class
name|DocTermOrdsRangeFilter
extends|extends
name|Filter
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|lowerVal
specifier|final
name|BytesRef
name|lowerVal
decl_stmt|;
DECL|field|upperVal
specifier|final
name|BytesRef
name|upperVal
decl_stmt|;
DECL|field|includeLower
specifier|final
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|final
name|boolean
name|includeUpper
decl_stmt|;
DECL|method|DocTermOrdsRangeFilter
specifier|private
name|DocTermOrdsRangeFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|lowerVal
parameter_list|,
name|BytesRef
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|lowerVal
operator|=
name|lowerVal
expr_stmt|;
name|this
operator|.
name|upperVal
operator|=
name|upperVal
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
block|}
comment|/** This method is implemented for each data type */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
specifier|abstract
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a BytesRef range filter using {@link org.apache.lucene.index.LeafReader#getSortedSetDocValues}. This works with all    * fields containing zero or one term in the field. The range can be half-open by setting one    * of the values to<code>null</code>.    */
DECL|method|newBytesRefRange
specifier|public
specifier|static
name|DocTermOrdsRangeFilter
name|newBytesRefRange
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|lowerVal
parameter_list|,
name|BytesRef
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|DocTermOrdsRangeFilter
argument_list|(
name|field
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedSetDocValues
name|docTermOrds
init|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|long
name|lowerPoint
init|=
name|lowerVal
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|docTermOrds
operator|.
name|lookupTerm
argument_list|(
name|lowerVal
argument_list|)
decl_stmt|;
specifier|final
name|long
name|upperPoint
init|=
name|upperVal
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|docTermOrds
operator|.
name|lookupTerm
argument_list|(
name|upperVal
argument_list|)
decl_stmt|;
specifier|final
name|long
name|inclusiveLowerPoint
decl_stmt|,
name|inclusiveUpperPoint
decl_stmt|;
comment|// Hints:
comment|// * binarySearchLookup returns -1, if value was null.
comment|// * the value is<0 if no exact hit was found, the returned value
comment|//   is (-(insertion point) - 1)
if|if
condition|(
name|lowerPoint
operator|==
operator|-
literal|1
operator|&&
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|inclusiveLowerPoint
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeLower
operator|&&
name|lowerPoint
operator|>=
literal|0
condition|)
block|{
name|inclusiveLowerPoint
operator|=
name|lowerPoint
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerPoint
operator|>=
literal|0
condition|)
block|{
name|inclusiveLowerPoint
operator|=
name|lowerPoint
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|inclusiveLowerPoint
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
operator|-
name|lowerPoint
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upperPoint
operator|==
operator|-
literal|1
operator|&&
name|upperVal
operator|==
literal|null
condition|)
block|{
name|inclusiveUpperPoint
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeUpper
operator|&&
name|upperPoint
operator|>=
literal|0
condition|)
block|{
name|inclusiveUpperPoint
operator|=
name|upperPoint
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|upperPoint
operator|>=
literal|0
condition|)
block|{
name|inclusiveUpperPoint
operator|=
name|upperPoint
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|inclusiveUpperPoint
operator|=
operator|-
name|upperPoint
operator|-
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|inclusiveUpperPoint
argument_list|<
literal|0
operator|||
name|inclusiveLowerPoint
argument_list|>
name|inclusiveUpperPoint
condition|)
block|{
return|return
literal|null
return|;
block|}
assert|assert
name|inclusiveLowerPoint
operator|>=
literal|0
operator|&&
name|inclusiveUpperPoint
operator|>=
literal|0
assert|;
return|return
operator|new
name|DocValuesDocIdSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
specifier|final
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|docTermOrds
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|docTermOrds
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
if|if
condition|(
name|ord
operator|>
name|inclusiveUpperPoint
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|ord
operator|>=
name|inclusiveLowerPoint
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
name|includeLower
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|lowerVal
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|lowerVal
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|upperVal
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|upperVal
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|includeUpper
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|DocTermOrdsRangeFilter
operator|)
condition|)
return|return
literal|false
return|;
name|DocTermOrdsRangeFilter
name|other
init|=
operator|(
name|DocTermOrdsRangeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
operator|||
name|this
operator|.
name|includeLower
operator|!=
name|other
operator|.
name|includeLower
operator|||
name|this
operator|.
name|includeUpper
operator|!=
name|other
operator|.
name|includeUpper
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|lowerVal
operator|!=
literal|null
condition|?
operator|!
name|this
operator|.
name|lowerVal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|lowerVal
argument_list|)
else|:
name|other
operator|.
name|lowerVal
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|upperVal
operator|!=
literal|null
condition|?
operator|!
name|this
operator|.
name|upperVal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|upperVal
argument_list|)
else|:
name|other
operator|.
name|upperVal
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|field
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|lowerVal
operator|!=
literal|null
operator|)
condition|?
name|lowerVal
operator|.
name|hashCode
argument_list|()
else|:
literal|550356204
expr_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|31
operator|)
expr_stmt|;
comment|// rotate to distinguish lower from upper
name|h
operator|^=
operator|(
name|upperVal
operator|!=
literal|null
operator|)
condition|?
name|upperVal
operator|.
name|hashCode
argument_list|()
else|:
operator|-
literal|1674416163
expr_stmt|;
name|h
operator|^=
operator|(
name|includeLower
condition|?
literal|1549299360
else|:
operator|-
literal|365038026
operator|)
operator|^
operator|(
name|includeUpper
condition|?
literal|1721088258
else|:
literal|1948649653
operator|)
expr_stmt|;
return|return
name|h
return|;
block|}
comment|/** Returns the field name for this filter */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Returns<code>true</code> if the lower endpoint is inclusive */
DECL|method|includesLower
specifier|public
name|boolean
name|includesLower
parameter_list|()
block|{
return|return
name|includeLower
return|;
block|}
comment|/** Returns<code>true</code> if the upper endpoint is inclusive */
DECL|method|includesUpper
specifier|public
name|boolean
name|includesUpper
parameter_list|()
block|{
return|return
name|includeUpper
return|;
block|}
comment|/** Returns the lower value of this range filter */
DECL|method|getLowerVal
specifier|public
name|BytesRef
name|getLowerVal
parameter_list|()
block|{
return|return
name|lowerVal
return|;
block|}
comment|/** Returns the upper value of this range filter */
DECL|method|getUpperVal
specifier|public
name|BytesRef
name|getUpperVal
parameter_list|()
block|{
return|return
name|upperVal
return|;
block|}
block|}
end_class

end_unit

