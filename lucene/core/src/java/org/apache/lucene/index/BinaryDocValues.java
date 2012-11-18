begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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

begin_comment
comment|// nocommit need marker interface?
end_comment

begin_class
DECL|class|BinaryDocValues
specifier|public
specifier|abstract
class|class
name|BinaryDocValues
block|{
comment|// nocommit throws IOE or not?
DECL|method|get
specifier|public
specifier|abstract
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
function_decl|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|BinaryDocValues
name|DEFAULT
init|=
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
block|{
name|ret
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

