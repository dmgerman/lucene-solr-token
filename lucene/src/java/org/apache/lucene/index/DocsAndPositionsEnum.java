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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Also iterates through positions. */
end_comment

begin_class
DECL|class|DocsAndPositionsEnum
specifier|public
specifier|abstract
class|class
name|DocsAndPositionsEnum
extends|extends
name|DocsEnum
block|{
comment|/** Returns the next position.  You should only call this    *  up to {@link DocsEnum#freq()} times else    *  the behavior is not defined. */
DECL|method|nextPosition
specifier|public
specifier|abstract
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns length of payload at current position */
DECL|method|getPayloadLength
specifier|public
specifier|abstract
name|int
name|getPayloadLength
parameter_list|()
function_decl|;
comment|/** Returns the payload at this position, or null if no    *  payload was indexed. */
DECL|method|getPayload
specifier|public
specifier|abstract
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|hasPayload
specifier|public
specifier|abstract
name|boolean
name|hasPayload
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|read
specifier|public
specifier|final
name|int
name|read
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getBulkResult
specifier|public
name|BulkReadResult
name|getBulkResult
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

