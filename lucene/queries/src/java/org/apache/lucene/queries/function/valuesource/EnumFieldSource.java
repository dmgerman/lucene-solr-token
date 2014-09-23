begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
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
name|IndexReader
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
name|ValueSourceScorer
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
name|IntDocValues
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
name|MutableValueInt
import|;
end_import

begin_comment
comment|/**  * Obtains int field values from {@link org.apache.lucene.index.LeafReader#getNumericDocValues} and makes  * those values available as other numeric types, casting as needed.  * strVal of the value is not the int value, but its string (displayed) value  */
end_comment

begin_class
DECL|class|EnumFieldSource
specifier|public
class|class
name|EnumFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|DEFAULT_VALUE
specifier|static
specifier|final
name|Integer
name|DEFAULT_VALUE
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|enumIntToStringMap
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|enumIntToStringMap
decl_stmt|;
DECL|field|enumStringToIntMap
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|enumStringToIntMap
decl_stmt|;
DECL|method|EnumFieldSource
specifier|public
name|EnumFieldSource
parameter_list|(
name|String
name|field
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|enumIntToStringMap
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|enumStringToIntMap
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|enumIntToStringMap
operator|=
name|enumIntToStringMap
expr_stmt|;
name|this
operator|.
name|enumStringToIntMap
operator|=
name|enumStringToIntMap
expr_stmt|;
block|}
DECL|method|tryParseInt
specifier|private
specifier|static
name|Integer
name|tryParseInt
parameter_list|(
name|String
name|valueStr
parameter_list|)
block|{
name|Integer
name|intValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|intValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|valueStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{     }
return|return
name|intValue
return|;
block|}
DECL|method|intValueToStringValue
specifier|private
name|String
name|intValueToStringValue
parameter_list|(
name|Integer
name|intVal
parameter_list|)
block|{
if|if
condition|(
name|intVal
operator|==
literal|null
condition|)
return|return
literal|null
return|;
specifier|final
name|String
name|enumString
init|=
name|enumIntToStringMap
operator|.
name|get
argument_list|(
name|intVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumString
operator|!=
literal|null
condition|)
return|return
name|enumString
return|;
comment|// can't find matching enum name - return DEFAULT_VALUE.toString()
return|return
name|DEFAULT_VALUE
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|stringValueToIntValue
specifier|private
name|Integer
name|stringValueToIntValue
parameter_list|(
name|String
name|stringVal
parameter_list|)
block|{
if|if
condition|(
name|stringVal
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Integer
name|intValue
decl_stmt|;
specifier|final
name|Integer
name|enumInt
init|=
name|enumStringToIntMap
operator|.
name|get
argument_list|(
name|stringVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumInt
operator|!=
literal|null
condition|)
comment|//enum int found for string
return|return
name|enumInt
return|;
comment|//enum int not found for string
name|intValue
operator|=
name|tryParseInt
argument_list|(
name|stringVal
argument_list|)
expr_stmt|;
if|if
condition|(
name|intValue
operator|==
literal|null
condition|)
comment|//not Integer
name|intValue
operator|=
name|DEFAULT_VALUE
expr_stmt|;
specifier|final
name|String
name|enumString
init|=
name|enumIntToStringMap
operator|.
name|get
argument_list|(
name|intValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumString
operator|!=
literal|null
condition|)
comment|//has matching string
return|return
name|intValue
return|;
return|return
name|DEFAULT_VALUE
return|;
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
literal|"enum("
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
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|arr
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|valid
init|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntDocValues
argument_list|(
name|this
argument_list|)
block|{
specifier|final
name|MutableValueInt
name|val
init|=
operator|new
name|MutableValueInt
argument_list|()
decl_stmt|;
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
name|arr
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
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|Integer
name|intValue
init|=
name|intVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|intValueToStringValue
argument_list|(
name|intValue
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
name|valid
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
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|Integer
name|lower
init|=
name|stringValueToIntValue
argument_list|(
name|lowerVal
argument_list|)
decl_stmt|;
name|Integer
name|upper
init|=
name|stringValueToIntValue
argument_list|(
name|upperVal
argument_list|)
decl_stmt|;
comment|// instead of using separate comparison functions, adjust the endpoints.
if|if
condition|(
name|lower
operator|==
literal|null
condition|)
block|{
name|lower
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|includeLower
operator|&&
name|lower
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
name|lower
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|upper
operator|==
literal|null
condition|)
block|{
name|upper
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|includeUpper
operator|&&
name|upper
operator|>
name|Integer
operator|.
name|MIN_VALUE
condition|)
name|upper
operator|--
expr_stmt|;
block|}
specifier|final
name|int
name|ll
init|=
name|lower
decl_stmt|;
specifier|final
name|int
name|uu
init|=
name|upper
decl_stmt|;
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|val
init|=
name|intVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// only check for deleted if it's the default value
comment|// if (val==0&& reader.isDeleted(doc)) return false;
return|return
name|val
operator|>=
name|ll
operator|&&
name|val
operator|<=
name|uu
return|;
block|}
block|}
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
name|MutableValueInt
name|mval
init|=
operator|new
name|MutableValueInt
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
name|value
operator|=
name|intVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|EnumFieldSource
name|that
init|=
operator|(
name|EnumFieldSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|enumIntToStringMap
operator|.
name|equals
argument_list|(
name|that
operator|.
name|enumIntToStringMap
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|enumStringToIntMap
operator|.
name|equals
argument_list|(
name|that
operator|.
name|enumStringToIntMap
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|enumIntToStringMap
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|enumStringToIntMap
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

