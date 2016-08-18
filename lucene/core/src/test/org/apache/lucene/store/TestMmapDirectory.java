begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_comment
comment|// import org.junit.Ignore;
end_comment

begin_comment
comment|/**  * Tests MMapDirectory  */
end_comment

begin_class
DECL|class|TestMmapDirectory
specifier|public
class|class
name|TestMmapDirectory
extends|extends
name|BaseDirectoryTestCase
block|{
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|MMapDirectory
name|m
init|=
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|m
operator|.
name|setPreload
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|assumeTrue
argument_list|(
literal|"test requires a jre that supports unmapping: "
operator|+
name|MMapDirectory
operator|.
name|UNMAP_NOT_SUPPORTED_REASON
argument_list|,
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
argument_list|)
expr_stmt|;
block|}
comment|// TODO: @Ignore("This test is for JVM testing purposes. There are no guarantees that it may not fail with SIGSEGV!")
DECL|method|testAceWithThreads
specifier|public
name|void
name|testAceWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
condition|;
name|iter
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testAceWithThreads"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|8
operator|*
literal|1024
operator|*
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|IndexInput
name|clone
init|=
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
specifier|final
name|byte
name|accum
index|[]
init|=
operator|new
name|byte
index|[
literal|32
operator|*
literal|1024
operator|*
literal|1024
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|shotgun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|shotgun
operator|.
name|await
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|clone
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|clone
operator|.
name|readBytes
argument_list|(
name|accum
argument_list|,
literal|0
argument_list|,
name|accum
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|AlreadyClosedException
name|ok
parameter_list|)
block|{
comment|// OK
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|shotgun
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|t1
operator|.
name|join
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

