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

begin_comment
comment|/**  * A {@link DirectoryReader} that wraps all its subreaders with  * {@link AssertingLeafReader}  */
end_comment

begin_class
DECL|class|AssertingDirectoryReader
specifier|public
class|class
name|AssertingDirectoryReader
extends|extends
name|FilterDirectoryReader
block|{
DECL|class|AssertingSubReaderWrapper
specifier|static
class|class
name|AssertingSubReaderWrapper
extends|extends
name|SubReaderWrapper
block|{
annotation|@
name|Override
DECL|method|wrap
specifier|public
name|LeafReader
name|wrap
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|AssertingLeafReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
DECL|method|AssertingDirectoryReader
specifier|public
name|AssertingDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
operator|new
name|AssertingSubReaderWrapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWrapDirectoryReader
specifier|protected
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingDirectoryReader
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
block|}
block|}
end_class

end_unit

