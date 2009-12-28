begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
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
name|index
operator|.
name|AbstractAllTermDocs
import|;
end_import

begin_class
DECL|class|InstantiatedAllTermDocs
class|class
name|InstantiatedAllTermDocs
extends|extends
name|AbstractAllTermDocs
block|{
DECL|field|reader
specifier|private
name|InstantiatedIndexReader
name|reader
decl_stmt|;
DECL|method|InstantiatedAllTermDocs
name|InstantiatedAllTermDocs
parameter_list|(
name|InstantiatedIndexReader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|reader
operator|.
name|isDeleted
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

