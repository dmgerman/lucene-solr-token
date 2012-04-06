begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.noggit
package|package
name|org
operator|.
name|apache
operator|.
name|noggit
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
name|nio
operator|.
name|CharBuffer
import|;
end_import

begin_comment
comment|// CharArr origins
end_comment

begin_comment
comment|// V1.0 7/06/97
end_comment

begin_comment
comment|// V1.1 9/21/99
end_comment

begin_comment
comment|// V1.2 2/02/04  // Java5 features
end_comment

begin_comment
comment|// V1.3 11/26/06 // Make safe for Java 1.4, work into Noggit
end_comment

begin_comment
comment|// Java5 version could look like the following:
end_comment

begin_comment
comment|// public class CharArr implements CharSequence, Appendable, Readable, Closeable {
end_comment

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|CharArr
specifier|public
class|class
name|CharArr
implements|implements
name|CharSequence
implements|,
name|Appendable
block|{
DECL|field|buf
specifier|protected
name|char
index|[]
name|buf
decl_stmt|;
DECL|field|start
specifier|protected
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|protected
name|int
name|end
decl_stmt|;
DECL|method|CharArr
specifier|public
name|CharArr
parameter_list|()
block|{
name|this
argument_list|(
literal|32
argument_list|)
expr_stmt|;
block|}
DECL|method|CharArr
specifier|public
name|CharArr
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|buf
operator|=
operator|new
name|char
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|method|CharArr
specifier|public
name|CharArr
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|set
argument_list|(
name|arr
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
DECL|method|setStart
specifier|public
name|void
name|setStart
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
DECL|method|setEnd
specifier|public
name|void
name|setEnd
parameter_list|(
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|arr
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
DECL|method|getArray
specifier|public
name|char
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getEnd
specifier|public
name|int
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|end
operator|-
name|start
return|;
block|}
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|size
argument_list|()
return|;
block|}
DECL|method|capacity
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|buf
operator|.
name|length
return|;
block|}
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|buf
index|[
name|start
operator|+
name|index
index|]
return|;
block|}
DECL|method|subSequence
specifier|public
name|CharArr
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|CharArr
argument_list|(
name|buf
argument_list|,
name|this
operator|.
name|start
operator|+
name|start
argument_list|,
name|this
operator|.
name|start
operator|+
name|end
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|start
operator|>=
name|end
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|buf
index|[
name|start
operator|++
index|]
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
name|cbuf
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|//TODO
return|return
literal|0
return|;
block|}
DECL|method|unsafeWrite
specifier|public
name|void
name|unsafeWrite
parameter_list|(
name|char
name|b
parameter_list|)
block|{
name|buf
index|[
name|end
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
DECL|method|unsafeWrite
specifier|public
name|void
name|unsafeWrite
parameter_list|(
name|int
name|b
parameter_list|)
block|{
name|unsafeWrite
argument_list|(
operator|(
name|char
operator|)
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|unsafeWrite
specifier|public
name|void
name|unsafeWrite
parameter_list|(
name|char
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|end
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|end
operator|+=
name|len
expr_stmt|;
block|}
DECL|method|resize
specifier|protected
name|void
name|resize
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|char
name|newbuf
index|[]
init|=
operator|new
name|char
index|[
name|Math
operator|.
name|max
argument_list|(
name|buf
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|len
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|newbuf
argument_list|,
literal|0
argument_list|,
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|=
name|newbuf
expr_stmt|;
block|}
DECL|method|reserve
specifier|public
name|void
name|reserve
parameter_list|(
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|end
operator|+
name|num
operator|>
name|buf
operator|.
name|length
condition|)
name|resize
argument_list|(
name|end
operator|+
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|b
parameter_list|)
block|{
if|if
condition|(
name|end
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
name|resize
argument_list|(
name|end
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|unsafeWrite
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
block|{
name|write
argument_list|(
operator|(
name|char
operator|)
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|char
index|[]
name|b
parameter_list|)
block|{
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|reserve
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|unsafeWrite
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|CharArr
name|arr
parameter_list|)
block|{
name|write
argument_list|(
name|arr
operator|.
name|buf
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|write
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|stringOffset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|reserve
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|s
operator|.
name|getChars
argument_list|(
name|stringOffset
argument_list|,
name|len
argument_list|,
name|buf
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|end
operator|+=
name|len
expr_stmt|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
block|{   }
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{
name|start
operator|=
name|end
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
DECL|method|toCharArray
specifier|public
name|char
index|[]
name|toCharArray
parameter_list|()
block|{
name|char
name|newbuf
index|[]
init|=
operator|new
name|char
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|newbuf
argument_list|,
literal|0
argument_list|,
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newbuf
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|size
argument_list|()
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|CharBuffer
name|cb
parameter_list|)
throws|throws
name|IOException
block|{
comment|/***     int sz = size();     if (sz<=0) return -1;     if (sz>0) cb.put(buf, start, sz);     return -1;     ***/
name|int
name|sz
init|=
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
name|cb
operator|.
name|put
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|sz
argument_list|)
expr_stmt|;
name|start
operator|=
name|end
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|fill
argument_list|()
expr_stmt|;
name|int
name|s
init|=
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|0
condition|)
return|return
name|sz
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|sz
return|;
name|sz
operator|+=
name|s
expr_stmt|;
name|cb
operator|.
name|put
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fill
specifier|public
name|int
name|fill
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
comment|// or -1?
block|}
comment|//////////////// Appendable methods /////////////
DECL|method|append
specifier|public
specifier|final
name|Appendable
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|append
argument_list|(
name|csq
argument_list|,
literal|0
argument_list|,
name|csq
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|append
specifier|public
name|Appendable
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|csq
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|append
specifier|public
specifier|final
name|Appendable
name|append
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

begin_class
DECL|class|NullCharArr
class|class
name|NullCharArr
extends|extends
name|CharArr
block|{
DECL|method|NullCharArr
specifier|public
name|NullCharArr
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|char
index|[
literal|1
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|unsafeWrite
specifier|public
name|void
name|unsafeWrite
parameter_list|(
name|char
name|b
parameter_list|)
block|{}
DECL|method|unsafeWrite
specifier|public
name|void
name|unsafeWrite
parameter_list|(
name|char
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{}
DECL|method|unsafeWrite
specifier|public
name|void
name|unsafeWrite
parameter_list|(
name|int
name|b
parameter_list|)
block|{}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|b
parameter_list|)
block|{}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{}
DECL|method|reserve
specifier|public
name|void
name|reserve
parameter_list|(
name|int
name|num
parameter_list|)
block|{}
DECL|method|resize
specifier|protected
name|void
name|resize
parameter_list|(
name|int
name|len
parameter_list|)
block|{}
DECL|method|append
specifier|public
name|Appendable
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|stringOffset
parameter_list|,
name|int
name|len
parameter_list|)
block|{   }
block|}
end_class

begin_comment
comment|// IDEA: a subclass that refills the array from a reader?
end_comment

begin_class
DECL|class|CharArrReader
class|class
name|CharArrReader
extends|extends
name|CharArr
block|{
DECL|field|in
specifier|protected
specifier|final
name|Reader
name|in
decl_stmt|;
DECL|method|CharArrReader
specifier|public
name|CharArrReader
parameter_list|(
name|Reader
name|in
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|start
operator|>=
name|end
condition|)
name|fill
argument_list|()
expr_stmt|;
return|return
name|start
operator|>=
name|end
condition|?
operator|-
literal|1
else|:
name|buf
index|[
name|start
operator|++
index|]
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|CharBuffer
name|cb
parameter_list|)
throws|throws
name|IOException
block|{
comment|// empty the buffer and then read direct
name|int
name|sz
init|=
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
name|cb
operator|.
name|put
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|int
name|sz2
init|=
name|in
operator|.
name|read
argument_list|(
name|cb
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz2
operator|>=
literal|0
condition|)
return|return
name|sz
operator|+
name|sz2
return|;
return|return
name|sz
operator|>
literal|0
condition|?
name|sz
else|:
operator|-
literal|1
return|;
block|}
DECL|method|fill
specifier|public
name|int
name|fill
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|start
operator|>=
name|end
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|end
operator|=
name|size
argument_list|()
expr_stmt|;
name|start
operator|=
literal|0
expr_stmt|;
block|}
comment|/***     // fill fully or not???     do {       int sz = in.read(buf,end,buf.length-end);       if (sz==-1) return;       end+=sz;     } while (end< buf.length);     ***/
name|int
name|sz
init|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|end
argument_list|,
name|buf
operator|.
name|length
operator|-
name|end
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
name|end
operator|+=
name|sz
expr_stmt|;
return|return
name|sz
return|;
block|}
block|}
end_class

begin_class
DECL|class|CharArrWriter
class|class
name|CharArrWriter
extends|extends
name|CharArr
block|{
DECL|field|sink
specifier|protected
name|Writer
name|sink
decl_stmt|;
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
block|{
try|try
block|{
name|sink
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|start
operator|=
name|end
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|b
parameter_list|)
block|{
if|if
condition|(
name|end
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|unsafeWrite
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|space
init|=
name|buf
operator|.
name|length
operator|-
name|end
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|space
condition|)
block|{
name|unsafeWrite
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|len
operator|<
name|buf
operator|.
name|length
condition|)
block|{
name|unsafeWrite
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|space
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|unsafeWrite
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|space
argument_list|,
name|len
operator|-
name|space
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
name|sink
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|stringOffset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|space
init|=
name|buf
operator|.
name|length
operator|-
name|end
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|space
condition|)
block|{
name|s
operator|.
name|getChars
argument_list|(
name|stringOffset
argument_list|,
name|stringOffset
operator|+
name|len
argument_list|,
name|buf
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|end
operator|+=
name|len
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|len
operator|<
name|buf
operator|.
name|length
condition|)
block|{
comment|// if the data to write is small enough, buffer it.
name|s
operator|.
name|getChars
argument_list|(
name|stringOffset
argument_list|,
name|stringOffset
operator|+
name|space
argument_list|,
name|buf
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|s
operator|.
name|getChars
argument_list|(
name|stringOffset
operator|+
name|space
argument_list|,
name|stringOffset
operator|+
name|len
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|end
operator|=
name|len
operator|-
name|space
expr_stmt|;
block|}
else|else
block|{
name|flush
argument_list|()
expr_stmt|;
comment|// don't buffer, just write to sink
try|try
block|{
name|sink
operator|.
name|write
argument_list|(
name|s
argument_list|,
name|stringOffset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
block|}
end_class

end_unit

