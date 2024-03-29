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
name|io
operator|.
name|Writer
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
comment|/** Single threaded BufferedWriter  *  Internal Solr use only, subject to change.  */
end_comment

begin_class
DECL|class|FastWriter
specifier|public
class|class
name|FastWriter
extends|extends
name|Writer
block|{
comment|// use default BUFSIZE of BufferedWriter so if we wrap that
comment|// it won't cause double buffering.
DECL|field|BUFSIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFSIZE
init|=
literal|8192
decl_stmt|;
DECL|field|sink
specifier|protected
specifier|final
name|Writer
name|sink
decl_stmt|;
DECL|field|buf
specifier|protected
name|char
index|[]
name|buf
decl_stmt|;
DECL|field|pos
specifier|protected
name|int
name|pos
decl_stmt|;
DECL|method|FastWriter
specifier|public
name|FastWriter
parameter_list|(
name|Writer
name|w
parameter_list|)
block|{
name|this
argument_list|(
name|w
argument_list|,
operator|new
name|char
index|[
name|BUFSIZE
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|FastWriter
specifier|public
name|FastWriter
parameter_list|(
name|Writer
name|sink
parameter_list|,
name|char
index|[]
name|tempBuffer
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|sink
operator|=
name|sink
expr_stmt|;
name|this
operator|.
name|buf
operator|=
name|tempBuffer
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|start
expr_stmt|;
block|}
DECL|method|wrap
specifier|public
specifier|static
name|FastWriter
name|wrap
parameter_list|(
name|Writer
name|sink
parameter_list|)
block|{
return|return
operator|(
name|sink
operator|instanceof
name|FastWriter
operator|)
condition|?
operator|(
name|FastWriter
operator|)
name|sink
else|:
operator|new
name|FastWriter
argument_list|(
name|sink
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
name|buf
index|[
name|pos
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|FastWriter
name|append
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
name|buf
index|[
name|pos
operator|++
index|]
operator|=
name|c
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|arr
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|space
init|=
name|buf
operator|.
name|length
operator|-
name|pos
decl_stmt|;
if|if
condition|(
name|len
operator|<=
name|space
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|len
operator|>
name|buf
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
comment|// flush
name|pos
operator|=
literal|0
expr_stmt|;
block|}
comment|// don't buffer, just write to sink
name|flush
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// buffer is too big to fit in the free space, but
comment|// not big enough to warrant writing on its own.
comment|// write whatever we can fit, then flush and iterate.
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|pos
argument_list|,
name|space
argument_list|)
expr_stmt|;
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|off
operator|+=
name|space
expr_stmt|;
name|len
operator|-=
name|space
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|str
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|space
init|=
name|buf
operator|.
name|length
operator|-
name|pos
decl_stmt|;
if|if
condition|(
name|len
operator|<=
name|space
condition|)
block|{
name|str
operator|.
name|getChars
argument_list|(
name|off
argument_list|,
name|off
operator|+
name|len
argument_list|,
name|buf
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|len
operator|>
name|buf
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
comment|// flush
name|pos
operator|=
literal|0
expr_stmt|;
block|}
comment|// don't buffer, just write to sink
name|flush
argument_list|(
name|str
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// buffer is too big to fit in the free space, but
comment|// not big enough to warrant writing on its own.
comment|// write whatever we can fit, then flush and iterate.
name|str
operator|.
name|getChars
argument_list|(
name|off
argument_list|,
name|off
operator|+
name|space
argument_list|,
name|buf
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|off
operator|+=
name|space
expr_stmt|;
name|len
operator|-=
name|space
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|sink
operator|!=
literal|null
condition|)
name|sink
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|char
index|[]
name|buf
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
name|sink
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|String
name|str
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
name|sink
operator|.
name|write
argument_list|(
name|str
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
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
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|sink
operator|!=
literal|null
condition|)
name|sink
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|flushBuffer
specifier|public
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

