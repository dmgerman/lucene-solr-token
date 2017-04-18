begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|DoublePoint
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
name|StoredField
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
name|DocValuesType
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
name|IndexableField
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DoubleFieldSource
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
name|valuesource
operator|.
name|MultiValuedDoubleFieldSource
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
name|search
operator|.
name|SortField
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
name|SortedNumericSelector
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
name|BytesRefBuilder
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
name|NumericUtils
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
name|QParser
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
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * {@code PointField} implementation for {@code Double} values.  * @see PointField  * @see DoublePoint  */
end_comment

begin_class
DECL|class|DoublePointField
specifier|public
class|class
name|DoublePointField
extends|extends
name|PointField
implements|implements
name|DoubleValueFieldType
block|{
DECL|method|DoublePointField
specifier|public
name|DoublePointField
parameter_list|()
block|{
name|type
operator|=
name|NumberType
operator|.
name|DOUBLE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toNativeType
specifier|public
name|Object
name|toNativeType
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|val
operator|instanceof
name|Number
condition|)
return|return
operator|(
operator|(
name|Number
operator|)
name|val
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
return|;
return|return
name|super
operator|.
name|toNativeType
argument_list|(
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPointRangeQuery
specifier|public
name|Query
name|getPointRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|double
name|actualMin
decl_stmt|,
name|actualMax
decl_stmt|;
if|if
condition|(
name|min
operator|==
literal|null
condition|)
block|{
name|actualMin
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|actualMin
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|min
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|minInclusive
condition|)
block|{
name|actualMin
operator|=
name|DoublePoint
operator|.
name|nextUp
argument_list|(
name|actualMin
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|max
operator|==
literal|null
condition|)
block|{
name|actualMax
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|actualMax
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|max
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|maxInclusive
condition|)
block|{
name|actualMax
operator|=
name|DoublePoint
operator|.
name|nextDown
argument_list|(
name|actualMax
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|DoublePoint
operator|.
name|newRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|actualMin
argument_list|,
name|actualMax
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|DoublePoint
operator|.
name|decodeDimension
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
specifier|final
name|Number
name|val
init|=
name|f
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
operator|==
literal|false
operator|&&
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|NUMERIC
condition|)
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|val
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
operator|==
literal|false
operator|&&
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|SORTED_NUMERIC
condition|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|val
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|val
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unexpected state. Field: '"
operator|+
name|f
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getExactQuery
specifier|protected
name|Query
name|getExactQuery
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
return|return
name|DoublePoint
operator|.
name|newExactQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|externalVal
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSetQuery
specifier|public
name|Query
name|getSetQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|externalVal
parameter_list|)
block|{
assert|assert
name|externalVal
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
if|if
condition|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|getSetQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|externalVal
argument_list|)
return|;
block|}
name|double
index|[]
name|values
init|=
operator|new
name|double
index|[
name|externalVal
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|val
range|:
name|externalVal
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|DoublePoint
operator|.
name|newSetQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|protected
name|String
name|indexedToReadable
parameter_list|(
name|BytesRef
name|indexedForm
parameter_list|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|DoublePoint
operator|.
name|decodeDimension
argument_list|(
name|indexedForm
operator|.
name|bytes
argument_list|,
name|indexedForm
operator|.
name|offset
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readableToIndexed
specifier|public
name|void
name|readableToIndexed
parameter_list|(
name|CharSequence
name|val
parameter_list|,
name|BytesRefBuilder
name|result
parameter_list|)
block|{
name|result
operator|.
name|grow
argument_list|(
name|Double
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|result
operator|.
name|setLength
argument_list|(
name|Double
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|DoublePoint
operator|.
name|encodeDimension
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|result
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
name|field
operator|.
name|checkSortability
argument_list|()
expr_stmt|;
name|Object
name|missingValue
init|=
literal|null
decl_stmt|;
name|boolean
name|sortMissingLast
init|=
name|field
operator|.
name|sortMissingLast
argument_list|()
decl_stmt|;
name|boolean
name|sortMissingFirst
init|=
name|field
operator|.
name|sortMissingFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortMissingLast
condition|)
block|{
name|missingValue
operator|=
name|top
condition|?
name|Double
operator|.
name|NEGATIVE_INFINITY
else|:
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sortMissingFirst
condition|)
block|{
name|missingValue
operator|=
name|top
condition|?
name|Double
operator|.
name|POSITIVE_INFINITY
else|:
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
name|SortField
name|sf
init|=
operator|new
name|SortField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|,
name|top
argument_list|)
decl_stmt|;
name|sf
operator|.
name|setMissingValue
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
return|return
name|sf
return|;
block|}
annotation|@
name|Override
DECL|method|getUninversionType
specifier|public
name|Type
name|getUninversionType
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|DOUBLE_POINT
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
block|{
name|field
operator|.
name|checkFieldCacheSource
argument_list|()
expr_stmt|;
return|return
operator|new
name|DoubleFieldSource
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSingleValueSource
specifier|protected
name|ValueSource
name|getSingleValueSource
parameter_list|(
name|SortedNumericSelector
operator|.
name|Type
name|choice
parameter_list|,
name|SchemaField
name|f
parameter_list|)
block|{
return|return
operator|new
name|MultiValuedDoubleFieldSource
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|choice
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createField
specifier|public
name|IndexableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|double
name|doubleValue
init|=
operator|(
name|value
operator|instanceof
name|Number
operator|)
condition|?
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
else|:
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoublePoint
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|doubleValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getStoredField
specifier|protected
name|StoredField
name|getStoredField
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|StoredField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|Double
operator|)
name|this
operator|.
name|toNativeType
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

