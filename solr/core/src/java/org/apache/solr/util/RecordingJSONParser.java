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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_class
DECL|class|RecordingJSONParser
specifier|public
class|class
name|RecordingJSONParser
extends|extends
name|JSONParser
block|{
DECL|field|buf
specifier|static
name|ThreadLocal
argument_list|<
name|char
index|[]
argument_list|>
name|buf
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|bufCopy
specifier|private
specifier|final
name|char
index|[]
name|bufCopy
decl_stmt|;
comment|//global position is the global position at the beginning of my buffer
DECL|field|globalPosition
specifier|private
name|long
name|globalPosition
init|=
literal|0
decl_stmt|;
DECL|field|sb
specifier|private
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|objectStarted
specifier|private
name|boolean
name|objectStarted
init|=
literal|false
decl_stmt|;
DECL|field|lastMarkedPosition
specifier|private
name|long
name|lastMarkedPosition
init|=
literal|0
decl_stmt|;
DECL|field|lastGlobalPosition
specifier|private
name|long
name|lastGlobalPosition
init|=
literal|0
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|8192
decl_stmt|;
DECL|method|RecordingJSONParser
specifier|public
name|RecordingJSONParser
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|getChars
argument_list|()
argument_list|)
expr_stmt|;
name|bufCopy
operator|=
name|buf
operator|.
name|get
argument_list|()
expr_stmt|;
name|buf
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
DECL|method|getChars
specifier|static
name|char
index|[]
name|getChars
parameter_list|()
block|{
name|buf
operator|.
name|set
argument_list|(
operator|new
name|char
index|[
name|BUFFER_SIZE
index|]
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|recordChar
specifier|private
name|void
name|recordChar
parameter_list|(
name|int
name|aChar
parameter_list|)
block|{
if|if
condition|(
name|objectStarted
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|aChar
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|aChar
operator|==
literal|'{'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|aChar
argument_list|)
expr_stmt|;
name|objectStarted
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|resetBuf
specifier|public
name|void
name|resetBuf
parameter_list|()
block|{
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|objectStarted
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextEvent
specifier|public
name|int
name|nextEvent
parameter_list|()
throws|throws
name|IOException
block|{
name|captureMissing
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|nextEvent
argument_list|()
return|;
block|}
DECL|method|captureMissing
specifier|private
name|void
name|captureMissing
parameter_list|()
block|{
name|long
name|currPosition
init|=
name|getPosition
argument_list|()
operator|-
name|globalPosition
decl_stmt|;
if|if
condition|(
name|currPosition
operator|<
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currPosition
operator|>
name|lastMarkedPosition
condition|)
block|{
for|for
control|(
name|long
name|i
init|=
name|lastMarkedPosition
init|;
name|i
operator|<
name|currPosition
condition|;
name|i
operator|++
control|)
block|{
name|recordChar
argument_list|(
name|bufCopy
index|[
operator|(
name|int
operator|)
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|currPosition
operator|<
name|lastMarkedPosition
condition|)
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currPosition
condition|;
name|i
operator|++
control|)
block|{
name|recordChar
argument_list|(
name|bufCopy
index|[
operator|(
name|int
operator|)
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|currPosition
operator|==
name|BUFFER_SIZE
operator|&&
name|lastGlobalPosition
operator|!=
name|globalPosition
condition|)
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currPosition
condition|;
name|i
operator|++
control|)
block|{
name|recordChar
argument_list|(
name|bufCopy
index|[
operator|(
name|int
operator|)
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|lastGlobalPosition
operator|=
name|globalPosition
expr_stmt|;
name|lastMarkedPosition
operator|=
name|currPosition
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|protected
name|void
name|fill
parameter_list|()
throws|throws
name|IOException
block|{
name|captureMissing
argument_list|()
expr_stmt|;
name|super
operator|.
name|fill
argument_list|()
expr_stmt|;
name|this
operator|.
name|globalPosition
operator|=
name|getPosition
argument_list|()
expr_stmt|;
block|}
DECL|method|getBuf
specifier|public
name|String
name|getBuf
parameter_list|()
block|{
name|captureMissing
argument_list|()
expr_stmt|;
if|if
condition|(
name|sb
operator|!=
literal|null
condition|)
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
return|return
literal|null
return|;
block|}
DECL|method|error
specifier|public
name|JSONParser
operator|.
name|ParseException
name|error
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
return|return
name|err
argument_list|(
name|msg
argument_list|)
return|;
block|}
block|}
end_class

end_unit

