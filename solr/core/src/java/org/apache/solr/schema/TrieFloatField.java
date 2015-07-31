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
name|util
operator|.
name|Map
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|docvalues
operator|.
name|FloatDocValues
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
name|SortedSetFieldSource
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
name|SortedSetSelector
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
name|NumericUtils
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
name|MutableValueFloat
import|;
end_import

begin_comment
comment|/**  * A numeric field that can contain single-precision 32-bit IEEE 754   * floating point values.  *  *<ul>  *<li>Min Value Allowed: 1.401298464324817E-45</li>  *<li>Max Value Allowed: 3.4028234663852886E38</li>  *</ul>  *  *<b>NOTE:</b> The behavior of this class when given values of   * {@link Float#NaN}, {@link Float#NEGATIVE_INFINITY}, or   * {@link Float#POSITIVE_INFINITY} is undefined.  *   * @see Float  * @see<a href="http://java.sun.com/docs/books/jls/third_edition/html/typesValues.html#4.2.3">Java Language Specification, s4.2.3</a>  */
end_comment

begin_class
DECL|class|TrieFloatField
specifier|public
class|class
name|TrieFloatField
extends|extends
name|TrieField
implements|implements
name|FloatValueFieldType
block|{
block|{
name|type
operator|=
name|TrieTypes
operator|.
name|FLOAT
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
name|floatValue
argument_list|()
return|;
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
return|return
name|Float
operator|.
name|parseFloat
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
DECL|method|getSingleValueSource
specifier|protected
name|ValueSource
name|getSingleValueSource
parameter_list|(
name|SortedSetSelector
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
name|SortedSetFieldSource
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|choice
argument_list|)
block|{
annotation|@
name|Override
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
name|SortedSetFieldSource
name|thisAsSortedSetFieldSource
init|=
name|this
decl_stmt|;
comment|// needed for nested anon class ref
name|SortedSetDocValues
name|sortedSet
init|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|SortedDocValues
name|view
init|=
name|SortedSetSelector
operator|.
name|wrap
argument_list|(
name|sortedSet
argument_list|,
name|selector
argument_list|)
decl_stmt|;
return|return
operator|new
name|FloatDocValues
argument_list|(
name|thisAsSortedSetFieldSource
argument_list|)
block|{
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
name|BytesRef
name|bytes
init|=
name|view
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|bytes
argument_list|)
argument_list|)
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
operator|-
literal|1
operator|!=
name|view
operator|.
name|getOrd
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
name|MutableValueFloat
name|mval
init|=
operator|new
name|MutableValueFloat
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
name|mval
operator|.
name|exists
operator|=
name|exists
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|value
operator|=
name|mval
operator|.
name|exists
condition|?
name|floatVal
argument_list|(
name|doc
argument_list|)
else|:
literal|0.0F
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

