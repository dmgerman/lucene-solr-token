begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|NumericDocValues
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
name|SortedNumericDocValues
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
name|BitSet
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
name|NumericUtils
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

begin_comment
comment|/**  * A special sort field that allows sorting parent docs based on nested / child level fields.  * Based on the sort order it either takes the document with the lowest or highest field value into account.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ToParentBlockJoinSortField
specifier|public
class|class
name|ToParentBlockJoinSortField
extends|extends
name|SortField
block|{
DECL|field|order
specifier|private
specifier|final
name|boolean
name|order
decl_stmt|;
DECL|field|parentFilter
specifier|private
specifier|final
name|BitSetProducer
name|parentFilter
decl_stmt|;
DECL|field|childFilter
specifier|private
specifier|final
name|BitSetProducer
name|childFilter
decl_stmt|;
comment|/**    * Create ToParentBlockJoinSortField. The parent document ordering is based on child document ordering (reverse).    *    * @param field The sort field on the nested / child level.    * @param type The sort type on the nested / child level.    * @param reverse Whether natural order should be reversed on the nested / child level.    * @param parentFilter Filter that identifies the parent documents.    * @param childFilter Filter that defines which child documents participates in sorting.    */
DECL|method|ToParentBlockJoinSortField
specifier|public
name|ToParentBlockJoinSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|BitSetProducer
name|parentFilter
parameter_list|,
name|BitSetProducer
name|childFilter
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|type
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|STRING
case|:
case|case
name|DOUBLE
case|:
case|case
name|FLOAT
case|:
case|case
name|LONG
case|:
case|case
name|INT
case|:
comment|// ok
break|break;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Sort type "
operator|+
name|type
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
name|this
operator|.
name|order
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|parentFilter
expr_stmt|;
name|this
operator|.
name|childFilter
operator|=
name|childFilter
expr_stmt|;
block|}
comment|/**    * Create ToParentBlockJoinSortField.    *    * @param field The sort field on the nested / child level.    * @param type The sort type on the nested / child level.    * @param reverse Whether natural order should be reversed on the nested / child document level.    * @param order Whether natural order should be reversed on the parent level.    * @param parentFilter Filter that identifies the parent documents.    * @param childFilter Filter that defines which child documents participates in sorting.    */
DECL|method|ToParentBlockJoinSortField
specifier|public
name|ToParentBlockJoinSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Type
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|boolean
name|order
parameter_list|,
name|BitSetProducer
name|parentFilter
parameter_list|,
name|BitSetProducer
name|childFilter
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|type
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|parentFilter
expr_stmt|;
name|this
operator|.
name|childFilter
operator|=
name|childFilter
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
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|STRING
case|:
return|return
name|getStringComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|getDoubleComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|FLOAT
case|:
return|return
name|getFloatComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
name|getLongComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|INT
case|:
return|return
name|getIntComparator
argument_list|(
name|numHits
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Sort type "
operator|+
name|getType
argument_list|()
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
DECL|method|getStringComparator
specifier|private
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getStringComparator
parameter_list|(
name|int
name|numHits
parameter_list|)
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
name|LeafReaderContext
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
name|BlockJoinSelector
operator|.
name|Type
name|type
init|=
name|order
condition|?
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MAX
else|:
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MIN
decl_stmt|;
specifier|final
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
name|DocValues
operator|.
name|emptySorted
argument_list|()
return|;
block|}
return|return
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|sortedSet
argument_list|,
name|type
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getIntComparator
specifier|private
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getIntComparator
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|IntComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Integer
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedNumericDocValues
name|sortedNumeric
init|=
name|DocValues
operator|.
name|getSortedNumeric
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
name|BlockJoinSelector
operator|.
name|Type
name|type
init|=
name|order
condition|?
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MAX
else|:
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MIN
decl_stmt|;
specifier|final
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
name|DocValues
operator|.
name|emptyNumeric
argument_list|()
return|;
block|}
return|return
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|sortedNumeric
argument_list|,
name|type
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Bits
name|getDocsWithValue
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Bits
name|docsWithValue
init|=
name|DocValues
operator|.
name|getDocsWithField
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
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
return|return
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|docsWithValue
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getLongComparator
specifier|private
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getLongComparator
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|LongComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Long
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedNumericDocValues
name|sortedNumeric
init|=
name|DocValues
operator|.
name|getSortedNumeric
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
name|BlockJoinSelector
operator|.
name|Type
name|type
init|=
name|order
condition|?
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MAX
else|:
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MIN
decl_stmt|;
specifier|final
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
name|DocValues
operator|.
name|emptyNumeric
argument_list|()
return|;
block|}
return|return
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|sortedNumeric
argument_list|,
name|type
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Bits
name|getDocsWithValue
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Bits
name|docsWithValue
init|=
name|DocValues
operator|.
name|getDocsWithField
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
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
return|return
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|docsWithValue
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getFloatComparator
specifier|private
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getFloatComparator
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|FloatComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Float
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedNumericDocValues
name|sortedNumeric
init|=
name|DocValues
operator|.
name|getSortedNumeric
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
name|BlockJoinSelector
operator|.
name|Type
name|type
init|=
name|order
condition|?
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MAX
else|:
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MIN
decl_stmt|;
specifier|final
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
name|DocValues
operator|.
name|emptyNumeric
argument_list|()
return|;
block|}
specifier|final
name|NumericDocValues
name|view
init|=
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|sortedNumeric
argument_list|,
name|type
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
decl_stmt|;
comment|// undo the numericutils sortability
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableFloatBits
argument_list|(
operator|(
name|int
operator|)
name|view
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|getDoubleComparator
specifier|private
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getDoubleComparator
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
return|return
operator|new
name|FieldComparator
operator|.
name|DoubleComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Double
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedNumericDocValues
name|sortedNumeric
init|=
name|DocValues
operator|.
name|getSortedNumeric
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
name|BlockJoinSelector
operator|.
name|Type
name|type
init|=
name|order
condition|?
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MAX
else|:
name|BlockJoinSelector
operator|.
name|Type
operator|.
name|MIN
decl_stmt|;
specifier|final
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
name|DocValues
operator|.
name|emptyNumeric
argument_list|()
return|;
block|}
specifier|final
name|NumericDocValues
name|view
init|=
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|sortedNumeric
argument_list|,
name|type
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
decl_stmt|;
comment|// undo the numericutils sortability
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableDoubleBits
argument_list|(
name|view
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Bits
name|getDocsWithValue
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Bits
name|docsWithValue
init|=
name|DocValues
operator|.
name|getDocsWithField
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
name|BitSet
name|parents
init|=
name|parentFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|children
init|=
name|childFilter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
return|return
name|BlockJoinSelector
operator|.
name|wrap
argument_list|(
name|docsWithValue
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

