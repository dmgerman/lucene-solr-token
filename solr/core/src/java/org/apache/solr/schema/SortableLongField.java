begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|DocTermsIndexDocValues
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
name|FieldCacheSource
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
name|UnicodeUtil
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
name|mutable
operator|.
name|MutableValue
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
name|mutable
operator|.
name|MutableValueLong
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
name|lucene
operator|.
name|index
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
name|solr
operator|.
name|util
operator|.
name|NumberUtils
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
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * A legacy numeric field type that encodes "Long" values as Strings such   * that Term enumeration order matches the natural numeric order.  This class   * should not be used except by people with existing indexes that already   * contain fields of this type.  New schemas should use {@link TrieLongField}.  *  *<p>  * The naming convention "Sortable" comes from the fact that both the numeric   * values and encoded String representations Sort identically (as opposed to   * a direct String representation where values such as "11" sort before values   * such as "2").  *</p>  *   * @see TrieLongField  * @deprecated use {@link LongField} or {@link TrieLongField} - will be removed in 5.x  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SortableLongField
specifier|public
class|class
name|SortableLongField
extends|extends
name|PrimitiveFieldType
block|{
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
name|reverse
parameter_list|)
block|{
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|)
return|;
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
argument_list|(
name|qparser
argument_list|)
expr_stmt|;
return|return
operator|new
name|SortableLongFieldSource
argument_list|(
name|field
operator|.
name|name
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
return|return
name|NumberUtils
operator|.
name|long2sortableStr
argument_list|(
name|val
argument_list|)
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
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|indexedForm
argument_list|)
return|;
block|}
DECL|method|indexedToReadable
specifier|public
name|CharsRef
name|indexedToReadable
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|CharsRef
name|charsRef
parameter_list|)
block|{
comment|// TODO: this could be more efficient, but the sortable types should be deprecated instead
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|input
argument_list|,
name|charsRef
argument_list|)
expr_stmt|;
specifier|final
name|char
index|[]
name|indexedToReadable
init|=
name|indexedToReadable
argument_list|(
name|charsRef
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|charsRef
operator|.
name|copyChars
argument_list|(
name|indexedToReadable
argument_list|,
literal|0
argument_list|,
name|indexedToReadable
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|charsRef
return|;
block|}
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
return|return
name|indexedToReadable
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Long
name|toObject
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
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
name|String
name|sval
init|=
name|f
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|writeLong
argument_list|(
name|name
argument_list|,
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|sval
argument_list|,
literal|0
argument_list|,
name|sval
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|SortableLongFieldSource
class|class
name|SortableLongFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|defVal
specifier|protected
name|long
name|defVal
decl_stmt|;
DECL|method|SortableLongFieldSource
specifier|public
name|SortableLongFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|SortableLongFieldSource
specifier|public
name|SortableLongFieldSource
parameter_list|(
name|String
name|field
parameter_list|,
name|long
name|defVal
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|defVal
operator|=
name|defVal
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
literal|"slong("
operator|+
name|field
operator|+
literal|')'
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
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|def
init|=
name|defVal
decl_stmt|;
return|return
operator|new
name|DocTermsIndexDocValues
argument_list|(
name|this
argument_list|,
name|readerContext
argument_list|,
name|field
argument_list|)
block|{
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|toTerm
parameter_list|(
name|String
name|readableValue
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|long2sortableStr
argument_list|(
name|readableValue
argument_list|)
return|;
block|}
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
name|longVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|longVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|ord
operator|==
literal|0
condition|?
name|def
else|:
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|spare
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|5
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
return|return
operator|(
name|double
operator|)
name|longVal
argument_list|(
name|doc
argument_list|)
return|;
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
return|return
name|Long
operator|.
name|toString
argument_list|(
name|longVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
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
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|ord
operator|==
literal|0
condition|?
literal|null
else|:
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|spare
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
name|longVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueLong
name|mval
init|=
operator|new
name|MutableValueLong
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|mval
operator|.
name|value
operator|=
name|def
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|mval
operator|.
name|value
operator|=
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|spare
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
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
return|return
name|o
operator|instanceof
name|SortableLongFieldSource
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
name|defVal
operator|==
operator|(
operator|(
name|SortableLongFieldSource
operator|)
name|o
operator|)
operator|.
name|defVal
return|;
block|}
DECL|field|hcode
specifier|private
specifier|static
name|int
name|hcode
init|=
name|SortableLongFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hcode
operator|+
name|super
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|int
operator|)
name|defVal
return|;
block|}
empty_stmt|;
block|}
end_class

end_unit

