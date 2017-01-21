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
name|legacy
operator|.
name|LegacyNumericUtils
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|LongDocValues
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

begin_comment
comment|/**  * A numeric field that can contain 64-bit signed two's complement integer values.  *  *<ul>  *<li>Min Value Allowed: -9223372036854775808</li>  *<li>Max Value Allowed: 9223372036854775807</li>  *</ul>  *   * @see Long  */
end_comment

begin_class
DECL|class|TrieLongField
specifier|public
class|class
name|TrieLongField
extends|extends
name|TrieField
implements|implements
name|LongValueFieldType
block|{
block|{
name|type
operator|=
name|NumberType
operator|.
name|LONG
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
name|longValue
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
name|Long
operator|.
name|parseLong
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
name|Double
name|v
init|=
name|Double
operator|.
name|parseDouble
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
name|longValue
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
name|LongDocValues
argument_list|(
name|thisAsSortedSetFieldSource
argument_list|)
block|{
specifier|private
name|int
name|lastDocID
decl_stmt|;
specifier|private
name|boolean
name|setDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docID
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docs out of order: lastDocID="
operator|+
name|lastDocID
operator|+
literal|" docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
if|if
condition|(
name|docID
operator|>
name|view
operator|.
name|docID
argument_list|()
condition|)
block|{
name|lastDocID
operator|=
name|docID
expr_stmt|;
return|return
name|docID
operator|==
name|view
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|docID
operator|==
name|view
operator|.
name|docID
argument_list|()
return|;
block|}
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|setDoc
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|BytesRef
name|bytes
init|=
name|view
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|>
literal|0
assert|;
return|return
name|LegacyNumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|bytes
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0L
return|;
block|}
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
throws|throws
name|IOException
block|{
return|return
name|setDoc
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|setDoc
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|mval
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|mval
operator|.
name|value
operator|=
name|LegacyNumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|view
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mval
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|mval
operator|.
name|value
operator|=
literal|0L
expr_stmt|;
block|}
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

