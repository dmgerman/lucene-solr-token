begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.legacy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|legacy
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
name|Field
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
name|IndexOptions
import|;
end_import

begin_comment
comment|/**  * Field extension with support for legacy numerics  * @deprecated Please switch to {@link org.apache.lucene.index.PointValues} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyField
specifier|public
class|class
name|LegacyField
extends|extends
name|Field
block|{
comment|/**    * Expert: creates a field with no initial value.    * Intended only for custom LegacyField subclasses.    * @param name field name    * @param type field type    * @throws IllegalArgumentException if either the name or type    *         is null.    */
DECL|method|LegacyField
specifier|public
name|LegacyField
parameter_list|(
name|String
name|name
parameter_list|,
name|LegacyFieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// Not indexed
return|return
literal|null
return|;
block|}
specifier|final
name|LegacyFieldType
name|fieldType
init|=
operator|(
name|LegacyFieldType
operator|)
name|fieldType
argument_list|()
decl_stmt|;
specifier|final
name|LegacyNumericType
name|numericType
init|=
name|fieldType
operator|.
name|numericType
argument_list|()
decl_stmt|;
if|if
condition|(
name|numericType
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|reuse
operator|instanceof
name|LegacyNumericTokenStream
operator|&&
operator|(
operator|(
name|LegacyNumericTokenStream
operator|)
name|reuse
operator|)
operator|.
name|getPrecisionStep
argument_list|()
operator|==
name|fieldType
operator|.
name|numericPrecisionStep
argument_list|()
operator|)
condition|)
block|{
comment|// lazy init the TokenStream as it is heavy to instantiate
comment|// (attributes,...) if not needed (stored field loading)
name|reuse
operator|=
operator|new
name|LegacyNumericTokenStream
argument_list|(
name|fieldType
operator|.
name|numericPrecisionStep
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LegacyNumericTokenStream
name|nts
init|=
operator|(
name|LegacyNumericTokenStream
operator|)
name|reuse
decl_stmt|;
comment|// initialize value in TokenStream
specifier|final
name|Number
name|val
init|=
operator|(
name|Number
operator|)
name|fieldsData
decl_stmt|;
switch|switch
condition|(
name|numericType
condition|)
block|{
case|case
name|INT
case|:
name|nts
operator|.
name|setIntValue
argument_list|(
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|nts
operator|.
name|setLongValue
argument_list|(
name|val
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|nts
operator|.
name|setFloatValue
argument_list|(
name|val
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|nts
operator|.
name|setDoubleValue
argument_list|(
name|val
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Should never get here"
argument_list|)
throw|;
block|}
return|return
name|reuse
return|;
block|}
return|return
name|super
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|,
name|reuse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTokenStream
specifier|public
name|void
name|setTokenStream
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
specifier|final
name|LegacyFieldType
name|fieldType
init|=
operator|(
name|LegacyFieldType
operator|)
name|fieldType
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldType
operator|.
name|numericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set private TokenStream on numeric fields"
argument_list|)
throw|;
block|}
name|super
operator|.
name|setTokenStream
argument_list|(
name|tokenStream
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

