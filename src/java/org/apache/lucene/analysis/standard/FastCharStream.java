begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// FastCharStream.java
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/** An efficient implementation of JavaCC's CharStream interface.<p>Note that  * this does not do line-number counting, but instead keeps track of the  * character position of the token in the input, as required by Lucene's {@link  * org.apache.lucene.analysis.Token} API. */
end_comment

begin_class
DECL|class|FastCharStream
specifier|public
specifier|final
class|class
name|FastCharStream
implements|implements
name|CharStream
block|{
DECL|field|buffer
name|char
index|[]
name|buffer
init|=
literal|null
decl_stmt|;
DECL|field|bufferLength
name|int
name|bufferLength
init|=
literal|0
decl_stmt|;
comment|// end of valid chars
DECL|field|bufferPosition
name|int
name|bufferPosition
init|=
literal|0
decl_stmt|;
comment|// next char to read
DECL|field|tokenStart
name|int
name|tokenStart
init|=
literal|0
decl_stmt|;
comment|// offset in buffer
DECL|field|bufferStart
name|int
name|bufferStart
init|=
literal|0
decl_stmt|;
comment|// position in file of buffer
DECL|field|input
name|Reader
name|input
decl_stmt|;
comment|// source of chars
comment|/** Constructs from a Reader. */
DECL|method|FastCharStream
specifier|public
name|FastCharStream
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
name|input
operator|=
name|r
expr_stmt|;
block|}
DECL|method|readChar
specifier|public
specifier|final
name|char
name|readChar
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bufferPosition
operator|>=
name|bufferLength
condition|)
name|refill
argument_list|()
expr_stmt|;
return|return
name|buffer
index|[
name|bufferPosition
operator|++
index|]
return|;
block|}
DECL|method|refill
specifier|private
specifier|final
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|newPosition
init|=
name|bufferLength
operator|-
name|tokenStart
decl_stmt|;
if|if
condition|(
name|tokenStart
operator|==
literal|0
condition|)
block|{
comment|// token won't fit in buffer
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
comment|// first time: alloc buffer
name|buffer
operator|=
operator|new
name|char
index|[
literal|2048
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bufferLength
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
comment|// grow buffer
name|char
index|[]
name|newBuffer
init|=
operator|new
name|char
index|[
name|buffer
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// shift token to front
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|tokenStart
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|newPosition
argument_list|)
expr_stmt|;
block|}
name|bufferLength
operator|=
name|newPosition
expr_stmt|;
comment|// update state
name|bufferPosition
operator|=
name|newPosition
expr_stmt|;
name|bufferStart
operator|+=
name|tokenStart
expr_stmt|;
name|tokenStart
operator|=
literal|0
expr_stmt|;
name|int
name|charsRead
init|=
comment|// fill space in buffer
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|newPosition
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|newPosition
argument_list|)
decl_stmt|;
if|if
condition|(
name|charsRead
operator|==
operator|-
literal|1
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past eof"
argument_list|)
throw|;
else|else
name|bufferLength
operator|+=
name|charsRead
expr_stmt|;
block|}
DECL|method|BeginToken
specifier|public
specifier|final
name|char
name|BeginToken
parameter_list|()
throws|throws
name|IOException
block|{
name|tokenStart
operator|=
name|bufferPosition
expr_stmt|;
return|return
name|readChar
argument_list|()
return|;
block|}
DECL|method|backup
specifier|public
specifier|final
name|void
name|backup
parameter_list|(
name|int
name|amount
parameter_list|)
block|{
name|bufferPosition
operator|-=
name|amount
expr_stmt|;
block|}
DECL|method|GetImage
specifier|public
specifier|final
name|String
name|GetImage
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
name|tokenStart
argument_list|,
name|bufferPosition
operator|-
name|tokenStart
argument_list|)
return|;
block|}
DECL|method|GetSuffix
specifier|public
specifier|final
name|char
index|[]
name|GetSuffix
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|char
index|[]
name|value
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
operator|-
name|len
argument_list|,
name|value
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
DECL|method|Done
specifier|public
specifier|final
name|void
name|Done
parameter_list|()
block|{
try|try
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
operator|+
literal|"; ignoring."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getColumn
specifier|public
specifier|final
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
DECL|method|getLine
specifier|public
specifier|final
name|int
name|getLine
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|getEndColumn
specifier|public
specifier|final
name|int
name|getEndColumn
parameter_list|()
block|{
return|return
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
DECL|method|getEndLine
specifier|public
specifier|final
name|int
name|getEndLine
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|getBeginColumn
specifier|public
specifier|final
name|int
name|getBeginColumn
parameter_list|()
block|{
return|return
name|bufferStart
operator|+
name|tokenStart
return|;
block|}
DECL|method|getBeginLine
specifier|public
specifier|final
name|int
name|getBeginLine
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

