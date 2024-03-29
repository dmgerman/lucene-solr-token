begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|FileStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|FileAttributeView
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|FileStoreAttributeView
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**    * A {@code FilterFileStore} contains another   * {@code FileStore}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment

begin_class
DECL|class|FilterFileStore
specifier|public
specifier|abstract
class|class
name|FilterFileStore
extends|extends
name|FileStore
block|{
comment|/**     * The underlying {@code FileStore} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|FileStore
name|delegate
decl_stmt|;
comment|/**    * URI scheme used for this instance.    */
DECL|field|scheme
specifier|protected
specifier|final
name|String
name|scheme
decl_stmt|;
comment|/**    * Construct a {@code FilterFileStore} based on     * the specified base store.    * @param delegate specified base store.    * @param scheme URI scheme identifying this instance.    */
DECL|method|FilterFileStore
specifier|public
name|FilterFileStore
parameter_list|(
name|FileStore
name|delegate
parameter_list|,
name|String
name|scheme
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|type
argument_list|()
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
name|delegate
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isReadOnly
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isReadOnly
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalSpace
specifier|public
name|long
name|getTotalSpace
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getTotalSpace
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUsableSpace
specifier|public
name|long
name|getUsableSpace
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getUsableSpace
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUnallocatedSpace
specifier|public
name|long
name|getUnallocatedSpace
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getUnallocatedSpace
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|supportsFileAttributeView
specifier|public
name|boolean
name|supportsFileAttributeView
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|FileAttributeView
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|supportsFileAttributeView
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|supportsFileAttributeView
specifier|public
name|boolean
name|supportsFileAttributeView
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|supportsFileAttributeView
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStoreAttributeView
specifier|public
parameter_list|<
name|V
extends|extends
name|FileStoreAttributeView
parameter_list|>
name|V
name|getFileStoreAttributeView
parameter_list|(
name|Class
argument_list|<
name|V
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getFileStoreAttributeView
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAttribute
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attribute
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getAttribute
argument_list|(
name|attribute
argument_list|)
return|;
block|}
block|}
end_class

end_unit

