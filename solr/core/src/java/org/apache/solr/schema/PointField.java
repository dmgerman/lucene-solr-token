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
name|io
operator|.
name|IOException
import|;
end_import

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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|document
operator|.
name|SortedNumericDocValuesField
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
name|IndexOrDocValuesQuery
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
name|CharsRef
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
name|CharsRefBuilder
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
name|common
operator|.
name|SolrException
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
name|response
operator|.
name|TextResponseWriter
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
comment|/**  * Provides field types to support for Lucene's {@link  * org.apache.lucene.document.IntPoint}, {@link org.apache.lucene.document.LongPoint}, {@link org.apache.lucene.document.FloatPoint} and  * {@link org.apache.lucene.document.DoublePoint}.  * See {@link org.apache.lucene.search.PointRangeQuery} for more details.  * It supports integer, float, long and double types. See subclasses for details.  *<br>  * {@code DocValues} are supported for single-value cases ({@code NumericDocValues}).  * {@code FieldCache} is not supported for {@code PointField}s, so sorting, faceting, etc on these fields require the use of {@code docValues="true"} in the schema.  */
end_comment

begin_class
DECL|class|PointField
specifier|public
specifier|abstract
class|class
name|PointField
extends|extends
name|NumericFieldType
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
DECL|method|isPointField
specifier|public
name|boolean
name|isPointField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getSingleValueSource
specifier|public
specifier|final
name|ValueSource
name|getSingleValueSource
parameter_list|(
name|MultiValueSelector
name|choice
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
comment|// trivial base case
if|if
condition|(
operator|!
name|field
operator|.
name|multiValued
argument_list|()
condition|)
block|{
comment|// single value matches any selector
return|return
name|getValueSource
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
return|;
block|}
comment|// Point fields don't support UninvertingReader. See SOLR-9202
if|if
condition|(
operator|!
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"docValues='true' is required to select '"
operator|+
name|choice
operator|.
name|toString
argument_list|()
operator|+
literal|"' value from multivalued field ("
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|") at query time"
argument_list|)
throw|;
block|}
comment|// multivalued Point fields all use SortedSetDocValues, so we give a clean error if that's
comment|// not supported by the specified choice, else we delegate to a helper
name|SortedNumericSelector
operator|.
name|Type
name|selectorType
init|=
name|choice
operator|.
name|getSortedNumericSelectorType
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|selectorType
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|choice
operator|.
name|toString
argument_list|()
operator|+
literal|" is not a supported option for picking a single value"
operator|+
literal|" from the multivalued field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|" (type: "
operator|+
name|this
operator|.
name|getTypeName
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|getSingleValueSource
argument_list|(
name|selectorType
argument_list|,
name|field
argument_list|)
return|;
block|}
comment|/**    * Helper method that will only be called for multivalued Point fields that have doc values.    * Default impl throws an error indicating that selecting a single value from this multivalued     * field is not supported for this field type    *    * @param choice the selector Type to use, will never be null    * @param field the field to use, guaranteed to be multivalued.    * @see #getSingleValueSource(MultiValueSelector,SchemaField,QParser)     */
DECL|method|getSingleValueSource
specifier|protected
specifier|abstract
name|ValueSource
name|getSingleValueSource
parameter_list|(
name|SortedNumericSelector
operator|.
name|Type
name|choice
parameter_list|,
name|SchemaField
name|field
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|multiValuedFieldCache
specifier|public
name|boolean
name|multiValuedFieldCache
parameter_list|()
block|{
return|return
literal|false
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
name|externalVals
parameter_list|)
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
name|externalVals
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
operator|&&
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
comment|// currently implemented as singleton range
return|return
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|externalVal
argument_list|,
name|externalVal
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
operator|&&
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|Query
name|pointsQuery
init|=
name|getExactQuery
argument_list|(
name|field
argument_list|,
name|externalVal
argument_list|)
decl_stmt|;
name|Query
name|dvQuery
init|=
name|getDocValuesRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|externalVal
argument_list|,
name|externalVal
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|IndexOrDocValuesQuery
argument_list|(
name|pointsQuery
argument_list|,
name|dvQuery
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getExactQuery
argument_list|(
name|field
argument_list|,
name|externalVal
argument_list|)
return|;
block|}
block|}
DECL|method|getExactQuery
specifier|protected
specifier|abstract
name|Query
name|getExactQuery
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
function_decl|;
DECL|method|getPointRangeQuery
specifier|public
specifier|abstract
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
function_decl|;
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
if|if
condition|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
operator|&&
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
return|return
name|getDocValuesRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
operator|&&
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|Query
name|pointsQuery
init|=
name|getPointRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
decl_stmt|;
name|Query
name|dvQuery
init|=
name|getDocValuesRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
decl_stmt|;
return|return
operator|new
name|IndexOrDocValuesQuery
argument_list|(
name|pointsQuery
argument_list|,
name|dvQuery
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getPointRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|storedToReadable
specifier|public
name|String
name|storedToReadable
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
return|return
name|toExternal
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Can't generate internal string in PointField. use PointField.toInternalByteRef"
argument_list|)
throw|;
block|}
DECL|method|toInternalByteRef
specifier|public
name|BytesRef
name|toInternalByteRef
parameter_list|(
name|String
name|val
parameter_list|)
block|{
specifier|final
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|readableToIndexed
argument_list|(
name|val
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|IndexableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeVal
argument_list|(
name|name
argument_list|,
name|toObject
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storedToIndexed
specifier|public
name|String
name|storedToIndexed
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported with PointFields"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|CharsRef
name|indexedToReadable
parameter_list|(
name|BytesRef
name|indexedForm
parameter_list|,
name|CharsRefBuilder
name|charsRef
parameter_list|)
block|{
specifier|final
name|String
name|value
init|=
name|indexedToReadable
argument_list|(
name|indexedForm
argument_list|)
decl_stmt|;
name|charsRef
operator|.
name|grow
argument_list|(
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|charsRef
operator|.
name|setLength
argument_list|(
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|charsRef
operator|.
name|length
argument_list|()
argument_list|,
name|charsRef
operator|.
name|chars
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|charsRef
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|indexedForm
parameter_list|)
block|{
return|return
name|indexedToReadable
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|indexedForm
argument_list|)
argument_list|)
return|;
block|}
DECL|method|indexedToReadable
specifier|protected
specifier|abstract
name|String
name|indexedToReadable
parameter_list|(
name|BytesRef
name|indexedForm
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getPrefixQuery
specifier|public
name|Query
name|getPrefixQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|String
name|termStr
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can't run prefix queries on numeric fields"
argument_list|)
throw|;
block|}
DECL|method|isFieldUsed
specifier|protected
name|boolean
name|isFieldUsed
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
name|boolean
name|indexed
init|=
name|field
operator|.
name|indexed
argument_list|()
decl_stmt|;
name|boolean
name|stored
init|=
name|field
operator|.
name|stored
argument_list|()
decl_stmt|;
name|boolean
name|docValues
init|=
name|field
operator|.
name|hasDocValues
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|indexed
operator|&&
operator|!
name|stored
operator|&&
operator|!
name|docValues
condition|)
block|{
if|if
condition|(
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
literal|"Ignoring unindexed/unstored field: "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|IndexableField
argument_list|>
name|createFields
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isFieldUsed
argument_list|(
name|sf
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|IndexableField
name|field
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|indexed
argument_list|()
condition|)
block|{
name|field
operator|=
name|createField
argument_list|(
name|sf
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sf
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
specifier|final
name|Number
name|numericValue
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
specifier|final
name|Object
name|nativeTypeObject
init|=
name|toNativeType
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|getNumberType
argument_list|()
operator|==
name|NumberType
operator|.
name|DATE
condition|)
block|{
name|numericValue
operator|=
operator|(
operator|(
name|Date
operator|)
name|nativeTypeObject
operator|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numericValue
operator|=
operator|(
name|Number
operator|)
name|nativeTypeObject
expr_stmt|;
block|}
block|}
else|else
block|{
name|numericValue
operator|=
name|field
operator|.
name|numericValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|bits
decl_stmt|;
if|if
condition|(
operator|!
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
if|if
condition|(
name|numericValue
operator|instanceof
name|Integer
operator|||
name|numericValue
operator|instanceof
name|Long
condition|)
block|{
name|bits
operator|=
name|numericValue
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numericValue
operator|instanceof
name|Float
condition|)
block|{
name|bits
operator|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|numericValue
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|numericValue
operator|instanceof
name|Double
assert|;
name|bits
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|numericValue
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|bits
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// MultiValued
if|if
condition|(
name|numericValue
operator|instanceof
name|Integer
operator|||
name|numericValue
operator|instanceof
name|Long
condition|)
block|{
name|bits
operator|=
name|numericValue
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numericValue
operator|instanceof
name|Float
condition|)
block|{
name|bits
operator|=
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|numericValue
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|numericValue
operator|instanceof
name|Double
assert|;
name|bits
operator|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|numericValue
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|bits
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sf
operator|.
name|stored
argument_list|()
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|getStoredField
argument_list|(
name|sf
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
DECL|method|getStoredField
specifier|protected
specifier|abstract
name|StoredField
name|getStoredField
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
block|}
end_class

end_unit

