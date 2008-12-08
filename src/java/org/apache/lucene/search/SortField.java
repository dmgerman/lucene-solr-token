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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
comment|/**  * Stores information about how to sort documents by terms in an individual  * field.  Fields must be indexed in order to sort by them.  *  *<p>Created: Feb 11, 2004 1:25:29 PM  *  * @since   lucene 1.4  * @version $Id$  * @see Sort  */
end_comment

begin_class
DECL|class|SortField
specifier|public
class|class
name|SortField
implements|implements
name|Serializable
block|{
comment|/** Sort by document score (relevancy).  Sort values are Float and higher    * values are at the front. */
DECL|field|SCORE
specifier|public
specifier|static
specifier|final
name|int
name|SCORE
init|=
literal|0
decl_stmt|;
comment|/** Sort by document number (index order).  Sort values are Integer and lower    * values are at the front. */
DECL|field|DOC
specifier|public
specifier|static
specifier|final
name|int
name|DOC
init|=
literal|1
decl_stmt|;
comment|/** Guess type of sort based on field contents.  A regular expression is used    * to look at the first term indexed for the field and determine if it    * represents an integer number, a floating point number, or just arbitrary    * string characters. */
DECL|field|AUTO
specifier|public
specifier|static
specifier|final
name|int
name|AUTO
init|=
literal|2
decl_stmt|;
comment|/** Sort using term values as Strings.  Sort values are String and lower    * values are at the front. */
DECL|field|STRING
specifier|public
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|3
decl_stmt|;
comment|/** Sort using term values as encoded Integers.  Sort values are Integer and    * lower values are at the front. */
DECL|field|INT
specifier|public
specifier|static
specifier|final
name|int
name|INT
init|=
literal|4
decl_stmt|;
comment|/** Sort using term values as encoded Floats.  Sort values are Float and    * lower values are at the front. */
DECL|field|FLOAT
specifier|public
specifier|static
specifier|final
name|int
name|FLOAT
init|=
literal|5
decl_stmt|;
comment|/** Sort using term values as encoded Longs.  Sort values are Long and    * lower values are at the front. */
DECL|field|LONG
specifier|public
specifier|static
specifier|final
name|int
name|LONG
init|=
literal|6
decl_stmt|;
comment|/** Sort using term values as encoded Doubles.  Sort values are Double and    * lower values are at the front. */
DECL|field|DOUBLE
specifier|public
specifier|static
specifier|final
name|int
name|DOUBLE
init|=
literal|7
decl_stmt|;
comment|/**    * Sort using term values as encoded Shorts.  Sort values are shorts and lower values are at the front    */
DECL|field|SHORT
specifier|public
specifier|static
specifier|final
name|int
name|SHORT
init|=
literal|8
decl_stmt|;
comment|/** Sort using a custom Comparator.  Sort values are any Comparable and    * sorting is done according to natural order. */
DECL|field|CUSTOM
specifier|public
specifier|static
specifier|final
name|int
name|CUSTOM
init|=
literal|9
decl_stmt|;
comment|/**    * Sort using term values as encoded bytes.  Sort values are bytes and lower values are at the front    */
DECL|field|BYTE
specifier|public
specifier|static
specifier|final
name|int
name|BYTE
init|=
literal|10
decl_stmt|;
comment|// IMPLEMENTATION NOTE: the FieldCache.STRING_INDEX is in the same "namespace"
comment|// as the above static int values.  Any new values must not have the same value
comment|// as FieldCache.STRING_INDEX.
comment|/** Represents sorting by document score (relevancy). */
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
name|int
name|type
init|=
name|AUTO
decl_stmt|;
comment|// defaults to determining type dynamically
DECL|field|locale
specifier|private
name|Locale
name|locale
decl_stmt|;
comment|// defaults to "natural order" (no Locale)
DECL|field|reverse
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
comment|// defaults to natural order
DECL|field|factory
specifier|private
name|SortComparatorSource
name|factory
decl_stmt|;
comment|/** Creates a sort by terms in the given field where the type of term value    * is determined dynamically ({@link #AUTO AUTO}).    * @param field Name of field to sort by, cannot be<code>null</code>.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field where    * the type of term value is determined dynamically ({@link #AUTO AUTO}).    * @param field Name of field to sort by, cannot be<code>null</code>.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field with the type of term    * values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|intern
argument_list|()
else|:
name|field
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
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
name|int
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|intern
argument_list|()
else|:
name|field
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field sorted    * according to the given locale.    * @param field  Name of field to sort by, cannot be<code>null</code>.    * @param locale Locale of values in the field.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|STRING
expr_stmt|;
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field sorted    * according to the given locale.    * @param field  Name of field to sort by, cannot be<code>null</code>.    * @param locale Locale of values in the field.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|STRING
expr_stmt|;
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
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
name|SortComparatorSource
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|intern
argument_list|()
else|:
name|field
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|CUSTOM
expr_stmt|;
name|this
operator|.
name|factory
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
name|SortComparatorSource
name|comparator
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|intern
argument_list|()
else|:
name|field
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|CUSTOM
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|comparator
expr_stmt|;
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
comment|/** Returns the type of contents in the field.    * @return One of the constants SCORE, DOC, AUTO, STRING, INT or FLOAT.    */
DECL|method|getType
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/** Returns the Locale by which term values are interpreted.    * May return<code>null</code> if no Locale was specified.    * @return Locale, or<code>null</code>.    */
DECL|method|getLocale
specifier|public
name|Locale
name|getLocale
parameter_list|()
block|{
return|return
name|locale
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
DECL|method|getFactory
specifier|public
name|SortComparatorSource
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
name|factory
argument_list|)
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
break|break;
default|default:
name|buffer
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|locale
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this.  If a    *  {@link #SortComparatorSource} was provided, it must    *  properly implement equals. */
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
name|other
operator|.
name|field
operator|==
name|this
operator|.
name|field
comment|// field is always interned
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
name|locale
operator|==
literal|null
condition|?
name|this
operator|.
name|locale
operator|==
literal|null
else|:
name|other
operator|.
name|locale
operator|.
name|equals
argument_list|(
name|this
operator|.
name|locale
argument_list|)
operator|)
operator|&&
operator|(
name|other
operator|.
name|factory
operator|==
literal|null
condition|?
name|this
operator|.
name|factory
operator|==
literal|null
else|:
name|other
operator|.
name|factory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|factory
argument_list|)
operator|)
operator|)
return|;
block|}
comment|/** Returns a hash code value for this object.  If a    *  {@link #SortComparatorSource} was provided, it must    *  properly implement hashCode. */
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
name|locale
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|locale
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x08150815
expr_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|factory
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x34987555
expr_stmt|;
return|return
name|hash
return|;
block|}
block|}
end_class

end_unit

