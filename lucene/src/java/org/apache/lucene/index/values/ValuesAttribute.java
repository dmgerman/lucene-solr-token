begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Attribute
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FloatsRef
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
name|LongsRef
import|;
end_import

begin_interface
DECL|interface|ValuesAttribute
specifier|public
interface|interface
name|ValuesAttribute
extends|extends
name|Attribute
block|{
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
function_decl|;
DECL|method|bytes
specifier|public
name|BytesRef
name|bytes
parameter_list|()
function_decl|;
DECL|method|floats
specifier|public
name|FloatsRef
name|floats
parameter_list|()
function_decl|;
DECL|method|ints
specifier|public
name|LongsRef
name|ints
parameter_list|()
function_decl|;
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|Values
name|type
parameter_list|)
function_decl|;
DECL|method|bytesComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
parameter_list|()
function_decl|;
DECL|method|setBytesComparator
specifier|public
name|void
name|setBytesComparator
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

