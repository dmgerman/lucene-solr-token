begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|io
operator|.
name|GeohashUtils
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A {@link SpatialPrefixTree} based on  *<a href="http://en.wikipedia.org/wiki/Geohash">Geohashes</a>.  * Uses {@link GeohashUtils} to do all the geohash work.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeohashPrefixTree
specifier|public
class|class
name|GeohashPrefixTree
extends|extends
name|LegacyPrefixTree
block|{
comment|/**    * Factory for creating {@link GeohashPrefixTree} instances with useful defaults    */
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|SpatialPrefixTreeFactory
block|{
annotation|@
name|Override
DECL|method|getLevelForDistance
specifier|protected
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|degrees
parameter_list|)
block|{
name|GeohashPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newSPT
specifier|protected
name|SpatialPrefixTree
name|newSPT
parameter_list|()
block|{
return|return
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
operator|!=
literal|null
condition|?
name|maxLevels
else|:
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|GeohashPrefixTree
specifier|public
name|GeohashPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
name|Rectangle
name|bounds
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|getMinX
argument_list|()
operator|!=
operator|-
literal|180
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Geohash only supports lat-lon world bounds. Got "
operator|+
name|bounds
argument_list|)
throw|;
name|int
name|MAXP
init|=
name|getMaxLevelsPossible
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxLevels
operator|<=
literal|0
operator|||
name|maxLevels
operator|>
name|MAXP
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxLen must be [1-"
operator|+
name|MAXP
operator|+
literal|"] but got "
operator|+
name|maxLevels
argument_list|)
throw|;
block|}
comment|/** Any more than this and there's no point (double lat and lon are the same). */
DECL|method|getMaxLevelsPossible
specifier|public
specifier|static
name|int
name|getMaxLevelsPossible
parameter_list|()
block|{
return|return
name|GeohashUtils
operator|.
name|MAX_PRECISION
return|;
block|}
annotation|@
name|Override
DECL|method|getWorldCell
specifier|public
name|Cell
name|getWorldCell
parameter_list|()
block|{
return|return
operator|new
name|GhCell
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLevelForDistance
specifier|public
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|dist
parameter_list|)
block|{
if|if
condition|(
name|dist
operator|==
literal|0
condition|)
return|return
name|maxLevels
return|;
comment|//short circuit
specifier|final
name|int
name|level
init|=
name|GeohashUtils
operator|.
name|lookupHashLenForWidthHeight
argument_list|(
name|dist
argument_list|,
name|dist
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|level
argument_list|,
name|maxLevels
argument_list|)
argument_list|,
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCell
specifier|protected
name|Cell
name|getCell
parameter_list|(
name|Point
name|p
parameter_list|,
name|int
name|level
parameter_list|)
block|{
return|return
operator|new
name|GhCell
argument_list|(
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|p
operator|.
name|getY
argument_list|()
argument_list|,
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|level
argument_list|)
argument_list|)
return|;
comment|//args are lat,lon (y,x)
block|}
DECL|method|stringToBytesPlus1
specifier|private
specifier|static
name|byte
index|[]
name|stringToBytesPlus1
parameter_list|(
name|String
name|token
parameter_list|)
block|{
comment|//copy ASCII token to byte array with one extra spot for eventual LEAF_BYTE if needed
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|token
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|token
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|token
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
DECL|class|GhCell
specifier|private
class|class
name|GhCell
extends|extends
name|LegacyCell
block|{
DECL|field|geohash
specifier|private
name|String
name|geohash
decl_stmt|;
comment|//cache; never has leaf byte, simply a geohash
DECL|method|GhCell
name|GhCell
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|super
argument_list|(
name|stringToBytesPlus1
argument_list|(
name|geohash
argument_list|)
argument_list|,
literal|0
argument_list|,
name|geohash
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|geohash
operator|=
name|geohash
expr_stmt|;
if|if
condition|(
name|isLeaf
argument_list|()
condition|)
name|this
operator|.
name|geohash
operator|=
name|geohash
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|geohash
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|GhCell
name|GhCell
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|super
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGrid
specifier|protected
name|GeohashPrefixTree
name|getGrid
parameter_list|()
block|{
return|return
name|GeohashPrefixTree
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|readCell
specifier|protected
name|void
name|readCell
parameter_list|(
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|super
operator|.
name|readCell
argument_list|(
name|bytesRef
argument_list|)
expr_stmt|;
name|geohash
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubCells
specifier|public
name|Collection
argument_list|<
name|Cell
argument_list|>
name|getSubCells
parameter_list|()
block|{
name|String
index|[]
name|hashes
init|=
name|GeohashUtils
operator|.
name|getSubGeohashes
argument_list|(
name|getGeohash
argument_list|()
argument_list|)
decl_stmt|;
comment|//sorted
name|List
argument_list|<
name|Cell
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|hashes
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|hash
range|:
name|hashes
control|)
block|{
name|cells
operator|.
name|add
argument_list|(
operator|new
name|GhCell
argument_list|(
name|hash
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cells
return|;
block|}
annotation|@
name|Override
DECL|method|getSubCellsSize
specifier|public
name|int
name|getSubCellsSize
parameter_list|()
block|{
return|return
literal|32
return|;
comment|//8x4
block|}
annotation|@
name|Override
DECL|method|getSubCell
specifier|protected
name|GhCell
name|getSubCell
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
return|return
operator|(
name|GhCell
operator|)
name|getGrid
argument_list|()
operator|.
name|getCell
argument_list|(
name|p
argument_list|,
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|)
return|;
comment|//not performant!
block|}
annotation|@
name|Override
DECL|method|getShape
specifier|public
name|Shape
name|getShape
parameter_list|()
block|{
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
name|shape
operator|=
name|GeohashUtils
operator|.
name|decodeBoundary
argument_list|(
name|getGeohash
argument_list|()
argument_list|,
name|getGrid
argument_list|()
operator|.
name|getSpatialContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|shape
return|;
block|}
DECL|method|getGeohash
specifier|private
name|String
name|getGeohash
parameter_list|()
block|{
if|if
condition|(
name|geohash
operator|==
literal|null
condition|)
name|geohash
operator|=
name|getTokenBytesNoLeaf
argument_list|(
literal|null
argument_list|)
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
return|return
name|geohash
return|;
block|}
block|}
comment|//class GhCell
block|}
end_class

end_unit

