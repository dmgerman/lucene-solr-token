begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|ThreadFilter
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Last minute patches.  * TODO: remove when integrated in system filters in rr.  */
end_comment

begin_class
DECL|class|QuickPatchThreadsFilter
specifier|public
class|class
name|QuickPatchThreadsFilter
implements|implements
name|ThreadFilter
block|{
annotation|@
name|Override
DECL|method|reject
specifier|public
name|boolean
name|reject
parameter_list|(
name|Thread
name|t
parameter_list|)
block|{
comment|// MacOS system thread.
if|if
condition|(
name|t
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"AWT-AppKit"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// J9 memory pool thread.
if|if
condition|(
name|t
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MemoryPoolMXBean notification dispatcher"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// forked process reaper on Unixish systems
if|if
condition|(
name|t
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"process reaper"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

