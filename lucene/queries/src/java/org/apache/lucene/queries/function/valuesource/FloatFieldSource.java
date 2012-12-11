begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|FieldCache
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
name|MutableValueFloat
import|;
end_import

begin_comment
comment|/**  * Obtains float field values from the {@link org.apache.lucene.search.FieldCache}  * using<code>getFloats()</code>  * and makes those values available as other numeric types, casting as needed.  *  *  */
end_comment

begin_class
DECL|class|FloatFieldSource
specifier|public
class|class
name|FloatFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|parser
specifier|protected
specifier|final
name|FieldCache
operator|.
name|FloatParser
name|parser
decl_stmt|;
DECL|method|FloatFieldSource
specifier|public
name|FloatFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|FloatFieldSource
specifier|public
name|FloatFieldSource
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|FloatParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
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
literal|"float("
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
name|float
index|[]
name|arr
init|=
name|cache
operator|.
name|getFloats
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|,
name|parser
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|valid
init|=
name|cache
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
name|FloatDocValues
argument_list|(
name|this
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
return|return
name|arr
index|[
name|doc
index|]
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
return|return
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|?
name|arr
index|[
name|doc
index|]
else|:
literal|null
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
name|float
index|[]
name|floatArr
init|=
name|arr
decl_stmt|;
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
name|value
operator|=
name|floatArr
index|[
name|doc
index|]
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
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|FloatFieldSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|FloatFieldSource
name|other
init|=
operator|(
name|FloatFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
operator|(
name|this
operator|.
name|parser
operator|==
literal|null
condition|?
name|other
operator|.
name|parser
operator|==
literal|null
else|:
name|this
operator|.
name|parser
operator|.
name|getClass
argument_list|()
operator|==
name|other
operator|.
name|parser
operator|.
name|getClass
argument_list|()
operator|)
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
name|h
init|=
name|parser
operator|==
literal|null
condition|?
name|Float
operator|.
name|class
operator|.
name|hashCode
argument_list|()
else|:
name|parser
operator|.
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|+=
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

