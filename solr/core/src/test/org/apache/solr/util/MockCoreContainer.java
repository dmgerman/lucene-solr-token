begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Paths
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
name|CoreContainer
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
name|CoreDescriptor
import|;
end_import

begin_class
DECL|class|MockCoreContainer
specifier|public
class|class
name|MockCoreContainer
extends|extends
name|CoreContainer
block|{
DECL|class|MockCoreDescriptor
specifier|public
specifier|static
class|class
name|MockCoreDescriptor
extends|extends
name|CoreDescriptor
block|{
DECL|method|MockCoreDescriptor
specifier|public
name|MockCoreDescriptor
parameter_list|()
block|{
name|super
argument_list|(
literal|"mock"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|MockCoreContainer
specifier|public
name|MockCoreContainer
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreRootDirectory
specifier|public
name|Path
name|getCoreRootDirectory
parameter_list|()
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
literal|"coreroot"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

