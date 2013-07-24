begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.vector
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|vector
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Circle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
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
name|DoubleField
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|Filter
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
name|FilteredQuery
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
name|MatchAllDocsQuery
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
name|NumericRangeQuery
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
name|QueryWrapperFilter
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|CachingDoubleValueSource
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
name|util
operator|.
name|ValueSourceFilter
import|;
end_import

begin_comment
comment|/**  * Simple {@link SpatialStrategy} which represents Points in two numeric {@link  * DoubleField}s.  The Strategy's best feature is decent distance sort.  *  *<h4>Characteristics:</h4>  *<ul>  *<li>Only indexes points; just one per field value.</li>  *<li>Can query by a rectangle or circle.</li>  *<li>{@link  * org.apache.lucene.spatial.query.SpatialOperation#Intersects} and {@link  * SpatialOperation#IsWithin} is supported.</li>  *<li>Uses the FieldCache for  * {@link #makeDistanceValueSource(com.spatial4j.core.shape.Point)} and for  * searching with a Circle.</li>  *</ul>  *  *<h4>Implementation:</h4>  * This is a simple Strategy.  Search works with {@link NumericRangeQuery}s on  * an x& y pair of fields.  A Circle query does the same bbox query but adds a  * ValueSource filter on  * {@link #makeDistanceValueSource(com.spatial4j.core.shape.Point)}.  *<p />  * One performance shortcoming with this strategy is that a scenario involving  * both a search using a Circle and sort will result in calculations for the  * spatial distance being done twice -- once for the filter and second for the  * sort.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PointVectorStrategy
specifier|public
class|class
name|PointVectorStrategy
extends|extends
name|SpatialStrategy
block|{
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
DECL|field|precisionStep
specifier|public
name|int
name|precisionStep
init|=
literal|8
decl_stmt|;
comment|// same as solr default
DECL|method|PointVectorStrategy
specifier|public
name|PointVectorStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldNamePrefix
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
block|}
DECL|method|setPrecisionStep
specifier|public
name|void
name|setPrecisionStep
parameter_list|(
name|int
name|p
parameter_list|)
block|{
name|precisionStep
operator|=
name|p
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<=
literal|0
operator|||
name|precisionStep
operator|>=
literal|64
condition|)
name|precisionStep
operator|=
name|Integer
operator|.
name|MAX_VALUE
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
comment|/** @see #createIndexableFields(com.spatial4j.core.shape.Shape) */
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
name|FieldType
name|doubleFieldType
init|=
operator|new
name|FieldType
argument_list|(
name|DoubleField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|doubleFieldType
operator|.
name|setNumericPrecisionStep
argument_list|(
name|precisionStep
argument_list|)
expr_stmt|;
name|Field
index|[]
name|f
init|=
operator|new
name|Field
index|[
literal|2
index|]
decl_stmt|;
name|f
index|[
literal|0
index|]
operator|=
operator|new
name|DoubleField
argument_list|(
name|fieldNameX
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|,
name|doubleFieldType
argument_list|)
expr_stmt|;
name|f
index|[
literal|1
index|]
operator|=
operator|new
name|DoubleField
argument_list|(
name|fieldNameY
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|,
name|doubleFieldType
argument_list|)
expr_stmt|;
return|return
name|f
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
DECL|method|makeFilter
specifier|public
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
comment|//unwrap the CSQ from makeQuery
name|ConstantScoreQuery
name|csq
init|=
name|makeQuery
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
name|csq
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
return|return
name|filter
return|;
else|else
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
name|csq
operator|.
name|getQuery
argument_list|()
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
name|ValueSourceFilter
name|vsf
init|=
operator|new
name|ValueSourceFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|makeWithin
argument_list|(
name|bbox
argument_list|)
argument_list|)
argument_list|,
name|makeDistanceValueSource
argument_list|(
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|vsf
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
comment|//TODO this is basically old code that hasn't been verified well and should probably be removed
DECL|method|makeQueryDistanceScore
specifier|public
name|Query
name|makeQueryDistanceScore
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
comment|// For starters, just limit the bbox
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
operator|!
operator|(
name|shape
operator|instanceof
name|Rectangle
operator|||
name|shape
operator|instanceof
name|Circle
operator|)
condition|)
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
name|Rectangle
name|bbox
init|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|bbox
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Crossing dateline not yet supported"
argument_list|)
throw|;
block|}
name|ValueSource
name|valueSource
init|=
literal|null
decl_stmt|;
name|Query
name|spatial
init|=
literal|null
decl_stmt|;
name|SpatialOperation
name|op
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|SpatialOperation
operator|.
name|is
argument_list|(
name|op
argument_list|,
name|SpatialOperation
operator|.
name|BBoxWithin
argument_list|,
name|SpatialOperation
operator|.
name|BBoxIntersects
argument_list|)
condition|)
block|{
name|spatial
operator|=
name|makeWithin
argument_list|(
name|bbox
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SpatialOperation
operator|.
name|is
argument_list|(
name|op
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
block|{
name|spatial
operator|=
name|makeWithin
argument_list|(
name|bbox
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getShape
argument_list|()
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
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
comment|// Make the ValueSource
name|valueSource
operator|=
name|makeDistanceValueSource
argument_list|(
name|shape
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|ValueSourceFilter
name|vsf
init|=
operator|new
name|ValueSourceFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|spatial
argument_list|)
argument_list|,
name|valueSource
argument_list|,
literal|0
argument_list|,
name|circle
operator|.
name|getRadius
argument_list|()
argument_list|)
decl_stmt|;
name|spatial
operator|=
operator|new
name|FilteredQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|vsf
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|op
operator|==
name|SpatialOperation
operator|.
name|IsDisjointTo
condition|)
block|{
name|spatial
operator|=
name|makeDisjoint
argument_list|(
name|bbox
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|spatial
operator|==
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|valueSource
operator|!=
literal|null
condition|)
block|{
name|valueSource
operator|=
operator|new
name|CachingDoubleValueSource
argument_list|(
name|valueSource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueSource
operator|=
name|makeDistanceValueSource
argument_list|(
name|shape
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Query
name|spatialRankingQuery
init|=
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|spatial
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|spatialRankingQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
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
name|bq
init|=
operator|new
name|BooleanQuery
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
return|;
block|}
DECL|method|rangeQuery
specifier|private
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
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
return|return
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|fieldName
argument_list|,
name|precisionStep
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
comment|/**    * Constructs a query to retrieve documents that fully contain the input envelope.    */
DECL|method|makeDisjoint
specifier|private
name|Query
name|makeDisjoint
parameter_list|(
name|Rectangle
name|bbox
parameter_list|)
block|{
if|if
condition|(
name|bbox
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"makeDisjoint doesn't handle dateline cross"
argument_list|)
throw|;
name|Query
name|qX
init|=
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
decl_stmt|;
name|Query
name|qY
init|=
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
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|qX
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|qY
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
block|}
end_class

end_unit

