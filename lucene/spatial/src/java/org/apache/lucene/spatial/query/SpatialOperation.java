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
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|HashMap
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
name|Locale
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

begin_comment
comment|/**  * A predicate that compares a stored geometry to a supplied geometry. It's enum-like. For more  * explanation of each predicate, consider looking at the source implementation  * of {@link #evaluate(com.spatial4j.core.shape.Shape, com.spatial4j.core.shape.Shape)}. It's important  * to be aware that Lucene-spatial makes no distinction of shape boundaries, unlike many standardized  * definitions. Nor does it make dimensional distinctions (e.g. line vs polygon).  * You can lookup a predicate by "Covers" or "Contains", for example, and you will get the  * same underlying predicate implementation.  *  * @see<a href="http://en.wikipedia.org/wiki/DE-9IM">DE-9IM at Wikipedia, based on OGC specs</a>  * @see<a href="http://edndoc.esri.com/arcsde/9.1/general_topics/understand_spatial_relations.htm">  *   ESRIs docs on spatial relations</a>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialOperation
specifier|public
specifier|abstract
class|class
name|SpatialOperation
implements|implements
name|Serializable
block|{
comment|//TODO rename to SpatialPredicate. Use enum?  LUCENE-5771
comment|// Private registry
DECL|field|registry
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SpatialOperation
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//has aliases
DECL|field|list
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|SpatialOperation
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Geometry Operations
comment|/** Bounding box of the *indexed* shape, then {@link #Intersects}. */
DECL|field|BBoxIntersects
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|BBoxIntersects
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"BBoxIntersects"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
name|indexedShape
operator|.
name|getBoundingBox
argument_list|()
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|.
name|intersects
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Bounding box of the *indexed* shape, then {@link #IsWithin}. */
DECL|field|BBoxWithin
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|BBoxWithin
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"BBoxWithin"
argument_list|)
block|{
block|{
name|register
argument_list|(
literal|"BBoxCoveredBy"
argument_list|)
expr_stmt|;
comment|//alias -- the better name
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
name|Rectangle
name|bbox
init|=
name|indexedShape
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
return|return
name|bbox
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|==
name|SpatialRelation
operator|.
name|WITHIN
operator|||
name|bbox
operator|.
name|equals
argument_list|(
name|queryShape
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Meets the "Covers" OGC definition (boundary-neutral). */
DECL|field|Contains
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|Contains
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"Contains"
argument_list|)
block|{
block|{
name|register
argument_list|(
literal|"Covers"
argument_list|)
expr_stmt|;
comment|//alias -- the better name
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
name|indexedShape
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|==
name|SpatialRelation
operator|.
name|CONTAINS
operator|||
name|indexedShape
operator|.
name|equals
argument_list|(
name|queryShape
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Meets the "Intersects" OGC definition. */
DECL|field|Intersects
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|Intersects
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"Intersects"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
name|indexedShape
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|.
name|intersects
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Meets the "Equals" OGC definition. */
DECL|field|IsEqualTo
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|IsEqualTo
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"Equals"
argument_list|)
block|{
block|{
name|register
argument_list|(
literal|"IsEqualTo"
argument_list|)
expr_stmt|;
comment|//alias (deprecated)
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
name|indexedShape
operator|.
name|equals
argument_list|(
name|queryShape
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Meets the "Disjoint" OGC definition. */
DECL|field|IsDisjointTo
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|IsDisjointTo
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"Disjoint"
argument_list|)
block|{
block|{
name|register
argument_list|(
literal|"IsDisjointTo"
argument_list|)
expr_stmt|;
comment|//alias (deprecated)
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
operator|!
name|indexedShape
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|.
name|intersects
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Meets the "CoveredBy" OGC definition (boundary-neutral). */
DECL|field|IsWithin
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|IsWithin
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"Within"
argument_list|)
block|{
block|{
name|register
argument_list|(
literal|"IsWithin"
argument_list|)
expr_stmt|;
comment|//alias (deprecated)
name|register
argument_list|(
literal|"CoveredBy"
argument_list|)
expr_stmt|;
comment|//alias -- the more appropriate name.
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
name|indexedShape
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|==
name|SpatialRelation
operator|.
name|WITHIN
operator|||
name|indexedShape
operator|.
name|equals
argument_list|(
name|queryShape
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Almost meets the "Overlaps" OGC definition, but boundary-neutral (boundary==interior). */
DECL|field|Overlaps
specifier|public
specifier|static
specifier|final
name|SpatialOperation
name|Overlaps
init|=
operator|new
name|SpatialOperation
argument_list|(
literal|"Overlaps"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
return|return
name|indexedShape
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|==
name|SpatialRelation
operator|.
name|INTERSECTS
return|;
comment|//not Contains or Within or Disjoint
block|}
block|}
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|SpatialOperation
specifier|protected
name|SpatialOperation
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|register
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|register
specifier|protected
name|void
name|register
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|registry
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
specifier|static
name|SpatialOperation
name|get
parameter_list|(
name|String
name|v
parameter_list|)
block|{
name|SpatialOperation
name|op
init|=
name|registry
operator|.
name|get
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
name|op
operator|=
name|registry
operator|.
name|get
argument_list|(
name|v
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown Operation: "
operator|+
name|v
argument_list|)
throw|;
block|}
return|return
name|op
return|;
block|}
DECL|method|values
specifier|public
specifier|static
name|List
argument_list|<
name|SpatialOperation
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|list
return|;
block|}
DECL|method|is
specifier|public
specifier|static
name|boolean
name|is
parameter_list|(
name|SpatialOperation
name|op
parameter_list|,
name|SpatialOperation
modifier|...
name|tst
parameter_list|)
block|{
for|for
control|(
name|SpatialOperation
name|t
range|:
name|tst
control|)
block|{
if|if
condition|(
name|op
operator|==
name|t
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns whether the relationship between indexedShape and queryShape is    * satisfied by this operation.    */
DECL|method|evaluate
specifier|public
specifier|abstract
name|boolean
name|evaluate
parameter_list|(
name|Shape
name|indexedShape
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
function_decl|;
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
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
name|name
return|;
block|}
block|}
end_class

end_unit

