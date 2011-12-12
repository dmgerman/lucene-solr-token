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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|StrDocValues
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
comment|/**  * Pass a the field value through as a String, no matter the type // Q: doesn't this mean it's a "string"?  *  **/
end_comment

begin_class
DECL|class|LiteralValueSource
specifier|public
class|class
name|LiteralValueSource
extends|extends
name|ValueSource
block|{
DECL|field|string
specifier|protected
specifier|final
name|String
name|string
decl_stmt|;
DECL|field|bytesRef
specifier|protected
specifier|final
name|BytesRef
name|bytesRef
decl_stmt|;
DECL|method|LiteralValueSource
specifier|public
name|LiteralValueSource
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|this
operator|.
name|string
operator|=
name|string
expr_stmt|;
name|this
operator|.
name|bytesRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
comment|/** returns the literal value */
DECL|method|getValue
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|string
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
return|return
operator|new
name|StrDocValues
argument_list|(
name|this
argument_list|)
block|{
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
name|string
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|bytesVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|target
parameter_list|)
block|{
name|target
operator|.
name|copyBytes
argument_list|(
name|bytesRef
argument_list|)
expr_stmt|;
return|return
literal|true
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
name|string
return|;
block|}
block|}
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
literal|"literal("
operator|+
name|string
operator|+
literal|")"
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
operator|!
operator|(
name|o
operator|instanceof
name|LiteralValueSource
operator|)
condition|)
return|return
literal|false
return|;
name|LiteralValueSource
name|that
init|=
operator|(
name|LiteralValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|string
operator|.
name|equals
argument_list|(
name|that
operator|.
name|string
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|field|hash
specifier|public
specifier|static
specifier|final
name|int
name|hash
init|=
name|LiteralValueSource
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
name|hash
operator|+
name|string
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

