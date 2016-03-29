begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.geopoint.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
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
name|spatial
operator|.
name|geopoint
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
name|GeoRect
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
comment|/** Package private implementation for the public facing GeoPointDistanceQuery delegate class.  *  *    @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointDistanceQueryImpl
specifier|final
class|class
name|GeoPointDistanceQueryImpl
extends|extends
name|GeoPointInBBoxQueryImpl
block|{
DECL|field|distanceQuery
specifier|private
specifier|final
name|GeoPointDistanceQuery
name|distanceQuery
decl_stmt|;
DECL|field|centerLon
specifier|private
specifier|final
name|double
name|centerLon
decl_stmt|;
comment|// optimization, maximum partial haversin needed to be a candidate
DECL|field|maxPartialDistance
specifier|private
specifier|final
name|double
name|maxPartialDistance
decl_stmt|;
comment|// optimization, used for detecting axis cross
DECL|field|axisLat
specifier|final
name|double
name|axisLat
decl_stmt|;
DECL|method|GeoPointDistanceQueryImpl
name|GeoPointDistanceQueryImpl
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|TermEncoding
name|termEncoding
parameter_list|,
specifier|final
name|GeoPointDistanceQuery
name|q
parameter_list|,
specifier|final
name|double
name|centerLonUnwrapped
parameter_list|,
specifier|final
name|GeoRect
name|bbox
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|bbox
operator|.
name|minLat
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|,
name|bbox
operator|.
name|minLon
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|)
expr_stmt|;
name|distanceQuery
operator|=
name|q
expr_stmt|;
name|centerLon
operator|=
name|centerLonUnwrapped
expr_stmt|;
comment|// unless our box is crazy, we can use this bound
comment|// to reject edge cases faster in postFilter()
if|if
condition|(
name|bbox
operator|.
name|maxLon
operator|-
name|centerLon
operator|<
literal|90
operator|&&
name|centerLon
operator|-
name|bbox
operator|.
name|minLon
operator|<
literal|90
condition|)
block|{
name|maxPartialDistance
operator|=
name|Math
operator|.
name|max
argument_list|(
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|)
argument_list|,
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|,
name|centerLon
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maxPartialDistance
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
name|axisLat
operator|=
name|GeoUtils
operator|.
name|axisLat
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|distanceQuery
operator|.
name|radiusMeters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setRewriteMethod
specifier|public
name|void
name|setRewriteMethod
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot change rewrite method"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|newCellComparator
specifier|protected
name|CellComparator
name|newCellComparator
parameter_list|()
block|{
return|return
operator|new
name|GeoPointRadiusCellComparator
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|class|GeoPointRadiusCellComparator
specifier|private
specifier|final
class|class
name|GeoPointRadiusCellComparator
extends|extends
name|CellComparator
block|{
DECL|method|GeoPointRadiusCellComparator
name|GeoPointRadiusCellComparator
parameter_list|(
name|GeoPointDistanceQueryImpl
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cellCrosses
specifier|protected
name|boolean
name|cellCrosses
parameter_list|(
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
comment|// bounding box check
if|if
condition|(
name|maxLat
argument_list|<
name|GeoPointDistanceQueryImpl
operator|.
name|this
operator|.
name|minLat
operator|||
name|maxLon
argument_list|<
name|GeoPointDistanceQueryImpl
operator|.
name|this
operator|.
name|minLon
operator|||
name|minLat
argument_list|>
name|GeoPointDistanceQueryImpl
operator|.
name|this
operator|.
name|maxLat
operator|||
name|minLon
argument_list|>
name|GeoPointDistanceQueryImpl
operator|.
name|this
operator|.
name|maxLon
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|centerLon
argument_list|<
name|minLon
operator|||
name|centerLon
argument_list|>
name|maxLon
operator|)
operator|&&
operator|(
name|axisLat
operator|+
name|GeoUtils
operator|.
name|AXISLAT_ERROR
argument_list|<
name|minLat
operator|||
name|axisLat
operator|-
name|GeoUtils
operator|.
name|AXISLAT_ERROR
argument_list|>
name|maxLat
operator|)
condition|)
block|{
if|if
condition|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|minLat
argument_list|,
name|minLon
argument_list|)
operator|>
name|distanceQuery
operator|.
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|)
operator|>
name|distanceQuery
operator|.
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|)
operator|>
name|distanceQuery
operator|.
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|maxLat
argument_list|,
name|maxLon
argument_list|)
operator|>
name|distanceQuery
operator|.
name|radiusMeters
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
annotation|@
name|Override
DECL|method|cellWithin
specifier|protected
name|boolean
name|cellWithin
parameter_list|(
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
if|if
condition|(
name|maxLon
operator|-
name|centerLon
operator|<
literal|90
operator|&&
name|centerLon
operator|-
name|minLon
operator|<
literal|90
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|minLat
argument_list|,
name|minLon
argument_list|)
operator|<=
name|distanceQuery
operator|.
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|)
operator|<=
name|distanceQuery
operator|.
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|)
operator|<=
name|distanceQuery
operator|.
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|maxLat
argument_list|,
name|maxLon
argument_list|)
operator|<=
name|distanceQuery
operator|.
name|radiusMeters
condition|)
block|{
comment|// we are fully enclosed, collect everything within this subtree
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|cellIntersectsShape
specifier|protected
name|boolean
name|cellIntersectsShape
parameter_list|(
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
return|return
name|cellCrosses
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
return|;
block|}
comment|/**      * The two-phase query approach. The parent {@link GeoPointTermsEnum} class matches      * encoded terms that fall within the minimum bounding box of the point-radius circle. Those documents that pass      * the initial bounding box filter are then post filter compared to the provided distance using the      * {@link org.apache.lucene.util.SloppyMath#haversinMeters(double, double, double, double)} method.      */
annotation|@
name|Override
DECL|method|postFilter
specifier|protected
name|boolean
name|postFilter
parameter_list|(
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|)
block|{
comment|// check bbox
if|if
condition|(
name|lat
argument_list|<
name|minLat
operator|||
name|lat
argument_list|>
name|maxLat
operator|||
name|lon
argument_list|<
name|minLon
operator|||
name|lon
argument_list|>
name|maxLon
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// first check the partial distance, if its more than that, it can't be<= radiusMeters
name|double
name|h1
init|=
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|distanceQuery
operator|.
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
if|if
condition|(
name|h1
operator|>
name|maxPartialDistance
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// fully confirm with part 2:
return|return
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|h1
argument_list|)
operator|<=
name|distanceQuery
operator|.
name|radiusMeters
return|;
block|}
block|}
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
name|GeoPointDistanceQueryImpl
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|GeoPointDistanceQueryImpl
name|that
init|=
operator|(
name|GeoPointDistanceQueryImpl
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|distanceQuery
operator|.
name|equals
argument_list|(
name|that
operator|.
name|distanceQuery
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
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
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|distanceQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getRadiusMeters
specifier|public
name|double
name|getRadiusMeters
parameter_list|()
block|{
return|return
name|distanceQuery
operator|.
name|getRadiusMeters
argument_list|()
return|;
block|}
block|}
end_class

end_unit

