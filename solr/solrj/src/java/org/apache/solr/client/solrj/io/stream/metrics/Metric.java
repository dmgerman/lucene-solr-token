begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import

begin_interface
DECL|interface|Metric
specifier|public
interface|interface
name|Metric
extends|extends
name|Serializable
block|{
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
function_decl|;
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
function_decl|;
DECL|method|newInstance
specifier|public
name|Metric
name|newInstance
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

