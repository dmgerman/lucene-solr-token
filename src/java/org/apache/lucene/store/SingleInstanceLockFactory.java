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
name|HashSet
import|;
end_import

begin_comment
comment|/**  * Implements {@link LockFactory} for a single in-process instance,  * meaning all locking will take place through this one instance.  * Only use this {@link LockFactory} when you are certain all  * IndexReaders and IndexWriters for a given index are running  * against a single shared in-process Directory instance.  This is  * currently the default locking for RAMDirectory.  *  * @see LockFactory  */
end_comment

begin_class
DECL|class|SingleInstanceLockFactory
specifier|public
class|class
name|SingleInstanceLockFactory
extends|extends
name|LockFactory
block|{
DECL|field|locks
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|locks
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
comment|// We do not use the LockPrefix at all, because the private
comment|// HashSet instance effectively scopes the locking to this
comment|// single Directory instance.
return|return
operator|new
name|SingleInstanceLock
argument_list|(
name|locks
argument_list|,
name|lockName
argument_list|)
return|;
block|}
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|locks
init|)
block|{
if|if
condition|(
name|locks
operator|.
name|contains
argument_list|(
name|lockName
argument_list|)
condition|)
block|{
name|locks
operator|.
name|remove
argument_list|(
name|lockName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_class
DECL|class|SingleInstanceLock
class|class
name|SingleInstanceLock
extends|extends
name|Lock
block|{
DECL|field|lockName
name|String
name|lockName
decl_stmt|;
DECL|field|locks
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|locks
decl_stmt|;
DECL|method|SingleInstanceLock
specifier|public
name|SingleInstanceLock
parameter_list|(
name|HashSet
argument_list|<
name|String
argument_list|>
name|locks
parameter_list|,
name|String
name|lockName
parameter_list|)
block|{
name|this
operator|.
name|locks
operator|=
name|locks
expr_stmt|;
name|this
operator|.
name|lockName
operator|=
name|lockName
expr_stmt|;
block|}
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|locks
init|)
block|{
return|return
name|locks
operator|.
name|add
argument_list|(
name|lockName
argument_list|)
return|;
block|}
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
synchronized|synchronized
init|(
name|locks
init|)
block|{
name|locks
operator|.
name|remove
argument_list|(
name|lockName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
synchronized|synchronized
init|(
name|locks
init|)
block|{
return|return
name|locks
operator|.
name|contains
argument_list|(
name|lockName
argument_list|)
return|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|lockName
return|;
block|}
block|}
end_class

end_unit

