begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Norm
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|MemoryIndexNormDocValues
class|class
name|MemoryIndexNormDocValues
extends|extends
name|DocValues
block|{
DECL|field|source
specifier|private
specifier|final
name|Source
name|source
decl_stmt|;
DECL|method|MemoryIndexNormDocValues
name|MemoryIndexNormDocValues
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadSource
specifier|protected
name|Source
name|loadSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|loadDirectSource
specifier|protected
name|Source
name|loadDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|source
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|class|SingleValueSource
specifier|public
specifier|static
class|class
name|SingleValueSource
extends|extends
name|Source
block|{
DECL|field|numericValue
specifier|private
specifier|final
name|Number
name|numericValue
decl_stmt|;
DECL|field|binaryValue
specifier|private
specifier|final
name|BytesRef
name|binaryValue
decl_stmt|;
DECL|method|SingleValueSource
specifier|protected
name|SingleValueSource
parameter_list|(
name|Norm
name|norm
parameter_list|)
block|{
name|super
argument_list|(
name|norm
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|numericValue
operator|=
name|norm
operator|.
name|field
argument_list|()
operator|.
name|numericValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|binaryValue
operator|=
name|norm
operator|.
name|field
argument_list|()
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|VAR_INTS
case|:
assert|assert
name|numericValue
operator|!=
literal|null
assert|;
return|return
name|numericValue
operator|.
name|longValue
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|getInt
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
assert|assert
name|numericValue
operator|!=
literal|null
assert|;
return|return
name|numericValue
operator|.
name|floatValue
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|getFloat
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
assert|assert
name|binaryValue
operator|!=
literal|null
assert|;
name|ref
operator|.
name|copyBytes
argument_list|(
name|binaryValue
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
return|return
name|super
operator|.
name|getBytes
argument_list|(
name|docID
argument_list|,
name|ref
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
name|binaryValue
operator|.
name|bytes
return|;
case|case
name|FIXED_INTS_16
case|:
return|return
operator|new
name|short
index|[]
block|{
name|numericValue
operator|.
name|shortValue
argument_list|()
block|}
return|;
case|case
name|FIXED_INTS_32
case|:
return|return
operator|new
name|int
index|[]
block|{
name|numericValue
operator|.
name|intValue
argument_list|()
block|}
return|;
case|case
name|FIXED_INTS_64
case|:
return|return
operator|new
name|long
index|[]
block|{
name|numericValue
operator|.
name|longValue
argument_list|()
block|}
return|;
case|case
name|FIXED_INTS_8
case|:
return|return
operator|new
name|byte
index|[]
block|{
name|numericValue
operator|.
name|byteValue
argument_list|()
block|}
return|;
case|case
name|VAR_INTS
case|:
return|return
operator|new
name|long
index|[]
block|{
name|numericValue
operator|.
name|longValue
argument_list|()
block|}
return|;
case|case
name|FLOAT_32
case|:
return|return
operator|new
name|float
index|[]
block|{
name|numericValue
operator|.
name|floatValue
argument_list|()
block|}
return|;
case|case
name|FLOAT_64
case|:
return|return
operator|new
name|double
index|[]
block|{
name|numericValue
operator|.
name|doubleValue
argument_list|()
block|}
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

