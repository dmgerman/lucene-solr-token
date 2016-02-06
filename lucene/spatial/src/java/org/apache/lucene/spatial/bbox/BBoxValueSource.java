begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
package|;
end_package

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
name|search
operator|.
name|Explanation
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
comment|/**  * A ValueSource in which the indexed Rectangle is returned from  * {@link org.apache.lucene.queries.function.FunctionValues#objectVal(int)}.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|BBoxValueSource
class|class
name|BBoxValueSource
extends|extends
name|ValueSource
block|{
DECL|field|strategy
specifier|private
specifier|final
name|BBoxStrategy
name|strategy
decl_stmt|;
DECL|method|BBoxValueSource
specifier|public
name|BBoxValueSource
parameter_list|(
name|BBoxStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
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
literal|"bboxShape("
operator|+
name|strategy
operator|.
name|getFieldName
argument_list|()
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
name|minX
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_minX
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|minY
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_minY
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|maxX
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_maxX
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|maxY
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_maxY
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|validBits
init|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_minX
argument_list|)
decl_stmt|;
comment|//could have chosen any field
comment|//reused
specifier|final
name|Rectangle
name|rect
init|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
operator|!
name|validBits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|rect
operator|.
name|reset
argument_list|(
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|minX
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
name|maxX
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
name|minY
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
name|maxY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rect
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|//TODO support WKT output once Spatial4j does
name|Object
name|v
init|=
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
literal|null
else|:
name|v
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|validBits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
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
literal|'='
operator|+
name|strVal
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
name|BBoxValueSource
name|that
init|=
operator|(
name|BBoxValueSource
operator|)
name|o
decl_stmt|;
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
name|strategy
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

