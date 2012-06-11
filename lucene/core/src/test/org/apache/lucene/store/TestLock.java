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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestLock
specifier|public
class|class
name|TestLock
extends|extends
name|LuceneTestCase
block|{
DECL|method|testObtain
specifier|public
name|void
name|testObtain
parameter_list|()
block|{
name|LockMock
name|lock
init|=
operator|new
name|LockMock
argument_list|()
decl_stmt|;
name|Lock
operator|.
name|LOCK_POLL_INTERVAL
operator|=
literal|10
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|obtain
argument_list|(
name|Lock
operator|.
name|LOCK_POLL_INTERVAL
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed to obtain lock"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should attempt to lock more than once"
argument_list|,
name|lock
operator|.
name|lockAttempts
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LockMock
specifier|private
class|class
name|LockMock
extends|extends
name|Lock
block|{
DECL|field|lockAttempts
specifier|public
name|int
name|lockAttempts
decl_stmt|;
annotation|@
name|Override
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
block|{
name|lockAttempts
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

