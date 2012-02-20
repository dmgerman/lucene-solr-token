begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.strategy.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|strategy
operator|.
name|util
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
name|NumericTokenStream
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
name|DoubleField
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
name|document
operator|.
name|FieldType
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

begin_comment
comment|/**  * Hold some of the parameters used by solr...  */
end_comment

begin_class
DECL|class|NumericFieldInfo
specifier|public
class|class
name|NumericFieldInfo
block|{
DECL|field|precisionStep
specifier|public
name|int
name|precisionStep
init|=
literal|8
decl_stmt|;
comment|// same as solr default
DECL|field|store
specifier|public
name|boolean
name|store
init|=
literal|true
decl_stmt|;
DECL|field|index
specifier|public
name|boolean
name|index
init|=
literal|true
decl_stmt|;
DECL|method|setPrecisionStep
specifier|public
name|void
name|setPrecisionStep
parameter_list|(
name|int
name|p
parameter_list|)
block|{
name|precisionStep
operator|=
name|p
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<=
literal|0
operator|||
name|precisionStep
operator|>=
literal|64
condition|)
name|precisionStep
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
DECL|method|createDouble
specifier|public
name|IndexableField
name|createDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|v
parameter_list|)
block|{
if|if
condition|(
operator|!
name|store
operator|&&
operator|!
name|index
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must be indexed or stored"
argument_list|)
throw|;
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|(
name|DoubleField
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|setStored
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setIndexed
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setNumericPrecisionStep
argument_list|(
name|precisionStep
argument_list|)
expr_stmt|;
return|return
operator|new
name|DoubleField
argument_list|(
name|name
argument_list|,
name|v
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

