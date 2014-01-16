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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|StringHelper
import|;
end_import

begin_comment
comment|// TODO(simonw) -- for cleaner transition, maybe we should make
end_comment

begin_comment
comment|// a new SortField that subclasses this one and always uses
end_comment

begin_comment
comment|// index values?
end_comment

begin_comment
comment|/**  * Stores information about how to sort documents by terms in an individual  * field.  Fields must be indexed in order to sort by them.  *  *<p>Created: Feb 11, 2004 1:25:29 PM  *  * @since   lucene 1.4  * @see Sort  */
end_comment

begin_class
DECL|class|SortField
specifier|public
class|class
name|SortField
block|{
comment|/**    * Specifies the type of the terms to be sorted, or special types such as CUSTOM    */
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
comment|/** Sort by document score (relevance).  Sort values are Float and higher      * values are at the front. */
DECL|enum constant|SCORE
name|SCORE
block|,
comment|/** Sort by document number (index order).  Sort values are Integer and lower      * values are at the front. */
DECL|enum constant|DOC
name|DOC
block|,
comment|/** Sort using term values as Strings.  Sort values are String and lower      * values are at the front. */
DECL|enum constant|STRING
name|STRING
block|,
comment|/** Sort using term values as encoded Integers.  Sort values are Integer and      * lower values are at the front. */
DECL|enum constant|INT
name|INT
block|,
comment|/** Sort using term values as encoded Floats.  Sort values are Float and      * lower values are at the front. */
DECL|enum constant|FLOAT
name|FLOAT
block|,
comment|/** Sort using term values as encoded Longs.  Sort values are Long and      * lower values are at the front. */
DECL|enum constant|LONG
name|LONG
block|,
comment|/** Sort using term values as encoded Doubles.  Sort values are Double and      * lower values are at the front. */
DECL|enum constant|DOUBLE
name|DOUBLE
block|,
comment|/** Sort using a custom Comparator.  Sort values are any Comparable and      * sorting is done according to natural order. */
DECL|enum constant|CUSTOM
name|CUSTOM
block|,
comment|/** Sort using term values as Strings, but comparing by      * value (using String.compareTo) for all comparisons.      * This is typically slower than {@link #STRING}, which      * uses ordinals to do the sorting. */
DECL|enum constant|STRING_VAL
name|STRING_VAL
block|,
comment|/** Sort use byte[] index values. */
DECL|enum constant|BYTES
name|BYTES
block|,
comment|/** Force rewriting of SortField using {@link SortField#rewrite(IndexSearcher)}      * before it can be used for sorting */
DECL|enum constant|REWRITEABLE
name|REWRITEABLE
block|}
comment|/** Represents sorting by document score (relevance). */
DECL|field|FIELD_SCORE
specifier|public
specifier|static
specifier|final
name|SortField
name|FIELD_SCORE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|Type
operator|.
name|SCORE
argument_list|)
decl_stmt|;
comment|/** Represents sorting by document number (index order). */
DECL|field|FIELD_DOC
specifier|public
specifier|static
specifier|final
name|SortField
name|FIELD_DOC
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|Type
operator|.
name|DOC
argument_list|)
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|type
specifier|private
name|Type
name|type
decl_stmt|;
comment|// defaults to determining type dynamically
DECL|field|reverse
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
comment|// defaults to natural order
DECL|field|parser
specifier|private
name|FieldCache
operator|.
name|Parser
name|parser
decl_stmt|;
comment|// Used for CUSTOM sort
DECL|field|comparatorSource
specifier|private
name|FieldComparatorSource
name|comparatorSource
decl_stmt|;
comment|// Used for 'sortMissingFirst/Last'
DECL|field|missingValue
specifier|public
name|Object
name|missingValue
init|=
literal|null
decl_stmt|;
comment|// Only used with type=STRING
DECL|field|sortMissingLast
specifier|public
name|boolean
name|sortMissingLast
decl_stmt|;
comment|/** Creates a sort by terms in the given field with the type of term    * values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field with the    * type of term values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field, parsed    * to numeric values using a custom {@link FieldCache.Parser}.    * @param field  Name of field to sort by.  Must not be null.    * @param parser Instance of a {@link FieldCache.Parser},    *  which must subclass one of the existing numeric    *  parsers from {@link FieldCache}. Sort type is inferred    *  by testing which numeric parser the parser subclasses.    * @throws IllegalArgumentException if the parser fails to    *  subclass an existing numeric parser, or field is null    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|Parser
name|parser
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|parser
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field, parsed    * to numeric values using a custom {@link FieldCache.Parser}.    * @param field  Name of field to sort by.  Must not be null.    * @param parser Instance of a {@link FieldCache.Parser},    *  which must subclass one of the existing numeric    *  parsers from {@link FieldCache}. Sort type is inferred    *  by testing which numeric parser the parser subclasses.    * @param reverse True if natural order should be reversed.    * @throws IllegalArgumentException if the parser fails to    *  subclass an existing numeric parser, or field is null    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|Parser
name|parser
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|IntParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|INT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|FloatParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|LongParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|DoubleParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Parser instance does not subclass existing numeric parser from FieldCache (got "
operator|+
name|parser
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/** Pass this to {@link #setMissingValue} to have missing    *  string values sort first. */
DECL|field|STRING_FIRST
specifier|public
specifier|final
specifier|static
name|Object
name|STRING_FIRST
init|=
operator|new
name|Object
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SortField.STRING_FIRST"
return|;
block|}
block|}
decl_stmt|;
comment|/** Pass this to {@link #setMissingValue} to have missing    *  string values sort last. */
DECL|field|STRING_LAST
specifier|public
specifier|final
specifier|static
name|Object
name|STRING_LAST
init|=
operator|new
name|Object
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SortField.STRING_LAST"
return|;
block|}
block|}
decl_stmt|;
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
name|type
operator|==
name|Type
operator|.
name|STRING
condition|)
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
literal|"For STRING type, missing value must be either STRING_FIRST or STRING_LAST"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|!=
name|Type
operator|.
name|INT
operator|&&
name|type
operator|!=
name|Type
operator|.
name|FLOAT
operator|&&
name|type
operator|!=
name|Type
operator|.
name|LONG
operator|&&
name|type
operator|!=
name|Type
operator|.
name|DOUBLE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing value only works for numeric or STRING types"
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
comment|/** Creates a sort with a custom comparison function.    * @param field Name of field to sort by; cannot be<code>null</code>.    * @param comparator Returns a comparator for sorting hits.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldComparatorSource
name|comparator
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparatorSource
operator|=
name|comparator
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, with a custom comparison function.    * @param field Name of field to sort by; cannot be<code>null</code>.    * @param comparator Returns a comparator for sorting hits.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldComparatorSource
name|comparator
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|Type
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|comparatorSource
operator|=
name|comparator
expr_stmt|;
block|}
comment|// Sets field& type, and ensures field is not NULL unless
comment|// type is SCORE or DOC
DECL|method|initFieldType
specifier|private
name|void
name|initFieldType
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|!=
name|Type
operator|.
name|SCORE
operator|&&
name|type
operator|!=
name|Type
operator|.
name|DOC
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field can only be null when type is SCORE or DOC"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
block|}
comment|/** Returns the name of the field.  Could return<code>null</code>    * if the sort is by SCORE or DOC.    * @return Name of field, possibly<code>null</code>.    */
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
comment|/** Returns the type of contents in the field.    * @return One of the constants SCORE, DOC, STRING, INT or FLOAT.    */
DECL|method|getType
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/** Returns the instance of a {@link FieldCache} parser that fits to the given sort type.    * May return<code>null</code> if no parser was specified. Sorting is using the default parser then.    * @return An instance of a {@link FieldCache} parser, or<code>null</code>.    */
DECL|method|getParser
specifier|public
name|FieldCache
operator|.
name|Parser
name|getParser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
comment|/** Returns whether the sort should be reversed.    * @return  True if natural order should be reversed.    */
DECL|method|getReverse
specifier|public
name|boolean
name|getReverse
parameter_list|()
block|{
return|return
name|reverse
return|;
block|}
comment|/** Returns the {@link FieldComparatorSource} used for    * custom sorting    */
DECL|method|getComparatorSource
specifier|public
name|FieldComparatorSource
name|getComparatorSource
parameter_list|()
block|{
return|return
name|comparatorSource
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
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SCORE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<score>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOC
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<doc>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<string"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING_VAL
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<string_val"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<int"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<long: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<float"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<double"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|CUSTOM
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<custom:\""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\": "
argument_list|)
operator|.
name|append
argument_list|(
name|comparatorSource
argument_list|)
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
break|break;
case|case
name|REWRITEABLE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<rewriteable: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|buffer
operator|.
name|append
argument_list|(
literal|"<???: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|reverse
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this.  If a    *  {@link FieldComparatorSource} or {@link    *  FieldCache.Parser} was provided, it must properly    *  implement equals (unless a singleton is always used). */
annotation|@
name|Override
DECL|method|equals
specifier|public
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
name|SortField
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|SortField
name|other
init|=
operator|(
name|SortField
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|StringHelper
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|,
name|this
operator|.
name|field
argument_list|)
operator|&&
name|other
operator|.
name|type
operator|==
name|this
operator|.
name|type
operator|&&
name|other
operator|.
name|reverse
operator|==
name|this
operator|.
name|reverse
operator|&&
operator|(
name|other
operator|.
name|comparatorSource
operator|==
literal|null
condition|?
name|this
operator|.
name|comparatorSource
operator|==
literal|null
else|:
name|other
operator|.
name|comparatorSource
operator|.
name|equals
argument_list|(
name|this
operator|.
name|comparatorSource
argument_list|)
operator|)
operator|)
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this.  If a    *  {@link FieldComparatorSource} or {@link    *  FieldCache.Parser} was provided, it must properly    *  implement hashCode (unless a singleton is always    *  used). */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|type
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x346565dd
operator|+
name|Boolean
operator|.
name|valueOf
argument_list|(
name|reverse
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|^
literal|0xaf5998bb
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|field
operator|.
name|hashCode
argument_list|()
operator|^
literal|0xff5685dd
expr_stmt|;
if|if
condition|(
name|comparatorSource
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|comparatorSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
DECL|field|bytesComparator
specifier|private
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
init|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
decl_stmt|;
DECL|method|setBytesComparator
specifier|public
name|void
name|setBytesComparator
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|b
parameter_list|)
block|{
name|bytesComparator
operator|=
name|b
expr_stmt|;
block|}
DECL|method|getBytesComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getBytesComparator
parameter_list|()
block|{
return|return
name|bytesComparator
return|;
block|}
comment|/** Returns the {@link FieldComparator} to use for    * sorting.    *    * @lucene.experimental    *    * @param numHits number of top hits the queue will store    * @param sortPos position of this SortField within {@link    *   Sort}.  The comparator is primary if sortPos==0,    *   secondary if sortPos==1, etc.  Some comparators can    *   optimize themselves when they are the primary sort.    * @return {@link FieldComparator} to use when sorting    */
DECL|method|getComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getComparator
parameter_list|(
specifier|final
name|int
name|numHits
parameter_list|,
specifier|final
name|int
name|sortPos
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SCORE
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|RelevanceComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|DOC
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|DocComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|INT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|IntComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|,
operator|(
name|Integer
operator|)
name|missingValue
argument_list|)
return|;
case|case
name|FLOAT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|FloatComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|,
operator|(
name|Float
operator|)
name|missingValue
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|LongComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|,
operator|(
name|Long
operator|)
name|missingValue
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|DoubleComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|,
operator|(
name|Double
operator|)
name|missingValue
argument_list|)
return|;
case|case
name|CUSTOM
case|:
assert|assert
name|comparatorSource
operator|!=
literal|null
assert|;
return|return
name|comparatorSource
operator|.
name|newComparator
argument_list|(
name|field
argument_list|,
name|numHits
argument_list|,
name|sortPos
argument_list|,
name|reverse
argument_list|)
return|;
case|case
name|STRING
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|TermOrdValComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|missingValue
operator|==
name|STRING_LAST
argument_list|)
return|;
case|case
name|STRING_VAL
case|:
comment|// TODO: should we remove this?  who really uses it?
return|return
operator|new
name|FieldComparator
operator|.
name|TermValComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|)
return|;
case|case
name|REWRITEABLE
case|:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"SortField needs to be rewritten through Sort.rewrite(..) and SortField.rewrite(..)"
argument_list|)
throw|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal sort type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/**    * Rewrites this SortField, returning a new SortField if a change is made.    * Subclasses should override this define their rewriting behavior when this    * SortField is of type {@link SortField.Type#REWRITEABLE}    *    * @param searcher IndexSearcher to use during rewriting    * @return New rewritten SortField, or {@code this} if nothing has changed.    * @throws IOException Can be thrown by the rewriting    * @lucene.experimental    */
DECL|method|rewrite
specifier|public
name|SortField
name|rewrite
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
comment|/** Whether the relevance score is needed to sort documents. */
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|type
operator|==
name|Type
operator|.
name|SCORE
return|;
block|}
block|}
end_class

end_unit

