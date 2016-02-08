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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|MergePolicy
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

begin_comment
comment|/**  * A {@link MergePolicyFactory} for wrapping additional {@link MergePolicyFactory factories}.  */
end_comment

begin_class
DECL|class|WrapperMergePolicyFactory
specifier|public
specifier|abstract
class|class
name|WrapperMergePolicyFactory
extends|extends
name|MergePolicyFactory
block|{
DECL|field|CLASS
specifier|private
specifier|static
specifier|final
name|String
name|CLASS
init|=
literal|"class"
decl_stmt|;
DECL|field|NO_SUB_PACKAGES
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|NO_SUB_PACKAGES
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|WRAPPED_PREFIX
specifier|static
specifier|final
name|String
name|WRAPPED_PREFIX
init|=
literal|"wrapped.prefix"
decl_stmt|;
comment|// not private so that test(s) can use it
DECL|field|wrappedMergePolicyArgs
specifier|private
specifier|final
name|MergePolicyFactoryArgs
name|wrappedMergePolicyArgs
decl_stmt|;
DECL|method|WrapperMergePolicyFactory
specifier|protected
name|WrapperMergePolicyFactory
parameter_list|(
name|SolrResourceLoader
name|resourceLoader
parameter_list|,
name|MergePolicyFactoryArgs
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|resourceLoader
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|wrappedMergePolicyArgs
operator|=
name|filterWrappedMergePolicyFactoryArgs
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the default wrapped {@link MergePolicy}. This is called if the factory settings do not explicitly specify    * the wrapped policy.    */
DECL|method|getDefaultWrappedMergePolicy
specifier|protected
name|MergePolicy
name|getDefaultWrappedMergePolicy
parameter_list|()
block|{
specifier|final
name|MergePolicyFactory
name|mpf
init|=
operator|new
name|DefaultMergePolicyFactory
argument_list|()
decl_stmt|;
return|return
name|mpf
operator|.
name|getMergePolicy
argument_list|()
return|;
block|}
comment|/** Returns an instance of the wrapped {@link MergePolicy} after it has been configured with all set parameters. */
DECL|method|getWrappedMergePolicy
specifier|protected
specifier|final
name|MergePolicy
name|getWrappedMergePolicy
parameter_list|()
block|{
if|if
condition|(
name|wrappedMergePolicyArgs
operator|==
literal|null
condition|)
block|{
return|return
name|getDefaultWrappedMergePolicy
argument_list|()
return|;
block|}
specifier|final
name|String
name|className
init|=
operator|(
name|String
operator|)
name|wrappedMergePolicyArgs
operator|.
name|remove
argument_list|(
name|CLASS
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Class name not defined for wrapped MergePolicyFactory!"
argument_list|)
throw|;
block|}
specifier|final
name|MergePolicyFactory
name|mpf
init|=
name|resourceLoader
operator|.
name|newInstance
argument_list|(
name|className
argument_list|,
name|MergePolicyFactory
operator|.
name|class
argument_list|,
name|NO_SUB_PACKAGES
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SolrResourceLoader
operator|.
name|class
block|,
name|MergePolicyFactoryArgs
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|resourceLoader
block|,
name|wrappedMergePolicyArgs
block|}
argument_list|)
decl_stmt|;
return|return
name|mpf
operator|.
name|getMergePolicy
argument_list|()
return|;
block|}
comment|/**    * Returns a {@link MergePolicyFactoryArgs} for the wrapped {@link MergePolicyFactory}. This method also removes all    * args from this instance's args.    */
DECL|method|filterWrappedMergePolicyFactoryArgs
specifier|private
name|MergePolicyFactoryArgs
name|filterWrappedMergePolicyFactoryArgs
parameter_list|()
block|{
specifier|final
name|String
name|wrappedPolicyPrefix
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|remove
argument_list|(
name|WRAPPED_PREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrappedPolicyPrefix
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|baseArgsPrefix
init|=
name|wrappedPolicyPrefix
operator|+
literal|'.'
decl_stmt|;
specifier|final
name|int
name|baseArgsPrefixLength
init|=
name|baseArgsPrefix
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|MergePolicyFactoryArgs
name|wrappedArgs
init|=
operator|new
name|MergePolicyFactoryArgs
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|args
operator|.
name|keys
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|key
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|baseArgsPrefix
argument_list|)
condition|)
block|{
name|wrappedArgs
operator|.
name|add
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|baseArgsPrefixLength
argument_list|)
argument_list|,
name|args
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|wrappedArgs
return|;
block|}
block|}
end_class

end_unit

