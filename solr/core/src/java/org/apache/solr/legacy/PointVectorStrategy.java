begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.legacy
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|legacy
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
name|document
operator|.
name|DoubleDocValuesField
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
name|Field
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
name|FieldType
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
name|StoredField
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
name|DocValuesType
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
name|IndexOptions
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
name|legacy
operator|.
name|LegacyDoubleField
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
name|legacy
operator|.
name|LegacyFieldType
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
name|legacy
operator|.
name|LegacyNumericRangeQuery
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
name|legacy
operator|.
name|LegacyNumericType
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
name|queries
operator|.
name|function
operator|.
name|FunctionRangeQuery
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|spatial
operator|.
name|SpatialStrategy
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
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
name|spatial
operator|.
name|query
operator|.
name|UnsupportedSpatialOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Circle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_comment
comment|/**  * Simple {@link SpatialStrategy} which represents Points in two numeric fields.  * The Strategy's best feature is decent distance sort.  *  *<p>  *<b>Characteristics:</b>  *<br>  *<ul>  *<li>Only indexes points; just one per field value.</li>  *<li>Can query by a rectangle or circle.</li>  *<li>{@link  * org.apache.lucene.spatial.query.SpatialOperation#Intersects} and {@link  * SpatialOperation#IsWithin} is supported.</li>  *<li>Requires DocValues for  * {@link #makeDistanceValueSource(org.locationtech.spatial4j.shape.Point)} and for  * searching with a Circle.</li>  *</ul>  *  *<p>  *<b>Implementation:</b>  *<p>  * This is a simple Strategy.  Search works with a pair of range queries on two {@link DoublePoint}s representing  * x&amp; y fields.  A Circle query does the same bbox query but adds a  * ValueSource filter on  * {@link #makeDistanceValueSource(org.locationtech.spatial4j.shape.Point)}.  *<p>  * One performance shortcoming with this strategy is that a scenario involving  * both a search using a Circle and sort will result in calculations for the  * spatial distance being done twice -- once for the filter and second for the  * sort.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PointVectorStrategy
specifier|public
class|class
name|PointVectorStrategy
extends|extends
name|SpatialStrategy
block|{
comment|// note: we use a FieldType to articulate the options we want on the field.  We don't use it as-is with a Field, we
comment|//  create more than one Field.
comment|/**    * pointValues, docValues, and nothing else.    */
DECL|field|DEFAULT_FIELDTYPE
specifier|public
specifier|static
name|FieldType
name|DEFAULT_FIELDTYPE
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|LEGACY_FIELDTYPE
specifier|public
specifier|static
name|LegacyFieldType
name|LEGACY_FIELDTYPE
decl_stmt|;
static|static
block|{
comment|// Default: pointValues + docValues
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|type
operator|.
name|setDimensions
argument_list|(
literal|1
argument_list|,
name|Double
operator|.
name|BYTES
argument_list|)
expr_stmt|;
comment|//pointValues (assume Double)
name|type
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
comment|//docValues
name|type
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|DEFAULT_FIELDTYPE
operator|=
name|type
expr_stmt|;
comment|// Legacy default: legacyNumerics
name|LegacyFieldType
name|legacyType
init|=
operator|new
name|LegacyFieldType
argument_list|()
decl_stmt|;
name|legacyType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|legacyType
operator|.
name|setNumericType
argument_list|(
name|LegacyNumericType
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|legacyType
operator|.
name|setNumericPrecisionStep
argument_list|(
literal|8
argument_list|)
expr_stmt|;
comment|// same as solr default
name|legacyType
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|//no docValues!
name|legacyType
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|legacyType
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|LEGACY_FIELDTYPE
operator|=
name|legacyType
expr_stmt|;
block|}
DECL|field|SUFFIX_X
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_X
init|=
literal|"__x"
decl_stmt|;
DECL|field|SUFFIX_Y
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_Y
init|=
literal|"__y"
decl_stmt|;
DECL|field|fieldNameX
specifier|private
specifier|final
name|String
name|fieldNameX
decl_stmt|;
DECL|field|fieldNameY
specifier|private
specifier|final
name|String
name|fieldNameY
decl_stmt|;
DECL|field|fieldsLen
specifier|private
specifier|final
name|int
name|fieldsLen
decl_stmt|;
DECL|field|hasStored
specifier|private
specifier|final
name|boolean
name|hasStored
decl_stmt|;
DECL|field|hasDocVals
specifier|private
specifier|final
name|boolean
name|hasDocVals
decl_stmt|;
DECL|field|hasPointVals
specifier|private
specifier|final
name|boolean
name|hasPointVals
decl_stmt|;
comment|// equiv to "hasLegacyNumerics":
DECL|field|legacyNumericFieldType
specifier|private
specifier|final
name|LegacyFieldType
name|legacyNumericFieldType
decl_stmt|;
comment|// not stored; holds precision step.
comment|/**    * Create a new {@link PointVectorStrategy} instance that uses {@link DoublePoint} and {@link DoublePoint#newRangeQuery}    */
DECL|method|newInstance
specifier|public
specifier|static
name|PointVectorStrategy
name|newInstance
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldNamePrefix
parameter_list|)
block|{
return|return
operator|new
name|PointVectorStrategy
argument_list|(
name|ctx
argument_list|,
name|fieldNamePrefix
argument_list|,
name|DEFAULT_FIELDTYPE
argument_list|)
return|;
block|}
comment|/**    * Create a new {@link PointVectorStrategy} instance that uses {@link LegacyDoubleField} for backwards compatibility.    * However, back-compat is limited; we don't support circle queries or {@link #makeDistanceValueSource(Point, double)}    * since that requires docValues (the legacy config didn't have that).    *    * @deprecated LegacyNumerics will be removed    */
annotation|@
name|Deprecated
DECL|method|newLegacyInstance
specifier|public
specifier|static
name|PointVectorStrategy
name|newLegacyInstance
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldNamePrefix
parameter_list|)
block|{
return|return
operator|new
name|PointVectorStrategy
argument_list|(
name|ctx
argument_list|,
name|fieldNamePrefix
argument_list|,
name|LEGACY_FIELDTYPE
argument_list|)
return|;
block|}
comment|/**    * Create a new instance configured with the provided FieldType options. See {@link #DEFAULT_FIELDTYPE}.    * a field type is used to articulate the desired options (namely pointValues, docValues, stored).  Legacy numerics    * is configurable this way too.    */
DECL|method|PointVectorStrategy
specifier|public
name|PointVectorStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldNamePrefix
parameter_list|,
name|FieldType
name|fieldType
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|fieldNamePrefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldNameX
operator|=
name|fieldNamePrefix
operator|+
name|SUFFIX_X
expr_stmt|;
name|this
operator|.
name|fieldNameY
operator|=
name|fieldNamePrefix
operator|+
name|SUFFIX_Y
expr_stmt|;
name|int
name|numPairs
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|this
operator|.
name|hasStored
operator|=
name|fieldType
operator|.
name|stored
argument_list|()
operator|)
condition|)
block|{
name|numPairs
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|this
operator|.
name|hasDocVals
operator|=
name|fieldType
operator|.
name|docValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|)
condition|)
block|{
name|numPairs
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|this
operator|.
name|hasPointVals
operator|=
name|fieldType
operator|.
name|pointDimensionCount
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|numPairs
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
name|fieldType
operator|instanceof
name|LegacyFieldType
operator|&&
operator|(
operator|(
name|LegacyFieldType
operator|)
name|fieldType
operator|)
operator|.
name|numericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|hasPointVals
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"pointValues and LegacyNumericType are mutually exclusive"
argument_list|)
throw|;
block|}
specifier|final
name|LegacyFieldType
name|legacyType
init|=
operator|(
name|LegacyFieldType
operator|)
name|fieldType
decl_stmt|;
if|if
condition|(
name|legacyType
operator|.
name|numericType
argument_list|()
operator|!=
name|LegacyNumericType
operator|.
name|DOUBLE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|getClass
argument_list|()
operator|+
literal|" does not support "
operator|+
name|legacyType
operator|.
name|numericType
argument_list|()
argument_list|)
throw|;
block|}
name|numPairs
operator|++
expr_stmt|;
name|legacyNumericFieldType
operator|=
operator|new
name|LegacyFieldType
argument_list|(
name|LegacyDoubleField
operator|.
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|legacyNumericFieldType
operator|.
name|setNumericPrecisionStep
argument_list|(
name|legacyType
operator|.
name|numericPrecisionStep
argument_list|()
argument_list|)
expr_stmt|;
name|legacyNumericFieldType
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|legacyNumericFieldType
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|fieldsLen
operator|=
name|numPairs
operator|*
literal|2
expr_stmt|;
block|}
DECL|method|getFieldNameX
name|String
name|getFieldNameX
parameter_list|()
block|{
return|return
name|fieldNameX
return|;
block|}
DECL|method|getFieldNameY
name|String
name|getFieldNameY
parameter_list|()
block|{
return|return
name|fieldNameY
return|;
block|}
annotation|@
name|Override
DECL|method|createIndexableFields
specifier|public
name|Field
index|[]
name|createIndexableFields
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
return|return
name|createIndexableFields
argument_list|(
operator|(
name|Point
operator|)
name|shape
argument_list|)
return|;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Can only index Point, not "
operator|+
name|shape
argument_list|)
throw|;
block|}
comment|/** @see #createIndexableFields(org.locationtech.spatial4j.shape.Shape) */
DECL|method|createIndexableFields
specifier|public
name|Field
index|[]
name|createIndexableFields
parameter_list|(
name|Point
name|point
parameter_list|)
block|{
name|Field
index|[]
name|fields
init|=
operator|new
name|Field
index|[
name|fieldsLen
index|]
decl_stmt|;
name|int
name|idx
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|hasStored
condition|)
block|{
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|StoredField
argument_list|(
name|fieldNameX
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|StoredField
argument_list|(
name|fieldNameY
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasDocVals
condition|)
block|{
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
name|fieldNameX
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
name|fieldNameY
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasPointVals
condition|)
block|{
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|DoublePoint
argument_list|(
name|fieldNameX
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|DoublePoint
argument_list|(
name|fieldNameY
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|legacyNumericFieldType
operator|!=
literal|null
condition|)
block|{
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|LegacyDoubleField
argument_list|(
name|fieldNameX
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|,
name|legacyNumericFieldType
argument_list|)
expr_stmt|;
name|fields
index|[
operator|++
name|idx
index|]
operator|=
operator|new
name|LegacyDoubleField
argument_list|(
name|fieldNameY
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|,
name|legacyNumericFieldType
argument_list|)
expr_stmt|;
block|}
assert|assert
name|idx
operator|==
name|fields
operator|.
name|length
operator|-
literal|1
assert|;
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|makeDistanceValueSource
specifier|public
name|ValueSource
name|makeDistanceValueSource
parameter_list|(
name|Point
name|queryPoint
parameter_list|,
name|double
name|multiplier
parameter_list|)
block|{
return|return
operator|new
name|DistanceValueSource
argument_list|(
name|this
argument_list|,
name|queryPoint
argument_list|,
name|multiplier
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeQuery
specifier|public
name|ConstantScoreQuery
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
if|if
condition|(
operator|!
name|SpatialOperation
operator|.
name|is
argument_list|(
name|args
operator|.
name|getOperation
argument_list|()
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|SpatialOperation
operator|.
name|IsWithin
argument_list|)
condition|)
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|args
operator|.
name|getOperation
argument_list|()
argument_list|)
throw|;
name|Shape
name|shape
init|=
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
if|if
condition|(
name|shape
operator|instanceof
name|Rectangle
condition|)
block|{
name|Rectangle
name|bbox
init|=
operator|(
name|Rectangle
operator|)
name|shape
decl_stmt|;
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|makeWithin
argument_list|(
name|bbox
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|shape
operator|instanceof
name|Circle
condition|)
block|{
name|Circle
name|circle
init|=
operator|(
name|Circle
operator|)
name|shape
decl_stmt|;
name|Rectangle
name|bbox
init|=
name|circle
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|Query
name|approxQuery
init|=
name|makeWithin
argument_list|(
name|bbox
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bqBuilder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|FunctionRangeQuery
name|vsRangeQuery
init|=
operator|new
name|FunctionRangeQuery
argument_list|(
name|makeDistanceValueSource
argument_list|(
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
argument_list|,
literal|0.0
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|bqBuilder
operator|.
name|add
argument_list|(
name|approxQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
comment|//should have lowest "cost" value; will drive iteration
name|bqBuilder
operator|.
name|add
argument_list|(
name|vsRangeQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|bqBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Only Rectangles and Circles are currently supported, "
operator|+
literal|"found ["
operator|+
name|shape
operator|.
name|getClass
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
comment|//TODO
block|}
block|}
comment|/**    * Constructs a query to retrieve documents that fully contain the input envelope.    */
DECL|method|makeWithin
specifier|private
name|Query
name|makeWithin
parameter_list|(
name|Rectangle
name|bbox
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|MUST
init|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
decl_stmt|;
if|if
condition|(
name|bbox
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
comment|//use null as performance trick since no data will be beyond the world bounds
name|bq
operator|.
name|add
argument_list|(
name|rangeQuery
argument_list|(
name|fieldNameX
argument_list|,
literal|null
comment|/*-180*/
argument_list|,
name|bbox
operator|.
name|getMaxX
argument_list|()
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|rangeQuery
argument_list|(
name|fieldNameX
argument_list|,
name|bbox
operator|.
name|getMinX
argument_list|()
argument_list|,
literal|null
comment|/*+180*/
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|//must match at least one of the SHOULD
block|}
else|else
block|{
name|bq
operator|.
name|add
argument_list|(
name|rangeQuery
argument_list|(
name|fieldNameX
argument_list|,
name|bbox
operator|.
name|getMinX
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxX
argument_list|()
argument_list|)
argument_list|,
name|MUST
argument_list|)
expr_stmt|;
block|}
name|bq
operator|.
name|add
argument_list|(
name|rangeQuery
argument_list|(
name|fieldNameY
argument_list|,
name|bbox
operator|.
name|getMinY
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxY
argument_list|()
argument_list|)
argument_list|,
name|MUST
argument_list|)
expr_stmt|;
return|return
name|bq
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a numeric range query based on FieldType    * {@link LegacyNumericRangeQuery} is used for indexes created using {@code FieldType.LegacyNumericType}    * {@link DoublePoint#newRangeQuery} is used for indexes created using {@link DoublePoint} fields    */
DECL|method|rangeQuery
specifier|private
name|Query
name|rangeQuery
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Double
name|min
parameter_list|,
name|Double
name|max
parameter_list|)
block|{
if|if
condition|(
name|hasPointVals
condition|)
block|{
if|if
condition|(
name|min
operator|==
literal|null
condition|)
block|{
name|min
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
if|if
condition|(
name|max
operator|==
literal|null
condition|)
block|{
name|max
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
return|return
name|DoublePoint
operator|.
name|newRangeQuery
argument_list|(
name|fieldName
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|legacyNumericFieldType
operator|!=
literal|null
condition|)
block|{
comment|// todo remove legacy numeric support in 7.0
return|return
name|LegacyNumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|fieldName
argument_list|,
name|legacyNumericFieldType
operator|.
name|numericPrecisionStep
argument_list|()
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
comment|//inclusive
block|}
comment|//TODO try doc-value range query?
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"An index is required for this operation."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

