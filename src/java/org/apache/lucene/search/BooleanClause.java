begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_comment
comment|/** A clause in a BooleanQuery. */
end_comment

begin_class
DECL|class|BooleanClause
specifier|public
class|class
name|BooleanClause
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** The query whose matching documents are combined by the boolean query. */
DECL|field|query
specifier|public
name|Query
name|query
decl_stmt|;
comment|/** If true, documents documents which<i>do not</i>     match this sub-query will<i>not</i> match the boolean query. */
DECL|field|required
specifier|public
name|boolean
name|required
init|=
literal|false
decl_stmt|;
comment|/** If true, documents documents which<i>do</i>     match this sub-query will<i>not</i> match the boolean query. */
DECL|field|prohibited
specifier|public
name|boolean
name|prohibited
init|=
literal|false
decl_stmt|;
comment|/** Constructs a BooleanClause with query<code>q</code>, required<code>r</code> and prohibited<code>p</code>. */
DECL|method|BooleanClause
specifier|public
name|BooleanClause
parameter_list|(
name|Query
name|q
parameter_list|,
name|boolean
name|r
parameter_list|,
name|boolean
name|p
parameter_list|)
block|{
name|query
operator|=
name|q
expr_stmt|;
name|required
operator|=
name|r
expr_stmt|;
name|prohibited
operator|=
name|p
expr_stmt|;
block|}
block|}
end_class

end_unit

