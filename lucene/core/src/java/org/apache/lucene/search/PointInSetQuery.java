begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Arrays
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
name|document
operator|.
name|BinaryPoint
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
name|document
operator|.
name|DoublePoint
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
name|document
operator|.
name|FloatPoint
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
name|document
operator|.
name|IntPoint
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
name|document
operator|.
name|LongPoint
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
name|FieldInfo
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
name|LeafReader
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
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|PointValues
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
name|PrefixCodedTerms
operator|.
name|TermIterator
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
name|PrefixCodedTerms
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
name|BytesRefBuilder
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
name|DocIdSetBuilder
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
comment|/**  * Abstract query class to find all documents whose single or multi-dimensional point values, previously indexed with e.g. {@link IntPoint},  * is contained in the specified set.  *  *<p>  * This is for subclasses and works on the underlying binary encoding: to  * create range queries for lucene's standard {@code Point} types, refer to factory  * methods on those classes, e.g. {@link IntPoint#newSetQuery IntPoint.newSetQuery()} for   * fields indexed with {@link IntPoint}.   * @see IntPoint  * @see LongPoint  * @see FloatPoint  * @see DoublePoint  * @see BinaryPoint   *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PointInSetQuery
specifier|public
specifier|abstract
class|class
name|PointInSetQuery
extends|extends
name|Query
block|{
comment|// A little bit overkill for us, since all of our "terms" are always in the same field:
DECL|field|sortedPackedPoints
specifier|final
name|PrefixCodedTerms
name|sortedPackedPoints
decl_stmt|;
DECL|field|sortedPackedPointsHashCode
specifier|final
name|int
name|sortedPackedPointsHashCode
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|numDims
specifier|final
name|int
name|numDims
decl_stmt|;
DECL|field|bytesPerDim
specifier|final
name|int
name|bytesPerDim
decl_stmt|;
comment|/** The {@code packedPoints} iterator must be in sorted order. */
DECL|method|PointInSetQuery
specifier|protected
name|PointInSetQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numDims
parameter_list|,
name|int
name|bytesPerDim
parameter_list|,
name|BytesRefIterator
name|packedPoints
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|bytesPerDim
argument_list|<
literal|1
operator|||
name|bytesPerDim
argument_list|>
name|PointValues
operator|.
name|MAX_NUM_BYTES
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bytesPerDim must be> 0 and<= "
operator|+
name|PointValues
operator|.
name|MAX_NUM_BYTES
operator|+
literal|"; got "
operator|+
name|bytesPerDim
argument_list|)
throw|;
block|}
name|this
operator|.
name|bytesPerDim
operator|=
name|bytesPerDim
expr_stmt|;
if|if
condition|(
name|numDims
argument_list|<
literal|1
operator|||
name|numDims
argument_list|>
name|PointValues
operator|.
name|MAX_DIMENSIONS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numDims must be> 0 and<= "
operator|+
name|PointValues
operator|.
name|MAX_DIMENSIONS
operator|+
literal|"; got "
operator|+
name|numDims
argument_list|)
throw|;
block|}
name|this
operator|.
name|numDims
operator|=
name|numDims
expr_stmt|;
comment|// In the 1D case this works well (the more points, the more common prefixes they share, typically), but in
comment|// the> 1 D case, where we are only looking at the first dimension's prefix bytes, it can at worst not hurt:
name|PrefixCodedTerms
operator|.
name|Builder
name|builder
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BytesRefBuilder
name|previous
init|=
literal|null
decl_stmt|;
name|BytesRef
name|current
decl_stmt|;
while|while
condition|(
operator|(
name|current
operator|=
name|packedPoints
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|length
operator|!=
name|numDims
operator|*
name|bytesPerDim
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"packed point length should be "
operator|+
operator|(
name|numDims
operator|*
name|bytesPerDim
operator|)
operator|+
literal|" but got "
operator|+
name|current
operator|.
name|length
operator|+
literal|"; field=\""
operator|+
name|field
operator|+
literal|"\" numDims="
operator|+
name|numDims
operator|+
literal|" bytesPerDim="
operator|+
name|bytesPerDim
argument_list|)
throw|;
block|}
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|previous
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|cmp
init|=
name|previous
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// deduplicate
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"values are out of order: saw "
operator|+
name|previous
operator|+
literal|" before "
operator|+
name|current
argument_list|)
throw|;
block|}
block|}
name|builder
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|previous
operator|.
name|copyBytes
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|sortedPackedPoints
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|sortedPackedPointsHashCode
operator|=
name|sortedPackedPoints
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We don't use RandomAccessWeight here: it's no good to approximate with "match all docs".
comment|// This is an inverted structure and should be used in the first pass:
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|PointValues
name|values
init|=
name|reader
operator|.
name|getPointValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed any points
return|return
literal|null
return|;
block|}
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed this field at all
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|!=
name|numDims
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|field
operator|+
literal|"\" was indexed with numDims="
operator|+
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|+
literal|" but this query has numDims="
operator|+
name|numDims
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|!=
name|bytesPerDim
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|field
operator|+
literal|"\" was indexed with bytesPerDim="
operator|+
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|+
literal|" but this query has bytesPerDim="
operator|+
name|bytesPerDim
argument_list|)
throw|;
block|}
name|DocIdSetBuilder
name|result
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|numDims
operator|==
literal|1
condition|)
block|{
comment|// We optimize this common case, effectively doing a merge sort of the indexed values vs the queried set:
name|values
operator|.
name|intersect
argument_list|(
name|field
argument_list|,
operator|new
name|MergePointVisitor
argument_list|(
name|sortedPackedPoints
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// NOTE: this is naive implementation, where for each point we re-walk the KD tree to intersect.  We could instead do a similar
comment|// optimization as the 1D case, but I think it'd mean building a query-time KD tree so we could efficiently intersect against the
comment|// index, which is probably tricky!
name|SinglePointVisitor
name|visitor
init|=
operator|new
name|SinglePointVisitor
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|TermIterator
name|iterator
init|=
name|sortedPackedPoints
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|point
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|point
operator|!=
literal|null
condition|;
name|point
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
name|visitor
operator|.
name|setPoint
argument_list|(
name|point
argument_list|)
expr_stmt|;
name|values
operator|.
name|intersect
argument_list|(
name|field
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|result
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Essentially does a merge sort, only collecting hits when the indexed point and query point are the same.  This is an optimization,    *  used in the 1D case. */
DECL|class|MergePointVisitor
specifier|private
class|class
name|MergePointVisitor
implements|implements
name|IntersectVisitor
block|{
DECL|field|result
specifier|private
specifier|final
name|DocIdSetBuilder
name|result
decl_stmt|;
DECL|field|iterator
specifier|private
name|TermIterator
name|iterator
decl_stmt|;
DECL|field|nextQueryPoint
specifier|private
name|BytesRef
name|nextQueryPoint
decl_stmt|;
DECL|field|lastMaxPackedValue
specifier|private
specifier|final
name|byte
index|[]
name|lastMaxPackedValue
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|sortedPackedPoints
specifier|private
specifier|final
name|PrefixCodedTerms
name|sortedPackedPoints
decl_stmt|;
DECL|method|MergePointVisitor
specifier|public
name|MergePointVisitor
parameter_list|(
name|PrefixCodedTerms
name|sortedPackedPoints
parameter_list|,
name|DocIdSetBuilder
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|sortedPackedPoints
operator|=
name|sortedPackedPoints
expr_stmt|;
name|lastMaxPackedValue
operator|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|bytesPerDim
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|sortedPackedPoints
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|nextQueryPoint
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|result
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
block|{
name|scratch
operator|.
name|bytes
operator|=
name|packedValue
expr_stmt|;
while|while
condition|(
name|nextQueryPoint
operator|!=
literal|null
condition|)
block|{
name|int
name|cmp
init|=
name|nextQueryPoint
operator|.
name|compareTo
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
comment|// Query point equals index point, so collect and return
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// Query point is before index point, so we move to next query point
name|nextQueryPoint
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Query point is after index point, so we don't collect and we return:
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
while|while
condition|(
name|nextQueryPoint
operator|!=
literal|null
condition|)
block|{
name|scratch
operator|.
name|bytes
operator|=
name|minPackedValue
expr_stmt|;
name|int
name|cmpMin
init|=
name|nextQueryPoint
operator|.
name|compareTo
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmpMin
operator|<
literal|0
condition|)
block|{
comment|// query point is before the start of this cell
name|nextQueryPoint
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|scratch
operator|.
name|bytes
operator|=
name|maxPackedValue
expr_stmt|;
name|int
name|cmpMax
init|=
name|nextQueryPoint
operator|.
name|compareTo
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmpMax
operator|>
literal|0
condition|)
block|{
comment|// query point is after the end of this cell
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
if|if
condition|(
name|cmpMin
operator|==
literal|0
operator|&&
name|cmpMax
operator|==
literal|0
condition|)
block|{
comment|// NOTE: we only hit this if we are on a cell whose min and max values are exactly equal to our point,
comment|// which can easily happen if many (> 1024) docs share this one value
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
else|else
block|{
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
comment|// We exhausted all points in the query:
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
comment|/** IntersectVisitor that queries against a highly degenerate shape: a single point.  This is used in the> 1D case. */
DECL|class|SinglePointVisitor
specifier|private
class|class
name|SinglePointVisitor
implements|implements
name|IntersectVisitor
block|{
DECL|field|result
specifier|private
specifier|final
name|DocIdSetBuilder
name|result
decl_stmt|;
DECL|field|pointBytes
specifier|private
specifier|final
name|byte
index|[]
name|pointBytes
decl_stmt|;
DECL|method|SinglePointVisitor
specifier|public
name|SinglePointVisitor
parameter_list|(
name|DocIdSetBuilder
name|result
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|pointBytes
operator|=
operator|new
name|byte
index|[
name|bytesPerDim
operator|*
name|numDims
index|]
expr_stmt|;
block|}
DECL|method|setPoint
specifier|public
name|void
name|setPoint
parameter_list|(
name|BytesRef
name|point
parameter_list|)
block|{
comment|// we verified this up front in query's ctor:
assert|assert
name|point
operator|.
name|length
operator|==
name|pointBytes
operator|.
name|length
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|point
operator|.
name|bytes
argument_list|,
name|point
operator|.
name|offset
argument_list|,
name|pointBytes
argument_list|,
literal|0
argument_list|,
name|pointBytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|result
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
block|{
assert|assert
name|packedValue
operator|.
name|length
operator|==
name|pointBytes
operator|.
name|length
assert|;
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|packedValue
argument_list|,
name|pointBytes
argument_list|)
condition|)
block|{
comment|// The point for this doc matches the point we are querying on
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
name|boolean
name|crosses
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|offset
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
name|int
name|cmpMin
init|=
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|minPackedValue
argument_list|,
name|offset
argument_list|,
name|pointBytes
argument_list|,
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmpMin
operator|>
literal|0
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
name|int
name|cmpMax
init|=
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|maxPackedValue
argument_list|,
name|offset
argument_list|,
name|pointBytes
argument_list|,
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmpMax
operator|<
literal|0
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
if|if
condition|(
name|cmpMin
operator|!=
literal|0
operator|||
name|cmpMax
operator|!=
literal|0
condition|)
block|{
name|crosses
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|crosses
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
else|else
block|{
comment|// NOTE: we only hit this if we are on a cell whose min and max values are exactly equal to our point,
comment|// which can easily happen if many docs share this one value
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
block|}
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
name|hash
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|sortedPackedPointsHashCode
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|numDims
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|bytesPerDim
expr_stmt|;
return|return
name|hash
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
name|other
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
block|{
specifier|final
name|PointInSetQuery
name|q
init|=
operator|(
name|PointInSetQuery
operator|)
name|other
decl_stmt|;
return|return
name|q
operator|.
name|numDims
operator|==
name|numDims
operator|&&
name|q
operator|.
name|bytesPerDim
operator|==
name|bytesPerDim
operator|&&
name|q
operator|.
name|sortedPackedPointsHashCode
operator|==
name|sortedPackedPointsHashCode
operator|&&
name|q
operator|.
name|sortedPackedPoints
operator|.
name|equals
argument_list|(
name|sortedPackedPoints
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|TermIterator
name|iterator
init|=
name|sortedPackedPoints
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|byte
index|[]
name|pointBytes
init|=
operator|new
name|byte
index|[
name|numDims
operator|*
name|bytesPerDim
index|]
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|BytesRef
name|point
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|point
operator|!=
literal|null
condition|;
name|point
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
name|first
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|point
operator|.
name|bytes
argument_list|,
name|point
operator|.
name|offset
argument_list|,
name|pointBytes
argument_list|,
literal|0
argument_list|,
name|pointBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|pointBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a string of a single value in a human-readable format for debugging.    * This is used by {@link #toString()}.    *    * The default implementation encodes the individual byte values.    *    * @param value single value, never null    * @return human readable value for debugging    */
DECL|method|toString
specifier|protected
specifier|abstract
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
function_decl|;
block|}
end_class

end_unit

