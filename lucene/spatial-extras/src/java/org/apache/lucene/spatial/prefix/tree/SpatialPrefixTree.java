begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
package|;
end_package

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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A spatial Prefix Tree, or Trie, which decomposes shapes into prefixed strings  * at variable lengths corresponding to variable precision.  Each string  * corresponds to a rectangular spatial region.  This approach is  * also referred to "Grids", "Tiles", and "Spatial Tiers".  *<p>  * Implementations of this class should be thread-safe and immutable once  * initialized.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialPrefixTree
specifier|public
specifier|abstract
class|class
name|SpatialPrefixTree
block|{
DECL|field|maxLevels
specifier|protected
specifier|final
name|int
name|maxLevels
decl_stmt|;
DECL|field|ctx
specifier|protected
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|method|SpatialPrefixTree
specifier|public
name|SpatialPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
assert|assert
name|maxLevels
operator|>
literal|0
assert|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|maxLevels
operator|=
name|maxLevels
expr_stmt|;
block|}
DECL|method|getSpatialContext
specifier|public
name|SpatialContext
name|getSpatialContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
DECL|method|getMaxLevels
specifier|public
name|int
name|getMaxLevels
parameter_list|()
block|{
return|return
name|maxLevels
return|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(maxLevels:"
operator|+
name|maxLevels
operator|+
literal|",ctx:"
operator|+
name|ctx
operator|+
literal|")"
return|;
block|}
comment|/**    * Returns the level of the largest grid in which its longest side is less    * than or equal to the provided distance (in degrees). Consequently {@code    * dist} acts as an error epsilon declaring the amount of detail needed in the    * grid, such that you can get a grid with just the right amount of    * precision.    *    * @param dist {@code>= 0}    * @return level [1 to maxLevels]    */
DECL|method|getLevelForDistance
specifier|public
specifier|abstract
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|dist
parameter_list|)
function_decl|;
comment|/**    * Given a cell having the specified level, returns the distance from opposite    * corners. Since this might vary depending on where the cell is, this method    * may over-estimate.    *    * @param level [1 to maxLevels]    * @return {@code> 0}    */
DECL|method|getDistanceForLevel
specifier|public
specifier|abstract
name|double
name|getDistanceForLevel
parameter_list|(
name|int
name|level
parameter_list|)
function_decl|;
comment|/**    * Returns the level 0 cell which encompasses all spatial data. Equivalent to {@link #readCell(BytesRef,Cell)}    * with no bytes.    */
DECL|method|getWorldCell
specifier|public
specifier|abstract
name|Cell
name|getWorldCell
parameter_list|()
function_decl|;
comment|//another possible name: getTopCell
comment|/**    * This creates a new Cell (or re-using {@code scratch} if provided), initialized to the state as read    * by the bytes.    * Warning: An implementation may refer to the same byte array (no copy). If {@link Cell#setLeaf()} is    * subsequently called, it would then modify these bytes.    */
DECL|method|readCell
specifier|public
specifier|abstract
name|Cell
name|readCell
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|Cell
name|scratch
parameter_list|)
function_decl|;
comment|/**    * Gets the intersecting cells for the specified shape, without exceeding    * detail level. If a cell is within the query shape then it's marked as a    * leaf and none of its children are added. For cells at detailLevel, they are marked as    * leaves too, unless it's a point.    *<p>    * IMPORTANT: Cells returned from the iterator can be re-used for cells at the same level. So you can't simply    * iterate to subsequent cells and still refer to the former cell nor the bytes returned from the former cell, unless    * you know the former cell is a parent.    *    * @param shape       the shape; possibly null but the caller should liberally call    *  {@code remove()} if so.    * @param detailLevel the maximum detail level to get cells for    * @return the matching cells    */
DECL|method|getTreeCellIterator
specifier|public
name|CellIterator
name|getTreeCellIterator
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
if|if
condition|(
name|detailLevel
operator|>
name|maxLevels
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"detailLevel> maxLevels"
argument_list|)
throw|;
block|}
return|return
operator|new
name|TreeCellIterator
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|getWorldCell
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
