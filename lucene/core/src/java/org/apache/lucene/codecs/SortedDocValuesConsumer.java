begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_class
DECL|class|SortedDocValuesConsumer
specifier|public
specifier|abstract
class|class
name|SortedDocValuesConsumer
block|{
comment|/** This is called, in value sort order, once per unique    *  value. */
DECL|method|addValue
specifier|public
specifier|abstract
name|void
name|addValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
function_decl|;
comment|/** This is called once per document after all values are    *  added. */
DECL|method|addDoc
specifier|public
specifier|abstract
name|void
name|addDoc
parameter_list|(
name|int
name|ord
parameter_list|)
function_decl|;
block|}
end_class

end_unit

