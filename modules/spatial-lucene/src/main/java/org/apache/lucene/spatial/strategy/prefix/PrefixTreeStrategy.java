begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.strategy.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|strategy
operator|.
name|prefix
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|IndexableField
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
name|spatial
operator|.
name|base
operator|.
name|distance
operator|.
name|DistanceCalculator
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
name|base
operator|.
name|prefix
operator|.
name|Node
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
name|base
operator|.
name|prefix
operator|.
name|SpatialPrefixTree
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
name|base
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
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
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
name|spatial
operator|.
name|strategy
operator|.
name|SimpleSpatialFieldInfo
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
name|strategy
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
name|strategy
operator|.
name|util
operator|.
name|CachedDistanceValueSource
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
DECL|class|PrefixTreeStrategy
specifier|public
specifier|abstract
class|class
name|PrefixTreeStrategy
extends|extends
name|SpatialStrategy
argument_list|<
name|SimpleSpatialFieldInfo
argument_list|>
block|{
DECL|field|grid
specifier|protected
specifier|final
name|SpatialPrefixTree
name|grid
decl_stmt|;
DECL|field|provider
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PointPrefixTreeFieldCacheProvider
argument_list|>
name|provider
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|PointPrefixTreeFieldCacheProvider
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|defaultFieldValuesArrayLen
specifier|protected
name|int
name|defaultFieldValuesArrayLen
init|=
literal|2
decl_stmt|;
DECL|field|distErrPct
specifier|protected
name|double
name|distErrPct
init|=
name|SpatialArgs
operator|.
name|DEFAULT_DIST_PRECISION
decl_stmt|;
DECL|method|PrefixTreeStrategy
specifier|public
name|PrefixTreeStrategy
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|)
block|{
name|super
argument_list|(
name|grid
operator|.
name|getSpatialContext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|grid
operator|=
name|grid
expr_stmt|;
block|}
comment|/** Used in the in-memory ValueSource as a default ArrayList length for this field's array of values, per doc. */
DECL|method|setDefaultFieldValuesArrayLen
specifier|public
name|void
name|setDefaultFieldValuesArrayLen
parameter_list|(
name|int
name|defaultFieldValuesArrayLen
parameter_list|)
block|{
name|this
operator|.
name|defaultFieldValuesArrayLen
operator|=
name|defaultFieldValuesArrayLen
expr_stmt|;
block|}
comment|/** See {@link SpatialPrefixTree#getMaxLevelForPrecision(org.apache.lucene.spatial.base.shape.Shape, double)}. */
DECL|method|setDistErrPct
specifier|public
name|void
name|setDistErrPct
parameter_list|(
name|double
name|distErrPct
parameter_list|)
block|{
name|this
operator|.
name|distErrPct
operator|=
name|distErrPct
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createField
specifier|public
name|IndexableField
name|createField
parameter_list|(
name|SimpleSpatialFieldInfo
name|fieldInfo
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|store
parameter_list|)
block|{
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getMaxLevelForPrecision
argument_list|(
name|shape
argument_list|,
name|distErrPct
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|cells
init|=
name|grid
operator|.
name|getNodes
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//true=intermediates cells
comment|//If shape isn't a point, add a full-resolution center-point so that
comment|// PrefixFieldCacheProvider has the center-points.
comment|// TODO index each center of a multi-point? Yes/no?
if|if
condition|(
operator|!
operator|(
name|shape
operator|instanceof
name|Point
operator|)
condition|)
block|{
name|Point
name|ctr
init|=
name|shape
operator|.
name|getCenter
argument_list|()
decl_stmt|;
comment|//TODO should be smarter; don't index 2 tokens for this in CellTokenizer. Harmless though.
name|cells
operator|.
name|add
argument_list|(
name|grid
operator|.
name|getNodes
argument_list|(
name|ctr
argument_list|,
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|fname
init|=
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
condition|)
block|{
comment|//TODO figure out how to re-use original string instead of reconstituting it.
name|String
name|wkt
init|=
name|grid
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
condition|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|fname
argument_list|,
name|wkt
argument_list|,
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|f
operator|.
name|setTokenStream
argument_list|(
operator|new
name|CellTokenStream
argument_list|(
name|cells
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
return|return
operator|new
name|StoredField
argument_list|(
name|fname
argument_list|,
name|wkt
argument_list|)
return|;
block|}
if|if
condition|(
name|index
condition|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|fname
argument_list|,
operator|new
name|CellTokenStream
argument_list|(
name|cells
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|,
name|TYPE_UNSTORED
argument_list|)
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Fields need to be indexed or store ["
operator|+
name|fname
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|/* Indexed, tokenized, not stored. */
DECL|field|TYPE_UNSTORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_UNSTORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
comment|/* Indexed, tokenized, stored. */
DECL|field|TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_UNSTORED
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_UNSTORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_UNSTORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_UNSTORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Outputs the tokenString of a cell, and if its a leaf, outputs it again with the leaf byte. */
DECL|class|CellTokenStream
specifier|final
specifier|static
class|class
name|CellTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|iter
specifier|private
name|Iterator
argument_list|<
name|Node
argument_list|>
name|iter
init|=
literal|null
decl_stmt|;
DECL|method|CellTokenStream
specifier|public
name|CellTokenStream
parameter_list|(
name|Iterator
argument_list|<
name|Node
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|iter
operator|=
name|tokens
expr_stmt|;
block|}
DECL|field|nextTokenStringNeedingLeaf
name|CharSequence
name|nextTokenStringNeedingLeaf
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextTokenStringNeedingLeaf
operator|!=
literal|null
condition|)
block|{
name|termAtt
operator|.
name|append
argument_list|(
name|nextTokenStringNeedingLeaf
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|Node
operator|.
name|LEAF_BYTE
argument_list|)
expr_stmt|;
name|nextTokenStringNeedingLeaf
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|cell
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|CharSequence
name|token
init|=
name|cell
operator|.
name|getTokenString
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|cell
operator|.
name|isLeaf
argument_list|()
condition|)
name|nextTokenStringNeedingLeaf
operator|=
name|token
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|makeValueSource
specifier|public
name|ValueSource
name|makeValueSource
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|SimpleSpatialFieldInfo
name|fieldInfo
parameter_list|)
block|{
name|DistanceCalculator
name|calc
init|=
name|grid
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|getDistCalc
argument_list|()
decl_stmt|;
return|return
name|makeValueSource
argument_list|(
name|args
argument_list|,
name|fieldInfo
argument_list|,
name|calc
argument_list|)
return|;
block|}
DECL|method|makeValueSource
specifier|public
name|ValueSource
name|makeValueSource
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|SimpleSpatialFieldInfo
name|fieldInfo
parameter_list|,
name|DistanceCalculator
name|calc
parameter_list|)
block|{
name|PointPrefixTreeFieldCacheProvider
name|p
init|=
name|provider
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//double checked locking idiom is okay since provider is threadsafe
name|p
operator|=
name|provider
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
operator|new
name|PointPrefixTreeFieldCacheProvider
argument_list|(
name|grid
argument_list|,
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|defaultFieldValuesArrayLen
argument_list|)
expr_stmt|;
name|provider
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Point
name|point
init|=
name|args
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
decl_stmt|;
return|return
operator|new
name|CachedDistanceValueSource
argument_list|(
name|point
argument_list|,
name|calc
argument_list|,
name|p
argument_list|)
return|;
block|}
DECL|method|getGrid
specifier|public
name|SpatialPrefixTree
name|getGrid
parameter_list|()
block|{
return|return
name|grid
return|;
block|}
block|}
end_class

end_unit

