begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
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
name|distance
operator|.
name|DistanceCalculator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|FunctionValues
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

begin_comment
comment|/**  * An implementation of the Lucene ValueSource that returns the spatial distance  * between an input point and a document's points in  * {@link ShapeFieldCacheProvider}. The shortest distance is returned if a  * document has more than one point.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|ShapeFieldCacheDistanceValueSource
specifier|public
class|class
name|ShapeFieldCacheDistanceValueSource
extends|extends
name|ValueSource
block|{
DECL|field|ctx
specifier|private
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|from
specifier|private
specifier|final
name|Point
name|from
decl_stmt|;
DECL|field|provider
specifier|private
specifier|final
name|ShapeFieldCacheProvider
argument_list|<
name|Point
argument_list|>
name|provider
decl_stmt|;
DECL|field|multiplier
specifier|private
specifier|final
name|double
name|multiplier
decl_stmt|;
DECL|method|ShapeFieldCacheDistanceValueSource
specifier|public
name|ShapeFieldCacheDistanceValueSource
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|ShapeFieldCacheProvider
argument_list|<
name|Point
argument_list|>
name|provider
parameter_list|,
name|Point
name|from
parameter_list|,
name|double
name|multiplier
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|this
operator|.
name|multiplier
operator|=
name|multiplier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|provider
operator|+
literal|", "
operator|+
name|from
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
specifier|final
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
specifier|private
specifier|final
name|ShapeFieldCache
argument_list|<
name|Point
argument_list|>
name|cache
init|=
name|provider
operator|.
name|getCache
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Point
name|from
init|=
name|ShapeFieldCacheDistanceValueSource
operator|.
name|this
operator|.
name|from
decl_stmt|;
specifier|private
specifier|final
name|DistanceCalculator
name|calculator
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|double
name|nullValue
init|=
operator|(
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
literal|180
operator|*
name|multiplier
else|:
name|Double
operator|.
name|MAX_VALUE
operator|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|List
argument_list|<
name|Point
argument_list|>
name|vals
init|=
name|cache
operator|.
name|getShapes
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
name|double
name|v
init|=
name|calculator
operator|.
name|distance
argument_list|(
name|from
argument_list|,
name|vals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|vals
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|v
operator|=
name|Math
operator|.
name|min
argument_list|(
name|v
argument_list|,
name|calculator
operator|.
name|distance
argument_list|(
name|from
argument_list|,
name|vals
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|v
operator|*
name|multiplier
return|;
block|}
return|return
name|nullValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|"="
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
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
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ShapeFieldCacheDistanceValueSource
name|that
init|=
operator|(
name|ShapeFieldCacheDistanceValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|ctx
operator|.
name|equals
argument_list|(
name|that
operator|.
name|ctx
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|from
operator|.
name|equals
argument_list|(
name|that
operator|.
name|from
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|provider
operator|.
name|equals
argument_list|(
name|that
operator|.
name|provider
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|multiplier
operator|!=
name|that
operator|.
name|multiplier
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
return|return
name|from
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

