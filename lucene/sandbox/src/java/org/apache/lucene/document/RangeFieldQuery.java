begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|IntPredicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|search
operator|.
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|Query
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
name|Scorer
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
name|Weight
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
comment|/**  * Query class for searching {@code RangeField} types by a defined {@link Relation}.  */
end_comment

begin_class
DECL|class|RangeFieldQuery
specifier|abstract
class|class
name|RangeFieldQuery
extends|extends
name|Query
block|{
comment|/** field name */
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
comment|/** query relation    * intersects: {@code CELL_CROSSES_QUERY},    * contains: {@code CELL_CONTAINS_QUERY},    * within: {@code CELL_WITHIN_QUERY} */
DECL|field|queryType
specifier|final
name|QueryType
name|queryType
decl_stmt|;
comment|/** number of dimensions - max 4 */
DECL|field|numDims
specifier|final
name|int
name|numDims
decl_stmt|;
comment|/** ranges encoded as a sortable byte array */
DECL|field|ranges
specifier|final
name|byte
index|[]
name|ranges
decl_stmt|;
comment|/** number of bytes per dimension */
DECL|field|bytesPerDim
specifier|final
name|int
name|bytesPerDim
decl_stmt|;
comment|/** Used by {@code RangeFieldQuery} to check how each internal or leaf node relates to the query. */
DECL|enum|QueryType
enum|enum
name|QueryType
block|{
comment|/** Use this for intersects queries. */
DECL|enum constant|INTERSECTS
name|INTERSECTS
block|,
comment|/** Use this for within queries. */
DECL|enum constant|WITHIN
name|WITHIN
block|,
comment|/** Use this for contains */
DECL|enum constant|CONTAINS
name|CONTAINS
block|,
comment|/** Use this for crosses queries */
DECL|enum constant|CROSSES
name|CROSSES
block|}
comment|/**    * Create a query for searching indexed ranges that match the provided relation.    * @param field field name. must not be null.    * @param ranges encoded range values; this is done by the {@code RangeField} implementation    * @param queryType the query relation    */
DECL|method|RangeFieldQuery
name|RangeFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
specifier|final
name|byte
index|[]
name|ranges
parameter_list|,
specifier|final
name|int
name|numDims
parameter_list|,
specifier|final
name|QueryType
name|queryType
parameter_list|)
block|{
name|checkArgs
argument_list|(
name|field
argument_list|,
name|ranges
argument_list|,
name|numDims
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Query type cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|queryType
operator|=
name|queryType
expr_stmt|;
name|this
operator|.
name|numDims
operator|=
name|numDims
expr_stmt|;
name|this
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|this
operator|.
name|bytesPerDim
operator|=
name|ranges
operator|.
name|length
operator|/
operator|(
literal|2
operator|*
name|numDims
operator|)
expr_stmt|;
block|}
comment|/** check input arguments */
DECL|method|checkArgs
specifier|private
specifier|static
name|void
name|checkArgs
parameter_list|(
name|String
name|field
parameter_list|,
specifier|final
name|byte
index|[]
name|ranges
parameter_list|,
specifier|final
name|int
name|numDims
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|numDims
operator|>
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dimension size cannot be greater than 4"
argument_list|)
throw|;
block|}
if|if
condition|(
name|ranges
operator|==
literal|null
operator|||
name|ranges
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"encoded ranges cannot be null or empty"
argument_list|)
throw|;
block|}
block|}
comment|/** Check indexed field info against the provided query data. */
DECL|method|checkFieldInfo
specifier|private
name|void
name|checkFieldInfo
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|/
literal|2
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
operator|/
literal|2
operator|+
literal|" but this query has numDims="
operator|+
name|numDims
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
specifier|final
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|,
name|boost
argument_list|)
block|{
specifier|final
name|RangeFieldComparator
name|target
init|=
operator|new
name|RangeFieldComparator
argument_list|()
decl_stmt|;
specifier|private
name|DocIdSet
name|buildMatchingDocIdSet
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|PointValues
name|values
parameter_list|)
throws|throws
name|IOException
block|{
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
argument_list|,
name|values
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|values
operator|.
name|intersect
argument_list|(
operator|new
name|IntersectVisitor
argument_list|()
block|{
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|adder
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|adder
operator|=
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
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|adder
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|leaf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|.
name|matches
argument_list|(
name|leaf
argument_list|)
condition|)
block|{
name|adder
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
name|byte
index|[]
name|node
init|=
name|getInternalRange
argument_list|(
name|minPackedValue
argument_list|,
name|maxPackedValue
argument_list|)
decl_stmt|;
comment|// compute range relation for BKD traversal
if|if
condition|(
name|target
operator|.
name|intersects
argument_list|(
name|node
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
elseif|else
if|if
condition|(
name|target
operator|.
name|within
argument_list|(
name|node
argument_list|)
condition|)
block|{
comment|// target within cell; continue traversing:
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
elseif|else
if|if
condition|(
name|target
operator|.
name|contains
argument_list|(
name|node
argument_list|)
condition|)
block|{
comment|// target contains cell; add iff queryType is not a CONTAINS or CROSSES query:
return|return
operator|(
name|queryType
operator|==
name|QueryType
operator|.
name|CONTAINS
operator|||
name|queryType
operator|==
name|QueryType
operator|.
name|CROSSES
operator|)
condition|?
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
else|:
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
comment|// target intersects cell; continue traversing:
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|build
argument_list|()
return|;
block|}
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
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// no docs in this segment indexed any ranges
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
comment|// no docs in this segment indexed this field
return|return
literal|null
return|;
block|}
name|checkFieldInfo
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|boolean
name|allDocsMatch
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|getDocCount
argument_list|()
operator|==
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
comment|// if query crosses, docs need to be further scrutinized
name|byte
index|[]
name|range
init|=
name|getInternalRange
argument_list|(
name|values
operator|.
name|getMinPackedValue
argument_list|()
argument_list|,
name|values
operator|.
name|getMaxPackedValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// if the internal node is not equal and not contained by the query, all docs do not match
if|if
condition|(
operator|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|ranges
argument_list|,
name|range
argument_list|)
operator|&&
operator|(
name|target
operator|.
name|contains
argument_list|(
name|range
argument_list|)
operator|&&
name|queryType
operator|!=
name|QueryType
operator|.
name|CONTAINS
operator|)
operator|)
operator|==
literal|false
condition|)
block|{
name|allDocsMatch
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|allDocsMatch
operator|=
literal|false
expr_stmt|;
block|}
name|DocIdSetIterator
name|iterator
init|=
name|allDocsMatch
operator|==
literal|true
condition|?
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
else|:
name|buildMatchingDocIdSet
argument_list|(
name|reader
argument_list|,
name|values
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|iterator
argument_list|)
return|;
block|}
comment|/** get an encoded byte representation of the internal node; this is        *  the lower half of the min array and the upper half of the max array */
specifier|private
name|byte
index|[]
name|getInternalRange
parameter_list|(
name|byte
index|[]
name|min
parameter_list|,
name|byte
index|[]
name|max
parameter_list|)
block|{
name|byte
index|[]
name|range
init|=
operator|new
name|byte
index|[
name|min
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|dimSize
init|=
name|numDims
operator|*
name|bytesPerDim
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|min
argument_list|,
literal|0
argument_list|,
name|range
argument_list|,
literal|0
argument_list|,
name|dimSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|max
argument_list|,
name|dimSize
argument_list|,
name|range
argument_list|,
name|dimSize
argument_list|,
name|dimSize
argument_list|)
expr_stmt|;
return|return
name|range
return|;
block|}
block|}
return|;
block|}
comment|/**    * RangeFieldComparator class provides the core comparison logic for accepting or rejecting indexed    * {@code RangeField} types based on the defined query range and relation.    */
DECL|class|RangeFieldComparator
class|class
name|RangeFieldComparator
block|{
DECL|field|predicate
specifier|final
name|Predicate
argument_list|<
name|byte
index|[]
argument_list|>
name|predicate
decl_stmt|;
comment|/** constructs the comparator based on the query type */
DECL|method|RangeFieldComparator
name|RangeFieldComparator
parameter_list|()
block|{
switch|switch
condition|(
name|queryType
condition|)
block|{
case|case
name|INTERSECTS
case|:
name|predicate
operator|=
name|this
operator|::
name|intersects
expr_stmt|;
break|break;
case|case
name|WITHIN
case|:
name|predicate
operator|=
name|this
operator|::
name|contains
expr_stmt|;
break|break;
case|case
name|CONTAINS
case|:
name|predicate
operator|=
name|this
operator|::
name|within
expr_stmt|;
break|break;
case|case
name|CROSSES
case|:
comment|// crosses first checks intersection (disjoint automatic fails),
comment|// then ensures the query doesn't wholly contain the leaf:
name|predicate
operator|=
parameter_list|(
name|byte
index|[]
name|leaf
parameter_list|)
lambda|->
name|this
operator|.
name|intersects
argument_list|(
name|leaf
argument_list|)
operator|&&
name|this
operator|.
name|contains
argument_list|(
name|leaf
argument_list|)
operator|==
literal|false
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid queryType ["
operator|+
name|queryType
operator|+
literal|"] found."
argument_list|)
throw|;
block|}
block|}
comment|/** determines if the candidate range matches the query request */
DECL|method|matches
specifier|private
name|boolean
name|matches
parameter_list|(
specifier|final
name|byte
index|[]
name|candidate
parameter_list|)
block|{
return|return
operator|(
name|Arrays
operator|.
name|equals
argument_list|(
name|ranges
argument_list|,
name|candidate
argument_list|)
operator|&&
name|queryType
operator|!=
name|QueryType
operator|.
name|CROSSES
operator|)
operator|||
name|predicate
operator|.
name|test
argument_list|(
name|candidate
argument_list|)
return|;
block|}
comment|/** check if query intersects candidate range */
DECL|method|intersects
specifier|private
name|boolean
name|intersects
parameter_list|(
specifier|final
name|byte
index|[]
name|candidate
parameter_list|)
block|{
return|return
name|relate
argument_list|(
parameter_list|(
name|int
name|d
parameter_list|)
lambda|->
name|compareMinMax
argument_list|(
name|candidate
argument_list|,
name|d
argument_list|)
operator|>
literal|0
operator|||
name|compareMaxMin
argument_list|(
name|candidate
argument_list|,
name|d
argument_list|)
operator|<
literal|0
argument_list|)
return|;
block|}
comment|/** check if query is within candidate range */
DECL|method|within
specifier|private
name|boolean
name|within
parameter_list|(
specifier|final
name|byte
index|[]
name|candidate
parameter_list|)
block|{
return|return
name|relate
argument_list|(
parameter_list|(
name|int
name|d
parameter_list|)
lambda|->
name|compareMinMin
argument_list|(
name|candidate
argument_list|,
name|d
argument_list|)
operator|<
literal|0
operator|||
name|compareMaxMax
argument_list|(
name|candidate
argument_list|,
name|d
argument_list|)
operator|>
literal|0
argument_list|)
return|;
block|}
comment|/** check if query contains candidate range */
DECL|method|contains
specifier|private
name|boolean
name|contains
parameter_list|(
specifier|final
name|byte
index|[]
name|candidate
parameter_list|)
block|{
return|return
name|relate
argument_list|(
parameter_list|(
name|int
name|d
parameter_list|)
lambda|->
name|compareMinMin
argument_list|(
name|candidate
argument_list|,
name|d
argument_list|)
operator|>
literal|0
operator|||
name|compareMaxMax
argument_list|(
name|candidate
argument_list|,
name|d
argument_list|)
operator|<
literal|0
argument_list|)
return|;
block|}
comment|/** internal method used by each relation method to test range relation logic */
DECL|method|relate
specifier|private
name|boolean
name|relate
parameter_list|(
name|IntPredicate
name|predicate
parameter_list|)
block|{
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|numDims
condition|;
operator|++
name|d
control|)
block|{
if|if
condition|(
name|predicate
operator|.
name|test
argument_list|(
name|d
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** compare the encoded min value (for the defined query dimension) with the encoded min value in the byte array */
DECL|method|compareMinMin
specifier|private
name|int
name|compareMinMin
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|dimension
parameter_list|)
block|{
comment|// convert dimension to offset:
name|dimension
operator|*=
name|bytesPerDim
expr_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|ranges
argument_list|,
name|dimension
argument_list|,
name|b
argument_list|,
name|dimension
argument_list|)
return|;
block|}
comment|/** compare the encoded min value (for the defined query dimension) with the encoded max value in the byte array */
DECL|method|compareMinMax
specifier|private
name|int
name|compareMinMax
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|dimension
parameter_list|)
block|{
comment|// convert dimension to offset:
name|dimension
operator|*=
name|bytesPerDim
expr_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|ranges
argument_list|,
name|dimension
argument_list|,
name|b
argument_list|,
name|numDims
operator|*
name|bytesPerDim
operator|+
name|dimension
argument_list|)
return|;
block|}
comment|/** compare the encoded max value (for the defined query dimension) with the encoded min value in the byte array */
DECL|method|compareMaxMin
specifier|private
name|int
name|compareMaxMin
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|dimension
parameter_list|)
block|{
comment|// convert dimension to offset:
name|dimension
operator|*=
name|bytesPerDim
expr_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|ranges
argument_list|,
name|numDims
operator|*
name|bytesPerDim
operator|+
name|dimension
argument_list|,
name|b
argument_list|,
name|dimension
argument_list|)
return|;
block|}
comment|/** compare the encoded max value (for the defined query dimension) with the encoded max value in the byte array */
DECL|method|compareMaxMax
specifier|private
name|int
name|compareMaxMax
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|dimension
parameter_list|)
block|{
comment|// convert dimension to max offset:
name|dimension
operator|=
name|numDims
operator|*
name|bytesPerDim
operator|+
name|dimension
operator|*
name|bytesPerDim
expr_stmt|;
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|ranges
argument_list|,
name|dimension
argument_list|,
name|b
argument_list|,
name|dimension
argument_list|)
return|;
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
name|classHash
argument_list|()
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|field
operator|.
name|hashCode
argument_list|()
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
name|queryType
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|ranges
argument_list|)
expr_stmt|;
return|return
name|hash
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
return|return
name|sameClassAs
argument_list|(
name|o
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|o
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|protected
name|boolean
name|equalsTo
parameter_list|(
name|RangeFieldQuery
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|field
argument_list|,
name|other
operator|.
name|field
argument_list|)
operator|&&
name|numDims
operator|==
name|other
operator|.
name|numDims
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|ranges
argument_list|,
name|other
operator|.
name|ranges
argument_list|)
operator|&&
name|other
operator|.
name|queryType
operator|==
name|queryType
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
literal|"<ranges:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|ranges
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|1
init|;
name|d
operator|<
name|numDims
condition|;
operator|++
name|d
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|ranges
argument_list|,
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a string of a single value in a human-readable format for debugging.    * This is used by {@link #toString()}.    *    * @param dimension dimension of the particular value    * @param ranges encoded ranges, never null    * @return human readable value for debugging    */
DECL|method|toString
specifier|protected
specifier|abstract
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|ranges
parameter_list|,
name|int
name|dimension
parameter_list|)
function_decl|;
block|}
end_class

end_unit

