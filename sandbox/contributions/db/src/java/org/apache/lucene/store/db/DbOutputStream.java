begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.db
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|db
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by the Open Source  * Applications Foundation on behalf of the Apache Software Foundation.  * For more information on the Open Source Applications Foundation, please see  *<http://www.osafoundation.org>.  * For more information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|store
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Db
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DbTxn
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|Dbt
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|DbException
import|;
end_import

begin_comment
comment|/**  * @author Andi Vajda  */
end_comment

begin_class
DECL|class|DbOutputStream
specifier|public
class|class
name|DbOutputStream
extends|extends
name|OutputStream
block|{
comment|/**      * The size of data blocks, currently 16k (2^14), is determined by this      * constant.      */
DECL|field|BLOCK_SHIFT
specifier|static
specifier|public
specifier|final
name|int
name|BLOCK_SHIFT
init|=
literal|14
decl_stmt|;
DECL|field|BLOCK_LEN
specifier|static
specifier|public
specifier|final
name|int
name|BLOCK_LEN
init|=
literal|1
operator|<<
name|BLOCK_SHIFT
decl_stmt|;
DECL|field|BLOCK_MASK
specifier|static
specifier|public
specifier|final
name|int
name|BLOCK_MASK
init|=
name|BLOCK_LEN
operator|-
literal|1
decl_stmt|;
DECL|field|position
DECL|field|length
specifier|protected
name|long
name|position
init|=
literal|0L
decl_stmt|,
name|length
init|=
literal|0L
decl_stmt|;
DECL|field|file
specifier|protected
name|File
name|file
decl_stmt|;
DECL|field|block
specifier|protected
name|Block
name|block
decl_stmt|;
DECL|field|txn
specifier|protected
name|DbTxn
name|txn
decl_stmt|;
DECL|field|files
DECL|field|blocks
specifier|protected
name|Db
name|files
decl_stmt|,
name|blocks
decl_stmt|;
DECL|field|flags
specifier|protected
name|int
name|flags
decl_stmt|;
DECL|method|DbOutputStream
specifier|protected
name|DbOutputStream
parameter_list|(
name|Db
name|files
parameter_list|,
name|Db
name|blocks
parameter_list|,
name|DbTxn
name|txn
parameter_list|,
name|int
name|flags
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|txn
operator|=
name|txn
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|files
argument_list|,
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|,
name|name
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|block
operator|=
operator|new
name|Block
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|length
operator|=
name|file
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|seek
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
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
name|length
operator|>
literal|0
condition|)
name|block
operator|.
name|put
argument_list|(
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|file
operator|.
name|modify
argument_list|(
name|files
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|,
name|length
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|flushBuffer
specifier|protected
name|void
name|flushBuffer
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|blockPos
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|&
name|BLOCK_MASK
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blockPos
operator|+
name|len
operator|>=
name|BLOCK_LEN
condition|)
block|{
name|int
name|blockLen
init|=
name|BLOCK_LEN
operator|-
name|blockPos
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|block
operator|.
name|getData
argument_list|()
argument_list|,
name|blockPos
argument_list|,
name|blockLen
argument_list|)
expr_stmt|;
name|block
operator|.
name|put
argument_list|(
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|len
operator|-=
name|blockLen
expr_stmt|;
name|offset
operator|+=
name|blockLen
expr_stmt|;
name|position
operator|+=
name|blockLen
expr_stmt|;
name|block
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|blockPos
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|block
operator|.
name|getData
argument_list|()
argument_list|,
name|blockPos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|position
operator|+=
name|len
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|>
name|length
condition|)
name|length
operator|=
name|position
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
return|;
block|}
DECL|method|seek
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
name|super
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|seekInternal
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"seeking past end of file"
argument_list|)
throw|;
if|if
condition|(
operator|(
name|pos
operator|>>>
name|BLOCK_SHIFT
operator|)
operator|==
operator|(
name|position
operator|>>>
name|BLOCK_SHIFT
operator|)
condition|)
name|position
operator|=
name|pos
expr_stmt|;
else|else
block|{
name|block
operator|.
name|put
argument_list|(
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|block
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|block
operator|.
name|get
argument_list|(
name|blocks
argument_list|,
name|txn
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|position
operator|=
name|pos
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

