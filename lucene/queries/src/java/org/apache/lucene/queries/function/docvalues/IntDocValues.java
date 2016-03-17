begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function.docvalues
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
name|docvalues
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
comment|/**  * Abstract {@link FunctionValues} implementation which supports retrieving int values.  * Implementations can control how the int values are loaded through {@link #intVal(int)}  */
end_comment

begin_class
DECL|class|IntDocValues
specifier|public
specifier|abstract
class|class
name|IntDocValues
extends|extends
name|FunctionValues
block|{
DECL|field|vs
specifier|protected
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|method|IntDocValues
specifier|public
name|IntDocValues
parameter_list|(
name|ValueSource
name|vs
parameter_list|)
block|{
name|this
operator|.
name|vs
operator|=
name|vs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|byteVal
specifier|public
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shortVal
specifier|public
name|short
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|floatVal
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
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|intVal
specifier|public
specifier|abstract
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doubleVal
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
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|intVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|objectVal
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|exists
argument_list|(
name|doc
argument_list|)
condition|?
name|intVal
argument_list|(
name|doc
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vs
operator|.
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
annotation|@
name|Override
DECL|method|getRangeScorer
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|LeafReaderContext
name|readerContext
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
name|int
name|lower
decl_stmt|,
name|upper
decl_stmt|;
comment|// instead of using separate comparison functions, adjust the endpoints.
if|if
condition|(
name|lowerVal
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
name|lower
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
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
name|upperVal
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
name|upper
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
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
name|readerContext
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
operator|!
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
return|return
literal|false
return|;
name|int
name|val
init|=
name|intVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
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
DECL|method|getValueFiller
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
name|exists
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

