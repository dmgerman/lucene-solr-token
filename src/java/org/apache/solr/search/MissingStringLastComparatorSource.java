begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|search
operator|.
name|*
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
name|IndexReader
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

begin_class
DECL|class|MissingStringLastComparatorSource
specifier|public
class|class
name|MissingStringLastComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|bigString
specifier|public
specifier|static
specifier|final
name|String
name|bigString
init|=
literal|"\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffffNULL_VAL"
decl_stmt|;
DECL|field|missingValueProxy
specifier|private
specifier|final
name|String
name|missingValueProxy
decl_stmt|;
DECL|method|MissingStringLastComparatorSource
specifier|public
name|MissingStringLastComparatorSource
parameter_list|()
block|{
name|this
argument_list|(
name|bigString
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@link FieldComparatorSource} that uses<tt>missingValueProxy</tt> as the value to return from ScoreDocComparator.sortValue()    * which is only used my multisearchers to determine how to collate results from their searchers.    *    * @param missingValueProxy   The value returned when sortValue() is called for a document missing the sort field.    * This value is *not* normally used for sorting, but used to create    */
DECL|method|MissingStringLastComparatorSource
specifier|public
name|MissingStringLastComparatorSource
parameter_list|(
name|String
name|missingValueProxy
parameter_list|)
block|{
name|this
operator|.
name|missingValueProxy
operator|=
name|missingValueProxy
expr_stmt|;
block|}
DECL|method|newComparator
specifier|public
name|FieldComparator
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MissingLastOrdComparator
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
name|sortPos
argument_list|,
name|reversed
argument_list|,
literal|true
argument_list|,
name|missingValueProxy
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// Copied from Lucene and modified since the Lucene version couldn't
end_comment

begin_comment
comment|// be extended or have it's values accessed.
end_comment

begin_comment
comment|// NOTE: there were a number of other interesting String
end_comment

begin_comment
comment|// comparators explored, but this one seemed to perform
end_comment

begin_comment
comment|// best all around.  See LUCENE-1483 for details.
end_comment

begin_class
DECL|class|MissingLastOrdComparator
class|class
name|MissingLastOrdComparator
extends|extends
name|FieldComparator
block|{
DECL|field|ords
specifier|private
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|String
index|[]
name|values
decl_stmt|;
DECL|field|readerGen
specifier|private
specifier|final
name|int
index|[]
name|readerGen
decl_stmt|;
DECL|field|currentReaderGen
specifier|private
name|int
name|currentReaderGen
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|lookup
specifier|private
name|String
index|[]
name|lookup
decl_stmt|;
DECL|field|order
specifier|private
name|int
index|[]
name|order
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|bottomSlot
specifier|private
name|int
name|bottomSlot
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|bottomOrd
specifier|private
name|int
name|bottomOrd
decl_stmt|;
DECL|field|bottomValue
specifier|private
name|String
name|bottomValue
decl_stmt|;
DECL|field|reversed
specifier|private
specifier|final
name|boolean
name|reversed
decl_stmt|;
DECL|field|sortPos
specifier|private
specifier|final
name|int
name|sortPos
decl_stmt|;
DECL|field|nullCmp
specifier|private
specifier|final
name|int
name|nullCmp
decl_stmt|;
DECL|field|nullVal
specifier|private
specifier|final
name|Comparable
name|nullVal
decl_stmt|;
DECL|method|MissingLastOrdComparator
specifier|public
name|MissingLastOrdComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|,
name|boolean
name|sortMissingLast
parameter_list|,
name|Comparable
name|nullVal
parameter_list|)
block|{
name|ords
operator|=
operator|new
name|int
index|[
name|numHits
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|String
index|[
name|numHits
index|]
expr_stmt|;
name|readerGen
operator|=
operator|new
name|int
index|[
name|numHits
index|]
expr_stmt|;
name|this
operator|.
name|sortPos
operator|=
name|sortPos
expr_stmt|;
name|this
operator|.
name|reversed
operator|=
name|reversed
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|nullCmp
operator|=
name|sortMissingLast
condition|?
literal|1
else|:
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|nullVal
operator|=
name|nullVal
expr_stmt|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
name|int
name|ord1
init|=
name|ords
index|[
name|slot1
index|]
decl_stmt|;
name|int
name|ord2
init|=
name|ords
index|[
name|slot2
index|]
decl_stmt|;
name|int
name|cmp
init|=
name|ord1
operator|-
name|ord2
decl_stmt|;
if|if
condition|(
name|ord1
operator|==
literal|0
operator|||
name|ord2
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
return|return
literal|0
return|;
return|return
name|ord1
operator|==
literal|0
condition|?
name|nullCmp
else|:
operator|-
name|nullCmp
return|;
block|}
if|if
condition|(
name|readerGen
index|[
name|slot1
index|]
operator|==
name|readerGen
index|[
name|slot2
index|]
condition|)
block|{
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
specifier|final
name|String
name|val1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|String
name|val2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|val1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|nullCmp
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
name|nullCmp
return|;
block|}
return|return
name|val1
operator|.
name|compareTo
argument_list|(
name|val2
argument_list|)
return|;
block|}
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
assert|assert
name|bottomSlot
operator|!=
operator|-
literal|1
assert|;
name|int
name|order
init|=
name|this
operator|.
name|order
index|[
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|cmp
init|=
name|bottomOrd
operator|-
name|order
decl_stmt|;
if|if
condition|(
name|bottomOrd
operator|==
literal|0
operator|||
name|order
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
return|return
literal|0
return|;
return|return
name|bottomOrd
operator|==
literal|0
condition|?
name|nullCmp
else|:
operator|-
name|nullCmp
return|;
block|}
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
specifier|final
name|String
name|val2
init|=
name|lookup
index|[
name|order
index|]
decl_stmt|;
if|if
condition|(
name|bottomValue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// bottom wins
return|return
name|nullCmp
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
comment|// doc wins
return|return
operator|-
name|nullCmp
return|;
block|}
return|return
name|bottomValue
operator|.
name|compareTo
argument_list|(
name|val2
argument_list|)
return|;
block|}
DECL|method|convert
specifier|private
name|void
name|convert
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
name|String
name|value
init|=
name|values
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|ords
index|[
name|slot
index|]
operator|=
literal|0
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|sortPos
operator|==
literal|0
operator|&&
name|bottomSlot
operator|!=
operator|-
literal|1
operator|&&
name|bottomSlot
operator|!=
name|slot
condition|)
block|{
comment|// Since we are the primary sort, the entries in the
comment|// queue are bounded by bottomOrd:
assert|assert
name|bottomOrd
operator|<
name|lookup
operator|.
name|length
assert|;
if|if
condition|(
name|reversed
condition|)
block|{
name|index
operator|=
name|binarySearch
argument_list|(
name|lookup
argument_list|,
name|value
argument_list|,
name|bottomOrd
argument_list|,
name|lookup
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
name|binarySearch
argument_list|(
name|lookup
argument_list|,
name|value
argument_list|,
literal|0
argument_list|,
name|bottomOrd
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Full binary search
name|index
operator|=
name|binarySearch
argument_list|(
name|lookup
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|index
operator|=
operator|-
name|index
operator|-
literal|2
expr_stmt|;
block|}
name|ords
index|[
name|slot
index|]
operator|=
name|index
expr_stmt|;
block|}
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|order
index|[
name|doc
index|]
decl_stmt|;
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
assert|assert
name|ord
operator|>=
literal|0
assert|;
name|values
index|[
name|slot
index|]
operator|=
name|lookup
index|[
name|ord
index|]
expr_stmt|;
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|,
name|int
name|numSlotsFull
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldCache
operator|.
name|StringIndex
name|currentReaderValues
init|=
name|ExtendedFieldCache
operator|.
name|EXT_DEFAULT
operator|.
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|currentReaderGen
operator|++
expr_stmt|;
name|order
operator|=
name|currentReaderValues
operator|.
name|order
expr_stmt|;
name|lookup
operator|=
name|currentReaderValues
operator|.
name|lookup
expr_stmt|;
assert|assert
name|lookup
operator|.
name|length
operator|>
literal|0
assert|;
if|if
condition|(
name|bottomSlot
operator|!=
operator|-
literal|1
condition|)
block|{
name|convert
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
name|bottomOrd
operator|=
name|ords
index|[
name|bottomSlot
index|]
expr_stmt|;
block|}
block|}
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|bottomSlot
operator|=
name|bottom
expr_stmt|;
if|if
condition|(
name|readerGen
index|[
name|bottom
index|]
operator|!=
name|currentReaderGen
condition|)
block|{
name|convert
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
name|bottomOrd
operator|=
name|ords
index|[
name|bottom
index|]
expr_stmt|;
assert|assert
name|bottomOrd
operator|>=
literal|0
assert|;
assert|assert
name|bottomOrd
operator|<
name|lookup
operator|.
name|length
assert|;
name|bottomValue
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
DECL|method|sortType
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|STRING
return|;
block|}
DECL|method|value
specifier|public
name|Comparable
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|Comparable
name|v
init|=
name|values
index|[
name|slot
index|]
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
name|nullVal
else|:
literal|null
return|;
block|}
DECL|method|getValues
specifier|public
name|String
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
DECL|method|getBottomSlot
specifier|public
name|int
name|getBottomSlot
parameter_list|()
block|{
return|return
name|bottomSlot
return|;
block|}
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
block|}
end_class

end_unit

