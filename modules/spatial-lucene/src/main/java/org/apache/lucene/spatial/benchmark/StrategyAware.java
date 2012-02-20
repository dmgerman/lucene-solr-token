begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.benchmark
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|benchmark
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
name|spatial
operator|.
name|base
operator|.
name|context
operator|.
name|SpatialContext
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
name|spatial
operator|.
name|strategy
operator|.
name|SpatialFieldInfo
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
name|spatial
operator|.
name|strategy
operator|.
name|SpatialStrategy
import|;
end_import

begin_interface
DECL|interface|StrategyAware
specifier|public
interface|interface
name|StrategyAware
parameter_list|<
name|T
extends|extends
name|SpatialFieldInfo
parameter_list|>
block|{
DECL|method|createFieldInfo
name|T
name|createFieldInfo
parameter_list|()
function_decl|;
DECL|method|createSpatialStrategy
name|SpatialStrategy
argument_list|<
name|T
argument_list|>
name|createSpatialStrategy
parameter_list|()
function_decl|;
DECL|method|getSpatialContext
name|SpatialContext
name|getSpatialContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

