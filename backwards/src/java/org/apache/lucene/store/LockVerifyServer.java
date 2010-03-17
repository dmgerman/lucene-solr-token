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
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

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
comment|/**  * Simple standalone server that must be running when you  * use {@link VerifyingLockFactory}.  This server simply  * verifies at most one process holds the lock at a time.  * Run without any args to see usage.  *  * @see VerifyingLockFactory  * @see LockStressTest  */
end_comment

begin_class
DECL|class|LockVerifyServer
specifier|public
class|class
name|LockVerifyServer
block|{
DECL|method|getTime
specifier|private
specifier|static
name|String
name|getTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
return|return
literal|"["
operator|+
operator|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|/
literal|1000
operator|)
operator|+
literal|"s] "
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nUsage: java org.apache.lucene.store.LockVerifyServer port\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|ServerSocket
name|s
init|=
operator|new
name|ServerSocket
argument_list|(
name|port
argument_list|)
decl_stmt|;
name|s
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nReady on port "
operator|+
name|port
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|int
name|lockedID
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Socket
name|cs
init|=
name|s
operator|.
name|accept
argument_list|()
decl_stmt|;
name|OutputStream
name|out
init|=
name|cs
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|cs
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|id
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|command
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|boolean
name|err
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|1
condition|)
block|{
comment|// Locked
if|if
condition|(
name|lockedID
operator|!=
literal|0
condition|)
block|{
name|err
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getTime
argument_list|(
name|startTime
argument_list|)
operator|+
literal|" ERROR: id "
operator|+
name|id
operator|+
literal|" got lock, but "
operator|+
name|lockedID
operator|+
literal|" already holds the lock"
argument_list|)
expr_stmt|;
block|}
name|lockedID
operator|=
name|id
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|lockedID
operator|!=
name|id
condition|)
block|{
name|err
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getTime
argument_list|(
name|startTime
argument_list|)
operator|+
literal|" ERROR: id "
operator|+
name|id
operator|+
literal|" released the lock, but "
operator|+
name|lockedID
operator|+
literal|" is the one holding the lock"
argument_list|)
expr_stmt|;
block|}
name|lockedID
operator|=
literal|0
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unrecognized command "
operator|+
name|command
argument_list|)
throw|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
if|if
condition|(
name|err
condition|)
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
else|else
name|out
operator|.
name|write
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

