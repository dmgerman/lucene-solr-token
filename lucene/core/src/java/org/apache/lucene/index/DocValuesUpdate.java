begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
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
name|NumericDocValuesField
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** An in-place update to a DocValues field. */
end_comment

begin_class
DECL|class|DocValuesUpdate
specifier|abstract
class|class
name|DocValuesUpdate
block|{
comment|/* Rough logic: OBJ_HEADER + 3*PTR + INT    * Term: OBJ_HEADER + 2*PTR    *   Term.field: 2*OBJ_HEADER + 4*INT + PTR + string.length*CHAR    *   Term.bytes: 2*OBJ_HEADER + 2*INT + PTR + bytes.length    * String: 2*OBJ_HEADER + 4*INT + PTR + string.length*CHAR    * T: OBJ_HEADER    */
DECL|field|RAW_SIZE_IN_BYTES
specifier|private
specifier|static
specifier|final
name|int
name|RAW_SIZE_IN_BYTES
init|=
literal|8
operator|*
name|NUM_BYTES_OBJECT_HEADER
operator|+
literal|8
operator|*
name|NUM_BYTES_OBJECT_REF
operator|+
literal|8
operator|*
name|NUM_BYTES_INT
decl_stmt|;
DECL|field|type
specifier|final
name|DocValuesFieldUpdates
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|term
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|value
specifier|final
name|Object
name|value
decl_stmt|;
DECL|field|docIDUpto
name|int
name|docIDUpto
init|=
operator|-
literal|1
decl_stmt|;
comment|// unassigned until applied, and confusing that it's here, when it's just used in BufferedDeletes...
comment|/**    * Constructor.    *     * @param term the {@link Term} which determines the documents that will be updated    * @param field the {@link NumericDocValuesField} to update    * @param value the updated value    */
DECL|method|DocValuesUpdate
specifier|protected
name|DocValuesUpdate
parameter_list|(
name|DocValuesFieldUpdates
operator|.
name|Type
name|type
parameter_list|,
name|Term
name|term
parameter_list|,
name|String
name|field
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|valueSizeInBytes
specifier|abstract
name|long
name|valueSizeInBytes
parameter_list|()
function_decl|;
DECL|method|sizeInBytes
specifier|final
name|int
name|sizeInBytes
parameter_list|()
block|{
name|int
name|sizeInBytes
init|=
name|RAW_SIZE_IN_BYTES
decl_stmt|;
name|sizeInBytes
operator|+=
name|term
operator|.
name|field
operator|.
name|length
argument_list|()
operator|*
name|NUM_BYTES_CHAR
expr_stmt|;
name|sizeInBytes
operator|+=
name|term
operator|.
name|bytes
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
name|sizeInBytes
operator|+=
name|field
operator|.
name|length
argument_list|()
operator|*
name|NUM_BYTES_CHAR
expr_stmt|;
name|sizeInBytes
operator|+=
name|valueSizeInBytes
argument_list|()
expr_stmt|;
return|return
name|sizeInBytes
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
literal|"term="
operator|+
name|term
operator|+
literal|",field="
operator|+
name|field
operator|+
literal|",value="
operator|+
name|value
return|;
block|}
comment|/** An in-place update to a binary DocValues field */
DECL|class|BinaryDocValuesUpdate
specifier|static
specifier|final
class|class
name|BinaryDocValuesUpdate
extends|extends
name|DocValuesUpdate
block|{
comment|/* Size of BytesRef: 2*INT + ARRAY_HEADER + PTR */
DECL|field|RAW_VALUE_SIZE_IN_BYTES
specifier|private
specifier|static
specifier|final
name|long
name|RAW_VALUE_SIZE_IN_BYTES
init|=
name|NUM_BYTES_ARRAY_HEADER
operator|+
literal|2
operator|*
name|NUM_BYTES_INT
operator|+
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
DECL|field|MISSING
specifier|static
specifier|final
name|BytesRef
name|MISSING
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|BinaryDocValuesUpdate
name|BinaryDocValuesUpdate
parameter_list|(
name|Term
name|term
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|DocValuesFieldUpdates
operator|.
name|Type
operator|.
name|BINARY
argument_list|,
name|term
argument_list|,
name|field
argument_list|,
name|value
operator|==
literal|null
condition|?
name|MISSING
else|:
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valueSizeInBytes
name|long
name|valueSizeInBytes
parameter_list|()
block|{
return|return
name|RAW_VALUE_SIZE_IN_BYTES
operator|+
operator|(
operator|(
name|BytesRef
operator|)
name|value
operator|)
operator|.
name|bytes
operator|.
name|length
return|;
block|}
block|}
comment|/** An in-place update to a numeric DocValues field */
DECL|class|NumericDocValuesUpdate
specifier|static
specifier|final
class|class
name|NumericDocValuesUpdate
extends|extends
name|DocValuesUpdate
block|{
DECL|field|MISSING
specifier|static
specifier|final
name|Long
name|MISSING
init|=
operator|new
name|Long
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|NumericDocValuesUpdate
name|NumericDocValuesUpdate
parameter_list|(
name|Term
name|term
parameter_list|,
name|String
name|field
parameter_list|,
name|Long
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|DocValuesFieldUpdates
operator|.
name|Type
operator|.
name|NUMERIC
argument_list|,
name|term
argument_list|,
name|field
argument_list|,
name|value
operator|==
literal|null
condition|?
name|MISSING
else|:
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valueSizeInBytes
name|long
name|valueSizeInBytes
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
block|}
block|}
block|}
end_class

end_unit

