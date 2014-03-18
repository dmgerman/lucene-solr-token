begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
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
name|shape
operator|.
name|Shape
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
name|SpatialRelation
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
name|AtomicReaderContext
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|prefix
operator|.
name|tree
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
name|util
operator|.
name|Bits
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
name|FixedBitSet
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

begin_comment
comment|/**  * A Filter matching documents that have an {@link SpatialRelation#INTERSECTS}  * (i.e. not DISTINCT) relationship with a provided query shape.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|IntersectsPrefixTreeFilter
specifier|public
class|class
name|IntersectsPrefixTreeFilter
extends|extends
name|AbstractVisitingPrefixTreeFilter
block|{
DECL|field|hasIndexedLeaves
specifier|private
specifier|final
name|boolean
name|hasIndexedLeaves
decl_stmt|;
DECL|method|IntersectsPrefixTreeFilter
specifier|public
name|IntersectsPrefixTreeFilter
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|int
name|prefixGridScanLevel
parameter_list|,
name|boolean
name|hasIndexedLeaves
parameter_list|)
block|{
name|super
argument_list|(
name|queryShape
argument_list|,
name|fieldName
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasIndexedLeaves
operator|=
name|hasIndexedLeaves
expr_stmt|;
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
name|hasIndexedLeaves
operator|==
operator|(
operator|(
name|IntersectsPrefixTreeFilter
operator|)
name|o
operator|)
operator|.
name|hasIndexedLeaves
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VisitorTemplate
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|hasIndexedLeaves
argument_list|)
block|{
specifier|private
name|FixedBitSet
name|results
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|start
parameter_list|()
block|{
name|results
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocIdSet
name|finish
parameter_list|()
block|{
return|return
name|results
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|visit
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cell
operator|.
name|getShapeRel
argument_list|()
operator|==
name|SpatialRelation
operator|.
name|WITHIN
operator|||
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|collectDocs
argument_list|(
name|results
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitLeaf
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
name|collectDocs
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitScanned
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|queryShape
operator|.
name|relate
argument_list|(
name|cell
operator|.
name|getShape
argument_list|()
argument_list|)
operator|.
name|intersects
argument_list|()
condition|)
name|collectDocs
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|getDocIdSet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

