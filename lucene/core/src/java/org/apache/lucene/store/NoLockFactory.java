begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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

begin_comment
comment|/**  * Use this {@link LockFactory} to disable locking entirely.  * This is a singleton, you have to use {@link #INSTANCE}.  *  * @see LockFactory  */
end_comment

begin_class
DECL|class|NoLockFactory
specifier|public
specifier|final
class|class
name|NoLockFactory
extends|extends
name|LockFactory
block|{
comment|/** The singleton */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|NoLockFactory
name|INSTANCE
init|=
operator|new
name|NoLockFactory
argument_list|()
decl_stmt|;
comment|// visible for AssertingLock!
DECL|field|SINGLETON_LOCK
specifier|static
specifier|final
name|NoLock
name|SINGLETON_LOCK
init|=
operator|new
name|NoLock
argument_list|()
decl_stmt|;
DECL|method|NoLockFactory
specifier|private
name|NoLockFactory
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|obtainLock
specifier|public
name|Lock
name|obtainLock
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|lockName
parameter_list|)
block|{
return|return
name|SINGLETON_LOCK
return|;
block|}
DECL|class|NoLock
specifier|private
specifier|static
class|class
name|NoLock
extends|extends
name|Lock
block|{
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|ensureValid
specifier|public
name|void
name|ensureValid
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NoLock"
return|;
block|}
block|}
block|}
end_class

end_unit

