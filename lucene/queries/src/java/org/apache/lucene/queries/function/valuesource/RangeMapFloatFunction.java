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
name|IndexSearcher
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<code>RangeMapFloatFunction</code> implements a map function over  * another {@link ValueSource} whose values fall within min and max inclusive to target.  *<br>  * Normally Used as an argument to a {@link org.apache.lucene.queries.function.FunctionQuery}  *  *  */
end_comment

begin_class
DECL|class|RangeMapFloatFunction
specifier|public
class|class
name|RangeMapFloatFunction
extends|extends
name|ValueSource
block|{
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|min
specifier|protected
specifier|final
name|float
name|min
decl_stmt|;
DECL|field|max
specifier|protected
specifier|final
name|float
name|max
decl_stmt|;
DECL|field|target
specifier|protected
specifier|final
name|ValueSource
name|target
decl_stmt|;
DECL|field|defaultVal
specifier|protected
specifier|final
name|ValueSource
name|defaultVal
decl_stmt|;
DECL|method|RangeMapFloatFunction
specifier|public
name|RangeMapFloatFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|float
name|min
parameter_list|,
name|float
name|max
parameter_list|,
name|float
name|target
parameter_list|,
name|Float
name|def
parameter_list|)
block|{
name|this
argument_list|(
name|source
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
name|target
argument_list|)
argument_list|,
name|def
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ConstValueSource
argument_list|(
name|def
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|RangeMapFloatFunction
specifier|public
name|RangeMapFloatFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|float
name|min
parameter_list|,
name|float
name|max
parameter_list|,
name|ValueSource
name|target
parameter_list|,
name|ValueSource
name|def
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|defaultVal
operator|=
name|def
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
literal|"map("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
name|min
operator|+
literal|","
operator|+
name|max
operator|+
literal|","
operator|+
name|target
operator|.
name|description
argument_list|()
operator|+
literal|")"
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
name|FunctionValues
name|vals
init|=
name|source
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|targets
init|=
name|target
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|defaults
init|=
operator|(
name|this
operator|.
name|defaultVal
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|defaultVal
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
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
name|float
name|val
init|=
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
operator|(
name|val
operator|>=
name|min
operator|&&
name|val
operator|<=
name|max
operator|)
condition|?
name|targets
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
else|:
operator|(
name|defaultVal
operator|==
literal|null
condition|?
name|val
else|:
name|defaults
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
operator|)
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
literal|"map("
operator|+
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|",min="
operator|+
name|min
operator|+
literal|",max="
operator|+
name|max
operator|+
literal|",target="
operator|+
name|targets
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|source
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
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
name|source
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|10
operator|)
operator||
operator|(
name|h
operator|>>>
literal|23
operator|)
expr_stmt|;
name|h
operator|+=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|14
operator|)
operator||
operator|(
name|h
operator|>>>
literal|19
operator|)
expr_stmt|;
name|h
operator|+=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|h
operator|+=
name|target
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|defaultVal
operator|!=
literal|null
condition|)
name|h
operator|+=
name|defaultVal
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
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
name|RangeMapFloatFunction
operator|.
name|class
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RangeMapFloatFunction
name|other
init|=
operator|(
name|RangeMapFloatFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|min
operator|==
name|other
operator|.
name|min
operator|&&
name|this
operator|.
name|max
operator|==
name|other
operator|.
name|max
operator|&&
name|this
operator|.
name|target
operator|.
name|equals
argument_list|(
name|other
operator|.
name|target
argument_list|)
operator|&&
name|this
operator|.
name|source
operator|.
name|equals
argument_list|(
name|other
operator|.
name|source
argument_list|)
operator|&&
operator|(
name|this
operator|.
name|defaultVal
operator|==
name|other
operator|.
name|defaultVal
operator|||
operator|(
name|this
operator|.
name|defaultVal
operator|!=
literal|null
operator|&&
name|this
operator|.
name|defaultVal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|defaultVal
argument_list|)
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

