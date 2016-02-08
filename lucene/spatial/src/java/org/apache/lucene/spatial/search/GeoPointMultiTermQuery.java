begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
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
name|Terms
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
name|TermsEnum
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
name|MultiTermQuery
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
name|util
operator|.
name|AttributeSource
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
name|document
operator|.
name|GeoPointField
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
name|document
operator|.
name|GeoPointField
operator|.
name|TermEncoding
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
name|GeoEncodingUtils
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
name|GeoRelationUtils
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
name|GeoUtils
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
name|SloppyMath
import|;
end_import

begin_comment
comment|/**  * TermQuery for GeoPointField for overriding {@link org.apache.lucene.search.MultiTermQuery} methods specific to  * Geospatial operations  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointMultiTermQuery
specifier|abstract
class|class
name|GeoPointMultiTermQuery
extends|extends
name|MultiTermQuery
block|{
comment|// simple bounding box optimization - no objects used to avoid dependencies
DECL|field|minLon
specifier|protected
specifier|final
name|double
name|minLon
decl_stmt|;
DECL|field|minLat
specifier|protected
specifier|final
name|double
name|minLat
decl_stmt|;
DECL|field|maxLon
specifier|protected
specifier|final
name|double
name|maxLon
decl_stmt|;
DECL|field|maxLat
specifier|protected
specifier|final
name|double
name|maxLat
decl_stmt|;
DECL|field|maxShift
specifier|protected
specifier|final
name|short
name|maxShift
decl_stmt|;
DECL|field|termEncoding
specifier|protected
specifier|final
name|TermEncoding
name|termEncoding
decl_stmt|;
DECL|field|cellComparator
specifier|protected
specifier|final
name|CellComparator
name|cellComparator
decl_stmt|;
comment|/**    * Constructs a query matching terms that cannot be represented with a single    * Term.    */
DECL|method|GeoPointMultiTermQuery
specifier|public
name|GeoPointMultiTermQuery
parameter_list|(
name|String
name|field
parameter_list|,
specifier|final
name|TermEncoding
name|termEncoding
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|minLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid minLon "
operator|+
name|minLon
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|maxLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid maxLon "
operator|+
name|maxLon
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|minLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid minLat "
operator|+
name|minLat
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|maxLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid maxLat "
operator|+
name|maxLat
argument_list|)
throw|;
block|}
specifier|final
name|long
name|minHash
init|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|)
decl_stmt|;
specifier|final
name|long
name|maxHash
init|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|maxLon
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
name|this
operator|.
name|minLon
operator|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|minHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|minLat
operator|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|minHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|maxHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|maxHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxShift
operator|=
name|computeMaxShift
argument_list|()
expr_stmt|;
name|this
operator|.
name|termEncoding
operator|=
name|termEncoding
expr_stmt|;
name|this
operator|.
name|cellComparator
operator|=
name|newCellComparator
argument_list|()
expr_stmt|;
name|this
operator|.
name|rewriteMethod
operator|=
name|GEO_CONSTANT_SCORE_REWRITE
expr_stmt|;
block|}
DECL|field|GEO_CONSTANT_SCORE_REWRITE
specifier|public
specifier|static
specifier|final
name|RewriteMethod
name|GEO_CONSTANT_SCORE_REWRITE
init|=
operator|new
name|RewriteMethod
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
block|{
return|return
operator|new
name|GeoPointTermQueryConstantScoreWrapper
argument_list|<>
argument_list|(
operator|(
name|GeoPointMultiTermQuery
operator|)
name|query
argument_list|)
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
specifier|final
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|GeoPointTermsEnum
operator|.
name|newInstance
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**    * Computes the maximum shift based on the diagonal distance of the bounding box    */
DECL|method|computeMaxShift
specifier|protected
name|short
name|computeMaxShift
parameter_list|()
block|{
comment|// in this case a factor of 4 brings the detail level to ~0.002/0.001 degrees lon/lat respectively (or ~222m/111m)
specifier|final
name|short
name|shiftFactor
decl_stmt|;
comment|// compute diagonal distance
name|double
name|midLon
init|=
operator|(
name|minLon
operator|+
name|maxLon
operator|)
operator|*
literal|0.5
decl_stmt|;
name|double
name|midLat
init|=
operator|(
name|minLat
operator|+
name|maxLat
operator|)
operator|*
literal|0.5
decl_stmt|;
if|if
condition|(
name|SloppyMath
operator|.
name|haversin
argument_list|(
name|minLat
argument_list|,
name|minLon
argument_list|,
name|midLat
argument_list|,
name|midLon
argument_list|)
operator|*
literal|1000
operator|>
literal|1000000
condition|)
block|{
name|shiftFactor
operator|=
literal|5
expr_stmt|;
block|}
else|else
block|{
name|shiftFactor
operator|=
literal|4
expr_stmt|;
block|}
return|return
call|(
name|short
call|)
argument_list|(
name|GeoPointField
operator|.
name|PRECISION_STEP
operator|*
name|shiftFactor
argument_list|)
return|;
block|}
comment|/**    * Abstract method to construct the class that handles all geo point relations    * (e.g., GeoPointInPolygon)    */
DECL|method|newCellComparator
specifier|abstract
specifier|protected
name|CellComparator
name|newCellComparator
parameter_list|()
function_decl|;
comment|/**    * Base class for all geo point relation comparators    */
DECL|class|CellComparator
specifier|static
specifier|abstract
class|class
name|CellComparator
block|{
DECL|field|geoPointQuery
specifier|protected
specifier|final
name|GeoPointMultiTermQuery
name|geoPointQuery
decl_stmt|;
DECL|method|CellComparator
name|CellComparator
parameter_list|(
name|GeoPointMultiTermQuery
name|query
parameter_list|)
block|{
name|this
operator|.
name|geoPointQuery
operator|=
name|query
expr_stmt|;
block|}
comment|/**      * Primary driver for cells intersecting shape boundaries      */
DECL|method|cellIntersectsMBR
specifier|protected
name|boolean
name|cellIntersectsMBR
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoRelationUtils
operator|.
name|rectIntersects
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|geoPointQuery
operator|.
name|minLon
argument_list|,
name|geoPointQuery
operator|.
name|minLat
argument_list|,
name|geoPointQuery
operator|.
name|maxLon
argument_list|,
name|geoPointQuery
operator|.
name|maxLat
argument_list|)
return|;
block|}
comment|/**      * Return whether quad-cell contains the bounding box of this shape      */
DECL|method|cellContains
specifier|protected
name|boolean
name|cellContains
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoRelationUtils
operator|.
name|rectWithin
argument_list|(
name|geoPointQuery
operator|.
name|minLon
argument_list|,
name|geoPointQuery
operator|.
name|minLat
argument_list|,
name|geoPointQuery
operator|.
name|maxLon
argument_list|,
name|geoPointQuery
operator|.
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
return|;
block|}
comment|/**      * Determine whether the quad-cell crosses the shape      */
DECL|method|cellCrosses
specifier|abstract
specifier|protected
name|boolean
name|cellCrosses
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
function_decl|;
comment|/**      * Determine whether quad-cell is within the shape      */
DECL|method|cellWithin
specifier|abstract
specifier|protected
name|boolean
name|cellWithin
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
function_decl|;
comment|/**      * Default shape is a rectangle, so this returns the same as {@code cellIntersectsMBR}      */
DECL|method|cellIntersectsShape
specifier|abstract
specifier|protected
name|boolean
name|cellIntersectsShape
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
function_decl|;
DECL|method|postFilter
specifier|abstract
specifier|protected
name|boolean
name|postFilter
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit
