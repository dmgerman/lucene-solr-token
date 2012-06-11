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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * InfoStream implementation over a {@link PrintStream}  * such as<code>System.out</code>.  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|PrintStreamInfoStream
specifier|public
class|class
name|PrintStreamInfoStream
extends|extends
name|InfoStream
block|{
comment|// Used for printing messages
DECL|field|MESSAGE_ID
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|MESSAGE_ID
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|messageID
specifier|protected
specifier|final
name|int
name|messageID
decl_stmt|;
DECL|field|stream
specifier|protected
specifier|final
name|PrintStream
name|stream
decl_stmt|;
DECL|method|PrintStreamInfoStream
specifier|public
name|PrintStreamInfoStream
parameter_list|(
name|PrintStream
name|stream
parameter_list|)
block|{
name|this
argument_list|(
name|stream
argument_list|,
name|MESSAGE_ID
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|PrintStreamInfoStream
specifier|public
name|PrintStreamInfoStream
parameter_list|(
name|PrintStream
name|stream
parameter_list|,
name|int
name|messageID
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
name|this
operator|.
name|messageID
operator|=
name|messageID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|message
specifier|public
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|stream
operator|.
name|println
argument_list|(
name|component
operator|+
literal|" "
operator|+
name|messageID
operator|+
literal|" ["
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|"; "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|(
name|String
name|component
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isSystemStream
argument_list|()
condition|)
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isSystemStream
specifier|public
name|boolean
name|isSystemStream
parameter_list|()
block|{
return|return
name|stream
operator|==
name|System
operator|.
name|out
operator|||
name|stream
operator|==
name|System
operator|.
name|err
return|;
block|}
block|}
end_class

end_unit

