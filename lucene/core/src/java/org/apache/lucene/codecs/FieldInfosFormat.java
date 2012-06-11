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
name|index
operator|.
name|FieldInfos
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**  * Encodes/decodes {@link FieldInfos}  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FieldInfosFormat
specifier|public
specifier|abstract
class|class
name|FieldInfosFormat
block|{
comment|/** Returns a {@link FieldInfosReader} to read field infos    *  from the index */
DECL|method|getFieldInfosReader
specifier|public
specifier|abstract
name|FieldInfosReader
name|getFieldInfosReader
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns a {@link FieldInfosWriter} to write field infos    *  to the index */
DECL|method|getFieldInfosWriter
specifier|public
specifier|abstract
name|FieldInfosWriter
name|getFieldInfosWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

