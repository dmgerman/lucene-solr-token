begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.vector
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|vector
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
name|distance
operator|.
name|DistanceCalculator
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
name|LeafReader
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
name|index
operator|.
name|DocValues
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
name|NumericDocValues
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
name|Map
import|;
end_import

begin_comment
comment|/**  * An implementation of the Lucene ValueSource model that returns the distance  * for a {@link PointVectorStrategy}.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|DistanceValueSource
specifier|public
class|class
name|DistanceValueSource
extends|extends
name|ValueSource
block|{
DECL|field|strategy
specifier|private
name|PointVectorStrategy
name|strategy
decl_stmt|;
DECL|field|from
specifier|private
specifier|final
name|Point
name|from
decl_stmt|;
DECL|field|multiplier
specifier|private
specifier|final
name|double
name|multiplier
decl_stmt|;
comment|/**    * Constructor.    */
DECL|method|DistanceValueSource
specifier|public
name|DistanceValueSource
parameter_list|(
name|PointVectorStrategy
name|strategy
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
name|strategy
operator|=
name|strategy
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|multiplier
operator|=
name|multiplier
expr_stmt|;
block|}
comment|/**    * Returns the ValueSource description.    */
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"DistanceValueSource("
operator|+
name|strategy
operator|+
literal|", "
operator|+
name|from
operator|+
literal|")"
return|;
block|}
comment|/**    * Returns the FunctionValues used by the function query.    */
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
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|readerContext
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValues
name|ptX
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|getFieldNameX
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|ptY
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|getFieldNameY
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|validX
init|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|getFieldNameX
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|validY
init|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|getFieldNameY
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
specifier|private
specifier|final
name|Point
name|from
init|=
name|DistanceValueSource
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
name|strategy
operator|.
name|getSpatialContext
argument_list|()
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
name|strategy
operator|.
name|getSpatialContext
argument_list|()
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
comment|// make sure it has minX and area
if|if
condition|(
name|validX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
assert|assert
name|validY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
assert|;
return|return
name|calculator
operator|.
name|distance
argument_list|(
name|from
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|ptX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|ptY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
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
name|DistanceValueSource
name|that
init|=
operator|(
name|DistanceValueSource
operator|)
name|o
decl_stmt|;
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
name|strategy
operator|.
name|equals
argument_list|(
name|that
operator|.
name|strategy
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

