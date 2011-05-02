begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IndexReader
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
name|queryParser
operator|.
name|ParseException
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
name|IndexSearcher
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
name|DistanceUtils
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
name|tier
operator|.
name|InvalidGeoException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SpatialParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|FunctionQParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|ValueSourceParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|*
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
name|Arrays
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
comment|/**  * Haversine function with one point constant  */
end_comment

begin_class
DECL|class|HaversineConstFunction
specifier|public
class|class
name|HaversineConstFunction
extends|extends
name|ValueSource
block|{
DECL|field|parser
specifier|public
specifier|static
name|ValueSourceParser
name|parser
init|=
operator|new
name|ValueSourceParser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// TODO: dispatch through SpatialQueriable in the future?
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
name|fp
operator|.
name|parseValueSourceList
argument_list|()
decl_stmt|;
comment|// "m" is a multi-value source, "x" is a single-value source
comment|// allow (m,m) (m,x,x) (x,x,m) (x,x,x,x)
comment|// if not enough points are present, "pt" will be checked first, followed by "sfield".
name|MultiValueSource
name|mv1
init|=
literal|null
decl_stmt|;
name|MultiValueSource
name|mv2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// nothing to do now
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|ValueSource
name|vs
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|vs
operator|instanceof
name|MultiValueSource
operator|)
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
name|mv1
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|ValueSource
name|vs1
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ValueSource
name|vs2
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs1
operator|instanceof
name|MultiValueSource
operator|&&
name|vs2
operator|instanceof
name|MultiValueSource
condition|)
block|{
name|mv1
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs1
expr_stmt|;
name|mv2
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs2
expr_stmt|;
block|}
else|else
block|{
name|mv1
operator|=
name|makeMV
argument_list|(
name|sources
argument_list|,
name|sources
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|ValueSource
name|vs1
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ValueSource
name|vs2
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs1
operator|instanceof
name|MultiValueSource
condition|)
block|{
comment|// (m,x,x)
name|mv1
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs1
expr_stmt|;
name|mv2
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// (x,x,m)
name|mv1
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
name|vs1
operator|=
name|sources
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|vs1
operator|instanceof
name|MultiValueSource
operator|)
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
name|mv2
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|4
condition|)
block|{
name|mv1
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
name|mv2
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|>
literal|4
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
if|if
condition|(
name|mv1
operator|==
literal|null
condition|)
block|{
name|mv1
operator|=
name|parsePoint
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|mv2
operator|=
name|parseSfield
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mv2
operator|==
literal|null
condition|)
block|{
name|mv2
operator|=
name|parsePoint
argument_list|(
name|fp
argument_list|)
expr_stmt|;
if|if
condition|(
name|mv2
operator|==
literal|null
condition|)
name|mv2
operator|=
name|parseSfield
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mv1
operator|==
literal|null
operator|||
name|mv2
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"geodist - not enough parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
comment|// We have all the parameters at this point, now check if one of the points is constant
name|double
index|[]
name|constants
decl_stmt|;
name|constants
operator|=
name|getConstants
argument_list|(
name|mv1
argument_list|)
expr_stmt|;
name|MultiValueSource
name|other
init|=
name|mv2
decl_stmt|;
if|if
condition|(
name|constants
operator|==
literal|null
condition|)
block|{
name|constants
operator|=
name|getConstants
argument_list|(
name|mv2
argument_list|)
expr_stmt|;
name|other
operator|=
name|mv1
expr_stmt|;
block|}
if|if
condition|(
name|constants
operator|!=
literal|null
operator|&&
name|other
operator|instanceof
name|VectorValueSource
condition|)
block|{
return|return
operator|new
name|HaversineConstFunction
argument_list|(
name|constants
index|[
literal|0
index|]
argument_list|,
name|constants
index|[
literal|1
index|]
argument_list|,
operator|(
name|VectorValueSource
operator|)
name|other
argument_list|)
return|;
block|}
return|return
operator|new
name|HaversineFunction
argument_list|(
name|mv1
argument_list|,
name|mv2
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** make a MultiValueSource from two non MultiValueSources */
DECL|method|makeMV
specifier|private
specifier|static
name|VectorValueSource
name|makeMV
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|,
name|List
argument_list|<
name|ValueSource
argument_list|>
name|orig
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|vs1
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ValueSource
name|vs2
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs1
operator|instanceof
name|MultiValueSource
operator|||
name|vs2
operator|instanceof
name|MultiValueSource
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|orig
argument_list|)
throw|;
block|}
return|return
operator|new
name|VectorValueSource
argument_list|(
name|sources
argument_list|)
return|;
block|}
DECL|method|parsePoint
specifier|private
specifier|static
name|MultiValueSource
name|parsePoint
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|pt
init|=
name|fp
operator|.
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|POINT
argument_list|)
decl_stmt|;
if|if
condition|(
name|pt
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|double
index|[]
name|point
init|=
literal|null
decl_stmt|;
try|try
block|{
name|point
operator|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
name|pt
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Bad spatial pt:"
operator|+
name|pt
argument_list|)
throw|;
block|}
return|return
operator|new
name|VectorValueSource
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ValueSource
index|[]
block|{
operator|new
name|DoubleConstValueSource
argument_list|(
name|point
index|[
literal|0
index|]
argument_list|)
block|,
operator|new
name|DoubleConstValueSource
argument_list|(
name|point
index|[
literal|1
index|]
argument_list|)
block|}
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getConstants
specifier|private
specifier|static
name|double
index|[]
name|getConstants
parameter_list|(
name|MultiValueSource
name|vs
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|vs
operator|instanceof
name|VectorValueSource
operator|)
condition|)
return|return
literal|null
return|;
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
operator|(
operator|(
name|VectorValueSource
operator|)
name|vs
operator|)
operator|.
name|getSources
argument_list|()
decl_stmt|;
if|if
condition|(
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ConstNumberSource
operator|&&
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|ConstNumberSource
condition|)
block|{
return|return
operator|new
name|double
index|[]
block|{
operator|(
operator|(
name|ConstNumberSource
operator|)
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDouble
argument_list|()
block|,
operator|(
operator|(
name|ConstNumberSource
operator|)
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getDouble
argument_list|()
block|}
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|parseSfield
specifier|private
specifier|static
name|MultiValueSource
name|parseSfield
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|sfield
init|=
name|fp
operator|.
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|sfield
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|SchemaField
name|sf
init|=
name|fp
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|sfield
argument_list|)
decl_stmt|;
name|ValueSource
name|vs
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|sf
argument_list|,
name|fp
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|vs
operator|instanceof
name|MultiValueSource
operator|)
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Spatial field must implement MultiValueSource:"
operator|+
name|sf
argument_list|)
throw|;
block|}
return|return
operator|(
name|MultiValueSource
operator|)
name|vs
return|;
block|}
comment|//////////////////////////////////////////////////////////////////////////////////////
DECL|field|latCenter
specifier|private
specifier|final
name|double
name|latCenter
decl_stmt|;
DECL|field|lonCenter
specifier|private
specifier|final
name|double
name|lonCenter
decl_stmt|;
DECL|field|p2
specifier|private
specifier|final
name|VectorValueSource
name|p2
decl_stmt|;
comment|// lat+lon, just saved for display/debugging
DECL|field|latSource
specifier|private
specifier|final
name|ValueSource
name|latSource
decl_stmt|;
DECL|field|lonSource
specifier|private
specifier|final
name|ValueSource
name|lonSource
decl_stmt|;
DECL|field|latCenterRad_cos
specifier|private
specifier|final
name|double
name|latCenterRad_cos
decl_stmt|;
comment|// cos(latCenter)
DECL|field|EARTH_MEAN_DIAMETER
specifier|private
specifier|static
specifier|final
name|double
name|EARTH_MEAN_DIAMETER
init|=
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
operator|*
literal|2
decl_stmt|;
DECL|method|HaversineConstFunction
specifier|public
name|HaversineConstFunction
parameter_list|(
name|double
name|latCenter
parameter_list|,
name|double
name|lonCenter
parameter_list|,
name|VectorValueSource
name|vs
parameter_list|)
block|{
name|this
operator|.
name|latCenter
operator|=
name|latCenter
expr_stmt|;
name|this
operator|.
name|lonCenter
operator|=
name|lonCenter
expr_stmt|;
name|this
operator|.
name|p2
operator|=
name|vs
expr_stmt|;
name|this
operator|.
name|latSource
operator|=
name|p2
operator|.
name|getSources
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|lonSource
operator|=
name|p2
operator|.
name|getSources
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|latCenterRad_cos
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|latCenter
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|)
expr_stmt|;
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"geodist"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
name|latVals
init|=
name|latSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
name|lonVals
init|=
name|lonSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|double
name|latCenterRad
init|=
name|this
operator|.
name|latCenter
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|lonCenterRad
init|=
name|this
operator|.
name|lonCenter
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|latCenterRad_cos
init|=
name|this
operator|.
name|latCenterRad_cos
decl_stmt|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
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
name|double
name|latRad
init|=
name|latVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
decl_stmt|;
name|double
name|lonRad
init|=
name|lonVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
decl_stmt|;
name|double
name|diffX
init|=
name|latCenterRad
operator|-
name|latRad
decl_stmt|;
name|double
name|diffY
init|=
name|lonCenterRad
operator|-
name|lonRad
decl_stmt|;
name|double
name|hsinX
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffX
operator|*
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|hsinY
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffY
operator|*
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|h
init|=
name|hsinX
operator|*
name|hsinX
operator|+
operator|(
name|latCenterRad_cos
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|latRad
argument_list|)
operator|*
name|hsinY
operator|*
name|hsinY
operator|)
decl_stmt|;
return|return
operator|(
name|EARTH_MEAN_DIAMETER
operator|*
name|Math
operator|.
name|atan2
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
name|h
argument_list|)
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|1
operator|-
name|h
argument_list|)
argument_list|)
operator|)
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
name|name
argument_list|()
operator|+
literal|'('
operator|+
name|latVals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|','
operator|+
name|lonVals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|','
operator|+
name|latCenter
operator|+
literal|','
operator|+
name|lonCenter
operator|+
literal|')'
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|latSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|lonSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
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
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|HaversineConstFunction
operator|)
condition|)
return|return
literal|false
return|;
name|HaversineConstFunction
name|other
init|=
operator|(
name|HaversineConstFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|latCenter
operator|==
name|other
operator|.
name|latCenter
operator|&&
name|this
operator|.
name|lonCenter
operator|==
name|other
operator|.
name|lonCenter
operator|&&
name|this
operator|.
name|p2
operator|.
name|equals
argument_list|(
name|other
operator|.
name|p2
argument_list|)
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
name|p2
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|latCenter
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|lonCenter
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
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
name|name
argument_list|()
operator|+
literal|'('
operator|+
name|p2
operator|+
literal|','
operator|+
name|latCenter
operator|+
literal|','
operator|+
name|lonCenter
operator|+
literal|')'
return|;
block|}
block|}
end_class

end_unit

