begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
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
name|AtomicReaderContext
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
name|RandomAccessOrds
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
name|SingletonSortedSetDocValues
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
name|SortedDocValues
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
name|search
operator|.
name|FieldCache
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
name|search
operator|.
name|FieldComparator
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
name|search
operator|.
name|SortField
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
comment|/**   * SortField for {@link SortedSetDocValues}.  *<p>  * A SortedSetDocValues contains multiple values for a field, so sorting with  * this technique "selects" a value as the representative sort value for the document.  *<p>  * By default, the minimum value in the set is selected as the sort value, but  * this can be customized. Selectors other than the default do have some limitations  * (see below) to ensure that all selections happen in constant-time for performance.  *<p>  * Like sorting by string, this also supports sorting missing values as first or last,  * via {@link #setMissingValue(Object)}.  *<p>  * Limitations:  *<ul>  *<li>Fields containing {@link Integer#MAX_VALUE} or more unique values  *       are unsupported.  *<li>Selectors other than the default ({@link Selector#MIN}) require   *       optional codec support. However several codecs provided by Lucene,   *       including the current default codec, support this.  *</ul>  */
end_comment

begin_class
DECL|class|SortedSetSortField
specifier|public
class|class
name|SortedSetSortField
extends|extends
name|SortField
block|{
comment|/** Selects a value from the document's set to use as the sort value */
DECL|enum|Selector
specifier|public
specifier|static
enum|enum
name|Selector
block|{
comment|/**       * Selects the minimum value in the set       */
DECL|enum constant|MIN
name|MIN
block|,
comment|/**       * Selects the maximum value in the set       */
DECL|enum constant|MAX
name|MAX
block|,
comment|/**       * Selects the middle value in the set.      *<p>      * If the set has an even number of values, the lower of the middle two is chosen.      */
DECL|enum constant|MIDDLE_MIN
name|MIDDLE_MIN
block|,
comment|/**       * Selects the middle value in the set.      *<p>      * If the set has an even number of values, the higher of the middle two is chosen      */
DECL|enum constant|MIDDLE_MAX
name|MIDDLE_MAX
block|}
DECL|field|selector
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
comment|/**    * Creates a sort, possibly in reverse, by the minimum value in the set     * for the document.    * @param field Name of field to sort by.  Must not be null.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortedSetSortField
specifier|public
name|SortedSetSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|,
name|Selector
operator|.
name|MIN
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a sort, possibly in reverse, specifying how the sort value from     * the document's set is selected.    * @param field Name of field to sort by.  Must not be null.    * @param reverse True if natural order should be reversed.    * @param selector custom selector for choosing the sort value from the set.    *<p>    * NOTE: selectors other than {@link Selector#MIN} require optional codec support.    */
DECL|method|SortedSetSortField
specifier|public
name|SortedSetSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|Selector
name|selector
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|CUSTOM
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
comment|/** Returns the selector in use for this sort */
DECL|method|getSelector
specifier|public
name|Selector
name|getSelector
parameter_list|()
block|{
return|return
name|selector
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
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|selector
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
name|SortedSetSortField
name|other
init|=
operator|(
name|SortedSetSortField
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
name|other
operator|.
name|selector
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"<sortedset"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getReverse
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
if|if
condition|(
name|missingValue
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" missingValue="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|" selector="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|selector
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Set how missing values (the empty set) are sorted.    *<p>    * Note that this must be {@link #STRING_FIRST} or {@link #STRING_LAST}.    */
annotation|@
name|Override
DECL|method|setMissingValue
specifier|public
name|void
name|setMissingValue
parameter_list|(
name|Object
name|missingValue
parameter_list|)
block|{
if|if
condition|(
name|missingValue
operator|!=
name|STRING_FIRST
operator|&&
name|missingValue
operator|!=
name|STRING_LAST
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"For SORTED_SET type, missing value must be either STRING_FIRST or STRING_LAST"
argument_list|)
throw|;
block|}
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|TermOrdValComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
name|missingValue
operator|==
name|STRING_LAST
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetDocValues
name|sortedSet
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortedSet
operator|.
name|getValueCount
argument_list|()
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"fields containing more than "
operator|+
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
operator|)
operator|+
literal|" unique terms are unsupported"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sortedSet
operator|instanceof
name|SingletonSortedSetDocValues
condition|)
block|{
comment|// it's actually single-valued in practice, but indexed as multi-valued,
comment|// so just sort on the underlying single-valued dv directly.
comment|// regardless of selector type, this optimization is safe!
return|return
operator|(
operator|(
name|SingletonSortedSetDocValues
operator|)
name|sortedSet
operator|)
operator|.
name|getSortedDocValues
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|selector
operator|==
name|Selector
operator|.
name|MIN
condition|)
block|{
return|return
operator|new
name|MinValue
argument_list|(
name|sortedSet
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|sortedSet
operator|instanceof
name|RandomAccessOrds
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"codec does not support random access ordinals, cannot use selector: "
operator|+
name|selector
argument_list|)
throw|;
block|}
name|RandomAccessOrds
name|randomOrds
init|=
operator|(
name|RandomAccessOrds
operator|)
name|sortedSet
decl_stmt|;
switch|switch
condition|(
name|selector
condition|)
block|{
case|case
name|MAX
case|:
return|return
operator|new
name|MaxValue
argument_list|(
name|randomOrds
argument_list|)
return|;
case|case
name|MIDDLE_MIN
case|:
return|return
operator|new
name|MiddleMinValue
argument_list|(
name|randomOrds
argument_list|)
return|;
case|case
name|MIDDLE_MAX
case|:
return|return
operator|new
name|MiddleMaxValue
argument_list|(
name|randomOrds
argument_list|)
return|;
case|case
name|MIN
case|:
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
block|}
return|;
block|}
comment|/** Wraps a SortedSetDocValues and returns the first ordinal (min) */
DECL|class|MinValue
specifier|static
class|class
name|MinValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|SortedSetDocValues
name|in
decl_stmt|;
DECL|method|MinValue
name|MinValue
parameter_list|(
name|SortedSetDocValues
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|nextOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the last ordinal (max) */
DECL|class|MaxValue
specifier|static
class|class
name|MaxValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|RandomAccessOrds
name|in
decl_stmt|;
DECL|method|MaxValue
name|MaxValue
parameter_list|(
name|RandomAccessOrds
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|in
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|ordAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the middle ordinal (or min of the two) */
DECL|class|MiddleMinValue
specifier|static
class|class
name|MiddleMinValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|RandomAccessOrds
name|in
decl_stmt|;
DECL|method|MiddleMinValue
name|MiddleMinValue
parameter_list|(
name|RandomAccessOrds
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|in
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|ordAt
argument_list|(
operator|(
name|count
operator|-
literal|1
operator|)
operator|>>>
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the middle ordinal (or max of the two) */
DECL|class|MiddleMaxValue
specifier|static
class|class
name|MiddleMaxValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|RandomAccessOrds
name|in
decl_stmt|;
DECL|method|MiddleMaxValue
name|MiddleMaxValue
parameter_list|(
name|RandomAccessOrds
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|in
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|ordAt
argument_list|(
name|count
operator|>>>
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

