begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|RAMDirectory
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
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/**  * Test utility - slow directory  */
end_comment

begin_comment
comment|// TODO: move to test-framework and sometimes use in tests?
end_comment

begin_class
DECL|class|SlowRAMDirectory
specifier|public
class|class
name|SlowRAMDirectory
extends|extends
name|RAMDirectory
block|{
DECL|field|IO_SLEEP_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|IO_SLEEP_THRESHOLD
init|=
literal|50
decl_stmt|;
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|sleepMillis
specifier|private
name|int
name|sleepMillis
decl_stmt|;
DECL|method|setSleepMillis
specifier|public
name|void
name|setSleepMillis
parameter_list|(
name|int
name|sleepMillis
parameter_list|)
block|{
name|this
operator|.
name|sleepMillis
operator|=
name|sleepMillis
expr_stmt|;
block|}
DECL|method|SlowRAMDirectory
specifier|public
name|SlowRAMDirectory
parameter_list|(
name|int
name|sleepMillis
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|sleepMillis
operator|=
name|sleepMillis
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sleepMillis
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
operator|new
name|SlowIndexOutput
argument_list|(
name|super
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sleepMillis
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
operator|new
name|SlowIndexInput
argument_list|(
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|doSleep
name|void
name|doSleep
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|sTime
init|=
name|length
operator|<
literal|10
condition|?
name|sleepMillis
else|:
call|(
name|int
call|)
argument_list|(
name|sleepMillis
operator|*
name|Math
operator|.
name|log
argument_list|(
name|length
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
operator|!=
literal|null
condition|)
block|{
name|sTime
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sTime
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Make a private random. */
DECL|method|forkRandom
name|Random
name|forkRandom
parameter_list|()
block|{
if|if
condition|(
name|random
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Delegate class to wrap an IndexInput and delay reading bytes by some    * specified time.    */
DECL|class|SlowIndexInput
specifier|private
class|class
name|SlowIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|ii
specifier|private
name|IndexInput
name|ii
decl_stmt|;
DECL|field|numRead
specifier|private
name|int
name|numRead
init|=
literal|0
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
decl_stmt|;
DECL|method|SlowIndexInput
specifier|public
name|SlowIndexInput
parameter_list|(
name|IndexInput
name|ii
parameter_list|)
block|{
name|super
argument_list|(
literal|"SlowIndexInput("
operator|+
name|ii
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|rand
operator|=
name|forkRandom
argument_list|()
expr_stmt|;
name|this
operator|.
name|ii
operator|=
name|ii
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|numRead
operator|>=
name|IO_SLEEP_THRESHOLD
condition|)
block|{
name|doSleep
argument_list|(
name|rand
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|numRead
operator|=
literal|0
expr_stmt|;
block|}
operator|++
name|numRead
expr_stmt|;
return|return
name|ii
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numRead
operator|>=
name|IO_SLEEP_THRESHOLD
condition|)
block|{
name|doSleep
argument_list|(
name|rand
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|numRead
operator|=
literal|0
expr_stmt|;
block|}
name|numRead
operator|+=
name|len
expr_stmt|;
name|ii
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|clone
annotation|@
name|Override
specifier|public
name|IndexInput
name|clone
parameter_list|()
block|{
return|return
name|ii
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|equals
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|ii
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|method|getFilePointer
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|ii
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
DECL|method|hashCode
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ii
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|length
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|ii
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|seek
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|ii
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Delegate class to wrap an IndexOutput and delay writing bytes by some    * specified time.    */
DECL|class|SlowIndexOutput
specifier|private
class|class
name|SlowIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|io
specifier|private
name|IndexOutput
name|io
decl_stmt|;
DECL|field|numWrote
specifier|private
name|int
name|numWrote
decl_stmt|;
DECL|field|rand
specifier|private
specifier|final
name|Random
name|rand
decl_stmt|;
DECL|method|SlowIndexOutput
specifier|public
name|SlowIndexOutput
parameter_list|(
name|IndexOutput
name|io
parameter_list|)
block|{
name|this
operator|.
name|io
operator|=
name|io
expr_stmt|;
name|this
operator|.
name|rand
operator|=
name|forkRandom
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numWrote
operator|>=
name|IO_SLEEP_THRESHOLD
condition|)
block|{
name|doSleep
argument_list|(
name|rand
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|numWrote
operator|=
literal|0
expr_stmt|;
block|}
operator|++
name|numWrote
expr_stmt|;
name|io
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numWrote
operator|>=
name|IO_SLEEP_THRESHOLD
condition|)
block|{
name|doSleep
argument_list|(
name|rand
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|numWrote
operator|=
literal|0
expr_stmt|;
block|}
name|numWrote
operator|+=
name|length
expr_stmt|;
name|io
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|io
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|flush
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|io
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|getFilePointer
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|io
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
DECL|method|getChecksum
annotation|@
name|Override
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|io
operator|.
name|getChecksum
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

