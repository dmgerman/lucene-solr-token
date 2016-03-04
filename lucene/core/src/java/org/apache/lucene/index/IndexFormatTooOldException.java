begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|util
operator|.
name|Objects
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
name|store
operator|.
name|DataInput
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when Lucene detects  * an index that is too old for this Lucene version  */
end_comment

begin_class
DECL|class|IndexFormatTooOldException
specifier|public
class|class
name|IndexFormatTooOldException
extends|extends
name|IOException
block|{
DECL|field|resourceDescription
specifier|private
specifier|final
name|String
name|resourceDescription
decl_stmt|;
DECL|field|reason
specifier|private
specifier|final
name|String
name|reason
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Integer
name|version
decl_stmt|;
DECL|field|minVersion
specifier|private
specifier|final
name|Integer
name|minVersion
decl_stmt|;
DECL|field|maxVersion
specifier|private
specifier|final
name|Integer
name|maxVersion
decl_stmt|;
comment|/** Creates an {@code IndexFormatTooOldException}.    *    *  @param resourceDescription describes the file that was too old    *  @param reason the reason for this exception if the version is not available    *    * @lucene.internal */
DECL|method|IndexFormatTooOldException
specifier|public
name|IndexFormatTooOldException
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
literal|"Format version is not supported (resource "
operator|+
name|resourceDescription
operator|+
literal|"): "
operator|+
name|reason
operator|+
literal|". This version of Lucene only supports indexes created with release 6.0 and later."
argument_list|)
expr_stmt|;
name|this
operator|.
name|resourceDescription
operator|=
name|resourceDescription
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
name|this
operator|.
name|version
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|minVersion
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|maxVersion
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Creates an {@code IndexFormatTooOldException}.    *    *  @param in the open file that's too old    *  @param reason the reason for this exception if the version is not available    *    * @lucene.internal */
DECL|method|IndexFormatTooOldException
specifier|public
name|IndexFormatTooOldException
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|this
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|in
argument_list|)
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an {@code IndexFormatTooOldException}.    *    *  @param resourceDescription describes the file that was too old    *  @param version the version of the file that was too old    *  @param minVersion the minimum version accepted    *  @param maxVersion the maximum version accepted    *     * @lucene.internal */
DECL|method|IndexFormatTooOldException
specifier|public
name|IndexFormatTooOldException
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|int
name|version
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
block|{
name|super
argument_list|(
literal|"Format version is not supported (resource "
operator|+
name|resourceDescription
operator|+
literal|"): "
operator|+
name|version
operator|+
literal|" (needs to be between "
operator|+
name|minVersion
operator|+
literal|" and "
operator|+
name|maxVersion
operator|+
literal|"). This version of Lucene only supports indexes created with release 6.0 and later."
argument_list|)
expr_stmt|;
name|this
operator|.
name|resourceDescription
operator|=
name|resourceDescription
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|minVersion
operator|=
name|minVersion
expr_stmt|;
name|this
operator|.
name|maxVersion
operator|=
name|maxVersion
expr_stmt|;
name|this
operator|.
name|reason
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Creates an {@code IndexFormatTooOldException}.    *    *  @param in the open file that's too old    *  @param version the version of the file that was too old    *  @param minVersion the minimum version accepted    *  @param maxVersion the maximum version accepted    *    * @lucene.internal */
DECL|method|IndexFormatTooOldException
specifier|public
name|IndexFormatTooOldException
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|version
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
block|{
name|this
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|in
argument_list|)
argument_list|,
name|version
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a description of the file that was too old    */
DECL|method|getResourceDescription
specifier|public
name|String
name|getResourceDescription
parameter_list|()
block|{
return|return
name|resourceDescription
return|;
block|}
comment|/**    * Returns an optional reason for this exception if the version information was not available. Otherwise<code>null</code>    */
DECL|method|getReason
specifier|public
name|String
name|getReason
parameter_list|()
block|{
return|return
name|reason
return|;
block|}
comment|/**    * Returns the version of the file that was too old.    * This method will return<code>null</code> if an alternative {@link #getReason()}    * is provided.    */
DECL|method|getVersion
specifier|public
name|Integer
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * Returns the maximum version accepted.    * This method will return<code>null</code> if an alternative {@link #getReason()}    * is provided.    */
DECL|method|getMaxVersion
specifier|public
name|Integer
name|getMaxVersion
parameter_list|()
block|{
return|return
name|maxVersion
return|;
block|}
comment|/**    * Returns the minimum version accepted    * This method will return<code>null</code> if an alternative {@link #getReason()}    * is provided.    */
DECL|method|getMinVersion
specifier|public
name|Integer
name|getMinVersion
parameter_list|()
block|{
return|return
name|minVersion
return|;
block|}
block|}
end_class

end_unit

