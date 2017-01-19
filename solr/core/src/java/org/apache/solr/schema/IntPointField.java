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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

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
name|IntPoint
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
name|legacy
operator|.
name|LegacyNumericType
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
name|IntFieldSource
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * {@code PointField} implementation for {@code Integer} values.  * @see PointField  * @see IntPoint  */
end_comment

begin_class
DECL|class|IntPointField
specifier|public
class|class
name|IntPointField
extends|extends
name|PointField
implements|implements
name|IntValueFieldType
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
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
name|intValue
argument_list|()
return|;
try|try
block|{
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|Float
name|v
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|intValue
argument_list|()
return|;
block|}
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
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
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
name|int
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
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
else|else
block|{
name|actualMin
operator|=
name|Integer
operator|.
name|parseInt
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
operator|++
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
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|actualMax
operator|=
name|Integer
operator|.
name|parseInt
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
operator|--
expr_stmt|;
block|}
block|}
return|return
name|IntPoint
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
name|IntPoint
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
return|return
name|val
return|;
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
name|IntPoint
operator|.
name|newExactQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|Integer
operator|.
name|parseInt
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
name|int
index|[]
name|values
init|=
operator|new
name|int
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
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|IntPoint
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
name|Integer
operator|.
name|toString
argument_list|(
name|IntPoint
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
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|result
operator|.
name|setLength
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|IntPoint
operator|.
name|encodeDimension
argument_list|(
name|Integer
operator|.
name|parseInt
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
name|Integer
operator|.
name|MIN_VALUE
else|:
name|Integer
operator|.
name|MAX_VALUE
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
name|Integer
operator|.
name|MAX_VALUE
else|:
name|Integer
operator|.
name|MIN_VALUE
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
name|INT
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"MultiValued Point fields with DocValues is not currently supported"
argument_list|)
throw|;
comment|//      return Type.SORTED_INTEGER;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|INTEGER_POINT
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
name|IntFieldSource
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
DECL|method|getNumericType
specifier|public
name|LegacyNumericType
name|getNumericType
parameter_list|()
block|{
return|return
name|LegacyNumericType
operator|.
name|INT
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
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isFieldUsed
argument_list|(
name|field
argument_list|)
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|boost
operator|!=
literal|1.0
operator|&&
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Can't use document/field boost for PointField. Field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|", boost: "
operator|+
name|boost
argument_list|)
expr_stmt|;
block|}
name|int
name|intValue
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
name|intValue
argument_list|()
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntPoint
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|intValue
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
name|Integer
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
annotation|@
name|Override
DECL|method|getType
specifier|public
name|PointTypes
name|getType
parameter_list|()
block|{
return|return
name|PointTypes
operator|.
name|INTEGER
return|;
block|}
block|}
end_class

end_unit

