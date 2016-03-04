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
name|java
operator|.
name|util
operator|.
name|Map
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
name|distance
operator|.
name|DistanceUtils
import|;
end_import

begin_comment
comment|/**  * Abstract Factory for creating {@link SpatialPrefixTree} instances with useful  * defaults and passed on configurations defined in a Map.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialPrefixTreeFactory
specifier|public
specifier|abstract
class|class
name|SpatialPrefixTreeFactory
block|{
DECL|field|DEFAULT_GEO_MAX_DETAIL_KM
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_GEO_MAX_DETAIL_KM
init|=
literal|0.001
decl_stmt|;
comment|//1m
DECL|field|PREFIX_TREE
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_TREE
init|=
literal|"prefixTree"
decl_stmt|;
DECL|field|MAX_LEVELS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_LEVELS
init|=
literal|"maxLevels"
decl_stmt|;
DECL|field|MAX_DIST_ERR
specifier|public
specifier|static
specifier|final
name|String
name|MAX_DIST_ERR
init|=
literal|"maxDistErr"
decl_stmt|;
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|maxLevels
specifier|protected
name|Integer
name|maxLevels
decl_stmt|;
comment|/**    * The factory  is looked up via "prefixTree" in args, expecting "geohash" or "quad".    * If it's neither of these, then "geohash" is chosen for a geo context, otherwise "quad" is chosen.    */
DECL|method|makeSPT
specifier|public
specifier|static
name|SpatialPrefixTree
name|makeSPT
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|SpatialPrefixTreeFactory
name|instance
decl_stmt|;
name|String
name|cname
init|=
name|args
operator|.
name|get
argument_list|(
name|PREFIX_TREE
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
name|cname
operator|=
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
literal|"geohash"
else|:
literal|"quad"
expr_stmt|;
if|if
condition|(
literal|"geohash"
operator|.
name|equalsIgnoreCase
argument_list|(
name|cname
argument_list|)
condition|)
name|instance
operator|=
operator|new
name|GeohashPrefixTree
operator|.
name|Factory
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
literal|"quad"
operator|.
name|equalsIgnoreCase
argument_list|(
name|cname
argument_list|)
condition|)
name|instance
operator|=
operator|new
name|QuadPrefixTree
operator|.
name|Factory
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
literal|"packedQuad"
operator|.
name|equalsIgnoreCase
argument_list|(
name|cname
argument_list|)
condition|)
name|instance
operator|=
operator|new
name|PackedQuadPrefixTree
operator|.
name|Factory
argument_list|()
expr_stmt|;
else|else
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|classLoader
operator|.
name|loadClass
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|instance
operator|=
operator|(
name|SpatialPrefixTreeFactory
operator|)
name|c
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|instance
operator|.
name|init
argument_list|(
name|args
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
return|return
name|instance
operator|.
name|newSPT
argument_list|()
return|;
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|initMaxLevels
argument_list|()
expr_stmt|;
block|}
DECL|method|initMaxLevels
specifier|protected
name|void
name|initMaxLevels
parameter_list|()
block|{
name|String
name|mlStr
init|=
name|args
operator|.
name|get
argument_list|(
name|MAX_LEVELS
argument_list|)
decl_stmt|;
if|if
condition|(
name|mlStr
operator|!=
literal|null
condition|)
block|{
name|maxLevels
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|mlStr
argument_list|)
expr_stmt|;
return|return;
block|}
name|double
name|degrees
decl_stmt|;
name|String
name|maxDetailDistStr
init|=
name|args
operator|.
name|get
argument_list|(
name|MAX_DIST_ERR
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxDetailDistStr
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
return|return;
comment|//let default to max
block|}
name|degrees
operator|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
name|DEFAULT_GEO_MAX_DETAIL_KM
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|degrees
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|maxDetailDistStr
argument_list|)
expr_stmt|;
block|}
name|maxLevels
operator|=
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
expr_stmt|;
block|}
comment|/** Calls {@link SpatialPrefixTree#getLevelForDistance(double)}. */
DECL|method|getLevelForDistance
specifier|protected
specifier|abstract
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|degrees
parameter_list|)
function_decl|;
DECL|method|newSPT
specifier|protected
specifier|abstract
name|SpatialPrefixTree
name|newSPT
parameter_list|()
function_decl|;
block|}
end_class

end_unit

