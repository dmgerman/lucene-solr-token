begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.index
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|index
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
name|MergePolicy
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
name|NoMergePolicy
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
name|core
operator|.
name|SolrResourceLoader
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
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_class
DECL|class|NoMergePolicyFactory
specifier|public
class|class
name|NoMergePolicyFactory
extends|extends
name|SimpleMergePolicyFactory
block|{
DECL|method|NoMergePolicyFactory
specifier|public
name|NoMergePolicyFactory
parameter_list|(
name|SolrResourceLoader
name|resourceLoader
parameter_list|,
name|MergePolicyFactoryArgs
name|args
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|resourceLoader
argument_list|,
name|args
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergePolicyInstance
specifier|protected
name|MergePolicy
name|getMergePolicyInstance
parameter_list|()
block|{
return|return
name|NoMergePolicy
operator|.
name|INSTANCE
return|;
block|}
block|}
end_class

end_unit

