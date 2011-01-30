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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|packed
operator|.
name|Direct16
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
name|packed
operator|.
name|Direct32
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
name|packed
operator|.
name|Direct8
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|ByteUtils
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
DECL|field|missingValueProxy
specifier|private
specifier|final
name|BytesRef
name|missingValueProxy
decl_stmt|;
DECL|method|MissingStringLastComparatorSource
specifier|public
name|MissingStringLastComparatorSource
parameter_list|()
block|{
name|this
argument_list|(
name|ByteUtils
operator|.
name|bigTerm
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@link FieldComparatorSource} that sorts null last in a normal ascending sort.    *<tt>missingValueProxy</tt> as the value to return from FieldComparator.value()    *    * @param missingValueProxy   The value returned when sortValue() is called for a document missing the sort field.    * This value is *not* normally used for sorting.    */
DECL|method|MissingStringLastComparatorSource
specifier|public
name|MissingStringLastComparatorSource
parameter_list|(
name|BytesRef
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
annotation|@
name|Override
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
name|TermOrdValComparator_SML
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
name|sortPos
argument_list|,
name|reversed
argument_list|,
name|missingValueProxy
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// Copied from Lucene's TermOrdValComparator and modified since the Lucene version couldn't
end_comment

begin_comment
comment|// be extended.
end_comment

begin_class
DECL|class|TermOrdValComparator_SML
class|class
name|TermOrdValComparator_SML
extends|extends
name|FieldComparator
block|{
DECL|field|NULL_ORD
specifier|private
specifier|static
specifier|final
name|int
name|NULL_ORD
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
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
name|BytesRef
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
DECL|field|termsIndex
specifier|private
name|FieldCache
operator|.
name|DocTermsIndex
name|termsIndex
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|NULL_VAL
specifier|private
specifier|final
name|BytesRef
name|NULL_VAL
decl_stmt|;
DECL|field|current
specifier|private
name|PerSegmentComparator
name|current
decl_stmt|;
DECL|method|TermOrdValComparator_SML
specifier|public
name|TermOrdValComparator_SML
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
name|BytesRef
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
name|BytesRef
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
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|NULL_VAL
operator|=
name|nullVal
expr_stmt|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TermOrdValComparator_SML
operator|.
name|createComparator
argument_list|(
name|context
operator|.
name|reader
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|// Base class for specialized (per bit width of the
comment|// ords) per-segment comparator.  NOTE: this is messy;
comment|// we do this only because hotspot can't reliably inline
comment|// the underlying array access when looking up doc->ord
DECL|class|PerSegmentComparator
specifier|private
specifier|static
specifier|abstract
class|class
name|PerSegmentComparator
extends|extends
name|FieldComparator
block|{
DECL|field|parent
specifier|protected
name|TermOrdValComparator_SML
name|parent
decl_stmt|;
DECL|field|ords
specifier|protected
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|BytesRef
index|[]
name|values
decl_stmt|;
DECL|field|readerGen
specifier|protected
specifier|final
name|int
index|[]
name|readerGen
decl_stmt|;
DECL|field|currentReaderGen
specifier|protected
name|int
name|currentReaderGen
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|termsIndex
specifier|protected
name|FieldCache
operator|.
name|DocTermsIndex
name|termsIndex
decl_stmt|;
DECL|field|bottomSlot
specifier|protected
name|int
name|bottomSlot
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|bottomOrd
specifier|protected
name|int
name|bottomOrd
decl_stmt|;
DECL|field|bottomSameReader
specifier|protected
name|boolean
name|bottomSameReader
init|=
literal|false
decl_stmt|;
DECL|field|bottomValue
specifier|protected
name|BytesRef
name|bottomValue
decl_stmt|;
DECL|field|tempBR
specifier|protected
specifier|final
name|BytesRef
name|tempBR
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|PerSegmentComparator
specifier|public
name|PerSegmentComparator
parameter_list|(
name|TermOrdValComparator_SML
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|PerSegmentComparator
name|previous
init|=
name|parent
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|currentReaderGen
operator|=
name|previous
operator|.
name|currentReaderGen
expr_stmt|;
name|bottomSlot
operator|=
name|previous
operator|.
name|bottomSlot
expr_stmt|;
name|bottomOrd
operator|=
name|previous
operator|.
name|bottomOrd
expr_stmt|;
name|bottomValue
operator|=
name|previous
operator|.
name|bottomValue
expr_stmt|;
block|}
name|ords
operator|=
name|parent
operator|.
name|ords
expr_stmt|;
name|values
operator|=
name|parent
operator|.
name|values
expr_stmt|;
name|readerGen
operator|=
name|parent
operator|.
name|readerGen
expr_stmt|;
name|termsIndex
operator|=
name|parent
operator|.
name|termsIndex
expr_stmt|;
name|currentReaderGen
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TermOrdValComparator_SML
operator|.
name|createComparator
argument_list|(
name|context
operator|.
name|reader
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|ords
index|[
name|slot1
index|]
operator|-
name|ords
index|[
name|slot2
index|]
return|;
block|}
specifier|final
name|BytesRef
name|val1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|BytesRef
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
literal|1
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
literal|1
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
annotation|@
name|Override
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
name|bottomValue
operator|=
name|values
index|[
name|bottomSlot
index|]
expr_stmt|;
if|if
condition|(
name|currentReaderGen
operator|==
name|readerGen
index|[
name|bottomSlot
index|]
condition|)
block|{
name|bottomOrd
operator|=
name|ords
index|[
name|bottomSlot
index|]
expr_stmt|;
name|bottomSameReader
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|bottomValue
operator|==
literal|null
condition|)
block|{
comment|// 0 ord is null for all segments
assert|assert
name|ords
index|[
name|bottomSlot
index|]
operator|==
name|NULL_ORD
assert|;
name|bottomOrd
operator|=
name|NULL_ORD
expr_stmt|;
name|bottomSameReader
operator|=
literal|true
expr_stmt|;
name|readerGen
index|[
name|bottomSlot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|index
init|=
name|binarySearch
argument_list|(
name|tempBR
argument_list|,
name|termsIndex
argument_list|,
name|bottomValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|bottomOrd
operator|=
operator|-
name|index
operator|-
literal|2
expr_stmt|;
name|bottomSameReader
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|bottomOrd
operator|=
name|index
expr_stmt|;
comment|// exact value match
name|bottomSameReader
operator|=
literal|true
expr_stmt|;
name|readerGen
index|[
name|bottomSlot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
name|ords
index|[
name|bottomSlot
index|]
operator|=
name|bottomOrd
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Comparable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
operator|==
literal|null
condition|?
name|parent
operator|.
name|NULL_VAL
else|:
name|values
index|[
name|slot
index|]
return|;
block|}
block|}
comment|// Used per-segment when bit width of doc->ord is 8:
DECL|class|ByteOrdComparator
specifier|private
specifier|static
specifier|final
class|class
name|ByteOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|byte
index|[]
name|readerOrds
decl_stmt|;
DECL|method|ByteOrdComparator
specifier|public
name|ByteOrdComparator
parameter_list|(
name|byte
index|[]
name|readerOrds
parameter_list|,
name|TermOrdValComparator_SML
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
block|}
annotation|@
name|Override
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
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFF
decl_stmt|;
if|if
condition|(
name|order
operator|==
literal|0
condition|)
name|order
operator|=
name|NULL_ORD
expr_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|order
return|;
block|}
else|else
block|{
comment|// ord is only approx comparable: if they are not
comment|// equal, we can use that; if they are equal, we
comment|// must fallback to compare by value
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
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
comment|// take care of the case where both vals are null
if|if
condition|(
name|order
operator|==
name|NULL_ORD
condition|)
return|return
literal|0
return|;
comment|// and at this point we know that neither value is null, so safe to compare
name|termsIndex
operator|.
name|lookup
argument_list|(
name|order
argument_list|,
name|tempBR
argument_list|)
expr_stmt|;
return|return
name|bottomValue
operator|.
name|compareTo
argument_list|(
name|tempBR
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
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
name|int
name|ord
init|=
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFF
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|NULL_ORD
expr_stmt|;
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
comment|// Used per-segment when bit width of doc->ord is 16:
DECL|class|ShortOrdComparator
specifier|private
specifier|static
specifier|final
class|class
name|ShortOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|short
index|[]
name|readerOrds
decl_stmt|;
DECL|method|ShortOrdComparator
specifier|public
name|ShortOrdComparator
parameter_list|(
name|short
index|[]
name|readerOrds
parameter_list|,
name|TermOrdValComparator_SML
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
block|}
annotation|@
name|Override
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
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFFFF
decl_stmt|;
if|if
condition|(
name|order
operator|==
literal|0
condition|)
name|order
operator|=
name|NULL_ORD
expr_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|order
return|;
block|}
else|else
block|{
comment|// ord is only approx comparable: if they are not
comment|// equal, we can use that; if they are equal, we
comment|// must fallback to compare by value
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
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
comment|// take care of the case where both vals are null
if|if
condition|(
name|order
operator|==
name|NULL_ORD
condition|)
return|return
literal|0
return|;
comment|// and at this point we know that neither value is null, so safe to compare
name|termsIndex
operator|.
name|lookup
argument_list|(
name|order
argument_list|,
name|tempBR
argument_list|)
expr_stmt|;
return|return
name|bottomValue
operator|.
name|compareTo
argument_list|(
name|tempBR
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
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
name|int
name|ord
init|=
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFFFF
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|NULL_ORD
expr_stmt|;
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
comment|// Used per-segment when bit width of doc->ord is 32:
DECL|class|IntOrdComparator
specifier|private
specifier|static
specifier|final
class|class
name|IntOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|int
index|[]
name|readerOrds
decl_stmt|;
DECL|method|IntOrdComparator
specifier|public
name|IntOrdComparator
parameter_list|(
name|int
index|[]
name|readerOrds
parameter_list|,
name|TermOrdValComparator_SML
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
block|}
annotation|@
name|Override
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
name|readerOrds
index|[
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|order
operator|==
literal|0
condition|)
name|order
operator|=
name|NULL_ORD
expr_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|order
return|;
block|}
else|else
block|{
comment|// ord is only approx comparable: if they are not
comment|// equal, we can use that; if they are equal, we
comment|// must fallback to compare by value
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
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
comment|// take care of the case where both vals are null
if|if
condition|(
name|order
operator|==
name|NULL_ORD
condition|)
return|return
literal|0
return|;
comment|// and at this point we know that neither value is null, so safe to compare
name|termsIndex
operator|.
name|lookup
argument_list|(
name|order
argument_list|,
name|tempBR
argument_list|)
expr_stmt|;
return|return
name|bottomValue
operator|.
name|compareTo
argument_list|(
name|tempBR
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
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
name|int
name|ord
init|=
name|readerOrds
index|[
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|NULL_ORD
expr_stmt|;
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
comment|// Used per-segment when bit width is not a native array
comment|// size (8, 16, 32):
DECL|class|AnyOrdComparator
specifier|private
specifier|static
specifier|final
class|class
name|AnyOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|readerOrds
decl_stmt|;
DECL|method|AnyOrdComparator
specifier|public
name|AnyOrdComparator
parameter_list|(
name|PackedInts
operator|.
name|Reader
name|readerOrds
parameter_list|,
name|TermOrdValComparator_SML
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
block|}
annotation|@
name|Override
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
operator|(
name|int
operator|)
name|readerOrds
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|==
literal|0
condition|)
name|order
operator|=
name|NULL_ORD
expr_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|order
return|;
block|}
else|else
block|{
comment|// ord is only approx comparable: if they are not
comment|// equal, we can use that; if they are equal, we
comment|// must fallback to compare by value
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
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
comment|// take care of the case where both vals are null
if|if
condition|(
name|order
operator|==
name|NULL_ORD
condition|)
return|return
literal|0
return|;
comment|// and at this point we know that neither value is null, so safe to compare
name|termsIndex
operator|.
name|lookup
argument_list|(
name|order
argument_list|,
name|tempBR
argument_list|)
expr_stmt|;
return|return
name|bottomValue
operator|.
name|compareTo
argument_list|(
name|tempBR
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
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
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|readerOrds
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|NULL_ORD
expr_stmt|;
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
DECL|method|createComparator
specifier|public
specifier|static
name|FieldComparator
name|createComparator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TermOrdValComparator_SML
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|parent
operator|.
name|termsIndex
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
name|parent
operator|.
name|field
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|docToOrd
init|=
name|parent
operator|.
name|termsIndex
operator|.
name|getDocToOrd
argument_list|()
decl_stmt|;
name|PerSegmentComparator
name|perSegComp
decl_stmt|;
if|if
condition|(
name|docToOrd
operator|instanceof
name|Direct8
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|ByteOrdComparator
argument_list|(
operator|(
operator|(
name|Direct8
operator|)
name|docToOrd
operator|)
operator|.
name|getArray
argument_list|()
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docToOrd
operator|instanceof
name|Direct16
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|ShortOrdComparator
argument_list|(
operator|(
operator|(
name|Direct16
operator|)
name|docToOrd
operator|)
operator|.
name|getArray
argument_list|()
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docToOrd
operator|instanceof
name|Direct32
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|IntOrdComparator
argument_list|(
operator|(
operator|(
name|Direct32
operator|)
name|docToOrd
operator|)
operator|.
name|getArray
argument_list|()
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|perSegComp
operator|=
operator|new
name|AnyOrdComparator
argument_list|(
name|docToOrd
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|perSegComp
operator|.
name|bottomSlot
operator|!=
operator|-
literal|1
condition|)
block|{
name|perSegComp
operator|.
name|setBottom
argument_list|(
name|perSegComp
operator|.
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|current
operator|=
name|perSegComp
expr_stmt|;
return|return
name|perSegComp
return|;
block|}
block|}
end_class

end_unit

