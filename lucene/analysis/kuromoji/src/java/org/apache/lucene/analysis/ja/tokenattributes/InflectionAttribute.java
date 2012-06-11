begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|tokenattributes
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
name|analysis
operator|.
name|ja
operator|.
name|Token
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

begin_comment
comment|/**  * Attribute for Kuromoji inflection data.  *<p>  * Note: in some cases this value may not be applicable,  * and will be null.  */
end_comment

begin_interface
DECL|interface|InflectionAttribute
specifier|public
interface|interface
name|InflectionAttribute
extends|extends
name|Attribute
block|{
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|()
function_decl|;
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|()
function_decl|;
DECL|method|setToken
specifier|public
name|void
name|setToken
parameter_list|(
name|Token
name|token
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

