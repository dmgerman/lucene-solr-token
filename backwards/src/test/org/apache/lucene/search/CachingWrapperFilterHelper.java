begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|IndexReader
import|;
end_import

begin_comment
comment|/**  * A unit test helper class to test when the filter is getting cached and when it is not.  */
end_comment

begin_class
DECL|class|CachingWrapperFilterHelper
specifier|public
class|class
name|CachingWrapperFilterHelper
extends|extends
name|CachingWrapperFilter
block|{
DECL|field|shouldHaveCache
specifier|private
name|boolean
name|shouldHaveCache
init|=
literal|false
decl_stmt|;
comment|/**    * @param filter Filter to cache results of    */
DECL|method|CachingWrapperFilterHelper
specifier|public
name|CachingWrapperFilterHelper
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|super
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
DECL|method|setShouldHaveCache
specifier|public
name|void
name|setShouldHaveCache
parameter_list|(
name|boolean
name|shouldHaveCache
parameter_list|)
block|{
name|this
operator|.
name|shouldHaveCache
operator|=
name|shouldHaveCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
operator|new
name|WeakHashMap
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// check cache
name|DocIdSet
name|cached
init|=
operator|(
name|DocIdSet
operator|)
name|cache
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldHaveCache
condition|)
block|{
name|TestCase
operator|.
name|assertNotNull
argument_list|(
literal|"Cache should have data "
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TestCase
operator|.
name|assertNull
argument_list|(
literal|"Cache should be null "
operator|+
name|cached
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cached
operator|!=
literal|null
condition|)
block|{
return|return
name|cached
return|;
block|}
block|}
specifier|final
name|DocIdSet
name|bits
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// update cache
name|cache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CachingWrapperFilterHelper("
operator|+
name|filter
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|CachingWrapperFilterHelper
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
operator|(
name|CachingWrapperFilterHelper
operator|)
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|filter
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x5525aacb
return|;
block|}
block|}
end_class

end_unit

