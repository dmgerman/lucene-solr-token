begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|query
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

begin_comment
comment|/**  * Principally holds the query {@link Shape} and the {@link SpatialOperation}.  * It's used as an argument to some methods on {@link org.apache.lucene.spatial.SpatialStrategy}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialArgs
specifier|public
class|class
name|SpatialArgs
block|{
DECL|field|DEFAULT_DISTERRPCT
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_DISTERRPCT
init|=
literal|0.025d
decl_stmt|;
DECL|field|operation
specifier|private
name|SpatialOperation
name|operation
decl_stmt|;
DECL|field|shape
specifier|private
name|Shape
name|shape
decl_stmt|;
DECL|field|distErrPct
specifier|private
name|Double
name|distErrPct
decl_stmt|;
DECL|field|distErr
specifier|private
name|Double
name|distErr
decl_stmt|;
DECL|method|SpatialArgs
specifier|public
name|SpatialArgs
parameter_list|(
name|SpatialOperation
name|operation
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|operation
operator|==
literal|null
operator|||
name|shape
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"operation and shape are required"
argument_list|)
throw|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
block|}
comment|/**    * Computes the distance given a shape and the {@code distErrPct}.  The    * algorithm is the fraction of the distance from the center of the query    * shape to its closest bounding box corner.    *    * @param shape Mandatory.    * @param distErrPct 0 to 0.5    * @param ctx Mandatory    * @return A distance (in degrees).    */
DECL|method|calcDistanceFromErrPct
specifier|public
specifier|static
name|double
name|calcDistanceFromErrPct
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|double
name|distErrPct
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
if|if
condition|(
name|distErrPct
argument_list|<
literal|0
operator|||
name|distErrPct
argument_list|>
literal|0.5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"distErrPct "
operator|+
name|distErrPct
operator|+
literal|" must be between [0 to 0.5]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|distErrPct
operator|==
literal|0
operator|||
name|shape
operator|instanceof
name|Point
condition|)
block|{
return|return
literal|0
return|;
block|}
name|Rectangle
name|bbox
init|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
comment|//Compute the distance from the center to a corner.  Because the distance
comment|// to a bottom corner vs a top corner can vary in a geospatial scenario,
comment|// take the closest one (greater precision).
name|Point
name|ctr
init|=
name|bbox
operator|.
name|getCenter
argument_list|()
decl_stmt|;
name|double
name|y
init|=
operator|(
name|ctr
operator|.
name|getY
argument_list|()
operator|>=
literal|0
condition|?
name|bbox
operator|.
name|getMaxY
argument_list|()
else|:
name|bbox
operator|.
name|getMinY
argument_list|()
operator|)
decl_stmt|;
name|double
name|diagonalDist
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|ctr
argument_list|,
name|bbox
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|y
argument_list|)
decl_stmt|;
return|return
name|diagonalDist
operator|*
name|distErrPct
return|;
block|}
comment|/**    * Gets the error distance that specifies how precise the query shape is. This    * looks at {@link #getDistErr()}, {@link #getDistErrPct()}, and {@code    * defaultDistErrPct}.    * @param defaultDistErrPct 0 to 0.5    * @return>= 0    */
DECL|method|resolveDistErr
specifier|public
name|double
name|resolveDistErr
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|double
name|defaultDistErrPct
parameter_list|)
block|{
if|if
condition|(
name|distErr
operator|!=
literal|null
condition|)
return|return
name|distErr
return|;
name|double
name|distErrPct
init|=
operator|(
name|this
operator|.
name|distErrPct
operator|!=
literal|null
condition|?
name|this
operator|.
name|distErrPct
else|:
name|defaultDistErrPct
operator|)
decl_stmt|;
return|return
name|calcDistanceFromErrPct
argument_list|(
name|shape
argument_list|,
name|distErrPct
argument_list|,
name|ctx
argument_list|)
return|;
block|}
comment|/** Check if the arguments make sense -- throw an exception if not */
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|operation
operator|.
name|isTargetNeedsArea
argument_list|()
operator|&&
operator|!
name|shape
operator|.
name|hasArea
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|operation
operator|+
literal|" only supports geometry with area"
argument_list|)
throw|;
block|}
if|if
condition|(
name|distErr
operator|!=
literal|null
operator|&&
name|distErrPct
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only distErr or distErrPct can be specified."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|SpatialArgsParser
operator|.
name|writeSpatialArgs
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|//------------------------------------------------
comment|// Getters& Setters
comment|//------------------------------------------------
DECL|method|getOperation
specifier|public
name|SpatialOperation
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
DECL|method|setOperation
specifier|public
name|void
name|setOperation
parameter_list|(
name|SpatialOperation
name|operation
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
DECL|method|getShape
specifier|public
name|Shape
name|getShape
parameter_list|()
block|{
return|return
name|shape
return|;
block|}
DECL|method|setShape
specifier|public
name|void
name|setShape
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
block|}
comment|/**    * A measure of acceptable error of the shape as a fraction.  This effectively    * inflates the size of the shape but should not shrink it.    *    * @return 0 to 0.5    * @see #calcDistanceFromErrPct(com.spatial4j.core.shape.Shape, double,    *      com.spatial4j.core.context.SpatialContext)    */
DECL|method|getDistErrPct
specifier|public
name|Double
name|getDistErrPct
parameter_list|()
block|{
return|return
name|distErrPct
return|;
block|}
DECL|method|setDistErrPct
specifier|public
name|void
name|setDistErrPct
parameter_list|(
name|Double
name|distErrPct
parameter_list|)
block|{
if|if
condition|(
name|distErrPct
operator|!=
literal|null
condition|)
name|this
operator|.
name|distErrPct
operator|=
name|distErrPct
expr_stmt|;
block|}
comment|/**    * The acceptable error of the shape.  This effectively inflates the    * size of the shape but should not shrink it.    *    * @return>= 0    */
DECL|method|getDistErr
specifier|public
name|Double
name|getDistErr
parameter_list|()
block|{
return|return
name|distErr
return|;
block|}
DECL|method|setDistErr
specifier|public
name|void
name|setDistErr
parameter_list|(
name|Double
name|distErr
parameter_list|)
block|{
name|this
operator|.
name|distErr
operator|=
name|distErr
expr_stmt|;
block|}
block|}
end_class

end_unit

