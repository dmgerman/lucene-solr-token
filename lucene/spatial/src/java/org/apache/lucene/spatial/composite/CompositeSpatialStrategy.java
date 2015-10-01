begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.composite
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|composite
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|prefix
operator|.
name|RecursivePrefixTreeStrategy
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
name|serialized
operator|.
name|SerializedDVStrategy
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
name|ShapePredicateValueSource
import|;
end_import

begin_comment
comment|/**  * A composite {@link SpatialStrategy} based on {@link RecursivePrefixTreeStrategy} (RPT) and  * {@link SerializedDVStrategy} (SDV).  * RPT acts as an index to the precision available in SDV, and in some circumstances can avoid geometry lookups based  * on where a cell is in relation to the query shape.  Currently the only predicate optimized like this is Intersects.  * All predicates are supported except for the BBox* ones, and Disjoint.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompositeSpatialStrategy
specifier|public
class|class
name|CompositeSpatialStrategy
extends|extends
name|SpatialStrategy
block|{
comment|//TODO support others? (BBox)
DECL|field|indexStrategy
specifier|private
specifier|final
name|RecursivePrefixTreeStrategy
name|indexStrategy
decl_stmt|;
comment|/** Has the geometry. */
comment|// TODO support others?
DECL|field|geometryStrategy
specifier|private
specifier|final
name|SerializedDVStrategy
name|geometryStrategy
decl_stmt|;
DECL|field|optimizePredicates
specifier|private
name|boolean
name|optimizePredicates
init|=
literal|true
decl_stmt|;
DECL|method|CompositeSpatialStrategy
specifier|public
name|CompositeSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|RecursivePrefixTreeStrategy
name|indexStrategy
parameter_list|,
name|SerializedDVStrategy
name|geometryStrategy
parameter_list|)
block|{
name|super
argument_list|(
name|indexStrategy
operator|.
name|getSpatialContext
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
comment|//field name; unused
name|this
operator|.
name|indexStrategy
operator|=
name|indexStrategy
expr_stmt|;
name|this
operator|.
name|geometryStrategy
operator|=
name|geometryStrategy
expr_stmt|;
block|}
DECL|method|getIndexStrategy
specifier|public
name|RecursivePrefixTreeStrategy
name|getIndexStrategy
parameter_list|()
block|{
return|return
name|indexStrategy
return|;
block|}
DECL|method|getGeometryStrategy
specifier|public
name|SerializedDVStrategy
name|getGeometryStrategy
parameter_list|()
block|{
return|return
name|geometryStrategy
return|;
block|}
DECL|method|isOptimizePredicates
specifier|public
name|boolean
name|isOptimizePredicates
parameter_list|()
block|{
return|return
name|optimizePredicates
return|;
block|}
comment|/** Set to false to NOT use optimized search predicates that avoid checking the geometry sometimes. Only useful for    * benchmarking. */
DECL|method|setOptimizePredicates
specifier|public
name|void
name|setOptimizePredicates
parameter_list|(
name|boolean
name|optimizePredicates
parameter_list|)
block|{
name|this
operator|.
name|optimizePredicates
operator|=
name|optimizePredicates
expr_stmt|;
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
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|fields
argument_list|,
name|indexStrategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|fields
argument_list|,
name|geometryStrategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|Field
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
comment|//TODO consider indexing center-point in DV?  Guarantee contained by the shape, which could then be used for
comment|// other purposes like faster WITHIN predicate?
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
specifier|final
name|SpatialOperation
name|pred
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|pred
operator|==
name|SpatialOperation
operator|.
name|BBoxIntersects
operator|||
name|pred
operator|==
name|SpatialOperation
operator|.
name|BBoxWithin
condition|)
block|{
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|pred
argument_list|)
throw|;
block|}
if|if
condition|(
name|pred
operator|==
name|SpatialOperation
operator|.
name|IsDisjointTo
condition|)
block|{
comment|//      final Query intersectQuery = makeQuery(new SpatialArgs(SpatialOperation.Intersects, args.getShape()));
comment|//      DocValues.getDocsWithField(reader, geometryStrategy.getFieldName());
comment|//TODO resurrect Disjoint spatial query utility accepting a field name known to have DocValues.
comment|// update class docs when it's added.
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|pred
argument_list|)
throw|;
block|}
specifier|final
name|ShapePredicateValueSource
name|predicateValueSource
init|=
operator|new
name|ShapePredicateValueSource
argument_list|(
name|geometryStrategy
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|pred
argument_list|,
name|args
operator|.
name|getShape
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("PredOpt: " + optimizePredicates);
if|if
condition|(
name|pred
operator|==
name|SpatialOperation
operator|.
name|Intersects
operator|&&
name|optimizePredicates
condition|)
block|{
comment|// We have a smart Intersects impl
specifier|final
name|SpatialPrefixTree
name|grid
init|=
name|indexStrategy
operator|.
name|getGrid
argument_list|()
decl_stmt|;
specifier|final
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|args
operator|.
name|resolveDistErr
argument_list|(
name|ctx
argument_list|,
literal|0.0
argument_list|)
argument_list|)
decl_stmt|;
comment|//default to max precision
return|return
operator|new
name|IntersectsRPTVerifyQuery
argument_list|(
name|args
operator|.
name|getShape
argument_list|()
argument_list|,
name|indexStrategy
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|indexStrategy
operator|.
name|getPrefixGridScanLevel
argument_list|()
argument_list|,
name|predicateValueSource
argument_list|)
return|;
block|}
else|else
block|{
comment|//The general path; all index matches get verified
name|SpatialArgs
name|indexArgs
decl_stmt|;
if|if
condition|(
name|pred
operator|==
name|SpatialOperation
operator|.
name|Contains
condition|)
block|{
comment|// note: we could map IsWithin as well but it's pretty darned slow since it touches all world grids
name|indexArgs
operator|=
name|args
expr_stmt|;
block|}
else|else
block|{
comment|//TODO add args.clone method with new predicate? Or simply make non-final?
name|indexArgs
operator|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|args
operator|.
name|getShape
argument_list|()
argument_list|)
expr_stmt|;
name|indexArgs
operator|.
name|setDistErr
argument_list|(
name|args
operator|.
name|getDistErr
argument_list|()
argument_list|)
expr_stmt|;
name|indexArgs
operator|.
name|setDistErrPct
argument_list|(
name|args
operator|.
name|getDistErrPct
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexArgs
operator|.
name|getDistErr
argument_list|()
operator|==
literal|null
operator|&&
name|indexArgs
operator|.
name|getDistErrPct
argument_list|()
operator|==
literal|null
condition|)
block|{
name|indexArgs
operator|.
name|setDistErrPct
argument_list|(
literal|0.10
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Query
name|indexQuery
init|=
name|indexStrategy
operator|.
name|makeQuery
argument_list|(
name|indexArgs
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeVerifyQuery
argument_list|(
name|indexQuery
argument_list|,
name|predicateValueSource
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

